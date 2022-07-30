package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;


public class BookOfShadowsRecipe extends ShapedRecipe {
    public static final RecipeSerializer<BookOfShadowsRecipe> SERIALIZER = new Serializer();

    public BookOfShadowsRecipe(ShapedRecipe compose) {
        super(compose.getId(), compose.getGroup(), compose.getWidth(), compose.getHeight(), compose.getIngredients(), compose.getResultItem());
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();

            int colorId;
            if (item instanceof DyeItem dye) {
                colorId = dye.getDyeColor().getId();
                return HexereiBookItem.withColors(colorId);
            }
        }
        return HexereiBookItem.withColors(0);
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ModItems.BOOK_OF_SHADOWS.get());
    }

    private static class Serializer implements RecipeSerializer<BookOfShadowsRecipe> {
        @Nonnull
        @Override
        public BookOfShadowsRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return new BookOfShadowsRecipe(SHAPED_RECIPE.fromJson(recipeId, json));
        }

        @Nonnull
        @Override
        public BookOfShadowsRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            return new BookOfShadowsRecipe(SHAPED_RECIPE.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull BookOfShadowsRecipe recipe) {
            SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}