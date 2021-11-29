package net.colors_wind.cglab3

import org.lwjgl.assimp.AIScene
import org.lwjgl.assimp.Assimp
import java.io.File
import java.util.Arrays
import java.io.FilenameFilter
import java.util.Locale

class Model(path: String) {
    private val scene: List<AIScene>

    init {
        val suffix = ".obj"
        val resourceDir = File(path)
        val names = resourceDir.list { _: File?, name: String ->
            name.lowercase().endsWith(suffix, ignoreCase = true)
        }!!
        val prefix = names.first().substringBefore('_');
        names.sortBy {
            assert(it.startsWith(prefix))
            return@sortBy it.substring(prefix.length, it.length - suffix.length)
        }
        scene = names.map {
            Assimp.aiImportFile(File(resourceDir, it).absolutePath, Assimp.aiProcess_GenNormals)!!
        }
        println(scene[0].mMeshes()?.sizeof())
    }

}


fun main() {
    val model = Model("src/main/resources/modelSequence/cactus")

    val x = 0;
}