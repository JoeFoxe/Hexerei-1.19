package net.joefoxe.hexerei.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ConnectingTable extends Block implements SimpleWaterloggedBlock {

    VoxelShape TOP = Block.box(0, 12, 0, 16, 16, 16),
            CORNER = Block.box(12, 0, 1, 15, 12, 4),
            CORNER_90 = Block.box(12, 0, 12, 15, 12, 15),
            CORNER_180 = Block.box(1, 0, 12, 4, 12, 15),
            CORNER_270 = Block.box(1, 0, 1, 4, 12, 4),

            INSIDE_CORNER = Stream.of(
                    Block.box(12, 0, 1, 15, 12, 4),
                    Block.box(11, 6, 1, 16, 9, 4),
                    Block.box(12, 6, 0, 15, 9, 5)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            INSIDE_CORNER_90 = Stream.of(
                    Block.box(12, 0, 12, 15, 12, 15),
                    Block.box(12, 6, 11, 15, 9, 16),
                    Block.box(11, 6, 12, 16, 9, 15)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            INSIDE_CORNER_180 = Stream.of(
                    Block.box(1, 0, 12, 4, 12, 15),
                    Block.box(0, 6, 12, 5, 9, 15),
                    Block.box(1, 6, 11, 4, 9, 16)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            INSIDE_CORNER_270 = Stream.of(
                    Block.box(1, 0, 1, 4, 12, 4),
                    Block.box(1, 6, 0, 4, 9, 5),
                    Block.box(0, 6, 1, 5, 9, 4)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            END = Block.box(0, 6, 1, 16, 9, 4),
            END_90 = Block.box(12, 6, 0, 15, 9, 16),
            END_180 = Block.box(0, 6, 12, 16, 9, 15),
            END_270 = Block.box(1, 6, 0, 4, 9, 16);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static BooleanProperty WEST = BooleanProperty.create("west"),
                                  EAST = BooleanProperty.create("east");
    public static final EnumProperty<North> NORTH = EnumProperty.create("north", North.class);
    public static final EnumProperty<South> SOUTH = EnumProperty.create("south", South.class);


    public ConnectingTable(Properties pProperties){
        super(pProperties.noOcclusion());
        registerDefaultState(super.defaultBlockState()
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(NORTH, North.NONE)
                .setValue(SOUTH, South.NONE)
                .setValue(WATERLOGGED, false));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
        boolean west = state.getValue(WEST),
                east = state.getValue(EAST);
        North north = state.getValue(NORTH);
        South south = state.getValue(SOUTH);

        List<VoxelShape> list = new ArrayList<>();
        list.add(TOP);

        if(north.equals(North.NONE) || north.equals(North.JUST_NORTH_WEST) || north.equals(North.JUST_NORTH_EAST) || north.equals(North.NORTH_EAST_AND_NORTH_WEST))
            list.add(END);
        if(!east)
            list.add(END_90);
        if(south.equals(South.NONE) || south.equals(South.JUST_SOUTH_WEST) || south.equals(South.JUST_SOUTH_EAST) || south.equals(South.SOUTH_EAST_AND_SOUTH_WEST))
            list.add(END_180);
        if(!west)
            list.add(END_270);

        if(!east && north != North.JUST_NORTH && north != North.NORTH_AND_NORTH_EAST && north != North.NORTH_AND_NORTH_WEST && north != North.ALL)
            list.add(CORNER);
        if(!east && south != South.JUST_SOUTH && south != South.SOUTH_AND_SOUTH_EAST && south != South.SOUTH_AND_SOUTH_WEST && south != South.ALL)
            list.add(CORNER_90);

        if(!west && north != North.JUST_NORTH && north != North.NORTH_AND_NORTH_EAST && north != North.NORTH_AND_NORTH_WEST && north != North.ALL)
            list.add(CORNER_270);
        if(!west && south != South.JUST_SOUTH && south != South.SOUTH_AND_SOUTH_EAST && south != South.SOUTH_AND_SOUTH_WEST && south != South.ALL)
            list.add(CORNER_180);

        if(west && (north == North.JUST_NORTH || north == North.NORTH_AND_NORTH_EAST))
            list.add(INSIDE_CORNER_270);
        if(west && (south == South.JUST_SOUTH || south == South.SOUTH_AND_SOUTH_EAST))
            list.add(INSIDE_CORNER_180);

        if(east && (north == North.JUST_NORTH || north == North.NORTH_AND_NORTH_WEST))
            list.add(INSIDE_CORNER);
        if(east && (south == South.JUST_SOUTH || south == South.SOUTH_AND_SOUTH_WEST))
            list.add(INSIDE_CORNER_90);

        return list.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
        BlockState bs_north = world.getBlockState(pos.north());
        BlockState bs_north_east = world.getBlockState(pos.north().east());
        BlockState bs_north_west = world.getBlockState(pos.north().west());
        BlockState bs_east = world.getBlockState(pos.east());
        BlockState bs_south = world.getBlockState(pos.south());
        BlockState bs_south_east = world.getBlockState(pos.south().east());
        BlockState bs_south_west = world.getBlockState(pos.south().west());
        BlockState bs_west = world.getBlockState(pos.west());
        North north = North.NONE;
        South south = South.NONE;

        if(bs_north.getBlock() == this){
            north = North.JUST_NORTH;
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() != this){
                north = North.NORTH_AND_NORTH_WEST;
            }
            if(bs_north_west.getBlock() != this && bs_north_east.getBlock() == this){
                north = North.NORTH_AND_NORTH_EAST;
            }
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() == this){
                north = North.ALL;
            }
        }else{
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() != this){
                north = North.JUST_NORTH_WEST;
            }
            if(bs_north_west.getBlock() != this && bs_north_east.getBlock() == this){
                north = North.JUST_NORTH_EAST;
            }
        }
        if(bs_south.getBlock() == this){
            south = South.JUST_SOUTH;
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() != this){
                south = South.SOUTH_AND_SOUTH_WEST;
            }
            if(bs_south_west.getBlock() != this && bs_south_east.getBlock() == this){
                south = South.SOUTH_AND_SOUTH_EAST;
            }
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() == this){
                south = South.ALL;
            }
        }else{
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() != this){
                south = South.JUST_SOUTH_WEST;
            }
            if(bs_south_west.getBlock() != this && bs_south_east.getBlock() == this){
                south = South.JUST_SOUTH_EAST;
            }
        }


        boolean east = bs_east.getBlock() == this,
                west = bs_west.getBlock() == this;
        return state
                .setValue(NORTH, north).setValue(EAST, east)
                .setValue(SOUTH, south).setValue(WEST, west);
    }
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return updateCorners(iblockreader, blockpos, super.getStateForPlacement(context));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WEST, EAST, NORTH, SOUTH, WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return updateCorners(world, pos, state);
    }
    public enum North implements StringRepresentable {
        JUST_NORTH,
        NORTH_AND_NORTH_WEST,
        NORTH_AND_NORTH_EAST,
        JUST_NORTH_WEST,
        JUST_NORTH_EAST,
        NORTH_EAST_AND_NORTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_NORTH -> "north";
                case NORTH_AND_NORTH_WEST -> "north_and_north_west";
                case NORTH_AND_NORTH_EAST -> "north_and_north_east";
                case JUST_NORTH_WEST -> "north_west";
                case JUST_NORTH_EAST -> "north_east";
                case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
    public enum South implements StringRepresentable {
        JUST_SOUTH,
        SOUTH_AND_SOUTH_WEST,
        SOUTH_AND_SOUTH_EAST,
        JUST_SOUTH_WEST,
        JUST_SOUTH_EAST,
        SOUTH_EAST_AND_SOUTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_SOUTH -> "south";
                case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
                case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
                case JUST_SOUTH_WEST -> "south_west";
                case JUST_SOUTH_EAST -> "south_east";
                case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
}
