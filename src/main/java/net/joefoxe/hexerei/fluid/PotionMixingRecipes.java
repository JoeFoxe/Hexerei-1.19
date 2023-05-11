package net.joefoxe.hexerei.fluid;


import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class PotionMixingRecipes {

    public static final List<Item> SUPPORTED_CONTAINERS = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

    public static List<FluidMixingRecipe> ALL;
    public static Map<Item, List<FluidMixingRecipe>> BY_ITEM;// = sortRecipesByItem(ALL);

    public static List<FluidMixingRecipe> createRecipes() {
        List<FluidMixingRecipe> mixingRecipes = new ArrayList<>();

        int recipeIndex = 0;

        List<Item> allowedSupportedContainers = new ArrayList<>();
        List<ItemStack> supportedContainerStacks = new ArrayList<>();
        for (Item container : SUPPORTED_CONTAINERS) {
            ItemStack stack = new ItemStack(container);
            supportedContainerStacks.add(stack);
            if (PotionBrewing.ALLOWED_CONTAINER.test(stack)) {
                allowedSupportedContainers.add(container);
            }
        }

        for (Item container : allowedSupportedContainers) {
            PotionFluid.BottleType bottleType = PotionFluidHandler.bottleTypeFromItem(container);
            for (PotionBrewing.Mix<Potion> mix : PotionBrewing.POTION_MIXES) {
                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(mix.from.get(), bottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(mix.to.get(), bottleType, 1000);
                if(mix.ingredient.getItems().length == 0 || mix.ingredient.getItems()[0] == null || mix.ingredient.getItems()[0].isEmpty())
                    return null;

                mixingRecipes.add(createRecipe("potion_mixing_vanilla_" + recipeIndex++, mix.ingredient, fromFluid, toFluid));
            }
        }

        for (PotionBrewing.Mix<Item> mix : PotionBrewing.CONTAINER_MIXES) {
            Item from = mix.from.get();
            if (!allowedSupportedContainers.contains(from)) {
                continue;
            }
            Item to = mix.to.get();
            if (!allowedSupportedContainers.contains(to)) {
                continue;
            }
            PotionFluid.BottleType fromBottleType = PotionFluidHandler.bottleTypeFromItem(from);
            PotionFluid.BottleType toBottleType = PotionFluidHandler.bottleTypeFromItem(to);
            Ingredient ingredient = mix.ingredient;
            if(mix.ingredient.getItems()[0] == null)
                return null;

            for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
                if (potion == Potions.EMPTY) {
                    continue;
                }

                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(potion, fromBottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(potion, toBottleType, 1000);

                mixingRecipes.add(createRecipe("potion_mixing_vanilla_" + recipeIndex++, ingredient, fromFluid, toFluid));
            }
        }

        recipeIndex = 0;
        for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
            if (recipe instanceof BrewingRecipe recipeImpl) {
                ItemStack output = recipeImpl.getOutput();
                if (!SUPPORTED_CONTAINERS.contains(output.getItem())) {
                    continue;
                }

                Ingredient input = recipeImpl.getInput();
                Ingredient ingredient = recipeImpl.getIngredient();
                FluidStack outputFluid = null;
                for (ItemStack stack : supportedContainerStacks) {
                    if (input.test(stack)) {
                        FluidStack inputFluid = PotionFluidHandler.getFluidFromPotionItem(stack);
                        if (outputFluid == null) {
                            outputFluid = PotionFluidHandler.getFluidFromPotionItem(output);
                        }
                        mixingRecipes.add(createRecipe("potion_mixing_modded_" + recipeIndex++, ingredient, inputFluid, outputFluid));
                    }
                }
            }
        }

        return mixingRecipes;
    }

    private static FluidMixingRecipe createRecipe(String id, Ingredient ingredient, FluidStack fromFluid, FluidStack toFluid) {
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(8, Ingredient.EMPTY);
        nonNullList.set(0, ingredient);
        nonNullList.set(4, ingredient);
        return new FluidMixingRecipe(new ResourceLocation(id), nonNullList, fromFluid, toFluid, FluidMixingRecipe.HeatCondition.HEATED);
    }

    public static Map<Item, List<FluidMixingRecipe>> sortRecipesByItem(List<FluidMixingRecipe> all) {
        Map<Item, List<FluidMixingRecipe>> byItem = new HashMap<>();
        Set<Item> processedItems = new HashSet<>();
        for (FluidMixingRecipe recipe : all) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack itemStack : ingredient.getItems()) {
                    Item item = itemStack.getItem();
                    if (processedItems.add(item)) {
                        byItem.computeIfAbsent(item, i -> new ArrayList<>())
                                .add(recipe);
                    }
                }
            }
            processedItems.clear();
        }
        return byItem;
    }

}