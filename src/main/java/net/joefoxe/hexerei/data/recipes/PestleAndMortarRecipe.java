package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PestleAndMortarRecipe implements Recipe<CraftingInput> {

    private final ItemStack output;
    private final int grindingTime;
    private final NonNullList<Ingredient> input;


    @Override
    public boolean isSpecial() {
        return true;
    }
    public PestleAndMortarRecipe(ItemStack output, NonNullList<Ingredient> input, int grindingTime) {
        this.output = output;
        this.input = input;
        this.grindingTime = grindingTime;

    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        List<Boolean> itemMatchesSlot = Stream.generate(() -> false).limit(input.size()).collect(Collectors.toList());

        // the flag is to break out early in case nothing matches for that slot
        boolean flag = false;
//        int numberOfEmpty = 5 - this.input.size();
        if (input.size() != this.input.size())
            return false;
        // cycle through each recipe slot
        for (Ingredient recipeItem : this.input) {
            //cycle through each slot for each recipe slot
            for (int i = 0; i < input.size(); i++) {
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
//        // cycle through each recipe slot
//        for(int j = 0; j < numberOfEmpty; j++) {
//            //cycle through each slot for each recipe slot
//            for (int i = 0; i < input.size(); i++) {
//                //if the recipe matches a slot
//                if (input.getItem(i).isEmpty()) {
//                    // if the slot is not taken up
//                    if (!itemMatchesSlot.get(i)) {
//                        //mark the slot as taken up
//                        itemMatchesSlot.set(i, true);
//                        flag = true;
//                        break;
//                    }
//                }
//            }
//            //this is where it breaks out early to stop the craft
//            if(!flag)
//                break;
//            //reset the flag for the next iteration
//            flag = false;
//        }


        // checks if a slot is not taken up, if it's not taken up then itll not craft
        for(int i = 0; i < input.size(); i++) {
            if (!itemMatchesSlot.get(i)) {
                return false;
            }
        }
        //if it reaches here that means it has completed the shapeless craft and should craft it
        return true;


//        SHAPED CRAFTING - maybe bring this back as another config in the recipe to see if its shaped or shapeless
//        if(input.get(0).test(inv.getItem(0)) &&
//            input.get(1).test(inv.getItem(1)) &&
//            input.get(2).test(inv.getItem(2)) &&
//            input.get(3).test(inv.getItem(3)) &&
//            input.get(4).test(inv.getItem(4)) &&
//            input.get(5).test(inv.getItem(5)) &&
//            input.get(6).test(inv.getItem(6)) &&
//            input.get(7).test(inv.getItem(7)))
//        {
//            return true;
//        }
//        return false;

    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return input;
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

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get());
    }

    public int getGrindingTime() { return this.grindingTime; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.PESTLE_AND_MORTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }


    public static class Type implements RecipeType<PestleAndMortarRecipe> {
        private Type() { }
        public static final PestleAndMortarRecipe.Type INSTANCE = new PestleAndMortarRecipe.Type();
        public static final String ID = "pestle_and_mortar";
    }


    public static class Serializer implements RecipeSerializer<PestleAndMortarRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<PestleAndMortarRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(recipe -> recipe.input),
                                Codec.INT.fieldOf("grindingTime").forGetter(recipe -> recipe.grindingTime)
                        )
                        .apply(instance, PestleAndMortarRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, PestleAndMortarRecipe> STREAM_CODEC = StreamCodec.of(
                PestleAndMortarRecipe.Serializer::toNetwork, PestleAndMortarRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<PestleAndMortarRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PestleAndMortarRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PestleAndMortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            inputs.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            int grindingTime = ByteBufCodecs.INT.decode(buffer);

            return new PestleAndMortarRecipe(output, inputs, grindingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PestleAndMortarRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeInt(recipe.input.size());
            for (Ingredient ingredient : recipe.input)
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            ByteBufCodecs.INT.encode(buffer, recipe.grindingTime);
        }
    }
}
