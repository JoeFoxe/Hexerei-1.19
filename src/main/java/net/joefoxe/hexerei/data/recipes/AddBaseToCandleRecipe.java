package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;


public class AddBaseToCandleRecipe extends CustomRecipe {

    public AddBaseToCandleRecipe(CraftingBookCategory category) {
        super(category);

    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */

    public boolean matches(CraftingInput pInv, Level pLevel) {
        ItemStack itemstack = ItemStack.EMPTY;
        BlockItem block = null;

        for(int j = 0; j < pInv.size(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
                    if (block != null) {
                        return false;
                    }

                    block = blockItem;
                }
            }
        }

        return !itemstack.isEmpty() && block != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput pInv, HolderLookup.Provider registries) {
        int i = 0;
        ItemStack candle = ItemStack.EMPTY;
        BlockItem block = null;

        for(int j = 0; j < pInv.size(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!candle.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    candle = itemstack1;
                } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
                    try {
                        if (block != null || !blockItem.getBlock().defaultBlockState().getShape(null, null).equals(Shapes.block())) {
                            return ItemStack.EMPTY;
                        }

                        block = blockItem;
                    } catch (Exception exception) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (!candle.isEmpty() && block != null) {
            ItemStack itemstack2 = candle.copy();
            itemstack2.setCount(1);

            CandleData data = new CandleData();
            data.load(itemstack2.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(), registries);

            if (BuiltInRegistries.BLOCK.containsValue(block.getBlock())) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("layerFromBlockLocation", true);
                tag.putString("layer", BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
                data.base.load(tag);
            }
            CompoundTag tag = itemstack2.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            data.save(tag, registries, true);
            itemstack2.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getOutput();
    }

    public ItemStack getOutput() {
        return ModItems.CANDLE.get().getDefaultInstance();
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADD_BASE_TO_CANDLE_SERIALIZER.get();
    }
//
    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Type implements RecipeType<AddBaseToCandleRecipe> {
        private Type() { }
        public static final AddBaseToCandleRecipe.Type INSTANCE = new AddBaseToCandleRecipe.Type();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

}