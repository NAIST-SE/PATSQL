package patsql.synth.benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.synth.Debug;
import patsql.synth.RASynthesizer;

public class ScalabilityTest {

	{
		Debug.isDebugMode = false;
	}
	private int[] ws = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 40, 50, 100, 150, 200, 250, 300 };

	@Test
	@Tag("synth")
	void testProj10Col() {
		int h = 10;
		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = inTable; // same
			SynthOption option = new SynthOption();
			// writeToFile("P10_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("P10, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testProj100Col() {
		int h = 100;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = inTable; // same
			SynthOption option = new SynthOption();
			// writeToFile("P100_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("P100, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testProj1000Col() {
		int h = 1000;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = inTable; // same
			SynthOption option = new SynthOption();
			// writeToFile("P1000_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("P1000, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testSP10Col() {
		int h = 10;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = randomTable(w, 1);
			SynthOption option = new SynthOption(//
					new Cell("v00", Type.Str)//
			);
			// writeToFile("SP10_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("SP10, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testSP100Col() {
		int h = 100;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = randomTable(w, 1);
			SynthOption option = new SynthOption(//
					new Cell("v00", Type.Str)//
			);
			// writeToFile("SP100_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("SP100, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testSP1000Col() {
		int h = 1000;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = randomTable(w, 1);
			SynthOption option = new SynthOption(//
					new Cell("v00", Type.Str)//
			);
			// writeToFile("SP1000_" + w, option, outTable, inTable);

			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("SP1000, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testPG10Col() {
		int h = 10;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = new Table(new ColSchema("cout", Type.Int));
			outTable.addRow(new Cell(Integer.toString(h), Type.Int));
			SynthOption option = new SynthOption();
			// writeToFile("PG10_" + w, option, outTable, inTable);
			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("PG10, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testPG100Col() {
		int h = 100;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = new Table(new ColSchema("cout", Type.Int));
			outTable.addRow(new Cell(Integer.toString(h), Type.Int));
			SynthOption option = new SynthOption();
			// writeToFile("PG100_" + w, option, outTable, inTable);
			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("PG100, " + w + ", " + (end - start));
		}
	}

	@Test
	@Tag("synth")
	void testPG1000Col() {
		int h = 1000;

		System.out.println("type, w, time");
		for (int w : ws) {
			Table inTable = randomTable(w, h);
			Table outTable = new Table(new ColSchema("cout", Type.Int));
			outTable.addRow(new Cell(Integer.toString(h), Type.Int));
			SynthOption option = new SynthOption();
			// writeToFile("PG1000_" + w, option, outTable, inTable);
			long start = System.currentTimeMillis();
			synth(option, outTable, inTable);
			long end = System.currentTimeMillis();
			System.out.println("PG1000, " + w + ", " + (end - start));
		}
	}

	private Table randomTable(int width, int heigth) {
		ColSchema[] sc = new ColSchema[width];
		for (int w = 0; w < sc.length; w++)
			sc[w] = new ColSchema("c" + w, Type.Str);
		Table table = new Table(sc);

		for (int h = 0; h < heigth; h++) {
			Cell[] row = new Cell[width];
			for (int w = 0; w < width; w++)
				row[w] = new Cell("v" + h + "" + w, Type.Str);
			table.addRow(row);
		}
		return table;
	}

	private void synth(SynthOption option, Table outTable, Table... inTables) {
		NamedTable[] ins = new NamedTable[inTables.length];
		for (int i = 0; i < inTables.length; i++) {
			ins[i] = new NamedTable("table" + i, inTables[i]);
		}
		Example example = new Example(outTable, ins);
		RASynthesizer synth = new RASynthesizer(example, option);
		// RAOperator result =
		synth.synthesize(111 * 1000);
		// String sql = SQLUtil.generateSQL(result);
		// System.out.println(sql);
	}

	private void writeToFile(String fileName, SynthOption option, Table outTable, Table... inTables) {
		try {
			File file = new File("scythe/" + fileName + ".txt");
			file.createNewFile();
			PrintWriter w = new PrintWriter(new FileWriter(file));

			w.println("#input\n");
			Table inTable = inTables[0];
			w.println(tableToCsv(inTable));

			w.println("#output\n");
			w.println(tableToCsv(outTable));

			w.println("#constraint\n");
			String agg = "";
			if (fileName.contains("G"))
				agg = " \"count\" ";

			if (option.extCells.length > 0) {
				w.println("{\n" //
						+ "	\"constants\": [ \"" + option.extCells[0] + "\" ],\n"//
						+ "	\"aggregation_functions\": [" + agg + "]\n" //
						+ "}");
			} else {
				w.println("{\n" //
						+ "	\"constants\": [],\n"//
						+ "	\"aggregation_functions\": [" + agg + "]\n" //
						+ "}");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String tableToCsv(Table table) {
		StringBuilder ret = new StringBuilder();
		// header row
		List<String> head = new ArrayList<>();
		for (ColSchema sc : table.schema())
			head.add(sc.name);
		ret.append(String.join(", ", head));
		ret.append("\n");

		// rows
		for (int i = 0; i < table.height(); i++) {
			List<String> cells = new ArrayList<>();
			for (Cell r : table.row(i))
				cells.add(r.value);
			ret.append(String.join(", ", cells));
			ret.append("\n");
		}
		return ret.toString();
	}

}
