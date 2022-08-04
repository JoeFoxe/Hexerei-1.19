package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookEntries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BookEntriesPacket {

    protected BookEntries bookEntries;

    public BookEntriesPacket(final BookEntries bookEntries) {
        this.bookEntries = bookEntries;
    }
    public BookEntriesPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag != null) {
            this.bookEntries = BookEntries.loadFromTag(tag);
        }
    }

    public static void encode(BookEntriesPacket object, FriendlyByteBuf buffer) {
        buffer.writeNbt(BookEntries.saveToTag(object.bookEntries));
    }

    public static BookEntriesPacket decode(FriendlyByteBuf buffer) {
        return new BookEntriesPacket(buffer);
    }

    public static void consume(BookEntriesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            BookManager.clearBookEntries();
            BookManager.addBookEntries(packet.bookEntries);
        });
        ctx.get().setPacketHandled(true);
    }
}