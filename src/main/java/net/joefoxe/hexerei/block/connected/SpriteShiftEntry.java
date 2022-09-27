package net.joefoxe.hexerei.block.connected;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class SpriteShiftEntry {
    protected StitchedSprite original;
    protected StitchedSprite target;

    public void set(ResourceLocation originalTextureLocation, ResourceLocation targetTextureLocation) {
        original = new StitchedSprite(originalTextureLocation);
        target = new StitchedSprite(targetTextureLocation);
    }

    public ResourceLocation getOriginalResourceLocation() {
        return original.getLocation();
    }

    public ResourceLocation getTargetResourceLocation() {
        return target.getLocation();
    }

    public TextureAtlasSprite getOriginal() {
        if(original.get() == null)
            original.loadSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(original.location).atlas());

        return original.get();
    }

    public TextureAtlasSprite getTarget() {
        return target.get();
    }

    public float getTargetU(float localU) {
        return getTarget().getU(getUnInterpolatedU(getOriginal(), localU));
    }

    public float getTargetV(float localV) {
        return getTarget().getV(getUnInterpolatedV(getOriginal(), localV));
    }

    public static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f * 16.0F;
    }

    public static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f * 16.0F;
    }
}
