package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookOfShadowsScreen extends Screen {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/owl_courier_delivery_gui.png");
    public final BookOfShadowsAltarTile book;
    protected int left;
    protected int top;
    int img_width = 244;
    int img_height = 170;
    float mouseGrabbedX = 0;
    float mouseGrabbedY = 0;

    public BookOfShadowsScreen(BookOfShadowsAltarTile book) {
        super(Component.translatable("screen.hexerei.book_of_shadows"));
        this.minecraft = Minecraft.getInstance();
        this.book = book;
    }

    protected void init() {
        this.left = this.width / 2 - this.img_width / 2;
        this.top = this.height / 2 - this.img_height / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {


        float inputXl = (mouseX - (this.left + img_width / 2f) + 123) / 123f * 6.55f - 0.5f;
        float inputYl = (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;
        float inputXr = ((mouseX - (this.left + img_width / 2f)) / 123f * 6.55f - 0.5f) + 0.25f;
        float inputYr = (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(this.left + img_width / 2f, this.top + img_height / 2f, 1500);

        Lighting.setupForFlatItems();
        float scale;
        scale = 298.75f;
        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        if ((int) guiScale == 3)
            scale = 306.38f;
        if ((int) guiScale == 4)
            scale = 298.86f;
        if ((int) guiScale == 5)
            scale = 294.11f;
        scale = 306.38f;
        pGuiGraphics.pose().scale(scale, -scale, scale);
        pGuiGraphics.pose().translate(-8f / 16f, -24f / 16f, -8f / 16f);
        pGuiGraphics.pose().translate(0.0041f,0,0);

        ItemStack stack = book.itemHandler.getStackInSlot(0);

        if(!(stack.getItem() instanceof HexereiBookItem))
            return;
        MultiBufferSource.BufferSource buffer = pGuiGraphics.bufferSource();

        float degreesSpunRender = book.degreesSpunRender;
        float degreesOpenedRender = book.degreesOpenedRender;
        float openedPercent = book.openedPercent;
        float floppedPercent = book.floppedPercent;

        book.degreesSpunRender = -180;
        book.degreesOpenedRender = Math.clamp(book.degreesOpenedRender, 0, 90);
        book.openedPercent = 0;
        book.floppedPercent = 0;

        Lighting.setupForFlatItems();
        if (book.openedPercent != 1) {
            book.drawing.drawPages(book, inputXl, inputYl, inputXr, inputYr, pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pPartialTick, PageDrawing.DrawingType.SCREEN);
        }
        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());

        Lighting.setupForFlatItems();
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - book.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(book.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-book.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(book.degreesFloppedRender / 10f) / 32);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(book.degreesOpenedRender - 90));
        pGuiGraphics.pose().translate(1f / 32f * (book.degreesOpenedRender / 90f), 1f / 32f * (1 - (book.degreesOpenedRender / 90f)), 0);
        pGuiGraphics.pose().translate(1f / 512f, 0, 0);

        renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
        renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - book.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(book.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-book.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(book.degreesFloppedRender / 10f) / 32);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-(book.degreesOpenedRender - 90)));
        pGuiGraphics.pose().translate(-1f / 32f * (book.degreesOpenedRender / 90f), 1f / 32f * (1 - (book.degreesOpenedRender / 90f)), 0);
        pGuiGraphics.pose().translate(-1f / 512f, 0, 0);

        renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
        renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((book.degreesSpunRender) / 57.3f) / 32f * (book.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - book.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(book.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-book.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(book.degreesFloppedRender / 10f) / 32);
        renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
        pGuiGraphics.pose().popPose();

        buffer.endBatch();

        pGuiGraphics.pose().popPose();


        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 2500);

        Lighting.setupForFlatItems();
//        pGuiGraphics.drawString(this.font, this.title.getVisualOrderText(), this.width / 2 - font.width(this.title.getVisualOrderText()) / 2, this.top - 30, 0x333333, false);



        if (book.currentBook.isOpened() && book.turnPage == 0 && (book.drawing.drawTooltipStack || book.drawing.drawTooltipText)){
            List<Component> tooltip = book.drawing.tooltipStack != null && !book.drawing.tooltipStack.isEmpty() ? book.drawing.tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL) : new ArrayList<>();

            if (!tooltip.isEmpty())
                tooltip.addAll(book.drawing.tooltipText);
            else
                tooltip = book.drawing.tooltipText;

            if (book.drawing.tooltipStack != null && !book.drawing.tooltipStack.isEmpty()) {
                String modId = HexereiUtil.getRegistryName(book.drawing.tooltipStack.getItem()).getNamespace();
                String modName = PageDrawing.getModNameForModId(modId);
                MutableComponent modNameComponent = Component.translatable(modName);
                modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
                if (tooltip.isEmpty() || !tooltip.getLast().getString().equals(modName))
                    tooltip.add(modNameComponent);
            }

            pGuiGraphics.renderTooltip(this.font, tooltip, book.drawing.tooltipStack != null && !book.drawing.tooltipStack.isEmpty() ? book.drawing.tooltipStack.getTooltipImage() : Optional.empty(), mouseX, mouseY);
        }

        pGuiGraphics.pose().popPose();


        book.degreesSpunRender = degreesSpunRender;
        book.degreesOpenedRender = degreesOpenedRender;
        book.openedPercent = openedPercent;
        book.floppedPercent = floppedPercent;

        Lighting.setupFor3DItems();
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float inputXl = (float) (mouseX - (this.left + img_width / 2f) + 123) / 123f * 6.55f - 0.5f;
        float inputYl = (float) (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;
        float inputXr = (float) ((mouseX - (this.left + img_width / 2f)) / 123f * 6.55f - 0.5f) + 0.25f;
        float inputYr = (float) (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;

        if (book.drawing.releaseClick(book, Minecraft.getInstance().player, inputXl, inputYl, inputXr, inputYr, PageDrawing.DrawingType.SCREEN))
            return true;

        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        if (handler.mouseGrabbed) {
            handler.mouseGrabbed = false;
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0, 0);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        if (handler.mouseGrabbed)
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0, 0);
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float inputXl = (float) (mouseX - (this.left + img_width / 2f) + 123) / 123f * 6.55f - 0.5f;
        float inputYl = (float) (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;
        float inputXr = (float) ((mouseX - (this.left + img_width / 2f)) / 123f * 6.55f - 0.5f) + 0.25f;
        float inputYr = (float) (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 2;

        if (book.drawing.interactClick(book, Minecraft.getInstance().player, inputXl, inputYl, inputXr, inputYr, PageDrawing.DrawingType.SCREEN)) {

            boolean clicked = false;

            BookEntries bookEntries = BookManager.getBookEntries();
            if (bookEntries != null) {
                for (BookChapter bookChapter : bookEntries.chapterList) {
                    for (BookPageEntry bookPageEntry : bookChapter.pages) {

                        BookPage page = BookManager.getBookPages(ResourceLocation.parse(bookPageEntry.location));
                        if (page != null) {
                            for (BookEntity bookEntity : page.entityList) {

                                if (bookEntity.markedForUpdate) {
                                    if (bookEntity.clicked) {
                                        clicked = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }


            MouseHandler handler = Minecraft.getInstance().mouseHandler;
            if (!handler.mouseGrabbed && clicked) {
                handler.mouseGrabbed = true;
                this.mouseGrabbedX = (float) handler.xpos;
                this.mouseGrabbedY = (float) handler.ypos;
                InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.mouseGrabbedX, this.mouseGrabbedY);
            }
            return true;
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    private void onDone() {
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void tick() {
        if (!this.isValid()) {
            this.onDone();
        }
    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && this.book != null && this.book.currentBook != null && this.book.openedPercent < 1;
    }


    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    public void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}