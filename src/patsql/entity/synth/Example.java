package patsql.entity.synth;

import patsql.entity.table.Table;
import patsql.ra.operator.TableEnv;

public class Example {
	public final NamedTable[] inputs;
	public final Table output;

	public Example(Table output, NamedTable... inputs) {
		this.inputs = inputs;
		this.output = output;
	}

	public TableEnv tableEnv() {
		TableEnv env = new TableEnv();
		for (NamedTable p : inputs) {
			env.put(p.name, p.table);
		}
		return env;
	}

	public String[] inputTableNames() {
		String[] ret = new String[inputs.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = inputs[i].name;
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("INPUT\n");
		for (NamedTable tbl : inputs) {
			sb.append(tbl);
		}
		sb.append("OUTPUT\n");
		sb.append(output);
		return sb.toString();
	}

}
