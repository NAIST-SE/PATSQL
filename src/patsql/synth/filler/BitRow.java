package patsql.synth.filler;

import java.util.BitSet;
import java.util.Set;

import patsql.entity.table.Cell;
import patsql.ra.predicate.Predicate;
import patsql.ra.util.PredUtil;

public class BitRow {
	public final Predicate pred;
	public final BitSet rowBits;

	public BitRow(Predicate pred, BitSet rowBits) {
		this.pred = pred;
		this.rowBits = rowBits;
	}

	public boolean isEmpty() {
		return rowBits.isEmpty();
	}

	public Set<Cell> usedConstants() {
		return PredUtil.usedConstants(pred);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowBits == null) ? 0 : rowBits.hashCode());
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
		BitRow other = (BitRow) obj;
		if (rowBits == null) {
			if (other.rowBits != null)
				return false;
		} else if (!rowBits.equals(other.rowBits))
			return false;
		return true;
	}

}
