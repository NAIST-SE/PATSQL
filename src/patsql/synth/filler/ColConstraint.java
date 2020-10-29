package patsql.synth.filler;

import patsql.entity.table.BitTable;
import patsql.entity.table.Column;
import patsql.entity.table.Table;
import patsql.ra.operator.RA;

public class ColConstraint {
	public final ColMatching matching;
	public final ColRelation relation;

	public ColConstraint(ColMatching matching, ColRelation relation) {
		this.matching = matching;
		this.relation = relation;
	}

	public static ColConstraint sameAsOutput() {
		return new ColConstraint(ColMatching.EXACT, ColRelation.BAG);
	}

	public ColConstraint update(RA input) {
		ColMatching nextM = matching.update(input);
		ColRelation nextR = relation.update(input);
		return new ColConstraint(nextM, nextR);
	}

	public boolean includeUnknown() {
		return matching == ColMatching.UNKNOWN || relation == ColRelation.UNKNOWN;
	}

	public boolean isPruned(BitTable currentTable, Table outTable) {
		if (!isValidHeight(currentTable.height(), outTable.height())) {
			return true;
		}

		return isPruned(currentTable.toTable(), outTable);
	}

	public boolean isPruned(Table currentTable, Table outTable) {
		if (includeUnknown()) {
			return false;
		}
		if (!isValidHeight(currentTable.height(), outTable.height())) {
			return true;
		}

		switch (matching) {
		case EXACT:
			if (currentTable.width() != outTable.width())
				return true;
			// comparison for all the columns.
			for (int i = 0; i < outTable.width(); i++) {
				Column col1 = currentTable.columns[i];
				Column col2 = outTable.columns[i];
				if (!relation.compare(col1, col2)) {
					return true;
				}
			}
			return false;
		case EXISTS:
			for (int j = 0; j < outTable.width(); j++) {
				boolean exists = false;
				for (int i = 0; i < currentTable.width(); i++) {
					Column col1 = currentTable.columns[i];
					Column col2 = outTable.columns[j];
					if (relation.compare(col1, col2)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					return true;
				}
			}
			return false;
		default:
			throw new IllegalStateException("relation can't be unknown.");
		}
	}

	private boolean isValidHeight(int heightOfCurrent, int heightOfOut) {
		switch (relation) {
		case BAG:
			if (heightOfCurrent != heightOfOut) {
				return false;
			}
			break;
		case SUPER_BAG:
			if (heightOfCurrent < heightOfOut) {
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public String toString() {
		return "C(" + matching.toString() + ", " + relation.toString() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matching == null) ? 0 : matching.hashCode());
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
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
		ColConstraint other = (ColConstraint) obj;
		if (matching != other.matching)
			return false;
		if (relation != other.relation)
			return false;
		return true;
	}

}
