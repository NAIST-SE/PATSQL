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
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Sort;

class SortExactTest {

	private Table tableIn1() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("14", Type.Int), //
				new Cell("104", Type.Int), //
				new Cell("BBB", Type.Str)//
		);
		table.addRow(//
				new Cell("10", Type.Int), //
				new Cell("110", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		return table;
	}

	private Table tableIn2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("6", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("9", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("11", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("2", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("8", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("12", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("7", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("10", Type.Int), new Cell("1", Type.Int));
		return table;
	}

	/**
	 * kye: first columns
	 */
	@Test
	void test01() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table tableOut1_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("10", Type.Int), //
				new Cell("110", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("14", Type.Int), //
				new Cell("104", Type.Int), //
				new Cell("BBB", Type.Str)//
		);
		return table;
	}

	/**
	 * key: second column
	 */
	@Test
	void test02() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = tableIn1();
		Table outTable = tableOut1_2();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table tableOut1_2() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("14", Type.Int), //
				new Cell("104", Type.Int), //
				new Cell("BBB", Type.Str)//
		);
		table.addRow(//
				new Cell("10", Type.Int), //
				new Cell("110", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		return table;
	}

	/**
	 * key: first column, order: desc
	 */
	@Test
	void test03() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = tableIn1();
		Table outTable = tableOut1_3();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table tableOut1_3() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("14", Type.Int), //
				new Cell("104", Type.Int), //
				new Cell("BBB", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("10", Type.Int), //
				new Cell("110", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		return table;
	}

	/**
	 * key: first column, order: desc
	 */
	@Test
	void test04() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = tableIn1();
		Table outTable = tableOut1_4();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table tableOut1_4() {
		Table table = new Table(//
				new ColSchema("A", Type.Int), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("10", Type.Int), //
				new Cell("110", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		table.addRow(//
				new Cell("14", Type.Int), //
				new Cell("104", Type.Int), //
				new Cell("BBB", Type.Str)//
		);
		return table;
	}

	/**
	 * key: first column, order: desc
	 */
	@Test
	void test05() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = tableIn2();
		Table outTable = tableOut2_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table tableOut2_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("8", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("4", Type.Int), new Cell("12", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("7", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("9", Type.Int));
		table.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int), new Cell("11", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("2", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("3", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("6", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("1", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("9", Type.Int), new Cell("4", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("10", Type.Int), new Cell("1", Type.Int));
		return table;
	}

	/*
	 * key: first column, order: desc
	 */
	@Test
	void test06() {
		BaseTable base = new BaseTable("table1");
		Sort sketch = new Sort(base, null);

		// fill a sketch using the given example and option.
		FillingStrategy strategy = new SortExact();
		Table inTable = table6();
		Table outTable = table6();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(result.size(), 1);
	}

	private Table table6() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		Table table = new Table(//
				new AggColSchema(Agg.Max, c1), //
				c1, //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(new Cell("A", Type.Str), new Cell("A", Type.Str), new Cell("1", Type.Int),
				new Cell("4", Type.Int));
		table.addRow(new Cell("B", Type.Str), new Cell("B", Type.Str), new Cell("2", Type.Int),
				new Cell("8", Type.Int));
		table.addRow(new Cell("C", Type.Str), new Cell("C", Type.Str), new Cell("3", Type.Int),
				new Cell("12", Type.Int));
		return table;
	}
}
