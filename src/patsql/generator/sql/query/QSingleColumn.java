package patsql.generator.sql.query;

import java.util.Optional;

public class QSingleColumn extends QColumn {

	public QRelation srcRelation;
	public String col;
	public QColumn aliasSrc;

	public QSingleColumn(QRelation srcRelation, String col) {
		this.srcRelation = srcRelation;
		this.col = col;
	}

	public QSingleColumn(QRelation srcRelation, QColumn aliasSrc) {
		this.srcRelation = srcRelation;
		this.aliasSrc = aliasSrc;
	}

	@Override
	public QRelation getSrcRelation() {
		return srcRelation;
	}

	@Override
	public String referencedColName() {
		if (col == null) {
			return aliasSrc.getColAlias().orElseGet(() -> aliasSrc.referencedColName());
		} else {
			return col;
		}
	}

	@Override
	public String toString() {
		if (col == null) {
			return srcRelation.toQualifierString()
					+ aliasSrc.getColAlias().orElseGet(() -> aliasSrc.referencedColName());
		} else {
			return srcRelation.toQualifierString() + col;
		}
	}

	@Override
	public String toStringAliased() {
		Optional<String> colAlias = getColAlias();
		if (!colAlias.isPresent()) {
			return toString();
		} else {
			return toString() + " AS " + colAlias.get();
		}
	}

	@Override
	public Optional<String> getColAlias() {
		if (isAliased) {
			return Optional.of(srcRelation.toQualifierString().replace('.', '_') + referencedColName());
		} else {
			return Optional.empty();
		}
	}

}
