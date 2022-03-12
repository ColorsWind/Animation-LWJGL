package net.colors_wind.cglab3.game.scene

import net.colors_wind.cglab3.engine.graph.*
import net.colors_wind.cglab3.engine.loader.StaticMeshesLoader
import net.colors_wind.cglab3.engine.loader.StaticSequenceLoader
import net.colors_wind.cglab3.ui.UIConfig
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.assimp.Assimp
import java.io.File
import kotlin.math.cos

class CactusScene : IScene {

    private val defaultMaterial = Material(
        ambientColour = Vector4f(119F / 255, 153F / 255, 17F / 255, 1.0F),
        diffuseColour = Vector4f(119F / 255, 153F / 255, 17F / 255, 1.0F),
        specularColour = Vector4f(228F / 255, 250F / 255, 182F / 255, 1.0F)
    )
    override val animateSequence: List<GameItem> =
        StaticSequenceLoader.load(File("src/main/resources/models/cactus/sequence"), defaultMaterial = defaultMaterial)
            .map { GameItem(it, rotation = Vector3f(90.0F, 0.0F, 0.0F), position = Vector3f(0.0F, 0.1F, 0.0F)) }
            .onEach { gameItem -> gameItem.meshes.forEach { mesh -> mesh.material = defaultMaterial } }
    override val background: GameItem = GameItem(
        StaticMeshesLoader.load(
            "src/main/resources/models/cactus/desert.obj",
            flags = Assimp.aiProcess_JoinIdenticalVertices or Assimp.aiProcess_Triangulate,
        )

    )

    override val configScene = UIConfig.Scene.CACTUS

    override fun getGameItems(tick: Int): Array<GameItem> {
        return arrayOf(background, animateSequence[tick % animateSequence.size])
    }


    override val directionalLight = DirectionalLight(
        color = Vector3f(0.2F, 0.3F, 0.25F),
        direction = Vector3f(1.0F, -1.0F, 0.0F),
        intensity = 0.5F
    )



    override val pointLightList = arrayOf(
        PointLight(
            color = Vector3f(1.0F, 1.0F, 1.0F),
            position = Vector3f(2.0F, 0.5F, 3.0F),
            intensity = 0.5F,
            attenuation = PointLight.Attenuation(1.0F, 1.0F, 0.2F)
        ),
    )

    override val spotLightList = arrayOf(
        SpotLight(
            pointLight = PointLight(
                color = Vector3f(1.0F, 1.0F, 1.0F),
                position = Vector3f(0.0F, 2.0F, 0.0F),
                intensity = 1.0F,
                attenuation = PointLight.Attenuation(1.0F, 0.1F, 0.2F)
            ),
            coneDirection = Vector3f(-1.0F, -1.0F, 1.0F),
            cutOffAngle = cos(Math.toRadians(120.0)).toFloat(),
        )
    )

    override val ambient = Vector3f(0.3F, 0.3F, 0.3F)


}