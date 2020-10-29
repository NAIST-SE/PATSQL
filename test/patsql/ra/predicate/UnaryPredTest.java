package patsql.ra.predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Type;

class UnaryPredTest {

	@Test
	void test01() {

		{
			ColSchema sc = new ColSchema("c", Type.Dbl);
			UnaryPred pred = new UnaryPred(//
					sc, //
					UnaryOp.IsNull //
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("null", Type.Null));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
		{
			ColSchema sc = new ColSchema("c", Type.Str);
			UnaryPred pred = new UnaryPred(//
					sc, //
					UnaryOp.IsNull //
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("null", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.False, actual);
		}
		{
			ColSchema sc = new ColSchema("c", Type.Str);
			UnaryPred pred = new UnaryPred(//
					sc, //
					UnaryOp.IsNotNull //
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("null", Type.Null));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.False, actual);
		}
		{
			ColSchema sc = new ColSchema("c", Type.Str);
			UnaryPred pred = new UnaryPred(//
					sc, //
					UnaryOp.IsNotNull //
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("null", Type.Str));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
	}

}
