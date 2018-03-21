/**
 * @author Lukas
 *
 */
public class Matrix4f {
	/**
	 * fields representing numbers in a 4x4 matrix. first letter if for the row and second for the column
	 */
	@SuppressWarnings("javadoc")
	float xx, xy, xz, xt, yx, yy, yz, yt, zx, zy, zz, zt, tx, ty, tz, tt;

	/**
	 * constructor.
	 */
	@SuppressWarnings("javadoc")
	public Matrix4f(float xx, float xy, float xz, float xt, float yx, float yy, float yz, float yt, float zx, float zy,
			float zz, float zt, float tx, float ty, float tz, float tt) {
		super();
		this.xx = xx;
		this.xy = xy;
		this.xz = xz;
		this.xt = xt;
		this.yx = yx;
		this.yy = yy;
		this.yz = yz;
		this.yt = yt;
		this.zx = zx;
		this.zy = zy;
		this.zz = zz;
		this.zt = zt;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.tt = tt;
	}

	/**
	 * convenience constructor for double->float conversion.
	 */
	@SuppressWarnings("javadoc")
	public Matrix4f(double xx, double xy, double xz, double xt, double yx, double yy, double yz, double yt, double zx,
			double zy, double zz, double zt, double tx, double ty, double tz, double tt) {
		this((float) xx, (float) xy, (float) xz, (float) xt, (float) yx, (float) yy, (float) yz, (float) yt, (float) zx,
				(float) zy, (float) zz, (float) zt, (float) tx, (float) ty, (float) tz, (float) tt);
	}

	/**
	 * @return a new identity matrix
	 */
	static Matrix4f identity() {
		return new Matrix4f(
				1, 0, 0, 0, 
				0, 1, 0, 0, 
				0, 0, 1, 0, 
				0, 0, 0, 1);
	}

	/**
	 * @param r degree in radians to rotate 
	 * @return a new rotation matrix
	 */
	static Matrix4f rotx_Mat(float r) {
		return new Matrix4f(
				1, 0, 0, 0, 
				0, Math.cos(r), -Math.sin(r), 0, 
				0, Math.sin(r), Math.cos(r), 0, 
				0, 0, 0, 1);
	}

	/**
	 * @param r degree in radians to rotate 
	 * @return a new rotation matrix
	 */
	static Matrix4f roty_Mat(float r) {
		return new Matrix4f(
				Math.cos(r), 0, Math.sin(r), 0, 
				0, 1, 0, 0, 
				-Math.sin(r), 0, Math.cos(r), 0, 
				0, 0, 0, 1);
	}

	/**
	 * @param r degree in radians to rotate 
	 * @return a new rotation matrix
	 */
	static Matrix4f rotz_Mat(float r) {
		return new Matrix4f(
				Math.cos(r), -Math.sin(r), 0, 0, 
				Math.sin(r), Math.cos(r), 0, 0, 
				0, 0, 1, 0, 
				0, 0, 0, 1);
	}

	
	/**
	 * @param x translation in x direction
	 * @param y translation in y direction
	 * @param z translation in z direction
	 * @return a new translation matrix
	 */
	static Matrix4f trans_Mat(float x, float y, float z) {
		return new Matrix4f(
				1, 0, 0, x, 
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1);
	}

	/**
	 * @param x scaling factor along x axis
	 * @param y scaling factor along x axis
	 * @param z scaling factor along x axis
	 * @return a new scaling matrix
	 */
	static Matrix4f scale_Mat(float x, float y, float z){
		return new Matrix4f(
				x, 0, 0, 0, 
				0, y, 0, 0, 
				0, 0, z, 0, 
				0, 0, 0, 1);
	}
	
	
	/**
	 * @param that matrix to multiply with
	 * @return a new matrix representing this * that
	 */
	public Matrix4f mult(Matrix4f that){
		Matrix4f result = identity();
		result.xx = this.xx * that.xx + this.xy * that.yx + this.xz * that.zx + this.xt * that.tx;
		result.yx = this.yx * that.xx + this.yy * that.yx + this.yz * that.zx + this.yt * that.tx;
		result.zx = this.zx * that.xx + this.zy * that.yx + this.zz * that.zx + this.zt * that.tx;
		result.tx = this.tx * that.xx + this.ty * that.yx + this.tz * that.zx + this.tt * that.tx;
		
		result.xy = this.xx * that.xy + this.xy * that.yy + this.xz * that.zy + this.xt * that.ty;
		result.yy = this.yx * that.xy + this.yy * that.yy + this.yz * that.zy + this.yt * that.ty;
		result.zy = this.zx * that.xy + this.zy * that.yy + this.zz * that.zy + this.zt * that.ty;
		result.ty = this.tx * that.xy + this.ty * that.yy + this.tz * that.zy + this.tt * that.ty;
		
		result.xz = this.xx * that.xz + this.xy * that.yz + this.xz * that.zz + this.xt * that.tz;
		result.yz = this.yx * that.xz + this.yy * that.yz + this.yz * that.zz + this.yt * that.tz;
		result.zz = this.zx * that.xz + this.zy * that.yz + this.zz * that.zz + this.zt * that.tz;
		result.tz = this.tx * that.xz + this.ty * that.yz + this.tz * that.zz + this.tt * that.tz;
		
		result.xt = this.xx * that.xt + this.xy * that.yt + this.xz * that.zt + this.xt * that.tt;
		result.yt = this.yx * that.xt + this.yy * that.yt + this.yz * that.zt + this.yt * that.tt;
		result.zt = this.zx * that.xt + this.zy * that.yt + this.zz * that.zt + this.zt * that.tt;
		result.tt = this.tx * that.xt + this.ty * that.yt + this.tz * that.zt + this.tt * that.tt;
		return result;
	}

	/**
	 * @return a new array with the matrix data
	 */
	public float[] flatten() {
		float[] result = new float[16];
		result[0]	= xx;
		result[1]	= yx;
		result[2] 	= zx;
		result[3] 	= tx;
		
		result[4] 	= xy;
		result[5] 	= yy;	
		result[6] 	= zy;
		result[7] 	= ty;
		
		result[8] 	= xz;
		result[9] 	= yz;
		result[10] 	= zz;
		result[11] 	= tz;
		
		result[12] 	= xt;
		result[13] 	= yt;
		result[14] 	= zt;
		result[15] 	= tt;
		return result;
	}

	/**
	 * @param r degree of rotation in radians
	 * @return a new matrix representing this * rotx_Mat(r)
	 */
	public Matrix4f rotx(float r) {
		return this.mult(rotx_Mat(r));
	}
	/**
	 * @param r degree of rotation in radians
	 * @return a new matrix representing this * roty_Mat(r)
	 */
	public Matrix4f roty(float r) {
		return this.mult(roty_Mat(r));
	}
	/**
	 * @param r degree of rotation in radians
	 * @return a new matrix representing this * rotz_Mat(r)
	 */
	public Matrix4f rotz(float r) {
		return this.mult(rotz_Mat(r));
	}

	
	/**
	 * @param x translation in x direction
	 * @param y translation in y direction
	 * @param z translation in z direction
	 * @return a new translation matrix representing this * trans_Mat(x,y,z)
	 */
	public Matrix4f trans(float x, float y, float z) {
		return this.mult(trans_Mat(x, y, z));
	}
	
	/**
	 * @param x scaling factor along x axis
	 * @param y scaling factor along x axis
	 * @param z scaling factor along x axis
	 * @return a new scaling matrix representing this * scale_Mat(x,y,z)
	 */
	public Matrix4f scale(float x, float y, float z){
		return this.mult(scale_Mat(x, y, z));
	}
}
