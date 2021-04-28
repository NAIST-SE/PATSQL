package patsql.synth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.BitTable;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;
import patsql.ra.util.RAOptimizer;
import patsql.ra.util.RAUtils;
import patsql.ra.util.RAVisitor;
import patsql.ra.util.Utils;
import patsql.synth.filler.RowSearchCollectingPredicates;
import patsql.synth.filler.SketchFiller;
import patsql.synth.sketcher.Sketcher;

public class RASynthesizer implements Callable<List<RAOperator>> {
	final Example example;
	final SynthOption option;

	public RASynthesizer(Example example, SynthOption option) {
		this.example = example;
		this.option = option;
	}

	@Override
	public List<RAOperator> call() throws Exception {
		return synthesizeTop5();
	}

	/**
	 * @return null if timeout happens.
	 */
	public List<RAOperator> synthesize(int timeoutMs) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<List<RAOperator>> future = service.submit(new RASynthesizer(example, option));
		try {
			return future.get(timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			if (Debug.isDebugMode) {
				System.out.println("SYNTHESIS TIME OUT");
			}
			future.cancel(true); // stops the running synthesis
			return null;
		}
	}

	public List<RAOperator> synthesizeTop5() {
		long startDebug = System.nanoTime();

		List<RAOperator> foundPrograms = synthesizePrograms();
		if (foundPrograms.isEmpty()) // if no solution is found
			return null;

		List<RAOperator> candidates = enumurateEquivalentPrograms(foundPrograms);
		List<RAOperator> ret = resolveTop5(candidates);

		if (Debug.isDebugMode) {
			long dur = (System.nanoTime() - startDebug) / 1000000;
			Debug.Time.doneSynth(dur);
		}
		return ret;
	}

	private List<RAOperator> synthesizePrograms() {
		boolean isOutputSorted = Arrays.stream(example.output.columns) //
				.map(col -> col.schema) //
				.anyMatch(schema -> example.output.isIncreasing(schema) || example.output.isDecreasing(schema));

		Sketcher sketcher = new Sketcher(example.inputs.length, isOutputSorted);

		// find candidates among sketches whose sizes are the same.
		List<RAOperator> foundPrograms = new ArrayList<>();
		int sizeOfSolutionSketch = -1;
		sketch: for (RAOperator s : sketcher) {
			// check termination
			int sizeOfSketch = Sketcher.sizeOf(s);
			if (sizeOfSolutionSketch > 0 && sizeOfSketch > sizeOfSolutionSketch)
				break;

			for (RAOperator sketch : assignNamesOnBaseTables(s)) {
				// check the timeout of itself.
				if (Thread.currentThread().isInterrupted()) {
					return null;
				}

				// check the timeout of itself.
				if (Thread.currentThread().isInterrupted()) {
					break sketch;
				}
				if (!isValidSketch(sketch)) {
					continue;
				}
				if (Debug.isDebugMode) {
					RAUtils.printSketch(sketch);
				}
				SketchFiller filler = new SketchFiller(sketch, example, option);
				for (RAOperator program : filler.fillSketch()) {
					if (!check(program))
						continue;

					// collect the found program as a candidate
					foundPrograms.add(program);
					sizeOfSolutionSketch = sizeOfSketch;
					continue sketch;
				}
			}
		}
		return foundPrograms;
	}

	private List<RAOperator> enumurateEquivalentPrograms(List<RAOperator> programs) {
		List<RAOperator> ret = new ArrayList<>();
		for (RAOperator p : programs) {
			List<RAOperator> ps = enumurateEquivalentPrograms(p);
			ret.addAll(ps);
		}
		return ret;
	}

	/**
	 * resolve predicates that yield the same output as the solution.
	 */
	private List<RAOperator> enumurateEquivalentPrograms(RAOperator program) {
		List<RAOperator> foundPrograms = new ArrayList<>();
		boolean[] hasSelection = new boolean[1];
		RAUtils.traverse(program, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind != RA.SELECTION)
					return true;
				hasSelection[0] = true;

				Selection target = (Selection) op;
				for (Selection found : enumerateEquivalentPredicates(target)) {
					RAOperator pgm = RAUtils.replace(program, found, target);
					pgm = RAOptimizer.optimize(pgm);
					foundPrograms.add(pgm);
				}
				return true;
			}
		});

		// when the program has no selection
		if (!hasSelection[0]) {
			foundPrograms.add(program);
		}
		return foundPrograms;
	}

	private List<Selection> enumerateEquivalentPredicates(Selection target) {
		Conjunction targetPred = target.condition;
		BitTable tmpTable = new BitTable(target.child.eval(example.tableEnv()));
		BitTable solutionTable = tmpTable.selection(targetPred);

		// The following operations are similar to SelectionPrune class.
		RowSearchCollectingPredicates search = new RowSearchCollectingPredicates(//
				tmpTable.height(), solutionTable.rowBits, option.extCells);

		for (ColSchema col : tmpTable.schema()) {
			for (BinaryOp binop : BinaryOp.values()) {
				for (Cell constCell : option.extCells) {
					if (!isTried(col, binop, constCell))
						continue;
					BinaryPred pred = new BinaryPred(col, binop, constCell);
					BitTable result = tmpTable.selection(pred);
					search.addPred(pred, result.rowBits);
				}
			}
		}

		for (ColSchema column : tmpTable.schema()) {
			for (UnaryOp unop : UnaryOp.values()) {
				UnaryPred pred = new UnaryPred(column, unop);
				BitTable result = tmpTable.selection(pred);
				search.addPred(pred, result.rowBits);
			}
		}

		search.generateKernel();

		List<Selection> ret = new ArrayList<>();
		for (Predicate pred : search.getPredicatesCollected()) {
			Selection clone = target.clone();
			clone.condition = Conjunction.from(pred);
			ret.add(clone);
		}
		return ret;
	}

	/**
	 * Order comparisons between String types are excluded.
	 */
	private boolean isTried(ColSchema left, BinaryOp binop, Cell right) {
		if (left.type != right.type)
			return false;

		// skip 'x' = ConcatConmma(...)
		if (left instanceof AggColSchema) {
			AggColSchema ag = (AggColSchema) left;
			if (ag.agg == Agg.ConcatComma || ag.agg == Agg.ConcatSlash || ag.agg == Agg.ConcatSpace)
				return false;
		}

		switch (left.type) {
		case Str:
			switch (binop) {
			case Eq:
			case NotEq:
				return true;
			default:
				return false;
			}
		default:
			return true;
		}
	}

	private List<RAOperator> resolveTop5(List<RAOperator> candidates) {
		// delete duplicated SQL queries
		Map<String, RAOperator> map = new HashMap<String, RAOperator>();
		for (RAOperator c : candidates) {
			String sql = SQLUtil.generateSQL(c);
			map.put(sql, c);
		}
		candidates = new ArrayList<>(map.values());

		// sort candidates based on ranking heuristics
		Collections.sort(candidates, new Comparator<RAOperator>() {
			@Override
			public int compare(RAOperator a, RAOperator b) {
				return heuristic(b) - heuristic(a); // decreasing order
			}
		});

		// slice the first five candidates
		List<RAOperator> ret = new ArrayList<>();
		for (int i = 0; i < 5 && i < candidates.size(); i++) {
			ret.add(candidates.get(i));
		}
		return ret;
	}

	private int heuristic(RAOperator op) {
		int weight1 = 30;
		int weight2 = 1;

		int score = 0;
		// constant coverage
		score = score + weight1 * RAUtils.usedConstants(op).size();
		// predicate naturalness
		score = score - weight2 * SQLUtil.generateSQL(op).length();
		return score;
	}

	private boolean check(RAOperator program) {
		Table result = program.eval(example.tableEnv());
		return result.hasSameRows(example.output);
	}

	private boolean isValidSketch(RAOperator sketch) {
		// if external constants exist, selection is needed.
		if (option.extCells.length > 0 && !hasRAOperator(sketch, RA.SELECTION)) {
			return false;
		}

		// if external constants do not exist, either NULL or left join is needed.
		if (option.extCells.length == 0 && hasRAOperator(sketch, RA.SELECTION)) {
			if (!inputContainsNull() && !hasRAOperator(sketch, RA.LEFTJOIN)) {
				return false;
			}
		}
		return true;
	}

	private Optional<Boolean> memo = Optional.empty();

	private boolean inputContainsNull() {
		if (memo.isPresent()) {
			return memo.get();
		}
		for (NamedTable in : example.inputs) {
			for (Column col : in.table.columns) {
				for (Cell c : col.cells()) {
					if (c.type == Type.Null) {
						memo = Optional.of(true);
						return true;
					}
				}
			}
		}
		memo = Optional.of(false);
		return false;
	}

	private boolean hasRAOperator(RAOperator sketch, RA ra) {
		boolean[] ret = new boolean[1];
		ret[0] = false;
		RAUtils.traverse(sketch, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == ra) {
					ret[0] = true;
					return false;
				}
				return true;
			}
		});
		return ret[0];
	}

	private List<RAOperator> assignNamesOnBaseTables(RAOperator sketch) {
		// collect base tables.
		List<BaseTable> baseTables = new ArrayList<>();
		RAUtils.traverse(sketch, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.BASETABLE) {
					baseTables.add((BaseTable) op);
				}
				return true;
			}
		});
		Collections.reverse(baseTables);

		// assign names on base tables.
		List<RAOperator> ret = new ArrayList<>();
		for (Assignment assignment : validAssignments(baseTables.size())) {
			RAOperator filled = sketch;
			for (int i = 0; i < baseTables.size(); i++) {
				BaseTable target = baseTables.get(i);
				BaseTable inserted = target.clone();
				inserted.tableId = assignment.names.get(i);
				// fill one ID by updating.
				filled = RAUtils.replace(filled, inserted, target);
			}
			ret.add(filled);
		}
		return ret;
	}

	private List<Assignment> validAssignments(int size) {
		List<List<String>> namesList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			List<String> names = Arrays.asList(example.inputTableNames());
			namesList.add(names);
		}

		List<Assignment> ret = new ArrayList<>();
		for (List<String> a : Utils.cartesianProduct(namesList)) {
			Assignment assign = new Assignment(a);
			if (assign.isValidAssignment()) {
				ret.add(assign);
			}
		}
		return ret;
	}

	private class Assignment {
		final List<String> names;

		public Assignment(List<String> names) {
			this.names = names;
		}

		private boolean isValidAssignment() {
			// all IDs must be used at least once.
			Set<String> set = new HashSet<>(names);
			if (set.size() != example.inputs.length) {
				return false;
			}

			return true;
		}
	}

}
