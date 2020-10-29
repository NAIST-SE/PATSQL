package patsql.synth.filler.strategy;

import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.ra.operator.RA;
import patsql.ra.operator.RAOperator;

public interface FillingStrategy {

	RA targetKind();

	public List<RAOperator> fill(RAOperator sketch, Example example, SynthOption option);

}
