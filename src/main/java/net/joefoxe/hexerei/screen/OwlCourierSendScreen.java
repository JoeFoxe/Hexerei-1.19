package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.SendOwlCourierPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import org.joml.Matrix4f;

import java.util.*;

public class OwlCourierSendScreen extends Screen {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/owl_courier_delivery_gui.png");

    public final OwlEntity owl;
    private List<ListButton> listButtons = new ArrayList<>();
    private ListButton sendButton;

    int ticks = 0;
    float scroll = 0f;
    float scrollOld = 0f;
    float scrollTarget = 0f;
    boolean scrollClicked = false;
    double scrollClickedPos = 0;
    int img_width = 124;
    int img_height = 164;
    int left;
    int top;
    int button_height = 16;
    int button_space = 3;
    int button_selected = 0;
    ScissorArea scissorArea;
    InteractionHand hand;

    public OwlCourierSendScreen(OwlEntity owl, InteractionHand hand, int selected) {
        super(Component.translatable("hexerei.owl_message.destination"));
        this.minecraft = Minecraft.getInstance();
        this.owl = owl;
        this.hand = hand;
        if (minecraft.player != null && selected != minecraft.player.getInventory().selected)
            onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (OwlCourierSendScreen.this.listButtons.size() > 6)
            this.scrollTarget = (float)(this.scrollTarget + ((float)(this.button_height + button_space) + ((this.button_height + button_space) * Mth.abs(this.scrollTarget - this.scroll))) / ((OwlCourierSendScreen.this.listButtons.size()) * OwlCourierSendScreen.this.button_height - scissorArea.height) * -scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pMouseX >= this.left + 250 - 149 && pMouseX < this.left + 250 - 149 + 10 && pMouseY >= this.top + 21 + this.scroll * 101 && pMouseY < this.top + 21 + 5 + this.scroll * 101) {
            this.scrollClicked = true;
            this.scrollClickedPos = pMouseY - (double)(this.top + 21 + (this.scroll * 101));
            return true;
        } else if (pMouseX >= this.left + 250 - 149 && pMouseX < this.left + 250 - 149 + 8 && pMouseY >= this.top + 21 && pMouseY < this.top + 21 + 106) {

            this.scroll = (float)(pMouseY - (this.top + 23)) / 101f;
            this.scrollClicked = true;
            this.scrollClickedPos = pMouseY - (double)(this.top + 21 + (this.scroll * 101));
        }
        for (ListButton button : listButtons) {
            if (button.isHovered && button.mouseClicked(pMouseX, pMouseY, pButton))
                return true;
        }
        if (sendButton != null)
            if (sendButton.isHovered && sendButton.mouseClicked(pMouseX, pMouseY, pButton))
                return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.scrollClicked = false;
        for (ListButton button : listButtons) {
            if (button.isHovered && button.mouseReleased(pMouseX, pMouseY, pButton))
                return true;
        }

        if (sendButton != null)
            if (sendButton.isHovered && sendButton.mouseReleased(pMouseX, pMouseY, pButton))
                return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void init() {

        this.left = this.width / 2 - this.img_width / 2;
        this.top = this.height / 2 - this.img_height / 2;

        this.scissorArea = new ScissorArea(this.left + 163 - 149, this.top + 21, 94, 106);

        listButtons.clear();
        int i = 0;

        int listSize = ClientOwlCourierDepotData.getDepots().size();

        if (minecraft != null && minecraft.player != null){

            ClientPacketListener handler = minecraft.player.connection;

            listSize += handler.getOnlinePlayers().size();

            for (PlayerInfo playerInfo : handler.getOnlinePlayers()) {
                int compare = minecraft.player.getUUID().compareTo(playerInfo.getProfile().getId());

                if (compare == 0)
                    continue;


                listButtons.add(new ListButton(this.left + 163 - 149, this.top + 21 + (button_height + button_space) * i, listSize < 7 ? 94 : 88, button_height, (button) -> {


                    for (ListButton lb : listButtons) {
                        lb.isSelected = false;
                    }
                    button.isSelected = true;

                    this.button_selected = button.index;


                }, (button) -> {

                    this.onDone();
                    HexereiPacketHandler.sendToServer(new SendOwlCourierPacket(this.owl, playerInfo.getProfile().getId(), this.hand));

//                OwlEntity.MessageText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).resultOrPartial(LOGGER::error).ifPresent((tag) -> {
//                });


                }, this.scissorArea, playerInfo.getProfile().getName(), true, false, i));
                i++;
            }
        }

        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : ClientOwlCourierDepotData.getDepots().entrySet()) {

            listButtons.add(new ListButton(this.left + 164 - 149, this.top + 21 + (button_height + button_space) * i, listSize < 7 ? 94 : 88, button_height, (button) -> {

                for (ListButton lb : listButtons) {
                    lb.isSelected = false;
                }
                button.isSelected = true;

                this.button_selected = button.index;

            }, (button) -> {

                this.onDone();
                HexereiPacketHandler.sendToServer(new SendOwlCourierPacket(this.owl, entry.getKey(), this.hand));

//                OwlEntity.MessageText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).resultOrPartial(LOGGER::error).ifPresent((tag) -> {
//                });

            }, this.scissorArea, entry.getValue().name, false, entry.getValue().isFull(), i));
            i++;
        }

        if (listButtons.isEmpty()) {
            minecraft.player.sendSystemMessage(Component.translatable("screen.hexerei.owl_send_screen.no_destinations"));
            onDone();
        }

        this.sendButton = new ListButton(this.left + 186 - 149, this.top + 133, 50, button_height, (button) -> {

            listButtons.get(this.button_selected).onComplete.onPress(listButtons.get(this.button_selected));

        }, (button) -> {}, new ScissorArea(this.left + 186 - 149, this.top + 133, 50, button_height), "Send", false, false, i) {
            @Override
            public int getY(float partialTicks) {
                return this.y;
            }

            @Override
            public void tooltip(List<Component> list) {
                if (isHovered()) {
                    if (!isDisabled())
                        list.add(Component.translatable("screen.hexerei.owl_send_screen.send_to", listButtons.get(button_selected).name).withStyle(Style.EMPTY.withColor(0xAAAAAA)));
                }
            }

            @Override
            public boolean isDisabled() {
                return listButtons.get(button_selected).isDisabled;
            }
        };



    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 5);
        if (OwlCourierSendScreen.this.listButtons.size() > 6) {
            if (this.scrollClicked) {
                this.scroll = Mth.clamp((float) (pMouseY - this.top - 21 - this.scrollClickedPos) / 101f, 0, 1);
                this.scrollTarget = this.scroll;
                this.scrollOld = this.scroll;
            }
        } else {
            this.scroll = 0;
        }
        float scrollLerp = Mth.lerp(pPartialTick, this.scrollOld, this.scroll);

        Lighting.setupForFlatItems();
//        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 4, 0x333333);

        for (ListButton button : listButtons)
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (sendButton != null)
            sendButton.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);

        pGuiGraphics.blit(GUI, left, top, 0, 0, img_width, img_height);

        if (OwlCourierSendScreen.this.listButtons.size() > 6) {
            pGuiGraphics.blit(GUI, left + 252 - 149, top + 21, 127, 177, 6, 53);
            pGuiGraphics.blit(GUI, left + 252 - 149, top + 21 + 53, 134, 177, 6, 53);
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 5.0F);
            pGuiGraphics.blit(GUI, left + 250 - 149, top + 21 + (int)(101 * scrollLerp), 127, 171, 10, 5);
            pGuiGraphics.pose().popPose();
        }

        List<Component> tooltipLines = new ArrayList<>();

        for (ListButton button : listButtons) {
            button.tooltip(tooltipLines);
        }

        if (sendButton != null)
            sendButton.tooltip(tooltipLines);

        if (!tooltipLines.isEmpty())
            pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), pMouseX, pMouseY);


        Lighting.setupFor3DItems();
        pGuiGraphics.pose().popPose();
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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
        this.scrollOld = this.scroll;

        if (this.scroll == 1 && this.scrollTarget > 1)
            this.scrollTarget = 1;
        if (this.scroll == 0 && this.scrollTarget < 0)
            this.scrollTarget = 0;

        float scrollDist = (OwlCourierSendScreen.this.listButtons.size() * (OwlCourierSendScreen.this.button_height + button_space) - scissorArea.height);
        if (OwlCourierSendScreen.this.listButtons.size() > 0) {
            float dist = Mth.abs((scrollDist * this.scrollTarget) - (scrollDist * this.scroll));
            float scale = dist / scrollDist;
            float speed = scale / 10f;
            this.scroll = Mth.clamp(HexereiUtil.moveTo(this.scroll, this.scrollTarget, Math.max(speed, 0.002f)), 0, 1);

        }
        else {
            this.scrollTarget = 0;
            this.scroll = 0;
        }

//        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }

    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && this.owl != null && this.owl.distanceTo(this.minecraft.player) < 8;
    }

//    private void setMessage(String p_277913_) {
//        this.messages[this.line] = p_277913_;
//        this.text = this.text.setMessage(this.line, Component.literal(p_277913_));
//    }


    private class ScissorArea {
        protected int width;
        protected int height;
        private int x;
        private int y;

        public ScissorArea(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }


    private class ListButton {
        protected int width;
        protected int height;
        private int x;
        public int y;
        private boolean isHovered;
        private boolean isDisabled;
        private boolean isSelected;
        private OnPress onPress;
        private OnPress onComplete;
        private ScissorArea scissorArea;

        private String name;
        private boolean isPlayerButton;
        private int index;

        public ListButton(int x, int y, int width, int height, OnPress onPress, OnPress onComplete, ScissorArea scissorArea, String name, boolean isPlayerButton, boolean isDisabled, int index) {
            this.isHovered = false;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.onPress = onPress;
            this.onComplete = onComplete;
            this.scissorArea = scissorArea;
            this.name = name;
            this.isPlayerButton = isPlayerButton;
            this.isDisabled = isDisabled;
            this.index = index;
            this.isSelected = index == button_selected;

        }

        public boolean isHovered() {
            return isHovered;
        }

        public boolean isDisabled() {
            return isDisabled;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public int getX() {
            return x;
        }

        public int getY(float partialTicks) {
            float scrollLerp = Mth.lerp(partialTicks, scrollOld, scroll);
            return y - (int)(scrollLerp * (listButtons.size() * (button_height + button_space) - scissorArea.height - button_space));
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void tooltip(List<Component> list) {
            if (isHovered()) {
                if (isDisabled())
                    list.add(Component.translatable("screen.hexerei.owl_send_screen.depot_too_full").withStyle(Style.EMPTY.withColor(0xFFAAAA)));
                else
                    list.add(Component.translatable((isPlayerButton ? "screen.hexerei.owl_send_screen.to_player" : "screen.hexerei.owl_send_screen.to_depot"), name).withStyle(Style.EMPTY.withColor(0xAAAAAA)));
            }
        }

        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

            if (!(pMouseX >= scissorArea.x && pMouseY >= scissorArea.y && pMouseX < scissorArea.x + scissorArea.width && pMouseY < scissorArea.y + scissorArea.height))
                this.isHovered = false;
            else
                this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY(pPartialTick) && pMouseX < this.getX() + this.width && pMouseY < this.getY(pPartialTick) + this.height;

            float alpha = 1.0f;
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 4.0F);
            pGuiGraphics.enableScissor(scissorArea.x, scissorArea.y, scissorArea.x + scissorArea.width + 1, scissorArea.y + scissorArea.height);
            Minecraft minecraft = Minecraft.getInstance();
            pGuiGraphics.setColor(1.0f, isPlayerButton ? 0.75F : 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int offset = getTextureOffset();
            nineSlice(GUI, pGuiGraphics, this.getX(), this.getY(pPartialTick), 0, this.getWidth(), this.getHeight(), 4, 4, 4, 4, 256, 256, 139 + offset, 165, 9, 9, alpha);

//            pGuiGraphics.blit(GUI, this.getX(), this.getY(pPartialTick), this.getWidth(), this.getHeight(), 3, 3, 74, button_height, 1, this.getTextureY());


            if (this.isDisabled())
                pGuiGraphics.setColor( 0.5f, 0.5F, 0.5F, 0.5F);
            else
                pGuiGraphics.setColor( 1.0f, 1.0F, 1.0F, 1.0F);
            int i = 0xEEEEEE;
            this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(alpha * 255.0F) << 24, pPartialTick);
            pGuiGraphics.setColor( 1.0f, 1.0F, 1.0F, 1.0F);
            pGuiGraphics.disableScissor();
//            pGuiGraphics.enableScissor(scissorArea.x - 4, scissorArea.y, scissorArea.x + scissorArea.width + 9, scissorArea.y + scissorArea.height);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
//            pGuiGraphics.disableScissor();
            pGuiGraphics.pose().popPose();

        }

        public void renderString(GuiGraphics pGuiGraphics, Font pFont, int pColor, float partialTicks) {
            this.renderScrollingString(pGuiGraphics, pFont, 4, pColor, partialTicks);
        }

        private int getTextureOffset() {
            int i = 0;

            if (this.isSelected())
                return i + 10 * 2;
            if (this.isDisabled())
                return i + 10;
            if (this.isHovered())
                return i + 10 * 3;

            return i;
        }

        protected static void renderScrollingString(GuiGraphics pGuiGraphics, Font pFont, Component pText, int pMinX, int pMinY, int pMaxX, int pMaxY, int pColor, float ticks) {
            int i = pFont.width(pText);
            int j = (pMinY + pMaxY - 9) / 2 + 1;
            int k = pMaxX - pMinX;
            if (i > k) {
                int l = i - k;
                double d0 = (double)ticks / 20.0D;
                double d1 = Math.max((double)l * 0.5D, 3.0D);
                double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1) + Math.PI) / 2.0D + 0.5D;
                double d3 = Mth.lerp(d2, 0.0D, (double)l);
                pGuiGraphics.enableScissor(pMinX - 1, pMinY, pMaxX + 1, pMaxY);
                pGuiGraphics.drawString(pFont, pText, pMinX - (int)d3, j, pColor);
                pGuiGraphics.disableScissor();
            } else {
                pGuiGraphics.drawCenteredString(pFont, pText, (pMinX + pMaxX) / 2, j, pColor);
            }
        }

        protected void renderScrollingString(GuiGraphics pGuiGraphics, Font pFont, int pWidth, int pColor, float partialTicks) {
            int i = this.getX() + pWidth;
            int j = this.getX() + this.getWidth() - pWidth;
            renderScrollingString(pGuiGraphics, pFont, Component.literal(this.name), i, this.getY(partialTicks), j, this.getY(partialTicks) + this.getHeight(), pColor, ticks + partialTicks);
        }

        public void onClick(double pMouseX, double pMouseY) {
            onPress.onPress(this);
        }

        public void onRelease(double pMouseX, double pMouseY) {
        }

        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (this.isValidClickButton(pButton)) {
                boolean flag = this.clicked(pMouseX, pMouseY);
                if (flag) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    this.onClick(pMouseX, pMouseY);
                    return true;
                }
            }

            return false;
        }

        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            if (this.isValidClickButton(pButton)) {
                this.onRelease(pMouseX, pMouseY);
                return true;
            } else {
                return false;
            }
        }

        protected boolean isValidClickButton(int pButton) {
            return pButton == 0;
        }

        protected boolean clicked(double pMouseX, double pMouseY) {
            return isHovered() && !isDisabled();
        }

        public interface OnPress {
            void onPress(ListButton pButton);
        }
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
            VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentCull(atlasLocation));
            vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
            vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0).setColor(1, 1, 1, alpha);
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