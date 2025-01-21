package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PotionBottleTypeData(PotionFluid.BottleType bottleType) {

    public static final PotionBottleTypeData EMPTY = new PotionBottleTypeData(PotionFluid.BottleType.REGULAR);

    public static final Codec<PotionBottleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            PotionFluid.BottleType.CODEC.fieldOf("Bottle").forGetter(PotionBottleTypeData::bottleType)
//            Codec.INT.xmap(PotionFluid.BottleType::byId, PotionFluid.BottleType::ordinal).fieldOf("Bottle").forGetter(PotionBottleTypeData::bottleType)
        ).apply(instance, PotionBottleTypeData::new)
    );

    public static StreamCodec<ByteBuf, PotionBottleTypeData> STREAM_CODEC = ByteBufCodecs.fromCodec(PotionBottleTypeData.CODEC);
}