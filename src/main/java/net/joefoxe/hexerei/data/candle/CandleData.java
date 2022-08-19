package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class CandleData {

    public int type;
    public boolean returnToBlock;
    public boolean hasCandle;
    public float x;
    public float y;
    public float z;
    public int height;
    public boolean lit;
    public int meltTimer;
    public static int meltTimerMAX = 6000;
    public int dyeColor;
    public ResourceLocation herbLayer;
    public ResourceLocation baseLayer;
    public ResourceLocation glowLayer;
    public ResourceLocation swirlLayer;

    public AbstractCandleEffect effect;
    public ArrayList<AbstractCandleEffect> effects;

    public CandleData(int dyeColor, boolean returnToBlock, float x, float y, float z, int height, int meltTimer, AbstractCandleEffect effect){
        this.dyeColor = dyeColor;
        this.returnToBlock = returnToBlock;
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
        this.meltTimer = meltTimer;
        this.effect = effect;
        this.effects = new ArrayList<>();
        this.effects.add(effect);

//        herbLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_herb_layer.png");
//        baseLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_base_layer.png");
//        glowLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_glow_layer.png");
//        swirlLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_swirl_layer.png");
    }

    public boolean hasBase(){
        return baseLayer != null;
    }

    public AbstractCandleEffect getEffect() {
        return effect;
    }

    public ArrayList<AbstractCandleEffect> getEffects() {
        return effects;
    }

    public CompoundTag save(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("dyeColor", this.dyeColor);
        tag.putInt("height", this.height);
        tag.putInt("meltTimer", this.meltTimer);
        tag.putBoolean("hasCandle", this.hasCandle);
        tag.putBoolean("lit", this.lit);
        tag.putBoolean("returnToBlock", this.returnToBlock);
        tag.putFloat("x", this.x);
        tag.putFloat("y", this.y);
        tag.putFloat("z", this.z);

        if(this.herbLayer != null)
            tag.putString("herbLayer", this.herbLayer.toString());
        if(this.baseLayer != null)
            tag.putString("baseLayer", this.baseLayer.toString());
        if(this.glowLayer != null)
            tag.putString("glowLayer", this.glowLayer.toString());
        if(this.swirlLayer != null)
            tag.putString("swirlLayer", this.swirlLayer.toString());

        return tag;
    }

    public void load(CompoundTag tag){
        this.dyeColor = tag.getInt("dyeColor");
        this.height = tag.getInt("height");
        this.meltTimer = tag.getInt("meltTimer");
        this.hasCandle = tag.getBoolean("hasCandle");
        this.lit = tag.getBoolean("lit");
        this.returnToBlock = tag.getBoolean("returnToBlock");
        this.x = tag.getFloat("x");
        this.y = tag.getFloat("y");
        this.z = tag.getFloat("z");

        if(tag.contains("herbLayer"))
            this.herbLayer = new ResourceLocation(tag.getString("herbLayer"));
        if(tag.contains("baseLayer"))
            this.baseLayer = new ResourceLocation(tag.getString("baseLayer"));
        if(tag.contains("glowLayer"))
            this.glowLayer = new ResourceLocation(tag.getString("glowLayer"));
        if(tag.contains("swirlLayer"))
            this.swirlLayer = new ResourceLocation(tag.getString("swirlLayer"));

    }


}
