package patsql.generator.sql.query;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Sort;
import patsql.ra.util.RAUtils;

class SQLSortTest {

	/**
	 * single key, ASC
	 */
	@Test
	@Tag("h2")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		RAOperator program = new Sort(//
				bt, //
				new SortKeys(new SortKey(c1, Order.Asc))//
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

	/**
	 * two keys, DESC, ASC
	 */
	@Test
	@Tag("h2")
	void test03() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		NamedTable nt = new NamedTable("table1", new Table(c1, c2, c3));
		BaseTable bt = Util.toBaseTable(nt);

		RAOperator program = new Sort(//
				bt, //
				new SortKeys(//
						new SortKey(c1, Order.Desc), //
						new SortKey(c3, Order.Asc)//
				)//
		);

		RAUtils.printTree(program);
		String sql = SQLUtil.generateSQL(program);
		System.out.println(sql);
		Util.checkSQLSyntax(sql, nt);
		// TODO needs assertion
	}

}
