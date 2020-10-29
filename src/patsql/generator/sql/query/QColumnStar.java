package patsql.generator.sql.query;

import java.util.Optional;

public class QColumnStar extends QColumn {
	private static final QColumnStar singleton = new QColumnStar();

	private QColumnStar() {
	}

	public static QColumnStar getInstance() {
		return singleton;
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public String toStringAliased() {
		return "*";
	}

	@Override
	public QRelation getSrcRelation() {
		throw new IllegalStateException();
	}

	@Override
	public String referencedColName() {
		throw new IllegalStateException();
	}

	@Override
	public Optional<String> getColAlias() {
		throw new IllegalStateException();
	}

}
