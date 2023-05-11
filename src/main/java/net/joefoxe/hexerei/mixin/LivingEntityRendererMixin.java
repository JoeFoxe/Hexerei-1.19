package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {


    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0))
    public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {

        Entity vehicle = entity.getVehicle();
        if(vehicle instanceof BroomEntity broom){
            float deltaMovementY = Mth.lerp(partialTicks, (float)broom.deltaMovementOld.y(), (float)broom.getDeltaMovement().y());
            float deltaRotation = Mth.lerp(partialTicks, (float)broom.deltaRotationOld, (float)broom.deltaRotation);

            boolean hasSeat = broom.getModule(BroomEntity.BroomSlot.SATCHEL).is(ModItems.BROOM_SEAT.get());

            int i = -1;
            if (broom.getPassengers().size() > 1) {
                i = broom.getPassengers().indexOf(entity);
            }
            if(entity instanceof Animal){
                i = 0;
            }
            if (i == 0) {
                matrixStack.translate(0f, entity.getBbHeight() / 2.5f, 0f);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-deltaRotation * 2));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(deltaMovementY * 25f));
                matrixStack.translate(0f, -entity.getBbHeight() / 2.5f, 0f);
            } else if(i == 1) {
                matrixStack.translate(0f, entity.getBbHeight() / 2.5f, 0f);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-deltaRotation * 2));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(deltaMovementY * 25f));
                matrixStack.translate(0f, -entity.getBbHeight() / 2.5f, 0f);
            } else {
                matrixStack.translate(0f, entity.getBbHeight() / 2.5f, 0f);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-deltaRotation * 2));
                if(hasSeat)
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(deltaMovementY * 25f));
                else
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(deltaMovementY * 25f));
                matrixStack.translate(0f, -entity.getBbHeight() / 2.5f, 0f);
            }

        }
    }

}