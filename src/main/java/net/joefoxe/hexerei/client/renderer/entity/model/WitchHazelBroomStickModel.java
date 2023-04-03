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

public class WitchHazelBroomStickModel extends ListModel<BroomEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "witch_hazel_broom_stick"), "main");
    public static final ModelLayerLocation POWER_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "witch_hazel_broom_power_stick"), "main");
    private final ModelPart broom_stick;

    public static LayerDefinition createBodyLayerNone() {
        return createBodyLayer(CubeDeformation.NONE);
    }
    public static LayerDefinition createBodyLayerEnlarge() {
        return createBodyLayer(new CubeDeformation(0.2f));
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cube) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Broom = partdefinition.addOrReplaceChild("Broom", CubeListBuilder.create().texOffs(0, 0).addBox(-11.2131F, -3.9639F, -0.8505F, 14.0F, 2.0F, 2.0F, cube)
                .texOffs(10, 10).addBox(-21.6631F, -3.9739F, -1.2505F, 4.0F, 2.0F, 2.0F, cube)
                .texOffs(19, 4).mirror().addBox(9.2369F, -3.9739F, -0.4505F, 5.0F, 2.0F, 2.0F, cube).mirror(false), PartPose.offset(0.2131F, 24.9639F, -0.1495F));

        PartDefinition stick_r1 = Broom.addOrReplaceChild("stick_r1", CubeListBuilder.create().texOffs(0, 4).addBox(-0.9889F, -1.005F, -1.5952F, 5.0F, 2.0F, 2.0F, cube), PartPose.offsetAndRotation(-14.6863F, -2.9689F, -0.9124F, 0.0F, -0.4363F, 0.0F));

        PartDefinition stick_r2 = Broom.addOrReplaceChild("stick_r2", CubeListBuilder.create().texOffs(0, 8).addBox(-3.4273F, -0.995F, -1.7749F, 4.0F, 2.0F, 2.0F, cube), PartPose.offsetAndRotation(-14.6863F, -2.9689F, -0.9124F, 0.0F, 0.4363F, 0.0F));

        PartDefinition stick_r3 = Broom.addOrReplaceChild("stick_r3", CubeListBuilder.create().texOffs(0, 8).mirror().addBox(-0.5727F, -0.995F, -0.2251F, 4.0F, 2.0F, 2.0F, cube).mirror(false), PartPose.offsetAndRotation(6.26F, -2.9689F, 1.2115F, 0.0F, 0.4363F, 0.0F));

        PartDefinition stick_r4 = Broom.addOrReplaceChild("stick_r4", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(-4.0111F, -1.005F, -0.4048F, 5.0F, 2.0F, 2.0F, cube).mirror(false), PartPose.offsetAndRotation(6.26F, -2.9689F, 1.2115F, 0.0F, -0.4363F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public WitchHazelBroomStickModel(ModelPart root) {
        this.broom_stick = root.getChild("Broom");
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        broom_stick.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(broom_stick);
    }

    @Override
    public void setupAnim(BroomEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }
}