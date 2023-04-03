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

public class MoonDustBrushModel extends ListModel<BroomEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "moon_dust_brush"), "main");
    public static final ModelLayerLocation POWER_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "moon_dust_brush_power"), "main");
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

        PartDefinition Broom = partdefinition.addOrReplaceChild("Broom", CubeListBuilder.create().texOffs(8, 28).addBox(11.2869F, -4.4639F, -1.3505F, 1.0F, 3.0F, 3.0F, cube.extend(0.1f))
                .texOffs(0, 27).addBox(9.7869F, -4.4639F, -1.3505F, 1.0F, 3.0F, 3.0F, cube.extend(-0.1f)), PartPose.offset(0.2131F, 24.9639F, -0.1495F));

        PartDefinition brush_r1 = Broom.addOrReplaceChild("brush_r1", CubeListBuilder.create().texOffs(16, 0).addBox(-2.5F, -0.75F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(25.3869F, -2.6638F, -0.8307F, -1.8837F, -0.0479F, 0.4694F));

        PartDefinition brush_r2 = Broom.addOrReplaceChild("brush_r2", CubeListBuilder.create().texOffs(24, 25).addBox(-4.0623F, -1.0711F, 1.0553F, 5.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(20.7458F, -2.9208F, 0.1517F, -0.0662F, 0.3505F, -0.0037F));

        PartDefinition brush_r3 = Broom.addOrReplaceChild("brush_r3", CubeListBuilder.create().texOffs(12, 25).addBox(-4.0623F, -0.9289F, 1.0555F, 5.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(20.7458F, -2.9208F, 0.1517F, -1.0074F, 0.1748F, -0.3065F));

        PartDefinition brush_r4 = Broom.addOrReplaceChild("brush_r4", CubeListBuilder.create().texOffs(16, 16).addBox(-2.5F, -0.7F, -1.0F, 5.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(19.8039F, -1.1252F, -0.8771F, -0.4913F, -0.1878F, -0.3482F));

        PartDefinition brush_r5 = Broom.addOrReplaceChild("brush_r5", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0622F, -0.9289F, -2.0554F, 5.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(20.7458F, -2.9208F, 0.1517F, -0.0662F, -0.3407F, -0.0044F));

        PartDefinition brush_r6 = Broom.addOrReplaceChild("brush_r6", CubeListBuilder.create().texOffs(14, 22).addBox(-4.0622F, -1.071F, -2.0554F, 5.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(20.7458F, -2.9208F, 0.1517F, -1.0045F, -0.1655F, 0.298F));

        PartDefinition brush_r7 = Broom.addOrReplaceChild("brush_r7", CubeListBuilder.create().texOffs(16, 13).addBox(-4.0222F, -2.1889F, -1.0F, 5.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(20.7458F, -2.9208F, 0.1517F, -0.5016F, 0.1555F, 0.2628F));

        PartDefinition brush_r8 = Broom.addOrReplaceChild("brush_r8", CubeListBuilder.create().texOffs(0, 18).addBox(-3.25F, -1.0F, -0.5F, 6.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(15.6019F, -2.8907F, 1.635F, -0.0718F, -0.5246F, 0.017F));

        PartDefinition brush_r9 = Broom.addOrReplaceChild("brush_r9", CubeListBuilder.create().texOffs(26, 22).addBox(-2.9F, -1.0F, -0.85F, 5.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(16.0636F, -4.2429F, -0.6724F, -1.0271F, 0.2346F, -0.4115F));

        PartDefinition brush_r10 = Broom.addOrReplaceChild("brush_r10", CubeListBuilder.create().texOffs(14, 19).addBox(18.5002F, -13.234F, 9.8753F, 6.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(-1.9176F, -9.1008F, -18.2839F, -1.0074F, -0.1748F, 0.3065F));

        PartDefinition brush_r11 = Broom.addOrReplaceChild("brush_r11", CubeListBuilder.create().texOffs(0, 21).addBox(-3.5F, -1.0F, -0.7F, 6.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(15.6167F, -2.9548F, -1.3363F, -0.07F, 0.4713F, -0.0053F));

        PartDefinition brush_r12 = Broom.addOrReplaceChild("brush_r12", CubeListBuilder.create().texOffs(0, 9).addBox(-21.5948F, -7.363F, -1.0107F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(40.5072F, 1.1388F, 0.1495F, 0.0F, 0.0F, -0.2182F));

        PartDefinition brush_r13 = Broom.addOrReplaceChild("brush_r13", CubeListBuilder.create().texOffs(0, 6).addBox(-17.5398F, -19.9777F, 18.9893F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(30.8463F, -22.9015F, -20.6484F, -1.5708F, 0.2182F, 0.0F));

        PartDefinition brush_r14 = Broom.addOrReplaceChild("brush_r14", CubeListBuilder.create().texOffs(0, 3).addBox(-12.9359F, -0.4819F, -1.0107F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(30.5719F, -2.1549F, 0.1495F, 0.0F, 0.0F, 0.2182F));

        PartDefinition brush_r15 = Broom.addOrReplaceChild("brush_r15", CubeListBuilder.create().texOffs(0, 0).addBox(-17.5398F, -26.9189F, 18.9893F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(40.7816F, -22.9015F, -23.8821F, -1.5708F, -0.2182F, 0.0F));

        PartDefinition brush_r16 = Broom.addOrReplaceChild("brush_r16", CubeListBuilder.create().texOffs(0, 12).addBox(5.9359F, -0.4819F, -1.0107F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(4.8519F, -2.1549F, 0.1495F, 0.0F, 0.0F, -0.2182F));

        PartDefinition brush_r17 = Broom.addOrReplaceChild("brush_r17", CubeListBuilder.create().texOffs(16, 7).addBox(18.0112F, -11.1446F, 8.8462F, 6.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(-8.2999F, -2.9994F, -11.3445F, -0.5042F, 0.1461F, 0.2704F));

        PartDefinition brush_r18 = Broom.addOrReplaceChild("brush_r18", CubeListBuilder.create().texOffs(28, 30).addBox(-2.85F, -0.35F, 0.15F, 2.0F, 1.0F, 1.0F, cube.extend(-0.1f)), PartPose.offsetAndRotation(24.8105F, -2.6569F, 1.2737F, -1.5708F, -0.3927F, -2.4952F));

        PartDefinition brush_r19 = Broom.addOrReplaceChild("brush_r19", CubeListBuilder.create().texOffs(16, 31).addBox(-2.85F, -0.35F, 0.15F, 2.0F, 1.0F, 1.0F, cube.extend(-0.1f)), PartPose.offsetAndRotation(24.6855F, -2.7319F, -0.7263F, -1.5708F, 0.3927F, -2.4952F));

        PartDefinition brush_r20 = Broom.addOrReplaceChild("brush_r20", CubeListBuilder.create().texOffs(5, 27).addBox(-1.85F, -0.85F, -0.6F, 2.0F, 2.0F, 1.0F, cube.extend(-0.1f)), PartPose.offsetAndRotation(24.8105F, -2.5069F, 0.2737F, -1.5708F, 0.0F, -2.8879F));

        PartDefinition brush_r21 = Broom.addOrReplaceChild("brush_r21", CubeListBuilder.create().texOffs(22, 29).addBox(-1.3F, -0.85F, -0.1F, 2.0F, 2.0F, 1.0F, cube), PartPose.offsetAndRotation(24.9105F, -2.7569F, 0.2737F, -1.5708F, 0.0F, -2.8879F));

        PartDefinition brush_r22 = Broom.addOrReplaceChild("brush_r22", CubeListBuilder.create().texOffs(16, 10).addBox(-3.5F, -0.75F, -1.0F, 6.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(15.593F, -4.3238F, 0.9574F, -0.4798F, -0.2173F, -0.3803F));

        PartDefinition brush_r23 = Broom.addOrReplaceChild("brush_r23", CubeListBuilder.create().texOffs(0, 15).addBox(14.5948F, -7.363F, -1.0107F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(-5.0835F, 1.1388F, 0.1495F, 0.0F, 0.0F, 0.2182F));

        PartDefinition brush_r24 = Broom.addOrReplaceChild("brush_r24", CubeListBuilder.create().texOffs(16, 1).addBox(10.5398F, -19.9777F, 18.9893F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(4.5774F, -22.9015F, -20.6484F, -1.5708F, -0.2182F, 0.0F));

        PartDefinition brush_r25 = Broom.addOrReplaceChild("brush_r25", CubeListBuilder.create().texOffs(16, 4).addBox(10.5398F, -26.9189F, 18.9893F, 7.0F, 1.0F, 2.0F, cube), PartPose.offsetAndRotation(-5.3579F, -22.9015F, -23.8821F, -1.5708F, 0.2182F, 0.0F));

        PartDefinition brush_r26 = Broom.addOrReplaceChild("brush_r26", CubeListBuilder.create().texOffs(28, 14).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(24.3868F, -1.8658F, 0.1675F, 0.0235F, 0.0153F, 0.0078F));

        PartDefinition brush_r27 = Broom.addOrReplaceChild("brush_r27", CubeListBuilder.create().texOffs(27, 19).addBox(-2.25F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(25.1723F, -2.2627F, 0.9221F, -0.4725F, 0.0479F, 0.4694F));

        PartDefinition brush_r28 = Broom.addOrReplaceChild("brush_r28", CubeListBuilder.create().texOffs(26, 0).addBox(-2.25F, -0.25F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(25.3869F, -3.1605F, 0.8797F, -0.3567F, 0.1561F, 0.126F));

        PartDefinition brush_r29 = Broom.addOrReplaceChild("brush_r29", CubeListBuilder.create().texOffs(28, 13).addBox(-3.0F, -0.5F, -0.25F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(25.3869F, -3.1605F, -0.5807F, 1.0753F, -0.0934F, -0.0382F));

        PartDefinition brush_r30 = Broom.addOrReplaceChild("brush_r30", CubeListBuilder.create().texOffs(13, 28).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(26.1368F, -2.7085F, 0.1102F, 0.0169F, -0.0224F, 0.3413F));

        PartDefinition brush_r31 = Broom.addOrReplaceChild("brush_r31", CubeListBuilder.create().texOffs(28, 16).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7869F, -4.1605F, -2.0807F, 1.0606F, 0.1902F, -0.123F));

        PartDefinition brush_r32 = Broom.addOrReplaceChild("brush_r32", CubeListBuilder.create().texOffs(28, 17).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7869F, -1.6638F, 2.3583F, 1.0643F, -0.1521F, 0.1014F));

        PartDefinition brush_r33 = Broom.addOrReplaceChild("brush_r33", CubeListBuilder.create().texOffs(28, 20).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7869F, -1.6638F, -2.0807F, -1.0606F, 0.1902F, 0.123F));

        PartDefinition brush_r34 = Broom.addOrReplaceChild("brush_r34", CubeListBuilder.create().texOffs(28, 21).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7869F, -4.1605F, 2.3583F, -1.0606F, -0.1902F, -0.123F));

        PartDefinition brush_r35 = Broom.addOrReplaceChild("brush_r35", CubeListBuilder.create().texOffs(23, 28).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7868F, -0.3658F, 0.1675F, 0.0258F, 0.011F, 0.1823F));

        PartDefinition brush_r36 = Broom.addOrReplaceChild("brush_r36", CubeListBuilder.create().texOffs(13, 29).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(18.7868F, -5.4585F, 0.1102F, 0.0258F, -0.011F, -0.1823F));

        PartDefinition brush_r37 = Broom.addOrReplaceChild("brush_r37", CubeListBuilder.create().texOffs(27, 29).addBox(0.6098F, -49.2614F, 0.349F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(-9.1996F, -46.7052F, 3.5749F, 3.0439F, -0.2828F, -0.347F));

        PartDefinition brush_r38 = Broom.addOrReplaceChild("brush_r38", CubeListBuilder.create().texOffs(30, 7).addBox(7.0594F, -3.6438F, -8.8922F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(6.2172F, -7.8829F, 12.0831F, 0.8986F, 0.444F, -0.0043F));

        PartDefinition brush_r39 = Broom.addOrReplaceChild("brush_r39", CubeListBuilder.create().texOffs(30, 8).addBox(13.6726F, -37.7039F, -0.7546F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(3.4089F, -41.0462F, -11.5614F, -2.6721F, 0.2828F, 0.347F));

        PartDefinition brush_r40 = Broom.addOrReplaceChild("brush_r40", CubeListBuilder.create().texOffs(30, 10).addBox(2.7785F, -1.3667F, 14.3589F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(0.7965F, -3.6683F, -10.3982F, -0.3242F, 0.3791F, -0.237F));

        PartDefinition brush_r41 = Broom.addOrReplaceChild("brush_r41", CubeListBuilder.create().texOffs(30, 11).addBox(-1.4501F, -37.4494F, 22.8777F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(-6.3866F, -37.4876F, -18.1189F, -2.1539F, -0.0039F, -0.444F));

        PartDefinition brush_r42 = Broom.addOrReplaceChild("brush_r42", CubeListBuilder.create().texOffs(13, 30).addBox(11.5032F, -37.4254F, -11.8363F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(-7.6253F, -30.4728F, 24.3398F, 1.8575F, -0.3791F, 0.237F));

        PartDefinition brush_r43 = Broom.addOrReplaceChild("brush_r43", CubeListBuilder.create().texOffs(30, 15).addBox(7.2136F, -12.1221F, -19.9601F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(-7.2301F, -4.4459F, 16.164F, 0.5932F, -0.444F, 0.0043F));

        PartDefinition brush_r44 = Broom.addOrReplaceChild("brush_r44", CubeListBuilder.create().texOffs(30, 18).addBox(15.732F, -32.1159F, -4.2442F, 4.0F, 1.0F, 0.0F, cube), PartPose.offsetAndRotation(7.8284F, -38.2831F, 11.524F, 2.6458F, 0.0039F, 0.444F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public MoonDustBrushModel(ModelPart root) {
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