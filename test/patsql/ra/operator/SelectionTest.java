package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;

class SelectionTest {

	@Test
	void testEval01() {
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

		Selection program = new Selection(//
				new BaseTable("table1"), //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str))//
				)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(1, result.height());
	}

	@Test
	void testEval02() {
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
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("002", Type.Str)//
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("003", Type.Str)//
		);

		Selection program = new Selection(//
				new BaseTable("table1"), //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str)), //
						new BinaryPred(c2, BinaryOp.Eq, new Cell("X", Type.Str))//
				));

		TableEnv env = new TableEnv();
		env.put("table1", table);

		Table result = program.eval(env);

		assertEquals(3, result.width());
		assertEquals(1, result.height());
	}

}
