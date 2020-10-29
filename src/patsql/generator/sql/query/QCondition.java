package patsql.generator.sql.query;

public interface QCondition {
	/** 結合の強さを与えると、カッコをつけるべきか計算して出力してくれる */
	public String toString(int p);
}
