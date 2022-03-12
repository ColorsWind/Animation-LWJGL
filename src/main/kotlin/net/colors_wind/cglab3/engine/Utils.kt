package net.colors_wind.cglab3.engine

import org.lwjgl.BufferUtils.createByteBuffer
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryUtil
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object Utils {
    @Throws(Exception::class)
    fun loadResource(fileName: String): String {
        val ins = Utils.javaClass.getResourceAsStream(fileName)!!
        val code = loadInputStream(ins)
        ins.close()
        return code
    }

    @Throws(Exception::class)
    fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
        var buffer: ByteBuffer
        val path: Path = Paths.get(resource)
        if (Files.isReadable(path)) {
            Files.newByteChannel(path).use { fc ->
                buffer = createByteBuffer(fc.size().toInt() + 1)
                while (fc.read(buffer) != -1);
            }
        } else {
            Utils::class.java.getResourceAsStream(resource).use { source ->
                Channels.newChannel(source!!).use { rbc ->
                    buffer = createByteBuffer(bufferSize)
                    while (true) {
                        val bytes: Int = rbc.read(buffer)
                        if (bytes == -1) {
                            break
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                        }
                    }
                }
            }
        }

        buffer.flip()
        return buffer
    }

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }


    @Throws(Exception::class)
    fun loadFile(filePath: String): String {
        val ins = FileInputStream(filePath)
        val code = loadInputStream(ins)
        ins.close()
        return code
    }

    @Throws(IOException::class)
    fun loadInputStream(inputStream: InputStream): String {
        val reader = InputStreamReader(inputStream, "utf-8")
        val code = reader.readText()
        reader.close()
        return code
    }

    fun sendFloatBuffer(data: FloatArray) {
        var buffer : FloatBuffer? = null
        try {
            buffer = MemoryUtil.memAllocFloat(data.size)
            buffer.put(data).flip()
            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            MemoryUtil.memFree(buffer)
        }
    }

    fun sendIntBuffer(data: IntArray) {
        var buffer : IntBuffer? = null
        try {
            buffer = MemoryUtil.memAllocInt(data.size)
            buffer.put(data).flip()
            GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            MemoryUtil.memFree(buffer)
        }
    }

}