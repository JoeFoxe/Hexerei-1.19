package net.joefoxe.hexerei.client.renderer.entity.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.time.LocalDateTime;

public class CrowModel<T extends CrowEntity> extends ColorableAgeableListModel<T> {
    public final ModelPart body;
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "crow"), "main");
    public static final ModelLayerLocation POWER_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "crow_power_layer"), "main");
    public CrowModel(ModelPart root) {
        this.body = root.getChild("body");
    }

//    public CrowModel(ModelPart p_170677_) {
//        this(p_170677_, RenderType::entityCutoutNoCull);
//    }

    public static LayerDefinition createBodyLayerNone() {
        return createBodyLayer(CubeDeformation.NONE);
    }
    public static LayerDefinition createBodyLayerEnlarge() {
        return createBodyLayer(new CubeDeformation(0.1f));
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cube) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(1, 6).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 5.0F, cube)
                .texOffs(0, 14).addBox(-1.5F, -2.308F, -0.4665F, 3.0F, 2.0F, 0.0F, cube), PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, -0.5236F, 0.0F, 0.0F));

        PartDefinition bandana_r1 = chest.addOrReplaceChild("bandana_r1", CubeListBuilder.create().texOffs(4, 16).addBox(-0.5F, -5.225F, -0.425F, 1.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(0.0F, 4.0F, 2.5F, 0.5236F, 0.0F, 0.0F));

        PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(12, 7).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, cube), PartPose.offset(-1.0F, -3.0F, 0.5F));

        PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(12, 7).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, cube), PartPose.offset(1.0F, -3.0F, 0.5F));

        PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create(), PartPose.offset(-1.0F, -5.5F, -1.5F));

        PartDefinition rightWing_r1 = rightWing.addOrReplaceChild("rightWing_r1", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-11.0F, -5.5F, -2.5F, 10.0F, 0.0F, 6.0F, cube).mirror(false), PartPose.offsetAndRotation(1.0F, 5.5F, -0.5F, 0.0F, 0.1745F, 0.0F));

        PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create(), PartPose.offset(1.0F, -5.5F, -1.5F));

        PartDefinition leftWing_r1 = leftWing.addOrReplaceChild("leftWing_r1", CubeListBuilder.create().texOffs(0, 25).addBox(1.0F, -5.5F, -2.5F, 10.0F, 0.0F, 6.0F, cube), PartPose.offsetAndRotation(-1.0F, 5.5F, -0.5F, 0.0F, -0.1745F, 0.0F));

        PartDefinition rightTail = body.addOrReplaceChild("rightTail", CubeListBuilder.create().texOffs(16, 14).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube), PartPose.offsetAndRotation(-0.75F, -3.0F, 2.5F, -0.2618F, -0.2618F, 0.0F));

        PartDefinition leftTail = body.addOrReplaceChild("leftTail", CubeListBuilder.create().texOffs(16, 14).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube).mirror(false), PartPose.offsetAndRotation(0.75F, -3.0F, 2.5F, -0.2618F, 0.2618F, 0.0F));

        PartDefinition tailMid = body.addOrReplaceChild("tailMid", CubeListBuilder.create().texOffs(13, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 7.0F, cube), PartPose.offsetAndRotation(0.0F, -3.5F, 3.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition wings = body.addOrReplaceChild("wings", CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -3.5F, -0.5F, 4.0F, 4.0F, 7.0F, cube), PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, -0.5236F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -3.0F, 3.0F, 3.0F, 3.0F, cube), PartPose.offset(0.0F, -5.0F, -1.5F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.0434F, -2.0F, 1.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(0.0F, -0.924F, -2.7521F, -0.1309F, 0.0F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.25F, -0.925F, 1.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(0.0F, -1.75F, -4.0F, 0.1309F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void renderOnShoulder(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float p_228284_5_, float p_228284_6_, float p_228284_7_, float p_228284_8_, int p_228284_9_) {
        body.getChild("leftWing").visible = false;
        body.getChild("rightWing").visible = false;
        body.getChild("wings").visible = true;
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setupAnim(CrowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        ModelPart leftWing = body.getChild("leftWing"), rightWing = body.getChild("rightWing"),
                wings = body.getChild("wings"), rightLeg = body.getChild("rightLeg"), leftLeg = body.getChild("leftLeg"),
                head = body.getChild("head"),
                rightTail = body.getChild("rightTail"),
                leftTail = body.getChild("leftTail"),
                tailMid = body.getChild("tailMid"),
                head_r1 = head.getChild("head_r1");
        if (!entity.isOnGround() || !entity.isInSittingPose())
            body.y = 24.0f;
        if (entity.isOnGround()) {
            leftWing.visible = false;
            rightWing.visible = false;
            wings.visible = true;

            if (entity.isTame() && entity.isInSittingPose()) {
                rightLeg.xRot = -(float)Math.PI / 6;
                leftLeg.xRot = -(float)Math.PI / 6;
                rightLeg.y = -4f;
                rightLeg.z = 1.5f;
                leftLeg.y = -4f;
                leftLeg.z = 1.5f;

                body.y = 25.5f;
            }
            else {
                rightLeg.y = -3f;
                rightLeg.z = 0.5f;
                leftLeg.y = -3f;
                leftLeg.z = 0.5f;
                rightLeg.xRot = Mth.cos(limbSwing * 2F + (float)Math.PI) * 2F * limbSwingAmount;
                leftLeg.xRot = Mth.cos(limbSwing * 2F) * 2F * limbSwingAmount;
            }
            head.xRot = (float)Math.toRadians(headPitch) + Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;

            rightTail.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;
            leftTail.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;
            tailMid.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;


            rightTail.yRot = -Mth.sin(0.05f);
            leftTail.yRot = Mth.sin(0.05f);

            rightTail.yRot += Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
            leftTail.yRot += Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
            tailMid.yRot = Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
            if(entity.tailWag)
            {
                rightTail.yRot += Mth.sin(15/100f) * 0.5f;
                leftTail.yRot -= Mth.sin(15/100f) * 0.5f;
            }

            rightTail.yRot -= Mth.sin(entity.tailFanTiltAngleActual/100f) * 0.5f;
            leftTail.yRot += Mth.sin(entity.tailFanTiltAngleActual/100f) * 0.5f;
        }
        else {


            if(entity.isPassenger())
            {
                leftWing.visible = false;
                rightWing.visible = false;
                wings.visible = true;

                rightLeg.xRot = -(float)Math.PI / 6;
                leftLeg.xRot = -(float)Math.PI / 6;
                rightLeg.y = -4f;
                rightLeg.z = 1.5f;
                leftLeg.y = -4f;
                leftLeg.z = 1.5f;

                body.y = 25.5f;
                head.xRot = (float)Math.toRadians(headPitch) + Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;

                rightTail.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;
                leftTail.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;
                tailMid.xRot = Mth.sin(Hexerei.getClientTicks()/25f) * 0.1f;


                rightTail.yRot = -Mth.sin(0.05f);
                leftTail.yRot = Mth.sin(0.05f);

                rightTail.yRot += Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
                leftTail.yRot += Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
                tailMid.yRot = Mth.sin(entity.tailWagTiltAngleActual/100f) * 0.2f;
                if(entity.tailWag)
                {
                    rightTail.yRot += Mth.sin(15/100f) * 0.5f;
                    leftTail.yRot -= Mth.sin(15/100f) * 0.5f;
                }

                rightTail.yRot -= Mth.sin(entity.tailFanTiltAngleActual/100f) * 0.5f;
                leftTail.yRot += Mth.sin(entity.tailFanTiltAngleActual/100f) * 0.5f;
            }
            else {
                leftWing.visible = true;
                rightWing.visible = true;
                wings.visible = false;

                rightLeg.xRot = Mth.sin(20);
                leftLeg.xRot = Mth.sin(20);

                rightWing.zRot = entity.rightWingAngleActual;
                leftWing.zRot = entity.leftWingAngleActual;

                head.xRot = (float) Math.toRadians(headPitch);

                rightTail.yRot = -Mth.sin(0.15f);
                leftTail.yRot = Mth.sin(0.15f);
            }


        }

        body.xRot = Mth.sin(entity.peckTiltAngleActual/100f);



        head.yRot = (float)Math.toRadians(netHeadYaw);
        head.zRot = Mth.sin(entity.headZTiltAngleActual/100f)/2f;
        head.xRot += Mth.sin(entity.headXTiltAngleActual/100f)/2f;
        if ((entity.isOnGround() || entity.isPassenger()) && entity.dance) {
            head.zRot = 0f;
            head.xRot = (float) Math.toRadians(headPitch) + Mth.sin(entity.animationCounter/1.5f)/12f;
            head.yRot = (float)Math.toRadians(netHeadYaw) + Mth.sin(entity.animationCounter/3f)/4f;

            rightTail.xRot = Mth.sin(entity.animationCounter/3f) * 0.1f;
            leftTail.xRot = Mth.sin(entity.animationCounter/3f) * 0.1f;
            tailMid.xRot = Mth.sin(entity.animationCounter/3f) * 0.1f;

            body.yRot = Mth.sin(entity.animationCounter/3f) * 0.2f;

            rightTail.yRot = Mth.sin(0.10f);
            leftTail.yRot = -Mth.sin(0.10f);
            body.y = 24f + Mth.abs(Mth.sin(entity.animationCounter/6f));

            rightTail.yRot += Mth.sin(entity.animationCounter/3f) * 0.4f;
            leftTail.yRot += Mth.sin(entity.animationCounter/3f) * 0.4f;
            tailMid.yRot = Mth.sin(entity.animationCounter/3f) * 0.4f;

            rightLeg.y = -3f - Mth.abs(Mth.sin(entity.animationCounter/6f));;
            rightLeg.z = 0.5f;
            leftLeg.y = -3f - Mth.abs(Mth.sin(entity.animationCounter/6f));;
            leftLeg.z = 0.5f;
            rightLeg.xRot = Mth.cos(limbSwing * 2F + (float)Math.PI) * 2F * limbSwingAmount;
            leftLeg.xRot = Mth.cos(limbSwing * 2F) * 2F * limbSwingAmount;
        }
        else
            body.yRot = 0;

        head_r1.xRot = Mth.sin(entity.cawTiltAngleActual/100f);
        if(entity.peckTiltAngleActual > 0)
            head_r1.xRot = Mth.sin(entity.peckTiltAngleActual/100f);


    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return null;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return null;
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
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


}