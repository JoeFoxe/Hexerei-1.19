package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface HexereiBookTooltip extends ClientTooltipComponent {


    @OnlyIn(Dist.CLIENT)
    default void renderText(Font p_169953_, int xIn, int yIn, Matrix4f matrix4f, MultiBufferSource.BufferSource buffer, int combinedOverlayIn, int combinedLightIn) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderImage(Font p_194048_, MultiBufferSource bufferSource, int xIn, int yIn, PoseStack poseStack, ItemRenderer itemRenderer, int zIn, int combinedOverlayIn, int combinedLightIn) {
    }
}