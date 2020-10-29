package patsql.synth.filler.strategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.ColSchema;
import patsql.entity.table.DateFuncColSchema;
import patsql.entity.table.Table;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.FillingConstraint;

public class BaseTablePrune implements FillingStrategy {

	final FillingConstraint constraint;

	public BaseTablePrune(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		return RA.BASETABLE;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}

		BaseTable target = (BaseTable) sketch;
		Table outTable = example.output;

		Table result = target.eval(example.tableEnv());
		if (!constraint.isPruned(result, outTable)) {
			// fill the sketch with new IDs.
			target.renamedCols = new ColSchema[result.width()];
			for (int i = 0; i < result.width(); i++) {
				ColSchema sc = result.columns[i].schema;
				if (sc instanceof DateFuncColSchema) {
					DateFuncColSchema dfcs = (DateFuncColSchema) sc;
					target.renamedCols[i] = new DateFuncColSchema(dfcs.func, dfcs.src); // new ID generated.
				} else
					target.renamedCols[i] = new ColSchema(sc.name, sc.type); // new ID generated.
			}
			return Arrays.asList(target);
		}
		return Collections.emptyList();
	}

}
