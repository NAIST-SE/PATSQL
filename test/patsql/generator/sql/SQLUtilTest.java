package patsql.generator.sql;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static patsql.generator.sql.SQLUtil.reformatStringAgg;
import static patsql.generator.sql.SQLUtil.reformatWindowFunc;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.junit.jupiter.api.Test;

class SQLUtilTest {

	private static String formatSQL(String sql) {
		String str = new BasicFormatterImpl().format(sql);
		return str.replaceAll("\n    ", "");// remove left padding.
	}

	@Test
	void testReformatWindowFunc01() {
		String sql = "SELECT rank() OVER (PARTITION BY t1.a, t1.b, t1.c "
				+ "ORDER BY t1.x DESC, t1.y ASC, t1.z DESC) FROM t1";
		String str = formatSQL(sql);
		str = reformatWindowFunc(str);
		boolean res = str
				.contains("rank() OVER (PARTITION BY t1.a, t1.b, t1.c ORDER BY t1.x DESC, t1.y ASC, t1.z DESC)");
		if (!res) {
			System.err.println(str);
		}
		assertTrue(res);
	}

	@Test
	void testReformatWindowFunc02() {
		String sql = "SELECT rank() OVER (PARTITION BY t1.a, t1.b, t1.c) FROM t1";
		String str = formatSQL(sql);
		str = reformatWindowFunc(str);
		boolean res = str.contains("rank() OVER (PARTITION BY t1.a, t1.b, t1.c)");
		if (!res) {
			System.err.println(str);
		}
		assertTrue(res);
	}

	@Test
	void testReformatWindowFunc03() {
		String sql = "SELECT rank() OVER (ORDER BY t1.x DESC, t1.y ASC, t1.z DESC) FROM t1";
		String str = formatSQL(sql);
		str = reformatWindowFunc(str);
		boolean res = str.contains("rank() OVER (ORDER BY t1.x DESC, t1.y ASC, t1.z DESC)");
		if (!res) {
			System.err.println(str);
		}
		assertTrue(res);
	}

	@Test
	void testReformatWindowFunc04() {
		String sql = "SELECT rank() OVER () FROM t1";
		String str = formatSQL(sql);
		str = reformatWindowFunc(str);
		boolean res = str.contains("rank() OVER ()");
		if (!res) {
			System.err.println(str);
		}
		assertTrue(res);
	}

	@Test
	void testReformatStringAgg01() {
		String sql = "SELECT T0.c1, string_agg(T0.c2, ' ') FROM input1 AS T0";
		String str = formatSQL(sql);
		str = reformatStringAgg(str);
		boolean res = str.contains("string_agg(T0.c2, ' ')");
		if (!res) {
			System.err.println(str);
		}
		assertTrue(res);
	}

}
