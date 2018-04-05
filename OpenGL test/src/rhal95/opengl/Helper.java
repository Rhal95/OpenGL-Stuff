package rhal95.opengl;

public class Helper {
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
	 * @param is
	 *            integer values to be stored in a new array
	 * @return a new array containing the given integers helper function to create a
	 *         new array from given values
	 */
	public static float[] array(float... is) {
		return is;
	}
}
