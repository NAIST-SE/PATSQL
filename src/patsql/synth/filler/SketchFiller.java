package patsql.synth.filler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.RAUtils;
import patsql.ra.util.RAVisitor;
import patsql.synth.Debug;
import patsql.synth.filler.strategy.BaseTablePrune;
import patsql.synth.filler.strategy.FillingStrategy;
import patsql.synth.filler.strategy.GroupbyPrune;
import patsql.synth.filler.strategy.JoinPrune;
import patsql.synth.filler.strategy.LeftJoinPrune;
import patsql.synth.filler.strategy.ProjectionMatching;
import patsql.synth.filler.strategy.ProjectionUnknown;
import patsql.synth.filler.strategy.Prune;
import patsql.synth.filler.strategy.SelectionAllConstantsUsed;
import patsql.synth.filler.strategy.SelectionPrune;
import patsql.synth.filler.strategy.SortExact;
import patsql.synth.filler.strategy.SortUnknown;
import patsql.synth.filler.strategy.WindowPrune;

import java.util.Stack;
import java.util.TreeMap;

public class SketchFiller {
	private final RAOperator sketchOrg;
	private final Example example;
	private final SynthOption option;

	private final Map<Integer, FillingConstraint> id2constraintMap = new TreeMap<>();

	public SketchFiller(RAOperator sketch, Example example, SynthOption option) {
		this.sketchOrg = sketch;
		this.example = example;
		this.option = option;

		propagateConstraint();
	}

	public void printConstraint() {
		for (Entry<Integer, FillingConstraint> e : id2constraintMap.entrySet()) {
			System.out.println(e.getKey() + "->" + e.getValue());
		}
	}

	private void propagateConstraint() {
		RAUtils.traverse(sketchOrg, new RAVisitor() {
			@Override
			public boolean on(RAOperator target) {
				// Initialize with constraints that are the same as the output.
				if (id2constraintMap.isEmpty()) {
					FillingConstraint constraint = FillingConstraint.sameAsOutput();
					id2constraintMap.put(target.ID(), constraint);
				}

				// propagate constraints to children
				for (RAOperator child : RAUtils.children(target)) {
					FillingConstraint current = id2constraintMap.get(target.ID());
					FillingConstraint next = current.update(target.kind);
					id2constraintMap.put(child.ID(), next);
				}
				return true;
			}
		});

	}

	public List<RAOperator> fillSketch() {
		// Filling a sketch in bottom-up manner. Therefore, the order of operators from
		// the bottom to top should be calculated here.
		Stack<Integer> targets = new Stack<>();
		RAUtils.traverse(sketchOrg, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				targets.add(op.ID());
				return true;
			}
		});
		List<RAOperator> worklist = new ArrayList<>();
		worklist.add(sketchOrg);
		while (!targets.empty()) {
			long startDebug = System.nanoTime();
			int targetId = targets.pop();
			worklist = fill(worklist, targetId);

			if (Debug.isDebugMode) {
				RAOperator t = searchSub(sketchOrg, targetId);
				long dur = (System.nanoTime() - startDebug) / 1000000;
				String str = String.format("%10d ms", dur);
				System.out.println(str + " " + worklist.size() + " " + t.kind);
				Debug.Time.register(t.kind, id2constraintMap.get(targetId), dur);
			}

			if (worklist.isEmpty()) {
				break;
			}
		}
		return worklist;
	}

	private List<RAOperator> fill(List<RAOperator> sketches, int targetId) {
		FillingStrategy strategy = chooseStrategy(targetId);

		// fill children using the strategy.
		List<RAOperator> ret = new ArrayList<>();
		for (RAOperator sk : sketches) {
			RAOperator sub = searchSub(sk, targetId);
			for (RAOperator filled : strategy.fill(sub, example, option)) {
				RAOperator r = RAUtils.replace(sk, filled, sub);
				ret.add(r);
			}
		}
		return ret;
	}

	private FillingStrategy chooseStrategy(int targetId) {
		// resolve the kind of component and level
		RAOperator target = searchSub(sketchOrg, targetId);
		FillingConstraint constraint = id2constraintMap.get(targetId);

		// select a filling strategy.
		FillingStrategy strategy = null;
		switch (target.kind) {
		case DISTINCT:
		case ROOT:
			// Pruning is executed.
			strategy = new Prune(constraint);
			break;
		case BASETABLE:
			strategy = new BaseTablePrune(constraint);
			break;
		case PROJECTION: {
			if (constraint.includeUnknown()) {
				// This projection is useless.
				strategy = new ProjectionUnknown(constraint);
				break;
			}
			if (constraint.col.matching == ColMatching.EXISTS) {
				// This projection is useless.
				strategy = new ProjectionUnknown(constraint);
				break;
			}
			if (constraint.col.matching == ColMatching.EXACT) { // always true
				strategy = new ProjectionMatching(constraint.col.relation);
			}
			break;
		}
		case SELECTION: {
			if (constraint.consts == ConstConstraint.AllConstsUsed) {
				strategy = new SelectionAllConstantsUsed(constraint);
			} else {
				strategy = new SelectionPrune(constraint);
			}
			break;
		}
		case GROUPBY: {
			strategy = new GroupbyPrune(constraint);
			break;
		}
		case WINDOWFUNC: {
			strategy = new WindowPrune(constraint);
			break;
		}
		case SORT: {
			if (constraint.col.matching == ColMatching.EXACT && constraint.col.relation == ColRelation.BAG) {
				strategy = new SortExact();
			} else {
				strategy = new SortUnknown();
			}
			break;
		}
		case JOIN: {
			strategy = new JoinPrune(constraint);
			break;
		}
		case LEFTJOIN: {
			strategy = new LeftJoinPrune(constraint);
			break;
		}
		}
		if (strategy == null) {
			throw new IllegalStateException("strategy is not selected.");
		}
		return strategy;
	}

	private RAOperator searchSub(RAOperator entire, int id) {
		RAOperator[] ret = new RAOperator[1];
		RAUtils.traverse(entire, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (id == op.ID()) {
					ret[0] = op;
					return false;
				}
				return true;
			}
		});
		if (ret[0] == null) {
			throw new IllegalStateException(id + " is not found.");
		}
		return ret[0];
	}

}
