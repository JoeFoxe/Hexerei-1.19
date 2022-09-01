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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DipperRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final FluidStack liquid;
    private final int fluidLevelsConsumed;
    private final int dippingTime;
    private final int dryingTime;
    private final int numberOfDips;
    private final boolean useInputItemAsOutput;
    protected static final List<Boolean> itemMatchesSlot = new ArrayList<>();


    public DipperRecipe(ResourceLocation id, NonNullList<Ingredient> inputs,
                        ItemStack output, FluidStack liquid, int fluidLevelsConsumed,
                        int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        this.id = id;
        this.output = output;
        this.recipeItems = inputs;
        this.liquid = liquid;
        this.fluidLevelsConsumed = fluidLevelsConsumed;
        this.dippingTime = dippingTime;
        this.dryingTime = dryingTime;
        this.numberOfDips = numberOfDips;
        this.useInputItemAsOutput = useInputItemAsOutput;

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
        if(recipeItems.get(0).test(inv.getItem(0) )||
                recipeItems.get(0).test(inv.getItem(1)) ||
                        recipeItems.get(0).test(inv.getItem(2)))
            return true;

        return false;


    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer inv) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {

        return output.copy();
    }

    public FluidStack getLiquid() { return this.liquid; }

    public int getFluidLevelsConsumed() { return this.fluidLevelsConsumed; }

    public int getDippingTime() { return this.dippingTime; }

    public int getDryingTime() { return this.dryingTime; }

    public int getNumberOfDips() { return this.numberOfDips; }

    public boolean getUseInputItemAsOutput() { return this.useInputItemAsOutput; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.CANDLE_DIPPER.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.DIPPER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<DipperRecipe> {
        private Type() { }
        public static final DipperRecipe.Type INSTANCE = new DipperRecipe.Type();
        public static final String ID = "dipper";
    }


    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<DipperRecipe> {
        public static final DipperRecipe.Serializer INSTANCE = new DipperRecipe.Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Hexerei.MOD_ID,"dipper");

        @Override
        public DipperRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            FluidStack liquid = deserializeFluidStack(GsonHelper.getAsJsonObject(json, "liquid"));
            boolean useInputItemAsOutput = GsonHelper.getAsBoolean(json, "useInputItemAsOutput", false);
            int fluidLevelsConsumed = GsonHelper.getAsInt(json, "fluidLevelsConsumed");
            int dippingTime = GsonHelper.getAsInt(json, "dippingTimeInTicks");
            int dryingTime = GsonHelper.getAsInt(json, "dryingTimeInTicks");
            int numberOfDips = GsonHelper.getAsInt(json, "numberOfDips");

            inputs.set(0, Ingredient.of(input));

            return new DipperRecipe(recipeId, inputs,
                    output, liquid, fluidLevelsConsumed, dippingTime, dryingTime, numberOfDips, useInputItemAsOutput);
        }

        @Nullable
        @Override
        public DipperRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
//            inputs.add(Ingredient.fromNetwork(buffer));
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new DipperRecipe(recipeId, inputs, output,
                    buffer.readFluidStack(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DipperRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients())
                ing.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());

            buffer.writeFluidStack(recipe.getLiquid());

            buffer.writeInt(recipe.getFluidLevelsConsumed());
            buffer.writeInt(recipe.getDippingTime());
            buffer.writeInt(recipe.getDryingTime());
            buffer.writeInt(recipe.getNumberOfDips());
            buffer.writeBoolean(recipe.getUseInputItemAsOutput());
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
}
