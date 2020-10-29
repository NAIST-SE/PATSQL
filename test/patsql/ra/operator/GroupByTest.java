package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.AggColSchema;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;

class GroupByTest {

	/**
	 * single key
	 */
	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
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

		RAOperator program = new GroupBy(//
				new BaseTable("table1"), //
				new GroupKeys(c1), //
				Aggregators.all(c1, c2, c3)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		System.out.println(table);
		System.out.println(result);
		assertEquals(21, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * 2 keys
	 */
	@Test
	void testEval02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
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

		RAOperator program = new GroupBy(//
				new BaseTable("table1"), //
				new GroupKeys(c1, c2), //
				new Aggregators(//
						new AggColSchema(Agg.Max, c3), //
						new AggColSchema(Agg.Min, c3), //
						new AggColSchema(Agg.Sum, c3), //
						new AggColSchema(Agg.Avg, c3)//
				)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(6, result.width());
		assertEquals(3, result.height());
	}

	/**
	 * Nulls in result
	 */
	@Test
	void testEval03() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str); // String
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("1", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("2", Type.Str)//
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("3", Type.Str)//
		);

		RAOperator program = new GroupBy(//
				new BaseTable("table1"), //
				new GroupKeys(c1, c2), //
				new Aggregators(//
						new AggColSchema(Agg.Avg, c3), //
						new AggColSchema(Agg.Sum, c3) //
				)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(4, result.width());
		assertEquals(3, result.height());
	}

}
