package net.colors_wind.cglab3.game

import net.colors_wind.cglab3.engine.Camera
import net.colors_wind.cglab3.engine.IGameLogic
import net.colors_wind.cglab3.engine.MouseInput
import net.colors_wind.cglab3.engine.Window
import net.colors_wind.cglab3.game.scene.CactusScene
import net.colors_wind.cglab3.game.scene.HorsesScene
import net.colors_wind.cglab3.game.scene.IScene
import net.colors_wind.cglab3.ui.GameDesignUI
import net.colors_wind.cglab3.ui.UIConfig
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL32.*
import org.lwjglb.game.Renderer


class AnimationGame : IGameLogic {
    private lateinit var scene: IScene
    private val renderer = Renderer()
    private val camera = Camera()
    private val cameraInc = Vector3f(0.0F, 0.0F, 0.0F)
    private val uiConfig = UIConfig()
    private val gameDesignUI = GameDesignUI(uiConfig)
    private var tick : Int = -1

    override fun init(window: Window) {
        renderer.init(window)
        scene =  CactusScene()
        window.setKeyCallback{ _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                window.setCaughtCursor(false)
            }
        }
        window.setMouseButtonCallback{_, key, action, _ ->
            if (key == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                window.setCaughtCursor(!window.isCaughtCursor())
            }
        }
        gameDesignUI.init()
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        cameraInc.set(0.0, 0.0, 0.0)
        if (window.isCaughtCursor()) {
            camera.moveCameraDirection(mouseInput.displayVec.mul(0.1F))
            if (window.isKeyPressed(GLFW_KEY_W)) {
                cameraInc.z = 1.0F
            }
            if (window.isKeyPressed(GLFW_KEY_S)) {
                cameraInc.z = -1.0F
            }
            if (window.isKeyPressed(GLFW_KEY_A)) {
                cameraInc.x = -1.0F
            }
            if (window.isKeyPressed(GLFW_KEY_D)) {
                cameraInc.x = 1.0F
            }
            if (window.isKeyPressed(GLFW_KEY_SPACE)) {
                cameraInc.y = 1.0F
            }
            if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
                cameraInc.y = -1.0F
            }
        }
        if (window.isKeyPressed(GLFW_KEY_M)) {
            synchronized(uiConfig.lock) {
                scene.updateConfig(uiConfig)
            }
            gameDesignUI.updateAndShowUI(uiConfig)
        }
        camera.moveCameraPosition(cameraInc)
        camera.updateViewCoordinate(window)
    }

    override fun update(window: Window, interval: Float, mouseInput: MouseInput) {
        if (uiConfig.shouldUpdateGame.getAndSet(false)) {
            synchronized(uiConfig.lock) {
                if (uiConfig.scene != scene.configScene) {
                    scene.cleanup()
                    scene = when(uiConfig.scene) {
                        UIConfig.Scene.CACTUS -> CactusScene()
                        UIConfig.Scene.HOUSE -> HorsesScene()
                    }
                } else {
                    scene.updateScene(uiConfig)
                }
//                when(uiConfig.shadingMode) {
//                    UIConfig.ShadingMode.FLAD_SHADING -> glShadeModel(GL_FLAT)
//                    else -> glShadeModel(GL_SMOOTH)
//                }
            }
            println("Update scene completed.")
        }
        tick++
    }

    override fun render(window: Window) {
        window.setClearColor(0.2F, 0.3F, 0.3F, 1.0F)
        renderer.render(window, camera, scene.getGameItems(tick), scene.ambient, scene.pointLightList, scene.spotLightList, scene.directionalLight)
    }

    override fun cleanup() {
        renderer.cleanup()
    }



}