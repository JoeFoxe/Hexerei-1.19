package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BookParagraphElements {
    public float x;
    public float y;
    public float height;
    public float width;
    public String verticalAlign;

    BookParagraphElements(float x, float y, float height, float width, String align){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.verticalAlign = align;
    }
    public static final Codec<BookParagraphElements> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.FLOAT.optionalFieldOf("height", 0f).forGetter(e -> e.height),
            Codec.FLOAT.optionalFieldOf("width", 0f).forGetter(e -> e.width),
            Codec.STRING.optionalFieldOf("verticalAlign", "top").forGetter(e -> e.verticalAlign) )
        .apply(instance, BookParagraphElements::new));


}
