package patsql.generator.sql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.generator.sql.query.Util;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.RAUtils;
import patsql.synth.RASynthesizer;

class SQLPostprocessorTest {

	/**
	 * unused grouped-by column
	 */
	@Test
	@Tag("h2")
	@Tag("synth")
	void test01() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table t = new Table(c1, c2, c3);
		t.addRow(new Cell("a", Type.Str), new Cell("x", Type.Str), new Cell("7", Type.Int));
		t.addRow(new Cell("a", Type.Str), new Cell("y", Type.Str), new Cell("8", Type.Int));
		t.addRow(new Cell("a", Type.Str), new Cell("y", Type.Str), new Cell("4", Type.Int));
		t.addRow(new Cell("b", Type.Str), new Cell("x", Type.Str), new Cell("9", Type.Int));
		t.addRow(new Cell("b", Type.Str), new Cell("x", Type.Str), new Cell("11", Type.Int));
		t.addRow(new Cell("b", Type.Str), new Cell("y", Type.Str), new Cell("10", Type.Int));
		t.addRow(new Cell("c", Type.Str), new Cell("x", Type.Str), new Cell("6", Type.Int));
		NamedTable nt = new NamedTable("table1", t);

		Table tout = new Table(new ColSchema("c4", Type.Int));
		tout.addRow(new Cell("10", Type.Int));

		Example example = new Example(tout, nt);
		System.out.println(example);

		RASynthesizer synth = new RASynthesizer(example, new SynthOption());
		RAOperator result = synth.synthesize();
		assertNotNull(result);

		RAUtils.printTree(result);
		String sql = SQLUtil.generateSQL(result);
		System.out.println(sql);
		Util.checkSQL(sql, tout, nt);

		// -- bad SQL
		// SELECT max(T0.min_c3)
		// FROM (SELECT c1, c2, min(c3) AS min_c3 FROM table1 GROUP BY c1, c2) AS T0

		assertFalse(sql.matches("(?s).*SELECT\\s+c1.*")); // (?s) is the DOTALL flag
		assertFalse(sql.matches("(?s).*c2,\\s*min\\(c3\\).*"));
	}

	/**
	 * unused joined table's column
	 */
	@Test
	@Tag("h2")
	@Tag("synth")
	void test02() {
		ColSchema c1 = new ColSchema("c1", Type.Str);
		ColSchema c2 = new ColSchema("c2", Type.Str);
		ColSchema c3 = new ColSchema("c3", Type.Int);
		Table t = new Table(c1, c2, c3);
		t.addRow(new Cell("a", Type.Str), new Cell("z", Type.Str), new Cell("12", Type.Int));
		t.addRow(new Cell("b", Type.Str), new Cell("y", Type.Str), new Cell("8", Type.Int));
		t.addRow(new Cell("c", Type.Str), new Cell("x", Type.Str), new Cell("9", Type.Int));
		t.addRow(new Cell("d", Type.Str), new Cell("x", Type.Str), new Cell("11", Type.Int));
		t.addRow(new Cell("e", Type.Str), new Cell("y", Type.Str), new Cell("10", Type.Int));
		t.addRow(new Cell("f", Type.Str), new Cell("z", Type.Str), new Cell("6", Type.Int));
		NamedTable nt = new NamedTable("table1", t);

		Table tout = new Table(new ColSchema("c4", Type.Str));
		tout.addRow(new Cell("a", Type.Str));
		tout.addRow(new Cell("d", Type.Str));
		tout.addRow(new Cell("e", Type.Str));

		Example example = new Example(tout, nt);
		System.out.println(example);

		RASynthesizer synth = new RASynthesizer(example, new SynthOption());
		RAOperator result = synth.synthesize();
		assertNotNull(result);

		RAUtils.printTree(result);
		String sql = SQLUtil.generateSQL(result);
		System.out.println(sql);

		// -- bad SQL
		// SELECT T0.c1
		// FROM table1 AS T0
		// JOIN (SELECT c2, max(c3) AS max_c3 FROM table1 GROUP BY c2) AS T1
		// ON T1.max_c3 = T0.c3
		// ORDER BY T0.c1 ASC

		assertFalse(sql.matches("(?s).*SELECT\\s+c2.*")); // (?s) is the DOTALL flag
	}

}
