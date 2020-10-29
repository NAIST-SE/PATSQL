package patsql.ra.operator;

import patsql.entity.table.BitTable;
import patsql.entity.table.Table;
import patsql.ra.predicate.JoinCond;

public class Join extends RAOperator {
	public RAOperator childL;
	public RAOperator childR;
	public JoinCond condition;

	public Join(RAOperator childL, RAOperator childR, JoinCond condition) {
		super();
		this.childL = childL;
		this.childR = childR;
		this.condition = condition;
	}

	public static Join empty() {
		return new Join(null, null, null);
	}

	@Override
	public Table eval(TableEnv env) {
		BitTable l = new BitTable(childL.eval(env));
		BitTable r = new BitTable(childR.eval(env));
		return l.join(r).selection(condition).toTable();
	}

	@Override
	public Join clone() {
		RAOperator l = (childL == null) ? null : childL.clone();
		RAOperator r = (childR == null) ? null : childR.clone();
		Join clone = new Join(l, r, condition);
		clone.id = this.id;
		return clone;
	}

}
