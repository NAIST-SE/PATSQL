package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.Table;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.Utils;
import patsql.synth.filler.ColRelation;

/**
 * In this algorithm, a projection sketch is filled by matching the intermediate
 * columns to the output columns. The matching criteria is given as "relation".
 */
public class ProjectionMatching implements FillingStrategy {
	final ColRelation relation;

	public ProjectionMatching(ColRelation relation) {
		this.relation = relation;
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
		Table tmpTable = target.child.eval(example.tableEnv());
		Table outTable = example.output;

		// match columns between the intermediate and output tables.
		List<List<Column>> pairsForEachOutCol = new ArrayList<>();
		for (Column outCol : outTable.columns) {
			List<Column> pairs = new ArrayList<>();
			for (Column tmpCol : tmpTable.columns) {
				if (relation.compare(outCol, tmpCol)) {
					if (tmpCol.hasSameName(outCol)) {
						// if there exists a column with the same name, use only it.
						pairs.clear();
						pairs.add(tmpCol);
						break;
					} else {
						pairs.add(tmpCol);
					}
				}
			}
			pairsForEachOutCol.add(pairs);
		}

		// fill sketches.
		List<RAOperator> ret = new ArrayList<>();
		for (List<Column> cols : Utils.cartesianProduct(pairsForEachOutCol)) {
			Projection clone = target.clone();
			clone.projCols = toCols(cols);
			ret.add(clone);
		}
		return ret;
	}

	private ColSchema[] toCols(List<Column> cols) {
		ColSchema[] ret = new ColSchema[cols.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = cols.get(i).schema;
		}
		return ret;
	}

}
