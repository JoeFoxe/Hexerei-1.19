package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.BroomStandTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class BroomStandWall extends BroomStand implements ITileEntity<BroomStandTile>, EntityBlock, SimpleWaterloggedBlock {
    VoxelShape shape = Stream.of(
            Block.box(2, 2, 0, 14, 14, 3),
            Block.box(10, 3, 3, 13, 6, 8),
            Block.box(3, 3, 3, 6, 6, 8)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_90 = Stream.of(
            Block.box(0, 2, 2, 3, 14, 14),
            Block.box(3, 3, 3, 8, 6, 6),
            Block.box(3, 3, 10, 8, 6, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_180 = Stream.of(
            Block.box(2, 2, 13, 14, 14, 16),
            Block.box(3, 3, 8, 6, 6, 13),
            Block.box(10, 3, 8, 13, 6, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_270 = Stream.of(
            Block.box(13, 2, 2, 16, 14, 14),
            Block.box(8, 3, 10, 13, 6, 13),
            Block.box(8, 3, 3, 13, 6, 6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public BroomStandWall(Properties pProperties){
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(WATERLOGGED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        if (context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN)
            return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getClickedFace().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(!pState.hasProperty(HorizontalDirectionalBlock.FACING))
            return shape;

        Direction dir = pState.getValue(HorizontalDirectionalBlock.FACING);

        return switch (dir){
            case DOWN, UP, NORTH -> shape;
            case SOUTH -> shape_180;
            case WEST -> shape_90;
            case EAST -> shape_270;
        };
    }
}
