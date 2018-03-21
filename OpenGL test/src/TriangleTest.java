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
public class TriangleTest {

	/**
	 * the glfw window
	 */
	private static long window;

	/**
	 * list of vertices and color. Order is: {x, y, z, r, g, b} so 6 floats per vertex
	 */
	private List<Float> vert_List = new ArrayList<>();
	/**
	 * specififes the order of vertices passed to the gpu
	 */
	private List<Integer> elem_List = new ArrayList<>();

	/**
	 * vertex shader program as multiline string
	 */
	private String vertex_Shader;
	/**
	 * fragment shader program as multiline string
	 */
	private String fragment_Shader;

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
	 * reading the shader files
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

	/**
	 * function called by main. cleans up after termination.
	 */
	public void run() {
		init();
		loop();
		glfwDestroyWindow(window);
		glfwFreeCallbacks(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	/**
	 * start up function. currently only glfw. 
	 */
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		window = glfwCreateWindow(500, 500, "Test", 0, 0);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		glfwMakeContextCurrent(window);

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
				glfwSetWindowShouldClose(window, true);
			}
			if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
				rotx += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
				rotx -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
				roty += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
				roty -= 0.1;
			}
		});

		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwGetWindowSize(window, width, height);
			GLFW.glfwSetWindowPos(window, (vidmode.width() - width.get()) / 2, (vidmode.height() - height.get()) / 2);
		}
	}

	/**
	 * main loop. can and should be minimized
	 */
	public void loop() {
		GL.createCapabilities();

		int vao = glGenVertexArrays();
		glBindVertexArray(vao);

		buildGeometry();


		try (MemoryStack stack = MemoryStack.stackPush()) {

			FloatBuffer verts = stack.mallocFloat(vert_List.size());
			IntBuffer elements = stack.mallocInt(elem_List.size());

			for (int i = 0; i < vert_List.size(); i += 3) {
				verts.put(vert_List.get(i + 0));
				verts.put(vert_List.get(i + 1));
				verts.put(vert_List.get(i + 2));
			}

			verts.position(0);

			for (int i : elem_List)
				elements.put(i);
			elements.position(0);

			int ebo = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

			int vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

			int vert_shader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vert_shader, vertex_Shader);
			glCompileShader(vert_shader);
			int[] vert_status = new int[1];
			glGetShaderiv(vert_shader, GL_COMPILE_STATUS, vert_status);
			if (vert_status[0] != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(vert_shader));
			}

			int frag_shader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(frag_shader, fragment_Shader);
			glCompileShader(frag_shader);
			int[] frag_status = new int[1];
			glGetShaderiv(frag_shader, GL_COMPILE_STATUS, frag_status);
			if (frag_status[0] != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(frag_shader));
			}

			int shaderProgram = glCreateProgram();
			glAttachShader(shaderProgram, vert_shader);
			glAttachShader(shaderProgram, frag_shader);

			glBindFragDataLocation(shaderProgram, 0, "outColor");

			glLinkProgram(shaderProgram);

			glUseProgram(shaderProgram);

			int posAttrib = glGetAttribLocation(shaderProgram, "position");
			glEnableVertexAttribArray(posAttrib);
			glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 0 * Float.BYTES);

			int colAttrib = glGetAttribLocation(shaderProgram, "color");
			glEnableVertexAttribArray(colAttrib);
			glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

			int modelAttrib = glGetUniformLocation(shaderProgram, "model");

			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);

			while (!glfwWindowShouldClose(window)) {
				glClearColor(0f, 0f, 0f, 1f);
				glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

				calcTransform(modelAttrib);

				glDrawElements(GL_TRIANGLES, elem_List.size(), GL_UNSIGNED_INT, 0);

				glfwSwapBuffers(window);
				glfwPollEvents();
			}
		}
	}

	/**
	 * @param modelAttrib
	 * calculates the world transformation.
	 */
	private void calcTransform(int modelAttrib) {
		float[] model = Matrix4f.identity().mult(Matrix4f.trans_Mat(0, 0, 0)).rotx(rotx).roty(roty).rotz(rotz).flatten();

		glUniformMatrix4fv(modelAttrib, false, model);
	}

	/**
	 * fills the vertex and element list
	 */
	private void buildGeometry() {
		 cube(-0.5f, -0.5f, 0f, 0.5f, Color.BLUE);
		cube(0.5f, 0.5f, 0f, 0.5f, Color.RED);
		 plane(-0.75f, -0.25f, -0.25f, -0.25f, -0.75f, -0.25f, 0.25f, 0.25f,
		 0.25f, Color.GREEN);
	}

	/**
	 * @param x x coord of cube center
	 * @param y y coord of cube center
	 * @param z z coord of cube center
	 * @param size the distance of one face to the opposite.
	 * @param c the color of the cube
	 */
	private void cube(float x, float y, float z, float size, Color c) {
		float r = size / 2;
		int pos = vert_List.size() / 6;
		vert_List.add(x - r); // 0
		vert_List.add(y - r); // 0 0 0
		vert_List.add(z - r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x - r); // 1
		vert_List.add(y - r); // 0 0 1
		vert_List.add(z + r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x - r); // 2
		vert_List.add(y + r); // 0 1 0
		vert_List.add(z - r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x - r); // 3
		vert_List.add(y + r); // 0 1 1
		vert_List.add(z + r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x + r); // 4
		vert_List.add(y - r); // 1 0 0
		vert_List.add(z - r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x + r); // 5
		vert_List.add(y - r); // 1 0 1
		vert_List.add(z + r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x + r); // 6
		vert_List.add(y + r); // 1 1 0
		vert_List.add(z - r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x + r); // 7
		vert_List.add(y + r); // 1 1 1
		vert_List.add(z + r);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

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
	 * Draws a triangle between 3 points with a specified color.
	 * The first 3 parameters specify the first point, 4-6 the second and 7-9 the third point. c specifies the color which should be used. 
	 */
	private void plane(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3,
			Color c) {
		int pos = vert_List.size() / 6;
		vert_List.add(x1);
		vert_List.add(y1);
		vert_List.add(z1);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x2);
		vert_List.add(y2);
		vert_List.add(z2);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		vert_List.add(x3);
		vert_List.add(y3);
		vert_List.add(z3);
		vert_List.add(255f / c.getRed());
		vert_List.add(255f / c.getGreen());
		vert_List.add(255f / c.getBlue());

		elem_List.add(pos);
		elem_List.add(pos + 1);
		elem_List.add(pos + 2);
	}

	/**
	 * @param args
	 * main function.
	 */
	public static void main(String[] args) {
		try {
			new TriangleTest().run();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
