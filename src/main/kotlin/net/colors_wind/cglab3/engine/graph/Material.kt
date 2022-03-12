package net.colors_wind.cglab3.engine.graph


import org.joml.Vector4f

class Material(
    var ambientColour: Vector4f = DEFAULT_COLOUR,
    var diffuseColour: Vector4f = DEFAULT_COLOUR,
    var specularColour: Vector4f = DEFAULT_COLOUR,
    var reflectance: Float= 0.0F,
    var texture: Texture? = null,
) {

    val isTextured: Boolean
        get() = texture != null

    companion object {
        val DEFAULT_COLOUR = Vector4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
}