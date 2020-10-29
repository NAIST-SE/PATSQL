package patsql.synth.filler;

import java.util.ArrayList;
import java.util.List;

import patsql.entity.table.Table;

/**
 * This class manages the result tables.
 */
public class TableMemo {
	final List<Table> pool = new ArrayList<>();

	public boolean lookup(Table table) {
		for (Table t : pool) {
			if (t.hasSameRows(table)) {
				return false;
			}
		}
		pool.add(table);
		return true;
	}

}
