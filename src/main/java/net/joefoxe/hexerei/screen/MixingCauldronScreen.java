package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.screen.renderer.FluidStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

public class MixingCauldronScreen extends AbstractContainerScreen<MixingCauldronContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/mixing_cauldron_gui.png");
    private final ResourceLocation INVENTORY = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/inventory.png");

    private FluidStackRenderer renderer;
    public MixingCauldronScreen(MixingCauldronContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabelY = 4;
        titleLabelX = 28;
        inventoryLabelY = 108;
        inventoryLabelX = 12;
        renderer = new FluidStackRenderer(2000,true,16, 32);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY) {
        return isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return (mouseX >= x && mouseX <= x + sizeX) && (mouseY >= y && mouseY <= y + sizeY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);



        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, 207, 127);
        this.blit(matrixStack, i + 150, j + 52, 220, 0, (int)(22 * this.menu.getCraftPercent()), 8);
        this.blit(matrixStack, i + 109, j + 46, 242, 0, 8, (int)(22 * this.menu.getCraftPercent()));





        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        RenderSystem.setShaderTexture(0, INVENTORY);
        this.blit(matrixStack, i + 6, j + 102, 0, 0, 176, 100);
        renderer.render(matrixStack, i + 42,j + 56, menu.getFluid());

        RenderSystem.disableDepthTest();
        itemRenderer.renderGuiItem(new ItemStack(ModBlocks.MIXING_CAULDRON.get().asItem()),
                this.leftPos + 86,
                this.topPos - 25);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(matrixStack, i + 81, j - 30, 230, 21, 26, 26);

        this.blit(matrixStack, i + 42,j + 56, 208, 12, 16, 32);


    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);


        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if(isMouseAboveArea(mouseX, mouseY, x, y, 42, 56)) {
            renderTooltip(matrixStack, renderer.getTooltip(menu.getFluid(), TooltipFlag.Default.NORMAL),
                    Optional.empty(), mouseX, mouseY);
        }
    }


}