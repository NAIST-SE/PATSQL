package patsql.entity.table;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class CellTest {

	/**
	 * Int
	 */
	@Test
	void testCompareTo01() {
		List<Cell> list = new ArrayList<>();
		list.add(new Cell("1", Type.Int));
		list.add(new Cell("4", Type.Int));
		list.add(new Cell("3", Type.Int));
		list.add(new Cell("2", Type.Int));
		list.add(new Cell("5", Type.Int));

		Collections.sort(list);

		String[] actual = new String[5];
		for (int i = 0; i < actual.length; i++) {
			actual[i] = list.get(i).value;
		}

		String[] expected = new String[] { "1", "2", "3", "4", "5" };
		assertArrayEquals(expected, actual);
	}

	/**
	 * with Null
	 */
	@Test
	void testCompareTo02() {
		List<Cell> list = new ArrayList<>();
		list.add(new Cell("1", Type.Int));
		list.add(new Cell("null", Type.Null));
		list.add(new Cell("3", Type.Int));
		list.add(new Cell("null", Type.Null));

		Collections.sort(list);

		Type[] actual = new Type[4];
		for (int i = 0; i < actual.length; i++) {
			actual[i] = list.get(i).type;
		}

		Type[] expected = new Type[] { Type.Null, Type.Null, Type.Int, Type.Int };
		assertArrayEquals(expected, actual);
	}

	/**
	 * Double
	 */
	@Test
	void testCompareTo03() {
		List<Cell> list = new ArrayList<>();
		list.add(new Cell("10.000", Type.Dbl));
		list.add(new Cell("9.000", Type.Dbl));

		Collections.sort(list);

		String[] actual = new String[2];
		for (int i = 0; i < actual.length; i++) {
			actual[i] = list.get(i).value;
		}

		String[] expected = new String[] { "9.000", "10.000" };
		assertArrayEquals(expected, actual);
	}

	/**
	 * String
	 */
	@Test
	void testCompareTo04() {
		List<Cell> list = new ArrayList<>();
		list.add(new Cell("10.000", Type.Str));
		list.add(new Cell("9.000", Type.Str));

		Collections.sort(list);

		String[] actual = new String[2];
		for (int i = 0; i < actual.length; i++) {
			actual[i] = list.get(i).value;
		}

		String[] expected = new String[] { "10.000", "9.000" };
		assertArrayEquals(expected, actual);
	}

	/**
	 * true is expected.
	 */
	@Test
	void testHasSameVec01() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("B", Type.Str));
		col2.addCell(new Cell("C", Type.Str));

		assertTrue(col1.hasSameVec(col2));
	}

	/**
	 * false is expected.
	 */
	@Test
	void testHasSameVec02() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));// disorder
		col2.addCell(new Cell("B", Type.Str));// disorder

		assertFalse(col1.hasSameVec(col2));
	}

	/**
	 * true is expected.
	 */
	@Test
	void testHasSameBag01() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("B", Type.Str));

		assertTrue(col1.hasSameBag(col2));
	}

	/**
	 * false is expected.
	 */
	@Test
	void testHasSameBag02() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("B", Type.Str));
		col2.addCell(new Cell("B", Type.Str));

		assertFalse(col1.hasSameBag(col2));
	}

	/**
	 * true is expected.
	 */
	@Test
	void testHasSameSet01() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("B", Type.Str));
		col2.addCell(new Cell("B", Type.Str));

		assertTrue(col1.hasSameSet(col2));
	}

	/**
	 * false is expected.
	 */
	@Test
	void testHasSameSet02() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("C", Type.Str));

		assertFalse(col1.hasSameSet(col2));
	}

	/**
	 * true is expected.
	 */
	@Test
	void testIsSupreSetOf01() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("C", Type.Str));

		assertTrue(col1.isSuperSetOf(col2));
	}

	/**
	 * true is expected.
	 */
	@Test
	void testIsSupreSetOf02() {
		Column col1 = new Column(new ColSchema("c1", Type.Str));
		col1.addCell(new Cell("A", Type.Str));
		col1.addCell(new Cell("B", Type.Str));
		col1.addCell(new Cell("C", Type.Str));

		Column col2 = new Column(new ColSchema("c1", Type.Str));
		col2.addCell(new Cell("A", Type.Str));
		col2.addCell(new Cell("C", Type.Str));
		col2.addCell(new Cell("XX", Type.Str));

		assertFalse(col1.isSuperSetOf(col2));
	}

}
