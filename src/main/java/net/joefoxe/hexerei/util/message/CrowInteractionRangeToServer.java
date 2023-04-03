package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowInteractionRangeToServer {
    int sourceId;
    int range;

    public CrowInteractionRangeToServer(CrowEntity entity, int newRange) {
        this.sourceId = entity.getId();
        this.range = newRange;
        entity.interactionRange = newRange;
    }
    public CrowInteractionRangeToServer(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.range = buf.readInt();
    }

    public static void encode(CrowInteractionRangeToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.range);
    }

    public static CrowInteractionRangeToServer decode(FriendlyByteBuf buffer) {
        return new CrowInteractionRangeToServer(buffer);
    }

    public static void consume(CrowInteractionRangeToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }


            if(world.getEntity(packet.sourceId) instanceof CrowEntity crowEntity)
                crowEntity.interactionRange = packet.range;

        });
        ctx.get().setPacketHandled(true);
    }
}