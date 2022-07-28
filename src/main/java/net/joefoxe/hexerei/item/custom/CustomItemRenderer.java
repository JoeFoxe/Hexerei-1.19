package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CustomItemRenderer {

    @OnlyIn(Dist.CLIENT)
    protected Minecraft minecraft = Minecraft.getInstance();
    @OnlyIn(Dist.CLIENT)
    private final Renderer renderer = new Renderer();

    @OnlyIn(Dist.CLIENT)
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

    }

    @OnlyIn(Dist.CLIENT)
    public Renderer getRenderer() {
        return renderer;
    }

    @OnlyIn(Dist.CLIENT)
    protected class Renderer extends BlockEntityWithoutLevelRenderer {

        public Renderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
            poseStack.translate(-0.2, 0.1, 0.10);
            CustomItemRenderer.this.renderByItem(stack, transformType, poseStack, multiBufferSource, light, overlay);
        }
    }

}