package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;

import java.util.ArrayList;
import java.util.Optional;

public class BookEntity {
    public float x;
    public float y;
    public float rot;
    public float rotO;
    public float scale;
    public String entityType;
    public Entity entity;
    public CompoundTag entityTags;
    public ArrayList<CompoundTag> entityTagsList;
    public int entityTagsListOn;
    public int entityTagsLastChange;
    public int entityTagsListOnSet;
    public BookHoverOffset offset;
    public float toRotate;
    public float toRotateO;
    public boolean hovered;
    public boolean clicked;
    public float hoverTick;
    public float hoverTickO;
    public float hoverTickRender;
    public boolean markedForUpdate;

    BookEntity(float scale, float x, float y, String entityType, Entity entity, CompoundTag entityTags, ArrayList<CompoundTag> entityTagsList, BookHoverOffset offset){
        this.x = x;
        this.y = y;
        this.rot = 0;
        this.rotO = 0;
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
        this.toRotateO = 0;
        this.hoverTick = 0;
        this.hoverTickO = 0;
        this.hoverTickRender = 0;
        this.hovered = false;
        this.clicked = false;
        this.markedForUpdate = false;
    }
    public static float normalizeRadian(float angle) { float twoPi = (float)(2 * Math.PI); angle = angle % twoPi; if (angle < 0) { angle += twoPi; } return angle;}
    private float normalizeAngle(float angle) {
        while (angle > 90) {
            angle -= 360;
        }
        while (angle < -270) {
            angle += 360;
        }
        return angle;
    }

    public float getRot(float partial) {
        return HexereiUtil.lerpAngle(this.rotO, this.rot, partial);
    }

    public void tick() {
        this.rotO = this.rot;
        this.hoverTickO = this.hoverTick;

        if (this.toRotate != 0) {
            if (this.toRotate > 0) {
                this.rot += Math.max(Math.abs(this.toRotate) / 10f, 0.01f) / 3f;
            } else {
                this.rot -= Math.max(Math.abs(this.toRotate) / 10f, 0.01f) / 3f;
            }

            this.toRotate = HexereiUtil.moveTo(this.toRotate, 0, Math.max(Math.abs(this.toRotate) / 10f, 0.01f));
        }
        this.rot = normalizeAngle(this.rot);

        if (this.hovered || this.clicked)
            this.hoverTick = HexereiUtil.moveTo(this.hoverTick, 1, 0.06f);
        else
            this.hoverTick = HexereiUtil.moveTo(this.hoverTick, 0, 0.08f);
        this.hovered = false;
    }

    public static BookEntity deserialize(JsonObject object) throws CommandSyntaxException {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float scale = GsonHelper.getAsFloat(object, "scale", 1);
        String string = GsonHelper.getAsString(object, "id", "player");


        JsonObject hover_offset = GsonHelper.getAsJsonObject(object, "hover_offset", new JsonObject());
        float hover_x = GsonHelper.getAsFloat(hover_offset, "x", 0);
        float hover_y = GsonHelper.getAsFloat(hover_offset, "y", 0);
        float hover_scale = GsonHelper.getAsFloat(hover_offset, "scale", 1);

        BookHoverOffset hoverOffset = new BookHoverOffset(hover_x, hover_y, hover_scale);


        Entity entity = null;
        Optional<EntityType<?>> type = EntityType.byString(string);
        if(type.isPresent() && Hexerei.proxy.getLevel() != null) {
            entity = type.get().create(Hexerei.proxy.getLevel());
        }
        CompoundTag tag = new CompoundTag();

        if(object.has("tag")) {
            tag = TagParser.parseTag(GsonHelper.getAsString(object, "tag", "{}"));

            if(entity != null)
                entity.load(tag);
        }

        JsonArray tag_array = GsonHelper.getAsJsonArray(object, "tag_array", new JsonArray());
        ArrayList<CompoundTag> entityTagsList = new ArrayList<>();
        for(int i = 0; i < tag_array.size(); i++){
            JsonObject obj = tag_array.get(i).getAsJsonObject();

            entityTagsList.add(TagParser.parseTag(GsonHelper.getAsString(obj, "tag", "{}")));
        }

        return new BookEntity(scale, x, y, string, entity, tag, entityTagsList, hoverOffset);
    }
}
