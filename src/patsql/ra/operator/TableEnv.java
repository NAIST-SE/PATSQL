package patsql.ra.operator;

import java.util.HashMap;

import patsql.entity.table.Table;

/**
 * This class is similar to the class "PredEnv". Refer to it.
 */
@SuppressWarnings("serial")
public class TableEnv extends HashMap<String, Table> {

	@Override
	public Table get(Object tid) {
		Table t = super.get(tid);
		if (t == null) {
			throw new IllegalStateException("Table id (" + tid + ") doesn't exist.");
		}
		return t;
	}

}
