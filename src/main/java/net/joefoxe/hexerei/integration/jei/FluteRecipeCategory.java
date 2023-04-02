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
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import java.util.Optional;

public class FluteRecipeCategory implements IRecipeCategory<CrowFluteRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "crow_flute_dye");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/crow_flute_dye_gui_jei.png");
    private IDrawable background;
    private final IDrawable icon;

    @Override
    public List<Component> getTooltipStrings(CrowFluteRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        //79, 59       24 x 18

//        if(recipe.getHeatCondition() != CrowFluteRecipe.HeatCondition.NONE && isHovering(mouseX, mouseY, 79, 59, 24, 18)){
//            List<Component> tooltip = new ArrayList<>();
//            tooltip.add(Component.translatable("tooltip.hexerei.heat_source"));
//
//            if(Screen.hasShiftDown()) {
//                tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("Heat source shown: - %s", Component.translatable(heatSource.getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCC5522)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            } else {
//                tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            }
//
//
//
//            return tooltip;
//        }


        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }
    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public FluteRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 144, 86);
        this.icon = new ExtraFluteIcon(() -> new ItemStack(ModItems.CROW_FLUTE.get()));
    }

    public static Block getTagStack(TagKey<Block> key){
        Optional<Block> optional = Registry.BLOCK.getTag(key).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value);

        return optional.orElse(Blocks.AIR);
        //        return Registry.ITEM.getTag(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(loc))).flatMap(tag -> tag.getRandomElement(new Random())).map(Holder::value);
    }

    @Override
    public RecipeType<CrowFluteRecipe> getRecipeType() {
        return new RecipeType<>(new ResourceLocation(Hexerei.MOD_ID, "crow_flute_dye"), CrowFluteRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("Crow Flute Crafting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CrowFluteRecipe recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);

        int size = recipe.getInputs().size();

        if(size > 0)
            builder.addSlot(RecipeIngredientRole.INPUT,15, 19).addIngredients(recipe.getInputs().get(0));
        if(size > 1)
            builder.addSlot(RecipeIngredientRole.INPUT,33, 19).addIngredients(recipe.getInputs().get(1));
        if(size > 2)
            builder.addSlot(RecipeIngredientRole.INPUT,51, 19).addIngredients(recipe.getInputs().get(2));
        if(size > 3)
            builder.addSlot(RecipeIngredientRole.INPUT,15, 37).addIngredients(recipe.getInputs().get(3));
        if(size > 4)
            builder.addSlot(RecipeIngredientRole.INPUT,33, 37).addIngredients(recipe.getInputs().get(4));
        if(size > 5)
            builder.addSlot(RecipeIngredientRole.INPUT,51, 37).addIngredients(recipe.getInputs().get(5));
        if(size > 6)
            builder.addSlot(RecipeIngredientRole.INPUT,15, 55).addIngredients(recipe.getInputs().get(6));
        if(size > 7)
            builder.addSlot(RecipeIngredientRole.INPUT,33, 55).addIngredients(recipe.getInputs().get(7));
        if(size > 8)
            builder.addSlot(RecipeIngredientRole.INPUT,51, 55).addIngredients(recipe.getInputs().get(8));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 37).addItemStack(recipe.getResultItem());

    }

    @Override
    public void draw(CrowFluteRecipe recipe, IRecipeSlotsView view, PoseStack matrixStack, double mouseX, double mouseY) {

        float newHeatSource = (Hexerei.getClientTicks()) % 200 / 200f;
        float craftPercent = (Hexerei.getClientTicks()) % 100 / 100f;
        boolean showOutput = (Hexerei.getClientTicks()) % 200 > 100;

        Minecraft minecraft = Minecraft.getInstance();

        Component outputName = recipe.getResultItem().getHoverName();// Component.translatable("Crow Flute");

        int width = minecraft.font.width(outputName);
        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 131){
            float percent = width/131f;
            matrixStack.pushPose();
            matrixStack.scale(1/percent, 1/percent, 1/percent);
            minecraft.font.draw(matrixStack, outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040);
            matrixStack.popPose();

        }else {
            minecraft.font.draw(matrixStack, outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040);
        }

//        BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
//
//        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
//        BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
//        rendererer.getBlockModel(blockState);
//        BakedModel bakedModel = rendererer.getBlockModel(blockState);//renderer.getModel(new ItemStack(ModBlocks.WILLOW_ALTAR.get()), null, null, 0);
//
//        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        matrixStack.pushPose();
//        matrixStack.translate(70f, 73, 100.0F + renderer.blitOffset);
//        matrixStack.translate(8.0F, -8.0F, 0.0F);
//        matrixStack.scale(20.0F, 20.0F, 20.0F);
//        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
//
//        Vec3 rotationOffset = new Vec3(0, 0, 0);
//        float zRot = 0;
//        float xRot = 20;
//        float yRot = 30;
//
//        matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
//        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
//        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) xRot));
//        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
//        matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
//
//        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        boolean flatLighting = !bakedModel.usesBlockLight();
//        if (flatLighting) {
//            Lighting.setupForFlatItems();
//        }
//        renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);
//
//
//        buffer.endBatch();
//        RenderSystem.enableDepthTest();
//        if (flatLighting) {
//            Lighting.setupFor3DItems();
//        }
//
//        matrixStack.popPose();

    }

    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch(rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());

                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
            }

        }
    }

}