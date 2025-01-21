package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class CrowWhitelistRenderer implements LayeredDraw.Layer {
    private static final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/crow_gui.png");

    @Override //(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight)
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        PoseStack poseStack = guiGraphics.pose();
        if(CrowWhitelistEvent.whiteListingCrow != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI);
            guiGraphics.blit(GUI, screenWidth / 2 - 9, screenHeight - 42, 238, 178, 18, 18, 256, 256);


//            public static void renderEntityInInventoryFollowsMouse(
//                    GuiGraphics guiGraphics,
//            int x1,
//            int y1,
//            int x2,
//            int y2,
//            int scale,
//            float yOffset,
//            float mouseX,
//            float mouseY,
//            LivingEntity entity
//    ) {
//                float f = (float)(x1 + x2) / 2.0F;
//                float f1 = (float)(y1 + y2) / 2.0F;
//                float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
//                float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));
//                // Forge: Allow passing in direct angle components instead of mouse position
//                renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3, entity);
//            }
//
//            public static void renderEntityInInventoryFollowsAngle(
//                    GuiGraphics p_282802_,
//            int p_275688_,
//            int p_275245_,
//            int p_275535_,
//            int p_294406_,
//            int p_294663_,
//            float p_275604_,
//            float angleXComponent,
//            float angleYComponent,
//            LivingEntity p_275689_
//    )



            InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, screenWidth / 2 - 16, screenHeight - 94, screenWidth / 2 + 16, screenHeight - 62, 40, 0.0625F, (float)Math.toRadians(-50), (float)Math.toRadians(10), CrowWhitelistEvent.whiteListingCrow);

            if(!CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.isEmpty()){
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pushPose();
                poseStack.translate(screenWidth / 2f - 14 - ((CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size() - 1) / 2f * 21), screenHeight - 40, 100.0F);
                poseStack.translate(8.0F, -8.0F, 0.0F);
                poseStack.scale(12.0F, 12.0F, 12.0F);
                poseStack.mulPose(new Matrix4f().scale(1, -1, 1));
                Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);

                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

                Lighting.setupFor3DItems();
                poseStack.last().normal().rotate(Axis.YP.rotationDegrees((float) -90));
                for(int itor = 0; itor < CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size(); itor++){
                    poseStack.pushPose();
                    poseStack.translate(itor * 1.7f, Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) / 4, 0.0F);

                    float zRot = 0;
                    float xRot = 20;
                    float yRot = 30 + (ClientEvents.getClientTicks()) + itor * 30;

                    poseStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(zRot));
                    poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
                    poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                    poseStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

                    BlockState state = CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.get(itor).defaultBlockState();
                    if (state.hasProperty(BlockStateProperties.AGE_1))
                        state = state.setValue(BlockStateProperties.AGE_1, Mth.clamp((int) (((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 2), 0, 1));
                    else if (state.hasProperty(BlockStateProperties.AGE_2))
                        state = state.setValue(BlockStateProperties.AGE_2, Mth.clamp((int) (((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 3), 0, 2));
                    else if (state.hasProperty(BlockStateProperties.AGE_3))
                        state = state.setValue(BlockStateProperties.AGE_3, Mth.clamp((int) (((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 4), 0, 3));
                    else if (state.hasProperty(BlockStateProperties.AGE_4))
                        state = state.setValue(BlockStateProperties.AGE_4, Mth.clamp((int)(((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 5), 0, 4));
                    else if (state.hasProperty(BlockStateProperties.AGE_5))
                        state = state.setValue(BlockStateProperties.AGE_5, Mth.clamp((int)(((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 6), 0, 5));
                    else if (state.hasProperty(BlockStateProperties.AGE_7))
                        state = state.setValue(BlockStateProperties.AGE_7, Mth.clamp((int)(((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30) + 1) / 2) * 8), 0, 7));
                    if(state.hasProperty(BlockStateProperties.BERRIES))
                        state = state.setValue(BlockStateProperties.BERRIES, true);
                    renderBlock(poseStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                    if(state.hasProperty(PickableDoublePlant.HALF)){
                        poseStack.pushPose();
                        poseStack.translate(0F, 1, 0.0F);
                        state = state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
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

    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

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