package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.CofferContainer;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joefoxe.hexerei.container.CofferContainer.OFFSET;

public class CofferScreen extends AbstractContainerScreen<CofferContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/coffer_gui.png");
    private final ResourceLocation INVENTORY = new ResourceLocation(Hexerei.MOD_ID,
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {


        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.renderButtonTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        int i = this.leftPos;
        int j = this.topPos - OFFSET;
        this.blit(matrixStack, i, j - 3, 0, 0, 214, 157);
        this.blit(matrixStack, i + 94, j - 30, 230, 0, 26, 26);
        if(this.menu.getToggled() == 1)
        {
            this.blit(matrixStack, i + 188, j + 130, 230, 44, 18, 18);
        }
        RenderSystem.setShaderTexture(0, INVENTORY);
        this.blit(matrixStack, i + 3, j + 129, 0, 0, 176, 100);

        //Rendering the coffer item on the top of the screen
        Minecraft minecraft = Minecraft.getInstance();

        RenderSystem.setShaderTexture(0, GUI);

        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        RenderSystem.disableDepthTest();
        itemRenderer.renderGuiItem(new ItemStack(ModBlocks.COFFER.get().asItem()),
                this.leftPos + 99,
                this.topPos - 25 - OFFSET);

        if(this.minecraft.player != null)
            InventoryScreen.renderEntityInInventory(this.leftPos + 107, this.topPos + 88 - OFFSET, 20, (float)(this.leftPos + 107 - x) , (float)(this.topPos + 88 - 30 - y - OFFSET), this.minecraft.player);

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

    public void renderButtonTooltip(PoseStack matrixStack, int mouseX, int mouseY){
        List<Component> components = new ArrayList<>();
        if (isHovering((double)mouseX, (double)mouseY, 188D, 130 - OFFSET, 18D, 18D)) {
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }
    }
}