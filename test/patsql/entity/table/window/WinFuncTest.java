package patsql.entity.table.window;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

class WinFuncTest {

	/**
	 * keys for partition and order are the same.
	 */
	@Test
	void test01() {
		Cell[] cells = new Cell[] { //
				new Cell("11", Type.Int), //
				new Cell("13", Type.Int), //
				new Cell("13", Type.Int), //
				new Cell("15", Type.Int), //
				new Cell("15", Type.Int), //
				new Cell("15", Type.Int), //
				new Cell("15", Type.Int), //
				new Cell("16", Type.Int),//
		};

		{
			String[] expected = { "1", "2", "2", "4", "4", "4", "4", "8" };
			String[] actual = IntStream.range(0, cells.length)//
					.mapToObj(i -> WinFunc.RANK.eval(i, cells, cells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			String[] expected = { "1", "3", "3", "7", "7", "7", "7", "8" };
			String[] actual = IntStream.range(0, cells.length)//
					.mapToObj(i -> WinFunc.COUNT.eval(i, cells, cells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			String[] expected = { "11", "13", "13", "15", "15", "15", "15", "16" };
			String[] actual = IntStream.range(0, cells.length)//
					.mapToObj(i -> WinFunc.MAX.eval(i, cells, cells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			String[] expected = { "11", "11", "11", "11", "11", "11", "11", "11" };
			String[] actual = IntStream.range(0, cells.length)//
					.mapToObj(i -> WinFunc.MIN.eval(i, cells, cells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			String[] expected = { "11", "37", "37", "97", "97", "97", "97", "113" };
			String[] actual = IntStream.range(0, cells.length)//
					.mapToObj(i -> WinFunc.SUM.eval(i, cells, cells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}

	}

	/**
	 * keys for partition and order are different.
	 */
	@Test
	void test02() {
		Cell[] keyCells = new Cell[] { //
				new Cell("1", Type.Int), //
				new Cell("2", Type.Int), //
				new Cell("3", Type.Int), //
				new Cell("4", Type.Int), //
				new Cell("4", Type.Int), //
		};

		Cell[] targetCells = new Cell[] { //
				new Cell("10", Type.Int), //
				new Cell("10", Type.Int), //
				new Cell("10", Type.Int), //
				new Cell("10", Type.Int), //
				new Cell("10", Type.Int), //
		};

		{
			String[] expected = { "10", "20", "30", "50", "50" };
			String[] actual = IntStream.range(0, targetCells.length)//
					.mapToObj(i -> WinFunc.SUM.eval(i, targetCells, keyCells).value)//
					.toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}

	}

}
