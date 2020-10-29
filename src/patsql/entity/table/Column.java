package patsql.entity.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Column {
	public final ColSchema schema;
	private List<Cell> cells = new ArrayList<>();

	public Column(ColSchema schema) {
		super();
		this.schema = schema;
	}

	public void addCell(Cell c) {
		if (!Type.canBeInserted(schema.type, c.type)) {
			throw new IllegalStateException(
					"There is a mismatch between data and types: (" + schema.type + ", " + c.type + ")");
		}
		cells.add(c);
	}

	public int size() {
		return cells.size();
	}

	public Cell cell(int i) {
		return cells.get(i);
	}

	public Cell[] cells() {
		return cells.toArray(new Cell[0]);
	}

	public boolean hasSameVec(Column other) {
		return cells.equals(other.cells);
	}

	public boolean hasSameBag(Column other) {
		List<Cell> l1 = new ArrayList<>(cells);
		List<Cell> l2 = new ArrayList<>(other.cells);
		Collections.sort(l1);
		Collections.sort(l2);
		return l1.equals(l2);
	}

	public boolean hasSameSet(Column other) {
		Set<Cell> s1 = new HashSet<>(cells);
		Set<Cell> s2 = new HashSet<>(other.cells);
		return s1.equals(s2);
	}

	public boolean isSuperSetOf(Column other) {
		Set<Cell> s1 = new HashSet<>(cells);
		Set<Cell> s2 = new HashSet<>(other.cells);
		return s1.containsAll(s2);
	}

	public boolean isSuperBagOf(Column other) {
		List<Cell> l1 = new ArrayList<>(cells);
		List<Cell> l2 = new ArrayList<>(other.cells);
		for (Cell e : l2) {
			boolean r = l1.remove(e);
			if (!r) {
				return false;
			}
		}
		return true;
	}

	public boolean hasSameName(Column other) {
		return schema.name.equals(other.schema.name);
	}

}
