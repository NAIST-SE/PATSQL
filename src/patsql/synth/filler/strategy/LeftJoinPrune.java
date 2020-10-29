package patsql.synth.filler.strategy;

import java.util.ArrayList;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.AggColSchema;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.agg.Agg;
import patsql.ra.operator.LeftJoin;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;
import patsql.ra.predicate.JoinCond;
import patsql.ra.predicate.JoinKeyPair;
import patsql.synth.filler.FillingConstraint;
import patsql.synth.filler.TableMemo;

public class LeftJoinPrune implements FillingStrategy {

	final FillingConstraint constraint;

	public LeftJoinPrune(FillingConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public RA targetKind() {
		return RA.LEFTJOIN;
	}

	@Override
	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option) {
		if (sketch.kind != targetKind()) {
			throw new IllegalStateException("Invalid filling strategy.");
		}

		LeftJoin target = (LeftJoin) sketch;

		Table outTable = example.output;
		Table tmpTableL = target.childL.eval(example.tableEnv());
		Table tmpTableR = target.childR.eval(example.tableEnv());

		// NOTE: Pruning with TRUE condition is not needed here. If null values are
		// contained in the output table and the intermediate table doesn't contain
		// any nulls, the pruning by SET doesn't work.

		TableMemo memo = new TableMemo();
		// TODO: For now, only a single key matching is supported.
		List<RAOperator> ret = new ArrayList<>();
		for (ColSchema left : tmpTableL.schema()) {
			for (ColSchema right : tmpTableR.schema()) {
				if (!isTried(left, right))
					continue;

				// single key
				JoinKeyPair keyPair = new JoinKeyPair(left, right);

				JoinCond cond = new JoinCond(keyPair);
				Table result = tmpTableL.leftJoin(tmpTableR, cond);
				if (!isPruned(result, outTable) && memo.lookup(result)) {
					LeftJoin clone = target.clone();
					clone.condition = cond;
					ret.add(clone);
				}
			}
		}
		return ret;
	}

	private boolean isPruned(Table result, Table outTable) {
		return constraint.isPruned(result, outTable);
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
