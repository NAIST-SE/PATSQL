package patsql.entity.table.agg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

public enum Agg {
	Max, //
	Min, //
	Count, //
	CountD, //
	Avg, //
	Sum, //
	ConcatComma, // separated by comma
	ConcatSpace, // separated by space
	ConcatSlash,// separated by slash
	; //

	public Type returnType(Type operand) {
		switch (this) {
		case Max:
		case Min:
		case Sum:
			return operand;
		case Count:
		case CountD:
			return Type.Int;
		case Avg:
			return Type.Dbl;
		case ConcatComma:
		case ConcatSpace:
		case ConcatSlash:
			return Type.Str;
		}
		return Type.Null;
	}

	public boolean isComputable(Type operand) {
		if (operand == Type.Null) {
			return false;
		}

		switch (this) {
		case Max:
		case Min:
		case Count:
		case CountD:
			return true;
		case Sum:
		case Avg:
			switch (operand) {
			case Int:
			case Dbl:
				return true;
			default:
				return false;
			}
		case ConcatComma:
		case ConcatSpace:
		case ConcatSlash: {
			if (operand == Type.Str) {
				return true;
			} else {
				return false;
			}
		}
		}
		throw new IllegalStateException("unknown aggregator type.");
	}

	public Cell eval(Cell[] cells) {
		if (cells.length == 0) {
			return new Cell("null", Type.Null);
		}

		List<Cell> cellsNotNull = new ArrayList<>();
		for (Cell c : cells) {
			if (c.type != Type.Null) {
				cellsNotNull.add(c);
			}
		}
		if (cellsNotNull.size() == 0) {
			return new Cell("null", Type.Null);
		}

		final Type operandType = cellsNotNull.get(0).type;
		final Type retType = returnType(operandType);

		switch (this) {
		case Max: {
			Cell max = cellsNotNull.get(0);
			for (Cell c : cellsNotNull) {
				if (max.compareTo(c) < 0)
					max = c;
			}
			return max;
		}
		case Min: {
			Cell min = cellsNotNull.get(0);
			for (Cell c : cellsNotNull) {
				if (c.compareTo(min) < 0)
					min = c;
			}
			return min;
		}
		case Sum: {
			switch (operandType) {
			case Int: {
				int sum = 0;
				for (Cell c : cellsNotNull)
					sum += Integer.parseInt(c.value);
				return new Cell(Integer.toString(sum), retType);
			}
			case Dbl: {
				double sum = 0.0;
				for (Cell c : cellsNotNull)
					sum += Double.parseDouble(c.value);
				return new Cell(Double.toString(sum), retType);
			}
			default:
				return new Cell("null", Type.Null);
			}
		}
		case Count: {
			int count = cellsNotNull.size();
			return new Cell(Integer.toString(count), retType);
		}
		case CountD: {
			Set<Cell> set = new HashSet<>();
			for (Cell c : cellsNotNull)
				set.add(c);
			int count = set.size();
			return new Cell(Integer.toString(count), retType);
		}
		case Avg: {
			switch (operandType) {
			case Int: {
				int sum = 0;
				for (Cell c : cellsNotNull)
					sum += Integer.parseInt(c.value);
				double avg = ((double) sum) / ((double) cellsNotNull.size());
				return new Cell(Double.toString(avg), retType);
			}
			case Dbl: {
				double sum = 0.0;
				for (Cell c : cellsNotNull)
					sum += Double.parseDouble(c.value);
				double avg = sum / (cellsNotNull.size());
				return new Cell(Double.toString(avg), retType);
			}
			default:
				return new Cell("null", Type.Null);
			}
		}
		case ConcatComma: {
			List<String> strs = cellsNotNull.stream().map(c -> c.value).collect(Collectors.toList());
			String str = String.join(", ", strs);
			return new Cell(str, Type.Str);
		}
		case ConcatSpace: {
			List<String> strs = cellsNotNull.stream().map(c -> c.value).collect(Collectors.toList());
			String str = String.join(" ", strs);
			return new Cell(str, Type.Str);
		}
		case ConcatSlash: {
			List<String> strs = cellsNotNull.stream().map(c -> c.value).collect(Collectors.toList());
			String str = String.join("/", strs);
			return new Cell(str, Type.Str);
		}
		}
		throw new IllegalStateException("unknown aggregator type.");
	}

}
