package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;
import net.joefoxe.hexerei.container.OwlContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joefoxe.hexerei.container.CofferContainer.OFFSET;

public class OwlScreen extends AbstractContainerScreen<OwlContainer> {

    private final static int FRONT_OVERLAY_BLIT_LAYER = 3;
    private final static int FRONT_BLIT_LAYER = 2;
    private final static int BACK_OVERLAY_BLIT_LAYER = 1;
    private final static int BACK_BLIT_LAYER = 0;
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/owl_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");

    public final OwlEntity owlEntity;
    public boolean quirkSideBarHidden;

    public OwlScreen(OwlContainer owlContainer, Inventory inv, Component titleIn) {
        super(owlContainer, inv, titleIn);
        owlEntity = owlContainer.owlEntity;
        titleLabelY = 1 - OFFSET;
        titleLabelX = 4;
        inventoryLabelY = 94;
        inventoryLabelX = 9;
        quirkSideBarHidden = true;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        this.renderBackground(guiGraphics, x, y, partialTicks);
        super.render(guiGraphics, x, y, partialTicks);
        int i = this.leftPos;
        int j = this.topPos;



        List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);
        if (this.quirkSideBarHidden) {
            if (!list.isEmpty()) {
                if (hovering(x, y, 9, 12, 160, 44))
                    guiGraphics.blit(GUI, i + 161, j + 45, FRONT_BLIT_LAYER, 215, 12, 7, 10, 256, 256);
                else
                    guiGraphics.blit(GUI, i + 161, j + 45, FRONT_BLIT_LAYER, 215, 1, 7, 10, 256, 256);
            }
        } else {
            guiGraphics.blit(GUI, i + 170, j + 37, FRONT_BLIT_LAYER, 230, 26, 26, 26, 256, 256);

            if (hovering(x, y, 8, 8, 161, 46))
                guiGraphics.blit(GUI, i + 162, j + 47, FRONT_BLIT_LAYER, 223, 12, 6, 6, 256, 256);
            else
                guiGraphics.blit(GUI, i + 162, j + 47, FRONT_BLIT_LAYER, 223, 1, 6, 6, 256, 256);




            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(i + 169.5, j + 63, 200.0F);
            guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
            guiGraphics.pose().scale(11.0F, 11.0F, 11.0F);
            guiGraphics.pose().mulPose(new Matrix4f().scale(1, -1, 1));
            Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);
            float zRot = 0;
            float xRot = 20;
            float yRot = 215f;
            guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
            guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            Lighting.setupFor3DItems();
            guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees((float) -45));
            if (!list.isEmpty() && minecraft != null && minecraft.level != null && minecraft.player != null) {
                BlockState state = list.getFirst().getFavoriteBlock().defaultBlockState();

                int col = minecraft.getBlockColors().getColor(state, minecraft.level, minecraft.player.blockPosition());
                renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, state, col);
                if (state.hasProperty(PickableDoublePlant.HALF)) {
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0F, 1, 0.0F);
                    state = state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
                    renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, state, col);
                    guiGraphics.pose().popPose();
                }
            }
            buffer.endBatch();
            guiGraphics.pose().popPose();

        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.leftPos + 94 - 20, j - 17 - 20, this.leftPos + 94 + 20, j - 17 + 20, 20, 0.0625F, x, y, owlEntity);

        this.renderTooltip(guiGraphics, x, y);
        this.renderButtonTooltip(guiGraphics, x, y);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }


    public boolean hovering(double mouseX, double mouseY, double width, double height, double x, double y) {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }

    public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY){

        List<Component> components = new ArrayList<>();

        List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);

        if(list.size() > 0) {
            if (!this.quirkSideBarHidden){
                if (hovering(mouseX, mouseY, 18D, 18D, 174, 41)) {
                    components.add(Component.translatable("tooltip.hexerei.owl_favorite_block").withStyle(ChatFormatting.DARK_AQUA));
                    components.add(Component.translatable("tooltip.hexerei.owl_favorite_block2").withStyle(ChatFormatting.GRAY));
                    components.add(Component.literal(""));
                    if (minecraft != null)
                        components.addAll(list.get(0).getFavoriteBlock().asItem().getDefaultInstance().getTooltipLines(Item.TooltipContext.EMPTY, minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
                }
                else if (hovering(mouseX, mouseY, 8D, 8D, 161, 46)) {
                    components.add(Component.translatable("tooltip.hexerei.owl_close_quirks_tab"));
                }
            } else {
                if (hovering(mouseX, mouseY, 9D, 12D, 160, 44)) {
                    components.add(Component.translatable("tooltip.hexerei.owl_open_quirks_tab"));
                }
            }
        }

        if (!components.isEmpty())
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);

    }

    private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int z, int color, boolean shadow){
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, z);
        guiGraphics.drawString(minecraft.font, component, 0, 0, color, shadow);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        int i = this.leftPos;
        int j = this.topPos;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        guiGraphics.blit(GUI, i, j, BACK_BLIT_LAYER, 0, 0, 188, 114, 256, 256);

        if(!owlEntity.itemHandler.getStackInSlot(0).isEmpty())
            guiGraphics.blit(GUI, i + 86 + 24, j + 50, BACK_BLIT_LAYER, 235, 31, 16, 16, 256, 256);
        if(!owlEntity.itemHandler.getStackInSlot(1).isEmpty())
            guiGraphics.blit(GUI, i + 37 + 24, j + 50, BACK_BLIT_LAYER, 235, 31, 16, 16, 256, 256);

        guiGraphics.blit(GUI, i + 81, j - 30, BACK_BLIT_LAYER, 230, 0, 26, 26, 256, 256);

        guiGraphics.blit(INVENTORY, i + 6, j + 90, BACK_BLIT_LAYER, 0, 0, 176, 100, 256, 256);
        RenderSystem.setShaderTexture(0, GUI);

        MutableComponent hat = Component.translatable("entity.hexerei.crow_slot_0");
        MutableComponent hand = Component.translatable("entity.hexerei.crow_slot_1");

        drawFont(guiGraphics, hat, this.leftPos + 45 + 24 - (float)(font.width(hat.getVisualOrderText()) / 2), j + 32, FRONT_OVERLAY_BLIT_LAYER, 0xFF606060, false);
        drawFont(guiGraphics, hand, this.leftPos + 94 + 24 - (float)(font.width(hand.getVisualOrderText()) / 2), j + 32, FRONT_OVERLAY_BLIT_LAYER, 0xFF606060, false);


    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean mouseReleased = super.mouseReleased(x, y, button);

        return mouseReleased;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);

        List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);
        if (list.size() > 0){
            if (this.quirkSideBarHidden && hovering(x, y, 9, 12, 160, 44)) {
                this.quirkSideBarHidden = !this.quirkSideBarHidden;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            else if (!this.quirkSideBarHidden && hovering(x, y, 8, 8, 161, 46)) {
                this.quirkSideBarHidden = !this.quirkSideBarHidden;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }


        return mouseClicked;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }
}