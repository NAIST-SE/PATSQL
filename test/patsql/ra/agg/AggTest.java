package patsql.ra.agg;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;

class AggTest {

	/**
	 * Int
	 */
	@Test
	void testEval01() {
		Cell[] cells = new Cell[] { //
				new Cell("1", Type.Int), //
				new Cell("2", Type.Int), //
				new Cell("3", Type.Int), //
				new Cell("3", Type.Int), // duplicated
				new Cell("4", Type.Int), //
				new Cell("5", Type.Int), //
		};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells),//
		};

		Cell[] expected = new Cell[] { //
				new Cell("5", Type.Int), //
				new Cell("1", Type.Int), //
				new Cell("6", Type.Int), //
				new Cell("5", Type.Int), //
				new Cell("3", Type.Dbl), //
				new Cell("18", Type.Int),//
		};

		assertArrayEquals(expected, actual);
	}

	/**
	 * Str
	 */
	@Test
	void testEval02() {
		Cell[] cells = new Cell[] { //
				new Cell("1", Type.Str), //
				new Cell("2", Type.Str), //
				new Cell("3", Type.Str), //
				new Cell("3", Type.Str), // duplicated
				new Cell("4", Type.Str), //
				new Cell("5", Type.Str), //
		};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells), //
				Agg.ConcatComma.eval(cells), //
				Agg.ConcatSpace.eval(cells), //
				Agg.ConcatSlash.eval(cells)//
		};

		Cell[] expected = new Cell[] { //
				new Cell("5", Type.Str), //
				new Cell("1", Type.Str), //
				new Cell("6", Type.Int), //
				new Cell("5", Type.Int), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("1, 2, 3, 3, 4, 5", Type.Str), //
				new Cell("1 2 3 3 4 5", Type.Str), //
				new Cell("1/2/3/3/4/5", Type.Str) //
		};

		assertArrayEquals(expected, actual);
	}

	/**
	 * Dbl
	 */
	@Test
	void testEval03() {
		Cell[] cells = new Cell[] { //
				new Cell("1", Type.Dbl), //
				new Cell("2", Type.Dbl), //
				new Cell("3", Type.Dbl), //
				new Cell("3", Type.Dbl), // duplicated
				new Cell("4", Type.Dbl), //
				new Cell("5", Type.Dbl), //
		};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells),//
		};

		Cell[] expected = new Cell[] { //
				new Cell("5.000", Type.Dbl), //
				new Cell("1.000", Type.Dbl), //
				new Cell("6", Type.Int), //
				new Cell("5", Type.Int), //
				new Cell("3.000", Type.Dbl), //
				new Cell("18.000", Type.Dbl),//
		};

		assertArrayEquals(expected, actual);
	}

	/**
	 * with Null
	 */
	@Test
	void testEval04() {
		Cell[] cells = new Cell[] { //
				new Cell("null", Type.Null), //
				new Cell("1", Type.Int), //
				new Cell("2", Type.Int), //
				new Cell("3", Type.Int), //
				new Cell("3", Type.Int), // duplicated
				new Cell("4", Type.Int), //
				new Cell("5", Type.Int), //
				new Cell("null", Type.Null), //
		};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells) //
		};

		Cell[] expected = new Cell[] { //
				new Cell("5", Type.Int), //
				new Cell("1", Type.Int), //
				new Cell("6", Type.Int), //
				new Cell("5", Type.Int), //
				new Cell("3", Type.Dbl), //
				new Cell("18", Type.Int),//
		};

		assertArrayEquals(expected, actual);
	}

	/**
	 * only Null
	 */
	@Test
	void testEval05() {
		Cell[] cells = new Cell[] { //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
		};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells),//
		};

		Cell[] expected = new Cell[] { //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
		};

		assertArrayEquals(expected, actual);
	}

	/**
	 * empty
	 */
	@Test
	void testEval06() {
		Cell[] cells = new Cell[] {};

		Cell[] actual = new Cell[] { //
				Agg.Max.eval(cells), //
				Agg.Min.eval(cells), //
				Agg.Count.eval(cells), //
				Agg.CountD.eval(cells), //
				Agg.Avg.eval(cells), //
				Agg.Sum.eval(cells),//
		};

		Cell[] expected = new Cell[] { //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null), //
		};

		assertArrayEquals(expected, actual);
	}

}
