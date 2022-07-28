package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookTurnPageToServer {
    BlockPos bookAltar;
    int turnPage;
    int chapter;
    int page;

    public BookTurnPageToServer(BookOfShadowsAltarTile bookAltar, int turnPage, int chapter, int page) {
        this.bookAltar = bookAltar.getBlockPos();
        this.turnPage = turnPage;
        this.chapter = chapter;
        this.page = page;
    }
    public BookTurnPageToServer(FriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.turnPage = buf.readInt();
        this.chapter = buf.readInt();
        this.page = buf.readInt();

    }

    public static void encode(BookTurnPageToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.bookAltar);
        buffer.writeInt(object.turnPage);
        buffer.writeInt(object.chapter);
        buffer.writeInt(object.page);
    }

    public static BookTurnPageToServer decode(FriendlyByteBuf buffer) {
        return new BookTurnPageToServer(buffer);
    }

    public static void consume(BookTurnPageToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((BookOfShadowsAltarTile)world.getBlockEntity(packet.bookAltar)).setTurnPage(packet.turnPage, packet.chapter, packet.page);
        });
        ctx.get().setPacketHandled(true);
    }
}