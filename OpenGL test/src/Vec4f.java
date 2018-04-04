
public class Vec4f extends Vec3f {
	float t;

	public Vec4f(float x, float y, float z, float t) {
		super(x, y, z);
		this.t = t;
	}

	public Vec4f(float x, float y, float z) {
		this(x, y, z, 1);
	}

	public Vec4f mult(float s) {
		return new Vec4f(x * s, y * s, z * s, t * s);
	}

	public Vec4f plus(Vec4f v) {
		return new Vec4f(x + v.x, y + v.y, z + v.z, t + v.t);
	}

	@Override
	public float[] flatten() {
		float[] result = new float[4];
		result[0] = x;
		result[1] = y;
		result[2] = z;
		result[3] = t;
		return result;
	}

	/**
	 * @return A vec3f with the t value as weight 
	 */
	Vec3f toVec3f() {
		return new Vec3f(x / t, y / t, z / t);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + t + ")";
	}
}
