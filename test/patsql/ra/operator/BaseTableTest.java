package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

class BaseTableTest {

	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table table = new Table(c1, c2);
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));

		BaseTable program = new BaseTable("table1");

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);
		assertEquals(2, result.width());
		assertEquals(2, result.height());
	}

	@Test
	void testEval02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table table = new Table(c1, c2);
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));

		BaseTable program = new BaseTable("table1"); // set new IDs.
		program.renamedCols = new ColSchema[] { //
				new ColSchema(c1.name, c1.type, -1), //
				new ColSchema(c2.name, c2.type, -2),//
		};

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);
		assertEquals(2, result.width());
		assertEquals(2, result.height());
		assertEquals(-1, result.schema()[0].id);
		assertEquals(-2, result.schema()[1].id);
	}

	/**
	 * self join with renamed IDs.
	 */
	@Test
	void testEval03() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table table = new Table(c1, c2);
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));

		TableEnv env = new TableEnv();
		env.put("table1", table);

		BaseTable b1 = new BaseTable("table1");
		BaseTable b2 = new BaseTable("table1");
		b2.renamedCols = new ColSchema[] { //
				new ColSchema(c1.name, c1.type, -1), //
				new ColSchema(c2.name, c2.type, -2),//
		};

		Table t1 = b1.eval(env);
		Table t2 = b2.eval(env); // retrieve a renamed ID from an evaluated table.

		Join program = new Join(b1, b2, //
				new JoinCond(//
						new JoinKeyPair(t1.schema()[0], t2.schema()[0])//
				)//
		);

		Table result = program.eval(env);
		assertEquals(4, result.width());
		assertEquals(2, result.height());
	}

}
