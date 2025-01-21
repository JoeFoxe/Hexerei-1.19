package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joefoxe.hexerei.container.BroomContainer.OFFSET;
import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;

public class BroomScreen extends AbstractContainerScreen<BroomContainer> {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/broom_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");

    public final BroomEntity broomEntity;
    public float dropdownOffset = 0;
    public float dropdownOffsetO = 0;
    public int offset = 0;
    public boolean dropdownClicked = false;

    public BroomScreen(BroomContainer broomContainer, Inventory inv, Component titleIn) {
        super(broomContainer, inv, titleIn);
        broomEntity = broomContainer.broomEntity;
        titleLabelY = 1 - OFFSET;
        titleLabelX = 4;
        inventoryLabelY = 94 - OFFSET;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        dropdownOffsetO = dropdownOffset;
        if (dropdownClicked)
            dropdownOffset = moveTo(dropdownOffset, 58, 4f);
        else
            dropdownOffset = moveTo(dropdownOffset, 15, 4f);
    }

    public double easeInOutCubic(float x) {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;

        return x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {


        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }


    public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        int offset = 0;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS))
            offset = 21;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS))
            offset = 42;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS))
            offset = 63;
//        if(dropdownOffset > 29){
//            if (x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 && y > this.topPos + 88 + offset + ((int)dropdownOffset) && y < this.topPos + 88 + offset + ((int)dropdownOffset) + 18) {
//                this.menu.setFloatMode(false);
//                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//
//            }
//            if (x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 && y > this.topPos + 60 + offset + ((int)dropdownOffset) && y < this.topPos + 60 + offset + ((int)dropdownOffset) + 18) {
//                this.menu.setFloatMode(true);
//                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//
//            }
//
//        }
//        if(x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 &&  y > this.topPos + 89 + offset && y < this.topPos + 89 + 18 + offset){
//            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//            dropdownClicked = !dropdownClicked;
//        }

        List<Component> components = new ArrayList<>();
        if (isHovering(mouseX, mouseY, 188.25D, 89 + offset - OFFSET, 18D, 18D)) {
            components.add(Component.translatable("tooltip.hexerei.broom_settings"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
        } else if (dropdownOffset > 29) {
            if (isHovering(mouseX, mouseY, 188.25D, 88 + offset + ((int) dropdownOffset) - OFFSET, 18D, 18D)) {
                components.add(Component.translatable("tooltip.hexerei.broom_float_mode_off"));
                if (Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.broom_float_mode_off_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }

            if (isHovering(mouseX, mouseY, 188.25D, 60 + offset + ((int) dropdownOffset) - OFFSET, 18D, 18D)) {
                components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on"));
                if (Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
        }


    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        int ddOffset = (int) (easeInOutCubic(Mth.lerp(partialTicks, dropdownOffsetO, dropdownOffset) / 58f) * 58f);

        int i = this.leftPos;
        int j = this.topPos;
        this.offset = 0;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS))
            offset = 21;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS))
            offset = 42;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS))
            offset = 63;
        inventoryLabelY = 94 + offset - OFFSET;

        guiGraphics.blit(GUI, i + 184, j + 55 + offset + ddOffset - OFFSET - 5, 230, 164, 26, 63);
        if (this.menu.getFloatMode()) {
            guiGraphics.blit(GUI, i + 188, j + 60 + offset + ddOffset - OFFSET, 238, 106, 18, 18);
        } else {
            guiGraphics.blit(GUI, i + 188, j + 88 + offset + ddOffset - OFFSET, 238, 70, 18, 18);
        }

        guiGraphics.blit(GUI, i, j - 3 - OFFSET, 0, 0, 214, 82);
        guiGraphics.blit(GUI, i, j + 79 + offset - OFFSET, 0, 82, 214, 34);


        if (!broomEntity.getModule(BroomEntity.BroomSlot.MISC).isEmpty())
            guiGraphics.blit(GUI, i + 37, j + 47 - OFFSET, 235, 31, 16, 16);
        if (!broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).isEmpty())
            guiGraphics.blit(GUI, i + 99, j + 47 - OFFSET, 235, 31, 16, 16);
        if (!broomEntity.getModule(BroomEntity.BroomSlot.BRUSH).isEmpty())
            guiGraphics.blit(GUI, i + 160, j + 47 - OFFSET, 235, 31, 16, 16);

        if (!broomEntity.isEnder()) {
            ItemStack satchel = broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
            if (satchel.is(HexereiTags.Items.SMALL_SATCHELS)) {
                guiGraphics.blit(GUI, i, j + 79 - OFFSET, 0, 116, 214, 21);
            }

            if (satchel.is(HexereiTags.Items.MEDIUM_SATCHELS)) {
                guiGraphics.blit(GUI, i, j + 79 - OFFSET, 0, 116, 214, 21);
                guiGraphics.blit(GUI, i, j + 79 + 21 - OFFSET, 0, 116, 214, 21);
            }

            if (satchel.is(HexereiTags.Items.LARGE_SATCHELS)) {
                guiGraphics.blit(GUI, i, j + 79 - OFFSET, 0, 116, 214, 21);
                guiGraphics.blit(GUI, i, j + 79 + 21 - OFFSET, 0, 116, 214, 21);
                guiGraphics.blit(GUI, i, j + 79 + 42 - OFFSET, 0, 116, 214, 21);
            }
        } else {

            guiGraphics.blit(GUI, i, j + 79 - OFFSET, 0, 200, 214, 21);
            guiGraphics.blit(GUI, i, j + 79 + 21 - OFFSET, 0, 200, 214, 21);
            guiGraphics.blit(GUI, i, j + 79 + 42 - OFFSET, 0, 200, 214, 21);
            guiGraphics.blit(GUI, i, j + 79 + 42 - OFFSET + 21, 0, 221, 214, 5);
            guiGraphics.blit(GUI, i, j + 75 - OFFSET, 0, 137, 214, 72);
        }


        guiGraphics.blit(GUI, i + 94, j - 30 - OFFSET, 230, 0, 26, 26);

        if (this.dropdownClicked)
            guiGraphics.blit(GUI, i + 188, j + 89 + offset - OFFSET, 238, 124, 18, 18);

        RenderSystem.setShaderTexture(0, INVENTORY);
        guiGraphics.blit(INVENTORY, i + 3, j + 88 + offset - OFFSET, 0, 0, 176, 100);

        RenderSystem.setShaderTexture(0, GUI);
        int extraBrush = broomEntity.getExtraBrush();
        if (broomEntity.isReplacer() && extraBrush != -1) {

            guiGraphics.blit(GUI, i + 12 + 21 * ((extraBrush - 3) % 9), j + 21 * ((extraBrush - 3) / 9) + 79 - OFFSET, 234, 142, 22, 22);
        }


        //Rendering the coffer item at the top of the screen
        Minecraft minecraft = Minecraft.getInstance();


        ItemRenderer itemRenderer = minecraft.getItemRenderer();


        RenderSystem.disableDepthTest();
        guiGraphics.renderItem(broomEntity.getPickResult(),
                this.leftPos + 99,
                this.topPos - 25 - OFFSET);

        MutableComponent misc = Component.translatable("tooltip.hexerei.broom_misc");
        MutableComponent satchel = Component.translatable("tooltip.hexerei.broom_satchel");
        MutableComponent brush = Component.translatable("tooltip.hexerei.broom_brush");

        drawFont(guiGraphics, misc, this.leftPos + 34, this.topPos + 29 - OFFSET, 0xFF606060, false);
        drawFont(guiGraphics, satchel, this.leftPos + 89, this.topPos + 29 - OFFSET, 0xFF606060, false);
        drawFont(guiGraphics, brush, this.leftPos + 154, this.topPos + 29 - OFFSET, 0xFF606060, false);

    }

    private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int color, boolean shadow){
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 1);
        guiGraphics.drawString(minecraft.font, component, 0, 0, color, shadow);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);


        if (dropdownOffset > 29) {
            if (x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 && y > this.topPos + 88 + this.offset + ((int) dropdownOffset) - OFFSET && y < this.topPos + 88 + this.offset + ((int) dropdownOffset) + 18 - OFFSET) {
                this.menu.setFloatMode(false);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            }
            if (x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 && y > this.topPos + 60 + this.offset + ((int) dropdownOffset) - OFFSET && y < this.topPos + 60 + this.offset + ((int) dropdownOffset) + 18 - OFFSET) {
                this.menu.setFloatMode(true);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            }

        }
        if (x > this.leftPos + 188.25f && x < this.leftPos + 188.25f + 18 && y > this.topPos + 89 + this.offset - OFFSET && y < this.topPos + 89 + 18 + this.offset - OFFSET) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            dropdownClicked = !dropdownClicked;
        }

//        this.menu.playSound();

        return mouseClicked;
    }
}