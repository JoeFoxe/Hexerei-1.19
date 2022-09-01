package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class CandleEffectParticlePacket {
    BlockPos pos;

    List<ResourceLocation> particleLocations;

    int livingId;

    int stage;

    public CandleEffectParticlePacket(BlockPos pos, List<ResourceLocation> particleLocations, int livingId, int stage) {
        this.pos = pos;
        this.particleLocations = particleLocations;
        this.livingId = livingId;
        this.stage = stage;
    }

    public static void encode(CandleEffectParticlePacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeInt(object.particleLocations.size());
        for(int i = 0; i < object.particleLocations.size(); i++){
            buffer.writeResourceLocation(object.particleLocations.get(i));
        }
        buffer.writeInt(object.livingId);
        buffer.writeInt(object.stage);
    }

    public static CandleEffectParticlePacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int size = buffer.readInt();
        List<ResourceLocation> list = new ArrayList<>();
        for(int i = 0; i < size; i++){
            list.add(buffer.readResourceLocation());
        }


        return new CandleEffectParticlePacket(pos, list, buffer.readInt(), buffer.readInt());
    }

    public static void consume(CandleEffectParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    Random random = new Random();
                    if(packet.stage == 0 && world.getEntity(packet.livingId) instanceof LivingEntity livingEntity) {
                        PotionCandleEffect.spawnParticles(world, packet.particleLocations, livingEntity);
                    }
                    if(packet.stage == 1)
                        Candle.spawnParticleWave(world, packet.pos, true, packet.particleLocations, 20);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}