package patsql.ra.operator;

import patsql.entity.table.Table;

public class Root extends RAOperator {
	public RAOperator child;

	public Root(RAOperator child) {
		super();
		this.child = child;
	}

	public static Root empty() {
		return new Root(null);
	}

	@Override
	public Table eval(TableEnv env) {
		return child.eval(env);
	}

	@Override
	public Root clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Root clone = new Root(c);
		clone.id = this.id;
		return clone;
	}

}
