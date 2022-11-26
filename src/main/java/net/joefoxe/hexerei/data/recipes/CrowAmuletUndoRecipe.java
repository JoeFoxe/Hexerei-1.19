package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.custom.CrowAmuletItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class CrowAmuletUndoRecipe extends CustomRecipe {

    public CrowAmuletUndoRecipe(ResourceLocation registryName) {
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
            if(item.getItem() instanceof CrowAmuletItem crowAmuletItem)
                nonnulllist.set(i, new ItemStack(crowAmuletItem));
        }
        return nonnulllist;
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        int keychain = 0;
        int other = 0;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof CrowAmuletItem) {
                    CompoundTag tag = stack.getOrCreateTag();

                    if(tag.contains("Items")){
                        ++keychain;
                    }
                } else {
                    ++other;
                }

                if (other > 1 || keychain > 1) {
                    return false;
                }
            }
        }

        return keychain == 1 && other == 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory) {
        ItemStack keychain = ItemStack.EMPTY;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof CrowAmuletItem) {
                    keychain = stack.copy();
                    keychain.setCount(1);
                }
            }
        }
        if (keychain.getItem() instanceof CrowAmuletItem) {
            CompoundTag tag = keychain.getOrCreateTag();
            ListTag list = tag.getList("Items", 10);
            keychain = ItemStack.of(list.getCompound(0));
        }

        return keychain;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 1;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CROW_AMULET_UNDO_SERIALIZER.get();
    }
}