package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.BookReloadListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerProxy implements SidedProxy {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public void init() {
        //
    }

    @Override
    public void openCodexGui() {
        //
    }

    @SubscribeEvent
    public static void onAddReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(new BookReloadListener());
    }
}