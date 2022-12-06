package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJei;
import net.joefoxe.hexerei.screen.renderer.FluidStackRenderer;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.DrainCauldronToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Optional;

public class MixingCauldronScreen extends AbstractContainerScreen<MixingCauldronContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/mixing_cauldron_gui.png");
    private final ResourceLocation INVENTORY = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/inventory.png");

    private final FluidStackRenderer renderer;

    private float dumpOffset;
    public boolean clickedDump;
    public MixingCauldronTile mixingCauldron;

    public MixingCauldronScreen(MixingCauldronContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        if(screenContainer.tileEntity instanceof MixingCauldronTile mixingCauldronTile)
            mixingCauldron = mixingCauldronTile;
        titleLabelY = 4;
        titleLabelX = 28;
        inventoryLabelY = 108;
        inventoryLabelX = 12;
        renderer = new FluidStackRenderer(2000,true,16, 32);
        dumpOffset = 0;
        clickedDump = false;
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY) {
        return isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return (mouseX >= x && mouseX <= x + sizeX) && (mouseY >= y && mouseY <= y + sizeY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int pMouseX, int pMouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);


        int i = this.leftPos;
        int j = this.topPos;

        if(isMouseOver(pMouseX, pMouseY, i + 20 - (int)dumpOffset, j + 56, 10 + (int)dumpOffset, 32)){
            dumpOffset = HexereiUtil.moveTo(dumpOffset, 31, 2 * ((32 - dumpOffset) / 31));
        } else {
            dumpOffset = HexereiUtil.moveTo(dumpOffset, 0, Math.abs(2 * ((-1 - dumpOffset) / 31)));
        }

        Component component = Component.translatable("Dump");
        float width = font.width(component.getVisualOrderText());
        this.blit(matrixStack, i + 20 - (int)dumpOffset, j + 56, 216, 61, 40, 32);
        if(clickedDump)
            this.blit(matrixStack, i + 20 - (int)dumpOffset + 9, j + 56 + 9, 226, 47, 30, 14);

        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 20){
            float percent = width/20;
            matrixStack.pushPose();
            matrixStack.scale(1/percent, 1/percent, 1/percent);
            minecraft.font.draw(matrixStack, component, (i + 44.5f - ((width / percent) / 2f) - (int)dumpOffset) * percent + (clickedDump ? 1f : 0), (j + 68 + lineHeight) * percent - 4.5f + (clickedDump ? 1f : 0), 0xFF404040);
            matrixStack.popPose();
        } else
            minecraft.font.draw(matrixStack, component, i + 44.5f - (width / 2f) - (int)dumpOffset + (clickedDump ? 1f : 0), j + 68 + lineHeight - 4.5f + (clickedDump ? 1f : 0), 0xFF606060);


        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(matrixStack, i, j, 0, 0, 207, 127);
        this.blit(matrixStack, i + 150, j + 52, 220, 0, (int)(22 * this.menu.getCraftPercent()), 8);
        this.blit(matrixStack, i + 109, j + 46, 242, 0, 8, (int)(22 * this.menu.getCraftPercent()));



        RenderSystem.setShaderTexture(0, INVENTORY);
        this.blit(matrixStack, i + 6, j + 102, 0, 0, 176, 100);
        if(menu.getRenderedFluid() != null) {
            FluidStack stack = menu.getRenderedFluid().copy();
            renderer.render(matrixStack, i + 42, j + 56, stack);
        }

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
            ArrayList<Component> tooltip = new ArrayList<>(renderer.getTooltip(menu.getRenderedFluid() != null ? menu.getRenderedFluid() : menu.getFluid(), TooltipFlag.Default.NORMAL));
            PotionFluidHandler.addPotionTooltip(menu.getFluid(), tooltip, 1);

            FluidStack fluidStack = menu.getFluid();


            if(!fluidStack.isEmpty()){
                String modId = Registry.FLUID.getKey(fluidStack.getFluid()).getNamespace();
                String modName = HexereiUtil.getModNameForModId(modId);
                MutableComponent modNameComponent = Component.translatable(modName);
                modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
                if (!HexereiModNameTooltipCompat.LOADED)
                    tooltip.add(modNameComponent);
            }


            renderTooltip(matrixStack, tooltip,
                    Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {


        if(pButton == 0)
            clickedDump = false;

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int button) {
        boolean mouseClicked = super.mouseClicked(pMouseX, pMouseY, button);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if(isMouseAboveArea((int)pMouseX, (int)pMouseY, x, y, 42, 56)) {

            if(button == 0)
                HexereiJei.showRecipe(mixingCauldron.getFluidStack());
            if(button == 1)
                HexereiJei.showUses(mixingCauldron.getFluidStack());
        }

        if(isMouseOver(pMouseX, pMouseY, this.leftPos + 20 - (int)dumpOffset + 9, this.topPos + 56 + 9, 30, 14) && dumpOffset > 20){

            clickedDump = true;
            if(mixingCauldron != null){
                if (mixingCauldron.getLevel() != null && mixingCauldron.getLevel().isClientSide)
                    HexereiPacketHandler.sendToServer(new DrainCauldronToServer(this.mixingCauldron));
            }

        }

        if(isMouseOver(pMouseX, pMouseY, this.leftPos + 20 - (int)dumpOffset + 9, this.topPos + 56 + 9, 30, 14) && dumpOffset > 20){

        }

        return mouseClicked;
    }
}