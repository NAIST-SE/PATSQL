package patsql.ra.predicate;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

public enum BinaryOp {
	Eq("="), //
	Gt(">"), //
	Geq(">="), //
	Lt("<"), //
	Leq("<="), //
	NotEq("<>");

	private final String str;

	private BinaryOp(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}

	public ExBool eval(Cell c1, Cell c2) {
		if (c1.type == Type.Null || c2.type == Type.Null) {
			return ExBool.Unknown;
		}

		if (c1.type != c2.type) {
			throw new IllegalStateException("type error :" + c1.type + " <->" + c2.type);
		}

		switch (this) {
		case Eq:
			return (c1.value.equals(c2.value)) ? //
					ExBool.True : ExBool.False;
		case NotEq:
			return (c1.value.equals(c2.value)) ? //
					ExBool.False : ExBool.True;
		case Gt:
			switch (c1.type) {
			case Int:
				return (Integer.parseInt(c1.value) > Integer.parseInt(c2.value)) ? //
						ExBool.True : ExBool.False;
			case Dbl:
				return (Double.parseDouble(c1.value) > Double.parseDouble(c2.value)) ? //
						ExBool.True : ExBool.False;
			default:
				return (c1.value.compareTo(c2.value) > 0) ? //
						ExBool.True : ExBool.False;
			}
		case Geq:
			switch (c1.type) {
			case Int:
				return (Integer.parseInt(c1.value) >= Integer.parseInt(c2.value)) ? //
						ExBool.True : ExBool.False;
			case Dbl:
				return (Double.parseDouble(c1.value) >= Double.parseDouble(c2.value)) ? //
						ExBool.True : ExBool.False;
			default:
				return (c1.value.compareTo(c2.value) >= 0) ? //
						ExBool.True : ExBool.False;
			}
		case Lt:
			switch (c1.type) {
			case Int:
				return (Integer.parseInt(c1.value) < Integer.parseInt(c2.value)) ? //
						ExBool.True : ExBool.False;
			case Dbl:
				return (Double.parseDouble(c1.value) < Double.parseDouble(c2.value)) ? //
						ExBool.True : ExBool.False;
			default:
				return (c1.value.compareTo(c2.value) < 0) ? //
						ExBool.True : ExBool.False;
			}
		case Leq:
			switch (c1.type) {
			case Int:
				return (Integer.parseInt(c1.value) <= Integer.parseInt(c2.value)) ? //
						ExBool.True : ExBool.False;
			case Dbl:
				return (Double.parseDouble(c1.value) <= Double.parseDouble(c2.value)) ? //
						ExBool.True : ExBool.False;
			default:
				return (c1.value.compareTo(c2.value) <= 0) ? //
						ExBool.True : ExBool.False;
			}
		}

		throw new IllegalStateException("switch error :" + c1.toString() + " <->" + c2.toString());
	}
}
