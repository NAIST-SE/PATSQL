package patsql.synth.benchmark;

import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ScytheRecentPostsTest {

	@Disabled("UNION ALL")
	@Test
	@Tag("synth")
	void recentPosts001SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/001"));
	}

	@Disabled("lead and UNION ALL.")
	@Test
	@Tag("synth")
	void recentPosts002XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/002X"));
	}

	@Test
	@Tag("synth")
	void recentPosts003SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/003"));
	}

	@Disabled("UNION ALL")
	@Test
	@Tag("synth")
	void recentPosts004ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/004A"));
	}

	@Test
	@Tag("synth")
	void recentPosts005MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/005M"));
	}

	@Disabled("string concatenation operator")
	@Test
	@Tag("synth")
	void recentPosts006XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/006X"));
	}

	@Test
	@Tag("synth")
	void recentPosts007SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/007"));
	}

	@Disabled("complex query using pivot")
	@Test
	@Tag("synth")
	void recentPosts008XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/008X"));
	}

	@Test
	@Tag("synth")
	void recentPosts009SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/009"));
	}

	@Disabled("RECURSIVE")
	@Test
	@Tag("synth")
	void recentPosts010XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/010X"));
	}

	@Test
	@Tag("synth")
	void recentPosts011SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/011"));
	}

	@Disabled("string concatenation operator")
	@Test
	@Tag("synth")
	void recentPosts012SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/012"));
	}

	@Disabled("condition between columns in WHERE not supported")
	@Test
	@Tag("synth")
	void recentPosts013SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/013"));
	}

	@Disabled("UNION and SELECT constants")
	@Test
	@Tag("synth")
	void recentPosts014SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/014"));
	}

	@Disabled("pivot")
	@Test
	@Tag("synth")
	void recentPosts015XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/015X"));
	}

	@Test
	@Tag("synth")
	void recentPosts016MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/016M"));
	}

	@Disabled("NOT EXISTS or left join using multiple conditions including a 'column = constant' one")
	@Test
	@Tag("synth")
	void recentPosts017ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/017A"));
	}

	@Disabled("timeout")
	@Test
	@Tag("synth")
	void recentPosts018XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/018X"));
	}

	/**
	 * MAX(window function) < 0
	 */
	@Test
	@Tag("synth")
	void recentPosts019SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/019"));
	}

	@Disabled("string concatenation operator and array aggregation function")
	@Test
	@Tag("synth")
	void recentPosts020XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/020X"));
	}

	@Test
	@Tag("synth")
	void recentPosts021MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/021M"));
	}

	@Disabled("CROSS JOIN and FETCH FIRST ROW ONLY")
	@Test
	@Tag("synth")
	void recentPosts022MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/022M"));
	}

	@Test
	@Tag("synth")
	void recentPosts023XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/023X"));
	}

	@Disabled("CASE")
	@Test
	@Tag("synth")
	void recentPosts024XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/024X"));
	}

	@Test
	@Tag("synth")
	void recentPosts025ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/025A"));
	}

	@Disabled("BOOLEAN type")
	@Test
	@Tag("synth")
	void recentPosts026XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/026X"));
	}

	@Disabled("date and time functions")
	@Test
	@Tag("synth")
	void recentPosts027XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/027X"));
	}

	@Test
	@Tag("synth")
	void recentPosts028SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/028"));
	}

	@Disabled("FULL JOIN and CASE and string concatenation operator")
	@Test
	@Tag("synth")
	void recentPosts029XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/029X"));
	}

	@Disabled("unclear")
	@Test
	@Tag("synth")
	void recentPosts030XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/030X"));
	}

	@Test
	@Tag("synth")
	void recentPosts031SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/031"));
	}

	@Test
	@Tag("synth")
	void recentPosts032MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/032M"));
	}

	@Disabled("date arithmetics")
	@Test
	@Tag("synth")
	void recentPosts033XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/033X"));
	}

	@Test
	@Tag("synth")
	void recentPosts034SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/034"));
	}

	@Disabled("percentage")
	@Test
	@Tag("synth")
	void recentPosts035XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/035X"));
	}

	@Disabled("ORed JOIN conditions")
	@Test
	@Tag("synth")
	void recentPosts036SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/036"));
	}

	@Disabled("date calculation")
	@Test
	@Tag("synth")
	void recentPosts037XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/037X"));
	}

	@Test
	@Tag("synth")
	void recentPosts038SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/038"));
	}

	@Disabled("UNION")
	@Test
	@Tag("synth")
	void recentPosts039ASynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/039A"));
	}

	@Test
	@Tag("synth")
	void recentPosts040SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/040"));
	}

	@Disabled("pivot")
	@Test
	@Tag("synth")
	void recentPosts041XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/041X"));
	}

	@Test
	@Tag("synth")
	void recentPosts042SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/042"));
	}

	@Disabled("RECURSIVE")
	@Test
	@Tag("synth")
	void recentPosts043XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/043X"));
	}

	@Disabled("JOIN using multiple columns")
	@Test
	@Tag("synth")
	void recentPosts044MSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/044M"));
	}

	@Test
	@Tag("synth")
	void recentPosts045SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/045"));
	}

	@Test
	@Tag("synth")
	void recentPosts046XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/046X"));
	}

	@Disabled("window function requires multiple ORDER BY keys")
	@Test
	@Tag("synth")
	void recentPosts047XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/047X"));
	}

	@Disabled("join on two inequality conditions")
	@Test
	@Tag("synth")
	void recentPosts048XXSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/048XX"));
	}

	@Disabled("to json")
	@Test
	@Tag("synth")
	void recentPosts049XSynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/049X"));
	}

	@Disabled("left join on a 'column = constant' condition")
	@Test
	@Tag("synth")
	void recentPosts050SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/050"));
	}

	@Test
	@Tag("synth")
	void recentPosts051SynthesisTest() {
		SUtil.synthesizeFromScytheFile(new File("examples/scythe_mod/recent_posts/051"));
	}

}
