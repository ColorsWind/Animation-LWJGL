package net.colors_wind.cglab3.game.scene

import net.colors_wind.cglab3.engine.graph.*
import net.colors_wind.cglab3.ui.UIConfig
import org.joml.Vector3f
import org.joml.Vector4f

interface IScene {
    val animateSequence: List<GameItem>
    val background: GameItem

    fun getGameItems(tick: Int): Array<GameItem> {
        return arrayOf(background, animateSequence[tick % animateSequence.size])
    }

    fun updateScene(uiConfig: UIConfig) {
        for (gameItem in animateSequence) {
            gameItem.position.set(uiConfig.translate)
            gameItem.rotation.set(uiConfig.rotation)
            gameItem.scale.set(uiConfig.scale)
            for (mesh in gameItem.meshes) {
                mesh.material.ambientColour.set(uiConfig.ka, 1.0F)
                mesh.material.diffuseColour.set(uiConfig.kd, 1.0F)
                mesh.material.specularColour.set(uiConfig.ks, 1.0F)
            }
        }
    }

    fun updateConfig(uiConfig: UIConfig) {
        val gameItem = animateSequence.first()
        val material = gameItem.meshes.first().material
        fun Vector3f.set(vector4f: Vector4f) {
            this.x = vector4f.x
            this.y = vector4f.y
            this.z = vector4f.z
        }
        uiConfig.ka.set(material.ambientColour)
        uiConfig.kd.set(material.diffuseColour)
        uiConfig.ks.set(material.specularColour)
        uiConfig.translate.set(gameItem.position)
        uiConfig.rotation.set(gameItem.rotation)
        uiConfig.scale.set(gameItem.scale)
    }

    fun cleanup() {
        for (gameItem in animateSequence) {
            for (mesh in gameItem.meshes) {
                mesh.cleanUp()
            }
        }
        for (mesh in background.meshes) {
            mesh.cleanUp()
        }
    }

    val configScene : UIConfig.Scene

    val directionalLight : DirectionalLight

    val pointLightList : Array<PointLight>

    val spotLightList : Array<SpotLight>

    val ambient : Vector3f



}