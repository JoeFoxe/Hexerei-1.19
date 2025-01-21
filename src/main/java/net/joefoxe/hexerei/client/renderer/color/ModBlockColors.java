package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;


@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
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
        event.register((state, reader, pos, color) -> {
            return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 0.5D);
        }, ModBlocks.LILY_PAD_BLOCK.get());

        event.register((state, reader, pos, color) -> {
                    return reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 0x442013;
                },
                ModBlocks.COFFER.get()
        );

        event.register((state, reader, pos, color) -> ConnectingCarpetDyed.getColorValue(state),
                ModBlocks.INFUSED_FABRIC_CARPET.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get(),
                ModBlocks.INFUSED_FABRIC_BLOCK.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get(),
                ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get(),
                ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get()
        );

        event.register((state, reader, pos, color) -> {
                    return reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 0x442013;
                },
                ModBlocks.BOOK_OF_SHADOWS_BACK.get(),ModBlocks.BOOK_OF_SHADOWS_COVER.get()
        );


    }


}