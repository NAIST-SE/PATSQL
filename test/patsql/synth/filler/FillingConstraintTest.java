package patsql.synth.filler;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

class FillingConstraintTest {

	/**
	 * EXACT, SUPER
	 */
	@Test
	void testCompare02() {
		Table tmp = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		tmp.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int) //
		);

		Table out = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		out.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		out.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXACT, ColRelation.SUPER_SET)//
		);

		assertFalse(constraint.isPruned(tmp, out));
	}

	/**
	 * EXISTS, SUPER
	 */
	@Test
	void testCompare04() {
		Table tmp = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		tmp.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int) //
		);

		Table out = new Table(//
				new ColSchema("B", Type.Int) //
		);
		out.addRow(//
				new Cell("12", Type.Int) //
		);
		out.addRow(//
				new Cell("13", Type.Int) //
		);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.SUPER_SET)//
		);

		assertFalse(constraint.isPruned(tmp, out));
	}

	/**
	 * EXISTS, BAG
	 */
	@Test
	void testCompare05() {
		Table tmp = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		tmp.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);
		tmp.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int) //
		);

		Table out = new Table(//
				new ColSchema("A", Type.Str), //
				new ColSchema("B", Type.Int) //
		);
		out.addRow(//
				new Cell("baz", Type.Str), //
				new Cell("13", Type.Int) //
		);
		out.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		out.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("12", Type.Int) //
		);

		FillingConstraint constraint = new FillingConstraint(//
				new ColConstraint(ColMatching.EXISTS, ColRelation.BAG)//
		);

		assertFalse(constraint.isPruned(tmp, out));
	}

}
