package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.PickableDoubleFlower;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class CrowWhitelistRenderer implements IGuiOverlay {
    private static final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/crow_gui.png");
    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if(CrowWhitelistEvent.whiteListingCrow != null) {
            gui.setupOverlayRenderState(true, false);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI);
            GuiComponent.blit(poseStack, screenWidth / 2 - 9, screenHeight - 42, 238, 178, 18, 18, 256, 256);

            InventoryScreen.renderEntityInInventory(screenWidth / 2, screenHeight - 78, 40, 35, -15, CrowWhitelistEvent.whiteListingCrow);

            if(!CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.isEmpty()){
                ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pushPose();
                poseStack.translate(screenWidth / 2f - 14 - ((CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size() - 1) / 2f * 21), screenHeight - 40, 100.0F + renderer.blitOffset);
                poseStack.translate(8.0F, -8.0F, 0.0F);
                poseStack.scale(12.0F, 12.0F, 12.0F);
                poseStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
                Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);

                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

                Lighting.setupFor3DItems();
                poseStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -90));
                for(int itor = 0; itor < CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size(); itor++){
                    poseStack.pushPose();
                    poseStack.translate(itor * 1.7f, Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) / 4, 0.0F);

                    float zRot = 0;
                    float xRot = 20;
                    float yRot = 30 + (Hexerei.getClientTicks()) + itor * 30;

                    poseStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees((float) xRot));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
                    poseStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

                    BlockState state = CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.get(itor).defaultBlockState();
                    if (state.hasProperty(BlockStateProperties.AGE_1))
                        state = state.setValue(BlockStateProperties.AGE_1, Mth.clamp((int) (((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 2), 0, 1));
                    else if (state.hasProperty(BlockStateProperties.AGE_2))
                        state = state.setValue(BlockStateProperties.AGE_2, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 3), 0, 2));
                    else if (state.hasProperty(BlockStateProperties.AGE_3))
                        state = state.setValue(BlockStateProperties.AGE_3, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 4), 0, 3));
                    else if (state.hasProperty(BlockStateProperties.AGE_4))
                        state = state.setValue(BlockStateProperties.AGE_4, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 5), 0, 4));
                    else if (state.hasProperty(BlockStateProperties.AGE_5))
                        state = state.setValue(BlockStateProperties.AGE_5, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 6), 0, 5));
                    else if (state.hasProperty(BlockStateProperties.AGE_7))
                        state = state.setValue(BlockStateProperties.AGE_7, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 8), 0, 7));
                    if(state.hasProperty(BlockStateProperties.BERRIES))
                        state = state.setValue(BlockStateProperties.BERRIES, true);
                    renderBlock(poseStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                    if(state.hasProperty(PickableDoubleFlower.HALF)){
                        poseStack.pushPose();
                        poseStack.translate(0F, 1, 0.0F);
                        state = state.setValue(PickableDoubleFlower.HALF, DoubleBlockHalf.UPPER);
                        renderBlock(poseStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
                buffer.endBatch();
                poseStack.popPose();
            }
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