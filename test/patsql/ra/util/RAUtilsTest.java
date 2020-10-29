package patsql.ra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Distinct;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Root;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;

class RAUtilsTest {

	@Test
	void testInsert01() {
		BaseTable p1 = new BaseTable("table");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();

		p2.child = p1;
		p3.child = p2;

		Selection added = Selection.empty();
		RAOperator created = RAUtils.insert(p3, added, p1);

		// p3 -> p2 -> added -> p1
		assertEquals(p3.ID(), created.ID());
		assertTrue(created instanceof Projection);
		RAOperator c1 = ((Projection) created).child;

		assertEquals(p2.ID(), c1.ID());
		assertTrue(c1 instanceof Selection);
		RAOperator c2 = ((Selection) c1).child;

		assertEquals(added.ID(), c2.ID());
		assertTrue(c2 instanceof Selection);
		RAOperator c3 = ((Selection) c2).child;

		assertEquals(p1.ID(), c3.ID());
		assertTrue(c3 instanceof BaseTable);
	}

	@Test
	void testInsert02() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");
		BaseTable base3 = new BaseTable("table3");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();
		GroupBy p5 = GroupBy.empty();
		Join p6 = Join.empty();
		LeftJoin p7 = LeftJoin.empty();
		Sort p8 = Sort.empty();
		Distinct p9 = Distinct.empty();
		Root p10 = Root.empty();

		p2.child = base1;
		p3.child = p2;
		p5.child = base2;
		p6.childL = p3;
		p6.childR = p5;
		p7.childL = p6;
		p7.childR = base3;
		p8.child = p7;
		p9.child = p8;
		p10.child = p9;

		Selection added = Selection.empty();
		// RAUtils.printTree(p10);
		@SuppressWarnings("unused")
		RAOperator created = RAUtils.insert(p10, added, base1);
		// RAUtils.printTree(created);
	}

	@Test
	void testInsert03() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");
		BaseTable base3 = new BaseTable("table3");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();
		GroupBy p5 = GroupBy.empty();
		Join p6 = Join.empty();
		LeftJoin p7 = LeftJoin.empty();
		Sort p8 = Sort.empty();
		Distinct p9 = Distinct.empty();
		Root p10 = Root.empty();

		p2.child = base1;
		p3.child = p2;
		p5.child = base2;
		p6.childL = p3;
		p6.childR = p5;
		p7.childL = p6;
		p7.childR = base3;
		p8.child = p7;
		p9.child = p8;
		p10.child = p9;

		Selection added = Selection.empty();
		// RAUtils.printTree(p10);
		@SuppressWarnings("unused")
		RAOperator created = RAUtils.insert(p10, added, base2);
		// RAUtils.printTree(created);
	}

	/**
	 * location is deep.
	 */
	@Test
	void testReplace01() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");
		BaseTable base3 = new BaseTable("table3");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();
		GroupBy p5 = GroupBy.empty();
		Join p6 = Join.empty();
		LeftJoin p7 = LeftJoin.empty();
		Sort p8 = Sort.empty();
		Distinct p9 = Distinct.empty();
		Root p10 = Root.empty();

		p2.child = base1;
		p3.child = p2;
		p5.child = base2;
		p6.childL = p3;
		p6.childR = p5;
		p7.childL = p6;
		p7.childR = base3;
		p8.child = p7;
		p9.child = p8;
		p10.child = p9;

		BaseTable replaced = new BaseTable("table4");
		// RAUtils.printTree(p10);
		@SuppressWarnings("unused")
		RAOperator created = RAUtils.replace(p10, replaced, p8);
		// RAUtils.printTree(created);
	}

	/**
	 * location is top.
	 */
	@Test
	void testReplace03() {
		BaseTable base1 = new BaseTable("table1");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();

		p2.child = base1;
		p3.child = p2;

		BaseTable replaced = new BaseTable("table4");
		// RAUtils.printTree(p3);
		@SuppressWarnings("unused")
		RAOperator created = RAUtils.replace(p3, replaced, p3);
		// RAUtils.printTree(created);
	}

	@Test
	void testReplace02() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");
		BaseTable base3 = new BaseTable("table3");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();
		GroupBy p5 = GroupBy.empty();
		Join p6 = Join.empty();
		LeftJoin p7 = LeftJoin.empty();
		Sort p8 = Sort.empty();
		Distinct p9 = Distinct.empty();
		Root p10 = Root.empty();

		p2.child = base1;
		p3.child = p2;
		p5.child = base2;
		p6.childL = p3;
		p6.childR = p5;
		p7.childL = p6;
		p7.childR = base3;
		p8.child = p7;
		p9.child = p8;
		p10.child = p9;

		BaseTable replaced = new BaseTable("table4");
		// RAUtils.printTree(p10);
		@SuppressWarnings("unused")
		RAOperator created = RAUtils.replace(p10, replaced, p5);
		// RAUtils.printTree(created);
	}

	@Test
	void testPrintTree01() {
		BaseTable base1 = new BaseTable("table1");
		BaseTable base2 = new BaseTable("table1");
		BaseTable base3 = new BaseTable("table3");
		Selection p2 = Selection.empty();
		Projection p3 = Projection.empty();
		GroupBy p5 = GroupBy.empty();
		Join p6 = Join.empty();
		LeftJoin p7 = LeftJoin.empty();
		Sort p8 = Sort.empty();
		Distinct p9 = Distinct.empty();
		Root p10 = Root.empty();

		p2.child = base1;
		p3.child = p2;
		p5.child = base2;
		p6.childL = p3;
		p6.childR = p5;
		p7.childL = p6;
		p7.childR = base3;
		p8.child = p7;
		p9.child = p8;
		p10.child = p9;

		// RAUtils.printTree(p10);
	}
}
