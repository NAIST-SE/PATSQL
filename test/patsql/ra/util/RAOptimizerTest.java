package patsql.ra.util;

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
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Distinct;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;
import patsql.ra.operator.TableEnv;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;

class RAOptimizerTest {

	@Test
	void testDeleteUnusedAggCols01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		Table table1 = new Table(c1, c2, c3);
		table1.addRow(//
				new Cell("B", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("98", Type.Dbl)//
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("99", Type.Dbl)//
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("C", Type.Str), //
				new Cell("101", Type.Dbl) //
		);
		table1.addRow(//
				new Cell("C", Type.Str), //
				new Cell("D", Type.Str), //
				new Cell("102", Type.Dbl)//
		);

		TableEnv env = new TableEnv();
		env.put("table1", table1);

		RAOperator b0 = new BaseTable("table1");

		AggColSchema ag1 = new AggColSchema(Agg.Max, c2);
		AggColSchema ag2 = new AggColSchema(Agg.Max, c3);
		AggColSchema ag3 = new AggColSchema(Agg.Min, c2);
		AggColSchema ag4 = new AggColSchema(Agg.Min, c3);

		GroupBy g1 = new GroupBy(b0, //
				new GroupKeys(c1), //
				new Aggregators(ag1, ag2, ag3, ag4)//
		);

		AggColSchema ag5 = new AggColSchema(Agg.Max, ag1);
		AggColSchema ag6 = new AggColSchema(Agg.Min, ag1);

		GroupBy g2 = new GroupBy(g1, //
				new GroupKeys(c1), //
				new Aggregators(ag5, ag6)//
		);
		RAOperator p2 = new Projection(g2, //
				new ColSchema[] { //
						c1, c3, c2, ag5, ag6 //
				}//
		);
		RAOperator p3 = new Distinct(p2);
		RAOperator program = new Sort(p3, //
				new SortKeys(new SortKey(c1, Order.Asc))//
		);

		assertEquals(4, g1.ags.aggColSchemas.length);
		assertEquals(2, g2.ags.aggColSchemas.length);
		program = RAOptimizer.deleteUnusedAggCols(program);
		assertEquals(1, g1.ags.aggColSchemas.length);
		assertEquals(2, g2.ags.aggColSchemas.length);
	}

	@Test
	void testDeleteUnusedColsFromBaseTable01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);
		ColSchema c4 = new ColSchema("c4", Type.Dbl);

		Table table1 = new Table(c1, c2, c3, c4);
		table1.addRow(//
				new Cell("B", Type.Str), //
				new Cell("A", Type.Str), //
				new Cell("98", Type.Dbl), //
				new Cell("98", Type.Dbl)//
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str), //
				new Cell("99", Type.Dbl), //
				new Cell("98", Type.Dbl)//
		);
		table1.addRow(//
				new Cell("A", Type.Str), //
				new Cell("C", Type.Str), //
				new Cell("101", Type.Dbl), //
				new Cell("98", Type.Dbl) //
		);
		table1.addRow(//
				new Cell("C", Type.Str), //
				new Cell("D", Type.Str), //
				new Cell("102", Type.Dbl), //
				new Cell("98", Type.Dbl)//
		);

		BaseTable b0 = new BaseTable("table1");
		b0.renamedCols = new ColSchema[] { //
				c1, c2, c3, c4//
		};

		Selection s1 = new Selection(b0, //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str))//
				)//
		);

		GroupBy g1 = new GroupBy(s1, //
				new GroupKeys(c2), //
				new Aggregators(//
						new AggColSchema(Agg.Max, c4), //
						new AggColSchema(Agg.Min, c1)//
				)//
		);

		RAOperator p2 = new Projection(g1, //
				new ColSchema[] { //
						c1, c2, //
				}//
		);
		RAOperator p3 = new Distinct(p2);
		RAOperator program = new Sort(p3, //
				new SortKeys(new SortKey(c1, Order.Asc))//
		);

		assertEquals(4, b0.renamedCols.length);
		program = RAOptimizer.deleteUnusedColsFromBaseTable(program);
		assertEquals(3, b0.renamedCols.length);
	}

}
