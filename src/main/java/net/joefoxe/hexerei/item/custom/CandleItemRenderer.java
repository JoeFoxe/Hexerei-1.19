package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.renderer.CandleRenderer;
import net.joefoxe.hexerei.util.DynamicTextureHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.Optional;

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
    public static CandleTile loadBlockEntityFromItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof Candle candle && candle.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()) instanceof CandleTile te) {
                te.setHeight(CandleItem.getHeight(stack));
                te.setDyeColor(HexereiUtil.getDyeColor(stack, Candle.BASE_COLOR));
                if (!stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().isEmpty()) {
                    te.candles.get(0).load(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(), Hexerei.DynamicRegistries.get());
                    if (stack.has(DataComponents.DYED_COLOR))
                        te.candles.get(0).dyeColor = stack.get(DataComponents.DYED_COLOR).rgb();
                }
                return te;
            }
        }
        return null;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        this.renderTileStuff(stack, itemDisplayContext, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        if (tag != null && !tag.isEmpty()) {
            CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
            return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : Candle.BASE_COLOR;
        }
        return Candle.BASE_COLOR;
    }


    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }

    public void renderTileStuff(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        CandleTile tileEntityIn = loadBlockEntityFromItem(stack);
        if (tileEntityIn == null) return;

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate(8 / 16f, 27f / 16f, 8 / 16f);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));

        CandleData candleData = tileEntityIn.candles.get(0);
        boolean hasBase = candleData.base.layer != null;

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

        float[] col = HexereiUtil.rgbIntToFloatArray(candleData.dyeColor);
        int height = 0;

        if (candleData.base.layer != null) {
            if(candleData.base.layerFromBlockLocation) {
                Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.base.layer);

                if (holder.isPresent()) {
                    BlockState blockState = holder.get().value().defaultBlockState();
                    ResourceLocation loc = HexereiUtil.getResource("candle_base/" + candleData.base.layer.getPath());
                    if (DynamicTextureHandler.textures.containsKey(loc)) {
                        DynamicTextureHandler.DynamicBaseSprite baseSprite = DynamicTextureHandler.textures.get(loc);
                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(loc));
                        matrixStackIn.pushPose();
                        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                        matrixStackIn.translate(0, -23f / 16f, 0);
                        CandleRenderer.renderCube(matrixStackIn, vertexConsumer2, baseSprite.width, baseSprite.height, baseSprite.width, 16, 16, combinedLightIn, combinedOverlayIn);
                        matrixStackIn.popPose();
                        height = baseSprite.height;
                    } else {

                        DynamicTextureHandler.addNewSprite(loc, blockState);
                    }

                }
            } else {

                VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
//                baseModel.base.render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                height = 1;
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                matrixStackIn.translate(0, -23f / 16f, 0);
                CandleRenderer.renderCube(matrixStackIn, vertexConsumer2, 3, height, 3, 16, 16, combinedLightIn, combinedOverlayIn);
                matrixStackIn.popPose();
            }
        } else {
//            matrixStackIn.translate(0, 1 / 16f, 0);
        }

        matrixStackIn.translate(0, -height / 16f, 0);

        VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(HexereiUtil.getResource("textures/block/candle.png")));
        if (candleData.height != 0 && candleData.height <= 7) {
            candleModel.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 1.0F));
        }


        matrixStackIn.pushPose();
        matrixStackIn.translate(0, (7 - candleData.height) / 16f, 0);
        candleModel.wick.render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
        matrixStackIn.popPose();


        if (candleData.herb.layer != null) {

            if(candleData.height != 0 && candleData.height <= 7) {
                if (candleData.herb.layerFromBlockLocation) {
                    Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.herb.layer);
                    if (holder.isPresent()) {
                        BlockState blockState = holder.get().value().defaultBlockState();

                        TextureAtlasSprite sprite = CandleRenderer.getFirstSprite(blockState);
                        if (sprite != null) {
                            VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png")));
                            herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                        }
                    }
                } else {

                    VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.herb.layer));
                    herbLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                }
            }

        }

        if (candleData.glow.layer != null) {
            if(candleData.glow.layerFromBlockLocation) {
                Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.glow.layer);
                if (holder.isPresent()) {
                    BlockState blockState = holder.get().value().defaultBlockState();

                    TextureAtlasSprite sprite = CandleRenderer.getFirstSprite(blockState);
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
                if(candleData.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                    int color = potionCandleEffect.effect.getColor();
                    float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                    if (candleData.height != 0 && candleData.height <= 7) {
                        glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col2[0], col2[1], col2[2], 0.75F));
                    }
                }else{
                    if(candleData.height != 0 && candleData.height <= 7) {
                        glowLayer.wax[candleData.height - 1].render(matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F));
                    }
                }
            }
        }

        if (candleData.swirl.layer != null) {
            if(candleData.swirl.layerFromBlockLocation) {
                Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.swirl.layer);
                if (holder.isPresent()) {
                    BlockState blockState = holder.get().value().defaultBlockState();

                    TextureAtlasSprite sprite = CandleRenderer.getFirstSprite(blockState);
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
        }

        matrixStackIn.popPose();

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
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

//                    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_) {
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}