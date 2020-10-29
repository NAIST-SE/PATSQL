package patsql.entity.table.sort;

import patsql.entity.table.ColSchema;

public class SortKey {
	public final ColSchema col;
	public final Order order;

	public SortKey(ColSchema col, Order order) {
		this.col = col;
		this.order = order;
	}

	@Override
	public String toString() {
		return "[" + col.id + "] " + order;
	}

}
