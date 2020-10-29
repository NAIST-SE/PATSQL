package patsql.generator.sql.query;

public class QQualifiedCol {

	public QRelation r;
	public String col;

	public QQualifiedCol(QRelation r, String col) {
		this.r = r;
		this.col = col;
	}

}
