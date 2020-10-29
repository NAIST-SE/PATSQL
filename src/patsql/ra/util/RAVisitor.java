package patsql.ra.util;

import patsql.ra.operator.RAOperator;

public interface RAVisitor {

	/** @return true if the search should continue. */
	public boolean on(RAOperator op);

}
