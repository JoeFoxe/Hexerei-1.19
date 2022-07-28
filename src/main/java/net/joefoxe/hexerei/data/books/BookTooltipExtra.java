package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

    public static BookTooltipExtra deserialize(JsonObject object) throws CommandSyntaxException {
        int color = GsonHelper.getAsInt(object, "color", 16777215);
        String color_hex = GsonHelper.getAsString(object, "color_hex", "none");
        String type = GsonHelper.getAsString(object, "type", "append");
        String text = GsonHelper.getAsString(object, "text", "empty");

        return new BookTooltipExtra(color, color_hex, type, text);
    }
}
