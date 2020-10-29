package patsql.generator.sql.query;

public class QSingleColumnCondition implements QCondition {
	public QColumn c;

	public QSingleColumnCondition(QColumn c) {
		this.c = c;
	}

	@Override
	public String toString(int p) {
		return c.toString();
	}
}
