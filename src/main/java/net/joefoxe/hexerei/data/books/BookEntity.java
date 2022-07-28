package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;

import java.util.ArrayList;
import java.util.Optional;

public class BookEntity {
    public float x;
    public float y;
    public float rot;
    public float scale;
    public String entityType;
    public Entity entity;
    public String entityTags;
    public ArrayList<String> entityTagsList;
    public int entityTagsListOn;
    public int entityTagsLastChange;
    public int entityTagsListOnSet;
    public BookHoverOffset offset;
    public float toRotate;
    public float hoverTick;

    BookEntity(float scale, float x, float y, String entityType, Entity entity, String entityTags, ArrayList<String> entityTagsList, BookHoverOffset offset){
        this.x = x;
        this.y = y;
        this.rot = 0;
        this.scale = scale;
        this.entityType = entityType;
        this.entityTags = entityTags;
        this.entityTagsList = entityTagsList;
        this.entityTagsListOn = 0;
        this.entityTagsListOnSet = 0;
        this.entityTagsLastChange = 0;
        this.entity = entity;
        this.offset = offset;
        this.toRotate = 0;
        this.hoverTick = 0;
    }

    public static BookEntity deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float scale = GsonHelper.getAsFloat(object, "scale", 1);
        String string = GsonHelper.getAsString(object, "name", "player");


        JsonObject hover_offset = GsonHelper.getAsJsonObject(object, "hover_offset", new JsonObject());
        float hover_x = GsonHelper.getAsFloat(hover_offset, "x", 0);
        float hover_y = GsonHelper.getAsFloat(hover_offset, "y", 0);
        float hover_scale = GsonHelper.getAsFloat(hover_offset, "scale", 1);

        BookHoverOffset hoverOffset = new BookHoverOffset(hover_x, hover_y, hover_scale);


        Entity entity = null;
        Optional<EntityType<?>> type = EntityType.byString(string);
        if(EntityType.byString(string).isPresent() && Hexerei.proxy.getLevel() != null) {
            entity = EntityType.byString(string).get().create(Hexerei.proxy.getLevel());
        }
        String tagString = "";

        if(object.has("tag"))
        {
            tagString = GsonHelper.getAsString(object, "tag", "");

            CompoundTag tag = TagParser.parseTag(tagString);

            if(entity != null)
                entity.load(tag);

            if(entity instanceof TamableAnimal)
                ((TamableAnimal)entity).setTame(true);

        }

        JsonArray tag_array = GsonHelper.getAsJsonArray(object, "tag_array", new JsonArray());
        ArrayList<String> entityTagsList = new ArrayList<>();
        for(int i = 0; i < tag_array.size(); i++){
            JsonObject obj = tag_array.get(i).getAsJsonObject();
            entityTagsList.add(GsonHelper.getAsString(obj, "tag", ""));
        }

        return new BookEntity(scale, x, y, string, entity, tagString, entityTagsList, hoverOffset);
    }
}
