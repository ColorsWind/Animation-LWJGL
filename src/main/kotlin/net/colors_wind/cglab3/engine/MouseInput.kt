package net.colors_wind.cglab3.engine

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW

class MouseInput {
    private val previousPos: Vector2d = Vector2d(-1.0, -1.0)
    private val currentPos: Vector2d = Vector2d(0.0, 0.0)
    val displayVec: Vector2f = Vector2f(0.0F, 0.0F)
    private var inWindow = false
    var isLeftButtonPressed = false
        private set
    var isRightButtonPressed = false
        private set

    fun init(window: Window) {
        GLFW.glfwSetCursorPosCallback(window.windowHandle) { windowHandle: Long, xpos: Double, ypos: Double ->
            currentPos.x = xpos
            currentPos.y = ypos
        }
        GLFW.glfwSetCursorEnterCallback(window.windowHandle) { windowHandle: Long, entered: Boolean ->
            inWindow = entered
        }
        GLFW.glfwSetMouseButtonCallback(window.windowHandle) { windowHandle: Long, button: Int, action: Int, mode: Int ->
            isLeftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS
            isRightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS
        }
    }

    fun input(window: Window) {
        displayVec.x = (currentPos.x - previousPos.x).toFloat()
        displayVec.y = (currentPos.y - previousPos.y).toFloat()
        previousPos.set(currentPos)
//        Thread.sleep(300L)
//        println(currentPos)
    }

}