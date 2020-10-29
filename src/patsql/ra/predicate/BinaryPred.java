package patsql.ra.predicate;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;

public class BinaryPred implements Predicate {
	public final ColSchema left;
	public final BinaryOp op;
	public final Cell right;

	public BinaryPred(ColSchema col, BinaryOp op, Cell right) {
		super();
		this.left = col;
		this.op = op;
		this.right = right;
	}

	@Override
	public ExBool eval(PredEnv env) {
		Cell l = env.get(left.id);
		return op.eval(l, right);
	}

	@Override
	public String toString() {
		return "[" + left + "] " + op + " " + right;
	}

}
