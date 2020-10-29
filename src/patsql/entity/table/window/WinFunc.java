package patsql.entity.table.window;

import java.util.Arrays;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;

public enum WinFunc {
	ROWNUM, //
	RANK, //
	//
	MAX, //
	MIN, //
	SUM, //
	COUNT, //
	;

	public boolean hasColParam() {
		switch (this) {
		case RANK:
		case ROWNUM:
			return false;
		default:
			return true;
		}
	}

	public boolean isAgg() {
		switch (this) {
		case MAX:
		case MIN:
		case SUM:
		case COUNT:
			return true;
		default:
			return false;
		}
	}

	public boolean isComputable(Type operand) {
		switch (this) {
		case MAX:
		case MIN:
		case COUNT:
			return true;
		case SUM:
			switch (operand) {
			case Int:
			case Dbl:
				return true;
			default:
				return false;
			}
		case ROWNUM:
		case RANK:
			// error
		}
		throw new IllegalStateException("Do not use this method for the type :" + this);
	}

	public Type returnType(ColSchema src) {
		switch (this) {
		case MAX:
		case MIN:
		case SUM:
			return (src != null) ? src.type : Type.Null;
		case COUNT:
		case RANK:
		case ROWNUM:
			return Type.Int;
		}
		throw new IllegalStateException("Unknown type : " + this);
	}

	public Cell eval(int index, Cell[] targetCells, Cell[] orderCells) {
		if (hasColParam()) {
			if (targetCells.length == 0) {
				return new Cell("null", Type.Null);
			}

			int end = index;
			while (end < orderCells.length - 1 && orderCells[end].equals(orderCells[end + 1])) {
				end++;
			}
			Cell[] tcells = Arrays.copyOfRange(targetCells, 0, end + 1);

			switch (this) {
			case MAX:
				return Agg.Max.eval(tcells);
			case MIN:
				return Agg.Min.eval(tcells);
			case SUM:
				return Agg.Sum.eval(tcells);
			case COUNT:
				return Agg.Count.eval(tcells);
			default:
				throw new IllegalStateException("unreachable");
			}
		} else {
			switch (this) {
			case RANK: {
				int i = index;
				while (i > 0 && orderCells[i].equals(orderCells[i - 1])) {
					i--;
				}
				return new Cell(Integer.toString(i + 1), returnType(null));
			}
			case ROWNUM: {
				if (!uniqueCells(orderCells))
					return new Cell("null", Type.Null);
				else
					return new Cell(Integer.toString(index + 1), returnType(null));
			}
			default:
				throw new IllegalStateException("unreachable");
			}
		}
	}

	private boolean uniqueCells(Cell[] orderCells) {
		for (int i = 1; i < orderCells.length; i++) {
			if (orderCells[i].equals(orderCells[i - 1])) {
				return false;
			}
		}
		return true;
	}

}
