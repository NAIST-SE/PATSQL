package patsql.synth.filler;

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
import patsql.ra.operator.Distinct;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Root;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;
import patsql.ra.util.RAUtils;

class SketchFillerTest {

	@Test
	void testLevelMap01() {
		// T π σ B
		RAOperator sketch = new Root(new Projection(new Selection(new BaseTable(null), null)));

		@SuppressWarnings("unused")
		SketchFiller filler = new SketchFiller(sketch, null, null);
		// RAUtils.printTree(sketch);
		// filler.printLevelMap();
	}

	@Test
	void testLevelMap02() {
		// δ π σ ⋈θ ( B ) ( γ B )
		BaseTable b1 = BaseTable.empty();
		BaseTable b2 = BaseTable.empty();
		GroupBy p1 = GroupBy.empty();
		p1.child = b2;
		Join p2 = Join.empty();
		p2.childL = b1;
		p2.childR = p1;
		Selection p3 = Selection.empty();
		p3.child = p2;
		Projection p4 = Projection.empty();
		p4.child = p3;
		Distinct sketch = Distinct.empty();
		sketch.child = p4;

		@SuppressWarnings("unused")
		SketchFiller filler = new SketchFiller(sketch, null, null);
		// RAUtils.printTree(sketch);
		// filler.printLevelMap();
	}

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

	private Table tableIn2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Str) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("13", Type.Str) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Str) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Str) //
		);
		return table;
	}

	private Table tableIn3() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("2", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("13", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("9", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("7", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("12", Type.Int) //
		);
		return table;
	}

	/**
	 * π B
	 */
	@Test
	void testFillSketch01() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection sketch = new Projection(base);

		// fill a sketch using the given example and option.
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(1, result.size());
	}

	/**
	 * π π B
	 */
	@Test
	void testFillSketch02() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection p1 = new Projection(base);
		Projection sketch = new Projection(p1);

		// fill a sketch using the given example and option.
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));

		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(8, result.size());
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
	 * π σ B
	 */
	@Test
	void testFillSketch03() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection p1 = new Selection(base, null);
		Projection sketch = new Projection(p1);

		// fill a sketch using the given example and option.
		Table inTable = tableIn1();
		Table outTable = tableOut1_2();

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		System.out.println(example);
		SynthOption option = new SynthOption(//
				new Cell("12", Type.Int)//
		);
		SketchFiller filler = new SketchFiller(sketch, example, option);
		List<RAOperator> result = filler.fillSketch();

		// only "B = 12"
		// ("B <= 12" is omitted because it produces the same result as "B=12")
		assertEquals(1, result.size());
	}

	/**
	 * filter out the third row and use columns "A" and "B" in "TableIn1".
	 */
	private Table tableOut1_2() {
		Table table = new Table(//
				new ColSchema("c1", Type.Int), //
				new ColSchema("c2", Type.Str) //
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("foo", Type.Str) //
		);
		table.addRow(//
				new Cell("12", Type.Int), //
				new Cell("bar", Type.Str) //
		);
		return table;
	}

	/**
	 * π σ σ B <br>
	 * The TRUE condition is allowed.
	 */
	@Test
	void testFillSketch04() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection p1 = new Selection(base, null);
		Selection p2 = new Selection(p1, null);
		Projection sketch = new Projection(p2);

		// fill a sketch using the given example and option.
		Table inTable = tableIn2();
		Table outTable = tableOut2_1();

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		SynthOption option = new SynthOption(//
				new Cell("12", Type.Str), //
				new Cell("foo", Type.Str) //
		);
		SketchFiller filler = new SketchFiller(sketch, example, option);
		List<RAOperator> result = filler.fillSketch();

		assertEquals(8, result.size());
	}

	private Table tableOut2_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Str) //
		);
		return table;
	}

	/**
	 * π σ γ B
	 */
	@Test
	void testFillSketch05() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		GroupBy p1 = new GroupBy(base, null, null);
		Selection p2 = new Selection(p1, null);
		Projection sketch = new Projection(p2);

		// fill a sketch using the given example and option.
		Table inTable = tableIn3();
		Table outTable = tableOut3_1();

		System.out.println(inTable);
		System.out.println(outTable);

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		SynthOption option = new SynthOption(//
				new Cell("bar", Type.Str) //
		);
		SketchFiller filler = new SketchFiller(sketch, example, option);
		List<RAOperator> result = filler.fillSketch();

		assertEquals(1, result.size());
	}

	private Table tableOut3_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("MAX(A)", Type.Int), //
				new ColSchema("MIN(A)", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("13", Type.Int), //
				new Cell("2", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("7", Type.Int) //
		);
		return table;
	}

	/**
	 * π σ γ σ B
	 */
	@Test
	void testFillSketch06() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Selection p1 = new Selection(base, null);
		GroupBy p2 = new GroupBy(p1, null, null);
		Selection p3 = new Selection(p2, null);
		Projection sketch = new Projection(p3);

		// fill a sketch using the given example and option.
		Table inTable = tableIn3();
		Table outTable = tableOut3_2();

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		SynthOption option = new SynthOption(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		SketchFiller filler = new SketchFiller(sketch, example, option);
		System.out.println(example);
		RAUtils.printTree(sketch);
		filler.printConstraint();
		List<RAOperator> result = filler.fillSketch();

		assertTrue(result.size() > 0);
	}

	private Table tableOut3_2() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("MAX(A)", Type.Int), //
				new ColSchema("MIN(A)", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("2", Type.Int), //
				new Cell("2", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("7", Type.Int) //
		);
		return table;
	}

	/**
	 * s π B
	 */
	@Test
	void testFillSketch07() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Sort p1 = new Sort(base, null);
		Projection p2 = new Projection(p1);
		Sort sketch = new Sort(p2, null);

		// fill a sketch using the given example and option.
		Table inTable = tableIn3();
		Table outTable = tableOut3_3();

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(1, result.size());
	}

	private Table tableOut3_3() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("2", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("7", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("9", Type.Int) //
		);
		table.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("13", Type.Int) //
		);
		return table;
	}

	private Table tableIn4_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("001", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("002", Type.Str) //
		);
		table.addRow(//
				new Cell("XXX", Type.Str), //
				new Cell("003", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str), //
				new Cell("004", Type.Str) //
		);
		return table;
	}

	private Table tableIn4_2() {
		Table table = new Table(//
				new ColSchema("C", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str) //
		);
		return table;
	}

	/**
	 * s π ⋈θ ( B ) ( B ) <br>
	 * This is like an EXIST operation.
	 */
	@Test
	void testFillSketch08() {
		// create a sketch to be filled.
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		Join p1 = new Join(base1, base2, null);
		Projection p2 = new Projection(p1);
		Sort sketch = new Sort(p2, null);

		// fill a sketch using the given example and option.
		Table inTable1 = tableIn4_1();
		Table inTable2 = tableIn4_2();
		Table outTable = tableOut4_1();

		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(1, result.size());
	}

	private Table tableOut4_1() {
		Table table = new Table(//
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("004", Type.Str) //
		);
		table.addRow(//
				new Cell("002", Type.Str) //
		);
		table.addRow(//
				new Cell("001", Type.Str) //
		);
		return table;
	}

	/**
	 * B<br>
	 * Pruned by Prune strategy for Base.
	 */
	@Test
	void testFillSketch09() {
		// create a sketch to be filled.
		BaseTable sketch = new BaseTable("table1");

		// fill a sketch using the given example and option.
		Table inTable = tableIn1();
		Table outTable = tableOut1_1();

		Example example = new Example(outTable, new NamedTable("table1", inTable));
		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(0, result.size());
	}

	private Table tableIn10_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("001", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("002", Type.Str) //
		);
		table.addRow(//
				new Cell("XXX", Type.Str), //
				new Cell("003", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str), //
				new Cell("004", Type.Str) //
		);
		return table;
	}

	private Table tableIn10_2() {
		Table table = new Table(//
				new ColSchema("C", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str) //
		);
		return table;
	}

	/**
	 * π σ ⋈L ( B ) ( B ) <br>
	 * This is like a NOT EXIST operation.
	 */
	@Test
	void testFillSketch10() {
		// create a sketch to be filled.
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		LeftJoin p1 = new LeftJoin(base1, base2, null);
		Selection p2 = new Selection(p1, null);
		Projection p3 = new Projection(p2);
		Root sketch = new Root(p3);

		// fill a sketch using the given example and option.
		Table inTable1 = tableIn10_1();
		Table inTable2 = tableIn10_2();
		Table outTable = tableOut10();

		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1), //
				new NamedTable("table2", inTable2)//
		);
		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(1, result.size());
	}

	private Table tableOut10() {
		Table table = new Table(//
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("003", Type.Str) //
		);
		return table;
	}

	private Table tableIn11_1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("001", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("002", Type.Str) //
		);
		table.addRow(//
				new Cell("XXX", Type.Str), //
				new Cell("003", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str), //
				new Cell("004", Type.Str) //
		);
		return table;
	}

	/**
	 * T π ⋈θ ( B1 ) ( B2 )<br>
	 * This is a self join case.
	 */
	@Test
	void testFillSketch11() {
		// create a sketch to be filled.
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");// self join
		Join p1 = new Join(base1, base2, null);
		Projection p2 = new Projection(p1);
		Root sketch = new Root(p2);

		// fill a sketch using the given example and option.
		Table inTable1 = tableIn11_1();
		Table outTable = tableOut11();

		Example example = new Example(outTable, //
				new NamedTable("table1", inTable1)//
		);
		SketchFiller filler = new SketchFiller(sketch, example, new SynthOption());
		List<RAOperator> result = filler.fillSketch();

		assertEquals(4, result.size());
	}

	private Table tableOut11() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("X", Type.Str), //
				new ColSchema("Y", Type.Str) //
		);
		table.addRow(//
				new Cell("AAA", Type.Str), //
				new Cell("001", Type.Str), //
				new Cell("001", Type.Str) //
		);
		table.addRow(//
				new Cell("BBB", Type.Str), //
				new Cell("002", Type.Str), //
				new Cell("002", Type.Str) //
		);
		table.addRow(//
				new Cell("XXX", Type.Str), //
				new Cell("003", Type.Str), //
				new Cell("003", Type.Str) //
		);
		table.addRow(//
				new Cell("CCC", Type.Str), //
				new Cell("004", Type.Str), //
				new Cell("004", Type.Str) //
		);
		return table;
	}

}
