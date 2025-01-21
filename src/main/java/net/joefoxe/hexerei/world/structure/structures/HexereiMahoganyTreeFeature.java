package net.joefoxe.hexerei.world.structure.structures;

import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;

public class HexereiMahoganyTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final ResourceLocation MAHOGANY_TREE1 = ResourceLocation.parse("hexerei:mahogany_tree1");
    private static final ResourceLocation MAHOGANY_TREE2 = ResourceLocation.parse("hexerei:mahogany_tree2");
    private static final ResourceLocation MAHOGANY_TREE3 = ResourceLocation.parse("hexerei:mahogany_tree3");
    private static final ResourceLocation MAHOGANY_TREE4 = ResourceLocation.parse("hexerei:mahogany_tree4");
    private static final ResourceLocation[] MAHOGANY_TREE = new ResourceLocation[]{MAHOGANY_TREE1, MAHOGANY_TREE2, MAHOGANY_TREE3, MAHOGANY_TREE4};

    public HexereiMahoganyTreeFeature(Codec codec) {
        super(codec);
    }

    public static boolean isAirOrLeavesAt(LevelSimulatedReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, (state) -> {
            return state.isAir() || state.is(BlockTags.LEAVES);
        });
    }

    public static boolean isAirOrLeavesOrLogsAt(LevelSimulatedReader reader, BlockPos pos) {

        return reader.isStateAtPosition(pos, (state) -> state.canBeReplaced() || state.isAir() || state.is(Blocks.BAMBOO) || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS));
    }

    private static boolean isDirtOrFarmlandAt(LevelSimulatedReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, (state) -> {
            Block block = state.getBlock();
            return isDirt(state) || block == Blocks.FARMLAND;
        });
    }
    //    (WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config)
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel reader = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();


        int i = rand.nextInt(MAHOGANY_TREE.length);

        if (!isDirtOrFarmlandAt(reader, pos.below()))
            return false;

        for(int j = 0; j < 8; j++) {

            BlockPos upPos = new BlockPos(pos).above(j + 1);

            if (!isAirOrLeavesOrLogsAt(reader, upPos)) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.south())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().south())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().south())) {
                return false;
            }
        }


//        if (isAirOrLeavesOrLogsAt(reader, pos.below().north()))
//            return false;
//        if (isAirOrLeavesOrLogsAt(reader, pos.below().south()))
//            return false;
//        if (isAirOrLeavesOrLogsAt(reader, pos.below().east()))
//            return false;
//        if (isAirOrLeavesOrLogsAt(reader, pos.below().west()))
//            return false;

        BlockRotProcessor BlockRotProcessor = new BlockRotProcessor(0.9F);

        StructureTemplateManager templatemanager = reader.getLevel().getServer().getStructureManager();
        StructureTemplate template = templatemanager.getOrCreate(MAHOGANY_TREE[i]);

        if (template == null) {
            Hexerei.LOGGER.error("Identifier to the specified nbt file was not found! : {}", MAHOGANY_TREE[i]);
            return false;
        }
        Rotation rotation = Rotation.getRandom(rand);

        // For proper offsetting the feature so it rotate properly around position parameter.
        BlockPos halfLengths = new BlockPos(
                template.getSize().getX() / 2,
                template.getSize().getY() / 2,
                template.getSize().getZ() / 2);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(pos);

        StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setRotation(rotation).setRotationPivot(halfLengths).setIgnoreEntities(false);

//        Optional<StructureProcessorList> processor = reader.getLevel().getServer().registryAccess().registry(Registries.PROCESSOR_LIST).get().getOptional(
//                HexereiUtil.getResource("mangrove_tree/mangrove_tree_legs"));
//        // add all processors
//        processor.ifPresent(structureProcessorList -> structureProcessorList.list().forEach(placementsettings::addProcessor));

        BlockPos pos1 = mutable.set(pos).move(-halfLengths.getX(), 0, -halfLengths.getZ());
        template.placeInWorld(reader, pos1, pos1, placementsettings, rand, 2);

        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {

            BlockPos.MutableBlockPos pos2 = new BlockPos.MutableBlockPos().set(pos.below().relative(direction));
            int count = 0;
            int length = rand.nextInt(3) + 1;
            int branch = rand.nextInt(length) + 1;
            while (isAirOrLeavesOrLogsAt(reader, pos2) && count <= length) {
                if (reader instanceof Level level)
                    level.setBlockAndUpdate(pos2, ModBlocks.MAHOGANY_LOG.get().defaultBlockState());
                else
                    reader.setBlock(pos2, ModBlocks.MAHOGANY_LOG.get().defaultBlockState(), 3);

                if (branch == count)
                    pos2.move(direction);
                pos2.move(Direction.DOWN);
                count++;
            }

        }


        return true;
    }

}