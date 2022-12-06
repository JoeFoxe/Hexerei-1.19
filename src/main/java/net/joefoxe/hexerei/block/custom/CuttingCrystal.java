package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CuttingCrystalTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CuttingCrystal extends Block implements ITileEntity<CuttingCrystalTile>, EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(LIT, false);
    }

    public static final VoxelShape SHAPE = Block.box(2, 0, 5, 14, 16, 11);

    public static final VoxelShape SHAPE_TURNED = Block.box(5, 0, 2, 11, 16, 14);

    private boolean posEquals(BlockPos pos, BlockPos pos2){
        return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if(blockentity instanceof CuttingCrystalTile cuttingCrystalTile) {
                if (!cuttingCrystalTile.boundPos.isEmpty())
                    cuttingCrystalTile.boundPos.remove(pPos);
                for(BlockPos pos : cuttingCrystalTile.boundPos){
                    if(pLevel.getBlockEntity(pos) instanceof CuttingCrystalTile cuttingCrystalTile1){
                        cuttingCrystalTile1.boundPos = cuttingCrystalTile.boundPos;
                        cuttingCrystalTile1.sync();
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @org.jetbrains.annotations.Nullable LivingEntity pPlacer, ItemStack pStack) {

        withTileEntityDo(pLevel, pPos, te -> {

            if(pStack.hasTag()){
                CompoundTag tag = pStack.getOrCreateTag();
                if(tag.contains("boundPos")) {
                    BlockPos blockPos = NbtUtils.readBlockPos(tag.getCompound("boundPos"));
                    if(pLevel.getBlockState(blockPos).getBlock() instanceof CuttingCrystal cuttingCrystal) {

                        cuttingCrystal.withTileEntityDo(pLevel, blockPos, cuttingCrystalTile -> {
                            if(!cuttingCrystalTile.boundPos.contains(pPos))
                                cuttingCrystalTile.boundPos.add(pPos);
                            if(!cuttingCrystalTile.boundPos.contains(blockPos))
                                cuttingCrystalTile.boundPos.add(blockPos);
//                            cuttingCrystalTile.boundPos.removeIf(pos -> !(pLevel.getBlockEntity(pos) instanceof CuttingCrystalTile));
                            for(BlockPos pos : cuttingCrystalTile.boundPos){
                                if((pLevel.getBlockEntity(pos) instanceof CuttingCrystalTile cuttingCrystalTile1)){
                                    cuttingCrystalTile1.boundPos = cuttingCrystalTile.boundPos;
                                }
                            }

                        });

                    }

                }
            }
//            te.readInventory(pStack.getOrCreateTag()
//                    .getCompound("Inventory"));
//
//            te.setDyeColor(Coffer.getColorStatic(pStack));
//
//            te.buttonToggled = pStack.getOrCreateTag()
//                    .getInt("ButtonToggled");
            te.sync();
        });
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }



    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        if (p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST || p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
            return SHAPE_TURNED;
        return SHAPE;
    }


    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);


        if (player.getItemInHand(handIn).is(ModItems.CROW_FLUTE.get()) && player.getItemInHand(handIn).getOrCreateTag().getInt("commandMode") == 2) {
            player.getItemInHand(handIn).useOn(new UseOnContext(player, handIn, hit));
            return InteractionResult.SUCCESS;
        }

        ItemStack itemstack = player.getItemInHand(handIn);
        Random random = new Random();
        if (tileEntity instanceof CuttingCrystalTile tile) {

            if(itemstack.getItem() == ModBlocks.CUTTING_CRYSTAL.get().asItem()){
                CompoundTag tag = itemstack.getOrCreateTag();
                tag.put("boundPos", NbtUtils.writeBlockPos(tile.getBlockPos()));
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    public CuttingCrystal(Properties properties) {
        super(properties.noOcclusion());
        this.withPropertiesOf(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED, LIT);
    }

//
//    @SuppressWarnings("deprecation")
//    @Override
//    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
//        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
//        return canSupportCenter(worldIn, pos.below(), Direction.UP);
//    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            MutableComponent string = (MutableComponent) Component.translatable(HexConfig.SAGE_BURNING_PLATE_RANGE.get() + "").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)));
            MutableComponent itemText = (MutableComponent) Component.translatable(ModItems.DRIED_SAGE_BUNDLE.get().getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_1", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_3", string).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (state.getBlock() != newState.getBlock()) {
//            BlockEntity tileentity = level.getBlockEntity(pos);
//            if (tileentity != null) {
//                CuttingCrystalTile te = (CuttingCrystalTile) level.getBlockEntity(pos);
//
//                if(!te.getItems().get(0).isEmpty())
//                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, te.getItems().get(0)));
//            }
//            super.onRemove(state, level, pos, newState, isMoving);
//        }
//    }

    @Override
    public Class<CuttingCrystalTile> getTileEntityClass() {
        return CuttingCrystalTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingCrystalTile(ModTileEntities.CUTTING_CRYSTAL_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.CUTTING_CRYSTAL_TILE.get() ?
                (world2, pos, state2, entity) -> ((CuttingCrystalTile)entity).tick() : null;
    }
}
