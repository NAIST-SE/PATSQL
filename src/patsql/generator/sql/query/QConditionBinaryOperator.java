package patsql.generator.sql.query;

public class QConditionBinaryOperator implements QCondition {
	public QCondition c1;
	public QCondition c2;
	private Operator o;

	public QConditionBinaryOperator(Operator o, QCondition c1, QCondition c2) {
		this.o = o;
		this.c1 = c1;
		this.c2 = c2;
	}

	public static enum Operator {
		AND(60), OR(70);

		private int p;

		private Operator(int p) {
			this.p = p;
		}

	}

	@Override
	public String toString(int p) {
		if (p < o.p) {
			// 親が強い場合
			return QQueryIndenter
					.indent("(\n" + c1.toString(o.p) + "\n" + o.toString() + "\n" + c2.toString(o.p - 1) + "\n)");
		} else {
			return c1.toString(o.p) + "\n" + o.toString() + "\n" + c2.toString(o.p - 1);
		}
	}
}
