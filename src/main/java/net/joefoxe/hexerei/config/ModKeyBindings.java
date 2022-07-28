package net.joefoxe.hexerei.config;


import com.mojang.blaze3d.platform.InputConstants;
import com.sun.java.accessibility.util.Translator;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class ModKeyBindings {
    public static final KeyMapping broomDescend;
    public static final KeyMapping bookJEIShowUses;
    public static final KeyMapping bookJEIShowRecipe;
    public static final KeyMapping glassesZoom;
    private static final String broom_category = I18n.get("hexerei.key.category.broom");
    private static final String book_hovering_category = I18n.get("hexerei.key.category.book_hovering");

    private static final String glasses_category = I18n.get("hexerei.key.category.glasses");

    private static final List<KeyMapping> allBindings;

    static InputConstants.Key getKey(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key);
    }

    static {

        allBindings = List.of(
                broomDescend = new KeyMapping("key.hexerei.broomDescend", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_LEFT_SHIFT), broom_category),

                bookJEIShowUses = new KeyMapping("key.hexerei.book_hovering_uses", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_U), book_hovering_category),
                bookJEIShowRecipe = new KeyMapping("key.hexerei.book_hovering_recipe", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_R), book_hovering_category),

                glassesZoom = new KeyMapping("key.hexerei.glasses_zoom", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_Z), glasses_category)
        );

    }

    private ModKeyBindings() {
    }

    public static void init() {
        for (KeyMapping binding : allBindings) {
            ClientRegistry.registerKeyBinding(binding);
        }
    }

}

