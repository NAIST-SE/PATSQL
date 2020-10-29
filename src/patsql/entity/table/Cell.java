package patsql.entity.table;

import java.text.DecimalFormat;

public class Cell implements Comparable<Cell> {
	public final Type type;
	public final String value;

	public Cell(String value, Type type) {
		// validation and formatting
		switch (type) {
		case Int:
			Integer.parseInt(value);
			break;
		case Dbl:
			double d = Double.parseDouble(value);
			DecimalFormat fm = new DecimalFormat("#.000");
			value = fm.format(d);
			break;
		case Null:
			value = "NULL";
			break;
		case Str:
			// NOP
			break;
		case Date:
			DateValue dateCell = DateValue.parse(value);
			value = dateCell.toString();
			break;
		}

		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int compareTo(Cell other) {
		// Null is the largest.
		if (this.type == Type.Null && other.type == Type.Null) {
			return 0;
		} else if (this.type == Type.Null) {
			return -1;
		} else if (other.type == Type.Null) {
			return 1;
		}

		return type.compare(this.value, other.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Cell other = (Cell) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
