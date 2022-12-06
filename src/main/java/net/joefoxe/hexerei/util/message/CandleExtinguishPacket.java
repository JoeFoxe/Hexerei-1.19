package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CandleExtinguishPacket {
    BlockPos pos;

    public CandleExtinguishPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CandleExtinguishPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
    }

    public static CandleExtinguishPacket decode(FriendlyByteBuf buffer) {
        return new CandleExtinguishPacket(buffer.readBlockPos());
    }

    public static void consume(CandleExtinguishPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getBlockEntity(packet.pos) != null){
                BlockEntity blockEntity = world.getBlockEntity(packet.pos);
                if(blockEntity instanceof CandleTile candleTile){
                    Candle.extinguish(world, packet.pos, world.getBlockState(packet.pos), candleTile);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}