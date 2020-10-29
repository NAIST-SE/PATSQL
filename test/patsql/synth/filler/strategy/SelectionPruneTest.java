package patsql.synth.filler.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class SelectionPruneTest {

	private Table tableIn1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("YYY", Type.Str)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	private Table tableIn2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("null", Type.Null), //
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("null", Type.Null)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	@Test
	void test01() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection sketch = new Selection(base, null/* to be filled */);

		// fill a sketch using the given example and option.
		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXACT, ColRelation.BAG)//
		);

		FillingStrategy strategy = new SelectionPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SynthOption option = new SynthOption(//
				new Cell("XXX", Type.Str)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, option);
		assertEquals(1, result.size());// C = XXX
	}

	private Table tableOut1_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	@Test
	void test02() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection sketch = new Selection(base, null/* to be filled */);

		// fill a sketch using the given example and option.
		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXACT, ColRelation.BAG)//
		);
		FillingStrategy strategy = new SelectionPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1_2();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SynthOption option = new SynthOption(//
				new Cell("YYY", Type.Str)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, option);
		assertEquals(1, result.size());// C <> YYY
	}

	private Table tableOut1_2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	@Test
	void test03() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection sketch = new Selection(base, null/* to be filled */);

		// fill a sketch using the given example and option.
		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXACT, ColRelation.BAG)//
		);
		FillingStrategy strategy = new SelectionPrune(constraint);
		Table inTable = tableIn2();
		Table outTable = tableOut2_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SynthOption option = new SynthOption( /* empty */ );
		List<RAOperator> result = strategy.fill(sketch, example, option);

		assertEquals(1, result.size());// C IS NOT NULL
	}

	private Table tableOut2_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("null", Type.Null), //
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	/**
	 * EXISTS, SET
	 */
	@Test
	void test04() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection sketch = new Selection(base, null/* to be filled */);

		// fill a sketch using the given example and option.
		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);
		FillingStrategy strategy = new SelectionPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1_3(); // order is changed.
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SynthOption option = new SynthOption(//
				new Cell("YYY", Type.Str)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, option);

		assertEquals(1, result.size());
	}

	private Table tableOut1_3() {
		Table table = new Table(//
				new ColSchema("B", Type.Int), //
				new ColSchema("C", Type.Str)//
		);
		table.addRow(//
				new Cell("13", Type.Int), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("XXX", Type.Str)//
		);

		return table;
	}

}
