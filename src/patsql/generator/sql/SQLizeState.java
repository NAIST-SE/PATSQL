package patsql.generator.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import patsql.generator.sql.query.QColumn;
import patsql.generator.sql.query.QColumnStar;
import patsql.generator.sql.query.QCondition;
import patsql.generator.sql.query.QJoinSpec;
import patsql.generator.sql.query.QQuery;
import patsql.generator.sql.query.QRelation;
import patsql.generator.sql.query.QSingleColumn;
import patsql.generator.sql.query.QSortSpec;
import patsql.generator.sql.query.QSubQuery;

public class SQLizeState {

	final public QQuery query;
	final public LinkedHashMap<Integer, QColumn> idToQColumn;

	public Stage stage;

	enum Stage {
		FROM, WHERE, GROUP_BY, HAVING, WINDOW, SELECT, ORDER_BY, DONE;
	}

	public SQLizeState(QRelation relation) {
		query = new QQuery(relation);
		stage = Stage.FROM;
		idToQColumn = new LinkedHashMap<>();
	}

	public void createTopQuery() {
		if (stage.compareTo(Stage.SELECT) < 0) {
			query.addSelectColumn(QColumnStar.getInstance());
		}
		stage = Stage.DONE;
	}

	public SQLizeState createSubQuery() {
		if (stage.compareTo(Stage.SELECT) < 0) {
			// fill projected columns
			fillSelectColumn(idToQColumn.values());
		}
		stage = Stage.DONE;

		// set this query as the parent's sub query
		QSubQuery subQuery = new QSubQuery(query);
		SQLizeState parent = new SQLizeState(subQuery);

		// update table alias
		for (Entry<Integer, QColumn> e : idToQColumn.entrySet()) {
			QColumn srcCol = e.getValue();
			if (query.selectColumns.stream().noneMatch(col -> col == srcCol))
				continue;

			srcCol.isAliased = true;
			// instantiate a new SingleColumn with got QColumn as aliasSrc.
			QSingleColumn newCol = new QSingleColumn(subQuery, srcCol);
			parent.idToQColumn.put(e.getKey(), newCol);
		}
		return parent;
	}

	public void registerCols(int id, QColumn agg) {
		idToQColumn.put(id, agg);
		stage = Stage.FROM;
	}

	public void resetIdToQColumnWith(LinkedHashMap<Integer, QColumn> newMap) {
		idToQColumn.clear();
		idToQColumn.putAll(newMap);
		stage = Stage.GROUP_BY;
	}

	public void fillSelectColumn(Collection<QColumn> col) {
		for (QColumn c : col) {
			query.addSelectColumn(c);
		}
		stage = Stage.SELECT;
	}

	public void fillJoin(QJoinSpec joinSpec) {
		query.addJoin(joinSpec);
		fillTableAliasIfNecessary();
		stage = Stage.FROM;
	}

	private void fillTableAliasIfNecessary() {
		// check column name conflicts like "T1.id = T2.id".
		Map<String, QRelation> colNameToQRelation = new HashMap<>(); // keys are column names or alias names
		for (QColumn c : idToQColumn.values()) {
			String key = c.referencedColName();
			QRelation gotRelation = colNameToQRelation.get(key);
			if (gotRelation == null) {
				colNameToQRelation.put(key, c.getSrcRelation());
			} else {
				// a conflict happens
				gotRelation.obtainTableAlias();
				c.getSrcRelation().obtainTableAlias();
			}
		}

		// check table name conflicts like "FROM A AS T1, A AS T2"
		Map<String, QRelation> tableNameToQRelation = new HashMap<>();
		for (QColumn c : idToQColumn.values()) {
			String key = c.getSrcRelation().toCalledName();
			QRelation gotRelation = tableNameToQRelation.get(key);
			if (gotRelation == null) {
				tableNameToQRelation.put(key, c.getSrcRelation());
			} else {
				// a conflict happens
				gotRelation.obtainTableAlias();
				c.getSrcRelation().obtainTableAlias();
			}
		}
	}

	public void fillGroup(QColumn key) {
		query.addGroup(key);
		stage = Stage.GROUP_BY;
	}

	public void fillSortSpec(QSortSpec o) {
		query.addSortSpec(o);
		stage = Stage.ORDER_BY;
	}

	public void fillDistinct() {
		query.setDistinct();
		stage = Stage.DONE;
	}

	public void fillWhereCondition(QCondition cond) {
		query.setWhereCondition(cond);
		stage = Stage.WHERE;
	}

	public void fillHavingCondition(QCondition cond) {
		query.setHavingCondition(cond);
		stage = Stage.HAVING;
	}

	public void setStageWindow() {
		stage = Stage.WINDOW;
	}

}
