
/**
 * @author Lukas
 *
 */
public class Vec3f {
	/**
	 * values of vector
	 */
	@SuppressWarnings("javadoc")
	float x, y, z;

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return a new vector with xyz values
	 */
	Vec3f of(float x, float y, float z) {
		return new Vec3f(x, y, z);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * 
	 */
	public Vec3f(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return a new array representing the vector
	 */
	public float[] flatten() {
		float[] result = new float[3];
		result[0] = x;
		result[1] = y;
		result[2] = z;
		return result;
	}

	/**
	 * @param v the vector to add
	 * @return a new vector representing this + v
	 */
	public Vec3f plus(Vec3f v) {
		return new Vec3f(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * @param s scalar to multiply the vector with
	 * @return a new vector scaled by s
	 */
	public Vec3f mult(float s) {
		return new Vec3f(x * s, y * s, z * s);
	}
}
