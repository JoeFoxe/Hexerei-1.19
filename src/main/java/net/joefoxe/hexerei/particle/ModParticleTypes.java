package net.joefoxe.hexerei.particle;

import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Hexerei.MOD_ID);


    public static final DeferredHolder<ParticleType<?>, ParticleType<CauldronParticleData>> CAULDRON = PARTICLES.register("cauldron_particle", () -> new ParticleType<>(true) {
        @Override
        public MapCodec<CauldronParticleData> codec() {
            return CauldronParticleData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, CauldronParticleData> streamCodec() {
            return CauldronParticleData.STREAM_CODEC;
        }


        //        @Nonnull
//        @Override
//        public Codec<CauldronParticleData> codec() {
//
//            return CauldronParticleData.codec(this);
//        }
    });
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = PARTICLES.register("blood_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_BIT = PARTICLES.register("blood_bit_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOOK_TEST = PARTICLES.register("book_test", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM = PARTICLES.register("broom_particle_1", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM_2 = PARTICLES.register("broom_particle_2", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM_3 = PARTICLES.register("broom_particle_3", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM_4 = PARTICLES.register("broom_particle_4", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM_5 = PARTICLES.register("broom_particle_5", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROOM_6 = PARTICLES.register("broom_particle_6", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOG = PARTICLES.register("fog_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXTINGUISH = PARTICLES.register("extinguish_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOON_BRUSH_1 = PARTICLES.register("moon_brush_1", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOON_BRUSH_2 = PARTICLES.register("moon_brush_2", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOON_BRUSH_3 = PARTICLES.register("moon_brush_3", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOON_BRUSH_4 = PARTICLES.register("moon_brush_4", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_BRUSH = PARTICLES.register("star_brush", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OWL_TELEPORT = PARTICLES.register("owl_teleport", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OWL_TELEPORT_BARN = PARTICLES.register("owl_teleport_barn", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OWL_TELEPORT_BARRED = PARTICLES.register("owl_teleport_barred", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OWL_TELEPORT_SNOWY = PARTICLES.register("owl_teleport_snowy", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAHOGANY_LEAVES = PARTICLES.register("mahogany_leaves", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WITCH_HAZEL_LEAVES = PARTICLES.register("witch_hazel_leaves", () -> new SimpleParticleType(true));

//    public static final DeferredHolder<SimpleParticleType> DOWSING_ROD_1 = PARTICLES.register("dowsing_rod_1", () -> new SimpleParticleType(true));
//    public static final DeferredHolder<SimpleParticleType> DOWSING_ROD_2 = PARTICLES.register("dowsing_rod_2", () -> new SimpleParticleType(true));

}