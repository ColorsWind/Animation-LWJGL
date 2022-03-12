package net.colors_wind.cglab3.engine.graph

import org.joml.Vector3f

class SpotLight(var pointLight: PointLight, var coneDirection: Vector3f, var cutOffAngle: Float) {

    constructor(spotLight: SpotLight) : this(
        PointLight(spotLight.pointLight),
        Vector3f(spotLight.coneDirection), 0f
    ) {
        cutOffAngle = spotLight.cutOffAngle
    }



}