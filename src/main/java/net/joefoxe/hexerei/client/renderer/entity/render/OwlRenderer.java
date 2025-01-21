package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.OwlModel;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.Map;

public class OwlRenderer extends MobRenderer<OwlEntity, OwlModel<OwlEntity>> {
    public static final ResourceLocation TEXTURE = HexereiUtil.getResource(Hexerei.MOD_ID, "textures/entity/owl.png");

    private static final Map<OwlVariant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(OwlVariant.class), (p_114874_) -> {
        p_114874_.put(OwlVariant.GREAT_HORNED, HexereiUtil.getResource("textures/entity/owl.png"));
        p_114874_.put(OwlVariant.BARN, HexereiUtil.getResource("textures/entity/owl_barn.png"));
        p_114874_.put(OwlVariant.BARRED, HexereiUtil.getResource("textures/entity/owl_barred.png"));
        p_114874_.put(OwlVariant.SNOWY, HexereiUtil.getResource("textures/entity/owl_snowy.png"));
    });
    public OwlRenderer(Context erm) {

        super(erm, new OwlModel<>(erm.bakeLayer(OwlModel.LAYER_LOCATION)), 0.25f);

        this.addLayer(new LayerOwlItem(this));
        this.addLayer(new LayerOwlCollar(this));
        this.addLayer(new LayerOwlHelmet(this, erm));
    }


    @Override
    public ResourceLocation getTextureLocation(OwlEntity pEntity) {
        return LOCATION_BY_VARIANT.get(pEntity.getVariant());
    }

    @Override
    public void render(OwlEntity owlEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(owlEntity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    @Override
    protected void scale(OwlEntity entitylivingbaseIn, PoseStack poseStack, float partialTickTime) {
        float f = 1;
        if (entitylivingbaseIn.isBaby()) {
            f *= 0.5f;
            this.shadowRadius = 0.125F;
        } else {
            this.shadowRadius = 0.25F;
        }

        poseStack.scale(f, f, f);
    }

    public static class LayerOwlItem extends RenderLayer<OwlEntity, OwlModel<OwlEntity>> {

        public LayerOwlItem(OwlRenderer render) {
            super(render);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, OwlEntity owl, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = owl.getItem(1);
            poseStack.pushPose();
            translateToHand(poseStack);
            poseStack.translate(0, 0.075F, 0.05);
            if(itemstack.getItem() instanceof BroomItem)
                poseStack.translate(0.1f, 0.12f, 0.06F);
            if(owl.isBaby()){
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(-2.5F));
            if(itemstack.getItem() instanceof BroomItem)
                poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-35F));
            poseStack.scale(0.75F, 0.75F, 0.75F);
            ItemStack stack = itemstack.copy();

            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(owl, stack, ItemDisplayContext.GROUND, false, poseStack, bufferIn, packedLightIn);
            poseStack.popPose();

            if (owl.messagingController.hasDelivery() && !owl.messagingController.getMessageStack().isEmpty()) {
                if (owl.messagingController.getMessageStack().getItem() == ModItems.COURIER_LETTER.get()){
                    poseStack.pushPose();
                    translateToFeet(poseStack);
                    poseStack.translate(0f, 0.1f, 0f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, owl.itemHeldSwingLast, owl.itemHeldSwing) * 1.5f));
                    poseStack.translate(-0.42f, 0.0f, -0.65f);
                    poseStack.scale(1, 1, 1);

                    renderBlock(poseStack, bufferIn, packedLightIn, ModBlocks.COURIER_LETTER.get().defaultBlockState());
                    poseStack.popPose();
                }

                if (owl.messagingController.getMessageStack().getItem() == ModItems.COURIER_PACKAGE.get()) {
                    poseStack.pushPose();
                    translateToFeet(poseStack);
                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
                    poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, owl.itemHeldSwingLast, owl.itemHeldSwing) * 1.5f));
                    poseStack.translate(0.57f, 0.3f, -0.20f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                    poseStack.scale(1, 1, 1);

                    renderBlock(poseStack, bufferIn, packedLightIn, ModBlocks.COURIER_PACKAGE.get().defaultBlockState());
                    poseStack.popPose();
                }
            }
//            }
        }

        private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").getChild("beak").translateAndRotate(matrixStack);

        }

        protected void translateToFeet(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("rightLeg").translateAndRotate(matrixStack);

        }
    }


    public static class LayerOwlCollar extends RenderLayer<OwlEntity, OwlModel<OwlEntity>> {
        private static final ResourceLocation OWL_DYE_LOCATION = HexereiUtil.getResource("textures/entity/owl_dye.png");

        public LayerOwlCollar(RenderLayerParent<OwlEntity, OwlModel<OwlEntity>> p_117707_) {
            super(p_117707_);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, OwlEntity entity, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            if (entity.isTame() && !entity.isInvisible() && (entity.getDyeColorId() != -1 || entity.getName().getString().equals("jeb_") || entity.getName().getString().equals("joe_"))) {
                float[] afloat = HexereiUtil.rgbIntToFloatArray(entity.getDyeColor().getTextureDiffuseColor());
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(OWL_DYE_LOCATION), false, false);
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F));

            }
        }
    }


    public static class LayerOwlHelmet extends RenderLayer<OwlEntity, OwlModel<OwlEntity>>{

        private final RenderLayerParent<OwlEntity, OwlModel<OwlEntity>>renderer;
        private final HumanoidModel<?> defaultBipedModel;
        private final TextureAtlas armorTrimAtlas;

        public LayerOwlHelmet(OwlRenderer renderer, Context renderManagerIn){
            super(renderer);
            this.renderer = renderer;
            defaultBipedModel = new HumanoidModel<>(renderManagerIn.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
            this.armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);

        }

        private void renderTrim(
                Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, net.minecraft.client.model.Model p_289663_, boolean p_289651_) {
            TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(p_289651_ ? p_289692_.innerTexture(p_323506_) : p_289692_.outerTexture(p_323506_));
            VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet(p_289692_.pattern().value().decal())));
            p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY);
        }


        private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, net.minecraft.client.model.Model p_289659_) {
            p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, OwlEntity owl, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

            poseStack.pushPose();
            ItemStack itemstack = owl.itemHandler.getStackInSlot(0);
            if (itemstack.getItem() instanceof ArmorItem armoritem) {


                EquipmentSlot pSlot = armoritem.getEquipmentSlot();
                HumanoidModel<?> a = defaultBipedModel;
                a = getArmorModelHook(owl, itemstack, EquipmentSlot.HEAD, a);
                a.setAllVisible(false);
                a.hat.visible = true;
                a.head.visible = true;
                translateToHead(poseStack);

                poseStack.scale(0.65F, 0.65F, 0.65F);
                poseStack.translate(0f,  0.15F, -0.05F);

                Model model = ClientHooks.getArmorModel(owl, itemstack, pSlot, a);
                ArmorMaterial armormaterial = armoritem.getMaterial().value();
                boolean flag1 = itemstack.hasFoil();
                IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
                extensions.setupModelAnimations(owl, itemstack, EquipmentSlot.HEAD, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);

                for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                    ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
                    int j = extensions.getArmorLayerTintColor(itemstack, owl, armormaterial$layer, layerIdx, -1);
                    ResourceLocation texture = ClientHooks.getArmorTexture(owl, itemstack, armormaterial$layer, false, pSlot);
                    renderHelmet(poseStack, bufferIn, packedLightIn, flag1, a, j, texture);
                }

                ArmorTrim armortrim = itemstack.get(DataComponents.TRIM);
                if (armortrim != null) {
                    this.renderTrim(armoritem.getMaterial(), poseStack, bufferIn, packedLightIn, armortrim, model, false);
                }

                if (itemstack.hasFoil()) {
                    this.renderGlint(poseStack, bufferIn, packedLightIn, model);
                }

            }
            else if((Block.byItem(itemstack.getItem())) instanceof AbstractSkullBlock)
            {
                translateToHand(poseStack);
                poseStack.scale(0.45F, 0.45F, 0.45F);
                poseStack.translate(0f, -0.25F, -0.2F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                renderItem(itemstack, owl.level(), poseStack, bufferIn, packedLightIn);
            }

            poseStack.popPose();

        }

        private void renderItem(ItemStack stack, Level level, PoseStack poseStack, MultiBufferSource bufferIn,
                                int combinedLightIn) {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                    OverlayTexture.NO_OVERLAY, poseStack, bufferIn, level, 1);
        }

        private void translateToHead(PoseStack poseStack) {
            translateToChest(poseStack);
            this.renderer.getModel().head.translateAndRotate(poseStack);
        }

        private void translateToChest(PoseStack poseStack) {
            this.renderer.getModel().owl.translateAndRotate(poseStack);
        }

        private void renderHelmet(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, int color, ResourceLocation armorResource) {
            VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
            renderer.getModel().copyPropertiesTo(modelIn);
            modelIn.head.xRot = 0F;
            modelIn.head.yRot = 0F;
            modelIn.head.zRot = 0F;
            modelIn.hat.xRot = 0F;
            modelIn.hat.yRot = 0F;
            modelIn.hat.zRot = 0F;
            modelIn.head.x = 0F;
            modelIn.head.y = 0F;
            modelIn.head.z = 0F;
            modelIn.hat.x = 0F;
            modelIn.hat.y = 0F;
            modelIn.hat.z = 0F;
            modelIn.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, color);

        }

//        public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
//            ArmorItem item = (ArmorItem)stack.getItem();
//            String texture = item.getMaterial().getName();
//            String domain = "minecraft";
//            int idx = texture.indexOf(':');
//            if (idx != -1) {
//                domain = texture.substring(0, idx);
//                texture = texture.substring(idx + 1);
//            }
//            String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (1), type == null ? "" : String.format("_%s", type));
//
//            s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
//            ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
//
//            if (resourcelocation == null) {
//                resourcelocation = new ResourceLocation(s1);
//                ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
//            }
//
//            return resourcelocation;
//        }
//
//        protected void setModelSlotVisible(HumanoidModel humanoidModel, EquipmentSlot slotIn) {
//            this.setModelVisible(humanoidModel);
//            switch (slotIn) {
//                case HEAD -> humanoidModel.head.visible = true;
//                case CHEST -> {
//                    humanoidModel.body.visible = true;
//                    humanoidModel.rightArm.visible = true;
//                    humanoidModel.leftArm.visible = true;
//                }
//                case LEGS -> {
//                    humanoidModel.body.visible = true;
//                    humanoidModel.rightLeg.visible = true;
//                    humanoidModel.leftLeg.visible = true;
//                }
//                case FEET -> {
//                    humanoidModel.rightLeg.visible = true;
//                    humanoidModel.leftLeg.visible = true;
//                }
//            }
//        }
//
//        protected void setModelVisible(HumanoidModel model) {
//            model.setAllVisible(false);
//
//        }

        protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
            Model basicModel = ClientHooks.getArmorModel(entity, itemStack, slot, model);
            return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").translateAndRotate(matrixStack);

        }

    }

}