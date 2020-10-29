package patsql.ra.predicate;

public class JoinCond implements Predicate {
	public final JoinKeyPair[] pairs;

	public JoinCond(JoinKeyPair... pairs) {
		this.pairs = pairs;
	}

	public static JoinCond fromPredicate(Predicate cond) {
		if (cond instanceof BinaryPred) {
			convErr(cond); // the right side of BinaryPred must be cell.
		} else if (cond instanceof Conjunction) {
			Conjunction c = (Conjunction) cond;

			JoinKeyPair[] pairs = new JoinKeyPair[c.predicates.length];
			for (int i = 0; i < c.predicates.length; i++) {
				Predicate p = c.predicates[i];
				if (!(p instanceof JoinKeyPair))
					convErr(p);
				pairs[i] = (JoinKeyPair) p;
			}
			return new JoinCond(pairs);
		} else if (cond instanceof JoinCond) {
			return (JoinCond) cond;
		} else if (cond instanceof JoinKeyPair) {
			JoinKeyPair p = (JoinKeyPair) cond;
			return new JoinCond(p);
		} else if (cond instanceof TruePred) {
			return new JoinCond();
		} else if (cond instanceof UnaryPred) {
			convErr(cond);
		}
		throw new IllegalStateException("unknown type of Predicate");
	}

	private static void convErr(Predicate cond) {
		throw new IllegalStateException("cannt convert " + cond + " to JoinCond");
	}

	@Override
	public ExBool eval(PredEnv env) {
		for (JoinKeyPair pred : pairs) {
			if (pred.eval(env) != ExBool.True)
				return ExBool.False;
		}
		return ExBool.True;
	}

	@Override
	public String toString() {
		if (pairs.length == 0) {
			return "TRUE";
		}

		String[] strs = new String[pairs.length];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = pairs[i].toString();
		}
		return String.join(" AND ", strs);
	}

}
