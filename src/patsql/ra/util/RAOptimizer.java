package patsql.ra.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;
import patsql.entity.table.WinColSchema;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.sort.SortKey;
import patsql.ra.operator.BaseTable;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;
import patsql.ra.operator.Window;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.UnaryPred;

import java.util.Set;

public class RAOptimizer {

	public static RAOperator optimize(RAOperator program) {
		RAOperator r1 = deleteUnusedAggCols(program);
		return r1;
	}

	public static RAOperator deleteUnusedAggCols(RAOperator program) {
		Map<RAOperator, Set<Integer>> usedIds = collectUsedColIDs(program);

		// update aggregation columns with collected ones.
		RAUtils.traverse(program, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.GROUPBY) {
					GroupBy p = (GroupBy) op;

					Set<Integer> targetIDs = new HashSet<>();
					for (Entry<RAOperator, Set<Integer>> e : usedIds.entrySet()) {
						if (op == e.getKey())
							continue; // ignore the IDs used by itself
						targetIDs.addAll(e.getValue());
					}

					List<AggColSchema> used = new ArrayList<>();
					for (AggColSchema sc : p.ags.aggColSchemas) {
						if (targetIDs.contains(sc.id)) {
							used.add(sc);
						}
					}
					p.ags = new Aggregators(used.toArray(new AggColSchema[0]));

					// use the used IDs instead of reduced ones.
					Set<Integer> idsReduced = new HashSet<>();
					for (ColSchema c : p.keys.colSchemas)
						idsReduced.addAll(usedIDs(c));
					for (AggColSchema ac : used)
						idsReduced.addAll(usedIDs(ac));
					usedIds.put(op, idsReduced);
				} else if (op.kind == RA.WINDOWFUNC) {
					Window p = (Window) op;

					Set<Integer> targetIDs = new HashSet<>();
					for (Entry<RAOperator, Set<Integer>> e : usedIds.entrySet()) {
						if (op == e.getKey())
							continue; // ignore the IDs used by itself
						targetIDs.addAll(e.getValue());
					}

					List<WinColSchema> used = new ArrayList<>();
					for (WinColSchema sc : p.cols) {
						if (targetIDs.contains(sc.id)) {
							used.add(sc);
						}
					}
					p.cols = used.toArray(new WinColSchema[0]);

					// use the used IDs instead of reduced ones.
					Set<Integer> idsReduced = new HashSet<>();
					for (ColSchema c : p.cols)
						idsReduced.addAll(usedIDs(c));
					for (WinColSchema wc : used)
						idsReduced.addAll(usedIDs(wc));
					usedIds.put(op, idsReduced);
				}
				return true;
			}
		});

		return program;

	}

	public static RAOperator deleteUnusedColsFromBaseTable(RAOperator program) {
		Set<Integer> targetIDs = new HashSet<>();
		for (Set<Integer> v : collectUsedColIDs(program).values()) {
			targetIDs.addAll(v);
		}

		// update columns in BaseTable
		RAUtils.traverse(program, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.BASETABLE) {
					BaseTable bt = (BaseTable) op;

					List<ColSchema> used = new ArrayList<>();
					for (ColSchema sc : bt.renamedCols) {
						if (targetIDs.contains(sc.id)) {
							used.add(sc);
						}
					}

					bt.renamedCols = used.toArray(new ColSchema[0]);
				}
				return true;
			}
		});

		return program;
	}

	private static Map<RAOperator, Set<Integer>> collectUsedColIDs(RAOperator program) {
		Map<RAOperator, Set<Integer>> ret = new HashMap<>();

		RAUtils.traverse(program, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				switch (op.kind) {
				case ROOT:
				case DISTINCT:
				case BASETABLE:
					break;
				case SORT: {
					Sort p = (Sort) op;
					Set<Integer> ids = new HashSet<>();
					for (SortKey key : p.sortKeys.keys) {
						ids.addAll(usedIDs(key.col));
					}
					ret.put(op, ids);
					break;
				}
				case PROJECTION: {
					Projection p = (Projection) op;
					Set<Integer> ids = new HashSet<>();
					for (ColSchema sc : p.projCols) {
						ids.addAll(usedIDs(sc));
					}
					ret.put(op, ids);
					break;
				}
				case SELECTION: {
					Selection p = (Selection) op;
					Set<Integer> ids = new HashSet<>();
					for (Predicate pred : p.condition.predicates) {
						if (pred instanceof BinaryPred) {
							ColSchema sc = ((BinaryPred) pred).left;
							ids.addAll(usedIDs(sc));
						} else if (pred instanceof UnaryPred) {
							ColSchema sc = ((UnaryPred) pred).operand;
							ids.addAll(usedIDs(sc));
						} else if (pred instanceof Disjunction) {
							// support "OR"
							for (Predicate pr : ((Disjunction) pred).predicates) {
								if (pr instanceof BinaryPred) {
									ColSchema sc = ((BinaryPred) pr).left;
									ids.addAll(usedIDs(sc));
								} else if (pr instanceof UnaryPred) {
									ColSchema sc = ((UnaryPred) pr).operand;
									ids.addAll(usedIDs(sc));
								}
							}
						}
					}
					ret.put(op, ids);
					break;
				}
				case GROUPBY: {
					GroupBy p = (GroupBy) op;
					Set<Integer> ids = new HashSet<>();
					for (ColSchema sc : p.keys.colSchemas) {
						ids.addAll(usedIDs(sc));
					}
					for (AggColSchema sc : p.ags.aggColSchemas) {
						ids.addAll(usedIDs(sc));
					}
					ret.put(op, ids);
					break;
				}
				case WINDOWFUNC: {
					Window p = (Window) op;
					Set<Integer> ids = new HashSet<>();
					for (WinColSchema c : p.cols) {
						if (c.src.isPresent()) {
							ids.addAll(usedIDs(c.src.get()));
						}
						for (ColSchema sc : c.partKey.colSchemas) {
							ids.addAll(usedIDs(sc));
						}
						for (SortKey key : c.orderKey.keys) {
							ids.addAll(usedIDs(key.col));
						}
					}
					ret.put(op, ids);
					break;
				}
				case JOIN: {
					Join p = (Join) op;
					Set<Integer> ids = new HashSet<>();
					for (JoinKeyPair pair : p.condition.pairs) {
						ids.addAll(usedIDs(pair.left));
						ids.addAll(usedIDs(pair.right));
					}
					ret.put(op, ids);
					break;
				}
				case LEFTJOIN: {
					LeftJoin p = (LeftJoin) op;
					Set<Integer> ids = new HashSet<>();
					for (JoinKeyPair pair : p.condition.pairs) {
						ids.addAll(usedIDs(pair.left));
						ids.addAll(usedIDs(pair.right));
					}
					ret.put(op, ids);
					break;
				}
				}
				return true;
			}
		});
		return ret;
	}

	private static List<Integer> usedIDs(ColSchema sc) {
		List<Integer> usedIDs = new ArrayList<>();
		for (ColSchema s : recursivelyUsedIDs(sc)) {
			usedIDs.add(s.id);
		}
		return usedIDs;
	}

	private static List<ColSchema> recursivelyUsedIDs(ColSchema sc) {
		if (sc instanceof AggColSchema) {
			AggColSchema ag = (AggColSchema) sc;
			return Arrays.asList(sc, ag.src);
		} else if (sc instanceof WinColSchema) {
			WinColSchema wc = (WinColSchema) sc;
			List<ColSchema> ret = new ArrayList<>();
			ret.add(sc);
			if (wc.src.isPresent())
				ret.add(wc.src.get());
			ret.addAll(Arrays.asList(wc.partKey.colSchemas));
			for (SortKey sk : wc.orderKey.keys)
				ret.add(sk.col);
			return ret;
		} else {
			return Arrays.asList(sc);
		}
	}

}
