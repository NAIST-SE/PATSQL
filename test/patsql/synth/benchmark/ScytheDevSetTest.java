package patsql.synth.benchmark;

import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ScytheDevSetTest {

	/**
	 * Retain the Latest record in each group.
	 */
	@Test
	@Tag("synth")
	void devSet002MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/002M"));
	}

	@Test
	@Tag("synth")
	void devSet005SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/005"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void devSet006MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/006M"));
	}

	@Test
	@Tag("synth")
	void devSet008SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/008"));
	}

	@Test
	@Tag("synth")
	void devSet010SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/010"));
	}

	/**
	 * Concatenation with slashes.
	 */
	@Test
	@Tag("synth")
	void devSet012SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/012"));
	}

	@Test
	@Tag("synth")
	void devSet013SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/013"));
	}

	@Test
	@Tag("synth")
	void devSet014SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/014"));
	}

	@Disabled("condition between columns in WHERE not supported")
	@Test
	@Tag("synth")
	void devSet015SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/015"));
	}

	@Test
	@Tag("synth")
	void devSet016SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/016"));
	}

	@Test
	@Tag("synth")
	void devSet017SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/017"));
	}

	@Test
	@Tag("synth")
	void devSet018SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/018"));
	}

	@Test
	@Tag("synth")
	void devSet019MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/019M"));
	}

	@Test
	@Tag("synth")
	void devSet020SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/020"));
	}

	@Test
	@Tag("synth")
	void devSet021SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/021"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void devSet022SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/022"));
	}

	@Test
	@Tag("synth")
	void devSet023SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/023"));
	}

	@Test
	@Tag("synth")
	void devSet024SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/024"));
	}

	@Test
	@Tag("synth")
	void devSet025SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/025"));
	}

	@Test
	@Tag("synth")
	void devSet026MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/026M"));
	}

	@Test
	@Tag("synth")
	void devSet028SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/028"));
	}

	@Test
	@Tag("synth")
	void devSet029SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/029"));
	}

	@Test
	@Tag("synth")
	void devSet030SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/030"));
	}

	@Test
	@Tag("synth")
	void devSet031SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/031"));
	}

	@Disabled("LIMIT")
	@Test
	@Tag("synth")
	void devSet032XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/032X"));
	}

	@Test
	@Tag("synth")
	void devSet033SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/033"));
	}

	@Test
	@Tag("synth")
	void devSet034SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/034"));
	}

	@Test
	@Tag("synth")
	void devSet035SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/035"));
	}

	@Test
	@Tag("synth")
	void devSet036SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/036"));
	}

	@Test
	@Tag("synth")
	void devSet037SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/037"));
	}

	@Test
	@Tag("synth")
	void devSet038SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/038"));
	}

	/**
	 * Join of two tables, one of which is for user 3 and the other is for other
	 * than 3.
	 */
	@Test
	@Tag("synth")
	void devSet039ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/039A"));
	}

	@Test
	@Tag("synth")
	void devSet040SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/040"));
	}

	@Test
	@Tag("synth")
	void devSet041SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/041"));
	}

	@Test
	@Tag("synth")
	void devSet042SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/042"));
	}

	@Test
	@Tag("synth")
	void devSet043MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/043M"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void devSet045SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/045"));
	}

	@Disabled("NOT EXISTS or left join using multiple conditions including a IS NOT NULL one")
	@Test
	@Tag("synth")
	void devSet046AMSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/046AM"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void devSet047MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/047M"));
	}

	@Test
	@Tag("synth")
	void devSet048SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/048"));
	}

	/**
	 * Left join.
	 */
	@Test
	@Tag("synth")
	void devSet050SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/050"));
	}

	@Test
	@Tag("synth")
	void devSet051SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/051"));
	}

	@Test
	@Tag("synth")
	void devSet052SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/052"));
	}

	/**
	 * rank
	 */
	@Test
	@Tag("synth")
	void devSet053AMSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/053AM"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void devSet054SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/054"));
	}

	@Test
	@Tag("synth")
	void devSet055AMSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/055AM"));
	}

	@Test
	@Tag("synth")
	void devSet056ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/056A"));
	}

	@Test
	@Tag("synth")
	void devSet057SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/057"));
	}

	@Test
	@Tag("synth")
	void devSet058MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/058M"));
	}

	@Test
	@Tag("synth")
	void devSet059MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/059M"));
	}

	@Test
	@Tag("synth")
	void devSet060SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/060"));
	}

	@Disabled("JOIN on inequality")
	@Test
	@Tag("synth")
	void devSet062SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/062"));
	}

	/**
	 * row_number
	 */
	@Test
	@Tag("synth")
	void devSet063ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/063A"));
	}

	@Test
	@Tag("synth")
	void devSet065MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/065M"));
	}

	@Test
	@Tag("synth")
	void devSet066SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/066"));
	}

	@Disabled("timeout. Ideally, use FULL JOIN")
	@Test
	@Tag("synth")
	void devSet067XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/067X"));
	}

	/**
	 * sum over
	 */
	@Test
	@Tag("synth")
	void devSet068SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/dev_set/068"));
	}

}
