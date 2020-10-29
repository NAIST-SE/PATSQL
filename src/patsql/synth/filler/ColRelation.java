package patsql.synth.filler;

import patsql.entity.table.Column;
import patsql.ra.operator.RA;

public enum ColRelation {
	BAG, // equality as bag, or multi-set
	SET, // equality as set
	SUPER_BAG, // superset w.r.t multi-set
	SUPER_SET, // superset
	UNKNOWN //
	;

	public ColRelation update(RA ra) {
		if (this == UNKNOWN)
			return UNKNOWN;

		switch (ra) {
		case DISTINCT:
			if (this == BAG) {
				return SET;
			}
			return this; // keep
		case SELECTION:
			if (this == BAG) {
				return SUPER_BAG;
			} else if (this == SET) {
				return SUPER_SET;
			}
			return this;// keep
		case SORT:
			return BAG;
		case GROUPBY:
		case WINDOWFUNC:
		case LEFTJOIN:
		case JOIN:
			return UNKNOWN;
		case PROJECTION:
		case ROOT:
		case BASETABLE:
			return this; // keep
		}

		throw new IllegalStateException("unknown operator type : " + ra);
	}

	public boolean compare(Column tmpCol, Column outCol) {
		switch (this) {
		case BAG:
			return tmpCol.hasSameBag(outCol);
		case SET:
			return tmpCol.hasSameSet(outCol);
		case SUPER_SET:
			return tmpCol.isSuperSetOf(outCol);
		case SUPER_BAG:
			return tmpCol.isSuperBagOf(outCol);
		default:
			throw new IllegalStateException("relation can't be unknown.");
		}
	}
}
