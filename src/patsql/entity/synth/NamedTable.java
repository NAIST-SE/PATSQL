package patsql.entity.synth;

import patsql.entity.table.Table;

public class NamedTable {
	public final String name;
	public final Table table;

	public NamedTable(String name, Table table) {
		super();
		this.name = name;
		this.table = table;
	}

	@Override
	public String toString() {
		return "[" + name + "]\n" + table;
	}

}
