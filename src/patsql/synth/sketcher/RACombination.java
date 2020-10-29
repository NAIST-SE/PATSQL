package patsql.synth.sketcher;

import patsql.ra.operator.RA;

public class RACombination {

	private static final boolean T = true;
	private static final boolean F = false;

	private static boolean[][] table = { //
			// parent\child, root, sort, δ, π, σ, γ, w, ⋈θ, ⋈L, ρ(R)
			/* root */ { F, T, T, T, F, F, F, F, F, F }, //
			/* sort */ { F, F, T, T, F, F, F, F, F, F }, //
			/* δ ****/ { F, F, F, T, F, F, F, F, F, F }, //
			/* π ****/ { F, F, F, F, T, T, T, T, T, T }, //
			/* σ ****/ { F, F, F, F, F, T, T, T, T, T }, //
			/* γ ****/ { F, F, F, F, T, T, T, T, T, T }, //
			/* w ****/ { F, F, F, F, T, T, T, T, T, T }, //
			/* ⋈θ ***/ { F, F, F, F, F, T, T, T, T, T }, //
			/* ⋈L ***/ { F, F, F, F, T, T, T, T, T, T }, //
			/* ρ(R) */ { F, F, F, F, F, F, F, F, F, F } //
	};

	public static boolean canBeChild(RA child, RA parent) {
		int childIdx = child.index();
		int parentIdx = parent.index();

		if (childIdx == -1 || parentIdx == -1) {
			return false;
		} else {
			return table[parentIdx][childIdx];
		}
	}

}
