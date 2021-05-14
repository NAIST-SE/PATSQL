package patsql.generator.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import patsql.generator.sql.query.QAggregatedColumn;
import patsql.generator.sql.query.QColumn;
import patsql.generator.sql.query.QColumnConstant;
import patsql.generator.sql.query.QColumnStar;
import patsql.generator.sql.query.QCondition;
import patsql.generator.sql.query.QConditionBinaryOperator;
import patsql.generator.sql.query.QConditionTrue;
import patsql.generator.sql.query.QDateFuncColumn;
import patsql.generator.sql.query.QJoinSpec;
import patsql.generator.sql.query.QJoinedTables;
import patsql.generator.sql.query.QQualifiedCol;
import patsql.generator.sql.query.QQuery;
import patsql.generator.sql.query.QRelation;
import patsql.generator.sql.query.QRelationBinaryOperator;
import patsql.generator.sql.query.QRelationUnaryOperator;
import patsql.generator.sql.query.QSingleColumn;
import patsql.generator.sql.query.QSingleColumnCondition;
import patsql.generator.sql.query.QSortSpec;
import patsql.generator.sql.query.QSubQuery;
import patsql.generator.sql.query.QWindowFuncColumn;

public class SQLPostprocessor {

	public static void postprocess(QQuery q) {
		renumberTableAlias(q, 0);
		removeUselessColumnAlias(q);
		removeUnusedColumns(q, true, Collections.emptySet());
	}

	private static int renumberTableAlias(QQuery q, int counter) {
		int cnt = counter;
		QJoinedTables fromTables = q.fromTables;

		// process subqueries
		if (fromTables.firstRelation instanceof QSubQuery) {
			QSubQuery sq = (QSubQuery) fromTables.firstRelation;
			cnt = renumberTableAlias(sq.query, cnt);
		}
		for (QJoinSpec joinSpec : fromTables.joins) {
			QRelation r = joinSpec.getRelation();
			if (r instanceof QSubQuery) {
				QSubQuery sq = (QSubQuery) r;
				cnt = renumberTableAlias(sq.query, cnt);
			}
		}

		// process this query
		if (fromTables.firstRelation.tableAlias != null) {
			fromTables.firstRelation.refillTableAliasWith(cnt++);
		}
		for (QJoinSpec joinSpec : fromTables.joins) {
			QRelation r = joinSpec.getRelation();
			if (r.tableAlias != null) {
				r.refillTableAliasWith(cnt++);
			}
		}

		return cnt;
	}

	private static void removeUselessColumnAlias(QQuery q) {
		for (QColumn c : q.selectColumns) {
			if (c instanceof QSingleColumn) {
				QSingleColumn sc = (QSingleColumn) c;
				if (sc.isAliased && sc.srcRelation.tableAlias == null) {
					String refColName = sc.referencedColName();
					if (sc.getColAlias().get().equals(refColName)) {
						sc.isAliased = false;
					}
				}
			}
		}

		QJoinedTables fromTables = q.fromTables;
		if (fromTables.firstRelation instanceof QSubQuery) {
			QSubQuery sq = (QSubQuery) fromTables.firstRelation;
			removeUselessColumnAlias(sq.query);
		}
		for (QJoinSpec joinSpec : fromTables.joins) {
			QRelation r = joinSpec.getRelation();
			if (r instanceof QSubQuery) {
				QSubQuery sq = (QSubQuery) r;
				removeUselessColumnAlias(sq.query);
			}
		}
	}

	private static void addToReferredColNameMap(Map<QRelation, Set<String>> referredColNameMap, QColumn col) {
		if (col instanceof QAggregatedColumn) {
			QAggregatedColumn acol = (QAggregatedColumn) col;
			referredColNameMap.computeIfAbsent(col.getSrcRelation(), key -> new HashSet<>())
					.add(acol.src.referencedColName());
		} else if (col instanceof QSingleColumn) {
			referredColNameMap.computeIfAbsent(col.getSrcRelation(), key -> new HashSet<>())
					.add(col.referencedColName());
		} else if (col instanceof QWindowFuncColumn) {
			QWindowFuncColumn wcol = (QWindowFuncColumn) col;
			for (QQualifiedCol qqcol : wcol.referredCols())
				referredColNameMap.computeIfAbsent(qqcol.r, key -> new HashSet<>()).add(qqcol.col);
		} // do nothing for QColumnStar and QColumnConstant
	}

	private static void removeUnusedColumns(QQuery q, boolean isTop, Set<String> referredColNames) {
		Map<QRelation, Set<String>> referredColNameMap = new HashMap<>();

		List<QColumn> newSelectColumns = new ArrayList<>();
		for (QColumn col : q.selectColumns) {
			Optional<String> outsideName = extractOutsideName(col);
			if (isTop || outsideName.isPresent() && referredColNames.remove(outsideName.get())) {
				newSelectColumns.add(col);
				addToReferredColNameMap(referredColNameMap, col);
			}
		}
		assert referredColNames.isEmpty() : referredColNames.toString();
		q.selectColumns = newSelectColumns;

		for (QJoinSpec js : q.fromTables.joins) {
			QCondition cond = js.getCondition();
			for (QColumn col : extractColumns(cond)) {
				addToReferredColNameMap(referredColNameMap, col);
			}
		}
		for (QColumn col : extractColumns(q.whereCondition)) {
			addToReferredColNameMap(referredColNameMap, col);
		}
		for (QColumn col : q.groups) {
			addToReferredColNameMap(referredColNameMap, col);
		}
		for (QColumn col : extractColumns(q.havingCondition)) {
			addToReferredColNameMap(referredColNameMap, col);
		}
		for (QSortSpec ss : q.sortSpecs) {
			addToReferredColNameMap(referredColNameMap, ss.c);
		}

		// process subqueries
		{
			QRelation r = q.fromTables.firstRelation;
			if (r instanceof QSubQuery) {
				QSubQuery subq = (QSubQuery) r;
				Set<String> referredColNamesForSub = referredColNameMap.getOrDefault(r, Collections.emptySet());
				removeUnusedColumns(subq.query, false, referredColNamesForSub);
			}
		}
		for (QJoinSpec js : q.fromTables.joins) {
			if (js.getRelation() instanceof QSubQuery) {
				QSubQuery subq = (QSubQuery) js.getRelation();
				Set<String> referredColNamesForSub //
						= referredColNameMap.getOrDefault(js.getRelation(), Collections.emptySet());
				removeUnusedColumns(subq.query, false, referredColNamesForSub);
			}
		}
	}

	private static Optional<String> extractOutsideName(QColumn col) {
		if (col instanceof QAggregatedColumn) {
			return Optional.of(col.getColAlias().get());
		} else if (col instanceof QDateFuncColumn) {
			return Optional.of(col.getColAlias().get());
		} else if (col instanceof QColumnConstant) {
			return Optional.empty();
		} else if (col instanceof QColumnStar) {
			return Optional.empty();
		} else if (col instanceof QSingleColumn) {
			return Optional.of(col.getColAlias().orElse(col.toString()));
		} else if (col instanceof QWindowFuncColumn) {
			return col.getColAlias();
		} else {
			throw new IllegalStateException();
		}
	}

	private static Set<QColumn> extractColumns(QCondition cond) {
		if (cond == null) {
			return Collections.emptySet();

		} else if (cond instanceof QConditionBinaryOperator) {
			QConditionBinaryOperator op = (QConditionBinaryOperator) cond;
			HashSet<QColumn> ret = new HashSet<>();
			ret.addAll(extractColumns(op.c1));
			ret.addAll(extractColumns(op.c2));
			return ret;

		} else if (cond instanceof QRelationBinaryOperator) {
			QRelationBinaryOperator op = (QRelationBinaryOperator) cond;
			HashSet<QColumn> ret = new HashSet<>();
			ret.addAll(extractColumns(op.c1));
			ret.addAll(extractColumns(op.c2));
			return ret;

		} else if (cond instanceof QRelationUnaryOperator) {
			QRelationUnaryOperator op = (QRelationUnaryOperator) cond;
			return new HashSet<>(extractColumns(op.c));

		} else if (cond instanceof QSingleColumnCondition) {
			QSingleColumnCondition scond = (QSingleColumnCondition) cond;
			return new HashSet<>(Arrays.asList(scond.c));
		} else if (cond instanceof QConditionTrue) {
			return Collections.emptySet();
		} else {
			throw new IllegalStateException(cond.getClass().toString());
		}
	}

}
