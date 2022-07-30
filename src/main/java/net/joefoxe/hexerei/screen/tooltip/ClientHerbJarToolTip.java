package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.items.JarHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHerbJarToolTip implements HexereiBookTooltip {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/gui/herb_jar_tooltip_inventory.png");
    private final JarHandler items;
    public int width;
    public Font font = Minecraft.getInstance().font;

    public MutableComponent shift_down = Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    public MutableComponent shift_up = Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));

    public ClientHerbJarToolTip(HerbJarItem.HerbJarToolTip tooltip) {
        this.items = tooltip.jarHandler();
    }

    public int getHeight() {
        return this.gridSizeY() * 20 + 2 + 4 + 10 + getHeightOffset();
    }

    public int getHeightOffset() {
        return ((font.lineHeight + 1) * (!Screen.hasShiftDown() ? 1 : items.isEmpty() ? 1 : 1));
    }

    public int getWidth(Font font) {


        return this.gridSizeX() * 18 + 2 + 10;
    }

    public void renderImage(Font p_194042_, int xIn, int yIn, PoseStack matrixStack, ItemRenderer p_194046_, int z) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();

        this.drawBorder(xIn, yIn, i, j, matrixStack, z);
        this.renderSlot(xIn + 1 + 5, yIn + 1 + 5 + getHeightOffset(), 0, p_194042_, matrixStack, p_194046_, z);

    }

    public void renderImage(Font p_194042_, MultiBufferSource bufferSource, int xIn, int yIn, PoseStack matrixStack, ItemRenderer p_194046_, int z, int overlay, int light) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();


        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));

        this.drawBorder(buffer, xIn, yIn, i, j, matrixStack, z, overlay, light);
        this.renderSlot(bufferSource, buffer, xIn + 1 + 5, yIn + 1 + 5 + getHeightOffset(), 0, p_194042_, matrixStack, p_194046_, z, overlay, light);

        this.renderSlotItemDecorations(bufferSource, buffer, xIn + 1 + 5, yIn + getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
        this.renderSlotItemCount(bufferSource, buffer, xIn + 1 + 5, yIn + getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
        this.renderSlotItem(bufferSource, buffer, xIn + 1 + 5, yIn + getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, MultiBufferSource.BufferSource buffer) {
        int k = 0;
        if(Screen.hasShiftDown())
            drawInternal(shift_down.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, true, lastpose, buffer, false, 0, 15728880);
        else
            drawInternal(shift_up.getVisualOrderText(), (float) (mouseX), (float) (mouseY), 16777215, true, lastpose, buffer, false, 0, 15728880);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, MultiBufferSource.BufferSource buffer, int overlay, int light) {
        if(Screen.hasShiftDown())
            drawInternal(shift_down.getVisualOrderText(), 0, 0, 16777215, false, lastpose, buffer, false, 0, light);
        else
            drawInternal(shift_up.getVisualOrderText(), 0, 0, 16777215, false, lastpose, buffer, false, 0, light);

    }

//    private void renderSlot(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, PoseStack p_194032_, ItemRenderer p_194033_, int z) {
//        ItemStack itemstack = this.items.getStackInSlot(slot);
//        if(itemstack.isEmpty())
//            this.blit(p_194032_, p_194027_, p_194028_, z, ClientHerbJarToolTip.Texture.BLOCKED_SLOT);
//        else
//            this.blit(p_194032_, p_194027_, p_194028_, z, ClientHerbJarToolTip.Texture.SLOT);
//        if(isGui){
//            p_194033_.renderAndDecorateItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
//            renderGuiItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1, (String) null);
//        }
//    }


    private void renderSlot(int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z) {
        ItemStack itemstack = this.items.getStackInSlot(slot);

        if(itemstack.isEmpty())
            this.blit(matrixStack, xIn, yIn, z, ClientHerbJarToolTip.Texture.BLOCKED_SLOT);
        else
            this.blit(matrixStack, xIn, yIn, z, ClientHerbJarToolTip.Texture.SLOT);

        p_194033_.renderAndDecorateItem(itemstack, xIn + 1, yIn + 1, slot);
        p_194033_.renderGuiItemDecorations(p_194031_, itemstack, xIn + 1, yIn + 1);

    }

    private void renderSlot(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.items.getStackInSlot(slot);

        matrixStack.pushPose();
        matrixStack.scale(1,1,0.0001f);
        if(itemstack.isEmpty())
            this.blit(matrixStack, buffer, xIn, yIn, 0, ClientHerbJarToolTip.Texture.BLOCKED_SLOT, overlay, light);
        else
            this.blit(matrixStack, buffer, xIn, yIn, 0, ClientHerbJarToolTip.Texture.SLOT, overlay, light);
        matrixStack.popPose();

        RenderSystem.enableDepthTest();
    }


    private void renderSlotItem(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.items.getStackInSlot(slot);

        PageDrawing.renderGuiItem(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
    }



    private void renderSlotItemCount(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.items.getStackInSlot(slot);

        PageDrawing.renderGuiItemCount(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
    }


    private void renderSlotItemDecorations(MultiBufferSource bufferSource, VertexConsumer buffer, int xIn, int yIn, int slot, Font p_194031_, PoseStack matrixStack, ItemRenderer p_194033_, int z, int overlay, int light) {
        ItemStack itemstack = this.items.getStackInSlot(slot);

        PageDrawing.renderGuiItemDecorations(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);

        RenderSystem.enableDepthTest();
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
//        p_92868_ = Minecraft.getInstance().font.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, matrix4f, p_92873_, p_92874_, p_92875_, light);
        return (int)p_92868_ + (p_92871_ ? 1 : 0);
    }

//
//    private int drawInternal(String p_92909_, float p_92910_, float p_92911_, int p_92912_, boolean p_92913_, Matrix4f p_92914_, MultiBufferSource p_92915_, boolean p_92916_, int p_92917_, int p_92918_, boolean p_92919_) {
//        if (p_92919_) {
//            p_92909_ = Minecraft.getInstance().font.bidirectionalShaping(p_92909_);
//        }
//
//        p_92912_ = adjustColor(p_92912_);
//        Matrix4f matrix4f = p_92914_.copy();
//        if (p_92913_) {
//            matrix4f.translate(new Vector3f(3/5f, 0, 0));
//            Minecraft.getInstance().font.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
//            matrix4f.translate(new Vector3f(-6/5f, 0, 0));
//            Minecraft.getInstance().font.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
//            matrix4f.translate(new Vector3f(3/5f, 3/5f, 0));
//            Minecraft.getInstance().font.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
//            matrix4f.translate(new Vector3f(0, -6/5f, 0));
//            Minecraft.getInstance().font.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
//            matrix4f.translate(new Vector3f(1, 1.75f, 1));
//        }
//
//        p_92910_ = Minecraft.getInstance().font.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
//        return (int)p_92910_ + (p_92913_ ? 1 : 0);
//    }
//
    private void drawBorder(int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int p_194025_) {
        this.blit(p_194024_, p_194020_ + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_TOP);
        this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_TOP);

        for(int i = 0; i < p_194022_; ++i) {
            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_HORIZONTAL_TOP);
            this.blit(p_194024_, p_194020_ + 1 + i * 18 + 5, p_194021_ + p_194023_ * 20 - 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for(int j = 0; j < p_194023_; ++j) {
            this.blit(p_194024_, p_194020_ + 5, p_194021_ + j * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_VERTICAL);
            this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + j * 20 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_VERTICAL);
        }

        this.blit(p_194024_, p_194020_ + 5, p_194021_ + p_194023_ * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_BOTTOM);
        this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + p_194023_ * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_BOTTOM);

        this.blit(p_194024_, p_194020_, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_VERTICAL);
        this.blit(p_194024_, p_194020_ + 25, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_VERTICAL);
        this.blit(p_194024_, p_194020_ + 5, p_194021_ + getHeightOffset(), p_194025_, Texture.THICK_BORDER_HORIZONTAL);
        this.blit(p_194024_, p_194020_ + 5, p_194021_ + 25 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_HORIZONTAL);


        this.blit(p_194024_, p_194020_, p_194021_ + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT);
        this.blit(p_194024_, p_194020_ + (18) + 7, p_194021_ + (18) + 7 + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT);
        this.blit(p_194024_, p_194020_, p_194021_ + (18) + 7 + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
        this.blit(p_194024_, p_194020_ + (18) + 7, p_194021_ + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT);

    }

    private void drawBorder(VertexConsumer buffer, int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int p_194025_, int overlay, int light) {
        this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_TOP, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_TOP, overlay, light);

        for(int i = 0; i < p_194022_; ++i) {
            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.BORDER_HORIZONTAL_TOP, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + p_194023_ * 20 - 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_HORIZONTAL_BOTTOM, overlay, light);
        }

        for(int j = 0; j < p_194023_; ++j) {
            this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + j * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_VERTICAL, overlay, light);
            this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + j * 20 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_VERTICAL, overlay, light);
        }

        this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + p_194023_ * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_BOTTOM, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + p_194023_ * 18 + 1 + 5 + getHeightOffset(), p_194025_, Texture.BORDER_CORNER_BOTTOM, overlay, light);

        this.blit(p_194024_, buffer, p_194020_, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_VERTICAL, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + 25, p_194021_ + 5 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_VERTICAL, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + getHeightOffset(), p_194025_, Texture.THICK_BORDER_HORIZONTAL, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 25 + getHeightOffset(), p_194025_, Texture.THICK_BORDER_HORIZONTAL, overlay, light);


        this.blit(p_194024_, buffer, p_194020_, p_194021_ + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + (18) + 7, p_194021_ + (18) + 7 + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_, p_194021_ + (18) + 7 + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT, overlay, light);
        this.blit(p_194024_, buffer, p_194020_ + (18) + 7, p_194021_ + getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT, overlay, light);

    }

    private void blit(PoseStack p_194036_, int p_194037_, int p_194038_, int z, Texture p_194040_) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(p_194036_, p_194037_, p_194038_, z == -420 ? 1 : z, (float)p_194040_.x, (float)p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
    }

    private void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, ClientHerbJarToolTip.Texture texture, int overlay, int light) {
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
//
//    public void renderGuiItemDecorations(Font p_115175_, ItemStack p_115176_, int p_115177_, int p_115178_, @Nullable String p_115179_) {
//        if (!p_115176_.isEmpty()) {
//            PoseStack posestack = new PoseStack();
//            if (p_115176_.getCount() != 1 || p_115179_ != null) {
//                String s = p_115179_ == null ? String.valueOf(p_115176_.getCount()) : p_115179_;
//                posestack.translate(0.0D, 0.0D, (double)(Minecraft.getInstance().getItemRenderer().blitOffset + 200.0F));
//                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//                drawInternal(s, (float)(p_115177_ + 19 - 2 - p_115175_.width(s)), (float)(p_115178_ + 6 + 3), 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880, Minecraft.getInstance().font.isBidirectional());
//
//                multibuffersource$buffersource.endBatch();
//            }
//
//            if (p_115176_.isBarVisible()) {
//                RenderSystem.disableDepthTest();
//                RenderSystem.disableTexture();
//                RenderSystem.disableBlend();
//                Tesselator tesselator = Tesselator.getInstance();
//                BufferBuilder bufferbuilder = tesselator.getBuilder();
//                int i = p_115176_.getBarWidth();
//                int j = p_115176_.getBarColor();
//                this.fillRect(bufferbuilder, p_115177_ + 2, p_115178_ + 13, 13, 2, 0, 0, 0, 255);
//                this.fillRect(bufferbuilder, p_115177_ + 2, p_115178_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
//                RenderSystem.enableBlend();
//                RenderSystem.enableTexture();
//                RenderSystem.enableDepthTest();
//            }
//
//            LocalPlayer localplayer = Hexerei.proxy.getPlayer();
//            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(p_115176_.getItem(), Minecraft.getInstance().getFrameTime());
//            if (f > 0.0F) {
//                RenderSystem.disableDepthTest();
//                RenderSystem.disableTexture();
//                RenderSystem.enableBlend();
//                RenderSystem.defaultBlendFunc();
//                Tesselator tesselator1 = Tesselator.getInstance();
//                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
//                this.fillRect(bufferbuilder1, p_115177_, p_115178_ + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
//                RenderSystem.enableTexture();
//                RenderSystem.enableDepthTest();
//            }
//
//        }
//    }

    private void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        p_115153_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.end();
        BufferUploader.end(p_115153_);
    }

    private int gridSizeX() {
        return 1;
    }

    private int gridSizeY() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        SLOT(0, 0, 18, 18),
        BLOCKED_SLOT(0, 40, 18, 18),
        BORDER_VERTICAL(0, 18, 1, 18),
        BORDER_HORIZONTAL_TOP(0, 18, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 58, 18, 1),
        BORDER_CORNER_TOP(0, 18, 1, 1),
        BORDER_CORNER_BOTTOM(0, 58, 1, 1),
        THICK_BORDER_CORNER_TOP_LEFT(0, 60, 5, 5),
        THICK_BORDER_CORNER_TOP_RIGHT(5, 60, 5, 5),
        THICK_BORDER_CORNER_BOTTOM_LEFT(0, 65, 5, 5),
        THICK_BORDER_CORNER_BOTTOM_RIGHT(5, 65, 5, 5),
        THICK_BORDER_VERTICAL(0, 75, 5, 20),
        THICK_BORDER_HORIZONTAL(0, 70, 20, 5);

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