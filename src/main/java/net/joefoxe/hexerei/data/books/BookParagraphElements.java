package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class BookParagraphElements {
    public float x;
    public float y;
    public float height;
    public float width;

    BookParagraphElements(float x, float y, float height, float width){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public static BookParagraphElements deserialize(JsonObject object) {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float height = GsonHelper.getAsFloat(object, "height", 0);
        float width = GsonHelper.getAsFloat(object, "width", 0);
        return new BookParagraphElements(x, y, height, width);
    }


}
