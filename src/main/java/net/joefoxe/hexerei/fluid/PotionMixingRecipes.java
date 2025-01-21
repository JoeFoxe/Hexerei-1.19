package net.joefoxe.hexerei.fluid;


import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
// additions by JoeFoxe
public class PotionMixingRecipes {

    public static final List<Item> SUPPORTED_CONTAINERS = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

    public static List<FluidMixingRecipe> ALL;
    public static Map<Item, List<FluidMixingRecipe>> BY_ITEM;// = sortRecipesByItem(ALL);

    public static List<BrewingRecipe> recipes = new ArrayList<>();

    public static List<BrewingRecipe> getAllBrewingRecipes(PotionBrewing potionBrewing) {
        if (recipes.isEmpty()) {
            potionBrewing.getRecipes().stream()
                    .filter(recipe -> recipe instanceof BrewingRecipe)
                    .map(recipe -> (BrewingRecipe) recipe).forEach(recipes::add);

            for(PotionBrewing.Mix<Potion> mix : potionBrewing.potionMixes){
                recipes.add(new BrewingRecipe(fromPotion(mix.from()), mix.ingredient(), fromPotion(mix.to()).getItems()[0]));
            }
        }
        return recipes;
    }

    private static boolean isContainer(PotionBrewing potionBrewing, ItemStack stack) {
        for(Ingredient ingredient : potionBrewing.containers) {
            if (ingredient.test(stack)) {
                return true;
            }
        }

        return false;
    }


    public static List<FluidMixingRecipe> createRecipes(PotionBrewing potionBrewing) {
        List<FluidMixingRecipe> mixingRecipes = new ArrayList<>();

        int recipeIndex = 0;


        List<Item> allowedSupportedContainers = new ArrayList<>();
        List<ItemStack> supportedContainerStacks = new ArrayList<>();
        for (Item container : SUPPORTED_CONTAINERS) {
            ItemStack stack = new ItemStack(container);
            supportedContainerStacks.add(stack);
            if (isContainer(potionBrewing, stack)) {
                allowedSupportedContainers.add(container);
            }
        }

        for (Item container : allowedSupportedContainers) {
            PotionFluid.BottleType bottleType = PotionFluidHandler.bottleTypeFromItem(container);
            for (PotionBrewing.Mix<Potion> mix : potionBrewing.potionMixes) {
                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(mix.from(), bottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(mix.to(), bottleType, 1000);
                if(mix.ingredient().getItems().length == 0 || mix.ingredient().getItems()[0] == null || mix.ingredient().getItems()[0].isEmpty())
                    continue;

                mixingRecipes.add(createRecipe("potion_mixing_vanilla_" + recipeIndex++, mix.ingredient(), fromFluid, toFluid));
            }
        }

        for (PotionBrewing.Mix<Item> mix : potionBrewing.containerMixes) {
            Item from = mix.from().value();
            if (!allowedSupportedContainers.contains(from)) {
                continue;
            }
            Item to = mix.to().value();
            if (!allowedSupportedContainers.contains(to)) {
                continue;
            }
            PotionFluid.BottleType fromBottleType = PotionFluidHandler.bottleTypeFromItem(from);
            PotionFluid.BottleType toBottleType = PotionFluidHandler.bottleTypeFromItem(to);
            Ingredient ingredient = mix.ingredient();
            if(mix.ingredient().getItems()[0] == null)
                continue;

            for (Holder.Reference<Potion> potion : BuiltInRegistries.POTION.holders().toList()) {
//                if (potion.value() == Potions.WATER.value()) {
//                    continue;
//                }

                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(potion, fromBottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(potion, toBottleType, 1000);

                mixingRecipes.add(createRecipe("potion_mixing_vanilla_" + recipeIndex++, ingredient, fromFluid, toFluid));
            }
        }

        recipeIndex = 0;

        for (BrewingRecipe recipe : getAllBrewingRecipes(potionBrewing)) {
            ItemStack output = recipe.getOutput();
            if (!SUPPORTED_CONTAINERS.contains(output.getItem())) {
                continue;
            }

            Ingredient input = recipe.getInput();
            Ingredient ingredient = recipe.getIngredient();
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

        if(mixingRecipes.isEmpty())
            mixingRecipes.add(createRecipe("potion_mixing_missing", Ingredient.EMPTY, new FluidStack(Fluids.WATER, 2000), new FluidStack(Fluids.WATER, 2000)));

        return mixingRecipes;
    }

    private static FluidMixingRecipe createRecipe(String id, Ingredient ingredient, FluidStack fromFluid, FluidStack toFluid) {
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(8, Ingredient.EMPTY);
        nonNullList.set(0, ingredient);
        nonNullList.set(4, ingredient);
        return new FluidMixingRecipe(nonNullList, fromFluid, toFluid, FluidMixingRecipe.HeatCondition.HEATED);
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

    public static Ingredient fromPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return getIngredient(stack);
    }

    public static Ingredient getIngredient(ItemStack input) {
        return DataComponentIngredient.of(false, input);
    }

}