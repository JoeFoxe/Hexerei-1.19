package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookBlocks {
    public float x;
    public float y;
    public BlockState blockState;
    public String type;
    public String tag;
    public boolean show_slot;
    public boolean refreshTag = false;
    public TagKey<Block> key;
    public List<Component> extra_tooltips = new ArrayList<>();
    List<BookTooltipExtra> extra_tooltips_raw;


    BookBlocks(String type, float x, float y, String tag_or_block, boolean show_slot, List<BookTooltipExtra> extra_tooltips_raw){
        if (type.equals("tag")) {
            this.blockState = Blocks.AIR.defaultBlockState();
            this.tag = tag_or_block;
            this.key = TagKey.create(Registries.BLOCK, ResourceLocation.parse(tag));
            if (BuiltInRegistries.BLOCK.getTag(key).isPresent()) {
                BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create()).ifPresentOrElse(
                        (blockHolder) -> this.blockState = blockHolder.value().defaultBlockState(),
                        () -> this.blockState = Blocks.AIR.defaultBlockState());
            }
        } else {
            Block block = BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(tag_or_block)) ? BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag_or_block)) : Blocks.AIR;
            this.blockState = block.defaultBlockState();
        }
        this.type = type;
        this.show_slot = show_slot;
        this.x = x;
        this.y = y;
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
    public static final Codec<BookBlocks> BLOCK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.STRING.fieldOf("id").forGetter(e -> BuiltInRegistries.BLOCK.getKey(e.blockState.getBlock()).toString()),
            Codec.BOOL.optionalFieldOf("show_slot", true).forGetter(e -> e.show_slot),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("extra_tooltips", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookBlocks::new));

    public static final Codec<BookBlocks> TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(e -> e.y),
            Codec.STRING.optionalFieldOf("id", "missing").forGetter(e -> e.tag),
            Codec.BOOL.optionalFieldOf("show_slot", true).forGetter(e -> e.show_slot),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("extra_tooltips", new ArrayList<>()).forGetter(e -> e.extra_tooltips_raw)
    ).apply(instance, BookBlocks::new));

    public static final Codec<BookBlocks> CODEC = Codec.of(
            BookBlocks::encode, BookBlocks::decode
    );

    public static <T > DataResult< T > encode(BookBlocks block, DynamicOps< T > ops, T prefix) {
        return switch (block.type) {
            case "block" -> BLOCK_CODEC.encode(block, ops, prefix);
            case "tag" -> TAG_CODEC.encode(block, ops, prefix);
            default -> DataResult.error(() -> "Unknown type: " + block.type);
        };
    }

    public static <T > DataResult<Pair<BookBlocks, T>> decode(DynamicOps < T > ops, T input) {
        Optional<T> typeOpt = ops.get(input, "type").result();
        if (typeOpt.isEmpty()) {
            return DataResult.error(() -> "Missing type field");
        }
        Dynamic<T> typeDynamic = new Dynamic<>(ops, typeOpt.get());
        String type = typeDynamic.asString("");

        return switch (type) {
            case "block" -> BLOCK_CODEC.decode(ops, input);
            case "tag" -> TAG_CODEC.decode(ops, input);
            default -> DataResult.error(() -> "Unknown type: " + type);
        };
    }

}
