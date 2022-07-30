package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

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
    List<Component> extra_tooltips;
    List<BookTooltipExtra> extra_tooltips_raw;

    BookItemsAndFluids(float x, float y, ItemStack item){
        this.x = x;
        this.y = y;
        this.item = item;
        this.fluid = null;
        this.tag = "null";
        this.type = "item";
        this.key = null;
        this.show_slot = true;
    }
    BookItemsAndFluids(float x, float y, ItemStack item, boolean show_slot){
        this.x = x;
        this.y = y;
        this.item = item;
        this.fluid = null;
        this.tag = "null";
        this.type = "item";
        this.key = null;
        this.show_slot = show_slot;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
        this.extra_tooltips = new ArrayList<>();
        this.extra_tooltips_raw = new ArrayList<>();
    }
    BookItemsAndFluids(float x, float y, ItemStack item, boolean show_slot, List<Component> extra_tooltips, List<BookTooltipExtra> extra_tooltips_raw){
        this.x = x;
        this.y = y;
        this.item = item;
        this.fluid = null;
        this.tag = "null";
        this.type = "item";
        this.key = null;
        this.show_slot = show_slot;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
        this.extra_tooltips = extra_tooltips;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }
    BookItemsAndFluids(float x, float y, FluidStack fluid){
        this.x = x;
        this.y = y;
        this.fluid = fluid;
        this.item = null;
        this.tag = "null";
        this.type = "fluid";
        this.key = null;
        this.capacity = 0;
        this.show_slot = false;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
    }
    BookItemsAndFluids(float x, float y, FluidStack fluid, int capacity){
        this.x = x;
        this.y = y;
        this.fluid = fluid;
        this.item = null;
        this.tag = "null";
        this.type = "fluid";
        this.key = null;
        this.capacity = capacity;
        this.show_slot = false;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
    }

    BookItemsAndFluids(float x, float y, FluidStack fluid, int amount, int capacity, boolean showSlot, float fluid_height, float fluid_width, float fluid_offset_x, float fluid_offset_y, List<Component> extra_tooltips, List<BookTooltipExtra> extra_tooltips_raw){

        this.x = x;
        this.y = y;
        this.fluid = fluid;
        this.item = null;
        this.tag = "null";
        this.type = "fluid";
        this.key = null;
        this.capacity = capacity;
        this.amount = amount;
        this.show_slot = showSlot;
        this.fluid_height = fluid_height;
        this.fluid_width = fluid_width;
        this.fluid_offset_x = fluid_offset_x;
        this.fluid_offset_y = fluid_offset_y;
        this.extra_tooltips = extra_tooltips;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }
    BookItemsAndFluids(float x, float y, FluidStack fluid, int amount, int capacity, boolean showSlot, float fluid_height, float fluid_width, float fluid_offset_x, float fluid_offset_y){
        this.x = x;
        this.y = y;
        this.fluid = fluid;
        this.item = null;
        this.tag = "null";
        this.type = "fluid";
        this.key = null;
        this.capacity = capacity;
        this.amount = amount;
        this.show_slot = showSlot;
        this.fluid_height = fluid_height;
        this.fluid_width = fluid_width;
        this.fluid_offset_x = fluid_offset_x;
        this.fluid_offset_y = fluid_offset_y;
    }
    BookItemsAndFluids(float x, float y, String tag){
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tag));
        this.item = new ItemStack(PageDrawing.getTagStackStatic(this.key).orElseGet(ItemStack.EMPTY::getItem));
        this.type = "tag";
        this.show_slot = true;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
    }
    BookItemsAndFluids(float x, float y, String tag, boolean showSlot){
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tag));
        this.item = new ItemStack(PageDrawing.getTagStackStatic(this.key).orElseGet(ItemStack.EMPTY::getItem));
        this.type = "tag";
        this.show_slot = showSlot;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
    }
    BookItemsAndFluids(float x, float y, String tag, boolean showSlot, List<Component> extra_tooltips, List<BookTooltipExtra> tooltipExtras){
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tag));
        this.item = new ItemStack(PageDrawing.getTagStackStatic(this.key).orElseGet(ItemStack.EMPTY::getItem));
        this.type = "tag";
        this.show_slot = showSlot;
        this.fluid_height = 16;
        this.fluid_width = 16;
        this.fluid_offset_x = 0;
        this.fluid_offset_y = 0;
        List<Component> extra_tooltips2 = new ArrayList<>();
        extra_tooltips2.add(Component.translatable("book.hexerei.tooltip.accepts_any", new TextComponent(tag)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x808080))));
        this.extra_tooltips = extra_tooltips2;
        this.extra_tooltips.addAll(extra_tooltips);
        this.extra_tooltips_raw = tooltipExtras;
    }

    public static BookItemsAndFluids deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        boolean show_slot = GsonHelper.getAsBoolean(object, "show_slot", true);
        String type = GsonHelper.getAsString(object, "type", "item");
        switch (type) {
            case "item": {
                Item item = GsonHelper.getAsItem(object, "name", Items.AIR);
                int count = GsonHelper.getAsInt(object, "count", 1);

                ItemStack stack = new ItemStack(item, count);
                if (object.has("tag")) {
                    String tagString = GsonHelper.getAsString(object, "tag", "");
                    CompoundTag tag = TagParser.parseTag(tagString);

                    stack.setTag(tag);
                }



                JsonArray yourJson = GsonHelper.getAsJsonArray(object,"extra_tooltips", new JsonArray());
                java.util.List<net.minecraft.network.chat.Component> textComponentsList = new ArrayList<>();

                net.minecraft.network.chat.Component component = Component.translatable("");
                List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

                for (int i = 0; i < yourJson.size(); i++) {
                    JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
                    int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
                    String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
                    String string_type = GsonHelper.getAsString(extraItemObject, "type", "append");
                    String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");

                    if(!hex_color.equals(""))
                        color = (int)Long.parseLong(hex_color, 16);

                    if(string_type.equals("trail")) {
                        textComponentsList.add(component);

                        component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
                    }
                    else if(string_type.equals("append")){
                        component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

                    }

                    if(!(i+1 < yourJson.size())){
                        if(!component.getString().equals(""))
                            textComponentsList.add(component);
                    }
                    bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, string_type));
                }

                return new BookItemsAndFluids(x, y, stack, show_slot, textComponentsList, bookTooltipExtraList);
            }
            case "tag": {

                JsonArray yourJson = GsonHelper.getAsJsonArray(object, "extra_tooltips", new JsonArray());
                java.util.List<net.minecraft.network.chat.Component> textComponentsList = new ArrayList<>();
                List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

                net.minecraft.network.chat.Component component = Component.translatable("");

                for (int i = 0; i < yourJson.size(); i++) {
                    JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
                    int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
                    String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
                    String string_type = GsonHelper.getAsString(extraItemObject, "type", "append");
                    String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");
                    if (!hex_color.equals(""))
                        color = (int) Long.parseLong(hex_color, 16);

                    if (string_type.equals("trail")) {
                        textComponentsList.add(component);

                        component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
                    } else if (string_type.equals("append")) {
                        component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

                    }

                    if (!(i + 1 < yourJson.size())) {
                        if (!component.getString().equals(""))
                            textComponentsList.add(component);
                    }
                    bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, string_type));
                }

                return new BookItemsAndFluids(x, y, GsonHelper.getAsString(object, "name", "null"), show_slot, textComponentsList, bookTooltipExtraList);
            }
            case "fluid": {
                String loc = GsonHelper.getAsString(object, "name", "minecraft:water");

                float fluid_height = GsonHelper.getAsFloat(object, "fluid_height", 16);
                float fluid_width = GsonHelper.getAsFloat(object, "fluid_width", 16);
                float fluid_offset_x = GsonHelper.getAsFloat(object, "fluid_offset_x", 0);
                float fluid_offset_y = GsonHelper.getAsFloat(object, "fluid_offset_y", 0);

                Fluid fluid = (Fluid) Registry.FLUID.getOptional(new ResourceLocation(loc)).orElseThrow(() -> {
                    return new JsonSyntaxException("Expected " + loc + " to be an item, was unknown string '" + loc + "'");
                });

                int amount = GsonHelper.getAsInt(object, "amount", 0);
                int capacity = GsonHelper.getAsInt(object, "capacity", 0);


                FluidStack fluidStack = new FluidStack(fluid, amount <= 0 ? 1000 : amount);
                if (object.has("tag")) {
                    String tagString = GsonHelper.getAsString(object, "tag", "");
                    CompoundTag tag = TagParser.parseTag(tagString);

                    fluidStack.setTag(tag);
                }

                JsonArray yourJson = GsonHelper.getAsJsonArray(object, "extra_tooltips", new JsonArray());
                java.util.List<net.minecraft.network.chat.Component> textComponentsList = new ArrayList<>();

                net.minecraft.network.chat.Component component = Component.translatable("");
                List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

                for (int i = 0; i < yourJson.size(); i++) {
                    JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
                    int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
                    String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
                    String string_type = GsonHelper.getAsString(extraItemObject, "type", "append");
                    String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");
                    if (!hex_color.equals(""))
                        color = (int) Long.parseLong(hex_color, 16);

                    if (string_type.equals("trail")) {
                        textComponentsList.add(component);

                        component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
                    } else if (string_type.equals("append")) {
                        component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

                    }

                    if (!(i + 1 < yourJson.size())) {
                        if (!component.getString().equals(""))
                            textComponentsList.add(component);
                    }

                    bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, string_type));
                }

                return new BookItemsAndFluids(x, y, fluidStack, amount, capacity, show_slot, fluid_height, fluid_width, fluid_offset_x, fluid_offset_y, textComponentsList, bookTooltipExtraList);
            }
            default:

                return new BookItemsAndFluids(x, y, ItemStack.EMPTY, show_slot);
        }
    }
}
