package net.joefoxe.hexerei.data.books;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class BookPage {
    public String showTitle;

    public String itemHyperlink;
    public ArrayList<BookParagraph> paragraph;
    public ArrayList<BookItemsAndFluids> itemList;
    public ArrayList<BookBlocks> blockList;
    public ArrayList<BookEntity> entityList;
    public ArrayList<BookImage> imageList;
    public ArrayList<BookNonItemTooltip> nonItemTooltipList;


    public BookPage(String showTitle, ArrayList<BookParagraph> paragraph, ArrayList<BookItemsAndFluids> itemList, ArrayList<BookBlocks> blockList, ArrayList<BookEntity> entityList, ArrayList<BookImage> imageList, ArrayList<BookNonItemTooltip> nonItemTooltipList, String itemHyperlink) {
        this.showTitle = showTitle;
        this.itemHyperlink = itemHyperlink;
        this.paragraph = paragraph;
        this.itemList = itemList;
        this.entityList = entityList;
        this.imageList = imageList;
        this.blockList = blockList;
        this.nonItemTooltipList = nonItemTooltipList;
    }

    public static CompoundTag saveToTag(BookPage bookPage) {
        CompoundTag tag = new CompoundTag();
        tag.putString("showTitle", bookPage.showTitle);
        tag.putString("item_hyperlink", bookPage.itemHyperlink);
        tag.putInt("numberOfPassages", bookPage.paragraph.size());
        for(int i = 0; i < bookPage.paragraph.size(); i++) {
            BookParagraph bookParagraph = bookPage.paragraph.get(i);
            CompoundTag compound = new CompoundTag();
            compound.putString("paragraph_text" + i,bookPage.paragraph.get(i).passage);
            compound.putString("align" + i,bookPage.paragraph.get(i).align);
            int num = bookParagraph.paragraphElements.size();
            compound.putInt("numberOfBoxes" + i, num);
            for(int k = 0; k < num; k++){

                BookParagraphElements bookParagraphElements = bookParagraph.paragraphElements.get(k);
                CompoundTag compoundBoxes = new CompoundTag();
                compoundBoxes.putFloat("box_x" + i + k, bookParagraphElements.x);
                compoundBoxes.putFloat("box_y" + i + k, bookParagraphElements.y);
                compoundBoxes.putFloat("box_height" + i + k, bookParagraphElements.height);
                compoundBoxes.putFloat("box_width" + i + k, bookParagraphElements.width);
                compoundBoxes.putString("box_verticalAlign" + i + k, bookParagraphElements.verticalAlign);
                compound.put("paragraph_box" + i + k, compoundBoxes);

            }
            tag.put("paragraph" + i, compound);
        }


        tag.putInt("numberOfItems", bookPage.itemList.size());
        for(int i = 0; i < bookPage.itemList.size(); i++) {
            CompoundTag compound = new CompoundTag();
            String type = (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).type);
            compound.putString("item_type" + i, type);

            switch (type) {
                case "fluid" -> {
                    //(x, y, fluidStack, amount, capacity, show_slot, fluid_height, fluid_width, fluid_offset_x, fluid_offset_y, textComponentsList)
                    compound.putFloat("item_x" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).x));
                    compound.putFloat("item_y" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).y));
                    compound.put("item_fluid" + i, ((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).fluid.save(Hexerei.DynamicRegistries.get(), new CompoundTag()));
                    compound.putFloat("item_capacity" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).capacity));
                    compound.putFloat("item_amount" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).amount));
                    compound.putFloat("item_fluid_height" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).fluid_height));
                    compound.putFloat("item_fluid_width" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).fluid_width));
                    compound.putFloat("item_fluid_offset_x" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).fluid_offset_x));
                    compound.putFloat("item_fluid_offset_y" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).fluid_offset_y));
                    compound.putString("item_tag" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).tag));
                    compound.putBoolean("item_show_slot" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).show_slot));
//                    float fluid_height = itemsAndFluids.getFloat("item_fluid_height" + i);
//                    float fluid_width = itemsAndFluids.getFloat("item_fluid_width" + i);
//                    float fluid_offset_x = itemsAndFluids.getFloat("item_fluid_offset_x" + i);
//                    float fluid_offset_y = itemsAndFluids.getFloat("item_fluid_offset_y" + i);


                    List<BookTooltipExtra> extra_tooltips_raw = (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).extra_tooltips_raw);
                    compound.putInt("item_number_of_extra_tooltips" + i, extra_tooltips_raw.size());
                    for (int k = 0; k < extra_tooltips_raw.size(); k++) {
                        compound.putInt("item_extra_tooltips_color" + i + k, extra_tooltips_raw.get(k).color);
                        compound.putString("item_extra_tooltips_color_hex" + i + k, extra_tooltips_raw.get(k).color_hex);
                        compound.putString("item_extra_tooltips_text" + i + k, extra_tooltips_raw.get(k).text);
                        compound.putString("item_extra_tooltips_type" + i + k, extra_tooltips_raw.get(k).type);
                    }
                    tag.put("items_and_fluids" + i, compound);
                }
                case "item" -> {
                    //(float x, float y, ItemStack item, boolean show_slot, List<Component> extra_tooltips)
                    if (!(bookPage.itemList.get(i).item).isEmpty()) {
//                        CompoundTag tag1 = new CompoundTag();
                        ItemStack stack = bookPage.itemList.get(i).item;
                        CompoundTag tag1 = (CompoundTag) stack.save(Hexerei.DynamicRegistries.get(), new CompoundTag());
                        compound.put("item" + i, tag1);
                    }

                    compound.putFloat("item_x" + i, (bookPage.itemList.get(i).x));
                    compound.putFloat("item_y" + i, (bookPage.itemList.get(i).y));
                    compound.putString("item_tag" + i, (bookPage.itemList.get(i).tag));
                    compound.putBoolean("item_show_slot" + i, (bookPage.itemList.get(i).show_slot));

                    List<BookTooltipExtra> extra_tooltips_raw = (bookPage.itemList.get(i).extra_tooltips_raw);
                    compound.putInt("item_number_of_extra_tooltips" + i, extra_tooltips_raw.size());
                    for (int k = 0; k < extra_tooltips_raw.size(); k++) {
                        compound.putInt("item_extra_tooltips_color" + i + k, extra_tooltips_raw.get(k).color);
                        compound.putString("item_extra_tooltips_color_hex" + i + k, extra_tooltips_raw.get(k).color_hex);
                        compound.putString("item_extra_tooltips_text" + i + k, extra_tooltips_raw.get(k).text);
                        compound.putString("item_extra_tooltips_type" + i + k, extra_tooltips_raw.get(k).type);
                    }

                    tag.put("items_and_fluids" + i, compound);
                }
                case "tag" -> {
                    //(float x, float y, ItemStack item, boolean show_slot, List<Component> extra_tooltips)
                    if (!(((BookItemsAndFluids) bookPage.itemList.toArray()[i]).item).isEmpty()) {
                        CompoundTag tag1 = new CompoundTag();
                        (((BookItemsAndFluids) bookPage.itemList.toArray()[i]).item).save(Hexerei.DynamicRegistries.get(), tag1);
                        compound.put("item" + i, tag1);
                    }

                    compound.putFloat("item_x" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).x));
                    compound.putFloat("item_y" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).y));
                    compound.putBoolean("item_show_slot" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).show_slot));
                    compound.putString("item_tag" + i, (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).tag));

                    List<BookTooltipExtra> extra_tooltips_raw = (((BookItemsAndFluids) (bookPage.itemList.toArray()[i])).extra_tooltips_raw);
                    compound.putInt("item_number_of_extra_tooltips" + i, extra_tooltips_raw.size());
                    for (int k = 1; k < extra_tooltips_raw.size(); k++) {
                        compound.putInt("item_extra_tooltips_color" + i + k, extra_tooltips_raw.get(k).color);
                        compound.putString("item_extra_tooltips_color_hex" + i + k, extra_tooltips_raw.get(k).color_hex);
                        compound.putString("item_extra_tooltips_text" + i + k, extra_tooltips_raw.get(k).text);
                        compound.putString("item_extra_tooltips_type" + i + k, extra_tooltips_raw.get(k).type);
                    }

                    tag.put("items_and_fluids" + i, compound);
                }
            }
        }


        tag.putInt("numberOfBlocks", bookPage.blockList.size());
        for(int i = 0; i < bookPage.blockList.size(); i++) {
            CompoundTag compound = new CompoundTag();
            String type = (((BookBlocks) (bookPage.blockList.toArray()[i])).type);
            compound.putString("block_type" + i, type);

            switch (type) {
                case "block" -> {
                    //(float x, float y, ItemStack block, boolean show_slot, List<Component> extra_tooltips)

                    BlockState state = (((BookBlocks) bookPage.blockList.toArray()[i]).blockState);

                    compound.putString("block", state.getBlock().getDescriptionId());
                    CompoundTag properties = new CompoundTag();
                    state.getValues().forEach((property, value) -> properties.putString(property.getName(), value.toString()));
                    compound.put("block_properties", properties);


                    compound.putFloat("block_x" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).x));
                    compound.putFloat("block_y" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).y));
                    compound.putString("block_tag" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).tag));
                    compound.putBoolean("block_show_slot" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).show_slot));

                    List<BookTooltipExtra> extra_tooltips_raw = (((BookBlocks) (bookPage.blockList.toArray()[i])).extra_tooltips_raw);
                    compound.putInt("block_number_of_extra_tooltips" + i, extra_tooltips_raw.size());
                    for (int k = 0; k < extra_tooltips_raw.size(); k++) {
                        compound.putInt("block_extra_tooltips_color" + i + k, extra_tooltips_raw.get(k).color);
                        compound.putString("block_extra_tooltips_color_hex" + i + k, extra_tooltips_raw.get(k).color_hex);
                        compound.putString("block_extra_tooltips_text" + i + k, extra_tooltips_raw.get(k).text);
                        compound.putString("block_extra_tooltips_type" + i + k, extra_tooltips_raw.get(k).type);
                    }

                    tag.put("blocks" + i, compound);
                }
                case "tag" -> {
                    //(float x, float y, ItemStack block, boolean show_slot, List<Component> extra_tooltips)

                    BlockState state = (((BookBlocks) bookPage.blockList.toArray()[i]).blockState);

                    compound.putString("block", state.getBlock().getDescriptionId());
                    CompoundTag properties = new CompoundTag();
                    state.getValues().forEach((property, value) -> properties.putString(property.getName(), value.toString()));
                    compound.put("block_properties", properties);


                    compound.putFloat("block_x" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).x));
                    compound.putFloat("block_y" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).y));
                    compound.putString("block_tag" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).tag));
                    compound.putBoolean("block_show_slot" + i, (((BookBlocks) (bookPage.blockList.toArray()[i])).show_slot));

                    List<BookTooltipExtra> extra_tooltips_raw = (((BookBlocks) (bookPage.blockList.toArray()[i])).extra_tooltips_raw);
                    compound.putInt("block_number_of_extra_tooltips" + i, extra_tooltips_raw.size());
                    for (int k = 1; k < extra_tooltips_raw.size(); k++) {
                        compound.putInt("block_extra_tooltips_color" + i + k, extra_tooltips_raw.get(k).color);
                        compound.putString("block_extra_tooltips_color_hex" + i + k, extra_tooltips_raw.get(k).color_hex);
                        compound.putString("block_extra_tooltips_text" + i + k, extra_tooltips_raw.get(k).text);
                        compound.putString("block_extra_tooltips_type" + i + k, extra_tooltips_raw.get(k).type);
                    }

                    tag.put("blocks" + i, compound);
                }
            }
        }
        tag.putInt("numberOfImages", bookPage.imageList.size());
        for(int i = 0; i < bookPage.imageList.size(); i++) {
            CompoundTag compound = new CompoundTag();
            BookImage bookImage = (BookImage) (bookPage.imageList.toArray()[i]);
            String texture = bookImage.imageLoc;
            float x = bookImage.x;
            float y = bookImage.y;
            float z = bookImage.z;
            float u = bookImage.u;
            float v = bookImage.v;
            float width = bookImage.width;
            float height = bookImage.height;
            float imageWidth = bookImage.imageWidth;
            float imageHeight = bookImage.imageHeight;
            float scale = bookImage.scale;
            int hyperlink_chapter = bookImage.hyperlink_chapter;
            int hyperlink_page = bookImage.hyperlink_page;
            String hyperlink_url = bookImage.hyperlink_url;
            String hyperlink_id = bookImage.hyperlink_id;
//            num = tag.getInt("image_numberOfImageEffects" + i);
            compound.putString("image_texture", texture);
            compound.putFloat("image_x", x);
            compound.putFloat("image_y", y);
            compound.putFloat("image_z", z);
            compound.putFloat("image_u", u);
            compound.putFloat("image_v", v);
            compound.putFloat("image_width", width);
            compound.putFloat("image_height", height);
            compound.putFloat("image_imageWidth", imageWidth);
            compound.putFloat("image_imageHeight", imageHeight);
            compound.putFloat("image_scale", scale);
            compound.putInt("image_hyperlink_chapter", hyperlink_chapter);
            compound.putInt("image_hyperlink_page", hyperlink_page);
            compound.putString("image_hyperlink_url", hyperlink_url);
            compound.putString("image_hyperlink_id", hyperlink_id);

            List<BookTooltipExtra> extra_tooltips_raw = bookImage.extra_tooltips_raw;
            compound.putInt("image_number_of_extra_tooltips", extra_tooltips_raw.size());
            for (int k = 0; k < extra_tooltips_raw.size(); k++) {
                compound.putInt("image_extra_tooltips_color" + k, extra_tooltips_raw.get(k).color);
                compound.putString("image_extra_tooltips_color_hex" + k, extra_tooltips_raw.get(k).color_hex);
                compound.putString("image_extra_tooltips_text" + k, extra_tooltips_raw.get(k).text);
                compound.putString("image_extra_tooltips_type" + k, extra_tooltips_raw.get(k).type);
            }
            List<BookImageEffect> bookImageEffects = bookImage.effects;
            compound.putInt("image_numberOfImageEffects", bookImageEffects.size());
            for (int k = 0; k < bookImageEffects.size(); k++) {
                BookImageEffect effect = bookImageEffects.get(k);
                compound.putString("image_type" + k, effect.type);
                compound.putFloat("image_speed" + k, effect.speed);
                compound.putFloat("image_amount" + k, effect.amount);
                compound.put("image_hoverImage" + k, BookImage.serialize(effect.hoverImage));
            }

            tag.put("images" + i, compound);

        }


        tag.putInt("numberOfNonItemTooltipList", bookPage.nonItemTooltipList.size());
        for(int i = 0; i < bookPage.nonItemTooltipList.size(); i++) {
            CompoundTag compound = new CompoundTag();
            BookNonItemTooltip nonItemTooltip = (BookNonItemTooltip) (bookPage.nonItemTooltipList.toArray()[i]);

            compound.putFloat("x", nonItemTooltip.x);
            compound.putFloat("y", nonItemTooltip.y);
            compound.putFloat("width", nonItemTooltip.width);
            compound.putFloat("height", nonItemTooltip.height);
            compound.putInt("hyperlink_chapter", nonItemTooltip.hyperlink_chapter);
            compound.putInt("hyperlink_page", nonItemTooltip.hyperlink_page);
            compound.putString("hyperlink_url", nonItemTooltip.hyperlink_url);
            compound.putString("hyperlink_id", nonItemTooltip.hyperlink_id);


            List<BookTooltipExtra> extra_tooltips_raw = nonItemTooltip.extra_tooltips_raw;
            compound.putInt("number_of_tooltips", extra_tooltips_raw.size());
            for (int k = 0; k < extra_tooltips_raw.size(); k++) {
                compound.putInt("extra_tooltips_color" + k, extra_tooltips_raw.get(k).color);
                compound.putString("extra_tooltips_color_hex" + k, extra_tooltips_raw.get(k).color_hex);
                compound.putString("extra_tooltips_text" + k, extra_tooltips_raw.get(k).text);
                compound.putString("extra_tooltips_type" + k, extra_tooltips_raw.get(k).type);
            }

            tag.put("nonItemTooltipList" + i, compound);
        }




        tag.putInt("numberOfEntities", bookPage.entityList.size());
        for(int i = 0; i < bookPage.entityList.size(); i++) {
            CompoundTag compound = new CompoundTag();
            BookEntity bookEntity = ((BookEntity) (bookPage.entityList.toArray()[i]));

            compound.putString("entity", bookEntity.entityType);
            compound.putFloat("x", (bookEntity.x));
            compound.putFloat("y", (bookEntity.y));
            compound.putFloat("scale", (bookEntity.scale));
            compound.put("entityTags", bookEntity.entityTags);
            compound.putInt("entityTagsListSize", bookEntity.entityTagsList.size());
            for(int k = 0; k < bookEntity.entityTagsList.size(); k++)
                compound.put("entityTagsList" + k, bookEntity.entityTagsList.get(k));
            tag.put("entities" + i, compound);
        }


        return tag;
    }

    public static BookPage loadFromTag(CompoundTag tag) {
        String show_title = tag.getString("showTitle");
        String item_hyperlink = tag.getString("item_hyperlink");
        int size = tag.getInt("numberOfPassages");
        ArrayList<BookParagraph> list = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            CompoundTag paragraph = tag.getCompound("paragraph" + i);
            String string = paragraph.getString("paragraph_text" + i);
            String align = paragraph.getString("align" + i);

            int num = paragraph.getInt("numberOfBoxes" + i);
            ArrayList<BookParagraphElements> boxList = new ArrayList<>();
            for(int k = 0; k < num; k++){

                CompoundTag boxes = paragraph.getCompound("paragraph_box" + i + k);
                float x = boxes.getFloat("box_x" + i + k);
                float y = boxes.getFloat("box_y" + i + k);
                float height = boxes.getFloat("box_height" + i + k);
                float width = boxes.getFloat("box_width" + i + k);
                String verticalAlign = boxes.getString("box_verticalAlign" + i + k);

                BookParagraphElements bookParagraphElements = new BookParagraphElements(x, y, height, width, verticalAlign);
                boxList.add(bookParagraphElements);
            }

            BookParagraph bookParagraph = new BookParagraph(boxList, string, align);
            list.add(bookParagraph);
        }

        int num = tag.getInt("numberOfItems");
        ArrayList<BookItemsAndFluids> itemList = new ArrayList<>();
        for(int i = 0; i < num; i++)
        {
            CompoundTag itemsAndFluids = tag.getCompound("items_and_fluids" + i);

            String type = itemsAndFluids.getString("item_type" + i);
            switch (type) {
                case "item" -> {
                    //(float x, float y, ItemStack item, boolean show_slot, List<Component> extra_tooltips)
                    float x = itemsAndFluids.getFloat("item_x" + i);
                    float y = itemsAndFluids.getFloat("item_y" + i);
                    boolean show_slot = itemsAndFluids.getBoolean("item_show_slot" + i);
                    List<Component> extra_tooltips = new ArrayList<>();
                    int item_number_of_extra_tooltips = itemsAndFluids.getInt("item_number_of_extra_tooltips" + i);

                    Component component = Component.translatable("");
                    List<BookTooltipExtra> tooltipExtras = new ArrayList<>();
                    for (int k = 0; k < item_number_of_extra_tooltips; k++) {
                        int color = itemsAndFluids.getInt("item_extra_tooltips_color" + i + k);
                        String color_hex = itemsAndFluids.getString("item_extra_tooltips_color_hex" + i + k);
                        String text = itemsAndFluids.getString("item_extra_tooltips_text" + i + k);
                        String text_type = itemsAndFluids.getString("item_extra_tooltips_type" + i + k);
                        tooltipExtras.add(new BookTooltipExtra(color,color_hex, text, text_type));

                        if (!color_hex.equals(""))
                            color = (int) Long.parseLong(color_hex, 16);

                        if (text_type.equals("trail")) {
                            extra_tooltips.add(component);

                            component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                        } else if (text_type.equals("append")) {
                            component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                        }

                        if (!(k + 1 < item_number_of_extra_tooltips)) {
                            if (!component.getString().equals(""))
                                extra_tooltips.add(component);
                        }

                    }

                    ItemStack stack = itemsAndFluids.contains("item" + i) ? ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), itemsAndFluids.getCompound("item" + i)) : ItemStack.EMPTY.copy();
                    BookItemsAndFluids bookItemStackInSlot = new BookItemsAndFluids(x, y, stack, show_slot, extra_tooltips, tooltipExtras);
                    itemList.add(bookItemStackInSlot);
                }
                case "fluid" -> {

                    //float x, float y, FluidStack fluid, int amount, int capacity, boolean showSlot, float fluid_height, float fluid_width, float fluid_offset_x, float fluid_offset_y, List<Component> extra_tooltips
                    float x = itemsAndFluids.getFloat("item_x" + i);
                    float y = itemsAndFluids.getFloat("item_y" + i);
                    float fluid_height = itemsAndFluids.getFloat("item_fluid_height" + i);
                    float fluid_width = itemsAndFluids.getFloat("item_fluid_width" + i);
                    float fluid_offset_x = itemsAndFluids.getFloat("item_fluid_offset_x" + i);
                    float fluid_offset_y = itemsAndFluids.getFloat("item_fluid_offset_y" + i);
                    int amount = itemsAndFluids.getInt("item_amount" + i);
                    int capacity = itemsAndFluids.getInt("item_capacity" + i);
                    boolean show_slot = itemsAndFluids.getBoolean("item_show_slot" + i);
                    int item_number_of_extra_tooltips = itemsAndFluids.getInt("item_number_of_extra_tooltips" + i);
                    CompoundTag compoundFluid = itemsAndFluids.getCompound("item_fluid" + i);
                    FluidStack fluid = FluidStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundFluid);
                    List<Component> extra_tooltips = new ArrayList<>();

                    Component component = Component.translatable("");
                    List<BookTooltipExtra> tooltipExtras = new ArrayList<>();
                    for (int k = 0; k < item_number_of_extra_tooltips; k++) {
                        int color = itemsAndFluids.getInt("item_extra_tooltips_color" + i + k);
                        String color_hex = itemsAndFluids.getString("item_extra_tooltips_color_hex" + i + k);
                        String text = itemsAndFluids.getString("item_extra_tooltips_text" + i + k);
                        String text_type = itemsAndFluids.getString("item_extra_tooltips_type" + i + k);
                        tooltipExtras.add(new BookTooltipExtra(color,color_hex, text, text_type));

                        if (!color_hex.equals(""))
                            color = (int) Long.parseLong(color_hex, 16);

                        if (text_type.equals("trail")) {
                            extra_tooltips.add(component);

                            component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                        } else if (text_type.equals("append")) {
                            component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                        }

                        if (!(k + 1 < item_number_of_extra_tooltips)) {
                            if (!component.getString().equals(""))
                                extra_tooltips.add(component);
                        }
                    }

                    BookItemsAndFluids bookItemStackInSlot = new BookItemsAndFluids(x, y, fluid, amount, capacity, show_slot, fluid_height, fluid_width, fluid_offset_x, fluid_offset_y, extra_tooltips, tooltipExtras);
                    itemList.add(bookItemStackInSlot);
                }
                case "tag" -> {
                    //(float x, float y, String tag, boolean showSlot, List<Component> extra_tooltips)
                    float x = itemsAndFluids.getFloat("item_x" + i);
                    float y = itemsAndFluids.getFloat("item_y" + i);
                    String item_tag = itemsAndFluids.getString("item_tag" + i);
                    boolean show_slot = itemsAndFluids.getBoolean("item_show_slot" + i);
                    List<Component> extra_tooltips = new ArrayList<>();
                    int item_number_of_extra_tooltips = itemsAndFluids.getInt("item_number_of_extra_tooltips" + i);

                    Component component = Component.translatable("");
                    List<BookTooltipExtra> tooltipExtras = new ArrayList<>();
                    for (int k = 1; k < item_number_of_extra_tooltips; k++) {
                        int color = itemsAndFluids.getInt("item_extra_tooltips_color" + i + k);
                        String color_hex = itemsAndFluids.getString("item_extra_tooltips_color_hex" + i + k);
                        String text = itemsAndFluids.getString("item_extra_tooltips_text" + i + k);
                        String text_type = itemsAndFluids.getString("item_extra_tooltips_type" + i + k);
                        tooltipExtras.add(new BookTooltipExtra(color,color_hex, text, text_type));

                        if (!color_hex.equals(""))
                            color = (int) Long.parseLong(color_hex, 16);

                        if (text_type.equals("trail")) {
                            extra_tooltips.add(component);

                            component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                        } else if (text_type.equals("append")) {
                            component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                        }

                        if (!(k + 1 < item_number_of_extra_tooltips)) {
                            if (!component.getString().isEmpty())
                                extra_tooltips.add(component);
                        }

                    }
                    BookItemsAndFluids bookItemStackInSlot = new BookItemsAndFluids(x, y, item_tag, show_slot, extra_tooltips, tooltipExtras);
                    itemList.add(bookItemStackInSlot);
                }
            }
        }
        int numberOfBlocks = tag.getInt("numberOfBlocks");
        ArrayList<BookBlocks> blockList = new ArrayList<>();
        for(int i = 0; i < numberOfBlocks; i++)
        {
            CompoundTag blocks = tag.getCompound("blocks" + i);

            String type = blocks.getString("block_type" + i);
            switch (type) {
                case "block" -> {
                    //(float x, float y, ItemStack item, boolean show_slot, List<Component> extra_tooltips)

                    Block block = Blocks.AIR;
                    if (BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(tag.getString("Block")))) {
                        block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("Block")));
                    }

                    BlockState state = block.defaultBlockState();
                    CompoundTag properties = tag.getCompound("Properties");
                    StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
                    for (String key : properties.getAllKeys()) {
                        Property<?> property = stateDefinition.getProperty(key);
                        if (property != null) {
                            state = setValueHelper(state, property, properties.getString(key));
                        }
                    }


                    float x = blocks.getFloat("block_x" + i);
                    float y = blocks.getFloat("block_y" + i);
                    boolean show_slot = blocks.getBoolean("block_show_slot" + i);
                    List<Component> extra_tooltips = new ArrayList<>();
                    int block_number_of_extra_tooltips = blocks.getInt("block_number_of_extra_tooltips" + i);

                    Component component = Component.translatable("");
                    List<BookTooltipExtra> tooltipExtras = new ArrayList<>();
                    for (int k = 0; k < block_number_of_extra_tooltips; k++) {
                        int color = blocks.getInt("block_extra_tooltips_color" + i + k);
                        String color_hex = blocks.getString("block_extra_tooltips_color_hex" + i + k);
                        String text = blocks.getString("block_extra_tooltips_text" + i + k);
                        String text_type = blocks.getString("block_extra_tooltips_type" + i + k);
                        tooltipExtras.add(new BookTooltipExtra(color,color_hex, text, text_type));

                        if (!color_hex.equals(""))
                            color = (int) Long.parseLong(color_hex, 16);

                        if (text_type.equals("trail")) {
                            extra_tooltips.add(component);

                            component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                        } else if (text_type.equals("append")) {
                            component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                        }

                        if (!(k + 1 < block_number_of_extra_tooltips)) {
                            if (!component.getString().equals(""))
                                extra_tooltips.add(component);
                        }

                    }
                    BookBlocks bookBlocks = new BookBlocks(x, y, state, show_slot, extra_tooltips, tooltipExtras);
                    blockList.add(bookBlocks);
                }
                case "tag" -> {
                    //(float x, float y, String tag, boolean showSlot, List<Component> extra_tooltips)
                    float x = blocks.getFloat("block_x" + i);
                    float y = blocks.getFloat("block_y" + i);
                    boolean show_slot = blocks.getBoolean("block_show_slot" + i);
                    String block_tag = blocks.getString("block_tag" + i);
                    List<Component> extra_tooltips = new ArrayList<>();
                    int block_number_of_extra_tooltips = blocks.getInt("block_number_of_extra_tooltips" + i);

                    Component component = Component.translatable("");
                    List<BookTooltipExtra> tooltipExtras = new ArrayList<>();
                    for (int k = 1; k < block_number_of_extra_tooltips; k++) {
                        int color = blocks.getInt("block_extra_tooltips_color" + i + k);
                        String color_hex = blocks.getString("block_extra_tooltips_color_hex" + i + k);
                        String text = blocks.getString("block_extra_tooltips_text" + i + k);
                        String text_type = blocks.getString("block_extra_tooltips_type" + i + k);
                        tooltipExtras.add(new BookTooltipExtra(color,color_hex, text, text_type));

                        if (!color_hex.equals(""))
                            color = (int) Long.parseLong(color_hex, 16);

                        if (text_type.equals("trail")) {
                            extra_tooltips.add(component);

                            component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                        } else if (text_type.equals("append")) {
                            component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                        }

                        if (!(k + 1 < block_number_of_extra_tooltips)) {
                            if (!component.getString().equals(""))
                                extra_tooltips.add(component);
                        }

                    }
                    BookBlocks bookBlock = new BookBlocks(x, y, block_tag, show_slot, extra_tooltips, tooltipExtras);
                    blockList.add(bookBlock);
                }
            }
        }
        int numberOfImages = tag.getInt("numberOfImages");
        ArrayList<BookImage> imageList = new ArrayList<>();
        for(int i = 0; i < numberOfImages; i++)
        {
            CompoundTag image = tag.getCompound("images" + i);
            String texture = image.getString("image_texture");

            float x = image.getFloat("image_x");
            float y = image.getFloat("image_y");
            float z = image.getFloat("image_z");

            float u = image.getFloat("image_u");
            float v = image.getFloat("image_v");

            float width = image.getFloat("image_width");
            float height = image.getFloat("image_height");

            float imageWidth = image.getFloat("image_imageWidth");
            float imageHeight = image.getFloat("image_imageHeight");

            float scale = image.getFloat("image_scale");

            int hyperlink_chapter = image.getInt("image_hyperlink_chapter");
            int hyperlink_page = image.getInt("image_hyperlink_page");
            String hyperlink_url = image.getString("image_hyperlink_url");
            String hyperlink_id = image.getString("image_hyperlink_id");

            List<Component> extra_tooltips = new ArrayList<>();
            int item_number_of_extra_tooltips = image.getInt("image_number_of_extra_tooltips");

            Component component = Component.translatable("");
            List<BookTooltipExtra> extra_tooltips_raw = new ArrayList<>();
            for (int k = 0; k < item_number_of_extra_tooltips; k++) {
                int color = image.getInt("image_extra_tooltips_color" + k);
                String color_hex = image.getString("image_extra_tooltips_color_hex" + k);
                String text = image.getString("image_extra_tooltips_text" + k);
                String text_type = image.getString("image_extra_tooltips_type" + k);
                extra_tooltips_raw.add(new BookTooltipExtra(color,color_hex, text, text_type));

                if (!color_hex.equals(""))
                    color = (int) Long.parseLong(color_hex, 16);

                if (text_type.equals("trail")) {
                    extra_tooltips.add(component);

                    component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                } else if (text_type.equals("append")) {
                    component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                }

                if (!(k + 1 < item_number_of_extra_tooltips)) {
                    if (!component.getString().equals(""))
                        extra_tooltips.add(component);
                }
            }

            int image_numberOfImageEffects = image.getInt("image_numberOfImageEffects");
            ArrayList<BookImageEffect> effectList = new ArrayList<>();
            for(int j = 0; j < image_numberOfImageEffects; j++) {
                String type = image.getString("image_type" + j);
                float speed = image.getFloat("image_speed" + j);
                float amount = image.getFloat("image_amount" + j);
                CompoundTag compoundTag = image.getCompound("image_hoverImage" + j);
                BookImage bookImage = BookImage.deserialize(compoundTag);
                BookImageEffect bookImageEffect = new BookImageEffect(type, speed, amount, bookImage);
                effectList.add(bookImageEffect);
            }
            BookImage bookImage = new BookImage(x, y, z, u, v, width, height, imageWidth, imageHeight, scale, texture, effectList, hyperlink_chapter, hyperlink_page, hyperlink_url, hyperlink_id, extra_tooltips, extra_tooltips_raw);
            imageList.add(bookImage);
        }

        int numberOfEntities = tag.getInt("numberOfEntities");
        ArrayList<BookEntity> entityList = new ArrayList<>();
        for(int i = 0; i < numberOfEntities; i++)
        {
            CompoundTag entity = tag.getCompound("entities" + i);

            String stringEntity = entity.getString("entity");
            float x = entity.getFloat("x");
            float y = entity.getFloat("y");
            float scale = entity.getFloat("scale");
            CompoundTag entityTags = entity.getCompound("entityTags");
            int entityTagsListSize = entity.getInt("entityTagsListSize");
            ArrayList<CompoundTag> tagList = new ArrayList<>();
            for(int k = 0; k < entityTagsListSize; k++)
                tagList.add(entity.getCompound("entityTagsList" + k));

            BookEntity bookEntity = new BookEntity(scale, x, y, stringEntity, null, entityTags, tagList, new BookHoverOffset(0,0,1));
            entityList.add(bookEntity);
        }

        int numberOfNonItemTooltipList = tag.getInt("numberOfNonItemTooltipList");
        ArrayList<BookNonItemTooltip> nonItemTooltipList = new ArrayList<>();
        for(int i = 0; i < numberOfNonItemTooltipList; i++)
        {
            CompoundTag nonItemTooltip = tag.getCompound("nonItemTooltipList" + i);

            float x = nonItemTooltip.getFloat("x");
            float y = nonItemTooltip.getFloat("y");
            float width = nonItemTooltip.getFloat("width");
            float height = nonItemTooltip.getFloat("height");
            int hyperlink_chapter = nonItemTooltip.getInt("hyperlink_chapter");
            int hyperlink_page = nonItemTooltip.getInt("hyperlink_page");
            String hyperlink_url = nonItemTooltip.getString("hyperlink_url");
            String hyperlink_id = nonItemTooltip.getString("hyperlink_id");

            List<Component> extra_tooltips = new ArrayList<>();
            int number_of_tooltips = nonItemTooltip.getInt("number_of_tooltips");

            Component component = Component.translatable("");
            List<BookTooltipExtra> extra_tooltips_raw = new ArrayList<>();
            for (int k = 0; k < number_of_tooltips; k++) {
                int color = nonItemTooltip.getInt("extra_tooltips_color" + k);
                String color_hex = nonItemTooltip.getString("extra_tooltips_color_hex" + k);
                String text = nonItemTooltip.getString("extra_tooltips_text" + k);
                String text_type = nonItemTooltip.getString("extra_tooltips_type" + k);
                extra_tooltips_raw.add(new BookTooltipExtra(color,color_hex, text, text_type));

                if (!color_hex.equals(""))
                    color = (int) Long.parseLong(color_hex, 16);

                if (text_type.equals("trail")) {
                    extra_tooltips.add(component);

                    component = Component.translatable(text).withStyle(Style.EMPTY.withColor(color));
                } else if (text_type.equals("append")) {
                    component.getSiblings().add(Component.translatable(text).withStyle(Style.EMPTY.withColor(color)));

                }

                if (!(k + 1 < number_of_tooltips)) {
                    if (!component.getString().equals(""))
                        extra_tooltips.add(component);
                }
            }

            BookNonItemTooltip bookNonItemTooltip = new BookNonItemTooltip(x, y, width, height, hyperlink_chapter, hyperlink_page, extra_tooltips, hyperlink_url, hyperlink_id, extra_tooltips_raw);
            nonItemTooltipList.add(bookNonItemTooltip);
        }

        return new BookPage(show_title, list, itemList, blockList, entityList, imageList, nonItemTooltipList, item_hyperlink);
    }
    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
