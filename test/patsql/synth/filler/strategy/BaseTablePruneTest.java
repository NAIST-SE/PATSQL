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
import patsql.synth.filler.FillingConstraint;

class BaseTablePruneTest {

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

	@Test
	void test01() {
		BaseTable sketch = new BaseTable("table1");

		// fill a sketch using the given example and option.
		FillingConstraint constraint = FillingConstraint.sameAsOutput();
		BaseTablePrune strategy = new BaseTablePrune(constraint);
		Table inTable = tableIn1();
		Table outTable = tableIn1();
		Example example = new Example(outTable, new NamedTable("table1", inTable));
		List<RAOperator> result = strategy.fill(sketch, example, new SynthOption());

		assertEquals(1, result.size());
	}

}
