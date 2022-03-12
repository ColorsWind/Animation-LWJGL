package net.colors_wind.cglab3.engine.loader

import net.colors_wind.cglab3.engine.graph.Texture
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object TextureCache {
    private val texturesMap: ConcurrentMap<String, Texture> = ConcurrentHashMap()
    @Throws(Exception::class)
    fun getTexture(path: String): Texture {
        return texturesMap.computeIfAbsent(path) {
            Texture(path)
        }
    }
}