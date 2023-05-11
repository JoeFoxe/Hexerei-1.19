package net.joefoxe.hexerei.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ArmorModel extends HumanoidModel<LivingEntity> implements IClientItemExtensions {

    public EquipmentSlot slot;
    public LivingEntity entity;

    public ArmorModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }
    @Override
    public void setupAnim(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!(pEntity instanceof ArmorStand entityIn)) {
            super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            return;
        }

        this.head.xRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getX();
        this.head.yRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getY();
        this.head.zRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getZ();
        this.head.setPos(0.0F, 1.0F, 0.0F);
        this.body.xRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getX();
        this.body.yRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getY();
        this.body.zRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getZ();
        this.leftArm.xRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getX();
        this.leftArm.yRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getY();
        this.leftArm.zRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getZ();
        this.rightArm.xRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getX();
        this.rightArm.yRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getY();
        this.rightArm.zRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getZ();
        this.leftLeg.xRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getX();
        this.leftLeg.yRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getY();
        this.leftLeg.zRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getZ();
        this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
        this.rightLeg.xRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getX();
        this.rightLeg.yRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getY();
        this.rightLeg.zRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getZ();
        this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
        this.hat.copyFrom(this.head);
    }

//    @Override
//    protected Iterable<ModelPart> headParts() {
//        return slot == EquipmentSlot.HEAD ? ImmutableList.of(modelHead) : ImmutableList.of();
//    }
//
//    @Override
//    protected Iterable<ModelPart> bodyParts() {
//        if (slot == EquipmentSlot.CHEST) {
//            return ImmutableList.of(modelBody, modelLeft_arm, modelRight_arm);
//        }
//        else if (slot == EquipmentSlot.LEGS) {
//            return ImmutableList.of(modelLeft_leg, modelRight_leg, modelBelt);
//        }
//        else if (slot == EquipmentSlot.FEET) {
//            return ImmutableList.of(modelLeft_foot, modelRight_foot);
//        }
//        else return ImmutableList.of();
//    }

//    @Override
//    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
////        super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//        matrixStack.pushPose();
//
//        if(entity != null) {
//            EntityModel entityModel = ((LivingEntityRenderer<?, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity)).getModel();
//            if(entityModel instanceof HumanoidModel livingModel){
//                this.attackTime = livingModel.attackTime;
//                this.riding = livingModel.riding;
//                this.young = livingModel.young;
//                this.leftArmPose = livingModel.leftArmPose;
//                this.rightArmPose = livingModel.rightArmPose;
//                this.crouching = livingModel.crouching;
//                this.head.copyFrom(livingModel.head);
//                this.body.copyFrom(livingModel.body);
//                this.rightArm.copyFrom(livingModel.rightArm);
//                this.leftArm.copyFrom(livingModel.leftArm);
//                this.rightLeg.copyFrom(livingModel.rightLeg);
//                this.leftLeg.copyFrom(livingModel.leftLeg);
//            }
//
//
//        }
//
//        if (this.slot == EquipmentSlot.HEAD) {
//            this.modelHead.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//        }
//        else if (this.slot == EquipmentSlot.CHEST) {
//            this.modelBody.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            this.modelRight_arm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            this.modelLeft_arm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//
//        } else if (this.slot == EquipmentSlot.LEGS) {
//            this.modelBelt.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            this.modelRight_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            this.modelLeft_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//
//        } else if (this.slot == EquipmentSlot.FEET) {
//            this.modelRight_foot.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            this.modelLeft_foot.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//        }
//        matrixStack.popPose();
//    }



    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        setPartVisibility(slot);
        super.renderToBuffer(ms, buffer, light, overlay, r, g, b, a);
    }

    private void setPartVisibility(EquipmentSlot slot) {
        setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                head.visible = true;
                hat.visible = true;
            }
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
            }
            case LEGS -> {
                body.visible = true;
                rightLeg.visible = true;
                leftLeg.visible = true;
            }
            case FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
            }
        }
    }

}