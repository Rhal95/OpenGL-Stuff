package rhal95.opengl.knot;

public enum Stitches {
	cast_on, cast_off, right, left, yarn_over, right_increase, left_increase, right_decrease, left_decrease;

	int topStitchesAmount() {
		switch (this) {
		case right_increase:
		case left_increase:
			return 2;
		case cast_off:
			return 0;
		default:
			return 1;
		}
	}

	int bottomStitchesAmount() {
		switch (this) {
		case right_decrease:
		case left_decrease:
			return 2;
		case yarn_over:
		case cast_on:
			return 0;
		default:
			return 1;
		}
	}
}
