package patsql.synth.benchmark;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;

import patsql.entity.synth.Example;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.RAOperator;
import patsql.ra.util.ScytheFileData;
import patsql.ra.util.Utils;
import patsql.synth.RASynthesizer;

public class SUtil {

	public static void synthesizeFromScytheFile(File file) {
		System.out.println(">>>>> Case " + file + " <<<<<");

		ScytheFileData sfd = Utils.loadFromScytheFile(file);

		Example ex = new Example(sfd.getOutput(), sfd.getInputsAsList());
		SynthOption opt = new SynthOption(sfd.getConstVals().toArray(new Cell[0]));

		System.out.print("===== Example =====\n" + ex);
		System.out.println(opt);

		RASynthesizer synth = new RASynthesizer(ex, opt);
		List<RAOperator> result = synth.synthesize(100000);

		assertNotNull(result);

		Utils.printCommentsinScytheFile(file);
		// RAUtils.printTree(result);

		int count = 0;
		for (RAOperator r : result) {
			String sql = SQLUtil.generateSQL(r);
			count++;
			System.out.print("\n[No." + count + "]");
			System.out.println(sql);
		}
	}

}
