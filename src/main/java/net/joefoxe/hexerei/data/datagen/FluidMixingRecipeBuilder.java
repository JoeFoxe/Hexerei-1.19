//package net.joefoxe.hexerei.data.datagen;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import net.joefoxe.hexerei.Hexerei;
//import net.joefoxe.hexerei.data.recipes.AddToCandleRecipe;
//import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
//import net.joefoxe.hexerei.fluid.PotionFluidHandler;
//import net.joefoxe.hexerei.util.HexereiUtil;
//import net.minecraft.advancements.Advancement;
//import net.minecraft.advancements.AdvancementRewards;
//import net.minecraft.advancements.CriterionTriggerInstance;
//import net.minecraft.advancements.RequirementsStrategy;
//import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
//import net.minecraft.core.NonNullList;
//import net.minecraft.core.Registry;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.data.recipes.RecipeBuilder;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.level.ItemLike;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//
//public class FluidMixingRecipeBuilder implements RecipeBuilder {
//
//    public static final List<Item> POTION_CONTAINERS = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
//    private final ArrayList<Ingredient> ingredients;
//    private final FluidStack input;
//    private final FluidStack result;
//    private final Advancement.Builder advancement = Advancement.Builder.advancement();
//
//    public FluidMixingRecipeBuilder(ArrayList<Ingredient> ingredients, FluidStack input, FluidStack result) {
//        this.result = result;
//        this.input = input;
//        this.ingredients = ingredients;
//    }
//
//    @Override
//    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
//        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
//        return this;
//    }
//
//    @Override
//    public RecipeBuilder group(@Nullable String pGroupName) {
//        return this;
//    }
//
//    @Override
//    public Item getResult() {
//        return null;
//    }
//
//    @Override
//    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
//        this.advancement.parent(new ResourceLocation("recipes/root"))
//                .addCriterion("has_the_recipe",
//                        RecipeUnlockedTrigger.unlocked(pRecipeId))
//                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
//
//        String path = "recipes/fluid_mixing/" + pRecipeId.getPath();
//
//        pFinishedRecipeConsumer.accept(new FluidMixingRecipeBuilder.Result(pRecipeId, this.input, this.result, this.ingredients,
//                this.advancement, new ResourceLocation(pRecipeId.getNamespace(), path.trim())));
//
//    }
//
//    public static class Result implements FinishedRecipe {
//        private final ResourceLocation id;
//        private final Advancement.Builder advancement;
//        private final ResourceLocation advancementId;
//
//
//        private final ArrayList<Ingredient> recipeItems;
//        private final FluidStack input;
//        private final FluidStack result;
//
//        public Result(ResourceLocation pId, FluidStack input, FluidStack result, ArrayList<Ingredient> recipeItems, Advancement.Builder pAdvancement,
//                      ResourceLocation pAdvancementId) {
//            this.id = pId;
//            this.advancement = pAdvancement;
//            this.advancementId = pAdvancementId;
//            this.recipeItems = recipeItems;
//            this.input = input;
//            this.result = result;
//        }
//
//        @Override
//        public void serializeRecipeData(JsonObject pJson) {
//            JsonArray jsonarray = new JsonArray();
//            for(Ingredient ingredient : this.recipeItems) {
//                jsonarray.add(ingredient.toJson());
//            }
//
//            pJson.add("ingredients", jsonarray);
//
//            pJson.add("input", serializeFluidStack(this.input, -1));
//
//            pJson.add("output", serializeFluidStack(this.result, -1));
//        }
//
//        @Override
//        public ResourceLocation getId() {
//
//            return new ResourceLocation(Hexerei.MOD_ID,
//                    Registry.POTION.getKey(PotionFluidHandler.getPotionFromFluidStack(this.result)).getPath() + "_from_fluid_mixing");
//        }
//
//        @Override
//        public RecipeSerializer<?> getType() {
//            return FluidMixingRecipe.Serializer.INSTANCE;
//        }
//
//        @Nullable
//        public JsonObject serializeAdvancement() {
//            return this.advancement.serializeToJson();
//        }
//
//        @Nullable
//        public ResourceLocation getAdvancementId() {
//            return this.advancementId;
//        }
//
//
//        public static JsonElement serializeFluidStack(FluidStack stack, int amount) {
//            JsonObject json = new JsonObject();
//            json.addProperty("fluid", HexereiUtil.getKeyOrThrow(stack.getFluid())
//                    .toString());
//            if(amount != -1)
//                json.addProperty("amount", stack.getAmount());
//            if (stack.hasTag())
//                json.addProperty("nbt", stack.getTag()
//                        .toString());
//            return json;
//        }
//    }
//}