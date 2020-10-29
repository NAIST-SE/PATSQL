package patsql.ra.operator;

import java.util.Arrays;

public enum RA {
	ROOT, //
	SORT, //
	DISTINCT, //
	PROJECTION, //
	SELECTION, //
	GROUPBY, //
	WINDOWFUNC, //
	JOIN, //
	LEFTJOIN, //
	BASETABLE; //

	public static RA from(RAOperator op) {
		if (op instanceof Root)
			return ROOT;
		if (op instanceof Sort)
			return SORT;
		if (op instanceof Distinct)
			return DISTINCT;
		if (op instanceof Projection)
			return PROJECTION;
		if (op instanceof Selection)
			return SELECTION;
		if (op instanceof GroupBy)
			return GROUPBY;
		if (op instanceof Join)
			return JOIN;
		if (op instanceof LeftJoin)
			return LEFTJOIN;
		if (op instanceof Window)
			return WINDOWFUNC;
		if (op instanceof BaseTable)
			return BASETABLE;

		throw new IllegalStateException("unknown operator type : " + op);
	}

	public int index() {
		for (int i = 0; i < RA.values().length; i++) {
			if (this == RA.values()[i])
				return i;
		}
		return -1;
	}

	public int weight() {
		return Arrays.asList(//
				BASETABLE, //
				SELECTION, //
				WINDOWFUNC, //
				GROUPBY, //
				JOIN, //
				LEFTJOIN, //
				PROJECTION, //
				DISTINCT, //
				SORT, //
				ROOT//
		).indexOf(this);
	}

}
