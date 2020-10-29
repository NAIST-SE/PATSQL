package patsql.entity.table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValue {
	public LocalDate date;

	private static String[] dateFormats = { //
			"yyyy-MM-dd", //
			"yyyy-MM-dd HH:mm:ss", //
			"yyyy-MM-dd HH:mm:ss+00:00", //
			"yyyy-MM-dd H:mm:ss", //
			"yyyy-MM-dd HH:mm", //
			"yyyy-MM-dd  H:mm", //
			"yyyy-MM-dd H:mm", //
			"MM/dd/yyyy", //
			"MM/dd/yyyy HH:mm:ss", //
			"MM/dd/yyyy HH:mm:ss+00:00", //
			"MM/dd/yyyy H:mm:ss", //
			"MM/dd/yyyy HH:mm", //
			"MM/dd/yyyy H:mm", //
			"yyyy/MM/dd", //
			"HH:mm:ss", //
			"HH:mm", //
			"yyyyMMdd"//
	};

	public DateValue(LocalDate date) {
		this.date = date;
	}

	public static DateValue parse(String dateStr) {
		LocalDate date = null;
		for (String format : dateFormats) {
			try {
				DateTimeFormatter f = DateTimeFormatter.ofPattern(format);
				date = LocalDate.parse(dateStr, f);
				break;
			} catch (DateTimeParseException e) {
				// skip
			}
		}
		if (date == null)
			throw new IllegalArgumentException("unsupported date format: " + dateStr);

		// ignore time
		return new DateValue(LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
	}

	public String year() {
		return String.valueOf(date.getYear());
	}

	public String month() {
		return String.valueOf(date.getMonthValue());
	}

	public String day() {
		return String.valueOf(date.getDayOfMonth());
	}

	@Override
	public String toString() {
		return String.format("%04d-%02d-%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
	}

	public int compareTo(DateValue other) {
		return this.date.compareTo(other.date);
	}

}
