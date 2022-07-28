package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BookPagesPacket {

    protected final Map<ResourceLocation, BookPage> bookPages;

    public BookPagesPacket(final Map<ResourceLocation, BookPage> bookPages) {
        this.bookPages = bookPages;
    }
    public BookPagesPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.bookPages = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation name = buf.readResourceLocation();
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                BookPage bookPage = BookPage.loadFromTag(tag);
                bookPages.put(name, bookPage);
            }
        }
    }

    public static void encode(BookPagesPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.bookPages.size());
        for (var entry : object.bookPages.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            buffer.writeNbt(BookPage.saveToTag(entry.getValue()));
        }
    }

    public static BookPagesPacket decode(FriendlyByteBuf buffer) {
        return new BookPagesPacket(buffer);
    }

    public static void consume(BookPagesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            BookManager.clearBookPages();
            packet.bookPages.keySet().forEach(k -> {
                BookPage bookPage = packet.bookPages.get(k);
                BookManager.addBookPage(k, bookPage);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}