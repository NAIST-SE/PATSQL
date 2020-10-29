package patsql.ra.predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

class UnaryOpTest {

	@Test
	void test01() {
		{
			ExBool actual = UnaryOp.IsNull.eval(//
					new Cell("null", Type.Null) //
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = UnaryOp.IsNull.eval(//
					new Cell("null", Type.Str) //
			);
			assertEquals(ExBool.False, actual);
		}
		{
			ExBool actual = UnaryOp.IsNotNull.eval(//
					new Cell("null", Type.Str) //
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = UnaryOp.IsNotNull.eval(//
					new Cell("null", Type.Null) //
			);
			assertEquals(ExBool.False, actual);
		}
	}

}
