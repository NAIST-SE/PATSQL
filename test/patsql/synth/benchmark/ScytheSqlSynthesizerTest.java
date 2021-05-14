package patsql.synth.benchmark;

import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ScytheSqlSynthesizerTest {

	/**
	 * Count(...) > 1 / Count(...) <> 1
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizerforumquestions1SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/forum-questions-1"));
	}

	/**
	 * Sum
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizerforumquestions2SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/forum-questions-2"));
	}

	/**
	 * having Count(..) > 5
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizerforumquestions3SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/forum-questions-3"));
	}

	/**
	 * This case can be synthesized, but the intention is unknown.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizerforumquestions4SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/forum-questions-4"));
	}

	/**
	 * The first input table is useless.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizerforumquestions5SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/forum-questions-5"));
	}

	/**
	 * joining 4 tables and AND condition are required.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_1SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_1"));
	}

	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_2SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_2"));
	}

	/**
	 * OR (the original output was wrong)
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_3ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_3"));
	}

	/**
	 * self join with a <> condition.
	 */
	@Disabled
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_4SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_4"));
	}

	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_5SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_5"));
	}

	/**
	 * HAVING count(...) < 5
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_6SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_6"));
	}

	/**
	 * Avg.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_7SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_7"));
	}

	/**
	 * Avg, <>
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_8SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_8"));
	}

	/**
	 * Max(...) = R128 AND MIN(...) = R128
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_9ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_9"));
	}

	/**
	 * find max count and join using that
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_10SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_10"));
	}

	/**
	 * NOT EXISTS.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_11SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_11"));
	}

	/**
	 * The most frequent value for each age.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_1_12SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_1_12"));
	}

	/**
	 * EXISTS.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_1SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_1"));
	}

	/**
	 * having count(*) = count(*)
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_2SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_2"));
	}

	/**
	 * count(*) = max(count(*)) and filtering
	 */
	@Disabled("timeout")
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_3SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_3"));
	}

	/**
	 * MIN(...) = MAX(...) = AES
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_4SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_4"));
	}

	/**
	 * v > avg(v)
	 */
	@Disabled("join on inequality")
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_5SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_5"));
	}

	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_6SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_6"));
	}

	/**
	 * "only red part" is realized by MIN(...) = MAX(...) = red.
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_7SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_7"));
	}

	/**
	 * covers "red" and "green"
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_8SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_8"));
	}

	/**
	 * Needs "OR".
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_9SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_9"));
	}

	/**
	 * filtering and count
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_10SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_10"));
	}

	/**
	 * covers "red" and "green", and the maximum cost
	 */
	@Test
	@Tag("synth")
	void sqlsynthesizertextbook_5_2_11SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/sqlsynthesizer/textbook_5_2_11"));
	}

}
