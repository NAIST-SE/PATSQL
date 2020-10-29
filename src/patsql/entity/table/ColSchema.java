package patsql.entity.table;

public class ColSchema {
	public final Type type;
	public final String name;
	public final int id;

	private static int colId = 0;

	public ColSchema(String name, Type type) {
		this.type = type;
		this.name = name;
		this.id = colId;
		colId++;
	}

	public ColSchema(String name, Type type, int id) {
		this.type = type;
		this.name = name;
		this.id = id;
	}

	public static int newID() {
		int ret = colId;
		colId++;
		return ret;
	}

	@Override
	public String toString() {
		return "[" + id + "] " + name + ":" + type;
	}

}
