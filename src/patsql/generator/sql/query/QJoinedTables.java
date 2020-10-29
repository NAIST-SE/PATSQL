package patsql.generator.sql.query;

import java.util.ArrayList;
import java.util.List;

public class QJoinedTables {

	public QRelation firstRelation;
	public List<QJoinSpec> joins = new ArrayList<>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(firstRelation);
		for (QJoinSpec join : joins) {
			sb.append(" ");
			sb.append(join);
		}
		return sb.toString();
	}

}
