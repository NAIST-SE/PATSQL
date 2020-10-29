package patsql.ra.predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.Cell;
import patsql.entity.table.Type;

class BinaryOpTest {

	@Test
	void test01() {
		{
			ExBool actual = BinaryOp.Eq.eval(//
					new Cell("12", Type.Int), //
					new Cell("12", Type.Int)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Eq.eval(//
					new Cell("12", Type.Int), //
					new Cell("11", Type.Int)//
			);
			assertEquals(ExBool.False, actual);
		}
		{
			ExBool actual = BinaryOp.Eq.eval(//
					new Cell("12.002", Type.Dbl), //
					new Cell("12.002", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Eq.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("12.00002", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Eq.eval(//
					new Cell("00100.000", Type.Dbl), //
					new Cell("100.000", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Gt.eval(//
					new Cell("13", Type.Int), //
					new Cell("12", Type.Int)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Gt.eval(//
					new Cell("12.001", Type.Dbl), //
					new Cell("12.000", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Gt.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("12.000", Type.Dbl)//
			);
			assertEquals(ExBool.False, actual);
		}
		{
			ExBool actual = BinaryOp.Gt.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("12.001", Type.Dbl)//
			);
			assertEquals(ExBool.False, actual);
		}
		{
			ExBool actual = BinaryOp.Gt.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("9.000", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Geq.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("9.000", Type.Dbl)//
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Geq.eval(//
					new Cell("12.000", Type.Dbl), //
					new Cell("12.000", Type.Dbl) //
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Lt.eval(//
					new Cell("9.000", Type.Dbl), //
					new Cell("12.000", Type.Dbl) //
			);
			assertEquals(ExBool.True, actual);
		}
		{
			ExBool actual = BinaryOp.Lt.eval(//
					new Cell("19.000", Type.Dbl), //
					new Cell("12.000", Type.Dbl) //
			);
			assertEquals(ExBool.False, actual);
		}

	}

}
