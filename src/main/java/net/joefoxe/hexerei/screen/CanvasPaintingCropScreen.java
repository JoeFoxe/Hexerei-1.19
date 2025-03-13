package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.data.books.BookPaintElement;
import net.joefoxe.hexerei.data.books.PaintSystem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.SetPaintingToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CanvasPaintingCropScreen extends Screen {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/owl_courier_delivery_gui.png");

    int ticks = 0;
    int img_width = 124;
    int img_height = 164;
    int left;
    int top;
    boolean clicked = false;
    boolean hasUV = false;
    PaintSystem paintSystem;
    BookPaintElement paintElement;


    int clickX = 0;
    int clickY = 0;
    int clickW = 16;
    int clickH = 16;

    float u0 = 0;
    float v0 = 0;
    float u1 = 1;
    float v1 = 1;

    boolean toolsVisibleOld;
    boolean draggingCenter = false;
    int draggingOffsetX = 0;
    int draggingOffsetY = 0;

    public CanvasPaintingCropScreen(BookPaintElement paintElement, PaintSystem paintSystem) {
        super(Component.translatable("screen.hexerei.canvas_painting_crop"));
        this.paintElement = paintElement;
        this.paintSystem = paintSystem;
        this.minecraft = Minecraft.getInstance();
        clickX = 0;
        clickY = 0;
        clickW = 16;
        clickH = 16;
        hasUV = true;
        setUV();
        toolsVisibleOld = paintSystem.toolsVisible;
        paintSystem.setToolsVisible(false);
    }

    private void setUV(){
        u0 = Math.clamp(this.clickX / (float)paintSystem.width, 0, 1);
        v0 = Math.clamp(this.clickY / (float)paintSystem.height, 0, 1);
        u1 = Math.clamp((this.clickX + this.clickW) / (float)paintSystem.width, 0, 1);
        v1 = Math.clamp((this.clickY + this.clickH) / (float)paintSystem.height, 0, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        if (pButton == 0) {
            int clickX = (int)((mouseX - (this.width / 2 - paintSystem.width * 2 * paintElement.scale)) / (4 * paintElement.scale));
            int clickY = (int)((mouseY - (this.height / 2 - paintSystem.height * 2 * paintElement.scale)) / (4 * paintElement.scale));
            if (clickX >= this.clickX && clickX < this.clickX + this.clickW && clickY >= this.clickY && clickY < this.clickY + this.clickH) {
                draggingCenter = true;
                draggingOffsetX = clickX - this.clickX;
                draggingOffsetY = clickY - this.clickY;
            } else {
                this.clickX = Math.clamp(clickX, 0, paintSystem.width - 1);
                this.clickY = Math.clamp(clickY, 0, paintSystem.height - 1);
                clickW = 0;
                clickH = 0;
                setUV();
                clicked = true;
            }
        } else if (pButton == 1) {
            clickW = 0;
            clickH = 0;
            u0 = 0;
            v0 = 0;
            u1 = 0;
            v1 = 0;
        }

        return super.mouseClicked(mouseX, mouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        if (clicked) {
            int w = Math.clamp((int)((mouseX - (this.width / 2 - paintSystem.width * 2 * paintElement.scale) + 4) / (4 * paintElement.scale)) - clickX, 0, paintSystem.width - clickX);
            int h = Math.clamp((int)((mouseY - (this.height / 2 - paintSystem.height * 2 * paintElement.scale) + 4) / (4 * paintElement.scale)) - clickY, 0, paintSystem.height - clickY);
            clickW = Math.min(w, h);
            clickH = Math.min(w, h);
            setUV();
        }
        if (draggingCenter) {

            clickX = Math.clamp((int)((mouseX - (this.width / 2 - paintSystem.width * 2 * paintElement.scale)) / (4 * paintElement.scale)) - draggingOffsetX, 0, paintSystem.width - clickW);
            clickY = Math.clamp((int)((mouseY - (this.height / 2 - paintSystem.height * 2 * paintElement.scale)) / (4 * paintElement.scale)) - draggingOffsetY, 0, paintSystem.height - clickH);
            setUV();
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int pButton) {

        if (clicked){
            clicked = false;
            setUV();
            hasUV = clickW != 0;
        }
        if (draggingCenter){
            draggingCenter = false;
        }
        return super.mouseClicked(mouseX, mouseY, pButton);
    }

    @Override
    public void onClose() {

        if (hasUV){
            String loc = paintSystem.getImageLocation().toString();
            HexereiPacketHandler.sendToServer(new SetPaintingToServer(loc, u0, u1, v0, v1));
        }

        paintSystem.setToolsVisible(toolsVisibleOld);
        super.onClose();
    }

    @Override
    protected void init() {

        this.left = this.width / 2 - this.img_width / 2;
        this.top = this.height / 2 - this.img_height / 2;

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 5);

        Lighting.setupForFlatItems();
        pGuiGraphics.drawCenteredString(this.font, Component.literal(this.clickW + " x " + this.clickH), this.width / 2 - 148, this.top + 40, 0x999999);

        float scale = 2 * paintElement.scale;
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/canvas.png"),
                    this.width / 2 - paintSystem.width * scale + u0 * paintSystem.width * scale * 2,
                    this.width / 2 - paintSystem.width * scale + u1 * paintSystem.width * scale * 2,
                    this.height / 2 - paintSystem.height * scale + v0 * paintSystem.height * scale * 2,
                    this.height / 2 - paintSystem.height * scale + v1 * paintSystem.height * scale * 2,
                    1, 0, 1, 0, 1, 0.45f);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/canvas.png"),
                    this.width / 2 - paintSystem.width * scale + u0 * paintSystem.width * scale * 2,
                    this.width / 2 - paintSystem.width * scale + u1 * paintSystem.width * scale * 2,
                    this.height / 2 - paintSystem.height * scale + v0 * paintSystem.height * scale * 2,
                    this.height / 2 - paintSystem.height * scale + v1 * paintSystem.height * scale * 2,
                    -1, 0, 1, 0, 1, 1);

        innerBlit(pGuiGraphics, paintSystem.getImageLocation(), this.width / 2 - paintSystem.width * scale,this.width / 2 + paintSystem.width * scale, this.height / 2 - paintSystem.height * scale, this.height / 2 + paintSystem.height * scale, 0, 0, 1, 0, 1, 1);

        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/canvas.png"), this.width / 2 - 180,this.width / 2 - 180 + 32 * 2, this.height / 2 - 16 * 2, this.height / 2 + 16 * 2, -1, 0, 1, 0, 1, 1);
        if (this.clickW != 0)
            innerBlit(pGuiGraphics, paintSystem.getImageLocation(), this.width / 2 - 180,this.width / 2 - 180 + 32 * 2, this.height / 2 - 16 * 2, this.height / 2 + 16 * 2, 0, u0, u1, v0, v1, 1);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/blank.png"), this.width / 2 - paintSystem.width * scale,this.width / 2 + paintSystem.width * scale, this.height / 2 - paintSystem.height * scale, this.height / 2 + paintSystem.height * scale, -4, 0, 1, 0, 1, 0.5f);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/blank.png"), this.width / 2 - paintSystem.width * scale - 4,this.width / 2 - paintSystem.width * scale, this.height / 2 - paintSystem.height * scale - 4, this.height / 2 + paintSystem.height * scale + 4, -5, 0, 1, 0, 1, 0, 0, 0, 0.975f);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/blank.png"), this.width / 2 + paintSystem.width * scale,this.width / 2 + paintSystem.width * scale + 4, this.height / 2 - paintSystem.height * scale - 4, this.height / 2 + paintSystem.height * scale + 4, -5, 0, 1, 0, 1, 0, 0, 0, 0.975f);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/blank.png"), this.width / 2 - paintSystem.width * scale - 4,this.width / 2 + paintSystem.width * scale + 4, this.height / 2 - paintSystem.height * scale - 4, this.height / 2 - paintSystem.height * scale, -5, 0, 1, 0, 1, 0, 0, 0, 0.975f);
        innerBlit(pGuiGraphics, ResourceLocation.parse("hexerei:textures/book/blank.png"), this.width / 2 - paintSystem.width * scale - 4,this.width / 2 + paintSystem.width * scale + 4, this.height / 2 + paintSystem.height * scale, this.height / 2 + paintSystem.height * scale + 4, -5, 0, 1, 0, 1, 0, 0, 0, 0.975f);

        List<Component> tooltipLines = new ArrayList<>();
        if (!tooltipLines.isEmpty())
            pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), mouseX, mouseY);


        Lighting.setupFor3DItems();
        pGuiGraphics.pose().popPose();
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
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
        this.ticks++;

//        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }

    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null;
    }

    private static void bufferQuad(ResourceLocation atlasLocation, GuiGraphics guiGraphics, float x, float y, float z, float width, float height, float uOffset, float vOffset, int uWidth, int vHeight, int spriteWidth, int spriteHeight, float alpha) {
        blit(guiGraphics, atlasLocation, x, x + width, y, y + height, z, uWidth, vHeight, uOffset, vOffset, spriteWidth, spriteHeight, alpha);
    }

    private static void blit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, float alpha) {
        innerBlit(guiGraphics, atlasLocation, x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float)textureWidth, (uOffset + (float)uWidth) / (float)textureWidth, (vOffset + 0.0F) / (float)textureHeight, (vOffset + (float)vHeight) / (float)textureHeight, alpha);
    }

    private static void innerBlit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float blitOffset, float minU, float maxU, float minV, float maxV, float alpha) {

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        if (alpha == 1) {
            RenderSystem.setShaderTexture(0, atlasLocation);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        } else {
            VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentEmissive(atlasLocation));
            vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(1, 1, 1, alpha);
        }
    }

    private static void innerBlit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float blitOffset, float minU, float maxU, float minV, float maxV, float red, float green, float blue, float alpha) {

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        if (alpha == 1) {
            RenderSystem.setShaderTexture(0, atlasLocation);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(red, green, blue, alpha);
            bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(red, green, blue, alpha);
            bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(red, green, blue, alpha);
            bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(red, green, blue, alpha);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        } else {
            VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentEmissive(atlasLocation));
            vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(red, green, blue, alpha);
            vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(red, green, blue, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(red, green, blue, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, -1, 0).setColor(red, green, blue, alpha);
        }
    }


    public static void nineSlice(
            ResourceLocation atlasLocation, GuiGraphics guiGraphics,
            float posX, float posY, float posZ, float width, float height,
            int sliceLeftWidth, int sliceRightWidth, int sliceTopHeight, int sliceBottomHeight,
            int spriteWidth, int spriteHeight,
            float uOffset, float vOffset, int uWidth, int vHeight, float alpha
    ) {
        int middleTextureWidth = uWidth - sliceLeftWidth - sliceRightWidth;
        int middleTextureHeight = vHeight - sliceBottomHeight - sliceTopHeight;

        float topV1 = vOffset + sliceTopHeight;
        float leftU1 = uOffset + sliceLeftWidth;
        float rightU0 = uOffset + uWidth - (sliceRightWidth);
        float bottomV0 = vOffset + vHeight - (sliceBottomHeight);

        float middleU0 = leftU1 + 1;
        float middleU1 = rightU0 + 1;

        float middleV0 = vOffset + (sliceTopHeight + 1);
        float middleV1 = vOffset + vHeight - (sliceBottomHeight + 1);

        float leftX = posX + sliceLeftWidth;
        float rightX = posX + width - sliceRightWidth;
        float topY = posY + sliceTopHeight;
        float bottomY = posY + height - sliceBottomHeight;

        float middleWidth = rightX - leftX;
        float middleHeight = bottomY - topY;

        // top left corner
        bufferQuad(atlasLocation, guiGraphics, posX, posY, posZ, sliceLeftWidth, sliceTopHeight, uOffset, vOffset, sliceLeftWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);

//        // top right corner
        bufferQuad(atlasLocation, guiGraphics, rightX, posY, posZ, sliceRightWidth, sliceTopHeight, rightU0, vOffset, sliceRightWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);

//        // bottom left corner
        bufferQuad(atlasLocation, guiGraphics, posX, bottomY, posZ, sliceLeftWidth, sliceBottomHeight, uOffset, bottomV0, sliceRightWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);
//
//        // bottom right corner
        bufferQuad(atlasLocation, guiGraphics, rightX, bottomY, posZ, sliceRightWidth, sliceBottomHeight, rightU0, bottomV0, sliceRightWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);

        // top
        bufferQuad(atlasLocation, guiGraphics, leftX, posY, posZ, middleWidth, sliceTopHeight, middleU0, vOffset, middleTextureWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);

//        // bottom
        bufferQuad(atlasLocation, guiGraphics, leftX, bottomY, posZ, middleWidth, sliceBottomHeight, middleU0, bottomV0, middleTextureWidth, sliceTopHeight, spriteWidth, spriteHeight, alpha);
//
//        // left
        bufferQuad(atlasLocation, guiGraphics, posX, topY, posZ, sliceLeftWidth, middleHeight, uOffset, middleV0, sliceLeftWidth, middleTextureHeight, spriteWidth, spriteHeight, alpha);
//
//        // right
        bufferQuad(atlasLocation, guiGraphics, rightX, topY, posZ, sliceRightWidth, middleHeight, rightU0, middleV0, sliceLeftWidth, middleTextureHeight, spriteWidth, spriteHeight, alpha);
//
//        // middle
        bufferQuad(atlasLocation, guiGraphics, leftX, topY, posZ, middleWidth, middleHeight, middleU0, middleV0 + 1, middleTextureWidth, middleTextureHeight, spriteWidth, spriteHeight, alpha);

        guiGraphics.bufferSource().endBatch();
//      if (tiled) {
//
//            int spriteMiddleWidth = spriteWidth - sliceLeftWidth - sliceRightWidth;
//            int spriteMiddleHeight = spriteHeight - sliceTopHeight - sliceBottomHeight;
//
//            int cols = Mth.ceil(middleWidth / (float) spriteMiddleWidth);
//            int rows = Mth.ceil(middleHeight / (float) spriteMiddleHeight);
//
//            for (int x = 0; x < cols; x++) {
//                for (int y = 0; y < rows; y++) {
//                    float mX = leftX + x * spriteMiddleWidth;
//                    float mY = topY + y * spriteMiddleHeight;
//                    bufferQuadBounded(vertexConsumer, poseStack, mX, mY, posZ, spriteMiddleWidth, spriteMiddleHeight, middleU0, middleU1, middleV0, middleV1, leftX, leftX + middleWidth, topY, topY + middleHeight);
//                }
//            }
//
//        } else {
//            throw new UnsupportedOperationException("Unsupported nine-slice draw mode: " + drawMode.toString());
//        }
    }
//
//    private static void bufferQuadBounded(VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, float width, float height, float minU, float maxU, float minV, float maxV, float xMin, float xMax, float yMin, float yMax) {
//
//        // Passing x as y and y as x is intentional
//        //noinspection SuspiciousNameCombination
//        if (Mth.equal(xMin, xMax) || Mth.equal(yMin, yMax)) {
//            return;
//        }
//
//        // Intersection test
//        if (x > xMax
//                || x + width < xMin
//                || y > yMax
//                || y + height < yMin) {
//            return;
//        }
//
//        final float du = maxU - minU;
//        final float dv = maxV - minV;
//
//        final float x0 = Math.max(x, xMin);
//        final float x1 = Math.min(x + width, xMax);
//        final float y0 = Math.max(y, yMin);
//        final float y1 = Math.min(y + height, yMax);
//
//        final float cminU = ((x0 - x) / width) * du + minU;
//        final float cmaxU = cminU + ((x1 - x0) / width) * du;
//        final float cminV = ((y0 - y) / height) * dv + minV;
//        final float cmaxV = cminV + ((y1 - y0) / height) * dv;
//
//        Matrix4f matrix = poseStack.last().pose();
//        vertexConsumer.vertex(matrix, x0, y1, z).uv(cminU, cmaxV).endVertex();
//        vertexConsumer.vertex(matrix, x1, y1, z).uv(cmaxU, cmaxV).endVertex();
//        vertexConsumer.vertex(matrix, x1, y0, z).uv(cmaxU, cminV).endVertex();
//        vertexConsumer.vertex(matrix, x0, y0, z).uv(cminU, cminV).endVertex();
//    }

}