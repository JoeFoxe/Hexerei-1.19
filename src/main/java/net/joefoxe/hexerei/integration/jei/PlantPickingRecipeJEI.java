package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlantPickingRecipeJEI {

    private ItemStack INPUT;
    private final ItemStack OUTPUT_ITEM;
    private final ItemStack OUTPUT_ITEM2;

    public PlantPickingRecipeJEI(PickableDoublePlant plantBlock){
        this.INPUT = plantBlock.asItem().getDefaultInstance();
        this.OUTPUT_ITEM = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.firstOutputKey).get().getDefaultInstance();
        this.OUTPUT_ITEM2 = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.secondOutputKey).get().getDefaultInstance();
    }
    public PlantPickingRecipeJEI(PickablePlant plantBlock){
        this.INPUT = plantBlock.asItem().getDefaultInstance();
        this.OUTPUT_ITEM = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.firstOutputKey).get().getDefaultInstance();
        this.OUTPUT_ITEM2 = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.secondOutputKey).get().getDefaultInstance();
    }

    public ItemStack getInput() {
        return INPUT;
    }

    public ItemStack getOutputItem() {
        return OUTPUT_ITEM;
    }

    public ItemStack getOutputItem2() {
        return OUTPUT_ITEM2;
    }

    public static List<PlantPickingRecipeJEI> getRecipeList() {

        List<PlantPickingRecipeJEI> recipeList = Lists.newArrayList();

        recipeList.add(new PlantPickingRecipeJEI(ModBlocks.BELLADONNA_PLANT.get()));
        recipeList.add(new PlantPickingRecipeJEI(ModBlocks.MANDRAKE_PLANT.get()));
        recipeList.add(new PlantPickingRecipeJEI(ModBlocks.MUGWORT_BUSH.get()));
        recipeList.add(new PlantPickingRecipeJEI(ModBlocks.YELLOW_DOCK_BUSH.get()));

        return recipeList;
    }
}
