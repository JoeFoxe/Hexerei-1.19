package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookBookmarkSwapToServer {
    BlockPos bookAltar;
    int slot;
    int slot2;

    public BookBookmarkSwapToServer(BookOfShadowsAltarTile bookAltar, int slot, int slot2) {
        this.bookAltar = bookAltar.getBlockPos();
        this.slot = slot;
        this.slot2 = slot2;
    }
    public BookBookmarkSwapToServer(FriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.slot = buf.readInt();
        this.slot2 = buf.readInt();

    }

    public static void encode(BookBookmarkSwapToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.bookAltar);
        buffer.writeInt(object.slot);
        buffer.writeInt(object.slot2);
    }

    public static BookBookmarkSwapToServer decode(FriendlyByteBuf buffer) {
        return new BookBookmarkSwapToServer(buffer);
    }

    public static void consume(BookBookmarkSwapToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((BookOfShadowsAltarTile)world.getBlockEntity(packet.bookAltar)).swapBookmarks(packet.slot, packet.slot2);
        });
        ctx.get().setPacketHandled(true);
    }
}