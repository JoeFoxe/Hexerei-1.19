package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BookEntriesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntriesPacket> CODEC  = StreamCodec.ofMember(BookEntriesPacket::encode, BookEntriesPacket::new);
    public static final Type<BookEntriesPacket> TYPE = new Type<>(HexereiUtil.getResource("book_entries"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    protected Map<ResourceLocation, BookEntries> bookEntries = new HashMap<>();

    public BookEntriesPacket(final Map<ResourceLocation, BookEntries> bookEntries) {
        this.bookEntries = bookEntries;
    }
    public BookEntriesPacket(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++){
            ResourceLocation book = buf.readResourceLocation();
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                this.bookEntries.put(book, BookEntries.loadFromTag(tag));
            }
        }
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(bookEntries.size());

        bookEntries.forEach(((resourceLocation, bookEntries1) -> {
            buffer.writeResourceLocation(resourceLocation);
            buffer.writeNbt(BookEntries.saveToTag(bookEntries1));
        }));
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        bookEntries.forEach(((resourceLocation, bookEntries1) -> {
            BookManager.clearBookEntries(resourceLocation);
            BookManager.addBookEntries(bookEntries1);
        }));
    }
}