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
public class NURBTest extends AbstractGLApplication {

	/** rotation around x axis. */
	private float rotx = 0;
	/** rotation around y axis. */
	private float roty = 0;
	/** rotation around z axis. */
	private float rotz = 0;

	Vec4f[] pts = { new Vec4f(0.3f, 0.2f, 0f), new Vec4f(-0.3f, -1f, 0), new Vec4f(-0.8f, 0.0f, 0),
			new Vec4f(-0.3f, 0.3f, 0f), new Vec4f(0.2f, 0.3f, 0f), new Vec4f(0f, -1f, 0f),
			new Vec4f(-0.3f, 0.75f, 0f) };

	int selected = 0;

	boolean changed = true;

	/**
	 * @throws IOException
	 *             We are reading the shaders from a extern file. Reading the
	 *             shaders. Could also be done in a normal function.
	 */
	public NURBTest() throws IOException {
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
		float[] model = Matrix4f.identity().mult(Matrix4f.trans_Mat(0, 0, 0)).rotx(rotx).roty(roty).rotz(rotz)
				.flatten();

		glUniformMatrix4fv(modelAttrib, false, model);
	}

	/**
	 * Calculates the vertices to be drawn.
	 */
	void buildGeometry() {
		vert_List.clear();
		elem_List.clear();

		int[] e = Helper.array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		Curve c = new NURBS(pts, e, 3);
		List<Vec3f> l = c.curve(0.0625f);
		for (Vec3f v : l) {
			elem_List.add(vert_List.size());
			vert_List.add(new ColorPoint(v, new Vec3f(1f, 1, 0)));
		}

	}

	/**
	 * @param args
	 *            Arguments. Main function to start the program.
	 */
	public static void main(String[] args) {
		try {
			new NURBTest().run();
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
			;
		};

	}

	@Override
	void glSettings() {
	}
}
