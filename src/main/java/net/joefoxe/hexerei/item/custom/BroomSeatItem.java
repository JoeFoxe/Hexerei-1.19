package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BroomSeatItem extends BroomAttachmentItem implements DyeableLeatherItem {

    public BroomSeatItem(Properties properties) {
        super(properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = BroomSeatItem.getColorStatic(stack);
        if(color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x563B24;
    }


    public static DyeColor getDyeColorNamed(ItemStack stack) {
        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
    }

}
