package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.CrowModel;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Map;

public class CrowRenderer extends MobRenderer<CrowEntity, CrowModel<CrowEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/crow.png");
    private static final ResourceLocation CROW_COLLAR_LOCATION = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/crow_collar.png");
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final Pair<ResourceLocation, CrowModel<CrowEntity>> crowResources;
    public CrowRenderer(Context erm) {

        super(erm, new CrowModel<>(erm.bakeLayer(CrowModel.LAYER_LOCATION)), 0.25f);

        this.crowResources = Pair.of(TEXTURE, new CrowModel<>(erm.bakeLayer(CrowModel.LAYER_LOCATION)));

        this.addLayer(new LayerCrowItem(this));
        this.addLayer(new LayerCrowCollar(this));
        this.addLayer(new LayerCrowHelmet(this, erm));
        this.addLayer(new CrowPowerLayer(this, erm.getModelSet()));
    }


    @Override
    public ResourceLocation getTextureLocation(CrowEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(CrowEntity crowEntity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

//        if (crowEntity.isTame() && !crowEntity.isInvisible() && crowEntity.getDyeColorId() != -1) {
//            float[] afloat = crowEntity.getDyeColor().getTextureDiffuseColors();
//            this.model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityDecal(CROW_COLLAR_LOCATION)), packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0f);
//        }

        super.render(crowEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);


//        if (!crowEntity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
//        {
////            matrixStackIn.pushPose();
//            renderItem(crowEntity.getItemInHand(InteractionHand.MAIN_HAND), partialTicks, matrixStackIn, bufferIn, packedLightIn);
////            matrixStackIn.popPose();
//        }

//        if(p_115455_.level.isClientSide)
//            System.out.println(p_115455_.headTiltTimer);
    }

    private void renderItem(ItemStack stack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    @Override
    protected void scale(CrowEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 1;
        if (entitylivingbaseIn.isBaby()) {
            f *= 0.5f;
            this.shadowRadius = 0.125F;
        } else {
            this.shadowRadius = 0.25F;
        }

        matrixStackIn.scale(f, f, f);
    }

    public class LayerCrowItem extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {

        public LayerCrowItem(CrowRenderer render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CrowEntity crow, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = crow.getItem(1);
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(0, -0.065F, -0.265F);
            if(itemstack.getItem() instanceof BroomItem)
                matrixStackIn.translate(0.1f, 0.16f, 0.01F);
            if(crow.isBaby()){
                matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            }
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-2.5F));
            if(itemstack.getItem() instanceof BroomItem)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            ItemStack stack = itemstack.copy();
//            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            if(itemstack.getItem() == ModItems.WARHAMMER.get() && crow.getDisplayName().getString().equals("Thor") && !itemstack.isEnchanted())
                stack = EnchantmentHelper.enchantItem(RandomSource.create(), itemstack.copy(), 1, false);

            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().body.getChild("head").translateAndRotate(matrixStack);

        }
    }


    @OnlyIn(Dist.CLIENT)
    public class LayerCrowCollar extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
        private static final ResourceLocation CROW_COLLAR_LOCATION = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/crow_collar.png");

        public LayerCrowCollar(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_117707_) {
            super(p_117707_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CrowEntity entity, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            if (entity.isTame() && !entity.isInvisible() && (entity.getDyeColorId() != -1 || entity.getName().getString().equals("jeb_") || entity.getName().getString().equals("joe_"))) {
                float[] afloat = entity.getDyeColor().getTextureDiffuseColors();
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(CROW_COLLAR_LOCATION), false, false);
                this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);

            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class CrowPowerLayer extends EnergySwirlLayer<CrowEntity, CrowModel<CrowEntity>> {
        private final CrowModel<CrowEntity> model;

        public CrowPowerLayer(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_174471_, EntityModelSet p_174472_) {
            super(p_174471_);
            this.model = new CrowModel<>(p_174472_.bakeLayer(CrowModel.POWER_LAYER_LOCATION));
        }

        protected float xOffset(float p_116683_) {
            return p_116683_ * 0.01F;
        }

        protected ResourceLocation getTextureLocation() {
            return POWER_LOCATION;
        }

        protected EntityModel<CrowEntity> model() {
            return this.model;
        }
    }


    public class LayerCrowHelmet extends RenderLayer<CrowEntity, CrowModel<CrowEntity>>{

        private final RenderLayerParent<CrowEntity, CrowModel<CrowEntity>>renderer;
        private final HumanoidModel defaultBipedModel;

        public LayerCrowHelmet(CrowRenderer renderer, EntityRendererProvider.Context renderManagerIn){
            super(renderer);
            this.renderer = renderer;
            defaultBipedModel = new HumanoidModel(renderManagerIn.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));

        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CrowEntity crow, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {

            matrixStackIn.pushPose();
            ItemStack itemstack = crow.itemHandler.getStackInSlot(0);
            if (itemstack.getItem() instanceof ArmorItem) {
                ArmorItem armoritem = (ArmorItem) itemstack.getItem();

                HumanoidModel a = getArmorModelHook(crow, itemstack, EquipmentSlot.HEAD, defaultBipedModel);
//                    this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                boolean flag1 = itemstack.hasFoil();
                boolean notAVanillaModel = a != defaultBipedModel;
                translateToHand(matrixStackIn);
                if(notAVanillaModel && Registry.ITEM.getKey(itemstack.getItem()).getNamespace().equals(Hexerei.MOD_ID)) {
                    matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                    matrixStackIn.translate(0f,  -0.15F, -0.25F);
                }
                else {
                    matrixStackIn.scale(0.48F, 0.48F, 0.48F);
                    matrixStackIn.translate(0f, -0.825F, -0.2F);
                }

                if (armoritem instanceof DyeableLeatherItem) {
                    int i = ((DyeableLeatherItem) armoritem).getColor(itemstack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    renderArmor(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, f, f1, f2, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, null));
                    renderArmor(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, "overlay"));
                } else
                renderArmor(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, null));
            }
            else if((Block.byItem(itemstack.getItem())) instanceof AbstractSkullBlock)
            {
                translateToHand(matrixStackIn);
                matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                matrixStackIn.translate(0f, -0.2F, -0.2F);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));
                renderItem(itemstack, matrixStackIn, bufferIn, packedLightIn);
            }

            matrixStackIn.popPose();



        }
        private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

        }

        private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                                int combinedLightIn) {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                    OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
        }

        private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

        private void renderArmor(CrowEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource) {
//            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(armorResource), false, glintIn);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
//            if(notAVanillaModel){
//                renderer.getModel().copyPropertiesTo(modelIn);
//                modelIn.body.y = 0;
//                modelIn.head.setPos(0.0F, 1.0F, 0.0F);
//                modelIn.hat.y = 0;
//                modelIn.head.xRot = renderer.getModel().body.xRot;
//                modelIn.head.yRot = renderer.getModel().body.yRot;
//                modelIn.head.zRot = renderer.getModel().body.zRot;
//                modelIn.head.x = renderer.getModel().body.x;
//                modelIn.head.y = renderer.getModel().body.y;
//                modelIn.head.z = renderer.getModel().body.z;
//                modelIn.hat.copyFrom(modelIn.head);
//                modelIn.body.copyFrom(modelIn.head);
//            }
            modelIn.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        }

        public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
            ArmorItem item = (ArmorItem)stack.getItem();
            String texture = item.getMaterial().getName();
            String domain = "minecraft";
            int idx = texture.indexOf(':');
            if (idx != -1)
            {
                domain = texture.substring(0, idx);
                texture = texture.substring(idx + 1);
            }
            String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (1), type == null ? "" : String.format("_%s", type));

            s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
            ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s1);

            if (resourcelocation == null)
            {
                resourcelocation = new ResourceLocation(s1);
                ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
            }

            return resourcelocation;
        }

        protected void setModelSlotVisible(HumanoidModel humanoidModel, EquipmentSlot slotIn) {
            this.setModelVisible(humanoidModel);
            switch (slotIn) {
                case HEAD:
                    humanoidModel.head.visible = true;
                    break;
                case CHEST:
                    humanoidModel.body.visible = true;
                    humanoidModel.rightArm.visible = true;
                    humanoidModel.leftArm.visible = true;
                    break;
                case LEGS:
                    humanoidModel.body.visible = true;
                    humanoidModel.rightLeg.visible = true;
                    humanoidModel.leftLeg.visible = true;
                    break;
                case FEET:
                    humanoidModel.rightLeg.visible = true;
                    humanoidModel.leftLeg.visible = true;
            }
        }

        protected void setModelVisible(HumanoidModel model) {
            model.setAllVisible(false);

        }

        protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
            Model basicModel = net.minecraftforge.client.ForgeHooksClient.getGenericArmorModel(entity, itemStack, slot, model);
            return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().body.getChild("head").translateAndRotate(matrixStack);

        }

    }

}