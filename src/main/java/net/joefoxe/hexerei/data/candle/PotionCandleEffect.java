package net.joefoxe.hexerei.data.candle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.CandleEffectParticlePacket;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
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

        if (this.effect == null) return;

        if(candleData.lit){
            if (candleData.cooldown >= MAX_TIME) {
                int duration = 10 * 20;
                if (!level.isClientSide())
                    applyEffects(level, blockEntity.getBlockPos(), 5, effect.isInstantenous() ? 1 : (int)(duration * candleData.getEffectDurationMultiplier()), Math.max(0, (int) candleData.getEffectAmplifierMultiplier() - 1), effect, candleData.effectParticle, candleData);
                candleData.cooldown = 0;
            }
            if(candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && candleData.effectParticle.size() > 0) {

                String resourceLocation = candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size()));
                try {
                    particle = ParticleArgument.readParticle(new StringReader(resourceLocation), level.registryAccess());
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }

            }
            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;
        }
    }




    private static void applyEffects(Level pLevel, BlockPos pPos, float size, int duration, int amplifier, @Nullable MobEffect pPrimary, List<String> particle, CandleData candleData) {
        if (pPrimary != null) {
            AABB aabb = (new AABB(pPos)).inflate(size).expandTowards(0.0D, (size * 4) < 4 ? 4 : size * 4 , 0.0D);
            List<LivingEntity> list = pLevel.getEntitiesOfClass(LivingEntity.class, aabb);

            int id = BuiltInRegistries.MOB_EFFECT.getId(pPrimary);
            Optional<Holder.Reference<MobEffect>> effectHolder = BuiltInRegistries.MOB_EFFECT.getHolder(id);
            for(LivingEntity living : list) {
                if(!pLevel.isClientSide && effectHolder.isPresent()) {
                    living.addEffect(
                            new MobEffectInstance(
                                    effectHolder.get(),
                                    duration,
                                    amplifier,
                                    true,
                                    false,
                                    true
                            )
                    );

                    if(particle != null && !particle.isEmpty())
                        HexereiPacketHandler.sendToNearbyClient(pLevel, pPos, new CandleEffectParticlePacket(pPos, particle, living.getId(), 0));
                }
            }
            if(particle != null && !particle.isEmpty())
                HexereiPacketHandler.sendToNearbyClient(pLevel, pPos, new CandleEffectParticlePacket(pPos, particle, 0, 1));

        }
    }

    public static void spawnParticles(Level pLevel, List<String> particle, LivingEntity living) {
        float heightOffset = living.getBbHeight() / 4f;
        for(int i = 0; i < 5; i++){
            float rotation = random.nextFloat() * 360f;
            Vec3 offset = new Vec3(random.nextDouble() * 2 * Math.cos(rotation), 0, random.nextDouble() * 2 * Math.sin(rotation));
            if (particle != null) {
                try {
                    pLevel.addParticle(ParticleArgument.readParticle(new StringReader(particle.get(random.nextInt(particle.size()))), pLevel.registryAccess()),
                            living.getX(), living.getY() + heightOffset, living.getZ(), offset.x / 32f, (random.nextDouble() + 0.5d) * 0.015d, offset.z / 32f);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public <T> AbstractCandleEffect getCopy() {
        return new PotionCandleEffect(this.effect, this.particle);
    }

    @Override
    public String getLocationName() {
        ResourceLocation loc = this.effect == null? null : BuiltInRegistries.MOB_EFFECT.getKey(this.effect);
        return loc != null ? loc.toString() : this.effect.getDescriptionId();
    }
}
