package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlockColors {

    // water blocks
    public static BlockColor setDynamicBlockColorProvider(double temp, double humidity) {
        return (unknown, lightReader, pos, unknown2) -> {
            assert lightReader != null;
            return BiomeColors.getAverageWaterColor(lightReader, pos);
        };
    }
    // water blocks
    public static BlockColor setDynamicBlockColorProviderGrass(double temp, double humidity) {
        return (unknown, lightReader, pos, unknown2) -> {
            assert lightReader != null;
            return BiomeColors.getAverageGrassColor(lightReader, pos);
        };
    }


    // dynamic grass block colors
    public static final BlockColor WATER_COLOR = setDynamicBlockColorProvider(1, 0.5);
    public static final BlockColor GRASS_COLOR = setDynamicBlockColorProviderGrass(1, 0.5);




    @SubscribeEvent
    public static void onBlockColorsInit(RegisterColorHandlersEvent.Block event) {
        final BlockColors blockColors = event.getBlockColors();

        // blocks
        blockColors.register((state, reader, pos, color) -> {
            return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 0.5D);
        }, ModBlocks.LILY_PAD_BLOCK.get());

        blockColors.register((state, reader, pos, color) -> {
                    return reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 0x442013;
                },
                ModBlocks.COFFER.get()
        );

//        SatchelItem.ItemHandlerConsumer items = event.getBlockColors()::register;
//        // s = stack, t = tint-layer
//        items.register((s, t) -> t == 1 ? ConnectingCarpetDyed.getColorValue(s) : -1, ModItems.INFUSED_FABRIC_CARPET_DYED.get());
        blockColors.register((state, reader, pos, color) -> {
                    return reader != null && pos != null ? ConnectingCarpetDyed.getColorValue(state, pos, reader) : 0x442013;
                },
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_WHITE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_ORANGE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_YELLOW.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIME.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PINK.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GRAY.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_CYAN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PURPLE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLUE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BROWN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GREEN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_RED.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLACK.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_WHITE.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIME.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PINK.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GRAY.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_CYAN.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLUE.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BROWN.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GREEN.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_RED.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLACK.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_WHITE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIME.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PINK.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GRAY.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_CYAN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLUE.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BROWN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GREEN.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_RED.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLACK.get(),

                ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK_STAIRS.get()
        );

        blockColors.register((state, reader, pos, color) -> {
                    return reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 0x442013;
                },
                ModBlocks.BOOK_OF_SHADOWS_BACK.get(),ModBlocks.BOOK_OF_SHADOWS_COVER.get()
        );


    }


}