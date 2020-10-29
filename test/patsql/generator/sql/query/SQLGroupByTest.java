package patsql.generator.sql.query;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.agg.Agg;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;
import patsql.ra.util.RAUtils;

class SQLGroupByTest {

	/**
	 * single key + selections
	 */
	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);

		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		AggColSchema c4 = new AggColSchema(Agg.Max, c3);
		AggColSchema c5 = new AggColSchema(Agg.Min, c3);
		AggColSchema c6 = new AggColSchema(Agg.Sum, c3);
		AggColSchema c7 = new AggColSchema(Agg.Avg, c3);

		RAOperator g1 = new GroupBy(//
				bt, //
				new GroupKeys(c1), //
				new Aggregators(c4, c5, c6, c7) //
		);

		RAOperator s1 = new Selection(g1, new Conjunction(//
				new UnaryPred(c4, UnaryOp.IsNull), //
				new UnaryPred(c5, UnaryOp.IsNotNull), //
				new BinaryPred(c6, BinaryOp.Eq, new Cell("99", Type.Int))));

		RAOperator program = new Projection(s1, new ColSchema[] { c1, c4, c5, c6, c7 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * 2 keys
	 */
	@Test
	@Tag("h2")
	void test02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);

		AggColSchema c4 = new AggColSchema(Agg.Max, c3);
		AggColSchema c5 = new AggColSchema(Agg.Min, c3);
		AggColSchema c6 = new AggColSchema(Agg.Sum, c3);
		AggColSchema c7 = new AggColSchema(Agg.Avg, c3);

		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		RAOperator g1 = new GroupBy(//
				bt, //
				new GroupKeys(c1, c2), //
				new Aggregators(c4, c5, c6, c7) //
		);

		RAOperator program = new Projection(g1, new ColSchema[] { c1, c2, c4, c5, c6, c7 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * grouping joined table
	 */
	@Test
	@Tag("h2")
	void test03() {
		ColSchema c11 = new ColSchema("c1", Type.Str);
		ColSchema c12 = new ColSchema("c2", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c11, c12));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c21 = new ColSchema("c1", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c21));
		BaseTable bt2 = Util.toBaseTable(nt2);

		RAOperator j1 = new Join(bt1, bt2, new JoinCond(new JoinKeyPair(c11, c21)));

		AggColSchema ac12 = new AggColSchema(Agg.Max, c12);
		RAOperator g1 = new GroupBy(j1, new GroupKeys(), new Aggregators(ac12));

		RAOperator program = new Projection(g1, new ColSchema[] { ac12 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2);
		// TODO needs assertion
	}

}
