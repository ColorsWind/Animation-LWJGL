package net.colors_wind.cglab3.engine

import net.colors_wind.cglab3.engine.graph.DirectionalLight
import net.colors_wind.cglab3.engine.graph.Material
import net.colors_wind.cglab3.engine.graph.PointLight
import net.colors_wind.cglab3.engine.graph.SpotLight
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack

class ShaderProgram {
    private val programId: Int = GL20.glCreateProgram()
    private var vertexShaderId = 0
    private var fragmentShaderId = 0
    private val uniforms: MutableMap<String, Int>
    @Throws(Exception::class)
    fun createUniform(uniformName: String) {
        val uniformLocation = GL20.glGetUniformLocation(programId, uniformName)
        if (uniformLocation < 0) {
            throw Exception("Could not find uniform:$uniformName")
        }
        uniforms[uniformName] = uniformLocation
    }

    @Throws(Exception::class)
    fun createPointLightListUniform(uniformName: String, size: Int) {
        for (i in 0 until size) {
            createPointLightUniform("$uniformName[$i]")
        }
    }

    @Throws(Exception::class)
    fun createPointLightUniform(uniformName: String) {
        createUniform("$uniformName.colour")
        createUniform("$uniformName.position")
        createUniform("$uniformName.intensity")
        createUniform("$uniformName.att.constant")
        createUniform("$uniformName.att.linear")
        createUniform("$uniformName.att.exponent")
    }

    @Throws(Exception::class)
    fun createSpotLightListUniform(uniformName: String, size: Int) {
        for (i in 0 until size) {
            createSpotLightUniform("$uniformName[$i]")
        }
    }

    @Throws(Exception::class)
    fun createSpotLightUniform(uniformName: String) {
        createPointLightUniform("$uniformName.pl")
        createUniform("$uniformName.conedir")
        createUniform("$uniformName.cutoff")
    }

    @Throws(Exception::class)
    fun createDirectionalLightUniform(uniformName: String) {
        createUniform("$uniformName.colour")
        createUniform("$uniformName.direction")
        createUniform("$uniformName.intensity")
    }

    @Throws(Exception::class)
    fun createMaterialUniform(uniformName: String) {
        createUniform("$uniformName.ambient")
        createUniform("$uniformName.diffuse")
        createUniform("$uniformName.specular")
        createUniform("$uniformName.hasTexture")
        createUniform("$uniformName.reflectance")
    }

    fun setUniform(uniformName: String, value: Matrix4f) {
        MemoryStack.stackPush().use { stack ->
            // Dump the matrix into a float buffer
            val fb = stack.mallocFloat(16)
            value[fb]
            GL20.glUniformMatrix4fv(uniforms[uniformName]!!, false, fb)
        }
    }

    fun setUniform(uniformName: String, value: Int) {
        GL20.glUniform1i(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Float) {
        GL20.glUniform1f(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Vector3f) {
        GL20.glUniform3f(uniforms[uniformName]!!, value.x, value.y, value.z)
    }

    fun setUniform(uniformName: String, value: Vector4f) {
        GL20.glUniform4f(uniforms[uniformName]!!, value.x, value.y, value.z, value.w)
    }

    fun setUniform(uniformName: String, pointLights: Array<PointLight>) {
        val numLights = pointLights.size
        for (i in 0 until numLights) {
            setUniform(uniformName, pointLights[i], i)
        }
    }

    fun setUniform(uniformName: String, pointLight: PointLight, pos: Int) {
        setUniform("$uniformName[$pos]", pointLight)
    }

    fun setUniform(uniformName: String, pointLight: PointLight) {
        setUniform("$uniformName.colour", pointLight.color)
        setUniform("$uniformName.position", pointLight.position)
        setUniform("$uniformName.intensity", pointLight.intensity)
        val att: PointLight.Attenuation = pointLight.attenuation
        setUniform("$uniformName.att.constant", att.constant)
        setUniform("$uniformName.att.linear", att.linear)
        setUniform("$uniformName.att.exponent", att.exponent)
    }

    fun setUniform(uniformName: String, spotLights: Array<SpotLight>) {
        val numLights = spotLights.size
        for (i in 0 until numLights) {
            setUniform(uniformName, spotLights[i], i)
        }
    }

    fun setUniform(uniformName: String, spotLight: SpotLight, pos: Int) {
        setUniform("$uniformName[$pos]", spotLight)
    }

    fun setUniform(uniformName: String, spotLight: SpotLight) {
        setUniform("$uniformName.pl", spotLight.pointLight)
        setUniform("$uniformName.conedir", spotLight.coneDirection)
        setUniform("$uniformName.cutoff", spotLight.cutOffAngle)
    }

    fun setUniform(uniformName: String, dirLight: DirectionalLight) {
        setUniform("$uniformName.colour", dirLight.color)
        setUniform("$uniformName.direction", dirLight.direction)
        setUniform("$uniformName.intensity", dirLight.intensity)
    }

    fun setUniform(uniformName: String, material: Material) {
        setUniform("$uniformName.ambient", material.ambientColour)
        setUniform("$uniformName.diffuse", material.diffuseColour)
        setUniform("$uniformName.specular", material.specularColour)
        setUniform("$uniformName.hasTexture", if (material.isTextured) 1 else 0)
        setUniform("$uniformName.reflectance", material.reflectance)
    }



    @Throws(Exception::class)
    fun createVertexShader(shaderCode: String) {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER)
    }

    @Throws(Exception::class)
    fun createFragmentShader(shaderCode: String) {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER)
    }

    @Throws(Exception::class)
    protected fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = GL20.glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }
        GL20.glShaderSource(shaderId, shaderCode)
        GL20.glCompileShader(shaderId)
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024))
        }
        GL20.glAttachShader(programId, shaderId)
        return shaderId
    }

    @Throws(Exception::class)
    fun link() {
        GL20.glLinkProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024))
        }
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId)
        }
        GL20.glValidateProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024))
        }
    }

    fun bind() {
        GL20.glUseProgram(programId)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            GL20.glDeleteProgram(programId)
        }
    }

    init {
        if (programId == 0) {
            throw Exception("Could not create Shader")
        }
        uniforms = HashMap()
    }
}