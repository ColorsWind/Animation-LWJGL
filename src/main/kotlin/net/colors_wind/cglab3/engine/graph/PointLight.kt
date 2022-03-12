package net.colors_wind.cglab3.engine.graph

import org.joml.Vector3f

class PointLight(
    val color: Vector3f,
    val position: Vector3f,
    val intensity: Float,
    val attenuation: Attenuation = Attenuation(3.0F, 1.0F, 0.0F)
) {

    constructor(pointLight: PointLight) : this(
        Vector3f(pointLight.color),
        Vector3f(pointLight.position),
        pointLight.intensity,
        pointLight.attenuation
    )
    class Attenuation(var constant: Float, var linear: Float, var exponent: Float)


}