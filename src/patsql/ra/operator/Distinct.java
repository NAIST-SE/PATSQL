package patsql.ra.operator;

import patsql.entity.table.Table;
import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;

public class Distinct extends RAOperator {
	public RAOperator child;

	public Distinct(RAOperator child) {
		super();
		this.child = child;
	}

	public static Distinct empty() {
		return new Distinct(null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table table = child.eval(env);
		Table result = table.groupBy(//
				new GroupKeys(table.schema()), //
				Aggregators.empty()//
		);
		return result;
	}

	@Override
	public Distinct clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Distinct clone = new Distinct(c);
		clone.id = this.id;
		return clone;
	}

}
