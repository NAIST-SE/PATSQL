package patsql.synth.filler.strategy;

import java.util.Arrays;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.sort.SortKeys;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Sort;

public class SortUnknown implements FillingStrategy {

	@Override
	public RA targetKind() {
		return RA.SORT;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}
		Sort target = (Sort) sketch;
		Sort clone = target.clone();
		clone.sortKeys = SortKeys.nil(); // because sort is useless.
		return Arrays.asList(clone);
	}

}
