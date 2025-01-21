package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PestleAndMortarRecipeCategory implements IRecipeCategory<PestleAndMortarRecipe> {
    public final static ResourceLocation UID = HexereiUtil.getResource("pestle_and_mortar");
    public final static ResourceLocation TEXTURE =
            HexereiUtil.getResource("textures/gui/pestle_and_mortar_jei.png");
    private final IDrawable background;
    private final IDrawable icon;


    public PestleAndMortarRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 143, 80);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get()));
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public RecipeType<PestleAndMortarRecipe> getRecipeType() {
        return new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class);
    }

    @Override
    public Component getTitle() {
        return ModBlocks.PESTLE_AND_MORTAR.get().getName();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PestleAndMortarRecipe recipe, IFocusGroup focuses) {
        builder.setShapeless();
        if(recipe.getIngredients().size() > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 11, 14).addIngredients(recipe.getIngredients().get(0));//.getItemStacks().init(0, true, 10, 13);
        if(recipe.getIngredients().size() > 1)
            builder.addSlot(RecipeIngredientRole.INPUT, 20, 36).addIngredients(recipe.getIngredients().get(1));
        if(recipe.getIngredients().size() > 2)
            builder.addSlot(RecipeIngredientRole.INPUT, 42, 45).addIngredients(recipe.getIngredients().get(2));
        if(recipe.getIngredients().size() > 3)
            builder.addSlot(RecipeIngredientRole.INPUT, 64, 36).addIngredients(recipe.getIngredients().get(3));
        if(recipe.getIngredients().size() > 4)
            builder.addSlot(RecipeIngredientRole.INPUT, 73, 14).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 31).addItemStack(recipe.getOutput());

//        builder.getItemStacks().set(ingredients);
    }
//
//    @Override
//    public void setIngredients(PestleAndMortarRecipe recipe, IIngredients ingredients) {
//        ingredients.setInputIngredients(recipe.getIngredients());
//        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, PestleAndMortarRecipe recipe, IIngredients ingredients) {
//
//        recipeLayout.setShapeless();
//        recipeLayout.getItemStacks().init(0, true, 10, 13);
//        recipeLayout.getItemStacks().init(1, true, 19, 35);
//        recipeLayout.getItemStacks().init(2, true, 41, 44);
//        recipeLayout.getItemStacks().init(3, true, 63, 35);
//        recipeLayout.getItemStacks().init(4, true, 72, 13);
//        recipeLayout.getItemStacks().init(5, false, 116, 30);
//
//        recipeLayout.getItemStacks().set(ingredients);
//
//    }

    @Override
    public void draw(PestleAndMortarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        background.draw(guiGraphics);

        int grindingTime = recipe.getGrindingTime();
        Minecraft minecraft = Minecraft.getInstance();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
        String grindingTimeString = grindingTime < Integer.MAX_VALUE ? grindingTime / 20 + (grindingTime % 20 == 0 ? "" : ("." + grindingTime % 20)) : "?";
        if(grindingTimeString.charAt(grindingTimeString.length()-1) == '0' && grindingTime != 0 && grindingTime % 20 != 0)
            grindingTimeString = grindingTimeString.substring(0, grindingTimeString.length()-1);
        MutableComponent dip_time_1 = Component.translatable("gui.jei.category.pestle_and_mortar.grind_time_1");
        MutableComponent dip_time_3 = Component.translatable("gui.jei.category.dipper.resultSeconds", grindingTimeString);

        minecraft.font.drawInBatch(dip_time_1, 6*1.666f, 68.5f*1.666f, 0xFF808080, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        minecraft.font.drawInBatch(dip_time_3, (58*1.666f), 68.5f*1.666f, 0xFF808080, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

        String outputName = recipe.getOutput().getHoverName().getString();
        minecraft.font.drawInBatch(outputName, 5*1.666f, 4*1.666f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

        guiGraphics.pose().popPose();
    }
}