package patsql.ra.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import patsql.entity.synth.Example;
import patsql.entity.table.Cell;
import patsql.entity.table.Table;
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

public class RAUtils {

	/**
	 * 
	 * insert {@code inserted} immediately above {@code location}.
	 * 
	 * @param entire   is an entire tree.
	 * @param inserted is an inserted operator.
	 * 
	 * @param location is a subtree in {@code entire}.
	 * @return cloned {@code entire} with {@code inserted} inserted above
	 *         {@code location}. If the parent of {@code location} is JOIN or LEFT
	 *         JOIN, {@code inserted} is inserted in the *left* child.
	 */
	public static RAOperator insert(RAOperator entire, RAOperator inserted, RAOperator location) {
		assert entire != null && inserted != null && location != null;
		RAOperator ret = entire.clone();

		if (ret.ID() == location.ID()) {
			setChild(inserted, 0, ret);
			return inserted;
		}

		insertSub(ret, inserted, location);
		return ret;
	}

	private static void insertSub(RAOperator current, RAOperator inserted, RAOperator location) {
		RAOperator[] children = children(current);
		for (int i = 0; i < children.length; i++) {
			RAOperator child = children[i];
			if (child.ID() == location.ID()) {
				// insert "inserted" between the current and child.
				setChild(current, i, inserted);
				setChild(inserted, 0, child); // the left most child is always filled
				return;
			}
			insertSub(child, inserted, location);
		}
	}

	public static RAOperator replace(RAOperator entire, RAOperator inserted, RAOperator location) {
		if (entire == null || inserted == null) {
			throw new IllegalStateException("null is contained in arguments.");
		}
		RAOperator ret = entire.clone();

		if (ret.ID() == location.ID()) {
			return inserted;
		}

		replaceSub(ret, inserted, location);
		return ret;
	}

	private static void replaceSub(RAOperator current, RAOperator inserted, RAOperator location) {
		RAOperator[] children = children(current);
		for (int i = 0; i < children.length; i++) {
			RAOperator child = children[i];
			if (child.ID() == location.ID()) {
				// replace the existing child with "inserted".
				setChild(current, i, inserted);
				return;
			}
			replaceSub(child, inserted, location);
		}
	}

	public static void setChild(RAOperator parent, int indexOfChildren, RAOperator child) {
		switch (parent.kind) {
		case ROOT: {
			Root p = (Root) parent;
			p.child = child;
			break;
		}
		case SORT: {
			Sort p = (Sort) parent;
			p.child = child;
			break;
		}
		case DISTINCT: {
			Distinct p = (Distinct) parent;
			p.child = child;
			break;
		}
		case PROJECTION: {
			Projection p = (Projection) parent;
			p.child = child;
			break;
		}
		case SELECTION: {
			Selection p = (Selection) parent;
			p.child = child;
			break;
		}
		case GROUPBY: {
			GroupBy p = (GroupBy) parent;
			p.child = child;
			break;
		}
		case WINDOWFUNC: {
			Window p = (Window) parent;
			p.child = child;
			break;
		}
		case JOIN: {
			Join p = (Join) parent;
			if (indexOfChildren == 0) {
				p.childL = child;
			} else {
				p.childR = child;
			}
			break;
		}
		case LEFTJOIN: {
			LeftJoin p = (LeftJoin) parent;
			if (indexOfChildren == 0) {
				p.childL = child;
			} else {
				p.childR = child;
			}
			break;
		}
		case BASETABLE: {
			throw new IllegalStateException("BaseTable cannot be a parent.");
		}
		}

	}

	public static RAOperator[] children(RAOperator parent) {
		final RAOperator[] EMPTY = new RAOperator[0];

		switch (parent.kind) {
		case ROOT: {
			Root p = (Root) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case SORT: {
			Sort p = (Sort) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case DISTINCT: {
			Distinct p = (Distinct) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case PROJECTION: {
			Projection p = (Projection) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case SELECTION: {
			Selection p = (Selection) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case GROUPBY: {
			GroupBy p = (GroupBy) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case WINDOWFUNC: {
			Window p = (Window) parent;
			return (p.child != null) ? new RAOperator[] { p.child } : EMPTY;
		}
		case JOIN: {
			Join p = (Join) parent;
			if (p.childL == null) {
				return (p.childR == null) ? //
						EMPTY//
						: new RAOperator[] { p.childR };
			} else {
				return (p.childR == null) ? //
						new RAOperator[] { p.childL } : //
						new RAOperator[] { p.childL, p.childR };
			}
		}
		case LEFTJOIN: {
			LeftJoin p = (LeftJoin) parent;
			if (p.childL == null) {
				return (p.childR == null) ? //
						EMPTY//
						: new RAOperator[] { p.childR };
			} else {
				return (p.childR == null) ? //
						new RAOperator[] { p.childL } : //
						new RAOperator[] { p.childL, p.childR };
			}
		}
		case BASETABLE: {
			return EMPTY;
		}
		}
		throw new IllegalStateException("unknown operator type : " + parent.kind);
	}

	public static void printTmpTables(RAOperator root, Example ex) {
		List<RAOperator> list = new ArrayList<>();
		RAUtils.traverse(root, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				list.add(op);
				return true;
			}
		});

		Collections.reverse(list);

		for (RAOperator r : list) {
			System.out.println(" ==== No." + r.ID() + " " + r.kind + " ==== ");
			Table tbl = r.eval(ex.tableEnv());
			System.out.println(tbl);
		}
	}

	public static void printTree(RAOperator op) {
		String str = buildTree(op);
		System.out.print(str);
	}

	private static String INDENT = "    ";

	public static String buildTree(RAOperator op) {
		StringBuilder sb = new StringBuilder();
		switch (op.kind) {
		case ROOT: {
			Root p = (Root) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));

			sb.append("Root(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case SORT: {
			Sort p = (Sort) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));
			sub.append(", " + p.sortKeys + "\n");

			sb.append("Sort(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case DISTINCT: {
			Distinct p = (Distinct) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));

			sb.append("Distinct(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case PROJECTION: {
			Projection p = (Projection) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));
			sub.append(", " + Arrays.toString(p.projCols) + "\n");

			sb.append("Projection(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case SELECTION: {
			Selection p = (Selection) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));
			sub.append(", " + p.condition + "\n");

			sb.append("Selection(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case GROUPBY: {
			GroupBy p = (GroupBy) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));
			sub.append(", " + p.keys + "\n");
			sub.append(", " + p.ags + "\n");

			sb.append("GroupBy(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case WINDOWFUNC: {
			Window p = (Window) op;
			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.child));
			sub.append(", " + Arrays.toString(p.cols) + "\n");

			sb.append("Window(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case JOIN: {
			Join p = (Join) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.childL));
			sub.append(", " + buildTree(p.childR));
			sub.append(", " + p.condition + "\n");

			sb.append("Join(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case LEFTJOIN: {
			LeftJoin p = (LeftJoin) op;

			StringBuilder sub = new StringBuilder();
			sub.append(buildTree(p.childL));
			sub.append(", " + buildTree(p.childR));
			sub.append(", " + p.condition + "\n");

			sb.append("LeftJoin(\n");
			sb.append(fill____(sub.toString()));
			sb.append(")\n");
			break;
		}
		case BASETABLE: {
			BaseTable p = (BaseTable) op;
			sb.append("BaseTable(" + p.tableId + ", ");
			sb.append(Arrays.toString(p.renamedCols) + ")\n");
			break;
		}
		}
		return sb.toString();
	}

	private static String fill____(String str) {
		return str.replaceAll("(?m)^.*$", INDENT + "$0");
	}

	public static void printSketch(RAOperator op) {
		String str = buildSketch(op);
		System.out.println(str);
	}

	public static String buildSketch(RAOperator op) {
		switch (op.kind) {
		case ROOT: {
			Root p = (Root) op;
			return " T " + buildSketch(p.child);
		}
		case SORT: {
			Sort p = (Sort) op;
			return " s " + buildSketch(p.child);
		}
		case DISTINCT: {
			Distinct p = (Distinct) op;
			return " δ " + buildSketch(p.child);
		}
		case PROJECTION: {
			Projection p = (Projection) op;
			return " π " + buildSketch(p.child);
		}
		case SELECTION: {
			Selection p = (Selection) op;
			return " σ " + buildSketch(p.child);
		}
		case GROUPBY: {
			GroupBy p = (GroupBy) op;
			return " γ " + buildSketch(p.child);
		}
		case WINDOWFUNC: {
			Window p = (Window) op;
			return " w " + buildSketch(p.child);
		}
		case JOIN: {
			Join p = (Join) op;
			return " ⋈θ (" + buildSketch(p.childL) + ") (" + buildSketch(p.childR) + ")";
		}
		case LEFTJOIN: {
			LeftJoin p = (LeftJoin) op;
			return " ⋈L (" + buildSketch(p.childL) + ") (" + buildSketch(p.childR) + ")";
		}
		case BASETABLE: {
			BaseTable p = (BaseTable) op;
			if (p.tableId != null)
				return " B" + p.tableId.replace("table", "").replace("input", "") + " ";
			else
				return " B ";
		}
		}
		throw new IllegalStateException("unknown operator type : " + op);
	}

	public static void traverse(RAOperator start, RAVisitor visitor) {
		Stack<RAOperator> stack = new Stack<>();
		stack.push(start);

		while (!stack.isEmpty()) {
			RAOperator op = stack.pop();
			boolean isContinue = visitor.on(op);
			if (!isContinue)
				break;

			// visit sub nodes
			for (RAOperator c : children(op)) {
				stack.push(c);
			}
		}
	}

	public static Set<Cell> usedConstants(RAOperator root) {
		Set<Cell> ret = new HashSet<>();
		traverse(root, new RAVisitor() {
			@Override
			public boolean on(RAOperator op) {
				if (op.kind == RA.SELECTION) {
					Selection p = (Selection) op;
					Set<Cell> consts = PredUtil.usedConstants(p.condition);
					ret.addAll(consts);
				}
				return true;
			}
		});
		return ret;
	}

}
