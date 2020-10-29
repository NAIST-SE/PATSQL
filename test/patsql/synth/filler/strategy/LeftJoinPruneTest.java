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
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class LeftJoinPruneTest {

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
				new ColSchema("D", Type.Str), //
				new ColSchema("E", Type.Str) //
		);
		table.addRow(new Cell("A", Type.Str), new Cell("XXX", Type.Str));
		table.addRow(new Cell("B", Type.Str), new Cell("YYY", Type.Str));
		return table;
	}

	@Test
	void test01() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table2");
		LeftJoin sketch = new LeftJoin(base1, base2, null);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SET)//
		);

		FillingStrategy strategy = new LeftJoinPrune(constraint);
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
				new ColSchema("D", Type.Str), //
				new ColSchema("E", Type.Str) //
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
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null)//
		);
		return table;
	}

}
