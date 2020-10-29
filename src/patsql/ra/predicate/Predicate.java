package patsql.ra.predicate;

public interface Predicate {
	public ExBool eval(PredEnv env);
}
