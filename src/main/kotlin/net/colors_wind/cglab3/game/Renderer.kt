package org.lwjglb.game

import net.colors_wind.cglab3.engine.*
import net.colors_wind.cglab3.engine.graph.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL33.*

class Renderer {
    private val transformation: Transformation = Transformation()
    private lateinit var shaderProgram: ShaderProgram
    private val specularPower: Float = 10.0F
    @Throws(Exception::class)
    fun init(window: Window) {
        // Create shader
        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("texture_sampler")
        // Create uniform for material
        shaderProgram.createMaterialUniform("material")
        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower")
        shaderProgram.createUniform("ambientLight")
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS)
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS)
        shaderProgram.createDirectionalLightUniform("directionalLight")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(
        window: Window, camera: Camera, gameItems: Array<GameItem>, ambientLight: Vector3f,
        pointLightList: Array<PointLight>, spotLightList: Array<SpotLight>, directionalLight: DirectionalLight,
    ) {
        clear()
        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        shaderProgram.bind()

        // Update projection Matrix
        val projectionMatrix =
            transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)

        // Update view Matrix
        val viewMatrix = transformation.getViewMatrix(camera)

        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight)
        shaderProgram.setUniform("texture_sampler", 0)
        // Render each gameItem
        for (gameItem in gameItems) {
            val modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix)
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
            gameItem.render(shaderProgram)
        }
        shaderProgram.unbind()
    }

    private fun renderLights(
        viewMatrix: Matrix4f, ambientLight: Vector3f,
        pointLightList: Array<PointLight>, spotLightList: Array<SpotLight>, directionalLight: DirectionalLight
    ) {
        shaderProgram.setUniform("ambientLight", ambientLight)
        shaderProgram.setUniform("specularPower", specularPower)

        // Process Point Lights
        var numLights = pointLightList.size
        for (i in 0 until numLights) {
            // Get a copy of the point light object and transform its position to view coordinates
            val currPointLight = PointLight(pointLightList[i])
            val lightPos: Vector3f = currPointLight.position
            val aux = Vector4f(lightPos, 1.0F)
            aux.mul(viewMatrix)
            lightPos.x = aux.x
            lightPos.y = aux.y
            lightPos.z = aux.z
            shaderProgram.setUniform("pointLights", currPointLight, i)
        }

        // Process Spot Ligths
        numLights = spotLightList.size
        for (i in 0 until numLights) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            val currSpotLight = SpotLight(spotLightList[i])
            val dir = Vector4f(currSpotLight.coneDirection, 0.0F)
            dir.mul(viewMatrix)
            currSpotLight.coneDirection = Vector3f(dir.x, dir.y, dir.z)
            val lightPos: Vector3f = currSpotLight.pointLight.position
            val aux = Vector4f(lightPos, 1.0F)
            aux.mul(viewMatrix)
            lightPos.x = aux.x
            lightPos.y = aux.y
            lightPos.z = aux.z
            shaderProgram.setUniform("spotLights", currSpotLight, i)
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        val currDirLight = DirectionalLight(directionalLight)
        val dir = Vector4f(currDirLight.direction, 0.0F)
        dir.mul(viewMatrix)
        currDirLight.direction = Vector3f(dir.x, dir.y, dir.z)
        shaderProgram.setUniform("directionalLight", currDirLight)
    }

    fun cleanup() {
        if (shaderProgram != null) {
            shaderProgram!!.cleanup()
        }
    }

    companion object {
        /**
         * Field of View in Radians
         */
        private val FOV = Math.toRadians(60.0).toFloat()
        private const val Z_NEAR = 0.01f
        private const val Z_FAR = 1000f
        private const val MAX_POINT_LIGHTS = 5
        private const val MAX_SPOT_LIGHTS = 5
    }

}