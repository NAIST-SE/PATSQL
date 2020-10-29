package patsql.ra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.ColSchema;
import patsql.entity.table.Type;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;

class PredUtilTest {

	@Test
	void testNumberOfOR01() {
		UnaryPred p = new UnaryPred(new ColSchema("c1", Type.Int), UnaryOp.IsNotNull);
		Conjunction pred = new Conjunction(//
				p//
		);
		assertEquals(0, PredUtil.numberOfOR(pred));
	}

	@Test
	void testNumberOfOR02() {
		UnaryPred p = new UnaryPred(new ColSchema("c1", Type.Int), UnaryOp.IsNotNull);
		Conjunction pred = new Conjunction(//
				p, //
				new Disjunction(p, p)//
		);
		assertEquals(1, PredUtil.numberOfOR(pred));
	}

	@Test
	void testNumberOfOR03() {
		UnaryPred p = new UnaryPred(new ColSchema("c1", Type.Int), UnaryOp.IsNotNull);
		Conjunction pred = new Conjunction(//
				p, //
				new Disjunction(p)//
		);
		assertEquals(0, PredUtil.numberOfOR(pred));
	}

	@Test
	void testNumberOfOR04() {
		UnaryPred p = new UnaryPred(new ColSchema("c1", Type.Int), UnaryOp.IsNotNull);
		Conjunction pred = new Conjunction(//
				p, //
				new Disjunction(p, p), //
				new Disjunction(p, p)//
		);
		assertEquals(2, PredUtil.numberOfOR(pred));
	}

	@Test
	void testNumberOfOR05() {
		UnaryPred p = new UnaryPred(new ColSchema("c1", Type.Int), UnaryOp.IsNotNull);
		Conjunction pred = new Conjunction(//
				p, //
				new Disjunction(p, p, p), //
				new Disjunction(p, p)//
		);
		assertEquals(3, PredUtil.numberOfOR(pred));
	}

}
