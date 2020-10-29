package patsql.generator.sql.query;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Join;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.util.RAUtils;

class SQLJoinTest {

	/**
	 * simple
	 */
	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);

		NamedTable nt1 = new NamedTable("table1", new Table(c1));
		BaseTable bt1 = Util.toBaseTable(nt1);

		NamedTable nt2 = new NamedTable("table2", new Table(c2, c3));
		BaseTable bt2 = Util.toBaseTable(nt2);

		Join program = new Join(//
				bt1, //
				bt2, //
				new JoinCond(new JoinKeyPair(c1, c2)) //
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2);
		// TODO needs assertion
	}

	/**
	 * composite-key
	 */
	@Test
	@Tag("h2")
	void test03() {
		ColSchema c11 = new ColSchema("c11", Type.Str);
		ColSchema c12 = new ColSchema("c12", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c11, c12));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c21 = new ColSchema("c21", Type.Str);
		ColSchema c22 = new ColSchema("c22", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c21, c22));
		BaseTable bt2 = Util.toBaseTable(nt2);

		Join program = new Join(//
				bt1, //
				bt2, //
				new JoinCond(//
						new JoinKeyPair(c11, c21), //
						new JoinKeyPair(c12, c22)//
				) //
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2);
		// TODO needs assertion
	}

	/**
	 * three tables.
	 */
	@Test
	@Tag("h2")
	void test04() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c1));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Dbl);
		NamedTable nt2 = new NamedTable("table2", new Table(c2, c3));
		BaseTable bt2 = Util.toBaseTable(nt2);

		ColSchema c4 = new ColSchema("c4", Type.Str);
		ColSchema c5 = new ColSchema("c5", Type.Str);
		NamedTable nt3 = new NamedTable("table3", new Table(c4, c5));
		BaseTable bt3 = Util.toBaseTable(nt3);

		Join program = new Join(//
				new Join(//
						bt1, //
						bt2, //
						new JoinCond(new JoinKeyPair(c1, c2)) //
				), //
				bt3, //
				new JoinCond(new JoinKeyPair(c1, c4)) //
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2, nt3);
		// TODO needs assertion
	}
}
