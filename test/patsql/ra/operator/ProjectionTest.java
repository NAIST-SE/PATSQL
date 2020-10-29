package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

class ProjectionTest {

	@Test
	void testEva01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("001", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("002", Type.Str)//
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("003", Type.Str)//
		);

		Projection program = new Projection(//
				new BaseTable("table1"), //
				new ColSchema[] { c3, c1 }//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(2, result.width());
		assertEquals(3, result.height());
	}

	@Test
	void testEva02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("001", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("002", Type.Str)//
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("003", Type.Str)//
		);

		Projection program = new Projection(//
				new BaseTable("table1"), //
				new ColSchema[] { c1, c1, c1, c1, c1 }//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(5, result.width());
		assertEquals(3, result.height());
	}

}
