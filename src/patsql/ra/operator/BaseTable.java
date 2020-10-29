package patsql.ra.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.DateFunc;
import patsql.entity.table.DateFuncColSchema;
import patsql.entity.table.DateValue;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

public class BaseTable extends RAOperator {
	public String tableId;
	public ColSchema[] renamedCols = new ColSchema[] {};

	public BaseTable(String tableId) {
		super();
		this.tableId = tableId;
	}

	public static BaseTable empty() {
		return new BaseTable(null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table table = env.get(tableId);

		// extract date columns
		List<Column> colsAdded = new ArrayList<>();
		for (Column col : table.columns) {
			if (col.schema.type != Type.Date)
				continue;

			Column colY = new Column(new DateFuncColSchema(DateFunc.YEAR, col.schema));
			Column colM = new Column(new DateFuncColSchema(DateFunc.MONTH, col.schema));
			Column colD = new Column(new DateFuncColSchema(DateFunc.DAY, col.schema));

			for (Cell cell : col.cells()) {
				if (cell.type == Type.Null) {
					colY.addCell(new Cell("", Type.Null));
					colM.addCell(new Cell("", Type.Null));
					colD.addCell(new Cell("", Type.Null));
				} else {
					DateValue date = DateValue.parse(cell.value);
					colY.addCell(new Cell(date.year(), Type.Int));
					colM.addCell(new Cell(date.month(), Type.Int));
					colD.addCell(new Cell(date.day(), Type.Int));
				}
			}

			colsAdded.add(colY);
			colsAdded.add(colM);
			colsAdded.add(colD);
		}
		table = table.addColumns(colsAdded.toArray(new Column[0]));

		if (renamedCols.length > 0) {
			if (renamedCols.length != table.width()) {
				throw new IllegalStateException(renamedCols.length + " <-> " + table.width());
			} else {
				int[] ids = Arrays.stream(renamedCols).mapToInt(e -> e.id).toArray();
				table = table.cloneWithNewColIDs(ids);
			}
		}
		return table;
	}

	@Override
	public BaseTable clone() {
		BaseTable copy = new BaseTable(tableId);
		copy.id = this.id;
		copy.renamedCols = this.renamedCols;
		return copy;
	}

}
