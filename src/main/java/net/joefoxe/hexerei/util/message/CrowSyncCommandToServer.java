package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowSyncCommandToServer {
    int sourceId;
    int command;

    public CrowSyncCommandToServer(Entity entity, int tag) {
        this.sourceId = entity.getId();
        this.command = tag;
    }
    public CrowSyncCommandToServer(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.command = buf.readInt();

    }

    public static void encode(CrowSyncCommandToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.command);
    }

    public static CrowSyncCommandToServer decode(FriendlyByteBuf buffer) {
        return new CrowSyncCommandToServer(buffer);
    }

    public static void consume(CrowSyncCommandToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                crowEntity.setCommand(packet.command);
        });
        ctx.get().setPacketHandled(true);
    }
}