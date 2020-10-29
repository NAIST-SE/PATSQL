package patsql.generator.sql.query;

public class QQueryIndenter {
	public final static String INDENT = "    ";

	/* 全体を字下げ */
	public static String indent(String s, String spaces) {
		return s.replaceAll("(?m)^", spaces);
	}

	/* 全体を字下げ */
	public static String indent(String s) {
		return indent(s, INDENT);
	}

	/* 先頭行以外の全体を字下げ */
	public static String indent_tail(String s, String spaces) {
		return s.replaceAll("(?m)^", spaces).replaceAll("^" + spaces, "");
	}

}