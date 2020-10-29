package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

class LeftJoinTest {

	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("D", Type.Str));
		table1.addRow(new Cell("E", Type.Str));
		table1.addRow(new Cell("B", Type.Str));

		Table table2 = new Table(c2);
		table2.addRow(new Cell("C", Type.Str));
		table2.addRow(new Cell("B", Type.Str));
		table2.addRow(new Cell("A", Type.Str));

		LeftJoin program = new LeftJoin(//
				new BaseTable("table1"), //
				new BaseTable("table2"), //
				new JoinCond(new JoinKeyPair(c1, c2)) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		Table result = program.eval(env);

		assertEquals(2, result.width());
		assertEquals(4, result.height());
	}

	@Test
	void testEval0() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		ColSchema c4 = new ColSchema("D", Type.Str);
		ColSchema c5 = new ColSchema("E", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table1.addRow(new Cell("A", Type.Str), new Cell("Y", Type.Str));
		table1.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str));
		table1.addRow(new Cell("D", Type.Str), new Cell("X", Type.Str));

		Table table2 = new Table(c3, c4, c5);
		table2.addRow(new Cell("A", Type.Str), new Cell("Z", Type.Str), new Cell("01", Type.Str));
		table2.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str), new Cell("03", Type.Str));
		table2.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str), new Cell("06", Type.Str));
		table2.addRow(new Cell("D", Type.Str), new Cell("Y", Type.Str), new Cell("08", Type.Str));

		LeftJoin program = new LeftJoin(//
				new BaseTable("table1"), //
				new BaseTable("table2"), //
				new JoinCond(//
						new JoinKeyPair(c2, c4), //
						new JoinKeyPair(c1, c3) //
				) //
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		Table result = program.eval(env);

		assertEquals(5, result.width());
		assertEquals(4, result.height());
	}

}
