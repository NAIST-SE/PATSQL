package patsql.synth.filler.strategy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.ColRelation;

class ProjectionBagTest {

	private Table tableIn1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Dbl)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("8.0", Type.Dbl)//
		);
		return table;
	}

	/**
	 * same columns w.r.t values, rows' order.
	 */
	@Test
	void test01() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection sketch = new Projection(base);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new ProjectionMatching(ColRelation.BAG);
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());
		assertEquals(1, result.size());

		RAOperator op = result.get(0);
		assertTrue(op instanceof Projection);
		Projection p = (Projection) op;

		ColSchema[] sc = inTable.schema();
		ColSchema[] expected = new ColSchema[] { sc[0], sc[1] };
		assertArrayEquals(expected, p.projCols);
	}

	/**
	 * use columns "A" and "B" in "TableIn1".
	 */
	private Table tableOut1_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int) //
		);
		return table;
	}

	/**
	 * different order of rows but be matched.
	 */
	@Test
	void test02() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection sketch = new Projection(base);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new ProjectionMatching(ColRelation.BAG);
		Table inTable = tableIn1();
		Table outTable = tableOut1_2();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());
		assertEquals(1, result.size());

		RAOperator op = result.get(0);
		assertTrue(op instanceof Projection);
		Projection p = (Projection) op;

		ColSchema[] sc = inTable.schema();
		ColSchema[] expected = new ColSchema[] { sc[0], sc[1] };
		assertArrayEquals(expected, p.projCols);
	}

	/**
	 * use columns "A" and "B"(different order) in "createTable1".
	 */
	private Table tableOut1_2() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("13", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("12", Type.Int) //
		);
		return table;
	}

	/**
	 * All columns have the same value set {12, 13}.
	 */
	private Table tableIn3() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(//
				new Cell("13", Type.Int), //
				new Cell("13", Type.Int), //
				new Cell("12", Type.Int)//
		);
		table.addRow(//
				new Cell("13", Type.Int), //
				new Cell("12", Type.Int), //
				new Cell("13", Type.Int)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("13", Type.Int), //
				new Cell("13", Type.Int)//
		);
		return table;
	}

	@Test
	void test05() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection sketch = new Projection(base);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new ProjectionMatching(ColRelation.BAG);
		Table inTable = tableIn3();
		Table outTable = tableOut3_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());
		assertEquals(0, result.size());
	}

	/**
	 * Derived from "TableIn2". <br>
	 * multiple combination for projection.
	 */
	private Table tableOut3_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Int), //
				new ColSchema("c3", Type.Int) //
		);
		table.addRow(//
				new Cell("100", Type.Int), //
				new Cell("100", Type.Int) //
		);
		table.addRow(//
				new Cell("100", Type.Int), //
				new Cell("100", Type.Int) //
		);
		table.addRow(//
				new Cell("100", Type.Int), //
				new Cell("100", Type.Int) //
		);
		return table;
	}

}
