package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.CandleEffectParticlePacket;
import net.joefoxe.hexerei.util.message.CandleExtinguishPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionCandleEffect extends AbstractCandleEffect{
    private static final int MAX_TIME = 8 * 20;

    @Nullable
    public MobEffect effect;


    public PotionCandleEffect(@Nullable MobEffect effect, ParticleOptions particleOptions){
        super(particleOptions);
        this.effect = effect;

    }
    public PotionCandleEffect(@Nullable MobEffect effect){
        super();
        this.effect = effect;

    }


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {

//        if (level.isClientSide()) return;
        if(candleData.lit){
            if (candleData.cooldown >= MAX_TIME) {
                int duration = 80;
                assert effect != null;
                if(effect.isInstantenous())
                    duration = 1;
                if (!level.isClientSide())
                    applyEffects(level, blockEntity.getBlockPos(), 5, duration, 0, effect, candleData.effectParticle, candleData);
                candleData.cooldown = 0;
            }
//            level.addParticle();
            if(candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && candleData.effectParticle.size() > 0)
                particle = (ParticleOptions) ForgeRegistries.PARTICLE_TYPES.getValue(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size())));
            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;
        }
    }

    private static void applyEffects(Level pLevel, BlockPos pPos, float size, int duration, int amplifier, @Nullable MobEffect pPrimary, List<ResourceLocation> particle, CandleData candleData) {
        if (pPrimary != null) {
            AABB aabb = (new AABB(pPos)).inflate(size).expandTowards(0.0D, (size * 4) < 4 ? 4 : size * 4 , 0.0D);
            List<LivingEntity> list = pLevel.getEntitiesOfClass(LivingEntity.class, aabb);

            PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(pPos.getX(), pPos.getY(), pPos.getZ(), 500, ((Level) pLevel).dimension());
            for(LivingEntity living : list) {
                if(!pLevel.isClientSide) {
                    living.addEffect(new MobEffectInstance(pPrimary, duration, amplifier, true, false, true));
                    if(particle != null && particle.size() > 0)
                        HexereiPacketHandler.instance.send(PacketDistributor.NEAR.with(() -> point), new CandleEffectParticlePacket(pPos, particle, living.getId(), 0));
                }
            }
            if(particle != null && particle.size() > 0)
                HexereiPacketHandler.instance.send(PacketDistributor.NEAR.with(() -> point), new CandleEffectParticlePacket(pPos, particle, 0, 1));

        }
    }

    public static void spawnParticles(Level pLevel, List<ResourceLocation> particle, LivingEntity living) {
        float heightOffset = living.getBbHeight() / 4f;
        for(int i = 0; i < 5; i++){
            float rotation = random.nextFloat() * 360f;
            Vec3 offset = new Vec3(random.nextDouble() * 2 * Math.cos(rotation), 0, random.nextDouble() * 2 * Math.sin(rotation));
            if(particle != null && Registry.PARTICLE_TYPE.get(particle.get(random.nextInt(particle.size()))) != null)
                pLevel.addParticle((ParticleOptions) Registry.PARTICLE_TYPE.get(particle.get(random.nextInt(particle.size()))), living.getX(), living.getY() + heightOffset, living.getZ(), offset.x / 16f, (random.nextDouble() + 0.5d) * 0.015d, offset.z / 16f);
        }
    }

    @Override
    public <T> AbstractCandleEffect getCopy() {
        return new PotionCandleEffect(this.effect, this.particle);
    }

    @Override
    public String getLocationName() {
        ResourceLocation loc = this.effect == null? null : Registry.MOB_EFFECT.getKey(this.effect);
        return loc != null ? loc.toString() : this.effect.getDescriptionId();
    }

    @Override
    public ParticleOptions getParticleType() {
        return ParticleTypes.HAPPY_VILLAGER;
    }
}
