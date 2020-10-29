package patsql.entity.table;

public enum Type {
	Int, //
	Dbl, //
	Str, //
	Date, //
	Null;

	public static Type from(String str) {
		switch (str.toLowerCase()) {
		case "int":
			return Int;
		case "dbl":
			return Dbl;
		case "str":
			return Str;
		case "date":
			return Date;
		case "null":
			return Null;
		}
		throw new IllegalStateException("invalid type string :" + str);
	}

	public static boolean canBeInserted(Type schemaType, Type cellType) {
		if (schemaType == Null || cellType == Null)
			return true;
		return schemaType == cellType;
	}

	public int compare(String value1, String value2) {
		switch (this) {
		case Int: {
			int v1 = Integer.parseInt(value1);
			int v2 = Integer.parseInt(value2);
			return v1 - v2;
		}
		case Dbl: {
			double v1 = Double.parseDouble(value1);
			double v2 = Double.parseDouble(value2);
			if (v1 == v2) {
				return 0;
			} else if (v1 < v2) {
				return -1;
			} else {
				return 1;
			}
		}
		case Str:
			return value1.compareTo(value2);
		case Date:
			DateValue v1 = DateValue.parse(value1);
			DateValue v2 = DateValue.parse(value2);
			return v1.compareTo(v2);
		case Null:
			throw new IllegalStateException("not supported.");
		}
		throw new IllegalStateException("unknown type.");
	}
}
