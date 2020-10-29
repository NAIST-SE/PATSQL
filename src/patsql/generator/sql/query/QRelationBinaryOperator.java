package patsql.generator.sql.query;

public class QRelationBinaryOperator implements QCondition {
	public QCondition c1;
	public QCondition c2;
	private Operator o;

	public QRelationBinaryOperator(Operator o, QCondition c1, QCondition c2) {
		this.o = o;
		this.c1 = c1;
		this.c2 = c2;
	}

	public enum Operator {
		EQ(40, "="), NE(40, "<>"), GT(40, ">"), GE(40, ">="), LT(40, "<"), LE(40, "<="), PLUS(30, "+"), MINUS(30, "-"),
		MULT(20, "*");

		private int p;
		private String s;

		private Operator(int p, String s) {
			this.p = p;
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}

	}

	@Override
	public String toString(int p) {
		if (p < o.p) {
			// 親が強い場合
			return "(\n" + QQueryIndenter.indent(c1.toString(p) + " " + o.toString() + " " + c2.toString(p - 1)) + ")";
		} else {
			return QQueryIndenter.indent(c1.toString(p) + " " + o.toString() + " " + c2.toString(p - 1));
		}
	}
}
