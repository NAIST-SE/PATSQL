package patsql.synth.filler.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class GroupbyPruneTest {

	private Table tableIn1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("YYY", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("17", Type.Int) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("YYY", Type.Str), //
				new Cell("5", Type.Int) //
		);
		return table;
	}

	/**
	 * The first column is the only candidate.
	 */
	@Test
	void test01() {
		BaseTable base = new BaseTable("table1");
		GroupBy sketch = new GroupBy(base, null, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);

		FillingStrategy strategy = new GroupbyPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());
		assertEquals(1, result.size());
		GroupBy g = (GroupBy) result.get(0);
		ColSchema[] sc = g.keys.colSchemas;
		assertEquals(1, sc.length);
		assertEquals("A", sc[0].name);// check the key name.
	}

	private Table tableOut1_1() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Int);
		ColSchema c4 = new AggColSchema(Agg.Max, c3);
		ColSchema c5 = new AggColSchema(Agg.Min, c3);
		Table table = new Table(c1, c4, c5);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("17", Type.Int), //
				new Cell("5", Type.Int) //
		);
		return table;
	}

	/**
	 * The second column is the only candidate.
	 */
	@Test
	void test02() {
		BaseTable base = new BaseTable("table1");
		GroupBy sketch = new GroupBy(base, null, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);

		FillingStrategy strategy = new GroupbyPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1_2();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
		GroupBy g = (GroupBy) result.get(0);
		ColSchema[] sc = g.keys.colSchemas;
		assertEquals(1, sc.length);
		assertEquals("B", sc[0].name); // check the key name.
	}

	private Table tableOut1_2() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Int);
		ColSchema c4 = new AggColSchema(Agg.Max, c3);
		ColSchema c5 = new AggColSchema(Agg.Min, c3);
		Table table = new Table(c1, c4, c5);
		table.addRow(//
				new Cell("XXX", Type.Str), //
				new Cell("17", Type.Int), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("YYY", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("5", Type.Int) //
		);
		return table;
	}

	private Table tableIn3() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("10", Type.Int) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("YYY", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("17", Type.Int) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("YYY", Type.Str), //
				new Cell("5", Type.Int) //
		);
		return table;
	}

	@Test
	void test03() {
		BaseTable base = new BaseTable("table1");
		GroupBy sketch = new GroupBy(base, null, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.UNKNOWN, ColRelation.UNKNOWN)//
		);

		FillingStrategy strategy = new GroupbyPrune(constraint);
		Table inTable = tableIn3();
		Table outTable = null;

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(4, result.size());
	}

	private Table tableIn4() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Date), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("20200401", Type.Date), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("20200401", Type.Date), //
				new Cell("10", Type.Int) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("20200403", Type.Date), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("20200404", Type.Date), //
				new Cell("17", Type.Int) //
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("20200406", Type.Date), //
				new Cell("5", Type.Int) //
		);
		return table;
	}

	@Test
	void test04() {
		BaseTable base = new BaseTable("table1");
		GroupBy sketch = new GroupBy(base, null, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.UNKNOWN, ColRelation.UNKNOWN)//
		);

		FillingStrategy strategy = new GroupbyPrune(constraint);
		Table inTable = tableIn4();
		Table outTable = null;

		System.out.println(inTable);
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(4, result.size());
	}

}
