package patsql.synth.filler.strategy;

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
import patsql.ra.operator.Window;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class WindowPruneTest {

	private Table tableIn1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		table.addRow(new Cell("AAA", Type.Str), new Cell("XXX", Type.Str), new Cell("8", Type.Int));
		table.addRow(new Cell("AAA", Type.Str), new Cell("YYY", Type.Str), new Cell("12", Type.Int));
		table.addRow(new Cell("AAA", Type.Str), new Cell("YYY", Type.Str), new Cell("10", Type.Int));
		table.addRow(new Cell("BBB", Type.Str), new Cell("XXX", Type.Str), new Cell("17", Type.Int));
		table.addRow(new Cell("BBB", Type.Str), new Cell("YYY", Type.Str), new Cell("5", Type.Int));
		return table;
	}

	private Table tableOut1() {
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Str), //
				new ColSchema("C", Type.Int) //
		);
		return table;
	}

	@Test
	void test01() {
		BaseTable base = new BaseTable("table1");
		Window sketch = new Window(base, null, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.UNKNOWN)//
		);

		FillingStrategy strategy = new WindowPrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableOut1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		for (RAOperator r : result) {
			Table t = r.eval(example.tableEnv());
			System.out.println(t);
		}
//		assertEquals(1, result.size());
//		GroupBy g = (GroupBy) result.get(0);
//		ColSchema[] sc = g.keys.colSchemas;
//		assertEquals(1, sc.length);
//		assertEquals("A", sc[0].name);// check the key name.
	}

}
