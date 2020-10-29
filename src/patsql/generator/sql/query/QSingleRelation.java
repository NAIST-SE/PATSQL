package patsql.generator.sql.query;

public class QSingleRelation extends QRelation {
	private String name;

	public QSingleRelation(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String asClause = tableAlias == null ? "" : (" AS " + tableAlias);
		return name + asClause;
	}

	@Override
	public String toCalledName() {
		return tableAlias == null ? name : tableAlias;
	}

}
