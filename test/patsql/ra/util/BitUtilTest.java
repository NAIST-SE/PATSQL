package patsql.ra.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

class BitUtilTest {

	@Test
	void testIndexes01() {
		BitSet bits = new BitSet();
		bits.set(1);
		bits.set(5);
		bits.set(9);

		int[] indexes = BitUtil.indexes(bits);
		assertArrayEquals(new int[] { 1, 5, 9 }, indexes);
	}

	@Test
	void testIncludes01() {
		{
			BitSet b1 = BitUtil.fromInts(1, 2, 3);
			BitSet b2 = BitUtil.fromInts(1, 2);
			assertTrue(BitUtil.includes(b1, b2));
			assertFalse(BitUtil.includes(b2, b1));
		}
		{
			BitSet b1 = BitUtil.fromInts(1, 2, 3);
			BitSet b2 = BitUtil.fromInts(1);
			assertTrue(BitUtil.includes(b1, b2));
			assertFalse(BitUtil.includes(b2, b1));
		}
		{
			BitSet b1 = BitUtil.fromInts(1, 2, 3);
			BitSet b2 = BitUtil.fromInts(1, 4);
			assertFalse(BitUtil.includes(b1, b2));
			assertFalse(BitUtil.includes(b2, b1));
		}
	}
}
