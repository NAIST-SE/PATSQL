package patsql.ra.operator;

import patsql.entity.table.BitTable;
import patsql.entity.table.Table;
import patsql.ra.predicate.Conjunction;

public class Selection extends RAOperator {
	public RAOperator child;
	public Conjunction condition;

	public Selection(RAOperator child, Conjunction condition) {
		super();
		this.child = child;
		this.condition = condition;
	}

	public static Selection empty() {
		return new Selection(null, null);
	}

	@Override
	public Table eval(TableEnv env) {
		BitTable tmp = new BitTable(child.eval(env));
		return tmp.selection(condition).toTable();
	}

	@Override
	public Selection clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Selection clone = new Selection(c, condition/* shallow copy */);
		clone.id = this.id;
		return clone;
	}

}
