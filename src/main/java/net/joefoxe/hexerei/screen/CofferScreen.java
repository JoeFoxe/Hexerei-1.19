package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joefoxe.hexerei.container.CofferContainer.OFFSET;

public class CofferScreen extends AbstractContainerScreen<CofferContainer> {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/coffer_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");

    public int mouseX;
    public int mouseY;


    public CofferScreen(CofferContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabelY = 1 - OFFSET;
        titleLabelX = 4;
        inventoryLabelY = 135 - OFFSET;
        mouseX = 0;
        mouseY = 0;
    }

    @Override
    protected void init() {
        super.init();

//        this.addButton(new ImageButton(this.leftPos + 188, this.topPos , 18, 18, 230, 26, 18, GUI, (button) -> {
////            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
////            this.recipeBookGui.toggleVisibility();
////            this.leftPos = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
//            ((ImageButton)button).setPosition(this.leftPos + 188, this.topPos );
//        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {


        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int i = this.leftPos;
        int j = this.topPos - OFFSET;
        guiGraphics.blit(GUI, i, j - 3, 0, 0, 214, 157);
        guiGraphics.blit(GUI, i + 94, j - 30, 230, 0, 26, 26);
        if(this.menu.getToggled() == 1)
        {
            guiGraphics.blit(GUI, i + 188, j + 130, 230, 44, 18, 18);
        }
        guiGraphics.blit(INVENTORY, i + 3, j + 129, 0, 0, 176, 100);

        //Rendering the coffer item on the top of the screen
        Minecraft minecraft = Minecraft.getInstance();

        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        RenderSystem.disableDepthTest();
        guiGraphics.renderItem(new ItemStack(ModBlocks.COFFER.get().asItem()),
                this.leftPos + 99,
                this.topPos - 25 - OFFSET);

        if(this.minecraft.player != null)
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, i + 107 - 26, j + 8 + 35, i + 107 + 26, j + 78 + 15, 22, 0.0625F, x, y, this.minecraft.player);
//            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.leftPos + 107 - 26, this.topPos + 88 - 26 - OFFSET, this.leftPos + 107 + 20, this.topPos + 88 + 20 - OFFSET, 20, 0.0625F, (float)(i + 51) - x, (float)(j + 75 - 50) - y, this.minecraft.player);

        RenderSystem.enableDepthTest();

    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);


        if(x > this.leftPos + 188 && x < this.leftPos + 188 + 18 &&  y > this.topPos + 129 - OFFSET && y < this.topPos + 129 + 18 - OFFSET){
            this.menu.setToggled(1 - this.menu.getToggled());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return mouseClicked;
    }


    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }

    public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY){
        List<Component> components = new ArrayList<>();
        if (isHovering(mouseX, mouseY, 188D, 130 - OFFSET, 18D, 18D)) {
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}