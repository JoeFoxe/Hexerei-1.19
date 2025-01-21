package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidMixingRecipe implements Recipe<RecipeInput> {

    private final NonNullList<Ingredient> recipeItems;
    private final FluidStack fluid;
    private final FluidStack fluidOutput;
    private final HeatCondition heatCondition;


    @Override
    public boolean isSpecial() {
        return true;
    }

    public FluidMixingRecipe(NonNullList<Ingredient> recipeItems, FluidStack fluid, FluidStack fluidOutput) {
        this(recipeItems, fluid, fluidOutput, HeatCondition.NONE);
    }
    public FluidMixingRecipe(NonNullList<Ingredient> recipeItems, FluidStack fluid, FluidStack fluidOutput, HeatCondition heatCondition) {
        this.recipeItems = recipeItems;
        this.fluid = fluid;
        this.fluidOutput = fluidOutput;
        this.heatCondition = heatCondition;
    }


    @Override
    public boolean matches(RecipeInput input, Level worldIn) {

        List<Boolean> itemMatchesSlot = Stream.generate(() -> false).limit(8).collect(Collectors.toList());

        // the flag is to break out early in case nothing matches for that slot
        boolean flag = false;

        // cycle through each recipe slot
        for (Ingredient recipeItem : recipeItems) {
            //cycle through each slot for each recipe slot
            for (int i = 0; i < 8; i++) {
                //if the recipe matches a slot
                if (recipeItem.test(input.getItem(i))) {
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
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }


    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public HeatCondition getHeatCondition() { return this.heatCondition; }
    public FluidStack getLiquid() { return this.fluid; }

    public FluidStack getLiquidOutput() { return this.fluidOutput; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.MIXING_CAULDRON.get());
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
    }

    public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<FluidMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
                                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
                                FluidStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.fluidOutput),
                                HeatCondition.CODEC.fieldOf("heatRequirement").forGetter(recipe -> recipe.heatCondition)
                        )
                        .apply(instance, FluidMixingRecipe::new)
        );
        //        public FluidMixingRecipe(NonNullList<Ingredient> inputs, ItemStack output, FluidStack fluid, int fluidLevelsConsumed, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        public static final StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> STREAM_CODEC = StreamCodec.of(
                FluidMixingRecipe.Serializer::toNetwork, FluidMixingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<FluidMixingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FluidMixingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            inputs.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(buffer);
            FluidStack outputFluid = FluidStack.STREAM_CODEC.decode(buffer);
            HeatCondition heatCondition = NeoForgeStreamCodecs.enumCodec(HeatCondition.class).decode(buffer);

            return new FluidMixingRecipe(inputs, inputFluid, outputFluid, heatCondition);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FluidMixingRecipe recipe) {
            buffer.writeInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems)
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluidOutput);
            NeoForgeStreamCodecs.enumCodec(HeatCondition.class).encode(buffer, recipe.heatCondition);
        }
    }




    public enum HeatCondition implements StringRepresentable {

        NONE, HEATED, SUPERHEATED,
        ;

        public String toString() {
            return this.getSerializedName();
        }

        public static final StringRepresentable.EnumCodec<HeatCondition> CODEC = StringRepresentable.fromEnum(HeatCondition::values);

        public static HeatCondition getHeated(String str) {
            return switch (str){
                case "heated" -> HEATED;
                case "superheated" -> SUPERHEATED;
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
