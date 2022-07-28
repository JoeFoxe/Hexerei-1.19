package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookBookmarkDeleteToServer {
    BlockPos bookAltar;
    int slot;

    public BookBookmarkDeleteToServer(BookOfShadowsAltarTile bookAltar, int slot) {
        this.bookAltar = bookAltar.getBlockPos();
        this.slot = slot;
    }
    public BookBookmarkDeleteToServer(FriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.slot = buf.readInt();

    }

    public static void encode(BookBookmarkDeleteToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.bookAltar);
        buffer.writeInt(object.slot);
    }

    public static BookBookmarkDeleteToServer decode(FriendlyByteBuf buffer) {
        return new BookBookmarkDeleteToServer(buffer);
    }

    public static void consume(BookBookmarkDeleteToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((BookOfShadowsAltarTile)world.getBlockEntity(packet.bookAltar)).deleteBookmark(packet.slot);
        });
        ctx.get().setPacketHandled(true);
    }
}