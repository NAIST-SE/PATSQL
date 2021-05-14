package patsql.entity.table;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import patsql.entity.table.agg.Agg;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.window.WinFunc;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

class TableTest {

	@Test
	void test01() {
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
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);

		assertEquals(3, table.width());
		assertEquals(2, table.height());
	}

	@Test
	void test02() {
		ColSchema c2 = new ColSchema("B", Type.Int);
		Table table = new Table(//
				new ColSchema("A", Type.Str), //
				c2, //
				new AggColSchema(Agg.Max, c2)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12", Type.Int) //
		);
		assertEquals(3, table.width());
		assertEquals(2, table.height());
	}

	@Test
	void test03() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		Table table = new Table(//
				c1, //
				new ColSchema("B", Type.Int), //
				new AggColSchema(Agg.Count, c1)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("1", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("2", Type.Int) //
		);
		assertEquals(3, table.width());
		assertEquals(2, table.height());
	}

	@Test
	void test04() {
		Table table = new Table(//
				new ColSchema("A", Type.Dbl), //
				new ColSchema("B", Type.Str) //
		);
		table.addRow(//
				new Cell("3.333333", Type.Dbl), //
				new Cell("1", Type.Null) //
		);
		table.addRow(//
				new Cell("123.45678", Type.Dbl), //
				new Cell("2", Type.Null) //
		);
		assertEquals(2, table.width());
		assertEquals(2, table.height());
	}

	@Test
	void testProject01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);
		ColSchema c3 = new ColSchema("C", Type.Dbl);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);

		Table result = table.project(new int[] { //
				c1.id, //
				c2.id //
		});

		assertEquals(2, result.width());
		assertEquals(2, result.height());
	}

	@Test
	void testProject02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);
		ColSchema c3 = new ColSchema("C", Type.Dbl);
		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int), //
				new Cell("12.8", Type.Dbl)//
		);

		Table result = table.project(new int[] { //
				c2.id, //
				c1.id, //
				c3.id, //
				c2.id, //
				c1.id, //
				c3.id//
		});

		assertEquals(6, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * nil key.
	 */
	@Test
	void testGroupBy01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);

		Table table = new Table(c1, c2);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("10", Type.Int) //
		);

		Table result = table.groupBy(//
				GroupKeys.nil(), //
				Aggregators.all(c1, c2)//
		);

		assertEquals(13, result.width());
		assertEquals(1, result.height());
	}

	/**
	 * nil key.
	 */
	@Test
	void testGroupBy02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);

		Table table = new Table(c1, c2);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("10", Type.Int) //
		);

		Table result = table.groupBy(//
				GroupKeys.nil(), //
				new Aggregators(//
						new AggColSchema(Agg.Max, c1), //
						new AggColSchema(Agg.Max, c2), //
						new AggColSchema(Agg.Min, c2)//
				)//
		);

		assertEquals(3, result.width());
		assertEquals(1, result.height());
	}

	/**
	 * single key, three aggregation.
	 */
	@Test
	void testGroupBy03() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);

		Table table = new Table(c1, c2);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("10", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("10", Type.Int) //
		);

		Table result = table.groupBy(//
				new GroupKeys(//
						c1//
				), //
				new Aggregators(//
						new AggColSchema(Agg.Max, c1), //
						new AggColSchema(Agg.Max, c2), //
						new AggColSchema(Agg.Min, c2)//
				)//
		);

		assertEquals(1 + 3, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * single key, no aggregation.
	 */
	@Test
	void testGroupBy04() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);

		Table table = new Table(c1, c2);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("10", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("10", Type.Int) //
		);

		Table result = table.groupBy(//
				new GroupKeys(//
						c1//
				), //
				new Aggregators()//
		);

		assertEquals(1 + 0, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * single key, no aggregation.
	 */
	@Test
	void testGroupBy05() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Int);

		Table table = new Table(c1, c2);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("10", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("10", Type.Int) //
		);

		Table result = table.groupBy(//
				new GroupKeys(//
						c1//
				), //
				new Aggregators()//
		);

		assertEquals(1 + 0, result.width());
		assertEquals(2, result.height());
	}

	/**
	 * two keys, three aggregation.
	 */
	@Test
	void testGroupBy06() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Int);

		Table table = new Table(c1, c2, c3);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("12", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("7", Type.Int) //
		);
		table.addRow(//
				new Cell("bar", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("8", Type.Int) //
		);
		table.addRow(//
				new Cell("foo", Type.Str), // duplicated
				new Cell("X", Type.Str), //
				new Cell("2", Type.Int) //
		);

		Table result = table.groupBy(//
				new GroupKeys(//
						c1, //
						c2//
				), //
				new Aggregators(//
						new AggColSchema(Agg.Max, c3), //
						new AggColSchema(Agg.Min, c3) //
				)//
		);

		assertEquals(2 + 2, result.width());
		assertEquals(3, result.height());
	}

	/**
	 * distinct
	 */
	@Test
	void testGroupBy07() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);

		Table table = new Table(c1, c2);
		table.addRow(new Cell("foo", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("foo", Type.Str), new Cell("X", Type.Str));
		table.addRow(new Cell("foo", Type.Str), new Cell("Y", Type.Str));
		table.addRow(new Cell("foo", Type.Str), new Cell("Y", Type.Str));
		table.addRow(new Cell("foo", Type.Str), new Cell("Y", Type.Str));
		table.addRow(new Cell("bar", Type.Str), new Cell("Y", Type.Str));
		table.addRow(new Cell("bar", Type.Str), new Cell("Y", Type.Str));
		table.addRow(new Cell("bar", Type.Str), new Cell("Y", Type.Str));

		Table result = table.groupBy(//
				new GroupKeys(//
						c1, //
						c2//
				), //
				Aggregators.empty()//
		);

		assertEquals(2, result.width());
		assertEquals(3, result.height());
	}

	/*
	 * left join
	 */
	@Test
	void testLeftJoin01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("C", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("D", Type.Str), //
				new Cell("X", Type.Str) //
		);

		Table table2 = new Table(c3);
		table2.addRow(//
				new Cell("A", Type.Str) //
		);
		table2.addRow(//
				new Cell("D", Type.Str) //
		);

		Table result = table1.leftJoin(//
				table2, //
				new JoinCond(new JoinKeyPair(c1, c3)) //
		);

		assertEquals(3, result.width());
		assertEquals(4, result.height());
	}

	/*
	 * left join, duplicated match.
	 */
	@Test
	void testLeftJoin02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("D", Type.Str), //
				new Cell("X", Type.Str) //
		);

		Table table2 = new Table(c3);
		table2.addRow(//
				new Cell("A", Type.Str) //
		);
		table2.addRow(//
				new Cell("A", Type.Str) //
		);
		table2.addRow(//
				new Cell("D", Type.Str) //
		);
		table2.addRow(//
				new Cell("D", Type.Str) //
		);

		Table result = table1.leftJoin(//
				table2, //
				new JoinCond(new JoinKeyPair(c1, c3)) //
		);

		assertEquals(3, result.width());
		assertEquals(2 * 2 + 1 + 1 * 2, result.height());
	}

	/*
	 * left join, composite key
	 */
	@Test
	void testLeftJoin03() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		ColSchema c4 = new ColSchema("D", Type.Str);
		ColSchema c5 = new ColSchema("E", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("A", Type.Str), // match
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("A", Type.Str), // not match
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("B", Type.Str), // match
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("D", Type.Str), // not match
				new Cell("X", Type.Str) //
		);

		Table table2 = new Table(c3, c4, c5);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("01", Type.Str) //
		);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("03", Type.Str) //
		);
		table2.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("06", Type.Str) //
		);
		table2.addRow(//
				new Cell("D", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("08", Type.Str) //
		);

		Table result = table1.leftJoin(//
				table2, //
				new JoinCond(//
						new JoinKeyPair(c1, c3), //
						new JoinKeyPair(c2, c4)//
				) //
		);

		assertEquals(5, result.width());
		assertEquals(4, result.height());
	}

	/*
	 * left join, composite key (different order than case03)
	 */
	@Test
	void testLeftJoin04() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		ColSchema c4 = new ColSchema("D", Type.Str);
		ColSchema c5 = new ColSchema("E", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("D", Type.Str), //
				new Cell("X", Type.Str) //
		);

		Table table2 = new Table(c3, c4, c5);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("01", Type.Str) //
		);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("03", Type.Str) //
		);
		table2.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("06", Type.Str) //
		);
		table2.addRow(//
				new Cell("D", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("08", Type.Str) //
		);

		Table result = table1.leftJoin(//
				table2, //
				new JoinCond(//
						new JoinKeyPair(c2, c4), // different order.
						new JoinKeyPair(c1, c3) //
				) //
		);

		assertEquals(5, result.width());
		assertEquals(4, result.height());
	}

	/*
	 * left join, empty key.
	 */
	@Test
	void testLeftJoin05() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		ColSchema c4 = new ColSchema("D", Type.Str);
		ColSchema c5 = new ColSchema("E", Type.Str);

		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str) //
		);

		Table table2 = new Table(c3, c4, c5);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("Z", Type.Str), //
				new Cell("01", Type.Str) //
		);
		table2.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("03", Type.Str) //
		);
		table2.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("06", Type.Str) //
		);

		Table result = table1.leftJoin(//
				table2, //
				new JoinCond() // empty
		);

		assertEquals(5, result.width());
		assertEquals(6, result.height());
	}

	/*
	 * Int types
	 */
	@Test
	void testIsIncreasing01() {
		ColSchema c1 = new ColSchema("A", Type.Int);
		ColSchema c2 = new ColSchema("B", Type.Int);
		ColSchema c3 = new ColSchema("C", Type.Int);
		Table table1 = new Table(c1, c2, c3);
		table1.addRow(//
				new Cell("10", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("1000", Type.Int) //
		);
		table1.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("999", Type.Int) //
		);
		table1.addRow(//
				new Cell("13", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("1001", Type.Int) //
		);

		assertTrue(table1.isIncreasing(c1));
		assertFalse(table1.isIncreasing(c2));
		assertFalse(table1.isIncreasing(c3));
	}

	/*
	 * Str type
	 */
	@Test
	void testIsIncreasing02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		Table table1 = new Table(c1, c2, c3);
		table1.addRow(//
				new Cell("10", Type.Str), //
				new Cell("100", Type.Str), //
				new Cell("1000", Type.Str) //
		);
		table1.addRow(//
				new Cell("12", Type.Str), //
				new Cell("100", Type.Str), //
				new Cell("999", Type.Str) //
		);
		table1.addRow(//
				new Cell("13", Type.Str), //
				new Cell("100", Type.Str), //
				new Cell("1001", Type.Str) //
		);

		assertTrue(table1.isIncreasing(c1));
		assertFalse(table1.isIncreasing(c2));
		assertFalse(table1.isIncreasing(c3));
	}

	/*
	 * Str with Null
	 */
	@Test
	void testIsIncreasing03() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("null", Type.Null), //
				new Cell("null", Type.Null)//
		);
		table1.addRow(//
				new Cell("10", Type.Str), //
				new Cell("null", Type.Null) //
		);
		table1.addRow(//
				new Cell("12", Type.Str), //
				new Cell("100", Type.Str) //
		);

		assertTrue(table1.isIncreasing(c1));
		assertTrue(table1.isIncreasing(c2));
	}

	/*
	 * Int type
	 */
	@Test
	void testIsDecreasing01() {
		ColSchema c1 = new ColSchema("A", Type.Int);
		ColSchema c2 = new ColSchema("B", Type.Int);
		ColSchema c3 = new ColSchema("C", Type.Int);
		Table table1 = new Table(c1, c2, c3);
		table1.addRow(//
				new Cell("10", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("1000", Type.Int) //
		);
		table1.addRow(//
				new Cell("12", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("999", Type.Int) //
		);
		table1.addRow(//
				new Cell("13", Type.Int), //
				new Cell("100", Type.Int), //
				new Cell("997", Type.Int) //
		);

		assertFalse(table1.isDecreasing(c1));
		assertFalse(table1.isDecreasing(c2));
		assertTrue(table1.isDecreasing(c3));
	}

	@Test
	void testHasUniqueCells01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		ColSchema c3 = new ColSchema("C", Type.Str);
		Table table1 = new Table(c1, c2, c3);
		table1.addRow(//
				new Cell("10", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		table1.addRow(//
				new Cell("10", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("Y", Type.Str) //
		);
		table1.addRow(//
				new Cell("10", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);

		assertFalse(table1.hasUnqueCells(new GroupKeys(c1)));
		assertFalse(table1.hasUnqueCells(new GroupKeys(c2)));
		assertFalse(table1.hasUnqueCells(new GroupKeys(c3)));
		assertFalse(table1.hasUnqueCells(new GroupKeys(c1, c2)));
		assertFalse(table1.hasUnqueCells(new GroupKeys(c1, c3)));
		assertTrue(table1.hasUnqueCells(new GroupKeys(c2, c3)));
		assertTrue(table1.hasUnqueCells(new GroupKeys(c1, c2, c3)));
	}

	@Test
	void testHasUniqueCells02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("1", Type.Str), //
				new Cell("A", Type.Str) //
		);
		table1.addRow(//
				new Cell("2", Type.Str), //
				new Cell("A", Type.Str) //
		);
		table1.addRow(//
				new Cell("3", Type.Str), //
				new Cell("B", Type.Str) //
		);

		assertTrue(table1.hasUnqueCells(new GroupKeys(c1)));
		assertFalse(table1.hasUnqueCells(new GroupKeys(c2)));
		assertTrue(table1.hasUnqueCells(new GroupKeys(c1, c2)));
	}

	@Test
	void testCloneWithNewColIds() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		Table table1 = new Table(c1, c2);
		table1.addRow(//
				new Cell("1", Type.Str), //
				new Cell("A", Type.Str) //
		);
		table1.addRow(//
				new Cell("2", Type.Str), //
				new Cell("A", Type.Str) //
		);
		table1.addRow(//
				new Cell("3", Type.Str), //
				new Cell("B", Type.Str) //
		);
		Table clone = table1.cloneWithNewColIDs(new int[] { -1, -2 });

		assertEquals(-1, clone.columns[0].schema.id);
		assertEquals(-2, clone.columns[1].schema.id);
		assertArrayEquals(table1.columns[0].cells(), clone.columns[0].cells());
		assertArrayEquals(table1.columns[1].cells(), clone.columns[1].cells());
	}

	@Test
	void testApplyWindow01() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table tbl = new Table(c1, c2);
		tbl.addRow(new Cell("1", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("2", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("2", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("1", Type.Int), new Cell("B", Type.Str));
		tbl.addRow(new Cell("2", Type.Int), new Cell("B", Type.Str));
		tbl.addRow(new Cell("3", Type.Int), new Cell("B", Type.Str));

		Table res = tbl.applyWindow(//
				new WinColSchema(WinFunc.SUM, c1, new GroupKeys(c2), new SortKey(c1, Order.Asc))//
				, new WinColSchema(WinFunc.SUM, c1, new GroupKeys(c2), new SortKey(c1, Order.Desc))//
		);
		{
			int col = 2;
			String[] expected = new String[] { "1", "5", "5", "1", "3", "6" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			int col = 3;
			String[] expected = new String[] { "5", "4", "4", "6", "5", "3" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
	}

	@Test
	void testApplyWindow02() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		Table tbl = new Table(c1, c2);
		tbl.addRow(new Cell("11", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("13", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("12", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("14", Type.Int), new Cell("B", Type.Str));
		tbl.addRow(new Cell("11", Type.Int), new Cell("B", Type.Str));

		Table res = tbl.applyWindow(//
				new WinColSchema(WinFunc.SUM, c1, GroupKeys.nil(), new SortKey(c1, Order.Asc))//
				, new WinColSchema(WinFunc.SUM, c1, new GroupKeys(c2), new SortKey(c1, Order.Asc))//
				, new WinColSchema(WinFunc.COUNT, c1, new GroupKeys(c2), new SortKey(c1, Order.Asc))//
				, new WinColSchema(WinFunc.COUNT, c1, GroupKeys.nil(), new SortKey(c1, Order.Asc))//
		);

		{
			int col = 2;
			String[] expected = new String[] { "22", "47", "34", "61", "22" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			int col = 3;
			String[] expected = new String[] { "11", "36", "23", "25", "11" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			int col = 4;
			String[] expected = new String[] { "1", "3", "2", "2", "1" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}
		{
			int col = 5;
			String[] expected = new String[] { "2", "4", "3", "5", "2" };
			String[] actual = Arrays.stream(res.columns[col].cells()).map(e -> e.value).toArray(String[]::new);
			assertArrayEquals(expected, actual);
		}

	}

	@Test
	void testAddColumn01() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);

		Table tbl = new Table(c1, c2);
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));

		Column col3 = new Column(new ColSchema("c3", Type.Str));
		col3.addCell(new Cell("X", Type.Str));
		col3.addCell(new Cell("X", Type.Str));
		col3.addCell(new Cell("X", Type.Str));

		Table result = tbl.addColumns(col3);
		assertEquals(3, result.height());
		assertEquals(3, result.width());
	}

	@Test
	void testAddColumn02() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);

		Table tbl = new Table(c1, c2);
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));

		Column col3 = new Column(new ColSchema("c3", Type.Str));
		col3.addCell(new Cell("X1", Type.Str));
		col3.addCell(new Cell("X2", Type.Str));
		col3.addCell(new Cell("X3", Type.Str));

		Column col4 = new Column(new ColSchema("c3", Type.Str));
		col4.addCell(new Cell("Y1", Type.Str));
		col4.addCell(new Cell("Y2", Type.Str));
		col4.addCell(new Cell("Y3", Type.Str));

		Table result = tbl.addColumns(col3, col4);
		assertEquals(3, result.height());
		assertEquals(4, result.width());
	}

	@Test
	void testAddColumn03() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);

		Table tbl = new Table(c1, c2);
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));
		tbl.addRow(new Cell("10", Type.Int), new Cell("A", Type.Str));

		Column col3 = new Column(new ColSchema("c3", Type.Str));
		col3.addCell(new Cell("X1", Type.Str));
		col3.addCell(new Cell("X2", Type.Str));

		assertThrows(IllegalArgumentException.class, () -> {
			tbl.addColumns(col3);
		});
	}

}
