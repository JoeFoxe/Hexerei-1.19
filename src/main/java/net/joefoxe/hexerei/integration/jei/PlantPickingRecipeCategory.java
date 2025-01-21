package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.joefoxe.hexerei.container.HerbJarContainer.OFFSET;

public class PlantPickingRecipeCategory implements IRecipeCategory<PlantPickingRecipeJEI> {
    public final static ResourceLocation UID = HexereiUtil.getResource("plant_picking");
    public final static ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/plant_picking_gui_jei.png");
    private final IDrawable background;
    private final IDrawable icon;
    public PlantPickingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 189, 59);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.MANDRAKE_PLANT.get()));
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
    public void getTooltip(ITooltipBuilder tooltip, PlantPickingRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(isHovering(mouseX, mouseY, 86, 23, 20, 20)){
            tooltip.add(Component.translatable("gui.jei.category.plant_picking_tooltip"));
        }
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public RecipeType<PlantPickingRecipeJEI> getRecipeType() {
        return new RecipeType<>(UID, PlantPickingRecipeJEI.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.plant_picking");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PlantPickingRecipeJEI recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);

        builder.addSlot(RecipeIngredientRole.INPUT,15, 24).addItemStack(recipe.getInput()).setOverlay(new IDrawable() {
            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {

//                background.draw(guiGraphics);

                if (recipe.getInput().getItem() instanceof BlockItem blockItem){


                    int ticks = (int)(ClientEvents.getClientTicks() / 30f);
                    int max_age = 0;

                    BlockState blockState = blockItem.getBlock().defaultBlockState();
                    if (blockState.hasProperty(BlockStateProperties.AGE_1)) {
                        max_age = 1;
                        blockState = blockState.setValue(BlockStateProperties.AGE_1, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }
                    else if (blockState.hasProperty(BlockStateProperties.AGE_2)) {
                        max_age = 2;
                        blockState = blockState.setValue(BlockStateProperties.AGE_2, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }
                    else if (blockState.hasProperty(BlockStateProperties.AGE_3)) {
                        max_age = 3;
                        blockState = blockState.setValue(BlockStateProperties.AGE_3, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }
                    else if (blockState.hasProperty(BlockStateProperties.AGE_4)) {
                        max_age = 4;
                        blockState = blockState.setValue(BlockStateProperties.AGE_4, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }
                    else if (blockState.hasProperty(BlockStateProperties.AGE_5)) {
                        max_age = 5;
                        blockState = blockState.setValue(BlockStateProperties.AGE_5, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }
                    else if (blockState.hasProperty(BlockStateProperties.AGE_7)) {
                        max_age = 7;
                        blockState = blockState.setValue(BlockStateProperties.AGE_7, Mth.clamp(ticks % (max_age + 1), 0, max_age));
                    }

                    if (Mth.clamp(ticks % (max_age + 1), 0, max_age) == max_age)
                        guiGraphics.blit(TEXTURE, 56, 23, 3, 59, 13, 16);

                    RenderSystem.enableDepthTest();
                    guiGraphics.pose().pushPose();

                    guiGraphics.pose().translate(xOffset, yOffset, 0);
                    guiGraphics.pose().mulPose(new Matrix4f().scale(1, -1, 1));
                    guiGraphics.pose().translate(-3, -15, 0);
                    guiGraphics.pose().scale(17, 17, 17);
                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    Vec3 rotationOffset = new Vec3(0, 0, 0);

                    float zRot = 0;
                    float xRot = 20;
                    float yRot = 30;

                    guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
                    guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
                    guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
                    guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
                    guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

                    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


                    renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFFFFFFFF);
                    if(blockState.hasProperty(PickableDoublePlant.HALF)){
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0F, 1, 0.0F);
                        blockState = blockState.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
                        renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFFFFFFFF);
                        guiGraphics.pose().popPose();
                    }

                    guiGraphics.pose().popPose();
                }
            }
        }, 74, 0);
        builder.addSlot(RecipeIngredientRole.OUTPUT,129, 24).addItemStack(recipe.getOutputItem());
        builder.addSlot(RecipeIngredientRole.OUTPUT,158, 24).addItemStack(recipe.getOutputItem2());

    }

    @Override
    public void draw(PlantPickingRecipeJEI recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        background.draw(guiGraphics);

        Minecraft minecraft = Minecraft.getInstance();
        Component outputName = recipe.getInput().getHoverName();

        int width = minecraft.font.width(outputName);
        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 80){
            float percent = width/80f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1/percent, 1/percent, 1/percent);
            minecraft.font.drawInBatch(outputName, 39 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            guiGraphics.pose().popPose();

        }else {
            minecraft.font.drawInBatch(outputName, 39, 5f + lineHeight - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}