package net.joefoxe.hexerei.world.gen;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.List;
import java.util.Random;

import static net.joefoxe.hexerei.block.custom.PickableDoublePlant.AGE;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELENITE_GEODE_KEY = registerKey("selenite_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_FLOWERS_KEY = registerKey("swamp_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERING_LILYPAD_KEY = registerKey("flowering_lilypad");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WITCH_HAZEL_KEY = registerKey("witch_hazel");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAHOGANY_KEY = registerKey("mahogany");
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, HexereiUtil.getResource(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static Random random = new Random();

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        register(context, SELENITE_GEODE_KEY, Feature.GEODE,
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

        register(context, SWAMP_FLOWERS_KEY, Feature.SIMPLE_RANDOM_SELECTOR,
                new SimpleRandomFeatureConfiguration(HolderSet.direct(
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.YELLOW_DOCK_BUSH.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MUGWORT_BUSH.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BELLADONNA_PLANT.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))),
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MANDRAKE_PLANT.get().defaultBlockState().setValue(AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))))))));
//
        register(context, FLOWERING_LILYPAD_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.LILY_PAD_BLOCK.get().defaultBlockState())))));
//
        register(context, WILLOW_KEY, ModFeatures.WILLOW_TREE.get(), NoneFeatureConfiguration.INSTANCE);
//
        register(context, WITCH_HAZEL_KEY, ModFeatures.WITCH_HAZEL_TREE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, MAHOGANY_KEY, ModFeatures.MAHOGANY_TREE.get(), NoneFeatureConfiguration.INSTANCE);
    }

}