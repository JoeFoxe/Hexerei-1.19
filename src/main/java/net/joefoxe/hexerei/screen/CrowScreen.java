package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrowScreen extends AbstractContainerScreen<CrowContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/crow_gui.png");
    private final ResourceLocation INVENTORY = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/inventory.png");

    public final CrowEntity crowEntity;

    public CrowScreen(CrowContainer crowContainer, Inventory inv, Component titleIn) {
        super(crowContainer, inv, titleIn);
        crowEntity = crowContainer.crowEntity;
        titleLabelY = 1;
        titleLabelX = 4;
        inventoryLabelY = 134;
        inventoryLabelX = 9;
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

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }


    public void renderButtonTooltip(PoseStack matrixStack, int mouseX, int mouseY){

        List<Component> components = new ArrayList<>();
        if (isHovering((double)mouseX, (double)mouseY, 23D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_command_gui_0"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_follow_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_follow_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_follow_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 43D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_command_gui_1"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_sit_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_sit_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 63D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_command_gui_2"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_wander_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_wander_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 83D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_command_gui_3"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_help_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_help_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_help_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 107D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_help_command_gui_0"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_gather_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_gather_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_gather_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 127D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_help_command_gui_1"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_harvest_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_harvest_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 147D, 92D, 18D, 18D)) {
            components.add(new TranslatableComponent("entity.hexerei.crow_help_command_gui_2"));
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("<%s>", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_pickpocket_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_pickpocket_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_pickpocket_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(new TranslatableComponent("tooltip.hexerei.crow_flute_pickpocket_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(new TranslatableComponent("[%s]", new TranslatableComponent("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }

    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int i = this.leftPos;
        int j = this.topPos;
        inventoryLabelY = 134;
        inventoryLabelX = 9;

        this.blit(matrixStack, i, j, 0, 0, 188, 153);

        if(this.menu.getCommand() == 0)
        {
            this.blit(matrixStack, i + 23, j + 92, 238, 52, 18, 18);
        }else if(this.menu.getCommand() == 1)
        {
            this.blit(matrixStack, i + 43, j + 92, 238, 70, 18, 18);
        }else if(this.menu.getCommand() == 2)
        {
            this.blit(matrixStack, i + 63, j + 92, 238, 88, 18, 18);
        }else if(this.menu.getCommand() == 3)
        {
            this.blit(matrixStack, i + 83, j + 92, 238, 106, 18, 18);
        }

        if (this.menu.getCommand() == 3) {
            if (this.menu.getHelpCommand() == 0) {
                this.blit(matrixStack, i + 107, j + 92, 238, 124, 18, 18);
            }
            if (this.menu.getHelpCommand() == 1) {
                this.blit(matrixStack, i + 127, j + 92, 238, 142, 18, 18);
            }
            if (this.menu.getHelpCommand() == 2) {
                this.blit(matrixStack, i + 147, j + 92, 238, 160, 18, 18);
            }
        }
        else
        {
            if (this.menu.getHelpCommand() == 0) {
                this.blit(matrixStack, i + 107, j + 92, 220, 124, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 107, j + 92, 202, 124, 18, 18);
            }
            if (this.menu.getHelpCommand() == 1) {
                this.blit(matrixStack, i + 127, j + 92, 220, 142, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 127, j + 92, 202, 142, 18, 18);
            }
            if (this.menu.getHelpCommand() == 2) {
                this.blit(matrixStack, i + 147, j + 92, 220, 160, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 147, j + 92, 202, 160, 18, 18);
            }
        }

        if(!crowEntity.itemHandler.getStackInSlot(0).isEmpty())
            this.blit(matrixStack, i + 86, j + 50, 235, 31, 16, 16);
        if(!crowEntity.itemHandler.getStackInSlot(1).isEmpty())
            this.blit(matrixStack, i + 37, j + 50, 235, 31, 16, 16);
        if(!crowEntity.itemHandler.getStackInSlot(2).isEmpty())
            this.blit(matrixStack, i + 134, j + 50, 235, 31, 16, 16);

        this.blit(matrixStack, i + 81, j - 30, 230, 0, 26, 26);

        RenderSystem.setShaderTexture(0, INVENTORY);
        this.blit(matrixStack, i + 6, j + 129, 0, 0, 176, 100);
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, GUI);
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        InventoryScreen.renderEntityInInventory(this.leftPos + 94, this.topPos - 10, 25, (float)(this.leftPos + 107 - x) , (float)(this.topPos + 88 - 30 - y), crowEntity);


        RenderSystem.disableDepthTest();
//        if(crowEntity.getCrowType() == CrowEntity.Type.MAHOGANY)
//            itemRenderer.renderGuiItem(new ItemStack(ModItems.MAHOGANY_BROOM.get().asItem()),
//                    this.leftPos + 99,
//                    this.topPos - 25);
//        if(crowEntity.getCrowType() == CrowEntity.Type.WILLOW)
//            itemRenderer.renderGuiItem(new ItemStack(ModItems.WILLOW_BROOM.get().asItem()),
//                    this.leftPos + 99,
//                    this.topPos - 25);

//        matrixStack.translate(this.leftPos + 42*1.666f, this.topPos + 14*1.666f, 0f);
        TranslatableComponent hat = new TranslatableComponent("entity.hexerei.crow_slot_0");
        TranslatableComponent misc1 = new TranslatableComponent("entity.hexerei.crow_slot_1");
        TranslatableComponent misc2 = new TranslatableComponent("entity.hexerei.crow_slot_2");
        TranslatableComponent command;
        TranslatableComponent helpCommand;
        if(this.menu.getCommand() == 0)
            command = new TranslatableComponent("entity.hexerei.crow_command_gui_0");
        else if(this.menu.getCommand() == 1)
            command = new TranslatableComponent("entity.hexerei.crow_command_gui_1");
        else if(this.menu.getCommand() == 2)
            command = new TranslatableComponent("entity.hexerei.crow_command_gui_2");
        else {
            command = new TranslatableComponent("entity.hexerei.crow_command_gui_3");

        }
        if(this.menu.getHelpCommand() == 0)
        {
            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_0");
        }else if(this.menu.getHelpCommand() == 1)
        {
            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_1");
        } else
        {
            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_2");
        }


        minecraft.font.draw(matrixStack, hat, this.leftPos + 45 - (float)(font.width(hat.getVisualOrderText()) / 2), this.topPos + 32, 0xFF606060);
        minecraft.font.draw(matrixStack, misc1, this.leftPos + 94 - (float)(font.width(misc1.getVisualOrderText()) / 2), this.topPos + 32, 0xFF606060);
        minecraft.font.draw(matrixStack, misc2, this.leftPos + 142 - (float)(font.width(misc2.getVisualOrderText()) / 2), this.topPos + 32, 0xFF606060);
//        minecraft.font.draw(matrixStack, command, this.leftPos + 94 - (float)(font.width(command.getVisualOrderText()) / 2), this.topPos + 77, 0xFF606060);

        minecraft.font.draw(matrixStack, command, this.leftPos + 56 - (float)(font.width(command.getVisualOrderText()) / 2), this.topPos + 77, 0xFF606060);
        minecraft.font.draw(matrixStack, helpCommand, this.leftPos + 131 - (float)(font.width(helpCommand.getVisualOrderText()) / 2), this.topPos + 77, 0xFF606060);

//        InventoryScreen.renderEntityInInventory(this.leftPos + 107, this.topPos + 88, 20, (float)(this.leftPos + 107 - x) , (float)(this.topPos + 88 - 30 - y), (LivingEntity) crowEntity);
//
//        RenderSystem.enableDepthTest();

//        TranslatableComponent command;
//        TranslatableComponent helpCommand;
//        TranslatableComponent crowSelect;
//        if(this.menu.getCommand() == 0)
//            command = new TranslatableComponent("entity.hexerei.crow_command_gui_0");
//        else if(this.menu.getCommand() == 1)
//            command = new TranslatableComponent("entity.hexerei.crow_command_gui_1");
//        else if(this.menu.getCommand() == 2)
//            command = new TranslatableComponent("entity.hexerei.crow_command_gui_2");
//        else {
//            command = new TranslatableComponent("entity.hexerei.crow_command_gui_3");
//
//        }
//        if(this.menu.getHelpCommand() == 0)
//        {
//            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_0");
//        }else if(this.menu.getHelpCommand() == 1)
//        {
//            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_1");
//        } else
//        {
//            helpCommand = new TranslatableComponent("entity.hexerei.crow_help_command_gui_2");
//        }
//
//        minecraft.font.draw(matrixStack, command, this.leftPos + 56 - (float)(font.width(command.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
//        minecraft.font.draw(matrixStack, helpCommand, this.leftPos + 131 - (float)(font.width(helpCommand.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
//        minecraft.font.draw(matrixStack, new TranslatableComponent("entity.hexerei.crow_flute_perch"), this.leftPos + 128 - (float)(font.width(new TranslatableComponent("entity.hexerei.crow_flute_perch").getVisualOrderText()) / 2), this.topPos + 91, 0xFFAAAAAA);



    }

    private float moveTo(float input, float moveTo, float speed)
    {
        float distance = moveTo - input;

        if(Math.abs(distance) <= speed)
        {
            return moveTo;
        }

        if(distance > 0)
        {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);

        if (x > this.leftPos + 23f && x < this.leftPos + 23f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setCommand(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_" + 0, this.crowEntity.getName()), true);
        }
        else if (x > this.leftPos + 43f && x < this.leftPos + 43f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setCommand(1);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_" + 1, this.crowEntity.getName()), true);
        }
        else if (x > this.leftPos + 63f && x < this.leftPos + 63f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setCommand(2);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_" + 2, this.crowEntity.getName()), true);
        }
        else if (x > this.leftPos + 83f && x < this.leftPos + 83f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setCommand(3);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_3_" + this.menu.getHelpCommand(), this.crowEntity.getName()), true);
            }
        }
        else if (x > this.leftPos + 107f && x < this.leftPos + 107f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setHelpCommand(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_3_0", this.crowEntity.getName()), true);
            }
        }
        else if (x > this.leftPos + 127f && x < this.leftPos + 127f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setHelpCommand(1);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_3_1", this.crowEntity.getName()), true);
            }
        }
        else if (x > this.leftPos + 147f && x < this.leftPos + 147f + 18 && y > this.topPos + 92 && y < this.topPos + 92 + 18) {
            this.menu.setHelpCommand(2);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(new TranslatableComponent("entity.hexerei.crow_command_3_2", this.crowEntity.getName()), true);
            }
        }

        return mouseClicked;
    }
}