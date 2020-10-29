package patsql.entity.table.sort;

public class SortKeys {
	public final SortKey[] keys;

	public SortKeys(SortKey... keys) {
		this.keys = keys;
	}

	public static SortKeys nil() {
		return new SortKeys();
	}

	@Override
	public String toString() {
		if (keys.length == 0) {
			return "nil";
		}

		String[] strs = new String[keys.length];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = keys[i].toString();
		}
		return String.join(" ", strs);
	}

}
