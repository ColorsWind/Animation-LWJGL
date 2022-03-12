package net.colors_wind.cglab3.engine


import net.colors_wind.cglab3.engine.graph.GameItem
import org.joml.Matrix4f
import org.joml.Vector3f
import java.lang.Math.*

class Transformation {
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val modelViewMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()

    fun getProjectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        val aspectRatio = width / height
        projectionMatrix.setPerspective(fov, aspectRatio, zNear, zFar)
        return projectionMatrix
    }

    fun getViewMatrix(camera: Camera): Matrix4f {
        viewMatrix.setLookAt(camera.position, Vector3f(camera.position).add(camera.front), camera.worldUp)
        return viewMatrix
    }

    fun getModelViewMatrix(gameItem: GameItem, viewMatrix: Matrix4f): Matrix4f {
        val rotation = gameItem.rotation
        modelViewMatrix.identity().translate(gameItem.position)
            .rotateX(toRadians(-rotation.x.toDouble()).toFloat())
            .rotateY(toRadians(-rotation.y.toDouble()).toFloat())
            .rotateZ(toRadians(-rotation.z.toDouble()).toFloat())
            .scale(gameItem.scale)
        val viewCurr = Matrix4f(viewMatrix)
        return viewCurr.mul(modelViewMatrix)
        return viewMatrix
    }

}
