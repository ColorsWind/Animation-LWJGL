package net.colors_wind.cglab3.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil

typealias KeyCallback = (window: Window, key: Int, scancode: Int, action: Int, mods: Int) -> Unit
typealias MouseButtonCallback = (window: Window, key: Int, action: Int, mods: Int) -> Unit

class Window(val title: String, var width: Int, var height: Int, var vSync: Boolean) {
    var windowHandle: Long = 0L
        private set
    var isResized = false

    fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE) // the window will be resizable
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        // Create the window
        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(windowHandle) { window: Long, width: Int, height: Int ->
            this.width = width
            this.height = height
            isResized = true
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(windowHandle) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
            }
        }

        // Get the resolution of the primary monitor
        val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        // Center our window
        GLFW.glfwSetWindowPos(
            windowHandle,
            (vidmode!!.width() - width) / 2,
            (vidmode.height() - height) / 2
        )

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowHandle)
        if (vSync) {
            // Enable v-sync
            GLFW.glfwSwapInterval(1)
        }

        // Make the window visible
        GLFW.glfwShowWindow(windowHandle)
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST);
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS
    }

    fun isWindowShouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(windowHandle)
    }

    fun setWindowShouldClose(shouldClose: Boolean = true) {
        GLFW.glfwSetWindowShouldClose(windowHandle, shouldClose)
    }


    fun isCaughtCursor(): Boolean = GLFW.glfwGetInputMode(windowHandle, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED

    fun setCaughtCursor(caught: Boolean) {
        GLFW.glfwSetInputMode(
            windowHandle,
            GLFW.GLFW_CURSOR,
            if (caught) GLFW.GLFW_CURSOR_DISABLED else GLFW.GLFW_CURSOR_NORMAL
        )
    }

    fun updateTitle(title: String) {
        GLFW.glfwSetWindowTitle(windowHandle, title)
    }


    inline fun setKeyCallback(crossinline callback: KeyCallback) {
        GLFW.glfwSetKeyCallback(windowHandle) { _, key, scancode, action, mods ->
            callback.invoke(
                this,
                key,
                scancode,
                action,
                mods
            )
        }
    }

    inline fun setMouseButtonCallback(crossinline callback: MouseButtonCallback) {
        GLFW.glfwSetMouseButtonCallback(windowHandle) { _, key, action, mods ->
            callback.invoke(
                this,
                key,
                action,
                mods
            )
        }
    }

    fun update() {
        GLFW.glfwSwapBuffers(windowHandle)
        GLFW.glfwPollEvents()
    }

    fun restoreState() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
    }
}