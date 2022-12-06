package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemRenderer;
import net.joefoxe.hexerei.item.custom.GlassesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {

    public PlayerItemInHandLayerMixin(RenderLayerParent<T, M> parent, ItemInHandRenderer renderer) {
        super(parent, renderer);
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "renderArmWithItem", at = @At(value = "HEAD"), cancellable = true)
    public void poseRightArm(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformType,
                             HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource,
                             int light, CallbackInfo ci) {

        if (stack.getItem() instanceof IThirdPersonItemRenderer item) {
            item.renderThirdPersonItem(this.getParentModel(), entity, stack, humanoidArm, poseStack, bufferSource, light);
            ci.cancel();
        }
        if(stack.getItem() instanceof GlassesItem && Hexerei.glassesZoomKeyPressEvent.zoomWithItemToggled){
            renderArmWithGlasses(entity, stack, humanoidArm, poseStack, bufferSource, light);
            ci.cancel();
        }

    }


    @OnlyIn(Dist.CLIENT)
    private void renderArmWithGlasses(LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_) {
        p_174521_.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.translateAndRotate(p_174521_);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(p_174521_, false);
        Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(p_174518_, p_174519_, ItemTransforms.TransformType.HEAD, false, p_174521_, p_174522_, p_174523_);
        p_174521_.popPose();
    }






}