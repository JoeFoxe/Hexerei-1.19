package net.joefoxe.hexerei.world;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.gen.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID)
public class ModWorldEvents {

    /*
    @SubscribeEvent
    public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
        ModEntityGeneration.onEntitySpawn(event);
        ModTreeGeneration.generateTrees(event);

        if (event.getCategory() == Biome.BiomeCategory.SWAMP) {
//            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacements.WILLOW_TREE);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FLOWERING_LILYPAD_PLACED);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.SWAMP_FLOWERS_PLACED);
            event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.SELENITE_GEODE_PLACED);
        }
        if (event.getCategory() == Biome.BiomeCategory.JUNGLE) {
//            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacements.MAHOGANY_TREE);
            event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.SELENITE_GEODE_PLACED);
        }

    }
    */
}