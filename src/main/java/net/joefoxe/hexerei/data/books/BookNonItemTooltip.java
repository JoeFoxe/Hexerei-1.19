package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public class BookNonItemTooltip {

    public float x;
    public float y;
    public float height;
    public float width;
    public BookHyperlink hyperlink;
    public List<Component> tooltip;
    List<BookTooltipExtra> extra_tooltips_raw;

    BookNonItemTooltip(float x, float y, float width, float height, BookHyperlink hyperlink, List<BookTooltipExtra> extra_tooltips_raw){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.tooltip = new ArrayList<>();
        this.hyperlink = hyperlink;
        this.extra_tooltips_raw = extra_tooltips_raw;

        for(BookTooltipExtra tooltipExtra : this.extra_tooltips_raw) {

            if (!tooltipExtra.color_hex.isEmpty())
                tooltipExtra.color = (int) Long.parseLong(tooltipExtra.color_hex, 16);
            if (tooltipExtra.type.equals("append")) {
                this.tooltip.getLast().getSiblings().add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            } else {
                this.tooltip.add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            }

        }
    }
    public static final Codec<BookNonItemTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.FLOAT.optionalFieldOf("width", 16f).forGetter(e -> e.width),
            Codec.FLOAT.optionalFieldOf("height", 16f).forGetter(e -> e.height),
            BookHyperlink.CODEC.optionalFieldOf("hyperlink", new BookHyperlink(-1, -1)).forGetter(e -> e.hyperlink),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("tooltip", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookNonItemTooltip::new));
}
