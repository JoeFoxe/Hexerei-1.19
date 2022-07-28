package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.GsonHelper;

public class BookHoverOffset {
    public float x;
    public float y;
    public float scale;

    BookHoverOffset(float x, float y, float scale){
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public static BookHoverOffset deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float scale = GsonHelper.getAsFloat(object, "scale", 1);

        return new BookHoverOffset(x, y, scale);
    }
}
