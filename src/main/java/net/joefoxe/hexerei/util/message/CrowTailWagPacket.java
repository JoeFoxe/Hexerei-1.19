package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowTailWagPacket {
    int sourceId;

    public CrowTailWagPacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public CrowTailWagPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public static void encode(CrowTailWagPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
    }

    public static CrowTailWagPacket decode(FriendlyByteBuf buffer) {
        return new CrowTailWagPacket(buffer);
    }

    public static void consume(CrowTailWagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getEntity(packet.sourceId) != null) {
                if((world.getEntity(packet.sourceId)) instanceof CrowEntity) {
                    ((CrowEntity) world.getEntity(packet.sourceId)).tailWag = true;
                    ((CrowEntity) world.getEntity(packet.sourceId)).tailWagTimer = 15;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}