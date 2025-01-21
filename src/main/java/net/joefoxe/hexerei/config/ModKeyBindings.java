package net.joefoxe.hexerei.config;


import com.mojang.blaze3d.platform.InputConstants;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;


@EventBusSubscriber(modid = Hexerei.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public final class ModKeyBindings {
    public static final KeyMapping broomDown;
    public static final KeyMapping broomDismount;
    public static final KeyMapping broomUp;
    public static final KeyMapping broomActivate;
    public static final KeyMapping bookJEIShowUses;
    public static final KeyMapping bookJEIShowRecipe;
    public static final KeyMapping glassesZoom;
    private static final String broom_category = "hexerei.key.category.broom";
    private static final String book_hovering_category = "hexerei.key.category.book_hovering";

    private static final String glasses_category = "hexerei.key.category.glasses";

    private static final List<KeyMapping> allBindings;

    static InputConstants.Key getKey(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key);
    }

    static {

        allBindings = List.of(
                broomDown = new KeyMapping("key.hexerei.broomDown", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_LEFT_CONTROL), broom_category),
                broomDismount = new KeyMapping("key.hexerei.broomDismount", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_LEFT_SHIFT), broom_category),
                broomUp = new KeyMapping("key.hexerei.broomUp", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_SPACE), broom_category),
                broomActivate = new KeyMapping("key.hexerei.broomActivate", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_G), broom_category),

                bookJEIShowUses = new KeyMapping("key.hexerei.book_hovering_uses", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_U), book_hovering_category),
                bookJEIShowRecipe = new KeyMapping("key.hexerei.book_hovering_recipe", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_R), book_hovering_category),

                glassesZoom = new KeyMapping("key.hexerei.glasses_zoom", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_Z), glasses_category)
        );

    }

    private ModKeyBindings() {
    }
    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent ev)
    {
        for (KeyMapping binding : allBindings) {
            ev.register(binding);
        }
    }

}

