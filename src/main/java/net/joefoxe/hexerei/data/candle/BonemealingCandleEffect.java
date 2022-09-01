package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class BonemealingCandleEffect extends AbstractCandleEffect{
    private static final int MAX_TIME = 8 * 20;

    public BonemealingCandleEffect(){
    }


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
//        if (level.isClientSide()) return;
        if(candleData.lit){

            if (candleData.cooldown >= MAX_TIME) {
                BlockPos crop = findCrop(level, blockEntity.getBlockPos());
                if (crop != null) {
                    Block block = level.getBlockState(crop).getBlock();
                    if ((!level.isClientSide()) && block instanceof CropBlock cropBlock) {
                        ServerLevel serverLevel = (ServerLevel) level;
                        cropBlock.performBonemeal(serverLevel, level.random, crop, level.getBlockState(crop));
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, crop.getX() + 0.5, crop.getY() + 0.5, crop.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.2);
                    }

                    candleData.cooldown = 0;
                }
            }
            if(candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && candleData.effectParticle.size() > 0)
                particle = (ParticleOptions) ForgeRegistries.PARTICLE_TYPES.getValue(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size())));
            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;
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
    public <T> AbstractCandleEffect getCopy() {
        return new BonemealingCandleEffect();
    }

    @Override
    public String getLocationName() {
        return new ResourceLocation(Hexerei.MOD_ID, "growth_effect").toString();
    }

    @Override
    public ParticleOptions getParticleType() {
        return ParticleTypes.HAPPY_VILLAGER;
    }
}
