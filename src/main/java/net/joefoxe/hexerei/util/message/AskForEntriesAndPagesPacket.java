package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.BookManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AskForEntriesAndPagesPacket {

    public AskForEntriesAndPagesPacket() {
    }
    public AskForEntriesAndPagesPacket(FriendlyByteBuf buf) {
    }

    public static void encode(AskForEntriesAndPagesPacket object, FriendlyByteBuf buffer) {
    }

    public static AskForEntriesAndPagesPacket decode(FriendlyByteBuf buffer) {
        return new AskForEntriesAndPagesPacket(buffer);
    }

    public static void consume(AskForEntriesAndPagesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            BookManager.sendBookEntriesToClient();
            BookManager.sendBookPagesToClient();
        });
        ctx.get().setPacketHandled(true);
    }
}