import java.util.ArrayList;
import java.util.List;

public class NURBS extends Curve {
	float[] weight;

	public NURBS(Vec3f[] points, int[] knots, float[] weight, int degree) {
		super(points, knots, degree);
		this.weight = weight;
	}

	@Override
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
}
