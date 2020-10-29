package patsql.ra.operator;

import patsql.entity.table.Table;
import patsql.ra.predicate.JoinCond;

public class LeftJoin extends RAOperator {
	public RAOperator childL;
	public RAOperator childR;
	public JoinCond condition;

	public LeftJoin(RAOperator childL, RAOperator childR, JoinCond condition) {
		super();
		this.childL = childL;
		this.childR = childR;
		this.condition = condition;
	}

	public static LeftJoin empty() {
		return new LeftJoin(null, null, null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table l = childL.eval(env);
		Table r = childR.eval(env);
		return l.leftJoin(r, condition);
	}

	@Override
	public LeftJoin clone() {
		RAOperator l = (childL == null) ? null : childL.clone();
		RAOperator r = (childR == null) ? null : childR.clone();
		LeftJoin clone = new LeftJoin(l, r, condition);
		clone.id = this.id;
		return clone;
	}

}
