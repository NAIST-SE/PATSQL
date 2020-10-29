package patsql.generator.sql.query;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Projection;
import patsql.ra.operator.Root;
import patsql.ra.util.RAUtils;

class SQLRootTest {

	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Str);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		Projection p1 = new Projection(//
				bt, //
				new ColSchema[] { c3, c1 }//
		);

		Root program = new Root(p1);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

}
