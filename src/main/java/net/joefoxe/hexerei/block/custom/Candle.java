package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.candle.AbstractCandleEffect;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.data.candle.CandleEffects;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.*;
import net.minecraft.Util;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.DirectionalPlaceContext;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Candle extends AbstractCandleBlock implements ITileEntity<CandleTile>, EntityBlock, SimpleWaterloggedBlock, DyeableLeatherItem {

    public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 4);
    public static final IntegerProperty CANDLES_LIT = IntegerProperty.create("candles_lit", 0, 4);
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int BASE_COLOR = 0xCCC398;

    private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = Util.make(() -> {
        Int2ObjectMap<List<Vec3>> int2objectmap = new Int2ObjectOpenHashMap<>();
        int2objectmap.defaultReturnValue(ImmutableList.of());
        int2objectmap.put(1, ImmutableList.of(new Vec3(0.5D, 0.5D, 0.5D)));
        int2objectmap.put(2, ImmutableList.of(new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.625D, 0.5D, 0.44D)));
        int2objectmap.put(3, ImmutableList.of(new Vec3(0.5D, 0.313D, 0.625D), new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.56D, 0.5D, 0.44D)));
        int2objectmap.put(4, ImmutableList.of(new Vec3(0.44D, 0.313D, 0.56D), new Vec3(0.625D, 0.44D, 0.56D), new Vec3(0.375D, 0.44D, 0.375D), new Vec3(0.56D, 0.5D, 0.375D)));
        return Int2ObjectMaps.unmodifiable(int2objectmap);
    });

    public static final VoxelShape ONE_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 9.0D, 10.0D);
    public static final VoxelShape TWO_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);
    public static final VoxelShape THREE_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);
    public static final VoxelShape FOUR_SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);


    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new OptionalDispenseItemBehavior() {

        public ItemStack execute(BlockSource source, ItemStack stack) {

            this.setSuccess(false);
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockpos = source.getPos().relative(direction);


                try {
                    this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(source.getLevel(), blockpos, direction, stack, direction)).consumesAction());
                } catch (Exception exception) {
                    LOGGER.error("Error trying to place shulker box at {}", blockpos, exception);
                }

                BlockEntity blockEntity = source.getLevel().getBlockEntity(blockpos);
                BlockState blockState = source.getLevel().getBlockState(blockpos);
                if(blockEntity instanceof CandleTile candleTile){
                    source.getLevel().scheduleTick(blockpos, blockState.getBlock(), 1);
                }
            }



            return stack;
//
//
//            if (!flag) {
//                return this.defaultDispenseItemBehavior.dispense(source, stack);
//            }
//
//            d3 = -0.9D;
//
//            AbstractMinecart abstractminecart = AbstractMinecart.createMinecart(level, d0, d1 + d3, d2, AbstractMinecart.Type.RIDEABLE);
//            if (stack.hasCustomHoverName()) {
//                abstractminecart.setCustomName(stack.getHoverName());
//            }
//
//            level.addFreshEntity(abstractminecart);
//            stack.shrink(1);
//            return stack;
        }
        protected void playSound(BlockSource p_42947_) {
            p_42947_.getLevel().levelEvent(1000, p_42947_.getPos(), 0);
        }
    };
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


        if (blockstate.is(ModBlocks.CANDLE.get())) {

            return blockstate.setValue(CANDLES, Math.min(4, blockstate.getValue(CANDLES) + 1));

        } else {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;

            return super.getStateForPlacement(context).setValue(WATERLOGGED, flag).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(CANDLES_LIT, 0);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if(pLevel.getBlockEntity(pPos) instanceof CandleTile candleTile){
            int analog = candleTile.updateAnalog();
            pLevel.updateNeighborsAt(pPos, pState.getBlock());
        }
        super.tick(pState, pLevel, pPos, pRandom);

    }

    public static boolean isLit(BlockState p_151934_) {
        return p_151934_.hasProperty(LIT) && (p_151934_.is(BlockTags.CANDLES) || p_151934_.is(BlockTags.CANDLE_CAKES)) && p_151934_.getValue(LIT);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return (useContext.getItemInHand().getItem() == ModItems.CANDLE.get())
                && state.getValue(CANDLES) < 4 || super.canBeReplaced(state, useContext);
    }

    public void dropCandles(Level level, BlockPos pos) {

        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof CandleTile candleTile && !level.isClientSide()) {
            for(int i = 0; i < 4; i++) {
                CandleData candleData = candleTile.candles.get(i);
                if (candleData.hasCandle) {
                    ItemStack itemStack = new ItemStack(ModBlocks.CANDLE.get());
                    if(candleData.dyeColor != Candle.BASE_COLOR)
                        CandleItem.setColorStatic(itemStack, candleData.dyeColor);
                    if(candleData.height < 7)
                        CandleItem.setHeight(itemStack, candleData.height);
                    if(candleData.herb.layer != null)
                        CandleItem.setHerbLayer(itemStack, candleData.herb.layer.toString());
                    if(candleData.base.layer != null)
                        CandleItem.setBaseLayer(itemStack, candleData.base.layer.toString());
                    if(candleData.glow.layer != null)
                        CandleItem.setGlowLayer(itemStack, candleData.glow.layer.toString());
                    if(candleData.swirl.layer != null)
                        CandleItem.setSwirlLayer(itemStack, candleData.swirl.layer.toString());
                    if(candleData.effect != null) {
                        CandleItem.setEffectLocation(itemStack, candleData.effect.getLocationName());
                    }
                    if(candleData.effectParticle != null)
                        CandleItem.setEffectParticle(itemStack, candleData.effectParticle);
                    if(!itemStack.hasTag() || itemStack.getOrCreateTag().isEmpty())
                        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(ModBlocks.CANDLE.get())));
                    else
                        popResource((ServerLevel) level, pos, itemStack);
                }
            }
        }
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack(ModItems.CANDLE.get());
        Optional<CandleTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(worldIn, pos));

//        int col = tileEntityOptional.map(CandleTile::getDyeColor).orElse(BASE_COLOR);
//        if (col != BASE_COLOR && col != 0)
//            setColor(item, tileEntityOptional.map(CandleTile::getDyeColor).orElse(BASE_COLOR));

        tileEntityOptional.ifPresent(candleTile -> {
            CandleData candleData = candleTile.candles.get(0);

            if (candleData.dyeColor != BASE_COLOR && candleData.dyeColor != 0)
                setColor(item, candleData.dyeColor);
            if(candleData.height < 7)
                CandleItem.setHeight(item, candleData.height);
            if(candleData.herb.layer != null)
                CandleItem.setHerbLayer(item, candleData.herb.layer.toString());
            if(candleData.base.layer != null)
                CandleItem.setBaseLayer(item, candleData.base.layer.toString());
            if(candleData.glow.layer != null)
                CandleItem.setGlowLayer(item, candleData.glow.layer.toString());
            if(candleData.swirl.layer != null)
                CandleItem.setSwirlLayer(item, candleData.swirl.layer.toString());
            if(candleData.effect != null)
                CandleItem.setEffectLocation(item, candleData.effect.getLocationName());
            if(candleData.effectParticle != null)
                CandleItem.setEffectParticle(item, candleData.effectParticle);
        });


        Component customName = tileEntityOptional.map(CandleTile::getCustomName).orElse(null);
        if (customName != null)
            if(customName.getString().length() > 0)
                item.setHoverName(customName);
        return item;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                dropCandles(level, pos);
            }
            for(Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
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

    public static VoxelShape getShape(BlockState state) {
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
        if(itemstack.getItem() == Items.FIRE_CHARGE)
        {


            if (canBeLit(state, pos, worldIn)) {
                CandleTile tile = ((CandleTile) worldIn.getBlockEntity(pos));
                if(tile == null)
                    return InteractionResult.FAIL;

                if (!tile.candles.get(0).hasCandle)
                    tile.candles.get(0).lit = true;
                if (!tile.candles.get(1).hasCandle)
                    tile.candles.get(1).lit = true;
                if (!tile.candles.get(2).hasCandle)
                    tile.candles.get(2).lit = true;
                if (!tile.candles.get(3).hasCandle)
                    tile.candles.get(3).lit = true;

                worldIn.playSound((Player) null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                itemstack.shrink(1);

                return InteractionResult.sidedSuccess(worldIn.isClientSide());
            }

        }
        return InteractionResult.PASS;
    }

    public Candle(Properties properties) {
        super(properties.noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(POWER, 0).setValue(CANDLES_LIT, 0).setValue(LIT, false));
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

    public static void spawnParticleWave(Level worldIn, BlockPos pos, boolean spawnExtraSmoke, List<ResourceLocation> particle, int amount) {
        RandomSource random = worldIn.getRandom();

        for(int i = 0; i < amount; i++){
            float rotation = random.nextFloat() * 360f;
            Vec3 offset = new Vec3(random.nextDouble() * 2 * Math.cos(rotation), 0, random.nextDouble() * 2 * Math.sin(rotation));

            if(particle != null && Registry.PARTICLE_TYPE.get(particle.get(random.nextInt(particle.size()))) != null) {
                worldIn.addParticle((ParticleOptions) Registry.PARTICLE_TYPE.get(particle.get(random.nextInt(particle.size()))), true, (double) pos.getX() + 0.5D + offset.x, (double) pos.getY() + random.nextDouble() * 0.15f, (double) pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.025D, offset.z / 8f);
                if (spawnExtraSmoke) {
                    worldIn.addParticle((ParticleOptions) Registry.PARTICLE_TYPE.get(particle.get(random.nextInt(particle.size()))), true, (double) pos.getX() + 0.5D + offset.x, (double) pos.getY() + random.nextDouble() * 0.15f, (double) pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.025D, offset.z / 8f);
                }
            }
        }
    }

    public static void extinguish(LevelAccessor level, BlockPos pos, BlockState state, CandleTile tile) {
        int numLit = 0;
        for(int i = 0; i < 4; i++)
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
                if(tile.candles.get(0).hasCandle)
                    tile.candles.get(0).lit = true;
                if(tile.candles.get(1).hasCandle)
                    tile.candles.get(1).lit = true;
                if(tile.candles.get(2).hasCandle)
                    tile.candles.get(2).lit = true;
                if(tile.candles.get(3).hasCandle)
                    tile.candles.get(3).lit = true;
            }

        }

    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (stack == null)
            return;
        withTileEntityDo(worldIn, pos, te -> {
            for(int i = 0; i < 4; i++){
                if (!te.candles.get(i).hasCandle) {
                    if (stack.getItem() instanceof CandleItem candleItem) {
                        te.candles.get(i).hasCandle = true;
                        te.candles.get(i).dyeColor = CandleItem.getColorStatic(stack);
                        te.candles.get(i).height = CandleItem.getHeight(stack);
                        String herbLayer = CandleItem.getHerbLayer(stack);
                        String baseLayer = CandleItem.getBaseLayer(stack);
                        String glowLayer = CandleItem.getGlowLayer(stack);
                        String swirlLayer = CandleItem.getSwirlLayer(stack);
                        String effectLocation = CandleItem.getEffectLocation(stack);
                        List<ResourceLocation> effectParticle = CandleItem.getEffectParticle(stack);

                        if (herbLayer != null)
                            te.candles.get(i).herb.layer = herbLayer.equals("minecraft:missingno") ? null : new ResourceLocation(herbLayer);
                        else
                            te.candles.get(i).herb.layer = null;

                        if (baseLayer != null)
                            te.candles.get(i).base.layer = baseLayer.equals("minecraft:missingno") ? null : new ResourceLocation(baseLayer);
                        else
                            te.candles.get(i).base.layer = null;

                        if (glowLayer != null)
                            te.candles.get(i).glow.layer = glowLayer.equals("minecraft:missingno") ? null : new ResourceLocation(glowLayer);
                        else
                            te.candles.get(i).glow.layer = null;

                        if (swirlLayer != null)
                            te.candles.get(i).swirl.layer = swirlLayer.equals("minecraft:missingno") ? null : new ResourceLocation(swirlLayer);
                        else
                            te.candles.get(i).swirl.layer = null;

                        if (effectLocation != null)
                            te.candles.get(i).setEffect(CandleEffects.getEffect(effectLocation).getCopy());
                        else
                            te.candles.get(i).effect = new AbstractCandleEffect();

                        if (effectParticle != null)
                            te.candles.get(i).effectParticle = effectParticle;
                        else
                            te.candles.get(i).effectParticle = null;
                        break;
                    }
                }
            }
            te.sync();
        });
        for(Direction direction : Direction.values()) {
            worldIn.updateNeighborsAt(pos.relative(direction), this);
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);

//        if (stack.hasCustomHoverName()) {
//            BlockEntity tileentity = worldIn.getBlockEntity(pos);
//            ((CandleTile)tileentity).customName = stack.getHoverName();
//        }

    }


    @Override

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        AtomicInteger toReturn = new AtomicInteger();
        if(pLevel.getBlockEntity(pPos) instanceof CandleTile candleTile){
            candleTile.updateAnalog();
            return candleTile.redstoneAnalogSignal;
        }
        return toReturn.get();
    }

    @Override
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getValue(Candle.POWER);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return pState.getValue(Candle.POWER) > 0;
    }


    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getValue(Candle.POWER);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        for(Direction direction : Direction.values()) {
            pLevel.updateNeighborsAt(pPos.relative(direction), this);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }



    @SuppressWarnings("deprecation")
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, CANDLES, WATERLOGGED, POWER, CANDLES_LIT, LIT);
    }



    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof CandleTile) {
            ((CandleTile)tileentity).entityInside(entityIn);
        }

    }

    public static boolean canBeLit(BlockState state, BlockPos pos, Level world) {
        CandleTile tile = ((CandleTile)world.getBlockEntity(pos));
        if(tile == null) return false;
        return !state.getValue(BlockStateProperties.WATERLOGGED) && (!tile.candles.get(0).lit || (!tile.candles.get(1).lit && tile.candles.get(1).hasCandle) || (!tile.candles.get(2).lit && tile.candles.get(2).hasCandle) || (!tile.candles.get(3).lit && tile.candles.get(3).hasCandle));
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
