package net.colors_wind.cglab3.engine.graph

import net.colors_wind.cglab3.engine.ShaderProgram
import net.colors_wind.cglab3.engine.Utils.sendFloatBuffer
import net.colors_wind.cglab3.engine.Utils.sendIntBuffer
import org.lwjgl.opengl.GL33.*


class Mesh(
    val positions: FloatArray,
    val normals: FloatArray,
    val texcoords: FloatArray,
    val indices: IntArray,
    var material: Material,
) {

    private val vaoId : Int = glGenVertexArrays()
    private val positionVboId : Int = glGenBuffers()
    private val texCoordId : Int = glGenBuffers()
    private val normalVboId : Int = glGenBuffers()
    private val eboId : Int = glGenBuffers()
    private val vertexCount : Int = indices.size

    init {
        // Bind VAO
        glBindVertexArray(vaoId)

        // Position VBO
        glBindBuffer(GL_ARRAY_BUFFER, positionVboId)
        sendFloatBuffer(positions)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        // TexCoord VBO
        glBindBuffer(GL_ARRAY_BUFFER, texCoordId )
        sendFloatBuffer(texcoords)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

        // Normals VBO
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId)
        sendFloatBuffer(normals)
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0)

        // EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        sendIntBuffer(indices)

        // Reset
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }


    fun cleanUp() {
        // Delete the VBOs and EBO
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glDeleteBuffers(positionVboId)
        glDeleteBuffers(texCoordId)
        glDeleteBuffers(normalVboId)

        glDeleteBuffers(eboId)

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }


    fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("material", material)
        val texture = material.texture
        if (texture != null) {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0)
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.id)
        }

        // Draw the mesh
        glBindVertexArray(vaoId)

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0)
    }

}