package patsql.entity.synth;

import patsql.entity.table.Cell;

public class SynthOption {

	public final Cell[] extCells;

	public SynthOption(Cell... externalCells) {
		this.extCells = externalCells;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSTANTS\n");
		if (extCells.length == 0) {
			sb.append("    (empty)\n");
		}
		for (Cell c : extCells) {
			sb.append("  * " + c.value + ":" + c.type + "\n");
		}
		return sb.toString();
	}
}
