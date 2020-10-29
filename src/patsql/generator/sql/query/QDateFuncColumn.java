package patsql.generator.sql.query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import patsql.entity.table.DateFunc;

public class QDateFuncColumn extends QColumn {

	public DateFunc func;
	public QColumn src;

	public QDateFuncColumn(DateFunc func, QColumn src) {
		this.func = func;
		this.src = src;
	}

	@Override
	public QRelation getSrcRelation() {
		return src.getSrcRelation();
	}

	@Override
	public String referencedColName() {
		return src.referencedColName();
	}

	@Override
	public String toString() {
		return toPostgreSQL();
	}

	@Override
	public String toStringAliased() {
		if (isAliased == false || !getColAlias().isPresent()) {
			return toPostgreSQL();
		} else {
			return toPostgreSQL() + " AS " + getColAlias().get();
		}
	}

	private String toPostgreSQL() {
		return String.format("EXTRACT(%s FROM %s)", func.toString().toLowerCase(), src);
	}

	@Override
	public Optional<String> getColAlias() {
		return Optional.of(String.format("%s_from_%s", func.toString().toLowerCase(),
				src.toString().toLowerCase().replace('.', '_')));
	}

	public List<QQualifiedCol> referredCols() {
		return Arrays.asList(new QQualifiedCol(src.getSrcRelation(), src.referencedColName()));
	}

}
