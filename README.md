# PATSQL - SQL Synthesizer
PATSQL is a programming-by-example tool that automatically synthesizes SQL queries from input and output tables.

You can try PATSQL at https://naist-se.github.io/patsql/.

## Requirements
The code is written in Java as an Eclipse project. Maven is also required to manage dependencies and build the project.
- Java 1.8 or later
- Maven 3.6.1 or later

## Contents
- `src`  - source code.
- `test` - test code.
- `examples` - input and output tables used for test and evaluation.
- `evaluation` - the details of the evaluation result in [the arXiv paper](#paper).

## How to execute the synthesis?
There are test cases under the `test` directory. The test cases would be helpful to understand the usage of each methods.

In particular, the test case `patsql.synth.RASynthesizerTest.ExampleForSQLSynthesis` shows a basic example of SQL synthesis as follows. 

```java
	@Test
	void ExampleForSQLSynthesis() {

		// Create the input table by giving the schema and rows
		Table inTable = new Table(
				new ColSchema("col1", Type.Str), 
				new ColSchema("col2", Type.Str), 
				new ColSchema("col3", Type.Int), 
				new ColSchema("col4", Type.Date)
		);
		inTable.addRow(
				new Cell("A1", Type.Str), 
				new Cell("XXX", Type.Str), 
				new Cell("123", Type.Int), 
				new Cell("20200709", Type.Date)
		);
		inTable.addRow(
				new Cell("A2", Type.Str), 
				new Cell("XXX", Type.Str), 
				new Cell("345", Type.Int), 
				new Cell("null", Type.Null)
		);
		inTable.addRow(
				new Cell("A3", Type.Str), 
				new Cell("XXX", Type.Str), 
				new Cell("567", Type.Int), 
				new Cell("20200713", Type.Date)
		);

		// Create the output table
		Table outTable = new Table(
				new ColSchema("col1", Type.Str), 
				new ColSchema("col3", Type.Int) 
		);
		outTable.addRow(new Cell("A1", Type.Str), new Cell("123", Type.Int));
		outTable.addRow(new Cell("A3", Type.Str), new Cell("567", Type.Int));

		// Specify used constants in the query as a hint
		SynthOption option = new SynthOption(
				new Cell("A2", Type.Str)
		);

		// Give a name to the input table. The name is used in the resulting query
		NamedTable namedInputTable = new NamedTable("input_table", inTable);

		// Execute synthesis
		Example example = new Example(outTable, namedInputTable);
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);

		// Print the given example and synthesis result
		System.out.println("** INPUT AND OUTPUT TABLES **");
		System.out.println(example);
		System.out.println("** FOUND PROGRAM IN DSL**");
		RAUtils.printTree(result);
		System.out.println("** SOLUTION QUERY **");
		System.out.println(sql);
	}
```

## Paper
"PATSQL: Efficient Synthesis of SQL Queries from Example Tables with Quick Inference of Projected Columns", submitted to [arXiv](https://arxiv.org/abs/2010.05807).
