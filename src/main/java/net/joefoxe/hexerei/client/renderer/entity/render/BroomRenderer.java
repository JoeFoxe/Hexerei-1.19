package net.joefoxe.hexerei.client.renderer.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.*;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)


public class BroomRenderer extends EntityRenderer<BroomEntity>
{
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom.png");
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/power_layer_light.png");

    public BroomRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.0F;
    }
    @Override
    public ResourceLocation getTextureLocation(BroomEntity p_114482_) {
        return TEXTURE;
    }

    public void render(BroomEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        BroomType broomType = entityIn.getBroomType();

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.0D, 0.375D + entityIn.floatingOffset, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw - (entityIn.deltaRotation * 2)));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)entityIn.getDeltaMovement().y() * 20f));
        float f = (float)entityIn.getTimeSinceHit() - partialTicks;
        float f1 = entityIn.getDamageTaken() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)entityIn.getForwardDirection()));
        }

        float f2 = entityIn.getRockingAngle(partialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            matrixStackIn.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entityIn.getRockingAngle(partialTicks), true));
        }

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStackIn.translate(0, -1.6, 0);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStackIn.translate(0, -2.75, 0);


        if(broomType.item() instanceof BroomStickItem broomItem) {
            if (broomItem.model == null)
                broomItem.bakeModels();

            if(broomItem.model != null) {
                VertexConsumer ivertexbuilderStick = bufferIn.getBuffer(broomItem.model.renderType(broomItem.texture));
                broomItem.model.renderToBuffer(matrixStackIn, ivertexbuilderStick, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if(BroomEntity.getDyeColorNamed(entityIn) != null) {
                if (broomItem.outter_model != null) {
                    DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
                    float[] afloat = new float[]{1, 1, 1};
                    if (dyeColor != null)
                        afloat = dyeColor.getTextureDiffuseColors();
                    float offset = Hexerei.getClientTicks() + partialTicks;
                    VertexConsumer ivertexbuilderStick = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                    broomItem.outter_model.renderToBuffer(matrixStackIn, ivertexbuilderStick, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }
            }
        }

        ItemStack brushStack = entityIn.itemHandler.getStackInSlot(2);
        if(brushStack.getItem() instanceof BroomBrushItem brushItem)
            if(brushItem.model == null)
                brushItem.bakeModels();

        if(entityIn.itemHandler.getStackInSlot(2).is(HexereiTags.Items.BROOM_BRUSH)) {

            matrixStackIn.pushPose();
            if(brushStack.getItem() instanceof BroomBrushItem brushItem && brushItem.model != null){

                if(broomType.item() instanceof BroomItem broomItem) {
                    matrixStackIn.translate(broomItem.getBrushOffset().x(), broomItem.getBrushOffset().y(), broomItem.getBrushOffset().z());
                }
                int light = packedLightIn;
                if(brushItem.shouldGlow(Minecraft.getInstance().level, brushStack)){
                    light = LightTexture.FULL_BRIGHT;
                }
                Model broomBrushModel = brushItem.model;
                VertexConsumer brushVertexConsumer = bufferIn.getBuffer(broomBrushModel.renderType(brushItem.texture));
                broomBrushModel.renderToBuffer(matrixStackIn, brushVertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                if (entityIn.hasCustomName() && BroomEntity.getDyeColorNamed(entityIn) != null) {

                    DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
                    float[] afloat = new float[]{1, 1, 1};
                    if (dyeColor != null)
                        afloat = dyeColor.getTextureDiffuseColors();
                    float offset = Hexerei.getClientTicks() + partialTicks;
                    VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                    broomBrushModel.renderToBuffer(matrixStackIn, vertexconsumer, light, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }
            }
            matrixStackIn.popPose();
        }

        ItemStack satchelStack = entityIn.itemHandler.getStackInSlot(1);
        if(satchelStack.getItem() instanceof BroomAttachmentItem satchelItem)
            if(satchelItem.model == null)
                satchelItem.bakeModels();

        if(entityIn.itemHandler.getStackInSlot(1).is(HexereiTags.Items.ALL_SATCHELS)) {
            if(satchelStack.getItem() instanceof BroomAttachmentItem satchelItem && satchelItem.model != null){

                Model satchelModel = satchelItem.model;
                VertexConsumer vertexConsumer = bufferIn.getBuffer(satchelModel.renderType(satchelItem.texture));
                satchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                if(satchelItem.dye_texture != null){
                    if (SatchelItem.getDyeColorNamed(satchelStack) != null) {
                        float[] afloat = SatchelItem.getDyeColorNamed(satchelStack).getTextureDiffuseColors();
                        VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(satchelItem.dye_texture), false, false);
                        satchelModel.renderToBuffer(matrixStackIn, vertexConsumerDye, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                    } else {
                        int col = SatchelItem.getColorStatic(satchelStack);
                        int i = (col & 16711680) >> 16;
                        int j = (col & '\uff00') >> 8;
                        int k = (col & 255);
                        float[] afloat = new float[]{(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
                        VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(satchelItem.dye_texture), false, false);
                        satchelModel.renderToBuffer(matrixStackIn, vertexConsumerDye, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                    }
                }
            }
        }


        ItemStack miscStack = entityIn.itemHandler.getStackInSlot(0);
        if(miscStack.getItem() instanceof BroomAttachmentItem miscItem)
            if(miscItem.model == null)
                miscItem.bakeModels();
        if(miscStack.is(HexereiTags.Items.BROOM_MISC)) {
            if(miscStack.getItem() instanceof KeychainItem keychainItem && keychainItem.model != null)
            {

                Model broomKeychainModel = keychainItem.model;
                VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomKeychainModel.renderType(keychainItem.texture));
                broomKeychainModel.renderToBuffer(matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                matrixStackIn.translate(-19/16f, 0, -0.4f/16f);

                matrixStackIn.translate(0, 2.75, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                matrixStackIn.translate(0, 1.3, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));


                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-(float)entityIn.getDeltaMovement().y() * 20f));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)(Math.atan2(entityIn.getDeltaMovement().z(), entityIn.getDeltaMovement().x())/(2*Math.PI)*360) - entityYaw));

                if(entityIn.selfItem != null && entityIn.selfItem.hasTag() && Hexerei.proxy.getPlayer().getMainHandItem().hasTag() && Hexerei.proxy.getPlayer().getMainHandItem().getTag().equals(entityIn.selfItem.getTag()) && Minecraft.getInstance().options.getCameraType().isFirstPerson())
                {

                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees((Hexerei.proxy.getPlayer().yHeadRot - Hexerei.proxy.getPlayer().yHeadRotO) * 1.5f));

                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.clamp((float)Hexerei.proxy.getPlayer().getDeltaMovement().yRot(-90).dot(Hexerei.proxy.getPlayer().getLookAngle()) * -125f, -70, 70)));

                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)(Hexerei.proxy.getPlayer().getLookAngle().y * -50f) - 50f));

                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.clamp((float)Hexerei.proxy.getPlayer().getDeltaMovement().dot(Hexerei.proxy.getPlayer().getLookAngle()) * -125f, -70, 70)));

                }
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((float)Mth.length(entityIn.getDeltaMovement().x(), entityIn.getDeltaMovement().z())) * 50f));



                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.translate(0, -1.3, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                matrixStackIn.translate(0, -2.7 - 1.5/16f, 0);


                Model broomKeychainChainModel = keychainItem.chain_resources.getSecond();
                VertexConsumer ivertexbuilderChain = bufferIn.getBuffer(broomKeychainChainModel.renderType(keychainItem.chain_resources.getFirst()));
                broomKeychainChainModel.renderToBuffer(matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);


                matrixStackIn.translate(0,  1+11.5/16f, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                if(entityIn.selfItem != null && entityIn.selfItem.hasTag() && Hexerei.proxy.getPlayer().getMainHandItem().hasTag() && Hexerei.proxy.getPlayer().getMainHandItem().getTag().equals(entityIn.selfItem.getTag()) && Minecraft.getInstance().options.getCameraType().isFirstPerson())
                {
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)(Hexerei.proxy.getPlayer().getLookAngle().y * 20f)));
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.clamp((float)Hexerei.proxy.getPlayer().getDeltaMovement().dot(Hexerei.proxy.getPlayer().getLookAngle()) * 50f, -20, 20)));
                }
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((float)Mth.length(entityIn.getDeltaMovement().x(), entityIn.getDeltaMovement().z())) * -20f));



                NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
                if(miscStack.hasTag())
                    ContainerHelper.loadAllItems(miscStack.getTag(), items);
                if(items.get(0).getItem() instanceof BroomItem) {
                    matrixStackIn.scale(0.45f, 0.45f, 0.45f);
                }
                else
                    matrixStackIn.scale(0.25f,0.25f,0.25f);
                renderItem(items.get(0), partialTicks, matrixStackIn, bufferIn, packedLightIn);

            }
            else if(miscStack.is(ModItems.BROOM_NETHERITE_TIP.get()))
            {
                if(miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
                    int light = packedLightIn / 15 * (15 - (int) (8 * (((miscStack.getDamageValue()) / (float) miscStack.getMaxDamage()))));

                    Model miscModel = miscItem.model;
                    VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
                    miscModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                    matrixStackIn.translate(-22 / 16f, 0, -0.4f / 16f);

                    matrixStackIn.translate(0, 2.68, 0);
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                    matrixStackIn.translate(0, 1.3, 0);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                    matrixStackIn.scale(0.3f, 0.3f, 0.3f);

                    renderItem(new ItemStack(ModItems.SELENITE_SHARD.get()), partialTicks, matrixStackIn, bufferIn, light);
                }
            }
            else if(miscStack.is(ModItems.BROOM_WATERPROOF_TIP.get()))
            {
                if(miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
                    int light = packedLightIn / 15 * (15 - (int) (8 * (((miscStack.getDamageValue()) / (float) miscStack.getMaxDamage()))));

                    Model miscModel = miscItem.model;
                    VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
                    miscModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                    matrixStackIn.translate(-22/16f, 0, -0.4f/16f);

                    matrixStackIn.translate(0, 2.68, 0);
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                    matrixStackIn.translate(0, 1.3, 0);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                    matrixStackIn.scale(0.3f,0.3f,0.3f);

                    renderItem(new ItemStack(Items.CONDUIT), partialTicks, matrixStackIn, bufferIn, light);
                }
            }
            else
            {

                if(miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
                    Model miscModel = miscItem.model;
                    VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
                    miscModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                    if(miscItem.dye_texture != null){
                        if (SatchelItem.getDyeColorNamed(satchelStack) != null) {
                            float[] afloat = SatchelItem.getDyeColorNamed(satchelStack).getTextureDiffuseColors();
                            VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(miscItem.dye_texture), false, false);
                            miscModel.renderToBuffer(matrixStackIn, vertexConsumerDye, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                        } else {
                            int col = SatchelItem.getColorStatic(satchelStack);
                            int i = (col & 16711680) >> 16;
                            int j = (col & '\uff00') >> 8;
                            int k = (col & 255);
                            float[] afloat = new float[]{(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
                            VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(miscItem.dye_texture), false, false);
                            miscModel.renderToBuffer(matrixStackIn, vertexConsumerDye, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                        }
                    }
                }
            }
        }

        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void renderItem(ItemStack stack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }
}