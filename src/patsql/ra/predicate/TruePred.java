package patsql.ra.predicate;

public class TruePred implements Predicate {

	@Override
	public ExBool eval(PredEnv env) {
		return ExBool.True;
	}

	@Override
	public String toString() {
		return "TRUE";
	}

}
