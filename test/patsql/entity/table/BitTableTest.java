package patsql.entity.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;

class BitTableTest {

	@Test
	void testSelection01() {
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

		BitTable btable = new BitTable(table);
		BitTable result = btable.selection(//
				new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str))//
		);

		assertEquals(3, result.width());
		assertEquals(1, result.height());
	}

	@Test
	void testSelection02() {
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

		BitTable btable = new BitTable(table);
		BitTable result = btable.selection(//
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.NotEq, new Cell("A", Type.Str)), //
						new BinaryPred(c3, BinaryOp.Eq, new Cell("003", Type.Str))//
				)//
		);

		assertEquals(3, result.width());
		assertEquals(1, result.height());
	}

	@Test
	void testSelection03() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("null", Type.Null)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("002", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("003", Type.Str)//
		);

		BitTable btable = new BitTable(table);
		BitTable result = btable.selection(//
				new Conjunction(//
						new UnaryPred(c3, UnaryOp.IsNotNull), //
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str)) //
				)//
		);

		assertEquals(3, result.width());
		assertEquals(2, result.height());
	}

	@Test
	void testJoin01() {
		Table table1 = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str)//
		);
		table1.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str), new Cell("001", Type.Str));
		table1.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str), new Cell("002", Type.Str));
		table1.addRow(new Cell("C", Type.Str), new Cell("Z", Type.Str), new Cell("003", Type.Str));

		Table table2 = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str) //
		);
		table2.addRow(new Cell("01", Type.Str), new Cell("T", Type.Str));
		table2.addRow(new Cell("02", Type.Str), new Cell("T", Type.Str));

		BitTable btable1 = new BitTable(table1);
		BitTable btable2 = new BitTable(table2);
		BitTable result = btable1.join(btable2);

		assertEquals(5, result.width());
		assertEquals(6, result.height());
	}

	@Test
	void testJoin02() {
		Table table1 = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str)//
		);
		table1.addRow(new Cell("A", Type.Str), new Cell("X", Type.Str), new Cell("001", Type.Str));
		table1.addRow(new Cell("B", Type.Str), new Cell("Y", Type.Str), new Cell("002", Type.Str));
		table1.addRow(new Cell("C", Type.Str), new Cell("Z", Type.Str), new Cell("003", Type.Str));

		Table table2 = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str) //
		);
		table2.addRow(new Cell("01", Type.Str), new Cell("T", Type.Str));
		table2.addRow(new Cell("02", Type.Str), new Cell("T", Type.Str));
		table2.addRow(new Cell("03", Type.Str), new Cell("T", Type.Str));

		BitTable btable1 = new BitTable(table1);
		BitTable btable2 = new BitTable(table2);
		BitTable result = btable1.join(btable2);

		assertEquals(5, result.width());
		assertEquals(9, result.height());
	}

	@Test
	void testJoin03() {
		Table table1 = new Table(//
				new ColSchema("c1", Type.Str) //
		);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("B", Type.Str));

		Table table2 = new Table(//
				new ColSchema("c2", Type.Str) //
		);
		table2.addRow(new Cell("01", Type.Str));
		table2.addRow(new Cell("02", Type.Str));
		table2.addRow(new Cell("03", Type.Str));
		table2.addRow(new Cell("04", Type.Str));

		Table table3 = new Table(//
				new ColSchema("c3", Type.Str) //
		);
		table3.addRow(new Cell("X", Type.Str));
		table3.addRow(new Cell("Y", Type.Str));
		table3.addRow(new Cell("Z", Type.Str));

		BitTable btable1 = new BitTable(table1);
		BitTable btable2 = new BitTable(table2);
		BitTable btable3 = new BitTable(table3);
		BitTable result = btable1//
				.join(btable2)//
				.join(btable3);

		assertEquals(3, result.width());
		assertEquals(24, result.height());
	}

	@Test
	void testComposite01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Int);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("B", Type.Str));

		Table table2 = new Table(c2);
		table2.addRow(new Cell("8", Type.Int));
		table2.addRow(new Cell("10", Type.Int));
		table2.addRow(new Cell("12", Type.Int));
		table2.addRow(new Cell("13", Type.Int));

		Table table3 = new Table(c3);
		table3.addRow(new Cell("99", Type.Dbl));
		table3.addRow(new Cell("101", Type.Dbl));
		table3.addRow(new Cell("102", Type.Dbl));

		BitTable btable1 = new BitTable(table1);
		BitTable btable2 = new BitTable(table2);
		BitTable btable3 = new BitTable(table3);
		Table result = btable1//
				.join(btable2.selection(//
						new BinaryPred(c2, BinaryOp.Geq, new Cell("10", Type.Int))//
				))//
				.join(btable3.selection(//
						new BinaryPred(c3, BinaryOp.Lt, new Cell("102", Type.Dbl))//
				))//
				.toTable().project(new int[] { //
						c3.id, //
						c2.id, //
						c1.id, //
				});

		assertEquals(3, result.width());
		assertEquals(12, result.height());
	}

}
