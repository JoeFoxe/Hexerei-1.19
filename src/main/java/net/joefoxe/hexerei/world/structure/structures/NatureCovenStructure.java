package net.joefoxe.hexerei.world.structure.structures;


import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class NatureCovenStructure extends Structure {

    // A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final MapCodec<NatureCovenStructure> CODEC = RecordCodecBuilder.<NatureCovenStructure>mapCodec(instance ->
            instance.group(NatureCovenStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, NatureCovenStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public NatureCovenStructure(StructureSettings config,
                                Holder<StructureTemplatePool> startPool,
                                Optional<ResourceLocation> startJigsawName,
                                int size,
                                HeightProvider startHeight,
                                Optional<Heightmap.Types> projectStartToHeightmap,
                                int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    /*
     * This is where extra checks can be done to determine if the structure can spawn here.
     * This only needs to be overridden if you're adding additional spawn conditions.
     *
     * Fun fact, if you set your structure separation/spacing to be 0/1, you can use
     * extraSpawningChecks to return true only if certain chunk coordinates are passed in
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

    private boolean extraSpawningChecks(GenerationContext context) {
        // Grabs the chunk position we are at
        ChunkPos chunkpos = context.chunkPos();

        // Checks to make sure our structure does not spawn above land that's higher than y = 150
        // to demonstrate how this method is good for checking extra conditions for spawning
        return context.chunkGenerator().getFirstOccupiedHeight(
                chunkpos.getMinBlockX(),
                chunkpos.getMinBlockZ(),
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                context.heightAccessor(),
                context.randomState()) < 150;

    }

    static final Logger LOGGER = LogUtils.getLogger();

    private boolean canBeReplaced(BlockState currBlock) {
        return (currBlock.canBeReplaced() ||
                currBlock.isAir() ||
                currBlock.is(BlockTags.LEAVES) ||
                currBlock.is(Blocks.MUD_BRICK_SLAB) ||
                currBlock.is(Blocks.MOSS_CARPET) ||
                currBlock.is(ModBlocks.WITCH_HAZEL_FENCE.get()) ||
                currBlock.is(ModBlocks.WITCH_HAZEL_SLAB.get()) ||
                currBlock.is(ModBlocks.WITCH_HAZEL_STAIRS.get()) ||
                currBlock.is(Blocks.WATER) ||
                currBlock.is(Blocks.LAVA));
    }
    @Override
    public void afterPlace(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pChunkGenerator, RandomSource pRandom, BoundingBox pBoundingBox, ChunkPos pChunkPos, PiecesContainer pPieces) {
        super.afterPlace(pLevel, pStructureManager, pChunkGenerator, pRandom, pBoundingBox, pChunkPos, pPieces);


        try {

            for (BlockPos blockPos : BlockPos.betweenClosed(pBoundingBox.minX(), pBoundingBox.minY(), pBoundingBox.minZ(), pBoundingBox.maxX(), pBoundingBox.maxY(), pBoundingBox.maxZ())) {
                if (pPieces.isInsidePiece(blockPos) && pLevel.isAreaLoaded(blockPos, 1) && pLevel.getBlockState(blockPos).is(Blocks.YELLOW_STAINED_GLASS_PANE)) {
                    if (pLevel instanceof ServerLevel serverLevel) {
                        // Always replace the glass itself with witch hazel pillar
                        serverLevel.setBlockAndUpdate(blockPos, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState());

                        // Generate vertical pillar down
                        BlockPos.MutableBlockPos mutable = blockPos.below().mutable();
                        BlockState currBlock = pLevel.getBlockState(mutable);
                        int itor = 0;
                        while (mutable.getY() > 0 && (canBeReplaced(currBlock))) {
                            if (itor != 1)
                                serverLevel.setBlockAndUpdate(mutable, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState());
                            else
                                serverLevel.setBlockAndUpdate(mutable, ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get().defaultBlockState());
                            mutable.move(Direction.DOWN);
                            currBlock = serverLevel.getBlockState(mutable);

                            if (!canBeReplaced(currBlock)) {
                                serverLevel.setBlockAndUpdate(mutable.above(), ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get().defaultBlockState());
                                break;
                            }

                            itor++;
                        }
                    } else if (pLevel instanceof WorldGenRegion worldGenRegion) {

                        // Always replace the glass itself with witch hazel pillar
                        worldGenRegion.setBlock(blockPos, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState(), 3);

                        // Generate vertical pillar down
                        BlockPos.MutableBlockPos mutable = blockPos.below().mutable();
                        BlockState currBlock = pLevel.getBlockState(mutable);
                        int itor = 0;
                        while (mutable.getY() > 0 && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA))) {
                            if (itor != 1)
                                worldGenRegion.setBlock(mutable, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState(), 3);
                            else
                                worldGenRegion.setBlock(mutable, ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get().defaultBlockState(), 3);
                            mutable.move(Direction.DOWN);
                            currBlock = worldGenRegion.getBlockState(mutable);

                            if (!(currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.MUD_BRICK_SLAB) || currBlock.is(Blocks.MOSS_CARPET) || currBlock.is(ModBlocks.WITCH_HAZEL_FENCE.get()) || currBlock.is(ModBlocks.WITCH_HAZEL_SLAB.get()) || currBlock.is(ModBlocks.WITCH_HAZEL_STAIRS.get()) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA))) {
                                worldGenRegion.setBlock(mutable.above(), ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get().defaultBlockState(), 3);
                                break;
                            }

                            itor++;
                        }
                    }


                }
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext pContext, Holder<StructureTemplatePool> pStartPool, Optional<ResourceLocation> pStartJigsawName, int pMaxDepth, BlockPos pPos, boolean pUseExpansionHack, Optional<Heightmap.Types> pProjectStartToHeightmap, int pMaxDistanceFromCenter) {
        RegistryAccess registryaccess = pContext.registryAccess();
        ChunkGenerator chunkgenerator = pContext.chunkGenerator();
        StructureTemplateManager structuretemplatemanager = pContext.structureTemplateManager();
        LevelHeightAccessor levelheightaccessor = pContext.heightAccessor();
        WorldgenRandom worldgenrandom = pContext.random();
        Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registries.TEMPLATE_POOL);
        Rotation rotation = Rotation.getRandom(worldgenrandom);
        StructureTemplatePool structuretemplatepool = pStartPool.value();
        StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
        if (structurepoolelement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos blockpos;
            if (pStartJigsawName.isPresent()) {
                ResourceLocation resourcelocation = pStartJigsawName.get();
                Optional<BlockPos> optional = getRandomNamedJigsaw(structurepoolelement, resourcelocation, pPos, rotation, structuretemplatemanager, worldgenrandom);
                if (optional.isEmpty()) {
                    LOGGER.error("No starting jigsaw {} found in start pool {}", resourcelocation, pStartPool.unwrapKey().map((p_248484_) -> {
                        return p_248484_.location().toString();
                    }).orElse("<unregistered>"));
                    return Optional.empty();
                }

                blockpos = optional.get();
            } else {
                blockpos = pPos;
            }

            Vec3i vec3i = blockpos.subtract(pPos);
            BlockPos blockpos1 = pPos.subtract(vec3i);
            PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(structuretemplatemanager, structurepoolelement, blockpos1, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuretemplatemanager, blockpos1, rotation), LiquidSettings.APPLY_WATERLOGGING);
            BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
            int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
            int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
            int k;
            if (pProjectStartToHeightmap.isPresent()) {
                k = pPos.getY() + chunkgenerator.getFirstFreeHeight(i, j, pProjectStartToHeightmap.get(), levelheightaccessor, pContext.randomState());
            } else {
                k = blockpos1.getY();
            }

            int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
            poolelementstructurepiece.move(0, k - l, 0);
            int i1 = k + vec3i.getY();
            Consumer<StructurePiecesBuilder> pGenerator = (structurePiecesBuilder) -> {
                List<PoolElementStructurePiece> list = Lists.newArrayList();
                list.add(poolelementstructurepiece);
                if (pMaxDepth > 0) {
                    AABB aabb = new AABB((double)(i - pMaxDistanceFromCenter), (double)(i1 - pMaxDistanceFromCenter), (double)(j - pMaxDistanceFromCenter), (double)(i + pMaxDistanceFromCenter + 1), (double)(i1 + pMaxDistanceFromCenter + 1), (double)(j + pMaxDistanceFromCenter + 1));
                    VoxelShape voxelshape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST);
                    addPieces(pContext.randomState(), pMaxDepth, pUseExpansionHack, chunkgenerator, structuretemplatemanager, levelheightaccessor, worldgenrandom, registry, poolelementstructurepiece, list, voxelshape, pProjectStartToHeightmap.orElse(null));
                    list.forEach(structurePiecesBuilder::addPiece);
                }
            };

            return Optional.of(new Structure.GenerationStub(new BlockPos(i, i1, j), pGenerator));
        }
    }

    private static void addPieces(RandomState pRandomState, int pMaxDepth, boolean pUseExpansionHack, ChunkGenerator pChunkGenerator, StructureTemplateManager pStructureTemplateManager, LevelHeightAccessor pLevel, RandomSource pRandom, Registry<StructureTemplatePool> pPools, PoolElementStructurePiece p_227219_, List<PoolElementStructurePiece> pPieces, VoxelShape p_227221_, Heightmap.Types pType) {
        Placer jigsawplacement$placer = new Placer(pPools, pMaxDepth, pChunkGenerator, pStructureTemplateManager, pPieces, pRandom);
        jigsawplacement$placer.placing.addLast(new PieceState(p_227219_, new MutableObject<>(p_227221_), 0));

        while(!jigsawplacement$placer.placing.isEmpty()) {
            PieceState jigsawplacement$piecestate = jigsawplacement$placer.placing.removeFirst();
            jigsawplacement$placer.tryPlacingChildren(jigsawplacement$piecestate.piece, jigsawplacement$piecestate.free, jigsawplacement$piecestate.depth, pUseExpansionHack, pLevel, pRandomState, pChunkGenerator, pType);
        }

    }

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource random;
        final Deque<PieceState> placing = Queues.newArrayDeque();

        Placer(Registry<StructureTemplatePool> pPools, int pMaxDepth, ChunkGenerator pChunkGenerator, StructureTemplateManager pStructureTemplateManager, List<? super PoolElementStructurePiece> pPieces, RandomSource pRandom) {
            this.pools = pPools;
            this.maxDepth = pMaxDepth;
            this.chunkGenerator = pChunkGenerator;
            this.structureTemplateManager = pStructureTemplateManager;
            this.pieces = pPieces;
            this.random = pRandom;
        }

        void tryPlacingChildren(PoolElementStructurePiece pPiece, MutableObject<VoxelShape> pFree, int pDepth, boolean pUseExpansionHack, LevelHeightAccessor pLevel, RandomState pRandomState, ChunkGenerator chunkGenerator, Heightmap.Types pType) {
            StructurePoolElement structurepoolelement = pPiece.getElement();

            BlockPos blockpos = pPiece.getPosition();
            Rotation rotation = pPiece.getRotation();
            StructureTemplatePool.Projection structuretemplatepool$projection = structurepoolelement.getProjection();
            boolean flag = structuretemplatepool$projection == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> mutableobject = new MutableObject<>();
            BoundingBox boundingbox = pPiece.getBoundingBox();
            int i = boundingbox.minY();

            label129:
            for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : structurepoolelement.getShuffledJigsawBlocks(this.structureTemplateManager, blockpos, rotation, this.random)) {
                Direction direction = JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state());
                BlockPos blockpos1 = structuretemplate$structureblockinfo.pos();
                BlockPos blockpos2 = blockpos1.relative(direction);
                int j = blockpos1.getY() - i;
                int k = -1;



                // hardcoded stairs for now, will change later on
                BlockPos forwards = blockpos1.relative(direction, 15);
                BlockPos left = blockpos1.relative(direction, 10).relative(direction.getCounterClockWise(), 6);
                BlockPos right = blockpos1.relative(direction, 10).relative(direction.getClockWise(), 6);

                StructureTemplate.StructureBlockInfo usingStructureBlockInf = structuretemplate$structureblockinfo;

                int groundLevel = chunkGenerator.getFirstFreeHeight(blockpos2.getX() , blockpos2.getZ(), pType, pLevel, pRandomState);
                int distToGround = blockpos2.getY() - groundLevel;

                boolean stairsFlag = false;
                CompoundTag tag = usingStructureBlockInf.nbt() != null ? usingStructureBlockInf.nbt().copy() : new CompoundTag();
                if (distToGround < 3 && distToGround > -5 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {
                    tag.putString("pool", "hexerei:coven/nature_coven/stairs");
                    tag.putString("target", "minecraft:stairs_up");
                    stairsFlag = true;
                } else if (distToGround > 8 && distToGround <= 15 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {
                    tag.putString("pool", "hexerei:coven/nature_coven/stairs");
                    tag.putString("target", "minecraft:stairs_down");
                    stairsFlag = true;
                } else if (distToGround > 15 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {

                    int groundLevelRight = chunkGenerator.getFirstFreeHeight(right.getX() , right.getZ(), pType, pLevel, pRandomState);
                    int distToGroundRight = right.getY() - groundLevelRight;
                    int groundLevelLeft = chunkGenerator.getFirstFreeHeight(left.getX() , left.getZ(), pType, pLevel, pRandomState);
                    int distToGroundLeft = left.getY() - groundLevelLeft;
                    int groundLevelForwards = chunkGenerator.getFirstFreeHeight(forwards.getX() , forwards.getZ(), pType, pLevel, pRandomState);
                    int distToGroundForwards = forwards.getY() - groundLevelForwards;

                    if (random.nextFloat() > 0.4f){
                        if (distToGroundForwards < 8){
                            tag.putString("pool", "hexerei:coven/nature_coven/streets");
                            tag.putString("target", "minecraft:street");
                            tag.putString("name", "minecraft:street");
                        }
                        else if (distToGroundLeft >= distToGroundRight){
                            tag.putString("pool", "hexerei:coven/nature_coven/stairs");
                            tag.putString("target", "minecraft:stairs_down_turn_right");
                        } else {
                            tag.putString("pool", "hexerei:coven/nature_coven/stairs");
                            tag.putString("target", "minecraft:stairs_down_turn_left");
                        }
                        stairsFlag = true;
                    }
                } else if (tag.contains("name") && tag.getString("name").equals("minecraft:street") && tag.contains("target") && tag.getString("target").equals("minecraft:stairs_up")) {
                    tag.putString("pool", "hexerei:coven/nature_coven/streets");
                    tag.putString("target", "minecraft:street");
                    tag.putString("name", "minecraft:street");
                } else if (tag.contains("name") && tag.getString("name").equals("minecraft:street") && tag.contains("target") && tag.getString("target").equals("minecraft:stairs_down")) {
                    tag.putString("pool", "hexerei:coven/nature_coven/streets");
                    tag.putString("target", "minecraft:street");
                    tag.putString("name", "minecraft:street");
                } else if (tag.contains("name") && tag.getString("name").equals("minecraft:street") && tag.contains("target") && tag.getString("target").equals("minecraft:stairs_down_turn")) {
                    tag.putString("pool", "hexerei:coven/nature_coven/streets");
                    tag.putString("target", "minecraft:street");
                    tag.putString("name", "minecraft:street");
                }
                usingStructureBlockInf = new StructureTemplate.StructureBlockInfo(structuretemplate$structureblockinfo.pos(), structuretemplate$structureblockinfo.state(), tag);




                ResourceKey<StructureTemplatePool> resourcekey = readPoolName(usingStructureBlockInf);
                Optional<? extends Holder<StructureTemplatePool>> optional = this.pools.getHolder(resourcekey);
                if (optional.isEmpty()) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)resourcekey.location());
                } else {
                    Holder<StructureTemplatePool> holder = optional.get();
                    if (holder.value().size() == 0 && !holder.is(Pools.EMPTY)) {
                        LOGGER.warn("Empty or non-existent pool: {}", (Object)resourcekey.location());
                    } else {
                        Holder<StructureTemplatePool> holder1 = holder.value().getFallback();
                        if (holder1.value().size() == 0 && !holder1.is(Pools.EMPTY)) {
                            LOGGER.warn("Empty or non-existent fallback pool: {}", holder1.unwrapKey().map((p_255599_) -> {
                                return p_255599_.location().toString();
                            }).orElse("<unregistered>"));
                        } else {
                            boolean flag1 = boundingbox.isInside(blockpos2);
                            MutableObject<VoxelShape> mutableobject1;
                            if (flag1) {
                                mutableobject1 = mutableobject;
                                if (mutableobject.getValue() == null) {
                                    mutableobject.setValue(Shapes.create(AABB.of(boundingbox)));
                                }
                            } else {
                                mutableobject1 = pFree;
                            }

                            List<StructurePoolElement> list = Lists.newArrayList();
                            if (pDepth != this.maxDepth) {
                                list.addAll(holder.value().getShuffledTemplates(this.random));
                            }

                            list.addAll(holder1.value().getShuffledTemplates(this.random));

                            for(StructurePoolElement structurepoolelement1 : list) {
                                if (structurepoolelement1 == EmptyPoolElement.INSTANCE) {
                                    break;
                                }

                                for(Rotation rotation1 : Rotation.getShuffled(this.random)) {
                                    List<StructureTemplate.StructureBlockInfo> list1 = structurepoolelement1.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation1, this.random);
                                    BoundingBox boundingbox1 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation1);
                                    int l;
                                    if (pUseExpansionHack && boundingbox1.getYSpan() <= 16) {
                                        l = list1.stream().mapToInt((p_255598_) -> {
                                            if (!boundingbox1.isInside(p_255598_.pos().relative(JigsawBlock.getFrontFacing(p_255598_.state())))) {
                                                return 0;
                                            } else {
                                                ResourceKey<StructureTemplatePool> resourcekey1 = readPoolName(p_255598_);
                                                Optional<? extends Holder<StructureTemplatePool>> optional1 = this.pools.getHolder(resourcekey1);
                                                Optional<Holder<StructureTemplatePool>> optional2 = optional1.map((p_255600_) -> {
                                                    return p_255600_.value().getFallback();
                                                });
                                                int j3 = optional1.map((p_255596_) -> {
                                                    return p_255596_.value().getMaxSize(this.structureTemplateManager);
                                                }).orElse(0);
                                                int k3 = optional2.map((p_255601_) -> {
                                                    return p_255601_.value().getMaxSize(this.structureTemplateManager);
                                                }).orElse(0);
                                                return Math.max(j3, k3);
                                            }
                                        }).max().orElse(0);
                                    } else {
                                        l = 0;
                                    }

                                    // reconnect the stairs to the street target and name
                                    List<StructureTemplate.StructureBlockInfo> list2 = new ArrayList<>();
                                    for(StructureTemplate.StructureBlockInfo usingStructureBlockInf1 : list1) {
                                        CompoundTag tag2 = usingStructureBlockInf1.nbt();
                                        if (stairsFlag && tag2 != null && tag2.contains("name") && tag2.getString("name").equals("minecraft:stairs_street")) {
                                            tag2.putString("pool", "hexerei:coven/nature_coven/streets");
                                            tag2.putString("target", "minecraft:street");
                                            tag2.putString("name", "minecraft:street");
                                        }
                                        list2.add(new StructureTemplate.StructureBlockInfo(usingStructureBlockInf1.pos(), usingStructureBlockInf1.state(), tag2));
                                    }

                                    for(StructureTemplate.StructureBlockInfo usingStructureBlockInf1 : list2) {

                                        if (JigsawBlock.canAttach(usingStructureBlockInf, usingStructureBlockInf1)) {
                                            BlockPos blockpos3 = usingStructureBlockInf1.pos();
                                            BlockPos blockpos4 = blockpos2.subtract(blockpos3);
                                            BoundingBox boundingbox2 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, blockpos4, rotation1);
                                            int i1 = boundingbox2.minY();
                                            StructureTemplatePool.Projection structuretemplatepool$projection1 = structurepoolelement1.getProjection();
                                            boolean flag2 = structuretemplatepool$projection1 == StructureTemplatePool.Projection.RIGID;
                                            int j1 = blockpos3.getY();
                                            int k1 = j - j1 + JigsawBlock.getFrontFacing(usingStructureBlockInf.state()).getStepY();
                                            int l1;
                                            if (flag && flag2) {
                                                l1 = i + k1;
                                            } else {
                                                if (k == -1) {
                                                    k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                                }

                                                l1 = k - j1;
                                            }

                                            int i2 = l1 - i1;
                                            BoundingBox boundingbox3 = boundingbox2.moved(0, i2, 0);
                                            BlockPos blockpos5 = blockpos4.offset(0, i2, 0);
                                            if (l > 0) {
                                                int j2 = Math.max(l + 1, boundingbox3.maxY() - boundingbox3.minY());
                                                boundingbox3.encapsulate(new BlockPos(boundingbox3.minX(), boundingbox3.minY() + j2, boundingbox3.minZ()));
                                            }

                                            if (!Shapes.joinIsNotEmpty(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3).deflate(0.25D)), BooleanOp.ONLY_SECOND)) {
                                                mutableobject1.setValue(Shapes.joinUnoptimized(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3)), BooleanOp.ONLY_FIRST));
                                                int i3 = pPiece.getGroundLevelDelta();
                                                int k2;
                                                if (flag2) {
                                                    k2 = i3 - k1;
                                                } else {
                                                    k2 = structurepoolelement1.getGroundLevelDelta();
                                                }

                                                PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(this.structureTemplateManager, structurepoolelement1, blockpos5, k2, rotation1, boundingbox3, LiquidSettings.APPLY_WATERLOGGING);
                                                int l2;
                                                if (flag) {
                                                    l2 = i + j;
                                                } else if (flag2) {
                                                    l2 = l1 + j1;
                                                } else {
                                                    if (k == -1) {
                                                        k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                                    }

                                                    l2 = k + k1 / 2;
                                                }

                                                pPiece.addJunction(new JigsawJunction(blockpos2.getX(), l2 - j + i3, blockpos2.getZ(), k1, structuretemplatepool$projection1));
                                                poolelementstructurepiece.addJunction(new JigsawJunction(blockpos1.getX(), l2 - j1 + k2, blockpos1.getZ(), -k1, structuretemplatepool$projection));
                                                this.pieces.add(poolelementstructurepiece);
                                                if (pDepth + 1 <= this.maxDepth) {
                                                    this.placing.addLast(new PieceState(poolelementstructurepiece, mutableobject1, pDepth + 1));
                                                }
                                                continue label129;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo pStructureBlockInfo) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.parse(pStructureBlockInfo.nbt().getString("pool")));
        }
    }


    static final class PieceState {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece pPiece, MutableObject<VoxelShape> pFree, int pDepth) {
            this.piece = pPiece;
            this.free = pFree;
            this.depth = pDepth;
        }
    }
    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement pElement, ResourceLocation pStartJigsawName, BlockPos pPos, Rotation pRotation, StructureTemplateManager pStructureTemplateManager, WorldgenRandom pRandom) {
        List<StructureTemplate.StructureBlockInfo> list = pElement.getShuffledJigsawBlocks(pStructureTemplateManager, pPos, pRotation, pRandom);
        Optional<BlockPos> optional = Optional.empty();

        for(StructureTemplate.StructureBlockInfo usingStructureBlockInf : list) {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(usingStructureBlockInf.nbt().getString("name"));
            if (pStartJigsawName.equals(resourcelocation)) {
                optional = Optional.of(usingStructureBlockInf.pos());
                break;
            }
        }

        return optional;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!extraSpawningChecks(context)) {
            return Optional.empty();
        }

        // Set's our spawning blockpos's y offset to be 60 blocks up.
        // Since we are going to have heightmap/terrain height spawning set to true further down, this will make it so we spawn 60 blocks above terrain.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG heightmap.
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<GenerationStub> structurePiecesGenerator =
                addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
                        // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter); // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.NATURE_COVEN.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}