package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BroomEnderSatchelBrushParticlePacket {
    int sourceId;
    float rotation;

    public BroomEnderSatchelBrushParticlePacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public BroomEnderSatchelBrushParticlePacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();

    }

    public static void encode(BroomEnderSatchelBrushParticlePacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
    }

    public static BroomEnderSatchelBrushParticlePacket decode(FriendlyByteBuf buffer) {
        return new BroomEnderSatchelBrushParticlePacket(buffer);
    }

    public static void consume(BroomEnderSatchelBrushParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((BroomEntity)world.getEntity(packet.sourceId)).transferBrushParticles();
        });
        ctx.get().setPacketHandled(true);
    }
}