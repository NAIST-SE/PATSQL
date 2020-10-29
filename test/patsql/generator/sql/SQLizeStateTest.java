package patsql.generator.sql;

import org.junit.jupiter.api.Test;

import patsql.generator.sql.query.QSingleColumn;
import patsql.generator.sql.query.QSingleRelation;

class SQLizeStateTest {

	@Test
	void test01() {
		QSingleRelation r = new QSingleRelation("table1");
		SQLizeState state = new SQLizeState(r);
		state.idToQColumn.put(1, new QSingleColumn(r, "c1"));
		state.idToQColumn.put(2, new QSingleColumn(r, "c2"));
		state.idToQColumn.put(3, new QSingleColumn(r, "c3"));

		SQLizeState parentState = state.createSubQuery();
		String str = parentState.query.toString();
		String sql = SQLUtil.reformatSQL(str);
		System.out.println(sql);

		SQLizeState p2 = parentState.createSubQuery();
		System.out.println(SQLUtil.reformatSQL(p2.query.toString()));
	}

}
