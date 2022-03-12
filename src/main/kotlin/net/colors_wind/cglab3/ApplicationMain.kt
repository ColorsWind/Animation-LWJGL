package net.colors_wind.cglab3

import net.colors_wind.cglab3.engine.GameEngine
import net.colors_wind.cglab3.engine.IGameLogic
import net.colors_wind.cglab3.game.AnimationGame
import net.colors_wind.cglab3.ui.GameDesignUI
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    GameDesignUI.initSwings()
    try {
        val vSync = true
        val gameLogic: IGameLogic = AnimationGame()
        val gameEng = GameEngine("Initializing", 1200, 800, vSync, gameLogic)
        gameEng.run()
    } catch (exception: Exception) {
        exception.printStackTrace()
        exitProcess(-1)
    }
}