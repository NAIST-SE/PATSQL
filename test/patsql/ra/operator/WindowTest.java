package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.WinColSchema;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.window.WinFunc;

class WindowTest {

	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);
		table.addRow(new Cell("1", Type.Str), new Cell("X", Type.Str), new Cell("1", Type.Int));
		table.addRow(new Cell("2", Type.Str), new Cell("X", Type.Str), new Cell("2", Type.Int));
		table.addRow(new Cell("3", Type.Str), new Cell("X", Type.Str), new Cell("3", Type.Int));
		table.addRow(new Cell("4", Type.Str), new Cell("Y", Type.Str), new Cell("1", Type.Int));

		RAOperator program = new Window(//
				new BaseTable("table1") //
				, new WinColSchema(WinFunc.RANK, null, new GroupKeys(c2), new SortKey(c3, Order.Asc))//
				, new WinColSchema(WinFunc.SUM, c3, GroupKeys.nil(), new SortKey(c1, Order.Asc))//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		System.out.println(table);
		Table result = program.eval(env);
		System.out.println(result);
		assertEquals(5, result.width());
		assertEquals(4, result.height());
	}

}
