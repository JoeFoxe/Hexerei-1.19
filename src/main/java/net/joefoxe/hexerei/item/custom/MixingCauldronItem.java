package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.Consumer;

public class MixingCauldronItem extends BlockItem {

    public MixingCauldronItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = HexereiUtil.getDyeColor(stack, 0xFFBE1C);
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

    public ItemStackHandler createHandler() {
        return new ItemStackHandler(36) {

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
    }

}