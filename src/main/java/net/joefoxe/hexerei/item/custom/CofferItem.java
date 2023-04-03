package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CofferItem extends BlockItem implements DyeableLeatherItem {

    public CofferItem(Block block, Properties properties) {
        super(block, properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

//    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
//    static class ColorRegisterHandler
//    {
//        @SubscribeEvent(priority = EventPriority.HIGHEST)
//        public static void registerCofferColors(RegisterColorHandlersEvent.Item event)
//        {
//            CofferItem.ItemHandlerConsumer items = event.getItemColors()::register;
//            items.register((s, t) -> t == 1 ? getColorValue(CofferItem.getDyeColorNamed(s), s) : -1, ModItems.COFFER.get());
//
//        }
//    }

    @Override
    public void setColor(ItemStack p_41116_, int p_41117_) {
        DyeableLeatherItem.super.setColor(p_41116_, p_41117_);
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

    public static int getDyeColorNamed(String name) {

        if(HexereiUtil.getDyeColorNamed(name)!= null){
            float f3 = (((Hexerei.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = Sheep.getColorArray(col1);
            float[] afloat2 = Sheep.getColorArray(col2);
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValue(f, f1, f2);
        }
        return 0;

    }

    public static DyeColor getDyeColorNamed(ItemStack stack) {

        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if(context.getPlayer() != null && context.getPlayer().isSteppingCarefully())
            return InteractionResult.PASS;
        return super.place(context);
    }

    public ItemStackHandler createHandler() {
        return new ItemStackHandler(36) {

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        playerIn.startUsingItem(handIn);
        if (!level.isClientSide) {
            if (playerIn.isShiftKeyDown() && itemstack.getCount() == 1) {

                MenuProvider containerProvider = createContainerProvider(itemstack, handIn, itemstack.getTag());

                NetworkHooks.openScreen((ServerPlayer) playerIn, containerProvider, b -> b.writeBoolean(false).writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));

            }
        }
        return InteractionResultHolder.success(itemstack);
    }

    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand, CompoundTag list) {
        return new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new CofferContainer(i, itemStack, playerInventory, playerEntity, hand);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.hexerei.coffer");
            }

        };
    }


    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStackHandler handler = createHandler();
        handler.deserializeNBT(stack.getOrCreateTag().getCompound("Inventory"));

        return Optional.of(new CofferItem.CofferItemToolTip(handler, stack));
    }

    public record CofferItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
    }

    private static ItemStack[] getContents(ItemStack p_150783_) {
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return new ItemStack[0];
        } else {
            ItemStack[]stacks = new ItemStack[36];
            for(int i = 0; i < stacks.length; i++)
                stacks[i] = ItemStack.of(compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i));
            return stacks;
        }
    }

    private static int[] getContentsSlot(ItemStack p_150783_) {
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return new int[0];
        } else {
            int[]slots = new int[36];
            for(int i = 0; i < compoundtag.getCompound("Inventory").getList("Items", 10).size(); i++)
                slots[i] = compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i).getInt("Slot");
//            ListTag listtag = ;
            return slots;
        }
    }

}