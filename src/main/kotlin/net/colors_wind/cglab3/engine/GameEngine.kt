package net.colors_wind.cglab3.engine

import java.lang.Exception
import java.lang.Runnable
import kotlin.Throws
import java.lang.InterruptedException

open class GameEngine(windowTitle: String, width: Int, height: Int, vSync: Boolean, gameLogic: IGameLogic) : Runnable {
    private val window: Window
    private val timer: Timer
    private val gameLogic: IGameLogic
    private val mouseInput: MouseInput

    override fun run() {
        try {
            init()
            gameLoop()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            cleanup()
        }
    }

    @Throws(Exception::class)
    protected fun init() {
        window.init()
        timer.init()
        mouseInput.init(window)
        gameLogic.init(window)
    }

    private fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0F
        val interval = 1F / TARGET_UPS
        val running = true
        while (running && !window.isWindowShouldClose()) {
            elapsedTime = timer.getElapsedTime()
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            if (!window.vSync) {
                sync()
            }
        }
    }

    private fun cleanup() {
        gameLogic.cleanup()
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime = timer.getLastLoopTime() + loopSlot
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }
        }
    }

    protected fun input() {
        mouseInput.input(window)
        gameLogic.input(window, mouseInput)
    }

    protected fun update(interval: Float) {
        gameLogic.update(window, interval, mouseInput)
    }

    protected fun render() {
        gameLogic.render(window)
        window.update()
    }

    companion object {
        const val TARGET_FPS = 20
        const val TARGET_UPS = 10
    }

    init {
        window = Window(windowTitle, width, height, vSync)
        mouseInput = MouseInput()
        this.gameLogic = gameLogic
        timer = Timer()
    }
}