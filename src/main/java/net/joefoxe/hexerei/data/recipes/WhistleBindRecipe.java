package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.joefoxe.hexerei.item.custom.WhistleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.UUID;


public class WhistleBindRecipe extends CustomRecipe {

    public WhistleBindRecipe(ResourceLocation registryName) {
        super(registryName);

    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer p_44004_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_44004_.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = p_44004_.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            }
            if(item.getItem() instanceof BroomItem broomItem)
                nonnulllist.set(i, item.copy());
        }
        return nonnulllist;
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        int whistle = 0;
        int other = 0;
        int broom = 0;
        ItemStack whistleItem = null;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof WhistleItem) {
                    ++whistle;
                    whistleItem = stack;
                } else if (stack.getItem() instanceof BroomItem) {
                    ++broom;
                } else {
                    ++other;
                }

                if (other > 1 || whistle > 1 || broom > 1) {
                    return false;
                }
            }
        }

        return whistle == 1 && other == 0 && (broom == 1 || (whistleItem.hasTag() && whistleItem.getOrCreateTag().contains("UUID")));
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory) {
        ItemStack whistleItem = ItemStack.EMPTY;
        UUID broomUUID = null;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof WhistleItem) {
                    whistleItem = stack.copy();
                    whistleItem.setCount(1);
                }
                if (stack.getItem() instanceof BroomItem) {
                    broomUUID = BroomItem.getUUID(stack);
                }
            }
        }

        if(broomUUID != null)
            whistleItem.getOrCreateTag().putUUID("UUID", broomUUID);
        else {
            if(whistleItem.hasTag())
                whistleItem.getOrCreateTag().remove("UUID");
        }

        return whistleItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 1;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.WHISTLE_BIND_SERIALIZER.get();
    }
}