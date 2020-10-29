package patsql.generator.sql.query;

import java.util.Optional;

public abstract class QColumn {

	public boolean isAliased;

	public abstract QRelation getSrcRelation();

	public abstract Optional<String> getColAlias();

	@Override
	public abstract String toString();

	public abstract String toStringAliased();

	public abstract String referencedColName();

}
