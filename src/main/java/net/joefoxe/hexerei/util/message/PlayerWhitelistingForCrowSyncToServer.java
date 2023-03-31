package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerWhitelistingForCrowSyncToServer {
    boolean whitelisting;

    public PlayerWhitelistingForCrowSyncToServer(boolean whitelisting) {
        this.whitelisting = whitelisting;
    }
    public PlayerWhitelistingForCrowSyncToServer(FriendlyByteBuf buf) {
        this.whitelisting = buf.readBoolean();
    }

    public static void encode(PlayerWhitelistingForCrowSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeBoolean(object.whitelisting);
    }

    public static PlayerWhitelistingForCrowSyncToServer decode(FriendlyByteBuf buffer) {
        return new PlayerWhitelistingForCrowSyncToServer(buffer);
    }

    public static void consume(PlayerWhitelistingForCrowSyncToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(packet.whitelisting)
                CrowWhitelistEvent.playersActivelyWhitelisting.add(ctx.get().getSender());
            else
                CrowWhitelistEvent.playersActivelyWhitelisting.remove(ctx.get().getSender());

        });
        ctx.get().setPacketHandled(true);
    }
}