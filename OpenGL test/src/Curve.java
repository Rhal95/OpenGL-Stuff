import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas
 *
 */
public class Curve {
	/**
	 * control points of curve
	 */
	Vec3f[] controlPoints;
	/**
	 * knot values of curve
	 */
	int[] knots;
	/**
	 * Degree of curve
	 */
	int degree;

	/**
	 * @param points
	 *            the control points for the curve
	 * @param knots
	 *            the knot values for the curve. Length of this array is
	 *            points.length + degree + 1
	 * @param degree
	 *            degree of the curve
	 */
	public Curve(Vec3f[] points, int[] knots, int degree) {
		super();
		this.controlPoints = points;
		this.knots = knots;
		this.degree = degree;
	}

	/**
	 * @param vec3fs
	 *            Vector values to be stored in a new array
	 * @return a new array containing the given vectors helper function to create a
	 *         new array from given values
	 */
	public static Vec3f[] array(Vec3f... vec3fs) {
		return vec3fs;
	}

	/**
	 * @param is
	 *            integer values to be stored in a new array
	 * @return a new array containing the given integers helper function to create a
	 *         new array from given values
	 */
	public static int[] array(int... is) {
		return is;
	}

	/**
	 * @param r
	 *            the current degree of the function
	 * @param i
	 *            the index of the corresponding knot span. Found with the
	 *            findIndex-function
	 * @param t
	 *            point on the line in knotspan values
	 * @return the searched point deBoor algorithm according to
	 *         https://en.wikipedia.org/wiki/De_Boor's_algorithm
	 * 
	 */
	private Vec3f deBoor(int r, int i, float t) {
		Vec3f result;
		if (r == 0) {
			result = controlPoints[i];
		} else {
			float a = (t - knots[i]) / (knots[i + 1 + degree - r] - knots[i]);
			result = deBoor(r - 1, i - 1, t).mult(1 - a).plus(deBoor(r - 1, i, t).mult(a));
		}
		return result;
	}

	/**
	 * @param res
	 *            how far the inserted points are apart. Powers of 2 work best
	 *            (2^-x)
	 * @return a list containing the points of the given curve in respect to the
	 *         given resolution.
	 */
	public List<Vec3f> curve(float res) {
		List<Vec3f> result = new ArrayList<>();

		for (float x = degree; x < controlPoints.length; x = x + res) {
			int k = 0;
			k = findIndex(x);
			Vec3f pt = deBoor(degree, k, x);
			result.add(pt);
		}

		return result;
	}

	/**
	 * @param x
	 *            the given value
	 * @return the found index or -1 if nothing was found function for finding the
	 *         knots-array index for which knots[i-1] <= x <= knots[i]
	 */
	private int findIndex(float x) {
		for (int i = 1; i < knots.length - 1; i++) {
			if (x < knots[i])
				return i - 1;
			else if (x == knots[knots.length - 1])
				return knots.length - 1;
		}
		return -1;
	}

}
