package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class WoodcutterRecipe extends SingleItemRecipe {

    public int ingredientCount;
    public WoodcutterRecipe(String pGroup, Ingredient pIngredient, String itemId, int itemCount, int ingredientCount) {
        super(ModRecipeTypes.WOODCUTTING_TYPE.get(), ModRecipeTypes.WOODCUTTING_SERIALIZER.get(), pGroup, pIngredient, new ItemStack(BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(itemId)).orElse(Items.AIR), itemCount));
        this.ingredientCount = ingredientCount;
    }
    public WoodcutterRecipe(String pGroup, Ingredient pIngredient, int ingredientCount, ItemStack output) {
        super(ModRecipeTypes.WOODCUTTING_TYPE.get(), ModRecipeTypes.WOODCUTTING_SERIALIZER.get(), pGroup, pIngredient, output);
        this.ingredientCount = ingredientCount;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(SingleRecipeInput pInv, Level pLevel) {
        return this.ingredient.test(pInv.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.WILLOW_WOODCUTTER.get());
    }


    @Override
    public RecipeType<?> getType() {
        return WoodcutterRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<WoodcutterRecipe> {
        private Type() { }
        public static final WoodcutterRecipe.Type INSTANCE = new WoodcutterRecipe.Type();
    }

    //{
    //  "type": "hexerei:woodcutting",
    //  "count": 3,
    //  "ingredient": {
    //    "item": "minecraft:oak_stairs"
    //  },
    //  "ingredient_count": 4,
    //  "result": "minecraft:oak_planks"
    //}


    public static class Serializer implements RecipeSerializer<WoodcutterRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<WoodcutterRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(SingleItemRecipe::getGroup),
                                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                                Codec.STRING.fieldOf("result").forGetter(recipe -> BuiltInRegistries.ITEM.getKey(recipe.result.getItem()).toString()),
                                Codec.INT.fieldOf("count").forGetter(recipe -> recipe.result.getCount()),
                                Codec.INT.fieldOf("ingredient_count").forGetter(recipe -> recipe.ingredientCount)
                        )
                        .apply(instance, WoodcutterRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, WoodcutterRecipe> STREAM_CODEC = StreamCodec.of(
                WoodcutterRecipe.Serializer::toNetwork, WoodcutterRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<WoodcutterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WoodcutterRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static WoodcutterRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = ByteBufCodecs.STRING_UTF8.decode(buffer);
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            String result = ByteBufCodecs.STRING_UTF8.decode(buffer);
            int count = ByteBufCodecs.INT.decode(buffer);
            int ingredient_count = ByteBufCodecs.INT.decode(buffer);

            return new WoodcutterRecipe(group, ingredient, result, count, ingredient_count);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, WoodcutterRecipe recipe) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            ByteBufCodecs.STRING_UTF8.encode(buffer, BuiltInRegistries.ITEM.getKey(recipe.result.getItem()).toString());
            ByteBufCodecs.INT.encode(buffer, recipe.result.getCount());
            ByteBufCodecs.INT.encode(buffer, recipe.ingredientCount);
        }
    }

}