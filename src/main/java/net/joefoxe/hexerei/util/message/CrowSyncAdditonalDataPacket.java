package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowSyncAdditonalDataPacket {
    int sourceId;
    CompoundTag tag;

    public CrowSyncAdditonalDataPacket(Entity entity, CompoundTag tag) {
        this.sourceId = entity.getId();
        this.tag = tag;
    }
    public CrowSyncAdditonalDataPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.tag = buf.readNbt();
    }

    public static void encode(CrowSyncAdditonalDataPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeNbt(object.tag);
    }

    public static CrowSyncAdditonalDataPacket decode(FriendlyByteBuf buffer) {
        return new CrowSyncAdditonalDataPacket(buffer);
    }

    public static void consume(CrowSyncAdditonalDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getEntity(packet.sourceId) instanceof CrowEntity crow) {
                crow.readAdditionalSaveDataNoSuper(packet.tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}