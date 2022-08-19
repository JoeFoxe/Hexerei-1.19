package net.joefoxe.hexerei.client.renderer.entity.model;

// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CandleModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation CANDLE_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_layer"), "main");
    public static final ModelLayerLocation CANDLE_HERB_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_herb_layer"), "main");
    public static final ModelLayerLocation CANDLE_BASE_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_base_layer"), "main");
    public static final ModelLayerLocation CANDLE_GLOW_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_glow_layer"), "main");
    public static final ModelLayerLocation CANDLE_SWIRL_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_swirl_layer"), "main");
    public final ModelPart wick;
    public final ModelPart wax1;
    public final ModelPart wax2;
    public final ModelPart wax3;
    public final ModelPart wax4;
    public final ModelPart wax5;
    public final ModelPart wax6;
    public final ModelPart wax7;
    public final ModelPart[] wax;
    public final ModelPart base;

    public CandleModel(ModelPart root) {
        this.wick = root.getChild("wick");
        this.wax1 = root.getChild("wax1");
        this.wax2 = root.getChild("wax2");
        this.wax3 = root.getChild("wax3");
        this.wax4 = root.getChild("wax4");
        this.wax5 = root.getChild("wax5");
        this.wax6 = root.getChild("wax6");
        this.wax7 = root.getChild("wax7");
        this.base = root.getChild("base");
        this.wax = new ModelPart[]{wax1, wax2, wax3, wax4, wax5, wax6, wax7};
    }

    public static LayerDefinition createBodyLayerHerb() {
        return createBodyLayer(new CubeDeformation(0.1f));
    }
    public static LayerDefinition createBodyLayerGlow() {
        return createBodyLayer(new CubeDeformation(0.15f));
    }
    public static LayerDefinition createBodyLayerSwirl() {
        return createBodyLayer(new CubeDeformation(0.20f));
    }

    public static LayerDefinition createBodyLayer() {
        return createBodyLayer(CubeDeformation.NONE);
    }
    public static LayerDefinition createBodyLayer(CubeDeformation cube) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wick = partdefinition.addOrReplaceChild("wick", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 7.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(0.0F, 7.0F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition wax1 = partdefinition.addOrReplaceChild("wax1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 15.0F, -1.0F, 2.0F, 1.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax2 = partdefinition.addOrReplaceChild("wax2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 14.0F, -1.0F, 2.0F, 2.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax3 = partdefinition.addOrReplaceChild("wax3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 13.0F, -1.0F, 2.0F, 3.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax4 = partdefinition.addOrReplaceChild("wax4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 12.0F, -1.0F, 2.0F, 4.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax5 = partdefinition.addOrReplaceChild("wax5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 11.0F, -1.0F, 2.0F, 5.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax6 = partdefinition.addOrReplaceChild("wax6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 10.0F, -1.0F, 2.0F, 6.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax7 = partdefinition.addOrReplaceChild("wax7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 9.0F, -1.0F, 2.0F, 7.0F, 2.0F,  cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(5, 6).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, cube), PartPose.offset(0.0F, 23.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
    public static LayerDefinition createBaseLayer() {
        CubeDeformation cube = CubeDeformation.NONE;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wick = partdefinition.addOrReplaceChild("wick", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 7.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(0.0F, 7.0F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition wax1 = partdefinition.addOrReplaceChild("wax1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 15.0F, -1.0F, 2.0F, 1.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax2 = partdefinition.addOrReplaceChild("wax2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 14.0F, -1.0F, 2.0F, 2.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax3 = partdefinition.addOrReplaceChild("wax3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 13.0F, -1.0F, 2.0F, 3.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax4 = partdefinition.addOrReplaceChild("wax4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 12.0F, -1.0F, 2.0F, 4.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax5 = partdefinition.addOrReplaceChild("wax5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 11.0F, -1.0F, 2.0F, 5.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax6 = partdefinition.addOrReplaceChild("wax6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 10.0F, -1.0F, 2.0F, 6.0F, 2.0F, cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition wax7 = partdefinition.addOrReplaceChild("wax7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 9.0F, -1.0F, 2.0F, 7.0F, 2.0F,  cube), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        wick.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, alpha);
        wax7.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, alpha);
    }
}