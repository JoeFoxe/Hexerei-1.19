package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CofferSyncCrowButtonToServer {
    BlockPos cofferTile;
    int toggled;

    public CofferSyncCrowButtonToServer(CofferTile cofferTile, int toggled) {
        this.cofferTile = cofferTile.getBlockPos();
        this.toggled = toggled;
    }
    public CofferSyncCrowButtonToServer(FriendlyByteBuf buf) {
        this.cofferTile = buf.readBlockPos();
        this.toggled = buf.readInt();

    }

    public static void encode(CofferSyncCrowButtonToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.cofferTile);
        buffer.writeInt(object.toggled);
    }

    public static CofferSyncCrowButtonToServer decode(FriendlyByteBuf buffer) {
        return new CofferSyncCrowButtonToServer(buffer);
    }

    public static void consume(CofferSyncCrowButtonToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((CofferTile)world.getBlockEntity(packet.cofferTile)).setButtonToggled(packet.toggled);
        });
        ctx.get().setPacketHandled(true);
    }
}