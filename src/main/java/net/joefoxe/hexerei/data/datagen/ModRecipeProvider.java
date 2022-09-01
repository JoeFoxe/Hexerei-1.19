package net.joefoxe.hexerei.data.datagen;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }


    public static String getItemName(ItemLike pItemLike) {
        return Registry.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    public static String getAddCandleRecipeName(ItemLike pResult) {
        return getItemName(pResult) + "_add_to_candle";
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {

        ForgeRegistries.BLOCKS.forEach((block) -> {
            boolean bool = Registry.BLOCK.getKey(block).getPath().contains("_planks");
            //                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Registry.BLOCK.getKey(block));
            if(bool){
                //                ResourceLocation location = sprite.getName();
                ItemStack stack = new ItemStack(ModItems.CANDLE.get());
                CandleItem.setBaseLayerFromBlock(stack, Registry.BLOCK.getKey(block).toString());
                new AddToCandleRecipeBuilder(block.asItem(), stack.getItem(), 1, stack.getOrCreateTag())
                        .unlockedBy("has_candle", inventoryTrigger(ItemPredicate.Builder.item()
                                .of(ModItems.CANDLE.get()).build())).save(pFinishedRecipeConsumer, getAddCandleRecipeName(block));
            }
        });

//
//        List<Item> allowedSupportedContainers = new ArrayList<>();
//        List<ItemStack> supportedContainerStacks = new ArrayList<>();
//        for (Item container : FluidMixingRecipeBuilder.POTION_CONTAINERS) {
//            ItemStack stack = new ItemStack(container);
//            supportedContainerStacks.add(stack);
////            if (PotionBrewing.ALLOWED_CONTAINER.test(stack)) {
////                allowedSupportedContainers.add(container);
////            }
//        }
//
//
//        for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
//            if (recipe instanceof BrewingRecipe recipeImpl) {
//                ItemStack output = recipeImpl.getOutput();
//                if (!FluidMixingRecipeBuilder.POTION_CONTAINERS.contains(output.getItem())) {
//                    continue;
//                }
//
//                Ingredient input = recipeImpl.getInput();
//                Ingredient ingredient = recipeImpl.getIngredient();
//                FluidStack outputFluid = null;
//                for (ItemStack stack : supportedContainerStacks) {
//                    if (input.test(stack)) {
//                        FluidStack inputFluid = PotionFluidHandler.getFluidFromPotionItem(stack);
//                        if (outputFluid == null) {
//                            outputFluid = PotionFluidHandler.getFluidFromPotionItem(output);
//                        }
////                        mixingRecipes.add(createRecipe("potion_mixing_modded_" + recipeIndex++, ingredient, inputFluid, outputFluid));
//
//
//                        new FluidMixingRecipeBuilder(new ArrayList<>(List.of(ingredient)), inputFluid, outputFluid)
//                                .unlockedBy("has_blaze_powder", inventoryTrigger(ItemPredicate.Builder.item()
//                                        .of(Items.BLAZE_POWDER).build())).save(pFinishedRecipeConsumer, new ResourceLocation(Hexerei.MOD_ID,
//                                        Registry.POTION.getKey(PotionFluidHandler.getPotionFromFluidStack(outputFluid)).getPath() + "_from_fluid_mixing"));
//
//                    }
//                }
//            }
//        }

//        ForgeRegistries.POTIONS.forEach((potion) -> {
//
//            if(PotionBrewing.isBrewablePotion(potion)) {
//                //                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Registry.BLOCK.getKey(potion));
//
//                ArrayList<Ingredient> ingredients = new ArrayList<>();
//                for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
//                    if (recipe instanceof BrewingRecipe brewingRecipe) {
//                        for (ItemStack s : brewingRecipe.getIngredient().getItems()) {
//                            ingredients.add(Ingredient.of(s));
//                        }
//
//                    }
//                }
////
////            BrewingRecipeRegistry.getRecipes().forEach(iBrewingRecipe->{
////                iBrewingRecipe.
////            });
//                //                ResourceLocation location = sprite.getName();
//                ItemStack stack = new ItemStack(ModItems.CANDLE.get());
//                new FluidMixingRecipeBuilder(ingredients, PotionFluidHandler.getFluidFromPotion(potion, PotionFluid.BottleType.REGULAR, 2000), PotionFluidHandler.getFluidFromPotion(potion, PotionFluid.BottleType.REGULAR, 2000))
//                        .unlockedBy("has_blaze_powder", inventoryTrigger(ItemPredicate.Builder.item()
//                                .of(Items.BLAZE_POWDER).build())).save(pFinishedRecipeConsumer, new ResourceLocation(Hexerei.MOD_ID,
//                                Registry.POTION.getKey(potion).getPath() + "_from_fluid_mixing"));
//            }
//        });

// from kaupenjoes github
//        ShapedRecipeBuilder.shaped(ModBlocks.EBONY_DOOR.get())
//                .define('E', ModBlocks.EBONY_PLANKS.get())
//                .pattern("EE")
//                .pattern("EE")
//                .pattern("EE")
//                .unlockedBy("has_ebony_planks", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModBlocks.EBONY_PLANKS.get()).build()))
//                .save(pFinishedRecipeConsumer);
//
//        ShapelessRecipeBuilder.shapeless(ModItems.CITRINE.get())
//                .requires(ModBlocks.CITRINE_BLOCK.get())
//                .unlockedBy("has_citrine_block", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModBlocks.CITRINE_BLOCK.get()).build()))
//                .save(pFinishedRecipeConsumer);
//
//        ShapedRecipeBuilder.shaped(ModBlocks.CITRINE_BLOCK.get())
//                .define('C', ModItems.CITRINE.get())
//                .pattern("CCC")
//                .pattern("CCC")
//                .pattern("CCC")
//                .unlockedBy("has_citrine", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModItems.CITRINE.get()).build()))
//                .save(pFinishedRecipeConsumer);

    }
}
