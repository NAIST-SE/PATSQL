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
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColMatching;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;

class ProjectionUnknownTest {

	/**
	 * simple case.
	 */
	@Test
	void test01() {
		// create a sketch to be filled.
		BaseTable base = new BaseTable("table1");
		Projection sketch = new Projection(base);

		// fill a sketch using the given example and option.
		ProjectionUnknown strategy = new ProjectionUnknown(//
				new FillingConstraint(new ColConstraint(ColMatching.UNKNOWN, ColRelation.UNKNOWN))//
		);
		Table inTable = createTable();
		Example example = new Example(null /* output not used */, new NamedTable("table1", inTable));
		SynthOption option = new SynthOption();

		List<RAOperator> result = strategy.fill(sketch, example, option);
		assertEquals(16, result.size());
	}

	private Table createTable() {
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
		return table;
	}

}
