package patsql.ra.predicate;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;

public class JoinKeyPair implements Predicate {
	public final ColSchema left;
	public final ColSchema right;
	public final BinaryOp op;

	public JoinKeyPair(ColSchema left, ColSchema right) {
		this.left = left;
		this.right = right;
		this.op = BinaryOp.Eq;
	}

	public JoinKeyPair(ColSchema left, ColSchema right, BinaryOp op) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public ExBool eval(PredEnv env) {
		Cell l = env.get(left.id);
		Cell r = env.get(right.id);
		return op.eval(l, r);
	}

	@Override
	public String toString() {
		return left + " = " + right;
	}

}
