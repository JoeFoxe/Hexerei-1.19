package net.joefoxe.hexerei.data.books;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookItemsAndFluids {
    public float x;
    public float y;
    public boolean show_slot;
    public ItemStack item;
    public FluidStack fluid;
    public float fluid_width;
    public float fluid_height;
    public float fluid_offset_x;
    public float fluid_offset_y;
    public int capacity;
    public int amount;
    public String tag;
    public boolean refreshTag = false;
    public String type;
    public TagKey<Item> key;
    public List<Component> extra_tooltips = new ArrayList<>();
    List<BookTooltipExtra> extra_tooltips_raw;
    public BakedModel modelCache = null;

    public BookItemsAndFluids(String type, Float x, Float y, FluidStack fluid, Integer capacity, Float fluid_height, Float fluid_width, Float fluid_offset_x, Float fluid_offset_y, Boolean showSlot, List<BookTooltipExtra> extra_tooltips_raw){

        this.type = type;
        this.x = x;
        this.y = y;
        this.fluid = fluid;
        this.item = null;
        this.tag = "null";
        this.key = null;
        this.capacity = capacity;
        this.amount = fluid.getAmount();
        this.show_slot = showSlot;
        this.fluid_height = fluid_height;
        this.fluid_width = fluid_width;
        this.fluid_offset_x = fluid_offset_x;
        this.fluid_offset_y = fluid_offset_y;
        this.extra_tooltips_raw = extra_tooltips_raw;

        for(BookTooltipExtra tooltipExtra : extra_tooltips_raw) {

            if (!tooltipExtra.color_hex.isEmpty())
                tooltipExtra.color = (int) Long.parseLong(tooltipExtra.color_hex, 16);
            if (tooltipExtra.type.equals("append")) {
                this.extra_tooltips.getLast().getSiblings().add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            } else {
                this.extra_tooltips.add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            }
        }
    }
    public BookItemsAndFluids(String type, float x, float y, ItemStack item, boolean show_slot, List<BookTooltipExtra> extra_tooltips) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.item = item;
        this.show_slot = show_slot;
        this.extra_tooltips_raw = extra_tooltips;

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
    public BookItemsAndFluids(String type, float x, float y, String tag, boolean show_slot, List<BookTooltipExtra> extra_tooltips) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.tag = tag;
        this.key = TagKey.create(Registries.ITEM, ResourceLocation.parse(tag));
        this.item = PageDrawing.getTagStack(this.key);
        this.show_slot = show_slot;
        this.extra_tooltips_raw = extra_tooltips;
        this.extra_tooltips.add(Component.translatable("book.hexerei.tooltip.accepts_any", Component.translatable(tag)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x808080))));

        for(BookTooltipExtra tooltipExtra : extra_tooltips) {

            if (!tooltipExtra.color_hex.isEmpty())
                tooltipExtra.color = (int) Long.parseLong(tooltipExtra.color_hex, 16);
            if (tooltipExtra.type.equals("append")) {
                this.extra_tooltips.getLast().getSiblings().add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            } else {
                this.extra_tooltips.add(Component.translatable(tooltipExtra.text).withStyle(Style.EMPTY.withColor(tooltipExtra.color)));
            }

        }
    }
    public static final Codec<BookItemsAndFluids> ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(e -> e.item),
            Codec.BOOL.optionalFieldOf("show_slot", true).forGetter(e -> e.show_slot),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("extra_tooltips", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookItemsAndFluids::new));

   public static final Codec<BookItemsAndFluids> FLUID_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(e -> e.fluid),
            Codec.INT.optionalFieldOf("capacity", 1000).forGetter(e -> e.capacity),
            Codec.FLOAT.optionalFieldOf("fluid_height", 16f).forGetter(e -> e.fluid_height),
            Codec.FLOAT.optionalFieldOf("fluid_width", 16f).forGetter(e -> e.fluid_width),
            Codec.FLOAT.optionalFieldOf("fluid_offset_x", 0f).forGetter(e -> e.fluid_offset_x),
            Codec.FLOAT.optionalFieldOf("fluid_offset_y", 0f).forGetter(e -> e.fluid_offset_y),
            Codec.BOOL.optionalFieldOf("show_slot", true).forGetter(e -> e.show_slot),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("extra_tooltips", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookItemsAndFluids::new));

    public static final Codec<BookItemsAndFluids> TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.STRING.optionalFieldOf("tag", "null").forGetter(e -> e.tag),
            Codec.BOOL.optionalFieldOf("show_slot", true).forGetter(e -> e.show_slot),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("extra_tooltips", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookItemsAndFluids::new));

    public static final Codec<BookItemsAndFluids> CODEC = Codec.of(
            BookItemsAndFluids::encode, BookItemsAndFluids::decode
    );

    public static <T > DataResult < T > encode(BookItemsAndFluids item, DynamicOps < T > ops, T prefix) {
        return switch (item.type) {
            case "item" -> ITEM_CODEC.encode(item, ops, prefix);
            case "fluid" -> FLUID_CODEC.encode(item, ops, prefix);
            case "tag" -> TAG_CODEC.encode(item, ops, prefix);
            default -> DataResult.error(() -> "Unknown type: " + item.type);
        };
    }

    public static <T > DataResult<Pair<BookItemsAndFluids, T>> decode(DynamicOps < T > ops, T input) {
        Optional<T> typeOpt = ops.get(input, "type").result();
        if (typeOpt.isEmpty()) {
            return DataResult.error(() -> "Missing type field");
        }
        Dynamic<T> typeDynamic = new Dynamic<>(ops, typeOpt.get());
        String type = typeDynamic.asString("");

        return switch (type) {
            case "item" -> ITEM_CODEC.decode(ops, input);
            case "fluid" -> FLUID_CODEC.decode(ops, input);
            case "tag" -> TAG_CODEC.decode(ops, input);
            default -> DataResult.error(() -> "Unknown type: " + type);
        };
    }
}
