package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class MixingCauldronRecipeCategory implements IRecipeCategory<MixingCauldronRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "mixingcauldron");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/mixing_cauldron_gui_jei.png");
    public final static ResourceLocation TEXTURE_BLANK =
            new ResourceLocation(Hexerei.MOD_ID, "textures/block/blank.png");
    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawable liquid;


    public MixingCauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 105);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MIXING_CAULDRON.get()));
        this.liquid = helper.createDrawable(TEXTURE, 182, 2, 12, 10);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }


    @Override
    public Class<? extends MixingCauldronRecipe> getRecipeClass() {
        return MixingCauldronRecipe.class;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.MIXING_CAULDRON.get().getName();
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
    public void setIngredients(MixingCauldronRecipe recipe, IIngredients ingredients) {
        ingredients.setInput(ForgeTypes.FLUID_STACK, recipe.getLiquid());
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
        ingredients.setOutput(ForgeTypes.FLUID_STACK, recipe.getLiquidOutput());
    }



    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MixingCauldronRecipe recipe, IIngredients ingredients) {
        recipeLayout.moveRecipeTransferButton(160, 90);
        recipeLayout.setShapeless();

        recipeLayout.getItemStacks().init(0, true, 79, 13);
        recipeLayout.getItemStacks().init(1, true, 101, 22);
        recipeLayout.getItemStacks().init(2, true, 110, 44);
        recipeLayout.getItemStacks().init(3, true, 101, 66);
        recipeLayout.getItemStacks().init(4, true, 79, 75);
        recipeLayout.getItemStacks().init(5, true, 57, 66);
        recipeLayout.getItemStacks().init(6, true, 48, 44);
        recipeLayout.getItemStacks().init(7, true, 57, 22);
        recipeLayout.getItemStacks().init(8, false, 141, 36);

        recipeLayout.getItemStacks().set(ingredients);
        FluidStack input = recipe.getLiquid();
        FluidStack output = recipe.getLiquidOutput();
        if(recipe.getFluidLevelsConsumed() != 0) {
            if(!input.isEmpty())
                input.setAmount(Mth.clamp(recipe.getFluidLevelsConsumed(), 0, 2000));

        }
        else {
            if(!output.isEmpty())
                output.setAmount(2000);

        }

        if(!output.isEmpty())
            output.setAmount(2000-recipe.getFluidLevelsConsumed());

        if(!input.isEmpty()) {
            recipeLayout.getFluidStacks().init(9, true, 20, 48, 12, 10, 2000, false, this.liquid);
            recipeLayout.getFluidStacks().set(9, input);
        }
        if(!output.isEmpty()) {
            recipeLayout.getFluidStacks().init(10, false, 144, 61, 12, 10, 2000, true, this.liquid);
            recipeLayout.getFluidStacks().set(10, output);
        }
    }

    @Override
    public void draw(MixingCauldronRecipe recipe, IRecipeSlotsView view, PoseStack matrixStack, double mouseX, double mouseY) {


        Minecraft minecraft = Minecraft.getInstance();

        String outputName = recipe.getResultItem().getHoverName().getString();
        matrixStack.scale(0.6f, 0.6f, 0.6f);
        minecraft.font.draw(matrixStack, outputName, 43*1.666f, 4*1.666f, 0xFF404040);
        minecraft.font.draw(matrixStack, Component.translatable("gui.jei.category.mixing_cauldron.fluid"), 19*1.666f, 36*1.666f, 0xFF404040);
        minecraft.font.draw(matrixStack, Component.translatable("gui.jei.category.mixing_cauldron.output"), 140*1.666f, 24*1.666f, 0xFF404040);

    }
}