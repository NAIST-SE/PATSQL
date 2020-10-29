package patsql.entity.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import patsql.entity.table.agg.Aggregators;
import patsql.entity.table.agg.GroupKeys;
import patsql.entity.table.sort.Order;
import patsql.entity.table.sort.SortKey;
import patsql.entity.table.sort.SortKeys;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;

import java.util.TreeMap;

public class Table {
	public final Column[] columns;

	public Table(ColSchema... schema) {
		this.columns = new Column[schema.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new Column(schema[i]);
		}
	}

	public ColSchema[] schema() {
		ColSchema[] ret = new ColSchema[columns.length];
		for (int i = 0; i < columns.length; i++) {
			ret[i] = columns[i].schema;
		}
		return ret;
	}

	public Table cloneWithNewColIDs(int[] ids) {
		if (ids.length != schema().length) {
			throw new IllegalStateException("The length of the new IDs is different from the existing ones.");
		}
		// use the new IDs.
		ColSchema[] schema = new ColSchema[width()];
		for (int i = 0; i < schema.length; i++) {
			ColSchema sc = columns[i].schema;
			schema[i] = new ColSchema(sc.name, sc.type, ids[i]);// use the ID here.
		}
		Table ret = new Table(schema);
		// deep copy of columns
		for (int i = 0; i < height(); i++) {
			ret.addRow(row(i));
		}
		return ret;
	}

	public int width() {
		return columns.length;
	}

	public int height() {
		if (columns.length == 0)
			return 0;
		return columns[0].size();
	}

	public void addRow(Cell... row) {
		if (row.length != width()) {
			throw new IllegalStateException(
					"Size of a row (" + row.length + ") is not " + width() + ". row data: {" + row + "}");
		}

		for (int i = 0; i < row.length; i++) {
			Cell r = row[i];
			Column col = columns[i];
			col.addCell(r);
		}
	}

	public Cell[] row(int r) {
		Cell[] ret = new Cell[columns.length];
		for (int i = 0; i < columns.length; i++) {
			ret[i] = columns[i].cell(r);
		}
		return ret;
	}

	public Column columnById(int id) {
		for (Column col : columns) {
			if (id == col.schema.id)
				return col;
		}
		throw new IllegalStateException("column id doesn't exist" + id);
	}

	public ColSchema[] schemaWithIds(int[] ids) {
		ColSchema[] ret = new ColSchema[ids.length];
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			ret[i] = columnById(id).schema;
		}
		return ret;
	}

	private Cell[] cellsWithSchema(int row, ColSchema[] schema) {
		Cell[] ret = new Cell[schema.length];
		for (int i = 0; i < schema.length; i++) {
			int id = schema[i].id;
			ret[i] = columnById(id).cell(row);
		}
		return ret;
	}

	public Cell cell(int col, int row) {
		return columns[col].cell(row);
	}

	public Table addColumns(Column... cols) {
		// validation
		for (Column col : cols) {
			if (col.size() != height())
				throw new IllegalArgumentException("The column has illagel size: " + col.size() + ", not " + height());
		}

		// create schema
		ColSchema[] schema = new ColSchema[width() + cols.length];
		for (int i = 0; i < width(); i++)
			schema[i] = columns[i].schema;
		for (int i = 0; i < cols.length; i++)
			schema[i + width()] = cols[i].schema;

		Table table = new Table(schema);
		// insert rows
		for (int j = 0; j < height(); j++) {
			Cell[] row = new Cell[width() + cols.length];
			for (int i = 0; i < width(); i++)
				row[i] = row(j)[i];
			for (int i = 0; i < cols.length; i++)
				row[i + width()] = cols[i].cell(j);
			table.addRow(row);
		}

		return table;
	}

	public Table project(int[] projColIds) {
		ColSchema[] proj = new ColSchema[projColIds.length];
		for (int i = 0; i < projColIds.length; i++) {
			boolean exists = false;
			for (Column c : columns) {
				if (projColIds[i] == c.schema.id) {
					proj[i] = c.schema;
					exists = true;
					continue;
				}
			}
			if (!exists) {
				throw new IllegalStateException("illegal projection column id :" + projColIds[i]);
			}
		}

		Table ret = new Table(proj);
		for (int i = 0; i < height(); i++) {
			Cell[] row = new Cell[proj.length];
			for (int j = 0; j < proj.length; j++) {
				int w = 0;
				while (columns[w].schema != proj[j])
					w++;
				row[j] = columns[w].cell(i);
			}
			ret.addRow(row);
		}
		return ret;
	}

	public Table groupBy(GroupKeys keys, Aggregators ags) {
		if (keys.isEmpty()) {
			Table ret = new Table(ags.aggColSchemas);
			Cell[] row = aggregate(ags);
			ret.addRow(row);
			return ret;
		}

		Map<KeyCells, Table> key2group = groupingOrderPreserved(keys);
		Table ret = new Table(concat(keys.colSchemas, ags.aggColSchemas));
		for (Entry<KeyCells, Table> e : key2group.entrySet()) {
			Table group = e.getValue();
			Cell[] aggCells = group.aggregate(ags);
			ret.addRow(concat(e.getKey().keys, aggCells));
		}
		return ret;
	}

	private void grouping(GroupKeys keys, Map<KeyCells, Table> map) {
		for (int i = 0; i < height(); i++) {
			Cell[] cells = cellsWithSchema(i, keys.colSchemas);
			KeyCells keyCells = new KeyCells(cells);
			Table group = map.get(keyCells);
			if (group == null) {
				group = new Table(schema());
				map.put(keyCells, group);
			}
			group.addRow(row(i));
		}
	}

	private Map<KeyCells, Table> groupingSortedByKey(GroupKeys keys) {
		Map<KeyCells, Table> map = new TreeMap<>();
		grouping(keys, map);
		return map;
	}

	private Map<KeyCells, Table> groupingOrderPreserved(GroupKeys keys) {
		Map<KeyCells, Table> map = new LinkedHashMap<>();
		grouping(keys, map);
		return map;
	}

	public Collection<Table> groups(GroupKeys keys) {
		return groupingOrderPreserved(keys).values();
	}

	public Table sort(SortKeys sortKeys) {
		Table ret = this;
		for (int i = sortKeys.keys.length - 1; i >= 0; i--) {
			ret = ret.sort(sortKeys.keys[i]);
		}
		return ret;
	}

	public Table sort(SortKey sortKey) {
		GroupKeys keys = new GroupKeys(sortKey.col);

		Map<KeyCells, Table> key2group = groupingSortedByKey(keys);
		Table ret = new Table(schema());

		List<KeyCells> keyList = new ArrayList<>(key2group.keySet());
		if (sortKey.order == Order.Desc) {
			Collections.reverse(keyList);
		}
		for (KeyCells key : keyList) {
			Table group = key2group.get(key);
			for (int i = 0; i < group.height(); i++) {
				ret.addRow(group.row(i));
			}
		}
		return ret;
	}

	/**
	 * @param schema specifies a target column
	 * @return true only if the values of cells are increasing and not all the same.
	 */
	public boolean isIncreasing(ColSchema schema) {
		Column col = columnById(schema.id);
		if (col.size() == 0)
			return false;

		final Cell[] cells = col.cells();
		Cell cur = cells[0];
		for (int i = 1; i < cells.length; i++) {
			if (cells[i].compareTo(cur) < 0)
				return false;// when decreasing
			cur = cells[i];
		}

		if (cur.compareTo(cells[0]) == 0) {
			// false returned when all values are the same
			return false;
		}
		return true;
	}

	/**
	 * @param scehma specifies a target column
	 * @return true only if the values of cells are decreasing and not all the same.
	 */
	public boolean isDecreasing(ColSchema schema) {
		Column col = columnById(schema.id);
		if (col.size() == 0)
			return false;

		final Cell[] cells = col.cells();
		Cell cur = cells[0];
		for (int i = 1; i < cells.length; i++) {
			if (cells[i].compareTo(cur) > 0)
				return false;// when increasing
			cur = cells[i];
		}

		if (cur.compareTo(cells[0]) == 0) {
			// false returned when all values are the same
			return false;
		}
		return true;
	}

	private <T> T[] concat(T[] arr1, T[] arr2) {
		T[] ret = Arrays.copyOf(arr1, arr1.length + arr2.length);
		for (int i = 0; i < arr2.length; i++) {
			ret[arr1.length + i] = arr2[i];
		}
		return ret;
	}

	private Cell[] aggregate(Aggregators ags) {
		Cell[] ret = new Cell[ags.aggColSchemas.length];
		for (int i = 0; i < ags.aggColSchemas.length; i++) {
			AggColSchema sc = ags.aggColSchemas[i];
			Column col = columnById(sc.srcColId());
			ret[i] = sc.agg.eval(col.cells());
		}
		return ret;
	}

	private static final Cell nc = new Cell("", Type.Null);

	public Table leftJoin(Table other, JoinCond condition) {
		Table ret = new Table(concat(this.schema(), other.schema()));
		for (int i1 = 0; i1 < height(); i1++) {
			boolean leftHasPair = false;
			for (int i2 = 0; i2 < other.height(); i2++) {

				// check the key matching
				boolean doMatch = true;
				for (JoinKeyPair pair : condition.pairs) {
					Cell c1 = columnById(pair.left.id).cell(i1);
					Cell c2 = other.columnById(pair.right.id).cell(i2);
					if (!c1.equals(c2)) {
						doMatch = false;
						break;
					}
				}
				if (doMatch) {
					// insert the matched column in i2;
					ret.addRow(concat(row(i1), other.row(i2)));
					leftHasPair = true;
				}
			}
			if (!leftHasPair) {
				// if no rows in the right match the left, fill Nulls.
				Cell[] r1 = row(i1);
				Cell[] r2 = new Cell[other.width()];
				Arrays.fill(r2, nc);
				ret.addRow(concat(r1, r2));
			}
		}
		return ret;
	}

	public boolean hasSameRows(Table other) {
		if (height() != other.height()) {
			return false;
		}

		// count each rows
		Map<Row, Integer> map1 = new HashMap<>();
		Map<Row, Integer> map2 = new HashMap<>();
		for (int i = 0; i < height(); i++) {
			{
				Row r = new Row(row(i));
				Integer cnt = map1.get(r);
				if (cnt == null)
					cnt = 0;
				map1.put(r, cnt + 1);
			}
			{
				Row r = new Row(other.row(i));
				Integer cnt = map2.get(r);
				if (cnt == null)
					cnt = 0;
				map2.put(r, cnt + 1);
			}
		}
		return map1.equals(map2);
	}

	public boolean hasUnqueCells(GroupKeys keys) {
		return groups(keys).size() == height();
	}

	public boolean hasDifferentCells(GroupKeys keys) {
		return groups(keys).size() > 1;
	}

	public Table applyWindow(WinColSchema... cols) {
		// validation
		if (cols.length == 0) {
			return this;
		}
		for (WinColSchema c : cols) {
			if (!c.isValid())
				throw new IllegalStateException("the windows schema is invalid :" + c);
		}

		// create new columns
		Map<Long, List<Cell>> rid2cells = new HashMap<>();
		for (WinColSchema wsc : cols) {
			for (Table g : groups(wsc.partKey)) {
				Table grp = g.sort(wsc.orderKey);

				// set cells to calculate the value.
				Cell[] targetCells = null;
				if (wsc.src.isPresent()) {
					targetCells = grp.columnById(wsc.src.get().id).cells();
				}

				// set cells to calculate the range.
				Cell[] orderKeys = null;
				if (wsc.hasOrderKey()) {
					SortKey k = wsc.orderKey.keys[0];
					orderKeys = grp.columnById(k.col.id).cells();
				}

				for (int i = 0; i < grp.height(); i++) {
					// if the function is aggregation, it applied on all rows.
					int lim = -1;
					if (wsc.func.isAgg())
						lim = (wsc.orderKey.keys.length > 0) ? i : grp.height() - 1;
					else
						lim = i;
					Cell result = wsc.func.eval(lim, targetCells, orderKeys);

					// register the result in the map.
					long rowID = id(grp.row(i));
					List<Cell> list = rid2cells.get(rowID);
					if (list == null)
						list = new ArrayList<>();
					list.add(result);
					rid2cells.put(rowID, list);
				}
			}
		}

		// append them to a table
		Table ret = new Table(concat(schema(), cols));
		for (int i = 0; i < height(); i++) {
			Cell[] org = row(i);
			Cell[] results = rid2cells.get(id(org)).toArray(new Cell[0]);
			ret.addRow(concat(org, results));
		}
		return ret;
	}

	private long id(Cell[] row) {
		if (row == null || row.length == 0) {
			throw new IllegalStateException("The row is empty.");
		}
		int ret = 0;
		for (int i = 0; i < row.length; i++) {
			ret += (i + 1) * System.identityHashCode(row[i]); // weighted sum
		}
		return ret;
	}

	@Override
	public String toString() {
		final String sep = " | ";
		final String hLine = "-----------------------------\n";

		StringBuilder sb = new StringBuilder();
		sb.append(hLine);
		for (ColSchema s : schema()) {
			sb.append(s).append(sep);
		}
		sb.append("\n");
		sb.append(hLine);
		for (int r = 0; r < height(); r++) {
			for (int c = 0; c < width(); c++) {
				sb.append(cell(c, r)).append(sep);
			}
			sb.append("\n");
		}
		sb.append(hLine);
		return sb.toString();
	}

	private class Row {
		final Cell[] cells;

		public Row(Cell... cells) {
			this.cells = cells;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(cells);
			// System.out.println(result);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Row other = (Row) obj;
			if (!Arrays.equals(cells, other.cells))
				return false;
			return true;
		}

	}

	private class KeyCells implements Comparable<KeyCells> {
		Cell[] keys;

		public KeyCells(Cell[] keys) {
			super();
			this.keys = keys;
		}

		@Override
		public String toString() {
			return Arrays.toString(keys);
		}

		@Override
		public int compareTo(KeyCells other) {
			if (keys.length != other.keys.length) {
				throw new IllegalStateException("different key length.");
			}

			for (int i = 0; i < keys.length; i++) {
				Cell t = keys[i];
				Cell o = other.keys[i];
				int res = t.compareTo(o);
				if (res == 0) {
					continue;
				} else {
					return res;
				}
			}
			return 0;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(keys);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyCells other = (KeyCells) obj;
			if (!Arrays.equals(keys, other.keys))
				return false;
			return true;
		}

	}

}
