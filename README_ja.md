# PATSQL - SQL Synthesizer
PATSQL は、入力テーブルと出力テーブルから SQL クエリを自動的に合成するprogramming-by-exampleのツールです。

アルゴリズムの詳細は以下の論文に説明されています。
Keita Takenouchi, Takashi Ishio, Joji Okada, Yuji Sakata: PATSQL: Efficient Synthesis of SQL Queries from Example Tables with Quick Inference of Projected Columns. PVLDB, vol.14, no.11, pp.1937-1949, 2021.  [PVLDB](https://vldb.org/pvldb/vol14/p1937-takenouchi.pdf) [arXiv](https://arxiv.org/abs/2010.05807)

合成は[デモ用ページ](https://naist-se.github.io/patsql/)で試すことができます。


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
PATSQLには `main` メソッドがありません。合成の実行例はそれぞれ JUnit のテストケースとして記述されています。
基本的なテストケースは`patsql.synth.RASynthesizerTest.ExampleForSQLSynthesis`に含まれています。  
このテストケースに沿ってツールを使うことができます。

このテストケースは以下の3ステップから構成されます
1. 入力データの準備
2. 合成の実行
3. 結果の出力

SQL合成は以下のように実行します。  
合成の本体を実装しているRASynthesizerをインスタンス化し、synthesizeメソッドを呼ぶことで合成が実行できます。
```java
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);
```

続いて、実行に必要な`example`と`option`について説明します。  
PATSQLの入力に必要なデータは入出力テーブルとヒント(生成するSQLに含まれる定数)である。  
入出力テーブルが`example`、ヒントが`option`に該当します。  


exampleを作成するために、まず入出力テーブルの作成方法から説明します。

#### **入出力テーブルの作成**
入出力テーブルはpatsqlで独自に実装したクラスを用います。定義は`patsql.entity.synth`と`patsql.entity.table`パッケージにあります。 
以下のようにカラム名としてColSchema、データとしてCellをのインスタンスを用いて列ごとにデータを追加していきます。  
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
テーブルは以下のようにcsvファイルから読み込むことも可能です。
```java
		Table inTable1 = Utils.loadTableFromFile("examples/input1.csv");
		Table outTable = Utils.loadTableFromFile("examples/output1.csv");
```

#### **exampleの作成**
exampleは上で作った入力テーブルと出力テーブルを引数に受けとって以下のように作成します。
```java
		Example example = new Example(outTable, namedInputTable);
```


#### **option(ヒント)の作成**
`SynthOption` オブジェクトはPATSQLに与えるヒントです。SQLクエリに含まれると予想される定数のヒントをCellインスタンスとして指定します。
```java
		// Specify used constants in the query as a hint
		SynthOption option = new SynthOption(
				new Cell("A2", Type.Str)
		);
```


#### **SQL合成の実行**
```java
		Example example = new Example(outTable, namedInputTable);
		RASynthesizer synth = new RASynthesizer(example, option);
		RAOperator result = synth.synthesize();

		// Convert the result into a SQL query
		String sql = SQLUtil.generateSQL(result);
```
上記のコードが示すテーブルと出力結果を以下に示します。
<table>
	<tr>
			<th>入力テーブル</th><th>出力テーブル</th><th>option</th><th>合成結果</th>
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
PATSQLは、スケッチ・ベースのアルゴリズムを採用しています。
スケッチベースのアルゴリズムは、DSLを合成し、そのDSLからSQLを生成します。
我々のDSLは、SELECT、PRJECT、LEFT JOINなどの拡張関係代数演算子にWINDOWを加えたものです。
`RQSynthesizer` のように、クラス名やパッケージ名に登場する `RA` は、関係代数（Relational Algebra) に由来します。

SCYTHEなどの他のSQL合成ツールと比較して、比較的少量のヒント（クエリで使用される定数）で、集約、ネストされたクエリ、ウィンドウ関数などの表現力の高いクエリ合成を行います。
詳しくは以下のファイルを御覧ください。
[アルゴリズム概要](/Algorithm_ja.md)

## 各パッケージの役割
| Package Name  | Description | the file you shoudl check first|
|---|---|---|
| `patsql.synth` | トップレベルのアルゴリズムが実装されている。 | `RASynthesizer.java` |
| `patsql.synth.sketcher` | sketcheの生成などを管理するIteratorであるsketcherが実装さている。論文に示されている関係代数のルールに従ったexpandSketchメソッドはsketcherに実装されている | `Sketcher.java` |
| `patsql.synth.filler` | sketchを補完に関する処理を扱う。論文に示されている関係代数のルールに従ったsketchCompletionメソッドが実装されている | `SketchFiller.java` |
| `patsql.synth.filler.strategy` | 関係代数演算子ごとの検索空間剪定アルゴリズムが実装さてれいる | `FillingStrategy.java` |
| `patsql.entity.synth` | PATSQLの入力であるExampleクラス、SynthOptionクラスなどを定義する | 全て重要 |
| `patsql.entity.table` | PATSQLで扱うTableを定義する | `Table.java` |
| `patsql.ra.operator` | 関係代数演算子を定義する | `RAOperator.java` |
| `patsql.ra.predicate` | predicate(論理演算の結果を返す、SQL文の述語部分のこと)を定義する | `Predicate.java` |
| `patsql.ra.util` | 関係代数演算のためのutilsをまとめた | `RAUtils.java` |
| `patsql.generator.sql` | DSLからSQLを合成するための処理を扱う | `SQLUtil` |


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
