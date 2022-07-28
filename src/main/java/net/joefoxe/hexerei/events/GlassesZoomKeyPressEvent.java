package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.item.custom.GlassesItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GlassesZoomKeyPressEvent {

    public boolean zoomToggled = false;
    public boolean zoomWithItemToggled = false;
    public boolean zoomWithKeyToggled = false;
    public float zoomTo = 0.6f;
    public float zoomAmount = 1f;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if(Minecraft.getInstance().screen == null) {
            if (event.getAction() == 1) {



                if (event.getKey() == ModKeyBindings.glassesZoom.getKey().getValue() && Hexerei.proxy.getPlayer() != null) {

                    Inventory inventory = Hexerei.proxy.getPlayer().inventory;
                    Item item = inventory.getArmor(3).getItem();
                    if(item instanceof GlassesItem){
                        zoomWithKeyToggled = !zoomWithKeyToggled;
                        if(zoomWithKeyToggled)
                            zoomAmount = Minecraft.getInstance().gameRenderer.fov;
                    }

                }
            }if (event.getAction() == 0) {
//                if (event.getKey() == ModKeyBindings.glassesZoom.getKey().getValue()) {
//
//                    zoomWithKeyToggled = false;
//
//                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onModifyFOV(FOVModifierEvent event){
        if(zoomWithKeyToggled){
            Inventory inventory = Hexerei.proxy.getPlayer().inventory;
            Item item = inventory.getArmor(3).getItem();
            if (!(item instanceof GlassesItem)) {
                zoomWithKeyToggled = false;
            }
        }
        zoomToggled = zoomWithItemToggled || zoomWithKeyToggled;
        if(zoomToggled) {
            event.setNewfov(zoomAmount);
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderLast(RenderLevelLastEvent event) {
        if(zoomToggled)
            zoomAmount = HexereiUtil.moveTo(zoomAmount, zoomTo, 0.02f);
    }

}
