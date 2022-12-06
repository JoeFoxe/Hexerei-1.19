package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class FillWaxingKitRecipe extends CustomRecipe {

    int waxUsed;
    public FillWaxingKitRecipe(ResourceLocation pId) {
        super(pId);
        waxUsed = 0;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        int kit = 0;
        int wax = 0;
        ItemStack kit_item = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack container_item = pInv.getItem(j);
            if (!container_item.isEmpty()) {
                if (container_item.is(ModItems.WAX_BLEND.get())) {
                    ++wax;
                } else {
                    if (!container_item.is(ModItems.WAXING_KIT.get())) {
                        return false;
                    }

                    kit_item = container_item;
                    ++kit;
                }
            }
        }

        return wax >= 1 && kit == 1 && (!kit_item.hasTag() || (!kit_item.getOrCreateTag().contains("waxCount") || kit_item.getOrCreateTag().getInt("waxCount") < 256));
    }

    public ItemStack assemble(CraftingContainer pInv) {
        int kit_i = 0;
        int wax_i = 0;
        ItemStack kit = ItemStack.EMPTY;
//        ItemStack wax = ItemStack.EMPTY;
        List<ItemStack> wax = new ArrayList<>();

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.WAX_BLEND.get())) {
                    wax.add(itemstack1);
                    wax_i += itemstack1.getCount();

                } else {
                    kit = itemstack1;
                    ++kit_i;
                }
            }
        }

        if (wax_i >= 1 && kit_i == 1) {
            ItemStack itemstack2 = kit.copy();
            int baseCount = 0;
            CompoundTag tag = itemstack2.getOrCreateTag();
            if(tag.contains("waxCount")){
                baseCount = tag.getInt("waxCount");
            }
            int count = baseCount + wax_i;
            if(count <= 256){
                this.waxUsed = wax_i;
                tag.putInt("waxCount", count);
            } else {

                this.waxUsed = 256 - baseCount;
                tag.putInt("waxCount", 256);
            }
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

        int waxUsedTemp = this.waxUsed;
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = pInv.getItem(i);
            if (itemstack.hasCraftingRemainingItem()) {
                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.getItem() == ModItems.WAX_BLEND.get()) {
                int toSubtract = 0;
                for(int j = 0; j < itemstack.getCount(); j++){
                    if(waxUsedTemp > 0){
                        toSubtract++;
                        waxUsedTemp--;
                    }
                }
                int count = itemstack.getCount() - toSubtract;
                if(count != 0) {
                    itemstack.setCount(count);
                    pInv.setItem(i, itemstack.copy());
                    itemstack.setCount(1);
                    nonnulllist.set(i, itemstack);
                }else {

                    pInv.setItem(i, ItemStack.EMPTY);
                    nonnulllist.set(i, ItemStack.EMPTY);
                }
            }
        }

        return nonnulllist;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.FILL_WAXING_KIT_SERIALIZER.get();
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }
}