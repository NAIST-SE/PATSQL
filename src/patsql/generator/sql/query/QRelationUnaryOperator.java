package patsql.generator.sql.query;

public class QRelationUnaryOperator implements QCondition {

	private Operator o;
	public QCondition c;

	public QRelationUnaryOperator(Operator o, QCondition c) {
		this.o = o;
		this.c = c;
	}

	public enum Operator {
		IS_NULL(40, "IS NULL"), IS_NOT_NULL(40, "IS NOT NULL");

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
			return "(\n" + QQueryIndenter.indent(c.toString(p) + " " + o.toString()) + ")";
		} else {
			return QQueryIndenter.indent(c.toString(p) + " " + o.toString());
		}
	}

}
