package net.joefoxe.hexerei.light;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

/**
 * Represents a dynamic light source.
 * This is a straight implementation from <a href="https://github.com/LambdAurora/LambDynamicLights">LambDynamicLights</a>, a super awesome Fabric mod!
 *
 * @author LambdAurora
 * @version 1.3.3
 * @since 1.0.0
 */
public interface LambHexereiDynamicLight {
    /**
     * Returns the dynamic light source X coordinate.
     *
     * @return the X coordinate
     */
    double getDynamicLightXH();

    /**
     * Returns the dynamic light source Y coordinate.
     *
     * @return the Y coordinate
     */
    double getDynamicLightYH();

    /**
     * Returns the dynamic light source Z coordinate.
     *
     * @return the Z coordinate
     */
    double getDynamicLightZH();

    /**
     * Returns the dynamic light source world.
     *
     * @return the world instance
     */
    Level getDynamicLightWorldH();

    /**
     * Returns whether the dynamic light is enabled or not.
     *
     * @return {@code true} if the dynamic light is enabled, else {@code false}
     */
    default boolean isDynamicLightEnabledH() {
        return LightManager.containsLightSource(this);
    }

    void resetDynamicLightH();

    default void setHexereiDynamicLightEnabled(boolean enabled) {
        this.resetDynamicLightH();
        if (enabled)
            LightManager.addLightSource(this);
        else
            LightManager.removeLightSource(this);
    }

    /**
     * Returns the luminance of the light source.
     * The maximum is 15, below 1 values are ignored.
     *
     * @return the luminance of the light source
     */
    int getLuminanceH();

    /**
     * Executed at each tick.
     */
    void dynamicLightTickH();

    /**
     * Returns whether this dynamic light source should update.
     *
     * @return {@code true} if this dynamic light source should update, else {@code false}
     */
    boolean shouldUpdateDynamicLightH();

    boolean lambdynlights$updateDynamicLightH(LevelRenderer renderer);

    void lambdynlights$scheduleTrackedChunksRebuildH(LevelRenderer renderer);
}