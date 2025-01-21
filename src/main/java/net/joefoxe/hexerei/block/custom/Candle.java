package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Candle extends AbstractCandleBlock implements ITileEntity<CandleTile>, EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<Candle> CODEC = simpleCodec(Candle::new);

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
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos blockpos = source.pos().relative(direction);


                try {
                    this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(source.level(), blockpos, direction, stack, direction)).consumesAction());
                } catch (Exception exception) {
                    LOGGER.error("Error trying to place candle at {}", blockpos, exception);
                }

                BlockEntity blockEntity = source.level().getBlockEntity(blockpos);
                BlockState blockState = source.level().getBlockState(blockpos);
                if(blockEntity instanceof CandleTile candleTile){
                    source.level().scheduleTick(blockpos, blockState.getBlock(), 1);
                }
            }

            return stack;
        }
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1000, source.pos(), 0);
        }
    };

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {

    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
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
                    CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                    candleData.save(tag, level.registryAccess(), true, false);
                    itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    if (candleData.dyeColor != Candle.BASE_COLOR)
                        itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(candleData.dyeColor, true));

                    popResource(level, pos, itemStack);
                }
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack item = new ItemStack(ModItems.CANDLE.get());
        Optional<CandleTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(level, pos));

        tileEntityOptional.ifPresent(candleTile -> {

            CompoundTag tag = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            CandleData candleData = candleTile.candles.get(0);
            candleData.save(tag, level.registryAccess(), true, false);
            item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            if (candleData.dyeColor != Candle.BASE_COLOR)
                item.set(DataComponents.DYED_COLOR, new DyedItemColor(candleData.dyeColor, true));

        });

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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(CANDLES)) {
            default -> ONE_SHAPE;
            case 2 -> TWO_SHAPE;
            case 3 -> THREE_SHAPE;
            case 4 -> FOUR_SHAPE;
        };
    }

    public static VoxelShape getShape(BlockState state) {
        return switch (state.getValue(CANDLES)) {
            default -> ONE_SHAPE;
            case 2 -> TWO_SHAPE;
            case 3 -> THREE_SHAPE;
            case 4 -> FOUR_SHAPE;
        };
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        Random random = new Random();
        if(itemstack.getItem() == Items.FLINT_AND_STEEL)
        {


            if (canBeLit(state, pos, level)) {
                CandleTile tile = ((CandleTile) level.getBlockEntity(pos));
                if(tile == null)
                    return ItemInteractionResult.FAIL;

                if (!tile.candles.get(0).lit)
                    tile.candles.get(0).lit = true;
                else if (!tile.candles.get(1).lit)
                    tile.candles.get(1).lit = true;
                else if (!tile.candles.get(2).lit)
                    tile.candles.get(2).lit = true;
                else if (!tile.candles.get(3).lit)
                    tile.candles.get(3).lit = true;
                else
                    return ItemInteractionResult.FAIL;

                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

        }
        if(itemstack.getItem() == Items.FIRE_CHARGE)
        {


            if (canBeLit(state, pos, level)) {
                CandleTile tile = ((CandleTile) level.getBlockEntity(pos));
                if(tile == null)
                    return ItemInteractionResult.FAIL;

                if (!tile.candles.get(0).hasCandle)
                    tile.candles.get(0).lit = true;
                if (!tile.candles.get(1).hasCandle)
                    tile.candles.get(1).lit = true;
                if (!tile.candles.get(2).hasCandle)
                    tile.candles.get(2).lit = true;
                if (!tile.candles.get(3).hasCandle)
                    tile.candles.get(3).lit = true;

                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                itemstack.shrink(1);

                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public Candle(Properties properties) {
        super(properties.noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(POWER, 0).setValue(CANDLES_LIT, 0).setValue(LIT, false));
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState blockState) {
        return PARTICLE_OFFSETS.get(blockState.getValue(CANDLES).intValue());
    }

    public static void spawnSmokeParticles(Level level, BlockPos pos, boolean spawnExtraSmoke) {
        RandomSource random = level.getRandom();
        SimpleParticleType basicparticletype = ModParticleTypes.EXTINGUISH.get();

        Vec3 offset = new Vec3(random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0, random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1));

        level.addParticle(basicparticletype, true, (double)pos.getX() + 0.5D + offset.x, (double)pos.getY() + random.nextDouble() * 0.15f, (double)pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.1D + 0.1D, offset.z / 8f);
        if (spawnExtraSmoke) {
            level.addParticle(basicparticletype, true, (double)pos.getX() + 0.5D + offset.x, (double)pos.getY() + random.nextDouble() * 0.15f, (double)pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.1D + 0.1D, offset.z / 8f);
        }
    }

    public static void spawnParticleWave(Level level, BlockPos pos, boolean spawnExtraSmoke, List<String> particle, int amount) {
        RandomSource random = level.getRandom();

        for(int i = 0; i < amount; i++){
            float rotation = random.nextFloat() * 30f + (360f / amount) * i;
            float ran = (float)random.nextDouble() * 0.15f + 0.15f;
            Vec3 offset = new Vec3(ran * Math.cos(rotation), 0, ran * Math.sin(rotation));

            if(!particle.isEmpty()) {
                try {
                    ParticleOptions options = ParticleArgument.readParticle(new StringReader(particle.get(random.nextInt(particle.size()))), level.registryAccess());
                    level.addParticle(options, true, (double) pos.getX() + 0.5D + offset.x, (double) pos.getY() + random.nextDouble() * 0.15f, (double) pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.025D, offset.z / 8f);
                    if (spawnExtraSmoke) {
                        level.addParticle(options, true, (double) pos.getX() + 0.5D + offset.x, (double) pos.getY() + random.nextDouble() * 0.15f, (double) pos.getZ() + 0.5D + offset.z, offset.x / 8f, random.nextDouble() * 0.025D, offset.z / 8f);
                    }

                } catch(CommandSyntaxException e) {
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
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (level.isClientSide()) {
            for(int i = 0; i < 10 * numLit; ++i) {
                spawnSmokeParticles((Level)level, pos, true);
            }
        }

    }

    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
            CandleTile tile = ((CandleTile)level.getBlockEntity(pos));
            boolean flag = (tile.candles.get(0).lit || tile.candles.get(1).lit || tile.candles.get(2).lit || tile.candles.get(3).lit);
            if (flag) {


                extinguish(level, pos, state, tile);

            }

            level.setBlock(pos, state.setValue(WATERLOGGED, Boolean.TRUE), 3);
            level.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(level));
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (projectile.isOnFire()) {
            CandleTile tile = ((CandleTile)level.getBlockEntity(hit.getBlockPos()));
            boolean flagLit = (tile.candles.get(0).lit && tile.candles.get(1).lit && tile.candles.get(2).lit && tile.candles.get(3).lit);
            Entity entity = projectile.getOwner();
            boolean flag = entity == null || entity instanceof Player || net.neoforged.neoforge.event.EventHooks.canEntityGrief(level, entity);
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (stack == null)
            return;
        withTileEntityDo(level, pos, te -> {
            int newCandlePos = 0;
            for(int i = 0; i < 4; i++){
                if (!te.candles.get(i).hasCandle) {
                    if (stack.getItem() instanceof CandleItem candleItem) {
                        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                        te.candles.get(i).load(tag, level.registryAccess(), true);
                        if (stack.has(DataComponents.DYED_COLOR))
                            te.candles.get(0).dyeColor = stack.get(DataComponents.DYED_COLOR).rgb();

                        te.setOffsetPos(true);
                        newCandlePos = i;
                        break;
                    }
                }
            }
            for(int i = 0; i < 4; i++){
                if (te.candles.get(i).returnToBlock || i == newCandlePos){
                    te.setOffsetPos(i);
                    te.candles.get(i).moveInstantlyToTarget();
                }
            }
            te.sync();
        });
        for(Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
        }
        super.setPlacedBy(level, pos, state, placer, stack);

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



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, CANDLES, WATERLOGGED, POWER, CANDLES_LIT, LIT);
    }



    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = level.getBlockEntity(pos);
        if (tileentity instanceof CandleTile tile) {
            tile.entityInside(entityIn);
        }

    }

    public static boolean canBeLit(BlockState state, BlockPos pos, Level world) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof CandleTile tile) {
            return !state.getValue(BlockStateProperties.WATERLOGGED) && (!tile.candles.get(0).lit || (!tile.candles.get(1).lit && tile.candles.get(1).hasCandle) || (!tile.candles.get(2).lit && tile.candles.get(2).hasCandle) || (!tile.candles.get(3).lit && tile.candles.get(3).hasCandle));
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            String str = CandleItem.getEffectLocation(stack);
            if(str != null && str.length() > 0 && !str.equals("hexerei:no_effect")) {
                String translateEffect = "effect." + (ResourceLocation.parse(str).getNamespace()) + "." + ResourceLocation.parse(str).getPath();
                MutableComponent component = Component.translatable(translateEffect).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_effect", component));

            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
