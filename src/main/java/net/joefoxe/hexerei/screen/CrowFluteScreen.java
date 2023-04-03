package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.CrowFluteContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrowFluteScreen extends AbstractContainerScreen<CrowFluteContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/crow_flute_gui.png");

    public final ItemStack crowFluteStack;
    public Player player;
    boolean xClicked;
    int xClickedTimer;

    public CrowFluteScreen(CrowFluteContainer crowFluteContainer, Inventory inv, Component titleIn) {
        super(crowFluteContainer, inv, titleIn);
        crowFluteStack = crowFluteContainer.stack;
        titleLabelY = 4 + 18;
        titleLabelX = 5;
        inventoryLabelY = -800;
        inventoryLabelX = 13;
        player = inv.player;
        xClicked = false;
        xClickedTimer = 10;
    }

    @Override
    protected void containerTick() {
        if(xClicked) {
            this.xClickedTimer--;
            if(xClickedTimer-- < 0) {
                xClicked = false;
                xClickedTimer = 10;
            }
        }
        super.containerTick();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.renderButtonTooltip(matrixStack, mouseX, mouseY);
    }

    public void renderButtonTooltip(PoseStack matrixStack, int mouseX, int mouseY){

        List<Component> components = new ArrayList<>();
        if (isHovering(mouseX, mouseY, 23D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_0"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 43D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_1"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 63D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_2"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 83D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_3"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 107D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_0"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 127D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_1"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 147D, 64D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_2"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 22D, 87D, 62D, 15D)) {
            components.add(Component.translatable("entity.hexerei.crow_flute_select"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 104D, 87D, 62D, 15D)) {
            components.add(Component.translatable("entity.hexerei.crow_flute_perch"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering(mouseX, mouseY, 89D, 90D, 10D, 10D)) {
            if (this.menu.getCommandMode() == 1) {
                components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_selected_button"));
                if (Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_selected_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            } else if (this.menu.getCommandMode() == 2) {
                components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_perch_button"));
                if (Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_perch_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }

    }

//    public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, @Nullable Font font, ItemStack stack) {
//        this.tooltipFont = font;
//        this.tooltipStack = stack;
//        this.renderTooltip(poseStack, textComponents, tooltipComponent, x, y);
//        this.tooltipFont = null;
//        this.tooltipStack = ItemStack.EMPTY;
//    }

    @Override
    public Component getTitle() {

        MutableComponent mutablecomponent = (Component.translatable("")).append(crowFluteStack.getHoverName());
        if (crowFluteStack.hasCustomHoverName()) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }

        return mutablecomponent;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int i = this.leftPos;
        int j = this.topPos;
        inventoryLabelY = -800;
        inventoryLabelX = 13;

        this.blit(matrixStack, i, j, 0, 0, 188, 130);

        if(this.menu.getCommand() == 0)
        {
            this.blit(matrixStack, i + 23, j + 64, 238, 52, 18, 18);
        }else if(this.menu.getCommand() == 1)
        {
            this.blit(matrixStack, i + 43, j + 64, 238, 70, 18, 18);
        }else if(this.menu.getCommand() == 2)
        {
            this.blit(matrixStack, i + 63, j + 64, 238, 88, 18, 18);
        }else if(this.menu.getCommand() == 3)
        {
            this.blit(matrixStack, i + 83, j + 64, 238, 106, 18, 18);
        }

        if (this.menu.getCommand() == 3) {
            if (this.menu.getHelpCommand() == 0) {
                this.blit(matrixStack, i + 107, j + 64, 238, 124, 18, 18);
            }
            if (this.menu.getHelpCommand() == 1) {
                this.blit(matrixStack, i + 127, j + 64, 238, 142, 18, 18);
            }
            if (this.menu.getHelpCommand() == 2) {
                this.blit(matrixStack, i + 147, j + 64, 238, 160, 18, 18);
            }
        }
        else
        {
            if (this.menu.getHelpCommand() == 0) {
                this.blit(matrixStack, i + 107, j + 64, 220, 124, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 107, j + 64, 202, 124, 18, 18);
            }
            if (this.menu.getHelpCommand() == 1) {
                this.blit(matrixStack, i + 127, j + 64, 220, 142, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 127, j + 64, 202, 142, 18, 18);
            }
            if (this.menu.getHelpCommand() == 2) {
                this.blit(matrixStack, i + 147, j + 64, 220, 160, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 147, j + 64, 202, 160, 18, 18);
            }
        }

        if(this.menu.getCommandMode() == 1)
        {
            this.blit(matrixStack, i + 22, j + 87, 0, 131, 62, 15);

            if(xClicked)
                this.blit(matrixStack, i + 89, j + 90, 74, 131, 10, 10);
            else
                this.blit(matrixStack, i + 89, j + 90, 63, 131, 10, 10);

        }

        if(this.menu.getCommandMode() == 2)
        {
            this.blit(matrixStack, i + 104, j + 87, 0, 147, 62, 15);

            if(xClicked)
                this.blit(matrixStack, i + 89, j + 90, 74, 131, 10, 10);
            else
                this.blit(matrixStack, i + 89, j + 90, 63, 131, 10, 10);
        }

        this.blit(matrixStack, i + 81, j - 14, 230, 0, 26, 26);

        if(crowFluteStack.getOrCreateTag().contains("crowList")) {

            for(int k = 0; k < this.menu.crowList.size(); k++){
                if (this.menu.crowList.get(k) != null) {
                    int offset = (this.menu.crowList.size() % 2 == 1 ? 0 : -10) + (k % 2 == 1 ? 1 : -1) * ((k + 1) / 2) * 20;
                    this.blit(matrixStack, i + 86 + offset, j + 125, 85, 131, 15, 3);
                    if(k != 0)
                        this.blit(matrixStack, i + 83 + offset + (k % 2 == 0 ? 20 : 0), j + 118, 101, 131, 2, 9);
                }
            }
        }
        Minecraft minecraft = Minecraft.getInstance();

        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        RenderSystem.disableDepthTest();
            itemRenderer.renderGuiItem(crowFluteStack,
                    this.leftPos + 86,
                    this.topPos - 25 + 16);

        if(crowFluteStack.getOrCreateTag().contains("crowList")) {

            for(int k = 0; k < this.menu.crowList.size(); k++){
                if (this.menu.crowList.get(k) != null) {
                    int offset = (this.menu.crowList.size() % 2 == 1 ? 0 : -10) + (k % 2 == 1 ? 1 : -1) * ((k + 1) / 2) * 20;
                    InventoryScreen.renderEntityInInventory(this.leftPos + 94 + offset, this.topPos + 126, 20, (float) (this.leftPos + 107 - x), (float) (this.topPos + 88 - 30 - y), (LivingEntity) this.menu.crowList.get(k));
                }
            }
        }
        MutableComponent command;
        MutableComponent helpCommand;
        MutableComponent crowSelect;
        if(this.menu.getCommand() == 0)
            command = Component.translatable("entity.hexerei.crow_command_gui_0");
        else if(this.menu.getCommand() == 1)
            command = Component.translatable("entity.hexerei.crow_command_gui_1");
        else if(this.menu.getCommand() == 2)
            command = Component.translatable("entity.hexerei.crow_command_gui_2");
        else {
            command = Component.translatable("entity.hexerei.crow_command_gui_3");
        }
        if(this.menu.getHelpCommand() == 0)
        {
            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_0");
        }else if(this.menu.getHelpCommand() == 1)
        {
            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_1");
        } else
        {
            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_2");
        }

        if(this.menu.getCommandMode() == 1)
        {
            crowSelect = Component.translatable("entity.hexerei.crow_flute_select");
            minecraft.font.draw(matrixStack, crowSelect, this.leftPos + 62 - (float)(font.width(crowSelect.getVisualOrderText()) / 2), this.topPos + 91, 0xFFCCCCCC);
        } else
        {
            crowSelect = Component.translatable("entity.hexerei.crow_flute_select");
            minecraft.font.draw(matrixStack, crowSelect, this.leftPos + 62 - (float)(font.width(crowSelect.getVisualOrderText()) / 2), this.topPos + 91, 0xFFAAAAAA);
        }

        minecraft.font.draw(matrixStack, command, this.leftPos + 56 - (float)(font.width(command.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
        minecraft.font.draw(matrixStack, helpCommand, this.leftPos + 131 - (float)(font.width(helpCommand.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
        minecraft.font.draw(matrixStack, Component.translatable("entity.hexerei.crow_flute_perch"), this.leftPos + 128 - (float)(font.width(Component.translatable("entity.hexerei.crow_flute_perch").getVisualOrderText()) / 2), this.topPos + 91, 0xFFAAAAAA);



    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);

        if (x > this.leftPos + 23f && x < this.leftPos + 23f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setCommand(0);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 43f && x < this.leftPos + 43f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setCommand(1);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 63f && x < this.leftPos + 63f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setCommand(2);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 83f && x < this.leftPos + 83f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setCommand(3);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 107f && x < this.leftPos + 107f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setHelpCommand(0);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 127f && x < this.leftPos + 127f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setHelpCommand(1);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 147f && x < this.leftPos + 147f + 18 && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
            this.menu.setHelpCommand(2);
            this.menu.setCommandMode(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (x > this.leftPos + 22 && x < this.leftPos + 22 + 62 && y > this.topPos + 87 && y < this.topPos + 87 + 15) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.menu.getCommandMode() == 0 || this.menu.getCommandMode() == 2)
                this.menu.setCommandMode(1);
            else
                this.menu.setCommandMode(0);

            if(this.menu.stack.getOrCreateTag().getInt("commandMode") == 1) {
//                Hexerei.proxy.getPlayer().closeContainer();
                Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message"), true);
            }
        }
        else if (x > this.leftPos + 104 && x < this.leftPos + 104 + 62 && y > this.topPos + 87 && y < this.topPos + 87 + 15) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.menu.getCommandMode() == 0 || this.menu.getCommandMode() == 1)
                this.menu.setCommandMode(2);
            else
                this.menu.setCommandMode(0);

            if(this.menu.stack.getOrCreateTag().getInt("commandMode") == 2) {
                Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message"), true);
            }
        }
        else if (x > this.leftPos + 89 && x < this.leftPos + 89 + 10 && y > this.topPos + 90 && y < this.topPos + 90 + 10) {

            if(this.menu.getCommandMode() == 1) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.menu.clearCrowList();
                Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_clear_select_message"), true);

                this.xClicked = true;
                this.xClickedTimer = 10;
            }
            if(this.menu.getCommandMode() == 2) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.menu.clearCrowPerch();
                Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_clear_perch_message"), true);

                this.xClicked = true;
                this.xClickedTimer = 10;
            }
        }




        return mouseClicked;
    }
}