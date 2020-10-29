package patsql.ra.predicate;

import java.util.HashMap;

import patsql.entity.table.Cell;

/**
 * This class is a type alias of HashMap<Integer, Cell> to represent an
 * environment for evaluation. The keys of this map are column IDs and the
 * values are actual cells.
 */
@SuppressWarnings("serial")
public class PredEnv extends HashMap<Integer, Cell> {

	@Override
	public Cell get(Object cid) {
		Cell c = super.get(cid);
		if (c == null) {
			throw new IllegalStateException("Column id (" + cid + ") doesn't exist.");
		}
		return c;
	}

}
