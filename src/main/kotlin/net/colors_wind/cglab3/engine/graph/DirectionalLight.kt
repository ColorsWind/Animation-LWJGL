package net.colors_wind.cglab3.engine.graph

import org.joml.Vector3f

class DirectionalLight(var color: Vector3f, var direction: Vector3f, var intensity: Float) {

    constructor(light: DirectionalLight) : this(Vector3f(light.color), Vector3f(light.direction), light.intensity) {}
}