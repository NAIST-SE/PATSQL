package patsql.generator.sql.query;

public class QSortSpec {
	public QColumn c;
	public Ordering ordering;

	public QSortSpec(QColumn c, Ordering ord) {
		this.c = c;
		ordering = ord;
	}

	public enum Ordering {
		ASC, DESC;
	}

	@Override
	public String toString() {
		return c.toString() + " " + ordering.toString();
	}
}
