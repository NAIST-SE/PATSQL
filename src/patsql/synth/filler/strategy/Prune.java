package patsql.synth.filler.strategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.BitTable;
import patsql.entity.table.Table;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.FillingConstraint;

/**
 * Prune prunes sketches using the constraint. This pruning is applied to a
 * sketch without holes.
 */
public class Prune implements FillingStrategy {

	final FillingConstraint constraint;

	public Prune(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		throw new IllegalStateException("unreachable");
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		Table outTable = example.output;
		BitTable result = new BitTable(sketch.eval(example.tableEnv()));

		if (!constraint.isPruned(result, outTable)) {
			return Arrays.asList(sketch);
		}
		return Collections.emptyList();
	}

}
