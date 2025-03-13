package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class BookPagesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookPagesPacket> CODEC  = StreamCodec.ofMember(BookPagesPacket::encode, BookPagesPacket::new);
    public static final Type<BookPagesPacket> TYPE = new Type<>(HexereiUtil.getResource("book_pages"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    protected final Map<ResourceLocation, Map<ResourceLocation, BookPage>> bookPages;

    public BookPagesPacket(final Map<ResourceLocation, Map<ResourceLocation, BookPage>> bookPages) {
        this.bookPages = bookPages;
    }
    public BookPagesPacket(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        this.bookPages = new HashMap<>();
        for (int i = 0; i < size; i++) {
//            ResourceLocation name = buf.readResourceLocation();
//            CompoundTag tag = buf.readNbt();
//            if (tag != null) {
//                BookPage bookPage = BookPage.loadFromTag(tag);
//                bookPages.put(name, bookPage);
//            }

            ResourceLocation book = buf.readResourceLocation();
            int size2 = buf.readInt();
            for (int j = 0; j < size2; j++) {
                ResourceLocation name = buf.readResourceLocation();
                CompoundTag tag = buf.readNbt();
                if (tag != null) {
                    BookPage bookPage = BookPage.loadFromTag(tag);
                    if (!bookPages.containsKey(book))
                        bookPages.put(book, new HashMap<>());
                    bookPages.get(book).put(name, bookPage);
//                    bookPages.put(name, bookPage);
                }
            }
        }
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(bookPages.size());
        for (var entry : bookPages.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            buffer.writeInt(entry.getValue().size());
            for (var entry2 : entry.getValue().entrySet()) {
                buffer.writeResourceLocation(entry2.getKey());
                buffer.writeNbt(BookPage.saveToTag(entry2.getValue()));
            }
        }
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        bookPages.keySet().forEach(book -> {
            BookManager.clearBookPages(book);
            bookPages.get(book).forEach((loc, map) -> {

                BookPage bookPage = bookPages.get(book).get(loc);
                bookPage.location = loc;
                BookManager.addBookPage(book, loc, bookPage);
            });

//            BookPage bookPage = bookPages.get(k);
//            BookManager.addBookPage(k, bookPage);
        });
    }
}