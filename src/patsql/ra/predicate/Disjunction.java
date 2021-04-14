package patsql.ra.predicate;

import java.util.ArrayList;
import java.util.List;

public class Disjunction implements Predicate {
	public final Predicate[] predicates;

	public Disjunction(Predicate... predicates) {
		List<Predicate> preds = new ArrayList<>();
		for (Predicate p : predicates) {
			if (p instanceof TruePred) {
				preds.clear();
				preds.add(p);
				break;
			}
			preds.add(p);
		}
		this.predicates = preds.toArray(new Predicate[0]);
	}

	public static Disjunction from(Predicate cond) {
		if (cond instanceof BinaryPred) {
			return new Disjunction(cond);
		} else if (cond instanceof Conjunction) {
			return new Disjunction(cond);
		} else if (cond instanceof Disjunction) {
			return (Disjunction) cond;
		} else if (cond instanceof JoinCond) {
			convErr(cond);
		} else if (cond instanceof JoinKeyPair) {
			convErr(cond);
		} else if (cond instanceof TruePred) {
			return new Disjunction(cond);
		} else if (cond instanceof UnaryPred) {
			return new Disjunction(cond);
		}
		throw new IllegalStateException("unknown type of Predicate");
	}

	private static void convErr(Predicate cond) {
		throw new IllegalStateException("cannt convert " + cond + " to Disjunction");
	}

	/**
	 * Note: The state of the receiver object does not change.
	 * 
	 * @return an instance of Disjunction, which is newly created.
	 */
	public Disjunction append(Predicate pred) {
		Predicate[] ret = new Predicate[predicates.length + 1];
		for (int i = 0; i < ret.length - 1; i++) {
			ret[i] = predicates[i];
		}
		ret[ret.length - 1] = pred;
		return new Disjunction(ret);
	}

	@Override
	public ExBool eval(PredEnv env) {
		for (Predicate pred : predicates) {
			if (pred.eval(env) == ExBool.True)
				return ExBool.True;
		}
		for (Predicate pred : predicates) {
			if (pred.eval(env) == ExBool.Unknown)
				return ExBool.Unknown;
		}
		return ExBool.False;
	}

	@Override
	public String toString() {
		if (predicates.length == 0) {
			return "TRUE";
		}

		String[] strs = new String[predicates.length];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = predicates[i].toString();
		}
		return "(" + String.join(" OR ", strs) + ")";
	}

}
