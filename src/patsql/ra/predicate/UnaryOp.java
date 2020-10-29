package patsql.ra.predicate;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

public enum UnaryOp {
	IsNull("IS NULL"), //
	IsNotNull("IS NOT NULL");//

	private final String str;

	private UnaryOp(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}

	public ExBool eval(Cell cell) {
		switch (this) {
		case IsNull:
			return (cell.type == Type.Null) ? //
					ExBool.True : ExBool.False;
		case IsNotNull:
			return (cell.type != Type.Null) ? //
					ExBool.True : ExBool.False;
		}

		throw new IllegalStateException("switch error :" + cell.toString());
	}
}
