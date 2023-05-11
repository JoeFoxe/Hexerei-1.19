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

public class BroomSeatModel extends ListModel<BroomEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "broom_seat"), "main");
    private final ModelPart satchel;
    private final ModelPart seat;


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Satchel = partdefinition.addOrReplaceChild("Satchel", CubeListBuilder.create().texOffs(42, 25).addBox(3.0F, -2.0F, -6.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 4).addBox(3.0F, -2.25F, -6.25F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(42, 18).addBox(3.0F, -2.0F, 4.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(41, 10).addBox(3.0F, -2.25F, 4.25F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(43, 33).addBox(4.5F, -3.5F, 2.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(44, 41).addBox(4.5F, -3.5F, -5.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 24.0F, 0.0F));

        PartDefinition Seat = partdefinition.addOrReplaceChild("Seat", CubeListBuilder.create().texOffs(2, 6).addBox(-8.0F, 0.0F, -4.5F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, 20.0F, 0.0F));

        PartDefinition cube_r1 = Seat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 21).addBox(-1.25F, -3.925F, -5.0F, 2.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.5F, 0.0F, 0.0F, 0.0436F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public BroomSeatModel(ModelPart root) {
        this.satchel = root.getChild("Satchel");
        this.seat = root.getChild("Seat");
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        satchel.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        seat.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(satchel, seat);
    }

    @Override
    public void setupAnim(BroomEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }
}