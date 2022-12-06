package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrainCauldronToServer {
    BlockPos cauldronPos;

    public DrainCauldronToServer(MixingCauldronTile cauldron) {
        this.cauldronPos = cauldron.getBlockPos();
    }
    public DrainCauldronToServer(FriendlyByteBuf buf) {
        this.cauldronPos = buf.readBlockPos();

    }

    public static void encode(DrainCauldronToServer object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.cauldronPos);
    }

    public static DrainCauldronToServer decode(FriendlyByteBuf buffer) {
        return new DrainCauldronToServer(buffer);
    }

    public static void consume(DrainCauldronToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getBlockEntity(packet.cauldronPos) instanceof MixingCauldronTile mixingCauldronTile) {
                mixingCauldronTile.setFluidStack(FluidStack.EMPTY);
                mixingCauldronTile.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}