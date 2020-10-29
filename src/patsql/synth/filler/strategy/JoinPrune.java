package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.BitTable;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.agg.Agg;
import patsql.ra.operator.Join;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.synth.filler.BitRow;
import patsql.synth.filler.ColConstraint;
import patsql.synth.filler.ColRelation;
import patsql.synth.filler.FillingConstraint;
import patsql.synth.filler.RowSearch;
import patsql.synth.filler.RowSearch.Op;

public class JoinPrune implements FillingStrategy {

	final FillingConstraint constraint;

	/**
	 * constraint used before selection by keys.
	 */
	final FillingConstraint preConstraint;

	public JoinPrune(FillingConstraint constraint) {
		this.constraint = constraint;

		ColRelation colRel = constraint.col.relation.update(RA.SELECTION);
		this.preConstraint = new FillingConstraint(new ColConstraint(constraint.col.matching, colRel));
	}

	@Override
	public RA targetKind() {
		return RA.JOIN;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}

		Join target = (Join) sketch;

		// set fields instead of giving them as parameters.
		BitTable tmpTableL = new BitTable(target.childL.eval(example.tableEnv()));
		BitTable tmpTableR = new BitTable(target.childR.eval(example.tableEnv()));
		Table outTable = example.output;
		BitTable product = tmpTableL.join(tmpTableR);

		if (preConstraint.isPruned(product, outTable)) {
			return Collections.emptyList();
		}

		RowSearch search = new RowSearch();
		// insert predicates
		for (ColSchema left : tmpTableL.schema()) {
			for (ColSchema right : tmpTableR.schema()) {
				if (!isTried(left, right))
					continue;
				JoinKeyPair keyPair = new JoinKeyPair(left, right);
				BitTable bits = product.selection(keyPair);
				search.addPred(keyPair, bits.rowBits);
			}
		}

		// generate AND condition.
		search.generate(Op.AND);

		List<RAOperator> ret = new ArrayList<>();
		for (BitRow row : search.singleRowsAll()) {
			product.rowBits = row.rowBits;// update the bits directly.

			// pruning by post condition.
			if (!constraint.isPruned(product, outTable)) {
				Join clone = target.clone();
				clone.condition = JoinCond.fromPredicate(row.pred);
				ret.add(clone);
			}
		}

		return ret;
	}

	private boolean isTried(ColSchema left, ColSchema right) {
		if (left.type != right.type) {
			return false;
		}

		if (left instanceof AggColSchema) {
			AggColSchema ag = (AggColSchema) left;
			if (ag.agg == Agg.ConcatComma || ag.agg == Agg.ConcatSlash || ag.agg == Agg.ConcatSpace)
				return false;
		}

		if (right instanceof AggColSchema) {
			AggColSchema ag = (AggColSchema) right;
			if (ag.agg == Agg.ConcatComma || ag.agg == Agg.ConcatSlash || ag.agg == Agg.ConcatSpace)
				return false;
		}

		return true;
	}

}
