package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

public class CandleItem extends BlockItem {

    public CandleItem(Block block, Properties properties) {
        super(block, properties.component(DataComponents.CUSTOM_DATA, CustomData.EMPTY));
        DispenserBlock.registerBehavior(this, Candle.DISPENSE_ITEM_BEHAVIOR);
    }


    public static void setHeight(ItemStack stack, int height) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("height", height);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getHeight(ItemStack stack) {

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(!tag.isEmpty()){
            return tag.contains("height", 99) ? tag.getInt("height") : 7;
        }
        return 7;
    }

//    public static void setCooldown(ItemStack p_41116_, int p_41117_) {
//        p_41116_.getOrCreateTag().putInt("cooldown", p_41117_);
//    }
//
//    public static int getCooldown(ItemStack stack) {
//        if(stack.hasTag()){
//            CompoundTag compoundtag = stack.getTag();
//            return compoundtag != null && compoundtag.contains("cooldown", 99) ? compoundtag.getInt("cooldown") : 0;
//        }
//        return 0;
//    }
//    public static void setLayer(ItemStack stack, String layer, String target) {
//        CompoundTag tag = stack.getOrCreateTagElement(target);
//        if(layer != null)
//            tag.putString("layer", layer);
//    }
//
    public static void setLayerFromBlock(ItemStack stack, String layer, String target) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(target))
            tag.put(target, new CompoundTag());
        CompoundTag innerTag = tag.getCompound(target);
        if(layer != null) {

            innerTag.putString("layer", layer);
            innerTag.putBoolean("layerFromBlockLocation", true);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
//
//    public static void setEffectLocation(ItemStack stack, String effect) {
//        CompoundTag tag = stack.getOrCreateTag();
//        if(effect != null)
//            tag.putString("effect", effect);
//        else{
//            tag.remove("effect");
//        }
//    }
//
//    public static void setEffectParticle(ItemStack stack, List<String> effectParticle) {
//        CompoundTag tag = stack.getOrCreateTagElement("effectParticle");
//        for(int i = 0; i < effectParticle.size(); i++){
//            if (effectParticle.get(i) != null)
//                tag.putString("particle" + i, effectParticle.get(i));
//            else {
//                tag.remove("particle");
//            }
//        }
//    }
//
//    public static String getHerbLayer(ItemStack stack) {
//        CompoundTag tag = stack.getTagElement("herb");
//        if(tag == null) return null;
//
//        return tag.contains("layer") ? tag.getString("layer") : null;
//    }
//
//    public static boolean getLayerFromBlock(ItemStack stack, String target) {
//        return getLayerFromBlock(stack.getTagElement(target));
//    }
//
//    public static boolean getLayerFromBlock(CompoundTag tag) {
//        if(tag == null) return false;
//        return tag.contains("layerFromBlockLocation") && tag.getBoolean("layerFromBlockLocation");
//    }

    public static String getBaseLayer(ItemStack stack) {

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return getLayerLoc(tag.contains("base") ? tag.getCompound("base") : tag);
    }

//    public static String getGlowLayer(ItemStack stack) {
//        CompoundTag tag = stack.getTagElement("glow");
//        return getLayerLoc(tag);
//    }
//
//    public static String getSwirlLayer(ItemStack stack) {
//        CompoundTag tag = stack.getTagElement("swirl");
//        return getLayerLoc(tag);
//    }

    private static String getLayerLoc(CompoundTag tag) {
        if(tag.isEmpty()) return null;
        return tag.contains("layer") ? tag.getString("layer") : null;
    }


    public static String getEffectLocation(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(!tag.isEmpty()){
            return tag.getString("effect");
        }
        return null;
    }

//    public static List<String> getEffectParticle(ItemStack stack) {
//        if(stack.hasTag()){
//            List<String> list = new ArrayList<>();
//            CompoundTag tag = stack.getOrCreateTagElement("effectParticle");
//
//            for(int i = 0; i < tag.size(); i++){
//                list.add(tag.getString("particle" + i));
//            }
//
//            return list;
//        }
//        return null;
//    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = HexereiUtil.getDyeColor(stack, Candle.BASE_COLOR);
        if(color == null && dyeCol != -1)
            return dyeCol;
        return color.getTextureDiffuseColor();
    }

    public static int getDyeColorNamed(String name) {

        if(HexereiUtil.getDyeColorNamed(name)!= null){
            float f3 = (((ClientEvents.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col1.getTextureDiffuseColor());
            float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValue(f, f1, f2);
        }
        return 0;

    }

    public static DyeColor getDyeColorNamed(ItemStack stack) {

        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }

}