package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.RecipeToServer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MixingCauldronTransferInfo implements IRecipeTransferHandler<MixingCauldronContainer, MixingCauldronRecipe>{

    @Override
    public Class<MixingCauldronContainer> getContainerClass() {
        return MixingCauldronContainer.class;
    }

    @Override
    public Class<MixingCauldronRecipe> getRecipeClass() {
        return MixingCauldronRecipe.class;
    }

    @Override//MixingCauldronContainer, MixingCauldronRecipe
    public @org.jetbrains.annotations.Nullable IRecipeTransferError transferRecipe(MixingCauldronContainer container, MixingCauldronRecipe recipe, IRecipeLayout recipeLayout, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {

        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

        MixingCauldronTile inventory = (MixingCauldronTile)container.tileEntity;

        List<Boolean> itemMatchesSlot = new ArrayList<>();
        for(int i = 0; i < 8; i++)
            itemMatchesSlot.add(i, false);

        // the flag is to break out early in case nothing matches for that slot
        boolean flag = false;

        // cycle through each recipe slot
        for(int j = 0; j < 8; j++) {
            Ingredient ingredient = recipe.getIngredients().get(j);
            //cycle through each slot for each recipe slot
            for (int i = 0; i < 8; i++) {
                //if the recipe matches a slot
                if (ingredient.test(inventory.items.get(i))) {
                    // if the slot is not taken up
                    if (!itemMatchesSlot.get(j)) {
                        //mark the slot as taken up
                        itemMatchesSlot.set(j, true);
                    }
                }
            }
            for(int i = 0; i < 36; i++){
                if (ingredient.test(pPlayer.inventory.items.get(i))) {
                    // if the slot is not taken up
                    if (!itemMatchesSlot.get(j)) {
                        //mark the slot as taken up
                        itemMatchesSlot.set(j, true);
                    }
                }
            }
        }
        // checks if a slot is not taken up, if its not taken up then itll not craft
        boolean allItemsMissing = true;
        for(int i = 0; i < 8; i++) {
            if (itemMatchesSlot.get(i)) {
                allItemsMissing = false;
            }
        }
        //if it reaches here that means it has completed the shapeless craft and should craft it

        int check = 0;
        if(!allItemsMissing)
            check = checkRecipe(guiIngredients, inventory, pPlayer);

        if(!pDoTransfer && check == 0) {
            return new IRecipeTransferError() {
                @Override
                public Type getType() {
                    return Type.USER_FACING;
                }


                @Override
                public void showError(PoseStack poseStack, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY) {

                    for(int i = 0; i < itemMatchesSlot.size(); i++){

                        if(!itemMatchesSlot.get(i)){
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                            Slot slot = container.slots.get(i + 37);

                            poseStack.pushPose();

                            poseStack.translate(0,0,1000);
                            GuiComponent.fill(poseStack, recipeX + slot.x, recipeY + slot.y + 9, recipeX + slot.x + 16, recipeY + slot.y + 9 + 16, 0x66FF0000);

                            poseStack.popPose();
                        }
                    }

                    IRecipeTransferError.super.showError(poseStack, mouseX, mouseY, recipeSlotsView, recipeX, recipeY);
                }
            };
        }
        if(!pDoTransfer && check == 1) {
            return new IRecipeTransferError() {
                @Override
                public Type getType() {
                    return Type.COSMETIC;
                }

                @Override
                public void showError(PoseStack poseStack, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY) {

                    for(int i = 0; i < itemMatchesSlot.size(); i++){

                        boolean bool = itemMatchesSlot.get(i);
                        if(!bool){
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                            Slot slot = container.slots.get(i + 37);

                            poseStack.pushPose();

                            poseStack.translate(0,0,1000);
                            GuiComponent.fill(poseStack, recipeX + slot.x, recipeY + slot.y + 9, recipeX + slot.x + 16, recipeY + slot.y + 9 + 16, 0x66FF0000);

                            poseStack.popPose();
                        }
                    }

                    IRecipeTransferError.super.showError(poseStack, mouseX, mouseY, recipeSlotsView, recipeX, recipeY);
                }
            };
        }
        if(pDoTransfer) {

            if(!transferRecipe(guiIngredients, inventory, pPlayer)) {
                return new IRecipeTransferError() {
                    @Override
                    public Type getType() {
                        return Type.USER_FACING;
                    }
                };
            }
        }
        return IRecipeTransferHandler.super.transferRecipe(container, recipe, recipeLayout, pPlayer, pMaxTransfer, pDoTransfer);
    }

    public static int checkRecipe(Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients, MixingCauldronTile blockEntity, Player player) {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            items.add(ItemStack.EMPTY);
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
            int recipeSlot = entry.getKey();
            if(recipeSlot != 8){
                List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
                if (!allIngredients.isEmpty()) {
                    items.set(recipeSlot, allIngredients.get(0));
                }
            }
        }
        NonNullList<ItemStack> inv = blockEntity.items;

        List<Boolean> itemMatchesSlot = new ArrayList<>();
        for(int i = 0; i < 7; i++)
            itemMatchesSlot.add(i, false);

        boolean matchesAtleastOne = false;

        for(ItemStack stack : player.inventory.items){

            for(int i = 0; i < 7; i++){
                if(itemMatchesSlot.get(i))
                    continue;
                if(stack.sameItemStackIgnoreDurability(items.get(i))) {
                    itemMatchesSlot.set(i, true);
                    matchesAtleastOne = true;
                }
            }
        }
        boolean matchesItems = true;

        for(boolean bool : itemMatchesSlot){
            if (!bool) {
                matchesItems = false;
                break;
            }
        }

        return matchesItems ? 2 : matchesAtleastOne ? 1 : 0;
    }

    public static boolean transferRecipe(Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients, MixingCauldronTile blockEntity, Player player) {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            items.add(ItemStack.EMPTY);
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
            int recipeSlot = entry.getKey();
            if(recipeSlot != 8){
                List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
                if (!allIngredients.isEmpty()) {
                    items.set(recipeSlot, allIngredients.get(0));
                }
            }
        }
        boolean matchesItems = false;
        NonNullList<ItemStack> inv = blockEntity.items;
        for(ItemStack stack : player.inventory.items){

            if(matchesItems)
                break;
            for(ItemStack stack2 : items){
                if(stack.sameItemStackIgnoreDurability(stack2)) {
                    matchesItems = true;
                    break;
                }
            }

        }
        if(matchesItems)
            HexereiPacketHandler.sendToServer(new RecipeToServer(items, blockEntity.getBlockPos(), player.getUUID()));
        return matchesItems;
    }

}
