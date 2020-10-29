package patsql.generator.sql.query;

import java.util.Optional;

import patsql.entity.table.agg.Agg;

public class QAggregatedColumn extends QColumn {

	public Agg agg;
	public QColumn src;

	public QAggregatedColumn(Agg agg, QColumn src) {
		this.agg = agg;
		this.src = src;
	}

	@Override
	public QRelation getSrcRelation() {
		return src.getSrcRelation();
	}

	@Override
	public String referencedColName() {
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return toPostgreSQL(agg, src.toString());
	}

	@Override
	public String toStringAliased() {
		if (isAliased == false || !getColAlias().isPresent()) {
			return toPostgreSQL(agg, src.toString());
		} else {
			return toPostgreSQL(agg, src.toString()) + " AS " + getColAlias().get();
		}
	}

	private static String toPostgreSQL(Agg agg, String srcCol) {
		switch (agg) {
		case ConcatComma:
			return String.format("string_agg(%s, ', ')", srcCol);
		case ConcatSlash:
			return String.format("string_agg(%s, '/')", srcCol);
		case ConcatSpace:
			return String.format("string_agg(%s, ' ')", srcCol);
		case CountD:
			return String.format("count(DISTINCT %s)", srcCol);
		case Avg:
		case Count:
		case Max:
		case Min:
		case Sum:
		default:
			return String.format("%s(%s)", agg.toString().toLowerCase(), srcCol);
		}
	}

	@Override
	public Optional<String> getColAlias() {
		String srcColStr = src.getColAlias().orElse(src.toString().replace('.', '_'));

		switch (agg) {
		case ConcatComma:
			return Optional.of("concat_comma_" + srcColStr);
		case ConcatSlash:
			return Optional.of("concat_slash_" + srcColStr);
		case ConcatSpace:
			return Optional.of("concat_space_" + srcColStr);
		case CountD:
			return Optional.of("count_distinct_" + srcColStr);
		case Avg:
		case Count:
		case Max:
		case Min:
		case Sum:
		default:
			return Optional.of(agg.toString().toLowerCase() + "_" + srcColStr);
		}
	}

}
