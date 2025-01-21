package net.joefoxe.hexerei.integration.jei;

import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BottlingRecipeJEI {

    public static List<CauldronEmptyingRecipe> getRecipeList(RecipeManager rm) {


        List<CauldronEmptyingRecipe> recipeList = new ArrayList<>(new ArrayList<>(rm.getAllRecipesFor(ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get())).stream().map(RecipeHolder::value).toList());
//        recipeList.add(new CauldronEmptyingRecipe(HexereiUtil.getResource("cauldron_emptying/" + val + "_to_bottle"), Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.WATER, 250), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.LAVA, 250), ModItems.LAVA_BOTTLE.get().getDefaultInstance()));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.TALLOW_FLUID.get(), 250), ModItems.TALLOW_BOTTLE.get().getDefaultInstance()));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.BLOOD_FLUID.get(), 250), ModItems.BLOOD_BOTTLE.get().getDefaultInstance()));

        for (FluidMixingRecipe recipe : PotionMixingRecipes.ALL){

            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            recipeList.forEach((rec) -> {
                if (FluidStack.isSameFluidSameComponents(rec.getFluid(), recipe.getLiquidOutput())){
                    atomicBoolean.set(true);
                }
            });
            if (!atomicBoolean.get()){
                ItemStack potionItem = PotionFluidHandler.fillBottle(recipe.getLiquidOutput());

//                PotionContents potion = recipe.getLiquidOutput().get(DataComponents.POTION_CONTENTS);
//                String val = potion != null ? Potion.getName(potion.potion(), "") : "missing";
//                HexereiUtil.getResource("cauldron_emptying/" + val + "_to_bottle"),
                recipeList.add(new CauldronEmptyingRecipe(Ingredient.of(Items.GLASS_BOTTLE.getDefaultInstance()), recipe.getLiquidOutput().copyWithAmount(250), potionItem));
            }
        }

        return recipeList;
    }
}
