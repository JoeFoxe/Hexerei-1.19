package net.joefoxe.hexerei.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by TGG on 25/03/2020.
 * <p>
 * The particle has two pieces of information which are used to customise it:
 * <p>
 * 1) The colour (tint) which is used to change the hue of the particle
 * 2) The diameter of the particle
 * <p>
 * This class is used to
 * 1) store this information, and
 * 2) transmit it between server and client (write and read methods), and
 * 3) parse it from a command string i.e. the /particle params
 */
public class CauldronParticleData extends ParticleType<CauldronParticleData> implements ParticleOptions {

    ParticleType<CauldronParticleData> type;
    FluidStack fluid;

    @SuppressWarnings("unchecked")
    public CauldronParticleData(FluidStack fluid){
        super(true);
        this.type = ModParticleTypes.CAULDRON.get();
        this.fluid = fluid;

    }
    @Nonnull
    @Override
    public ParticleType<CauldronParticleData> getType() {
        return type;
    }

    public static final MapCodec<CauldronParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    FluidStack.CODEC.fieldOf("fluid").forGetter(d -> d.fluid)
            )
            .apply(instance, CauldronParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronParticleData> STREAM_CODEC = StreamCodec.of(
            CauldronParticleData::toNetwork, CauldronParticleData::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, CauldronParticleData data) {
        FluidStack.STREAM_CODEC.encode(buf, data.fluid);
    }

    public static CauldronParticleData fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new CauldronParticleData(FluidStack.STREAM_CODEC.decode(buffer));
    }

    @Override
    public @NotNull MapCodec<CauldronParticleData> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, CauldronParticleData> streamCodec() {
        return STREAM_CODEC;
    }
}
