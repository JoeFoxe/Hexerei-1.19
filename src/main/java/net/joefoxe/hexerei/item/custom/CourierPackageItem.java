package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.PackageContainer;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CourierPackageItem extends BlockItem {

    public CourierPackageItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // open message menu
        if(isSealed(pContext.getItemInHand()))
            return super.useOn(pContext);

        if (!pContext.isSecondaryUseActive()) {
            if (pContext.getPlayer() != null) {
                if (!pContext.getLevel().isClientSide) {
                    openMenu(pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand());
                }
            }
        }

        return pContext.isSecondaryUseActive() ? super.useOn(pContext) : InteractionResult.CONSUME;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        if (!level.isClientSide) {
            openMenu(playerIn, handIn, itemstack);
        }
        return itemstack.getCount() == 1 ? (isSealed(itemstack) ? InteractionResultHolder.fail(itemstack) : InteractionResultHolder.consume(itemstack)) : InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(stack);

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(wrapper.isEmpty()) {
                // say how to open the menu and seal
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_use").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_menu").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else
            {
                if(wrapper.getSealed()) {
                    // say how to deliver or how to open
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    // must be sealed
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
    }

    private boolean openMenu(Player player, InteractionHand hand, ItemStack stack) {
        if (!player.isSteppingCarefully() && stack.getCount() == 1) {
            if (!isSealed(stack)){
                MenuProvider containerProvider = createContainerProvider(stack, hand);

                int slotIndex = hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected;
                player.openMenu(containerProvider, b -> b.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1).writeByte(slotIndex));
                return true;
            }
        }
        return false;
    }

    public static boolean isSealed(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data != null && data.copyTag().contains("Sealed") && data.copyTag().getBoolean("Sealed");
    }

    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand) {
        return new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new PackageContainer(i, itemStack, playerInventory, hand, hand == InteractionHand.OFF_HAND ? -1 : playerEntity.getInventory().selected);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.hexerei.package");
            }

        };
    }


//    @Override
//    public @org.jetbrains.annotations.Nullable ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {
//        return new PackageInvWrapper(stack);
//    }

    public static class PackageInvWrapper implements IItemHandlerModifiable
    {
        private final ItemStack stack;

        private CompoundTag cachedTag;
        private NonNullList<ItemStack> itemStacksCache;
        private boolean sealed;

        public PackageInvWrapper(ItemStack stack) {
            this.stack = stack;
            this.sealed = getSealed();
        }

        @Override
        public int getSlots() {
            return 5;
        }

        public boolean isEmpty() {
            NonNullList<ItemStack> list = getItemList();
            for(ItemStack stack1 : list) {
                if (!stack1.isEmpty())
                    return false;
            }
            return true;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            if (!validateSlotIndex(slot))
                return ItemStack.EMPTY;
            return getItemList().get(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!isItemValid(slot, stack))
                return stack;

            validateSlotIndex(slot);

            NonNullList<ItemStack> itemStacks = getItemList();

            ItemStack existing = itemStacks.get(slot);

            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    itemStacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                }
                else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                setItemList(itemStacks);
            }

            return reachedLimit ? stack.copyWithCount(stack.getCount()- limit) : ItemStack.EMPTY;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            NonNullList<ItemStack> itemStacks = getItemList();
            if (amount == 0)
                return ItemStack.EMPTY;

            validateSlotIndex(slot);

            ItemStack existing = itemStacks.get(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    itemStacks.set(slot, ItemStack.EMPTY);
                    setItemList(itemStacks);
                    return existing;
                }
                else {
                    return existing.copy();
                }
            }
            else {
                if (!simulate) {
                    itemStacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                    setItemList(itemStacks);
                }

                return existing.copyWithCount(toExtract);
            }
        }

        private boolean validateSlotIndex(int slot) {
            if (slot < 0 || slot >= getSlots()) {
                System.out.println("invalid slot - " + slot);
                return false;
            }
            return true;
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.getItem() instanceof CourierPackageItem) {
                PackageInvWrapper wrapper = new PackageInvWrapper(stack);
                if (!wrapper.isEmpty())
                    return false;
            }


            return stack.getItem().canFitInsideContainerItems();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlotIndex(slot);
            if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
            NonNullList<ItemStack> itemStacks = getItemList();
            itemStacks.set(slot, stack);
            setItemList(itemStacks);
        }

        private NonNullList<ItemStack> getItemList() {
            CustomData data = this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            CompoundTag rootTag = data.copyTag();
            if (cachedTag == null || !cachedTag.equals(rootTag))
                itemStacksCache = refreshItemList(rootTag);
            return itemStacksCache;
        }

        private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag) {
            NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
            if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST)) {
                ContainerHelper.loadAllItems(rootTag, itemStacks, Hexerei.DynamicRegistries.get());
            }
            cachedTag = rootTag;
            return itemStacks;
        }


        private void setItemList(NonNullList<ItemStack> itemStacks) {

            boolean isEmpty = true;
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty()) {
                    isEmpty = false;
                }
            }

            CompoundTag existing = this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            CompoundTag rootTag = ContainerHelper.saveAllItems(existing, itemStacks, Hexerei.DynamicRegistries.get());

            if (!isEmpty) {
                BlockItem.setBlockEntityData(this.stack, ModTileEntities.COURIER_PACKAGE_TILE.get(), rootTag);
                cachedTag = rootTag;
            } else {
                this.stack.remove(DataComponents.BLOCK_ENTITY_DATA);
                cachedTag = null;
            }

        }

        public void setSealed(int sealed) {

            this.sealed = sealed == 1;

            CustomData existing = this.stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (existing != null) {
                CompoundTag tag = existing.copyTag();
                if (isEmpty()) {
                    this.sealed = false;
                    tag.putBoolean("Sealed", false);
                    return;
                }
                tag.putBoolean("Sealed", this.sealed);
//                this.stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
                BlockItem.setBlockEntityData(stack, ModTileEntities.COURIER_PACKAGE_TILE.get(), tag);

                cachedTag = tag;
            }
            else
                this.sealed = false;

        }

        public boolean getSealed() {

            CustomData existing = this.stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (existing != null)
                return existing.contains("Sealed") && existing.copyTag().getBoolean("Sealed");
            this.sealed = false;
            return false;
        }

//        @Override
//        public @org.jetbrains.annotations.Nullable IItemHandler getCapability(ItemStack stack, Void context) {
//            return Capabilities.ItemHandler.ITEM.getCapability(stack, context);
//        }

//        @Override
//        @NotNull
//        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
//            return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, this.holder);
//        }
    }

}