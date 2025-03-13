package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;

public class CrystalBallRenderer implements BlockEntityRenderer<CrystalBallTile> {

    public static ModelResourceLocation ORB = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "block/crystal_ball_orb1"));
    public static ModelResourceLocation ORB2 = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "block/crystal_ball_orb2"));

    @Override
    public void render(CrystalBallTile tileEntityIn, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CrystalBallTile))
            return;


        renderMoon(tileEntityIn, poseStack, partialTicks, bufferIn);

        poseStack.pushPose();
        poseStack.translate(8f / 16f, 8f / 16f, 8f / 16f);
        poseStack.translate(0f/16f , tileEntityIn.orbOffset/16f, 0f/16f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 4));


//        poseStack.pushPose();
//        poseStack.translate(0.5f, 0.5f, 0.5f);
//        poseStack.scale(1.33f, 1.33f, 1.33f);
//        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ORB2);
//        if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
//            Minecraft.getInstance().getItemRenderer().render(
//                    ModBlocks.CRYSTAL_BALL.get().asItem().getDefaultInstance(),
//                    ItemDisplayContext.FIXED,
//                    false,
//                    poseStack,
//                    bufferIn,
//                    combinedLightIn,
//                    combinedOverlayIn,
//                    baseModel
//            );
//        }
//        poseStack.popPose();

        if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();

        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ORB2);
        if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            int i = -1;
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            for (RenderType rt : baseModel.getRenderTypes(ModBlocks.CRYSTAL_BALL.get().defaultBlockState(), RandomSource.create(42), ModelData.EMPTY))
                dispatcher.getModelRenderer().renderModel(poseStack.last(), bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)), ModBlocks.CRYSTAL_BALL.get().defaultBlockState(), baseModel, f, f1, f2, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, rt);
        }


        if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();

        baseModel = Minecraft.getInstance().getModelManager().getModel(ORB);
        if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            int i = -1;
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            for (RenderType rt : baseModel.getRenderTypes(ModBlocks.CRYSTAL_BALL.get().defaultBlockState(), RandomSource.create(42), ModelData.EMPTY))
                dispatcher.getModelRenderer().renderModel(poseStack.last(), bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)), ModBlocks.CRYSTAL_BALL.get().defaultBlockState(), baseModel, f, f1, f2, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, rt);
        }

        if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();



//        renderBlock(poseStack, bufferIn, combinedLightIn, combinedOverlayIn, ModBlocks.CRYSTAL_BALL_ORB.get().defaultBlockState(), null, 0xFFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        renderBlock(poseStack, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_STAND.get().defaultBlockState());
        poseStack.popPose();

    }

    public void renderMoon(CrystalBallTile tileEntityIn, PoseStack poseStack, float partialTicks, MultiBufferSource bufferIn) {
        int xOffset = 0;
        int yOffset = 0;
        switch(MoonPhases.MoonCondition.getMoonPhase(tileEntityIn.getLevel())){
            case NONE -> {
                xOffset = 12;
                yOffset = 12 + 32 + 32;
            }
            case NEW_MOON -> {
                xOffset = 12;
                yOffset = 12 + 32;
            }
            case WAXING_CRESCENT -> {
                xOffset = 12 + 32;
                yOffset = 12 + 32;
            }
            case FIRST_QUARTER -> {
                xOffset = 12 + 32 + 32;
                yOffset = 12 + 32;
            }
            case WAXING_GIBBOUS -> {
                xOffset = 12 + 32 + 32 + 32;
                yOffset = 12 + 32;
            }
            case FULL_MOON -> {
                xOffset = 12;
                yOffset = 12;
            }
            case WANING_GIBBOUS -> {
                xOffset = 12 + 32;
                yOffset = 12;
            }
            case LAST_QUARTER -> {
                xOffset = 12 + 32 + 32;
                yOffset = 12;
            }
            case WANING_CRESCENT -> {
                xOffset = 12 + 32 + 32 + 32;
                yOffset = 12;
            }
        }

        renderQuad(tileEntityIn, poseStack, xOffset, yOffset, bufferIn.getBuffer(ModRenderTypes.entityTranslucent(HexereiUtil.getResource("textures/gui/moon_phases.png"))), partialTicks);

        if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();
    }

    public void renderQuad(CrystalBallTile tileEntityIn, PoseStack poseStack, int xOffset, int yOffset, VertexConsumer consumer, float partialTicks) {
        Vector3f[] bottomVertices = new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0)};
        Vector3f[] topVertices = new Vector3f[]{new Vector3f(0, 1, 0), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0)};

        applyWobble(bottomVertices, 0f);
        applyWobble(topVertices, 0.5f);

        Collection<Vector3f[]> offsetMap = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            offsetMap.add(new Vector3f[]{bottomVertices[(i + 1) % 4], bottomVertices[i], topVertices[(i) % 4], topVertices[(i + 1) % 4]});
        }
        poseStack.pushPose();
        poseStack.translate(0.5f,8f / 16f,0.5f);
        poseStack.translate(0f/16f , tileEntityIn.orbOffset/16f, 0f/16f);

        float inc = Math.max(0, Math.abs(tileEntityIn.centerYawIncrement) - 10) / 90f;
        float vscale = 1 - inc * 0.59f;
        float hscale = 1 + inc * 0.59f;

        poseStack.mulPose(Axis.YP.rotationDegrees(-lerpAngle(tileEntityIn.centerYawO, tileEntityIn.centerYaw, partialTicks)));
        poseStack.mulPose(Axis.XP.rotationDegrees(lerpAngle(tileEntityIn.centerPitchO, tileEntityIn.centerPitch, partialTicks)));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        float scale = 0.18f + (0.07f * (1 - inc));
        poseStack.scale(scale, scale, scale);

        drawWobblyCube(poseStack, 0.8f * vscale, 0.8f * hscale, 0.86f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        drawWobblyCube(poseStack, 0.68f * vscale, 0.68f * hscale, 0.5f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        drawWobblyCube(poseStack, 0.896f * vscale, 0.896f * hscale, 0.6f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        poseStack.popPose();
    }

    public static float lerpAngle(float startAngle, float endAngle, float alpha) {
        startAngle = normalizeAngle(startAngle);
        endAngle = normalizeAngle(endAngle);
        float difference = endAngle - startAngle;
        if (difference > 180.0f) {
            difference -= 360.0f;
        } else if (difference < -180.0f) {
            difference += 360.0f;
        }
        return normalizeAngle(startAngle + alpha * difference);
    }
    private static float normalizeAngle(float angle) {
        while (angle > 180.0f) {
            angle -= 360.0f;
        } while (angle < -180.0f) {
            angle += 360.0f;
        } return angle;
    }

    public static void drawWobblyCube(PoseStack poseStack, float vscale, float hscale, float alpha, Collection<Vector3f[]> offsetMap, Vector3f[] bottomVertices, Vector3f[] topVertices, VertexConsumer consumer, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.scale(hscale,vscale,hscale);

        drawSide(poseStack, alpha, offsetMap.stream().toList().get(0), consumer, xOffset, yOffset);
        drawSide(poseStack, alpha, offsetMap.stream().toList().get(1), consumer, xOffset + 8, yOffset);
        drawSide(poseStack, alpha, offsetMap.stream().toList().get(2), consumer, xOffset + 8, yOffset + 8);
        drawSide(poseStack, alpha, offsetMap.stream().toList().get(3), consumer, xOffset + 16, yOffset);

        drawSide(poseStack, alpha, new Vector3f[]{bottomVertices[3], bottomVertices[2], bottomVertices[1], bottomVertices[0]}, consumer, xOffset + 16, yOffset + 8);
        drawSide(poseStack, alpha, topVertices, consumer, xOffset, yOffset + 8);
        poseStack.popPose();
    }

    public static void drawSide(PoseStack poseStack, float alpha, Vector3f[] offsets, VertexConsumer consumer, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        Matrix4f matrix = poseStack.last().pose();

        consumer.addVertex(matrix, offsets[0].x(), offsets[0].y(), offsets[0].z()).setColor(1, 1, 1, alpha).setUv((xOffset + 8) / 256f, (yOffset + 8) / 256f).setNormal(0, 1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0);
        consumer.addVertex(matrix, offsets[1].x(), offsets[1].y(), offsets[1].z()).setColor(1, 1, 1, alpha).setUv((xOffset) / 256f, (yOffset + 8) / 256f).setNormal(0, 1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0);
        consumer.addVertex(matrix, offsets[2].x(), offsets[2].y(), offsets[2].z()).setColor(1, 1, 1, alpha).setUv((xOffset) / 256f, yOffset / 256f).setNormal(0, 1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0);
        consumer.addVertex(matrix, offsets[3].x(), offsets[3].y(), offsets[3].z()).setColor(1, 1, 1, alpha).setUv((xOffset + 8) / 256f, yOffset / 256f).setNormal(0, 1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0);

        poseStack.popPose();
    }

    public static void applyWobble(Vector3f[] offsets, float initialOffset) {
        applyWobble(offsets, initialOffset, 0.025f);
    }

    public static void applyWobble(Vector3f[] offsets, float initialOffset, float strength) {
        float value = initialOffset;
        for (Vector3f vector3f : offsets) {
            float sine = Mth.sin((float) (Minecraft.getInstance().level.getGameTime() / 10.0F + (value * Math.PI * 2))) * strength;
            vector3f.add(sine, -sine, sine);
            value += 0.25f;
        }
    }

    private void renderItem(ItemStack stack, Level level, PoseStack poseStack, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, poseStack, bufferIn, level, 1);
    }


    private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }
    private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, float red, float green, float blue) {
        renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, red, green, blue);
    }
    private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color) {
        renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, net.minecraft.client.renderer.RenderType renderType, int color) {
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
                    for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, float red, float green, float blue) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);

//                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, red, green, blue, p_110916_, p_110917_, modelData, null);
                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, red, green, blue, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlockC(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, float red, float green, float blue) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, 0, 0, 0, p_110916_, p_110917_, modelData, null);
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
