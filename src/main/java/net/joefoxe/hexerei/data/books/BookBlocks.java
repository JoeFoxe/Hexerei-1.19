package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class BookBlocks {
    public float x;
    public float y;
    public BlockState blockState;
    public String type;
    public String tag;
    public boolean show_slot;
    public boolean refreshTag = false;
    public TagKey<Block> key;
    public List<Component> extra_tooltips;
    List<BookTooltipExtra> extra_tooltips_raw;

    BookBlocks(float x, float y, BlockState blockState, boolean show_slot){
        this.x = x;
        this.y = y;
        this.tag = "null";
        this.type = "block";
        this.key = null;
        this.blockState = blockState;
        this.show_slot = show_slot;
        this.extra_tooltips = new ArrayList<>();
        this.extra_tooltips_raw = new ArrayList<>();
    }

    BookBlocks(float x, float y, BlockState blockState, boolean show_slot, List<Component> extra_tooltips, List<BookTooltipExtra> extra_tooltips_raw){
        this.x = x;
        this.y = y;
        this.tag = "null";
        this.type = "block";
        this.key = null;
        this.blockState = blockState;
        this.show_slot = show_slot;
        this.extra_tooltips = extra_tooltips;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }

    BookBlocks(float x, float y, String tag, boolean show_slot){
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.type = "tag";
        this.blockState = Blocks.AIR.defaultBlockState();
        this.show_slot = show_slot;
        this.key = TagKey.create(Registries.BLOCK, ResourceLocation.parse(tag));
        if (BuiltInRegistries.BLOCK.getTag(key).isPresent()){
            BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create()).ifPresentOrElse(
                    (blockHolder) -> this.blockState = blockHolder.value().defaultBlockState(),
                    () -> this.blockState = Blocks.AIR.defaultBlockState());
        }
        this.extra_tooltips = new ArrayList<>();
        this.extra_tooltips_raw = new ArrayList<>();
    }

    BookBlocks(float x, float y, String tag, boolean show_slot, List<Component> extra_tooltips, List<BookTooltipExtra> extra_tooltips_raw){
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.type = "tag";
        this.show_slot = show_slot;
        this.blockState = Blocks.AIR.defaultBlockState();
        this.key = TagKey.create(Registries.BLOCK, ResourceLocation.parse(tag));
        if (BuiltInRegistries.BLOCK.getTag(key).isPresent()){
            BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create()).ifPresentOrElse(
                    (blockHolder) -> this.blockState = blockHolder.value().defaultBlockState(),
                    () -> this.blockState = Blocks.AIR.defaultBlockState());
        }
        this.extra_tooltips = extra_tooltips;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }


    public static BookBlocks deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        String type = GsonHelper.getAsString(object, "type", "block");
        boolean show_slot = GsonHelper.getAsBoolean(object, "show_slot", false);
        switch (type) {
            case "block" -> {
                Block block = BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(GsonHelper.getAsString(object, "id"))) ? BuiltInRegistries.BLOCK.get(ResourceLocation.parse(GsonHelper.getAsString(object, "id"))) : Blocks.AIR;

                BlockState state = block.defaultBlockState();
//                if (object.has("tag")) {
//                    String tagString = GsonHelper.getAsString(object, "tag", "");
//                    CompoundTag tag = TagParser.parseTag(tagString);
//
//                    stack.setTag(tag);
//                }


                JsonArray yourJson = GsonHelper.getAsJsonArray(object, "extra_tooltips", new JsonArray());
                List<Component> textComponentsList = new ArrayList<>();

                Component component = Component.translatable("");
                List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

                for (int i = 0; i < yourJson.size(); i++) {
                    JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
                    int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
                    String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
                    String string_type = GsonHelper.getAsString(extraItemObject, "type", "append");
                    String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");

                    if (!hex_color.isEmpty())
                        color = (int) Long.parseLong(hex_color, 16);

                    if (string_type.equals("trail")) {
                        textComponentsList.add(component);

                        component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
                    } else if (string_type.equals("append")) {
                        component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

                    }

                    if (!(i + 1 < yourJson.size())) {
                        if (!component.getString().isEmpty())
                            textComponentsList.add(component);
                    }
                    bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, string_type));
                }

                return new BookBlocks(x, y, state, show_slot, textComponentsList, bookTooltipExtraList);
            }
            case "tag" -> {

                JsonArray yourJson = GsonHelper.getAsJsonArray(object, "extra_tooltips", new JsonArray());
                List<Component> textComponentsList = new ArrayList<>();
                List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

                Component component = Component.translatable("");

                for (int i = 0; i < yourJson.size(); i++) {
                    JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
                    int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
                    String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
                    String string_type = GsonHelper.getAsString(extraItemObject, "type", "append");
                    String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");
                    if (!hex_color.isEmpty())
                        color = (int) Long.parseLong(hex_color, 16);

                    if (string_type.equals("trail")) {
                        textComponentsList.add(component);

                        component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
                    } else if (string_type.equals("append")) {
                        component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

                    }

                    if (!(i + 1 < yourJson.size())) {
                        if (!component.getString().isEmpty())
                            textComponentsList.add(component);
                    }
                    bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, string_type));
                }

                return new BookBlocks(x, y, GsonHelper.getAsString(object, "id", "null"), show_slot, textComponentsList, bookTooltipExtraList);
            }
            default -> {
                return new BookBlocks(x, y, Blocks.AIR.defaultBlockState(), show_slot);
            }
        }
    }
}
