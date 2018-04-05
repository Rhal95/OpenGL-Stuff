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
public abstract class AbstractGLApplication {

	/**
	 * The glfw window.
	 */
	static long window;

	/**
	 * List of vertices. Should be changed to Vec3f or a new Vertex-class for
	 * better readability. Contains position and color info. in the order {x, y,
	 * z, r, g, b} for each point. Normals will be added some time.
	 */
	List<ColorPoint> vert_List = new ArrayList<>();
	/**
	 * List which specifies the order in which vertices are passed to the GPU.
	 */
	List<Integer> elem_List = new ArrayList<>();

	/**
	 * Vertex shader as multiline String.
	 */
	String vertex_Shader;
	/**
	 * Fragment shader as multiline String.
	 */
	String fragment_Shader;

	/**
	 * Buffer for the vertices (position and color)
	 */
	FloatBuffer verts;

	/**
	 * buffer for the elements (order of drawn vertices)
	 */
	IntBuffer elements;

	/**
	 * The Attribute for loading the model matrix in the shader
	 */
	int modelAttrib;

	/**
	 * Vertex Array Buffer
	 */
	int vao;

	/**
	 * Element Buffer Object
	 */
	int ebo;

	/**
	 * Vertex Buffer Object
	 */
	int vbo;

	/**
	 * The index for using the vertex shader
	 */
	int vert_shader;

	/**
	 * The index for using the fragment shader
	 */
	int frag_shader;

	/**
	 * the index for using the shader program
	 */
	int shaderProgram;

	/**
	 * the index for loading position to the vertex shader
	 */
	int posAttrib;

	/**
	 * the index for loading color to the vertex shader
	 */
	int colAttrib;

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

		glfwSetKeyCallback(window, keyHandler()

		);

		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwGetWindowSize(window, width, height);
			GLFW.glfwSetWindowPos(window, (vidmode.width() - width.get()) / 2, (vidmode.height() - height.get()) / 2);
		}

		// Initializing OpenGL

		GL.createCapabilities();

		glSettings();
		
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

		colAttrib = glGetAttribLocation(shaderProgram, "color");

		modelAttrib = glGetUniformLocation(shaderProgram, "model");

		// building geometry and loading them to the GPU

		vao = glGenVertexArrays();// vertex array object
		glBindVertexArray(vao);

		ebo = glGenBuffers();// element buffer object
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

		vbo = glGenBuffers();// vertex buffer object
		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		buildGeometry();

		verts = MemoryStack.stackMallocFloat(vert_List.size() * 6);

		elements = MemoryStack.stackMallocInt(elem_List.size());

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

	abstract void glSettings();

	/**
	 * Main loop.
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
	 *            The location of the model attribute in the shader. Calculates
	 *            the world transformation.
	 */
	abstract void calcTransform(int modelAttrib);

	/**
	 * Calculates the vertices to be drawn.
	 */
	abstract void buildGeometry();

	/**
	 * @returna GLFWKeyCallbackI function which handles the input
	 */
	public abstract GLFWKeyCallbackI keyHandler();
}
