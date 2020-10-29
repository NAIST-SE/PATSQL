package patsql.ra.operator;

import patsql.entity.table.Table;

public abstract class RAOperator {
	public final RA kind;

	protected int id;
	private static int idCount = 0;

	public RAOperator() {
		kind = RA.from(this);
		id = idCount;
		idCount++;
	}

	public int ID() {
		return id;
	}

	public abstract Table eval(TableEnv env);

	@Override
	public abstract RAOperator clone();

}
