# PATSQL - SQL Synthesizer
PATSQL is a programming-by-example tool that automatically synthesizes SQL queries from input and output tables.

The algorithm is described in the paper:
Keita Takenouchi, Takashi Ishio, Joji Okada, Yuji Sakata: PATSQL: Efficient Synthesis of SQL Queries from Example Tables with Quick Inference of Projected Columns. PVLDB, vol.14, no.11, pp.1937-1949, 2021. [PVLDB](https://vldb.org/pvldb/vol14/p1937-takenouchi.pdf)　[arXiv](https://arxiv.org/abs/2010.05807)

You can try a synthesis on [our demonstration page](https://naist-se.github.io/patsql/).

[Japanese version of this README is here./ 日本語版はこちら。](README_ja.md)

## Requirements
The code is written in Java as an Eclipse project. Maven is also required to manage dependencies and build the project.
- Java 1.8 or later
- Maven 3.6.1 or later

## Contents
- `src`  - source code.
- `test` - test code.
- `test/patsql/synth/benchmark/ScytheSqlSynthesizerTest.java` - test code to execute `ase13` benchmark.
- `test/patsql/synth/benchmark/ScytheTopRatedPostsTest.java` - test code to execute `so-top` benchmark.
- `test/patsql/synth/benchmark/ScytheDevSetTest.java` - test code to execute `so-dev` benchmark.
- `test/patsql/synth/benchmark/ScytheRecentPostsTest.java` - test code to execute `so-rec` benchmark.
- `test/patsql/synth/benchmark/KaggleTest.java` - test code to execute `kaggle` benchmark.
- `examples` - input and output tables used for test and evaluation.
- `evaluation` - benchmark results in html. 

## Installation
Execute the following maven command. This generates `patsql-engine-1.0.0.jar` in the `target` directory. 

```
mvn install -DskipTests
```

## How to execute the synthesis?
PATSQL does not have a main method.  SQL synthesis examples are provided as JUnit test cases.  
A basic test case is included in `patsql.synth.RASynthesizerTest.ExampleForSQLSynthesis`.   
You can follow this test case to use the tool.  

This test case consists of the following three steps: 1.
1. prepare the input data
2. run the synthesis
3. output the results

SQL synthesis is executed as follows.  
The synthesis can be executed by instantiating the RASynthesizer and calling the synthesize method. RASynthesizer calss implements the core of the synthesis

```java
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);
```

Then, we will explain the `example` and `option` required for execution.  
The data required for PATSQL input are input/output tables and hints (constants included in the SQL to be generated).  
The input/output table corresponds to `example` and the hint corresponds to `option`.  



### **Creating input and output tables**
The input and output tables are defined by our original classes. See `patsql.entity.synth` and `patsql.entity.table` packages for the definition. 

Input/output tables use classes implemented independently by patsql. See `patsql.entity.synth` and `patsql.entity.table` for definitions.   
We will add data to each column using ColSchema instance as column name and Cell instance as data as follows.  

```java
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

		// Give a name to the input table. The name is used in the resulting query
		NamedTable namedInputTable = new NamedTable("input_table", inTable);
```

We can also create table instance from a csv file as follows:
```java
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/output1.csv");
```

#### **Creating example**
The example takes the input and output tables created above as arguments.
```java
		Example example = new Example(outTable, namedInputTable);
```


### **Creating option(hint)**
PATSQL needs constants that are expected to be included in the SQL queries as hints.
A `SynthOption` object is a hint to give to PATSQL, specifying a constant hint as a Cell instance that is expected to be included in the SQL query.
```java
		// Specify used constants in the query as a hint
		SynthOption option = new SynthOption(
				new Cell("A2", Type.Str)
		);
```

### **Executing SQL synthesis**
```java
		Example example = new Example(outTable, namedInputTable);
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);
```

The input and output tables and SQL queries that synthesized by PATSQL are shown below
<table>
	<tr>
			<th>Input table</th><th>Output table</th><th>Option</th><th>Synthesis results</th>
	</tr>
	<tr>
		<td>
		<table>
			<tr>
				<th>col1</th>
				<th>col2</th>
				<th>col3</th>
				<th>col4</th>
			</tr>
			<tr>
				<td>A1</td>
				<td>XXX</td>
				<td>123</td>
				<td>20200709</td>
			</tr>
			<tr>
				<td>A2</td>
				<td>XXX</td>
				<td>345</td>
				<td>null</td>
			</tr>
			<tr>
				<td>A3</td>
				<td>XXX</td>
				<td>567</td>
				<td>20200713</td>
			</tr>
		</table>
	</td>
	<td>
		<table>
			<tr>
				<th>col1</th>
				<th>col3</th>
			</tr>
			<tr>
				<td>A1</td>
				<td>123</td>
			</tr>
			<tr>
				<td>A2</td>
				<td>345</td>
			</tr>
			<tr>
				<td>A3</td>
				<td>567</td>
			</tr>
		</table>
	</td>
	<td>
	A2
	</td>
	<td>
SELECT<br>
&emsp;&emsp;col1,<br>  
&emsp;&emsp;col3<br>
FROM<br>
&emsp;&emsp;input_table<br>
WHERE<br>
&emsp;&emsp;col1 <> 'A2'<br>
ORDER BY<br> 
&emsp;&emsp;col1 ASC<br>
	</td>
</tr>
</table>


## Algorithm Summary
PATSQL uses a sketch-based algorithm.
The sketch-based algorithm synthesizes a DSL and then generates SQL from the DSL.
Our DSL is the extended relational algebra operators SELECT, PRJECT, LEFT JOIN, etc. plus WINDOW.
`RA` in class names and package names comes from Relational Algebra.

It performs highly expressive query synthesis for aggregates, nested queries, windowed functions, etc. with a relatively small amount of hints (constants used in queries) compared to other SQL synthesis tools such as SCYTHE.

For details, please see the following file  
[Algorithm overview](/Algorithm.md)

## The role of each package
| Package Name  | Description | the file you shoudl check first|
|---|---|---|
| `patsql.synth` | Top-level algorithms can be found here. If you want to understand patsql, you should read this package first. | `RASynthesizer.java` |
| `patsql.synth.sketcher` | Sketcher is iterator to handle sketche generating. The expandSketch method has been implemented according to the rules of relational algebra given in the paper. | `Sketcher.java` |
| `patsql.synth.filler` | Handle sketch completion. The sketchCompletion method is implemented according to the rules of relational algebra shown in the paper. | `SketchFiller.java` |
| `patsql.synth.filler.strategy` | A search space pruning algorithm for each relational algebra operator is implemented. | `FillingStrategy.java` |
| `patsql.entity.synth` | Define the Example class, SynthOption class, NamedTable class. used for PATSQL input. | all |
| `patsql.entity.table` | Define the Table class to be used by PATSQL | `Table.java` |
| `patsql.ra.operator` | Define relational algebra operators | `RAOperator.java` |
| `patsql.ra.predicate` | Define predicate  | `Predicate.java` |
| `patsql.ra.util` | A collection of utils for relational algebra operations. | `RAUtils.java` |
| `patsql.generator.sql` | Handle the process of synthesizing SQL from DSL. | `SQLUtil` |


## Third-party libraries used in this project
See the transitive dependencies from each link below.  

### JUnit Jupiter API
Module "junit-jupiter-api" of JUnit 5.  
License: EPL 2.0  
Version: 5.5.1  
https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.5.1  

### JUnit Jupiter Engine
Module "junit-jupiter-engine" of JUnit 5.  
License: EPL 2.0  
Version: 5.5.1  
https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.5.1  

### H2 Database Engine
Used to validate the syntax of synthesized queries in test.  
License: EPL 1.0, MPL 2.0  
Version: 1.4.200  
https://mvnrepository.com/artifact/com.h2database/h2/1.4.200  

### Gson
Used to serialize and deserialize Java objects to JSON.  
License: Apache 2.0  
Version: 2.8.6  
https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.6

### Hibernate Core Relocation
Used to format SQL queries.  
License: LGPL 2.1  
Version: 5.4.11.Final  
https://mvnrepository.com/artifact/org.hibernate/hibernate-core/5.4.11.Final  
