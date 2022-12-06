package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

@OnlyIn(Dist.CLIENT)
public class ClientBroomToolTip implements HexereiBookTooltip {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/gui/broom_tooltip_inventory.png");
    private final ItemStackHandler handler;
    private final ItemStack self;
    public Font font = Minecraft.getInstance().font;

    public MutableComponent shift_down = Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    public MutableComponent shift_up = Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));

    public ClientBroomToolTip(BroomItem.BroomItemToolTip tooltip) {
        this.handler = tooltip.handler();
        this.self = tooltip.self();
    }

    public int getHeight() {
        return (!Screen.hasShiftDown() ? font.lineHeight /2: !isEmpty() ? this.gridSizeY() * 20 + 2 + 8 : 0) + getHeightOffset();
    }

    public int getHeightOffset() {
        return (int)((font.lineHeight + 1) * (3.6));
    }

    public boolean isEmpty()
    {
        boolean empty = true;
        for(int i = 2; i < handler.getSlots(); i++)
        {
            if(!handler.getStackInSlot(i).isEmpty()) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    public int getWidth(Font font) {


        if(Screen.hasShiftDown()){

            if (isEmpty())
                return 82;
            else
                return this.gridSizeX() * 18 + 2 + 10;
        }else
            return 82;
    }






    public void renderImage(Font p_194042_, int p_194043_, int p_194044_, PoseStack p_194045_, ItemRenderer p_194046_, int z) {
        int k = 0;
        boolean flag = z != -420;

        for (int x = 0; x < this.gridSizeX(); x++) {
            if (k == 3)
                break;
            int j1 = p_194043_ + x * 26 + 1 + 3;
            int k1 = p_194044_ + 1 + 5;
            this.renderSlotTop(j1, k1 + getHeightOffset() - 28, k, flag, p_194042_, p_194045_, p_194046_, z);
            k++;

        }
        if(Screen.hasShiftDown() && !isEmpty()){
            int i = this.gridSizeX();
            int j = this.gridSizeY();

            for (int y = 0; y < this.gridSizeY(); y++) {
                for (int x = 0; x < this.gridSizeX(); x++) {
                    if (k == 30)
                        break;
                    int j1 = p_194043_ + x * 18 + 1 + 5;
                    int k1 = p_194044_ + y * 18 + 1 + 5;
                    this.renderSlot(j1, k1 + getHeightOffset(), k, flag, p_194042_, p_194045_, p_194046_, z);
                    k++;
                }
            }

            if(!handler.getStackInSlot(1).isEmpty())
                this.drawBorder(p_194043_, p_194044_ + getHeightOffset(), i, j, p_194045_, z);
        }
    }


    public void renderImage(Font p_194042_, MultiBufferSource bufferSource, int p_194043_, int p_194044_, PoseStack matrixStack, ItemRenderer p_194046_, int z, int overlay, int light) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));

        int k = 0;


        for (int x = 0; x < this.gridSizeX(); x++) {
            if (k == 3)
                break;
            int j1 = p_194043_ + x * 26 + 1 + 3;
            int k1 = p_194044_ + 1 + 5;
//                this.renderSlotTop(j1, k1 + getHeightOffset() - 28, k, flag, p_194042_, p_194045_, p_194046_, z);
            this.renderSlotTop(bufferSource, buffer, j1, k1 + getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
            k++;

        }
        k = 0;
        for (int x = 0; x < this.gridSizeX(); x++) {
            if (k == 3)
                break;
            int j1 = p_194043_ + x * 26 + 1 + 3;
            int k1 = p_194044_ + 1 + 5;
//                this.renderSlotTop(j1, k1 + getHeightOffset() - 28, k, flag, p_194042_, p_194045_, p_194046_, z);
            this.renderSlotItemDecorations(bufferSource, buffer, j1, k1 + getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
            k++;

        }
        k = 0;
        for (int x = 0; x < this.gridSizeX(); x++) {
            if (k == 3)
                break;
            int j1 = p_194043_ + x * 26 + 1 + 3;
            int k1 = p_194044_ + 1 + 5;
//                this.renderSlotTop(j1, k1 + getHeightOffset() - 28, k, flag, p_194042_, p_194045_, p_194046_, z);
            this.renderSlotItemCount(bufferSource, buffer, j1, k1 + getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
            k++;

        }

        if(Screen.hasShiftDown() && !isEmpty()){
            int i = this.gridSizeX();
            int j = this.gridSizeY();

            for (int y = 0; y < this.gridSizeY(); y++) {
                for (int x = 0; x < this.gridSizeX(); x++) {
                    if (k == 30)
                        break;
                    int j1 = p_194043_ + x * 18 + 1 + 5;
                    int k1 = p_194044_ + y * 18 + 1 + 5;
//                        this.renderSlot(j1, k1 + getHeightOffset(), k, flag, p_194042_, p_194045_, p_194046_, z);
                    this.renderSlot(bufferSource, buffer, j1, k1 + getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                    k++;
                }
            }

            if(!handler.getStackInSlot(1).isEmpty())
                this.drawBorder(buffer, p_194043_, p_194044_ + getHeightOffset(), i, j, matrixStack, z, overlay, light);


            k = 3;
            for (int y = 0; y < this.gridSizeY(); y++) {
                for (int x = 0; x < this.gridSizeX(); x++) {
                    if (k == 30)
                        break;
                    int j1 = p_194043_ + x * 18 + 1 + 5;
                    int k1 = p_194044_ + y * 18 + 1 + 5;
//                        this.renderSlot(j1, k1 + getHeightOffset(), k, flag, p_194042_, p_194045_, p_194046_, z);
                    this.renderSlotItemDecorations(bufferSource, buffer, j1, k1 + getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                    k++;
                }
            }
            k = 3;
            for (int y = 0; y < this.gridSizeY(); y++) {
                for (int x = 0; x < this.gridSizeX(); x++) {
                    if (k == 30)
                        break;
                    int j1 = p_194043_ + x * 18 + 1 + 5;
                    int k1 = p_194044_ + y * 18 + 1 + 5;
//                        this.renderSlot(j1, k1 + getHeightOffset(), k, flag, p_194042_, p_194045_, p_194046_, z);
                    this.renderSlotItemCount(bufferSource, buffer, j1, k1 + getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                    k++;
                }
            }
            k = 3;
            for (int y = 0; y < this.gridSizeY(); y++) {
                for (int x = 0; x < this.gridSizeX(); x++) {
                    if (k == 30)
                        break;
                    int j1 = p_194043_ + x * 18 + 1 + 5;
                    int k1 = p_194044_ + y * 18 + 1 + 5;
//                        this.renderSlot(j1, k1 + getHeightOffset(), k, flag, p_194042_, p_194045_, p_194046_, z);
                    this.renderSlotItem(bufferSource, buffer, j1, k1 + getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                    k++;
                }
            }
        }

        k = 0;
        for (int x = 0; x < this.gridSizeX(); x++) {
            if (k == 3)
                break;
            int j1 = p_194043_ + x * 26 + 1 + 3;
            int k1 = p_194044_ + 1 + 5;
//                this.renderSlotTop(j1, k1 + getHeightOffset() - 28, k, flag, p_194042_, p_194045_, p_194046_, z);
            this.renderSlotItem(bufferSource, buffer, j1, k1 + getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
            k++;

        }
    }






    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, MultiBufferSource.BufferSource buffer) {
        if(Screen.hasShiftDown())
            drawInternal(shift_down.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, true, lastpose, buffer, false, 0, 15728880);
        else
            drawInternal(shift_up.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, true, lastpose, buffer, false, 0, 15728880);

    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, MultiBufferSource.BufferSource buffer, int overlay, int light) {
        if(Screen.hasShiftDown())
            drawInternal(shift_down.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, false, lastpose, buffer, false, 0, light);
        else
            drawInternal(shift_up.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, false, lastpose, buffer, false, 0, light);

    }

    private static int adjustColor(int p_92720_) {
        return (p_92720_ & -67108864) == 0 ? p_92720_ | -16777216 : p_92720_;
    }

    private int drawInternal(FormattedCharSequence p_92867_, float p_92868_, float p_92869_, int p_92870_, boolean p_92871_, Matrix4f p_92872_, MultiBufferSource p_92873_, boolean p_92874_, int p_92875_, int light) {
        p_92870_ = adjustColor(p_92870_);
        Matrix4f matrix4f = p_92872_.copy();
        int guiOffsetX = light == 15728880 ?- 1 : 12;
        int guiOffsetY = light == 15728880 ? -1 : 0;
        if (p_92871_) {
            matrix4f.translate(new Vector3f(3/5f, 0, 0));
            Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, p_92872_, p_92873_, p_92874_, p_92875_, light);
            matrix4f.translate(new Vector3f(-6/5f, 0, 0));
            Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, p_92872_, p_92873_, p_92874_, p_92875_, light);
            matrix4f.translate(new Vector3f(3/5f, 3/5f, 0));
            Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, p_92872_, p_92873_, p_92874_, p_92875_, light);
            matrix4f.translate(new Vector3f(0, -6/5f, 0));
            Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, p_92872_, p_92873_, p_92874_, p_92875_, light);
            matrix4f.translate(new Vector3f(1, 1.75f, 1));

        }
        p_92868_ = Minecraft.getInstance().font.drawInBatch(p_92867_, p_92868_ + guiOffsetX, p_92869_ + guiOffsetY, 16777216, false, matrix4f, p_92873_, p_92874_, p_92875_, light);
//        matrix4f.translate(new Vector3f(0, 0, -0.1f));

//        p_92868_ = Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, matrix4f, p_92873_, p_92874_, p_92875_, light);
        return (int)p_92868_ + (p_92871_ ? 1 : 0);
    }

    private void renderSlot(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, PoseStack p_194032_, ItemRenderer p_194033_, int z) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);
        if(itemstack.isEmpty())
            this.blit(p_194032_, p_194027_, p_194028_, z == -420 ? 1 : z, Texture.BLOCKED_SLOT);
        else
            this.blit(p_194032_, p_194027_, p_194028_, z == -420 ? 1 : z, Texture.SLOT);
        if(isGui){
            p_194033_.renderAndDecorateItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
            p_194033_.renderGuiItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
        }
    }

    private void renderSlotTop(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, PoseStack p_194032_, ItemRenderer p_194033_, int z) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);

        if(slot == 0)
            this.blit(p_194032_, p_194027_ - 4, p_194028_ - 4, z == -420 ? 1 : z, Texture.MISC_SLOT);
        if(slot == 1)
            this.blit(p_194032_, p_194027_ - 4, p_194028_ - 4, z == -420 ? 1 : z, Texture.SATCHEL_SLOT);
        if(slot == 2) {
            this.blit(p_194032_, p_194027_ - 4, p_194028_ - 4, z == -420 ? 1 : z, Texture.BRUSH_SLOT);
            if(!self.getOrCreateTag().contains("floatMode")){
                if(isGui){
                    p_194033_.renderAndDecorateItem(new ItemStack(ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1, slot);
                    p_194033_.renderGuiItemDecorations(p_194031_, new ItemStack(ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1);
                }
            }
        }
        if(isGui){
            p_194033_.renderAndDecorateItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
            p_194033_.renderGuiItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
        }
    }



    private void renderSlot(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);

        matrixStack.pushPose();
        matrixStack.scale(1,1,0.0001f);
        if(itemstack.isEmpty())
            this.blit(matrixStack, buffer, xIn, yIn, 0, Texture.BLOCKED_SLOT, overlay, light);
        else
            this.blit(matrixStack, buffer, xIn, yIn, 0, Texture.SLOT, overlay, light);
        matrixStack.popPose();

        RenderSystem.enableDepthTest();


    }
    private void renderSlotTop(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);

        matrixStack.pushPose();
        matrixStack.scale(1,1,0.0001f);
        if(slot == 0)
            this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, Texture.MISC_SLOT, overlay, light);
        if(slot == 1)
            this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, Texture.SATCHEL_SLOT, overlay, light);
        if(slot == 2) {
            this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, Texture.BRUSH_SLOT, overlay, light);
//            if(!self.getOrCreateTag().contains("floatMode")){
//                if(isGui){
//                    p_194033_.renderAndDecorateItem(new ItemStack(ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1, slot);
//                    p_194033_.renderGuiItemDecorations(p_194031_, new ItemStack(ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1);
//                }
//            }
        }
        matrixStack.popPose();

        RenderSystem.enableDepthTest();


    }


//    private void renderSlot(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
//        ItemStack itemstack = this.handler.getStackInSlot(slot);
//        if(itemstack.isEmpty())
//            this.blit(matrixStack, xIn, yIn, z == -420 ? 1 : z, Texture.BLOCKED_SLOT);
//        else
//            this.blit(matrixStack, xIn, yIn, z == -420 ? 1 : z, Texture.SLOT);
//    }
//
//    private void renderSlotTop(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
//        ItemStack itemstack = this.handler.getStackInSlot(slot);
//
//        if(slot == 0)
//            this.blit(matrixStack, xIn - 4, yIn - 4, z == -420 ? 1 : z, Texture.MISC_SLOT);
//        if(slot == 1)
//            this.blit(matrixStack, xIn - 4, yIn - 4, z == -420 ? 1 : z, Texture.SATCHEL_SLOT);
//        if(slot == 2) {
//            this.blit(matrixStack, xIn - 4, yIn - 4, z == -420 ? 1 : z, Texture.BRUSH_SLOT);
//        }
//    }

    private void renderSlotItem(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);
        if(!self.getOrCreateTag().contains("floatMode") && slot == 2){

            PageDrawing.renderGuiItem(bufferSource, p_194031_, new ItemStack(ModItems.BROOM_BRUSH.get()), matrixStack, xIn, yIn, overlay, light);
        }

        PageDrawing.renderGuiItem(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
    }

    private void renderSlotItemCount(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);

        PageDrawing.renderGuiItemCount(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
    }

    private void renderSlotItemDecorations(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.handler.getStackInSlot(slot);

        PageDrawing.renderGuiItemDecorations(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
    }

    private void drawBorder(int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int z) {
        this.blit(p_194024_, p_194020_ + 5, p_194021_ + 5, z - 1, Texture.BORDER_CORNER_TOP);
        this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5, z - 1, Texture.BORDER_CORNER_TOP);


        this.blit(p_194024_, p_194020_, p_194021_ + 3, z, Texture.THICK_BORDER_VERTICAL);
        this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 3, z, Texture.THICK_BORDER_VERTICAL);

        for(int j = 0; j < p_194023_; ++j) {
            this.blit(p_194024_, p_194020_ + 5, p_194021_ + 5 + j * 18 - 1, z, Texture.BORDER_VERTICAL);
            this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5 + j * 18, z, Texture.BORDER_VERTICAL);

            this.blit(p_194024_, p_194020_, p_194021_ + 6 + j * 18, z, Texture.THICK_BORDER_VERTICAL);
            this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 6 + j * 18, z, Texture.THICK_BORDER_VERTICAL);
        }

        this.blit(p_194024_, p_194020_ + 1 + 4, p_194021_, z, Texture.THICK_BORDER_HORIZONTAL);
        this.blit(p_194024_, p_194020_ + 1 + 4, p_194021_ + 6 + p_194023_ * 18, z, Texture.THICK_BORDER_HORIZONTAL);

        for(int i = 0; i < p_194022_; ++i) {
            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5, z, Texture.BORDER_HORIZONTAL_TOP);
            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + p_194023_ * 18, z, Texture.BORDER_HORIZONTAL_BOTTOM);


            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_, z, Texture.THICK_BORDER_HORIZONTAL);
            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_ + 6 + p_194023_ * 18, z, Texture.THICK_BORDER_HORIZONTAL);

        }

        this.blit(p_194024_, p_194020_, p_194021_, z, Texture.THICK_BORDER_CORNER_TOP_LEFT);
        this.blit(p_194024_, p_194020_ + (18 * 9) + 6, p_194021_ + (18 * this.gridSizeY()) + 6, z, Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT);
        this.blit(p_194024_, p_194020_, p_194021_ + (18 * this.gridSizeY()) + 6, z, Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
        this.blit(p_194024_, p_194020_ + (18 * this.gridSizeX()) + 6, p_194021_, z, Texture.THICK_BORDER_CORNER_TOP_RIGHT);
    }

    private void drawBorder(VertexConsumer buffer, int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int z, int overlay, int light) {
        this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 5, z - 1, Texture.BORDER_CORNER_TOP, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5, z - 1, Texture.BORDER_CORNER_TOP, overlay, light);


        this.blit(p_194024_, buffer, p_194020_, p_194021_ + 3, z, Texture.THICK_BORDER_VERTICAL, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 3, z, Texture.THICK_BORDER_VERTICAL, overlay, light);

        for(int j = 0; j < p_194023_; ++j) {
            this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 5 + j * 18 - 1, z, Texture.BORDER_VERTICAL, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5 + j * 18, z, Texture.BORDER_VERTICAL, overlay, light);

            this.blit(p_194024_, buffer, p_194020_, p_194021_ + 6 + j * 18, z, Texture.THICK_BORDER_VERTICAL, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 6 + j * 18, z, Texture.THICK_BORDER_VERTICAL, overlay, light);
        }

        this.blit(p_194024_, buffer, p_194020_ + 1 + 4, p_194021_, z, Texture.THICK_BORDER_HORIZONTAL, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + 1 + 4, p_194021_ + 6 + p_194023_ * 18, z, Texture.THICK_BORDER_HORIZONTAL, overlay, light);

        for(int i = 0; i < p_194022_; ++i) {
            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5, z, Texture.BORDER_HORIZONTAL_TOP, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + p_194023_ * 18, z, Texture.BORDER_HORIZONTAL_BOTTOM, overlay, light);


            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_, z, Texture.THICK_BORDER_HORIZONTAL, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + 6 + p_194023_ * 18, z, Texture.THICK_BORDER_HORIZONTAL, overlay, light);

        }

        this.blit(p_194024_, buffer, p_194020_, p_194021_, z - 1, Texture.THICK_BORDER_CORNER_TOP_LEFT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + (18 * 9) + 6, p_194021_ + (18 * this.gridSizeY()) + 6, z - 1, Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_, p_194021_ + (18 * this.gridSizeY()) + 6, z - 1, Texture.THICK_BORDER_CORNER_BOTTOM_LEFT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + (18 * this.gridSizeX()) + 6, p_194021_, z - 1, Texture.THICK_BORDER_CORNER_TOP_RIGHT, overlay, light);
    }


    private void blit(PoseStack p_194036_, int p_194037_, int p_194038_, int p_194039_, Texture p_194040_) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(p_194036_, p_194037_, p_194038_, p_194039_, (float)p_194040_.x, (float)p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
    }


    private void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, ClientBroomToolTip.Texture texture, int overlay, int light) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
//        MultiBufferSource.BufferSource buffer =
        blit(poseStack, buffer, xIn, yIn, zIn, (float)texture.x, (float)texture.y, texture.w, texture.h, 128, 128, overlay, light);
    }




    public static void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, float p_93148_, float p_93149_, int p_93150_, int p_93151_, int p_93152_, int p_93153_, int overlay, int light) {
        innerBlit(poseStack, buffer, xIn, xIn + p_93150_, yIn, yIn + p_93151_, zIn, p_93150_, p_93151_, p_93148_, p_93149_, p_93152_, p_93153_, overlay, light);
    }

    private static void innerBlit(PoseStack p_93188_, VertexConsumer buffer, int p_93189_, int p_93190_, int p_93191_, int p_93192_, int p_93193_, int p_93194_, int p_93195_, float p_93196_, float p_93197_, int p_93198_, int p_93199_, int overlay, int light) {
        innerBlit(p_93188_, buffer, p_93189_, p_93190_, p_93191_, p_93192_, p_93193_, (p_93196_ + 0.0F) / (float)p_93198_, (p_93196_ + (float)p_93194_) / (float)p_93198_, (p_93197_ + 0.0F) / (float)p_93199_, (p_93197_ + (float)p_93195_) / (float)p_93199_, overlay, light);
    }

    private static void innerBlit(PoseStack poseStack, VertexConsumer buffer, int p_93114_, int p_93115_, int p_93116_, int p_93117_, int p_93118_, float p_93119_, float p_93120_, float p_93121_, float p_93122_, int overlay, int light) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder $$10 = Tesselator.getInstance().getBuilder();
//        $$10.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        poseStack.pushPose();

        poseStack.translate(0,0, p_93118_);

        buffer.vertex(poseStack.last().pose(), (float)p_93114_, (float)p_93117_, (float)p_93118_).color(255, 255, 255, 255).uv(p_93119_, p_93122_).overlayCoords(overlay).uv2(light).normal(0F, 1F, 0F).endVertex();
        buffer.vertex(poseStack.last().pose(), (float)p_93115_, (float)p_93117_, (float)p_93118_).color(255, 255, 255, 255).uv(p_93120_, p_93122_).overlayCoords(overlay).uv2(light).normal(0F, 1F, 0F).endVertex();
        buffer.vertex(poseStack.last().pose(), (float)p_93115_, (float)p_93116_, (float)p_93118_).color(255, 255, 255, 255).uv(p_93120_, p_93121_).overlayCoords(overlay).uv2(light).normal(0F, 1F, 0F).endVertex();
        buffer.vertex(poseStack.last().pose(), (float)p_93114_, (float)p_93116_, (float)p_93118_).color(255, 255, 255, 255).uv(p_93119_, p_93121_).overlayCoords(overlay).uv2(light).normal(0F, 1F, 0F).endVertex();
//        $$10.end();

        poseStack.popPose();
//        BufferUploader.end($$10);
    }

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {


        ItemStack satchel = handler.getStackInSlot(1);

        if(satchel.is(HexereiTags.Items.SMALL_SATCHELS))
            return 1;
        if(satchel.is(HexereiTags.Items.MEDIUM_SATCHELS))
            return 2;
        if(satchel.is(HexereiTags.Items.LARGE_SATCHELS))
            return 3;


        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        SLOT(0, 0, 18, 18),
        BLOCKED_SLOT(0, 40, 18, 18),
        BORDER_VERTICAL(0, 20, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 58, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1),
        THICK_BORDER_CORNER_TOP_LEFT(0, 60, 5, 5),
        THICK_BORDER_CORNER_TOP_RIGHT(5, 60, 5, 5),
        THICK_BORDER_CORNER_BOTTOM_LEFT(0, 65, 5, 5),
        THICK_BORDER_CORNER_BOTTOM_RIGHT(5, 65, 5, 5),
        THICK_BORDER_VERTICAL(0, 75, 5, 18),
        THICK_BORDER_HORIZONTAL(0, 70, 18, 5),
        MISC_SLOT(30, 0, 26, 26),
        SATCHEL_SLOT(57, 0, 26, 26),
        BRUSH_SLOT(84, 0, 26, 26);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
            this.x = p_169928_;
            this.y = p_169929_;
            this.w = p_169930_;
            this.h = p_169931_;
        }
    }
}