package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.HexereiSupporterBenefits;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(value = HumanoidArmorLayer.class, priority = 999)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    @OnlyIn(Dist.CLIENT)
    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("HEAD"), method = "renderArmorPiece", cancellable = true)
    public void renderArmorPiece(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci) {

        ItemStack itemstack = p_117121_.getItemBySlot(p_117122_);
        if (itemstack.getItem() instanceof ArmorItem armoritem) {
            if (armoritem.getSlot() == p_117122_) {
                this.getParentModel().copyPropertiesTo(p_117124_);
                this.setPartVisibility(p_117124_, p_117122_);
                net.minecraft.client.model.Model model = getArmorModelHook(p_117121_, itemstack, p_117122_, p_117124_);
                boolean flag1 = itemstack.hasFoil();
                if(armoritem instanceof WitchArmorItem witchArmorItem){
                    int i = (witchArmorItem).getColor(itemstack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    if (p_117121_ instanceof Player player && HexereiSupporterBenefits.matchesSupporterUUID(player.getUUID())) // - 80 + ((int)(Math.sin(p_117121_.level.nextSubTickCount() / 100f) * (80)))
                    {
                        this.renderModel(p_117119_, p_117120_, 15728880, flag1, model, f, f1, f2, this.getArmorResource(p_117121_, itemstack, p_117122_, null));
                        this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_117121_, itemstack, p_117122_, "overlay"));
                        ci.cancel();
                    }
                    if (p_117121_ instanceof Player player && !HexereiSupporterBenefits.matchesSupporterUUID(player.getUUID())) // - 80 + ((int)(Math.sin(p_117121_.level.nextSubTickCount() / 100f) * (80)))
                    {
                        this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, f, f1, f2, this.getArmorResource(p_117121_, itemstack, p_117122_, null));
                        this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_117121_, itemstack, p_117122_, "overlay"));
                        ci.cancel();
                    }
                    if (!(p_117121_ instanceof Player)) // - 80 + ((int)(Math.sin(p_117121_.level.nextSubTickCount() / 100f) * (80)))
                    {
                        this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, f, f1, f2, this.getArmorResource(p_117121_, itemstack, p_117122_, null));
                        this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_117121_, itemstack, p_117122_, "overlay"));
                        ci.cancel();
                    }
                }
            }
        }
    }

    private boolean usesInnerModel(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }

    private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, p_117114_, p_117115_, p_117116_, 1.0F);
    }

    public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = HumanoidArmorLayer.ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            HumanoidArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    protected void setPartVisibility(A p_117126_, EquipmentSlot p_117127_) {
        p_117126_.setAllVisible(false);
        switch (p_117127_) {
            case HEAD -> {
                p_117126_.head.visible = true;
                p_117126_.hat.visible = true;
            }
            case CHEST -> {
                p_117126_.body.visible = true;
                p_117126_.rightArm.visible = true;
                p_117126_.leftArm.visible = true;
            }
            case LEGS -> {
                p_117126_.body.visible = true;
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
            }
            case FEET -> {
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
            }
        }

    }

}