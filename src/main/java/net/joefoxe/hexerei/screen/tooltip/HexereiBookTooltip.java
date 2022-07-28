package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

@OnlyIn(Dist.CLIENT)
public interface HexereiBookTooltip extends ClientTooltipComponent {


    @OnlyIn(Dist.CLIENT)
    default void renderText(Font p_169953_, int xIn, int yIn, Matrix4f matrix4f, MultiBufferSource.BufferSource buffer, int combinedOverlayIn, int combinedLightIn) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderImage(Font p_194048_, MultiBufferSource bufferSource, int xIn, int yIn, PoseStack poseStack, ItemRenderer itemRenderer, int zIn, int combinedOverlayIn, int combinedLightIn) {
    }
}