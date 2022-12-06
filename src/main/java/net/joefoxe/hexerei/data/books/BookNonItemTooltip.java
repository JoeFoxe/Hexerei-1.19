package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BookNonItemTooltip {

    public float x;
    public float y;
    public float height;
    public float width;
    public int hyperlink_chapter;
    public int hyperlink_page;
    public String hyperlink_url;
    public java.util.List<net.minecraft.network.chat.Component> textComponentsList;
    List<BookTooltipExtra> extra_tooltips_raw;

    BookNonItemTooltip(float x, float y, float width, float height, int hyperlink_chapter, int hyperlink_page, java.util.List<net.minecraft.network.chat.Component> textComponentsList, String hyperlink_url, List<BookTooltipExtra> extra_tooltips_raw){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.textComponentsList = textComponentsList;
        this.hyperlink_chapter = hyperlink_chapter;
        this.hyperlink_page = hyperlink_page;
        this.hyperlink_url = hyperlink_url;
        this.extra_tooltips_raw = extra_tooltips_raw;
    }

    public static BookNonItemTooltip deserialize(JsonObject object) {

        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float width = GsonHelper.getAsFloat(object, "width", 0);
        float height = GsonHelper.getAsFloat(object, "height", 0);
        JsonObject hyperlink = GsonHelper.getAsJsonObject(object, "hyperlink", new JsonObject());
        int chapter = GsonHelper.getAsInt(hyperlink, "chapter", -1);
        int page = GsonHelper.getAsInt(hyperlink, "page", -1);
        String url = GsonHelper.getAsString(hyperlink, "url", "");



        JsonArray yourJson = GsonHelper.getAsJsonArray(object,"text_components");
        java.util.List<Component> textComponentsList = new ArrayList<>();
        List<BookTooltipExtra> bookTooltipExtraList = new ArrayList<>();

        MutableComponent component = Component.translatable("");

        for (int i = 0; i < yourJson.size(); i++) {
            JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
            int color = GsonHelper.getAsInt(extraItemObject, "color", 16777215);
            String string = GsonHelper.getAsString(extraItemObject, "text", "empty");
            String type = GsonHelper.getAsString(extraItemObject, "type", "append");
            String hex_color = GsonHelper.getAsString(extraItemObject, "color_hex", "");
            if(!hex_color.equals(""))
                color = (int)Long.parseLong(hex_color, 16);

            if(type.equals("trail")) {
                textComponentsList.add(component);

                component = Component.translatable(string).withStyle(Style.EMPTY.withColor(color));
            }
            else if(type.equals("append")){
                component.getSiblings().add(Component.translatable(string).withStyle(Style.EMPTY.withColor(color)));

            }

            if(!(i+1 < yourJson.size())){
                if(!component.getString().equals(""))
                    textComponentsList.add(component);
            }
            bookTooltipExtraList.add(new BookTooltipExtra(color, hex_color, string, type));
        }



        return new BookNonItemTooltip(x, y, width, height, chapter, page, textComponentsList, url, bookTooltipExtraList);
    }
}
