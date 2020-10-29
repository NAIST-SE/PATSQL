package patsql.synth.sketcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import patsql.ra.operator.Join;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.RAUtils;
import patsql.ra.util.RAVisitor;

class SketcherTest {

	@Test
	void testGenerateSketches001() {
		Sketcher sketcher = new Sketcher(1, false);
		RAOperator current = sketcher.next();

		{
			int count = 0;
			while (Sketcher.sizeOf(current) == 2) {
				count++;
				current = sketcher.next();
			}
			assertEquals(1, count);
		}

		{
			int count = 0;
			while (Sketcher.sizeOf(current) == 3) {
				count++;
				current = sketcher.next();
			}
			assertEquals(5, count);
		}
	}

	@Test
	void testGenerateSketches002() {
		Sketcher sketcher = new Sketcher(1, true);
		RAOperator current = sketcher.next();

		{
			int count = 0;
			while (Sketcher.sizeOf(current) == 3) {
				count++;
				current = sketcher.next();
			}
			assertEquals(1, count);
		}

		{
			int count = 0;
			while (Sketcher.sizeOf(current) == 4) {
				count++;
				current = sketcher.next();
			}
			assertEquals(5, count);
		}
	}

	@Test
	void testGenerateSketches003() {
		Sketcher sketcher = new Sketcher(1, false);
		Set<RAOperator> set = new TreeSet<>(Sketcher::compare);

		RAOperator last = sketcher.next();
		RAOperator current;
		final int iteration = 1000;
		for (int i = 0; i < iteration; i++) {
			set.add(last);
			current = sketcher.next();
			assertTrue(Sketcher.sizeOf(last) <= Sketcher.sizeOf(current));
			last = current;
		}

		assertEquals(iteration, set.size());
	}

	private static Set<String> replaceθWithL(String str) {
		Set<String> ret = new TreeSet<>();
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) == 'θ')
				ret.add(str.substring(0, i) + "L" + str.substring(i + 1));
		return ret;
	}

	@Test
	void testGenerateSketches004() {
		Sketcher sketcher = new Sketcher(2, false);
		Set<String> set = new TreeSet<>();

		final int iteration = 1000;
		for (int i = 0; i < iteration; i++) {
			RAOperator sketch = sketcher.next();
			String str = RAUtils.buildSketch(sketch);

			for (String s : replaceθWithL(str))
				if (set.contains(s))
					fail();

			set.add(str);
		}
	}

	private static List<RAOperator> swapChildrenOfθ(RAOperator opr) {
		List<RAOperator> ret = new ArrayList<>();
		List<Join> joins = new ArrayList<>();

		RAUtils.traverse(opr, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.JOIN)
					joins.add((Join) op);
				return true;
			}
		});

		for (Join j : joins) {
			Join swapped = j.clone();
			swapped.childL = j.childR;
			swapped.childR = j.childL;
			ret.add(RAUtils.replace(opr, swapped, j));
		}

		return ret;
	}

	@Test
	void testGenerateSketches005() {
		Sketcher sketcher = new Sketcher(1, false);
		Set<String> set = new TreeSet<>();

		final int iteration = 1000;
		for (int i = 0; i < iteration; i++) {
			RAOperator sketch = sketcher.next();
			String str = RAUtils.buildSketch(sketch);

			for (RAOperator op : swapChildrenOfθ(sketch)) {
				String s = RAUtils.buildSketch(op);
				if (set.contains(s))
					fail();
			}

			set.add(str);
		}
	}

	@Test
	void testSeeds001() {
		for (int i = 1; i < 8; i++) {
			int expected = 1 + i;
			Sketcher.seeds(i, false).forEach(raOperator -> assertEquals(expected, Sketcher.sizeOf(raOperator)));
		}
	}
}
