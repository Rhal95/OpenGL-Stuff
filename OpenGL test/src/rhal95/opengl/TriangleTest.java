package rhal95.opengl;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author Lukas
 *
 */
public class TriangleTest extends AbstractGLApplication {

	/**
	 * rotation around x axis
	 */
	private float rotx = 0;
	/**
	 * rotation around y axis
	 */
	private float roty = 0;
	/**
	 * rotation around z axis
	 */
	private float rotz = 0;

	/**
	 * @throws IOException
	 *             reading the shader files
	 */
	public TriangleTest() throws IOException {
		List<String> vs = Files.readAllLines(new File("src/vertexShader.txt").toPath());
		StringBuilder vb = new StringBuilder();
		for (String s : vs) {
			vb.append(s);
			vb.append("\n");
		}
		vertex_Shader = vb.toString();

		List<String> fs = Files.readAllLines(new File("src/fragmentShader.txt").toPath());
		StringBuilder fb = new StringBuilder();
		for (String s : fs) {
			fb.append(s);
			fb.append("\n");
		}
		fragment_Shader = fb.toString();
	}

	@Override
	public void loop() {
		while (!glfwWindowShouldClose(window)) {

			glClearColor(0f, 0f, 0.25f, 1f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			calcTransform(modelAttrib);

			glDrawElements(GL_TRIANGLES, elem_List.size(), GL_UNSIGNED_INT, 0);

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	/**
	 * @param modelAttrib
	 *            calculates the world transformation.
	 */
	void calcTransform(int modelAttrib) {
		float[] model = Matrix4f.identity().mult(Matrix4f.trans_Mat(0, 0, 0)).rotx(rotx).roty(roty).rotz(rotz)
				.flatten();

		glUniformMatrix4fv(modelAttrib, false, model);
	}

	/**
	 * fills the vertex and element list
	 */
	void buildGeometry() {
		cube(-0.5f, -0.5f, 0f, 0.5f, Color.BLUE);
		cube(0.5f, 0.5f, 0f, 0.5f, Color.RED);
		plane(-0.75f, -0.25f, -0.25f, -0.25f, -0.75f, -0.25f, 0.25f, 0.25f, 0.25f, Color.GREEN);
	}

	/**
	 * @param x
	 *            x coord of cube center
	 * @param y
	 *            y coord of cube center
	 * @param z
	 *            z coord of cube center
	 * @param size
	 *            the distance of one face to the opposite.
	 * @param c
	 *            the color of the cube
	 */
	private void cube(float x, float y, float z, float size, Color c) {
		float r = size / 2;
		int pos = vert_List.size();
		vert_List.add(new ColorPoint(new Vec3f(x - r, y - r, z - r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x - r, y - r, z + r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x - r, y + r, z - r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x - r, y + r, z + r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x + r, y - r, z - r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x + r, y - r, z + r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x + r, y + r, z - r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x + r, y + r, z + r),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));

		// Down
		elem_List.add(pos + 0);
		elem_List.add(pos + 2);
		elem_List.add(pos + 6);
		elem_List.add(pos + 6);
		elem_List.add(pos + 4);
		elem_List.add(pos + 0);

		// Up
		elem_List.add(pos + 7);
		elem_List.add(pos + 3);
		elem_List.add(pos + 1);
		elem_List.add(pos + 1);
		elem_List.add(pos + 5);
		elem_List.add(pos + 7);

		// Left
		elem_List.add(pos + 0);
		elem_List.add(pos + 4);
		elem_List.add(pos + 5);
		elem_List.add(pos + 5);
		elem_List.add(pos + 1);
		elem_List.add(pos + 0);

		// Back
		elem_List.add(pos + 0);
		elem_List.add(pos + 1);
		elem_List.add(pos + 3);
		elem_List.add(pos + 3);
		elem_List.add(pos + 2);
		elem_List.add(pos + 0);

		// Right
		elem_List.add(pos + 7);
		elem_List.add(pos + 6);
		elem_List.add(pos + 2);
		elem_List.add(pos + 2);
		elem_List.add(pos + 3);
		elem_List.add(pos + 7);

		// Front
		elem_List.add(pos + 7);
		elem_List.add(pos + 5);
		elem_List.add(pos + 4);
		elem_List.add(pos + 4);
		elem_List.add(pos + 6);
		elem_List.add(pos + 7);
	}

	/**
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @param x3
	 * @param y3
	 * @param z3
	 * @param c
	 * 
	 *            Draws a triangle between 3 points with a specified color. The
	 *            first 3 parameters specify the first point, 4-6 the second and
	 *            7-9 the third point. c specifies the color which should be
	 *            used.
	 */
	private void plane(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3,
			Color c) {
		int pos = vert_List.size() / 6;
		vert_List.add(new ColorPoint(new Vec3f(x1, y1, z1),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x2, y2, z2),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));
		vert_List.add(new ColorPoint(new Vec3f(x3, y3, z3),
				new Vec3f(255f / c.getRed(), 255f / c.getGreen(), 255f / c.getBlue())));

		elem_List.add(pos);
		elem_List.add(pos + 1);
		elem_List.add(pos + 2);
	}

	/**
	 * @param args
	 *            main function.
	 */
	public static void main(String[] args) {
		try {
			new TriangleTest().run();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public GLFWKeyCallbackI keyHandler() {
		return (window, key, scancode, action, mods) -> {
			if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
				glfwSetWindowShouldClose(window, true);
			}
			if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
				rotx -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
				rotx += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
				roty -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
				roty += 0.1;
			}
		};
	}

	@Override
	void glSettings() {
		glCullFace(GL_BACK);
	}
}
