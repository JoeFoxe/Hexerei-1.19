package net.joefoxe.hexerei.client.renderer.entity.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.HexereiPaintingEntity;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HexereiPaintingRenderer extends EntityRenderer<HexereiPaintingEntity> {
    public HexereiPaintingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(HexereiPaintingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        PaintingTextureManager paintingtexturemanager = Minecraft.getInstance().getPaintingTextures();
        this.renderPainting(poseStack, buffer, entity, 1, 1,
                entity.getEntityData().get(HexereiPaintingEntity.U0), entity.getEntityData().get(HexereiPaintingEntity.U1),
                entity.getEntityData().get(HexereiPaintingEntity.V0), entity.getEntityData().get(HexereiPaintingEntity.V1), paintingtexturemanager.getBackSprite());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(HexereiPaintingEntity entity) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    public ResourceLocation getCanvasTextureLocation() {
        return ResourceLocation.parse("hexerei:textures/book/canvas.png");
    }

    private void renderPainting(PoseStack poseStack, MultiBufferSource buffer, HexereiPaintingEntity painting, int width, int height, float u0, float u1, float v0, float v1, TextureAtlasSprite backSprite) {
        PoseStack.Pose posestack$pose = poseStack.last();
        float f = (float)(-width) / 2.0F;
        float f1 = (float)(-height) / 2.0F;
        float f2 = 0.03125F;
        float f3 = backSprite.getU0();
        float f4 = backSprite.getU1();
        float f5 = backSprite.getV0();
        float f6 = backSprite.getV1();
        float f7 = backSprite.getU0();
        float f8 = backSprite.getU1();
        float f9 = backSprite.getV0();
        float f10 = backSprite.getV(0.0625F);
        float f11 = backSprite.getU0();
        float f12 = backSprite.getU(0.0625F);
        float f13 = backSprite.getV0();
        float f14 = backSprite.getV1();
        double d0 = (double)1.0F / (double)width;
        double d1 = (double)1.0F / (double)height;

        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                float f15 = f + (float)(i + 1);
                float f16 = f + (float)i;
                float f17 = f1 + (float)(j + 1);
                float f18 = f1 + (float)j;
                int k = painting.getBlockX();
                int l = Mth.floor(painting.getY() + (double)((f17 + f18) / 2.0F));
                int i1 = painting.getBlockZ();
                Direction direction = painting.getDirection();
                if (direction == Direction.NORTH) {
                    k = Mth.floor(painting.getX() + (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.WEST) {
                    i1 = Mth.floor(painting.getZ() - (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.SOUTH) {
                    k = Mth.floor(painting.getX() - (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.EAST) {
                    i1 = Mth.floor(painting.getZ() + (double)((f15 + f16) / 2.0F));
                }

                int j1 = LevelRenderer.getLightColor(painting.level(), new BlockPos(k, l, i1));
                float f19 = 1;
                float f20 = 0;
                float f21 = 1;
                float f22 = 0;
                VertexConsumer consumer = buffer.getBuffer(RenderType.entitySolid(this.getCanvasTextureLocation()));
                this.vertex(posestack$pose, consumer, f15, f18, f20, f21, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f19, f21, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f19, f22, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f20, f22, -0.03125F, 0, 0, -1, j1);
                if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
                    bufferSource.endBatch();
                f19 = u1;
                f20 = u0;
                f21 = v1;
                f22 = v0;
                ResourceLocation loc = ResourceLocation.parse(painting.getPaintingLocation());
                if (!loc.equals(ResourceLocation.withDefaultNamespace("missingno"))){
                    VertexConsumer consumer1 = buffer.getBuffer(ModRenderTypes.bookTranslucent(loc));
                    this.vertex(posestack$pose, consumer1, f15, f18, f20, f21, -0.03265F, 0, 0, -1, j1);
                    this.vertex(posestack$pose, consumer1, f16, f18, f19, f21, -0.03265F, 0, 0, -1, j1);
                    this.vertex(posestack$pose, consumer1, f16, f17, f19, f22, -0.03265F, 0, 0, -1, j1);
                    this.vertex(posestack$pose, consumer1, f15, f17, f20, f22, -0.03265F, 0, 0, -1, j1);
                    if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
                        bufferSource.endBatch();
                }


                VertexConsumer consumer2 = buffer.getBuffer(RenderType.entitySolid(this.getTextureLocation(painting)));
                this.vertex(posestack$pose, consumer2, f15, f17, f4, f5, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer2, f16, f17, f3, f5, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer2, f16, f18, f3, f6, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer2, f15, f18, f4, f6, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer2, f15, f17, f7, f9, -0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f17, f8, f9, -0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f17, f8, f10, 0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f17, f7, f10, 0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f18, f7, f9, 0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f18, f8, f9, 0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f18, f8, f10, -0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f18, f7, f10, -0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f17, f12, f13, 0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f18, f12, f14, 0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f18, f11, f14, -0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f15, f17, f11, f13, -0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f17, f12, f13, -0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f18, f12, f14, -0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f18, f11, f14, 0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer2, f16, f17, f11, f13, 0.03125F, 1, 0, 0, j1);
            }
        }

    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int packedLight) {
        consumer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
    }
}
