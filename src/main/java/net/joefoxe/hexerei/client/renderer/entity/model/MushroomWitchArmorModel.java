// Made with Blockbench 4.2.3
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.joefoxe.hexerei.client.renderer.entity.model;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MushroomWitchArmorModel {
    public static MeshDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

		PartDefinition Body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition RightBoot = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition LeftBoot = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition LeftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition RightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition Head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition Hat = root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);


//		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition head_r1_r1 = Head.addOrReplaceChild("head_r1_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-4.825F, -32.95F, -5.25F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9181F, 24.0542F, -9.2644F, -0.3048F, -0.0186F, -0.1276F));

		PartDefinition head_r2_r1 = Head.addOrReplaceChild("head_r2_r1", CubeListBuilder.create().texOffs(74, 27).addBox(-1.25F, -0.75F, -1.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4849F, -11.8984F, 3.584F, -0.6773F, 0.749F, 1.4749F));

		PartDefinition head_r4_r1 = Head.addOrReplaceChild("head_r4_r1", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -3.0F, -0.675F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0402F, -10.5987F, 3.6368F, 0.1274F, 1.1259F, -3.0809F));

		PartDefinition head_r3_r1 = Head.addOrReplaceChild("head_r3_r1", CubeListBuilder.create().texOffs(50, 65).addBox(0.5F, -1.0F, 0.1F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5799F, -11.1174F, 4.5643F, -0.4525F, 1.0241F, 2.2393F));

		PartDefinition head_r4_r2 = Head.addOrReplaceChild("head_r4_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.8382F, -32.337F, -14.6321F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(42, 11).addBox(-0.8382F, -32.337F, -16.6321F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 35).addBox(-0.8382F, -32.337F, -4.6321F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 39).addBox(7.1618F, -32.337F, -4.6321F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(75, 67).addBox(7.1618F, -32.337F, -16.6321F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1908F, 27.6664F, -0.4374F, -0.3048F, -0.0186F, -0.1276F));

		PartDefinition head_r5_r1 = Head.addOrReplaceChild("head_r5_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.025F, -1.25F, -1.75F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.3381F, -4.379F, -0.6137F, -0.1739F, -0.0186F, -0.1276F));

		PartDefinition head_r4_r3 = Head.addOrReplaceChild("head_r4_r3", CubeListBuilder.create().texOffs(67, 45).addBox(-1.3562F, -1.0008F, -5.6069F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(68, 7).addBox(-1.3562F, -1.0008F, 6.3931F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4832F, -5.0783F, -1.5777F, -0.3048F, -0.0186F, -0.1276F));

		PartDefinition head_r5_r2 = Head.addOrReplaceChild("head_r5_r2", CubeListBuilder.create().texOffs(39, 19).addBox(-0.5F, -0.5F, 0.175F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.299F, -5.8697F, -7.298F, 0.0006F, -0.0186F, -0.1276F));

		PartDefinition head_r7_r1 = Head.addOrReplaceChild("head_r7_r1", CubeListBuilder.create().texOffs(6, 79).addBox(-1.5F, -1.0F, 0.3F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9389F, -1.0709F, 5.8858F, 0.0006F, -0.0186F, -0.1276F));

		PartDefinition head_r7_r2 = Head.addOrReplaceChild("head_r7_r2", CubeListBuilder.create().texOffs(56, 19).addBox(-4.125F, -0.996F, -1.15F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.806F, -4.2741F, 4.4655F, -0.3209F, 0.3138F, -0.2357F));

		PartDefinition head_r8_r1 = Head.addOrReplaceChild("head_r8_r1", CubeListBuilder.create().texOffs(39, 19).addBox(-0.5F, -0.575F, 0.175F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8739F, -2.0664F, 5.5918F, 0.0331F, 0.271F, -0.2134F));

		PartDefinition head_r6_r1 = Head.addOrReplaceChild("head_r6_r1", CubeListBuilder.create().texOffs(67, 55).addBox(-2.4F, -0.998F, -0.425F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.806F, -4.2741F, 4.4655F, -0.3713F, -0.5971F, 0.0821F));

		PartDefinition head_r5_r3 = Head.addOrReplaceChild("head_r5_r3", CubeListBuilder.create().texOffs(47, 43).addBox(-0.5043F, -34.2898F, -5.275F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1353F, 25.7266F, -12.2402F, -0.3521F, -0.5156F, 0.0458F));

		PartDefinition head_r6_r2 = Head.addOrReplaceChild("head_r6_r2", CubeListBuilder.create().texOffs(34, 6).addBox(0.4283F, -34.4329F, -5.8296F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0467F, 25.4919F, -10.5342F, -0.4114F, 0.7224F, -0.4143F));

		PartDefinition head_r7_r3 = Head.addOrReplaceChild("head_r7_r3", CubeListBuilder.create().texOffs(30, 17).addBox(3.7904F, -0.9892F, 2.3201F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4832F, -5.0783F, -1.5777F, -0.4441F, -0.7977F, 0.1948F));

		PartDefinition head_r8_r2 = Head.addOrReplaceChild("head_r8_r2", CubeListBuilder.create().texOffs(30, 12).addBox(-2.4242F, -0.989F, 1.265F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4832F, -5.0783F, -1.5777F, -0.353F, 0.5198F, -0.3144F));

		PartDefinition head_r9_r1 = Head.addOrReplaceChild("head_r9_r1", CubeListBuilder.create().texOffs(0, 23).addBox(-4.4901F, -34.6229F, -3.5319F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1243F, 23.6123F, -11.8088F, -0.3481F, 0.0316F, 0.0844F));

		PartDefinition head_r10_r1 = Head.addOrReplaceChild("head_r10_r1", CubeListBuilder.create().texOffs(32, 15).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2424F, -8.3702F, 0.9323F, -0.435F, 0.1227F, 0.3078F));

		PartDefinition head_r11_r1 = Head.addOrReplaceChild("head_r11_r1", CubeListBuilder.create().texOffs(64, 19).addBox(-1.75F, -2.75F, -2.75F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9677F, -11.2478F, 2.7025F, -0.5659F, 0.6012F, 1.0441F));

		PartDefinition head_r12_r1 = Head.addOrReplaceChild("head_r12_r1", CubeListBuilder.create().texOffs(20, 54).addBox(-3.0F, -2.0F, -3.5F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9857F, -10.1287F, 1.9956F, -0.4111F, 0.298F, 0.742F));

		PartDefinition mushies = Head.addOrReplaceChild("mushies", CubeListBuilder.create(), PartPose.offset(-5.5083F, -8.9083F, -4.4917F));

		PartDefinition mushy = mushies.addOrReplaceChild("mushy", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = mushy.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 79).addBox(-0.7667F, -0.5667F, -1.7583F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(46, 78).addBox(-0.7667F, -0.5667F, 0.7417F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 69).addBox(-1.4917F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(35, 76).addBox(1.0083F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(76, 19).addBox(-0.7417F, -1.0917F, -1.0083F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(62, 0).addBox(-0.2417F, -0.0917F, -0.5083F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2987F, 0.4791F, -0.3165F));

		PartDefinition mushy7 = mushies.addOrReplaceChild("mushy7", CubeListBuilder.create(), PartPose.offsetAndRotation(9.7108F, 2.3649F, 9.7881F, -0.2617F, 1.0842F, 0.6855F));

		PartDefinition cube_r2 = mushy7.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(41, 27).addBox(-0.7667F, -0.5667F, -1.7583F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(47, 27).addBox(-0.7667F, -0.5667F, 0.7417F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 36).addBox(-1.4917F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(24, 39).addBox(1.0083F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(55, 45).addBox(-0.7417F, -1.0917F, -1.0083F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(5, 28).addBox(-0.2417F, -0.0917F, -0.5083F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2108F, 0.0691F, 0.1152F, 0.2987F, 0.4791F, -0.3165F));

		PartDefinition mushy2 = mushies.addOrReplaceChild("mushy2", CubeListBuilder.create(), PartPose.offsetAndRotation(4.4608F, -1.0686F, 0.1566F, -0.7316F, -0.819F, 0.9399F));

		PartDefinition cube_r3 = mushy2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(75, 77).addBox(-0.7667F, -0.5667F, -1.7583F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(40, 78).addBox(-0.7667F, -0.5667F, 0.7417F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(56, 30).addBox(-1.4917F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(62, 67).addBox(1.0083F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 76).addBox(-0.7417F, -1.0917F, -1.0083F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(27, 23).addBox(-0.2417F, -0.0917F, -0.5083F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2108F, 0.0691F, 0.1152F, 0.2987F, 0.4791F, -0.3165F));

		PartDefinition mushy3 = mushies.addOrReplaceChild("mushy3", CubeListBuilder.create(), PartPose.offsetAndRotation(3.2F, -3.0F, 8.75F, 2.2365F, 1.191F, 2.4653F));

		PartDefinition cube_r4 = mushy3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(39, 76).addBox(-0.4213F, -1.9466F, -1.0488F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(56, 14).addBox(-1.1463F, -1.9716F, -2.7738F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(75, 71).addBox(-0.3963F, -2.4716F, -2.7988F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(56, 11).addBox(1.3537F, -1.9716F, -2.7738F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 0).addBox(-0.4213F, -1.9466F, -3.5488F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 45).addBox(0.1037F, -1.4716F, -2.2988F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3538F, 3.2293F, 2.0304F, 2.2061F, 0.9749F, 1.4276F));

		PartDefinition cube_r5 = mushy3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(24, 42).addBox(-0.0083F, -0.4965F, -2.2537F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3538F, 3.2293F, 2.0304F, 2.3104F, 0.8398F, 1.1523F));

		PartDefinition mushy4 = mushies.addOrReplaceChild("mushy4", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -3.0F, 8.75F, 1.5827F, 1.2524F, 1.8904F));

		PartDefinition cube_r6 = mushy4.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(45, 76).addBox(-0.7667F, -0.5667F, -1.7583F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(77, 55).addBox(-0.7667F, -0.5667F, 0.7417F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 56).addBox(-1.4917F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(56, 27).addBox(1.0083F, -0.5917F, -0.9833F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(75, 74).addBox(-0.7417F, -1.0917F, -1.0083F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 53).addBox(-0.2417F, -0.0917F, -0.5083F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2987F, 0.4791F, -0.3165F));

		PartDefinition cube_r7 = mushy4.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(46, 0).addBox(-0.55F, -1.0F, -0.45F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2503F, 2.0466F, 0.8516F, 0.6922F, 0.338F, -0.1381F));

		PartDefinition mushy5 = mushies.addOrReplaceChild("mushy5", CubeListBuilder.create(), PartPose.offsetAndRotation(11.0167F, -2.2189F, 3.7112F, 1.5827F, -1.2524F, -1.8904F));

		PartDefinition cube_r8 = mushy5.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.2533F, 0.1374F, -1.02F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7224F, 5.8806F, -1.019F, -3.1039F, 0.1595F, 0.2098F));

		PartDefinition cube_r9 = mushy5.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(7, 0).addBox(-1.122F, -0.0019F, -0.5832F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(37, 13).addBox(-1.1301F, -0.6287F, -1.1515F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7224F, 5.8806F, -1.019F, 2.4383F, 0.0923F, 0.0029F));

		PartDefinition cube_r10 = mushy5.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(53, 27).addBox(-0.2917F, -0.5662F, -0.2793F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 28).addBox(-0.2835F, 0.0606F, 0.289F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7181F, 0.9594F, 0.2744F, 0.5798F, -0.4421F, 0.2535F));

		PartDefinition cube_r11 = mushy5.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(27, 27).addBox(-0.5301F, 1.006F, -0.0569F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7181F, 0.9594F, 0.2744F, 0.9466F, -0.2629F, 0.125F));

		PartDefinition mushy6 = mushies.addOrReplaceChild("mushy6", CubeListBuilder.create(), PartPose.offsetAndRotation(10.0927F, -0.4189F, 5.3043F, 2.1861F, -0.8682F, -2.2129F));

		PartDefinition cube_r12 = mushy6.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(52, 47).addBox(-0.2917F, -0.5662F, -0.2793F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(6, 24).addBox(-0.2835F, 0.0606F, 0.289F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1339F, -0.3428F, -0.6507F, 0.5798F, -0.4421F, 0.2535F));

		PartDefinition cube_r13 = mushy6.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 24).addBox(-0.5301F, 1.006F, -0.0569F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1339F, -0.3428F, -0.6507F, 0.9466F, -0.2629F, 0.125F));

		PartDefinition shelf_mushy = mushies.addOrReplaceChild("shelf_mushy", CubeListBuilder.create(), PartPose.offset(9.0566F, -2.3066F, 1.8474F));

		PartDefinition cube_r14 = shelf_mushy.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(7, 13).addBox(0.425F, -0.45F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1206F, -0.2494F, 1.1236F));

		PartDefinition cube_r15 = shelf_mushy.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(33, 27).addBox(-1.5F, -0.25F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5029F, 0.7149F, 0.2067F, 0.2765F, 0.0013F, 0.0044F));

		PartDefinition cube_r16 = shelf_mushy.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(37, 12).addBox(-1.5F, -0.5F, -0.1F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4861F, 0.499F, -0.3543F, 1.1139F, -0.0253F, 0.0098F));

		PartDefinition cube_r17 = shelf_mushy.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(0, 13).addBox(-0.45F, -0.575F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -0.0132F, 0.004F, 0.1069F, 0.2555F, -1.1601F));

		PartDefinition shelf_mushy2 = mushies.addOrReplaceChild("shelf_mushy2", CubeListBuilder.create(), PartPose.offsetAndRotation(6.5534F, -4.1734F, 3.7774F, 0.0039F, 0.0869F, 0.299F));

		PartDefinition cube_r18 = shelf_mushy2.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(0, 12).addBox(0.0059F, 0.4579F, -0.5025F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.025F, -0.5716F, -0.0213F, 0.1206F, -0.2494F, 1.1236F));

		PartDefinition cube_r19 = shelf_mushy2.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(68, 35).addBox(-1.5F, -0.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5221F, 0.1433F, 0.1854F, 0.2765F, 0.0013F, 0.0044F));

		PartDefinition cube_r20 = shelf_mushy2.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(54, 11).addBox(-1.5F, -0.5F, -0.1F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5388F, -0.0727F, -0.3756F, 1.1139F, -0.0253F, 0.0098F));

		PartDefinition cube_r21 = shelf_mushy2.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(7, 12).addBox(-0.45F, -0.575F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.975F, -0.5849F, -0.0173F, 0.1069F, 0.2555F, -1.1601F));

		PartDefinition shelf_mushy6 = mushies.addOrReplaceChild("shelf_mushy6", CubeListBuilder.create(), PartPose.offsetAndRotation(6.5534F, -0.678F, 9.3303F, -2.2967F, -0.0617F, -2.7425F));

		PartDefinition cube_r22 = shelf_mushy6.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 0).addBox(0.0059F, 0.4579F, -0.5025F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.025F, -0.5716F, -0.0213F, 0.1206F, -0.2494F, 1.1236F));

		PartDefinition cube_r23 = shelf_mushy6.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(15, 56).addBox(-1.5F, -0.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5221F, 0.1433F, 0.1854F, 0.2765F, 0.0013F, 0.0044F));

		PartDefinition cube_r24 = shelf_mushy6.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(22, 36).addBox(-1.5F, -0.5F, -0.1F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5388F, -0.0727F, -0.3756F, 1.1139F, -0.0253F, 0.0098F));

		PartDefinition cube_r25 = shelf_mushy6.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(0, 1).addBox(-0.45F, -0.575F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.975F, -0.5849F, -0.0173F, 0.1069F, 0.2555F, -1.1601F));

		PartDefinition shelf_mushy5 = mushies.addOrReplaceChild("shelf_mushy5", CubeListBuilder.create(), PartPose.offsetAndRotation(9.5534F, -4.3263F, 5.4356F, -0.1467F, 0.0617F, 0.3991F));

		PartDefinition cube_r26 = shelf_mushy5.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(7, 0).addBox(0.0059F, 0.4579F, -0.5025F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.025F, -0.5716F, -0.0213F, 0.1206F, -0.2494F, 1.1236F));

		PartDefinition cube_r27 = shelf_mushy5.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(62, 35).addBox(-1.5F, -0.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5221F, 0.1433F, 0.1854F, 0.2765F, 0.0013F, 0.0044F));

		PartDefinition cube_r28 = shelf_mushy5.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(46, 3).addBox(-1.5F, -0.5F, -0.1F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5388F, -0.0727F, -0.3756F, 1.1139F, -0.0253F, 0.0098F));

		PartDefinition cube_r29 = shelf_mushy5.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(7, 1).addBox(-0.45F, -0.575F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.975F, -0.5849F, -0.0173F, 0.1069F, 0.2555F, -1.1601F));

		PartDefinition shelf_mushy4 = mushies.addOrReplaceChild("shelf_mushy4", CubeListBuilder.create(), PartPose.offsetAndRotation(4.1534F, -3.9732F, 6.0708F, -2.4013F, 1.2566F, -2.1275F));

		PartDefinition cube_r30 = shelf_mushy4.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(0, 11).addBox(0.0059F, 0.4579F, -0.5025F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.025F, -0.5716F, -0.0213F, 0.1206F, -0.2494F, 1.1236F));

		PartDefinition cube_r31 = shelf_mushy4.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(30, 67).addBox(-1.5F, -0.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5221F, 0.1433F, 0.1854F, 0.2765F, 0.0013F, 0.0044F));

		PartDefinition cube_r32 = shelf_mushy4.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(47, 47).addBox(-1.5F, -0.5F, -0.1F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5388F, -0.0727F, -0.3756F, 1.1139F, -0.0253F, 0.0098F));

		PartDefinition cube_r33 = shelf_mushy4.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(7, 11).addBox(-0.45F, -0.575F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.975F, -0.5849F, -0.0173F, 0.1069F, 0.2555F, -1.1601F));

		PartDefinition shelf_mushy3 = mushies.addOrReplaceChild("shelf_mushy3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3816F, 0.3119F, 3.9353F, -0.202F, -0.0829F, -0.3843F));

		PartDefinition cube_r34 = shelf_mushy3.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(0, 23).addBox(-0.5F, -0.45F, -0.425F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1236F, -0.2494F, -0.1206F));

		PartDefinition cube_r35 = shelf_mushy3.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(0, 28).addBox(-0.5F, -0.25F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2067F, 0.7149F, 1.5029F, 0.0044F, 0.0013F, -0.2765F));

		PartDefinition cube_r36 = shelf_mushy3.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(22, 32).addBox(-0.1F, -0.5F, -1.5F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3543F, 0.499F, 1.4861F, 0.0098F, -0.0253F, -1.1139F));

		PartDefinition cube_r37 = shelf_mushy3.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(6, 23).addBox(-0.5F, -0.575F, 0.45F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.004F, -0.0132F, 3.0F, -1.1601F, 0.2555F, -0.1069F));

//		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(30, 29).addBox(-5.0F, -0.5F, -3.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
//				.texOffs(24, 43).addBox(-4.5F, 7.501F, -2.501F, 9.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body_r1 = Body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(65, 67).addBox(-5.0F, -16.5F, 0.01F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 12).addBox(-5.0F, -16.5F, -2.99F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9102F, 24.2703F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition body_r2 = Body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(74, 9).addBox(-1.025F, -2.7F, -1.5F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2869F, 14.7125F, -1.49F, -0.5236F, 0.0F, -0.1745F));

		PartDefinition body_r3 = Body.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(74, 2).addBox(-0.975F, -2.7F, -1.5F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2869F, 14.7125F, -1.49F, -0.5236F, 0.0F, 0.1745F));

		PartDefinition body_r4 = Body.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(0, 0).addBox(3.0F, -16.5F, -2.99F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.9102F, 24.2703F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition body_r5 = Body.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(55, 67).addBox(-1.0F, -4.5F, -1.5F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7661F, 11.758F, 1.51F, 0.0F, 0.0F, -0.1745F));

		PartDefinition body_r6 = Body.addOrReplaceChild("body_r6", CubeListBuilder.create().texOffs(0, 35).addBox(-4.975F, -10.5F, -0.75F, 10.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.01F, 11.2618F, 2.7227F, 0.1317F, 0.0F, 0.0F));

//		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.0F, 1.0F, 0.0F, -0.0133F, -0.0125F, -0.0853F));

		PartDefinition rightarm_r1 = RightArm.addOrReplaceChild("rightarm_r1", CubeListBuilder.create().texOffs(61, 59).addBox(-2.2814F, 1.8917F, -2.9187F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6951F, 5.1954F, 0.2218F, -0.014F, 0.0821F, 0.1306F));

		PartDefinition rightarm_r2 = RightArm.addOrReplaceChild("rightarm_r2", CubeListBuilder.create().texOffs(0, 53).addBox(-1.969F, -8.4881F, -2.5253F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6951F, 5.9454F, 0.2218F, -0.0135F, -0.0064F, 0.0445F));

		PartDefinition rightarm_r3 = RightArm.addOrReplaceChild("rightarm_r3", CubeListBuilder.create().texOffs(62, 0).addBox(-1.75F, -0.5F, -2.25F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1622F, 7.9253F, 3.2088F, -0.2886F, 0.8566F, -0.0735F));

		PartDefinition rightarm_r4 = RightArm.addOrReplaceChild("rightarm_r4", CubeListBuilder.create().texOffs(0, 69).addBox(-4.75F, 1.5F, 0.25F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(38, 54).addBox(-4.25F, -1.5F, 0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0983F, 3.2608F, -0.7944F, -0.0041F, 0.7798F, 0.0409F));

		PartDefinition rightarm_r5 = RightArm.addOrReplaceChild("rightarm_r5", CubeListBuilder.create().texOffs(27, 27).addBox(-1.25F, -1.25F, -0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5781F, 0.1539F, 2.0367F, -0.0041F, -0.791F, 0.0467F));

//		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition leftarm_r1 = LeftArm.addOrReplaceChild("leftarm_r1", CubeListBuilder.create().texOffs(56, 37).addBox(-3.7186F, 1.8917F, -2.9187F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7068F, 5.1989F, 0.1468F, -0.0217F, -0.0703F, -0.1311F));

		PartDefinition leftarm_r2 = LeftArm.addOrReplaceChild("leftarm_r2", CubeListBuilder.create().texOffs(47, 49).addBox(-3.031F, -8.4881F, -2.5253F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(44, 0).addBox(-3.531F, -9.2381F, -3.0253F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7068F, 5.9489F, 0.1468F, -0.0201F, 0.0188F, -0.0457F));

		PartDefinition leftarm_r3 = LeftArm.addOrReplaceChild("leftarm_r3", CubeListBuilder.create().texOffs(34, 0).addBox(-2.25F, -0.5F, -2.25F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1739F, 7.9288F, 3.1338F, -0.2964F, -0.8435F, 0.0782F));

		PartDefinition leftarm_r4 = LeftArm.addOrReplaceChild("leftarm_r4", CubeListBuilder.create().texOffs(62, 45).addBox(2.0F, 1.5F, 0.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 23).addBox(2.5F, -1.5F, 0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5866F, 3.2643F, -0.8694F, -0.0132F, -0.7673F, -0.0356F));

		PartDefinition feathers = LeftArm.addOrReplaceChild("feathers", CubeListBuilder.create(), PartPose.offset(-1.4287F, 0.229F, 0.15F));

		PartDefinition feather_r1 = feathers.addOrReplaceChild("feather_r1", CubeListBuilder.create(), PartPose.offsetAndRotation(3.3075F, -3.5968F, 2.3999F, -0.5107F, -0.5313F, -0.6482F));

		PartDefinition feather_r2 = feathers.addOrReplaceChild("feather_r2", CubeListBuilder.create(), PartPose.offsetAndRotation(4.4492F, -1.1555F, 3.3527F, -1.7342F, -0.5874F, 0.389F));

		PartDefinition feather_r3 = feathers.addOrReplaceChild("feather_r3", CubeListBuilder.create(), PartPose.offsetAndRotation(3.2999F, 0.6819F, 3.9032F, -2.0813F, -0.3443F, 1.1283F));

		PartDefinition feather_r4 = feathers.addOrReplaceChild("feather_r4", CubeListBuilder.create(), PartPose.offsetAndRotation(3.3075F, -3.5968F, -2.3999F, 0.5107F, 0.5313F, -0.6482F));

		PartDefinition feather_r5 = feathers.addOrReplaceChild("feather_r5", CubeListBuilder.create(), PartPose.offsetAndRotation(3.2999F, 0.6819F, -3.9032F, 2.0813F, 0.3443F, 1.1283F));

		PartDefinition feather_r6 = feathers.addOrReplaceChild("feather_r6", CubeListBuilder.create(), PartPose.offsetAndRotation(3.1114F, -1.5281F, 3.6198F, -2.0357F, -0.4057F, 1.0041F));

		PartDefinition feather_r7 = feathers.addOrReplaceChild("feather_r7", CubeListBuilder.create(), PartPose.offsetAndRotation(3.1114F, -1.5281F, 3.6198F, -1.4383F, -0.5375F, 0.5115F));

		PartDefinition feather_r8 = feathers.addOrReplaceChild("feather_r8", CubeListBuilder.create(), PartPose.offsetAndRotation(3.1114F, -1.5281F, -3.6198F, 1.4383F, 0.5375F, 0.5115F));

		PartDefinition feather_r9 = feathers.addOrReplaceChild("feather_r9", CubeListBuilder.create(), PartPose.offsetAndRotation(3.1114F, -1.5281F, -3.6198F, 2.0357F, 0.4057F, 1.0041F));

		PartDefinition feather_r10 = feathers.addOrReplaceChild("feather_r10", CubeListBuilder.create(), PartPose.offsetAndRotation(4.4492F, -1.1555F, -3.3527F, 1.7342F, 0.5874F, 0.389F));

		PartDefinition feather_r11 = feathers.addOrReplaceChild("feather_r11", CubeListBuilder.create(), PartPose.offsetAndRotation(4.7935F, -3.8956F, -1.885F, 0.2882F, 0.5236F, -0.4894F));

		PartDefinition feather_r12 = feathers.addOrReplaceChild("feather_r12", CubeListBuilder.create(), PartPose.offsetAndRotation(2.909F, -3.7249F, -0.1261F, -0.2465F, -0.2182F, -0.4841F));

		PartDefinition feather_r13 = feathers.addOrReplaceChild("feather_r13", CubeListBuilder.create(), PartPose.offsetAndRotation(2.909F, -3.7249F, 0.1261F, 0.0243F, 0.436F, -0.3914F));

		PartDefinition feather_r14 = feathers.addOrReplaceChild("feather_r14", CubeListBuilder.create(), PartPose.offsetAndRotation(4.8214F, -3.843F, 2.1146F, -0.4088F, -0.3926F, -0.4746F));

		PartDefinition feather_r15 = feathers.addOrReplaceChild("feather_r15", CubeListBuilder.create(), PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, -0.5627F, -0.3357F, 1.3216F));

		PartDefinition feather_r16 = feathers.addOrReplaceChild("feather_r16", CubeListBuilder.create(), PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, 0.6259F, 0.2169F, 1.1857F));

		PartDefinition feather_r17 = feathers.addOrReplaceChild("feather_r17", CubeListBuilder.create(), PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, -0.008F, -0.1183F, 1.2219F));

		PartDefinition feather_r18 = feathers.addOrReplaceChild("feather_r18", CubeListBuilder.create(), PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, 0.6259F, 0.2169F, 1.1857F));

		PartDefinition feather_r19 = feathers.addOrReplaceChild("feather_r19", CubeListBuilder.create(), PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, -0.0081F, 0.2308F, 1.2191F));

		PartDefinition feather_r20 = feathers.addOrReplaceChild("feather_r20", CubeListBuilder.create(), PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, -0.5627F, -0.3357F, 1.3216F));

		PartDefinition feather_r21 = feathers.addOrReplaceChild("feather_r21", CubeListBuilder.create(), PartPose.offsetAndRotation(6.401F, -2.0952F, 2.0806F, -0.5627F, -0.3357F, 0.6235F));

		PartDefinition feather_r22 = feathers.addOrReplaceChild("feather_r22", CubeListBuilder.create(), PartPose.offsetAndRotation(6.3731F, -2.1478F, -1.919F, 0.6259F, 0.2169F, 0.4876F));

		PartDefinition feather_r23 = feathers.addOrReplaceChild("feather_r23", CubeListBuilder.create(), PartPose.offsetAndRotation(5.679F, -3.3455F, 0.1018F, -0.0109F, 0.0101F, 0.2609F));

		PartDefinition feather_r24 = feathers.addOrReplaceChild("feather_r24", CubeListBuilder.create(), PartPose.offsetAndRotation(5.679F, -3.3455F, 0.1018F, 0.4006F, -0.1312F, -0.2155F));

		PartDefinition feather_r25 = feathers.addOrReplaceChild("feather_r25", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0712F, -2.7099F, 2.5799F, -0.9739F, -0.6044F, 0.6491F));

		PartDefinition feather_r26 = feathers.addOrReplaceChild("feather_r26", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0712F, -2.7099F, -2.5799F, 0.9739F, 0.6044F, 0.6491F));

//		PartDefinition Belt = partdefinition.addOrReplaceChild("Belt", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//		PartDefinition RightBoot = partdefinition.addOrReplaceChild("RightBoot", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition feather_r27 = RightBoot.addOrReplaceChild("feather_r27", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.4017F, -8.6716F, 0.0985F, 0.6259F, -0.2169F, -1.1857F));

		PartDefinition feather_r28 = RightBoot.addOrReplaceChild("feather_r28", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0794F, 6.8242F, -0.7251F, 0.652F, 0.1706F, -1.1512F));

		PartDefinition feather_r29 = RightBoot.addOrReplaceChild("feather_r29", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.4226F, 6.7928F, 3.351F, -1.0817F, 0.55F, -1.552F));

		PartDefinition feather_r30 = RightBoot.addOrReplaceChild("feather_r30", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0794F, 6.8242F, -0.7251F, -0.1277F, 0.0007F, -1.1415F));

		PartDefinition feather_r31 = RightBoot.addOrReplaceChild("feather_r31", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0794F, 6.8242F, 0.7251F, -0.5606F, 0.1156F, -1.1423F));

		PartDefinition feather_r32 = RightBoot.addOrReplaceChild("feather_r32", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.4017F, -9.0716F, -0.0985F, -0.6259F, 0.2169F, -1.1857F));

		PartDefinition rightboot_r1 = RightBoot.addOrReplaceChild("rightboot_r1", CubeListBuilder.create().texOffs(35, 65).addBox(-2.5F, -17.75F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(23, 75).addBox(-2.5F, -14.75F, -3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(30, 64).addBox(-2.0F, -13.75F, -4.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0873F, 0.0F));

		PartDefinition rightboot_r2 = RightBoot.addOrReplaceChild("rightboot_r2", CubeListBuilder.create().texOffs(56, 27).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.75F, 0.0F, 0.0718F, 0.0936F, -0.175F));

//		PartDefinition LeftBoot = partdefinition.addOrReplaceChild("LeftBoot", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition feather_r33 = LeftBoot.addOrReplaceChild("feather_r33", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0794F, 6.8242F, -0.7251F, -0.1277F, -0.0007F, 1.1415F));

		PartDefinition feather_r34 = LeftBoot.addOrReplaceChild("feather_r34", CubeListBuilder.create(), PartPose.offsetAndRotation(2.4226F, 6.7928F, 3.351F, -1.0817F, -0.55F, 1.552F));

		PartDefinition feather_r35 = LeftBoot.addOrReplaceChild("feather_r35", CubeListBuilder.create(), PartPose.offsetAndRotation(8.4017F, -9.0716F, -0.0985F, -0.6259F, -0.2169F, 1.1857F));

		PartDefinition feather_r36 = LeftBoot.addOrReplaceChild("feather_r36", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0794F, 6.8242F, 0.7251F, -0.5606F, -0.1156F, 1.1423F));

		PartDefinition feather_r37 = LeftBoot.addOrReplaceChild("feather_r37", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0794F, 6.8242F, -0.7251F, 0.652F, -0.1706F, 1.1512F));

		PartDefinition feather_r38 = LeftBoot.addOrReplaceChild("feather_r38", CubeListBuilder.create(), PartPose.offsetAndRotation(8.4017F, -8.6716F, 0.0985F, 0.6259F, 0.2169F, 1.1857F));

		PartDefinition leftboot_r1 = LeftBoot.addOrReplaceChild("leftboot_r1", CubeListBuilder.create().texOffs(15, 64).addBox(-2.5F, -17.75F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(11, 75).addBox(-2.5F, -14.75F, -3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 53).addBox(-2.0F, -13.75F, -4.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -0.0873F, 0.0F));

		PartDefinition leftboot_r2 = LeftBoot.addOrReplaceChild("leftboot_r2", CubeListBuilder.create().texOffs(56, 11).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.75F, 0.0F, 0.0718F, -0.0936F, 0.175F));

		return mesh;
	}


	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

}