package net.joefoxe.hexerei.data.recipes;


import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

import java.util.function.Consumer;

public class HexereiRecipeProvider extends RecipeProvider {


    public HexereiRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        //this still doesnt work? - fix data gen
//        SpecialRecipeBuilder.special(CofferDyeingRecipe.SERIALIZER).save(consumer, "hexerei:coffer_dyeing");
        SpecialRecipeBuilder.special(KeychainRecipe.SERIALIZER).save(consumer, "hexerei:keychain_apply");

//        ShapedRecipeBuilder.shaped(ModItems.CROW_FLUTE.get())
//         .define('#', ModBlocks.MAHOGANY_PLANKS.get().asItem())
//         .pattern("# #")
//         .pattern("###")
//         .group("boat")
//         .unlockedBy("in_water", insideOf(Blocks.WATER))
//         .save(consumer, "hexerei:crow_flute_dye");

//        ShapedRecipeBuilder.shaped(ModItems.CROW_FLUTE.get())
//                .define('D', Tags.Items.DYES)
//                .define('P', ModBlocks.MAHOGANY_PLANKS.get().asItem())
//                .pattern("  P")
//                .pattern("DP ")
//                .pattern("PD ")
//                .save(consumer, "hexerei:crow_flute_dye");
//                .unlockedBy("has_item", AccessorRecipeProvider.botania_condition(ItemPredicate.Builder.item().of(Tags.Items.DYES).build())
//                .save(WrapperResult.ofType(TwigWandRecipe.SERIALIZER, consumer));
    }
}
