package net.joefoxe.hexerei.data.datagen;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WoodcutterRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final int ingredient_count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    private final String type;

    public WoodcutterRecipeBuilder(ItemLike ingredient, ItemLike result, int count, int ingredient_count, String type) {
        this.ingredient = Ingredient.of(ingredient);
        this.result = result.asItem();
        this.count = count;
        this.ingredient_count = ingredient_count;
        this.type = type;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("give_recipe",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(this.ingredient.getItems()[0].getItem()).build()))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.AND);

        String path = "recipes/" + pRecipeId.getPath();

        pFinishedRecipeConsumer.accept(new WoodcutterRecipeBuilder.Result(pRecipeId, this.result, this.count, this.ingredient_count, this.ingredient,
                this.advancement, new ResourceLocation(pRecipeId.getNamespace(), path.trim()),this.type));

    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final Ingredient ingredient;
        private final int count;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public final int ingredientCount;
        public final String type;

        public Result(ResourceLocation pId, Item pResult, int pCount, int ingredientCount, Ingredient ingredient, Advancement.Builder pAdvancement,
                      ResourceLocation pAdvancementId, String type) {
            this.ingredientCount = ingredientCount;
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.ingredient = ingredient;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.type = type;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(ingredient.getItems()[0].getItem()).toString());

            pJson.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
            pJson.add("ingredient", jsonobject);
            if (this.count > 1) {
                pJson.addProperty("count", this.count);
            }
            if (this.ingredientCount > 1) {
                pJson.addProperty("ingredient_count", this.ingredientCount);
            }

        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(Hexerei.MOD_ID,
                    "woodcutting" + "/" + this.type + "/" + ForgeRegistries.ITEMS.getKey(this.result).getPath() + "_from_" + ForgeRegistries.ITEMS.getKey(this.ingredient.getItems()[0].getItem()).getPath() + "_woodcutting");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return WoodcutterRecipe.Serializer.INSTANCE;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}