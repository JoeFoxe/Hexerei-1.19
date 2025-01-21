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
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class WoodcutterRecipeCategory implements IRecipeCategory<WoodcutterRecipe> {
    public final static ResourceLocation UID = HexereiUtil.getResource("woodcutter");
    public final static ResourceLocation TEXTURE =
            HexereiUtil.getResource("textures/gui/drying_rack_jei.png");
    private final IDrawable background;
    private final IDrawable icon;


    public WoodcutterRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 100, 53);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.WILLOW_WOODCUTTER.get()));
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
    public RecipeType<WoodcutterRecipe> getRecipeType() {
        return new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("hexerei.container.woodcutter");
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
    public void setRecipe(IRecipeLayoutBuilder builder, WoodcutterRecipe recipe, IFocusGroup focuses) {

        int count = recipe.ingredientCount;
        Ingredient ingredient = recipe.getIngredients().get(0);
        ItemStack[] stacks = ingredient.getItems();
        for (ItemStack stack : stacks) {
            stack.setCount(count);
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 16).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 16).addItemStack(getResultItem(recipe));
    }

    @Override
    public void draw(WoodcutterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        background.draw(guiGraphics);
//        int dryingTime = recipe.getDryingTime();
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);

        String outputName = recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getHoverName().getString();
        minecraft.font.drawInBatch(outputName, 5*1.666f, 4*1.666f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

        guiGraphics.pose().popPose();
    }

    public static ItemStack getResultItem(Recipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.getResultItem(registryAccess);
    }
}