package net.colors_wind.cglab3.ui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

public class UIConfig {
    public final Vector3f ka = new Vector3f();
    public final Vector3f kd = new Vector3f();
    public final Vector3f ks = new Vector3f();

    public final Vector3f translate = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f scale = new Vector3f();

    public final AtomicBoolean shouldUpdateGame = new AtomicBoolean(false);
    public final Object lock = new Object();


    public enum Scene {
        CACTUS, HOUSE
    }

    public enum ShadingMode {
        FLAD_SHADING, GOURAUD_SHADING, PHONG_SHADING
    }

    @NotNull public Scene scene = Scene.CACTUS;
    @NotNull public ShadingMode shadingMode = ShadingMode.PHONG_SHADING;

    @NotNull
    @Contract(pure = true)
    public static String vectorToString(@NotNull Vector3f vector3f) {
        return String.format("%.2f,%.2f,%.2f", vector3f.x, vector3f.y, vector3f.z);
    }

    @NotNull
    public static Vector3f parseStringVector(@NotNull String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        float x = Float.parseFloat(stringTokenizer.nextToken());
        float y = Float.parseFloat(stringTokenizer.nextToken());
        float z = Float.parseFloat(stringTokenizer.nextToken());
        return new Vector3f(x, y, z);
    }

}
