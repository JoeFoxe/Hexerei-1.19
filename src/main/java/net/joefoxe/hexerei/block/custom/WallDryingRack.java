package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.joefoxe.hexerei.tileentity.DryingRackTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

public class WallDryingRack extends HerbDryingRack {


    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return updateSides(iblockreader, blockpos, super.getStateForPlacement(context)).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
//        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 6, 12, 14, 7, 14),
            Block.box(1, 5, 14, 15, 9, 15),
            Block.box(0, 4, 15, 16, 10, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_90 = Stream.of(
            Block.box(2, 6, 2, 4, 7, 14),
            Block.box(1, 5, 1, 2, 9, 15),
            Block.box(0, 4, 0, 1, 10, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_180 = Stream.of(
            Block.box(2, 6, 2, 14, 7, 4),
            Block.box(1, 5, 1, 15, 9, 2),
            Block.box(0, 4, 0, 16, 10, 1)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_270 = Stream.of(
            Block.box(12, 6, 2, 14, 7, 14),
            Block.box(14, 5, 1, 15, 9, 15),
            Block.box(15, 4, 0, 16, 10, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_LEFT = Shapes.join(Block.box(14, 6, 12, 16, 7, 14), Block.box(15, 5, 14, 16, 9, 15), BooleanOp.OR);
    public static final VoxelShape SHAPE_LEFT_90 = Shapes.join(Block.box(2, 6, 14, 4, 7, 16), Block.box(1, 5, 15, 2, 9, 16), BooleanOp.OR);
    public static final VoxelShape SHAPE_LEFT_180 = Shapes.join(Block.box(0, 6, 2, 2, 7, 4), Block.box(0, 5, 1, 1, 9, 2), BooleanOp.OR);
    public static final VoxelShape SHAPE_LEFT_270 = Shapes.join(Block.box(12, 6, 0, 14, 7, 2), Block.box(14, 5, 0, 15, 9, 1), BooleanOp.OR);

    public static final VoxelShape SHAPE_RIGHT = Shapes.join(Block.box(0, 6, 12, 2, 7, 14), Block.box(0, 5, 14, 1, 9, 15), BooleanOp.OR);
    public static final VoxelShape SHAPE_RIGHT_90 = Shapes.join(Block.box(2, 6, 0, 4, 7, 2), Block.box(1, 5, 0, 2, 9, 1), BooleanOp.OR);
    public static final VoxelShape SHAPE_RIGHT_180 = Shapes.join(Block.box(14, 6, 2, 16, 7, 4), Block.box(15, 5, 1, 16, 9, 2), BooleanOp.OR);
    public static final VoxelShape SHAPE_RIGHT_270 = Shapes.join(Block.box(12, 6, 14, 14, 7, 16), Block.box(14, 5, 15, 15, 9, 16), BooleanOp.OR);

    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, SHAPE, Direction.NORTH, SHAPE_180, Direction.WEST, SHAPE_90, Direction.EAST, SHAPE_270));
    private static final Map<Direction, VoxelShape> AABBS_LEFT = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, SHAPE_LEFT, Direction.NORTH, SHAPE_LEFT_180, Direction.WEST, SHAPE_LEFT_90, Direction.EAST, SHAPE_LEFT_270));
    private static final Map<Direction, VoxelShape> AABBS_RIGHT = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, SHAPE_RIGHT, Direction.NORTH, SHAPE_RIGHT_180, Direction.WEST, SHAPE_RIGHT_90, Direction.EAST, SHAPE_RIGHT_270));

    public static BooleanProperty WEST = BooleanProperty.create("west"),
                                  EAST = BooleanProperty.create("east"),
                                  NORTH = BooleanProperty.create("north"),
                                  SOUTH = BooleanProperty.create("south"),
                                  RIGHT = BooleanProperty.create("right"),
                                  LEFT = BooleanProperty.create("left");

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof DryingRackTile) {
            ((DryingRackTile)tileEntity).interactDryingRack(player, hit);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
        boolean left = pState.getValue(LEFT);
        boolean right = pState.getValue(RIGHT);
        if(!left && right){
            return Stream.of(
                    AABBS_RIGHT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                    AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING))
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        } else if(!right && left) {
            return Stream.of(
                    AABBS_LEFT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                    AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING))
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        } else if(right && left) {
            return Stream.of(
                    AABBS_LEFT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                    AABBS_RIGHT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                    AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING))
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        } else {
            return AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING));
        }
    }
//
//    @Override
//    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {
//
//        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//
//            tooltip.add(Component.translatable("tooltip.hexerei.herb_drying_rack_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//        } else {
//            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
////            tooltip.add(Component.translatable("tooltip.hexerei.herb_drying_rack"));
//        }
//        super.appendHoverText(stack, world, tooltip, flagIn);
//    }

    public WallDryingRack(Properties properties) {

        super(properties.noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED, NORTH, SOUTH, EAST, WEST, LEFT, RIGHT);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


    protected BlockState updateSides(BlockGetter world, BlockPos pos, BlockState state) {
        BlockState bs_north = world.getBlockState(pos.north());
        BlockState bs_east = world.getBlockState(pos.east());
        BlockState bs_south = world.getBlockState(pos.south());
        BlockState bs_west = world.getBlockState(pos.west());


        Direction dir = state.getValue(HorizontalDirectionalBlock.FACING);
        boolean north = bs_north.getBlock() == this && bs_north.hasProperty(HorizontalDirectionalBlock.FACING) && bs_north.getValue(HorizontalDirectionalBlock.FACING) == dir,
                south = bs_south.getBlock() == this && bs_south.hasProperty(HorizontalDirectionalBlock.FACING) && bs_south.getValue(HorizontalDirectionalBlock.FACING) == dir,
                east = bs_east.getBlock() == this && bs_east.hasProperty(HorizontalDirectionalBlock.FACING) && bs_east.getValue(HorizontalDirectionalBlock.FACING) == dir,
                west = bs_west.getBlock() == this && bs_west.hasProperty(HorizontalDirectionalBlock.FACING) && bs_west.getValue(HorizontalDirectionalBlock.FACING) == dir;

        boolean left = (dir == Direction.NORTH && west) || (dir == Direction.WEST && south) || (dir == Direction.SOUTH && east) || (dir == Direction.EAST && north);
        boolean right = (dir == Direction.NORTH && east) || (dir == Direction.WEST && north) || (dir == Direction.SOUTH && west) || (dir == Direction.EAST && south);
        return state
                .setValue(NORTH, north).setValue(EAST, east)
                .setValue(SOUTH, south).setValue(WEST, west)
                .setValue(LEFT, left).setValue(RIGHT, right);
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(updateSides(world, pos, state), facing, facingState, world, pos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.relative(pState.getValue(HorizontalDirectionalBlock.FACING))).getMaterial().isSolid();
    }

    @Override
    public Class<DryingRackTile> getTileEntityClass() {
        return DryingRackTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackTile(ModTileEntities.DRYING_RACK_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.DRYING_RACK_TILE.get() ?
                (world2, pos, state2, entity) -> ((DryingRackTile)entity).tick() : null;
    }

}
