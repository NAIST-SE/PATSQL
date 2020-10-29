package patsql.generator.sql.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.util.RAUtils;

class SQLSelectionTest {

	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		Selection program = new Selection(//
				bt, //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str))//
				)//
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	@Test
	@Tag("h2")
	void test02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		Selection program = new Selection(//
				bt, //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Eq, new Cell("A", Type.Str)), //
						new BinaryPred(c2, BinaryOp.Eq, new Cell("X", Type.Str))//
				));

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * where only
	 */
	@Test
	@Tag("h2")
	void test03() {
		ColSchema c1 = new ColSchema("c1", Type.Dbl);
		NamedTable nt = new NamedTable("table1", new Table(c1));
		BaseTable bt = Util.toBaseTable(nt);

		Selection program = new Selection(//
				bt, //
				new Conjunction(//
						new BinaryPred(c1, BinaryOp.Geq, new Cell("3.1", Type.Dbl)) //
				));

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		assertTrue(sql.contains("WHERE"));
		assertFalse(sql.contains("HAVING"));
		// TODO needs assertion
	}

	@Test
	@Tag("h2")
	/**
	 * having only
	 */
	void test04() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Dbl);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2));
		BaseTable bt = Util.toBaseTable(nt);

		AggColSchema c3 = new AggColSchema(Agg.Max, c2);
		RAOperator g1 = new GroupBy(//
				bt, //
				new GroupKeys(c1), //
				new Aggregators(c3) //
		);

		Selection s1 = new Selection(//
				g1, //
				new Conjunction(//
						new BinaryPred(c3, BinaryOp.Geq, new Cell("3.1", Type.Dbl)) //
				));

		RAOperator program = new Projection(s1, c1);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		assertFalse(sql.contains("WHERE"));
		assertTrue(sql.contains("HAVING"));
		// TODO needs assertion
	}

	@Test
	@Tag("h2")
	/**
	 * where and having
	 */
	void test05() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Dbl);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2));
		BaseTable bt = Util.toBaseTable(nt);

		Selection s1 = new Selection(//
				bt, //
				new Conjunction(//
						new BinaryPred(c2, BinaryOp.Lt, new Cell("5.0", Type.Dbl)) //
				));

		AggColSchema c3 = new AggColSchema(Agg.Max, c2);
		RAOperator g1 = new GroupBy(//
				s1, //
				new GroupKeys(c1), //
				new Aggregators(c3) //
		);

		Selection s2 = new Selection(//
				g1, //
				new Conjunction(//
						new BinaryPred(c3, BinaryOp.Geq, new Cell("3.1", Type.Dbl)) //
				));

		RAOperator program = new Projection(s2, c1);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		assertTrue(sql.contains("WHERE"));
		assertTrue(sql.contains("HAVING"));
		// TODO needs assertion
	}
}
