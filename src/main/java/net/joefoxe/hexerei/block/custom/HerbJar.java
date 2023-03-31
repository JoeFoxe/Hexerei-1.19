package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class HerbJar extends Block implements ITileEntity<HerbJarTile>, EntityBlock, SimpleWaterloggedBlock, DyeableLeatherItem {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty GUI_RENDER = BooleanProperty.create("gui_render");
    public static final BooleanProperty DYED = BooleanProperty.create("dyed");

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
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, Boolean.valueOf(direction == Direction.UP));
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER)).setValue(GUI_RENDER, false).setValue(DYED, HexereiUtil.getColorStatic(context.getItemInHand()) != 0x422F1E).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
                }
            }
        }

        return null;
    }


    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }
    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
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

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (player.getItemInHand(handIn).is(ModItems.CROW_FLUTE.get()) && player.getItemInHand(handIn).getOrCreateTag().getInt("commandMode") == 2) {
            player.getItemInHand(handIn).useOn(new UseOnContext(player, handIn, hit));
            return InteractionResult.SUCCESS;
        }
        if ((itemstack.isEmpty() && player.isShiftKeyDown()) || state.getValue(HorizontalDirectionalBlock.FACING).getOpposite() != hit.getDirection()) {

            BlockEntity tileEntity = worldIn.getBlockEntity(pos);

            if(!worldIn.isClientSide()) {
                if (tileEntity instanceof HerbJarTile) {
                    ((HerbJarTile) tileEntity).sync();
                    MenuProvider containerProvider = createContainerProvider(worldIn, pos, getCloneItemStack(worldIn, pos, state));
                    NetworkHooks.openScreen(((ServerPlayer) player), containerProvider, b -> b.writeBlockPos(tileEntity.getBlockPos()).writeItem(getCloneItemStack(worldIn, pos, state)));
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }

            return InteractionResult.SUCCESS;
        }

        BlockEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof HerbJarTile) {
            ((HerbJarTile)tileEntity).interactPutItems(player);
        }

        return InteractionResult.SUCCESS;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                ItemStack cloneItemStack = getCloneItemStack(level, pos, state);
                if(!level.isClientSide())
                    popResource((ServerLevel)level, pos, cloneItemStack);
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
        BlockHitResult rayResult = rayTraceEyeLevel(worldIn, playerIn, playerIn.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1);
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
            if (!playerIn.inventory.add(item)) {
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
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(GUI_RENDER, false).setValue(DYED, false));
    }

    @SuppressWarnings("deprecation")
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        super.appendHoverText(stack, world, tooltip, flagIn);
    }
    

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack(this);
        Optional<HerbJarTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(worldIn, pos));
//        System.out.println(worldIn.getBlockEntity(pos));e
        CompoundTag tag = item.getOrCreateTag();
        JarHandler empty = tileEntityOptional.map(herb_jar -> herb_jar.itemHandler)
                .orElse(new JarHandler(1,1024));
        CompoundTag inv = tileEntityOptional.map(herb_jar -> herb_jar.itemHandler.serializeNBT())
                .orElse(new CompoundTag());


        if(!empty.getStackInSlot(0).isEmpty())
            tag.put("Inventory", inv);


        int col = tileEntityOptional.map(herbJarTile -> herbJarTile.dyeColor).orElse(0x422F1E);
        if(col != 0x422F1E && col != 0)
            setColor(item, col);

        int toggled = tileEntityOptional.map(herbJarTile -> herbJarTile.buttonToggled).orElse(0);
        if(toggled == 1)
            tag.putInt("ButtonToggled", toggled);

        Component customName = tileEntityOptional.map(HerbJarTile::getCustomName)
                .orElse(null);

        if (customName != null)
            if(customName.getString().length() > 0)
                item.setHoverName(customName);
        return item;
    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            ((HerbJarTile)tileentity).customName = stack.getHoverName();
        }

        if (worldIn.isClientSide())
            return;
        if (stack == null)
            return;
        withTileEntityDo(worldIn, pos, te -> {
            te.readInventory(stack.getOrCreateTag()
                    .getCompound("Inventory"));
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            int intCol = -1;
            if(col != null)
                intCol = HexereiUtil.getColorValue(col);
            if(intCol == -1)
                intCol = HexereiUtil.getColorStatic(stack);
            te.setDyeColor(intCol);

            te.buttonToggled = stack.getOrCreateTag()
                    .getInt("ButtonToggled");
        });

    }

    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {

            worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.valueOf(true)), 3);
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
    @OnlyIn(Dist.CLIENT)
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
