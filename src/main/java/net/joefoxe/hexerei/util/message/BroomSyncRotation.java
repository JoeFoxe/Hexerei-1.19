package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BroomSyncRotation {
    int sourceId;
    float rotation;

    public BroomSyncRotation(Entity entity, float tag) {
        this.sourceId = entity.getId();
        this.rotation = tag;
    }
    public BroomSyncRotation(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.rotation = buf.readFloat();

    }

    public static void encode(BroomSyncRotation object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeFloat(object.rotation);
    }

    public static BroomSyncRotation decode(FriendlyByteBuf buffer) {
        return new BroomSyncRotation(buffer);
    }

    public static void consume(BroomSyncRotation packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }


            if (!world.getEntity(packet.sourceId).isControlledByLocalInstance())
                ((BroomEntity) world.getEntity(packet.sourceId)).setRotation(packet.rotation);
        });
        ctx.get().setPacketHandled(true);
    }
}