package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BonemealingCandleEffect implements CandleEffect{
    private static final int MAX_TIME = 8 * 20;

    private int checkCooldown;


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
        if (level.isClientSide()) return;
        if(candleData.lit){
            ServerLevel serverLevel = (ServerLevel) level;

            if (checkCooldown >= MAX_TIME) {
                BlockPos crop = findCrop(level, blockEntity.getBlockPos());
                if (crop != null) {
                    Block block = level.getBlockState(crop).getBlock();
                    if (block instanceof CropBlock cropBlock) {
                        cropBlock.performBonemeal(serverLevel, level.random, crop, level.getBlockState(crop));
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, crop.getX() + 0.5, crop.getY() + 0.5, crop.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.2);
                    }
                    checkCooldown = 0;
                }
            }
            checkCooldown = (checkCooldown + 1) % Integer.MAX_VALUE;
        }
    }

    @Nullable
    public BlockPos findCrop(Level level, BlockPos jarPos) {
        ArrayList<BlockPos> crops = new ArrayList<>();
        for (BlockPos pos : area) {
            BlockPos relativePos = jarPos.offset(pos);
            BlockState state = level.getBlockState(relativePos);
            if (!state.isAir() && state.getBlock() instanceof CropBlock) {
                crops.add(relativePos);
            }
        }

        if(crops.isEmpty()) return null;

        return crops.get(level.random.nextInt(crops.size()));
    }

    @Override
    public ParticleOptions getParticleType() {
        return ParticleTypes.HAPPY_VILLAGER;
    }
}
