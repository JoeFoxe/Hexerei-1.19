package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleHerbLayer;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.renderer.HerbJarRenderer;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.commons.codec.binary.Hex;

public class CandleItemRenderer extends CustomItemRenderer {

    CandleModel herbLayer;
    CandleModel glowLayer;
    CandleModel swirlLayer;
    CandleModel candleModel;
    CandleModel baseModel;
    public CandleItemRenderer() {
        super();

    }

    @OnlyIn(Dist.CLIENT)
    public static CandleTile loadBlockEntityFromItem(CompoundTag tag, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof Candle candle) {
                CandleTile te = (CandleTile)candle.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                te.setDyeColor(getCustomColor(tag));
                te.setHeight(CandleItem.getHeight(stack));
                String herbLayer = CandleItem.getHerbLayer(stack);
                String baseLayer = CandleItem.getBaseLayer(stack);
                String glowLayer = CandleItem.getGlowLayer(stack);
                String swirlLayer = CandleItem.getSwirlLayer(stack);
                if (herbLayer != null)
                    te.candles.get(0).herbLayer = new ResourceLocation(herbLayer);
                else
                    te.candles.get(0).herbLayer = null;
                if (baseLayer != null)
                    te.candles.get(0).baseLayer = new ResourceLocation(baseLayer);
                else
                    te.candles.get(0).baseLayer = null;
                if (glowLayer != null)
                    te.candles.get(0).glowLayer = new ResourceLocation(glowLayer);
                else
                    te.candles.get(0).glowLayer = null;
                if (swirlLayer != null)
                    te.candles.get(0).swirlLayer = new ResourceLocation(swirlLayer);
                else
                    te.candles.get(0).swirlLayer = null;


//                if(item.hasCustomHoverName())
//                    te.customName = item.getHoverName();
////                if (te != null) te.load(tag);
                return te;
            }
        }
        return null;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

//        matrixStackIn.pushPose();
//        matrixStackIn.translate(0.2, -0.1, -0.10);
//        BlockItem item = ((BlockItem) stack.getItem());
//        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, null);
//        matrixStackIn.popPose();

        this.renderTileStuff(stack.getOrCreateTag(), stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0xCCC398;
    }



    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GUI, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }

    public void renderTileStuff(CompoundTag tag, ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        CandleTile tileEntityIn = loadBlockEntityFromItem(tag, stack);
        if (tileEntityIn == null) return;

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate( 8/16f, 28f/16f, 8/16f);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));

        CandleData candleData = tileEntityIn.candles.get(0);
        boolean hasBase = candleData.baseLayer != null;

        if(herbLayer == null) herbLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_HERB_LAYER));
        if(glowLayer == null) glowLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_GLOW_LAYER));
        if(swirlLayer == null) swirlLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_SWIRL_LAYER));
        if(candleModel == null) candleModel = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_LAYER));
        if(baseModel == null) baseModel = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_BASE_LAYER));

        float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);

        if(hasBase){
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.baseLayer));
            baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }else{
            matrixStackIn.translate( 0, 1/16f, 0);
        }

        VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Hexerei.MOD_ID, "textures/block/candle.png")));
        if(candleData.height != 0 && candleData.height <= 7) {
            candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 1.0F);
        }


        matrixStackIn.pushPose();
        matrixStackIn.translate( 0, (7 - candleData.height)/16f, 0);
        candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();


        if(candleData.herbLayer != null){
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herbLayer));
            if (candleData.height != 0 && candleData.height <= 7) {
                herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.75F);
            }
        }

        if(candleData.glowLayer != null){
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.glowLayer));
            if (candleData.height != 0 && candleData.height <= 7) {
                glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.75F);
            }
        }

        if(candleData.swirlLayer != null){
            float offset = Hexerei.getClientTicksWithoutPartial() + Minecraft.getInstance().getFrameTime();
            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.energySwirl(candleData.swirlLayer, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
            if (candleData.height != 0 && candleData.height <= 7) {
                swirlLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, col[0], col[1], col[2], 0.75F);
            }
        }

        matrixStackIn.popPose();

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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

//                    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_) {
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
            }

        }
    }

}