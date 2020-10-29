package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

class DistinctTest {

	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table table = new Table(c1, c2);
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("X", Type.Str));

		RAOperator program = new Distinct(//
				new BaseTable("table1")//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(2, result.width());
		assertEquals(2, result.height());
	}

}
