package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.CrowModel;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.AskForMapDataPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class CrowRenderer extends MobRenderer<CrowEntity, CrowModel<CrowEntity>> {
    public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/entity/crow.png");
    private static final ResourceLocation CROW_COLLAR_LOCATION = HexereiUtil.getResource("textures/entity/crow_collar.png");
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final HumanoidModel defaultBipedModel;
    public static Map<Item, ResourceLocation> TRINKET_LOCATION = Util.make(() ->{
        Map<Item, ResourceLocation> map = new HashMap<>();
        map.put(ModItems.CROW_ANKH_AMULET.get(), ResourceLocation.tryParse("hexerei:textures/item/crow_ankh_amulet_trinket.png"));
        return map;
    });

    private static final Map<CrowVariant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(CrowVariant.class), (p_114874_) -> {
        p_114874_.put(CrowVariant.BLACK, HexereiUtil.getResource("textures/entity/crow.png"));
        p_114874_.put(CrowVariant.HOODED, HexereiUtil.getResource("textures/entity/crow_hooded.png"));
        p_114874_.put(CrowVariant.NORTHWESTERN, HexereiUtil.getResource("textures/entity/crow_northwestern.png"));
        p_114874_.put(CrowVariant.PIED, HexereiUtil.getResource("textures/entity/crow_pied.png"));
        p_114874_.put(CrowVariant.ALBINO, HexereiUtil.getResource("textures/entity/crow_albino.png"));
        p_114874_.put(CrowVariant.GRAY, HexereiUtil.getResource("textures/entity/crow_gray.png"));
        p_114874_.put(CrowVariant.DARKBROWN, HexereiUtil.getResource("textures/entity/crow.png"));
    });
    private final Pair<ResourceLocation, CrowModel<CrowEntity>> crowResources;
    public CrowRenderer(Context erm) {

        super(erm, new CrowModel<>(erm.bakeLayer(CrowModel.LAYER_LOCATION)), 0.25f);

        this.crowResources = Pair.of(TEXTURE, new CrowModel<>(erm.bakeLayer(CrowModel.LAYER_LOCATION)));
        defaultBipedModel = new HumanoidModel(erm.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));

        this.addLayer(new LayerCrowItem(this));
        this.addLayer(new LayerCrowCollar(this));
        this.addLayer(new LayerCrowMisc(this));
        this.addLayer(new LayerCrowHelmet(this, erm));
        this.addLayer(new CrowPowerLayer(this, erm.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CrowEntity pEntity) {
        return LOCATION_BY_VARIANT.get(pEntity.getVariant());
    }
//    @Override
//    public ResourceLocation getTextureLocation(CrowEntity entity) {
//        return TEXTURE;
//    }

    @Override
    public void render(CrowEntity crowEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {

//        if (crowEntity.isTame() && !crowEntity.isInvisible() && crowEntity.getDyeColorId() != -1) {
//            float[] afloat = crowEntity.getDyeColor().getTextureDiffuseColors();
//            this.model.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityDecal(CROW_COLLAR_LOCATION)), packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0f);
//        }

        poseStack.pushPose();

//        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
//        poseStack.mulPose(Vector3f.YP.rotationDegrees(crowEntity.getYRot() * 2f + 180f));
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
//        poseStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
        super.render(crowEntity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();


//        if (!crowEntity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
//        {
////            poseStack.pushPose();
//            renderItem(crowEntity.getItemInHand(InteractionHand.MAIN_HAND), partialTicks, poseStack, bufferIn, packedLightIn);
////            poseStack.popPose();
//        }

//        if(p_115455_.level.isClientSide)
//            System.out.println(p_115455_.headTiltTimer);
    }

    @Override
    protected void scale(CrowEntity entitylivingbaseIn, PoseStack poseStack, float partialTickTime) {
        float f = 1;
        if (entitylivingbaseIn.isBaby()) {
            f *= 0.5f;
            this.shadowRadius = 0.125F;
        } else {
            this.shadowRadius = 0.25F;
        }

        poseStack.scale(f, f, f);
    }

    public class LayerCrowItem extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {

        public LayerCrowItem(CrowRenderer render) {
            super(render);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, CrowEntity crow, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = crow.getItem(1);
            poseStack.pushPose();
            translateToHand(poseStack);
            poseStack.translate(0, -0.065F, -0.265F);
            if(itemstack.getItem() instanceof BroomItem)
                poseStack.translate(0.1f, 0.16f, 0.01F);
            if(crow.isBaby()){
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(-2.5F));
            if(itemstack.getItem() instanceof BroomItem)
                poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
            poseStack.scale(0.75F, 0.75F, 0.75F);
            ItemStack stack = itemstack.copy();
//            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemDisplayContext.GROUND, false, poseStack, bufferIn, packedLightIn);
//            if(itemstack.getItem() == ModItems.WARHAMMER.get() && crow.getDisplayName().getString().equals("Thor") && !itemstack.isEnchanted())
//                stack = EnchantmentHelper.enchantItem(RandomSource.create(), itemstack.copy(), 1, false);

            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemDisplayContext.GROUND, false, poseStack, bufferIn, packedLightIn);
            poseStack.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().body.getChild("head").translateAndRotate(matrixStack);

        }
    }


    public class LayerCrowCollar extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
        private static final ResourceLocation CROW_COLLAR_LOCATION = HexereiUtil.getResource("textures/entity/crow_collar.png");

        public LayerCrowCollar(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_117707_) {
            super(p_117707_);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, CrowEntity entity, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            if (entity.isTame() && !entity.isInvisible() && (entity.getDyeColorId() != -1 || entity.getName().getString().equals("jeb_") || entity.getName().getString().equals("joe_"))) {
                float[] afloat = HexereiUtil.rgbIntToFloatArray(entity.getDyeColor().getTextureDiffuseColor());
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(CROW_COLLAR_LOCATION), false, false);
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F));

            }
        }
    }


    public class LayerCrowMisc extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
        private static final ResourceLocation CROW_AMULET_LOCATION = HexereiUtil.getResource("textures/entity/crow_amulet.png");

        public LayerCrowMisc(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_117707_) {
            super(p_117707_);
        }

//        @Override
//        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CrowEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
//
//        }


        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, CrowEntity crow, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            ItemStack itemstack = crow.getItem(2);
            if (!crow.isInvisible() && !itemstack.isEmpty()) {

                boolean renderNecklace = false;

                poseStack.pushPose();
                translateToBody(poseStack);
                poseStack.translate(0, -0.15F, -0.165F);
                if(itemstack.getItem() instanceof BroomItem)
                    poseStack.translate(0.1f, 0.16f, 0.01F);
                if(crow.isBaby()){
                    poseStack.scale(0.75F, 0.75F, 0.75F);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(180F));
                if(itemstack.getItem() instanceof BroomItem)
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
                poseStack.mulPose(Axis.XP.rotationDegrees(180F));
                poseStack.scale(0.1F, 0.1F, 0.1F);
                ItemStack stack = itemstack.copy();
    //            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemDisplayContext.GROUND, false, poseStack, bufferIn, packedLightIn);


                BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);

                ResourceLocation loc = TRINKET_LOCATION.getOrDefault(stack.getItem(), null);

                if(loc != null) {

                    poseStack.pushPose();
                    poseStack.translate(0, 0, -0.02F);
                    poseStack.scale(10, 10, 10);

                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
//                    RenderSystem.setShader(GameRenderer::getNewEntityShader);

                    Matrix4f matrix = poseStack.last().pose();
                    MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(loc));

                    poseStack.last().normal().rotate(Axis.XP.rotationDegrees((float) 45));
                    PoseStack.Pose normal = poseStack.last();
                    int u = 0;
                    int v = 0;
                    int imageWidth = 16;
                    int imageHeight = 16;
                    int width = 16;
                    int height = 16;
                    float u1 = (u + 0.0F) / (float) imageWidth;
                    float u2 = (u + (float) width) / (float) imageWidth;
                    float v1 = (v + 0.0F) / (float) imageHeight;
                    float v2 = (v + (float) height) / (float) imageHeight;

                    boolean activeFlag = false;
                    int temp = packedLightIn;
                    if (stack.has(DataComponents.CUSTOM_DATA)) {
                        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

                        if(tag.contains("Active") && tag.getBoolean("Active")) {
                            packedLightIn = LightTexture.FULL_BRIGHT;
                            activeFlag = true;

                        }
                    }

                    buffer.addVertex(matrix,  0.055f / 16 * width, -0.055f / 16 * height,0).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                    buffer.addVertex(matrix, 0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                    buffer.addVertex(matrix, -0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                    buffer.addVertex(matrix,  -0.055f / 16 * width,-0.055f / 16 * height, 0).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);

                    if(activeFlag){

                        buffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.parse("hexerei:textures/item/crow_active_amulet_trinket.png")));

                        poseStack.translate(0, 0, 0.002F);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(-(ClientEvents.getClientTicks()) % 360f));
                        poseStack.last().normal().rotate(Axis.XP.rotationDegrees((float) 45));
                        normal = poseStack.last();
                        imageWidth = 32;
                        imageHeight = 32;
                        width = 32;
                        height = 32;
                        u1 = (u + 0.0F) / (float) imageWidth;
                        u2 = (u + (float) width) / (float) imageWidth;
                        v1 = (v + 0.0F) / (float) imageHeight;
                        v2 = (v + (float) height) / (float) imageHeight;

                        buffer.addVertex(matrix, 0.055f / 16 * width, -0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100f))).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, 0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100f))).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, -0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100f))).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, -0.055f / 16 * width, -0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100f))).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);


                        poseStack.scale(1.15f, 1.15f, 1.15f);
                        poseStack.translate(0, 0, -0.004F);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(1.5f * (ClientEvents.getClientTicks()) % 360f));
                        buffer.addVertex(matrix, 0.055f / 16 * width, -0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75f))).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, 0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75f))).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, -0.055f / 16 * width, 0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75f))).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);
                        buffer.addVertex(matrix, -0.055f / 16 * width, -0.055f / 16 * height, 0).setColor(1, 1, 1, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75f))).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(normal, 1F, 0F, 0F);

                    }
                    packedLightIn = temp;
                    poseStack.popPose();
                    poseStack.translate(0, 0, 0.03F);
                    poseStack.scale(1.15F, 1.15F, 1.15F);
                    Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, new ItemStack(ModItems.CROW_BLANK_AMULET_TRINKET.get()), ItemDisplayContext.FIXED, false, poseStack, bufferIn, packedLightIn);
                    renderNecklace = true;
                }else {

//                    if(stack.is(Items.FILLED_MAP)) {
//                        poseStack.pushPose();
//                        poseStack.translate(-1/2f, -1/2f, 0);
//                        poseStack.scale(1F/128F, 1F/128F, 1);
//                        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
//                        poseStack.translate(-128F, -128F, 0);
//                        MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, crow.level());
//                        if(mapitemsaveddata == null)
//                                HexereiPacketHandler.sendToServer(new AskForMapDataPacket(itemstack));
//                        if(mapitemsaveddata != null && crow.getFramedMapId().isPresent())
//                            Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, bufferIn, crow.getFramedMapId().getAsInt(), mapitemsaveddata, true, packedLightIn);
//                        poseStack.popPose();
//                        poseStack.translate(0, 0, 0.03F);
//                        poseStack.scale(1.15F, 1.15F, 1.15F);
//                        Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, new ItemStack(ModItems.CROW_BLANK_AMULET_TRINKET_FRAME.get()), ItemDisplayContext.FIXED, false, poseStack, bufferIn, packedLightIn);
//                        renderNecklace = true;
//
//                    }
                    if(stack.is(ModItems.CROW_BLANK_AMULET.get())){
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 0.03F);
                        poseStack.scale(1.15F, 1.15F, 1.15F);
                        Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemDisplayContext.FIXED, false, poseStack, bufferIn, packedLightIn);
                        poseStack.popPose();
                        renderNecklace = true;
                    }
                }

                poseStack.popPose();

                if(renderNecklace){
                    VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(CROW_AMULET_LOCATION), false, false);
                    this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
                }

            }
        }

        protected void translateToBody(PoseStack matrixStack) {
            this.getParentModel().body.translateAndRotate(matrixStack);

        }
    }

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
        private final HumanoidModel<?> defaultBipedModel;
        private final TextureAtlas armorTrimAtlas;

        public LayerCrowHelmet(CrowRenderer renderer, EntityRendererProvider.Context renderManagerIn){
            super(renderer);
            this.renderer = renderer;
            defaultBipedModel = new HumanoidModel<>(renderManagerIn.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
            this.armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, CrowEntity crow, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

            poseStack.pushPose();
            ItemStack itemstack = crow.itemHandler.getStackInSlot(0);
            if (itemstack.getItem() instanceof ArmorItem armoritem) {


                EquipmentSlot pSlot = armoritem.getEquipmentSlot();
                HumanoidModel<?> a = defaultBipedModel;
                a = getArmorModelHook(crow, itemstack, EquipmentSlot.HEAD, a);
                a.setAllVisible(false);
                a.hat.visible = true;
                a.head.visible = true;
                translateToHead(poseStack);
                poseStack.scale(0.35F, 0.35F, 0.35F);
                poseStack.translate(0f,  -0.1F, -0.25F);

                Model model = ClientHooks.getArmorModel(crow, itemstack, pSlot, a);
                ArmorMaterial armormaterial = armoritem.getMaterial().value();
                boolean flag1 = itemstack.hasFoil();
                IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
                extensions.setupModelAnimations(crow, itemstack, EquipmentSlot.HEAD, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);

                for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                    ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
                    int j = extensions.getArmorLayerTintColor(itemstack, crow, armormaterial$layer, layerIdx, -1);
                    ResourceLocation texture = ClientHooks.getArmorTexture(crow, itemstack, armormaterial$layer, false, pSlot);
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
                poseStack.translate(0f, -0.2F, -0.2F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                renderItem(itemstack, crow.level(), poseStack, bufferIn, packedLightIn);
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
            this.renderer.getModel().body.translateAndRotate(poseStack);
        }

        private void renderHelmet(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, int col, ResourceLocation armorResource) {
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
            modelIn.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, col);

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

        protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
            Model basicModel = ClientHooks.getArmorModel(entity, itemStack, slot, model);
            return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().body.getChild("head").translateAndRotate(matrixStack);

        }

    }

}