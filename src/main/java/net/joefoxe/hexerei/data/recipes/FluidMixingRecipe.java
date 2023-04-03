package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.fluid.FluidIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidMixingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> recipeItems;
    private final FluidStack liquid;
    private final FluidStack liquidOutput;
    protected static final List<Boolean> itemMatchesSlot = new ArrayList<>();

    private final HeatCondition heatCondition;

    @Override
    public boolean isSpecial() {
        return true;
    }

    public FluidMixingRecipe(ResourceLocation id, NonNullList<Ingredient> recipeItems, FluidStack liquid, FluidStack liquidOutput) {
        this.id = id;
//        NonNullList<Ingredient> recipeItemsTemp = NonNullList.withSize(recipeItems.length, Ingredient.EMPTY);
//
//        for(int i = 0; i < recipeItems.length; i++){
//            recipeItemsTemp.set(i, Ingredient.of(recipeItems));
//        }
        this.recipeItems = recipeItems;
        this.liquid = liquid;
        this.liquidOutput = liquidOutput;
        this.heatCondition = HeatCondition.NONE;

        for(int i = 0; i < 8; i++) {
            itemMatchesSlot.add(false);
        }

    }
    public FluidMixingRecipe(ResourceLocation id, NonNullList<Ingredient> recipeItems, FluidStack liquid, FluidStack liquidOutput, HeatCondition heatCondition) {
        this.id = id;
//        NonNullList<Ingredient> recipeItemsTemp = NonNullList.withSize(recipeItems.length, Ingredient.EMPTY);
//
//        for(int i = 0; i < recipeItems.length; i++){
//            recipeItemsTemp.set(i, Ingredient.of(recipeItems));
//        }
        this.recipeItems = recipeItems;
        this.liquid = liquid;
        this.liquidOutput = liquidOutput;
        this.heatCondition = heatCondition;

        for(int i = 0; i < 8; i++) {
            itemMatchesSlot.add(false);
        }

    }

    public List<FluidIngredient> getFluidIngredients(){
        return new ArrayList<>(List.of(FluidIngredient.fromFluidStack(this.liquid)));
    }
    public FluidIngredient getFluidIngredient(){
        return FluidIngredient.fromFluidStack(this.liquid);
    }


    @Override
    public boolean matches(SimpleContainer inv, Level worldIn) {

        for(int i = 0; i < 8; i++)
            itemMatchesSlot.set(i, false);

        // the flag is to break out early in case nothing matches for that slot
        boolean flag = false;

        // cycle through each recipe slot
        for (Ingredient recipeItem : recipeItems) {
            //cycle through each slot for each recipe slot
            for (int i = 0; i < 8; i++) {
                //if the recipe matches a slot
                if (recipeItem.test(inv.getItem(i))) {
                    // if the slot is not taken up
                    if (!itemMatchesSlot.get(i)) {
                        //mark the slot as taken up
                        itemMatchesSlot.set(i, true);
                        flag = true;
                        break;
                    }
                }
            }
            //this is where it breaks out early to stop the craft
            if (!flag)
                break;
            //reset the flag for the next iteration
            flag = false;
        }
        // checks if a slot is not taken up, if it's not taken up then itll not craft
        for(int i = 0; i < 8; i++) {
            if (!itemMatchesSlot.get(i))
                return false;
        }
        //if it reaches here that means it has completed the shapeless craft and should craft it
        return true;


//        SHAPED CRAFTING - maybe bring this back as another config in the recipe to see if its shaped or shapeless
//        if(recipeItems.get(0).test(inv.getItem(0)) &&
//            recipeItems.get(1).test(inv.getItem(1)) &&
//            recipeItems.get(2).test(inv.getItem(2)) &&
//            recipeItems.get(3).test(inv.getItem(3)) &&
//            recipeItems.get(4).test(inv.getItem(4)) &&
//            recipeItems.get(5).test(inv.getItem(5)) &&
//            recipeItems.get(6).test(inv.getItem(6)) &&
//            recipeItems.get(7).test(inv.getItem(7)))
//        {
//            return true;
//        }
//        return false;

    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }


    @Override
    public ItemStack assemble(SimpleContainer p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {

        return ItemStack.EMPTY;
    }

    public HeatCondition getHeatCondition() { return this.heatCondition; }
    public FluidStack getLiquid() { return this.liquid; }

    public FluidStack getLiquidOutput() { return this.liquidOutput; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.MIXING_CAULDRON.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.FLUID_MIXING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return FluidMixingRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<FluidMixingRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_mixing";
    }

    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
            public static final Serializer INSTANCE = new Serializer();
            public static final ResourceLocation ID =
                    new ResourceLocation(Hexerei.MOD_ID,"fluid_mixing");

        @Override
        public FluidMixingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//            HexereiUtil.readEnum(GsonHelper.getas, "Bottle", PotionFluid.BottleType.class);
            FluidStack liquid = deserializeFluidStack(GsonHelper.getAsJsonObject(json, "input"));
            FluidStack liquidOutput = deserializeFluidStack(GsonHelper.getAsJsonObject(json, "output"));

            String heatRequirement = GsonHelper.getAsString(json, "heatRequirement", "none");
            HeatCondition heatCondition = HeatCondition.getHeated(heatRequirement);

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(8, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new FluidMixingRecipe(recipeId,
                    inputs, liquid, liquidOutput, heatCondition);
        }

        @Nullable
        @Override
        public FluidMixingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(8, Ingredient.EMPTY);

            for (int i = 0; i < buffer.readInt(); i++) {
                inputs.add(Ingredient.fromNetwork(buffer));
            }

            FluidStack inputFluid = buffer.readFluidStack();
            FluidStack outputFluid = buffer.readFluidStack();
            HeatCondition heated = buffer.readEnum(HeatCondition.class);


            return new FluidMixingRecipe(
                    recipeId,
                    inputs,
                    inputFluid,
                    outputFluid,
                    heated);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidMixingRecipe recipe) {

            buffer.writeInt(recipe.getIngredients().size());

            //ingredients
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buffer);
            }
            //input
            buffer.writeFluidStack(recipe.getLiquid());
            //output
            buffer.writeFluidStack(recipe.getLiquidOutput());
            //enum heatsource
            buffer.writeEnum(recipe.heatCondition);
        }

        public static FluidStack deserializeFluidStack(JsonObject json) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
            if (fluid == null)
                throw new JsonSyntaxException("Unknown fluid '" + id + "'");
            FluidStack stack = new FluidStack(fluid, 1);

            if (!json.has("nbt"))
                return stack;

            try {
                JsonElement element = json.get("nbt");
                stack.setTag(TagParser.parseTag(
                        element.isJsonObject() ? Hexerei.GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));

            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            return stack;
        }

    }

    public enum HeatCondition implements StringRepresentable {

        NONE, HEATED, SUPERHEATED,
        ;

        public String toString() {
            return this.getSerializedName();
        }

        public static HeatCondition getHeated(String str) {
            return switch (str){
                case "heated" -> HEATED;
                case "superheated" -> SUPERHEATED;
                case "none" -> NONE;
                default -> NONE;
            };
        }

        public String getSerializedName() {
            return switch (this){
                case HEATED -> "heated";
                case SUPERHEATED -> "superheated";
                case NONE -> "none";
            };
        }
    }
}
