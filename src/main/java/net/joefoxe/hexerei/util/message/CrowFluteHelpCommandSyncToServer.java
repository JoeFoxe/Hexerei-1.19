package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CrowFluteHelpCommandSyncToServer {
    ItemStack flute;
    int helpCommand;
    UUID entityId;
    int hand;

    public CrowFluteHelpCommandSyncToServer(ItemStack flute, int helpCommand, UUID entityId, int hand) {
        this.flute = flute;
        this.helpCommand = helpCommand;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteHelpCommandSyncToServer(FriendlyByteBuf buf) {
        this.flute = buf.readItem();
        this.helpCommand = buf.readInt();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();

    }

    public static void encode(CrowFluteHelpCommandSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.flute);
        buffer.writeInt(object.helpCommand);
        buffer.writeUUID(object.entityId);
        buffer.writeInt(object.hand);
    }

    public static CrowFluteHelpCommandSyncToServer decode(FriendlyByteBuf buffer) {
        return new CrowFluteHelpCommandSyncToServer(buffer);
    }

    public static void consume(CrowFluteHelpCommandSyncToServer packet, Supplier<NetworkEvent.Context> ctx) {
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
                if (world.getPlayerByUUID(packet.entityId).getMainHandItem().getItem() == packet.flute.getItem())
                    world.getPlayerByUUID(packet.entityId).getMainHandItem().getOrCreateTag().putInt("helpCommandSelected", packet.helpCommand);
            }
            else {
                if (world.getPlayerByUUID(packet.entityId).getOffhandItem().getItem() == packet.flute.getItem())
                    world.getPlayerByUUID(packet.entityId).getOffhandItem().getOrCreateTag().putInt("helpCommandSelected", packet.helpCommand);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}