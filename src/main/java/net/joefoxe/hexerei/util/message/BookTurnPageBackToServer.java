package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookTurnPageBackToServer {
    BlockPos herbJarTile;
    int toggled;

    public BookTurnPageBackToServer(HerbJarTile herbJarTile, int toggled) {
        this.herbJarTile = herbJarTile.getBlockPos();
        this.toggled = toggled;
    }
    public BookTurnPageBackToServer(FriendlyByteBuf buf) {
        this.herbJarTile = buf.readBlockPos();
        this.toggled = buf.readInt();

    }

    public static void encode(BookTurnPageBackToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.herbJarTile);
        buffer.writeInt(object.toggled);
    }

    public static BookTurnPageBackToServer decode(FriendlyByteBuf buffer) {
        return new BookTurnPageBackToServer(buffer);
    }

    public static void consume(BookTurnPageBackToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((HerbJarTile)world.getBlockEntity(packet.herbJarTile)).setButtonToggled(packet.toggled);
        });
        ctx.get().setPacketHandled(true);
    }
}