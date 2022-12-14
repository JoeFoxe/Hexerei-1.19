package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PestleAndMortarRecipeCategory implements IRecipeCategory<PestleAndMortarRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "pestle_and_mortar");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/pestle_and_mortar_jei.png");
    private final IDrawable background;
    private final IDrawable icon;


    public PestleAndMortarRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 143, 80);
        this.icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends PestleAndMortarRecipe> getRecipeClass() {
        return PestleAndMortarRecipe.class;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.PESTLE_AND_MORTAR.get().getName();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(PestleAndMortarRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PestleAndMortarRecipe recipe, IIngredients ingredients) {

        recipeLayout.setShapeless();
        recipeLayout.getItemStacks().init(0, true, 10, 13);
        recipeLayout.getItemStacks().init(1, true, 19, 35);
        recipeLayout.getItemStacks().init(2, true, 41, 44);
        recipeLayout.getItemStacks().init(3, true, 63, 35);
        recipeLayout.getItemStacks().init(4, true, 72, 13);
        recipeLayout.getItemStacks().init(5, false, 116, 30);

        recipeLayout.getItemStacks().set(ingredients);

    }

    @Override
    public void draw(PestleAndMortarRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {

        int grindingTime = recipe.getGrindingTime();
        Minecraft minecraft = Minecraft.getInstance();

        matrixStack.scale(0.6f, 0.6f, 0.6f);
        String grindingTimeString = grindingTime < Integer.MAX_VALUE ? grindingTime / 20 + (grindingTime % 20 == 0 ? "" : ("." + Integer.toString(grindingTime % 20))) : "?";
        if(grindingTimeString.charAt(grindingTimeString.length()-1) == '0' && grindingTime != 0 && grindingTime % 20 != 0)
            grindingTimeString = grindingTimeString.substring(0, grindingTimeString.length()-1);
        TranslatableComponent dip_time_1 = new TranslatableComponent("gui.jei.category.pestle_and_mortar.grind_time_1");
        TranslatableComponent dip_time_3 = new TranslatableComponent("gui.jei.category.dipper.resultSeconds", grindingTimeString);


        minecraft.font.draw(matrixStack, dip_time_1, 6*1.666f, 68.5f*1.666f, 0xFF808080);
        minecraft.font.draw(matrixStack, dip_time_3, (58*1.666f), 68.5f*1.666f, 0xFF808080);

        String outputName = recipe.getResultItem().getHoverName().getString();
        minecraft.font.draw(matrixStack, outputName, 5*1.666f, 4*1.666f, 0xFF404040);


    }
}