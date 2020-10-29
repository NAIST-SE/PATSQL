package patsql.synth.filler;

import patsql.ra.operator.RA;

public enum ConstConstraint {
	AllConstsUsed, //
	NotAllConstsUsed, //
	Unknown//
	;

	public ConstConstraint update(RA ra) {
		if (ra == RA.SELECTION) {
			return NotAllConstsUsed;
		}
		return this;
	}
}
