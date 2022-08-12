package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
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
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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
    public RecipeType<MixingCauldronRecipe> getRecipeType() {
        return new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class);
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
    public void setRecipe(IRecipeLayoutBuilder builder, MixingCauldronRecipe recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);
        builder.setShapeless();

        builder.addSlot(RecipeIngredientRole.INPUT,80, 14).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT,102, 23).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT,111, 45).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT,102, 67).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.INPUT,80, 76).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.INPUT,58, 67).addIngredients(recipe.getIngredients().get(5));
        builder.addSlot(RecipeIngredientRole.INPUT,49, 45).addIngredients(recipe.getIngredients().get(6));
        builder.addSlot(RecipeIngredientRole.INPUT,58, 23).addIngredients(recipe.getIngredients().get(7));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 37).addItemStack(recipe.getResultItem());

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
            builder.addSlot(RecipeIngredientRole.INPUT, 20, 48).addFluidStack(recipe.getLiquid().getFluid(), recipe.getFluidLevelsConsumed())
                    .setFluidRenderer(2000, false, 12, 10).setBackground(this.liquid, 0, 0);
        }
        if(!output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 144, 61).addFluidStack(recipe.getLiquid().getFluid(), recipe.getFluidLevelsConsumed())
                    .setFluidRenderer(2000, true, 12, 10).setBackground(this.liquid, 0, 0);
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