package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.screen.renderer.FluidStackRenderer;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.DrainCauldronToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Optional;

public class MixingCauldronScreen extends AbstractContainerScreen<MixingCauldronContainer> {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/mixing_cauldron_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");
    public final static ResourceLocation MOON_PHASES =
            HexereiUtil.getResource("textures/gui/moon_phases.png");

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

    private void drawFont(GuiGraphics guiGraphics, Component component, float x, float y, int z, int color, float scalePercent, boolean shadow){
        guiGraphics.pose().pushPose();
        if (scalePercent != 0)
            guiGraphics.pose().scale(1/scalePercent, 1/scalePercent, 1/scalePercent);
        guiGraphics.pose().translate(x, y, z);
        guiGraphics.drawString(minecraft.font, component, 0, 0, color, shadow);
        guiGraphics.pose().popPose();
    }

    private static final int FRONT_BLIT_LAYER = 1;
    private static final int BACK_BLIT_LAYER = 0;

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int pMouseX, int pMouseY) {
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
        guiGraphics.blit(GUI, i + 20 - (int)dumpOffset, j + 56, BACK_BLIT_LAYER, 216, 61, 40, 32, 256, 256);
        if(clickedDump)
            guiGraphics.blit(GUI, i + 20 - (int)dumpOffset + 9, j + 56 + 9, BACK_BLIT_LAYER, 226, 47, 30, 14, 256, 256);

        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 20){
            float percent = width/20;
            drawFont(guiGraphics, component, (i + 44.5f - ((width / percent) / 2f) - (int)dumpOffset) * percent + (clickedDump ? 1f : 0), (j + 68 + lineHeight) * percent - 4.5f + (clickedDump ? 1f : 0), BACK_BLIT_LAYER, 0xFF404040, percent, false);
        } else
            drawFont(guiGraphics, component, i + 44.5f - (width / 2f) - (int)dumpOffset + (clickedDump ? 1f : 0), j + 68 + lineHeight - 4.5f + (clickedDump ? 1f : 0), BACK_BLIT_LAYER, 0xFF606060, 1, false);


        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, i, j, FRONT_BLIT_LAYER, 0, 0, 207, 127, 256, 256);
        guiGraphics.blit(GUI, i + 150, j + 52, FRONT_BLIT_LAYER, 220, 0, (int)(22 * this.menu.getCraftPercent()), 8, 256, 256);
        guiGraphics.blit(GUI, i + 109, j + 46, FRONT_BLIT_LAYER, 242, 0, 8, (int)(22 * this.menu.getCraftPercent()), 256, 256);

        if (mixingCauldron.usingRecipeNeedsHeat && !mixingCauldron.hasHeatSource) {
            if (ClientEvents.getClientTicksWithoutPartial() % 20 < 10)
                guiGraphics.blit(GUI, i + 102, j + 62, FRONT_BLIT_LAYER, 233, 94, 22, 14, 256, 256);
        } else {
            if (mixingCauldron.hasHeatSource) {
                guiGraphics.blit(GUI, i + 102, j + 62, FRONT_BLIT_LAYER, 233, 94, 22, 14, 256, 256);
            }
        }


        if (mixingCauldron.usingRecipeNeedsMoonPhase != MoonPhases.MoonCondition.NONE) {
            if ((ClientEvents.getClientTicksWithoutPartial() % 20 >= 10 && MoonPhases.MoonCondition.getMoonPhase(mixingCauldron.getLevel()) != mixingCauldron.usingRecipeNeedsMoonPhase) ||
                    MoonPhases.MoonCondition.getMoonPhase(mixingCauldron.getLevel()) == mixingCauldron.usingRecipeNeedsMoonPhase) {
                guiGraphics.pose().pushPose();
                float scale = 1.5f;
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.pose().translate((i + 107) / scale, (j + 35) / scale, 10);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.FULL_MOON)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 12, 12, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WANING_GIBBOUS)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 44, 12, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.LAST_QUARTER)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 76, 12, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WANING_CRESCENT)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 108, 12, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.NEW_MOON)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 12, 44, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WAXING_CRESCENT)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 44, 44, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.FIRST_QUARTER)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 76, 44, 8, 8, 256, 256);
                if(mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WAXING_GIBBOUS)
                    guiGraphics.blit(MOON_PHASES, 0, 0, FRONT_BLIT_LAYER, 108, 44, 8, 8, 256, 256);
                guiGraphics.pose().popPose();
            }
        }




        RenderSystem.setShaderTexture(0, INVENTORY);
        guiGraphics.blit(INVENTORY, i + 6, j + 102, FRONT_BLIT_LAYER, 0, 0, 176, 100, 256, 256);
        if(menu.getRenderedFluid() != null) {
            FluidStack stack = menu.getRenderedFluid().copy();
            renderer.render(guiGraphics, i + 42, j + 56, stack);
        }

        RenderSystem.disableDepthTest();
        guiGraphics.renderItem(new ItemStack(ModBlocks.MIXING_CAULDRON.get().asItem()),
                this.leftPos + 86,
                this.topPos - 25);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, i + 81, j - 30, FRONT_BLIT_LAYER, 230, 21, 26, 26, 256, 256);

        guiGraphics.blit(GUI, i + 42,j + 56, FRONT_BLIT_LAYER, 208, 12, 16, 32, 256, 256);




    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);



        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if(isMouseAboveArea(mouseX, mouseY, x, y, 42, 56)) {
            ArrayList<Component> tooltip = new ArrayList<>(renderer.getTooltip(menu.getRenderedFluid() != null ? menu.getRenderedFluid() : menu.getFluid(), TooltipFlag.Default.NORMAL));
//            PotionFluidHandler.addPotionTooltip(menu.getFluid(), tooltip, 1);
            menu.getFluid().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).addPotionTooltip(tooltip::add, 1.0f, 20f);

            FluidStack fluidStack = menu.getFluid();


            if(!fluidStack.isEmpty()){
                String modId = HexereiUtil.getRegistryName(fluidStack.getFluid()).getNamespace();
                String modName = HexereiUtil.getModNameForModId(modId);
                MutableComponent modNameComponent = Component.translatable(modName);
                modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
                if (!HexereiModNameTooltipCompat.LOADED)
                    tooltip.add(modNameComponent);
            }


            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
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

            if (ModList.get().isLoaded("jei")) {
                if (button == 0)
                    net.joefoxe.hexerei.integration.jei.HexereiJei.showRecipe(mixingCauldron.getFluidStack());
                if (button == 1)
                    net.joefoxe.hexerei.integration.jei.HexereiJei.showUses(mixingCauldron.getFluidStack());
            }
        }

        if(isMouseOver(pMouseX, pMouseY, this.leftPos + 20 - (int)dumpOffset + 9, this.topPos + 56 + 9, 30, 14) && dumpOffset > 20){

            clickedDump = true;
            if(mixingCauldron != null){
                if (mixingCauldron.getLevel() != null && mixingCauldron.getLevel().isClientSide)
                    HexereiPacketHandler.sendToServer(new DrainCauldronToServer(this.mixingCauldron.getPos()));
            }

        }

        return mouseClicked;
    }
}