package net.joefoxe.hexerei.world.structure.structures;
//
//import com.google.common.collect.ImmutableList;
//import com.mojang.serialization.Codec;
//import net.joefoxe.hexerei.Hexerei;
//import net.joefoxe.hexerei.world.structure.ModStructures;
//import net.minecraft.core.Registry;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.MobCategory;
//import net.minecraft.world.level.*;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.biome.BiomeSource;
//import net.minecraft.world.level.biome.MobSpawnSettings;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.levelgen.Heightmap;
//import net.minecraft.world.level.levelgen.LegacyRandomSource;
//import net.minecraft.world.level.levelgen.WorldgenRandom;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.feature.StructureFeature;
//import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
//import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
//import net.minecraft.world.level.levelgen.structure.BoundingBox;
//import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
//import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
//import net.minecraft.world.level.levelgen.structure.StructureStart;
//import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
//import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
//import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
//import net.minecraftforge.common.util.Lazy;
//import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
//import org.checkerframework.checker.units.qual.C;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//import java.util.function.Predicate;
//
//public class WitchHutStructure extends StructureFeature<JigsawConfiguration> {
//
//
//    public WitchHutStructure(Codec<JigsawConfiguration> codec) {
//        super(codec, (context) -> {
//                    // Check if the spot is valid for structure gen. If false, return nothing to signal to the game to skip this spawn attempt.
//                    if (!WitchHutStructure.isFeatureChunk(context)) {
//                        return Optional.empty();
//                    }
//                    // Create the pieces layout of the structure and give it to
//                    else {
//                        return WitchHutStructure.createPiecesGenerator(context);
//                    }
//                },
//                PostPlacementProcessor.NONE);
//    }
//    @Override
//    public GenerationStep.Decoration step() {
//        return GenerationStep.Decoration.SURFACE_STRUCTURES;
//    }
//
//    private static final Lazy<List<MobSpawnSettings.SpawnerData>> STRUCTURE_MONSTERS = Lazy.of(() -> ImmutableList.of(
//            new MobSpawnSettings.SpawnerData(EntityType.WITCH, 100, 4, 9)
//    ));
//    private static final Lazy<List<MobSpawnSettings.SpawnerData>> STRUCTURE_CREATURES = Lazy.of(() -> ImmutableList.of(
//            new MobSpawnSettings.SpawnerData(EntityType.CAT, 100, 1, 2)
//    ));
//
//    // Hooked up in StructureTutorialMain. You can move this elsewhere or change it up.
//    public static void setupStructureSpawns(final StructureSpawnListGatherEvent event) {
//        if(event.getStructure() == ModStructures.WITCH_HUT.get()) {
//            event.addEntitySpawns(MobCategory.MONSTER, STRUCTURE_MONSTERS.get());
//            event.addEntitySpawns(MobCategory.CREATURE, STRUCTURE_CREATURES.get());
//        }
//    }
//
//    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
//        BlockPos blockPos = context.chunkPos().getWorldPosition();
//
//        // Grab height of land. Will stop at first non-air block.
//        int landHeight = context.chunkGenerator().getFirstOccupiedHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
//
//        // Grabs column of blocks at given position. In overworld, this column will be made of stone, water, and air.
//        // In nether, it will be netherrack, lava, and air. End will only be endstone and air. It depends on what block
//        // the chunk generator will place for that dimension.
//        NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(blockPos.getX(), blockPos.getZ(), context.heightAccessor());
//
//        // Combine the column of blocks with land height and you get the top block itself which you can test.
//        BlockState topBlock = columnOfBlocks.getBlock(landHeight);
//
//        // Now we test to make sure our structure is not spawning on water or other fluids.
//        // You can do height check instead too to make it spawn at high elevations.
//        return topBlock.getFluidState().isEmpty(); //landHeight > 100;
//    }
//
//
//    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
//        // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
//        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);
//
//        /*
//         * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
//         * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
//         * Then loop through it and look for land with air above it and set blockpos's Y value to it.
//         * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
//         * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
//         */
//        // NoiseColumn blockReader = context.chunkGenerator().getBaseColumn(blockpos.getX(), blockpos.getZ(), context.heightAccessor());
//
//
//        /*
//         * The only reason we are using JigsawConfiguration here is because further down, we are using
//         * JigsawPlacement.addPieces which requires JigsawConfiguration. However, if you create your own
//         * JigsawPlacement.addPieces, you could reduce the amount of workarounds like above that you need
//         * and give yourself more opportunities and control over your structures.
//         *
//         * An example of a custom JigsawPlacement.addPieces in action can be found here:
//         * https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
//         */
//        JigsawConfiguration newConfig = new JigsawConfiguration(
//                // The path to the starting Template Pool JSON file to read.
//                //
//                // Note, this is "structure_tutorial:run_down_house/start_pool" which means
//                // the game will automatically look into the following path for the template pool:
//                // "resources/data/structure_tutorial/worldgen/template_pool/run_down_house/start_pool.json"
//                // This is why your pool files must be in "data/<modid>/worldgen/template_pool/<the path to the pool here>"
//                // because the game automatically will check in worldgen/template_pool for the pools.
//                () -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
//                        .get(new ResourceLocation(Hexerei.MOD_ID, "witch_hut/start_pool")),
//
//                // How many pieces outward from center can a recursive jigsaw structure spawn.
//                // Our structure is only 1 piece outward and isn't recursive so any value of 1 or more doesn't change anything.
//                // However, I recommend you keep this a decent value like 7 so people can use datapacks to add additional pieces to your structure easily.
//                // But don't make it too large for recursive structures like villages or you'll crash server due to hundreds of pieces attempting to generate!
//                // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
//                10
//        );
//
//        // Create a new context with the new config that has our json pool. We will pass this into JigsawPlacement.addPieces
//        PieceGeneratorSupplier.Context<JigsawConfiguration> newContext = new PieceGeneratorSupplier.Context<>(
//                context.chunkGenerator(),
//                context.biomeSource(),
//                context.seed(),
//                context.chunkPos(),
//                newConfig,
//                context.heightAccessor(),
//                context.validBiome(),
//                context.structureManager(),
//                context.registryAccess()
//        );
//
//        Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
//                JigsawPlacement.addPieces(
//                        newContext, // Used for JigsawPlacement to get all the proper behaviors done.
//                        PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
//                        blockpos.above(context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()) + 3), // Position of the structure. Y value is ignored if last parameter is set to true.
//                        false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
//                        // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
//                        false // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
//                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
//                );
//        /*
//         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
//         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
//         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
//         *
//         * An example of a custom JigsawPlacement.addPieces in action can be found here (warning, it is using Mojmap mappings):
//         * https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
//         */
//
//        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
//        return structurePiecesGenerator;
//    }
//
//
//}


import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.apache.logging.log4j.Level;

import java.util.Optional;

public class WitchHutStructure extends StructureFeature<JigsawConfiguration> {

    public WitchHutStructure() {
        // Create the pieces layout of the structure and give it to the game
        super(JigsawConfiguration.CODEC, WitchHutStructure::createPiecesGenerator, PostPlacementProcessor.NONE);
    }

    /**
     *        : WARNING!!! DO NOT FORGET THIS METHOD!!!! :
     * If you do not override step method, your structure WILL crash the biome as it is being parsed!
     *
     * Generation step for when to generate the structure. there are 10 stages you can pick from!
     * This surface structure stage places the structure before plants and ores are generated.
     */
    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    /*
     * This is where extra checks can be done to determine if the structure can spawn here.
     * This only needs to be overridden if you're adding additional spawn conditions.
     *
     * Fun fact, if you set your structure separation/spacing to be 0/1, you can use
     * isFeatureChunk to return true only if certain chunk coordinates are passed in
     * which allows you to spawn structures only at certain coordinates in the world.
     *
     * Basically, this method is used for determining if the land is at a suitable height,
     * if certain other structures are too close or not, or some other restrictive condition.
     *
     * For example, Pillager Outposts added a check to make sure it cannot spawn within 10 chunk of a Village.
     * (Bedrock Edition seems to not have the same check)
     *
     * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
     * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
     * Then loop through it and look for land with air above it and set blockpos's Y value to it.
     * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
     * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
     *
     * Also, please for the love of god, do not do dimension checking here.
     * If you do and another mod's dimension is trying to spawn your structure,
     * the locate command will make minecraft hang forever and break the game.
     * Use the biome tags for where to spawn the structure and users can datapack
     * it to spawn in specific biomes that aren't in the dimension they don't like if they wish.
     */
    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        // Grabs the chunk position we are at
        ChunkPos chunkpos = context.chunkPos();

        // Checks to make sure our structure does not spawn within 10 chunks of an Ocean Monument
        // to demonstrate how this method is good for checking extra conditions for spawning

        ResourceKey<StructureSet> key = ResourceKey.<StructureSet>create(Registry.STRUCTURE_SET_REGISTRY, new ResourceLocation(Hexerei.MOD_ID, "dark_coven"));
        return !context.chunkGenerator().hasFeatureChunkInRange(key, context.seed(), chunkpos.x, chunkpos.z, 3);
    }

    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!WitchHutStructure.isFeatureChunk(context)) {
            return Optional.empty();
        }

        // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

        // Find the top Y value of the land and then offset our structure to 60 blocks above that.
        // WORLD_SURFACE_WG will stop at top water so we don't accidentally put our structure into the ocean if it is a super deep ocean.
        int topLandY = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        blockpos = blockpos.above(topLandY + 4);

        Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                        blockpos, // Position of the structure. Y value is ignored if last parameter is set to true.
                        false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                        // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                        false // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                );

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         *
         * An example of a custom JigsawPlacement.addPieces in action can be found here (warning, it is using Mojmap mappings):
         * https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.18.2/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
         */

        if(structurePiecesGenerator.isPresent()) {
            // I use to debug and quickly find out if the structure is spawning or not and where it is.
            // This is returning the coordinates of the center starting piece.
            Hexerei.LOGGER.log(Level.DEBUG, "WitchHut at {}", blockpos);
        }

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }
}