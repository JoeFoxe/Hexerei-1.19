package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.Collection;

public class CrystalBallRenderer implements BlockEntityRenderer<CrystalBallTile> {


    @Override
    public void render(CrystalBallTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CrystalBallTile))
            return;



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

        DyeColor col = HexereiUtil.getDyeColorNamed("jeb_", 0);
        float f = 1;
        float f1 = 1;
        float f2 = 1;
        if(col != null){

            float f3 = (((Hexerei.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col2 = HexereiUtil.getDyeColorNamed("jeb_", 1);

            float[] afloat1 = Sheep.getColorArray(col);
            float[] afloat2 = Sheep.getColorArray(col2);
            f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;

        }

//        matrixStackIn.pushPose();
//
//        matrixStackIn.translate(0.5f, 9f / 16f, 0.5f);
//        matrixStackIn.translate(0f/16f , tileEntityIn.orbOffset/16f, 0f/16f);
//        matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//        matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
//
//        VertexConsumer buffer = bufferIn.getBuffer(ModRenderTypes.MOON_PHASE);
//        Matrix4f matrix = matrixStackIn.last().pose();
//
//        float size = 8f;
//
//        buffer.vertex(matrix, size, -size, 0.01f).color(1, 1, 1, tileEntityIn.moonAlpha).uv(xOffset / 256f, yOffset / 256f).uv2(0xF000F0).endVertex();
//        buffer.vertex(matrix, size, size, 0.01f).color(1, 1, 1, tileEntityIn.moonAlpha).uv(xOffset / 256f, (yOffset + 8) / 256f).uv2(0xF000F0).endVertex();
//        buffer.vertex(matrix, -size, size, 0.01f).color(1, 1, 1, tileEntityIn.moonAlpha).uv((xOffset + 8) / 256f, (yOffset + 8) / 256f).uv2(0xF000F0).endVertex();
//        buffer.vertex(matrix, -size, -size, 0.01f).color(1, 1, 1, tileEntityIn.moonAlpha).uv((xOffset + 8) / 256f, yOffset / 256f).uv2(0xF000F0).endVertex();
//
//        matrixStackIn.popPose();
        renderQuad(tileEntityIn, matrixStackIn, xOffset, yOffset, bufferIn.getBuffer(ModRenderTypes.MOON_PHASE), partialTicks);

        matrixStackIn.pushPose();
        matrixStackIn.translate(8f / 16f, 9f / 16f, 8f / 16f);
        matrixStackIn.translate(0f/16f , tileEntityIn.orbOffset/16f, 0f/16f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 4));

        renderBlock(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModBlocks.CRYSTAL_BALL_ORB.get().defaultBlockState(), null, 0xFFFFFF);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(8f/16f , 7f/16f, 8f/16f);
        matrixStackIn.translate(0f/16f , tileEntityIn.largeRingOffset/16f, 0f/16f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 6));
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_LARGE_RING.get().defaultBlockState());
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(8f/16f , 4.5f/16f, 8f/16f);
        matrixStackIn.translate(0f/16f , tileEntityIn.smallRingOffset/16f, 0f/16f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 6));
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_SMALL_RING.get().defaultBlockState());
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_STAND.get().defaultBlockState());
        matrixStackIn.popPose();

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
        poseStack.translate(0.5f,9f / 16f,0.5f);
        poseStack.translate(0f/16f , tileEntityIn.orbOffset/16f, 0f/16f);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(-Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 4));
        poseStack.scale(0.25f,0.25f,0.25f);
        drawWobblyCube(poseStack, 1f, 0.86f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        drawWobblyCube(poseStack, 0.85f, 0.5f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        drawWobblyCube(poseStack, 1.12f, 0.6f * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset);
        poseStack.popPose();
    }

    public static void drawWobblyCube(PoseStack poseStack, float scale, float alpha, Collection<Vector3f[]> offsetMap, Vector3f[] bottomVertices, Vector3f[] topVertices, VertexConsumer consumer, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.scale(scale,scale,scale);
        for (Vector3f[] offsets : offsetMap) {
            drawSide(poseStack, alpha, offsets, consumer, xOffset, yOffset);
        }
        drawSide(poseStack, alpha, new Vector3f[]{bottomVertices[3], bottomVertices[2], bottomVertices[1], bottomVertices[0]}, consumer, xOffset, yOffset);
        drawSide(poseStack, alpha, topVertices, consumer, xOffset, yOffset);
        poseStack.popPose();
    }

    public static void drawSide(PoseStack poseStack, float alpha, Vector3f[] offsets, VertexConsumer consumer, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        Matrix4f matrix = poseStack.last().pose();
//        DyeColor col = HexereiUtil.getDyeColorNamed("jeb_", 0); // for potential color changing if dyed?
        float f = 1;
        float f1 = 1;
        float f2 = 1;
//        if(col != null){
//
//            float f3 = (((Hexerei.getClientTicks()) / 10f * 4) % 16) / (float) 16;
//
//            DyeColor col2 = HexereiUtil.getDyeColorNamed("jeb_", 1);
//
//            float[] afloat1 = Sheep.getColorArray(col);
//            float[] afloat2 = Sheep.getColorArray(col2);
//            f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
//            f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
//            f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
//
//        }

        consumer.vertex(matrix, offsets[0].x(), offsets[0].y(), offsets[0].z()).color(f, f1, f2, alpha).uv((xOffset) / 256f, (yOffset + 8) / 256f).uv2(0xF000F0).endVertex();
        consumer.vertex(matrix, offsets[1].x(), offsets[1].y(), offsets[1].z()).color(f, f1, f2, alpha).uv((xOffset + 8) / 256f, (yOffset + 8) / 256f).uv2(0xF000F0).endVertex();
        consumer.vertex(matrix, offsets[2].x(), offsets[2].y(), offsets[2].z()).color(f, f1, f2, alpha).uv((xOffset + 8) / 256f, yOffset / 256f).uv2(0xF000F0).endVertex();
        consumer.vertex(matrix, offsets[3].x(), offsets[3].y(), offsets[3].z()).color(f, f1, f2, alpha).uv((xOffset) / 256f, yOffset / 256f).uv2(0xF000F0).endVertex();

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

    private void renderItem(ItemStack stack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }


    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, float red, float green, float blue) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, red, green, blue);
    }
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType, int color) {
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
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, float red, float green, float blue) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);

//                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, red, green, blue, p_110916_, p_110917_, modelData, null);
                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, red, green, blue, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
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
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }


}
