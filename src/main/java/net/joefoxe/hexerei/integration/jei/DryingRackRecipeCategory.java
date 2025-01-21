package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.DryingRackRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DryingRackRecipeCategory implements IRecipeCategory<DryingRackRecipe> {
    public final static ResourceLocation UID = HexereiUtil.getResource("drying_rack");
    public final static ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/drying_rack_jei.png");
    private final IDrawable background;
    private final IDrawable icon;


    public DryingRackRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 100, 53);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.HERB_DRYING_RACK.get()));
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
    public RecipeType<DryingRackRecipe> getRecipeType() {
        return new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class);
    }

    @Override
    public Component getTitle() {
        return ModBlocks.HERB_DRYING_RACK.get().getName();
    }

//    @Override
//    public IDrawable getBackground() {
//        return this.background;
//    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingRackRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT,14, 16).addIngredients(recipe.getIngredients().getFirst());
        builder.addSlot(RecipeIngredientRole.OUTPUT,70, 16).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(DryingRackRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        background.draw(guiGraphics);

        int dryingTime = recipe.getDryingTime();
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
        String dryingTimeString = dryingTime < Integer.MAX_VALUE ? dryingTime / 20 + (dryingTime % 20 == 0 ? "" : ("." + dryingTime % 20)) : "?";
        if(dryingTimeString.charAt(dryingTimeString.length()-1) == '0' && dryingTime != 0 && dryingTime % 20 != 0)
            dryingTimeString = dryingTimeString.substring(0, dryingTimeString.length()-1);
        MutableComponent dip_time_1 = Component.translatable("gui.jei.category.dipper.dry_time_1");
        MutableComponent dip_time_3 = Component.translatable("gui.jei.category.dipper.resultSeconds", dryingTimeString);
        minecraft.font.drawInBatch(dip_time_1, 6*1.666f, 41*1.666f, 0xFF808080, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        minecraft.font.drawInBatch(dip_time_3, 55*1.666f, 41*1.666f, 0xFF808080, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

        String outputName = recipe.getOutput().getHoverName().getString();
        minecraft.font.drawInBatch(outputName, 5*1.666f, 4*1.666f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        guiGraphics.pose().popPose();

    }
}