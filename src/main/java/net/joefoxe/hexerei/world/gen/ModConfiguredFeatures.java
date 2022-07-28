package net.joefoxe.hexerei.world.gen;

import com.google.common.collect.ImmutableList;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.data.worldgen.features.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

import static net.joefoxe.hexerei.block.custom.PickableDoubleFlower.AGE;

public class ModConfiguredFeatures {

    public static final Holder<ConfiguredFeature<GeodeConfiguration, ?>> SELENITE_GEODE =
            FeatureUtils.register("selenite_geode", Feature.GEODE,
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

    public static final Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> SWAMP_FLOWERS =
            FeatureUtils.register("swamp_flowers", Feature.SIMPLE_RANDOM_SELECTOR,
                    new SimpleRandomFeatureConfiguration(HolderSet.direct(
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                    new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.YELLOW_DOCK_BUSH.get().defaultBlockState().setValue(AGE, 2)))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                    new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MUGWORT_BUSH.get().defaultBlockState().setValue(AGE, 2)))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                    new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BELLADONNA_FLOWER.get().defaultBlockState().setValue(AGE, 2)))),
                            PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                    new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MANDRAKE_FLOWER.get().defaultBlockState().setValue(AGE, 2)))) ))
                    );


    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> LILY_PAD_CONFIG =
            FeatureUtils.register("patch_flower_waterlily", Feature.RANDOM_PATCH,
                    new RandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.LILY_PAD_BLOCK.get().defaultBlockState())))));


    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> WILLOW =
            FeatureUtils.register("willow_tree", ModFeatures.WILLOW_TREE.get(), new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                    new StraightTrunkPlacer(5, 6, 3),
                    BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                    new TwoLayersFeatureSize(1, 0, 2)).build());

    public static final Holder<PlacedFeature> WILLOW_CHECKED = PlacementUtils.register("willow_checked", WILLOW,
            PlacementUtils.filteredByBlockSurvival(ModBlocks.WILLOW_SAPLING.get()));

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> WILLOW_SPAWN =
            FeatureUtils.register("willow_spawn", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(WILLOW_CHECKED,
                            0.5F)), WILLOW_CHECKED));



    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> MAHOGANY =
            FeatureUtils.register("mahogany_tree", ModFeatures.MAHOGANY_TREE.get(), new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(ModBlocks.MAHOGANY_LOG.get()),
                    new StraightTrunkPlacer(5, 6, 3),
                    BlockStateProvider.simple(ModBlocks.MAHOGANY_LEAVES.get()),
                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                    new TwoLayersFeatureSize(1, 0, 2)).build());

    public static final Holder<PlacedFeature> MAHOGANY_CHECKED = PlacementUtils.register("mahogany_checked", MAHOGANY,
            PlacementUtils.filteredByBlockSurvival(ModBlocks.MAHOGANY_SAPLING.get()));

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> MAHOGANY_SPAWN =
            FeatureUtils.register("mahogany_spawn", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(MAHOGANY_CHECKED,
                            0.5F)), MAHOGANY_CHECKED));





//TwoLayerFeature(int limit, int lowerSize, int upperSize)
//ThreeLayersFeatureSize(int limit, int upperLimit, int lowerSize, int middleSize, int upperSize, OptionalInt size)

    private static <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> register(String key,
                                                                                       ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key, configuredFeature);
    }


}