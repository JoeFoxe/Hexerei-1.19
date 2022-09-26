package net.joefoxe.hexerei.world.gen;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
import java.util.Random;

import static net.joefoxe.hexerei.block.custom.PickableDoubleFlower.AGE;

public class ModConfiguredFeatures {


    public static final Holder<ConfiguredFeature<GeodeConfiguration, ?>> SELENITE_GEODE =
            FeatureUtils.register("hexerei:selenite_geode", Feature.GEODE,
                    new GeodeConfiguration(new GeodeBlockSettings(
                            BlockStateProvider.simple(Blocks.AIR),
                            BlockStateProvider.simple(ModBlocks.SELENITE_BLOCK.get()),
                            BlockStateProvider.simple(ModBlocks.BUDDING_SELENITE.get()),
                            BlockStateProvider.simple(Blocks.CALCITE),
                            BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                            List.of(ModBlocks.SMALL_SELENITE_BUD.get().defaultBlockState(),
                                    ModBlocks.MEDIUM_SELENITE_BUD.get().defaultBlockState(),
                                    ModBlocks.LARGE_SELENITE_BUD.get().defaultBlockState(),
                                    ModBlocks.SELENITE_CLUSTER.get().defaultBlockState()),
                            BlockTags.FEATURES_CANNOT_REPLACE,
                            BlockTags.GEODE_INVALID_BLOCKS),
                            new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D),
                            new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true,
                            UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2),
                            -16, 16, 0.05D, 1));


    public static Random random = new Random();
    public static final Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> SWAMP_FLOWERS =
            FeatureUtils.register("hexerei:swamp_flowers", Feature.SIMPLE_RANDOM_SELECTOR,
                    new SimpleRandomFeatureConfiguration(HolderSet.direct(
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.YELLOW_DOCK_BUSH.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MUGWORT_BUSH.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BELLADONNA_FLOWER.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MANDRAKE_FLOWER.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))))));

    public static final Holder<PlacedFeature> COMMON_SWAMP_FLOWERS = PlacementUtils.register("hexerei:common_swamp_flowers", SWAMP_FLOWERS, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> FLOWERING_LILYPAD =
            FeatureUtils.register("hexerei:flowering_lilypad", Feature.RANDOM_PATCH,
                    new RandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.LILY_PAD_BLOCK.get().defaultBlockState())))));

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> WILLOW =
            FeatureUtils.register("hexerei:willow_tree", ModFeatures.WILLOW_TREE.get(), new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                    new StraightTrunkPlacer(5, 6, 3),
                    BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                    new TwoLayersFeatureSize(1, 0, 2)).build());

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> MAHOGANY =
            FeatureUtils.register("hexerei:mahogany_tree", ModFeatures.MAHOGANY_TREE.get(), new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(ModBlocks.MAHOGANY_LOG.get()),
                    new StraightTrunkPlacer(5, 6, 3),
                    BlockStateProvider.simple(ModBlocks.MAHOGANY_LEAVES.get()),
                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                    new TwoLayersFeatureSize(1, 0, 2)).build());

    public static final Holder<PlacedFeature> WILLOW_CHECKED = PlacementUtils.register("hexerei:willow_checked", WILLOW,
            PlacementUtils.filteredByBlockSurvival(ModBlocks.WILLOW_SAPLING.get()));

    public static final Holder<PlacedFeature> MAHOGANY_CHECKED = PlacementUtils.register("hexerei:mahogany_checked", MAHOGANY,
            PlacementUtils.filteredByBlockSurvival(ModBlocks.MAHOGANY_SAPLING.get()));
    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> WILLOW_SPAWN =
            FeatureUtils.register("hexerei:willow_spawn", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(WILLOW_CHECKED,
                            0.5F)), WILLOW_CHECKED));

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> MAHOGANY_SPAWN =
            FeatureUtils.register("hexerei:mahogany_spawn", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(MAHOGANY_CHECKED,
                            0.5F)), MAHOGANY_CHECKED));


    // Based on the swamp trees in vanilla


    public static final Holder<PlacedFeature> TREES_WILLOW_SWAMP = PlacementUtils.register("hexerei:trees_willow_swamp", WILLOW,
            PlacementUtils.countExtra(2, 0.1F, 1),
            InSquarePlacement.spread(),
            SurfaceWaterDepthFilter.forMaxDepth(2),
            PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(),
            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)));

}