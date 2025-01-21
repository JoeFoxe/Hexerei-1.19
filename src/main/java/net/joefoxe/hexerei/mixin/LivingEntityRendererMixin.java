package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
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
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0))
    public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {

        Entity vehicle = entity.getVehicle();
        if(vehicle instanceof BroomEntity broom){
            if(broom.deltaMovementOld == null)
                broom.deltaMovementOld = broom.getDeltaMovement();
            float deltaMovementY = Mth.lerp(partialTicks, (float)broom.deltaMovementOld.y(), (float)broom.getDeltaMovement().y());
            float deltaRotation = Mth.lerp(partialTicks, broom.deltaRotationOld, broom.deltaRotation);
            float deltaRotY = Mth.lerp(partialTicks, broom.yRotO, broom.getYRot());

            matrixStack.translate(0f, entity.getBbHeight() / 2.5f, 0f);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-deltaRotation * 2));
            float normalized = (deltaRotY % 360 / 360f);
            float rot = (deltaRotY < 0 ? (1 + normalized) : normalized);

            matrixStack.mulPose(Axis.ZP.rotationDegrees(-Mth.cos(rot * 2 * Mth.PI) * deltaMovementY * 25f));
            matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(rot * 2 * Mth.PI) * deltaMovementY * 25f));
            matrixStack.translate(0f, -entity.getBbHeight() / 2.5f, 0f);

        }
    }

}