package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
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
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BloodSigilRecipeCategory implements IRecipeCategory<BloodSigilRecipeJEI> {
    public final static ResourceLocation UID = HexereiUtil.getResource("blood_sigil");
    public final static ResourceLocation TEXTURE =
            HexereiUtil.getResource("textures/gui/blood_sigil_gui_jei.png");
    private IDrawable background;
    private final IDrawable icon;
    private final IDrawable cauldronFG;
    public BloodSigilRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 126, 59);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.BLOOD_SIGIL.get()));
        this.cauldronFG = helper.createDrawable(TEXTURE, 232, 48, 24, 16);
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
    public void getTooltip(ITooltipBuilder tooltip, BloodSigilRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {


        if(isHovering(mouseX, mouseY, 33, 25, 24, 15)){
            tooltip.add(Component.translatable("gui.jei.category.blood_sigil_tooltip1"));
        }
        else if(isHovering(mouseX, mouseY, 58, 18, 24, 21)){
            tooltip.add(Component.translatable("gui.jei.category.blood_sigil_tooltip2"));
        }
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public RecipeType<BloodSigilRecipeJEI> getRecipeType() {
        return new RecipeType<>(UID, BloodSigilRecipeJEI.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.blood_sigil");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BloodSigilRecipeJEI recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);

        builder.addSlot(RecipeIngredientRole.INPUT,14, 24).addItemStack(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT,96, 24)
                .setFluidRenderer(500, false, 16, 16)
                .addFluidStack(recipe.getOutputFluid().getFluid(), 250, recipe.getOutputFluid().getComponentsPatch()).setOverlay(new IDrawable() {
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

                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(xOffset, yOffset, 0);
                double val = Math.sin((ClientEvents.getClientTicks()) / 5f);
                if (Minecraft.getInstance().player != null)
                    renderEntityInInventoryFollowsAngle(guiGraphics, 9,  (Math.min(val, 0.25)) * 10 + 10, 9, 16, (float)Math.toRadians(-20), (float)Math.toRadians(-30), Minecraft.getInstance().player);
                guiGraphics.pose().popPose();


                Lighting.setupFor3DItems();
                RenderSystem.enableDepthTest();
                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();


                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(xOffset, yOffset, 0);
                guiGraphics.pose().mulPose(new Matrix4f().scale(1, -1, 1));
                guiGraphics.pose().translate(-3, -15, 0);
                guiGraphics.pose().scale(17, 17, 17);
                guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(0));
                guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(20));
                guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(30));
                BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);
                MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipe.getOutputFluid(), 1, 1, OverlayTexture.NO_OVERLAY);
                guiGraphics.pose().popPose();

                BloodSigilRecipeCategory.this.cauldronFG.draw(guiGraphics, 92, 28);

            }
        }, -34, 0);

    }

    @Override
    public void draw(BloodSigilRecipeJEI recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        Minecraft minecraft = Minecraft.getInstance();
        Component outputName = recipe.getOutputFluid().getHoverName();

        background.draw(guiGraphics);

        int width = minecraft.font.width(outputName);
        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 80){
            float percent = width/80f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1/percent, 1/percent, 1/percent);
            minecraft.font.drawInBatch(outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            guiGraphics.pose().popPose();

        }else {
            minecraft.font.drawInBatch(outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
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


    public static void renderEntityInInventoryFollowsAngle(GuiGraphics pGuiGraphics, double pX, double pY, double pZ, int pScale, float angleXComponent, float angleYComponent, LivingEntity pEntity) {
        float f = angleXComponent;
        float f1 = angleYComponent;
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;
        pEntity.yBodyRot = 180.0F + f * 20.0F;
        pEntity.setYRot(180.0F + f * 40.0F);
        pEntity.setXRot(-f1 * 20.0F);
        pEntity.yHeadRot = pEntity.getYRot();
        pEntity.yHeadRotO = pEntity.getYRot();
        renderEntityInInventory(pGuiGraphics, pX, pY, pZ, pScale, quaternionf, quaternionf1, pEntity);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
    }

    public static void renderEntityInInventory(GuiGraphics pGuiGraphics, double pX, double pY, double pZ, int pScale, Quaternionf pPose, @Nullable Quaternionf pCameraOrientation, LivingEntity pEntity) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((double)pX, (double)pY, (double)pZ);
        pGuiGraphics.pose().mulPose((new Matrix4f()).scaling((float)pScale, (float)pScale, (float)(-pScale)));
        pGuiGraphics.pose().mulPose(pPose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (pCameraOrientation != null) {
            pCameraOrientation.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(pCameraOrientation);
        }

        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), 15728880);
        });
        pGuiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

}