package patsql.synth.filler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.BitSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import patsql.entity.table.BitTable;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.TruePred;
import patsql.synth.filler.RowSearch.Op;

class RowSearchTest {

	@Test
	void test01() {
		ColSchema a1 = new ColSchema("a1", Type.Str);
		ColSchema a2 = new ColSchema("a2", Type.Str);
		ColSchema a3 = new ColSchema("a3", Type.Str);
		Table tmp = new Table(a1, a2, a3);
		tmp.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("C", Type.Str)//
		);
		tmp.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("B", Type.Str)//
		);
		tmp.addRow(//
				new Cell("C", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("A", Type.Str)//
		);
		tmp.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("B", Type.Str)//
		);

		ColSchema b1 = new ColSchema("b1", Type.Str);
		ColSchema b2 = new ColSchema("b2", Type.Str);
		Table out = new Table(b1, b2);
		out.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		out.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);

		RowSearch search = new RowSearch();
		{
			BinaryPred pred = new BinaryPred(a1, BinaryOp.Eq, new Cell("A", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a1, BinaryOp.Eq, new Cell("B", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a3, BinaryOp.Eq, new Cell("A", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a3, BinaryOp.Eq, new Cell("B", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
	}

	@Test
	void testResolveSatisfiedSingleRows01() {
		ColSchema a1 = new ColSchema("a1", Type.Str);
		ColSchema a2 = new ColSchema("a2", Type.Str);
		ColSchema a3 = new ColSchema("a3", Type.Str);
		ColSchema a4 = new ColSchema("a4", Type.Str);
		ColSchema a5 = new ColSchema("a5", Type.Str);

		Table tmp = new Table(a1, a2, a3, a4, a5);
		tmp.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("C", Type.Str), //
				new Cell("P", Type.Str), //
				new Cell("P", Type.Str)//
		);
		tmp.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("P", Type.Str), //
				new Cell("Q", Type.Str)//
		);
		tmp.addRow(//
				new Cell("C", Type.Str), //
				new Cell("X", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("P", Type.Str), //
				new Cell("Q", Type.Str)//
		);
		tmp.addRow(//
				new Cell("C", Type.Str), //
				new Cell("Y", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("P", Type.Str), //
				new Cell("P", Type.Str)//
		);

		ColSchema b1 = new ColSchema("b1", Type.Str);
		ColSchema b2 = new ColSchema("b2", Type.Str);
		Table out = new Table(b1, b2);
		out.addRow(//
				new Cell("A", Type.Str), //
				new Cell("X", Type.Str) //
		);
		out.addRow(//
				new Cell("B", Type.Str), //
				new Cell("Y", Type.Str) //
		);

		RowSearch search = new RowSearch();

		{
			// always TRUE
			Predicate ok = new TruePred();
			BitSet bits = new BitTable(tmp).selection(ok).rowBits;
			search.addPred(ok, bits);
		}
		{
			// r1
			BinaryPred pred = new BinaryPred(a1, BinaryOp.Eq, new Cell("A", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			// r2
			BinaryPred pred = new BinaryPred(a1, BinaryOp.Eq, new Cell("B", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			// r1 and r2
			BinaryPred ok = new BinaryPred(a1, BinaryOp.Eq, new Cell("C", Type.Str));
			BitSet bits = new BitTable(tmp).selection(ok).rowBits;
			search.addPred(ok, bits);
		}
		{
			// r2 and r3
			BinaryPred ok = new BinaryPred(a5, BinaryOp.Eq, new Cell("Q", Type.Str));
			BitSet bits = new BitTable(tmp).selection(ok).rowBits;
			search.addPred(ok, bits);
		}

		{
			List<BitRow> result = search.singleRowsAll();
			assertEquals(5, result.size());
		}

		search.generate(Op.OR);
		System.out.println(search);
		{
			List<BitRow> result = search.singleRowsAll();
			assertEquals(9, result.size());
		}

		search.generate(Op.AND);
		System.out.println(search);
		{
			List<BitRow> result = search.singleRowsAll();
			assertEquals(11, result.size());
		}
	}

}
