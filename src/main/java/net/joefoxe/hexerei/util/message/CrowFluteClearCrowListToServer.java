package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CrowFluteClearCrowListToServer {
    ItemStack flute;
    UUID entityId;
    int hand;

    public CrowFluteClearCrowListToServer(ItemStack flute, UUID entityId, int hand) {
        this.flute = flute;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteClearCrowListToServer(FriendlyByteBuf buf) {
        this.flute = buf.readItem();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();
    }

    public static void encode(CrowFluteClearCrowListToServer object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.flute);
        buffer.writeUUID(object.entityId);
        buffer.writeInt(object.hand);
    }

    public static CrowFluteClearCrowListToServer decode(FriendlyByteBuf buffer) {
        return new CrowFluteClearCrowListToServer(buffer);
    }

    public static void consume(CrowFluteClearCrowListToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(packet.hand == 0) {
                if (world.getPlayerByUUID(packet.entityId).getMainHandItem().getItem() == packet.flute.getItem()) {

                    world.getPlayerByUUID(packet.entityId).getMainHandItem().getOrCreateTag().remove("crowList");
                    world.getPlayerByUUID(packet.entityId).getMainHandItem().getOrCreateTag().put("crowList", new CompoundTag());
                }
            }
            else
            {
                if (world.getPlayerByUUID(packet.entityId).getOffhandItem().getItem() == packet.flute.getItem()) {

                    world.getPlayerByUUID(packet.entityId).getOffhandItem().getOrCreateTag().remove("crowList");
                    world.getPlayerByUUID(packet.entityId).getOffhandItem().getOrCreateTag().put("crowList", new CompoundTag());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}