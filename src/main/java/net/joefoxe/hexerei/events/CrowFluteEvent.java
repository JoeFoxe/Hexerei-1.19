package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CrowFluteEvent {

    @SubscribeEvent
    public static void selectBlockPosition(PlayerInteractEvent.RightClickBlock event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof CrowFluteItem) {
            if (event.getItemStack().getOrCreateTag().getInt("commandMode") == 2)
                event.setUseBlock(Event.Result.DENY);
        }
    }
}
