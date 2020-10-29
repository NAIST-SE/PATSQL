package patsql.synth.benchmark;

import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class KaggleTest {

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_T_1.md"));
	}

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_T_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_T_2.md"));
	}

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_T_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_T_3.md"));
	}

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_T_4() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_T_4.md"));
	}

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_X_1.md"));
	}

	/**
	 * DISTINCT, WHERE. Synthesized with GROUP BY instead of DISTINCT.
	 */
	@Test
	@Tag("synth")
	void test_1_2_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_X_2.md"));
	}

	/**
	 * WHERE
	 */
	@Test
	@Tag("synth")
	void test_1_2_X_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_2_X_3.md"));
	}

	/**
	 * group by, count, having
	 */
	@Test
	@Tag("synth")
	void test_1_3_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_3_T_1.md"));
	}

	/**
	 * group by, count, having. Same I/O to 1_3_T_1. COUNT(1) is expected, but hard
	 * to synthesize.
	 */
	@Test
	@Tag("synth")
	void test_1_3_T_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_3_T_2.md"));
	}

	/**
	 * group by, count, having. The original query has constant 10000 instead of 10.
	 */
	@Test
	@Tag("synth")
	void test_1_3_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_3_X_1.md"));
	}

	/**
	 * count, where
	 */
	@Test
	@Tag("synth")
	void test_1_3_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_3_X_2.md"));
	}

	/**
	 * count, extract, group by, order by. The query in the tutorial has extract
	 * DAYOFWEEK function, but there is day_of_week field.
	 */
	@Test
	@Tag("synth")
	void test_1_4_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_4_T_1.md"));
	}

	/**
	 * avg, where, group by, order by
	 */
	@Test
	@Tag("synth")
	void test_1_4_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_4_X_1.md"));
	}

	/**
	 * count, where, group by, having, order by. The synthesized query does not have
	 * WHERE for now. Constant 5 is used instead of 175.
	 */
	@Test
	@Tag("synth")
	void test_1_4_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_4_X_2.md"));
	}

	/**
	 * CTE, count, group by, order by
	 */
	@Test
	@Tag("synth")
	void test_1_5_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_5_T_1.md"));
	}

	/**
	 * extract(year), count, group by, order by
	 */
	@Test
	@Tag("synth")
	void test_1_5_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_5_X_1.md"));
	}

	/**
	 * extract(month), count, where, group by, order by
	 */
	@Test
	@Tag("synth")
	void test_1_5_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_5_X_2.md"));
	}

	/**
	 * CTE, extract(hour), where, count, sum, *, /, group by, order by
	 */
	@Disabled("extract(hour), *, /")
	@Test
	@Tag("synth")
	void test_1_5_X_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_5_X_3.md"));
	}

	/**
	 * count, inner join, group by, order by
	 */
	@Test
	@Tag("synth")
	void test_1_6_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_6_T_1.md"));
	}

	/**
	 * where, like
	 */
	@Disabled("like")
	@Test
	@Tag("synth")
	void test_1_6_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_6_X_1.md"));
	}

	/**
	 * where, inner join, like
	 */
	@Disabled("like")
	@Test
	@Tag("synth")
	void test_1_6_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_6_X_2.md"));
	}

	/**
	 * where, count, inner join, like, group by
	 */
	@Disabled("like")
	@Test
	@Tag("synth")
	void test_1_6_X_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/1_6_X_3.md"));
	}

	/**
	 * CTE, count, group by, left join, where, order by
	 */
	@Test
	@Tag("synth")
	void test_2_1_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_T_1.md"));
	}

	/**
	 * where, union distinct
	 */
	@Disabled("union distinct")
	@Test
	@Tag("synth")
	void test_2_1_T_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_T_2.md"));
	}

	/**
	 * min, timestamp_diff(second), left join, where, group by, order by
	 */
	@Disabled("timestamp_diff(second), compare 2018-01-01 and 2018-01-01 0:00:00")
	@Test
	@Tag("synth")
	void test_2_1_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_X_1.md"));
	}

	/**
	 * min, full join, where, group by, but full join is not necessary
	 */
	@Disabled("10-min timeout")
	@Test
	@Tag("synth")
	void test_2_1_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_X_2.md"));
	}

	/**
	 * min, full join, right join, where, group by, but full join is not necessary
	 */
	@Disabled("10-min timeout")
	@Test
	@Tag("synth")
	void test_2_1_X_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_X_3.md"));
	}

	/**
	 * where, union distinct
	 */
	@Disabled("union distinct")
	@Test
	@Tag("synth")
	void test_2_1_X_4() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_1_X_4.md"));
	}

	/**
	 * CTE, count, where, extract, group by, sum (window function)
	 */
	@Disabled("10-min timeout")
	@Test
	@Tag("synth")
	void test_2_2_T_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_2_T_1.md"));
	}

	/**
	 * timestamp-to-time, first/last value (partition by, order by, rows unbounded
	 * following), where, date
	 */
	@Disabled("timestamp-to-time, first/last value rows unbounded following, date()")
	@Test
	@Tag("synth")
	void test_2_2_T_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_2_T_2.md"));
	}

	/**
	 * CTE, count, where, group by, order by, avg (window function, order by, rows
	 * between 15 preceding and 15 following)
	 */
	@Disabled("rows preceding/following")
	@Test
	@Tag("synth")
	void test_2_2_X_1() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_2_X_1.md"));
	}

	/**
	 * rank (partition by, order by), where, timestamp-to-date
	 */
	@Disabled("timestamp-to-date")
	@Test
	@Tag("synth")
	void test_2_2_X_2() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_2_X_2.md"));
	}

	/**
	 * timestamp_diff, lag (partition by, order by), where, timestamp-to-date
	 */
	@Disabled("timestamp_diff(minute), timestamp-to-date")
	@Test
	@Tag("synth")
	void test_2_2_X_3() {
		SUtil.synthesizeFromScytheFile(new File("examples/kaggle/2_2_X_3.md"));
	}

}
