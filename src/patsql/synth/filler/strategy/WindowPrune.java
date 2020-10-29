package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.Table;
import patsql.entity.table.WinColSchema;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.window.WinFunc;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Window;
import patsql.synth.filler.FillingConstraint;

public class WindowPrune implements FillingStrategy {

	final FillingConstraint constraint;

	public WindowPrune(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		return RA.WINDOWFUNC;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}
		Window target = (Window) sketch;
		Table outTable = example.output;
		Table tmpTable = target.child.eval(example.tableEnv());

		List<WinColSchema> winSchemas = new ArrayList<>();
		for (WinFunc func : WinFunc.values()) {
			for (ColSchema src : srcColumns(tmpTable)) {
				for (GroupKeys pKey : partitionKey(tmpTable)) {
					for (SortKey oKey : orderbyKey(tmpTable)) {
						WinColSchema wsc = new WinColSchema(func, src, pKey, oKey);
						if (wsc.isValid()) {
							winSchemas.add(wsc);
						}
					}
				}
			}
		}

		WinColSchema[] wsc = winSchemas.toArray(new WinColSchema[0]);
		List<RAOperator> ret = new ArrayList<>();
		Table got = tmpTable.applyWindow(wsc);
		if (!constraint.isPruned(got, outTable)) {
			Window clone = target.clone();
			clone.cols = reduce(wsc, tmpTable);
			ret.add(clone);
		}
		return ret;
	}

	private WinColSchema[] reduce(WinColSchema[] winSchemas, Table table) {
		List<Column> pool = new ArrayList<>(Arrays.asList(table.columns));

		List<WinColSchema> ret = new ArrayList<>();
		for (WinColSchema wsc : winSchemas) {
			Column col = eval(wsc, table);
			boolean exists = false;
			for (Column c : pool) {
				if (col.hasSameVec(c)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				ret.add(wsc);
				pool.add(col);
			}

		}
		return ret.toArray(new WinColSchema[0]);
	}

	private Column eval(WinColSchema wsc, Table table) {
		int w = table.width();
		return table.applyWindow(wsc).columns[w];
	}

	private ColSchema[] srcColumns(Table table) {
		List<ColSchema> ret = new ArrayList<>();
		ret.add(null);// null added for RANK() and so on.
		Arrays.stream(table.schema())//
				.filter(e -> !(e instanceof WinColSchema))//
				.filter(e -> !(e instanceof AggColSchema))//
				.forEach(e -> ret.add(e));
		return ret.toArray(new ColSchema[0]);
	}

	private GroupKeys[] partitionKey(Table table) {
		List<GroupKeys> ret = new ArrayList<>();
		ret.add(GroupKeys.nil());// without any partition key

		for (int i = 0; i < table.width(); i++) {
			ColSchema sc1 = table.schema()[i];
			if (sc1 instanceof WinColSchema || sc1 instanceof AggColSchema)
				continue;

			{ // single key
				GroupKeys k = new GroupKeys(sc1);
				if (!table.hasUnqueCells(k)) {
					ret.add(k);
				}
			}

			for (int j = i + 1; j < table.width(); j++) {
				ColSchema sc2 = table.schema()[j];
				if (sc1 instanceof WinColSchema || sc1 instanceof AggColSchema)
					continue;

				// two keys
				GroupKeys k = new GroupKeys(sc1, sc2);
				if (!table.hasUnqueCells(k)) {
					ret.add(k);
				}
			}
		}
		return ret.toArray(new GroupKeys[0]);
	}

	private SortKey[] orderbyKey(Table table) {
		List<SortKey> ret = new ArrayList<>();
		ret.add(null);// without any partition key
		for (ColSchema osc : table.schema()) {
			if (osc instanceof WinColSchema)
				continue;
			if (osc instanceof AggColSchema)
				continue;

			for (Order ord : Order.values()) {
				ret.add(new SortKey(osc, ord));
			}
		}
		return ret.toArray(new SortKey[0]);
	}

}
