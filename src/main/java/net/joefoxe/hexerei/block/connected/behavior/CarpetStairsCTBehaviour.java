package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;

public class CarpetStairsCTBehaviour extends ConnectedTextureBehaviour.Base {

    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;

    public CarpetStairsCTBehaviour(CTSpriteShiftEntry layerShift) {
        this(layerShift, null);
    }


    private boolean posEquals(BlockPos pos, BlockPos pos2){
        return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
    }


    public static boolean checkLeft(BlockState stateIn, BlockPos currentPos, BlockAndTintGetter worldIn)
    {
        if(stateIn.hasProperty(StairBlock.FACING)) {
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.north()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.south()).getBlock() instanceof ConnectingCarpetStairs;
        }
        return false;
    }

    public boolean checkRight(BlockState stateIn, BlockPos currentPos, BlockAndTintGetter worldIn)
    {

        if(stateIn.hasProperty(StairBlock.FACING)){
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.south()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() instanceof ConnectingCarpetStairs;
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.north()).getBlock() instanceof ConnectingCarpetStairs;
        }
        return false;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {

        BlockState below = reader.getBlockState(pos.below());

        if (below.hasProperty(StairBlock.FACING)) {
            Direction facing = state.getValue(StairBlock.FACING);
            if(posEquals(otherPos, pos.above())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.above().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.above().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.above().east()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.above().west()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.above().south())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.above().south().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.above().south().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.above().south().east()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.above().south().west()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.above().north())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.above().north().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.above().north().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.above().north().east()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.above().north().west()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.above().east())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.above().east().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.above().east().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.above().east().east()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.above().east().west()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.above().west())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.above().west().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.above().west().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.above().west().east()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.above().west().west()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.below().south())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.below().south().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.below().south().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.below().south().west()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.below().south().east()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.below().north())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.below().north().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.below().north().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.below().north().west()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.below().north().east()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.below().east())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.below().east().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.below().east().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.below().east().west()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.below().east().east()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.below().west())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.below().west().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.below().west().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.below().west().west()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.below().west().east()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.below())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.below().south()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.below().north()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.below().west()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.below().east()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.north())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.north().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.north().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.north().east())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.north().east().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.north().east().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.north().west())) {
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.north().west().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.north().west().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.south())) {
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.south().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.south().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.south().east())) {
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.south().east().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.south().east().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.south().west())) {
                if (facing == Direction.SOUTH)
                    return reader.getBlockState(pos.south().west().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.NORTH)
                    return reader.getBlockState(pos.south().west().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.east())) {
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.east().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.east().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.east().north())) {
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.east().north().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.east().north().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.east().south())) {
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.east().south().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.east().south().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.west())) {
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.west().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.west().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.west().north())) {
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.west().north().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.west().north().below()).getBlock() instanceof CarpetBlock;
            }
            if(posEquals(otherPos, pos.west().south())) {
                if (facing == Direction.WEST)
                    return reader.getBlockState(pos.west().south().above()).getBlock() instanceof CarpetBlock;
                if (facing == Direction.EAST)
                    return reader.getBlockState(pos.west().south().below()).getBlock() instanceof CarpetBlock;
            }
        }
        if(below.hasProperty(StairBlock.HALF) && below.getValue(StairBlock.HALF) == Half.BOTTOM) {

        }
        if (state.getBlock() == other.getBlock() && other.hasProperty(StairBlock.FACING)) {
            Direction facing = state.getValue(StairBlock.FACING);
            Direction facing2 = other.getValue(StairBlock.FACING);
            return facing == facing2 && pos.getY() == otherPos.getY();

        }

        return state.getBlock() == other.getBlock() && pos.getY() == otherPos.getY();
    }

    public CarpetStairsCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
        this.layerShift = layerShift;
        this.topShift = topShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return direction.getAxis()
                .isHorizontal() ? layerShift : topShift;
    }

}