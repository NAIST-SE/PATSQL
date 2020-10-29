package patsql.generator.sql.query;

public abstract class QRelation {

	public String tableAlias;
	protected static int incId = 0;

	public void obtainTableAlias() {
		if (tableAlias == null) {
			tableAlias = toTableAlias(incId++);
		}
	}

	public void refillTableAliasWith(int i) {
		assert tableAlias != null;
		tableAlias = toTableAlias(i);
	}

	private static String toTableAlias(int i) {
		return "T" + i;
	}

	public abstract String toCalledName();

	public String toQualifierString() {
		return tableAlias == null ? "" : (tableAlias + ".");
	}

}
