package net.joefoxe.hexerei.data.candle;

import net.minecraft.resources.ResourceLocation;

public class CandleLayer {
    public float meltingSpeedMultiplier;
    public float radiusMultiplier;
    public float effectAmplifierMultiplier;
    public float effectCooldownMultiplier;
    public float effectDurationMultiplier;
    public boolean layerFromBlockLocation;
    public ResourceLocation layer;
    public CandleLayer(float meltingSpeedMultiplier, float radiusMultiplier, float effectAmplifierMultiplier, float effectCooldownMultiplier, float effectDurationMultiplier, ResourceLocation layer, boolean layerFromBlockLocation){
         this.meltingSpeedMultiplier = meltingSpeedMultiplier;
         this.radiusMultiplier = radiusMultiplier;
         this.effectAmplifierMultiplier = effectAmplifierMultiplier;
         this.effectCooldownMultiplier = effectCooldownMultiplier;
         this.effectDurationMultiplier = effectDurationMultiplier;
         this.layer = layer;
         this.layerFromBlockLocation = layerFromBlockLocation;
    }
}

