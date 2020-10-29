package patsql.synth.benchmark;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import patsql.entity.synth.Example;
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

}
