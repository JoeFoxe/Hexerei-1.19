package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;


public class AddToCandleRecipe extends CustomRecipe {

    NonNullList<Ingredient> inputs;
    NonNullList<Ingredient> newInputs;
    ItemStack output;

    public AddToCandleRecipe(ResourceLocation pId, NonNullList<Ingredient> inputs, ItemStack output) {
        super(pId);
        NonNullList<Ingredient> newInputs = NonNullList.withSize(inputs.size() + 1, Ingredient.of(new ItemStack(ModItems.CANDLE.get())));
        for(int i = 0; i < inputs.size(); i++){
            newInputs.set(i + 1,inputs.get(i));
        }

        this.inputs = inputs;
        this.newInputs = newInputs;
        this.output = output;
    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {

                    if(newInputs.isEmpty() || newInputs.get(1).getItems().length == 0)
                        return false;

                    CompoundTag tag = new CompoundTag();
                    CompoundTag tag2 = new CompoundTag();
                    if(itemstack1.hasTag())
                        tag = itemstack1.getOrCreateTag();
                    if(newInputs.get(1).getItems()[0].hasTag())
                        tag2 = newInputs.get(1).getItems()[0].getOrCreateTag();
                    boolean compare = NbtUtils.compareNbt(tag2, tag, true);

                    if ((itemstack1.is(this.newInputs.get(1).getItems()[0].getItem()) && compare)) {
                        ++i;
                    }

                }
            }
        }

        return !itemstack.isEmpty() && i == 1;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer pInv) {
        int i = 0;
        ItemStack candle = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!candle.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    candle = itemstack1;
                } else {


                    CompoundTag tag = new CompoundTag();
                    CompoundTag tag2 = new CompoundTag();
                    if(itemstack1.hasTag())
                        tag = itemstack1.getOrCreateTag();
                    if(newInputs.get(1).getItems()[0].hasTag())
                        tag2 = newInputs.get(1).getItems()[0].getOrCreateTag();
                    boolean compare = NbtUtils.compareNbt(tag2, tag, true);

                    if (!itemstack1.is(this.newInputs.get(1).getItems()[0].getItem()) && compare) {
                        return ItemStack.EMPTY;
                    }
                    ++i;
                }
            }
        }

        if (!candle.isEmpty() && i >= 1) {
            ItemStack itemstack2 = candle.copy();
            itemstack2.setCount(1);

            itemstack2.getOrCreateTag().merge(output.getOrCreateTag());
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getResultItem() {
        return getOutput();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public NonNullList<Ingredient> getInputs() {
        return newInputs;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

//
//    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
//        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);
//
//        for(int i = 0; i < nonnulllist.size(); ++i) {
//            ItemStack itemstack = pInv.getItem(i);
//            if (itemstack.hasCraftingRemainingItem()) {
//                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
//            } else if (itemstack.getItem() instanceof WrittenBookItem) {
//                ItemStack itemstack1 = itemstack.copy();
//                itemstack1.setCount(1);
//                nonnulllist.set(i, itemstack1);
//                break;
//            }
//        }
//
//        return nonnulllist;
//    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADD_TO_CANDLE_SERIALIZER.get();
    }
//
    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Type implements RecipeType<AddToCandleRecipe> {
        private Type() { }
        public static final AddToCandleRecipe.Type INSTANCE = new AddToCandleRecipe.Type();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }


    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<AddToCandleRecipe> {
        public static final AddToCandleRecipe.Serializer INSTANCE = new AddToCandleRecipe.Serializer();

        @Override
        public AddToCandleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            inputs.set(0, Ingredient.of(input));

            return new AddToCandleRecipe(recipeId, inputs,
                    output);
        }

        @Nullable
        @Override
        public AddToCandleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new AddToCandleRecipe(recipeId, inputs, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AddToCandleRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients())
                ing.toNetwork(buffer);
            buffer.writeItem(recipe.getOutput());
        }

    }
}