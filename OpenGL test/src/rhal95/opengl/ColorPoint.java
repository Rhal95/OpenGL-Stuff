package rhal95.opengl;
import java.util.Arrays;

public class ColorPoint {
	Vec3f p;
	Vec3f c;

	public float[] flatten() {
		float[] result = new float[6];
		float[] pa = p.flatten();
		float[] ca = c.flatten();
		result[0] = pa[0];
		result[1] = pa[1];
		result[2] = pa[2];
		result[3] = ca[0];
		result[4] = ca[1];
		result[5] = ca[2];
		
		return result;
	}

	public ColorPoint(Vec3f p) {
		this(p, new Vec3f(1f,1f,1f));
	}
	
	public ColorPoint(Vec3f p, Vec3f c) {
		super();
		this.p = p;
		this.c = c;
	}
}
