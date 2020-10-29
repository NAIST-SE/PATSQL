package patsql.ra.operator;

import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;

public class Projection extends RAOperator {
	public RAOperator child;
	public ColSchema[] projCols;

	public Projection(RAOperator child, ColSchema... projColIds) {
		super();
		this.child = child;
		this.projCols = projColIds;
	}

	public static Projection empty() {
		return new Projection(null, (ColSchema) null);
	}

	@Override
	public Table eval(TableEnv env) {
		return child.eval(env).project(colIds());
	}

	private int[] colIds() {
		int[] ret = new int[projCols.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = projCols[i].id;
		}
		return ret;
	}

	@Override
	public Projection clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Projection clone = new Projection(c, projCols/* shallow copy */);
		clone.id = this.id;
		return clone;
	}

}
