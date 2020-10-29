package patsql.generator.sql.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class QColumnConstant<T> extends QColumn {
	private T c;

	public QColumnConstant(T c) {
		this.c = c;
	}

	private String cToString() {
		if (c instanceof String) {
			String s = (String) c;
			// note that Date is handled as String.
			// DATE 'YYYY-MM-DD' literal is not supported.
			return String.format("'%s'", s.replaceAll("'", "''"));
		} else if (c instanceof ZonedDateTime || c instanceof LocalDateTime || c instanceof LocalDate
				|| c instanceof LocalTime) {
			return Utils.dateToLiteral(c);
		} else {
			return c.toString();
		}
	}

	@Override
	public String toString() {
		return cToString();
	}

	@Override
	public String toStringAliased() {
		return cToString();
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
