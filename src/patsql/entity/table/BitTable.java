package patsql.entity.table;

import java.util.Arrays;
import java.util.BitSet;

import patsql.ra.predicate.ExBool;
import patsql.ra.predicate.PredEnv;
import patsql.ra.predicate.Predicate;

public class BitTable {
	public final Table[] tables;
	public BitSet rowBits;

	public BitTable(Table... tables) {
		this.tables = tables;
		this.rowBits = defaultBits();
	}

	/**
	 * return shallow copy.
	 */
	@Override
	public BitTable clone() {
		BitTable clone = new BitTable(tables);
		BitSet bits = new BitSet();
		bits.or(rowBits);
		clone.rowBits = bits;
		return clone;
	}

	private BitSet defaultBits() {
		int len = heightOrg();
		BitSet ret = new BitSet(len);
		ret.set(0, len);
		return ret;
	}

	public Table toTable() {
		Table ret = new Table(schema());
		foreach(new RowVisitor() {
			@Override
			public void f(int index, Cell[] row) {
				ret.addRow(row);
			}
		});
		return ret;
	}

	public BitTable selection(Predicate pred) {
		BitTable ret = clone();

		final ColSchema[] sc = schema();
		foreach(new RowVisitor() {
			@Override
			public void f(int index, Cell[] row) {
				PredEnv env = new PredEnv();
				for (int i = 0; i < sc.length; i++) {
					env.put(sc[i].id, row[i]);
				}
				if (pred.eval(env) != ExBool.True) {
					ret.rowBits.clear(index);
				}
			}
		});
		return ret;
	}

	public BitTable join(BitTable other) {
		// concatenate table arrays.
		Table[] tbls = new Table[tables.length + other.tables.length];
		for (int i = 0; i < tables.length; i++) {
			tbls[i] = tables[i];
		}
		for (int i = 0; i < other.tables.length; i++) {
			tbls[i + tables.length] = other.tables[i];
		}

		BitTable ret = new BitTable(tbls);
		// clear rows whose source row is false.
		final int sndLen = other.heightOrg();
		for (int i1 = 0; i1 < heightOrg(); i1++) {
			for (int i2 = 0; i2 < other.heightOrg(); i2++) {
				boolean bit1 = rowBits.get(i1);
				boolean bit2 = other.rowBits.get(i2);
				if (!bit1 || !bit2) {
					int idx = i1 * sndLen + i2;
					ret.rowBits.clear(idx);
				}
			}
		}
		return ret;
	}

	private void foreach(RowVisitor visitor) {
		Accessor acc = new Accessor(tables);
		while (true) {
			int k = acc.serialIndex();
			if (rowBits.get(k)) {
				visitor.f(k, row(acc));
			}

			boolean hasNext = acc.inc();
			if (!hasNext)
				break;
		}
	}

	public int width() {
		return schema().length;
	}

	public int height() {
		return rowBits.cardinality();
	}

	private Cell[] row(Accessor acc) {
		Cell[] ret = new Cell[width()];
		int c = 0;
		for (int i = 0; i < tables.length; i++) {
			Table table = tables[i];
			for (Cell row : table.row(acc.indexes[i])) {
				ret[c] = row;
				c++;
			}
		}
		return ret;
	}

	public ColSchema[] schema() {
		ColSchema[] ret = new ColSchema[sumOfCols()];
		int i = 0;
		for (Table tbl : tables) {
			for (ColSchema s : tbl.schema()) {
				ret[i] = s;
				i++;
			}
		}
		return ret;
	}

	private int sumOfCols() {
		int sum = 0;
		for (Table t : tables) {
			sum += t.width();
		}
		return sum;
	}

	private int heightOrg() {
		int prod = 1;
		for (Table t : tables) {
			prod *= t.height();
		}
		return prod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowBits == null) ? 0 : rowBits.hashCode());
		result = prime * result + Arrays.hashCode(tables);
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
		BitTable other = (BitTable) obj;

		if (!Arrays.equals(tables, other.tables))
			return false;
		return true;
	}

	private class Accessor {
		final int[] indexes;
		final int[] limits;

		public Accessor(Table... tables) {
			indexes = new int[tables.length];
			limits = limits(tables);
		}

		private int[] limits(Table... tables) {
			int[] ret = new int[tables.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = tables[i].height();
			}
			return ret;
		}

		public int serialIndex() {
			int[] rem = new int[indexes.length];
			rem[indexes.length - 1] = limits[indexes.length - 1];
			for (int i = indexes.length - 2; i >= 0; i--) {
				rem[i] = limits[i] * rem[i + 1];
			}

			int ret = 0;
			for (int i = 0; i < indexes.length - 1; i++) {
				ret += indexes[i] * rem[i + 1];
			}
			ret += indexes[indexes.length - 1];
			return ret;
		}

		public boolean inc() {
			for (int i = indexes.length - 1; i >= 0; i--) {
				if (indexes[i] < limits[i] - 1) {
					indexes[i]++;
					return true;
				} else {
					indexes[i] = 0;
				}
			}
			return false;
		}

	}

	private interface RowVisitor {
		public void f(int index, Cell[] row);
	}

}
