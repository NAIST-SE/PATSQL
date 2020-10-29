package patsql.ra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

class UtilsTest {

	@Test
	void testLoadTableFromFile() {
		String path = "examples/101_input.csv";
		File file = new File(path);
		Table table = Utils.loadTableFromFile(file);

		assertEquals(5, table.height());
		assertEquals(10, table.width());
	}

	@Test
	void testGuessInt() {
		Table table = Utils.parseTable( //
				"c1:Number", //
				"1", //
				"2", //
				"3");
		ColSchema[] schema = table.schema();
		assertEquals(Type.Int, schema[0].type);
	}

	@Test
	void testGuessDbl() {
		Table table = Utils.parseTable( //
				"c1:Number", //
				"1", //
				"2.0", //
				"3");
		ColSchema[] schema = table.schema();
		assertEquals(Type.Dbl, schema[0].type);
	}

	@Test
	void testWhitespaceNull() {
		Table table = Utils.parseTable( //
				"c1:Str", //
				"a", //
				" ", //
				"z");
		Column column = table.columns[0];
		assertEquals(Type.Null, column.cell(1).type);
	}

}
