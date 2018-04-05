package rhal95.opengl;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
public class CurveDrawer extends AbstractGLApplication {

	Vec4f[] pts;
	int[] knots;
	int selected = 0;
	int degree;
	float resolution;
	
	float x=0,y=0,z=0;
	
	boolean changed = true;
	private float rx;
	private float ry;
	private float rz;

	/**
	 * @throws IOException
	 *             We are reading the shaders from a extern file. Reading the
	 *             shaders. Could also be done in a normal function.
	 */
	public CurveDrawer(Vec4f[] pts, int[] knots, int degree, float resolution) throws IOException {
		this.pts = pts;
		this.degree = degree;
		this.resolution = resolution;
		this.knots = knots;
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

			if (changed) {
				changed = false;

				buildGeometry();

				verts.clear();

				elements.clear();

				for (ColorPoint cp : vert_List) {
					verts.put(cp.flatten(), 0, 6);
				}
				verts.position(0);

				for (int i : elem_List)
					elements.put(i);
				elements.position(0);

				glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
				glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

				glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 0 * Float.BYTES);
				glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

				glEnableVertexAttribArray(posAttrib);
				glEnableVertexAttribArray(colAttrib);
			}
			calcTransform(modelAttrib);

			glDrawElements(GL_LINE_STRIP, elem_List.size(), GL_UNSIGNED_INT, 0);

			glfwSwapBuffers(window);
			glfwPollEvents();
		}

	}

	/**
	 * @param modelAttrib
	 *            The location of the model attribute in the shader. Calculates
	 *            the world transformation.
	 */
	void calcTransform(int modelAttrib) {
		float[] model = Matrix4f.identity().mult(Matrix4f.trans_Mat(x, y, z)).scale(0.1f, 0.1f, 0.1f).rotx(rx).roty(ry).rotz(rz).flatten();

		glUniformMatrix4fv(modelAttrib, false, model);
	}

	/**
	 * Calculates the vertices to be drawn.
	 */
	void buildGeometry() {
		vert_List.clear();
		elem_List.clear();

		Curve c = new NURBS(pts, knots, degree);
		List<Vec3f> l = c.curve(resolution);
		for (Vec3f v : l) {
			elem_List.add(vert_List.size());
			vert_List.add(new ColorPoint(v, new Vec3f(1f, 1, 0)));
		}

	}


	@Override
	public GLFWKeyCallbackI keyHandler() {
		return (window, key, scancode, action, mods) -> {
			if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
				glfwSetWindowShouldClose(window, true);
			}
			if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
				pts[selected].x -= 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
				pts[selected].x += 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
				pts[selected].y += 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
				pts[selected].y -= 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_TAB) == GLFW_PRESS) {
				selected = (selected + 1) % pts.length;
			}
			if (glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS) {
				for (Vec3f v : pts) {
					System.out.println(v);
				}
			}
			if (glfwGetKey(window, GLFW_KEY_PAGE_UP) == GLFW_PRESS) {
				pts[selected].t += 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_PAGE_DOWN) == GLFW_PRESS) {
				pts[selected].t -= 0.1;
				changed = true;
			}
			if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
				x += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
				x -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
				y += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
				y -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
				z += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
				z -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) {
				rx += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) {
				rx -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
				ry += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_H) == GLFW_PRESS) {
				ry -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS) {
				rz += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS) {
				rz -= 0.1;
			}
			;
		};

	}

	@Override
	void glSettings() {
	}

}
