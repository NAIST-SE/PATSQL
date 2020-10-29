package patsql.ra.predicate;

import java.util.ArrayList;
import java.util.List;

public class Conjunction implements Predicate {
	public final Predicate[] predicates;

	public Conjunction(Predicate... predicates) {
		List<Predicate> preds = new ArrayList<>();
		for (Predicate p : predicates) {
			if (p instanceof TruePred)
				continue;
			preds.add(p);
		}
		this.predicates = preds.toArray(new Predicate[0]);
	}

	public static Conjunction from(Predicate cond) {
		if (cond instanceof BinaryPred) {
			return new Conjunction(cond);
		} else if (cond instanceof Conjunction) {
			return (Conjunction) cond;
		} else if (cond instanceof Disjunction) {
			return new Conjunction(cond);

		} else if (cond instanceof JoinCond) {
			convErr(cond);
		} else if (cond instanceof JoinKeyPair) {
			convErr(cond);
		} else if (cond instanceof TruePred) {
			return new Conjunction(cond);
		} else if (cond instanceof UnaryPred) {
			return new Conjunction(cond);
		}
		throw new IllegalStateException("unknown type of Predicate");
	}

	private static void convErr(Predicate cond) {
		throw new IllegalStateException("cannt convert " + cond + " to Predicate");
	}

	/**
	 * Note: The state of the receiver object does not change.
	 * 
	 * @return an instance of Conjunction, which is newly created.
	 */
	public Conjunction append(Predicate pred) {
		Predicate[] ret = new Predicate[predicates.length + 1];
		for (int i = 0; i < ret.length - 1; i++) {
			ret[i] = predicates[i];
		}
		ret[ret.length - 1] = pred;
		return new Conjunction(ret);
	}

	@Override
	public ExBool eval(PredEnv env) {
		for (Predicate pred : predicates) {
			if (pred.eval(env) == ExBool.False)
				return ExBool.False;
		}
		for (Predicate pred : predicates) {
			if (pred.eval(env) == ExBool.Unknown)
				return ExBool.Unknown;
		}
		return ExBool.True;
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
		return String.join(" AND ", strs);
	}

}
