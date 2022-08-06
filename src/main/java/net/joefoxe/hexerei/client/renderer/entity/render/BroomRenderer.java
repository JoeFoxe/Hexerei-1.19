package net.joefoxe.hexerei.client.renderer.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.*;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
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
import com.mojang.math.Vector3f;
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
    protected static final ResourceLocation WILLOW_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/willow_broom.png");
    protected static final ResourceLocation HERB_BRUSH_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/herb_enhanced_brush.png");
    protected static final ResourceLocation SATCHEL_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel.png");
    protected static final ResourceLocation SATCHEL_TEXTURE_DYE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel_dye.png");
    protected static final ResourceLocation SATCHEL_SMALL_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_small_satchel.png");
    protected static final ResourceLocation SATCHEL_SMALL_TEXTURE_DYE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_small_satchel_dye.png");
    protected static final ResourceLocation SATCHEL_LARGE_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_large_satchel.png");
    protected static final ResourceLocation SATCHEL_LARGE_TEXTURE_DYE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_large_satchel_dye.png");
    protected static final ResourceLocation KEYCHAIN_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_keychain.png");
    protected static final ResourceLocation NETHERITE_TIP_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_netherite_tip.png");
    protected static final ResourceLocation WATERPROOF_TIP_TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_waterproof_tip.png");
    private final Pair<ResourceLocation, BroomModel> broomResources;
    private final Pair<ResourceLocation, BroomStickBaseModel> broomStickResources;
    private final Pair<ResourceLocation, BroomBrushBaseModel> broomBrushResources;
    private final Pair<ResourceLocation, BroomRingsModel> broomRingsResources;
    private final Pair<ResourceLocation, BroomSmallSatchelModel> broomSmallSatchelResources;
    private final Pair<ResourceLocation, BroomMediumSatchelModel> broomMediumSatchelResources;
    private final Pair<ResourceLocation, BroomLargeSatchelModel> broomLargeSatchelResources;
    private final Pair<ResourceLocation, BroomKeychainModel> broomKeychainResources;
    private final Pair<ResourceLocation, BroomNetheriteTipModel> broomNetheriteTipResources;
    private final Pair<ResourceLocation, BroomWaterproofTipModel> broomWaterproofTipResources;
    private final Pair<ResourceLocation, BroomKeychainChainModel> broomKeychainChainResources;
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/power_layer_light.png");
    private final BroomStickBaseModel broomPowerModel;
    private final BroomBrushBaseModel broomBrushPowerModel;

    public BroomRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.0F;
        this.broomResources = Pair.of(TEXTURE, new BroomModel(context.bakeLayer(BroomModel.LAYER_LOCATION)));
        this.broomRingsResources = Pair.of(TEXTURE, new BroomRingsModel(context.bakeLayer(BroomRingsModel.LAYER_LOCATION)));
        this.broomSmallSatchelResources = Pair.of(SATCHEL_SMALL_TEXTURE, new BroomSmallSatchelModel(context.bakeLayer(BroomSmallSatchelModel.LAYER_LOCATION)));
        this.broomMediumSatchelResources = Pair.of(SATCHEL_TEXTURE, new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION)));
        this.broomLargeSatchelResources = Pair.of(SATCHEL_LARGE_TEXTURE, new BroomLargeSatchelModel(context.bakeLayer(BroomLargeSatchelModel.LAYER_LOCATION)));
        this.broomStickResources = Pair.of(TEXTURE, new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION)));
        this.broomBrushResources = Pair.of(TEXTURE, new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION)));
        this.broomKeychainResources = Pair.of(KEYCHAIN_TEXTURE, new BroomKeychainModel(context.bakeLayer(BroomKeychainModel.LAYER_LOCATION)));
        this.broomKeychainChainResources = Pair.of(KEYCHAIN_TEXTURE, new BroomKeychainChainModel(context.bakeLayer(BroomKeychainChainModel.LAYER_LOCATION)));
        this.broomNetheriteTipResources = Pair.of(NETHERITE_TIP_TEXTURE, new BroomNetheriteTipModel(context.bakeLayer(BroomNetheriteTipModel.LAYER_LOCATION)));
        this.broomWaterproofTipResources = Pair.of(WATERPROOF_TIP_TEXTURE, new BroomWaterproofTipModel(context.bakeLayer(BroomWaterproofTipModel.LAYER_LOCATION)));
        this.broomPowerModel = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
        this.broomBrushPowerModel = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.POWER_LAYER_LOCATION));


    }

    private float moveTo(float input, float moveTo, float speed)
    {
        float distance = moveTo - input;

        if(Math.abs(distance) <= speed)
        {
            return moveTo;
        }

        if(distance > 0)
        {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }
    @Override
    public ResourceLocation getTextureLocation(BroomEntity p_114482_) {
        return TEXTURE;
    }

    public void render(BroomEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
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
        BroomModel broomModel = broomResources.getSecond();
        BroomStickBaseModel broomStickModel = broomStickResources.getSecond();
        broomModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        broomStickModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        ResourceLocation loc = TEXTURE;
        if(entityIn.getBroomType() == BroomEntity.Type.WILLOW)
            loc = WILLOW_TEXTURE;
        VertexConsumer ivertexbuilderStick = bufferIn.getBuffer(broomStickModel.renderType(loc));
        broomStickModel.renderToBuffer(matrixStackIn, ivertexbuilderStick, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if(BroomEntity.getDyeColorNamed(entityIn) != null) {
            DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
            float[] afloat = new float[] {1, 1, 1};
            if(dyeColor != null)
                afloat = BroomEntity.getDyeColorNamed(entityIn).getTextureDiffuseColors();
            float offset = Hexerei.getClientTicks();
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
            this.broomPowerModel.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);

        }

        if(entityIn.itemHandler.getStackInSlot(2).is(HexereiTags.Items.BROOM_BRUSH)) {

            if(entityIn.itemHandler.getStackInSlot(2).is(ModItems.HERB_ENHANCED_BROOM_BRUSH.get())) {
                BroomBrushBaseModel broomBrushModel = broomBrushResources.getSecond();
                broomBrushModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderBrush = bufferIn.getBuffer(broomBrushModel.renderType(HERB_BRUSH_TEXTURE));
                broomBrushModel.renderToBuffer(matrixStackIn, ivertexbuilderBrush, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }else {
                BroomBrushBaseModel broomBrushModel = broomBrushResources.getSecond();
                broomBrushModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderBrush = bufferIn.getBuffer(broomBrushModel.renderType(TEXTURE));
                broomBrushModel.renderToBuffer(matrixStackIn, ivertexbuilderBrush, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if(entityIn.hasCustomName() && BroomEntity.getDyeColorNamed(entityIn) != null) {


                DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
                float[] afloat = new float[] {1, 1, 1};
                if(dyeColor != null)
                    afloat = BroomEntity.getDyeColorNamed(entityIn).getTextureDiffuseColors();
                float offset = Hexerei.getClientTicks();
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, (offset * 0.01F) % 1.0F, offset * 0.01F % 1.0F));
                this.broomBrushPowerModel.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
            }

        }


        if(entityIn.itemHandler.getStackInSlot(1).is(HexereiTags.Items.SMALL_SATCHELS)) {
            ItemStack stack = entityIn.itemHandler.getStackInSlot(1);
            BroomSmallSatchelModel broomSmallSatchelModel = broomSmallSatchelResources.getSecond();
            broomSmallSatchelModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
            VertexConsumer ivertexbuilderSatchel = bufferIn.getBuffer(broomSmallSatchelModel.renderType(SATCHEL_SMALL_TEXTURE));
            broomSmallSatchelModel.renderToBuffer(matrixStackIn, ivertexbuilderSatchel, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);



            if(SatchelItem.getDyeColorNamed(stack) != null) {
                float[] afloat = SatchelItem.getDyeColorNamed(stack).getTextureDiffuseColors();
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_SMALL_TEXTURE_DYE), false, false);
                broomSmallSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
            }
            else
            {
                if(stack.getItem() instanceof SatchelItem){
                    int col = SatchelItem.getColorStatic(stack);
                    int i = (col & 16711680) >> 16;
                    int j = (col & '\uff00') >> 8;
                    int k = (col & 255);
                    float afloat[] = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
                    VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_SMALL_TEXTURE_DYE), false, false);
                    broomSmallSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }

            }

        }
        if(entityIn.itemHandler.getStackInSlot(1).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
            ItemStack stack = entityIn.itemHandler.getStackInSlot(1);
            BroomMediumSatchelModel broomMediumSatchelModel = broomMediumSatchelResources.getSecond();
            broomMediumSatchelModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
            VertexConsumer ivertexbuilderSatchel = bufferIn.getBuffer(broomMediumSatchelModel.renderType(SATCHEL_TEXTURE));
            broomMediumSatchelModel.renderToBuffer(matrixStackIn, ivertexbuilderSatchel, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            if(SatchelItem.getDyeColorNamed(stack) != null) {
                float[] afloat = SatchelItem.getDyeColorNamed(stack).getTextureDiffuseColors();
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_TEXTURE_DYE), false, false);
                broomMediumSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
            }
            else
            {
                if(stack.getItem() instanceof SatchelItem){
                    int col = SatchelItem.getColorStatic(stack);
                    int i = (col & 16711680) >> 16;
                    int j = (col & '\uff00') >> 8;
                    int k = (col & 255);
                    float afloat[] = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
                    VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_TEXTURE_DYE), false, false);
                    broomMediumSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }

            }


        }
        if(entityIn.itemHandler.getStackInSlot(1).is(HexereiTags.Items.LARGE_SATCHELS)) {
            ItemStack stack = entityIn.itemHandler.getStackInSlot(1);
            BroomLargeSatchelModel broomLargeSatchelModel = broomLargeSatchelResources.getSecond();
            broomLargeSatchelModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
            VertexConsumer ivertexbuilderSatchel = bufferIn.getBuffer(broomLargeSatchelModel.renderType(SATCHEL_LARGE_TEXTURE));
            broomLargeSatchelModel.renderToBuffer(matrixStackIn, ivertexbuilderSatchel, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            if(SatchelItem.getDyeColorNamed(stack) != null) {
                float[] afloat = SatchelItem.getDyeColorNamed(stack).getTextureDiffuseColors();
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_LARGE_TEXTURE_DYE), false, false);
                broomLargeSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
            }
            else
            {
                if(stack.getItem() instanceof SatchelItem){
                    int col = SatchelItem.getColorStatic(stack);
                    int i = (col & 16711680) >> 16;
                    int j = (col & '\uff00') >> 8;
                    int k = (col & 255);
                    float afloat[] = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
                    VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(SATCHEL_LARGE_TEXTURE_DYE), false, false);
                    broomLargeSatchelModel.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }

            }

        }


        if(entityIn.itemHandler.getStackInSlot(0).is(HexereiTags.Items.BROOM_MISC)) {
            if(entityIn.itemHandler.getStackInSlot(0).getItem() == ModItems.BROOM_KEYCHAIN.get())
            {

                BroomKeychainModel broomKeychainModel = broomKeychainResources.getSecond();
                broomKeychainModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomKeychainModel.renderType(broomKeychainResources.getFirst()));
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


                BroomKeychainChainModel broomKeychainChainModel = broomKeychainChainResources.getSecond();
                broomKeychainChainModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderChain = bufferIn.getBuffer(broomKeychainChainModel.renderType(broomKeychainChainResources.getFirst()));
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
                if(entityIn.itemHandler.getStackInSlot(0).hasTag())
                    ContainerHelper.loadAllItems(entityIn.itemHandler.getStackInSlot(0).getTag(), items);
                if(items.get(0).getItem() instanceof BroomItem) {
                    matrixStackIn.scale(0.45f, 0.45f, 0.45f);
                }
                else
                    matrixStackIn.scale(0.25f,0.25f,0.25f);
                renderItem(items.get(0), partialTicks, matrixStackIn, bufferIn, packedLightIn);

            }
            else if(entityIn.itemHandler.getStackInSlot(0).is(ModItems.BROOM_NETHERITE_TIP.get()))
            {
                int light = (int)(packedLightIn / 15 * (15 - (int)(8 * (((entityIn.itemHandler.getStackInSlot(0).getDamageValue()) / (float)entityIn.itemHandler.getStackInSlot(0).getMaxDamage())))));

                BroomNetheriteTipModel broomNetheriteTipModel = broomNetheriteTipResources.getSecond();
                broomNetheriteTipModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomNetheriteTipModel.renderType(broomNetheriteTipResources.getFirst()));
                broomNetheriteTipModel.renderToBuffer(matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                matrixStackIn.translate(-22/16f, 0, -0.4f/16f);

                matrixStackIn.translate(0, 2.68, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                matrixStackIn.translate(0, 1.3, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                matrixStackIn.scale(0.3f,0.3f,0.3f);

                renderItem(new ItemStack(ModItems.SELENITE_SHARD.get()), partialTicks, matrixStackIn, bufferIn, light);
            }
            else if(entityIn.itemHandler.getStackInSlot(0).is(ModItems.BROOM_WATERPROOF_TIP.get()))
            {
                int light = (int)(packedLightIn / 15 * (15 - (int)(8 * (((entityIn.itemHandler.getStackInSlot(0).getDamageValue()) / (float)entityIn.itemHandler.getStackInSlot(0).getMaxDamage())))));

                BroomWaterproofTipModel broomWaterproofTipModel = broomWaterproofTipResources.getSecond();
                broomWaterproofTipModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomWaterproofTipModel.renderType(broomWaterproofTipResources.getFirst()));
                broomWaterproofTipModel.renderToBuffer(matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                matrixStackIn.translate(-22/16f, 0, -0.4f/16f);

                matrixStackIn.translate(0, 2.68, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                matrixStackIn.translate(0, 1.3, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                matrixStackIn.scale(0.3f,0.3f,0.3f);

                renderItem(new ItemStack(Items.CONDUIT), partialTicks, matrixStackIn, bufferIn, light);
            }
            else
            {
                BroomRingsModel broomRingsModel = broomRingsResources.getSecond();
                broomRingsModel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomRingsModel.renderType(broomRingsResources.getFirst()));
                broomRingsModel.renderToBuffer(matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        if (!entityIn.canSwim()) {
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RenderType.waterMask());
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