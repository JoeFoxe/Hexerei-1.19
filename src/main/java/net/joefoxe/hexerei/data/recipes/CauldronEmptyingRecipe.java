package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;


public class CauldronEmptyingRecipe implements Recipe<CauldronEmptyingRecipe.Wrapper> {

    private final Ingredient input;
    private final FluidStack fluid;
    private final ItemStack output;

    public CauldronEmptyingRecipe(Ingredient input, FluidStack fluid, ItemStack output) {
        this.input = input;
        this.fluid = fluid;
        this.output = output;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(Wrapper pContainer, Level pLevel) {
        return input.test(pContainer.getInput()) && FluidStack.isSameFluidSameComponents(fluid, pContainer.getFluid()) && pContainer.getFluid().getAmount() >= fluid.getAmount();
    }

    @Override
    public ItemStack assemble(Wrapper input, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }


    public ItemStack getResultItem() {
        return output.copy();
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_EMPTYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get();
    }

    public static class Wrapper extends RecipeWrapper {

        private final FluidStack fluid;
        public Wrapper(ItemStack stack, FluidStack fluid) {
            super(new ItemStackHandler(1));
            this.fluid = fluid;
            this.inv.insertItem(0, stack, false);
//            setItem(0, stack);
        }

        public ItemStack getInput() {
            return this.getItem(0);
        }
        public FluidStack getFluid() {
            return fluid;
        }
    }




    public static class Serializer implements RecipeSerializer<CauldronEmptyingRecipe> {

        private static final MapCodec<CauldronEmptyingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output)
                        )
                        .apply(instance, CauldronEmptyingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronEmptyingRecipe> STREAM_CODEC = StreamCodec.of(
                CauldronEmptyingRecipe.Serializer::toNetwork, CauldronEmptyingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CauldronEmptyingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronEmptyingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CauldronEmptyingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.STREAM_CODEC.decode(buffer);
            return new CauldronEmptyingRecipe(input, fluid, output);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronEmptyingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
        }
    }
}
