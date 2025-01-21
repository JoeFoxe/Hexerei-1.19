package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.platform.Lighting;
import com.mojang.logging.LogUtils;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.UpdateOwlCourierDepotNamePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OwlCourierDepotNameScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();

    public final OwlCourierDepotTile depot;
    private String name;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
    private int frame;
    @Nullable
    private TextFieldHelper messageField;

    private final List<String> nameList = List.of(
            "Feathered Express",
            "Hoot Hollow",
            "Wise Wing Depot",
            "Moonbeam Mail",
            "Starfall Station",
            "Enchanted Aerie",
            "Twilight Nest",
            "Whispering Grove",
            "Owlpost Oasis",
            "Nocturnal Nexus",
            "Mystic Messenger",
            "Aurora Aviary",
            "Celestial Couriers",
            "Eclipse Enclave",
            "Kappa Kiosk",
            "MonkaMail",
            "Wingwhisper Wayhouse",
            "Hootsuite Hub",
            "Feathered Flux Depot",
            "Owlsome Outpost",
            "Cosmic Courier",
            "Mystical Mailroom",
            "Nebula Nook",
            "Celestial Cache",
            "Starwhisper Station",
            "Quill and Quirk",
            "Arcane Aviary",
            "Quillcraft Quarters",
            "Hoooooooo Hoooo",
            "Owl Be Back"

    );

    public OwlCourierDepotNameScreen(OwlCourierDepotTile depot, Component titleIn) {
        super(titleIn);

        this.minecraft = Minecraft.getInstance();

        List<String> filtered = nameList.stream().filter((string) -> {
            for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : ClientOwlCourierDepotData.getDepots().entrySet()) {
                if (entry.getValue().name.equals(string))
                    return false;
            }
            return true;
        }).toList();
        this.name = filtered.size() > 0 ? filtered.get(new Random().nextInt(filtered.size())) : "";

        this.frame = 0;
        this.depot = depot;
    }

    @Override
    protected void init() {

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onDone();
        }).bounds(this.width / 2 - 50, this.height / 4 + 144, 100, 20).build());
        this.messageField = new TextFieldHelper(() -> {
            return this.name;
        }, this::setName, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (string) -> {
            return this.minecraft.font.width(string) <= this.getMaxTextLineWidth();
        });

    }   /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pKeyCode the key code of the pressed key.
     * @param pScanCode the scan code of the pressed key.
     * @param pModifiers the keyboard modifiers.
     */
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 265) {
            this.messageField.setCursorToStart();
            return true;
        } else if (pKeyCode != 264 && pKeyCode != 257 && pKeyCode != 335) {
            return this.messageField.keyPressed(pKeyCode) ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
        } else {
            this.messageField.setCursorToEnd();
            return true;
        }
    }   /**
     * Called when a character is typed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pCodePoint the code point of the typed character.
     * @param pModifiers the keyboard modifiers.
     */
    public boolean charTyped(char pCodePoint, int pModifiers) {
        this.messageField.charTyped(pCodePoint);
        return true;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        Lighting.setupForFlatItems();
//        this.renderBackground(pGuiGraphics, pMouseX, pMouseY ,pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        this.renderSign(pGuiGraphics);
        Lighting.setupFor3DItems();
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

//    @Override
//    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        guiGraphics.pose().pushPose();
//        guiGraphics.pose().translate(0.0F, 0.0F, 40.0F);
//        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//        guiGraphics.pose().popPose();
//    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    protected void offsetSign(GuiGraphics pGuiGraphics) {
        pGuiGraphics.pose().translate((float)this.width / 2.0F, 55.0F, 50.0F);
    }

    private void renderSign(GuiGraphics pGuiGraphics) {
        pGuiGraphics.pose().pushPose();
        this.offsetSign(pGuiGraphics);
        this.renderMessageText(pGuiGraphics);
        pGuiGraphics.pose().popPose();
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return (mouseX >= x && mouseX <= x + sizeX) && (mouseY >= y && mouseY <= y + sizeY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    public int getTextLineHeight() {
        return 10;
    }

    public int getMaxTextLineWidth() {
        return 140;
    }

    private void onDone() {


        if (!name.isEmpty()) {
            if (depot.getLevel() != null)
                HexereiPacketHandler.sendToServer(new UpdateOwlCourierDepotNamePacket(GlobalPos.of(depot.getLevel().dimension(), depot.getBlockPos()), name));
        }
//        OwlEntity.MessageText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).resultOrPartial(LOGGER::error).ifPresent((tag) -> {
////            HexereiPacketHandler.sendToServer(new SendOwlMessagePacket(this.owl, (CompoundTag) tag, Minecraft.getInstance().player));
//        });
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderMessageText(GuiGraphics pGuiGraphics) {
        pGuiGraphics.pose().translate(0.0F, 0.0F, 4.0F);
        pGuiGraphics.pose().scale(TEXT_SCALE.x(), TEXT_SCALE.y(), TEXT_SCALE.z());
        boolean flag = this.frame / 6 % 2 == 0;
        int j = this.messageField.getCursorPos();
        int k = this.messageField.getSelectionPos();
        int l = this.getTextLineHeight() / 2;
        int i1 = this.getTextLineHeight() - l;

        String s = this.name;
        if (s != null) {
            if (this.font.isBidirectional()) {
                s = this.font.bidirectionalShaping(s);
            }

            int k1 = -this.font.width(s) / 2;
            pGuiGraphics.drawString(this.font, s, k1, this.getTextLineHeight() - l, 0xAAAAAA, false);
            if (j >= 0 && flag) {
                int l1 = this.font.width(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
                int i2 = l1 - this.font.width(s) / 2;
                if (j >= s.length()) {
                    pGuiGraphics.drawString(this.font, "_", i2, i1, 0xAAAAAA, false);
                }
            }
        }

        if (s != null && j >= 0) {
            int l3 = this.font.width(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
            int i4 = l3 - this.font.width(s) / 2;
            if (flag && j < s.length()) {
                pGuiGraphics.fill(i4, i1 - 1, i4 + 1, i1 + this.getTextLineHeight(), -16777216 | 0xAAAAAA);
            }

            if (k != j) {
                int j4 = Math.min(j, k);
                int j2 = Math.max(j, k);
                int k2 = this.font.width(s.substring(0, j4)) - this.font.width(s) / 2;
                int l2 = this.font.width(s.substring(0, j2)) - this.font.width(s) / 2;
                int i3 = Math.min(k2, l2);
                int j3 = Math.max(k2, l2);
                pGuiGraphics.fill(RenderType.guiTextHighlight(), i3, i1, j3, i1 + this.getTextLineHeight(), -16776961);
            }
        }

    }

    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }

    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && this.depot != null && this.depot.getBlockPos().distToCenterSqr(this.minecraft.player.position()) < 8 * 8;
    }

    private void setName(String str) {
        this.name = str;
    }




}