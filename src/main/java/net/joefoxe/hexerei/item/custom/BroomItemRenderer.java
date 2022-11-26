package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class BroomItemRenderer extends CustomItemRenderer {

    private BroomRenderer renderer;

    private static final Minecraft minecraft = Minecraft.getInstance();

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        if (renderer == null) {
            renderer = new BroomRenderer(new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.gameRenderer.itemInHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font));
        }


        stack.pushPose();
        renderer.render(((BroomItem)itemStack.getItem()).getBroomFast(minecraft.level, itemStack), 0F, 1F, stack, source, light);
        stack.popPose();
    }

}
