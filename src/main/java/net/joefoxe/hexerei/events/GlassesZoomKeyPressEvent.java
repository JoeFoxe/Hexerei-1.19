package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.compat.CurioCompat;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.item.custom.GlassesItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GlassesZoomKeyPressEvent {

    public boolean zoomToggled = false;
    public boolean zoomWithItemToggled = false;
    public boolean zoomWithKeyToggled = false;
    public float zoomTo = 0.6f;
    public float zoomAmount = 1f;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyEvent(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (event.getAction() == 1) {

                if (event.getKey() == ModKeyBindings.glassesZoom.getKey().getValue() && Hexerei.proxy.getPlayer() != null) {

                    Player player = Hexerei.proxy.getPlayer();
                    if (player == null) return;
                    boolean curioFlag = false;
                    if (Hexerei.curiosLoaded) {
                        curioFlag = CurioCompat.hasGlasses(player);
                    }
                    if (player.inventory.getArmor(3).getItem() instanceof GlassesItem || curioFlag) {
                        zoomWithKeyToggled = !zoomWithKeyToggled;
                        if (zoomWithKeyToggled)
                            zoomAmount = Minecraft.getInstance().gameRenderer.fov;
                    }

                }
            }
            /*
            if (event.getAction() == 0) {
                if (event.getKey() == ModKeyBindings.glassesZoom.getKey().getValue()) {

                    zoomWithKeyToggled = false;

                }
            }*/
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onModifyFOV(ComputeFovModifierEvent event) {
        if (zoomWithKeyToggled) {
            Player player = Hexerei.proxy.getPlayer();
            if (player == null) return;
            Item item = player.inventory.getArmor(3).getItem();
            boolean curioFlag = false;
            if (Hexerei.curiosLoaded) {
                curioFlag = CurioCompat.hasGlasses(player);
            }
            if (!(item instanceof GlassesItem || curioFlag)) {
                zoomWithKeyToggled = false;
            }
        }
        zoomToggled = zoomWithItemToggled || zoomWithKeyToggled;
        if (zoomToggled) {
            event.setNewFovModifier(zoomAmount);
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderLast(RenderLevelStageEvent event) {
        if (zoomToggled)
            zoomAmount = HexereiUtil.moveTo(zoomAmount, zoomTo, 0.02f);
    }

}
