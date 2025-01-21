package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ConnectingCarpetStairs extends CarpetBlock implements Waxed, CTDyable {

    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty LEFT = BooleanProperty.create("left");

    public static BooleanProperty WEST = BooleanProperty.create("west"),
            EAST = BooleanProperty.create("east");
    public static final EnumProperty<North> NORTH = EnumProperty.create("north", North.class);
    public static final EnumProperty<South> SOUTH = EnumProperty.create("south", South.class);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

//    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;

    @Override
    public DyeColor getDyeColor(BlockState blockState) {
        if (blockState.hasProperty(COLOR))
            return blockState.getValue(COLOR);
        return DyeColor.WHITE;
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        boolean east = pState.getValue(EAST);
        boolean west = pState.getValue(WEST);
        North northState = pState.getValue(NORTH);
        South southState = pState.getValue(SOUTH);
        boolean north = northState == North.ALL || northState == North.JUST_NORTH || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_AND_NORTH_WEST;
        boolean north_east = northState == North.ALL || northState == North.JUST_NORTH_EAST || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean north_west = northState == North.ALL || northState == North.JUST_NORTH_WEST || northState == North.NORTH_AND_NORTH_WEST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean south = southState == South.ALL || southState == South.JUST_SOUTH || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_WEST;
        boolean south_east = southState == South.ALL || southState == South.JUST_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;
        boolean south_west = southState == South.ALL || southState == South.JUST_SOUTH_WEST || southState == South.SOUTH_AND_SOUTH_WEST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;

        switch (pRot){
            case NONE -> {
                return pState;
            }
            case CLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(south_east && east && north_east)
                    southTemp = South.ALL;
                else if (!south_east && east && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (south_east && east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (south_east && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!south_east && east)
                    southTemp = South.JUST_SOUTH;
                else if (!south_east && north_east)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (south_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if(south_west && west && north_west)
                    northTemp = North.ALL;
                else if (!south_west && west && north_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south_west && west)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && north_west)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && west)
                    northTemp = North.JUST_NORTH;
                else if (!south_west && north_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_west)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, north).setValue(WEST, south).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
            case CLOCKWISE_180 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if (north && north_east && north_west)
                    southTemp = South.ALL;
                else if (north && north_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && !north_east && north)
                    southTemp = South.JUST_SOUTH;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if (south && south_east && south_west)
                    northTemp = North.ALL;
                else if (south && south_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south && south_east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && !south_east && south)
                    northTemp = North.JUST_NORTH;
                else if (south_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, west).setValue(WEST, east).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);

            }
            case COUNTERCLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(north_west && west && south_west)
                    southTemp = South.ALL;
                else if (!north_west && west && south_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north_west && west)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && south_west)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && west)
                    southTemp = South.JUST_SOUTH;
                else if (!north_west && south_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_WEST;

                if(north_east && east && south_east)
                    northTemp = North.ALL;
                else if (!north_east && east && south_east)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (north_east && east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (north_east && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!north_east && east)
                    northTemp = North.JUST_NORTH;
                else if (!north_east && south_east)
                    northTemp = North.JUST_NORTH_EAST;
                else if (north_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, south).setValue(WEST, north).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
        }
        return pState;
//        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }
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



    protected static final VoxelShape VOXEL_SHAPE_THICK_N = Stream.of(
            Block.box(0, 0, 0, 16, 3, 11),
            Block.box(0, -8, 8, 16, 0, 11),
            Block.box(0, -8, 11, 16, -5, 19),
            Block.box(0, -16, 16, 16, -8, 19)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape VOXEL_SHAPE_THICK_E = Stream.of(
            Block.box(5, 0, 0, 16, 3, 16),
            Block.box(5, -8, 0, 8, 0, 16),
            Block.box(-3, -8, 0, 5, -5, 16),
            Block.box(-3, -16, 0, 0, -8, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape VOXEL_SHAPE_THICK_S = Stream.of(
            Block.box(0, 0, 5, 16, 3, 16),
            Block.box(0, -8, 5, 16, 0, 8),
            Block.box(0, -8, -3, 16, -5, 5),
            Block.box(0, -16, -3, 16, -8, 0)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape VOXEL_SHAPE_THICK_W = Stream.of(
            Block.box(0, 0, 0, 11, 3, 16),
            Block.box(8, -8, 0, 11, 0, 16),
            Block.box(11, -8, 0, 19, -5, 16),
            Block.box(16, -16, 0, 19, -8, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {

        if(state.hasProperty(StairBlock.FACING)){
            if(state.getValue(StairBlock.FACING) == Direction.NORTH) {
                return VOXEL_SHAPE_THICK_N;
            }
            if(state.getValue(StairBlock.FACING) == Direction.EAST){
                return VOXEL_SHAPE_THICK_E;
            }
            if(state.getValue(StairBlock.FACING) == Direction.SOUTH){
                return VOXEL_SHAPE_THICK_S;
            }
            if(state.getValue(StairBlock.FACING) == Direction.WEST){
                return VOXEL_SHAPE_THICK_W;
            }
        }
        return VOXEL_SHAPE_THICK_N;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        if(state.hasProperty(StairBlock.FACING)){
            if(state.getValue(StairBlock.FACING) == Direction.NORTH) {
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE270_RIGHT;
                else if(!left && right)
                    return VOXEL_SHAPE270_LEFT;
                else if(!left)
                    return VOXEL_SHAPE270;
                else return VOXEL_SHAPE270_FULL;
            }
            if(state.getValue(StairBlock.FACING) == Direction.EAST){
                boolean left = state.getValue(LEFT);
                boolean right = state.getValue(RIGHT);
                if(left && !right)
                    return VOXEL_SHAPE_RIGHT;
                else if(!left && right)
                    return VOXEL_SHAPE_LEFT;
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
        if(stateIn.hasProperty(StairBlock.FACING) && stateIn.hasProperty(COLOR)) {
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.west()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.north()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.east()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.south()).getValue(COLOR) == stateIn.getValue(COLOR);
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        if (!pState.hasProperty(COLOR))
            return drops;
        List<ItemStack> updated_drops = new ArrayList<>();
        for (ItemStack stack : drops){
            if (stack.getItem() == ModBlocks.INFUSED_FABRIC_CARPET.get().asItem() || stack.getItem() == ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get().asItem()){
                DyeColor color = pState.getValue(COLOR);
                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                tag.putString("color", color.getName());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
            updated_drops.add(stack);
        }
        return updated_drops;
    }
    public boolean checkRight(BlockState stateIn, BlockPos currentPos, LevelAccessor worldIn)
    {

        if(stateIn.hasProperty(StairBlock.FACING)){
            if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH)
                return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.east()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.EAST)
                return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.south()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH)
                return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.west()).getValue(COLOR) == stateIn.getValue(COLOR);
            else if (stateIn.getValue(StairBlock.FACING) == Direction.WEST)
                return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock() && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING) && worldIn.getBlockState(currentPos.north()).getValue(COLOR) == stateIn.getValue(COLOR);
        }
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyecolor = dyeItem.getDyeColor();
            if(this.getDyeColor(state) == dyecolor)
                return ItemInteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }

            BlockState newBlockstate = level.getBlockState(pos).setValue(StairBlock.FACING, level.getBlockState(pos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(state, pos, level))
                    .setValue(LEFT, checkLeft(state, pos, level)).setValue(COLOR, dyecolor);

            if(state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()) {
                Block.popResource(level, pos, new ItemStack(Items.GOLD_NUGGET));
                newBlockstate = ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get().defaultBlockState().setValue(COLOR, dyecolor);
            }

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlockstate));
            level.levelEvent(player, 3003, pos, 0);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);

        }
        else if(stack.getItem() == Items.GOLD_NUGGET) {
            if(state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get())
                return ItemInteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }
            BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get().defaultBlockState();
            if(!player.isCreative())
                stack.shrink(1);

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlockstate));
            level.levelEvent(player, 3004, pos, 0);
            level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);

        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {

        return getUnWaxed(state, context, itemAbility)
                .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                .setValue(RIGHT, state.getValue(RIGHT))
                .setValue(LEFT, state.getValue(LEFT))
                .setValue(COLOR, state.getValue(COLOR));
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = this.parentBlock.asItem().getDefaultInstance();
        DyeColor color = getDyeColor(state);
        if (color != DyeColor.WHITE) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putString("color", color.getName());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
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
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Level level = context.getLevel();


        if(level.getBlockState(pos.below()).getBlock() instanceof StairBlock && level.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM)
        {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            String colorName = tag.contains("color") ? tag.getString("color") : "";
            DyeColor color = DyeColor.byName(colorName, DyeColor.WHITE);
            return this.defaultBlockState()
                    .setValue(StairBlock.FACING, level.getBlockState(pos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(state, pos, level))
                    .setValue(LEFT, checkLeft(state, pos, level))
                    .setValue(COLOR, color)
                    ;
        }

        return defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StairBlock.FACING, RIGHT, LEFT, COLOR);
    }



    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        if(world.getBlockState(pos.below()).getBlock() instanceof StairBlock && world.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM)
        {
            return this.defaultBlockState()
                    .setValue(StairBlock.FACING, world.getBlockState(pos.below()).getValue(StairBlock.FACING))
                    .setValue(RIGHT, checkRight(state, pos, world))
                    .setValue(LEFT, checkLeft(state, pos, world))
                    .setValue(COLOR, state.getValue(COLOR))
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
