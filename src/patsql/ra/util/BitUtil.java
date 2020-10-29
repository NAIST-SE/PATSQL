package patsql.ra.util;

import java.util.BitSet;

public class BitUtil {

	public static String toBitString(BitSet bits, int width) {
		if (bits == null) {
			return "null";
		}
		if (width < 0) {
			width = bits.length();
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < width; i++) {
			if (bits.get(i)) {
				sb.append(" 1");
			} else {
				sb.append(" 0");
			}
		}
		return "[" + sb.toString() + "]";
	}

	public static int[] indexes(BitSet bits) {
		int cd = bits.cardinality();
		int[] ret = new int[cd];

		int p = 0;
		for (int i = 0; i < cd; i++) {
			ret[i] = bits.nextSetBit(p);
			p = ret[i] + 1;
		}
		return ret;
	}

	public static BitSet fromInts(int... ints) {
		BitSet ret = new BitSet();
		for (int i : ints) {
			ret.set(i);
		}
		return ret;
	}

	public static boolean includes(BitSet a, BitSet b) {
		BitSet clone = (BitSet) a.clone();
		clone.or(b);
		return a.equals(clone);
	}

}
