package net.joefoxe.hexerei.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GlassesCurioRender implements ICurioRenderer {
    public static void register() {
        CuriosRendererRegistry.register(ModItems.READING_GLASSES.get(), GlassesCurioRender::new);
    }

    /**
     * Performs rendering of the curio.
     *
     * @param stack
     * @param slotContext       The slot context of the curio that is being rendered
     * @param matrixStack
     * @param renderLayerParent
     * @param renderTypeBuffer
     * @param light
     * @param limbSwing
     * @param limbSwingAmount
     * @param partialTicks
     * @param ageInTicks
     * @param netHeadYaw
     * @param headPitch
     */
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();
        if (renderLayerParent.getModel() instanceof PlayerModel playerModel) {
            playerModel.getHead().translateAndRotate(matrixStack);
        }
        matrixStack.scale(0.60f, 0.60f, 0.60f);
        matrixStack.mulPose(new Quaternion(0, 0, 180, true));
        matrixStack.translate(0.0D, 0.4D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack,
                ItemTransforms.TransformType.HEAD,
                light,
                light,
                matrixStack,
                renderTypeBuffer,
                0);
        matrixStack.popPose();
    }
}
