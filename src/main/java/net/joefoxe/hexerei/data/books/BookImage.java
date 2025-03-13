package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public class BookImage {
    public float x;
    public float y;
    public float z;
    public float u;
    public float v;
    public float width;
    public float height;
    public float imageWidth;
    public float imageHeight;
    public float scale;
    public String texture;
    public BookHyperlink hyperlink;
    public ArrayList<BookImageEffect> effects;
    List<Component> extra_tooltips = new ArrayList<>();
    List<BookTooltipExtra> extra_tooltips_raw;

    BookImage(float x, float y, float z, float u, float v, float width, float height, float imageWidth, float imageHeight, float scale, String texture, ArrayList<BookImageEffect> effects){
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.scale = scale;
        this.texture = texture;
        this.effects = effects;
        this.hyperlink= new BookHyperlink(-1, -1);
        this.extra_tooltips_raw = new ArrayList<>();
    }
    BookImage(float x, float y, float z, float u, float v, float width, float height, float imageWidth, float imageHeight, float scale, String texture,
              BookHyperlink hyperlink, List<BookTooltipExtra> extra_tooltips_raw, List<BookImageEffect> effects){
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.scale = scale;
        this.texture = texture;
        this.effects = new ArrayList<>(effects);
        this.hyperlink = hyperlink;
        this.extra_tooltips_raw = extra_tooltips_raw;

        for(BookTooltipExtra tooltipExtra : this.extra_tooltips_raw) {

            if (!tooltipExtra.color_hex.isEmpty())
                tooltipExtra.color = (int) Long.parseLong(tooltipExtra.color_hex, 16);
            if (tooltipExtra.type.equals("append")) {
                this.extra_tooltips.getLast().getSiblings().add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            } else {
                this.extra_tooltips.add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            }

        }
    }
    public static final Codec<BookImage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.FLOAT.optionalFieldOf("z", 0f).forGetter(e -> e.z),
            Codec.FLOAT.optionalFieldOf("u", 0f).forGetter(e -> e.u),
            Codec.FLOAT.optionalFieldOf("v", 0f).forGetter(e -> e.v),
            Codec.FLOAT.optionalFieldOf("width", 16f).forGetter(e -> e.width),
            Codec.FLOAT.optionalFieldOf("height", 16f).forGetter(e -> e.height),
            Codec.FLOAT.optionalFieldOf("imageWidth", 16f).forGetter(e -> e.imageWidth),
            Codec.FLOAT.optionalFieldOf("imageHeight", 16f).forGetter(e -> e.imageHeight),
            Codec.FLOAT.optionalFieldOf("scale", 0f).forGetter(e -> e.scale),
            Codec.STRING.optionalFieldOf("texture", "").forGetter(e -> e.texture),
            BookHyperlink.CODEC.optionalFieldOf("hyperlink", new BookHyperlink(-1, -1)).forGetter(e -> e.hyperlink),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("tooltip", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw),
            BookImageEffect.CODEC.listOf().optionalFieldOf("effects", new ArrayList<>()).forGetter(e -> e.effects)
    ).apply(instance, BookImage::new));

}
