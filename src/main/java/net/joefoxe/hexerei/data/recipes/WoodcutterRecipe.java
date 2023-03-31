package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;

public class WoodcutterRecipe extends SingleItemRecipe {

    public int ingredientCount;
    public WoodcutterRecipe(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult, int ingredientCount) {
        super(ModRecipeTypes.WOODCUTTING_TYPE.get(), ModRecipeTypes.WOODCUTTING_SERIALIZER.get(), pId, pGroup, pIngredient, pResult);
        this.ingredientCount = ingredientCount;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(Container pInv, Level pLevel) {
        return this.ingredient.test(pInv.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.WILLOW_WOODCUTTER.get());
    }


    @Override
    public RecipeType<?> getType() {
        return WoodcutterRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<WoodcutterRecipe> {
        private Type() { }
        public static final WoodcutterRecipe.Type INSTANCE = new WoodcutterRecipe.Type();
        public static final String ID = "woodcutting";
    }


    public static class Serializer implements RecipeSerializer<WoodcutterRecipe> {
        public static final WoodcutterRecipe.Serializer INSTANCE = new WoodcutterRecipe.Serializer();

        public WoodcutterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String group = GsonHelper.getAsString(pJson, "group", "");
            Ingredient ingredient;
            int ingredient_count = GsonHelper.getAsInt(pJson, "ingredient_count", 1);
            if (GsonHelper.isArrayNode(pJson, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(pJson, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "ingredient"));
            }

            String result = GsonHelper.getAsString(pJson, "result");
            int count = GsonHelper.getAsInt(pJson, "count", 1);
            ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(result)), count);
            return new WoodcutterRecipe(pRecipeId, group, ingredient, itemstack, ingredient_count);
        }

        public WoodcutterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            int ingredient_count = pBuffer.readInt();
            return new WoodcutterRecipe(pRecipeId, group, ingredient, result, ingredient_count);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, WoodcutterRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeInt(pRecipe.ingredientCount);
        }
    }

}