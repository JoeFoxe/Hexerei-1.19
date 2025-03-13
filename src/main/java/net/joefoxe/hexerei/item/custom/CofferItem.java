package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class CofferItem extends BlockItem {

    public CofferItem(Block block, Properties properties) {
        super(block, properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = getColorStatic(stack);
        if(color == null && dyeCol != -1)
            return dyeCol;
        return color.getTextureDiffuseColor();
    }

    public static int getColorStatic(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(0x422F1E, true)).rgb();
    }

    public static int getDyeColorNamed(String name) {

        if(HexereiUtil.getDyeColorNamed(name)!= null){
            float f3 = (((ClientEvents.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col1.getTextureDiffuseColor());
            float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
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
    public InteractionResult place(BlockPlaceContext context) {
        if(context.getPlayer() != null && !context.getPlayer().isCrouching())
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
        boolean isCrouching = playerIn.isCrouching();

        playerIn.startUsingItem(handIn);
        if (playerIn instanceof ServerPlayer serverPlayer) {
            if (!isCrouching && itemstack.getCount() == 1) {

                MenuProvider containerProvider = createContainerProvider(itemstack, handIn);

                serverPlayer.openMenu(containerProvider, b -> b.writeBoolean(false).writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));
            }
        }
        return isCrouching ? InteractionResultHolder.pass(itemstack) : InteractionResultHolder.success(itemstack);
    }

    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand) {
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
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("Inventory") && FMLEnvironment.dist.isClient())
            handler.deserializeNBT(Minecraft.getInstance().level.registryAccess(), tag.getCompound("Inventory"));

        return Optional.of(new CofferItem.CofferItemToolTip(handler, stack));
    }

    public record CofferItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
    }

//    private static ItemStack[] getContents(ItemStack p_150783_) {
//        CompoundTag compoundtag = p_150783_.getTag();
//        if (compoundtag == null) {
//            return new ItemStack[0];
//        } else {
//            ItemStack[]stacks = new ItemStack[36];
//            for(int i = 0; i < stacks.length; i++)
//                stacks[i] = ItemStack.of(compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i));
//            return stacks;
//        }
//    }
//
//    private static int[] getContentsSlot(ItemStack p_150783_) {
//        CompoundTag compoundtag = p_150783_.getTag();
//        if (compoundtag == null) {
//            return new int[0];
//        } else {
//            int[]slots = new int[36];
//            for(int i = 0; i < compoundtag.getCompound("Inventory").getList("Items", 10).size(); i++)
//                slots[i] = compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i).getInt("Slot");
////            ListTag listtag = ;
//            return slots;
//        }
//    }

}