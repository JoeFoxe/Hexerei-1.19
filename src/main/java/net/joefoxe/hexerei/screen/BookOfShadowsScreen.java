package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.renderer.CrystalBallRenderer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.UpdateBookDataToServer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookOfShadowsScreen extends Screen {
    public final BookOfShadowsAltarTile altar;
    protected int left;
    protected int top;
    int img_width = 244;
    int img_height = 170;
    float mouseGrabbedX = 0;
    float mouseGrabbedY = 0;

    ItemStack fromItem = null;
    Player player;
    InteractionHand hand;
    float flippingFast = 0;
    float flippingFastO = 0;
    public BookOfShadowsScreen(BookOfShadowsAltarTile altar) {
        super(Component.translatable("screen.hexerei.book_of_shadows"));
        this.minecraft = Minecraft.getInstance();
        this.altar = altar;
    }

    public BookOfShadowsScreen(Player player, InteractionHand hand) {
        super(Component.translatable("screen.hexerei.book_of_shadows"));
        ItemStack stack = player.getItemInHand(hand);
        this.player = player;
        this.hand = hand;
        this.minecraft = Minecraft.getInstance();
        this.fromItem = stack;

        this.altar = ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get().create(BlockPos.ZERO, ModBlocks.BOOK_OF_SHADOWS_ALTAR.get().defaultBlockState());
        if (this.altar != null) {
            this.altar.currentBook = stack.getOrDefault(ModDataComponents.BOOK, BookData.EMPTY);
            this.altar.fromItem = true;
            if (this.altar.currentBook.isOpened()) {
                this.altar.openedPercent = 0.5f;
                this.altar.floppedPercent = 0.5f;
            }
        }
        else
            onClose();
    }

    protected void init() {
        this.left = this.width / 2 - this.img_width / 2;
        this.top = this.height / 2 - this.img_height / 2;
    }

    public Vec2 getLeftCursor(int mouseX, int mouseY){
        float inputXl = (mouseX - (this.left + img_width / 2f) + 123) / 123f * 6.55f - 0.5f;
        float inputYl = (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 1;
        return new Vec2(inputXl, inputYl);
    }

    public Vec2 getRightCursor(int mouseX, int mouseY){
        float inputXr = ((mouseX - (this.left + img_width / 2f)) / 123f * 6.55f - 0.5f) + 0.25f;
        float inputYr = (mouseY - (this.top + img_height / 2f) + 87) / 174 * 9.1f - 1;
        return new Vec2(inputXr, inputYr);
    }

    private float easeInOutElastic(double x) {

        double c5 = (2 * Math.PI) / 4.5;

        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? 4 * x * x * x
                : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1);

    }
    public static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;

        return 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {


        ItemStack stack = this.fromItem != null ? this.fromItem : altar.itemHandler.getStackInSlot(0);

        if(!(stack.getItem() instanceof HexereiBookItem))
            return;

        Vec2 left = getLeftCursor(mouseX, mouseY);
        Vec2 right = getRightCursor(mouseX, mouseY);

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

        altar.degreesSpunRender = CrystalBallRenderer.lerpAngle(altar.degreesSpunOld, altar.degreesSpun, pPartialTick);// book.drawing.moveToAngle(book.degreesSpun, book.degreesSpunTo, book.degreesSpunSpeed * pPartialTick);
        altar.buttonScaleRender = Math.max(0, BookOfShadowsAltarTile.easeButtons(Mth.lerp(pPartialTick, altar.buttonScaleOld, altar.buttonScale)));
        altar.degreesOpenedRender = BookOfShadowsAltarTile.easeOpened(Mth.lerp(pPartialTick, altar.openedPercentOld, altar.openedPercent)) * 90;
        altar.degreesFloppedRender = BookOfShadowsAltarTile.easeFlop(Mth.lerp(pPartialTick, altar.floppedPercentOld, altar.floppedPercent)) * 90;

        altar.pageOneRotationRender = Mth.lerp(pPartialTick, altar.pageOneRotationLast, altar.pageOneRotation);
        altar.pageTwoRotationRender = Mth.lerp(pPartialTick, altar.pageTwoRotationLast, altar.pageTwoRotation);

        if (altar.turnPage == 1 || altar.turnPage == 2) {
            altar.degreesOpenedRender += (Mth.sin(altar.pageOneRotationRender / 180 * Mth.PI) * 5) + (Mth.sin(altar.pageTwoRotationRender / 180 * Mth.PI) * 5);
        } else {
            altar.degreesOpenedRender += (Mth.abs(Mth.sin(Mth.lerp(pPartialTick, this.flippingFastO, this.flippingFast) * Mth.PI / 2f))) * 25f;
        }

        pGuiGraphics.pose().scale(scale, -scale, scale);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(altar.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, 8f / 16f);
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(Math.max(0, altar.degreesOpenedRender) / 90 * Mth.PI) * -7.5f));
        if (altar.turnPage == 1 || altar.turnPage == 2){
//            pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees((Mth.sin(book.pageOneRotationRender / 180 * Mth.PI) * 2.5f) - (Mth.sin(book.pageTwoRotationRender / 180 * Mth.PI) * 2.5f)));
            pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(altar.pageOneRotationRender / 180 * Mth.PI) * -10f));
            pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(altar.pageTwoRotationRender / 180 * Mth.PI) * -10f));
        }
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(Mth.lerp(pPartialTick, this.flippingFastO, this.flippingFast) * Mth.PI / 2f) * -5f));
        pGuiGraphics.pose().translate(0f, 0, -8f / 16f);
        pGuiGraphics.pose().translate(-8f / 16f, -23f / 16f, -8f / 16f);
        pGuiGraphics.pose().translate(0.0041f,0,0);
        MultiBufferSource.BufferSource buffer = pGuiGraphics.bufferSource();


        boolean isBookOfShadows = altar.currentBook != null && altar.currentBook.book().equals(HexereiUtil.getResource("book_of_shadows"));

        float degreesSpunRender = altar.degreesSpunRender;
        float degreesOpenedRender = altar.degreesOpenedRender;
//        float openedPercent = book.openedPercent;
        float floppedPercent = altar.floppedPercent;
//        float floppedPercent = book.closestDist;
        float degreesFloppedRender = altar.degreesFloppedRender;

        altar.degreesSpunRender = -180;
        altar.degreesOpenedRender = Math.clamp(altar.degreesOpenedRender, 0, 90);
//        book.openedPercent = 0;
        altar.floppedPercent = 0;
        altar.degreesFloppedRender = 0;
        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());

        Lighting.setupForFlatItems();
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altar.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(altar.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-altar.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(altar.degreesFloppedRender / 10f) / 32);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(altar.degreesOpenedRender - 90));
        pGuiGraphics.pose().translate(1f / 32f * (altar.degreesOpenedRender / 90f), 1f / 32f * (1 - (altar.degreesOpenedRender / 90f)), 0);
        pGuiGraphics.pose().translate(1f / 512f, 0, 0);
        if (isBookOfShadows) {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        } else {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        }
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altar.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(altar.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-altar.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(altar.degreesFloppedRender / 10f) / 32);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-(altar.degreesOpenedRender - 90)));
        pGuiGraphics.pose().translate(-1f / 32f * (altar.degreesOpenedRender / 90f), 1f / 32f * (1 - (altar.degreesOpenedRender / 90f)), 0);
        pGuiGraphics.pose().translate(-1f / 512f, 0, 0);
        if (isBookOfShadows) {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        } else {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
        }
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8f / 16f, 18f / 16f, 8f / 16f);
        pGuiGraphics.pose().translate((float) Math.sin((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altar.degreesSpunRender) / 57.3f) / 32f * (altar.degreesOpenedRender / 5f - 12f));
        pGuiGraphics.pose().translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altar.degreesFlopped / 90) - 1) / 16f), 0);
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(altar.degreesSpunRender));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-altar.degreesFloppedRender));
        pGuiGraphics.pose().translate(0, 0, -(altar.degreesFloppedRender / 10f) / 32);
        if (isBookOfShadows) {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_OF_SHADOWS_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
        } else {
            renderBlock(pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, ModBlocks.BOOK_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
        }
        pGuiGraphics.pose().popPose();

        if (buffer instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();

        Lighting.setupForFlatItems();
        if (altar.openedPercent != 1) {
            altar.drawing.drawPages(altar, left.x, left.y, right.x, right.y, pGuiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pPartialTick, PageDrawing.DrawingType.SCREEN);
        }

        buffer.endBatch();

        pGuiGraphics.pose().popPose();


        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 2500);

        Lighting.setupForFlatItems();
//        pGuiGraphics.drawString(this.font, this.title.getVisualOrderText(), this.width / 2 - font.width(this.title.getVisualOrderText()) / 2, this.top - 30, 0x333333, false);



        if (altar.currentBook.isOpened() && altar.openedPercent < 0.15f && altar.turnPage == 0 && (altar.drawing.drawTooltipStack || altar.drawing.drawTooltipText)){
            List<Component> tooltip = altar.drawing.tooltipStack != null && !altar.drawing.tooltipStack.isEmpty() ? altar.drawing.tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL) : new ArrayList<>();

            if (!tooltip.isEmpty())
                tooltip.addAll(altar.drawing.tooltipText);
            else
                tooltip = altar.drawing.tooltipText;

            if (altar.drawing.tooltipStack != null && !altar.drawing.tooltipStack.isEmpty()) {
                String modId = HexereiUtil.getRegistryName(altar.drawing.tooltipStack.getItem()).getNamespace();
                String modName = PageDrawing.getModNameForModId(modId);
                MutableComponent modNameComponent = Component.translatable(modName);
                modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
                if (tooltip.isEmpty() || !tooltip.getLast().getString().equals(modName))
                    tooltip.add(modNameComponent);
            }

            pGuiGraphics.renderTooltip(this.font, tooltip, altar.drawing.tooltipStack != null && !altar.drawing.tooltipStack.isEmpty() ? altar.drawing.tooltipStack.getTooltipImage() : Optional.empty(), mouseX, mouseY);
        }

        pGuiGraphics.pose().popPose();


        altar.degreesSpunRender = degreesSpunRender;
        altar.degreesOpenedRender = degreesOpenedRender;
//        book.openedPercent = openedPercent;
        altar.floppedPercent = floppedPercent;
        altar.degreesFloppedRender = degreesFloppedRender;

        Lighting.setupFor3DItems();
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    /**
     * Handles keypresses, clipboard functions, and page turning
     */
//    private boolean bookKeyPressed(BookWritableTextBox writable, int keyCode, int scanCode, int modifiers) {
//        if (Screen.isSelectAll(keyCode)) {
//            writable.pageEdit.selectAll();
//            return true;
//        } else if (Screen.isCopy(keyCode)) {
//            writable.pageEdit.copy();
//            return true;
//        } else if (Screen.isPaste(keyCode)) {
//            writable.pageEdit.paste();
//            return true;
//        } else if (Screen.isCut(keyCode)) {
//            writable.pageEdit.cut();
//            return true;
//        } else {
//            TextFieldHelper.CursorStep textfieldhelper$cursorstep = Screen.hasControlDown()
//                    ? TextFieldHelper.CursorStep.WORD
//                    : TextFieldHelper.CursorStep.CHARACTER;
//            switch (keyCode) {
//                case 257:
//                case 335:
//                    writable.pageEdit.insertText("\n");
//                    return true;
//                case 259:
//                    writable.pageEdit.removeFromCursor(-1, textfieldhelper$cursorstep);
//                    return true;
//                case 261:
//                    writable.pageEdit.removeFromCursor(1, textfieldhelper$cursorstep);
//                    return true;
//                case 262:
//                    writable.pageEdit.moveBy(1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
//                    return true;
//                case 263:
//                    writable.pageEdit.moveBy(-1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
//                    return true;
//                case 264:
//                    writable.keyDown();
//                    return true;
//                case 265:
//                    writable.keyUp();
//                    return true;
//                case 268:
//                    writable.keyHome();
//                    return true;
//                case 269:
//                    writable.keyEnd();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (super.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        } else {
//            BookWritableTextBox writable = PageDrawing.focusedWritableTextBox;
//            if (writable != null){
//                if (this.bookKeyPressed(writable, keyCode, scanCode, modifiers)) {
//                    writable.clearDisplayCache();
//                    return true;
//                }
//            }
//            return false;
//        }
//    }
//    /**
//     * Called when a character is typed within the GUI element.
//     * <p>
//     * @return {@code true} if the event is consumed, {@code false} otherwise.
//     *
//     * @param codePoint the code point of the typed character.
//     * @param modifiers the keyboard modifiers.
//     */
//    @Override
//    public boolean charTyped(char codePoint, int modifiers) {
//        if (super.charTyped(codePoint, modifiers)) {
//            return true;
//        } else if (StringUtil.isAllowedChatCharacter(codePoint)) {
//            if (PageDrawing.focusedWritableTextBox != null){
//                BookWritableTextBox writable = PageDrawing.focusedWritableTextBox.getRight();
//                writable.client.pageEdit.insertText(Character.toString(codePoint));
//                writable.client.clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
//                return true;
//            }
//            return false;
//        } else {
//            return false;
//        }
//    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        Vec2 left = getLeftCursor((int)mouseX, (int)mouseY);
        Vec2 right = getRightCursor((int)mouseX, (int)mouseY);

        if (altar.openedPercent < 0.15f && altar.drawing.releaseClick(altar, Minecraft.getInstance().player, left.x, left.y, right.x, right.y, PageDrawing.DrawingType.SCREEN)) {
            if (this.fromItem != null) {
                HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
            }
            return true;
        }

        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        if (handler.mouseGrabbed) {
            handler.mouseGrabbed = false;
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0, 0);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        PageDrawing.clearFocusedWritableTextBox();
        if (this.fromItem != null) {
            HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
        }


        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        if (handler.mouseGrabbed)
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0, 0);
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2 left = getLeftCursor((int)mouseX, (int)mouseY);
        Vec2 right = getRightCursor((int)mouseX, (int)mouseY);
        if (!altar.currentBook.isOpened() && altar.openedPercent > 0.75f) {
            altar.currentBook = altar.currentBook.setOpened(true);
            if (this.fromItem != null) {
                HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
            }
        } else if (altar.openedPercent < 0.35f && altar.drawing.interactClick(altar, Minecraft.getInstance().player, left.x, left.y, right.x, right.y, PageDrawing.DrawingType.SCREEN)) {
            if (this.fromItem != null) {
                HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
            }

            boolean clicked = false;

            BookEntries bookEntries = BookManager.getBookEntries(altar.currentBook.getBook());
            if (bookEntries != null) {
                for (BookChapter bookChapter : bookEntries.chapterList) {
                    for (BookPageEntry bookPageEntry : bookChapter.pages) {

                        BookPage page = BookManager.getBookPages(altar.currentBook.getBook(), ResourceLocation.parse(bookPageEntry.location));
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
        flippingFastO = flippingFast;
        if (this.altar.turnPage == -1) {
            flippingFast = HexereiUtil.moveTo(flippingFast, 1, 0.025f + 0.1f * (1 - flippingFast));
        } else {
            flippingFast = HexereiUtil.moveTo(flippingFast, 0, 0.05f + 0.1f * (1 - flippingFast));
        }
        if (this.fromItem != null) {
            this.altar.tickClient();
            this.altar.tickBook(this.fromItem, true);
        }
        if (!this.isValid()) {
            this.onDone();
        }
    }

    private boolean isValid() {
        if (this.fromItem != null)
            return this.minecraft != null && this.minecraft.player != null && this.altar != null && this.altar.currentBook != null;
        return this.minecraft != null && this.minecraft.player != null && this.altar != null && this.altar.currentBook != null && this.altar.openedPercent < 1;
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