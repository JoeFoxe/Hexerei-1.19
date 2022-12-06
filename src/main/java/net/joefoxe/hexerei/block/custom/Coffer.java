package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Coffer extends BaseEntityBlock implements ITileEntity<CofferTile>, SimpleWaterloggedBlock, DyeableLeatherItem {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
        ItemStack clone = ((Coffer)state.getBlock()).getCloneItemStack(level, pos, state);
        int dyeCol = Coffer.getColorStatic(clone);
        DyeColor color = Coffer.getDyeColorNamed(clone);
        if(color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = getColorStatic(stack);
        if(color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }


    public static DyeColor getDyeColorNamed(ItemStack stack) {



        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
//
//        if(stack.getHoverName().getString().equals("jeb_"))
//            return DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//
//        if(stack.getHoverName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            });
//
//        if(stack.getHoverName().getString().equals("bi_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            });
//
//        if(stack.getHoverName().getString().equals("trans_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            });
//
//        if(stack.getHoverName().getString().equals("joe_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            });
//
////        if(this.getName().getString().equals("les_"))
////            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/10) % 15)) {
////                case 1 -> 0;
////                case 2 -> 0;
////                case 3 -> 0;
////                case 4 -> 1;
////                case 5 -> 1;
////                case 6 -> 1;
////                case 7 -> 2;
////                case 8 -> 2;
////                case 9 -> 2;
////                case 10 -> 6;
////                case 11 -> 6;
////                case 12 -> 6;
////                case 13 -> 14;
////                case 14 -> 14;
////                case 15 -> 14;
////                default -> 0;
////            });
//
//
//        //DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//        return null;
    }

    @Override
    public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {
        updateOrDestroy(p_60462_, p_60462_, p_60463_, p_60464_, 0);
        super.tick(p_60462_, p_60463_, p_60464_, p_60465_);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 0, 4, 14, 4, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_TURNED = Stream.of(
            Block.box(4, 0, 2, 12, 4, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


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

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);
        if (itemstack.is(ModItems.CROW_FLUTE.get()) && itemstack.getOrCreateTag().getInt("commandMode") == 2) {
            player.getItemInHand(handIn).useOn(new UseOnContext(player, handIn, hit));
            return InteractionResult.SUCCESS;
        }
        if(!worldIn.isClientSide()) {


            BlockEntity tileEntity = worldIn.getBlockEntity(pos);

            if (tileEntity instanceof CofferTile) {
                MenuProvider containerProvider = createContainerProvider(worldIn, pos);

                NetworkHooks.openScreen(((ServerPlayer) player), containerProvider, b -> b.writeBoolean(true).writeLong(tileEntity.getBlockPos().asLong()));

            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }

        }
        return InteractionResult.SUCCESS;
    }

    public Coffer(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
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
            ItemStack cloneItemStack = getCloneItemStack(world, pos, state);
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
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, world, pos, explosion);

        if (world instanceof ServerLevel) {
            ItemStack cloneItemStack = getCloneItemStack(world, pos, state);
            if (world.getBlockState(pos) != state && !world.isClientSide()) {
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, cloneItemStack));
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
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack(this);
        Optional<CofferTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(worldIn, pos));

        CompoundTag tag = item.getOrCreateTag();
        CompoundTag inv = tileEntityOptional.map(coffer -> coffer.itemStackHandler.serializeNBT())
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

        setColor(item, tileEntityOptional.map(cofferTile -> cofferTile.dyeColor).orElse(0x422F1E));

        tag.putInt("ButtonToggled", tileEntityOptional.map(cofferTile -> cofferTile.buttonToggled).orElse(0));


        Component customName = tileEntityOptional.map(CofferTile::getCustomName)
                .orElse(null);

        if (customName != null)
            if(customName.getString().length() > 0)
                item.setHoverName(customName);
        return item;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        withTileEntityDo(worldIn, pos, te -> {
            te.readInventory(stack.getOrCreateTag()
                    .getCompound("Inventory"));

            te.setDyeColor(Coffer.getColorStatic(stack));

            te.buttonToggled = stack.getOrCreateTag()
                    .getInt("ButtonToggled");
            te.sync();
        });
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if(tileentity != null)
                ((CofferTile)tileentity).customName = stack.getHoverName();
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            if(stack.getItem() instanceof CofferItem cofferItem) {
                ItemStackHandler handler = cofferItem.createHandler();
                handler.deserializeNBT(stack.getOrCreateTag().getCompound("Inventory"));
                if(isEmpty(handler)){
                    tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }




        } else {
                tooltip.add(Component.translatable("tooltip.hexerei.coffer").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {

        //world.addParticle(ParticleTypes.ENCHANT, pos.getX() + Math.round(rand.nextDouble()), pos.getY() + 1.2d, pos.getZ() + Math.round(rand.nextDouble()) , (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.035d ,(rand.nextDouble() - 0.5d) / 50d);

        super.animateTick(state, world, pos, rand);
    }

    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
        return new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new CofferContainer(i, worldIn, pos, playerInventory, playerEntity);
            }

            @Override
            public Component getDisplayName() {
                if(((CofferTile)worldIn.getBlockEntity(pos)).customName != null)
                    return Component.translatable(((CofferTile)worldIn.getBlockEntity(pos)).customName.getString());
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
