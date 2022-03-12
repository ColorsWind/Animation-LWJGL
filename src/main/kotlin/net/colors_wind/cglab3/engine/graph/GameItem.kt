package net.colors_wind.cglab3.engine.graph

import net.colors_wind.cglab3.engine.ShaderProgram
import org.joml.Vector3f

open class GameItem(
    val meshes: Array<Mesh>,
    val position: Vector3f = Vector3f(0.0F, 0.0F, 0.0F),
    var scale: Vector3f = Vector3f(1.0F, 1.0F, 1.0F),
    val rotation : Vector3f = Vector3f(0.0F, 0.0F, 0.0F),
) {

    fun render(shaderProgram: ShaderProgram) {
        for (mesh in meshes) {
            mesh.render(shaderProgram)
        }
    }
}