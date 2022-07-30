package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.GsonHelper;

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
    public String imageLoc;
    public int hyperlink_chapter;
    public int hyperlink_page;
    public String hyperlink_url;
    public ArrayList<BookImageEffect> effects;
    List<Component> extra_tooltips;
    List<BookTooltipExtra> extra_tooltips_raw;

    BookImage(float x, float y, float z, float u, float v, float width, float height, float imageWidth, float imageHeight, float scale, String imageLoc, ArrayList<BookImageEffect> effects){
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
        this.imageLoc = imageLoc;
        this.effects = effects;
        this.hyperlink_chapter = -1;
        this.hyperlink_page = -1;
        this.hyperlink_url = "";
    }
    BookImage(float x, float y, float z, float u, float v, float width, float height, float imageWidth, float imageHeight, float scale, String imageLoc, ArrayList<BookImageEffect> effects, int hyperlink_chapter, int hyperlink_page, String hyperlink_url, List<Component> extra_tooltips, List<BookTooltipExtra> extra_tooltips_raw){
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
        this.imageLoc = imageLoc;
        this.effects = effects;
        this.hyperlink_chapter = hyperlink_chapter;
        this.hyperlink_page = hyperlink_page;
        this.hyperlink_url = hyperlink_url;
        this.extra_tooltips = extra_tooltips;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }

    public static CompoundTag serialize(BookImage bookImage) {
        CompoundTag compound = new CompoundTag();
        compound.putString("image_texture", bookImage.imageLoc);
        compound.putFloat("image_x", bookImage.x);
        compound.putFloat("image_y", bookImage.y);
        compound.putFloat("image_z", bookImage.z);
        compound.putFloat("image_u", bookImage.u);
        compound.putFloat("image_v", bookImage.v);
        compound.putFloat("image_width", bookImage.width);
        compound.putFloat("image_height", bookImage.height);
        compound.putFloat("image_imageWidth", bookImage.imageWidth);
        compound.putFloat("image_imageHeight", bookImage.imageHeight);
        compound.putFloat("image_scale", bookImage.scale);
        compound.putInt("image_hyperlink_chapter", bookImage.hyperlink_chapter);
        compound.putInt("image_hyperlink_page", bookImage.hyperlink_page);
        compound.putString("image_hyperlink_url", bookImage.hyperlink_url);

        compound.putInt("image_number_of_extra_tooltips", bookImage.extra_tooltips_raw.size());
        for (int k = 0; k < bookImage.extra_tooltips_raw.size(); k++) {
            compound.putInt("image_extra_tooltips_color" + k, bookImage.extra_tooltips_raw.get(k).color);
            compound.putString("image_extra_tooltips_color_hex" + k, bookImage.extra_tooltips_raw.get(k).color_hex);
            compound.putString("image_extra_tooltips_text" + k, bookImage.extra_tooltips_raw.get(k).text);
            compound.putString("image_extra_tooltips_type" + k, bookImage.extra_tooltips_raw.get(k).type);
        }
        compound.putInt("image_numberOfImageEffects", bookImage.effects.size());
        for (int k = 0; k < bookImage.effects.size(); k++) {
            BookImageEffect effect = bookImage.effects.get(k);
            compound.putString("image_type" + k, effect.type);
            compound.putFloat("image_speed" + k, effect.speed);
            compound.putFloat("image_amount" + k, effect.amount);
            compound.put("image_hoverImage" + k, serialize(effect.hoverImage));
        }
        return compound;
    }
    public static BookImage deserialize(CompoundTag image) {
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


        int num = image.getInt("image_numberOfImageEffects");
        ArrayList<BookImageEffect> effectList = new ArrayList<>();
        for(int j = 0; j < num; j++) {
            String type = image.getString("image_type" + j);
            float speed = image.getFloat("image_speed" + j);
            float amount = image.getFloat("image_amount" + j);
            CompoundTag compoundTag = image.getCompound("image_hoverImage" + j);
            BookImage bookImage = deserialize(compoundTag);
            BookImageEffect bookImageEffect = new BookImageEffect(type, speed, amount, bookImage);
            effectList.add(bookImageEffect);
        }
        return new BookImage(x, y, z, u, v, width, height, imageWidth, imageHeight, scale, texture, effectList, hyperlink_chapter, hyperlink_page, hyperlink_url, extra_tooltips, extra_tooltips_raw);
    }

    public static BookImage deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float z = GsonHelper.getAsFloat(object, "z", 0);
        float u = GsonHelper.getAsFloat(object, "u", 0);
        float v = GsonHelper.getAsFloat(object, "v", 0);
        float width = GsonHelper.getAsFloat(object, "width", 18);
        float height = GsonHelper.getAsFloat(object, "height", 18);
        float imageWidth = GsonHelper.getAsFloat(object, "imageWidth", 18);
        float imageHeight = GsonHelper.getAsFloat(object, "imageHeight", 18);
        float scale = GsonHelper.getAsFloat(object, "scale", 1);
        JsonObject hyperlink = GsonHelper.getAsJsonObject(object, "hyperlink", new JsonObject());
        int chapter = GsonHelper.getAsInt(hyperlink, "chapter", -1);
        int page = GsonHelper.getAsInt(hyperlink, "page", -1);
        String url = GsonHelper.getAsString(hyperlink, "url", "");

        JsonArray yourJson = GsonHelper.getAsJsonArray(object,"effects", new JsonArray());
        ArrayList<BookImageEffect> effectList = new ArrayList<>();
        for (int i = 0; i < yourJson.size(); i++) {
            JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
            effectList.add(BookImageEffect.deserialize(extraItemObject));
        }
        String itemLoc = GsonHelper.getAsString(object, "texture", "");

        JsonArray textComp = GsonHelper.getAsJsonArray(object,"tooltip", new JsonArray());
        java.util.List<net.minecraft.network.chat.Component> textComponentsList = new ArrayList<>();
        List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

        MutableComponent component = Component.translatable("");

        for (int i = 0; i < textComp.size(); i++) {
            JsonObject extraItemObject = textComp.get(i).getAsJsonObject();
            int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
            String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
            String type = GsonHelper.getAsString(extraItemObject, "type", "append");
            String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");
            if(!hex_color.equals(""))
                color = (int)Long.parseLong(hex_color, 16);

            if(type.equals("trail")) {
                textComponentsList.add(component);
                component = (Component.translatable(string)).withStyle(Style.EMPTY.withColor(color));
            }
            else if(type.equals("append")){
                component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));
            }

            if(!(i + 1 < textComp.size())){
                if(!component.getString().equals(""))
                    textComponentsList.add(component);
            }
            bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, type));
        }

        return new BookImage(x, y, z, u, v, width, height, imageWidth, imageHeight, scale, itemLoc, effectList, chapter, page, url, textComponentsList, bookTooltipExtraList);
    }
}
