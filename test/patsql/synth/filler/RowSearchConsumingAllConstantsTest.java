package patsql.synth.filler;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

import patsql.entity.table.BitTable;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;

class RowSearchConsumingAllConstantsTest {

	@Test
	void test01() {
		ColSchema a1 = new ColSchema("a1", Type.Str);
		ColSchema a2 = new ColSchema("a2", Type.Str);
		ColSchema a3 = new ColSchema("a3", Type.Str);
		Table tmp = new Table(a1, a2, a3);
		tmp.addRow(//
				new Cell("0", Type.Str), //
				new Cell("0", Type.Str), //
				new Cell("C", Type.Str)//
		);
		tmp.addRow(//
				new Cell("0", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("0", Type.Str)//
		);
		tmp.addRow(//
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("0", Type.Str)//
		);
		tmp.addRow(//
				new Cell("A", Type.Str), //
				new Cell("0", Type.Str), //
				new Cell("C", Type.Str)//
		);

		ColSchema b1 = new ColSchema("b1", Type.Str);
		ColSchema b2 = new ColSchema("b2", Type.Str);
		ColSchema b3 = new ColSchema("b3", Type.Str);
		Table out = new Table(b1, b2, b3);
		out.addRow(//
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("0", Type.Str)//
		);
		out.addRow(//
				new Cell("A", Type.Str), //
				new Cell("0", Type.Str), //
				new Cell("C", Type.Str)//
		);

		RowSearchConsumingAllConstants search = new RowSearchConsumingAllConstants(//
				tmp.height(), //
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("C", Type.Str)//
		);

		{
			BinaryPred pred = new BinaryPred(a1, BinaryOp.Eq, new Cell("A", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a1, BinaryOp.NotEq, new Cell("A", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a2, BinaryOp.Eq, new Cell("B", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a2, BinaryOp.NotEq, new Cell("B", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a3, BinaryOp.Eq, new Cell("C", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}
		{
			BinaryPred pred = new BinaryPred(a3, BinaryOp.NotEq, new Cell("C", Type.Str));
			BitSet bits = new BitTable(tmp).selection(pred).rowBits;
			search.addPred(pred, bits);
		}

		search.generateKernel();
		System.out.println(search);

	}

}
