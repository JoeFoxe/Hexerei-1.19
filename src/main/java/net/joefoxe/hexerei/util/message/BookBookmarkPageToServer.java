package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookBookmarkPageToServer {
    BlockPos bookAltar;
    int chapter;
    int page;

    public BookBookmarkPageToServer(BookOfShadowsAltarTile bookAltar, int chapter, int page) {
        this.bookAltar = bookAltar.getBlockPos();
        this.chapter = chapter;
        this.page = page;
    }
    public BookBookmarkPageToServer(FriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.chapter = buf.readInt();
        this.page = buf.readInt();

    }

    public static void encode(BookBookmarkPageToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.bookAltar);
        buffer.writeInt(object.chapter);
        buffer.writeInt(object.page);
    }

    public static BookBookmarkPageToServer decode(FriendlyByteBuf buffer) {
        return new BookBookmarkPageToServer(buffer);
    }

    public static void consume(BookBookmarkPageToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((BookOfShadowsAltarTile)world.getBlockEntity(packet.bookAltar)).clickPageBookmark(packet.chapter, packet.page);
        });
        ctx.get().setPacketHandled(true);
    }
}