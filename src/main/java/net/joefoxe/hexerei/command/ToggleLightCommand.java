package net.joefoxe.hexerei.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ToggleDynamicLightPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ToggleLightCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hexerei-dynamic-light").
                requires(sender -> sender.hasPermission(0))
                .then(Commands.literal("on").executes(context -> resetPlayers(context.getSource(), true)))
                .then(Commands.literal("off").executes(context -> resetPlayers(context.getSource(), false))));
    }

    private static int resetPlayers(CommandSourceStack source, boolean enable) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 1;
        }
        HexereiPacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> player), new ToggleDynamicLightPacket(enable));
        String path = enable ? "hexerei.dynamic_light_on" : "hexerei.dynamic_light_off";
        player.sendSystemMessage(Component.translatable(path, enable));
        return 1;
    }
}