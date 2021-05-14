package patsql.generator.sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

import patsql.entity.table.AggColSchema;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.DateFuncColSchema;
import patsql.entity.table.DateValue;
import patsql.entity.table.WinColSchema;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.generator.sql.SQLizeState.Stage;
import patsql.generator.sql.query.QAggregatedColumn;
import patsql.generator.sql.query.QColumn;
import patsql.generator.sql.query.QColumnConstant;
import patsql.generator.sql.query.QCondition;
import patsql.generator.sql.query.QConditionBinaryOperator;
import patsql.generator.sql.query.QConditionTrue;
import patsql.generator.sql.query.QDateFuncColumn;
import patsql.generator.sql.query.QJoinSpec;
import patsql.generator.sql.query.QJoinSpec.JoinType;
import patsql.generator.sql.query.QQuery;
import patsql.generator.sql.query.QRelationBinaryOperator;
import patsql.generator.sql.query.QRelationBinaryOperator.Operator;
import patsql.generator.sql.query.QRelationUnaryOperator;
import patsql.generator.sql.query.QSingleColumn;
import patsql.generator.sql.query.QSingleColumnCondition;
import patsql.generator.sql.query.QSingleRelation;
import patsql.generator.sql.query.QSortSpec;
import patsql.generator.sql.query.QSortSpec.Ordering;
import patsql.generator.sql.query.QWindowFuncColumn;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Distinct;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Root;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;
import patsql.ra.operator.Window;
import patsql.ra.predicate.BinaryOp;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.TruePred;
import patsql.ra.predicate.UnaryOp;
import patsql.ra.predicate.UnaryPred;
import patsql.ra.util.RAUtils;

public class SQLUtil {

	public static String generateSQL(RAOperator program) {
		SQLizeState state = generateSQLizeState(program);
		state.createTopQuery();
		QQuery query = state.query;
		SQLPostprocessor.postprocess(query);
		String str = query.toString();
		str = reformatSQL(str);
		return str;
	}

	public static String reformatSQL(String sql) {
		String wsRemoved = removeExtraWhitespaces(sql);
		String str = new BasicFormatterImpl().format(wsRemoved);
		String sep = System.lineSeparator();
		str = str.replaceAll(sep + "    ", sep);// remove left padding.
		str = reformatWindowFunc(str);
		str = reformatStringAgg(str);
		str = reformatExtract(str);
		return str;
	}

	public static String removeExtraWhitespaces(String str) {
		return str.replaceAll("\\s+", " ").replaceAll("\\( ", "(").replaceAll(" \\)", ")").replaceAll(" ,(?! )", ", ")
				.trim();
	}

	static String reformatWindowFunc(String str) {
		Pattern pat = Pattern.compile("(?<func>\\S+\\([^)]*\\)) OVER \\(" //
				+ "(PARTITION\\s+BY\\s+(?<part>\\S+(,\\s*\\S+)*))?\\s*"
				+ "(ORDER BY\\s+(?<sort>\\S+ (ASC|DESC)(,\\s*\\S+ (ASC|DESC))*))?\\)", Pattern.MULTILINE);
		Matcher matcher = pat.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String func = matcher.group("func").replaceAll("\\s+", " ");
			String part = matcher.group("part");
			if (part != null)
				part = part.replaceAll("\\s+", " ");
			String sort = matcher.group("sort");
			if (sort != null)
				sort = sort.replaceAll("\\s+", " ");
			if (part == null && sort == null) {
				matcher.appendReplacement(sb, func + " OVER ()");
			} else if (part == null) {
				matcher.appendReplacement(sb, func + " OVER (ORDER BY " + sort + ")");
			} else if (sort == null) {
				matcher.appendReplacement(sb, func + " OVER (PARTITION BY " + part + ")");
			} else {
				matcher.appendReplacement(sb, func + " OVER (PARTITION BY " + part + " ORDER BY " + sort + ")");
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	static String reformatStringAgg(String str) {
		return str.replaceAll("(?m)string_agg\\((\\S+),\\s+([^)]+)\\)", "string_agg($1, $2)");
	}

	static String reformatExtract(String str) {
		return str.replaceAll("(?m)EXTRACT\\((\\S+)\\s+FROM\\s+(\\S+)\\)", "EXTRACT($1 FROM $2)");
	}

	public static SQLizeState generateSQLizeState(RAOperator op) {
		switch (op.kind) {
		case BASETABLE: {
			BaseTable bt = (BaseTable) op;
			QSingleRelation singleRelation = new QSingleRelation(bt.tableId);
			SQLizeState ret = new SQLizeState(singleRelation);
			for (ColSchema col : bt.renamedCols) {
				QColumn c;
				if (col instanceof DateFuncColSchema) {
					DateFuncColSchema dfcol = (DateFuncColSchema) col;
					c = new QDateFuncColumn(dfcol.func, new QSingleColumn(singleRelation, dfcol.src.name));
				} else {
					c = new QSingleColumn(singleRelation, col.name);
				}
				ret.registerCols(col.id, c);
			}
			return ret;
		}
		case DISTINCT: {
			Distinct distinct = (Distinct) op;
			RAOperator child = distinct.child;
			SQLizeState tmp = generateSQLizeState(child);
			tmp.fillDistinct();
			return tmp;
		}
		case GROUPBY: {
			GroupBy gb = (GroupBy) op;
			RAOperator child = gb.child;

			SQLizeState tmp = generateSQLizeState(child);
			SQLizeState ret = tmp.stage.compareTo(Stage.GROUP_BY) >= 0 ? tmp.createSubQuery() : tmp;

			LinkedHashMap<Integer, QColumn> newMap = new LinkedHashMap<>();

			// fill grouping keys
			for (ColSchema colSchema : gb.keys.colSchemas) {
				QColumn col = ret.idToQColumn.get(colSchema.id);
				assert col != null;
				ret.fillGroup(col);
				newMap.put(colSchema.id, col);
			}

			// register aggregation columns.
			for (AggColSchema acs : gb.ags.aggColSchemas) {
				QColumn col = ret.idToQColumn.get(acs.src.id);
				assert col != null : acs.src;
				QAggregatedColumn aggCol = new QAggregatedColumn(acs.agg, col);
				newMap.put(acs.id, aggCol);
			}

			ret.resetIdToQColumnWith(newMap);

			return ret;
		}
		case JOIN: {
			Join join = (Join) op;
			SQLizeState stateL = generateSQLizeState(join.childL);
			SQLizeState stateR = generateSQLizeState(join.childR);
			return join(stateL, stateR, join.condition);
		}
		case LEFTJOIN: {
			LeftJoin lj = (LeftJoin) op;
			SQLizeState stateL = generateSQLizeState(lj.childL);
			SQLizeState stateR = generateSQLizeState(lj.childR);
			return leftJoin(stateL, stateR, lj.condition);
		}
		case WINDOWFUNC: {
			Window win = (Window) op;
			RAOperator child = win.child;
			SQLizeState ret = generateSQLizeState(child);

			for (WinColSchema wcs : win.cols) {
				Optional<QColumn> srcCol = wcs.src.map(s -> ret.idToQColumn.get(s.id));
				assert !srcCol.isPresent() || srcCol.get() != null;

				List<QColumn> partCols = new ArrayList<>();
				for (ColSchema colSchema : wcs.partKey.colSchemas) {
					QColumn col = ret.idToQColumn.get(colSchema.id);
					assert col != null;
					partCols.add(col);
				}

				List<QSortSpec> sortSpecs = new ArrayList<>();
				for (SortKey sortKey : wcs.orderKey.keys) {
					QColumn qCol = ret.idToQColumn.get(sortKey.col.id);
					assert qCol != null;

					Ordering ordering;
					if (sortKey.order == Order.Asc) {
						ordering = Ordering.ASC;
					} else if (sortKey.order == Order.Desc) {
						ordering = Ordering.DESC;
					} else {
						throw new RuntimeException("unexpected Order: " + sortKey.order);
					}

					sortSpecs.add(new QSortSpec(qCol, ordering));
				}

				QWindowFuncColumn winCol = new QWindowFuncColumn(wcs.func, srcCol, partCols, sortSpecs);
				ret.idToQColumn.put(wcs.id, winCol);
			}

			ret.setStageWindow();
			return ret;
		}
		case PROJECTION: {
			Projection proj = (Projection) op;
			RAOperator child = proj.child;
			SQLizeState tmp = generateSQLizeState(child);
			List<QColumn> cols = new ArrayList<>();
			for (ColSchema projCol : proj.projCols) {
				cols.add(tmp.idToQColumn.get(projCol.id));
			}
			tmp.fillSelectColumn(cols);
			return tmp;
		}
		case ROOT: {
			Root root = (Root) op;
			RAOperator child = root.child;
			return generateSQLizeState(child);
		}
		case SELECTION: {
			Selection sel = (Selection) op;
			SQLizeState tmp = generateSQLizeState(sel.child);
			if (tmp.stage.compareTo(Stage.HAVING) <= 0 && tmp.stage.compareTo(Stage.WHERE) > 0) {
				QCondition qcond = predToQCond(sel.condition, tmp.idToQColumn);
				tmp.fillHavingCondition(qcond);
				return tmp;
			} else {
				SQLizeState ret = tmp.stage.compareTo(Stage.HAVING) > 0 ? tmp.createSubQuery() : tmp;
				QCondition qcond = predToQCond(sel.condition, ret.idToQColumn);
				ret.fillWhereCondition(qcond);
				return ret;
			}
		}
		case SORT: {
			Sort sort = (Sort) op;

			SQLizeState tmp = generateSQLizeState(sort.child);

			SQLizeState ret;
			if (Arrays.stream(sort.sortKeys.keys) //
					.map(sortKey -> tmp.idToQColumn.get(sortKey.col.id))
					.anyMatch(qCol -> qCol instanceof QWindowFuncColumn)) {
				ret = tmp.createSubQuery();
				ret.fillSelectColumn(ret.idToQColumn.values());
			} else {
				ret = tmp;
			}

			for (SortKey sortKey : sort.sortKeys.keys) {
				QColumn qCol = ret.idToQColumn.get(sortKey.col.id);
				assert qCol != null;

				Ordering ordering;
				if (sortKey.order == Order.Asc) {
					ordering = Ordering.ASC;
				} else if (sortKey.order == Order.Desc) {
					ordering = Ordering.DESC;
				} else {
					throw new RuntimeException("unexpected Order: " + sortKey.order);
				}

				ret.fillSortSpec(new QSortSpec(qCol, ordering));
			}

			return ret;
		}
		default:
			throw new RuntimeException("unexpected operator: " + op.getClass());
		}
	}

	private static SQLizeState join(SQLizeState a, SQLizeState b, JoinCond cond) {
		if (a.stage == Stage.FROM && b.stage == Stage.FROM) {
			if (b.query.fromTables.joins.isEmpty()) {
				// join w/o subquery
				a.idToQColumn.putAll(b.idToQColumn);
				QCondition qcond = predToQCond(cond, a.idToQColumn);
				a.fillJoin(new QJoinSpec(JoinType.INNER, b.query.fromTables.firstRelation, qcond));
				return a;

			} else if (a.query.fromTables.joins.isEmpty()) {
				// swap
				return join(b, a, cond);

			} else {
				// join with subquery
				// not expected to be used by synthesized programs
				SQLizeState subb = b.createSubQuery();
				a.idToQColumn.putAll(subb.idToQColumn);
				QCondition qcond = predToQCond(cond, a.idToQColumn);
				a.fillJoin(new QJoinSpec(JoinType.INNER, subb.query.fromTables.firstRelation, qcond));
				return a;
			}

		} else if (a.stage == Stage.FROM) {
			// join with subquery
			SQLizeState subb = b.createSubQuery();
			a.idToQColumn.putAll(subb.idToQColumn);
			QCondition qcond = predToQCond(cond, a.idToQColumn);
			a.fillJoin(new QJoinSpec(JoinType.INNER, subb.query.fromTables.firstRelation, qcond));
			return a;

		} else if (b.stage == Stage.FROM) {
			// swap
			return join(b, a, cond);

		} else {
			// join with subquery (both side)
			SQLizeState suba = a.createSubQuery();
			SQLizeState subb = b.createSubQuery();
			suba.idToQColumn.putAll(subb.idToQColumn);
			QCondition qcond = predToQCond(cond, suba.idToQColumn);
			suba.fillJoin(new QJoinSpec(JoinType.INNER, subb.query.fromTables.firstRelation, qcond));
			return suba;
		}
	}

	private static SQLizeState leftJoin(SQLizeState l, SQLizeState r, JoinCond cond) {
		if (l.stage == Stage.FROM && r.stage == Stage.FROM) {
			if (r.query.fromTables.joins.isEmpty()) {
				// join w/o subquery
				l.idToQColumn.putAll(r.idToQColumn);
				QCondition qcond = predToQCond(cond, l.idToQColumn);
				l.fillJoin(new QJoinSpec(JoinType.LEFT, r.query.fromTables.firstRelation, qcond));
				return l;

			} else {
				// join with subquery
				SQLizeState subR = r.createSubQuery();
				l.idToQColumn.putAll(subR.idToQColumn);
				QCondition qcond = predToQCond(cond, l.idToQColumn);
				l.fillJoin(new QJoinSpec(JoinType.LEFT, subR.query.fromTables.firstRelation, qcond));
				return l;
			}

		} else if (l.stage == Stage.FROM) {
			// join with subquery
			SQLizeState subR = r.createSubQuery();
			l.idToQColumn.putAll(subR.idToQColumn);
			QCondition qcond = predToQCond(cond, l.idToQColumn);
			l.fillJoin(new QJoinSpec(JoinType.LEFT, subR.query.fromTables.firstRelation, qcond));
			return l;

		} else {
			// join with subquery (both side)
			SQLizeState subL = l.createSubQuery();
			SQLizeState subR = r.createSubQuery();
			subL.idToQColumn.putAll(subR.idToQColumn);
			QCondition qcond = predToQCond(cond, subL.idToQColumn);
			subL.fillJoin(new QJoinSpec(JoinType.LEFT, subR.query.fromTables.firstRelation, qcond));
			return subL;
		}
	}

	private static QCondition predToQCond(Predicate pred, Map<Integer, QColumn> idToQColumn) {
		if (pred instanceof BinaryPred) {
			BinaryPred bp = (BinaryPred) pred;

			BinaryOp bo = bp.op;
			Operator op;
			switch (bo) {
			case Eq:
				op = Operator.EQ;
				break;
			case NotEq:
				op = Operator.NE;
				break;
			case Gt:
				op = Operator.GT;
				break;
			case Geq:
				op = Operator.GE;
				break;
			case Lt:
				op = Operator.LT;
				break;
			case Leq:
				op = Operator.LE;
				break;
			default:
				throw new RuntimeException("unexpected: " + bo);
			}

			QCondition leftCond = new QSingleColumnCondition(idToQColumn.get(bp.left.id));

			Cell right = bp.right;
			QSingleColumnCondition rightCond;
			switch (right.type) {
			case Int:
				rightCond = new QSingleColumnCondition(new QColumnConstant<Integer>(Integer.valueOf(right.value)));
				break;
			case Dbl:
				rightCond = new QSingleColumnCondition(new QColumnConstant<Double>(Double.valueOf(right.value)));
				break;
			case Date:
				rightCond = new QSingleColumnCondition(
						new QColumnConstant<LocalDate>(DateValue.parse(right.value).date));
				break;
			case Str:
				rightCond = new QSingleColumnCondition(new QColumnConstant<String>(right.value));
				break;
			case Null:
				throw new RuntimeException("The only operator for NULL is IS");
			default:
				throw new RuntimeException("unexpected right type: " + right.type);
			}

			return new QRelationBinaryOperator(op, leftCond, rightCond);

		} else if (pred instanceof Conjunction) {
			Conjunction conjunction = (Conjunction) pred;
			QCondition ret = null;
			for (Predicate subpred : conjunction.predicates) {
				QCondition qcond = predToQCond(subpred, idToQColumn);
				if (ret == null) {
					ret = qcond;
				} else {
					ret = new QConditionBinaryOperator(QConditionBinaryOperator.Operator.AND, ret, qcond);
				}
			}
			return ret;

		} else if (pred instanceof Disjunction) {
			Disjunction disjunction = (Disjunction) pred;
			QCondition ret = null;
			for (Predicate subpred : disjunction.predicates) {
				QCondition qcond = predToQCond(subpred, idToQColumn);
				if (ret == null) {
					ret = qcond;
				} else {
					ret = new QConditionBinaryOperator(QConditionBinaryOperator.Operator.OR, ret, qcond);
				}
			}
			return ret;

		} else if (pred instanceof JoinCond) {
			JoinCond joinCond = (JoinCond) pred;
			QCondition ret = null;
			for (JoinKeyPair jkp : joinCond.pairs) {
				QCondition qcond = predToQCond(jkp, idToQColumn);
				if (ret == null) {
					ret = qcond;
				} else {
					ret = new QConditionBinaryOperator(QConditionBinaryOperator.Operator.AND, ret, qcond);
				}
			}
			return ret;

		} else if (pred instanceof JoinKeyPair) {
			JoinKeyPair jkp = (JoinKeyPair) pred;
			QCondition condL = new QSingleColumnCondition(idToQColumn.get(jkp.left.id));
			QCondition condR = new QSingleColumnCondition(idToQColumn.get(jkp.right.id));
			return new QRelationBinaryOperator(Operator.EQ, condL, condR);

		} else if (pred instanceof UnaryPred) {
			UnaryPred up = (UnaryPred) pred;

			QCondition condL = new QSingleColumnCondition(idToQColumn.get(up.operand.id));

			patsql.generator.sql.query.QRelationUnaryOperator.Operator operator;
			if (up.op == UnaryOp.IsNull) {
				operator = patsql.generator.sql.query.QRelationUnaryOperator.Operator.IS_NULL;
			} else if (up.op == UnaryOp.IsNotNull) {
				operator = patsql.generator.sql.query.QRelationUnaryOperator.Operator.IS_NOT_NULL;
			} else {
				throw new RuntimeException("unexpected unary operator: " + up.op);
			}

			return new QRelationUnaryOperator(operator, condL);

		} else if (pred instanceof TruePred) {
			return new QConditionTrue();
		} else {
			throw new RuntimeException("unexpected predicate: " + pred.getClass());
		}
	}

}
