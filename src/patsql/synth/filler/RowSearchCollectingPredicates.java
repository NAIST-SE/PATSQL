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
import patsql.synth.filler.RowSearch.Op;

public class RowSearchCollectingPredicates {
	private List<BitRow> rows = new ArrayList<>();
	private BitSetMemo memo = new BitSetMemo();

	private BitSet targetBits;
	private Set<Predicate> predsCollected = new HashSet<>();

	public RowSearchCollectingPredicates(BitSet targetBits) {
		this.targetBits = targetBits;
	}

	public Set<Predicate> getPredicatesCollected() {
		return predsCollected;
	}

	public void addPred(Predicate pred, BitSet bits) {
		BitRow row = new BitRow(pred, bits);
		if (!row.isEmpty() && memo.lookup(row.rowBits, pred)) {
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
					Predicate pred = rows.get(idx).pred; // works fine ???????????????????????????
					if (!result.bits.isEmpty() && memo.lookup(result.bits, pred)) {
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

	public class BitSetMemo {
		private Set<BitSet> memo = new HashSet<>();

		public boolean lookup(BitSet bits, Predicate pred) {
			if (bits.equals(targetBits)) {
				predsCollected.add(pred);
			}

			if (memo.contains(bits)) {
				return false;
			}
			memo.add(bits);
			return true;
		}
	}
}
