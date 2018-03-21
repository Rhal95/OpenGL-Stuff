import java.util.ArrayList;
import java.util.List;

public class Curve {
	Vec3f[] pts;
	int[] ts;

	
	public Curve(Vec3f[] pts, int[] ts) {
		super();
		this.pts = pts;
		this.ts = ts;
	}

	public static Vec3f[] array(Vec3f...vec3fs){
		return vec3fs;
	}
	
	public static int[] array(int...is ){
		return is;
	}

	
	List<Vec3f> bezier(float res) {
		List<Vec3f> result = new ArrayList<>();

		for (double i = 0; i <= 1; i += res) {
			float t = (float) i;
			result.add(pts[0].mult((1 - t) * (1 - t) * (1 - t)).plus(pts[1].mult(3 * (1 - t) * (1 - t) * t))
					.plus(pts[2].mult(3 * (1 - t) * t * t)).plus(pts[3].mult(t * t * t)));
		}
		
		return result;

	}
	
	List<Vec3f> bspline(float res) {
		List<Vec3f> result = new ArrayList<>();

		for (double i = 0; i <= 1; i += res) {
			float t = (float) i;
			result.add(pts[0].mult((1 - t) * (1 - t) * (1 - t)).plus(pts[1].mult(3 * (1 - t) * (1 - t) * t))
					.plus(pts[2].mult(3 * (1 - t) * t * t)).plus(pts[3].mult(t * t * t)));
		}
		
		return result;

	}

}
