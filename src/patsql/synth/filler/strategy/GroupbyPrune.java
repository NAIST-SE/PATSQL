package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.WinColSchema;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.FillingConstraint;
import patsql.synth.filler.TableMemo;

public class GroupbyPrune implements FillingStrategy {

	final FillingConstraint constraint;

	public GroupbyPrune(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		return RA.GROUPBY;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}

		GroupBy target = (GroupBy) sketch;
		Table outTable = example.output;
		Table tmpTable = target.child.eval(example.tableEnv());

		// All the columns are always used as aggregation results.
		Aggregators ags = Aggregators.all(tmpTable.schema());
		List<RAOperator> ret = new ArrayList<>();

		{ // nil key
			GroupKeys keys = GroupKeys.nil();
			Table got = tmpTable.groupBy(keys, ags);
			if (!constraint.isPruned(got, outTable)) {
				GroupBy clone = cloneWith(target, keys, ags);
				ret.add(clone);
			}
		}

		TableMemo memo = new TableMemo();
		// with actual keys
		for (GroupKeys keys : keyCandidates(tmpTable)) {
			Table got = tmpTable.groupBy(keys, ags);
			if (!constraint.isPruned(got, outTable) && memo.lookup(got)) {
				GroupBy clone = cloneWith(target, keys, ags);
				ret.add(clone);
			}
		}
		return ret;
	}

	/**
	 * Only one or two keys are allowed.<br>
	 * Unique keys are skipped because they are useless.
	 */
	private List<GroupKeys> keyCandidates(Table tmpTable) {
		List<GroupKeys> ret = new ArrayList<>();
		for (int i = 0; i < tmpTable.width(); i++) {
			ColSchema sc1 = tmpTable.schema()[i];
			if (sc1 instanceof WinColSchema || sc1 instanceof AggColSchema)
				continue;

			// single key
			GroupKeys k1 = new GroupKeys(sc1);
			if (isValidKey(tmpTable, k1)) {
				ret.add(k1);
			} else {
				continue; // skip finding the second key.
			}

			for (int j = i + 1; j < tmpTable.width(); j++) {
				ColSchema sc2 = tmpTable.schema()[j];
				if (sc1 instanceof WinColSchema || sc1 instanceof AggColSchema)
					continue;

				// skip meaningless keys
				GroupKeys k2 = new GroupKeys(sc2);
				if (!isValidKey(tmpTable, k2))
					continue;

				// two keys
				GroupKeys k = new GroupKeys(sc1, sc2);
				if (!isValidKey(tmpTable, k))
					continue;

				if (tmpTable.groups(k1).size() < tmpTable.groups(k).size()) {
					ret.add(k);
				}
			}
		}

		// sort by the key length.
		Collections.sort(ret, new Comparator<GroupKeys>() {
			@Override
			public int compare(GroupKeys k1, GroupKeys k2) {
				return k1.colSchemas.length - k2.colSchemas.length;
			}
		});
		return ret;
	}

	private boolean isValidKey(Table tmpTable, GroupKeys k) {
		return !tmpTable.hasUnqueCells(k) && tmpTable.hasDifferentCells(k);
	}

	private GroupBy cloneWith(GroupBy target, GroupKeys keys, Aggregators ags) {
		GroupBy clone = target.clone();
		clone.keys = keys;
		clone.ags = ags;
		return clone;
	}

}
