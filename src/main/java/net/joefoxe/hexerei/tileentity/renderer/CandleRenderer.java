package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomModel;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleHerbLayer;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.client.renderer.entity.model.MushroomWitchArmorModel;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

public class CandleRenderer implements BlockEntityRenderer<CandleTile> {


    CandleHerbLayer herbLayer;
    CandleModel candleModel;
    public CandleRenderer() {
        super();

    }


    @Override
    public void render(CandleTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CandleTile))
            return;

//        matrixStackIn.pushPose();
//        matrixStackIn.translate(8f/16f , 4.5f/16f, 8f/16f);
//        matrixStackIn.translate(0f/16f , tileEntityIn.smallRingOffset/16f, 0f/16f);
//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesSpun * 6));
//        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_SMALL_RING.get().defaultBlockState());
//        matrixStackIn.popPose();
        if(tileEntityIn.candles.get(0).type != 0) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(0).x , tileEntityIn.candles.get(0).y, tileEntityIn.candles.get(0).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
//                matrixStackIn.translate(0 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
//                matrixStackIn.translate(-1 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
//                matrixStackIn.translate(-1 , 0, 0);
            }

            if(tileEntityIn.candles.get(0).x == 0 && tileEntityIn.candles.get(0).y == 0 && tileEntityIn.candles.get(0).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-1f / 16f, 0f / 16f, 3f / 16f);
                else if (tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }

            if(tileEntityIn.candles.get(0).height == 0)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_0_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_1_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_2_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_3_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 4)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_4_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 5)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_5_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 6)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_6_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);
            if(tileEntityIn.candles.get(0).height == 7)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_7_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(0).type), tileEntityIn.candles.get(0).dyeColor);





            if(herbLayer == null) herbLayer = new CandleHerbLayer(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.CANDLE_HERB_LAYER));
            if(candleModel == null) candleModel = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_LAYER));

            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
            candleModel.renderToBuffer(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_herb_layer.png")));
            herbLayer.renderToBuffer(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.75F);

//            matrixStackIn.translate(0, -15f / 16f, 0);
//            VertexConsumer vertexConsumer = bufferIn.getBuffer(herbLayer.renderType(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle_herb_layer.png")));
//            herbLayer.renderToBuffer(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(1).type != 0) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.candles.get(1).x , tileEntityIn.candles.get(1).y, tileEntityIn.candles.get(1).z);
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
//                matrixStackIn.translate(0 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
//                matrixStackIn.translate(-1 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
//                matrixStackIn.translate(-1 , 0, 0);
            }

            if(tileEntityIn.candles.get(1).x == 0 && tileEntityIn.candles.get(1).y == 0 && tileEntityIn.candles.get(1).z == 0) {
                if(tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
                else if(tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
                else if(tileEntityIn.numberOfCandles == 2)
                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
            }

            if(tileEntityIn.candles.get(1).height == 0)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_0_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_1_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_2_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_3_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 4)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_4_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 5)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_5_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 6)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_6_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            if(tileEntityIn.candles.get(1).height == 7)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_7_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(1).type), tileEntityIn.candles.get(1).dyeColor);
            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(2).type != 0) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);
            matrixStackIn.translate(tileEntityIn.candles.get(2).x , tileEntityIn.candles.get(2).y, tileEntityIn.candles.get(2).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
//                matrixStackIn.translate(0 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
//                matrixStackIn.translate(-1 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
//                matrixStackIn.translate(-1 , 0, 0);
            }

            if(tileEntityIn.candles.get(2).x == 0 && tileEntityIn.candles.get(2).y == 0 && tileEntityIn.candles.get(2).z == 0) {
                if (tileEntityIn.numberOfCandles == 4)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, 2f / 16f);
                else if (tileEntityIn.numberOfCandles == 3)
                    matrixStackIn.translate(-2f / 16f, 0f / 16f, -3f / 16f);
            }

            if(tileEntityIn.candles.get(2).height == 0)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_0_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_1_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_2_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_3_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 4)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_4_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 5)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_5_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 6)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_6_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            if(tileEntityIn.candles.get(2).height == 7)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_7_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(2).type), tileEntityIn.candles.get(2).dyeColor);
            matrixStackIn.popPose();
        }
        if(tileEntityIn.candles.get(3).type != 0) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f/16f , 0f/16f, 8f/16f);;
            matrixStackIn.translate(tileEntityIn.candles.get(3).x , tileEntityIn.candles.get(3).y, tileEntityIn.candles.get(3).z);
            if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270f));
//                matrixStackIn.translate(0 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
//                matrixStackIn.translate(-1 , 0, -1);
            } else if(tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
//                matrixStackIn.translate(-1 , 0, 0);
            }

            if(tileEntityIn.candles.get(3).x == 0 && tileEntityIn.candles.get(3).y == 0 && tileEntityIn.candles.get(3).z == 0) {
                matrixStackIn.translate(3f / 16f, 0f / 16f, -2f / 16f);
            }
            if(tileEntityIn.candles.get(3).height == 0)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_0_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_1_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_2_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_3_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 4)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_4_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 5)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_5_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 6)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_6_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
            if(tileEntityIn.candles.get(3).height == 7)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_7_OF_7.get().defaultBlockState().setValue(Candle.SLOT_ONE_TYPE, tileEntityIn.candles.get(3).type), tileEntityIn.candles.get(3).dyeColor);
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
            switch(rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
            }

        }
    }


}
