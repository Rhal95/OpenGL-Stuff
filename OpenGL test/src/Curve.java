import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Curve {
	Vec3f[] controlPoints; // length n
	int[] knots;
	int degree;

	public Curve(Vec3f[] points, int[] knots, int degree) {
		super();
		this.controlPoints = points;
		this.knots = knots;
		this.degree = degree;
	}

	public static Vec3f[] array(Vec3f... vec3fs) {
		return vec3fs;
	}

	public static int[] array(int... is) {
		return is;
	}

	Vec3f deBoor(int r, int i, float t) {
		Vec3f result;
		if (r == 0) {
			result = controlPoints[i];
		} else {
			float a = (t - knots[i]) / (knots[i + 1 + degree - r] - knots[i]);
			result = deBoor(r - 1, i - 1, t).mult(1 - a).plus(deBoor(r - 1, i, t).mult(a));
		}
		return result;
	}

	List<Vec3f> curve(float res) {
		List<Vec3f> result = new ArrayList<>();

		for (float x = degree; x < controlPoints.length; x = x + res) {
			int k = 0;
			k = findIndex(x);
			Vec3f pt = deBoor(degree, k, x);
			result.add(pt);
		}

		return result;
	}

	private int findIndex(float x) {
		for (int i = 1; i < knots.length-1; i++) {
			if(x < knots[i]) return i-1;
			else if(x == knots[knots.length-1])
				return knots.length-1;
		}
		return -1;
	}

}
