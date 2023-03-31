package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.PickableDoubleFlower;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import static net.joefoxe.hexerei.container.CofferContainer.OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrowScreen extends AbstractContainerScreen<CrowContainer> {
    private final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/crow_gui.png");
    private final ResourceLocation INVENTORY = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/inventory.png");

    public final CrowEntity crowEntity;
    public float whitelistOffset;
    public float leftPanelOffset;
    private int whitelistPage;
    private int rangeSlider;
    private boolean rangeSliderClicked;
    private double rangeSliderClickedPos;

    public CrowScreen(CrowContainer crowContainer, Inventory inv, Component titleIn) {
        super(crowContainer, inv, titleIn);
        crowEntity = crowContainer.crowEntity;
        titleLabelY = 1 - OFFSET;
        titleLabelX = 4;
        inventoryLabelY = 134 - OFFSET;
        inventoryLabelX = 9;
        whitelistOffset = 0;
        leftPanelOffset = 0;
        whitelistPage = 0;
        rangeSlider = crowEntity.interactionRange;
        rangeSliderClickedPos = 0;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        int i = this.leftPos;
        int j = this.topPos - OFFSET;
        if(isMouseOver(mouseX, mouseY, i + 184, j + 18, 10 + (int)whitelistOffset, 100)){
            whitelistOffset = HexereiUtil.moveTo(whitelistOffset, 28, 2 * ((32 - whitelistOffset) / 31));
        } else {
            whitelistOffset = HexereiUtil.moveTo(whitelistOffset, 0, Math.abs(2 * ((-1 - whitelistOffset) / 31)));
        }
        if(isMouseOver(mouseX, mouseY, i - 5 - (int)leftPanelOffset, j + 18, 10 + (int)leftPanelOffset, 100)){
            leftPanelOffset = HexereiUtil.moveTo(leftPanelOffset, 28, 2 * ((32 - leftPanelOffset) / 31));
        } else {
            leftPanelOffset = HexereiUtil.moveTo(leftPanelOffset, 0, Math.abs(2 * ((-1 - leftPanelOffset) / 31)));
        }

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.renderButtonTooltip(matrixStack, mouseX, mouseY);
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return (mouseX >= x && mouseX <= x + sizeX) && (mouseY >= y && mouseY <= y + sizeY);
    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos - OFFSET + y && mouseY < this.topPos - OFFSET + y + height;
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }


    public void renderButtonTooltip(PoseStack matrixStack, int mouseX, int mouseY){

        List<Component> components = new ArrayList<>();
        if(whitelistOffset > 21){
            if (isHovering((double) mouseX, (double) mouseY, 190 - 28 + (int) whitelistOffset, 27, 18D, 18D)) {
                components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button"));
                if (Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
            if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset, 19 + 29, 7, 7)) {
                if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 1) {
                    components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
                    this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                }
            } else if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset, 19 + 29 + 18, 7, 7)) {
                if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 2) {
                    components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
                    this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                }
            } else if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset, 19 + 29 + 18 + 18, 7, 7)) {
                if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 3) {
                    components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
                    this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                }
            } else {
                if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset - 12, 19 + 29 + 4, 16, 16)) {

                    if (crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 1) {
                        components.add(crowEntity.harvestWhitelist.get(whitelistPage * 3).getName());
                        components.add(crowEntity.harvestWhitelist.get(whitelistPage * 3).getName());
                        this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                    }
                } else if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset - 12, 19 + 29 + 18 + 4, 16, 16)) {

                    if (crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 2) {
                        components.add(crowEntity.harvestWhitelist.get(whitelistPage * 3 + 1).getName());
                        this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                    }
                } else if (isHovering((double) mouseX, (double) mouseY, 184 - 28 + 19 + (int) whitelistOffset - 12, 19 + 29 + 18 + 18 + 4, 16, 16)) {

                    if (crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 3) {
                        components.add(crowEntity.harvestWhitelist.get(whitelistPage * 3 + 2).getName());
                        this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
                    }
                }
            }
            if (isHovering((double) mouseX, (double) mouseY, 184 + 21 - 28 + (int) whitelistOffset, 19 + 88, 7, 10)) {
                components.add(Component.translatable("tooltip.hexerei.crow_whitelist_next"));
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
            if (isHovering((double) mouseX, (double) mouseY, 187 - 28 + (int) whitelistOffset, 19 + 88, 7, 10)) {
                components.add(Component.translatable("tooltip.hexerei.crow_whitelist_back"));
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
        }
        if(leftPanelOffset > 21){
            if (isHovering((double) mouseX, (double) mouseY, 5 - (int)leftPanelOffset, 107, 7, 10)) {
                components.add(Component.translatable("tooltip.hexerei.crow_range_decrease"));
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
            else if (isHovering((double) mouseX, (double) mouseY, 5 + 18 - (int)leftPanelOffset, 107, 7, 10)) {
                components.add(Component.translatable("tooltip.hexerei.crow_range_increase"));
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
            else if (isHovering((double) mouseX, (double) mouseY, 6 - (int)leftPanelOffset, 107 - 13, 22, 15)) {
                components.add(Component.translatable("tooltip.hexerei.crow_range_interaction"));
                if(Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_range_interaction_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_range_interaction_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
            else if (isHovering((double) mouseX, (double) mouseY, 4 - (int)leftPanelOffset, 107 - 13 - 64, 18, 18)) {
                components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle"));
                if(Screen.hasShiftDown()) {
                    components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_attack_toggled", this.crowEntity.canAttack ? "On" : "Off").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                else {
                    components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
                this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
            }
        }
        if (isHovering((double)mouseX, (double)mouseY, 23D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_0"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 43D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_1"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 63D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_2"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 83D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_command_gui_3"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 107D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_0"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 127D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_1"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            this.renderTooltip(matrixStack, components, Optional.empty(), mouseX, mouseY, Minecraft.getInstance().font, ItemStack.EMPTY);
        }


        if (isHovering((double)mouseX, (double)mouseY, 147D, 92D, 18D, 18D)) {
            components.add(Component.translatable("entity.hexerei.crow_help_command_gui_2"));
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
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
        int j = this.topPos - OFFSET;
        inventoryLabelY = 134 - OFFSET;
        inventoryLabelX = 9;
        GuiComponent.blit(matrixStack, i + 184 - 28 + (int)whitelistOffset, j + 19, 0, 0, 156, 37, 100, 256, 256);
        GuiComponent.blit(matrixStack, i - 5 - (int)leftPanelOffset, j + 19, 0, 74, 156, 37, 100, 256, 256);

        if(CrowWhitelistEvent.whiteListingCrow != null && CrowWhitelistEvent.whiteListingCrow == crowEntity){
            GuiComponent.blit(matrixStack, i + 184 - 28 + 6 + (int)whitelistOffset, j + 19 + 8, 1, 238, 178, 18, 18, 256, 256);
        }
        GuiComponent.blit(matrixStack, i + 184 - 28 + (int)whitelistOffset, j + 19 + 100 - 12, 1, 37, 244, 37, 12, 256, 256);
        GuiComponent.blit(matrixStack, i + 2 - (int)leftPanelOffset, j + 19 + 100 - 12, 1, 37, 244, 37, 12, 256, 256);
        //(leftPanelOffset > 21 && x > this.leftPos + 8 - (int)leftPanelOffset && x < this.leftPos + 8 - (int)leftPanelOffset + 18 && y > this.topPos + 30 && y < this.topPos + 30 + 18)
        if(!crowEntity.canAttack)
            GuiComponent.blit(matrixStack, i + 8 - (int)leftPanelOffset, j + 30, 2, 238, 196, 18, 18, 256, 256);

        //range slider
        if(rangeSliderClicked)
            GuiComponent.blit(matrixStack, i - 5 + 14 - (int)leftPanelOffset, j + 19 + 64 - Mth.clamp(crowEntity.interactionRange + (int) (this.rangeSliderClickedPos - y), 0, 24), 1, 40, 232, 16, 5, 256, 256);
        else
            GuiComponent.blit(matrixStack, i - 5 + 14 - (int)leftPanelOffset, j + 19 + 64 - crowEntity.interactionRange, 1, 40, 238, 16, 5, 256, 256);

        MutableComponent component = Component.literal(String.valueOf(crowEntity.interactionRange));
        if(rangeSliderClicked)
            component = Component.literal(String.valueOf(Mth.clamp(crowEntity.interactionRange + (int) (this.rangeSliderClickedPos - y), 0, 24)));
        if(minecraft != null)
            minecraft.font.draw(matrixStack, component, i - 5 + 22.5f - (int)leftPanelOffset - (float)(font.width(component.getVisualOrderText()) / 2), j + 102 - font.lineHeight / 2f, 0xFF303030);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 1)
            GuiComponent.blit(matrixStack, i + 184 - 28 + 19 + (int)whitelistOffset, j + 19 + 29, 2, 231, 117, 7, 7, 256, 256);

        if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 2)
            GuiComponent.blit(matrixStack, i + 184 - 28 + 19 + (int)whitelistOffset, j + 19 + 29 + 18, 2, 231, 117, 7, 7, 256, 256);

        if(crowEntity.harvestWhitelist.size() >= whitelistPage * 3 + 3)
            GuiComponent.blit(matrixStack, i + 184 - 28 + 19 + (int)whitelistOffset, j + 19 + 29 + 18 + 18, 2, 231, 117, 7, 7, 256, 256);

        if(this.menu.crowEntity.harvestWhitelist.size() > 3 + 3 * whitelistPage) {
            GuiComponent.blit(matrixStack, i + 184 + 21 - 28 + (int)whitelistOffset, j + 19 + 88, 3, 217, 107, 7, 10, 256, 256);
        }
        if(whitelistPage > 0){
            GuiComponent.blit(matrixStack, i + 184 + 3 - 28 + (int)whitelistOffset, j + 19 + 88, 3, 210, 107, 7, 10, 256, 256);
        }

        if(this.menu.crowEntity.interactionRange < 24) {
            GuiComponent.blit(matrixStack, i + 5 - (int)leftPanelOffset + 18, j + 19 + 100 - 12, 3, 217, 107, 7, 10, 256, 256);
        }
        if(this.menu.crowEntity.interactionRange > 0){
            GuiComponent.blit(matrixStack, i + 5 - (int)leftPanelOffset, j + 19 + 100 - 12, 3, 210, 107, 7, 10, 256, 256);
        }

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

        InventoryScreen.renderEntityInInventory(this.leftPos + 94, j - 10, 25, (float)(this.leftPos + 107 - x) , (float)(j + 88 - 30 - y), crowEntity);


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
        MutableComponent hat = Component.translatable("entity.hexerei.crow_slot_0");
        MutableComponent misc1 = Component.translatable("entity.hexerei.crow_slot_1");
        MutableComponent misc2 = Component.translatable("entity.hexerei.crow_slot_2");
        MutableComponent command;
        MutableComponent helpCommand;
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



        font.draw(matrixStack, hat, this.leftPos + 45 - (float)(font.width(hat.getVisualOrderText()) / 2), j + 32, 0xFF606060);
        font.draw(matrixStack, misc1, this.leftPos + 94 - (float)(font.width(misc1.getVisualOrderText()) / 2), j + 32, 0xFF606060);
        font.draw(matrixStack, misc2, this.leftPos + 142 - (float)(font.width(misc2.getVisualOrderText()) / 2), j + 32, 0xFF606060);
//        font.draw(matrixStack, command, this.leftPos + 94 - (float)(font.width(command.getVisualOrderText()) / 2), j + 77, 0xFF606060);

        font.draw(matrixStack, command, this.leftPos + 56 - (float)(font.width(command.getVisualOrderText()) / 2), j + 77, 0xFF606060);
        font.draw(matrixStack, helpCommand, this.leftPos + 131 - (float)(font.width(helpCommand.getVisualOrderText()) / 2), j + 77, 0xFF606060);

//        InventoryScreen.renderEntityInInventory(this.leftPos + 107, j + 88, 20, (float)(this.leftPos + 107 - x) , (float)(j + 88 - 30 - y), (LivingEntity) crowEntity);
//
//        RenderSystem.enableDepthTest();

//        MutableComponent command;
//        MutableComponent helpCommand;
//        MutableComponent crowSelect;
//        if(this.menu.getCommand() == 0)
//            command = Component.translatable("entity.hexerei.crow_command_gui_0");
//        else if(this.menu.getCommand() == 1)
//            command = Component.translatable("entity.hexerei.crow_command_gui_1");
//        else if(this.menu.getCommand() == 2)
//            command = Component.translatable("entity.hexerei.crow_command_gui_2");
//        else {
//            command = Component.translatable("entity.hexerei.crow_command_gui_3");
//
//        }
//        if(this.menu.getHelpCommand() == 0)
//        {
//            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_0");
//        }else if(this.menu.getHelpCommand() == 1)
//        {
//            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_1");
//        } else
//        {
//            helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_2");
//        }
//
//        minecraft.font.draw(matrixStack, command, this.leftPos + 56 - (float)(font.width(command.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
//        minecraft.font.draw(matrixStack, helpCommand, this.leftPos + 131 - (float)(font.width(helpCommand.getVisualOrderText()) / 2), this.topPos + 63 - 14, 0xFF606060);
//        minecraft.font.draw(matrixStack, Component.translatable("entity.hexerei.crow_flute_perch"), this.leftPos + 128 - (float)(font.width(Component.translatable("entity.hexerei.crow_flute_perch").getVisualOrderText()) / 2), this.topPos + 91, 0xFFAAAAAA);

        if(!crowEntity.harvestWhitelist.isEmpty() && whitelistOffset > 21){
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pushPose();
            matrixStack.translate(this.leftPos + 186f - 28 + (int)whitelistOffset, j + 73, 100.0F);
            matrixStack.translate(8.0F, -8.0F, 0.0F);
            matrixStack.scale(11.0F, 11.0F, 11.0F);
            matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
            Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);
            float zRot = 0;
            float xRot = 20;
            float yRot = 130 * (whitelistOffset - 21) / 8f;
            matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) xRot));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
            matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            Lighting.setupFor3DItems();
            matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -90));
            int max3 = 0;
            for(int itor = whitelistPage * 3; itor < crowEntity.harvestWhitelist.size(); itor++){
                max3++;
                if(max3 > 3)
                    break;
                matrixStack.pushPose();
                matrixStack.translate(0F, -(max3 - 1) * 1.75f, 0.0F);
                BlockState state = crowEntity.harvestWhitelist.get(itor).defaultBlockState();
                if (state.hasProperty(BlockStateProperties.AGE_1))
                    state = state.setValue(BlockStateProperties.AGE_1, 1);
                else if (state.hasProperty(BlockStateProperties.AGE_2))
                    state = state.setValue(BlockStateProperties.AGE_2, 2);
                else if (state.hasProperty(BlockStateProperties.AGE_3))
                    state = state.setValue(BlockStateProperties.AGE_3, 3);
                else if (state.hasProperty(BlockStateProperties.AGE_4))
                    state = state.setValue(BlockStateProperties.AGE_4, 4);
                else if (state.hasProperty(BlockStateProperties.AGE_5))
                    state = state.setValue(BlockStateProperties.AGE_5, 5);
                else if (state.hasProperty(BlockStateProperties.AGE_7))
                    state = state.setValue(BlockStateProperties.AGE_7, 7);

                matrixStack.scale((whitelistOffset - 21) / 8f, (whitelistOffset - 21) / 8f, (whitelistOffset - 21) / 8f);
                renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                if(state.hasProperty(PickableDoubleFlower.HALF)){
                    matrixStack.pushPose();
                    matrixStack.translate(0F, 1, 0.0F);
                    state = state.setValue(PickableDoubleFlower.HALF, DoubleBlockHalf.UPPER);
                    renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                    matrixStack.popPose();
                }
                matrixStack.popPose();
            }
            buffer.endBatch();
            matrixStack.popPose();
        }

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
    public boolean mouseReleased(double x, double y, int button) {
        boolean mouseReleased = super.mouseReleased(x, y, button);

        if(this.rangeSliderClicked) {
            this.rangeSliderClicked = false;
            crowEntity.interactionRange = Mth.clamp(crowEntity.interactionRange + (int) (this.rangeSliderClickedPos - y), 0, 24);
            HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(crowEntity, crowEntity.interactionRange));
        }

        return mouseReleased;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);
        int i = this.leftPos;
        int j = this.topPos - OFFSET;

        if (whitelistOffset > 21 && x > i + 190f - 28 + (int)whitelistOffset && x < i + 190f - 28 + (int)whitelistOffset + 18 && y > j + 27 && y < j + 27 + 18) {
            if(CrowWhitelistEvent.whiteListingCrow == null || this.menu.crowEntity != CrowWhitelistEvent.whiteListingCrow) {
                CrowWhitelistEvent.whiteListingCrow = this.menu.crowEntity;

                if(this.crowEntity.getOwner() instanceof Player)
                    ((Player)this.crowEntity.getOwner()).displayClientMessage(Component.translatable("Right Click a harvestable block to add to the whitelist"), true);
            }
            else
                CrowWhitelistEvent.whiteListingCrow = null;

            HexereiPacketHandler.sendToServer(new PlayerWhitelistingForCrowSyncToServer(CrowWhitelistEvent.whiteListingCrow != null));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        //slider bar
        else if (leftPanelOffset > 21 && x > i + 9 - (int)leftPanelOffset && x < i + 9 - (int)leftPanelOffset + 16 && y > j + 30 + 52 - crowEntity.interactionRange && y < j + 30 + 52 - crowEntity.interactionRange + 7) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.rangeSliderClicked = true;
            this.rangeSliderClickedPos = y;
        }
        //slider inside
        else if (leftPanelOffset > 21 && x > i + 9 + 6 - (int)leftPanelOffset && x < i + 9 + 10 - (int)leftPanelOffset && y > j + 30 + 55 - 24 && y < j + 30 + 56) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            int newPos = (j + 30 + 55) - (int)y;
            HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(crowEntity, newPos));
            this.rangeSliderClicked = true;
            this.rangeSliderClickedPos = y;
        }
        //crow combat button
        else if (leftPanelOffset > 21 && x > i + 8 - (int)leftPanelOffset && x < i + 8 - (int)leftPanelOffset + 18 && y > j + 30 && y < j + 30 + 18) {
            HexereiPacketHandler.sendToServer(new CrowCanAttackToServer(crowEntity, !crowEntity.canAttack));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        //decrease slider
        else if (crowEntity.getInteractionRange() > 0 && leftPanelOffset > 21 && x > i + 5 - (int)leftPanelOffset && x < i + 5 - (int)leftPanelOffset + 7 && y > j + 107 && y < j + 107 + 10) {
            HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(crowEntity, crowEntity.getInteractionRange() - 1));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        //increased slider
        else if (crowEntity.getInteractionRange() < 24 && leftPanelOffset > 21 && x > i + 5 + 18 - (int)leftPanelOffset && x < i + 5 + 18 - (int)leftPanelOffset + 7 && y > j + 107 && y < j + 107 + 10) {
            HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(crowEntity, crowEntity.getInteractionRange() + 1));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        else if (whitelistOffset > 21 && x > i + 184 - 28 + 19 + (int)whitelistOffset && x < i + 184 - 28 + 19 + (int)whitelistOffset + 7 && y > j + 19 + 29 && y < j + 19 + 29 + 7) {
            if(this.menu.crowEntity.harvestWhitelist.size() > whitelistPage * 3) {
                this.menu.crowEntity.harvestWhitelist.remove(whitelistPage * 3);
                if(this.menu.crowEntity.harvestWhitelist.size() - whitelistPage * 3 == 0 && whitelistPage > 0)
                    whitelistPage--;
                HexereiPacketHandler.sendToServer(new CrowWhitelistSyncToServer(this.menu.crowEntity, this.menu.crowEntity.harvestWhitelist));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
        else if (whitelistOffset > 21 && x > i + 184 - 28 + 19 + (int)whitelistOffset && x < i + 184 - 28 + 19 + (int)whitelistOffset + 7 && y > j + 19 + 29 + 18 && y < j + 19 + 29 + 18 + 7) {
            if(this.menu.crowEntity.harvestWhitelist.size() > 1 + whitelistPage * 3) {
                this.menu.crowEntity.harvestWhitelist.remove(1 + whitelistPage * 3);
                HexereiPacketHandler.sendToServer(new CrowWhitelistSyncToServer(this.menu.crowEntity, this.menu.crowEntity.harvestWhitelist));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
        else if (whitelistOffset > 21 && x > i + 184 - 28 + 19 + (int)whitelistOffset && x < i + 184 - 28 + 19 + (int)whitelistOffset + 7 && y > j + 19 + 29 + 18 + 18 && y < j + 19 + 29 + 18 + 18 + 7) {
            if(this.menu.crowEntity.harvestWhitelist.size() > 2 + whitelistPage * 3) {
                this.menu.crowEntity.harvestWhitelist.remove(2 + whitelistPage * 3);
                HexereiPacketHandler.sendToServer(new CrowWhitelistSyncToServer(this.menu.crowEntity, this.menu.crowEntity.harvestWhitelist));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
        else if (whitelistOffset > 21 && x > i + 184 + 3 - 28 + (int)whitelistOffset && x < i + 184 + 3 - 28 + (int)whitelistOffset + 7 && y > j + 19 + 88 && y < j + 19 + 88 + 10) {

            if(whitelistPage > 0) {
                whitelistPage--;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
        else if (whitelistOffset > 21 && x > i + 184 + 21 - 28 + (int)whitelistOffset && x < i + 184 + 20 - 28 + (int)whitelistOffset + 7 && y > j + 19 + 88 && y < j + 19 + 88 + 10) {

            if(this.menu.crowEntity.harvestWhitelist.size() > 3 + 3 * whitelistPage) {
                whitelistPage++;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
        else if (x > i + 23f && x < i + 23f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setCommand(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_" + 0, this.crowEntity.getName()), true);
        }
        else if (x > i + 43f && x < i + 43f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setCommand(1);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_" + 1, this.crowEntity.getName()), true);
        }
        else if (x > i + 63f && x < i + 63f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setCommand(2);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player)
                ((Player)this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_" + 2, this.crowEntity.getName()), true);
        }
        else if (x > i + 83f && x < i + 83f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setCommand(3);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_" + this.menu.getHelpCommand(), this.crowEntity.getName()), true);
            }
        }
        else if (x > i + 107f && x < i + 107f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setHelpCommand(0);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_0", this.crowEntity.getName()), true);
            }
        }
        else if (x > i + 127f && x < i + 127f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setHelpCommand(1);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_1", this.crowEntity.getName()), true);
            }
        }
        else if (x > i + 147f && x < i + 147f + 18 && y > j + 92 && y < j + 92 + 18) {
            this.menu.setHelpCommand(2);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(this.crowEntity.getOwner() instanceof Player) {
                    ((Player) this.crowEntity.getOwner()).displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_2", this.crowEntity.getName()), true);
            }
        }

        return mouseClicked;
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch(rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());

                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
            }

        }
    }
}