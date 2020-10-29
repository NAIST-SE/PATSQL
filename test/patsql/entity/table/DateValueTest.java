package patsql.entity.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DateValueTest {

	@Test
	void test01() {
		DateValue cell = DateValue.parse("2020-04-12");
		assertEquals("2020", cell.year());
		assertEquals("4", cell.month());
		assertEquals("12", cell.day());
	}

	@Test
	void test02() {
		DateValue cell = DateValue.parse("2020-04-01");
		assertEquals("2020", cell.year());
		assertEquals("4", cell.month());
		assertEquals("1", cell.day());
	}

	@Test
	void test03() {
		DateValue cell = DateValue.parse("2020/04/01");
		assertEquals("2020", cell.year());
		assertEquals("4", cell.month());
		assertEquals("1", cell.day());
	}

	@Test
	void test04() {
		DateValue cell = DateValue.parse("04/01/2020");
		assertEquals("2020", cell.year());
		assertEquals("4", cell.month());
		assertEquals("1", cell.day());
	}

	@Test
	void test05() {
		DateValue cell = DateValue.parse("04/01/2020 12:09:45");
		assertEquals("2020", cell.year());
		assertEquals("4", cell.month());
		assertEquals("1", cell.day());
	}

	@Test
	void test06() {
		assertThrows(IllegalArgumentException.class, () -> {
			DateValue.parse("04 05 2020");
		});
	}

	@Test
	void testCompareTo01() {
		DateValue d1 = DateValue.parse("2020-04-12");
		DateValue d2 = DateValue.parse("2020-04-12");
		assertEquals(0, d1.compareTo(d2));
	}

	@Test
	void testCompareTo02() {
		DateValue d1 = DateValue.parse("2020-04-12 12:32:12");
		DateValue d2 = DateValue.parse("2020-04-12");
		assertEquals(0, d1.compareTo(d2));
	}

	@Test
	void testCompareTo03() {
		DateValue d1 = DateValue.parse("2020-04-10");
		DateValue d2 = DateValue.parse("2020-04-12");
		assertTrue(d1.compareTo(d2) < 0);
	}

	@Test
	void testCompareTo04() {
		DateValue d1 = DateValue.parse("2021-04-10");
		DateValue d2 = DateValue.parse("2020-04-10");
		assertTrue(d1.compareTo(d2) > 0);
	}

}
