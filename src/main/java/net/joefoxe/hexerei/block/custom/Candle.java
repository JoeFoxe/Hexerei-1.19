package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class Candle extends AbstractCandleBlock implements ITileEntity<CandleTile>, EntityBlock, SimpleWaterloggedBlock, DyeableLeatherItem {

    public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 4);
    public static final IntegerProperty CANDLES_LIT = IntegerProperty.create("candles_lit", 0, 4);
    public static final IntegerProperty SLOT_ONE_TYPE = IntegerProperty.create("slot_one_type", 0, 10);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty PLAYER_NEAR = BooleanProperty.create("player_near");

    private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = Util.make(() -> {
        Int2ObjectMap<List<Vec3>> int2objectmap = new Int2ObjectOpenHashMap<>();
        int2objectmap.defaultReturnValue(ImmutableList.of());
        int2objectmap.put(1, ImmutableList.of(new Vec3(0.5D, 0.5D, 0.5D)));
        int2objectmap.put(2, ImmutableList.of(new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.625D, 0.5D, 0.44D)));
        int2objectmap.put(3, ImmutableList.of(new Vec3(0.5D, 0.313D, 0.625D), new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.56D, 0.5D, 0.44D)));
        int2objectmap.put(4, ImmutableList.of(new Vec3(0.44D, 0.313D, 0.56D), new Vec3(0.625D, 0.44D, 0.56D), new Vec3(0.375D, 0.44D, 0.375D), new Vec3(0.56D, 0.5D, 0.375D)));
        return Int2ObjectMaps.unmodifiable(int2objectmap);
    });

    protected static final VoxelShape ONE_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 9.0D, 10.0D);
    protected static final VoxelShape TWO_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);
    protected static final VoxelShape THREE_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);
    protected static final VoxelShape FOUR_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {

    }
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        int candleType = 1;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_BLUE.get().asItem())
            candleType = 2;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_BLACK.get().asItem())
            candleType = 3;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_LIME.get().asItem())
            candleType = 4;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_ORANGE.get().asItem())
            candleType = 5;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_PINK.get().asItem())
            candleType = 6;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_PURPLE.get().asItem())
            candleType = 7;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_RED.get().asItem())
            candleType = 8;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_CYAN.get().asItem())
            candleType = 9;
        if(context.getItemInHand().getItem() == ModBlocks.CANDLE_YELLOW.get().asItem())
            candleType = 10;
        if (blockstate.is(ModBlocks.CANDLE.get())
                || blockstate.is(ModBlocks.CANDLE_BLUE.get())
                || blockstate.is(ModBlocks.CANDLE_BLACK.get())
                || blockstate.is(ModBlocks.CANDLE_LIME.get())
                || blockstate.is(ModBlocks.CANDLE_ORANGE.get())
                || blockstate.is(ModBlocks.CANDLE_PINK.get())
                || blockstate.is(ModBlocks.CANDLE_PURPLE.get())
                || blockstate.is(ModBlocks.CANDLE_RED.get())
                || blockstate.is(ModBlocks.CANDLE_CYAN.get())
                || blockstate.is(ModBlocks.CANDLE_YELLOW.get())) {


            if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CandleTile tile)
            {
                if(tile.candles.get(1).type == 0) {
                    tile.candles.get(1).type = candleType;
                    tile.candles.get(1).meltTimer = tile.candleMeltTimerMAX;
                    if(context.getItemInHand().getItem() instanceof CandleItem candleItem) {
                        tile.candles.get(1).dyeColor = candleItem.getColor(context.getItemInHand());
                        tile.candles.get(1).height = CandleItem.getHeight(context.getItemInHand());
                    }
                }
                else if(tile.candles.get(2).type == 0) {
                    tile.candles.get(2).type = candleType;
                    tile.candles.get(2).meltTimer = tile.candleMeltTimerMAX;
                    if(context.getItemInHand().getItem() instanceof CandleItem candleItem) {
                        tile.candles.get(2).dyeColor = candleItem.getColor(context.getItemInHand());
                        tile.candles.get(2).height = CandleItem.getHeight(context.getItemInHand());
                    }
                }
                else if(tile.candles.get(3).type == 0) {
                    tile.candles.get(3).type = candleType;
                    tile.candles.get(3).meltTimer = tile.candleMeltTimerMAX;
                    if(context.getItemInHand().getItem() instanceof CandleItem candleItem) {
                        tile.candles.get(3).dyeColor = candleItem.getColor(context.getItemInHand());
                        tile.candles.get(3).height = CandleItem.getHeight(context.getItemInHand());
                    }
                }
            }
            return blockstate.setValue(CANDLES, Integer.valueOf(Math.min(4, blockstate.getValue(CANDLES) + 1)));

        } else {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;

            return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag)).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(SLOT_ONE_TYPE, candleType).setValue(CANDLES_LIT, 0);
        }
    }


    public static boolean isLit(BlockState p_151934_) {
        return p_151934_.hasProperty(LIT) && (p_151934_.is(BlockTags.CANDLES) || p_151934_.is(BlockTags.CANDLE_CAKES)) && p_151934_.getValue(LIT);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return (useContext.getItemInHand().getItem() == ModBlocks.CANDLE.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_BLUE.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_BLACK.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_LIME.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_ORANGE.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_PINK.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_PURPLE.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_RED.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_CYAN.get().asItem()
                || useContext.getItemInHand().getItem() == ModBlocks.CANDLE_YELLOW.get().asItem())
                && state.getValue(CANDLES) < 4 || super.canBeReplaced(state, useContext);
    }

    public void dropCandles(Level world, BlockPos pos) {



        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof CandleTile && !world.isClientSide()) {
            CandleTile candleTile = (CandleTile) entity;
            if (candleTile.candles.get(0).type != 0) {
                ItemStack itemStack = new ItemStack(ModBlocks.CANDLE.get());
                ((CandleItem)itemStack.getItem()).setColor(itemStack, candleTile.candles.get(0).dyeColor);
                ((CandleItem)itemStack.getItem()).setHeight(itemStack, candleTile.candles.get(0).height);
                popResource((ServerLevel) world, pos, itemStack);
            }
            if (candleTile.candles.get(1).type != 0) {
                ItemStack itemStack = new ItemStack(ModBlocks.CANDLE.get());
                ((CandleItem)itemStack.getItem()).setColor(itemStack, candleTile.candles.get(1).dyeColor);
                ((CandleItem)itemStack.getItem()).setHeight(itemStack, candleTile.candles.get(1).height);
                popResource((ServerLevel) world, pos, itemStack);
            }
            if (candleTile.candles.get(2).type != 0) {
                ItemStack itemStack = new ItemStack(ModBlocks.CANDLE.get());
                ((CandleItem)itemStack.getItem()).setColor(itemStack, candleTile.candles.get(2).dyeColor);
                ((CandleItem)itemStack.getItem()).setHeight(itemStack, candleTile.candles.get(2).height);
                popResource((ServerLevel) world, pos, itemStack);
            }
            if (candleTile.candles.get(3).type != 0) {
                ItemStack itemStack = new ItemStack(ModBlocks.CANDLE.get());
                ((CandleItem)itemStack.getItem()).setColor(itemStack, candleTile.candles.get(3).dyeColor);
                ((CandleItem)itemStack.getItem()).setHeight(itemStack, candleTile.candles.get(3).height);
                popResource((ServerLevel) world, pos, itemStack);
            }

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                dropCandles(level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch(state.getValue(CANDLES)) {
            case 1:
            default:
                return ONE_SHAPE;
            case 2:
                return TWO_SHAPE;
            case 3:
                return THREE_SHAPE;
            case 4:
                return FOUR_SHAPE;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);
        Random random = new Random();
            if(itemstack.getItem() == Items.FLINT_AND_STEEL)
            {


                if (canBeLit(state, pos, worldIn)) {
                    CandleTile tile = ((CandleTile) worldIn.getBlockEntity(pos));
                    if(tile == null)
                        return InteractionResult.FAIL;

                    if (!tile.candles.get(0).lit)
                        tile.candles.get(0).lit = true;
                    else if (!tile.candles.get(1).lit)
                        tile.candles.get(1).lit = true;
                    else if (!tile.candles.get(2).lit)
                        tile.candles.get(2).lit = true;
                    else if (!tile.candles.get(3).lit)
                        tile.candles.get(3).lit = true;
                    else
                        return InteractionResult.FAIL;

                    worldIn.playSound((Player) null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                    itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));

                    return InteractionResult.sidedSuccess(worldIn.isClientSide());
                }

            }
        return InteractionResult.PASS;
    }

    public Candle(Properties properties) {
        super(properties.noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(PLAYER_NEAR, false).setValue(CANDLES_LIT, 0).setValue(LIT, false));
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState blockState) {
        return PARTICLE_OFFSETS.get(blockState.getValue(CANDLES).intValue());
    }

    public static void spawnSmokeParticles(Level worldIn, BlockPos pos, boolean spawnExtraSmoke) {
        RandomSource random = worldIn.getRandom();
        SimpleParticleType basicparticletype = ModParticleTypes.EXTINGUISH.get();

        Vec3 offset = new Vec3(random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0, random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1));

        worldIn.addParticle(basicparticletype, true, (double)pos.getX() + 0.5D + offset.x, (double)pos.getY() + random.nextDouble() * 0.15f, (double)pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.1D + 0.1D, offset.z / 8f);
        if (spawnExtraSmoke) {
            worldIn.addParticle(basicparticletype, true, (double)pos.getX() + 0.5D + offset.x, (double)pos.getY() + random.nextDouble() * 0.15f, (double)pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.1D + 0.1D, offset.z / 8f);
        }
    }

    public static void extinguish(LevelAccessor level, BlockPos pos, BlockState state, CandleTile tile) {
        int numLit = 0;
        for(int i = 0; i < 3; i++)
            if(tile.candles.get(i).lit) numLit++;

        tile.candles.get(0).lit = false;
        tile.candles.get(1).lit = false;
        tile.candles.get(2).lit = false;
        tile.candles.get(3).lit = false;
        if (!level.isClientSide()) {
            level.playSound((Player)null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (level.isClientSide()) {
            for(int i = 0; i < 10 * numLit; ++i) {
                spawnSmokeParticles((Level)level, pos, true);
            }
        }
//        tile.setChanged();

    }

    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
            CandleTile tile = ((CandleTile)worldIn.getBlockEntity(pos));
            boolean flag = (tile.candles.get(0).lit || tile.candles.get(1).lit || tile.candles.get(2).lit || tile.candles.get(3).lit);
            if (flag) {


                extinguish(worldIn, pos, state, tile);

            }

            worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void onProjectileHit(Level worldIn, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (projectile.isOnFire()) {
            CandleTile tile = ((CandleTile)worldIn.getBlockEntity(hit.getBlockPos()));
            boolean flagLit = (tile.candles.get(0).lit && tile.candles.get(1).lit && tile.candles.get(2).lit && tile.candles.get(3).lit);
            Entity entity = projectile.getOwner();
            boolean flag = entity == null || entity instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, entity);
            if (flag && !flagLit && !state.getValue(WATERLOGGED)) {
                if(tile.candles.get(0).type != 0)
                    tile.candles.get(0).lit = true;
                if(tile.candles.get(1).type != 0)
                    tile.candles.get(1).lit = true;
                if(tile.candles.get(2).type != 0)
                    tile.candles.get(2).lit = true;
                if(tile.candles.get(3).type != 0)
                    tile.candles.get(3).lit = true;
            }

        }

    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (stack == null)
            return;
        withTileEntityDo(worldIn, pos, te -> {
            if(te.candles.get(0).type == 0) {
                if(stack.getItem() instanceof CandleItem candleItem) {
                    te.candles.get(0).dyeColor = candleItem.getColor(stack);
                    te.candles.get(0).height = CandleItem.getHeight(stack);
                }
            }
            else if(te.candles.get(1).type == 0) {
                if(stack.getItem() instanceof CandleItem candleItem) {
                    te.candles.get(1).dyeColor = candleItem.getColor(stack);
                    te.candles.get(1).height = candleItem.getHeight(stack);
                }
            }
            else if(te.candles.get(2).type == 0) {
                if(stack.getItem() instanceof CandleItem candleItem) {
                    te.candles.get(2).dyeColor = candleItem.getColor(stack);
                    te.candles.get(2).height = candleItem.getHeight(stack);
                }
            }
            else if(te.candles.get(3).type == 0) {
                if(stack.getItem() instanceof CandleItem candleItem) {
                    te.candles.get(3).dyeColor = candleItem.getColor(stack);
                    te.candles.get(3).height = candleItem.getHeight(stack);
                }
            }
            te.sync();
//            if(te.candles.get(0).type != 0)
//            te.setDyeColor(Coffer.getColorStatic(stack));
        });
        super.setPlacedBy(worldIn, pos, state, placer, stack);

//        if (stack.hasCustomHoverName()) {
//            BlockEntity tileentity = worldIn.getBlockEntity(pos);
//            ((CandleTile)tileentity).customName = stack.getHoverName();
//        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, CANDLES, WATERLOGGED, PLAYER_NEAR, SLOT_ONE_TYPE, CANDLES_LIT, LIT);
    }

    public static boolean canBeLit(BlockState state, BlockPos pos, Level world) {
        CandleTile tile = ((CandleTile)world.getBlockEntity(pos));
        if(tile == null) return false;
        return !state.getValue(BlockStateProperties.WATERLOGGED) && (!tile.candles.get(0).lit || (!tile.candles.get(1).lit && tile.candles.get(1).type != 0) || (!tile.candles.get(2).lit && tile.candles.get(2).type != 0) || (!tile.candles.get(3).lit && tile.candles.get(3).type != 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.candle_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.candle_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.candle_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }


    @Override
    public Class<CandleTile> getTileEntityClass() {
        return CandleTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CandleTile(ModTileEntities.CANDLE_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.CANDLE_TILE.get() ?
                (world2, pos, state2, entity) -> ((CandleTile)entity).tick() : null;
    }

}
