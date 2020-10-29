package patsql.synth.filler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.Predicate;
import patsql.ra.util.BitUtil;

public class RowSearch {
	private List<BitRow> rows = new ArrayList<>();
	private BitSetMemo memo = new BitSetMemo();

	public void addPred(Predicate pred, BitSet bits) {
		BitRow row = new BitRow(pred, bits);
		if (!row.isEmpty() && memo.lookup(row.rowBits)) {
			rows.add(row);
		}
	}

	public void generate(Op operatoion) {
		// dp map: size -> indexes of BitRows.
		Map<Integer, List<BitRowCand>> dpMap = new HashMap<>();

		// set the base case (size = 1).
		List<BitRowCand> singles = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			BitSet row = rows.get(i).rowBits;
			List<Integer> idx = new ArrayList<>();
			idx.add(i);
			BitRowCand cand = new BitRowCand(operatoion, idx, row);
			singles.add(cand);
		}
		dpMap.put(1, singles);

		// set the cases with size more than one.
		for (int n = 2; true; n++) {
			List<BitRowCand> candidates = new ArrayList<>();
			for (BitRowCand previous : dpMap.get(n - 1)) {
				for (int idx = previous.maxIndex() + 1; idx < singles.size(); idx++) {
					// create next Join keys with pruning.
					BitRowCand added = singles.get(idx);
					BitRowCand result = previous.calc(added);
					if (!result.bits.isEmpty() && memo.lookup(result.bits)) {
						candidates.add(result);
					}
				}
			}
			// if no pair found, finish.
			if (candidates.isEmpty()) {
				break;
			} else {
				dpMap.put(n, candidates);
			}
		}

		// convert to BitRows
		List<BitRow> createdRows = new ArrayList<>();
		for (List<BitRowCand> c : dpMap.values()) {
			for (BitRowCand cand : c) {
				BitRow r = cand.toBitRow();
				createdRows.add(r);
			}
		}

		// update rows
		this.rows = createdRows;
	}

	private class BitRowCand {
		final Op op;
		final List<Integer> indexes; // sorted
		final BitSet bits;

		public BitRowCand(Op op, List<Integer> indexes, BitSet cache) {
			this.op = op;
			this.indexes = indexes;
			this.bits = cache;
		}

		public BitRow toBitRow() {
			Predicate[] preds = new Predicate[indexes.size()];
			for (int i = 0; i < indexes.size(); i++) {
				Integer idx = indexes.get(i);
				preds[i] = rows.get(idx).pred;
			}

			Predicate cond = null;
			if (preds.length == 1) {
				cond = preds[0];
			} else if (op == Op.AND) {
				cond = new Conjunction(preds);
			} else if (op == Op.OR) {
				cond = new Disjunction(preds);
			}
			if (cond == null)
				throw new IllegalStateException("unkwnon Op type: " + op);

			return new BitRow(cond, bits);
		}

		public int maxIndex() {
			return indexes.get(indexes.size() - 1);
		}

		public BitRowCand calc(BitRowCand other) {
			List<Integer> idx = new ArrayList<>();
			idx.addAll(indexes);
			idx.addAll(other.indexes);
			BitSet clone = op.calc(bits, other.bits);
			return new BitRowCand(op, idx, clone);
		}

	}

	public enum Op {
		AND, OR;

		public BitSet calc(BitSet a, BitSet b) {
			BitSet clone = (BitSet) a.clone();
			switch (this) {
			case AND:
				clone.and(b);
				break;
			case OR:
				clone.or(b);
				break;
			}
			return clone;
		}
	}

	public List<BitRow> singleRowsAll() {
		return rows;
	}

	private int maxBitsLength() {
		int ret = 0;
		for (BitRow row : rows) {
			int len = row.rowBits.length();
			if (len > ret)
				ret = len;
		}
		return ret;
	}

	@Override
	public String toString() {
		int len = maxBitsLength();
		StringBuilder sb = new StringBuilder();
		sb.append(" --- BITS --- \n");
		for (BitRow row : rows) {
			sb.append(BitUtil.toBitString(row.rowBits, len));
			sb.append("  ");
			sb.append(row.pred.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public class BitSetMemo {
		private Set<BitSet> memo = new HashSet<>();

		public boolean lookup(BitSet bits) {
			if (memo.contains(bits)) {
				return false;
			}
			memo.add(bits);
			return true;
		}
	}
}
