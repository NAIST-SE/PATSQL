package patsql.ra.predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Type;

class BinaryPredTest {

	@Test
	void tes01() {
		ColSchema sc = new ColSchema("c", Type.Dbl);
		{
			BinaryPred pred = new BinaryPred(//
					sc, //
					BinaryOp.Eq, //
					new Cell("12", Type.Int)//
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("12", Type.Int));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
		{
			BinaryPred pred = new BinaryPred(//
					sc, //
					BinaryOp.Eq, //
					new Cell("13", Type.Int)//
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("12", Type.Int));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.False, actual);
		}
		{
			BinaryPred pred = new BinaryPred(//
					sc, //
					BinaryOp.Gt, //
					new Cell("13.02", Type.Dbl)//
			);

			PredEnv env = new PredEnv();
			env.put(sc.id, new Cell("14.00", Type.Dbl));

			ExBool actual = pred.eval(env);
			assertEquals(ExBool.True, actual);
		}
	}

}
