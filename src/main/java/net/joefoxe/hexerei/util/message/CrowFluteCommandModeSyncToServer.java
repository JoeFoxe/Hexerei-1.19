package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CrowFluteCommandModeSyncToServer {
    ItemStack flute;
    int mode;
    UUID entityId;
    int hand;

    public CrowFluteCommandModeSyncToServer(ItemStack flute, int mode, UUID entityId, int hand) {
        this.flute = flute;
        this.mode = mode;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteCommandModeSyncToServer(FriendlyByteBuf buf) {
        this.flute = buf.readItem();
        this.mode = buf.readInt();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();

    }

    public static void encode(CrowFluteCommandModeSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.flute);
        buffer.writeInt(object.mode);
        buffer.writeUUID(object.entityId);
        buffer.writeInt(object.hand);
    }

    public static CrowFluteCommandModeSyncToServer decode(FriendlyByteBuf buffer) {
        return new CrowFluteCommandModeSyncToServer(buffer);
    }

    public static void consume(CrowFluteCommandModeSyncToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                if (((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getItem() == packet.flute.getItem())
                    ((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getOrCreateTag().putInt("commandMode", packet.mode);
            }
            else
            {
                if (((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getItem() == packet.flute.getItem())
                    ((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getOrCreateTag().putInt("commandMode", packet.mode);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}