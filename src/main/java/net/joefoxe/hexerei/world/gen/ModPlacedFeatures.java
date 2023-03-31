package net.joefoxe.hexerei.world.gen;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Hexerei.MOD_ID);


    public static final RegistryObject<PlacedFeature> WILLOW_PLACED = PLACED_FEATURES.register("willow_placed",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.WILLOW_SPAWN, VegetationPlacements.treePlacement(
                    PlacementUtils.countExtra(2, 0.1F, 1))));

    public static final RegistryObject<PlacedFeature> WITCH_HAZEL_PLACED = PLACED_FEATURES.register("witch_hazel_placed",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.WITCH_HAZEL_SPAWN, VegetationPlacements.treePlacement(
                    PlacementUtils.countExtra(2, 0.1F, 1))));

    public static final RegistryObject<PlacedFeature> MAHOGANY_PLACED = PLACED_FEATURES.register("mahogany_placed",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.MAHOGANY_SPAWN, VegetationPlacements.treePlacement(
                    PlacementUtils.countExtra(2, 0.1F, 1))));

    public static final RegistryObject<PlacedFeature> SWAMP_FLOWERS_PLACED = PLACED_FEATURES.register("swamp_flowers_placed",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.SWAMP_FLOWERS, List.of(CountPlacement.of(3), RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP, BiomeFilter.biome(), PlacementUtils.HEIGHTMAP)));

    public static final RegistryObject<PlacedFeature> FLOWERING_LILYPAD_PLACED = PLACED_FEATURES.register("flowering_lilypad_placed",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.FLOWERING_LILYPAD, VegetationPlacements.worldSurfaceSquaredWithCount(4)));

    public static final RegistryObject<PlacedFeature> SELENITE_GEODE_PLACED_SWAMP = PLACED_FEATURES.register("selenite_geode_placed_swamp",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.SELENITE_GEODE, List.of(RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome())));

    public static final RegistryObject<PlacedFeature> SELENITE_GEODE_PLACED_JUNGLE = PLACED_FEATURES.register("selenite_geode_placed_jungle",
            () -> new PlacedFeature((Holder<ConfiguredFeature<?,?>>)(Holder<? extends ConfiguredFeature<?,?>>)
                    ModConfiguredFeatures.SELENITE_GEODE, List.of(RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome())));

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}
