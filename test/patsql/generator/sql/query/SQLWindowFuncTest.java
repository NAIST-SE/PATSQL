package patsql.generator.sql.query;

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
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.RAUtils;
import patsql.synth.RASynthesizer;

class SQLWindowFuncTest {

	private static void checkNotNull(SynthOption option, Table outTable, Table... inTables) {
		NamedTable[] ins = new NamedTable[inTables.length];
		for (int i = 0; i < inTables.length; i++) {
			ins[i] = new NamedTable("table" + i, inTables[i]);
		}
		Example example = new Example(outTable, ins);
		System.out.println(example);
		System.out.println(option);

		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();
		assertNotNull(result);

		RAUtils.printTree(result);
		String sql = SQLUtil.generateSQL(result);
		System.out.println(sql);
		Util.checkSQL(sql, outTable, ins);
	}

	@Test
	@Tag("h2")
	void test01() {
		Table inTable1 = new Table(new ColSchema("grp", Type.Str));
		inTable1.addRow(new Cell("a", Type.Str));
		inTable1.addRow(new Cell("b", Type.Str));
		Table inTable2 = new Table(new ColSchema("grp", Type.Str), new ColSchema("val", Type.Int));
		inTable2.addRow(new Cell("a", Type.Str), new Cell("1", Type.Int));
		inTable2.addRow(new Cell("a", Type.Str), new Cell("2", Type.Int));
		inTable2.addRow(new Cell("b", Type.Str), new Cell("3", Type.Int));
		inTable2.addRow(new Cell("b", Type.Str), new Cell("4", Type.Int));
		Table outTable = new Table(new ColSchema("grp", Type.Str), new ColSchema("val", Type.Int),
				new ColSchema("sum", Type.Int));
		outTable.addRow(new Cell("a", Type.Str), new Cell("1", Type.Int), new Cell("1", Type.Int));
		outTable.addRow(new Cell("a", Type.Str), new Cell("2", Type.Int), new Cell("3", Type.Int));
		outTable.addRow(new Cell("b", Type.Str), new Cell("3", Type.Int), new Cell("3", Type.Int));
		outTable.addRow(new Cell("b", Type.Str), new Cell("4", Type.Int), new Cell("7", Type.Int));
		SynthOption option = new SynthOption();
		checkNotNull(option, outTable, inTable1, inTable2);
	}

	@Test
	@Tag("h2")
	void test02() {
		Table inTable2 = new Table(new ColSchema("grp", Type.Str), new ColSchema("val", Type.Int));
		inTable2.addRow(new Cell("a", Type.Str), new Cell("1", Type.Int));
		inTable2.addRow(new Cell("a", Type.Str), new Cell("3", Type.Int));
		inTable2.addRow(new Cell("b", Type.Str), new Cell("7", Type.Int));
		inTable2.addRow(new Cell("b", Type.Str), new Cell("11", Type.Int));
		Table outTable = new Table(new ColSchema("sum", Type.Int));
		outTable.addRow(new Cell("3", Type.Int));
		outTable.addRow(new Cell("14", Type.Int));
		SynthOption option = new SynthOption();
		checkNotNull(option, outTable, inTable2);
	}

}
