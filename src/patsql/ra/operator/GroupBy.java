package patsql.ra.operator;

import patsql.entity.table.Table;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;

public class GroupBy extends RAOperator {
	public RAOperator child;
	public GroupKeys keys;
	public Aggregators ags;

	public GroupBy(RAOperator child, GroupKeys keys, Aggregators ags) {
		super();
		this.child = child;
		this.keys = keys;
		this.ags = ags;
	}

	public static GroupBy empty() {
		return new GroupBy(null, null, null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table table = child.eval(env);
		return table.groupBy(keys, ags);
	}

	@Override
	public GroupBy clone() {
		RAOperator c = (child == null) ? null : child.clone();
		GroupBy clone = new GroupBy(c, keys, ags);
		clone.id = this.id;
		return clone;
	}

}
