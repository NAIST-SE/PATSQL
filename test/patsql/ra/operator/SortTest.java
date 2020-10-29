package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;

class SortTest {

	/**
	 * single key, ASC
	 */
	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("1", Type.Int)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("2", Type.Int)//
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("3", Type.Int)//
		);

		RAOperator program = new Sort(//
				new BaseTable("table1"), //
				new SortKeys(new SortKey(c1, Order.Asc))//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(3, result.height());
	}

	/**
	 * two keys, DESC, ASC
	 */
	@Test
	void testEval03() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);

		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("4", Type.Int)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("2", Type.Int)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("3", Type.Int)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("1", Type.Int)//
		);

		RAOperator program = new Sort(//
				new BaseTable("table1"), //
				new SortKeys(//
						new SortKey(c1, Order.Desc), //
						new SortKey(c3, Order.Asc)//
				)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(4, result.height());
	}

	/**
	 * nil key
	 */
	@Test
	void testEval04() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);

		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("4", Type.Int)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("2", Type.Int)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("3", Type.Int)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("1", Type.Int)//
		);

		RAOperator program = new Sort(//
				new BaseTable("table1"), //
				SortKeys.nil()//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);
		assertEquals(3, result.width());
		assertEquals(4, result.height());
	}

}
