package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowCanAttackToServer {
    int sourceId;
    boolean canAttack;

    public CrowCanAttackToServer(CrowEntity entity, boolean canAttack) {
        this.sourceId = entity.getId();
        this.canAttack = canAttack;
        entity.canAttack = canAttack;
    }
    public CrowCanAttackToServer(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.canAttack = buf.readBoolean();
    }

    public static void encode(CrowCanAttackToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeBoolean(object.canAttack);
    }

    public static CrowCanAttackToServer decode(FriendlyByteBuf buffer) {
        return new CrowCanAttackToServer(buffer);
    }

    public static void consume(CrowCanAttackToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                crowEntity.canAttack = packet.canAttack;

        });
        ctx.get().setPacketHandled(true);
    }
}