package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowEatParticlesPacket {
    int sourceId;
    ItemStack stack;

    public CrowEatParticlesPacket(Entity entity, ItemStack stack) {
        this.sourceId = entity.getId();
        this.stack = stack.copy();
    }
    public CrowEatParticlesPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.stack = buf.readItem().copy();
    }

    public static void encode(CrowEatParticlesPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeItemStack(object.stack, false);
    }

    public static CrowEatParticlesPacket decode(FriendlyByteBuf buffer) {
        return new CrowEatParticlesPacket(buffer);
    }

    public static void consume(CrowEatParticlesPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    ((CrowEntity) world.getEntity(packet.sourceId)).eatParticles(packet.stack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}