package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Coffer extends Block implements ITileEntity<CofferTile>, SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<Coffer> CODEC = simpleCodec(Coffer::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public Coffer(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
        if (!(level.getBlockEntity(pos) instanceof CofferTile cofferTile)) {
            return CofferTile.DEFAULT_COLOR;
        }
        int dyeCol = cofferTile.dyeColor;

        DyeColor color = Coffer.getDyeColorNamed(cofferTile.customName != null ? cofferTile.customName.getString() : "");
        if(color == null)
            return dyeCol;
        return color.getTextureDiffuseColor();
    }

    public static int getColorStatic(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, 0x422F1E);
    }


    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }
    public static DyeColor getDyeColorNamed(String name) {
        return HexereiUtil.getDyeColorNamed(name, 0);
    }

    @Override
    public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {
        updateOrDestroy(p_60462_, p_60462_, p_60463_, p_60464_, 0);
        super.tick(p_60462_, p_60463_, p_60464_, p_60465_);
    }

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Optional.of(Block.box(2, 0, 4, 14, 4, 12)).get();

    public static final VoxelShape SHAPE_TURNED = Optional.of(Block.box(4, 0, 2, 12, 4, 14)).get();


    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
        if (p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST || p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
            return SHAPE_TURNED;
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.COFFER_TILE.get() ?
                (world2, pos, state2, entity) -> ((CofferTile)entity).tick() : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
            stack.useOn(new UseOnContext(player, hand, hitResult));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {

            BlockEntity tileEntity = level.getBlockEntity(pos);

            if (tileEntity instanceof CofferTile) {
                MenuProvider containerProvider = createContainerProvider(level, pos);

                serverPlayer.openMenu(containerProvider, b -> b.writeBoolean(true).writeLong(tileEntity.getBlockPos().asLong()));
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }

        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (player instanceof FakePlayer)
            return;
        if (world instanceof ServerLevel) {
            ItemStack cloneItemStack = getCloneItemStack(state, new BlockHitResult(pos.getCenter(), Direction.UP, pos, true), world, pos, player);
            world.destroyBlock(pos, false);
            if (world.getBlockState(pos) != state && !world.isClientSide()) {
                if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.AIR)
                    player.setItemInHand(InteractionHand.MAIN_HAND,cloneItemStack);
                else
                    player.getInventory().placeItemBackInInventory(cloneItemStack);
            }

        }
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
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return super.getCloneItemStack(level, pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack item = new ItemStack(this);
        Optional<CofferTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(level, pos));

        CompoundTag tag = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag inv = tileEntityOptional.map(coffer -> coffer.itemStackHandler.serializeNBT(level.registryAccess()))
                .orElse(new CompoundTag());

        ItemStackHandler empty = tileEntityOptional.map(herb_jar -> herb_jar.itemStackHandler)
                .orElse(new ItemStackHandler(36));

        boolean flag = false;
        for(int i = 0; i < 36; i++)
        {
            if(!empty.getStackInSlot(i).isEmpty())
            {
                flag = true;
                break;
            }
        }
        if(flag)
            tag.put("Inventory", inv);
        tag.putInt("ButtonToggled", tileEntityOptional.map(cofferTile -> cofferTile.buttonToggled).orElse(0));

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        item.set(DataComponents.DYED_COLOR, new DyedItemColor(tileEntityOptional.map(cofferTile -> cofferTile.dyeColor).orElse(0x422F1E), true));


        Component customName = tileEntityOptional.map(CofferTile::getCustomName).orElse(null);

        if (customName != null)
            if(!customName.getString().isEmpty())
                item.set(DataComponents.CUSTOM_NAME, customName);
        return item;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        withTileEntityDo(worldIn, pos, te -> {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            te.readInventory(worldIn.registryAccess(), tag.getCompound("Inventory"));

            te.setDyeColor(Coffer.getColorStatic(stack));

            te.buttonToggled = tag.getInt("ButtonToggled");
            te.sync();
        });
        super.setPlacedBy(worldIn, pos, state, placer, stack);


        if (stack.has(DataComponents.CUSTOM_NAME)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if(tileentity != null)
                ((CofferTile)tileentity).customName = stack.get(DataComponents.CUSTOM_NAME);
        }

    }

    public boolean isEmpty(ItemStackHandler handler)
    {
        boolean empty = true;
        for(int i = 0; i < handler.getSlots(); i++)
        {
            if(!handler.getStackInSlot(i).isEmpty()) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            if(stack.getItem() instanceof CofferItem cofferItem) {
                ItemStackHandler handler = cofferItem.createHandler();

                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if (tag.contains("Inventory") && context.level() != null)
                    handler.deserializeNBT(context.level().registryAccess(), tag.getCompound("Inventory"));
                if(isEmpty(handler)){
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }

        } else {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }


    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
        return new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new CofferContainer(i, worldIn, pos, playerInventory, playerEntity);
            }

            @Override
            public Component getDisplayName() {
                if(worldIn.getBlockEntity(pos) instanceof CofferTile cofferTile && cofferTile.customName != null)
                    return Component.translatable(cofferTile.customName.getString());
                return Component.translatable("screen.hexerei.coffer");
            }

        };
    }

//    @Nullable
//    @Override
//    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
//        BlockEntity te = ModTileEntities.COFFER_TILE.get().create();
//        return te;
//    }
//
//    @Override
//    public boolean hasBlockEntity(BlockState state) {
//        return true;
//    }
    @Override
    public Class<net.joefoxe.hexerei.tileentity.CofferTile> getTileEntityClass() {
        return CofferTile.class;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CofferTile(ModTileEntities.COFFER_TILE.get(), pos, state);
    }

}
