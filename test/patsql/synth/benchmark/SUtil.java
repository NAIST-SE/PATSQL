package patsql.synth.benchmark;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.ScytheFileData;
import patsql.ra.util.Utils;
import patsql.synth.RASynthesizer;

public class SUtil {

	public static RAOperator synthesizeFromScytheFile(File file) {
		System.out.println(">>>>> Case " + file + " <<<<<");

		ScytheFileData sfd = Utils.loadFromScytheFile(file);
		// printCellStatistics(sfd);

		Example ex = new Example(sfd.getOutput(), sfd.getInputsAsList());
		SynthOption opt = new SynthOption(sfd.getConstVals().toArray(new Cell[0]));

		System.out.print("===== Example =====\n" + ex);
		System.out.println(opt);

		RASynthesizer synth = new RASynthesizer(ex, opt);
		RAOperator result = synth.synthesize(100000);

		assertNotNull(result);

		Utils.printCommentsinScytheFile(file);
		//RAUtils.printTree(result);

		String sql = SQLUtil.generateSQL(result);
		System.out.println(sql);

		return result;
	}

	@SuppressWarnings("unused")
	private static void printCellStatistics(ScytheFileData sfd) {
		int total = 0;
		List<Integer> columnCounts = new ArrayList<>();
		for (NamedTable input : sfd.getInputs()) {
			total += input.table.width() * input.table.height();
			columnCounts.add(input.table.width());
		}
		total += sfd.getConstVals().size();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[statistics] total cells: %d, output cols: %d", total, sfd.getOutput().width()));
		for (int i = 0; i < columnCounts.size(); i++) {
			sb.append(String.format(", input%d cols: %d", i + 1, columnCounts.get(i)));
		}
		System.out.println(sb);
	}

}