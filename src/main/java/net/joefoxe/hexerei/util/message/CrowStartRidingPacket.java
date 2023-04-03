package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowStartRidingPacket {
    int sourceIdCrow;
    int sourceIdPlayer;

    public CrowStartRidingPacket(Entity entity, Player player) {
        this.sourceIdCrow = entity.getId();
        this.sourceIdPlayer = player.getId();
    }
    public CrowStartRidingPacket(FriendlyByteBuf buf) {
        this.sourceIdCrow = buf.readInt();
        this.sourceIdPlayer = buf.readInt();
    }

    public static void encode(CrowStartRidingPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceIdCrow);
        buffer.writeInt(object.sourceIdPlayer);
    }

    public static CrowStartRidingPacket decode(FriendlyByteBuf buffer) {
        return new CrowStartRidingPacket(buffer);
    }

    public static void consume(CrowStartRidingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getEntity(packet.sourceIdCrow) != null) {
                if((world.getEntity(packet.sourceIdCrow)) instanceof CrowEntity) {
                    world.getEntity(packet.sourceIdCrow).startRiding((world.getEntity(packet.sourceIdPlayer)), true);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}