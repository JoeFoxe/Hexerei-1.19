package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.tileentity.CandleTile;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

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
        if (tileEntityIn.candles.get(0).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 0f / 16f, 8f / 16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x, tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 0;
            CandleData candleData = tileEntityIn.candles.get(candle);
            if(candleData.hasBase()){
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 1;
            CandleData candleData = tileEntityIn.candles.get(candle);
            if(candleData.hasBase()) {
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 2;
            CandleData candleData = tileEntityIn.candles.get(candle);
            if(candleData.hasBase()) {
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }


            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 0f / 16f, 8f / 16f);
            matrixStackIn.translate(tileEntityIn.candles.get(3).x, tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0)
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 3;
            CandleData candleData = tileEntityIn.candles.get(candle);
            if(candleData.hasBase()) {
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }


            matrixStackIn.popPose();
        }





        // Candle and wick layer
        if(tileEntityIn.candles.get(0).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x , tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 0;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);

            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
            if(candleData.height != 0 && candleData.height <= 7) {
                candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 1.0F);
            }


            matrixStackIn.pushPose();
            matrixStackIn.translate( 0, (7 - candleData.height)/16f, 0);
            candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 1;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
            if(candleData.height != 0 && candleData.height <= 7) {
                candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 1.0F);
            }

            matrixStackIn.pushPose();
            matrixStackIn.translate( 0, (7 - candleData.height)/16f, 0);
            candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 2;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
            if(candleData.height != 0 && candleData.height <= 7) {
                candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 1.0F);
            }

            matrixStackIn.pushPose();
            matrixStackIn.translate( 0, (7 - candleData.height)/16f, 0);
            candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).hasCandle) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 0f / 16f, 8f / 16f);
            matrixStackIn.translate(tileEntityIn.candles.get(3).x, tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0)
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 3;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
            if(candleData.height != 0 && candleData.height <= 7) {
                candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 1.0F);
            }

            matrixStackIn.pushPose();
            matrixStackIn.translate( 0, (7 - candleData.height)/16f, 0);
            candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();

            matrixStackIn.popPose();
        }



        //herb layer//////////////////////

        if(tileEntityIn.candles.get(0).hasCandle && tileEntityIn.candles.get(0).herb.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x , tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 0;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);

            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
            if(candleData.height != 0 && candleData.height <= 7) {
                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).hasCandle && tileEntityIn.candles.get(1).herb.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 1;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
            if(candleData.height != 0 && candleData.height <= 7) {
                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).hasCandle && tileEntityIn.candles.get(2).herb.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 2;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
            if(candleData.height != 0 && candleData.height <= 7) {
                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).hasCandle && tileEntityIn.candles.get(3).herb.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 0f / 16f, 8f / 16f);
            matrixStackIn.translate(tileEntityIn.candles.get(3).x, tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0)
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 3;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
            if(candleData.height != 0 && candleData.height <= 7) {
                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
            }

            matrixStackIn.popPose();
        }

        //glow layer//////////////////////

        if(tileEntityIn.candles.get(0).hasCandle && tileEntityIn.candles.get(0).glow.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x , tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 0;
            CandleData candleData = tileEntityIn.candles.get(candle);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);

            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glow.layer));
            if(candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                int color = potionCandleEffect.effect.getColor();
                float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                if (candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col2[0], col2[1], col2[2], 0.75F);
                }
            }else{
                if(candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).hasCandle && tileEntityIn.candles.get(1).glow.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 1;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glow.layer));
            if(candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                int color = potionCandleEffect.effect.getColor();
                float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                if (candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col2[0], col2[1], col2[2], 0.75F);
                }
            }else{
                if(candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).hasCandle && tileEntityIn.candles.get(2).glow.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 2;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glow.layer));
            if(candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                int color = potionCandleEffect.effect.getColor();
                float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                if (candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col2[0], col2[1], col2[2], 0.75F);
                }
            }else{
                if(candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).hasCandle && tileEntityIn.candles.get(3).glow.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(3).x , tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0)
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 3;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glow.layer));
            if(candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                int color = potionCandleEffect.effect.getColor();
                float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                if (candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col2[0], col2[1], col2[2], 0.75F);
                }
            }else{
                if(candleData.height != 0 && candleData.height <= 7) {
                    glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 0.75F);
                }
            }

            matrixStackIn.popPose();
        }

        //swirl layer//////////////////////

        if(tileEntityIn.candles.get(0).hasCandle && tileEntityIn.candles.get(0).swirl.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x , tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 0;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);
            if(candleData.swirl.layer != null){
                float offset = Hexerei.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTime();
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirl.layer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                if (candleData.height != 0 && candleData.height <= 7) {
                    swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).hasCandle && tileEntityIn.candles.get(1).swirl.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 1;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);
            if(candleData.swirl.layer != null){
                float offset = Hexerei.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTime();
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirl.layer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                if (candleData.height != 0 && candleData.height <= 7) {
                    swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).hasCandle && tileEntityIn.candles.get(2).swirl.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 2;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);
            if(candleData.swirl.layer != null){
                float offset = Hexerei.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTime();
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirl.layer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                if (candleData.height != 0 && candleData.height <= 7) {
                    swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 0.75F);
                }
            }

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).hasCandle && tileEntityIn.candles.get(3).swirl.layer != null) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 0f / 16f, 8f / 16f);
            matrixStackIn.translate(tileEntityIn.candles.get(3).x, tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
            else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0)
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);

            matrixStackIn.translate( 0/16f, 24f/16f, 0/16f);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));


            int candle = 3;
            CandleData candleData = tileEntityIn.candles.get(candle);


            if(!candleData.hasBase())
                matrixStackIn.translate( 0/16f, 1f/16f, 0/16f);
            float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);
            if(candleData.swirl.layer != null){
                float offset = Hexerei.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTime();
                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirl.layer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                if (candleData.height != 0 && candleData.height <= 7) {
                    swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 0.75F);
                }
            }

            matrixStackIn.popPose();
        }

    }

    private void renderItem(ItemStack stack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn,1);
    }


    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }


    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, int color) {
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
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }


}
