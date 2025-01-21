package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class HerbJar extends Block implements ITileEntity<HerbJarTile>, EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty GUI_RENDER = BooleanProperty.create("gui_render");
    public static final BooleanProperty DYED = BooleanProperty.create("dyed");

    public static final DyedItemColor DEFAULT_COLOR = new DyedItemColor(0xC9B199, false);
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        for(Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(GUI_RENDER, false).setValue(DYED, HexereiUtil.getDyeColor(context.getItemInHand()) != 0x422F1E).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
                }
            }
        }

        return null;
    }


    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(5, -0.5, 5, 11, 0, 11),
            Block.box(5.5, 13, 5.5, 10.5, 15, 10.5),
            Block.box(4.5, 12, 10.5, 11.5, 14, 11.5),
            Block.box(4.5, 12, 4.5, 11.5, 14, 5.5),
            Block.box(4.5, 12, 5.5, 5.5, 14, 10.5),
            Block.box(10.5, 12, 5.5, 11.5, 14, 10.5),
            Block.box(4, 0, 4, 12, 11, 12),
            Block.box(5, 11, 5, 11, 12, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
            stack.useOn(new UseOnContext(player, hand, hitResult));
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (tileEntity instanceof HerbJarTile && state.getValue(HorizontalDirectionalBlock.FACING).getOpposite() == hitResult.getDirection()) {
            ((HerbJarTile)tileEntity).interactPutItems(player);
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if ((player.isShiftKeyDown()) || state.getValue(HorizontalDirectionalBlock.FACING).getOpposite() != hitResult.getDirection()) {

            BlockEntity tileEntity = level.getBlockEntity(pos);

            if(!level.isClientSide()) {
                if (tileEntity instanceof HerbJarTile) {
                    ((HerbJarTile) tileEntity).sync();//(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
                    MenuProvider containerProvider = createContainerProvider(level, pos, getCloneItemStack(state, hitResult, level, pos, player));
                    player.openMenu(containerProvider, b -> b.writeNbt(getCloneItemStack(level, pos, state).save(level.registryAccess())).writeBlockPos(tileEntity.getBlockPos()));
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }

            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                ItemStack cloneItemStack = getCloneItemStack(state, new BlockHitResult(pos.getCenter(), Direction.UP, pos, true), level, pos, (Player)null);
                if(!level.isClientSide())
                    popResource(level, pos, cloneItemStack);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    
    protected BlockHitResult rayTraceEyeLevel(Level world, Player player, double length) {
        Vec3 eyePos = player.getEyePosition(1);
        Vec3 lookPos = player.getViewVector(1);
        Vec3 endPos = eyePos.add(lookPos.x * length, lookPos.y * length, lookPos.z * length);
        ClipContext context = new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return world.clip(context);
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) {
        BlockHitResult rayResult = rayTraceEyeLevel(worldIn, playerIn, playerIn.blockInteractionRange() + 1);
        if (rayResult.getType() == HitResult.Type.MISS)
            return;

        Direction side = rayResult.getDirection();

        BlockEntity tile = worldIn.getBlockEntity(pos);
        HerbJarTile herbJarTile = null;
        //System.out.println(worldIn.isClientSide());
        if(tile instanceof  HerbJarTile)
            herbJarTile = (HerbJarTile) tile;
        if (state.getValue(HorizontalDirectionalBlock.FACING).getOpposite() != rayResult.getDirection())
            return;

        ItemStack item;
        if (playerIn.isShiftKeyDown()) {
            item = herbJarTile.takeItems(0, herbJarTile.itemHandler.getStackInSlot(0).getCount());
        }
        else {
            item = herbJarTile.takeItems(0, 1);
        }

        if (!item.isEmpty()) {
            if (!playerIn.getInventory().add(item)) {
                dropItemStack(worldIn, pos.relative(side), playerIn, item);
                worldIn.sendBlockUpdated(pos, state, state, 3);
            }
            else
                worldIn.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, ((worldIn.random.nextFloat() - worldIn.random.nextFloat()) * .7f + 1) * 2);
        }

        super.attack(state, worldIn, pos, playerIn);
    }

    private void dropItemStack (Level world, BlockPos pos, Player player, @Nonnull ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        Vec3 motion = entity.getDeltaMovement();
        entity.push(-motion.x, -motion.y, -motion.z);
        world.addFreshEntity(entity);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public HerbJar(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE).setValue(GUI_RENDER, false).setValue(DYED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, HANGING, WATERLOGGED, GUI_RENDER, DYED);
    }

//    @Override
//    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
//        super.onBlockExploded(state, world, pos, explosion);
//
//        if (world instanceof ServerLevel) {
//            ItemStack cloneItemStack = getCloneItemStack(world, pos, state);
//            if (world.getBlockState(pos) != state && !world.isClientSide()) {
//                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, cloneItemStack));
//            }
//
//        }
//    }


    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return super.getCloneItemStack(level, pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack item = new ItemStack(this);
        Optional<HerbJarTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(level, pos));
        CompoundTag tag = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        JarHandler empty = tileEntityOptional.map(herb_jar -> herb_jar.itemHandler)
                .orElse(new JarHandler(1,1024));
        CompoundTag inv = tileEntityOptional.map(herb_jar -> herb_jar.itemHandler.serializeNBT(level.registryAccess()))
                .orElse(new CompoundTag());


        if(!empty.getStackInSlot(0).isEmpty())
            tag.put("Inventory", inv);

        tileEntityOptional.ifPresent((herbJarTile -> {
            if (herbJarTile.hasDyeColor()) {
                item.set(DataComponents.DYED_COLOR, herbJarTile.dyeColor);
            }
        }));


        int toggled = tileEntityOptional.map(herbJarTile -> herbJarTile.buttonToggled).orElse(0);
        if(toggled == 1)
            tag.putInt("ButtonToggled", toggled);

        if (!tag.isEmpty())
            item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        Component customName = tileEntityOptional.map(HerbJarTile::getCustomName)
                .orElse(null);
        if (customName != null)
            if(!customName.getString().isEmpty())
                item.set(DataComponents.CUSTOM_NAME, customName);
        return item;
    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.has(DataComponents.CUSTOM_NAME)) {
            if (worldIn.getBlockEntity(pos) instanceof HerbJarTile herbJarTile)
                herbJarTile.customName = stack.getHoverName();
        }

        if (worldIn.isClientSide())
            return;
        withTileEntityDo(worldIn, pos, te -> {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            te.readInventory(worldIn.registryAccess(), tag.getCompound("Inventory"));
            if (stack.has(DataComponents.DYED_COLOR))
                te.dyeColor = stack.getOrDefault(DataComponents.DYED_COLOR, HerbJar.DEFAULT_COLOR);

            te.buttonToggled = tag.getInt("ButtonToggled");
        });

    }

    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {

            worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.TRUE), 3);
            worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
//        if(!stateIn.canSurvive(worldIn, currentPos))
//        {
//            if(!worldIn.isClientSide() && worldIn instanceof ServerLevel) {
//                ItemStack cloneItemStack = getCloneItemStack(worldIn, currentPos, stateIn);
//                worldIn.addFreshEntity(new ItemEntity(((ServerLevel) worldIn).getLevel(), currentPos.getX() + 0.5f, currentPos.getY() - 0.5f, currentPos.getZ() + 0.5f, cloneItemStack));
//            }
//        }

        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        Direction direction = getBlockConnected(state).getOpposite();
        return Block.canSupportCenter(worldIn, pos.relative(direction), direction.getOpposite());
    }

    protected static Direction getBlockConnected(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }


    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    }

    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos, ItemStack stack) {
        return new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new HerbJarContainer(i, stack, worldIn, pos, playerInventory, playerEntity);
            }

            @Override
            public Component getDisplayName() {
                if(((HerbJarTile)worldIn.getBlockEntity(pos)).customName != null)
                    return Component.translatable(((HerbJarTile)worldIn.getBlockEntity(pos)).customName.getString());
                return Component.translatable("screen.hexerei.herb_jar");
            }

        };
    }

    @Override
    public Class<HerbJarTile> getTileEntityClass() {
        return HerbJarTile.class;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HerbJarTile(ModTileEntities.HERB_JAR_TILE.get(), pos, state);
    }

//    @Override
//    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
//        ClientRegistry.registerISTER(consumer, JarItemRenderer::new);
//    }
}
