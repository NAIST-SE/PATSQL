package patsql.entity.table.agg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;

public class Aggregators {
	public final AggColSchema[] aggColSchemas;

	public Aggregators(AggColSchema... aggColSchemas) {
		this.aggColSchemas = aggColSchemas;
	}

	public static Aggregators all(ColSchema... srcCols) {
		List<AggColSchema> ret = new ArrayList<>();

		for (Agg agg : Agg.values()) {
			for (ColSchema src : srcCols) {
				if (!agg.isComputable(src.type))
					continue;

				// avoid MAX(ConcatComma(...))
				if (src instanceof AggColSchema) {
					AggColSchema ag = (AggColSchema) src;
					if (ag.agg == Agg.ConcatComma || ag.agg == Agg.ConcatSlash || ag.agg == Agg.ConcatSpace)
						continue;
				}
				ret.add(new AggColSchema(agg, src));
			}
		}
		return new Aggregators(ret.toArray(new AggColSchema[0]));
	}

	public static Aggregators empty() {
		return new Aggregators();
	}

	@Override
	public String toString() {
		return Arrays.toString(aggColSchemas);
	}

}
