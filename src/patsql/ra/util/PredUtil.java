package patsql.ra.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import patsql.entity.table.Cell;
import patsql.ra.predicate.BinaryPred;
import patsql.ra.predicate.Conjunction;
import patsql.ra.predicate.Disjunction;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.ra.predicate.Predicate;
import patsql.ra.predicate.TruePred;
import patsql.ra.predicate.UnaryPred;

public class PredUtil {

	public static int numberOfOR(Predicate pred) {
		if (pred instanceof BinaryPred) {
			return 0;
		} else if (pred instanceof Conjunction) {
			Conjunction p = (Conjunction) pred;
			int sum = 0;
			for (Predicate t : p.predicates) {
				sum += numberOfOR(t);
			}
			return sum;
		} else if (pred instanceof Disjunction) {
			Disjunction p = (Disjunction) pred;
			int len = p.predicates.length;
			return (len == 0) ? 0 : len - 1;
		} else if (pred instanceof JoinCond) {
			return 0;
		} else if (pred instanceof JoinKeyPair) {
			return 0;
		} else if (pred instanceof TruePred) {
			return 0;
		} else if (pred instanceof UnaryPred) {
			return 0;
		} else {
			throw new IllegalStateException("unknown predicate");
		}
	}

	public static Set<Cell> usedConstants(Predicate pred) {
		if (pred instanceof BinaryPred) {
			BinaryPred p = (BinaryPred) pred;
			return new HashSet<>(Arrays.asList(p.right));
		} else if (pred instanceof Conjunction) {
			Conjunction p = (Conjunction) pred;
			Set<Cell> ret = new HashSet<>();
			for (Predicate t : p.predicates) {
				ret.addAll(usedConstants(t));
			}
			return ret;
		} else if (pred instanceof Disjunction) {
			Disjunction p = (Disjunction) pred;
			Set<Cell> ret = new HashSet<>();
			for (Predicate t : p.predicates) {
				ret.addAll(usedConstants(t));
			}
			return ret;
		} else if (pred instanceof JoinCond) {
			return Collections.emptySet();
		} else if (pred instanceof JoinKeyPair) {
			return Collections.emptySet();
		} else if (pred instanceof TruePred) {
			return Collections.emptySet();
		} else if (pred instanceof UnaryPred) {
			return Collections.emptySet();
		} else {
			throw new IllegalStateException("unknown predicate");
		}
	}

}
