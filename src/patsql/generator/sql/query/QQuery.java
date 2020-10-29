package patsql.generator.sql.query;

import java.util.ArrayList;
import java.util.List;

public class QQuery {
	public List<QColumn> selectColumns = new ArrayList<>();
	public QJoinedTables fromTables = new QJoinedTables();
	public QCondition whereCondition = null;
	public List<QSortSpec> sortSpecs = new ArrayList<>();
	public List<QColumn> groups = new ArrayList<>();
	public QCondition havingCondition = null;
	public boolean isDistinct = false;

	public QQuery(QRelation fromRelation) {
		fromTables.firstRelation = fromRelation;
	}

	public void addSelectColumn(QColumn col) {
		selectColumns.add(col);
	}

	public void addJoin(QJoinSpec joinSpec) {
		fromTables.joins.add(joinSpec);
	}

	public void setWhereCondition(QCondition c) {
		whereCondition = c;
	}

	public void setHavingCondition(QCondition c) {
		havingCondition = c;
	}

	public void addSortSpec(QSortSpec o) {
		sortSpecs.add(o);
	}

	public void addGroup(QColumn col) {
		groups.add(col);
	}

	public void addHavingCondition(QCondition c) {
		if (havingCondition == null) {
			havingCondition = c;
		} else {
			havingCondition = new QConditionBinaryOperator(QConditionBinaryOperator.Operator.AND, havingCondition, c);
		}
	}

	public void setDistinct() {
		isDistinct = true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		/* SELECT */
		if (isDistinct) {
			sb.append("SELECT DISTINCT\n");
		} else {
			sb.append("SELECT\n");
		}

		// column alias exists?
		sb.append(String.join(", ", selectColumns.stream().map(QColumn::toStringAliased).toArray(String[]::new)));
		sb.append("\n");

		/* FROM */
		sb.append("FROM\n");
		sb.append(QQueryIndenter.INDENT);
		sb.append(fromTables.toString());
		sb.append("\n");

		/* WHERE */
		if (whereCondition != null) {
			sb.append("WHERE\n");
			sb.append(whereCondition.toString(1000));
			sb.append("\n");
		}

		/* GROUP BY */
		if (!groups.isEmpty()) {
			sb.append("GROUP BY\n");
			sb.append(QQueryIndenter.INDENT);
			List<String> grouperStrings = new ArrayList<>();
			for (QColumn col : groups) {
				grouperStrings.add(col.toString());
			}
			sb.append(String.join(", ", grouperStrings));
			sb.append("\n");
		}

		/* HAVING */
		if (havingCondition != null) {
			sb.append("HAVING\n");
			sb.append(havingCondition.toString(100));
			sb.append("\n");
		}

		/* ORDER BY */
		if (!sortSpecs.isEmpty()) {
			sb.append("ORDER BY\n");
			sb.append(QQueryIndenter.INDENT);
			sb.append(String.join(",\n" + QQueryIndenter.INDENT,
					sortSpecs.stream().map(QSortSpec::toString).toArray(String[]::new)));
			sb.append("\n");
		}

		return sb.toString();
	}

}
