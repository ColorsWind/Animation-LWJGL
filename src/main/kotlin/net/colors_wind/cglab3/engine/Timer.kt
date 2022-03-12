package net.colors_wind.cglab3.engine

class Timer {

    private var lastLoopTime: Double = 0.0

    fun init() {
        lastLoopTime = getTime()
    }

    fun getElapsedTime(): Float {
        val time: Double = getTime()
        val elapsedTime = (time - lastLoopTime).toFloat()
        lastLoopTime = time
        return elapsedTime
    }

    fun getTime(): Double = System.nanoTime() / 1000_000_000.0

    fun getLastLoopTime() : Double = lastLoopTime
}