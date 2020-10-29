package patsql.entity.table;

import java.util.Optional;

import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;
import patsql.entity.table.window.WinFunc;

public class WinColSchema extends ColSchema {
	public WinFunc func;
	public Optional<ColSchema> src;
	public GroupKeys partKey;
	public SortKeys orderKey;

	public WinColSchema(WinFunc func, ColSchema src, GroupKeys pKey, SortKey oKey) {
		super(str(func, src, pKey, oKey), func.returnType(src));
		this.func = func;
		this.src = Optional.ofNullable(src);
		this.partKey = (pKey == null) ? GroupKeys.nil() : pKey;
		this.orderKey = (oKey == null) ? SortKeys.nil() : new SortKeys(oKey);
	}

	private static String str(WinFunc func, ColSchema src, GroupKeys pKey, SortKey oKey) {
		return func + "(" + ((src == null) ? "" : src.name) + ")("//
				+ ((pKey == null) ? "" : pKey)//
				+ ","//
				+ ((oKey == null) ? "" : oKey)//
				+ ")";
	}

	public boolean hasOrderKey() {
		return orderKey.keys.length != 0;
	}

	public boolean isValid() {
		switch (func) {
		case COUNT:
		case SUM:
			if (!src.isPresent())
				return false;
			if (!hasOrderKey())// excluded because it is the same as GroupBy+Join
				return false;
			break;
		case MAX:
		case MIN:
			if (!src.isPresent())
				return false;
			if (!hasOrderKey())// excluded because it is the same as GroupBy+Join
				return false;
			break;
		case RANK:
			if (!hasOrderKey())
				return false;
			break;
		case ROWNUM:
			// TODO: the order key must assure the uniqueness, but current implementation
			// don't when the key has duplicated values.
			if (!hasOrderKey())
				return false;
			if (src.isPresent())
				return false;
			break;
		}
		return true;
	}

}
