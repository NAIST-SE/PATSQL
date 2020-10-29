package patsql.generator.sql.query;

public class QSubQuery extends QRelation {
	public QQuery query;

	public QSubQuery(QQuery query) {
		this.query = query;
		obtainTableAlias();
	}

	@Override
	public String toString() {
		return "(" + QQueryIndenter.indent_tail(query.toString(), QQueryIndenter.INDENT + " ")
				+ QQueryIndenter.indent(") AS " + tableAlias);
	}

	@Override
	public String toCalledName() {
		assert tableAlias != null;
		return tableAlias;
	}

}
