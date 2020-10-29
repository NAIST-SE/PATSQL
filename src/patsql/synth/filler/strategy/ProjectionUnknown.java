package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.Utils;
import patsql.synth.filler.FillingConstraint;

/**
 * This is a normal strategy that fills holes in a Select clause. In this
 * strategy, the information about the output table is not used, and it
 * generates all of the combinations of the columns in a intermediate table.
 */
public class ProjectionUnknown implements FillingStrategy {

	final FillingConstraint constraint;

	public ProjectionUnknown(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		return RA.PROJECTION;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}
		Projection target = (Projection) sketch;
		Table outTable = example.output;
		Table tmpTable = target.child.eval(example.tableEnv());

		List<RAOperator> ret = new ArrayList<>();
		for (List<ColSchema> comb : choose(tmpTable.schema())) {
			for (List<ColSchema> perm : permute(comb)) {
				int[] cols = perm.stream().mapToInt(e -> e.id).toArray();
				Table got = tmpTable.project(cols);
				if (!constraint.isPruned(got, outTable)) {
					Projection clone = target.clone();
					clone.projCols = perm.toArray(new ColSchema[0]);
					ret.add(clone);
				}
			}
		}
		return ret;
	}

	private List<List<ColSchema>> choose(ColSchema[] cols) {
		List<List<ColSchema>> combs = new ArrayList<>();
		for (ColSchema c : cols) {
			List<ColSchema> cs = new ArrayList<>();
			cs.add(null);
			cs.add(c);
			combs.add(cs);
		}

		List<List<ColSchema>> ret = new ArrayList<>();
		for (List<ColSchema> comb : Utils.cartesianProduct(combs)) {
			ret.add(comb.stream().filter(e -> e != null).collect(Collectors.toList()));
		}
		return ret;
	}

	private List<List<ColSchema>> permute(List<ColSchema> cols) {
		if (cols.isEmpty()) {
			List<List<ColSchema>> ret = new ArrayList<>();
			ret.add(new ArrayList<>());
			return ret;
		}

		List<List<ColSchema>> ret = new ArrayList<>();
		for (int i = 0; i < cols.size(); i++) {
			List<ColSchema> clone = new ArrayList<>(cols);
			ColSchema head = clone.remove(i);

			for (List<ColSchema> tail : permute(clone)) {
				List<ColSchema> cs = new ArrayList<>();
				cs.add(head);
				cs.addAll(tail);
				ret.add(cs);
			}
		}

		return ret;
	}

}
