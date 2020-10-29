package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Sort;
import patsql.synth.filler.FillingConstraint;

public class SortExact implements FillingStrategy {

	final FillingConstraint constraint = FillingConstraint.sameAsOutput();

	@Override
	public RA targetKind() {
		return RA.SORT;
	}

	/**
	 * A list whose length is at most 1 is returned. The element in the list is the
	 * smallest key pair to realize a given manipulation. <br>
	 * 
	 * NOTE: The precondition of this filling must be EXACT-BAG, which is naturally
	 * ensured by a constraint propagation.
	 */
	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}
		Sort target = (Sort) sketch;

		Table outTable = example.output;
		Table tmpTable = target.child.eval(example.tableEnv());

		TreeMap<Integer, List<Candidate>> keys = new TreeMap<>();

		Queue<Candidate> queue = new LinkedList<>();
		queue.add(new Candidate(tmpTable));
		while (!queue.isEmpty()) {
			Candidate cand = queue.remove();

			int size = cand.size();
			List<Candidate> list = keys.get(size);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(cand);
			if (size > 0)
				keys.put(size, list);

			// generate nest candidates
			queue.addAll(cand.next(tmpTable, outTable));
		}

		// resolve target tables to be realized by this sort.
		List<Candidate> wantedTables = new ArrayList<>();
		for (Candidate candMax : keys.get(keys.lastKey())) {
			wantedTables.add(candMax);
		}

		// search keys in increasing order.
		for (List<Candidate> cands : keys.values()) {
			// reflect priority.
			Collections.sort(cands);
			for (Candidate cand : cands) {
				for (Candidate w : wantedTables) {
					if (hasSameRowORder(cand.eval(), w.eval())) {
						Sort clone = target.clone();
						clone.sortKeys = cand.sortKeysTmp();
						return Arrays.asList(clone);
					}
				}
			}
		}
		throw new IllegalStateException("");
	}

	private boolean hasSameRowORder(Table a, Table b) {
		if (a.width() != b.width()) {
			return false;
		}
		for (int i = 0; i < a.width(); i++) {
			if (!a.columns[i].hasSameVec(b.columns[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * A candidate for sort key that is used for filling.
	 */
	private class Candidate implements Comparable<Candidate> {
		final SortKey[] sortKeysTmp;
		final SortKey[] sortKeysOut;
		final Table tmpTable;

		public Candidate(Table tmpTable) {
			this.sortKeysTmp = new SortKey[0];
			this.sortKeysOut = new SortKey[0];
			this.tmpTable = tmpTable;
		}

		public Candidate(Table tmpTable, SortKey[] sortKeysTmp, SortKey[] sortKeysOut) {
			if (sortKeysTmp.length != sortKeysOut.length) {
				throw new IllegalStateException("The size of two tables must be the same.");
			}

			this.sortKeysTmp = sortKeysTmp;
			this.sortKeysOut = sortKeysOut;
			this.tmpTable = tmpTable;
		}

		private SortKeys sortKeysTmp() {
			if (sortKeysTmp.length == 0) {
				return SortKeys.nil();
			}
			return new SortKeys(sortKeysTmp);
		}

		public int size() {
			return sortKeysTmp.length;
		}

		public Table eval() {
			return tmpTable.sort(sortKeysTmp());
		}

		public List<Candidate> next(Table tmpTable, Table outTable) {
			// grouping by the key.
			Collection<Table> groups = outTable.groups(groupKeysOut());

			List<Candidate> ret = new ArrayList<>();
			for (int i = 0; i < outTable.width(); i++) {
				ColSchema scTmp = tmpTable.columns[i].schema;
				ColSchema scOut = outTable.columns[i].schema;

				// resolve a needed order.
				Order order = null;
				if (needsAscSort(scOut, groups)) {
					order = Order.Asc;
				} else if (needsDescSort(scOut, groups)) {
					order = Order.Desc;
				}
				if (order != null) {
					SortKey keyT = new SortKey(scTmp, order);
					SortKey keyO = new SortKey(scOut, order);
					ret.add(new Candidate(tmpTable, append(sortKeysTmp, keyT), append(sortKeysOut, keyO)));
				}
			}
			return ret;
		}

		private GroupKeys groupKeysOut() {
			ColSchema[] sc = new ColSchema[sortKeysOut.length];
			for (int i = 0; i < sc.length; i++) {
				sc[i] = sortKeysOut[i].col;
			}
			return new GroupKeys(sc);
		}

		private SortKey[] append(SortKey[] keys, SortKey key) {
			SortKey[] ret = new SortKey[keys.length + 1];
			for (int i = 0; i < keys.length; i++) {
				ret[i] = keys[i];
			}
			ret[keys.length] = key;
			return ret;
		}

		private boolean needsAscSort(ColSchema sc, Collection<Table> tables) {
			boolean ret = false;
			for (Table table : tables) {
				if (table.isDecreasing(sc)) {
					// if cells within a group decreases, the sort key is invalid
					return false;
				}
				if (!ret && table.isIncreasing(sc)) {
					// at least one group require the sort key.
					ret = true;
				}
			}
			return ret;
		}

		private boolean needsDescSort(ColSchema sc, Collection<Table> tables) {
			boolean ret = false;
			for (Table table : tables) {
				if (table.isIncreasing(sc)) {
					// if cells within a group decreases, the sort key is invalid
					return false;
				}
				if (!ret && table.isDecreasing(sc)) {
					// at least one group require the sort key.
					ret = true;
				}
			}
			return ret;
		}

		private boolean hasAggCol() {
			for (SortKey k : sortKeysTmp) {
				if (k.col instanceof AggColSchema)
					return true;
			}
			return false;
		}

		@Override
		public int compareTo(Candidate o) {
			if (sortKeysTmp.length != o.sortKeysTmp.length) {
				return sortKeysTmp.length - o.sortKeysTmp.length;
			}

			if (hasAggCol() && o.hasAggCol()) {
				return 0;
			} else if (o.hasAggCol()) {
				return -1;
			} else if (hasAggCol()) {
				return 1;
			}
			return 0;
		}

	}

}
