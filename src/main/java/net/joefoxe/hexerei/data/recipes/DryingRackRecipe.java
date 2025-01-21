package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DryingRackRecipe implements Recipe<CraftingInput> {

    private final ItemStack output;
    private final Ingredient input;
    private final int dryingTime;

    @Override
    public boolean isSpecial() {
        return true;
    }

    public DryingRackRecipe(Ingredient input, ItemStack output, int dryingTime) {
        this.output = output;
        this.input = input;
        this.dryingTime = dryingTime;
    }


    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        return inv.items().stream().anyMatch((stack) -> input.test(stack));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, input);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return getOutput();
    }

    public ItemStack getOutput(){
        return output.copy();
    }
    public int getDryingTime() { return this.dryingTime; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.HERB_DRYING_RACK.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.DRYING_RACK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }


    public static class Type implements RecipeType<DryingRackRecipe> {
        private Type() { }
        public static final DryingRackRecipe.Type INSTANCE = new DryingRackRecipe.Type();
        public static final String ID = "drying_rack";
    }


    public static class Serializer implements RecipeSerializer<DryingRackRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<DryingRackRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                Codec.INT.fieldOf("dryingTime").forGetter(recipe -> recipe.dryingTime)
                        )
                        .apply(instance, DryingRackRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> STREAM_CODEC = StreamCodec.of(
                DryingRackRecipe.Serializer::toNetwork, DryingRackRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<DryingRackRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> streamCodec() {
            return STREAM_CODEC;
        }


        static <B extends ByteBuf> StreamCodec.CodecOperation<B, Ingredient, NonNullList<Ingredient>> list() {
            return p_320272_ -> ByteBufCodecs.collection((s) -> NonNullList.withSize(s, Ingredient.EMPTY), p_320272_);
        }

        private static DryingRackRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            int dryingTime = ByteBufCodecs.INT.decode(buffer);
            return new DryingRackRecipe(input, output, dryingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, DryingRackRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            ByteBufCodecs.INT.encode(buffer, recipe.dryingTime);
        }
    }
}
