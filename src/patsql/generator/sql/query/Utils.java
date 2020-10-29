package patsql.generator.sql.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

	/**
	 * @param parsed Object returned by Utils.parseDate
	 * @return
	 */
	public static String dateToLiteral(Object parsed) {
		String colType;
		String dateTimePart;
		if (parsed instanceof ZonedDateTime) {
			colType = "timestamp with time zone";
			dateTimePart = ((ZonedDateTime) parsed).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		} else if (parsed instanceof LocalDateTime) {
			colType = "timestamp";
			dateTimePart = ((LocalDateTime) parsed).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} else if (parsed instanceof LocalDate) {
			colType = "date";
			dateTimePart = ((LocalDate) parsed).format(DateTimeFormatter.ISO_LOCAL_DATE);
		} else if (parsed instanceof LocalTime) {
			colType = "time";
			dateTimePart = ((LocalTime) parsed).format(DateTimeFormatter.ISO_LOCAL_TIME);
		} else {
			throw new RuntimeException("unexpected: " + parsed);
		}
		return String.format("%s '%s'", colType.toUpperCase(), dateTimePart);
	}

}
