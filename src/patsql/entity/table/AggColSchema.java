package patsql.entity.table;

import patsql.entity.table.agg.Agg;

public class AggColSchema extends ColSchema {
	public final Agg agg;
	public final ColSchema src;

	public AggColSchema(Agg agg, ColSchema src) {
		super(agg + "(" + src.name + ")", agg.returnType(src.type));
		this.agg = agg;
		this.src = src;
	}

	public int srcColId() {
		return src.id;
	}

}
