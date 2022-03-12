package net.colors_wind.cglab3.engine

import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f = Vector3f(0.0F, 1.0F, 0.0F),
    var yaw: Float = 45.0F,
    var pitch: Float = 0.0F,
) {
    val worldUp: Vector3f = Vector3f(0.0F, 1.0F, 0.0F)

    val front: Vector3f = Vector3f(0.0F, 0.0F, 0.0F)
    val right: Vector3f = Vector3f(0.0F, 0.0F, 0.0F)
    val up: Vector3f = Vector3f(0.0F, 0.0F, 1.0F)

    fun updateViewCoordinate(window: Window) {
        front.set(
            cos(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())),
            sin(Math.toRadians(pitch.toDouble())),
            sin(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())),
        ).normalize()
        // also re-calculate the Right and Up vector
        right.set(front).cross(worldUp).normalize()
        up.set(right).cross(worldUp).normalize()
        fun s(f: Float) : String = "%.2f".format(f)
        window.updateTitle("x=${s(position.x)} y=${s(position.y)} z=${s(position.z)} yaw=${s(yaw)} pitch=${s(pitch)}")
    }


    fun moveCameraDirection(directionMoveVector: Vector2f) {
        yaw = (yaw + directionMoveVector.x * SPEED_YAW).mod(360.0F)
        pitch = (pitch - directionMoveVector.y * SPEED_PITCH).coerceIn(-89.0F, 89.0F)
    }

    fun moveCameraPosition(positionMoveVector: Vector3f) {
        positionMoveVector.mul(SPEED)
        position.add(Vector3f(front).apply { y = 0.0F }.mul(positionMoveVector.z))
            .add(Vector3f(right).apply { y = 0.0F }.mul(positionMoveVector.x))
            .add(Vector3f(worldUp).mul(positionMoveVector.y))
    }

    companion object {
        val SPEED = 0.1F
        val SPEED_YAW = 0.3F
        val SPEED_PITCH = 1.0F
    }
}