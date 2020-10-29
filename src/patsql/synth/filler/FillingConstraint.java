package patsql.synth.filler;

import patsql.entity.table.BitTable;
import patsql.entity.table.Table;
import patsql.ra.operator.RA;

public class FillingConstraint {
	public final ColConstraint col;
	public final ConstConstraint consts;

	public FillingConstraint(ColConstraint col, ConstConstraint consts) {
		this.col = col;
		this.consts = consts;
	}

	public FillingConstraint(ColConstraint col) {
		this.col = col;
		this.consts = ConstConstraint.Unknown;
	}

	public static FillingConstraint sameAsOutput() {
		return new FillingConstraint(//
				ColConstraint.sameAsOutput(), //
				ConstConstraint.AllConstsUsed//
		);
	}

	public FillingConstraint update(RA input) {
		return new FillingConstraint(//
				col.update(input), //
				consts.update(input)//
		);
	}

	public boolean includeUnknown() {
		return col.includeUnknown();
	}

	public boolean isPruned(BitTable currentTable, Table outTable) {
		if (col.isPruned(currentTable, outTable)) {
			return true;
		}
		return false;
	}

	public boolean isPruned(Table currentTable, Table outTable) {
		if (col.isPruned(currentTable, outTable)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return col.toString() + "," + consts.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((col == null) ? 0 : col.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FillingConstraint other = (FillingConstraint) obj;
		if (col == null) {
			if (other.col != null)
				return false;
		} else if (!col.equals(other.col))
			return false;
		return true;
	}

}
