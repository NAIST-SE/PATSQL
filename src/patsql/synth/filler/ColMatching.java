package patsql.synth.filler;

import patsql.ra.operator.RA;

public enum ColMatching {
	EXACT, // all the columns match.

	EXISTS, // for all the columns in the output, there exists a column that corresponds to
			// it in the intermediate table.

	UNKNOWN //
	;

	public ColMatching update(RA ra) {
		if (this == UNKNOWN)
			return UNKNOWN;

		if (ra == RA.PROJECTION) {
			if (this == EXACT) {
				return EXISTS; // make it weak.
			}
			if (this == EXISTS) {
				return EXISTS;// keep
			}
		}

		return this; // keep
	}
}
