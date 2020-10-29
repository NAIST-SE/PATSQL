package patsql.ra.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import patsql.entity.table.AggColSchema;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

class RAOperatorTest {

	@Test
	void testEval01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1);
		table1.addRow(new Cell("B", Type.Str));
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("A", Type.Str));
		table1.addRow(new Cell("C", Type.Str));
		table1.addRow(new Cell("D", Type.Str));
		table1.addRow(new Cell("D", Type.Str));

		Table table2 = new Table(c2, c3);
		table2.addRow(new Cell("A", Type.Str), new Cell("98", Type.Dbl));
		table2.addRow(new Cell("B", Type.Str), new Cell("99", Type.Dbl));
		table2.addRow(new Cell("C", Type.Str), new Cell("101", Type.Dbl));
		table2.addRow(new Cell("D", Type.Str), new Cell("102", Type.Dbl));

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		RAOperator b0 = new BaseTable("table1");
		RAOperator b1 = new BaseTable("table2");

		RAOperator s1 = new Selection(b1, new Conjunction(//
				new BinaryPred(//
						c3, BinaryOp.NotEq, new Cell("99", Type.Dbl)//
				)//
		));

		RAOperator j1 = new Join(b0, s1, new JoinCond(//
				new JoinKeyPair(c1, c2)//
		));

		RAOperator p1 = new Projection(j1, //
				new ColSchema[] { c1, c3, c2 }//
		);

		RAOperator d1 = new Distinct(p1);

		RAOperator program = new Sort(d1, //
				new SortKeys(new SortKey(c1, Order.Asc))//
		);

		Table result = program.eval(env);
		assertEquals(3, result.width());
		assertEquals(3, result.height());
	}

	@Test
	void testEval02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c4 = new ColSchema("c4", Type.Int);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1, c4);
		table1.addRow(new Cell("B", Type.Str), new Cell("12", Type.Int));
		table1.addRow(new Cell("A", Type.Str), new Cell("8", Type.Int));
		table1.addRow(new Cell("A", Type.Str), new Cell("6", Type.Int));
		table1.addRow(new Cell("C", Type.Str), new Cell("7", Type.Int));
		table1.addRow(new Cell("D", Type.Str), new Cell("10", Type.Int));
		table1.addRow(new Cell("D", Type.Str), new Cell("2", Type.Int));

		Table table2 = new Table(c2, c3);
		table2.addRow(new Cell("A", Type.Str), new Cell("98", Type.Dbl));
		table2.addRow(new Cell("B", Type.Str), new Cell("99", Type.Dbl));
		table2.addRow(new Cell("C", Type.Str), new Cell("101", Type.Dbl));
		table2.addRow(new Cell("D", Type.Str), new Cell("102", Type.Dbl));

		TableEnv env = new TableEnv();
		env.put("table1", table1);
		env.put("table2", table2);

		RAOperator b0 = new BaseTable("table1");
		RAOperator b1 = new BaseTable("table2");

		RAOperator j1 = new Join(b0, b1, new JoinCond(//
				new JoinKeyPair(c1, c2)//
		));

		AggColSchema c5 = new AggColSchema(Agg.Max, c4);
		AggColSchema c6 = new AggColSchema(Agg.Sum, c4); //
		AggColSchema c7 = new AggColSchema(Agg.Avg, c3);//
		RAOperator g1 = new GroupBy(j1, //
				new GroupKeys(c1), //
				new Aggregators(c5, c6, c7)//
		);

		RAOperator p1 = new Projection(g1, //
				new ColSchema[] { c1, c5, c6, c7 }//
		);

		RAOperator program = new Sort(p1, //
				new SortKeys(new SortKey(c5, Order.Desc))//
		);

		Table result = program.eval(env);
		assertEquals(4, result.width());
		assertEquals(4, result.height());
	}

}
