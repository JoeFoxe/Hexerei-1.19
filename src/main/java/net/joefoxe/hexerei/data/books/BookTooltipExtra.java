package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.GsonHelper;

public class BookTooltipExtra {
    public int color;
    public String color_hex;
    public String text;
    public String type;

    BookTooltipExtra(int color, String color_hex, String text, String type){
        this.color = color;
        this.color_hex = color_hex;
        this.text = text;
        this.type = type;
    }

    public static final Codec<BookTooltipExtra> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("color", 16777215).forGetter(e -> e.color),
            Codec.STRING.optionalFieldOf("color_hex", "").forGetter(e -> e.color_hex),
            Codec.STRING.optionalFieldOf("text", "append").forGetter(e -> e.text),
            Codec.STRING.optionalFieldOf("type", "empty").forGetter(e -> e.type) ).apply(instance, BookTooltipExtra::new));
}
