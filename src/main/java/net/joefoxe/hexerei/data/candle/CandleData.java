package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CandleData {

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
    public int cooldown;
    public CandleLayer base;
    public CandleLayer herb;
    public CandleLayer glow;
    public CandleLayer swirl;

    public AbstractCandleEffect effect;
    public ArrayList<AbstractCandleEffect> effects;
    public Component customName;
    public List<ResourceLocation> effectParticle;

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
        this.effectParticle = null;
        this.cooldown = 0;

        this.base = new CandleLayer(1, 1,1, 1,1,null,  false);
        this.herb = new CandleLayer(1, 1,1, 1,1,null,  false);
        this.glow = new CandleLayer(1, 1,1, 1,1,null,  false);
        this.swirl = new CandleLayer(1, 1,1, 1,1,null, false);


//        herbLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_herb_layer.png");
//        baseLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_base_layer.png");
//        glowLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_glow_layer.png");
//        swirlLayer = new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_swirl_layer.png");
    }

    public boolean hasBase(){
        return base.layer != null;
    }

    public void setEffect(AbstractCandleEffect effect){
        this.effect = effect;
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

        tag.putString("effect", this.effect.getLocationName());
        tag.putInt("cooldown", this.cooldown);

        if(this.effectParticle != null) {
            CompoundTag compoundTag = new CompoundTag();
            for(int i = 0; i < this.effectParticle.size(); i++){
                compoundTag.putString("particle" + i, this.effectParticle.get(i).toString());
                tag.put("effectParticle", compoundTag);
            }
        }
        if(this.base.layer != null) {
            CompoundTag ct = new CompoundTag();
            ct.putFloat("meltingSpeedMultiplier", this.base.meltingSpeedMultiplier);
            ct.putFloat("radiusMultiplier", this.base.radiusMultiplier);
            ct.putFloat("effectAmplifierMultiplier", this.base.effectAmplifierMultiplier);
            ct.putFloat("effectCooldownMultiplier", this.base.effectCooldownMultiplier);
            ct.putFloat("effectDurationMultiplier", this.base.effectDurationMultiplier);
            ct.putBoolean("layerFromBlockLocation", this.base.layerFromBlockLocation);
            ct.putString("layer", this.base.layer.toString());
            tag.put("base", ct);
        }
        if(this.herb.layer != null) {
            CompoundTag ct = new CompoundTag();
            ct.putFloat("meltingSpeedMultiplier", this.herb.meltingSpeedMultiplier);
            ct.putFloat("radiusMultiplier", this.herb.radiusMultiplier);
            ct.putFloat("effectAmplifierMultiplier", this.herb.effectAmplifierMultiplier);
            ct.putFloat("effectCooldownMultiplier", this.herb.effectCooldownMultiplier);
            ct.putFloat("effectDurationMultiplier", this.herb.effectDurationMultiplier);
            ct.putBoolean("layerFromBlockLocation", this.herb.layerFromBlockLocation);
            ct.putString("layer", this.herb.layer.toString());
            tag.put("herb", ct);
        }
        if(this.glow.layer != null) {
            CompoundTag ct = new CompoundTag();
            ct.putFloat("meltingSpeedMultiplier", this.glow.meltingSpeedMultiplier);
            ct.putFloat("radiusMultiplier", this.glow.radiusMultiplier);
            ct.putFloat("effectAmplifierMultiplier", this.glow.effectAmplifierMultiplier);
            ct.putFloat("effectCooldownMultiplier", this.glow.effectCooldownMultiplier);
            ct.putFloat("effectDurationMultiplier", this.glow.effectDurationMultiplier);
            ct.putBoolean("layerFromBlockLocation", this.glow.layerFromBlockLocation);
            ct.putString("layer", this.glow.layer.toString());
            tag.put("glow", ct);
        }
        if(this.swirl.layer != null) {
            CompoundTag ct = new CompoundTag();
            ct.putFloat("meltingSpeedMultiplier", this.swirl.meltingSpeedMultiplier);
            ct.putFloat("radiusMultiplier", this.swirl.radiusMultiplier);
            ct.putFloat("effectAmplifierMultiplier", this.swirl.effectAmplifierMultiplier);
            ct.putFloat("effectCooldownMultiplier", this.swirl.effectCooldownMultiplier);
            ct.putFloat("effectDurationMultiplier", this.swirl.effectDurationMultiplier);
            ct.putBoolean("layerFromBlockLocation", this.swirl.layerFromBlockLocation);
            ct.putString("layer", this.swirl.layer.toString());
            tag.put("swirl", ct);
        }
        if(this.customName != null)
            tag.putString("customName", Component.Serializer.toJson(this.customName));

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

        if(tag.contains("effect")) {
            setEffect(CandleEffects.getEffect(tag.getString("effect")));
            this.cooldown = tag.getInt("cooldown");
        }

        if(tag.contains("effectParticle")) {
            this.effectParticle = new ArrayList<>();
            CompoundTag compoundTag = tag.getCompound("effectParticle");
            for(int i = 0; i < compoundTag.size(); i++) {
                this.effectParticle.add(new ResourceLocation(compoundTag.getString("particle" + i)));
            }
        }
        if(tag.contains("base")) {
            CompoundTag ct = tag.getCompound("base");
            this.base = new CandleLayer(ct.getFloat("meltingSpeedMultiplier"),ct.getFloat("radiusMultiplier"),ct.getFloat("effectAmplifierMultiplier"),
                    ct.getFloat("effectCooldownMultiplier"), ct.getFloat("effectDurationMultiplier"),
                    new ResourceLocation(ct.getString("layer")), ct.getBoolean("layerFromBlockLocation"));
        }
        if(tag.contains("herb")) {
            CompoundTag ct = tag.getCompound("herb");
            this.herb = new CandleLayer(ct.getFloat("meltingSpeedMultiplier"),ct.getFloat("radiusMultiplier"),ct.getFloat("effectAmplifierMultiplier"),
                    ct.getFloat("effectCooldownMultiplier"), ct.getFloat("effectDurationMultiplier"),
                    new ResourceLocation(ct.getString("layer")), ct.getBoolean("layerFromBlockLocation"));
        }
        if(tag.contains("glow")) {
            CompoundTag ct = tag.getCompound("glow");
            this.glow = new CandleLayer(ct.getFloat("meltingSpeedMultiplier"),ct.getFloat("radiusMultiplier"),ct.getFloat("effectAmplifierMultiplier"),
                    ct.getFloat("effectCooldownMultiplier"), ct.getFloat("effectDurationMultiplier"),
                    new ResourceLocation(ct.getString("layer")), ct.getBoolean("layerFromBlockLocation"));
        }
        if(tag.contains("swirl")) {
            CompoundTag ct = tag.getCompound("swirl");
            this.swirl = new CandleLayer(ct.getFloat("meltingSpeedMultiplier"),ct.getFloat("radiusMultiplier"),ct.getFloat("effectAmplifierMultiplier"),
                    ct.getFloat("effectCooldownMultiplier"), ct.getFloat("effectDurationMultiplier"),
                    new ResourceLocation(ct.getString("layer")), ct.getBoolean("layerFromBlockLocation"));
        }
        if(tag.contains("customName"))
            this.customName = Component.Serializer.fromJson(tag.getString("customName"));

    }


}
