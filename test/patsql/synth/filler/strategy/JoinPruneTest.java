package patsql.synth.filler.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Join;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class JoinPruneTest {

	private Table tableIn1_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(new Cell("001", Type.Str), new Cell("A", Type.Str), new Cell("1", Type.Int));
		table.addRow(new Cell("002", Type.Str), new Cell("B", Type.Str), new Cell("2", Type.Int));
		table.addRow(new Cell("003", Type.Str), new Cell("A", Type.Str), new Cell("3", Type.Int));
		table.addRow(new Cell("004", Type.Str), new Cell("C", Type.Str), new Cell("4", Type.Int));
		return table;
	}

	private Table tableIn1_2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(new Cell("A", Type.Str), new Cell("XXX", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("YYY", Type.Str));
		table.addRow(new Cell("C", Type.Str), new Cell("XXX", Type.Str));
		return table;
	}

	/**
	 * Simple. Single key needed.
	 */
	@Test
	void test01() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);

		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn1_1();
		Table inTable2 = tableIn1_2();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
	}

	private Table tableOut1_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int), //
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //

		);
		table.addRow(//
				new Cell("001", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("1", Type.Int), //
				new Cell("A", Type.Str), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("002", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("2", Type.Int), //
				new Cell("B", Type.Str), //
				new Cell("YYY", Type.Str));
		table.addRow(//
				new Cell("003", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("3", Type.Int), //
				new Cell("A", Type.Str), //
				new Cell("XXX", Type.Str)//
		);
		table.addRow(//
				new Cell("004", Type.Str), //
				new Cell("C", Type.Str), //
				new Cell("4", Type.Int), //
				new Cell("C", Type.Str), //
				new Cell("XXX", Type.Str)//
		);
		return table;
	}

	/**
	 * Column disorder. Filling successes because EXISTS criteria is applied.
	 */
	@Test
	void test02() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);
		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn1_1();
		Table inTable2 = tableIn1_2();
		Table outTable = tableOut1_2();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
	}

	private Table tableOut1_2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("001", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("1", Type.Int) //

		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("YYY", Type.Str), //
				new Cell("002", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("2", Type.Int) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("003", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("3", Type.Int) //
		);
		table.addRow(//
				new Cell("C", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("004", Type.Str), //
				new Cell("C", Type.Str), //
				new Cell("4", Type.Int) //
		);
		return table;
	}

	private Table tableIn3_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("32", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("18", Type.Str)//
		);
		return table;
	}

	private Table tableIn3_2() {
		Table table = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str), //
				new ColSchema("c6", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("PPP", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("QQQ", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("RRR", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("SSS", Type.Str)//
		);
		return table;
	}

	/**
	 * 2 keys Join.
	 */
	@Test
	void test03() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);
		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn3_1();
		Table inTable2 = tableIn3_2();
		Table outTable = tableOut3();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
	}

	private Table tableOut3() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str) //
		);
		table.addRow(//
				new Cell("32", Type.Str), //
				new Cell("PPP", Type.Str)//
		);
		table.addRow(//
				new Cell("18", Type.Str), //
				new Cell("SSS", Type.Str)//
		);
		return table;
	}

	private Table tableIn4_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("32", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("18", Type.Str)//
		);
		return table;
	}

	private Table tableIn4_2() {
		Table table = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str), //
				new ColSchema("c6", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("PPP", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("QQQ", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("RRR", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("SSS", Type.Str)//
		);
		return table;
	}

	/**
	 * 2 key join is redundant.
	 */
	@Test
	void test04() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SUPER_SET)//
		);
		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn4_1();
		Table inTable2 = tableIn4_2();
		Table outTable = tableOut4();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(2, result.size());
	}

	private Table tableOut4() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str) //
		);
		table.addRow(//
				new Cell("32", Type.Str), //
				new Cell("PPP", Type.Str)//
		);
		table.addRow(//
				new Cell("18", Type.Str), //
				new Cell("SSS", Type.Str)//
		);
		return table;
	}

	private Table tableIn5_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("Y", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("2", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("2", Type.Str), //
				new Cell("Y", Type.Str)//
		);
		return table;
	}

	private Table tableIn5_2() {
		Table table = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("2", Type.Str)//
		);
		return table;
	}

	/**
	 * No results of filling because there are no unique keys.
	 */
	@Disabled // because the unique key constraint is disabled now.
	@Test
	void test05() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SUPER_SET)//
		);
		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn5_1();
		Table inTable2 = tableIn5_2();
		Table outTable = tableOut5();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(0, result.size());
	}

	private Table tableOut5() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str)//
		);
		return table;
	}

	private Table tableIn6_1() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str), //
				new ColSchema("c3", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("Y", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("2", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("X", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("2", Type.Str), //
				new Cell("Y", Type.Str)//
		);
		return table;
	}

	private Table tableIn6_2() {
		Table table = new Table(//
				new ColSchema("c4", Type.Str), //
				new ColSchema("c5", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str)//
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("2", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("1", Type.Str)//
		);
		table.addRow(//
				new Cell("B", Type.Str), //
				new Cell("2", Type.Str)//
		);
		return table;
	}

	/**
	 * only 2 key join is filled because it is a unique key.
	 */
	@Disabled // because the unique key constraint is disabled now.
	@Test
	void test06() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join sketch = new Join(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SUPER_SET)//
		);
		FillingStrategy strategy = new JoinPrune(constraint);
		Table inTable1 = tableIn6_1();
		Table inTable2 = tableIn6_2();
		Table outTable = tableOut6();
		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
	}

	private Table tableOut6() {
		Table table = new Table(//
				new ColSchema("c1", Type.Str), //
				new ColSchema("c2", Type.Str) //
		);
		table.addRow(//
				new Cell("A", Type.Str), //
				new Cell("1", Type.Str)//
		);
		return table;
	}

}
