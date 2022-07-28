package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CrowFluteClearCrowPerchToServer {
    ItemStack flute;
    UUID entityId;
    int hand;

    public CrowFluteClearCrowPerchToServer(ItemStack flute, UUID entityId, int hand) {
        this.flute = flute;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteClearCrowPerchToServer(FriendlyByteBuf buf) {
        this.flute = buf.readItem();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();
    }

    public static void encode(CrowFluteClearCrowPerchToServer object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.flute);
        buffer.writeUUID(object.entityId);
        buffer.writeInt(object.hand);
    }

    public static CrowFluteClearCrowPerchToServer decode(FriendlyByteBuf buffer) {
        return new CrowFluteClearCrowPerchToServer(buffer);
    }

    public static void consume(CrowFluteClearCrowPerchToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                if (((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getItem() == packet.flute.getItem()) {

                    ListTag list = ((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getOrCreateTag().getList("crowList", CompoundTag.TAG_COMPOUND);
                    for(int i = 0; i < list.size(); i++)
                    {
                        CompoundTag compoundTag = list.getCompound(i);
                        Entity entity = ((ServerLevel)world).getEntity(compoundTag.getUUID("UUID"));
                        if(entity instanceof CrowEntity)
                        {
                            ((CrowEntity) entity).setPerchPos(null);
                        }
                    }
//                    ((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getOrCreateTag().put("crowList", new CompoundTag());
                }
            }
            else
            {
                if (((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getItem() == packet.flute.getItem()){

                    ListTag list = ((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getOrCreateTag().getList("crowList", CompoundTag.TAG_COMPOUND);
                    for(int i = 0; i < list.size(); i++)
                    {
                        CompoundTag compoundTag = list.getCompound(i);
                        Entity entity = ((ServerLevel)world).getEntity(compoundTag.getUUID("UUID"));
                        if(entity instanceof CrowEntity)
                        {
                            ((CrowEntity) entity).setPerchPos(null);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}