package patsql.ra.operator;

import patsql.entity.table.Table;
import patsql.entity.table.sort.SortKeys;

public class Sort extends RAOperator {
	public RAOperator child;
	public SortKeys sortKeys;

	public Sort(RAOperator child, SortKeys keys) {
		super();
		this.child = child;
		this.sortKeys = keys;
	}

	public static Sort empty() {
		return new Sort(null, null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table table = child.eval(env);
		return table.sort(sortKeys);
	}

	@Override
	public Sort clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Sort clone = new Sort(c, sortKeys);
		clone.id = this.id;
		return clone;
	}

}
