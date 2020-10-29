package patsql.synth.filler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import patsql.entity.table.Cell;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;
import patsql.ra.util.BitUtil;
import patsql.ra.util.PredUtil;
import patsql.ra.util.Utils;

public class RowSearchConsumingAllConstants {
	private final Cell[] consts;
	private final int numOfRows;

	private List<BitRow> primRows = new ArrayList<>();

	private Map<Cell, BitRowMemo> const2memo = new LinkedHashMap<>();
	private BitRowMemo memo = new BitRowMemo();

	public RowSearchConsumingAllConstants(int numOfRows, Cell... constants) {
		consts = constants;
		this.numOfRows = numOfRows;
	}

	public void addPred(Predicate pred, BitSet bits) {
		BitRow row = new BitRow(pred, bits);
		if (row.isEmpty()) {
			return;
		}
		if (isNotEq(pred) && bits.cardinality() == numOfRows) {
			return; // ignore irrelevant comparison with "<>".
		}
		if (isNotNull(pred) && bits.cardinality() == numOfRows) {
			return; // ignore IS NOT NULL, which is always true.
		}

		Set<Cell> consts = PredUtil.usedConstants(pred);
		if (consts.size() == 0) {
			// condition with an unary operator
			primRows.add(row);
		} else if (consts.size() == 1) {
			// condition with a binary operator
			Cell c = new ArrayList<>(consts).get(0);
			BitRowMemo memo = const2memo.get(c);
			if (memo == null) {
				memo = new BitRowMemo();
			}
			if (memo.lookup(row)) {
				primRows.add(row);
			}
			const2memo.put(c, memo);
		} else {
			throw new IllegalStateException("unsupported predicate: " + pred);
		}
	}

	private boolean isNotEq(Predicate pred) {
		if (!(pred instanceof BinaryPred))
			return false;

		BinaryPred bin = (BinaryPred) pred;
		return bin.op == BinaryOp.NotEq;
	}

	private boolean isNotNull(Predicate pred) {
		if (!(pred instanceof UnaryPred))
			return false;

		UnaryPred un = (UnaryPred) pred;
		return un.op == UnaryOp.IsNotNull;
	}

	public void generateKernel() {
		// 1. resolve kernel, which is composed of all the constants.
		List<Conj> kernels = resolveKernels();

		// 2. extend kernels
		expandKernels(kernels);
	}

	private void expandKernels(List<Conj> kernels) {
		List<Conj> candidates = new ArrayList<>();
		candidates.addAll(kernels);

		while (!candidates.isEmpty()) {
			List<Conj> nexts = new ArrayList<>();
			for (Conj cand : candidates) {
				for (BitRow prim : primRows) {
					for (Conj c : cand.expand(prim)) {
						BitRow row = new BitRow(c.pred(), c.bits());
						if (memo.lookup(row)) {
							nexts.add(c);
						}
					}
				}
			}
			candidates = nexts;
		}
	}

	private List<Conj> resolveKernels() {
		List<Conj> kernels = new ArrayList<>();

		List<List<BitRow>> condsForEachConst = new ArrayList<>();
		for (Cell c : consts) {
			BitRowMemo memo = const2memo.get(c);
			if (memo == null)
				return Collections.emptyList(); // when no columns use the constant
			condsForEachConst.add(memo.rows());
		}

		// conversion:
		// [[a=1, a>1], [b=5, b>5]] => [[a=1, b=5], [a=1, b>5], [a>1, b=5], [a>1, b>5]]
		for (List<BitRow> rowComb : Utils.cartesianProduct(condsForEachConst)) {
			List<Conj> candidates = new ArrayList<>();
			// start with the empty condition.
			candidates.add(new Conj());
			for (BitRow row : rowComb) {
				// expand each candidate by inserting a predicate with a constant.
				List<Conj> nexts = new ArrayList<>();
				for (Conj cand : candidates) {
					nexts.addAll(cand.expand(row));
				}
				candidates = nexts;
			}
			// insert into the returned list
			for (Conj c : candidates) {
				BitRow row = new BitRow(c.pred(), c.bits());
				if (memo.lookup(row)) {
					kernels.add(c);
				}
			}
		}

		return kernels;
	}

	public List<BitRow> allRows() {
		return memo.rows();
	}

	private class Conj {
		final List<Disj> clauses = new ArrayList<>();

		public List<Conj> expand(BitRow row) {
			if (clauses.isEmpty()) {
				Conj ret = new Conj();
				Disj d = new Disj();
				d.add(row);
				ret.add(d);
				return Arrays.asList(ret);
			}

			List<Conj> ret = new ArrayList<>();
			// append it as a new AND.
			{
				Conj clone = clone();
				Disj dis = new Disj();
				dis.add(row);
				clone.add(dis);
				if (!clone.bits().isEmpty()) {
					ret.add(clone);
				}
			}

			// insert it as a new OR in an existing clause.
			for (int i = 0; i < clauses.size(); i++) {
				Conj clone = clone();
				Disj dis = clone.clauses.get(i);
				dis.add(row);
				if (!clone.bits().isEmpty()) {
					ret.add(clone);
				}
			}
			return ret;
		}

		public BitSet bits() {
			BitSet ret = new BitSet();
			for (int i = 0; i < clauses.size(); i++) {
				BitSet b = clauses.get(i).bits();
				if (i == 0) {
					ret.or(b); // set first
				} else {
					ret.and(b);
				}
			}
			return ret;
		}

		public void add(Disj d) {
			clauses.add(d);
		}

		@Override
		public Conj clone() {
			Conj clone = new Conj();
			// deep copy
			for (Disj c : clauses) {
				clone.clauses.add(c.clone());
			}
			return clone;
		}

		public Conjunction pred() {
			Disjunction[] ds = clauses.stream().map(Disj::toPred).toArray(Disjunction[]::new);
			return new Conjunction(ds);
		}

	}

	private class Disj {
		final List<BitRow> terms = new ArrayList<>();

		public void add(BitRow row) {
			terms.add(row);
		}

		@Override
		public Disj clone() {
			Disj clone = new Disj();
			clone.terms.addAll(terms);
			return clone;
		}

		public Disjunction toPred() {
			Predicate[] ps = terms.stream().map(e -> e.pred).toArray(Predicate[]::new);
			return new Disjunction(ps);
		}

		public BitSet bits() {
			BitSet ret = new BitSet();
			for (BitRow r : terms) {
				ret.or(r.rowBits);
			}
			return ret;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" --- BITS --- \n");
		for (BitRow row : memo.rows()) {
			sb.append(BitUtil.toBitString(row.rowBits, numOfRows));
			sb.append("  ");
			sb.append(row.pred.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public class BitRowMemo {
		private Map<BitSet, BitRow> map = new LinkedHashMap<>();

		public List<BitRow> rows() {
			List<BitRow> ret = new ArrayList<>();
			ret.addAll(map.values());
			return ret;
		}

		public boolean lookup(BitRow bits) {
			BitRow row = map.get(bits.rowBits);
			if (row != null) {
				// compare the number of "OR". Less is preferred.
				// bits.pred
				int n1 = PredUtil.numberOfOR(bits.pred);
				int n2 = PredUtil.numberOfOR(row.pred);
				if (n1 < n2) {
					map.put(bits.rowBits, bits);
				}
				return false;
			}
			map.put(bits.rowBits, bits);
			return true;
		}
	}

}
