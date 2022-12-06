package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

public class DyeableCarpetItem extends BlockItem {
    public DyeableCarpetItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }


    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        tooltip.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        super.appendHoverText(stack, world, tooltip, flagIn);
    }



    @org.jetbrains.annotations.Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof SlabBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.SLAB_TYPE) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM)
            return ModBlocks.CARPET_SLAB.get().defaultBlockState();
        if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof StairBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.HALF) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.HALF) == Half.BOTTOM) {
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK_STAIRS.get().getStateForPlacement(pContext);
        }
        return super.getPlacementState(pContext);
    }




    public static Block getBlockByColor(@Nullable DyeColor pColor) {
        if (pColor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            switch (pColor) {
                case WHITE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE_STAIRS.get();
                case ORANGE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE_STAIRS.get();
                case MAGENTA:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA_STAIRS.get();
                case LIGHT_BLUE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE_STAIRS.get();
                case YELLOW:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW_STAIRS.get();
                case LIME:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME_STAIRS.get();
                case PINK:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK_STAIRS.get();
                case GRAY:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY_STAIRS.get();
                case LIGHT_GRAY:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY_STAIRS.get();
                case CYAN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN_STAIRS.get();
                case PURPLE:
                default:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE_STAIRS.get();
                case BLUE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE_STAIRS.get();
                case BROWN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN_STAIRS.get();
                case GREEN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN_STAIRS.get();
                case RED:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED_STAIRS.get();
                case BLACK:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK_STAIRS.get();
            }
        }
    }


    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerCarpetColors(RegisterColorHandlersEvent.Item event)
        {
            ItemHandlerConsumer items = event.getItemColors()::register;
            // s = stack, t = tint-layer
            items.register((s, t) -> {
                return t == 0 ? ConnectingCarpetDyed.getColorValue(s) : -1;
            },

                    ModItems.INFUSED_FABRIC_CARPET_DYED_WHITE.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_ORANGE.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_YELLOW.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_LIME.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_PINK.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_GRAY.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_CYAN.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_PURPLE.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_BLUE.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_BROWN.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_GREEN.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_RED.get(),
                    ModItems.INFUSED_FABRIC_CARPET_DYED_BLACK.get(),

                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_WHITE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_ORANGE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_YELLOW.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_LIME.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_PINK.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_GRAY.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_CYAN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_PURPLE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_BLUE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_BROWN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_GREEN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_RED.get(),
                    ModItems.WAXED_INFUSED_FABRIC_CARPET_DYED_BLACK.get(),

                    ModItems.INFUSED_FABRIC_BLOCK_DYED_WHITE.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_LIME.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_PINK.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_GRAY.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_CYAN.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_BLUE.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_BROWN.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_GREEN.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_RED.get(),
                    ModItems.INFUSED_FABRIC_BLOCK_DYED_BLACK.get(),

                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_WHITE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIME.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_PINK.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_GRAY.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_CYAN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLUE.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_BROWN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_GREEN.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_RED.get(),
                    ModItems.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLACK.get());
        }
    }


}
