package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SatchelItem extends BroomAttachmentItem implements DyeableLeatherItem {

    public SatchelItem(Properties properties) {
        super(properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = SatchelItem.getColorStatic(stack);
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
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }


    public static DyeColor getDyeColorNamed(ItemStack stack) {


        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);

//        if(stack.getHoverName().getString().equals("jeb_"))
//            return DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//
//        if(stack.getHoverName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            });
//
//        if(stack.getHoverName().getString().equals("bi_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            });
//
//        if(stack.getHoverName().getString().equals("trans_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            });
//
//        if(stack.getHoverName().getString().equals("joe_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            });
//
////        if(this.getName().getString().equals("les_"))
////            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/10) % 15)) {
////                case 1 -> 0;
////                case 2 -> 0;
////                case 3 -> 0;
////                case 4 -> 1;
////                case 5 -> 1;
////                case 6 -> 1;
////                case 7 -> 2;
////                case 8 -> 2;
////                case 9 -> 2;
////                case 10 -> 6;
////                case 11 -> 6;
////                case 12 -> 6;
////                case 13 -> 14;
////                case 14 -> 14;
////                case 15 -> 14;
////                default -> 0;
////            });
//
//
//        //DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//        return null;
    }

}
