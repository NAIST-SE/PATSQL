ここで使用している画像は全て論文から引用したものである。詳しくは論文を御覧ください。
## アルゴリズム概要

後に説明する方法で生成したSktcherを用いて、以下のような探索方法で入出力に合致するDSLを決定する

1. AssignTableメソッドによって各スケッチにテーブル名を割り当てる。

2. CompleteSketch(SKetchFIller)を呼び出してスケッチの残りの□を全て補完する。

3. 補間が成功してプログラム p が見つかると、そのpが出力テーブルと等しいかを再度チェックして、結果として p をreturnする

4. そうでなければ（補間が成功しなければ）ExpaedSketchを呼び出して 1 に戻る

5. pが見つかるまで1~4が繰り返し行われる

<table>
	<tr>
		<th>実装</th><th>アルゴリズム</th>
	</tr>
	<tr>
		<td>
<code>patsql.synth.RASynthesizer.synthesize()</code>
<java>
    
		Sketcher sketcher = new Sketcher(example.inputs.length, isOutputSorted);

		for (RAOperator s : sketcher) {
			for (RAOperator sketch : assignNamesOnBaseTables(s)) {
				// check the timeout of itself.
				if (Thread.currentThread().isInterrupted()) {
					return null;
				}

				if (!isValidSketch(sketch)) {
					continue;
				}
				if (Debug.isDebugMode) {
					RAUtils.printSketch(sketch);
				}
				SketchFiller filler = new SketchFiller(sketch, example, option);
				for (RAOperator program : filler.fillSketch()) {
					if (!check(program))
						continue;

					// optimize the program returned.
					program = RAOptimizer.optimize(program);
					if (Debug.isDebugMode) {
						long dur = (System.nanoTime() - startDebug) / 1000000;
						Debug.Time.doneSynth(dur);
					}
					return program;
				}
			}
		}
</java>
		</td>
		<td><img width="350" alt="synth_algorithm" src="https://user-images.githubusercontent.com/63132753/131972806-0b3c48d5-cb19-4509-ac93-9864b3c1e391.PNG" ></td>
	</tr>
</table>


## sketcherの生成アルゴリズム

sketcherとは与えられた入出力から可能なsketchをIteratorとして扱ったものである。

sketcherの生成は以下のような規則を用いた。

**スケッチ内の演算子間の親子関係の制限について**

<img width="350" alt="expand_sketch" src="https://user-images.githubusercontent.com/63132753/131973191-8cad185b-200f-4af1-a5a3-9a1c9225313f.PNG">

スケッチに現れることができる関係代数演算子は上の表のように制限されている。

例えば、入力スケッチがProject(Table(□), □)の場合、Projectの次に来るのが許容されるのは、表において✔である、Select, Group, Window, Join, LeftJoin, Tableであるので、

•Project(Select(Table(□), □), □)  
•Project(Group(Table(□), □, □), □)  
•Project(Join(Table(□), Table(□), □), □)  

などが含まれる計6個のスケッチが返される

上の表のように次にくるスケッチを制限できる理由としては、

* X1:  Order 以外の演算子はレコードの順序を保持しないため、Order で決定された順序がスケッチの先頭にある場合にのみ意味を持つから

* X2:  実際のクエリでは稀と考えられるので除外

* X3:  これらの組み合わせを含むスケッチは正規の形ではないから。正規形ではないスケッチからプログラム p を得る場合、正規形のスケッチからは必ず p と等価なプログラムを得ることができるらしい

**関係代数の変換規則**

①同じ演算子を繰り返すプログラムは、必ず繰り返しのないプログラムとして表現できる

•Project(Project(T , c1), c2) → Project(T , c2)  
•Select(Select(T , p1), p2) → Select(T , p1 ∧ p2)  

②Project を Select や Join の上に移動させるためのルール

•Select(Project(T , c), p) → Project(Select(T , p), c)  
•Join(Project(T1, c),T2, p) → Project(Join(T1,T2, p), c′)  

同じルールがProject以外にGroup, Window,LeftJoinにも当てはまる

⇒①、②のルールより、**スケッチは最大一つのProjectしか含まない**ことがわかる 


また、PatSQLでは②のルールを用いて、**スケッチの構成要素である Order、Distinct、Project は、この順序でスケッチの先頭に表示することとする**

※UNION句をスケッチ構造にいれてしまうと、Projectの位置を上に移動させることができないので、このアルゴリズムではUNIONをサポートできなかった。

実装は`patsql.synth.sketcher.Sketcher.java`を御覧ください

## Sketch Completion(sketcheFiller)のアルゴリズム

以下に各関係代数演算子別のsketch completionのアルゴリズムを示す。fillSketch()によって呼び出される処理である。

<img width="800" alt="sketchCompletionAlgorithm" src="https://user-images.githubusercontent.com/63132753/131972971-e5931637-a4cc-4ef9-8b1f-9f5b991662fa.PNG">

関数 CompleteSketch(s,$T_{in}$, C) がエントリポイントであり、制約 φを伝播してスケッチを再帰的に補完するための Complete という補助関数を呼び出す。

このアルゴリズムではテーブルの包含関係φに基づいて検索空間を剪定する。

上に示す包含関係に基づいて剪定するアルゴリズムを満たした場合にプログラムpを返す
それぞれの演算子に対する実装は`patsql.synth.filler.strategy` を御覧ください
また、SketchFillerの実装は`patsql.synth.filler.SketchFiller.java`


