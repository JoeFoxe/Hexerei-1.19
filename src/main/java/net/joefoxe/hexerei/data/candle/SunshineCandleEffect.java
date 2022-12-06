package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class SunshineCandleEffect extends AbstractCandleEffect{
    private static final int MAX_TIME = 8 * 20;

    public SunshineCandleEffect(){
    }


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
//        if (level.isClientSide()) return;
        if(candleData.lit){

            if (candleData.cooldown >= MAX_TIME) {
                if ((level instanceof ServerLevel serverLevel)) {
                    // look up command for setting weather to clear skies
                    serverLevel.getLevel().setWeatherParameters(MAX_TIME + 1, 0, false, false);
//                    serverLevel.sendParticles(particle, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.2);

                    if(particle != null && candleData.effectParticle.size() > 0) {
//                        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 500, level.dimension());
//                        HexereiPacketHandler.instance.send(PacketDistributor.NEAR.with(() -> point), new CandleEffectParticlePacket(blockEntity.getBlockPos(), candleData.effectParticle, 0, 1));
                    }
                }
                candleData.cooldown = 0;
            }
            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;

//
//            if(candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && candleData.effectParticle.size() > 0)
//                particle = (ParticleOptions) ForgeRegistries.PARTICLE_TYPES.getValue(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size())));
//            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;
        }

    }


    @Override
    public <T> AbstractCandleEffect getCopy() {
        return new SunshineCandleEffect();
    }

    @Override
    public String getLocationName() {
        return new ResourceLocation(Hexerei.MOD_ID, "sunshine_effect").toString();
    }

    @Override
    public ParticleOptions getParticleType() {
        return ParticleTypes.HAPPY_VILLAGER;
    }
}
