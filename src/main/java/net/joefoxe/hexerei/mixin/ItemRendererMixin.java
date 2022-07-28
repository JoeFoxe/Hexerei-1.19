//package net.joefoxe.hexerei.mixin;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.joefoxe.hexerei.item.ModItems;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.block.model.ItemTransforms;
//import net.minecraft.client.renderer.entity.ItemRenderer;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import javax.annotation.Nullable;
//
//@Mixin(ItemRenderer.class)
//public abstract class ItemRendererMixin {
//
//    @Shadow
//    public float blitOffset;
//
//    @Shadow
//    public abstract void render(ItemStack stack, ItemTransforms.TransformType transform, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay, BakedModel model);
//
//    @Shadow
//    public abstract BakedModel getModel(ItemStack p_174265_, @Nullable Level p_174266_, @Nullable LivingEntity p_174267_, int p_174268_);
//
//    @Inject(
//            method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
//            at = @At(value = "RETURN")
//    )
//    private void renderInGui(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
//        if (!stack.isEmpty() && stack.getItem() == ModItems.BROOM_KEYCHAIN.get()) {
//            LocalPlayer player = Hexerei.proxy.getPlayer();
//
//            if (player != null) {
//
//                CompoundTag tag = stack.getOrCreateTag();
//                if(tag.contains("Items")){
//                    ListTag list = tag.getList("Items", 10);
//                    ItemStack ammo = ItemStack.of(list.getCompound(0));
//                    if (!ammo.isEmpty()) {
//
//                        PoseStack posestack = RenderSystem.getModelViewStack();
//                        posestack.pushPose();
//
//
//                        posestack.translate(16.0F * (-0.25D) + (8.0F + x) * (1 - 0.4f),
//                                16.0F * (0.25D + 0.025) + (8.0F + y) * (1 - 0.4f),
//                                16.0F + (100.0F + this.blitOffset) * (1 - 0.4f));
//                        posestack.scale(0.4f, 0.4f, 0.4f);
//
//                        //0.4 scale
//                        RenderSystem.applyModelViewMatrix();
//
////                        Minecraft.getInstance().getItemRenderer().renderGuiItem(ammo, x, y);
//
//                        posestack.popPose();
//                        RenderSystem.applyModelViewMatrix();
//                    }
//                }
//
//
//            }
//        }
//    }
//
//
//}