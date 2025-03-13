package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {


    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 1))
    public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {

        Entity vehicle = entity.getVehicle();
        if(vehicle instanceof BroomEntity broom){
            boolean hasSeat = broom.getModule(BroomEntity.BroomSlot.SATCHEL).is(ModItems.BROOM_SEAT.get());
            if(broom.deltaMovementOld == null)
                broom.deltaMovementOld = broom.getDeltaMovement();
            float deltaMovementY = Mth.lerp(partialTicks, (float)broom.deltaMovementOld.y(), (float)broom.getDeltaMovement().y());
            float deltaRotation = Math.clamp(Mth.lerp(partialTicks, broom.deltaRotationOld, broom.deltaRotation), -13 + broom.deltaRotation / 22.5f, 13 + broom.deltaRotation / 22.5f);

            matrixStack.translate(0f, entity.getBbHeight() / 2.5f, 0f);

            int i = broom.getPassengers().indexOf(entity);


            float rotation = 0f;
            if (i == 1){
                rotation = 0f;
            } else if(hasSeat && (i == 0)) {
                rotation = 90f;
            }
            if(entity.getType().is(HexereiTags.Entity.CAN_RIDE_BROOM))
                rotation = 80f;
            if(entity instanceof CrowEntity)
                rotation = 60f;
            if(entity instanceof OwlEntity)
                rotation = 40f;
            if(entity instanceof Cat)
                rotation = 90f;



            if (entity instanceof Cat cat){
//                matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation));
                matrixStack.mulPose(Axis.XP.rotationDegrees(180));
                matrixStack.mulPose(Axis.YP.rotationDegrees(deltaMovementY * 25f));
                matrixStack.mulPose(Axis.YP.rotationDegrees(180));
                matrixStack.mulPose(Axis.XP.rotationDegrees(180));
                matrixStack.mulPose(Axis.YP.rotationDegrees(180));
//                matrixStack.mulPose(Axis.ZP.rotationDegrees(-rotation));
                matrixStack.mulPose(Axis.XP.rotationDegrees(-deltaRotation * 2));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(-deltaRotation * 3f));
            } else {
                matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(180 + deltaMovementY * 25f));
                matrixStack.mulPose(Axis.XP.rotationDegrees(180 - deltaRotation * 3f));
                matrixStack.mulPose(Axis.YP.rotationDegrees(180));
                matrixStack.mulPose(Axis.YP.rotationDegrees(-rotation));
                matrixStack.mulPose(Axis.YP.rotationDegrees(-deltaRotation * 2));
            }


            matrixStack.translate(0f, -entity.getBbHeight() / 2.5f, 0f);

        }
    }

}