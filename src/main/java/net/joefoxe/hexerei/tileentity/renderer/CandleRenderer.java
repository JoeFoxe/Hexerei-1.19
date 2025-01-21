package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.DynamicTextureHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.Optional;

public class CandleRenderer implements BlockEntityRenderer<CandleTile> {


    CandleModel herbLayer;
    CandleModel glowLayer;
    CandleModel swirlLayer;
    CandleModel candleModel;
    CandleModel baseModel;
    public CandleRenderer() {
        super();

    }

    @Override
    public AABB getRenderBoundingBox(CandleTile blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(25);
    }

    @Override
    public void render(CandleTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if (!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CandleTile))
            return;


        if (herbLayer == null)
            herbLayer = new CandleModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_HERB_LAYER));
        if (glowLayer == null)
            glowLayer = new CandleModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_GLOW_LAYER));
        if (swirlLayer == null)
            swirlLayer = new CandleModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_SWIRL_LAYER));
        if (candleModel == null)
            candleModel = new CandleModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_LAYER));
        if (baseModel == null)
            baseModel = new CandleModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_BASE_LAYER));



        // base layer
        for(CandleData candleData : tileEntityIn.candles) {
            candleData.baseHeight = 0;
            if (candleData.hasCandle && candleData.hasBase()) {

                matrixStackIn.pushPose();
                translate(matrixStackIn, candleData, partialTicks, tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));

                if(candleData.base.layerFromBlockLocation) {
                    Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.base.layer);

                    if (holder.isPresent()) {
                        BlockState blockState = holder.get().value().defaultBlockState();
                        ResourceLocation loc = HexereiUtil.getResource("candle_base/" + candleData.base.layer.getPath());
                        if (DynamicTextureHandler.textures.containsKey(loc)) {
                            DynamicTextureHandler.DynamicBaseSprite baseSprite = DynamicTextureHandler.textures.get(loc);
                            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(loc));
                            renderCube(matrixStackIn, vertexConsumer2, baseSprite.width, baseSprite.height, baseSprite.width, 16, 16, combinedLightIn, combinedOverlayIn);
                            candleData.baseHeight = baseSprite.height;
                        } else {

                            DynamicTextureHandler.addNewSprite(loc, blockState);
                        }

                    } else {
                        // draw missing texture cube

                        candleData.baseHeight = 2;
                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(MissingTextureAtlasSprite.getLocation()));
                        renderCube(matrixStackIn, vertexConsumer2, 3, candleData.baseHeight, 3, 16, 16, combinedLightIn, combinedOverlayIn);
                    }
                } else {

                    candleData.baseHeight = 1;
                    VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                    renderCube(matrixStackIn, vertexConsumer2, 3, candleData.baseHeight, 3, 16, 16, combinedLightIn, combinedOverlayIn);
//                    baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }

                matrixStackIn.popPose();
            }
        }


        // Candle and wick layer
        for(CandleData candleData : tileEntityIn.candles) {
            if (candleData.hasCandle) {

                matrixStackIn.pushPose();
                translate(matrixStackIn, candleData, partialTicks, tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));

                matrixStackIn.translate(0 / 16f, 23f / 16f, 0 / 16f);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));

                float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);

                matrixStackIn.translate(0 / 16f, -candleData.baseHeight / 16f, 0 / 16f);
                VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(HexereiUtil.getResource("textures/block/candle.png")));
                if (candleData.height != 0 && candleData.height <= 7) {
                    candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 1.0F));
                }


                matrixStackIn.pushPose();
                matrixStackIn.translate(0, (7 - candleData.height) / 16f, 0);
                candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
                matrixStackIn.popPose();

                matrixStackIn.popPose();
            }
        }


        //herb layer//////////////////////

        for(CandleData candleData : tileEntityIn.candles) {
            if (candleData.hasCandle && candleData.hasHerb()) {

                matrixStackIn.pushPose();
                translate(matrixStackIn, candleData, partialTicks, tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));

                matrixStackIn.translate(0 / 16f, 23f / 16f, 0 / 16f);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));

                float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);

                matrixStackIn.translate(0 / 16f, -candleData.baseHeight / 16f, 0 / 16f);

                if (candleData.height != 0 && candleData.height <= 7) {
                    if (candleData.herb.layerFromBlockLocation) {
                        Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.herb.layer);
                        if (holder.isPresent()) {
                            BlockState blockState = holder.get().value().defaultBlockState();

                            TextureAtlasSprite sprite = getFirstSprite(blockState);
                            if (sprite != null) {
                                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png")));
                                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
                            }
                        }
                    } else {

                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
                        herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                    }
                }

                matrixStackIn.popPose();
            }
        }

        //glow layer//////////////////////

        for(CandleData candleData : tileEntityIn.candles) {
            if (candleData.hasCandle && candleData.hasGlow()) {

                matrixStackIn.pushPose();
                translate(matrixStackIn, candleData, partialTicks, tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));

                matrixStackIn.translate(0 / 16f, 23f / 16f, 0 / 16f);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));

                float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


                matrixStackIn.translate(0 / 16f, -candleData.baseHeight / 16f, 0 / 16f);


                if (candleData.glow.layerFromBlockLocation) {
                    Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.glow.layer);
                    if (holder.isPresent()) {
                        BlockState blockState = holder.get().value().defaultBlockState();

                        TextureAtlasSprite sprite = getFirstSprite(blockState);
                        if (sprite != null) {
                            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png")));
                            if (candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                                int color = potionCandleEffect.effect.getColor();
                                float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                                if (candleData.height != 0 && candleData.height <= 7) {
                                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col2[0], col2[1], col2[2], 0.75F));
                                }
                            } else {
                                if (candleData.height != 0 && candleData.height <= 7) {
                                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                                }
                            }
                        }
                    }
                } else {

                    VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glow.layer));
                    if (candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                        int color = potionCandleEffect.effect.getColor();
                        float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                        if (candleData.height != 0 && candleData.height <= 7) {
                            glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col2[0], col2[1], col2[2], 0.75F));
                        }
                    } else {
                        if (candleData.height != 0 && candleData.height <= 7) {
                            glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                        }
                    }
                }


                matrixStackIn.popPose();
            }
        }

        //swirl layer//////////////////////


        for(CandleData candleData : tileEntityIn.candles) {
            if (candleData.hasCandle && candleData.hasSwirl()) {

                matrixStackIn.pushPose();
                translate(matrixStackIn, candleData, partialTicks, tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));

                matrixStackIn.translate(0 / 16f, 23f / 16f, 0 / 16f);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));

                matrixStackIn.translate(0 / 16f, -candleData.baseHeight / 16f, 0 / 16f);

                float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);

                if (candleData.swirl.layerFromBlockLocation) {
                    Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.swirl.layer);
                    if (holder.isPresent()) {
                        BlockState blockState = holder.get().value().defaultBlockState();

                        TextureAtlasSprite sprite = getFirstSprite(blockState);
                        if (sprite != null) {
                            float offset = ClientEvents.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTimeNs();
                            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png"), (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                            if (candleData.height != 0 && candleData.height <= 7) {
                                swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 0.75F));
                            }
                        }
                    }
                } else {

                    float offset = ClientEvents.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTimeNs();
                    VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirl.layer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                    if (candleData.height != 0 && candleData.height <= 7) {
                        swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 0.75F));
                    }
                }

                matrixStackIn.popPose();
            }
        }

    }

    private void translate(PoseStack poseStack, CandleData candleData, float partialTicks, Direction facing) {
        poseStack.translate(8f / 16f, 0f / 16f, 8f / 16f);
        poseStack.translate(
                Mth.lerp(partialTicks, candleData.xO, candleData.x),
                Mth.lerp(partialTicks, candleData.yO, candleData.y),
                Mth.lerp(partialTicks, candleData.zO, candleData.z));
        if (facing == Direction.EAST)
            poseStack.mulPose(Axis.YP.rotationDegrees(270f));
        else if (facing == Direction.SOUTH)
            poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        else if (facing == Direction.WEST)
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
    }
    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
    }


    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    public static void renderCube(PoseStack poseStack, VertexConsumer vertexConsumer, float xSize, float ySize, float zSize, float texWidth, float texHeight, int combinedLightIn, int combinedOverlayIn) {

        poseStack.pushPose();
        poseStack.translate(0 / 16f, -0.01f / 16f, 0 / 16f);
        poseStack.scale(0.065f, 0.065f, 0.065f);

        float xOffset = xSize / 2.0f;
        float yOffset = ySize;
        float zOffset = zSize / 2.0f;


        float uT0 = 0;
        float uT1 = uT0 + (xSize / texWidth);
        float vT0 = 0;
        float vT1 = vT0 + (xSize / texWidth);
        // Top Face
        addVertex(vertexConsumer, poseStack, -xOffset,  yOffset, -zOffset, uT0, vT0, 0, 1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset,  yOffset, -zOffset, uT0, vT1, 0, 1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset,  yOffset, zOffset, uT1, vT1, 0, 1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset,  yOffset, zOffset, uT1, vT0, 0, 1, 0, combinedLightIn, combinedOverlayIn);

        float uB0 = (xSize / texWidth);
        float uB1 = uB0 + (xSize / texWidth);
        float vB0 = 0;
        float vB1 = vB0 + (xSize / texWidth);
        // Bottom Face
        addVertex(vertexConsumer, poseStack, -xOffset, 0, zOffset, uB0, vB0, 0, -1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, 0, -zOffset, uB1, vB0, 0, -1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, 0, -zOffset, uB1, vB1, 0, -1, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, 0, zOffset, uB0, vB1, 0, -1, 0, combinedLightIn, combinedOverlayIn);

        float uN0 = 0;
        float uN1 = uN0 + (ySize / texWidth);
        float vN0 = (xSize / texWidth);
        float vN1 = (xSize / texWidth) + (xSize / texWidth);
        // North Face
        addVertex(vertexConsumer, poseStack, -xOffset, 0, -zOffset, uN1, vN1, 0, 0, -1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, 0, -zOffset, uN1, vN0, 0, 0, -1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, yOffset, -zOffset, uN0, vN0, 0, 0, -1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, yOffset, -zOffset, uN0, vN1, 0, 0, -1, combinedLightIn, combinedOverlayIn);


        float uS0 = (ySize / texWidth);
        float uS1 = uS0 + (ySize / texWidth);
        float vS0 = (xSize / texWidth);
        float vS1 = vS0 + (xSize / texWidth);
        // South Face
        addVertex(vertexConsumer, poseStack, -xOffset, 0, zOffset, uS1, vS0, 0, 0, 1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, 0, zOffset, uS1, vS1, 0, 0, 1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, yOffset, zOffset, uS0, vS1, 0, 0, 1, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, yOffset, zOffset, uS0, vS0, 0, 0, 1, combinedLightIn, combinedOverlayIn);


        float uW0 = 0;
        float uW1 = uW0 + (ySize / texWidth);
        float vW0 = (xSize / texWidth) + (xSize / texWidth);
        float vW1 = vW0 + (xSize / texWidth);
        // West Face
        addVertex(vertexConsumer, poseStack, -xOffset, 0, -zOffset, uW1, vW0, -1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, 0, zOffset, uW1, vW1, -1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, yOffset, zOffset, uW0, vW1, -1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, -xOffset, yOffset, -zOffset, uW0, vW0, -1, 0, 0, combinedLightIn, combinedOverlayIn);


        float uE0 = (ySize / texWidth);
        float uE1 = uE0 + (ySize / texWidth);
        float vE0 = (xSize / texWidth) + (xSize / texWidth);
        float vE1 = vE0 + (xSize / texWidth);
        // East Face
        addVertex(vertexConsumer, poseStack, xOffset, 0, -zOffset, uE1, vE1, 1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, 0, zOffset, uE1, vE0, 1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, yOffset, zOffset, uE0, vE0, 1, 0, 0, combinedLightIn, combinedOverlayIn);
        addVertex(vertexConsumer, poseStack, xOffset, yOffset, -zOffset, uE0, vE1, 1, 0, 0, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }

    private static void addVertex(VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, float u, float v, float nx, float ny, float nz, int combinedLightIn, int combinedOverlayIn) {
        vertexConsumer
                .addVertex(poseStack.last().pose(), x, y, z)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setOverlay(combinedOverlayIn)
                .setLight(combinedLightIn)
                .setNormal(poseStack.last(), nx, ny, nz);
    }


    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

    public static TextureAtlasSprite getFirstSprite(BlockState blockState) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

        // Cycle through all directions first
        for (Direction direction : Direction.values()) {
            List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
            if (!quads.isEmpty()) {
                return quads.get(0).getSprite();
            }
        }

        // Then cycle through the unculled quads
        List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
        if (!unculledQuads.isEmpty()) {
            return unculledQuads.get(0).getSprite();
        }

        return null; // Return null if no sprite is found
    }

    public static NativeImage modifyTexture(NativeImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        NativeImage newImage = new NativeImage(width, height, true);

        // Example modification: Invert colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = originalImage.getPixelRGBA(x, y);
                int invertedColor = ~color | (color & 0xFF000000); // Invert color while keeping alpha
                newImage.setPixelRGBA(x, y, invertedColor);
            }
        }

        return newImage;
    }


}
