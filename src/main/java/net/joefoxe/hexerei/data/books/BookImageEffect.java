package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.GsonHelper;

public class BookImageEffect {
    public BookImage hoverImage;
    public String type;
    public float speed;
    public float amount;

    BookImageEffect(String type, float speed, float amount, BookImage hoverImage){
        this.type = type;
        this.speed = speed;
        this.amount = amount;
        this.hoverImage = hoverImage;
    }
    BookImageEffect(String type, float speed, float amount){
        this.type = type;
        this.speed = speed;
        this.amount = amount;
        this.hoverImage = null;
    }

    public static BookImageEffect deserialize(JsonObject object) throws CommandSyntaxException {
        String type = GsonHelper.getAsString(object, "type", "none");
        float speed = GsonHelper.getAsFloat(object, "speed", 0);
        float amount = GsonHelper.getAsFloat(object, "amount", 0);
        BookImage hoverImage = BookImage.deserialize(GsonHelper.getAsJsonObject(object, "image", new JsonObject()));

//        if(object.has("tag"))
//        {
//            String tagString = GsonHelper.getAsString(object, "tag", "");
//            CompoundTag tag = TagParser.parseTag(tagString);
//
//            stack.setTag(tag);
//        }

        return new BookImageEffect(type, speed, amount, hoverImage);
    }
}
