package patsql.ra.operator;

import patsql.entity.table.Table;
import patsql.entity.table.WinColSchema;

public class Window extends RAOperator {
	public RAOperator child;
	public WinColSchema[] cols;

	public Window(RAOperator child, WinColSchema... cols) {
		super();
		this.child = child;
		this.cols = cols;
	}

	public static Window empty() {
		return new Window(null, (WinColSchema[]) null);
	}

	@Override
	public Table eval(TableEnv env) {
		Table table = child.eval(env);
		return table.applyWindow(cols);
	}

	@Override
	public Window clone() {
		RAOperator c = (child == null) ? null : child.clone();
		Window clone = new Window(c, cols);
		clone.id = this.id;
		return clone;
	}

}
