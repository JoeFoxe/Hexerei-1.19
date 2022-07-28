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

public class CrowFluteCommandSyncToServer {
    ItemStack flute;
    int command;
    UUID entityId;
    int hand;

    public CrowFluteCommandSyncToServer(ItemStack flute, int command, UUID entityId, int hand) {
        this.flute = flute;
        this.command = command;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteCommandSyncToServer(FriendlyByteBuf buf) {
        this.flute = buf.readItem();
        this.command = buf.readInt();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();

    }

    public static void encode(CrowFluteCommandSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.flute);
        buffer.writeInt(object.command);
        buffer.writeUUID(object.entityId);
        buffer.writeInt(object.hand);
    }

    public static CrowFluteCommandSyncToServer decode(FriendlyByteBuf buffer) {
        return new CrowFluteCommandSyncToServer(buffer);
    }

    public static void consume(CrowFluteCommandSyncToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                    ((Player) world.getPlayerByUUID(packet.entityId)).getMainHandItem().getOrCreateTag().putInt("commandSelected", packet.command);
            }
            else
            {
                if (((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getItem() == packet.flute.getItem())
                    ((Player) world.getPlayerByUUID(packet.entityId)).getOffhandItem().getOrCreateTag().putInt("commandSelected", packet.command);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}