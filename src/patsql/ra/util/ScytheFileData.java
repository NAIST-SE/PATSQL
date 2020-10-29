package patsql.ra.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.Cell;
import patsql.entity.table.Table;

/**
 * The data structure to store examples and used constants that corresponds to
 * Scythe file format.
 */
public class ScytheFileData {
	private List<NamedTable> inputs = new ArrayList<>();
	private Table output;
	private List<Cell> constVals = new ArrayList<>();

	public ScytheFileData(List<NamedTable> inputs, Table output, List<Cell> constVals) {
		this.inputs = inputs;
		this.output = output;
		this.constVals = constVals;
	}

	public List<NamedTable> getInputs() {
		return Collections.unmodifiableList(inputs);
	}

	public NamedTable[] getInputsAsList() {
		return getInputs().toArray(new NamedTable[0]);
	}

	public Table getOutput() {
		return output;
	}

	public List<Cell> getConstVals() {
		return Collections.unmodifiableList(constVals);
	}

}