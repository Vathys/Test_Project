package org.lwjglb.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Window {
    private String title;
    private int width, height;
    private boolean vSync;
    private long windowHandle;
    private boolean resized;

    public Window(String title, int width, int height, boolean vSync) {
	this.title = title;
	this.width = width;
	this.height = height;
	this.vSync = vSync;
	this.resized = false;

    }

    public void init() {
	GLFWErrorCallback.createPrint(System.err).set();

	if (!glfwInit()) {
	    throw new IllegalStateException("Unable to initialize GLFW");
	}

	glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
	glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
	glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

	windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);

	if (windowHandle == NULL) {
	    throw new RuntimeException("Failed to create GLFW window");
	}

	glfwSetKeyCallback(windowHandle, new GLFWKeyCallbackI() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
		    glfwSetWindowShouldClose(windowHandle, true);
		}
	    }
	});

	GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

	glfwSetWindowPos(windowHandle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

	glfwMakeContextCurrent(windowHandle);

	if (isVSync()) {
	    glfwSwapInterval(1);
	}

	glfwShowWindow(windowHandle);

	GL.createCapabilities();

	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	glEnable(GL_DEPTH_TEST);
	// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public long getWindowHandle() {
	return windowHandle;
    }

    public void setClearColor(float r, float g, float b, float alpha) {
	glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
	return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
	return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
	return title;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public boolean isResized() {
	return resized;
    }

    public void setResized(boolean resized) {
	this.resized = resized;
    }

    public boolean isVSync() {
	return vSync;
    }

    public void setvSync(boolean vSync) {
	this.vSync = vSync;
    }

    public void update() {
	glfwSwapBuffers(windowHandle);
	glfwPollEvents();
    }
}
