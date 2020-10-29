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
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Root;
import patsql.ra.operator.Selection;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.util.RAUtils;

class SQLSubqueryTest {

	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);

		NamedTable nt1 = new NamedTable("table1", new Table(c1));
		BaseTable bt1 = Util.toBaseTable(nt1);

		NamedTable nt2 = new NamedTable("table2", new Table(c2));
		BaseTable bt2 = Util.toBaseTable(nt2);

		NamedTable nt3 = new NamedTable("table3", new Table(c3));
		BaseTable bt3 = Util.toBaseTable(nt3);

		RAOperator j1 = new Join(bt2, bt3, new JoinCond(//
				new JoinKeyPair(c2, c3)//
		));

		RAOperator j2 = new Join(bt1, j1, new JoinCond(//
				new JoinKeyPair(c1, c2)//
		));

		Projection p1 = new Projection(//
				j2, //
				new ColSchema[] { c3 }//
		);

		Root program = new Root(p1);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2, nt3);
		// TODO needs assertion
	}

	/**
	 * double group-by
	 */
	@Test
	@Tag("h2")
	void test02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);

		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		AggColSchema c4 = new AggColSchema(Agg.Max, c2);
		AggColSchema c5 = new AggColSchema(Agg.Max, c3);

		RAOperator g1 = new GroupBy(//
				bt, //
				new GroupKeys(c1), //
				new Aggregators(c4, c5) //
		);

		AggColSchema c6 = new AggColSchema(Agg.Max, c1);
		AggColSchema c7 = new AggColSchema(Agg.Max, c5);

		RAOperator g2 = new GroupBy(//
				g1, //
				new GroupKeys(c4), //
				new Aggregators(c6, c7) //
		);

		RAOperator program = new Projection(g2, new ColSchema[] { c4, c6 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * left join the same table three times
	 */
	@Test
	@Tag("h2")
	void test03() {
		String tableName = "table1";

		ColSchema c11 = new ColSchema("c1", Type.Int);
		ColSchema c12 = new ColSchema("c2", Type.Int);
		BaseTable bt1 = new BaseTable(tableName);
		bt1.renamedCols = new ColSchema[] { c11, c12 };

		ColSchema c21 = new ColSchema("c1", Type.Int);
		ColSchema c22 = new ColSchema("c2", Type.Int);
		BaseTable bt2 = new BaseTable(tableName);
		bt2.renamedCols = new ColSchema[] { c21, c22 };

		ColSchema c31 = new ColSchema("c1", Type.Int);
		ColSchema c32 = new ColSchema("c2", Type.Int);
		BaseTable bt3 = new BaseTable(tableName);
		bt3.renamedCols = new ColSchema[] { c31, c32 };

		NamedTable nt = new NamedTable(tableName, new Table(c11, c12));

		// subquery
		RAOperator j1 = new LeftJoin(bt1, bt2, new JoinCond(//
				new JoinKeyPair(c11, c21)//
		));

		RAOperator j2 = new LeftJoin(bt3, j1, new JoinCond(//
				new JoinKeyPair(c31, c11)//
		));

		RAOperator program = new Projection(j2, new ColSchema[] { c12, c22, c32 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * left join the same table three times (no subquery)
	 */
	@Test
	@Tag("h2")
	void test04() {
		String tableName = "table1";

		ColSchema c11 = new ColSchema("c1", Type.Int);
		ColSchema c12 = new ColSchema("c2", Type.Int);
		BaseTable bt1 = new BaseTable(tableName);
		bt1.renamedCols = new ColSchema[] { c11, c12 };

		ColSchema c21 = new ColSchema("c1", Type.Int);
		ColSchema c22 = new ColSchema("c2", Type.Int);
		BaseTable bt2 = new BaseTable(tableName);
		bt2.renamedCols = new ColSchema[] { c21, c22 };

		ColSchema c31 = new ColSchema("c1", Type.Int);
		ColSchema c32 = new ColSchema("c2", Type.Int);
		BaseTable bt3 = new BaseTable(tableName);
		bt3.renamedCols = new ColSchema[] { c31, c32 };

		NamedTable nt = new NamedTable(tableName, new Table(c11, c12));

		// subquery
		RAOperator j1 = new LeftJoin(bt1, bt2, new JoinCond(//
				new JoinKeyPair(c11, c21)//
		));

		RAOperator j2 = new LeftJoin(j1, bt3, new JoinCond(//
				new JoinKeyPair(c11, c31)//
		));

		RAOperator program = new Projection(j2, new ColSchema[] { c12, c22, c32 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * double group-by with selections
	 */
	@Test
	@Tag("h2")
	void test05() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);

		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		AggColSchema c4 = new AggColSchema(Agg.Max, c2);
		AggColSchema c5 = new AggColSchema(Agg.Max, c3);

		RAOperator s1 = new Selection(bt, new Conjunction(new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str))));

		RAOperator g1 = new GroupBy(//
				s1, //
				new GroupKeys(c1), //
				new Aggregators(c4, c5) //
		);

		RAOperator s2 = new Selection(g1, new Conjunction(new BinaryPred(c1, BinaryOp.Eq, new Cell("B", Type.Str))));

		AggColSchema c6 = new AggColSchema(Agg.Max, c1);
		AggColSchema c7 = new AggColSchema(Agg.Max, c5);

		RAOperator g2 = new GroupBy(//
				s2, //
				new GroupKeys(c4), //
				new Aggregators(c6, c7) //
		);

		RAOperator s3 = new Selection(g2, new Conjunction(new BinaryPred(c4, BinaryOp.Eq, new Cell("C", Type.Str))));

		RAOperator program = new Projection(s3, new ColSchema[] { c4, c6 });

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * join two joined tables
	 */
	@Test
	@Tag("h2")
	void test06() {
		ColSchema c11 = new ColSchema("c1", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c11));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c21 = new ColSchema("c1", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c21));
		BaseTable bt2 = Util.toBaseTable(nt2);

		ColSchema c31 = new ColSchema("c1", Type.Str);
		NamedTable nt3 = new NamedTable("table3", new Table(c31));
		BaseTable bt3 = Util.toBaseTable(nt3);

		ColSchema c41 = new ColSchema("c1", Type.Str);
		NamedTable nt4 = new NamedTable("table4", new Table(c41));
		BaseTable bt4 = Util.toBaseTable(nt4);

		RAOperator j1 = new Join(bt1, bt2, new JoinCond(new JoinKeyPair(c11, c21)));

		RAOperator j2 = new Join(bt3, bt4, new JoinCond(new JoinKeyPair(c31, c41)));

		RAOperator j3 = new Join(j1, j2, new JoinCond(new JoinKeyPair(c11, c31)));

		RAOperator program = new Projection(j3, c11);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2, nt3, nt4);
		// TODO needs assertion
	}

	/**
	 * join two grouped tables
	 */
	@Test
	@Tag("h2")
	void test07() {
		ColSchema c11 = new ColSchema("c1", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c11));
		BaseTable bt1 = Util.toBaseTable(nt1);

		AggColSchema ac11 = new AggColSchema(Agg.Max, c11);
		RAOperator g1 = new GroupBy(bt1, new GroupKeys(), new Aggregators(ac11));

		ColSchema c21 = new ColSchema("c1", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c21));
		BaseTable bt2 = Util.toBaseTable(nt2);

		AggColSchema ac21 = new AggColSchema(Agg.Max, c21);
		GroupBy g2 = new GroupBy(bt2, new GroupKeys(), new Aggregators(ac21));

		RAOperator j1 = new Join(g1, g2, new JoinCond(new JoinKeyPair(ac11, ac21)));

		RAOperator program = new Projection(j1, ac11, ac21);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2);
		// TODO needs assertion
	}

}