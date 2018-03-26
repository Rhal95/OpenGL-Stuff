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
public class NURBTest {

	/**
	 * The glfw window.
	 */
	private static long window;

	/**
	 * List of vertices. Should be changed to Vec3f or a new Vertex-class for better
	 * readability. Contains position and color info. in the order {x, y, z, r, g,
	 * b} for each point. Normals will be added some time.
	 */
	private List<ColorPoint> vert_List = new ArrayList<>();
	/**
	 * List which specifies the order in which vertices are passed to the GPU.
	 */
	private List<Integer> elem_List = new ArrayList<>();

	/**
	 * Vertex shader as multiline String.
	 */
	private String vertex_Shader;
	/**
	 * Fragment shader as multiline String.
	 */
	private String fragment_Shader;

	/** rotation around x axis. */
	private float rotx = 0;
	/** rotation around y axis. */
	private float roty = 0;
	/** rotation around z axis. */
	private float rotz = 0;

	private FloatBuffer verts;

	private IntBuffer elements;

	Vec3f[] pts = { new Vec3f(0.3f, 0.2f, 0f), new Vec3f(-0.3f, -1f, 0), new Vec3f(-0.8f, 0.0f, 0),
			new Vec3f(-0.3f, 0.3f, 0f), new Vec3f(0.2f, 0.3f, 0f), new Vec3f(0f, -1f, 0f),
			new Vec3f(-0.3f, 0.75f, 0f) };

	int selected = 0;

	private int modelAttrib;

	private int vao;

	private int ebo;

	private int vbo;

	private int vert_shader;

	private int frag_shader;

	private int shaderProgram;

	private int posAttrib;

	private int colAttrib;

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

	/**
	 * This function is called by the main. It cleans up when shutting down the
	 * program.
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
	 * Here belongs everything for creating the window and preparing OpenGL.
	 * Currently only glfw (window).
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
				pts[selected].x -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
				pts[selected].x += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
				pts[selected].y += 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
				pts[selected].y -= 0.1;
			}
			if (glfwGetKey(window, GLFW_KEY_TAB) == GLFW_PRESS) {
				selected = (selected + 1) % pts.length;
			}
			if (glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS) {
				for (Vec3f v : pts) {
					System.out.println(v);
				}
			}
		});

		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwGetWindowSize(window, width, height);
			GLFW.glfwSetWindowPos(window, (vidmode.width() - width.get()) / 2, (vidmode.height() - height.get()) / 2);
		}

		GL.createCapabilities();

		glEnable(GL_POINT_SIZE);

		vao = glGenVertexArrays();ebo = glGenBuffers();vbo = glGenBuffers();
		glBindVertexArray(vao);

		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		buildGeometry();
		
		
		verts = FloatBuffer.allocate(vert_List.size() * 6);
		
		elements = IntBuffer.allocate(elem_List.size());
		for (ColorPoint cp : vert_List) {
			verts.put(cp.flatten(), 0, 6);
		}
		verts.position(0);
		for (int i : elem_List)
			elements.put(i);
		elements.position(0);

		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
		glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

		
		vert_shader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vert_shader, vertex_Shader);
		glCompileShader(vert_shader);
		int[] vert_status = new int[1];
		glGetShaderiv(vert_shader, GL_COMPILE_STATUS, vert_status);
		if (vert_status[0] != GL_TRUE) {
			System.err.println(glGetShaderInfoLog(vert_shader));
		}

		frag_shader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(frag_shader, fragment_Shader);
		glCompileShader(frag_shader);
		int[] frag_status = new int[1];
		glGetShaderiv(frag_shader, GL_COMPILE_STATUS, frag_status);
		if (frag_status[0] != GL_TRUE) {
			System.err.println(glGetShaderInfoLog(frag_shader));
		}

		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vert_shader);
		glAttachShader(shaderProgram, frag_shader);

		glBindFragDataLocation(shaderProgram, 0, "outColor");

		glLinkProgram(shaderProgram);

		glUseProgram(shaderProgram);

		posAttrib = glGetAttribLocation(shaderProgram, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 0 * Float.BYTES);

		colAttrib = glGetAttribLocation(shaderProgram, "color");
		glEnableVertexAttribArray(colAttrib);
		glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

		modelAttrib = glGetUniformLocation(shaderProgram, "model");
	

		

	}

	/**
	 * Main loop. Builds the scene and runs the loop itself. Should be refactored to
	 * only include the loop itself.
	 */
	public void loop() {

		while (!glfwWindowShouldClose(window)) {
		
			glClearColor(0f, 0f, 0.25f, 1f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			calcTransform(modelAttrib);

			glDrawElements(GL_LINE_STRIP, elem_List.size(), GL_UNSIGNED_INT, 0);

			glfwSwapBuffers(window);
			glfwPollEvents();

		}

	}

	/**
	 * @param modelAttrib
	 *            The location of the model attribute in the shader. Calculates the
	 *            world transformation.
	 */
	private void calcTransform(int modelAttrib) {
		float[] model = Matrix4f.identity().mult(Matrix4f.trans_Mat(0, 0, 0)).rotx(rotx).roty(roty).rotz(rotz)
				.flatten();

		glUniformMatrix4fv(modelAttrib, false, model);
	}

	/**
	 * Calculates the vertices to be drawn.
	 */
	private void buildGeometry() {
		vert_List.clear();
		elem_List.clear();

		int[] e = Curve.array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		Curve c = new Curve(pts, e, 3);
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

}
