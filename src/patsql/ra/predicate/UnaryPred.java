package patsql.ra.predicate;

import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;

public class UnaryPred implements Predicate {
	public final ColSchema operand;
	public final UnaryOp op;

	public UnaryPred(ColSchema operand, UnaryOp op) {
		super();
		this.operand = operand;
		this.op = op;
	}

	@Override
	public ExBool eval(PredEnv env) {
		Cell l = env.get(operand.id);
		return op.eval(l);
	}

	@Override
	public String toString() {
		return "[" + operand + "] " + op;
	}

}
