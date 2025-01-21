package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class DipperRecipe implements Recipe<CraftingInput> {

    private final ItemStack input;
    private final ItemStack output;
    private final FluidStack fluid;
    private final int dippingTime;
    private final int dryingTime;
    private final int numberOfDips;
    private final boolean useInputItemAsOutput;


    @Override
    public boolean isSpecial() {
        return true;
    }
    public DipperRecipe(ItemStack input, ItemStack output, FluidStack fluid, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.dippingTime = dippingTime;
        this.dryingTime = dryingTime;
        this.numberOfDips = numberOfDips;
        this.useInputItemAsOutput = useInputItemAsOutput;

    }

    public FluidStack getFluid() {
        return this.fluid;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        return inv.items().stream().anyMatch((stack -> stack.is(input.getItem())));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, Ingredient.of(input));
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getOutput();
    }

    public ItemStack getOutput(){
        return output.copy();
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public FluidStack getLiquid() { return this.fluid; }

    public int getFluidLevelsConsumed() { return this.fluid.getAmount(); }

    public int getDippingTime() { return this.dippingTime; }

    public int getDryingTime() { return this.dryingTime; }

    public int getNumberOfDips() { return this.numberOfDips; }

    public boolean getUseInputItemAsOutput() { return this.useInputItemAsOutput; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.CANDLE_DIPPER.get());
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
                ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID,"dipper");

        private static final MapCodec<DipperRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
                                Codec.INT.fieldOf("dippingTime").forGetter(recipe -> recipe.dippingTime),
                                Codec.INT.fieldOf("dryingTime").forGetter(recipe -> recipe.dryingTime),
                                Codec.INT.fieldOf("numberOfDips").forGetter(recipe -> recipe.numberOfDips),
                                Codec.BOOL.optionalFieldOf("useInputItemAsOutput", false).forGetter(recipe -> recipe.useInputItemAsOutput)
                        )
                        .apply(instance, DipperRecipe::new)
        );
//        public DipperRecipe(NonNullList<Ingredient> inputs, ItemStack output, FluidStack fluid, int fluidLevelsConsumed, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        public static final StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> STREAM_CODEC = StreamCodec.of(
                DipperRecipe.Serializer::toNetwork, DipperRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<DipperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> streamCodec() {
            return STREAM_CODEC;
        }


        static <B extends ByteBuf> StreamCodec.CodecOperation<B, Ingredient, NonNullList<Ingredient>> list() {
            return p_320272_ -> ByteBufCodecs.collection((s) -> NonNullList.withSize(s, Ingredient.EMPTY), p_320272_);
        }

        private static DipperRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack input = ItemStack.STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.STREAM_CODEC.decode(buffer);
            int dippingTime = ByteBufCodecs.INT.decode(buffer);
            int dryingTime = ByteBufCodecs.INT.decode(buffer);
            int numberOfDips = ByteBufCodecs.INT.decode(buffer);
            boolean useInputItemAsOutput = ByteBufCodecs.BOOL.decode(buffer);
            return new DipperRecipe(input, output, fluid, dippingTime, dryingTime, numberOfDips, useInputItemAsOutput);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, DipperRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
            ByteBufCodecs.INT.encode(buffer, recipe.dippingTime);
            ByteBufCodecs.INT.encode(buffer, recipe.dryingTime);
            ByteBufCodecs.INT.encode(buffer, recipe.numberOfDips);
            ByteBufCodecs.BOOL.encode(buffer, recipe.useInputItemAsOutput);
        }
    }
}
