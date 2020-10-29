package patsql.synth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import patsql.ra.operator.RA;
import patsql.synth.filler.FillingConstraint;

import java.util.TreeMap;

public class Debug {

	public static boolean isDebugMode = true;

	public static void printSummary() {
		if (isDebugMode) {
			Time.print();
		}
	}

	public static class Time {
		private static Map<Object, Long> ra2ms = new HashMap<>();
		private static List<Long> synthms = new ArrayList<>();

		public static void doneSynth(Long ms) {
			synthms.add(ms);
		}

		public static void register(Object o, Long ms) {
			Long l = ra2ms.get(o);
			if (l == null) {
				l = 0L;
			}
			ra2ms.put(o, l + ms);
		}

		public static void register(RA r, FillingConstraint c, Long ms) {
			RaC rac = new Time.RaC(r, c);
			Long l = ra2ms.get(rac);
			if (l == null) {
				l = 0L;
			}
			ra2ms.put(rac, l + ms);
		}

		public static void print() {
			System.out.println("** DEBUG INFO **");
			System.out.println(new Date().toString());
			TreeMap<Long, Object> map = new TreeMap<>();
			for (Entry<Object, Long> e : ra2ms.entrySet()) {
				map.put(e.getValue(), e.getKey());
			}
			for (Entry<Long, Object> e : map.entrySet()) {
				String str = String.format("%10d ms", e.getKey());
				System.out.println(str + " " + e.getValue());
			}

			if (!synthms.isEmpty()) {
				System.out.println(" - for each program");

				Collections.sort(synthms);
				Long max = synthms.get(synthms.size() - 1);
				Long min = synthms.get(0);
				Long med = synthms.get((synthms.size() - 1) / 2);
				Long sum = synthms.stream().reduce(0L, Long::sum);
				Long size = (long) synthms.size();
				Long avg = sum / size;

				System.out.println("  size " + String.format("%10d", size));
				System.out.println("   min " + String.format("%10d ms", min));
				System.out.println("   med " + String.format("%10d ms", med));
				System.out.println("   avg " + String.format("%10d ms", avg));
				System.out.println("   max " + String.format("%10d ms", max));
				System.out.println("   sum " + String.format("%10d ms", sum));

				for (Long t : synthms) {
					System.out.println((t != 0) ? t : 1);

//					double r = ((double) t / (double) max) * 60;
//					System.out.print("|");
//					for (int i = 0; i < (int) r; i++) {
//						System.out.print("+");
//					}
//					System.out.println();
				}
			}

		}

		static class RaC {
			RA ra;
			FillingConstraint c;

			public RaC(RA ra, FillingConstraint c) {
				this.ra = ra;
				this.c = c;
			}

			@Override
			public String toString() {
				return ra + "\t" + c;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((c == null) ? 0 : c.hashCode());
				result = prime * result + ((ra == null) ? 0 : ra.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				RaC other = (RaC) obj;
				if (c == null) {
					if (other.c != null)
						return false;
				} else if (!c.equals(other.c))
					return false;
				if (ra != other.ra)
					return false;
				return true;
			}
		}

	}
}
