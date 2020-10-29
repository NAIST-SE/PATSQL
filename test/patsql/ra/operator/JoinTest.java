package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

class JoinTest {

	/**
	 * simple
	 */
	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("B", Type.Str));

		Table table2 = new Table(c2, c3);
		table2.addRow(new Cell("A", Type.Str), new Cell("99", Type.Dbl));
		table2.addRow(new Cell("B", Type.Str), new Cell("100", Type.Dbl));
		table2.addRow(new Cell("C", Type.Str), new Cell("101", Type.Dbl));
		table2.addRow(new Cell("D", Type.Str), new Cell("102", Type.Dbl));

		Join program = new Join(//
				new BaseTable("table1"), //
				new BaseTable("table2"), //
				new JoinCond(new JoinKeyPair(c1, c2)) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * key duplication
	 */
	@Test
	void testEval02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("A", Type.Str));

		Table table2 = new Table(c2, c3);
		table2.addRow(new Cell("A", Type.Str), new Cell("99", Type.Dbl));
		table2.addRow(new Cell("A", Type.Str), new Cell("100", Type.Dbl));
		table2.addRow(new Cell("A", Type.Str), new Cell("101", Type.Dbl));

		Join program = new Join(//
				new BaseTable("table1"), //
				new BaseTable("table2"), //
				new JoinCond(new JoinKeyPair(c1, c2)) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(6, result.height());
	}

	/**
	 * composite-key
	 */
	@Test
	void testEval03() {
		ColSchema c11 = new ColSchema("c11", Type.Str);
		ColSchema c12 = new ColSchema("c12", Type.Str);
		ColSchema c21 = new ColSchema("c21", Type.Str);
		ColSchema c22 = new ColSchema("c22", Type.Str);

		Table table1 = new Table(c11, c12);
		table1.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table1.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));

		Table table2 = new Table(c21, c22);
		table2.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table2.addRow(new Cell("A", Type.Str), new Cell("Y", Type.Str));
		table2.addRow(new Cell("B", Type.Str), new Cell("X", Type.Str));
		table2.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));

		Join program = new Join(//
				new BaseTable("table1"), //
				new BaseTable("table2"), //
				new JoinCond(//
						new JoinKeyPair(c11, c21), //
						new JoinKeyPair(c12, c22)//
				) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		Table result = program.eval(env);

		assertEquals(4, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * three tables.
	 */
	@Test
	void testEval04() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);
		ColSchema c4 = new ColSchema("c4", Type.Str);
		ColSchema c5 = new ColSchema("c5", Type.Str);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("B", Type.Str));
		table1.addRow(new Cell("C", Type.Str));
		table1.addRow(new Cell("E", Type.Str));

		Table table2 = new Table(c2, c3);
		table2.addRow(new Cell("A", Type.Str), new Cell("99", Type.Dbl));
		table2.addRow(new Cell("B", Type.Str), new Cell("100", Type.Dbl));
		table2.addRow(new Cell("C", Type.Str), new Cell("101", Type.Dbl));
		table2.addRow(new Cell("D", Type.Str), new Cell("102", Type.Dbl));

		Table table3 = new Table(c4, c5);
		table3.addRow(new Cell("A", Type.Str), new Cell("Foo", Type.Str));
		table3.addRow(new Cell("B", Type.Str), new Cell("Bar", Type.Str));
		table3.addRow(new Cell("C", Type.Str), new Cell("Baz", Type.Str));

		Join program = new Join(//
				new Join(//
						new BaseTable("table1"), //
						new BaseTable("table2"), //
						new JoinCond(new JoinKeyPair(c1, c2)) //
				), //
				new BaseTable("table3"), //
				new JoinCond(new JoinKeyPair(c1, c4)) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);
		env.put("table3", table3);

		Table result = program.eval(env);

		assertEquals(5, result.width());
		assertEquals(3, result.height());
	}
}
