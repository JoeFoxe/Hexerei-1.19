package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
//                             corners      cover
public record BookColorData(int color1, int color2) {
    public static int DEFAULT_1 = 0xC19343;
    public static int DEFAULT_2 = 0xA85062;

    public static final BookColorData EMPTY = new BookColorData(DEFAULT_1, DEFAULT_2);
    public static final BookColorData EMPTY_NOTEBOOK = new BookColorData(0x6E7982, 0xF0F0F0);
    public static final BookColorData EMPTY_COLORS = new BookColorData(0x6E7982, 0x00A595);

    public static final Codec<BookColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.fieldOf("color1").forGetter(BookColorData::color1),
                            Codec.INT.fieldOf("color2").forGetter(BookColorData::color2)
                    ).apply(instance, BookColorData::new)
    );

    public static StreamCodec<ByteBuf, BookColorData> STREAM_CODEC = ByteBufCodecs.fromCodec(BookColorData.CODEC);
}