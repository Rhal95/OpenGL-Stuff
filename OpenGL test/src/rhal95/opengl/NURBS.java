package rhal95.opengl;
import java.util.ArrayList;
import java.util.List;

public class NURBS extends Curve {
	Vec4f[] points;

	public NURBS(Vec3f[] points, int[] knots, float[] weight, int degree) {
		super(points, knots, degree);
		Vec4f[] vectors = new Vec4f[points.length];
		for(int i =0 ; i < points.length; i++){
			Vec3f p = points[i];
			vectors[i] = new Vec4f(p.x, p.y, p.z, weight[i]);
		}
		this.points = vectors;
	}

	public NURBS(Vec4f[] points, int[] knots, int degree) {
		super(points, knots, degree);
		this.points = points;
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
	protected Vec4f deBoor(int r, int i, float t) {
		Vec4f result;
		if (r == 0) {
			result = points[i];
		} else {
			float a = (t - knots[i]) / (knots[i + 1 + degree - r] - knots[i]);
			result = deBoor(r - 1, i - 1, t).mult(1 - a).plus(deBoor(r - 1, i, t).mult(a));
		}
		return result;
	}
	
	@Override
	public List<Vec3f> curve(float res) {
		List<Vec3f> result = new ArrayList<>();

		for (float x = degree; x < controlPoints.length; x = x + res) {
			int k = 0;
			k = findIndex(x);
			Vec4f pt = (Vec4f) deBoor(degree, k, x);
			result.add(pt.toVec3f());
		}

		return result;
	}
}
