package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class CrowTailFanPacket {
    int sourceId;

    public CrowTailFanPacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public CrowTailFanPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public static void encode(CrowTailFanPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
    }

    public static CrowTailFanPacket decode(FriendlyByteBuf buffer) {
        return new CrowTailFanPacket(buffer);
    }

    public static void consume(CrowTailFanPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    ((CrowEntity) world.getEntity(packet.sourceId)).tailFan = true;
                    ((CrowEntity) world.getEntity(packet.sourceId)).tailFanTimer = 15;
                    ((CrowEntity) world.getEntity(packet.sourceId)).tailFanTiltAngle = 20 + new Random().nextInt(20);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}