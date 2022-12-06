package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ConnectingCarpetStairs extends CarpetBlock implements Waxed {

    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty LEFT = BooleanProperty.create("left");

    public static BooleanProperty WEST = BooleanProperty.create("west"),
            EAST = BooleanProperty.create("east");
    public static final EnumProperty<North> NORTH = EnumProperty.create("north", North.class);
    public static final EnumProperty<South> SOUTH = EnumProperty.create("south", South.class);

//    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;


    public Block parentBlock;
    protected static final VoxelShape VOXEL_SHAPE = Stream.of(
            Block.box(8, 0, 2, 16, 1, 14),
            Block.box(7, -7, 2, 8, 0, 14),
            Block.box(-1, -16, 2, 0, -8, 14),
            Block.box(0, -8, 2, 8, -7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE90 = Stream.of(
            Block.box(0, 0, 2, 8, 1, 14),
            Block.box(8, -7, 2, 9, 0, 14),
            Block.box(16, -16, 2, 17, -8, 14),
            Block.box(8, -8, 2, 16, -7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE180 = Stream.of(
            Block.box(2, 0, 8, 14, 1, 16),
            Block.box(2, -7, 7, 14, 0, 8),
            Block.box(2, -16, -1, 14, -8, 0),
            Block.box(2, -8, 0, 14, -7, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE270 = Stream.of(
            Block.box(2, 0, 0, 14, 1, 8),
            Block.box(2, -7, 8, 14, 0, 9),
            Block.box(2, -16, 16, 14, -8, 17),
            Block.box(2, -8, 8, 14, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    protected static final VoxelShape VOXEL_SHAPE_LEFT = Stream.of(
            Block.box(8, 0, 2, 16, 1, 16),
            Block.box(7, -7, 2, 8, 0, 16),
            Block.box(-1, -16, 2, 0, -8, 16),
            Block.box(0, -8, 2, 8, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE90_LEFT = Stream.of(
            Block.box(0, 0, 2, 8, 1, 16),
            Block.box(8, -7, 2, 9, 0, 16),
            Block.box(16, -16, 2, 17, -8, 16),
            Block.box(8, -8, 2, 16, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE180_LEFT = Stream.of(
            Block.box(2, 0, 8, 16, 1, 16),
            Block.box(2, -7, 7, 16, 0, 8),
            Block.box(2, -16, -1, 16, -8, 0),
            Block.box(2, -8, 0, 16, -7, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE270_LEFT = Stream.of(
            Block.box(2, 0, 0, 16, 1, 8),
            Block.box(2, -7, 8, 16, 0, 9),
            Block.box(2, -16, 16, 16, -8, 17),
            Block.box(2, -8, 8, 16, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();



    protected static final VoxelShape VOXEL_SHAPE_RIGHT = Stream.of(
            Block.box(8, 0, 0, 16, 1, 14),
            Block.box(7, -7, 0, 8, 0, 14),
            Block.box(-1, -16, 0, 0, -8, 14),
            Block.box(0, -8, 0, 8, -7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE90_RIGHT = Stream.of(
            Block.box(0, 0, 0, 8, 1, 14),
            Block.box(8, -7, 0, 9, 0, 14),
            Block.box(16, -16, 0, 17, -8, 14),
            Block.box(8, -8, 0, 16, -7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE180_RIGHT = Stream.of(
            Block.box(0, 0, 8, 14, 1, 16),
            Block.box(0, -7, 7, 14, 0, 8),
            Block.box(0, -16, -1, 14, -8, 0),
            Block.box(0, -8, 0, 14, -7, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE270_RIGHT = Stream.of(
            Block.box(0, 0, 0, 14, 1, 8),
            Block.box(0, -7, 8, 14, 0, 9),
            Block.box(0, -16, 16, 14, -8, 17),
            Block.box(0, -8, 8, 14, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();



    protected static final VoxelShape VOXEL_SHAPE_FULL = Stream.of(
            Block.box(8, 0, 0, 16, 1, 16),
            Block.box(7, -7, 0, 8, 0, 16),
            Block.box(-1, -16, 0, 0, -8, 16),
            Block.box(0, -8, 0, 8, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE90_FULL = Stream.of(
            Block.box(0, 0, 0, 8, 1, 16),
            Block.box(8, -7, 0, 9, 0, 16),
            Block.box(16, -16, 0, 17, -8, 16),
            Block.box(8, -8, 0, 16, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE180_FULL = Stream.of(
            Block.box(0, 0, 8, 16, 1, 16),
            Block.box(0, -7, 7, 16, 0, 8),
            Block.box(0, -16, -1, 16, -8, 0),
            Block.box(0, -8, 0, 16, -7, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected static final VoxelShape VOXEL_SHAPE270_FULL = Stream.of(
            Block.box(0, 0, 0, 16, 1, 8),
            Block.box(0, -7, 8, 16, 0, 9),
            Block.box(0, -16, 16, 16, -8, 17),
            Block.box(0, -8, 8, 16, -7, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {

        if(state.hasProperty(StairBlock.FACING)){
            if(state.getValue(StairBlock.FACING) == Direction.NORTH) {
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE270_LEFT;
                else if(!left && right)
                    return VOXEL_SHAPE270_RIGHT;
                else if(!left)
                    return VOXEL_SHAPE270;
                else return VOXEL_SHAPE270_FULL;
            }
            if(state.getValue(StairBlock.FACING) == Direction.EAST){
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE_LEFT;
                else if(!left && right)
                    return VOXEL_SHAPE_RIGHT;
                else if(!left)
                    return VOXEL_SHAPE;
                else return VOXEL_SHAPE_FULL;
            }
            if(state.getValue(StairBlock.FACING) == Direction.SOUTH){
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE180_LEFT;
                else if(!left && right)
                    return VOXEL_SHAPE180_RIGHT;
                else if(!left)
                    return VOXEL_SHAPE180;
                else return VOXEL_SHAPE180_FULL;
            }
            if(state.getValue(StairBlock.FACING) == Direction.WEST){
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE90_LEFT;
                else if(!left && right)
                    return VOXEL_SHAPE90_RIGHT;
                else if(!left)
                    return VOXEL_SHAPE90;
                else return VOXEL_SHAPE90_FULL;
            }
        }
        return VOXEL_SHAPE;
    }

    public static boolean checkLeft(BlockState stateIn, BlockPos currentPos, LevelAccessor worldIn)
    {
        if(stateIn.hasProperty(StairBlock.FACING)) {
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
        }
        return false;
    }

    public boolean checkRight(BlockState stateIn, BlockPos currentPos, LevelAccessor worldIn)
    {

        if(stateIn.hasProperty(StairBlock.FACING)){
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING);
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos blockpos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if(player.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
            DyeColor dyecolor = dyeItem.getDyeColor();
              if(this.parentBlock instanceof ConnectingCarpetDyed carpetDyed && carpetDyed.dyeColor == dyecolor)
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }
            BlockState newBlockstate = getBlockByColor(dyecolor).defaultBlockState().setValue(StairBlock.FACING, pLevel.getBlockState(blockpos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(pState, blockpos, pLevel))
                    .setValue(LEFT, checkLeft(pState, blockpos, pLevel));

            if(!player.isCreative())
                player.getItemInHand(pHand).shrink(1);
            if(!player.isCreative() && pState.getBlock() == ModBlocks.CARPET_STAIRS.get())
                Block.popResource(pLevel, blockpos, new ItemStack(Items.GOLD_NUGGET));

            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }
        else if(player.getItemInHand(pHand).getItem() == Items.GOLD_NUGGET) {
            if(pState.getBlock() == ModBlocks.CARPET_STAIRS.get())
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }
            BlockState newBlockstate = ModBlocks.CARPET_STAIRS.get().defaultBlockState();
            if(!player.isCreative())
                player.getItemInHand(pHand).shrink(1);

            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3004, blockpos, 0);
            pLevel.playSound(player, blockpos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }

        return super.use(pState, pLevel, blockpos, player, pHand, pHit);
    }

    //                    if(player.getItemInHand(pHand).getItem() instanceof DyeItem)
//	{
//		DyeColor dyecolor = ((DyeItem)itemstack.getItem()).getDyeColor();


    public static Block getBlockByColor(@Nullable DyeColor pColor) {
        if (pColor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            switch (pColor) {
                case WHITE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE_STAIRS.get();
                case ORANGE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE_STAIRS.get();
                case MAGENTA:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA_STAIRS.get();
                case LIGHT_BLUE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE_STAIRS.get();
                case YELLOW:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW_STAIRS.get();
                case LIME:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME_STAIRS.get();
                case PINK:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK_STAIRS.get();
                case GRAY:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY_STAIRS.get();
                case LIGHT_GRAY:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY_STAIRS.get();
                case CYAN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN_STAIRS.get();
                case PURPLE:
                default:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE_STAIRS.get();
                case BLUE:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE_STAIRS.get();
                case BROWN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN_STAIRS.get();
                case GREEN:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN_STAIRS.get();
                case RED:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED_STAIRS.get();
                case BLACK:
                    return ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK_STAIRS.get();
            }
        }
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return getUnWaxed(state, context, toolAction);
    }
    public ConnectingCarpetStairs(Properties pProperties){
        super(pProperties.noOcclusion());
        this.parentBlock = this;
    }
    public ConnectingCarpetStairs(Properties pProperties, Block block){
        super(pProperties.noOcclusion());
        this.parentBlock = block;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(this.parentBlock);
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
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Level level = context.getLevel();


        if(level.getBlockState(pos.below()).getBlock() instanceof StairBlock && level.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM)
        {
            return this.defaultBlockState()
                    .setValue(StairBlock.FACING, level.getBlockState(pos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(state, pos, level))
                    .setValue(LEFT, checkLeft(state, pos, level))
                    ;
        }

        return defaultBlockState();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StairBlock.FACING, RIGHT, LEFT);
    }



    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        if(world.getBlockState(pos.below()).getBlock() instanceof StairBlock && world.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM)
        {
            return this.defaultBlockState()
                    .setValue(StairBlock.FACING, world.getBlockState(pos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(state, pos, world))
                    .setValue(LEFT, checkLeft(state, pos, world))
                    ;
        }

        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : defaultBlockState();
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
