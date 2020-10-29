package patsql.synth.sketcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import patsql.ra.operator.BaseTable;
import patsql.ra.operator.Distinct;
import patsql.ra.operator.GroupBy;
import patsql.ra.operator.Join;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.Projection;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.operator.Root;
import patsql.ra.operator.Selection;
import patsql.ra.operator.Sort;
import patsql.ra.operator.Window;
import patsql.ra.util.RAUtils;
import patsql.ra.util.RAVisitor;

public class Sketcher implements Iterator<RAOperator>, Iterable<RAOperator> {

	private TreeSet<RAOperator> sketches = new TreeSet<>(Sketcher::compare);

	public Sketcher(int tableCount, boolean isOutputSorted) {
		sketches.addAll(seeds(tableCount, isOutputSorted));
	}

	static Collection<RAOperator> seeds(int tableCount, boolean isOutputSorted) {
		Set<RAOperator> joins = new TreeSet<>(Sketcher::compare);

		// First, add a sketch, "Root-π-R" or "Root-sort-π-R".
		Root root = Root.empty();
		Projection proj = Projection.empty();
		if (isOutputSorted) {
			Sort sort = Sort.empty();
			root.child = sort;
			sort.child = proj;
		} else {
			root.child = proj;
		}
		BaseTable base = BaseTable.empty();
		proj.child = base;
		joins.add(root);

		for (int i = 1; i < tableCount; i++) {
			Set<RAOperator> nextJoins = new TreeSet<>(Sketcher::compare);
			for (RAOperator join : joins) {
				nextJoins.addAll(extendedSketches(join, Arrays.asList(RA.JOIN, RA.LEFTJOIN)));
			}
			joins = nextJoins;
		}

		return joins;
	}

	@Override
	public Iterator<RAOperator> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	/**
	 * Returns set of sketches whose size is greater than the last ones.
	 * <ul>
	 * <li>First time: returns {Root-π-R}.</li>
	 * <li>Next time: returns sketch whose size is equal to or greater than that of
	 * previous ones.</li>
	 * </ul>
	 */
	@Override
	public RAOperator next() {
		List<RA> ras = new ArrayList<>(Arrays.asList(RA.values()));
		ras.removeAll(Arrays.asList(RA.ROOT, RA.SORT));

		RAOperator first = sketches.pollFirst();
		sketches.addAll(extendedSketches(first, ras));
		return first;
	}

	private static List<RAOperator> extendedSketches(RAOperator sketch, Collection<RA> ras) {
		List<RAOperator> ret = new ArrayList<>();
		for (RA insertedRA : ras) {
			RAUtils.traverse(sketch, new RAVisitor() {
				@Override
				public boolean on(RAOperator parent) {
					if (!RACombination.canBeChild(insertedRA, parent.kind))
						return true;

					for (RAOperator child : RAUtils.children(parent)) {
						if (RACombination.canBeChild(child.kind, insertedRA)) {
							RAOperator ins = defaultOp(insertedRA);
							RAOperator next = RAUtils.insert(sketch, ins, child);
							ret.add(next);
						}
					}
					return true;
				}
			});
		}
		return ret;
	}

	static final int compare(RAOperator r1, RAOperator r2) {
		if (sizeOf(r1) != sizeOf(r2)) {
			return sizeOf(r1) - sizeOf(r2);
		}

		List<RAOperator> l1 = serialize(r1);
		List<RAOperator> l2 = serialize(r2);

		if (l1.size() != l2.size()) {
			return l1.size() - l2.size();
		}

		for (int i = l1.size() - 1; i >= 0; i--) {
			RAOperator o1 = l1.get(i);
			RAOperator o2 = l2.get(i);

			int n1 = o1.kind.weight();
			int n2 = o2.kind.weight();

			if (n1 != n2) {
				return n1 - n2;
			}
		}
		return 0;
	}

	public static List<RAOperator> serialize(RAOperator op) {
		switch (op.kind) {
		case ROOT: {
			Root p = (Root) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case SORT: {
			Sort p = (Sort) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case DISTINCT: {
			Distinct p = (Distinct) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case PROJECTION: {
			Projection p = (Projection) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case SELECTION: {
			Selection p = (Selection) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case GROUPBY: {
			GroupBy p = (GroupBy) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case WINDOWFUNC: {
			Window p = (Window) op;
			List<RAOperator> ret = serialize(p.child);
			ret.add(0, p);
			return ret;
		}
		case JOIN: {
			Join p = (Join) op;
			List<RAOperator> l = serialize(p.childL);
			List<RAOperator> r = serialize(p.childR);
			List<RAOperator> ret = new LinkedList<>();
			if (compare(p.childL, p.childR) > 0) {
				ret.addAll(l);
				ret.addAll(r);
			} else {
				ret.addAll(r);
				ret.addAll(l);
			}
			ret.add(p);
			return ret;
		}
		case LEFTJOIN: {
			LeftJoin p = (LeftJoin) op;
			List<RAOperator> l = serialize(p.childL);
			List<RAOperator> r = serialize(p.childR);
			List<RAOperator> ret = new LinkedList<>();
			ret.addAll(l);
			ret.addAll(r);
			ret.add(p);
			return ret;
		}
		case BASETABLE: {
			BaseTable p = (BaseTable) op;
			List<RAOperator> ret = new LinkedList<>();
			ret.add(p);
			return ret;
		}
		}
		throw new IllegalStateException("unknown operator type : " + op);
	}

	/**
	 * @param ra is a kind of RA.
	 * @return a default operator to be inserted. JOIN/LEFT JOIN creates a operator
	 *         whose left child is no child and right child is BaseTable.
	 */
	public static RAOperator defaultOp(RA ra) {
		switch (ra) {
		case ROOT: {
			return Root.empty();
		}
		case SORT: {
			return Sort.empty();
		}
		case DISTINCT: {
			return Distinct.empty();
		}
		case PROJECTION: {
			return Projection.empty();
		}
		case SELECTION: {
			return Selection.empty();
		}
		case GROUPBY: {
			return GroupBy.empty();
		}
		case WINDOWFUNC: {
			return Window.empty();
		}
		case JOIN: {
			Join ret = Join.empty();
			ret.childR = BaseTable.empty();
			return ret;
		}
		case LEFTJOIN: {
			LeftJoin ret = LeftJoin.empty();
			ret.childR = BaseTable.empty();
			return ret;
		}
		case BASETABLE: {
			return BaseTable.empty();
		}
		}
		throw new IllegalStateException("unknown operator type : " + ra);
	}

	public static int sizeOf(RAOperator op) {
		int[] ret = new int[1];
		RAUtils.traverse(op, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.BASETABLE)
					return true;
				if (op.kind == RA.WINDOWFUNC) {
					ret[0] = ret[0] + 2; // The size of window is set to 2.999 (the highest in 2)
				}
				ret[0]++;
				return true;
			}
		});
		return ret[0];
	}

}
