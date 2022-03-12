package net.colors_wind.cglab3.engine.loader


import net.colors_wind.cglab3.engine.graph.Material
import net.colors_wind.cglab3.engine.graph.Mesh

import org.lwjgl.assimp.Assimp
import java.io.File
import java.lang.Exception

object StaticSequenceLoader {


    @Throws(Exception::class)
    fun load(
        sequencePath: File,
        texturesDir: String? = "",
        flags: Int = Assimp.aiProcess_JoinIdenticalVertices or Assimp.aiProcess_Triangulate or Assimp.aiProcess_FixInfacingNormals or Assimp.aiProcess_GenNormals,
        suffix: String = ".obj",
        defaultMaterial: Material = Material()
    ): Array<Array<Mesh>> {
        if (!sequencePath.exists())
            throw RuntimeException("${sequencePath.absolutePath} not exist!")
        val fileNames = sequencePath.list { _: File, name: String ->
            name.lowercase().endsWith(suffix, ignoreCase = true)
        }!!
        val prefix = fileNames.first().substringBefore('_')
        fileNames.sortBy {
            assert(it.startsWith(prefix))
            return@sortBy it.substring(prefix.length + 1, it.length - suffix.length).toInt()
        }
        val meshesSequence = fileNames.map {
            StaticMeshesLoader.load(File(sequencePath, it).absolutePath, texturesDir, flags, defaultMaterial)
        }.toTypedArray()
        return meshesSequence
    }
}