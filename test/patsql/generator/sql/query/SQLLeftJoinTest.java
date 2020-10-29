package patsql.generator.sql.query;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.LeftJoin;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.util.RAUtils;

class SQLLeftJoinTest {

	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c1));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c2 = new ColSchema("B", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c2));
		BaseTable bt2 = Util.toBaseTable(nt2);

		LeftJoin program = new LeftJoin(//
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

	@Test
	@Tag("h2")
	void test02() {
		ColSchema c1 = new ColSchema("A", Type.Str);
		ColSchema c2 = new ColSchema("B", Type.Str);
		NamedTable nt1 = new NamedTable("table1", new Table(c1, c2));
		BaseTable bt1 = Util.toBaseTable(nt1);

		ColSchema c3 = new ColSchema("C", Type.Str);
		ColSchema c4 = new ColSchema("D", Type.Str);
		ColSchema c5 = new ColSchema("E", Type.Str);
		NamedTable nt2 = new NamedTable("table2", new Table(c3, c4, c5));
		BaseTable bt2 = Util.toBaseTable(nt2);

		LeftJoin program = new LeftJoin(//
				bt1, //
				bt2, //
				new JoinCond(//
						new JoinKeyPair(c2, c4), //
						new JoinKeyPair(c1, c3) //
				) //
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt1, nt2);
		// TODO needs assertion
	}

}
