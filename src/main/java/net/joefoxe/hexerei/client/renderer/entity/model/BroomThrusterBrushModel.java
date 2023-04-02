package net.joefoxe.hexerei.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)

public class BroomThrusterBrushModel extends ListModel<BroomEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "broom_thruster_brush"), "main");
    public static final ModelLayerLocation POWER_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "broom_thruster_brush_power"), "main");
    private final ModelPart broom_brush;

    public static LayerDefinition createBodyLayerNone() {
        return createBodyLayer(CubeDeformation.NONE);
    }
    public static LayerDefinition createBodyLayerEnlarge() {
        return createBodyLayer(new CubeDeformation(0.15f));
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cube) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Broom = partdefinition.addOrReplaceChild("Broom", CubeListBuilder.create().texOffs(8, 30).addBox(11.2869F, -4.4639F, -1.3505F, 1.0F, 3.0F, 3.0F, cube.extend(0.1f))
                .texOffs(0, 28).addBox(9.7869F, -4.4639F, -1.3505F, 1.0F, 3.0F, 3.0F, cube.extend(-0.1f)), PartPose.offset(0.2131F, 24.9639F, -0.1495F));

        PartDefinition brush_r1 = Broom.addOrReplaceChild("brush_r1", CubeListBuilder.create().texOffs(22, 24).addBox(3.5245F, 1.2877F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -3.0157F, 0.1388F, 0.7835F, -0.0617F, 0.0618F));

        PartDefinition brush_r2 = Broom.addOrReplaceChild("brush_r2", CubeListBuilder.create().texOffs(10, 24).addBox(3.5245F, -2.2377F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 0.7835F, 0.0617F, -0.0618F));

        PartDefinition brush_r3 = Broom.addOrReplaceChild("brush_r3", CubeListBuilder.create().texOffs(0, 22).addBox(3.5245F, -2.2377F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 2.3581F, 0.0617F, 0.0618F));

        PartDefinition brush_r4 = Broom.addOrReplaceChild("brush_r4", CubeListBuilder.create().texOffs(10, 15).addBox(-0.5079F, -0.55F, -1.8F, 1.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(13.1948F, -2.8753F, 2.4266F, 1.5708F, 0.0F, 0.0F));

        PartDefinition brush_r5 = Broom.addOrReplaceChild("brush_r5", CubeListBuilder.create().texOffs(10, 15).addBox(0.0F, -0.175F, -2.725F, 1.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(12.6948F, -5.2685F, 1.0835F, 1.9635F, 0.0F, 0.0F));

        PartDefinition brush_r6 = Broom.addOrReplaceChild("brush_r6", CubeListBuilder.create().texOffs(10, 15).addBox(-0.525F, -4.7F, -0.8F, 2.0F, 2.0F, 2.0F, cube)
                .texOffs(9, 15).addBox(-1.025F, -0.65F, -0.35F, 1.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(12.6948F, -5.2685F, 1.0835F, -3.1416F, 0.0F, 0.0F));

        PartDefinition brush_r7 = Broom.addOrReplaceChild("brush_r7", CubeListBuilder.create().texOffs(10, 15).addBox(-1.8F, -0.55F, -0.025F, 2.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(13.4948F, -4.5967F, 1.826F, 2.3562F, 0.0F, 0.0F));

        PartDefinition brush_r8 = Broom.addOrReplaceChild("brush_r8", CubeListBuilder.create().texOffs(10, 15).addBox(-2.0F, -0.7F, 0.0F, 2.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(15.4191F, -4.1862F, 1.4155F, 2.3957F, 0.274F, 0.2849F));

        PartDefinition brush_r9 = Broom.addOrReplaceChild("brush_r9", CubeListBuilder.create().texOffs(14, 21).addBox(3.5245F, 1.1377F, -1.075F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -3.0157F, 0.1388F, 2.3581F, -0.0617F, -0.0618F));

        PartDefinition brush_r10 = Broom.addOrReplaceChild("brush_r10", CubeListBuilder.create().texOffs(26, 21).addBox(3.5245F, 1.2377F, -1.1F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -3.0157F, 0.1388F, 1.5708F, -0.0873F, 0.0F));

        PartDefinition brush_r11 = Broom.addOrReplaceChild("brush_r11", CubeListBuilder.create().texOffs(0, 25).addBox(3.5245F, -2.2377F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 1.5708F, 0.0873F, 0.0F));

        PartDefinition brush_r12 = Broom.addOrReplaceChild("brush_r12", CubeListBuilder.create().texOffs(10, 27).addBox(3.5245F, 1.3127F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -3.0157F, 0.1388F, 0.0F, 0.0F, 0.0873F));

        PartDefinition brush_r13 = Broom.addOrReplaceChild("brush_r13", CubeListBuilder.create().texOffs(22, 27).addBox(3.5245F, -2.2377F, -1.0F, 4.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 0.0F, 0.0F, -0.0873F));

        PartDefinition brush_r14 = Broom.addOrReplaceChild("brush_r14", CubeListBuilder.create().texOffs(0, 12).addBox(-3.2255F, -1.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 0.0F, 0.0F, -0.2182F));

        PartDefinition brush_r15 = Broom.addOrReplaceChild("brush_r15", CubeListBuilder.create().texOffs(0, 15).addBox(-3.2255F, 0.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, 0.0F, 0.0F, 0.2182F));

        PartDefinition brush_r16 = Broom.addOrReplaceChild("brush_r16", CubeListBuilder.create().texOffs(0, 9).addBox(-3.2256F, -1.7377F, -0.9999F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -2.3682F, -0.1537F, 0.1555F));

        PartDefinition brush_r17 = Broom.addOrReplaceChild("brush_r17", CubeListBuilder.create().texOffs(0, 6).addBox(-3.2255F, -1.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -0.7734F, -0.1537F, -0.1555F));

        PartDefinition brush_r18 = Broom.addOrReplaceChild("brush_r18", CubeListBuilder.create().texOffs(0, 3).addBox(-3.2256F, 0.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -2.3682F, 0.1537F, -0.1555F));

        PartDefinition brush_r19 = Broom.addOrReplaceChild("brush_r19", CubeListBuilder.create().texOffs(0, 0).addBox(-3.2255F, 0.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -0.7734F, 0.1537F, 0.1555F));

        PartDefinition brush_r20 = Broom.addOrReplaceChild("brush_r20", CubeListBuilder.create().texOffs(16, 1).addBox(-3.2256F, -1.7377F, -0.9999F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -1.5708F, -0.2182F, 0.0F));

        PartDefinition brush_r21 = Broom.addOrReplaceChild("brush_r21", CubeListBuilder.create().texOffs(0, 20).addBox(-3.7744F, 0.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -1.5708F, -0.2182F, 0.0F));

        PartDefinition brush_r22 = Broom.addOrReplaceChild("brush_r22", CubeListBuilder.create().texOffs(15, 19).addBox(-3.7744F, -1.2377F, -0.4999F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -1.5708F, 0.2182F, 0.0F));

        PartDefinition brush_r23 = Broom.addOrReplaceChild("brush_r23", CubeListBuilder.create().texOffs(18, 7).addBox(-3.7745F, 0.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, 0.0F, 0.0F, -0.2182F));

        PartDefinition brush_r24 = Broom.addOrReplaceChild("brush_r24", CubeListBuilder.create().texOffs(0, 18).addBox(-3.7745F, -1.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, 0.0F, 0.0F, 0.2182F));

        PartDefinition brush_r25 = Broom.addOrReplaceChild("brush_r25", CubeListBuilder.create().texOffs(17, 17).addBox(-3.7744F, -1.2377F, -0.4999F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -2.3682F, 0.1537F, -0.1555F));

        PartDefinition brush_r26 = Broom.addOrReplaceChild("brush_r26", CubeListBuilder.create().texOffs(16, 15).addBox(-3.7745F, -1.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -0.7734F, 0.1537F, 0.1555F));

        PartDefinition brush_r27 = Broom.addOrReplaceChild("brush_r27", CubeListBuilder.create().texOffs(16, 12).addBox(-3.7744F, 0.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -2.3682F, -0.1537F, 0.1555F));

        PartDefinition brush_r28 = Broom.addOrReplaceChild("brush_r28", CubeListBuilder.create().texOffs(16, 9).addBox(-3.7745F, 0.2377F, -0.5F, 7.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(19.0058F, -2.9122F, 0.1388F, -0.7734F, -0.1537F, -0.1555F));

        PartDefinition brush_r29 = Broom.addOrReplaceChild("brush_r29", CubeListBuilder.create().texOffs(16, 4).addBox(-3.2256F, 0.7377F, -1.0F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(14.0679F, -2.9122F, 0.1388F, -1.5708F, 0.2182F, 0.0F));

        PartDefinition bone = Broom.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(15.4191F, -3.4362F, -1.3345F, 2.3562F, 0.0F, 0.0F));

        PartDefinition brush_r30 = bone.addOrReplaceChild("brush_r30", CubeListBuilder.create().texOffs(10, 15).addBox(-2.0F, -0.7F, 0.0F, 2.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.3957F, 0.274F, 0.2849F));

        PartDefinition brush_r31 = bone.addOrReplaceChild("brush_r31", CubeListBuilder.create().texOffs(10, 15).addBox(-1.8F, -0.55F, -0.025F, 2.0F, 1.0F, 1.0F, cube), PartPose.offsetAndRotation(-1.9243F, -0.4105F, 0.4105F, 2.3562F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public BroomThrusterBrushModel(ModelPart root) {
        this.broom_brush = root.getChild("Broom");
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        broom_brush.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(broom_brush);
    }

    @Override
    public void setupAnim(BroomEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }
}