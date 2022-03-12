package net.colors_wind.cglab3.engine.loader

import net.colors_wind.cglab3.engine.graph.Material
import net.colors_wind.cglab3.engine.graph.Mesh
import net.colors_wind.cglab3.engine.graph.Texture
import org.joml.Vector4f
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import java.io.File
import java.nio.IntBuffer


object StaticMeshesLoader {
    @Throws(Exception::class)
    fun load(
        resourcePath: String,
        texturesDir: String? = File(resourcePath).parent,
        flags: Int = aiProcess_JoinIdenticalVertices or aiProcess_Triangulate or aiProcess_FixInfacingNormals or aiProcess_GenNormals,
        defaultMaterial: Material = Material()
    ): Array<Mesh> {
        val aiScene = aiImportFile(resourcePath, flags) ?: throw Exception("Error loading model")
        val numMaterials = aiScene.mNumMaterials()
        val aiMaterials = aiScene.mMaterials()!!
        val materials = arrayListOf<Material>()
        for (i in 0 until numMaterials) {
            val aiMaterial = AIMaterial.create(aiMaterials[i])
            processMaterial(aiMaterial, materials, texturesDir, defaultMaterial)
        }
        val numMeshes = aiScene.mNumMeshes()
        val aiMeshes = aiScene.mMeshes()!!
        val meshes = (0 until numMeshes).map {  i ->
            val aiMesh = AIMesh.create(aiMeshes[i])
            return@map processMesh(aiMesh, materials)
        }.toTypedArray()
        return meshes
    }


    private fun processTextCoords(aiMesh: AIMesh, textures: MutableList<Float>) {
        val textCoords = aiMesh.mTextureCoords(0)
        val numTextCoords = textCoords?.remaining() ?: 0
        for (i in 0 until numTextCoords) {
            val textCoord = textCoords!!.get()
            textures.add(textCoord.x())
            textures.add(1 - textCoord.y())
        }
    }

    private fun processMesh(aiMesh: AIMesh, materials: List<Material>): Mesh {
        val vertices = arrayListOf<Float>()
        val textures= arrayListOf<Float>()
        val normals= arrayListOf<Float>()
        val indices= arrayListOf<Int>()

        processVertices(aiMesh, vertices)
        processNormals(aiMesh, normals)
        processTextCoords(aiMesh, textures)
        processIndices(aiMesh, indices)

        val material: Material
        val materialIdx = aiMesh.mMaterialIndex()
        material = if (materialIdx >= 0 && materialIdx < materials.size) {
            materials[materialIdx]
        } else {
            Material()
        }
        if(textures.isEmpty()) {
            var i = 0
            while (i < vertices.size) {
                textures.add(vertices[i])
                textures.add(vertices[i+2])
                i += 3
            }
        }
        val mesh = Mesh(
            vertices.toFloatArray(),
            normals.toFloatArray(),
            textures.toFloatArray(),
            indices.toIntArray(),
            material,
        )
        return mesh
    }

    @Throws(Exception::class)
    private fun processMaterial(
        aiMaterial: AIMaterial, materials: MutableList<Material>,
        texturesDir: String?,
        defaultMaterial: Material
    ) {
        val colour = AIColor4D.create()
        val path = AIString.calloc()
        aiGetMaterialTexture(
            aiMaterial, aiTextureType_AMBIENT, 0, path, null as IntBuffer?,
            null, null, null, null, null
        )
        val textPath = path.dataString()
        var texture: Texture? = null
        if (texturesDir != null && textPath.isNotEmpty()) {
            texture = TextureCache.getTexture(File(File(texturesDir), textPath).absolutePath)
        }
        var ambient: Vector4f = defaultMaterial.ambientColour
        var result: Int = aiGetMaterialColor(
            aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0,
            colour
        )
        if (result == 0) {
            ambient = Vector4f(colour.r(), colour.g(), colour.b(), colour.a())
        }
        var diffuse: Vector4f = defaultMaterial.diffuseColour
        result = aiGetMaterialColor(
            aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
            colour
        )
        if (result == 0) {
            diffuse = Vector4f(colour.r(), colour.g(), colour.b(), colour.a())
        }
        var specular: Vector4f = defaultMaterial.specularColour
        result = aiGetMaterialColor(
            aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0,
            colour
        )
        if (result == 0) {
            specular = Vector4f(colour.r(), colour.g(), colour.b(), colour.a())
        }
        val material = Material(ambient, diffuse, specular, 1.0f)
        material.texture = texture
        materials.add(material)
    }



    private fun processVertices(aiMesh: AIMesh, vertices: MutableList<Float>) {
        val aiVertices = aiMesh.mVertices()
        while (aiVertices.remaining() > 0) {
            val aiVertex = aiVertices.get()
            vertices.add(aiVertex.x())
            vertices.add(aiVertex.y())
            vertices.add(aiVertex.z())
        }
    }

    private fun processNormals(aiMesh: AIMesh, normals: MutableList<Float>) {
        val aiNormals = aiMesh.mNormals()
        while (aiNormals != null && aiNormals.remaining() > 0) {
            val aiNormal = aiNormals.get()
            normals.add(aiNormal.x())
            normals.add(aiNormal.y())
            normals.add(aiNormal.z())
        }
    }


    private fun processIndices(aiMesh: AIMesh, indices: MutableList<Int>) {
        val numFaces = aiMesh.mNumFaces()
        val aiFaces = aiMesh.mFaces()
        for (i in 0 until numFaces) {
            val aiFace = aiFaces[i]
            val buffer = aiFace.mIndices()
            while (buffer.remaining() > 0) {
                indices.add(buffer.get())
            }
        }
    }
}