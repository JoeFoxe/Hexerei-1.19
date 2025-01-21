package net.joefoxe.hexerei.data.datagen;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.AddToCandleRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AddToCandleRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final Ingredient ingredient;
//    private final int count;
//    private final CompoundTag nbt;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public AddToCandleRecipeBuilder(ItemLike ingredient, ItemStack result) {
        this.ingredient = Ingredient.of(ingredient);
        this.result = result;
//        this.count = result.getCount();
//        this.nbt = result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> criterion) {
        this.advancement.addCriterion(pCriterionName, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        save(recipeOutput, resourceLocation.toString());
    }

//    @Override
//    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
//        this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
//                .addCriterion("has_the_recipe",
//                        RecipeUnlockedTrigger.unlocked(pRecipeId))
//                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
//
//        String path = "recipes/add_to_candle/" + pRecipeId.getPath();
//
//        recipeOutput.accept(new AddToCandleRecipeBuilder.Result(id, this.result, this.count, this.ingredient,
//                this.advancement, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path.trim()), this.nbt));
//    }
//
//    @Override
//    public void save(RecipeOutput recipeOutput) {
//        RecipeBuilder.super.save(recipeOutput);
//    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
//        AdvancementHolder holder = recipeOutput.advancement()
//                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceLocation.parse(id)))
//                .rewards(AdvancementRewards.Builder.recipe(ResourceLocation.parse(id)))
//                .requirements(AdvancementRequirements.Strategy.OR)
//                .build(ResourceLocation.withDefaultNamespace("recipe/root"));
//
//        AddToCandleRecipe recipe = new AddToCandleRecipe(this.ingredient, this.result);
//
//        String path = ResourceLocation.parse(id).getPath();
//
//        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, path.trim()), recipe, holder);





        AdvancementHolder holder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceLocation.parse(id)))
                .rewards(AdvancementRewards.Builder.recipe(ResourceLocation.parse(id)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .build(ResourceLocation.withDefaultNamespace("recipes/root"));
//        this.advancement.build(ResourceLocation.withDefaultNamespace("recipes/root"))
//                .addCriterion("has_the_recipe",
//                        RecipeUnlockedTrigger.unlocked(pRecipeId))
//                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);

        FluidStack fls1 = PotionFluidHandler.getFluidFromPotion(Potions.FIRE_RESISTANCE, PotionFluid.BottleType.SPLASH, 250);

        FluidStack fls2 = PotionFluidHandler.getFluidFromPotion(Potions.WATER, PotionFluid.BottleType.SPLASH, 250);

        FluidMixingRecipe recipe = new FluidMixingRecipe(NonNullList.withSize(8, Ingredient.EMPTY), fls1, fls2);

//        AddToCandleRecipe recipe = new AddToCandleRecipe(this.ingredient, this.result);

        String path = ResourceLocation.parse(id).getPath();

        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, path.trim()), recipe, holder);

    }

//    public static class Result implements RecipeOutput {
//        private final ResourceLocation id;
//        private final Item result;
//        private final Ingredient ingredient;
//        private final int count;
//        private final Advancement.Builder advancement;
//        private final ResourceLocation advancementId;
//
//        public final CompoundTag nbt;
//
//        public Result(ResourceLocation pId, Item pResult, int pCount, Ingredient ingredient, Advancement.Builder pAdvancement,
//                      ResourceLocation pAdvancementId, CompoundTag nbt) {
//            this.nbt = nbt;
//            this.id = pId;
//            this.result = pResult;
//            this.count = pCount;
//            this.ingredient = ingredient;
//            this.advancement = pAdvancement;
//            this.advancementId = pAdvancementId;
//        }
//
//        @Override
//        public void serializeRecipeData(JsonObject pJson) {
//            JsonObject jsonobject = new JsonObject();
//            jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(ingredient.getItems()[0].getItem()).toString());
//
//            pJson.add("input", jsonobject);
//            jsonobject = new JsonObject();
//            jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
//            if(nbt != null && !nbt.isEmpty())
//                jsonobject.addProperty("nbt", this.nbt.toString());
//            if (this.count > 1) {
//                jsonobject.addProperty("count", this.count);
//            }
//
//            pJson.add("output", jsonobject);
//        }
//
//        @Override
//        public ResourceLocation getId() {
//            return HexereiUtil.getResource(
//                    ForgeRegistries.ITEMS.getKey(this.ingredient.getItems()[0].getItem()).getPath() + "_add_to_candle");
//        }
//
//        @Override
//        public RecipeSerializer<?> getType() {
//            return AddToCandleRecipe.Serializer.INSTANCE;
//        }
//
//        @javax.annotation.Nullable
//        public JsonObject serializeAdvancement() {
//            return this.advancement.serializeToJson();
//        }
//
//        @javax.annotation.Nullable
//        public ResourceLocation getAdvancementId() {
//            return this.advancementId;
//        }
//
//        @Override
//        public Advancement.Builder advancement() {
//            return null;
//        }
//
//        @Override
//        public void accept(ResourceLocation id, Recipe<?> recipe, @org.jetbrains.annotations.Nullable AdvancementHolder advancement, ICondition... conditions) {
//
//        }
//    }
}