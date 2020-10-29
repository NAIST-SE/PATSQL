package patsql.ra.predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Type;

class DisjunctionTest {

	@Test
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Int);
		ColSchema c2 = new ColSchema("c1", Type.Str);
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // TRUE
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// TRUE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("12", Type.Int));
			env.put(c2.id, new Cell("A", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // FALSE
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// TRUE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("13", Type.Int));
			env.put(c2.id, new Cell("A", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // TRUE
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// FALSE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("12", Type.Int));
			env.put(c2.id, new Cell("B", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // FALSE
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// FALSE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("13", Type.Int));
			env.put(c2.id, new Cell("B", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.False, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // UNKNOWN
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// UNKNOWN
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("null", Type.Null));
			env.put(c2.id, new Cell("null", Type.Null));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.Unknown, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // UNKNOWN
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// FALSE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("null", Type.Null));
			env.put(c2.id, new Cell("B", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.Unknown, actual);
		}
		{
			Predicate pred = new Disjunction(//
					new BinaryPred(c1, BinaryOp.Eq, new Cell("12", Type.Int)), // UNKNOWN
					new BinaryPred(c2, BinaryOp.Eq, new Cell("A", Type.Str))// TRUE
			);

			PredEnv env = new PredEnv();
			env.put(c1.id, new Cell("null", Type.Null));
			env.put(c2.id, new Cell("A", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
	}

}
