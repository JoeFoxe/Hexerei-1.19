package net.joefoxe.hexerei.world.processor;


import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Dynamically generates support legs.
 * Yellow stained glass is used to mark the corner positions where the legs will spawn for simplicity.
 */
@MethodsReturnNonnullByDefault
public class NatureCovenLegProcessor extends StructureProcessor {
    public static final NatureCovenLegProcessor INSTANCE = new NatureCovenLegProcessor();
    public static final Codec<NatureCovenLegProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @ParametersAreNonnullByDefault
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, StructureTemplate.StructureBlockInfo blockInfoLocal, StructureTemplate.StructureBlockInfo blockInfoGlobal, StructurePlaceSettings structurePlacementData, @Nullable StructureTemplate template) {
        if (blockInfoGlobal.state.getBlock() == Blocks.YELLOW_STAINED_GLASS_PANE) {
            ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos);
            ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
            RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos);

            // Always replace the glass itself with witch hazel pillar
            currentChunk.setBlockState(blockInfoGlobal.pos, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState(), false);
            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState(), blockInfoGlobal.nbt);

            // Generate vertical pillar down
            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos.below().mutable();
            BlockState currBlock = worldReader.getBlockState(mutable);
            int itor = 0;
            while (mutable.getY() > 0 && (currBlock.getMaterial() == Material.AIR || currBlock.getMaterial() == Material.WATER || currBlock.getMaterial() == Material.LAVA)) {
                if(itor != 1)
                    currentChunk.setBlockState(mutable, ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get().defaultBlockState(), false);
                else
                    currentChunk.setBlockState(mutable, ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get().defaultBlockState(), false);
                mutable.move(Direction.DOWN);
                currBlock = worldReader.getBlockState(mutable);

                if(!(currBlock.getMaterial() == Material.AIR || currBlock.getMaterial() == Material.WATER || currBlock.getMaterial() == Material.LAVA)) {
                    currentChunk.setBlockState(mutable.above(), Blocks.MUD_BRICKS.defaultBlockState(), false);
                    break;
                }

                itor++;
            }
        } else if (blockInfoGlobal.state.getBlock() == Blocks.RED_STAINED_GLASS_PANE) {
            ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos);
            ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
            RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos);

            // Always replace the observer itself with a ladder and grab the correct facing of the ladder from the ladder placed above
            BlockState newState = ModBlocks.WILLOW_VINES_PLANT.get().defaultBlockState();
            currentChunk.setBlockState(blockInfoGlobal.pos, newState, false);

            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos, newState, blockInfoGlobal.nbt);

            // Generate vertical pillar down
            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos.below().mutable();
            BlockState currBlock = worldReader.getBlockState(mutable);
            while (mutable.getY() > 0 && (currBlock.getMaterial() == Material.AIR || currBlock.getMaterial() == Material.WATER || currBlock.getMaterial() == Material.LAVA)) {
                currentChunk.setBlockState(mutable, newState, false);
                mutable.move(Direction.DOWN);
                currBlock = worldReader.getBlockState(mutable);

                if(!(currBlock.getMaterial() == Material.AIR || currBlock.getMaterial() == Material.WATER || currBlock.getMaterial() == Material.LAVA)) {
                    currentChunk.setBlockState(mutable.above(), ModBlocks.WILLOW_VINES.get().defaultBlockState(), false);
                    break;
                }
            }
        }

        return blockInfoGlobal;
    }

    protected StructureProcessorType<?> getType() {
        return Hexerei.NATURE_COVEN_LEG_PROCESSOR;
    }
}

