package patsql.entity.table.agg;

import java.util.Arrays;

import patsql.entity.table.ColSchema;

public class GroupKeys {
	public final ColSchema[] colSchemas;

	public GroupKeys(ColSchema... colSchemas) {
		this.colSchemas = colSchemas;
	}

	public boolean isEmpty() {
		return colSchemas.length == 0;
	}

	public static GroupKeys nil() {
		return new GroupKeys();
	}

	@Override
	public String toString() {
		return Arrays.toString(colSchemas);
	}

}
