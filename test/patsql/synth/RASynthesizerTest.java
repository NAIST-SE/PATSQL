package patsql.synth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Disabled;
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
import patsql.ra.util.Utils;

class RASynthesizerTest {

	private void checkNotNullTop5(SynthOption option, Table outTable, Table... inTables) {
		Debug.isDebugMode = false;

		NamedTable[] ins = new NamedTable[inTables.length];
		for (int i = 0; i < inTables.length; i++) {
			ins[i] = new NamedTable("table" + i, inTables[i]);
		}
		Example example = new Example(outTable, ins);
		System.out.println(example);
		System.out.println(option);

		RASynthesizer synth = new RASynthesizer(example, option);
		List<RAOperator> results = synth.synthesizeTop5();
		assertNotNull(results);

		for (int i = 0; i < results.size(); i++) {
			RAOperator result = results.get(i);
			// RAUtils.printTree(result);
			String sql = SQLUtil.generateSQL(result);
			System.out.print("\n[No. " + (i + 1) + "]");
			System.out.println(sql);
		}
	}

	@Test
	void ExampleForSQLSynthesis() {
		// Create the input table by giving the schema and rows
		Table inTable = new Table(//
				new ColSchema("col1", Type.Str), //
				new ColSchema("col2", Type.Str), //
				new ColSchema("col3", Type.Int), //
				new ColSchema("col4", Type.Date)//
		);
		inTable.addRow(//
				new Cell("A1", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("123", Type.Int), //
				new Cell("20200709", Type.Date)//
		);
		inTable.addRow(//
				new Cell("A2", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("345", Type.Int), //
				new Cell("null", Type.Null)// the value doesn't matter if null
		);
		inTable.addRow(//
				new Cell("A3", Type.Str), //
				new Cell("XXX", Type.Str), //
				new Cell("567", Type.Int), //
				new Cell("20200713", Type.Date)//
		);

		// Create the output table
		Table outTable = new Table(//
				new ColSchema("col1", Type.Str), //
				new ColSchema("col3", Type.Int) //
		);
		outTable.addRow(new Cell("A1", Type.Str), new Cell("123", Type.Int));
		outTable.addRow(new Cell("A3", Type.Str), new Cell("567", Type.Int));

		// Specify used constants in the query as a hint.
		SynthOption option = new SynthOption(//
				new Cell("A2", Type.Str)//
		);

		// Give a name to the input table. The name is used in the resulting query.
		NamedTable namedInputTable = new NamedTable("input_table", inTable);

		// Execute synthesis
		Example example = new Example(outTable, namedInputTable);
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);

		// Print the given example and synthesis result.
		System.out.println("** INPUT AND OUTPUT TABLES **");
		System.out.println(example);
		System.out.println("** FOUND PROGRAM IN DSL**");
		RAUtils.printTree(result);
		System.out.println("** SOLUTION QUERY **");
		System.out.println(sql);
	}

	@Test
	void TestSynthesizer001() {
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/output1.csv");
		SynthOption option = new SynthOption(//
				new Cell("35", Type.Int)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	@Test
	void TestSynthesizer002() {
		Table inTable1 = Utils.loadTableFromFile("examples/input2.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/input1.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	@Test
	void TestSynthesizer003() {
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/output2.csv");
		SynthOption option = new SynthOption(//
				new Cell("35", Type.Int), //
				new Cell("75", Type.Int)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Same as case 1, only more columns.
	 */
	@Test
	void TestSynthesizer004() {
		Table inTable1 = Utils.loadTableFromFile("examples/input2.csv");
		Table outTable = Utils.loadTableFromFile("examples/output1.csv");
		SynthOption option = new SynthOption(//
				new Cell("35", Type.Int)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * StrEquals test.
	 */
	@Test
	void TestSynthesizer005() {
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/output3.csv");
		SynthOption option = new SynthOption(//
				new Cell("Japan", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Join test.
	 */
	@Test
	void TestSynthesizer006() {
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/input3.csv");
		Table outTable = Utils.loadTableFromFile("examples/output4.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * Join test with select.
	 */
	@Test
	void TestSynthesizer007() {
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/input3.csv");
		Table outTable = Utils.loadTableFromFile("examples/output5.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * Choose 5 columns out of 10 (no permute)
	 */
	@Test
	void TestSynthesizer008() {
		Table inTable1 = Utils.loadTableFromFile("examples/input4.csv");
		Table outTable = Utils.loadTableFromFile("examples/output6.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Choose 8 columns out of 10 (no permute)
	 */
	@Test
	void TestSynthesizer009() {
		Table inTable1 = Utils.loadTableFromFile("examples/input4.csv");
		Table outTable = Utils.loadTableFromFile("examples/output7.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Choose 5 columns out of 10 (permute)
	 */
	@Test
	void TestSynthesizer010() {
		Table inTable1 = Utils.loadTableFromFile("examples/input4.csv");
		Table outTable = Utils.loadTableFromFile("examples/output8.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Choose 7 columns out of 10 (permute)
	 */
	@Test
	void TestSynthesizer011() {
		Table inTable1 = Utils.loadTableFromFile("examples/input4.csv");
		Table outTable = Utils.loadTableFromFile("examples/output9.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Choose 8 columns out of 10 (permute)
	 */
	@Test
	void TestSynthesizer012() {
		Table inTable1 = Utils.loadTableFromFile("examples/input4.csv");
		Table outTable = Utils.loadTableFromFile("examples/output10.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Date type and DateEquals.
	 */
	@Test
	void TestSynthesizer014() {
		Table inTable1 = Utils.loadTableFromFile("examples/input6.csv");
		Table outTable = Utils.loadTableFromFile("examples/output11.csv");
		SynthOption option = new SynthOption(//
				new Cell("19900101", Type.Date)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for DateGeq and order by date type.
	 */
	@Test
	void TestSynthesizer015() {
		Table inTable1 = Utils.loadTableFromFile("examples/input11_2.csv");
		Table outTable = Utils.loadTableFromFile("examples/output11_2.csv");
		SynthOption option = new SynthOption(//
				new Cell("20000101", Type.Date)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for ORDER BY, ASC.
	 */
	@Test
	void TestSynthesizer101() {
		Table inTable1 = Utils.loadTableFromFile("examples/101_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/101_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for ORDER BY, DESC.
	 */
	@Test
	void TestSynthesizer102() {
		Table inTable1 = Utils.loadTableFromFile("examples/102_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/102_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for ORDER BY, ASC. The key column is of string type.
	 */
	@Test
	void TestSynthesizer104() {
		Table inTable1 = Utils.loadTableFromFile("examples/104_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/104_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, count with a NIL key.
	 */
	@Test
	void TestSynthesizer120() {
		Table inTable1 = Utils.loadTableFromFile("examples/120_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/120_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, max with a NIL key.
	 */
	@Test
	void TestSynthesizer121() {
		Table inTable1 = Utils.loadTableFromFile("examples/121_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/121_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, max with a NIL key.
	 */
	@Test
	void TestSynthesizer122() {
		Table inTable1 = Utils.loadTableFromFile("examples/122_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/122_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, count with a key grouping.
	 */
	@Test
	void TestSynthesizer123() {
		Table inTable1 = Utils.loadTableFromFile("examples/123_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/123_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, max with a key grouping.
	 */
	@Test
	void TestSynthesizer124() {
		Table inTable1 = Utils.loadTableFromFile("examples/124_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/124_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, min with a key grouping.
	 */
	@Test
	void TestSynthesizer125() {
		Table inTable1 = Utils.loadTableFromFile("examples/125_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/125_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, min, count, max with a key grouping.
	 */
	@Test
	void TestSynthesizer126() {
		Table inTable1 = Utils.loadTableFromFile("examples/126_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/126_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY with large tables.
	 */
	@Test
	void TestSynthesizer127() {
		Table inTable1 = Utils.loadTableFromFile("examples/127_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/127_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, min, max for String types.
	 */
	@Test
	void TestSynthesizer128() {
		Table inTable1 = Utils.loadTableFromFile("examples/128_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/128_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for GROUP BY, sum, countdistinct, avg with a key grouping.
	 */
	@Test
	void TestSynthesizer130() {
		Table inTable1 = Utils.loadTableFromFile("examples/130_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/130_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for avg(3,3,4) = 3.333.
	 */
	@Test
	void TestSynthesizer131() {
		Table inTable1 = Utils.loadTableFromFile("examples/131_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/131_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for JOIN using a column having NULL value.
	 */
	@Test
	void TestSynthesizer134() {
		Table inTable1 = Utils.loadTableFromFile("examples/134_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/134_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/134_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * test case for ASC ORDER BY a column having NULL value.
	 */
	@Test
	void TestSynthesizer135() {
		Table inTable1 = Utils.loadTableFromFile("examples/135_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/135_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for DESC ORDER BY a column having NULL value.
	 */
	@Test
	void TestSynthesizer136() {
		Table inTable1 = Utils.loadTableFromFile("examples/136_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/136_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for DISTINCT.
	 */
	@Test
	void TestSynthesizer137() {
		Table inTable1 = Utils.loadTableFromFile("examples/137_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/137_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for NULL and NOT NULL.
	 */
	@Test
	void TestSynthesizer138() {
		Table inTable1 = Utils.loadTableFromFile("examples/138_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/138_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for LEFT JOIN.
	 */
	@Test
	void TestSynthesizer139() {
		Table inTable1 = Utils.loadTableFromFile("examples/139_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/139_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/139_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * test case for sub query, max, NumEquals
	 */
	@Test
	void TestSynthesizer140() {
		Table inTable1 = Utils.loadTableFromFile("examples/140_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/140_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for sub query, min, StrEquals
	 */
	@Test
	void TestSynthesizer141() {
		Table inTable1 = Utils.loadTableFromFile("examples/141_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/141_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for sub query, max, DateEquals
	 */
	@Test
	void TestSynthesizer142() {
		Table inTable1 = Utils.loadTableFromFile("examples/142_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/142_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for sub query (self join), multiple key join.
	 */
	@Test
	void TestSynthesizer143() {
		Table inTable1 = Utils.loadTableFromFile("examples/143_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/143_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * test case for join by 3 key column pairs.
	 */
	@Test
	void TestSynthesizer170() {
		Table inTable1 = Utils.loadTableFromFile("examples/170_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/170_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/170_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * test case for join by 3 key column pairs with 3 tables.
	 */
	@Test
	void TestSynthesizer171() {
		Table inTable1 = Utils.loadTableFromFile("examples/171_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/171_2_input.csv");
		Table inTable3 = Utils.loadTableFromFile("examples/171_3_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/171_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2, inTable3);
	}

	/**
	 * Complicated case. Join 3 tables, 3 Filters, Sort, Select
	 */
	@Test
	void TestSynthesizer200() {
		Table inTable1 = Utils.loadTableFromFile("examples/200_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/200_2_input.csv");
		Table inTable3 = Utils.loadTableFromFile("examples/200_3_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/200_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("A", Type.Str), //
				new Cell("YY", Type.Str), //
				new Cell("AAA", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1, inTable2, inTable3);
	}

	/**
	 * Complicated case. Join 3 tables, 5 Filters, Sort, Select
	 */
	@Disabled("take much time finding top5")
	@Test
	void TestSynthesizer201() {
		Table inTable1 = Utils.loadTableFromFile("examples/200_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/200_2_input.csv");
		Table inTable3 = Utils.loadTableFromFile("examples/200_3_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/201_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("A", Type.Str), //
				new Cell("YY", Type.Str), //
				new Cell("200", Type.Int), //
				new Cell("300", Type.Int), //
				new Cell("AAA", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1, inTable2, inTable3);
	}

	/**
	 * 10 filters
	 */
	@Disabled("explosion by OR")
	@Test
	void TestSynthesizer202() {
		Table inTable1 = Utils.loadTableFromFile("examples/202_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/202_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("T1", Type.Str), //
				new Cell("T2", Type.Str), //
				new Cell("F3", Type.Str), //
				new Cell("F4", Type.Str), //
				new Cell("T5", Type.Str), //
				new Cell("F6", Type.Str), //
				new Cell("T7", Type.Str), //
				new Cell("T8", Type.Str), //
				new Cell("F9", Type.Str), //
				new Cell("T10", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * 5 filters without indexes
	 */
	@Disabled("explosion by OR")
	@Test
	void TestSynthesizer203() {
		Table inTable1 = Utils.loadTableFromFile("examples/203_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/203_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("F", Type.Str) //
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Motivating example for the paper "Synthesizing Highly Expressive SQL Queries
	 * from Input-Output Examples".
	 */
	@Test
	@Disabled("OR")
	void TestSynthesizer204() {
		Table inTable1 = Utils.loadTableFromFile("examples/204_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/204_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/204_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("12/25", Type.Date), //
				new Cell("12/24", Type.Date)//
		);
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * self join with duplication.
	 */
	@Test
	void TestSynthesizer210() {
		Table inTable1 = Utils.loadTableFromFile("examples/210_1_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/210_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("A", Type.Str), //
				new Cell("B", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * synthesis without sort. (no valid keys)
	 */
	@Test
	void TestSynthesizer211() {
		Table inTable1 = Utils.loadTableFromFile("examples/211_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/211_input.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Join without reducing rows.
	 */
	@Test
	void TestSynthesizer212() {
		Table inTable1 = Utils.loadTableFromFile("examples/212_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/212_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/212_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	/**
	 * COUNT(...) = MAX(COUNT(...))
	 */
	@Test
	void TestSynthesizer213() {
		Table inTable = Utils.loadTableFromFile("examples/213_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/213_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable);
	}

	/**
	 * COUNT(...) = MAX(COUNT(...)) <br>
	 * 2 key grouping
	 */
	@Disabled("taking much time")
	@Test
	void TestSynthesizer214() {
		Table inTable = Utils.loadTableFromFile("examples/214_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/214_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable);
	}

	/**
	 * Should use AND, not OR.
	 */
	@Test
	void TestSynthesizer215() {
		Table inTable = Utils.loadTableFromFile("examples/215_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/215_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("10", Type.Int), //
				new Cell("A", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable);
	}

	/**
	 * A constant is not needed, but force it to be used.
	 */
	@Test
	void TestSynthesizer216() {
		Table inTable = Utils.loadTableFromFile("examples/216_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/216_input.csv");
		SynthOption option = new SynthOption(//
				new Cell("10", Type.Int) //
		);
		checkNotNullTop5(option, outTable, inTable);
	}

	/**
	 * The synthesis is impossible.
	 */
	@Test
	void TestSynthesizerTimeout01() {
		Table inTable1 = Utils.loadTableFromFile("examples/210_1_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/210_output.csv");
		SynthOption option = new SynthOption();

		Example example = new Example(outTable, new NamedTable("table1", inTable1));

		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize(300);
		assertNull(result);
	}

	@Disabled("takes 245 seconds")
	@Test
	void TestSynthesizer217() {
		Table inTable1 = Utils.loadTableFromFile("examples/217_1_input.csv");
		Table inTable2 = Utils.loadTableFromFile("examples/217_2_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/217_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1, inTable2);
	}

	@Test
	void TestSynthesizer218() {
		Table inTable1 = Utils.loadTableFromFile("examples/218_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/218_output.csv");
		SynthOption option = new SynthOption(//
				new Cell("T", Type.Str)//
		);
		checkNotNullTop5(option, outTable, inTable1);
	}

	@Test
	void TestSynthesizer300() {
		Table inTable1 = Utils.loadTableFromFile("examples/300_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/300_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Window functions: MIN, MAX (with grouping)
	 */
	@Test
	void TestSynthesizer301() {
		Table inTable1 = Utils.loadTableFromFile("examples/301_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/301_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Window functions: RANK
	 */
	@Test
	void TestSynthesizer302() {
		Table inTable1 = Utils.loadTableFromFile("examples/302_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/302_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Window functions: SUM
	 */
	@Test
	void TestSynthesizer303() {
		Table inTable1 = Utils.loadTableFromFile("examples/303_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/303_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * Window functions: RANK, SUM
	 */
	@Test
	void TestSynthesizer304() {
		Table inTable1 = Utils.loadTableFromFile("examples/304_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/304_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}

	/**
	 * extract YAER from date.
	 */
	@Test
	void TestSynthesizer310() {
		Table inTable1 = Utils.loadTableFromFile("examples/310_input.csv");
		Table outTable = Utils.loadTableFromFile("examples/310_output.csv");
		SynthOption option = new SynthOption();
		checkNotNullTop5(option, outTable, inTable1);
	}
}
