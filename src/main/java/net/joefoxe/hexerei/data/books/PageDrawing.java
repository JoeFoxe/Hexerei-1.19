package net.joefoxe.hexerei.data.books;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.screen.BookOfShadowsScreen;
import net.joefoxe.hexerei.screen.CanvasPaintingCropScreen;
import net.joefoxe.hexerei.screen.tooltip.HexereiBookTooltip;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class PageDrawing {
    public float lineWidth;
    public float lineHeight;
    public BookOfShadowsAltarTile altarTile;
    public ItemStack tooltipStack;
    public List<Component> tooltipText;
    public BookImage slotOverlay;
    public boolean drawTooltip;
    public boolean drawTooltipStack;
    public boolean drawTooltipStackFlag;
    public boolean drawTooltipTextFlag;
    public float drawTooltipScale;
    public float drawTooltipScaleOld;
    public boolean drawTooltipText;
    public boolean drawSlotOverlay;
    public PageOn slotOverlayPageOn;
    public ArrayList<Float> bookmarkHoverAmount = new ArrayList<>(Stream.generate(() -> 0.0f).limit(20).toList());
    public ArrayList<Float> bookmarkHoverAmountOld = new ArrayList<>(Stream.generate(() -> 0.0f).limit(20).toList());
    public ArrayList<Float> bookmarkHoverAmountRender = new ArrayList<>(Stream.generate(() -> 0.0f).limit(20).toList());
    public ArrayList<Integer> bookmarkHovered = new ArrayList<>();
    public static boolean isClicked;
    public static boolean isClickedOld;
    public static ArrayList<ResourceLocation> pageTextureLocs = new ArrayList<>();
    public static ArrayList<ResourceLocation> overlayTextureLocs = new ArrayList<>();
    public static Triple<BookOfShadowsAltarTile, ResourceLocation, BookWritableTextBox> focusedWritableTextBox = null;
    public static Triple<BookOfShadowsAltarTile, ResourceLocation, BookWritableTextBox> focusedWritableTextBoxLast = null;

    public static ItemRenderer itemRenderer;

    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    public static final float CORNERS = (float) MixingCauldron.SHAPE.min(Direction.Axis.X) + 3 / 16f;
    public static final float MIN_Y = 4f / 16f;
    public static final float MAX_Y = 15f/ 16f;

    public PageDrawing(BookOfShadowsAltarTile altarTile) {
        this.lineWidth = 0;
        this.lineHeight = 0;
        this.tooltipStack = ItemStack.EMPTY;
        this.tooltipText = new ArrayList<>();
        this.slotOverlay = new BookImage(0, 0, 1, 0, 0, 20, 20, 20, 20, 1, "hexerei:textures/book/slot_hover.png", new ArrayList<>());
        this.drawTooltipStack = false;
        this.drawTooltipStackFlag = false;
        this.drawTooltipTextFlag = false;
        this.drawTooltipScale = 0;
        this.drawTooltipText = false;
        this.drawSlotOverlay = false;
        this.slotOverlayPageOn = PageOn.LEFT_PAGE;
        itemRenderer = Hexerei.proxy.getLevel() == null ? null : Hexerei.proxy.getLevel().isClientSide ? Minecraft.getInstance().getItemRenderer() : null;
        this.altarTile = altarTile;
    }

    public static void clearFocusedWritableTextBox() {
        if (PageDrawing.focusedWritableTextBox != null) {
            PageDrawing.focusedWritableTextBox.getRight().client.clicked = false;
            PageDrawing.focusedWritableTextBox.getRight().client.pageEdit.setCursorPos(PageDrawing.focusedWritableTextBox.getRight().client.pageEdit.getCursorPos(), false);
            PageDrawing.focusedWritableTextBox.getRight().client.clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
        }
        setFocusedWritableTextBoxNull();
    }

    public static void setFocusedWritableTextBoxNull() {
        setFocusedWritableTextBox(null, null, null);
    }
    public static void setFocusedWritableTextBox(BookOfShadowsAltarTile altarTile, ResourceLocation pageLoc, BookWritableTextBox bookWritableTextBox) {
        if (altarTile == null || bookWritableTextBox == null || pageLoc == null) {
            PageDrawing.focusedWritableTextBoxLast = PageDrawing.focusedWritableTextBox;
            PageDrawing.focusedWritableTextBox = null;
        } else {

            PageDrawing.focusedWritableTextBoxLast = PageDrawing.focusedWritableTextBox;
            PageDrawing.focusedWritableTextBox = new Triple<>() {
                @Override
                public BookOfShadowsAltarTile getLeft() {
                    return altarTile;
                }

                @Override
                public ResourceLocation getMiddle() {
                    return pageLoc;
                }

                @Override
                public BookWritableTextBox getRight() {
                    return bookWritableTextBox;
                }
            };
        }
    }

    public enum DrawingType {
        BOOK(),
        SCREEN(),
        GUI();

        public static DrawingType byId(int id) {
            DrawingType[] type = values();
            return type[id < 0 || id >= type.length ? 0 : id];
        }
    }


    protected static final Quaternionf ITEM_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis((65) * Math.PI / 180, 1, 0, 0);
        quaternion.rotateAxis((float)((50) * Math.PI / 180), 0, 1, 0);


        return quaternion;
    });
    protected static final Quaternionf BLOCK_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis((35) * Math.PI / 180, 1, 0, 0);
        quaternion.rotateAxis((float)((35) * Math.PI / 180), 0, 1, 0);


        return quaternion;
    });
    protected static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis(-45 * Math.PI / 180, 1, 0, 0);
        return quaternion;
    });

    public static ItemStack getTagStack(TagKey<Item> key) {

        float fl = 0;
        if (FMLEnvironment.dist.isClient())
            fl = ClientEvents.getClientTicks();
        return BuiltInRegistries.ITEM.getRandomElementOf(key, RandomSource.create((long) (fl * 1000f))).orElse(Holder.direct(Items.AIR)).value().getDefaultInstance();
    }

    public static Block getTagBlock(TagKey<Block> key) {

        float fl = 0;
        if (FMLEnvironment.dist.isClient())
            fl = ClientEvents.getClientTicks();
        return BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create((long) (fl * 1000f))).orElse(Holder.direct(Blocks.AIR)).value();
    }


    @OnlyIn(Dist.CLIENT)
    public static void renderItem(BookOfShadowsAltarTile altarTile, @NotNull BookItemsAndFluids itemStackElement, PoseStack poseStack, MultiBufferSource buffer, float xIn, float yIn, float zLevel, int combinedLight, int combinedOverlay, PageOn pageOn, DrawingType drawingType) {

        ItemStack itemStack = itemStackElement.item;

        if (itemStackElement.type.equals("tag")) {
            int mod = ((int) ClientEvents.getClientTicks()) % 20;

            if (itemStackElement.item.isEmpty()) {
                itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
            }

            if ((mod == 19 || mod == 18) && itemStackElement.refreshTag) {
                itemStack = getTagStack(itemStackElement.key);
                if (itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                if (itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
                itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);
            }
            if (mod == 1 || mod == 2) {
                itemStackElement.refreshTag = true;
            }
        }

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.021f / 16f);
        poseStack.scale(0.049f, 0.049f, 0.001f);
        poseStack.translate(yIn * 1.259f, -xIn * 1.259f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        poseStack.translate(-4.75f / 8f, -4.5f / 8f, 0);
        poseStack.scale(0.065f, 0.065f, 0.05f);

        renderGuiItemDecorations(buffer, Minecraft.getInstance().font, itemStack, poseStack, 0, 0, combinedOverlay, combinedLight);
        poseStack.translate(0.75f / 8f / 0.065f, 0.5f / 8f / 0.065f, 0);
        poseStack.scale(0.965f, 0.965f, 0.965f);
        renderGuiItemCount(buffer, Minecraft.getInstance().font, itemStack, poseStack, 0, 0, combinedOverlay, combinedLight);
        poseStack.popPose();
        Vector3f[] shaderLightDirections = new Vector3f[2];
        shaderLightDirections[0] = new Vector3f(RenderSystem.shaderLightDirections[0]);
        shaderLightDirections[1] = new Vector3f(RenderSystem.shaderLightDirections[1]);
        int[] originalLightmap = Util.make(() -> {
            int[] vals = new int[12];
            for(int i = 0; i < 12; ++i) {
                vals[i] = RenderSystem.getShaderTexture(i);
            }
            return vals;
        });

        try {
            if (itemRenderer == null)
                itemRenderer = Minecraft.getInstance().getItemRenderer();
            if (itemStackElement.modelCache == null)
                itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemStackElement.modelCache.isGui3d()) {
                poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_3D);
            } else {
                poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
            }
//            Lighting.setupForFlatItems();

            itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, buffer, combinedLight, combinedOverlay, itemStackElement.modelCache);

        } catch (Exception e) {
            // Shrug
        }
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();

        // Restore original lighting settings
        for(int i = 0; i < 12; ++i) {
            RenderSystem.setShaderTexture(i, originalLightmap[i]);
        }
        ShaderInstance shaderinstance = RenderSystem.getShader();
        RenderSystem.setShaderLights(shaderLightDirections[0], shaderLightDirections[1]);
        RenderSystem.setupShaderLights(shaderinstance);

        poseStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    public static void renderBlock(BookOfShadowsAltarTile altarTile, @NotNull BookBlocks blockElement, PoseStack poseStack, MultiBufferSource buffer, float xIn, float yIn, float zLevel, int combinedLight, int combinedOverlay, PageOn pageOn, DrawingType drawingType) {

        BlockState blockState = blockElement.blockState;

        if (blockElement.type.equals("tag")) {
            int mod = ((int) ClientEvents.getClientTicks()) % 60;
            if (blockState.is(Blocks.AIR)) {
                blockState = getTagBlock(blockElement.key).defaultBlockState();
                blockElement.blockState = blockState;
            }

            if ((mod == 59 || mod == 58) && blockElement.refreshTag) {
                blockState = getTagBlock(blockElement.key).defaultBlockState();
                if (blockState.equals(blockElement.blockState))
                    blockState = getTagBlock(blockElement.key).defaultBlockState();
                if (blockState.equals(blockElement.blockState))
                    blockState = getTagBlock(blockElement.key).defaultBlockState();
                blockElement.blockState = blockState;
                blockElement.refreshTag = false;
            }
            if (mod == 1 || mod == 2) {
                blockElement.refreshTag = true;
            }
        }

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        float scale = 0.62f;
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.57f / 16f, -0.021f / 16f);
        poseStack.scale(0.049f * scale, 0.049f * scale, 0.001f);
        poseStack.translate(yIn * 1.259f * (1 / scale), -xIn * 1.259f * (1 / scale), 0);
        poseStack.translate(0.25f, 0.25f, 0.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        poseStack.mulPose(Axis.XP.rotationDegrees(225f - 180f - 15f));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(-0.25f, -0.25f, -0.25f);

        int light = blockState.getLightEmission(altarTile.getLevel(), altarTile.getBlockPos());

        try {
            if (blockState.getBlock() instanceof LiquidBlock liquidBlock) {
//                blockState = liquidBlock.getFluidState(liquidBlock.defaultBlockState()).createLegacyBlock().setValue(LiquidBlock.LEVEL, 7);
                poseStack.last().normal().set(poseStack.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
                renderFluidBlockGUI(poseStack, buffer, new FluidStack(liquidBlock.fluid, 2000), 1, combinedLight, combinedOverlay);
                if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
                    bufferSource.endBatch();
            }else {
                poseStack.last().normal().set(poseStack.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, buffer, combinedLight, combinedOverlay, ModelData.EMPTY, null);
            }
        } catch (Exception e) {
            // Shrug
        }

        poseStack.popPose();

    }
    public static int adjustCombinedLight(int currentCombinedLight, int otherBlockLight) {
//        int currentBlockLight = currentCombinedLight & 0xFFFF; int currentSkyLight = (currentCombinedLight >> 16) & 0xFFFF;
        int currentBlockLight = (currentCombinedLight >> 4) & 0xFFFF;
        int currentSkyLight = (currentCombinedLight >> 20) & 0xFFFF;
        // Use the maximum of the current block light and the other block's light
        int adjustedBlockLight = Math.max(currentBlockLight, otherBlockLight);
        // Recombine the adjusted block light with the current sky light
        return LightTexture.pack(adjustedBlockLight, currentSkyLight);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderFluidBlockGUI(PoseStack poseStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, int combinedLight, int combinedOverlay){
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);

        alpha *= (color >> 24 & 255) / 255f;

        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuadsBlock(poseStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, combinedLight, combinedOverlay);
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderQuadsBlock(Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, int light, int overlay){
        float height = (MIN_Y + (MAX_Y - MIN_Y)) * 0.8f;
        float minU = sprite.getU(CORNERS);
        float maxU = sprite.getU((1 - CORNERS));
        float minV = sprite.getV(CORNERS);
        float maxV = sprite.getV((1 - CORNERS));

        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, CORNERS / 5f).setColor(r, g, b, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r, g, b, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r, g, b, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, CORNERS / 5f).setColor(r, g, b, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);


        float shading = 0.75f;
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);


        shading = 0.45f;
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
    }


    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItemDecorations(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light) {

        if (itemStack.isBarVisible()) {

            poseStack.pushPose();
            int i = itemStack.getBarWidth();
            int j = itemStack.getBarColor();
            poseStack.translate(0, 0, -4.15f);
            fillRect(poseStack, bufferSource, xIn + 2.75f, yIn + 13.75f, 0, 13, 1.5f, 0, 0, 0, 255, overlay, light);
            fillRect(poseStack, bufferSource, xIn + 2.75f, yIn + 13.75f, -0.5f, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255, overlay, light);
            poseStack.popPose();
        }

    }


    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItemCount(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light) {

        if (itemStack.getCount() > 1) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -7f);
            String s = String.valueOf(itemStack.getCount());
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)) + 1f, (yIn + 6 + 3) + 1f, HexereiUtil.getColorValueAlpha(0.245f, 0.245f, 0.245f, 1), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, overlay, light);
//              drawInBatch(pText, float pX,                        float pY,           int pColor,                                                  boolean pDropShadow,   Matrix4f pMatrix,   MultiBufferSource pBuffer, Font.DisplayMode pDisplayMode, int pBackgroundColor, int pPackedLightCoords) {
            poseStack.translate(0, 0, -6f);
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)), (yIn + 6 + 3), 16777215, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, overlay, light);
            multibuffersource$buffersource.endBatch();
            poseStack.popPose();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItem(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light) {


        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        poseStack.scale(16, 16, 1f);
        poseStack.translate(yIn * 1.25f * 2 / 40 + 0.55f, -xIn * 1.25f * 2 / 40 - 0.55f, -2f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        poseStack.mulPose(Axis.YP.rotationDegrees(180));


        try {
            BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemModel.isGui3d()) {
                poseStack.last().normal().set(poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_3D));
            }
            else
                poseStack.last().normal().set(poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT));


            itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, bufferSource, light, overlay, itemModel);
        } catch (Exception e) {
            // Shrug
        }


        poseStack.popPose();
    }


//    @OnlyIn(Dist.CLIENT)
//    private static void fill(RenderType renderType, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zIn, float widthIn, float heightIn, int p_115158_, int p_115159_, int p_115160_, int p_115161_, int overlay, int light) {
//        fill(renderType, poseStack, bufferSource, xIn, yIn, widthIn, heightIn, 1, 1, )
//    }

    @OnlyIn(Dist.CLIENT)
    private static void fill(RenderType renderType, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zIn, float widthIn, float heightIn, int p_115158_, int p_115159_, int p_115160_, int p_115161_, int overlay, int light) {

        poseStack.pushPose();
        PoseStack.Pose normal = poseStack.last();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth = 1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;


        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, -1F, -1F, 0F);
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, -1F, -1F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, -1F, -1F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, -1F, -1F, 0F);
        poseStack.popPose();
    }


    @OnlyIn(Dist.CLIENT)
    private static void fillRect(PoseStack poseStack, MultiBufferSource p_115153_, float xIn, float yIn, float zIn, float widthIn, float heightIn, int p_115158_, int p_115159_, int p_115160_, int p_115161_, int overlay, int light) {

        poseStack.pushPose();
        PoseStack.Pose normal = poseStack.last();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth = 1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;


        VertexConsumer buffer = p_115153_.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/blank.png")));
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        poseStack.popPose();
    }


    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPageUnder(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.translate(0, 1f / 32f, 0);

        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(90f - altarTile.degreesOpenedRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(90f - altarTile.degreesOpenedRender) / 90f) * (-altarTile.pageTwoRotationRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageTwoRotationRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (altarTile.pageOneRotationRender / 16f)));
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        poseStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPage(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {


        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));

        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 33);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(90f - altarTile.degreesOpenedRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(90f - altarTile.degreesOpenedRender) / 90f) * (-altarTile.pageTwoRotationRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageTwoRotationRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (altarTile.pageOneRotationRender / 16f)));
        }
//        poseStack.translate(0,1/64f,0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPageUnder(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((90f - altarTile.degreesOpenedRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((90f - altarTile.degreesOpenedRender) / 90f) * (-altarTile.pageOneRotationRender)));
//            poseStack.mulPose(Axis.ZP.rotationDegrees(((90f - altarTile.degreesOpenedRender) / 90f) * (altarTile.pageTwoRotationRender / 16f)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageOneRotationRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (altarTile.pageTwoRotationRender / 16f)));
        }
//        poseStack.translate(0, 1 / 64f, 0);

    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPage(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((90f - altarTile.degreesOpenedRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((90f - altarTile.degreesOpenedRender) / 90f) * (-altarTile.pageOneRotationRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageOneRotationRender)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (altarTile.pageTwoRotationRender / 16f)));
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        poseStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPagePrevious(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));

        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 33);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(90f - altarTile.degreesOpenedRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageTwoRotationRender / 16f + 180 / 16f)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPagePrevious(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((90f - altarTile.degreesOpenedRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (-altarTile.pageOneRotationRender / 16f + 180 / 16f)));
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        poseStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPagePrevious2(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));

        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 33);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(90f - altarTile.degreesOpenedRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (180 / 16f)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPagePrevious2(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 10)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.translate(0, 1f / 32f, 0);
        if (drawingType == DrawingType.SCREEN) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((90f - altarTile.degreesOpenedRender)));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpenedRender / 1.12f)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpenedRender / 1.12f) / 90f) * (180 / 16f)));
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
        poseStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public void translateToMiddleButton(BookOfShadowsAltarTile altarTile, PoseStack poseStack, DrawingType drawingType, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        poseStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        if (drawingType == DrawingType.BOOK)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        else if (drawingType == DrawingType.GUI)
            poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 - 13)));
        else if (drawingType == DrawingType.SCREEN)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        if (drawingType == DrawingType.GUI && transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.XP.rotationDegrees(-55));
        poseStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(270));
        poseStack.translate(2.95f / 64f, 7.1f / 16f, 11f / 32f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
//        poseStack.translate(0,0,0);
//        poseStack.scale(0.003f,0.003f,0.003f);
//        poseStack.translate(-16, -16, -10);

    }

    public void drawPage(BookPage page, BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, DrawingType drawingType, ItemDisplayContext transformType, float partial) {
        drawPage(page, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferIn, combinedLightIn, combinedOverlayIn, pageOn, drawingType, transformType, -1, partial);
    }

    public void drawPage(BookPage page, BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, DrawingType drawingType, ItemDisplayContext transformType, int pageNum, float partial) {

//        float w = bookImage.width / 330 * bookImage.scale / 0.062f;
//        float h = bookImage.height / 330 * bookImage.scale / 0.062f;
//        float x = bookImage.x - w / 2 + 0.45f;
//        float y = bookImage.y - h / 2 + 0.49f;
        //w 6.55
        //h 9.1

        boolean left = pageOn == PageOn.LEFT_PAGE || pageOn == PageOn.LEFT_PAGE_PREV || pageOn == PageOn.RIGHT_PAGE_UNDER;
//        Random random = new Random((pageNum / 2 + (left ? 0 : 1)) * 15217L);
//        String pageLoc = pageLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageLocs.get(random.nextInt(pageLocs.size())).toString();
//        drawImage(new BookImage(-0.5f - (left ? 0.45f : 0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, left ? 13 : -13, 18, 10.2f, pageLoc, new ArrayList<>()),
//                altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

        BookEntries bookEntries = BookManager.getBookEntries(altarTile.currentBook.getBook());
        if (page != null && bookEntries != null) {

            for (BookPaintElement paintElement : page.paintElements)
                drawPaintElement(paintElement, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, -0.25f, combinedLightIn, combinedOverlayIn, pageOn, -1, drawingType, transformType, partial);

            for (BookWritableTextBox writableTextBox : page.writableTextBoxes)
                drawString(writableTextBox, altarTile, poseStack, bufferIn, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

            for (BookParagraph bookParagraph : page.paragraph)
                drawString(bookParagraph, altarTile, poseStack, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

            int pageOnNum = pageNum + 1 - bookEntries.chapterList.getFirst().endPage;
            //draw page number
            BookParagraph bookParagraph = new BookParagraph(new ArrayList<>(List.of(new BookParagraphElements(left ? 14.3f : 0, 19.25f, 1, 30, "top"))), pageOnNum > 0 ? String.valueOf(pageOnNum) : HexereiUtil.intToRoman(pageNum + 1), "left");
            if (left)
                bookParagraph.paragraphElements.getFirst().x -= Minecraft.getInstance().font.width(bookParagraph.passage) / 8f;

            drawString(bookParagraph, altarTile, poseStack, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

            for (BookItemsAndFluids bookItemStackInSlot : page.itemList) {
                drawItemInSlot(altarTile, bookItemStackInSlot, poseStack, bufferIn, bookItemStackInSlot.x, bookItemStackInSlot.y, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
            }

            for (BookBlocks bookBlocks : page.blockList) {
                drawBlock(altarTile, bookBlocks, poseStack, bufferIn, bookBlocks.x, bookBlocks.y, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
            }

            if (transformType == ItemDisplayContext.NONE && (pageOn == PageOn.LEFT_PAGE || pageOn == PageOn.RIGHT_PAGE)) {
                for (BookItemsAndFluids bookItemStackInSlot : page.itemList) {

                    if (canInteract(pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86f, 0.86f, altarTile, drawingType)) {

                        if (bookItemStackInSlot.item != null) {
                            if (!bookItemStackInSlot.item.isEmpty()) {
                                this.tooltipStack = bookItemStackInSlot.item;
                                this.tooltipText = bookItemStackInSlot.extra_tooltips;
                                this.drawTooltipStack = true;
                            }
                        } else {
                            this.tooltipText = getFluidTooltip(bookItemStackInSlot);
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }
                        this.slotOverlay.x = bookItemStackInSlot.x;
                        this.slotOverlay.y = bookItemStackInSlot.y;
                        ArrayList<BookImageEffect> effects = new ArrayList<>();
                        effects.add(new BookImageEffect("scale", 20, 1.1f));
                        this.slotOverlay.effects = effects;
                        this.slotOverlayPageOn = pageOn;
                        this.drawSlotOverlay = true;
                        break;
                    }

                    if (this.drawTooltipStack)
                        break;
                }
                for (BookBlocks bookBlock : page.blockList) {


                    if (canInteract(pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, bookBlock.x, bookBlock.y, 0.86f, 0.86f, altarTile, drawingType)) {

                        if (!bookBlock.blockState.is(Blocks.AIR)) {
                            List<Component> tooltipList = new ArrayList<>(bookBlock.extra_tooltips);
                            tooltipList.addFirst(bookBlock.blockState.getBlock().getName().withStyle(ChatFormatting.WHITE));
                            this.tooltipText = tooltipList;
                            this.drawTooltipText = true;
                            this.tooltipStack = ItemStack.EMPTY;
                        }
                        this.slotOverlay.x = bookBlock.x;
                        this.slotOverlay.y = bookBlock.y;
                        ArrayList<BookImageEffect> effects = new ArrayList<>();
                        effects.add(new BookImageEffect("scale", 20, 1.1f));
                        this.slotOverlay.effects = effects;
                        this.slotOverlayPageOn = pageOn;
                        this.drawSlotOverlay = true;
                        break;
                    }

                    if (this.drawTooltipText)
                        break;
                }

                for (BookEntity bookEntity : page.entityList) {

                    bookEntity.hoverTickRender = easeInOutElastic(Mth.lerp(partial, bookEntity.hoverTickO, bookEntity.hoverTick));

                    if (bookEntity.entity != null)
                        bookEntity.entity.tickCount = (int) ClientEvents.getClientTicksWithoutPartial();


                    float xIn = bookEntity.x + bookEntity.offset.x + 0.52f;
                    float yIn = bookEntity.y + bookEntity.offset.y;
                    float width = 1.25f + bookEntity.scale / 5f;
                    if (canInteract(pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, xIn - width/2, yIn - width/2, width, width, altarTile, drawingType)) {
                        bookEntity.hovered = true;
                    }

                    if (bookEntity.hoverTickRender > 0) {
                        BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64, 32, 64, 32, 0.75f * bookEntity.hoverTickRender, "hexerei:textures/book/rotate_entity.png", new ArrayList<>());
                        drawImage(bookImage, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

                        float lerpRotate = Mth.lerp(ClientEvents.getPartial(), bookEntity.toRotateO, bookEntity.toRotate);
                        BookImage bookImage2;
                        float v = Math.clamp(lerpRotate / 2000f, -0.8f, 0.8f);
                        float v1 = (float) Math.pow(Math.min(Math.abs(lerpRotate) / 4000f, 0.4f), 2) * 2.25f;
                        if (bookEntity.clicked) {
                            bookImage2 = new BookImage(bookEntity.x - v, bookEntity.y + 0.85f - v1, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTickRender, "hexerei:textures/book/right_click_icon_hover.png", new ArrayList<>());
                        } else {
                            bookImage2 = new BookImage(bookEntity.x - v, bookEntity.y + 0.85f - v1, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTickRender, "hexerei:textures/book/right_click_icon.png", new ArrayList<>());
                        }
                        drawImage(bookImage2, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
                    }

//                    if (pageOn == PageOn.LEFT_PAGE) {
//
//                        float xIn = bookEntity.x + bookEntity.offset.x + 0.52f;
//                        float yIn = bookEntity.y + bookEntity.offset.y;
//                        float width = 1.25f + bookEntity.scale / 5f;
//                        if (canInteract(leftCursorX, leftCursorY, xIn - width/2, yIn - width/2, width, width, altarTile, drawingType)) {
//                            bookEntity.hovered = true;
//                        }
//
//
//                    } else {
//                        MouseHandler handler = Minecraft.getInstance().mouseHandler;
//
//                        float xIn = bookEntity.x + bookEntity.offset.x + 0.52f;
//                        float yIn = bookEntity.y + bookEntity.offset.y;
//                        float width = 1.25f + bookEntity.scale / 5f;
//                        if (canInteract(rightCursorX, rightCursorY, xIn - width/2, yIn - width/2, width, width, altarTile, drawingType)) {
//
//                            bookEntity.hovered = true;
//                        }
//
//                        if (bookEntity.hoverTickRender > 0) {
//
//                            BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64, 32, 64, 32, 0.75f * bookEntity.hoverTickRender, "hexerei:textures/book/rotate_entity.png", new ArrayList<>());
//                            drawImage(bookImage, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
//
//                            if (handler.isRightPressed()) {
//                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTickRender, "hexerei:textures/book/right_click_icon_hover.png", new ArrayList<>());
//                                drawImage(bookImage2, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
//
//                            } else {
//                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTickRender, "hexerei:textures/book/right_click_icon.png", new ArrayList<>());
//                                drawImage(bookImage2, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
//
//                            }
//                        }
//
//                    }

                }


                for (BookNonItemTooltip bookNonItemTooltip : page.nonItemTooltipList) {

                    if (canInteract(leftCursorX, leftCursorY, bookNonItemTooltip.x, bookNonItemTooltip.y, bookNonItemTooltip.width, bookNonItemTooltip.height, altarTile, drawingType)) {

                        this.tooltipText = bookNonItemTooltip.tooltip;
                        this.tooltipStack = ItemStack.EMPTY;
                        this.drawTooltipText = true;
                    }
                    if (this.drawTooltipText)
                        break;
                }
                for (BookWritableTextBox bookWritableTextBox : page.writableTextBoxes) {

                    float xCursor = pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX;
                    float yCursor = pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY;
                    if (canInteract(xCursor, yCursor, bookWritableTextBox.paragraphElement.x + 0.45f, bookWritableTextBox.paragraphElement.y, bookWritableTextBox.paragraphElement.width / 6.15f, bookWritableTextBox.paragraphElement.height / 2.57f, altarTile, drawingType)) {
                        if (focusedWritableTextBox != null && focusedWritableTextBox.getLeft() == altarTile) {
                            if (focusedWritableTextBox.getRight() == bookWritableTextBox) {

                                if (focusedWritableTextBox.getRight().client.clicked) {
                                    BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = focusedWritableTextBox.getRight().client.getDisplayCache(focusedWritableTextBox.getLeft().currentBook);
                                    BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i((int) ((xCursor - bookWritableTextBox.paragraphElement.x - 0.45f) / 5 * 115f), (int) ((yCursor - bookWritableTextBox.paragraphElement.y) / 7.1f * 162f));
                                    if (pos2i.x != focusedWritableTextBox.getRight().client.clickedPos.x || pos2i.y != focusedWritableTextBox.getRight().client.clickedPos.y) {
                                        int i = bookeditscreen$displaycache.getIndexAtPosition(
                                                ClientProxy.font(), pos2i
                                        );
                                        focusedWritableTextBox.getRight().client.pageEdit.setCursorPos(i, true);
                                        focusedWritableTextBox.getRight().client.clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
                                    }
                                }
                            }
                        }
                    }
                }


            }
            for (BookImage bookImage : page.imageList) {
                drawImage(bookImage, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

                if (bookImage.extra_tooltips == null || bookImage.extra_tooltips.isEmpty())
                    continue;

                float w = bookImage.width / 330 * bookImage.scale / 0.062f;
                float h = bookImage.height / 330 * bookImage.scale / 0.062f;
                float x = bookImage.x - w / 2 + 0.45f;
                float y = bookImage.y - h / 2 + 0.49f;
                if (canInteract(pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, x, y, w, h, altarTile, drawingType) && (pageOn == PageOn.LEFT_PAGE || pageOn == PageOn.RIGHT_PAGE)) {
                    this.tooltipText = bookImage.extra_tooltips;
                    this.tooltipStack = ItemStack.EMPTY;
                    this.drawTooltipText = true;
                }


            }


            //drawing slot overlay
            if (this.drawSlotOverlay)
                drawImage(this.slotOverlay, altarTile, pageOn == PageOn.LEFT_PAGE ? leftCursorX : rightCursorX, pageOn == PageOn.LEFT_PAGE ? leftCursorY : rightCursorY, poseStack, bufferIn, 0, combinedLightIn, combinedOverlayIn, this.slotOverlayPageOn, drawingType);

            for (BookEntity bookEntity : page.entityList) {
                bookEntity.markedForUpdate = true;
                if (bookEntity.entity instanceof TamableAnimal tamable)
                    if (tamable.isOrderedToSit() && !tamable.isInSittingPose())
                        tamable.setInSittingPose(true);
                if (bookEntity.entity instanceof LivingEntity livingEntity) {
                    drawLivingEntity(altarTile, poseStack, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.getRot(partial) + Mth.PI, 20, (float) (107), (float) (88 - 30), livingEntity, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
                } else if (bookEntity.entity != null) {
                    drawEntity(altarTile, poseStack, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.getRot(partial) + Mth.PI, 20, (float) (107), (float) (88 - 30), bookEntity.entity, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
                }
            }
        }


    }

    @OnlyIn(Dist.CLIENT)
    public void drawLivingEntity(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, LivingEntity livingEntity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, DrawingType drawingType) {
        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.translate(-1f / 512f, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        poseStack.scale(0.049f * scale, 0.049f * scale, 0.003f);
        poseStack.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        float $$6 = (float) Math.atan(p_98854_ / 40.0F);
        float $$7 = (float) Math.atan(p_98855_ / 40.0F);
        Quaternionf $$10 = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf $$11 = Axis.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        float $$12 = livingEntity.yBodyRot;
        float $$13 = livingEntity.getYRot();
        float $$15 = livingEntity.yHeadRotO;
        float $$16 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = rot + livingEntity.getId();
        livingEntity.setYRot(rot + livingEntity.getId());
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conjugate();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        livingEntity.yBodyRot = $$12;
        livingEntity.setYRot($$13);
        livingEntity.yHeadRotO = $$15;
        livingEntity.yHeadRot = $$16;
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawEntity(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, Entity entity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, DrawingType drawingType) {
        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.translate(-1f / 512f, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        poseStack.scale(0.049f * scale, 0.049f * scale, 0.003f);
        poseStack.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        float $$7 = (float) Math.atan(p_98855_ / 40.0F);
        Quaternionf $$10 = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf $$11 = Axis.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        poseStack.mulPose(Axis.YP.rotationDegrees(-(rot + entity.getId())));
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conjugate();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTooltips(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTicks) throws CommandSyntaxException {
        this.drawTooltip = altarTile.turnPage == 0;

        this.drawTooltipScale = Mth.lerp(partialTicks, altarTile.tooltipScaleOld, altarTile.tooltipScale);
        this.drawTooltipScaleOld = this.drawTooltipScale;
        if (this.drawTooltipStack && altarTile.turnPage == 0) {
            altarTile.drawTooltip = true;
            this.drawTooltipStackFlag = true;
            this.drawTooltipTextFlag = false;
        } else if (this.drawTooltipText && altarTile.turnPage == 0) {
            altarTile.drawTooltip = true;
            this.drawTooltipTextFlag = true;
            this.drawTooltipStackFlag = false;
        } else {
            altarTile.drawTooltip = false;
            if (this.drawTooltipScale == 0) {
                this.drawTooltipStackFlag = false;
                this.drawTooltipTextFlag = false;
            }
        }

        if (this.drawTooltipScale > 0) {
            if (this.drawTooltipStackFlag)
                drawTooltipImage(this.tooltipStack, altarTile, poseStack, bufferSource, 0, light, overlay, partialTicks);
            else
                drawTooltipText(altarTile, poseStack, bufferSource, 0, light, overlay, partialTicks);
        }
    }


    public int getBookPageSeed(String location, UUID bookuuid) {
        return location.hashCode() * 31959 * bookuuid.hashCode();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawPages(BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTicks, DrawingType drawingType) {
        drawPages(altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, drawingType, ItemDisplayContext.NONE, partialTicks);
    }


    @OnlyIn(Dist.CLIENT)
    public void drawPages(BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, DrawingType drawingType, ItemDisplayContext transformType, float partialTicks) {

        if (ClientProxy.keys == null)
            ClientProxy.keys = Minecraft.getInstance().options.keyMappings;

        this.drawSlotOverlay = false;
        this.drawTooltipStack = false;
        this.drawTooltipText = false;

        BookData bookData = altarTile.currentBook;
        if (bookData == null)
            return;

        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());

        if (bookEntries == null)
            return;

        String left_page = "";
        String right_page = "";
        String left_page_prev = "";
        String left_page_under = "";
        String left_page_under_under = "";
        String left_page_under_under_under = "";
        String right_page_under = "";
        String right_page_prev = "";
        String right_page_prev_prev = "";
        int location1_p = 0;
        int location2_p = 0;
        int location1_back_p = 0;
        int location2_back_p = 0;
        int location1_next_p = 0;
        int location2_next_p = 0;
        int chapter = 0;
        int page = 0;
        chapter = Math.max(0, bookData.getChapter());
        page = Math.max(0, bookData.getPage());
        if (page % 2 == 1)
            page--;

        if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0) {
            BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page);
            left_page = pageEntry.location;
            location1_p = pageEntry.pageNum;
        }
        if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0) {
            BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page + 1);
            right_page = pageEntry.location;
            location2_p = pageEntry.pageNum;
        }


        int next_page_chapter = chapter;
        int next_page_page = page;
        int back_page_chapter = chapter;
        int back_page_page = page;
        if (next_page_page < bookEntries.chapterList.get(chapter).pages.size() - 2)
            next_page_page += 2;
        else if (chapter < bookEntries.chapterList.size() - 1) {
            next_page_chapter++;
            next_page_page = 0;
        } else
            next_page_chapter = -1;

        if (next_page_chapter != -1 && next_page_chapter < bookEntries.chapterList.size() && next_page_page < bookEntries.chapterList.get(next_page_chapter).pages.size()) {

            BookPageEntry pageEntry = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page);
            right_page_under = pageEntry.location;
            location1_next_p = pageEntry.pageNum;
            if (bookEntries.chapterList.get(next_page_chapter).pages.size() > next_page_page + 1) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page + 1);
                right_page_prev = pageEntry2.location;
                location2_next_p = pageEntry2.pageNum;
                List<BookPageEntry> entries = bookEntries.chapterList.stream().flatMap((entry) -> entry.pages.stream()).toList();
                if (location2_next_p + 2 < entries.size())
                    right_page_prev_prev = entries.get(location2_next_p + 2).location;
            }
        }


        if (back_page_page - 2 >= 0)
            back_page_page -= 2;
        else if (back_page_chapter > 0) {
            back_page_chapter--;
            back_page_page = bookEntries.chapterList.get(back_page_chapter).pages.size() - 1;
            if (back_page_page % 2 == 1)
                back_page_page--;
        } else
            back_page_chapter = -1;

        if (back_page_chapter != -1 && back_page_chapter < bookEntries.chapterList.size() && back_page_page < bookEntries.chapterList.get(back_page_chapter).pages.size()) {

            BookPageEntry pageEntry = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page);
            left_page_prev = pageEntry.location;
            location1_back_p = pageEntry.pageNum;
            if (bookEntries.chapterList.get(back_page_chapter).pages.size() > back_page_page + 1) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page + 1);
                left_page_under = pageEntry2.location;
                location2_back_p = pageEntry2.pageNum;
            }

            if (back_page_page - 1 > 0) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page - 1);
                left_page_under_under = pageEntry2.location;
            } else if (back_page_chapter - 1 > 0) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter - 1).pages.getLast();
                left_page_under_under = pageEntry2.location;
            }

            if (back_page_page - 3 > 0) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page - 3);
                left_page_under_under_under = pageEntry2.location;
            } else if (back_page_chapter - 1 > 0) {
                BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter - 1).pages.get(bookEntries.chapterList.get(back_page_chapter - 1).pages.size() - 3);
                left_page_under_under_under = pageEntry2.location;
            }

        }

        if (transformType != ItemDisplayContext.GUI) {

            if (drawingType == DrawingType.SCREEN) {
                if (altarTile.pageOneRotationRender < 65 + 90 && altarTile.pageTwoRotationRender < 65 + 90) {

                    int seed =  getBookPageSeed(left_page_under, bookData.getUUID());
                    Random random = new Random(seed);
                    String pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page, bookData.getUUID());
                    random = new Random(seed);
                    pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page, bookData.getUUID());
                    random = new Random(seed);
                    ResourceLocation loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawImage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                    seed =  getBookPageSeed(left_page, bookData.getUUID());
                    random = new Random(seed);
                    loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);

                    BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page));
                    BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page));
                    drawPage(page1, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, drawingType, transformType, location1_p, partialTicks);
                    drawPage(page2, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, drawingType, transformType, location2_p, partialTicks);
                }
                if (altarTile.pageOneRotationRender > 15) {

                    int seed =  getBookPageSeed(right_page, bookData.getUUID());
                    Random random = new Random(seed);
                    String pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_UNDER, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page_prev, bookData.getUUID());
                    random = new Random(seed);
                    pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page_prev, bookData.getUUID());
                    random = new Random(seed);
                    ResourceLocation loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawImage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page_under, bookData.getUUID());
                    random = new Random(seed);
                    loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawImage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_UNDER, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);


                    BookPage page2_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_under));
                    BookPage page2_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_prev));
                    drawPage(page2_under, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_UNDER, drawingType, transformType, location1_next_p, partialTicks);
                    drawPage(page2_prev, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_PREV, drawingType, transformType, location2_next_p, partialTicks);
                }
                if (altarTile.pageTwoRotationRender > 15) {

                    int seed =  getBookPageSeed(left_page_under_under, bookData.getUUID());
                    Random random = new Random(seed);
                    String pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(left_page_under, bookData.getUUID());
                    random = new Random(seed);
                    pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_UNDER, -1, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(left_page_under, bookData.getUUID());
                    random = new Random(seed);
                    ResourceLocation loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_UNDER, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                    seed =  getBookPageSeed(left_page_prev, bookData.getUUID());
                    random = new Random(seed);
                    loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);


                    BookPage page1_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_under));
                    BookPage page1_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_prev));
                    drawPage(page1_under, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_UNDER, drawingType, transformType, location2_back_p, partialTicks);
                    drawPage(page1_prev, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_PREV, drawingType, transformType, location1_back_p, partialTicks);

                }

            } else {

                int seed =  getBookPageSeed(left_page_under_under, bookData.getUUID());
                Random random = new Random(seed);
                String pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV, -1, drawingType, transformType);

                seed =  getBookPageSeed(left_page_under, bookData.getUUID());
                random = new Random(seed);
                pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_UNDER, -1, drawingType, transformType);
                drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, -1, drawingType, transformType);

                seed =  getBookPageSeed(right_page, bookData.getUUID());
                random = new Random(seed);
                pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, -1, drawingType, transformType);
                drawBasePage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_UNDER, -1, drawingType, transformType);

                seed =  getBookPageSeed(right_page_prev, bookData.getUUID());
                random = new Random(seed);
                pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                drawBasePage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.2f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                        altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV, -1, drawingType, transformType);


                seed =  getBookPageSeed(left_page_prev, bookData.getUUID());
                random = new Random(seed);
                ResourceLocation loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                seed =  getBookPageSeed(right_page, bookData.getUUID());
                random = new Random(seed);
                loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                seed =  getBookPageSeed(right_page_under, bookData.getUUID());
                random = new Random(seed);
                loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_UNDER, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                seed =  getBookPageSeed(left_page_under, bookData.getUUID());
                random = new Random(seed);
                loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_UNDER, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                seed =  getBookPageSeed(left_page, bookData.getUUID());
                random = new Random(seed);
                loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.45f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                seed =  getBookPageSeed(right_page_prev, bookData.getUUID());
                random = new Random(seed);
                loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                if (loc != null && seed != 0)
                    drawImage(new BookImage(-0.5f - (0.1f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.19f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                            altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);



                if (altarTile.openedPercent < 0.6f) {
                    seed =  getBookPageSeed(left_page_under_under_under, bookData.getUUID());
                    random = new Random(seed);
                    pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawImage(new BookImage(-0.5f - (0.5f - (altarTile.pageTwoRotationRender / 180f) * 0.05f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.3f + (-altarTile.pageOneRotationRender / 180f) / 64f, 0, 0, 13, 18, 13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV_PREV, drawingType);
                    seed =  getBookPageSeed(left_page_under_under_under, bookData.getUUID());
                    random = new Random(seed);
                    loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawImage(new BookImage(-0.5f - (0.5f - (altarTile.pageTwoRotationRender / 180f) * 0.05f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.29f, 0, 0, 13, 18, 13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE_PREV_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);

                    seed =  getBookPageSeed(right_page_prev_prev, bookData.getUUID());
                    random = new Random(seed);
                    pageLoc = pageTextureLocs.isEmpty() ? "hexerei:textures/book/pages/page_1.png" : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                    drawImage(new BookImage(-0.5f - (0.05f + (altarTile.pageOneRotationRender / 180f) * 0.05f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.3f + (-altarTile.pageOneRotationRender / 180f) / 64f, 0, 0, 13, 18, -13, 18, 10.2f, pageLoc, new ArrayList<>()),
                            altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV_PREV, drawingType);
                    seed =  getBookPageSeed(right_page_prev_prev, bookData.getUUID());
                    random = new Random(seed);
                    loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                    if (loc != null && seed != 0)
                        drawImage(new BookImage(-0.5f - (0.05f + (altarTile.pageOneRotationRender / 180f) * 0.05f) + 6.55f / 2f, -1 - 0.49f + 9.1f / 2f, -0.29f, 0, 0, 13, 18, -13, 18, 10.2f, loc.toString(), new ArrayList<>()),
                                altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE_PREV_PREV, 0x99FFFFFF, drawingType, ItemDisplayContext.NONE);
                }


                BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page));
                BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page));
                drawPage(page1, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, drawingType, transformType, location1_p, partialTicks);
                drawPage(page2, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, drawingType, transformType, location2_p, partialTicks);
                if (altarTile.pageTwoRotationRender < 87.5f) {
                    BookPage page2_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_under));
                    BookPage page2_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_prev));
                    drawPage(page2_under, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_UNDER, drawingType, transformType, location1_next_p, partialTicks);
                    drawPage(page2_prev, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_PREV, drawingType, transformType, location2_next_p, partialTicks);
                }
                if (altarTile.pageOneRotationRender < 87.5f) {
                    BookPage page1_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_under));
                    BookPage page1_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_prev));
                    drawPage(page1_under, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_UNDER, drawingType, transformType, location2_back_p, partialTicks);
                    drawPage(page1_prev, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_PREV, drawingType, transformType, location1_back_p, partialTicks);
                }
            }
        } else {

            BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
            BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
            drawPage(page1, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, drawingType, transformType, location1_p, partialTicks);
            drawPage(page2, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, drawingType, transformType, location2_p, partialTicks);
        }

        drawBaseButtons(altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, !right_page_under.isEmpty(), !left_page_prev.isEmpty(), chapter, page, drawingType, partialTicks);

    }

    @OnlyIn(Dist.CLIENT)
    public void drawBaseButtons(BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, DrawingType drawingType, float partial) {
        drawBaseButtons(altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, poseStack, bufferSource, light, overlay, drawNext, drawBack, chapter, page, drawingType, ItemDisplayContext.NONE, false, partial);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawBaseButtons(BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, DrawingType drawingType, ItemDisplayContext transformType, boolean fullyExtended, float partial) {

        BookEntries bookEntries = BookManager.getBookEntries(altarTile.currentBook.getBook());
        if (bookEntries != null) {

            for (int i = 0; i < this.bookmarkHoverAmountRender.size(); i++)
                this.bookmarkHoverAmountRender.set(i, easeInOutElastic(Mth.lerp(partial, this.bookmarkHoverAmountOld.get(i), this.bookmarkHoverAmount.get(i))));


            boolean drawBookmarkButton = chapter != 0;
            BookData bookData = altarTile.currentBook;

            if (drawBookmarkButton && drawingType != DrawingType.GUI) {
                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                BookImageEffect bookImageEffect_hover_overlay = new BookImageEffect("hover_overlay", 35, 10f, new BookImage(-0.5f, -1f, -1, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2f, "hexerei:textures/book/bookmark_button_hover.png", effects));

                boolean flag = canInteract(leftCursorX, leftCursorY, -0.45f, -0.96f, 0.86f, 0.86f, altarTile, drawingType);
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    effects.add(bookImageEffect_hover_overlay);
                }


                if (bookData != null) {

                    DyeColor bookmark_color = DyeColor.WHITE;
                    int bookmark_chapter = 0;
                    int bookmark_page = 0;
                    String bookmark_id;
                    boolean flag2 = false;

                    for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                            boolean flag3 = false;
                            if (!slot.getId().isEmpty()) {
                                bookmark_color = slot.getColor();
                                bookmark_id = slot.getId();
                                for (BookChapter chapterEntry : bookEntries.chapterList) {
                                    for (BookPageEntry pageEntry : chapterEntry.pages) {
                                        if (pageEntry.location.equals(bookmark_id)) {
                                            bookmark_chapter = pageEntry.chapterNum;
                                            bookmark_page = pageEntry.chapterPageNum;
                                            flag3 = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (flag3) {
                                if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
                                    flag2 = true;
                                    break;
                                }
                            }

                    }
                    // draw bookmark button
                    if (flag2) {


                        if (flag) {
                            List<Component> list = new ArrayList<>();

                            String output = bookmark_color.getName().substring(0, 1).toUpperCase() + bookmark_color.getName().substring(1);
                            output = output.replaceAll("_", " ");

                            list.add(Component.translatable("Change Color - %s", Component.translatable("%s", output).withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))).withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }


                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_underlay.png", effects);
                        BookImage bookImage_overlay = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_overlay.png", effects);

                        drawImage(bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, drawingType);
                        drawImage(bookImage_overlay, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);

                    } else {

                        if (flag) {
                            List<Component> list = new ArrayList<>();

                            list.add(Component.translatable("Bookmark Page").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }

                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button.png", effects);

                        drawImage(bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, drawingType);
                    }

                    //draw bookmarks


                }
            }
            if (bookData != null) {

                DyeColor bookmark_color;
                int bookmark_chapter = 0;
                int bookmark_page = 0;
                ResourceLocation bookmark_id;
                for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                    boolean flag2 = false;
                    if (!slot.getId().isEmpty()) {


                        bookmark_color = slot.getColor();
                        if (!slot.getId().isEmpty())
                            bookmark_id = ResourceLocation.parse(slot.getId());
                        else
                            bookmark_id = null;

                        boolean flag3 = false;
                        if (bookmark_id != null) {
                            for (BookChapter chapterEntry : bookEntries.chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (ResourceLocation.parse(pageEntry.location).equals(bookmark_id)) {
                                        bookmark_chapter = pageEntry.chapterNum;
                                        bookmark_page = pageEntry.chapterPageNum;
                                        flag3 = true;
                                        break;
                                    }
                                }
                            }
                        }


                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();

                        if (slot.getIndex() < 5) {

                            float xIn = -0.3f - altarTile.buttonScaleRender - 0.15f;
                            float yIn = slot.getIndex() * 1.5f;

                            float width = 0.935f;
                            if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                if (!this.bookmarkHovered.contains(slot.getIndex()))
                                    this.bookmarkHovered.add(slot.getIndex());

                                List<Component> list = new ArrayList<>();

                                if (flag3) {
                                    String name = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                                    BookPage bookmarkedPage = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(name));
                                    if (bookmarkedPage != null) {
                                        name = bookmarkedPage.name;
                                    }
                                    if (name.isEmpty()) {
                                        list.add(Component.translatable("%s%s - Page %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    } else {

                                        list.add(Component.translatable("%s%s - %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable(name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    }
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }

                            }

                            float bookX = xIn + 0.8f - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2 * altarTile.buttonScaleRender;
                            if (fullyExtended)
                                bookX = xIn + 0.4f - 0.33f;

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, altarTile, poseStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                            drawBookmark(bookImageOverlay, altarTile, poseStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                        }
                        if (slot.getIndex() >= 5 && slot.getIndex() < 10) {


                            float xIn = -5.5f + slot.getIndex() * 1.15f;
                            float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;

                            float width = 0.86f;
                            if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                if (!this.bookmarkHovered.contains(slot.getIndex()))
                                    this.bookmarkHovered.add(slot.getIndex());

                                List<Component> list = new ArrayList<>();

                                if (flag3) {
                                    String name = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                                    BookPage bookmarkedPage = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(name));
                                    if (bookmarkedPage != null) {
                                        name = bookmarkedPage.name;
                                    }
                                    if (name.isEmpty()) {
                                        list.add(Component.translatable("%s%s - Page %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    } else {

                                        list.add(Component.translatable("%s%s - %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable(name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    }
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            float bookY = yIn + 0.8f - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2 * altarTile.buttonScaleRender;

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, altarTile, poseStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                            drawBookmark(bookImageOverlay, altarTile, poseStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                        }
                        if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                            float xIn = -11.25f + slot.getIndex() * 1.15f;
                            float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;

                            float width = 0.86f;
                            if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                if (!this.bookmarkHovered.contains(slot.getIndex()))
                                    this.bookmarkHovered.add(slot.getIndex());
                                List<Component> list = new ArrayList<>();

                                if (flag3) {
                                    String name = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                                    BookPage bookmarkedPage = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(name));
                                    if (bookmarkedPage != null) {
                                        name = bookmarkedPage.name;
                                    }
                                    if (name.isEmpty()) {
                                        list.add(Component.translatable("%s%s - Page %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    } else {

                                        list.add(Component.translatable("%s%s - %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable(name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    }
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            float bookY = yIn + 0.8f - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2 * altarTile.buttonScaleRender;

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, altarTile, poseStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                            drawBookmark(bookImageOverlay, altarTile, poseStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                        }
                        if (slot.getIndex() >= 15) {

                            float xIn = 5.2f + altarTile.buttonScaleRender + 0.15f;
                            float yIn = (slot.getIndex() - 15) * 1.5f;

                            float width = 0.86f;
                            if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                if (!this.bookmarkHovered.contains(slot.getIndex()))
                                    this.bookmarkHovered.add(slot.getIndex());

                                List<Component> list = new ArrayList<>();
                                if (flag3) {
                                    String name = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                                    BookPage bookmarkedPage = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(name));
                                    if (bookmarkedPage != null) {
                                        name = bookmarkedPage.name;
                                    }
                                    if (name.isEmpty()) {
                                        list.add(Component.translatable("%s%s - Page %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    } else {

                                        list.add(Component.translatable("%s%s - %s%s",
                                                        Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                                        Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable(name).withStyle(Style.EMPTY.withColor(10329495)),
                                                        Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))))
                                                .withStyle(Style.EMPTY.withColor(10329495)));
                                    }
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            float bookX = xIn - 0.7f + this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2 * altarTile.buttonScaleRender;

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, altarTile, poseStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                            drawBookmark(bookImageOverlay, altarTile, poseStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), drawingType, transformType);
                        }


                        if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
                            if (!this.bookmarkHovered.contains(slot.getIndex()))
                                this.bookmarkHovered.add(slot.getIndex());
                        }

                    }
                }


                //send to server to update the slotClicked

                if (altarTile.slotClicked != -1) {
                    for (int i = 0; i < 20; i++) {

                        if (i == altarTile.slotClicked)
                            continue;

                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                        if (i < 5) {

                            float xIn = -0.3f - altarTile.buttonScaleRender - 0.15f;
                            float yIn = i * 1.5f;

                            float width = 0.935f;
                            if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * altarTile.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, altarTile, poseStack, bufferSource, 1, 90, light, overlay, PageOn.LEFT_PAGE, -1, drawingType, transformType);
                        }
                        if (i >= 5 && i < 10) {

                            float xIn = -5.5f + i * 1.15f;
                            float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;

                            float width = 0.935f;
                            if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * altarTile.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, altarTile, poseStack, bufferSource, 1, 0, light, overlay, PageOn.LEFT_PAGE, -1, drawingType, transformType);
                        }
                        if (i >= 10 && i < 15) {

                            float xIn = -11.25f + i * 1.15f;
                            float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;

                            float width = 0.935f;
                            if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * altarTile.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, altarTile, poseStack, bufferSource, 1, 0, light, overlay, PageOn.RIGHT_PAGE, -1, drawingType, transformType);
                        }
                        if (i >= 15) {

                            float xIn = 5.2f + altarTile.buttonScaleRender + 0.15f;
                            float yIn = (i - 15) * 1.5f;

                            float width = 0.935f;
                            if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * altarTile.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, altarTile, poseStack, bufferSource, 1, -90, light, overlay, PageOn.RIGHT_PAGE, -1, drawingType, transformType);
                        }

                    }
                }
            }


            if (drawingType != DrawingType.GUI) {

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);

                String loc = "hexerei:textures/book/font_button.png";
                if (drawBack)
                    loc = "hexerei:textures/book/back_page.png";

                float x = -0.45f, y = 7.2f, width = 0.86f;
                if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    List<Component> list = new ArrayList<>();
                    if (drawBack) {
                        list.add(Component.translatable("Back").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));

                        loc = "hexerei:textures/book/back_page_hover.png";
                    } else {
                        list.add(Component.translatable("Change Font").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));

                        loc = "hexerei:textures/book/font_button_hover.png";
                    }

                    this.tooltipText = list;
                    this.tooltipStack = ItemStack.EMPTY;
                    this.drawTooltipText = true;
                }

                BookImage bookImage = new BookImage(-0.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2, loc, effects);

                drawImage(bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, drawingType);
            }


            if (drawingType != DrawingType.GUI) {

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc_close = "hexerei:textures/book/close.png";
                String loc_del = "hexerei:textures/book/delete.png";

                float width = 0.86f;
                if (canInteract(rightCursorX, rightCursorY, -0.25f - width / 2, 7.5f - width / 2, width, width, altarTile, drawingType)) {
//                if (canInteract(0 - width / 2, 7.2f - width / 2, width, width, altarTile, PageOn.MIDDLE_BUTTON)) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);

                    if (altarTile.slotClicked != -1 && altarTile.slotClickedTick > 5) {
                        loc_del = "hexerei:textures/book/delete_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Delete Bookmark").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                    } else {
                        loc_close = "hexerei:textures/book/close_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Close Book").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                    }
                    this.drawTooltipText = true;
                    this.tooltipStack = ItemStack.EMPTY;
                }
                BookImage bookImage;
                if (altarTile.slotClicked != -1 && altarTile.slotClickedTick > 5)
                    bookImage = new BookImage(0, 0, 35, 0, 0, 32, 32, 32, 32, altarTile.bookmarkSelectorScale / 1.5f, loc_del, effects);
                else
                    bookImage = new BookImage(0, 0, 35, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2f, loc_close, effects);

                drawImage(bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, drawingType);

                effects = new ArrayList<>();
                bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc = "hexerei:textures/book/home.png";

                if (canInteract(rightCursorX, rightCursorY, -0.25f - width / 2, -0.5f - width / 2, width, width, altarTile, drawingType)) {
//                if (canInteract(0 - width / 2, -1 - width / 2, width, width, altarTile, PageOn.MIDDLE_BUTTON)) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    loc = "hexerei:textures/book/home_hover.png";
                    List<Component> list = new ArrayList<>();
                    list.add(Component.translatable("Home").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                    this.tooltipText = list;
                    this.drawTooltipText = true;
                    this.tooltipStack = ItemStack.EMPTY;
                }

                bookImage = new BookImage(0, -8.1f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2f, loc, effects);

                drawImage(bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, drawingType);


                if (drawNext) {
                    effects = new ArrayList<>();
                    bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                    bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                    loc = "hexerei:textures/book/next_page.png";

//                    if (canInteract(5.415f, 7.2f, 0.86f, 0.86f, altarTile, PageOn.RIGHT_PAGE)) {
                    if (canInteract(rightCursorX, rightCursorY, 5.415f, 7.2f, 0.86f, 0.86f, altarTile, drawingType)) {
                        effects.add(bookImageEffect_scale);
                        effects.add(bookImageEffect_tilt);
                        loc = "hexerei:textures/book/next_page_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Next").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }


                    bookImage = new BookImage(5.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2, loc, effects);

                    drawImage(bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, drawingType);
                }


                if (drawingType != DrawingType.SCREEN) {


                    effects = new ArrayList<>();
                    bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                    bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                    loc = "hexerei:textures/book/open_gui.png";

                    if (Minecraft.getInstance().screen == null && canInteract(5.49f, -0.97f, 0.86f, 0.86f, altarTile, PageOn.RIGHT_PAGE)) {
                        effects.add(bookImageEffect_scale);
                        effects.add(bookImageEffect_tilt);
                        loc = "hexerei:textures/book/open_gui_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Open in GUI").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }


                    bookImage = new BookImage(5.5f, -1f, 0, 0, 0, 32, 32, 32, 32, altarTile.buttonScaleRender / 2, loc, effects);

                    drawImage(bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, drawingType);
                }
            }
        }
    }

    private float moveTo(float input, float moveTo, float speed) {
        float distance = moveTo - input;

        if (Math.abs(distance) <= speed) {
            return moveTo;
        }

        if (distance > 0) {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawItemInSlot(BookOfShadowsAltarTile altarTile, BookItemsAndFluids bookItemStackInSlot, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {
        if (bookItemStackInSlot.type.equals("item") || bookItemStackInSlot.type.equals("tag")) {
            if (bookItemStackInSlot.show_slot)
                drawSlot(altarTile, poseStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, drawingType);
            renderItem(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, drawingType);
        } else if (bookItemStackInSlot.type.equals("fluid")) {
            drawFluidInSlot(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, drawingType);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void drawBlock(BookOfShadowsAltarTile altarTile, BookBlocks bookItemStackInSlot, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {
        if (bookItemStackInSlot.type.equals("block") || bookItemStackInSlot.type.equals("tag")) {
            if (bookItemStackInSlot.show_slot)
                drawSlot(altarTile, poseStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, drawingType);
            renderBlock(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, drawingType);
        }

    }


    public static Vec3 getPointOnPlane(float x, float y, float xscale, float yscale, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        Vector3f leftOffset = new Vector3f(0.375f, 0.532f, -0.03f);
        Vector3f rightOffset = new Vector3f(-0.012f, 0.532f, -0.03f);
        return getPointOnPlane(leftOffset, rightOffset, x, y, xscale, yscale, altarTile, pageOn);
    }

    public static Vec3 getPointOnPlane(Vector3f leftOffset, Vector3f rightOffset, float x, float y, float xscale, float yscale, BookOfShadowsAltarTile altarTile, PageOn pageOn){
        Vector3f offset = pageOn == PageOn.RIGHT_PAGE ? rightOffset : leftOffset;
        BlockPos blockPos = altarTile.getBlockPos();
        Vec3 pointBase = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                blockPos.getY() + 18 / 16f + ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        Vector3f vector3f = new Vector3f(offset.x, offset.y, offset.z).add(x * -xscale, y * -yscale, 0.03f);
        vector3f.rotate(Axis.YP.rotationDegrees((pageOn == PageOn.RIGHT_PAGE ? -1 : 1) * (10 + altarTile.degreesOpened / 1.12f)));
        vector3f.add(0, 0, -0.03f);
        vector3f.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
        return pointBase.add(vector3f.x, vector3f.y, vector3f.z);
    }

    public void tick() {
        isClickedOld = isClicked;

        this.bookmarkHoverAmountOld = new ArrayList<>(this.bookmarkHoverAmount);
        for (int i = 0; i < this.bookmarkHoverAmount.size(); i++) {
            int finalI = i;
            if (!this.bookmarkHovered.stream().filter((f) -> f == finalI).toList().isEmpty())
                this.bookmarkHoverAmount.set(i, moveTo(this.bookmarkHoverAmount.get(i), 1, 0.1f));
            else
                this.bookmarkHoverAmount.set(i, moveTo(this.bookmarkHoverAmount.get(i), 0, 0.05f));
        }
        this.bookmarkHovered = new ArrayList<>();

        this.drawTooltipScaleOld = this.drawTooltipScale;
        if (this.drawTooltipStack && this.drawTooltip) {
            this.drawTooltipStackFlag = true;
            this.drawTooltipTextFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1f, 0.1f);
        } else if (this.drawTooltipText && this.drawTooltip) {
            this.drawTooltipTextFlag = true;
            this.drawTooltipStackFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1f, 0.1f);
        } else {
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 0, 0.2f);
            if (this.drawTooltipScale == 0) {
                this.drawTooltipStackFlag = false;
                this.drawTooltipTextFlag = false;
            }
        }



        boolean debugDraw = false;
        if (altarTile.currentBook != null && BookManager.getBookEntries(altarTile.currentBook.getBook()) != null && altarTile.openedPercent != 1 && debugDraw) {

            BookEntries bookEntries = BookManager.getBookEntries(altarTile.currentBook.getBook());

            boolean drawEdges = true;
            if (drawEdges){
                int count = 15;
                for (int i = 0; i < count + 1; i++) {
                    float xIn = -0.5f + i * 6.5f / (float) count;
                    float yIn = -1f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }
                for (int i = 0; i < count + 1; i++) {
                    float xIn = -0.5f + i * 6.5f / (float) count;
                    float yIn = 8f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }
                for (int i = 0; i < count + 1; i++) {
                    float xIn = -0.5f;
                    float yIn = i * 9 / (float) count - 1f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }

                for (int i = 0; i < count + 1; i++) {
                    float xIn = -0.15f + i * 6.5f / (float) count;
                    float yIn = -1f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }
                for (int i = 0; i < count + 1; i++) {
                    float xIn = -0.15f + i * 6.5f / (float) count;
                    float yIn = 8f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }
                for (int i = 0; i < count + 1; i++) {
                    float xIn = 6.35f;
                    float yIn = i * 9 / (float) count - 1f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                }
            }


            boolean drawBookmarks = false;
            if (drawBookmarks){
                for (int i = 0; i < 5; i++) {

                    float xIn = -0.3f - altarTile.buttonScaleRender - 0.15f;
                    float yIn = i * 1.5f;
                    float width = 0.935f;
                    float height = 0.935f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);

                }
                for (int i = 5; i < 10; i++) {

                    float xIn = -5.5f + i * 1.15f;
                    float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;
                    float width = 0.935f;
                    float height = 0.935f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);

                }
                for (int i = 10; i < 15; i++) {

                    float xIn = -11.25f + i * 1.15f;
                    float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;
                    float width = 0.935f;
                    float height = 0.935f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }
                for (int i = 15; i < 20; i++) {

                    float xIn = 5.2f + altarTile.buttonScaleRender + 0.15f;
                    float yIn = (i - 15) * 1.5f;
                    float width = 0.935f;
                    float height = 0.935f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }
            }


            boolean drawCorners = false;
            if (drawCorners){
                // next
                {
                    float xIn = 5.415f;
                    float yIn = 7.2f;
                    float width = 0.86f;
                    float height = 0.86f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }


                // open gui
                {
                    float xIn = 5.415f;
                    float yIn = -0.97f;
                    float width = 0.86f;
                    float height = 0.86f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.RIGHT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }


                // bookmark
                {
                    float xIn = -0.45f;
                    float yIn = -0.96f;
                    float width = 0.86f;
                    float height = 0.86f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }


                // back
                {
                    float xIn = -0.45f;
                    float yIn = 7.2f;
                    float width = 0.86f;
                    float height = 0.86f;
                    float xscale = 0.062f;
                    float yscale = 0.062f;
                    Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, PageOn.LEFT_PAGE);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                    altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                }
            }


            String location1 = "";
            String location2 = "";
            int chapter = altarTile.currentBook.getChapter();
            int page = altarTile.currentBook.getPage();
            if (page % 2 == 1)
                page--;

            if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0)
                location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
            if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0)
                location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

            BookPage page1 = BookManager.getBookPages(altarTile.currentBook.getBook(), ResourceLocation.parse(location1));
            BookPage page2 = BookManager.getBookPages(altarTile.currentBook.getBook(), ResourceLocation.parse(location2));

            MutableComponent component = Component.literal("");

            for(PageOn pageOn : List.of(PageOn.LEFT_PAGE, PageOn.RIGHT_PAGE)){
                BookPage pageUsed = pageOn == PageOn.LEFT_PAGE ? page1 : page2;

                if (pageUsed != null) {

                    if (false)
                    for (BookNonItemTooltip bookNonItemTooltip : pageUsed.nonItemTooltipList) {

                        float xIn = bookNonItemTooltip.x;
                        float yIn = bookNonItemTooltip.y;
                        float xscale = 0.062f;
                        float yscale = 0.062f;
                        float width = bookNonItemTooltip.width;
                        float height = bookNonItemTooltip.height;
                        Vector3f rightOffset = new Vector3f(-0.012f, 0.532f, -0.03f);
                        Vector3f leftOffset = new Vector3f(0.375f, 0.532f, -0.03f);
                        Vec3 planePoint = getPointOnPlane(leftOffset, rightOffset, xIn, yIn, xscale, yscale, altarTile, pageOn);
                        Vec3 planePoint2 = getPointOnPlane(leftOffset, rightOffset, xIn + width, yIn + height, xscale, yscale, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                    }

                    for (BookEntity entity : pageUsed.entityList) {

                        float xIn = entity.x + entity.offset.x + 0.52f;
                        float yIn = entity.y + entity.offset.y;
                        float xscale = 0.062f;
                        float width = 1.25f + entity.scale / 5f;
                        Vec3 planePoint2 = getPointOnPlane(xIn - width/2, yIn - width/2, xscale, xscale, altarTile, pageOn);
                        Vec3 planePoint3 = getPointOnPlane(xIn + width/2, yIn + width/2, xscale, xscale, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint3.x, planePoint3.y, planePoint3.z, 0, 0, 0);

                    }

                    if (false)
                    for (BookItemsAndFluids item : pageUsed.itemList) {

                        float xIn = item.x;
                        float yIn = item.y;
                        float xscale = 0.062f;
                        float yscale = 0.062f;
                        float width = 0.0565f / xscale;
                        float height = 0.0565f / yscale;
                        Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, pageOn);
                        Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);

                    }

                    if (false)
                    for (BookBlocks bookBlock : pageUsed.blockList) {

                        float xIn = bookBlock.x;
                        float yIn = bookBlock.y;
                        float xscale = 0.062f;
                        float yscale = 0.062f;
                        float width = 0.0565f / xscale;
                        float height = 0.0565f / yscale;
                        Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, altarTile, pageOn);
                        Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);

                    }

                    if (false)
                    for (BookImage bookImage : pageUsed.imageList) {

                        float xIn = bookImage.x;
                        float yIn = bookImage.y;
                        float xscale = 0.062f;
                        float yscale = 0.062f;
                        float width = bookImage.width / 330 * bookImage.scale / xscale;
                        float height = bookImage.height / 330 * bookImage.scale / xscale;
                        Vector3f leftOffset = new Vector3f(0.3505f, 0.5015f, -0.03f);
                        Vector3f rightOffset = new Vector3f(-0.042f, 0.5015f, -0.03f);
                        Vec3 planePoint = getPointOnPlane(leftOffset, rightOffset, xIn - width / 2, yIn - height / 2, xscale, yscale, altarTile, pageOn);
                        Vec3 planePoint2 = getPointOnPlane(leftOffset, rightOffset, xIn + width / 2, yIn + height / 2, xscale, yscale, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0, 0, 0);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0, 0, 0);

                    }


                    Vec2 ip = PageDrawing.getIntersectPoint(Hexerei.proxy.getPlayer().getLookAngle(), Hexerei.proxy.getPlayer().getEyePosition(), altarTile, pageOn);
                    if (ip != null) {
                        if (!component.getString().isEmpty())
                            component.append(Component.literal("    -    "));
                        component.append(Component.literal(pageOn + String.format(": %.3f,  %.3f", ip.x, ip.y)));
                        Vec3 planePoint = getPointOnPlane(ip.x, ip.y, 0.062f, 0.062f, altarTile, pageOn);
                        altarTile.getLevel().addParticle(ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 2, 0, 0);
                    }
                }
            }
            if (!component.getString().isEmpty() && Math.sqrt(altarTile.getBlockPos().distToCenterSqr(Hexerei.proxy.getPlayer().getEyePosition())) < Hexerei.proxy.getPlayer().getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE))
                Hexerei.proxy.getPlayer().displayClientMessage(component, true);
        }

    }



    public static String getModNameForModId(String modId) {
        return HexereiUtil.getModNameForModId(modId);
    }

    public static List<BlockPos> getAltars(Player playerIn) {
        double reach = playerIn.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

        List<BlockPos> altars = new ArrayList<>();

        float f = playerIn.getXRot();
        float f1 = playerIn.getYRot();
        Vec3 vec3 = playerIn.getEyePosition();
        Vec3 vec31 = new Vec3(0f, 0f, 0.25f);
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;

        float section = 0;
        while (section <= reach) {
            BlockPos pos = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section));
            BlockPos pos2 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90))));
            BlockPos pos3 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90))));
            if (!altars.contains(pos))
                altars.add(pos);
            if (!altars.contains(pos2))
                altars.add(pos2);
            if (!altars.contains(pos3))
                altars.add(pos3);
            if (section > reach) {
                section = (float) reach;
                pos = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section));
                pos2 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90))));
                pos3 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90))));
                if (!altars.contains(pos))
                    altars.add(pos);
                if (!altars.contains(pos2))
                    altars.add(pos2);
                if (!altars.contains(pos3))
                    altars.add(pos3);
                break;
            }
            else
                section += 0.25f;
        }
        return altars;
    }


    public static Vec3 calculatePlaneNormal(Vec3 originPointOnPlane, Vec3 anotherPointOnPlane, Vec3 thirdPointOnPlane) {
        // Calculate the plane basis vectors V1 and V2
        Vec3 V1 = anotherPointOnPlane.subtract(originPointOnPlane);
        Vec3 V2 = thirdPointOnPlane.subtract(originPointOnPlane);

        // Calculate the normal vector using the cross product of V1 and V2
        Vec3 normal = V1.cross(V2).normalize();

        return normal;
    }

    public static Vec2 getLookingAtPointOnPlane(Vec3 originPointOnPlane, Vec3 anotherPointOnPlane, Vec3 thirdPointOnPlane, Vec3 rayStart, Vec3 rayDirection) {

        // Calculate the plane normal
        Vec3 planeNormal = calculatePlaneNormal(originPointOnPlane, anotherPointOnPlane, thirdPointOnPlane);

        // Calculate the plane basis vectors (u, v)
        Vec3 u = anotherPointOnPlane.subtract(originPointOnPlane).normalize();
        Vec3 v = planeNormal.cross(u).normalize();

        // Calculate t for the ray-plane intersection
        double t = planeNormal.dot(originPointOnPlane.subtract(rayStart)) / planeNormal.dot(rayDirection);

        // If t is negative, the ray does not intersect the plane in the forward direction
        if (t < 0) return null;

        // Calculate the intersection point
        Vec3 intersection = rayStart.add(rayDirection.scale(t));

        // Convert intersection point to local plane coordinates (x, y)
        Vec3 planeToPoint = intersection.subtract(originPointOnPlane);
        double x = planeToPoint.dot(u);
        double y = planeToPoint.dot(v);

        // Check if the (x, y) coordinates are within the specified area
        return new Vec2((float)x, (float)y);
    }


    @OnlyIn(Dist.CLIENT)
    public static boolean canInteract(float x, float y, float width, float height, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        Player player = Minecraft.getInstance().player;
        Vec2 ip = getIntersectPoint(player.getLookAngle(), player.getEyePosition(), altarTile, pageOn);
        return ip != null && ip.x >= x && ip.x <= x + width && ip.y >= y && ip.y <= y + height &&
                Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean canInteract(float xCursor, float yCursor, float x, float y, float width, float height, BookOfShadowsAltarTile altarTile, DrawingType drawingType) {
        Player player = Minecraft.getInstance().player;
        return xCursor >= x && xCursor <= x + width && yCursor >= y && yCursor <= y + height &&
                (drawingType == DrawingType.SCREEN || Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE));
    }


    @OnlyIn(Dist.CLIENT)
    public static boolean canInteract(Vector3f leftOffset, Vector3f rightOffset, float x, float y, float width, float height, Player player, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        Vec2 ip = getIntersectPoint(leftOffset, rightOffset, player.getLookAngle(), player.getEyePosition(), altarTile, pageOn);
        return ip != null && ip.x >= x && ip.x <= x + width && ip.y >= y && ip.y <= y + height &&
                Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
    }


    @OnlyIn(Dist.CLIENT)
    public static Vec2 getIntersectPoint(Vec3 rayVector, Vec3 rayPoint, BookOfShadowsAltarTile altarTile, PageOn pageOn) {

        Vector3f rightOffset = new Vector3f(-0.012f, 0.532f, -0.03f);
        Vector3f leftOffset = new Vector3f(0.375f, 0.532f, -0.03f);
        return getIntersectPoint(leftOffset, rightOffset, rayVector, rayPoint, altarTile, pageOn);
    }

    @OnlyIn(Dist.CLIENT)
    public static Vec2 getIntersectPoint(Vector3f leftOffset, Vector3f rightOffset, Vec3 rayVector, Vec3 rayPoint, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        if (pageOn != PageOn.LEFT_PAGE && pageOn != PageOn.RIGHT_PAGE && pageOn != PageOn.MIDDLE_BUTTON)
            return null;

        float scale = 0.062f;
        Vector3f middleOffset = new Vector3f(0, 0.5f, -0.03f);
        Vector3f offset = pageOn == PageOn.RIGHT_PAGE ? rightOffset : pageOn == PageOn.LEFT_PAGE ? leftOffset : middleOffset;
        BlockPos blockPos = altarTile.getBlockPos();
        Vec3 pointBase = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                blockPos.getY() + 18 / 16f + ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        Vector3f vector3f = new Vector3f(offset.x, offset.y, offset.z).add(0 * -scale, 0 * -scale, 0.03f);
        if (pageOn != PageOn.MIDDLE_BUTTON)
            vector3f.rotate(Axis.YP.rotationDegrees((pageOn == PageOn.RIGHT_PAGE ? -1 : 1) * (10 + altarTile.degreesOpened / 1.12f)));
        vector3f.add(0, 0, -0.03f);
        vector3f.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
        Vec3 planePoint = pointBase.add(vector3f.x, vector3f.y, vector3f.z);

        vector3f = new Vector3f(offset.x, offset.y, offset.z).add(5 * -scale, 0 * -scale, 0);
        if (pageOn != PageOn.MIDDLE_BUTTON)
            vector3f.rotate(Axis.YP.rotationDegrees((pageOn == PageOn.RIGHT_PAGE ? -1 : 1) * (10 + altarTile.degreesOpened / 1.12f)));
        vector3f.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
        Vec3 planePoint2 = pointBase.add(vector3f.x, vector3f.y, vector3f.z);

        vector3f = new Vector3f(offset.x, offset.y, offset.z).add(0 * -scale, 5 * -scale, 0);
        if (pageOn != PageOn.MIDDLE_BUTTON)
            vector3f.rotate(Axis.YP.rotationDegrees((pageOn == PageOn.RIGHT_PAGE ? -1 : 1) * (10 + altarTile.degreesOpened / 1.12f)));
        vector3f.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
        Vec3 planePoint3 = pointBase.add(vector3f.x, vector3f.y, vector3f.z);

        Vec2 val = getLookingAtPointOnPlane(planePoint, planePoint2, planePoint3, rayPoint, rayVector);
        return val != null ? val.scale(1 / scale) : null;
    }

    public boolean interactClick(BookOfShadowsAltarTile altarTile, Player playerIn, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, DrawingType drawingType) {
        if (altarTile.turnPage == 0) {

            if (altarTile.slotClicked != -1) {
                if (++altarTile.slotClickedTick > 0) {
                    playerIn.swinging = false;

                }
            }

            BookData bookData = altarTile.currentBook;

            if (bookData != null && bookData.isOpened()) {
                if (PageDrawing.checkClick(bookData, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, drawingType)) {
                    isClickedOld = true;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean releaseClick(BookOfShadowsAltarTile altarTile, Player playerIn, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, DrawingType drawingType) {
        //released
        if (PageDrawing.focusedWritableTextBox != null)
            PageDrawing.focusedWritableTextBox.getRight().client.clicked = false;
        if (altarTile.turnPage == 0) {

            BookData bookData = altarTile.currentBook;

            if (bookData != null) {

                BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());

                if (bookEntries != null) {

                    int chapterNum = bookData.getChapter();
                    int pageNum = bookData.getPage();
                    if (pageNum % 2 == 1)
                        pageNum--;

                    String location1 = "";

                    if (bookEntries.chapterList.get(chapterNum).pages.size() > pageNum && pageNum >= 0)
                        location1 = bookEntries.chapterList.get(chapterNum).pages.get(pageNum).location;

                    BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location1));

                    List<BookPageEntry> entries = bookEntries.chapterList.stream().flatMap((entry) -> entry.pages.stream()).toList();
                    for (BookPageEntry bookPageEntry : entries) {
                        BookPage page = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(bookPageEntry.location));
                        if (page != null) {
                            boolean flag = page == page1; // if its page 1 use left, otherwise use right
                            for (BookPaintElement paintElement : page.paintElements) {
                                PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());
                                if (paintElement.client != null) {
                                    float w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
                                    float h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
                                    float x = paintElement.x + 0.025f;// + 0.455f;
                                    float y = paintElement.y - 0.5f;// + 0.49f;
                                    int xPixel = (int) (((flag ? leftCursorX : rightCursorX) - x + (flag ? 0 : 0.2f)) / w * paintElement.width);
                                    int yPixel = (int) (((flag ? leftCursorY : rightCursorY) - y) / h * paintElement.height);
                                    paintSystem.released(xPixel, yPixel);

                                    paintSystem.getValueSliders().release();
                                }
                            }
                        }
                    }


                    if (altarTile.slotClicked != -1) {
                        float x, y, width = 0.86f;

                        int bookmark_chapter = 0;
                        int bookmark_page = 0;
                        ResourceLocation bookmark_id;

                        BookData.Bookmarks bookmarks = bookData.getBookmarks();

                        x = -0.25f;
                        y = 7.5f;
                        if (canInteract(rightCursorX, rightCursorY, x - width / 2, y - width / 2, width, width, altarTile, drawingType)) {
//                if (PageDrawing.canInteract(0 - 0.86f / 2, 7.2f - 0.86f / 2, 0.86f, 0.86f, altarTile, PageDrawing.PageOn.MIDDLE_BUTTON)) {
                            //send signal to server that you deleted your bookmark.
                            altarTile.deleteBookmark(altarTile.slotClicked);
                            altarTile.slotClicked = -1;
                            altarTile.slotClickedTick = 0;
                            return true;
                        }


                        boolean flag2 = false;
                        for (BookData.Bookmarks.Slot slot : bookmarks.getSlots()) {

                            bookmark_id = ResourceLocation.parse(slot.getId());

                            if (slot.getIndex() < 5) {

                                x = -0.3f - altarTile.buttonScaleRender - 0.15f;
                                y = slot.getIndex() * 1.5f;
                                width = 0.935f;
                                if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                                    flag2 = true;
                                }
                            }
                            if (slot.getIndex() >= 5 && slot.getIndex() < 10) {

                                x = -5.5f + slot.getIndex() * 1.15f;
                                y = -0.75f - altarTile.buttonScaleRender - 0.25f;
                                width = 0.935f;
                                if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                                    flag2 = true;
                                }
                            }
                            if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                                x = -11.25f + slot.getIndex() * 1.15f;
                                y = -0.75f - altarTile.buttonScaleRender - 0.25f;
                                width = 0.935f;
                                if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)) {
                                    flag2 = true;
                                }
                            }
                            if (slot.getIndex() >= 15) {

                                x = 5.2f + altarTile.buttonScaleRender + 0.15f;
                                y = (slot.getIndex() - 15) * 1.5f;
                                width = 0.935f;
                                if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)) {
                                    flag2 = true;
                                }
                            }


                            if (flag2) {
                                if (altarTile.slotClicked == slot.getIndex()) {
                                    if (altarTile.slotClickedTick < 20) {
                                        //click the same bookmark
                                        for (BookChapter chapter : bookEntries.chapterList) {
                                            for (BookPageEntry pageEntry : chapter.pages) {
                                                if (ResourceLocation.parse(pageEntry.location).equals(bookmark_id)) {
                                                    altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                                    altarTile.slotClicked = -1;
                                                    altarTile.slotClickedTick = 0;
                                                    return true;
                                                }
                                            }
                                        }
                                        altarTile.setTurnPage(-1, bookmark_chapter, bookmark_page);
                                        altarTile.slotClicked = -1;
                                        altarTile.slotClickedTick = 0;
                                        return true;
                                    }
                                } else {
                                    //drag the bookmark to another slot
                                    altarTile.swapBookmarks(altarTile.slotClicked, slot.getIndex());
                                    altarTile.drawing.bookmarkHoverAmount.set(slot.getIndex(), 0f);
                                    altarTile.drawing.bookmarkHoverAmount.set(altarTile.slotClicked, 0f);
                                    altarTile.slotClicked = -1;
                                    altarTile.slotClickedTick = 0;
                                    return true;
                                }

                                break;
                            }

                        }
                    }
                }
            }

        }




        altarTile.slotClicked = -1;
        altarTile.slotClickedTick = 0;
        return false;
    }

    public static class ImageConverter {
        public static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = bufferedImage.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;

                    // Ensure the color values are properly handled
                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    nativeImage.setPixelRGBA(x, y, rgba);
                }
            }

            return nativeImage;
        }
        public static NativeImage convertToNativeImage(BufferedImage bufferedImage, NativeImage baseImage) {
            if (baseImage == null)
                return null;
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Get the dimensions of the base image
            int baseWidth = baseImage.getWidth();
            int baseHeight = baseImage.getHeight();

            // Determine the region where the BufferedImage will be pasted onto the base image
            int pasteWidth = Math.min(width, baseWidth);
            int pasteHeight = Math.min(height, baseHeight);

            // Iterate over the region and paste the BufferedImage onto the base image
            for (int y = 0; y < pasteHeight; y++) {
                for (int x = 0; x < pasteWidth; x++) {
                    int argb = bufferedImage.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    int b = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int r = argb & 0xFF;

                    // Ensure the color values are properly handled
                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    baseImage.setPixelRGBA(x, y, rgba);
                }
            }

            return baseImage;
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean checkClick(BookData bookData, BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, DrawingType drawingType) {

        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());

        if(bookEntries == null)
            return false;

        if (!isClicked) {
            float width = 0.86f;
            float x, y;


            // Bookmark
            x = -0.45f; y = -0.96f;
            if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                if (altarTile.currentBook.getChapter() != 0) {
                    altarTile.clickPageBookmark(altarTile.currentBook.getChapter(), altarTile.currentBook.getPage());
                    return true;
                }
            }

            // Back
            x = -0.45f; y = 7.2f;
            if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {

                if (altarTile.slotClicked == -1 && PageDrawingEvents.clickedBack(altarTile)) {
                    altarTile.setTurnPage(2);
                    return true;
                } else if (altarTile.slotClicked == -1) {

                    ClientProxy.fontIndex++;
                    return true;
                }
            }

            // Home
            x = -0.25f; y = -0.5f;
            if (canInteract(rightCursorX, rightCursorY, x - width / 2, y - width / 2, width, width, altarTile, drawingType)) {
                altarTile.setTurnPage(-1, 0, 0);
                return true;
            }

            // Close
            x = -0.25f; y = 7.5f;
            if (canInteract(rightCursorX, rightCursorY, x - width / 2, y - width / 2, width, width, altarTile, drawingType)) {
                altarTile.setTurnPage(-2);
                return true;
            }

            // GUI
            x = 5.49f; y = -0.97f;
            if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType) && Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new BookOfShadowsScreen(altarTile));
                return true;
            }

            // Next
            x = 5.415f; y = 7.2f;
            if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)) {
                if (altarTile.slotClicked == -1 && PageDrawingEvents.clickedNext(altarTile)) {
                    altarTile.setTurnPage(1);
                    return true;
                }
            }

            for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                if (!slot.getId().isEmpty()) {

                    if (slot.getIndex() < 5) {

                        float xIn = -0.3f - altarTile.buttonScaleRender - 0.15f;
                        float yIn = slot.getIndex() * 1.5f;
                        width = 0.935f;
                        if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                            altarTile.slotClicked = slot.getIndex();
                            return true;
                        }
                    }
                    if (slot.getIndex() >= 5 && slot.getIndex() < 10) {

                        float xIn = -5.5f + slot.getIndex() * 1.15f;
                        float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;
                        width = 0.935f;
                        if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                            altarTile.slotClicked = slot.getIndex();
                            return true;
                        }
                    }
                    if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                        float xIn = -11.25f + slot.getIndex() * 1.15f;
                        float yIn = -0.75f - altarTile.buttonScaleRender - 0.25f;
                        width = 0.935f;
                        if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                            altarTile.slotClicked = slot.getIndex();
                            return true;
                        }
                    }
                    if (slot.getIndex() >= 15) {

                        float xIn = 5.2f + altarTile.buttonScaleRender + 0.15f;
                        float yIn = (slot.getIndex() - 15) * 1.5f;
                        width = 0.935f;
                        if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                            altarTile.slotClicked = slot.getIndex();
                            return true;
                        }
                    }
                }
            }
        }


        String location1 = "";
        String location2 = "";

        int chapter = bookData.getChapter();
        int page = bookData.getPage();
        if (page % 2 == 1)
            page--;

        if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0)
            location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
        if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0)
            location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

        BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location1));
        BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location2));

        if (page1 != null) {

            if (!isClicked) {

                for (BookPaintElement paintElement : page1.paintElements) {

                    if (paintElement.client == null)
                        continue;

                    PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());

                    float cursorX = leftCursorX;
                    float cursorY = leftCursorY;

                    float w;
                    float h;
                    float x;
                    float y;

                    if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BOOK_CANVAS.get()) {

                        w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
                        h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
                        x = paintElement.x + 0.025f;
                        y = paintElement.y - 0.5f;
                        if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {

                            Minecraft.getInstance().setScreen(new CanvasPaintingCropScreen(paintElement, paintSystem));
                            return true;
                        }
                    }


                    if (paintSystem.getMovingSelection() == null){
                        for (PaintSystem.Button button : paintSystem.buttons) {
                            if(!button.isVisible(paintSystem))
                                continue;
                            BookImage image = new BookImage(button.getX(paintSystem, PageOn.LEFT_PAGE, 0), button.getY(paintSystem, PageOn.LEFT_PAGE, 0), 0, 0, 0, button.width, button.height, button.width, button.height, button.getScale(altarTile.buttonScaleRender), button.getTexture(paintSystem), new ArrayList<>());

                            w = image.width / 330 * image.scale / 0.062f;
                            h = image.height / 330 * image.scale / 0.062f;
                            x = image.x - w / 2 + 0.455f;
                            y = image.y - h / 2 + 0.49f;

                            if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                                if (!button.getDisabled(paintSystem)) {
                                    button.onClick(paintSystem);
                                    button.clicked = true;
                                    return true;
                                } else
                                    return false;
                            }
                        }


                        if (paintSystem.toolsVisible) {
                            for (int i = 0; i < 3; i++) {
                                if (i >= paintSystem.getColors().colors.size())
                                    continue;
                                PaintSystem.Colors.ColorSelection colorSelection = paintSystem.getColors().colors.get(i);

                                float w1 = colorSelection.colorPosData.width / 326 * 2.55f / 0.062f;
                                float h1 = colorSelection.colorPosData.height / 326 * 2.55f / 0.062f;
                                float x1 = (float) colorSelection.colorPosData.pos.x + 0.025f - w1 / 2;
                                float y1 = (float) colorSelection.colorPosData.pos.y - 0.5f - 0.025f - h1 / 2;
                                if (canInteract(cursorX, cursorY, x1, y1, w1, h1, altarTile, drawingType)) {
                                    if (i == 2)
                                        paintSystem.getColors().cycleColorBack(paintSystem);
                                    else
                                        paintSystem.getColors().cycleColor(paintSystem);
                                    return true;
                                }

                            }
                        }

                        if (paintSystem.toolsVisible)
                            if (paintSystem.getValueSliders().click(cursorX, cursorY, PageOn.LEFT_PAGE))
                                return true;
                    }


                    if (paintSystem.toolsVisible) {
                        w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
                        h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
                        x = paintElement.x + 0.025f;
                        y = paintElement.y - 0.5f;
                        float cushion = paintSystem.getBrush().size * 0.12f;
                        if (canInteract(cursorX, cursorY, x - cushion, y - cushion, w + cushion * 2, h + cushion * 2, altarTile, drawingType)) {

                            float xPixel = ((cursorX - x) / w * paintElement.width);
                            float yPixel = ((cursorY - y) / h * paintElement.height);
                            paintSystem.click(xPixel, yPixel);
                            return true;
                        }
                    }

                }

                for (BookWritableTextBox bookWritableTextBox : page1.writableTextBoxes) {

                    if (canInteract(leftCursorX, leftCursorY, bookWritableTextBox.paragraphElement.x + 0.45f, bookWritableTextBox.paragraphElement.y, bookWritableTextBox.paragraphElement.width / 6.15f, bookWritableTextBox.paragraphElement.height / 2.57f, altarTile, drawingType)) {
                        setFocusedWritableTextBox(altarTile, page1.location, bookWritableTextBox);

                        long i = Util.getMillis();
                        BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = focusedWritableTextBox.getRight().client.getDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook);
                        BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i((int) ((leftCursorX - bookWritableTextBox.paragraphElement.x - 0.45f) / 5 * 115f), (int) ((leftCursorY - bookWritableTextBox.paragraphElement.y) / 7.1f * 162f));
                        int j = bookeditscreen$displaycache.getIndexAtPosition(
                                ClientProxy.font(), pos2i
                        );
                        bookWritableTextBox.client.clicked = true;
                        bookWritableTextBox.client.clickedPos = pos2i;
                        if (j >= 0) {
                            if (j != focusedWritableTextBox.getRight().client.lastIndex || i - focusedWritableTextBox.getRight().client.lastClickTime >= 250L) {
                                focusedWritableTextBox.getRight().client.pageEdit.setCursorPos(j, Screen.hasShiftDown());
                            } else if (!focusedWritableTextBox.getRight().client.pageEdit.isSelecting()) {
                                focusedWritableTextBox.getRight().client.selectWord(j, focusedWritableTextBox.getLeft().currentBook);
                            } else {
                                focusedWritableTextBox.getRight().client.pageEdit.selectAll();
                            }

                            focusedWritableTextBox.getRight().client.clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
                        }

                        focusedWritableTextBox.getRight().client.lastIndex = j;
                        focusedWritableTextBox.getRight().client.lastClickTime = i;

                        return true;
                    }
                }
                for (BookNonItemTooltip bookNonItemTooltip : page1.nonItemTooltipList) {

                    if (bookNonItemTooltip.hyperlink.id.isEmpty() && bookNonItemTooltip.hyperlink.url.isEmpty())
                        continue;

                    if (canInteract(leftCursorX, leftCursorY, bookNonItemTooltip.x, bookNonItemTooltip.y, bookNonItemTooltip.width, bookNonItemTooltip.height, altarTile, drawingType)) {
                        if (!bookNonItemTooltip.hyperlink.url.isEmpty())
                            showLinkScreenClient(bookNonItemTooltip.hyperlink.url);
                        if (!bookNonItemTooltip.hyperlink.id.isEmpty()) {
                            for (BookChapter chapterEntry : bookEntries.chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (pageEntry.location.equals(bookNonItemTooltip.hyperlink.id)) {
                                        altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                for (BookItemsAndFluids bookItemStackInSlot : page1.itemList) {

                    if (canInteract(leftCursorX, leftCursorY, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86f, 0.86f, altarTile, drawingType)) {
                        String itemRegistryName;

                        if (bookItemStackInSlot.item != null)
                            itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.item.getItem()).toString();
                        else
                            itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.fluid.getFluid()).toString();

                        boolean flag = false;
                        if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
                            BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                            if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                altarTile.setTurnPage(-1, hyperlink.chapter, hyperlink.page);
                            flag = true;
                        }
                        if (!flag) {
                            for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                    String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                    BookPage page_check = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location3));
                                    if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                        if (!(chapter == j && (page == k || page == k - 1)))
                                            altarTile.setTurnPage(-1, j, k);
                                        BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

                for (BookImage bookImage : page1.imageList) {


                    float w = bookImage.width / 330 * bookImage.scale / 0.062f;
                    float h = bookImage.height / 330 * bookImage.scale / 0.062f;
                    float x = bookImage.x - w / 2 + 0.45f;
                    float y = bookImage.y - h / 2 + 0.49f;
                    if (canInteract(leftCursorX, leftCursorY, x, y, w, h, altarTile, drawingType)) {

                        if (!bookImage.hyperlink.url.isEmpty())
                            showLinkScreenClient(bookImage.hyperlink.url);
                        if (!bookImage.hyperlink.id.isEmpty()) {

                            for (BookChapter chapterEntry : bookEntries.chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (pageEntry.location.equals(bookImage.hyperlink.id)) {
                                        altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }


                if (altarTile.slotClicked == -1) {
                    for (BookEntity bookEntity : page1.entityList) {

                        float xIn = bookEntity.x + bookEntity.offset.x + 0.52f;
                        float yIn = bookEntity.y + bookEntity.offset.y;
                        float width = 1.25f + bookEntity.scale / 5f;
                        if (canInteract(leftCursorX, leftCursorY, xIn - width/2, yIn - width/2, width, width, altarTile, drawingType)) {
                            bookEntity.clicked = true;
                            return true;
                        }
                    }
                }
            }

        }
        if (page2 != null) {

            if (!isClicked) {


                for (BookPaintElement paintElement : page2.paintElements) {

                    if (paintElement.client == null)
                        continue;

                    PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());

                    float cursorX = rightCursorX;
                    float cursorY = rightCursorY;

                    float w;
                    float h;
                    float x;
                    float y;

                    if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BOOK_CANVAS.get()) {

                        w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
                        h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
                        x = paintElement.x + 0.025f;
                        y = paintElement.y - 0.5f;
                        if (canInteract(cursorX + 0.22f, cursorY, x, y, w, h, altarTile, drawingType)) {

                            Minecraft.getInstance().setScreen(new CanvasPaintingCropScreen(paintElement, paintSystem));
                            return true;
                        }
                    }

                    if (paintSystem.getMovingSelection() == null) {
                        for (PaintSystem.Button button : paintSystem.buttons) {
                            if(!button.isVisible(paintSystem))
                                continue;
                            BookImage image = new BookImage(button.getX(paintSystem, PageOn.RIGHT_PAGE, 0), button.getY(paintSystem, PageOn.RIGHT_PAGE, 0), 0, 0, 0, button.width, button.height, button.width, button.height, button.getScale(altarTile.buttonScaleRender), button.getTexture(paintSystem), new ArrayList<>());

                            w = image.width / 330 * image.scale / 0.062f;
                            h = image.height / 330 * image.scale / 0.062f;
                            x = image.x - w / 2 + 0.455f;
                            y = image.y - h / 2 + 0.49f;

                            if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                                if (!button.getDisabled(paintSystem)) {
                                    button.onClick(paintSystem);
                                    button.clicked = true;
                                    return true;
                                } else
                                    return false;
                            }
                        }


                        if (paintSystem.toolsVisible) {
                            for (int i = 0; i < 3; i++) {
                                if (i >= paintSystem.getColors().colors.size())
                                    continue;
                                PaintSystem.Colors.ColorSelection colorSelection = paintSystem.getColors().colors.get(i);

                                float w1 = colorSelection.colorPosData.width / 326 * 2.55f / 0.062f;
                                float h1 = colorSelection.colorPosData.height / 326 * 2.55f / 0.062f;
                                float x1 = (float) colorSelection.colorPosData.pos.x + 0.025f - w1 / 2 + 0.8f;
                                float y1 = (float) colorSelection.colorPosData.pos.y - 0.5f - 0.025f - h1 / 2;
                                if (canInteract(cursorX, cursorY, x1, y1, w1, h1, altarTile, drawingType)) {
                                    if (i == 2)
                                        paintSystem.getColors().cycleColorBack(paintSystem);
                                    else
                                        paintSystem.getColors().cycleColor(paintSystem);
                                    return true;
                                }

                            }
                        }

                        if (paintSystem.toolsVisible)
                            if (paintSystem.getValueSliders().click(cursorX, cursorY, PageOn.RIGHT_PAGE))
                                return true;
                    }


                    if (paintSystem.toolsVisible) {
                        w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
                        h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
                        x = paintElement.x + 0.025f;// + 0.455f;
                        y = paintElement.y - 0.5f;
                        float cushion = paintSystem.getBrush().size * 0.12f;
                        if (canInteract(cursorX + 0.22f, cursorY, x - cushion, y - cushion, w + cushion * 2, h + cushion * 2, altarTile, drawingType)) {

                            float xPixel = ((cursorX - x) / w * paintElement.width) + 1.8f;
                            float yPixel = ((cursorY - y) / h * paintElement.height);
                            paintSystem.click(xPixel, yPixel);
                            return true;
                        }
                    }

                }

                for (BookWritableTextBox bookWritableTextBox : page2.writableTextBoxes) {

                    if (canInteract(rightCursorX, rightCursorY, bookWritableTextBox.paragraphElement.x + 0.45f, bookWritableTextBox.paragraphElement.y, bookWritableTextBox.paragraphElement.width / 6.15f, bookWritableTextBox.paragraphElement.height / 2.57f, altarTile, drawingType)) {
                        setFocusedWritableTextBox(altarTile, page2.location, bookWritableTextBox);

                        long i = Util.getMillis();
                        BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = focusedWritableTextBox.getRight().client.getDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook);
                        BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i((int) ((rightCursorX - bookWritableTextBox.paragraphElement.x - 0.45f) / 5 * 115f), (int) ((rightCursorY - bookWritableTextBox.paragraphElement.y) / 7.1f * 162f));
                        int j = bookeditscreen$displaycache.getIndexAtPosition(
                                ClientProxy.font(), pos2i
                        );
                        bookWritableTextBox.client.clicked = true;
                        bookWritableTextBox.client.clickedPos = pos2i;
                        if (j >= 0) {
                            if (j != focusedWritableTextBox.getRight().client.lastIndex || i - focusedWritableTextBox.getRight().client.lastClickTime >= 250L) {
                                focusedWritableTextBox.getRight().client.pageEdit.setCursorPos(j, Screen.hasShiftDown());
                            } else if (!focusedWritableTextBox.getRight().client.pageEdit.isSelecting()) {
                                focusedWritableTextBox.getRight().client.selectWord(j, focusedWritableTextBox.getLeft().currentBook);
                            } else {
                                focusedWritableTextBox.getRight().client.pageEdit.selectAll();
                            }

                            focusedWritableTextBox.getRight().client.clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
                        }

                        focusedWritableTextBox.getRight().client.lastIndex = j;
                        focusedWritableTextBox.getRight().client.lastClickTime = i;

                        return true;
                    }
                }

                for (BookNonItemTooltip bookNonItemTooltip : page2.nonItemTooltipList) {

                    if (bookNonItemTooltip.hyperlink.id.isEmpty() && bookNonItemTooltip.hyperlink.url.isEmpty())
                        continue;

                    if (canInteract(rightCursorX, rightCursorY, bookNonItemTooltip.x, bookNonItemTooltip.y, bookNonItemTooltip.width, bookNonItemTooltip.height, altarTile, drawingType)) {
//                            if (intersectPointNonItem(bookNonItemTooltip.x, bookNonItemTooltip.y, bookNonItemTooltip.width, bookNonItemTooltip.height, playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageOn.RIGHT_PAGE) &&
//                                    Math.sqrt(altarTile.getBlockPos().distToCenterSqr(playerIn.getEyePosition())) <= reach) {

                        if (!bookNonItemTooltip.hyperlink.url.isEmpty())
                            showLinkScreenClient(bookNonItemTooltip.hyperlink.url);
                        if (!bookNonItemTooltip.hyperlink.id.isEmpty()) {

                            for (BookChapter chapterEntry : bookEntries.chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (pageEntry.location.equals(bookNonItemTooltip.hyperlink.id)) {
                                        altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                for (BookItemsAndFluids bookItemStackInSlot : page2.itemList) {

                    if (canInteract(rightCursorX, rightCursorY, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86f, 0.86f, altarTile, drawingType)) {
//                            if (intersectPointItems(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageOn.RIGHT_PAGE) &&
//                                    Math.sqrt(altarTile.getBlockPos().distToCenterSqr(playerIn.getEyePosition())) <= reach) {

                        String itemRegistryName;

                        if (bookItemStackInSlot.item != null)
                            itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.item.getItem()).toString();
                        else
                            itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.fluid.getFluid()).toString();

                        boolean flag = false;
                        if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
//                                System.out.println("Found previous hyperlink");
                            BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                            if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                altarTile.setTurnPage(-1, hyperlink.chapter, hyperlink.page);
                            flag = true;
                        }
                        if (!flag) {
                            for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                    String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                    BookPage page_check = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location3));
                                    if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                        if (!(chapter == j && (page == k || page == k - 1)))
                                            altarTile.setTurnPage(-1, j, k);
                                        BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

                for (BookImage bookImage : page2.imageList) {

                    if (bookImage.hyperlink.id.isEmpty() && bookImage.hyperlink.url.isEmpty())
                        continue;



                    float w = bookImage.width / 330 * bookImage.scale / 0.062f;
                    float h = bookImage.height / 330 * bookImage.scale / 0.062f;
                    float x = bookImage.x - w / 2 + 0.45f;
                    float y = bookImage.y - h / 2 + 0.49f;
                    if (canInteract(rightCursorX, rightCursorY, x, y, w, h, altarTile, drawingType)) {
//                            if (intersectPointImage(bookImage.x, bookImage.y, bookImage.height, bookImage.width, bookImage.scale, playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageOn.RIGHT_PAGE) &&
//                                    Math.sqrt(altarTile.getBlockPos().distToCenterSqr(playerIn.getEyePosition())) <= reach) {


                        if (!bookImage.hyperlink.url.isEmpty())
                            showLinkScreenClient(bookImage.hyperlink.url);
                        if (!bookImage.hyperlink.id.isEmpty()) {

                            for (BookChapter chapterEntry : bookEntries.chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (pageEntry.location.equals(bookImage.hyperlink.id)) {
                                        altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                        return true;
                                    }
                                }
                            }
                        }
                        break;

                        //add hyperlink stuff here
//                            loc.set(bookImageEffect.hoverImage.imageLoc);
                    }
                }
            }


            if (altarTile.slotClicked == -1) {
                for (BookEntity bookEntity : page2.entityList) {

                    float xIn = bookEntity.x + bookEntity.offset.x + 0.52f;
                    float yIn = bookEntity.y + bookEntity.offset.y;
                    float width = 1.25f + bookEntity.scale / 5f;
                    if (canInteract(rightCursorX, rightCursorY, xIn - width/2, yIn - width/2, width, width, altarTile, drawingType)) {
                        bookEntity.clicked = true;
//                            if (canInteract(leftOffset, rightOffset, xIn - width / 2f, yIn - width / 2f, width, width, playerIn, altarTile, PageOn.RIGHT_PAGE)) {
//                                if (!isRightPressedOld) {
//                                    playerIn.swing(InteractionHand.MAIN_HAND);
//                                    Hexerei.entityClicked = true;
//                                }
                        return true;
                    }
                }
            }

        }


        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void showLinkScreenClient(String link) {
        ConfirmLinkScreen screen = new ConfirmLinkScreen((p_169232_) -> {
            if (p_169232_) {
                Util.getPlatform().openUri(link);
            }
            Minecraft.getInstance().setScreen(null);
        }, link, true);

        Minecraft.getInstance().setScreen(screen);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawSlot(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        poseStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        poseStack.translate(xIn / 8.1f, yIn / 8.1f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/slot.png")));

//        poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = poseStack.last();
        int u = 0;
        int v = 0;
        int imageWidth = 32;
        int imageHeight = 32;
        int width = 18;
        int height = 18;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 125).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 125).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 125).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 125).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource source)
            source.endBatch();

        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawFluidInSlot(BookOfShadowsAltarTile altarTile, @NotNull BookItemsAndFluids bookItemsAndFluids, PoseStack poseStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {

        poseStack.pushPose();
        FluidStack stack = bookItemsAndFluids.fluid;
        int capacity = bookItemsAndFluids.capacity;
        boolean showSlot = bookItemsAndFluids.show_slot;
        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        poseStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        poseStack.translate(xIn / 8.1f, yIn / 8.1f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        Matrix4f matrix = poseStack.last().pose();
        if (showSlot) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/slot.png")));

//            poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
            PoseStack.Pose normal = poseStack.last();
            int u = 0;
            int v = 0;
            int imageWidth = 18;
            int imageHeight = 18;
            int width = 18;
            int height = 18;
            float u1 = (u + 0.0F) / (float) imageWidth;
            float u2 = (u + (float) width) / (float) imageWidth;
            float v1 = (v + 0.0F) / (float) imageHeight;
            float v2 = (v + (float) height) / (float) imageHeight;

            buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        }
        drawFluid(poseStack, bufferSource, (int) bookItemsAndFluids.fluid_width, (int) bookItemsAndFluids.fluid_height, stack, capacity, light, overlay, bookItemsAndFluids.fluid_offset_x, bookItemsAndFluids.fluid_offset_y, bookItemsAndFluids.fluid_width, bookItemsAndFluids.fluid_height);

        poseStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    private void drawFluid(PoseStack poseStack, MultiBufferSource bufferSource, final int tiledWidth, final int tiledHeight, FluidStack fluidStack, int capacity, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        Fluid fluid = fluidStack.getFluid();

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

//        FluidType attributes = fluid.getFluidType();
//        int fluidColor = attributes.getColor(fluidStack);
        int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(fluidStack);

        int amount = fluidStack.getAmount();
//        int amount = (int)Math.abs((Math.sin(ClientEvents.getClientTicks() / 100) * 2000));
        if (amount == 0)
            amount = capacity > 0 ? capacity : 1000;
        int scaledAmount = (amount * tiledHeight) / (capacity != 0 ? capacity : 1000);
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > tiledHeight) {
            scaledAmount = tiledHeight;
        }
        if (capacity == 0)
            scaledAmount = tiledHeight;

        drawTiledSprite(poseStack, bufferSource, tiledWidth, tiledHeight, fluidColor, scaledAmount, fluidStillSprite, capacity, amount, light, overlay, x_offset, y_offset, width, height);

    }

    @OnlyIn(Dist.CLIENT)
    private static void drawTiledSprite(PoseStack poseStack, MultiBufferSource bufferSource, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int capacity, int amount, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final int yTileCount = scaledAmount / TEXTURE_SIZE;
        final int yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width2 = (xTile == xTileCount) ? xRemainder : (int) width;
                int height2 = (yTile == yTileCount) ? yRemainder : (int) height;
//                if(capacity > 0 && capacity >= amount)
//                    height2 *= ((float)amount / (float)capacity);
                int x_tile = (xTile * TEXTURE_SIZE);
                int y_tile = tiledHeight - ((yTile + 1) * (int) height);
                if (width2 > 0 && height2 > 0) {
                    int maskTop = (int) height - height2;
                    int maskRight = (int) width - width2;

                    drawTextureWithMasking(poseStack, bufferSource, capacity, amount, x_tile, y_tile, sprite, color, maskTop, maskRight, 1, light, overlay, x_offset, y_offset, width, height);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        if (fluidStack.isEmpty())
            return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("missingno"));
        Fluid fluid = fluidStack.getFluid();
        ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack);
        return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }

    @OnlyIn(Dist.CLIENT)
    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawTextureWithMasking(PoseStack poseStack, MultiBufferSource bufferSource, int capacity, int amount, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int color, int maskTop, int maskRight, float zLevel, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        uMax = uMax - (maskRight / width * (uMax - uMin));
        vMax = vMax - (maskTop / height * (vMax - vMin));

        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        poseStack.pushPose();
        poseStack.translate(0.001f, 0.0485f + (y_offset * 0.005975f), (x_offset * 0.005975f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose normal = poseStack.last();
        poseStack.popPose();


        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        buffer.addVertex(matrix, 0, -0.055f / 18 * (width), 0).setColor(red, green, blue, alpha).setUv(uMin, vMax).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * (width), 0).setColor(red, green, blue, alpha).setUv(uMax, vMax).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * (width), 0.055f / 9 * (height - maskTop)).setColor(red, green, blue, alpha).setUv(uMax, vMin).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * (width), 0.055f / 9 * (height - maskTop)).setColor(red, green, blue, alpha).setUv(uMin, vMin).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

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


//        double c1 = 1.70158;
//        double c2 = c1 * 1.525;
//
//        return x == 0
//                ? 0
//                : x == 1
//                ? 1
//                : x < 0.5
//                ? (float)((1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2)
//                : (float)((Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTooltipImage(ItemStack stack, BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, float partialTicks) {

        poseStack.pushPose();

        poseStack.translate(8f / 16f, 18f / 16f, 8f / 16f);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(270));
        float scale = easeInOutElastic(Mth.lerp(partialTicks, this.drawTooltipScaleOld, this.drawTooltipScale));
        poseStack.translate(0.05f + 0.1f * scale, -(1 - (this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1)) / 12f, 0);
        poseStack.scale(scale, scale, scale);

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        this.tooltipStack = stack;
        if (!this.tooltipStack.isEmpty()) {
            List<Component> tooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);

            if (!tooltip.isEmpty())
                tooltip.addAll(this.tooltipText);

            String modId = HexereiUtil.getRegistryName(this.tooltipStack.getItem()).getNamespace();
            String modName = getModNameForModId(modId);
            MutableComponent modNameComponent = Component.translatable(modName);
            modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
            if (tooltip.isEmpty() || !tooltip.getLast().getString().equals(modName))
                tooltip.add(modNameComponent);

            List<Component> list = new ArrayList<>(this.tooltipText);
            list.addFirst(Component.translatable(""));
            this.renderTooltip(this.tooltipStack, bufferSource, poseStack, tooltip, stack.getTooltipImage(), 0, 0, overlay, light);
        }

        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public static List<Component> getFluidTooltip(BookItemsAndFluids bookItemStackInSlot) {
        FluidStack fluidStack = bookItemStackInSlot.fluid;
        int capacity = bookItemStackInSlot.capacity;
        int amount = bookItemStackInSlot.amount;
        List<Component> tooltip = new ArrayList<>();
        Fluid fluidType = fluidStack.getFluid();

        MutableComponent displayName = (MutableComponent) fluidStack.getHoverName();
        displayName.withStyle(ChatFormatting.WHITE);
        tooltip.add(displayName);
        if (capacity != 0) {
            MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount.with.capacity", nf.format(amount), nf.format(capacity));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        } else if (amount != 0) {
            MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount", nf.format(amount));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        }

        if (!bookItemStackInSlot.extra_tooltips.isEmpty())
            tooltip.addAll(bookItemStackInSlot.extra_tooltips);


        String modId = HexereiUtil.getRegistryName(fluidStack.getFluid()).getNamespace();
        String modName = getModNameForModId(modId);
        MutableComponent modNameComponent = Component.translatable(modName);
        modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
        tooltip.add(modNameComponent);

        return tooltip;
    }


    @OnlyIn(Dist.CLIENT)
    public void drawTooltipText(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, float partialTicks) {

        poseStack.pushPose();

        poseStack.translate(8f / 16f, 18f / 16f, 8f / 16f);
        poseStack.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
        poseStack.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
        poseStack.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2 + 45)));
        poseStack.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
        poseStack.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(270));
        float scale = easeInOutElastic(Mth.lerp(partialTicks, this.drawTooltipScaleOld, this.drawTooltipScale));
        poseStack.translate(0.05f + 0.1f * scale, -(1 - (Math.min(this.drawTooltipScale, 1) < 0.5f ? Math.min(this.drawTooltipScale, 1) * 2f : 1)) / 12f, 0);

        if (scale < 0) scale = 0;
        poseStack.scale(scale, scale, scale);


//        poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);

        List<Component> list = new ArrayList<>(this.tooltipText);
        list.addFirst(Component.translatable(""));
        this.renderTooltip(this.tooltipStack, bufferSource, poseStack, this.tooltipText, Optional.empty(), 0, 0, overlay, light);

        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void renderTooltip(ItemStack stack, MultiBufferSource buffer, PoseStack p_169389_, List<Component> components, Optional<TooltipComponent> p_169391_, int p_169392_, int p_169393_, int overlay, int light) {
        List<ClientTooltipComponent> list = new ArrayList<>();
        List<ClientTooltipComponent> list2 = new ArrayList<>();
        try {
            list = ClientHooks.gatherTooltipComponents(stack, components, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
            List<Component> newComponentList = new ArrayList<>();
            for (Component component : components) {
                newComponentList.add(Component.translatable(component.getString()).withStyle(component.getStyle().withColor(0x292929)));
            }
            list2 = ClientHooks.gatherTooltipComponents(stack, newComponentList, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
        } catch (RuntimeException e) {
//            shrug
        }
        this.renderTooltipInternal(buffer, p_169389_, list, list2, p_169392_, p_169393_, overlay, light);
    }


    @OnlyIn(Dist.CLIENT)
    private void renderTooltipInternal(MultiBufferSource bufferSource, PoseStack poseStack, List<ClientTooltipComponent> clientTooltipComponentList, List<ClientTooltipComponent> clientTooltipComponentList2, int p_169386_, int p_169387_, int overlay, int light) {
        if (!clientTooltipComponentList.isEmpty()) {

            RenderTooltipEvent.Pre preEvent = ClientHooks.onRenderTooltipPre(this.tooltipStack, new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource), p_169386_, p_169387_, 750, 750, clientTooltipComponentList, Minecraft.getInstance().font, DefaultTooltipPositioner.INSTANCE);
            if (preEvent.isCanceled()) {
                return;
            }

            int i = 0;
            int j = clientTooltipComponentList.size() == 1 ? -2 : 0;

            ClientTooltipComponent clientTooltipComponent;
            int l;
            for (Iterator<ClientTooltipComponent> var8 = clientTooltipComponentList.iterator(); var8.hasNext(); j += clientTooltipComponent.getHeight()) {
                clientTooltipComponent = var8.next();
                l = clientTooltipComponent.getWidth(preEvent.getFont());
                if (l > i) {
                    i = l;
                }
            }

            int j2 = preEvent.getX() + 12;
            int k2 = preEvent.getY() - 12;
            if (j2 + i > 750) {
                j2 -= 28 + i;
            }

            if (k2 + j + 6 > 750) {
                k2 = 750 - j - 6;
            }

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));

            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.scale(0.003f, 0.003f, 0.003f);
            poseStack.translate(-(i + 15) / 2f, -(j + 15) / 2f, -10);


            RenderTooltipEvent.Color colorEvent = ClientHooks.onRenderTooltipColor(this.tooltipStack, new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource), j2, k2, preEvent.getFont(), clientTooltipComponentList);
            fillGradient(poseStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 0.2f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(poseStack, buffer, j2 - 3, k2 - 4, j2 + i + 3, k2 - 2, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart(), overlay, light);
            fillGradient(poseStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 4, 0.1f, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(poseStack, buffer, j2 - 4, k2 - 3, j2 - 2, k2 + j + 3, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(poseStack, buffer, j2 + i + 2, k2 - 3, j2 + i + 4, k2 + j + 3, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            ((MultiBufferSource.BufferSource) bufferSource).endBatch();
            buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            fillGradient(poseStack, buffer, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(poseStack, buffer, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(poseStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderStart(), overlay, light);
            fillGradient(poseStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 0, colorEvent.getBorderEnd(), colorEvent.getBorderEnd(), overlay, light);
            RenderSystem.enableDepthTest();

            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            poseStack.translate(0.0D, 0.0D, 0.01D);

            poseStack.scale(1, 1, 0.00001f);
            int l1 = k2;

            Matrix4f matrix4f = poseStack.last().pose();
            int l2;
            ClientTooltipComponent clientTooltipComponent2;
            for (l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = clientTooltipComponentList.get(l2);
                if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip) {
                    hexereiBookTooltip.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource, overlay, light);
                }
                else if (clientTooltipComponent2 instanceof ClientTextTooltip clientTextTooltip) {
                    int r = (int) (0.25f * 255.0f);
                    int g = (int) (0.25f * 255.0f);
                    int b = (int) (0.25f * 255.0f);
                    int a = (int) (1 * 255.0F);


                    int col = (a << 24) | (r << 16) | (g << 8) | b;
                    Font font = preEvent.getFont();
                    matrix4f = poseStack.last().pose();
                    font.drawInBatch(clientTextTooltip.text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, light);
                    poseStack.pushPose();
                    poseStack.translate(0.5f, 0.5f, 7500);
                    matrix4f = poseStack.last().pose();
                    font.drawInBatch(((ClientTextTooltip) clientTooltipComponentList2.get(l2)).text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, light);
                    poseStack.popPose();
                }
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

            multibuffersource$buffersource.endBatch();
            l1 = k2;

            poseStack.scale(1, 1, 333.333f);
            for (l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = clientTooltipComponentList.get(l2);
                RenderSystem.enableDepthTest();
                if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip)
                    hexereiBookTooltip.renderImage(preEvent.getFont(), bufferSource, j2, l1, poseStack, itemRenderer, 0, overlay, light);
//                else
//                    clientTooltipComponent2.renderImage(preEvent.getFont(), j2, l1, poseStack, this.itemRenderer, 0);
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

        }

    }

    private static int adjustColor(int p_92720_) {
        return (p_92720_ & -67108864) == 0 ? p_92720_ | -16777216 : p_92720_;
    }

    protected static void fillGradient(PoseStack poseStack, VertexConsumer buffer, int p_93126_, int p_93127_, int p_93128_, int p_93129_, float p_93130_, int pColorFrom, int pColorTo, int overlay, int light) {

        float fromAlpha = (float) FastColor.ARGB32.alpha(pColorFrom) / 255.0F * 0.9f;
        float f1 = (float)FastColor.ARGB32.red(pColorFrom) / 255.0F;
        float f2 = (float)FastColor.ARGB32.green(pColorFrom) / 255.0F;
        float f3 = (float)FastColor.ARGB32.blue(pColorFrom) / 255.0F;
        float toAlpha = (float)FastColor.ARGB32.alpha(pColorTo) / 255.0F * 0.9f;
        float f5 = (float)FastColor.ARGB32.red(pColorTo) / 255.0F;
        float f6 = (float)FastColor.ARGB32.green(pColorTo) / 255.0F;
        float f7 = (float)FastColor.ARGB32.blue(pColorTo) / 255.0F;

        PoseStack.Pose normal = poseStack.last();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth = 1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix4f, p_93128_, p_93127_, p_93130_).setColor(f1, f2, f3, fromAlpha).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93126_, p_93127_, p_93130_).setColor(f1, f2, f3, fromAlpha).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93126_, p_93129_, p_93130_).setColor(f5, f6, f7, toAlpha).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93128_, p_93129_, p_93130_).setColor(f5, f6, f7, toAlpha).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);


    }


    @OnlyIn(Dist.CLIENT)
    public void drawBookmark(BookImage bookImage, BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, float rotate, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType) {

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * bookImage.scale, 0.5f * bookImage.scale, 0.5f * bookImage.scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((bookImage.x / 8.1f - 0.03f / 16f) / bookImage.scale, (bookImage.y / 8.1f - 0.053f / 16f) / bookImage.scale, (drawingType == DrawingType.SCREEN ? -5f - zLevel : -zLevel) / 1600f / bookImage.scale);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("scale")) {

                float f = bookImageEffect.amount - 1;

                float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((ClientEvents.getClientTicks()) / bookImageEffect.speed)));
                poseStack.scale(x, x, x);
            }
        }));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("tilt")) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((ClientEvents.getClientTicks()) / bookImageEffect.speed)));
            }
        }));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotate));
        if (transformType != ItemDisplayContext.NONE)
            poseStack.mulPose(Axis.ZP.rotationDegrees(-35));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(bookImage.texture)));

        PoseStack.Pose normal = poseStack.last();
        int u = (int) bookImage.u;
        int v = (int) bookImage.v;
        int imageWidth = (int) bookImage.imageWidth;
        int imageHeight = (int) bookImage.imageHeight;
        int width = (int) bookImage.width;
        int height = (int) bookImage.height;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }


        if (transformType != ItemDisplayContext.NONE) {
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

            buffer.addVertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        } else {
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        }
        poseStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {
        drawImage(bookImage, altarTile, cursorX, cursorY, poseStack, bufferSource, zLevel, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE);
    }
    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType) {
        drawImage(bookImage, altarTile, cursorX, cursorY, poseStack, bufferSource, zLevel, light, overlay, pageOn, color, drawingType, transformType, false);
    }
    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType, boolean transparent) {

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);//
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * bookImage.scale, 0.5f * bookImage.scale, 0.5f * bookImage.scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((bookImage.x / 8.1f - 0.03f / 16f) / bookImage.scale, (bookImage.y / 8.1f - 0.053f / 16f) / bookImage.scale, -(zLevel + bookImage.z) / 1600f);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("scale")) {

                float f = bookImageEffect.amount - 1;

                float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((ClientEvents.getClientTicks()) / bookImageEffect.speed)));
                poseStack.scale(x, x, x);
            }
        }));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("tilt")) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((ClientEvents.getClientTicks()) / bookImageEffect.speed)));
            }
        }));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        AtomicReference<String> loc = new AtomicReference<>(bookImage.texture);
        AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
        AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);

        AtomicReference<Integer> u = new AtomicReference<>((int) bookImage.u);
        AtomicReference<Integer> v = new AtomicReference<>((int) bookImage.v);
        AtomicReference<Integer> imageWidth = new AtomicReference<>((int) bookImage.imageWidth);
        AtomicReference<Integer> imageHeight = new AtomicReference<>((int) bookImage.imageHeight);
        AtomicReference<Integer> width = new AtomicReference<>((int) bookImage.width);
        AtomicReference<Integer> height = new AtomicReference<>((int) bookImage.height);


        AtomicBoolean flag = new AtomicBoolean(false);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("hover_change_texture")) {

                float w = bookImage.width / 330 * bookImage.scale / 0.062f;
                float h = bookImage.height / 330 * bookImage.scale / 0.062f;
                float x = bookImage.x - w / 2 + 0.455f;
                float y = bookImage.y - h / 2 + 0.49f;
                if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                    flag.set(true);
                    loc.set(bookImageEffect.hoverImage.texture);
                }
            }
            if (bookImageEffect.type.equals("hover_overlay")) {

                float w = bookImage.width / 330 * bookImage.scale / 0.062f;
                float h = bookImage.height / 330 * bookImage.scale / 0.062f;
                float x = bookImage.x - w / 2 + 0.45f;
                float y = bookImage.y - h / 2 + 0.49f;
                if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                    overlay_image.set(bookImageEffect.hoverImage);
                    overlay_draw.set(true);
                }

            }


            if (flag.get()) {

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if (bookHoverImageEffect.type.equals("scale")) {

                        float f = bookHoverImageEffect.amount - 1;

                        float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((ClientEvents.getClientTicks()) / bookHoverImageEffect.speed)));
                        poseStack.scale(x, x, x);
                    }
                }));

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if (bookHoverImageEffect.type.equals("tilt")) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(-bookHoverImageEffect.amount * Mth.sin((ClientEvents.getClientTicks()) / bookHoverImageEffect.speed)));
                    }
                }));

            }


        }));

        Matrix4f matrix = poseStack.last().pose();

        PoseStack.Pose normal = poseStack.last();

        float u1 = (u.get() + 0.0F) / (float) imageWidth.get();
        float u2 = (u.get() + (float) width.get()) / (float) imageWidth.get();
        float v1 = (v.get() + 0.0F) / (float) imageHeight.get();
        float v2 = (v.get() + (float) height.get()) / (float) imageHeight.get();

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            a = (float) (color >> 24 & 255) / 255.0F;
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }
        VertexConsumer buffer;
        if (a != 1 || transparent)
            buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));
        else
            buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));


        buffer.addVertex(matrix, 0, -0.055f / 18 * height.get(), -0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height.get(), -0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height.get(), 0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height.get(), 0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();

        if (overlay_draw.get()) {
            BookImage ov_img = overlay_image.get();
            VertexConsumer buffer2;
            if (a != 1 || transparent)
                buffer2 = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));
            else
                buffer2 = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));

            float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
            float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
            float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
            float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;

            float overlay_a = 1;
            float overlay_r = 1;
            float overlay_g = 1;
            float overlay_b = 1;

            if (color != -1) {
                overlay_r = (float) (color >> 16 & 255) / 255.0F;
                overlay_g = (float) (color >> 8 & 255) / 255.0F;
                overlay_b = (float) (color & 255) / 255.0F;
            }

            poseStack.pushPose();
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            poseStack.popPose();
            if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
                multiBufferSource.endBatch();
        }

        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawPaintColorSlider(float offset, PaintSystem.ValueSlider valueSlider, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, float scale, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType, float partial) {

        if (scale == 0)
            return;
        float x;
        float y;
        float width;
        float height;
        float width2;
        float height2;

        if (valueSlider.isHorizontal()){
            x = valueSlider.getX(pageOn) + valueSlider.getValue() * valueSlider.width / 8f + 0.006f;
            y = valueSlider.getY(pageOn) + offset;
            width = 0.75f + 0.1f;
            height = 1.25f + 0.1f;
            height *= valueSlider.getHoveringScale(partial);
        } else {
            x = valueSlider.getX(pageOn) + offset;
            y = valueSlider.getY(pageOn) - valueSlider.getValue() * valueSlider.height / 8f + valueSlider.height / 16f;
            width = 1.25f + 0.1f;
            height = 0.75f + 0.1f;
            width *= valueSlider.getHoveringScale(partial);
        }
        width2 = width - 0.6f;
        height2 = height - 0.6f;

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * scale * 2.55f, 0.5f * scale * 2.55f, 0.5f * 2.55f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((x / 8.1f - 0.475f / 8.1f) / (scale * 2.55f), (y / 8.1f - 1f / 8.1f) / (scale * 2.55f), -(zLevel) / 1600f);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = poseStack.last().pose();

        PoseStack.Pose normal = poseStack.last();

        float u1 = 0;
        float u2 = 1;
        float v1 = 0;
        float v2 = 1;

        float a = 1;
        float r = 0;
        float g = 0;
        float b = 0;

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));

        a = (color >> 24 & 255) / 255.0F;
        r = (color >> 16 & 255) / 255.0F;
        g = (color >> 8 & 255) / 255.0F;
        b = (color & 255) / 255.0F;
        buffer.addVertex(matrix, 0.00001f, -0.055f / 18 * height2, -0.055f / 18 * width2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0.00001f, 0.055f / 18 * height2, -0.055f / 18 * width2).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0.00001f, 0.055f / 18 * height2, 0.055f / 18 * width2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0.00001f, -0.055f / 18 * height2, 0.055f / 18 * width2).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();

        buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
        a = 1;
        r = 0;
        g = 0;
        b = 0;
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();
        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawPaintColors(PaintSystem paintSystem, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, float scale, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType, ItemDisplayContext transformType, float partial) {

        if (scale <= 0)
            return;

        for (int i = 0; i < 3; i++) {
            if (i > paintSystem.getColors().colors.size())
                continue;
            PaintSystem.Colors.ColorSelection colorSelection = paintSystem.getColors().colors.get(i);
            float x = Mth.lerp(partial, (float) colorSelection.colorPosDataOld.pos.x, (float) colorSelection.colorPosData.pos.x) + (pageOn.isOnLeftSide() ? 0 : 0.8f);
            float y = Mth.lerp(partial, (float) colorSelection.colorPosDataOld.pos.y, (float) colorSelection.colorPosData.pos.y);
            float z = Mth.lerp(partial, (float) colorSelection.colorPosDataOld.pos.z, (float) colorSelection.colorPosData.pos.z);
            float width = Mth.lerp(partial, colorSelection.colorPosDataOld.width, colorSelection.colorPosData.width);
            float height = Mth.lerp(partial, colorSelection.colorPosDataOld.height, colorSelection.colorPosData.height);
            float width2 = width - 0.75f;
            float height2 = height - 0.75f;

            poseStack.pushPose();

            if (pageOn == PageOn.LEFT_PAGE)
                translateToLeftPage(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.LEFT_PAGE_UNDER)
                translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.LEFT_PAGE_PREV)
                translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
                translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
            if (pageOn == PageOn.RIGHT_PAGE)
                translateToRightPage(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
                translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.RIGHT_PAGE_PREV)
                translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
                translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
            else if (pageOn == PageOn.MIDDLE_BUTTON)
                translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
            poseStack.scale(0.5f * scale * 2.55f, 0.5f * scale * 2.55f, 0.5f * 2.55f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

            poseStack.translate((x / 8.1f - 0.475f / 8.1f) / (scale * 2.55f), (y / 8.1f - 1f / 8.1f) / (scale * 2.55f), -(zLevel) / 1600f);

            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));

            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

            Matrix4f matrix = poseStack.last().pose();

            PoseStack.Pose normal = poseStack.last();

            float u1 = 0;
            float u2 = 1;
            float v1 = 0;
            float v2 = 1;

            VertexConsumer buffer;

            int color = colorSelection.getColor();

            buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            float a = (color >> 24 & 255) / 255.0F;
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;
            buffer.addVertex(matrix, 0.0001f + z, -0.055f / 18 * height2, -0.055f / 18 * width2).setColor(r, g, b, 1).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.0001f + z, 0.055f / 18 * height2, -0.055f / 18 * width2).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.0001f + z, 0.055f / 18 * height2, 0.055f / 18 * width2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.0001f + z, -0.055f / 18 * height2, 0.055f / 18 * width2).setColor(r, g, b, 1).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

            if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
                multiBufferSource.endBatch();

            buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            a = 1;
            r = 0;
            g = 0;
            b = 0;
            buffer.addVertex(matrix, 0.00007f + z, -0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.00007f + z, 0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.00007f + z, 0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0.00007f + z, -0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

            if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
                multiBufferSource.endBatch();


            poseStack.popPose();



            float w1 = width / 326 * 2.55f / 0.062f;
            float h1 = height / 326 * 2.55f / 0.062f;
            float x1 = x + 0.025f - w1 / 2;
            float y1 = y - 0.5f - 0.025f - h1 / 2;
            if (canInteract(cursorX, cursorY, x1, y1, w1, h1, altarTile, drawingType)) {
                List<Component> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("Cycle Colors").withStyle(ChatFormatting.GRAY));
                this.tooltipText = tooltipList;
                this.drawTooltipText = true;
                this.tooltipStack = ItemStack.EMPTY;
            }
        }



    }

    @OnlyIn(Dist.CLIENT)
    public void drawPaintColorSliderBar(float heightPercent, PaintSystem.ValueSlider valueSlider, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, float scale, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color1, int color2, DrawingType drawingType, ItemDisplayContext transformType, boolean hueSlider, float partial) {

        if (scale == 0)
            return;
        float x = valueSlider.getX(pageOn);
        float y = valueSlider.getY(pageOn);
        float width = valueSlider.width;
        float height = valueSlider.height * heightPercent;

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * scale * 2.55f, 0.5f * scale * 2.55f, 0.5f * 2.55f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((x / 8.1f - 0.475f / 8.1f) / (scale * 2.55f), (y / 8.1f - 1f / 8.1f) / (scale * 2.55f), -(zLevel) / 1600f);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = poseStack.last().pose();

        PoseStack.Pose normal = poseStack.last();

        float u1 = 0;
        float u2 = 1;
        float v1 = 0;
        float v2 = 1;

        float a1 = 1;
        float r1 = 1;
        float g1 = 1;
        float b1 = 1;

        if (color1 != -1) {
            a1 = (float) (color1 >> 24 & 255) / 255.0F;
            r1 = (float) (color1 >> 16 & 255) / 255.0F;
            g1 = (float) (color1 >> 8 & 255) / 255.0F;
            b1 = (float) (color1 & 255) / 255.0F;
        }

        float a2 = 1;
        float r2 = 1;
        float g2 = 1;
        float b2 = 1;

        if (color2 != -1) {
            a2 = (float) (color2 >> 24 & 255) / 255.0F;
            r2 = (float) (color2 >> 16 & 255) / 255.0F;
            g2 = (float) (color2 >> 8 & 255) / 255.0F;
            b2 = (float) (color2 & 255) / 255.0F;
        }
        VertexConsumer buffer;
        if (hueSlider) {
            float bubblePosX = valueSlider.getValue();
            float bubblePosY = 0.5f;
            float bubbleRadius = 0.275f;                                          // normalized value for radius
            float bubbleStrength = (valueSlider.getHoveringScale(partial) - 1);   // intensity of the bubble effect
            if (!valueSlider.isHorizontal()) {
                bubblePosY = 1 - valueSlider.getValue();
                bubblePosX = 0.5f;
            }

            ClientEvents.hueSliderShader.safeGetUniform("bubblePos").set(bubblePosX, bubblePosY);
            ClientEvents.hueSliderShader.safeGetUniform("bubbleRadius").set(bubbleRadius);
            ClientEvents.hueSliderShader.safeGetUniform("bubbleStrength").set(bubbleStrength);
            ClientEvents.hueSliderShader.safeGetUniform("isHorizontal").set(valueSlider.isHorizontal() ? 1 : 0);
            buffer = bufferSource.getBuffer(ModRenderTypes.hueSlider(ResourceLocation.parse("hexerei:textures/book/blank.png")));
        }
        else {

            float bubblePosX = valueSlider.getValue();
            float bubblePosY = 0.5f;
            float bubbleRadius = 0.275f;                                          // normalized value for radius
            float bubbleStrength = (valueSlider.getHoveringScale(partial) - 1);   // intensity of the bubble effect
            if (!valueSlider.isHorizontal()) {
                bubblePosY = 1 - valueSlider.getValue();
                bubblePosX = 0.5f;
            }

            ClientEvents.sliderShader.safeGetUniform("bubblePos").set(bubblePosX, bubblePosY);
            ClientEvents.sliderShader.safeGetUniform("bubbleRadius").set(bubbleRadius);
            ClientEvents.sliderShader.safeGetUniform("bubbleStrength").set(bubbleStrength);
            ClientEvents.sliderShader.safeGetUniform("isHorizontal").set(valueSlider.isHorizontal() ? 1 : 0);
            buffer = bufferSource.getBuffer(ModRenderTypes.slider(ResourceLocation.parse("hexerei:textures/book/blank.png")));
        }

        if (hueSlider) {
            if (valueSlider.isHorizontal()) {
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 9 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 9 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            } else {
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 9 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 9 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            }
        } else {
            if (valueSlider.isHorizontal()) {
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0).setColor(r1, g1, b1, a1).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0).setColor(r1, g1, b1, a1).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 9 * width).setColor(r2, g2, b2, a2).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 9 * width).setColor(r2, g2, b2, a2).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            } else {
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0).setColor(r2, g2, b2, a2).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0).setColor(r1, g1, b1, a1).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 9 * width).setColor(r1, g1, b1, a1).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
                buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 9 * width).setColor(r2, g2, b2, a2).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            }
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawPaintElement(BookPaintElement paintElement, BookOfShadowsAltarTile altarTile, float cursorX, float cursorY, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType, float partial) {

        if (paintElement.client == null) {
            paintElement.client = new BookPaintElement.Client(paintElement);
            return;
        }

        BookData bookData = altarTile.currentBook;
        PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());
        paintSystem.shouldTick = true; // allow it to tick next tick cycle

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * paintElement.scale * 2.55f, 0.5f * paintElement.scale * 2.55f, 0.5f * 2.55f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((paintElement.x / 8.1f - 0.475f / 8.1f) / (paintElement.scale * 2.55f) - (pageOn.isOnLeftSide() ? 0 : (0.15f / 8.1f / 2.55f)), (paintElement.y / 8.1f - 1f / 8.1f) / (paintElement.scale * 2.55f), -(zLevel + paintElement.z) / 1600f);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        AtomicReference<Integer> width = new AtomicReference<>((int) paintElement.width);
        AtomicReference<Integer> height = new AtomicReference<>((int) paintElement.height);


        Matrix4f matrix = poseStack.last().pose();

        PoseStack.Pose normal = poseStack.last();

        float u1 = 0;
        float u2 = 1;
        float v1 = 0;
        float v2 = 1;

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            a = (float) (color >> 24 & 255) / 255.0F;
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }
        VertexConsumer buffer;
        buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(paintSystem.getImageLocation()));

        buffer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 9 * height.get(), 0).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 9 * height.get(), 0.055f / 9 * width.get()).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0, 0.055f / 9 * width.get()).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();

        poseStack.popPose();




        if (pageOn == PageOn.LEFT_PAGE) {
            float w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
            float h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
            float x = paintElement.x + 0.025f;// + 0.455f;
            float y = paintElement.y - 0.5f;// + 0.49f;

            if (paintSystem.toolsVisible) {
                if (canInteract(cursorX, cursorY, x - paintElement.width * paintElement.scale * 0.24f, y - paintElement.height * paintElement.scale * 0.24f, w + paintElement.width * paintElement.scale * 0.24f * 2, h + paintElement.height * paintElement.scale * 0.24f * 2, altarTile, drawingType)) {

                    float xPixel = ((cursorX - x) / w * paintElement.width);
                    float yPixel = ((cursorY - y) / h * paintElement.height);
                    paintSystem.hover(xPixel, yPixel);
                }
            }

            drawPaintColors(paintSystem, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * paintSystem.getColorsVisibility(partial), poseStack, bufferSource, 0, light, overlay, pageOn, drawingType, ItemDisplayContext.NONE, partial);

            for (PaintSystem.ValueSlider slider : paintSystem.getValueSliders().getSliders()) {
                if (slider.shouldRender(paintSystem)) {
                    if (slider.isDragging() && slider.isVisible(paintSystem))
                        slider.updateValue(cursorX, cursorY, pageOn);

                    drawPaintColorSlider((slider.isHorizontal() ? 0 : (slider.width / 326 * 5f * 4.1f)), slider, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * slider.getVisibility(partial), poseStack, bufferSource, 0, light, overlay, pageOn, slider.getSliderColor(paintSystem), drawingType, ItemDisplayContext.NONE, partial);
                    drawPaintColorSliderBar(1f, slider, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * slider.getVisibility(partial), poseStack, bufferSource, -0.1f, light, overlay, pageOn, slider.getColor1(paintSystem), slider.getColor2(paintSystem), drawingType, ItemDisplayContext.NONE, slider.isSpecialHueSlider(), partial);

                    w = slider.width / 326 * 2.55f / 0.062f;
                    h = slider.height / 326 * 2.55f / 0.062f;
                    x = slider.getX(pageOn) + 0.025f;
                    y = slider.getY(pageOn) - 0.5f - h / 2;
                    if (slider.isVisible(paintSystem) && (slider.isDragging() || canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType))) {
                        slider.setHovering();
                        List<Component> tooltipList = new ArrayList<>();
                        tooltipList.add(slider.getTooltip(paintSystem));
                        this.tooltipText = tooltipList;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }
            }


            ArrayList<BookImageEffect> effects = new ArrayList<>(List.of(
                    new BookImageEffect("scale", 50, 1.25f),
                    new BookImageEffect("tilt", 35, 10f)));

            // Buttons
            for (PaintSystem.Button button : paintSystem.buttons) {
                if (!button.shouldRender(paintSystem))
                    continue;

                if (button.selected.apply(paintSystem)){
                    BookImage selectedTool = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0f, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), "hexerei:textures/book/paint_tools/tool_selected.png", new ArrayList<>());
                    drawImage(selectedTool, altarTile, cursorX, cursorY, poseStack, bufferSource, 0, light, overlay, pageOn, drawingType);
                }
            }
            for (PaintSystem.Button button : paintSystem.buttons) {
                if (!button.shouldRender(paintSystem))
                    continue;
                boolean disabled = button.getDisabled(paintSystem);
                BookImage image = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), disabled ? button.getDisabledTexture(paintSystem) : button.getTexture(paintSystem), new ArrayList<>());
                BookImage imageHover = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), button.getHoverTexture(paintSystem), effects);

                boolean drawHover = false;
                w = image.width / 330 * image.scale / 0.062f;
                h = image.height / 330 * image.scale / 0.062f;
                x = image.x - w / 2 + 0.455f;
                y = image.y - h / 2 + 0.49f;

                if (button.clickedScale != 1)
                    drawHover = true;

                if (button.isVisible(paintSystem) && canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                    drawHover = true;
                    if (!button.getTooltipList().isEmpty()) {
                        this.tooltipText = button.getTooltipList();
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }

                if (drawHover && !disabled) {
                    drawImage(imageHover, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.1f, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE, true);
                } else {
                    drawImage(image, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.1f, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE, true);
                }
            }

        } else if (pageOn == PageOn.RIGHT_PAGE) {
            float w = paintElement.width / 326 * 2.55f * paintElement.scale / 0.062f;
            float h = paintElement.height / 326 * 2.55f * paintElement.scale / 0.062f;
            float x = paintElement.x + 0.025f;// + 0.455f;
            float y = paintElement.y - 0.5f;// + 0.49f;

            if (paintSystem.toolsVisible) {
                if (canInteract(cursorX + (pageOn.isOnLeftSide() ? 0 : 0.22f), cursorY, x - paintElement.width * paintElement.scale * 0.24f, y - paintElement.height * paintElement.scale * 0.24f, w + paintElement.width * paintElement.scale * 0.24f * 2, h + paintElement.height * paintElement.scale * 0.24f * 2, altarTile, drawingType)) {

                    float xPixel = ((cursorX - x) / w * paintElement.width) + (pageOn.isOnLeftSide() ? 0 : 1.8f);
                    float yPixel = ((cursorY - y) / h * paintElement.height);
                    paintSystem.hover(xPixel, yPixel);
                }
            }

            drawPaintColors(paintSystem, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * paintSystem.getColorsVisibility(partial), poseStack, bufferSource, 0, light, overlay, pageOn, drawingType, ItemDisplayContext.NONE, partial);

            for (PaintSystem.ValueSlider slider : paintSystem.getValueSliders().getSliders()) {
                if (slider.shouldRender(paintSystem)) {
                    if (slider.isDragging() && slider.isVisible(paintSystem))
                        slider.updateValue(cursorX, cursorY, pageOn);

                    drawPaintColorSlider((slider.isHorizontal() ? 0 : (slider.width / 326 * 5f * 4.1f)), slider, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * slider.getVisibility(partial), poseStack, bufferSource, 0, light, overlay, pageOn, slider.getSliderColor(paintSystem), drawingType, ItemDisplayContext.NONE, partial);
                    drawPaintColorSliderBar(1f, slider, altarTile, cursorX, cursorY, altarTile.buttonScaleRender * slider.getVisibility(partial), poseStack, bufferSource, -0.1f, light, overlay, pageOn, slider.getColor1(paintSystem), slider.getColor2(paintSystem), drawingType, ItemDisplayContext.NONE, slider.isSpecialHueSlider(), partial);

                    w = slider.width / 326 * 2.55f / 0.062f;
                    h = slider.height / 326 * 2.55f / 0.062f;
                    x = slider.getX(pageOn) + 0.025f - (pageOn.isOnLeftSide() ? 0 : 0.09f);
                    y = slider.getY(pageOn) - 0.5f - h / 2;
                    if (slider.isVisible(paintSystem) && (slider.isDragging() || canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType))) {
                        slider.setHovering();
                        List<Component> tooltipList = new ArrayList<>();
                        tooltipList.add(slider.getTooltip(paintSystem));
                        this.tooltipText = tooltipList;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }
            }


            ArrayList<BookImageEffect> effects = new ArrayList<>(List.of(
                    new BookImageEffect("scale", 50, 1.25f),
                    new BookImageEffect("tilt", 35, 10f)));

            // Buttons
            for (PaintSystem.Button button : paintSystem.buttons) {
                if (!button.shouldRender(paintSystem))
                    continue;

                if (button.selected.apply(paintSystem)){
                    BookImage selectedTool = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0f, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), "hexerei:textures/book/paint_tools/tool_selected.png", new ArrayList<>());
                    drawImage(selectedTool, altarTile, cursorX, cursorY, poseStack, bufferSource, 0, light, overlay, pageOn, drawingType);
                }
            }
            for (PaintSystem.Button button : paintSystem.buttons) {
                if (!button.shouldRender(paintSystem))
                    continue;
                boolean disabled = button.getDisabled(paintSystem);
                BookImage image = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), disabled ? button.getDisabledTexture(paintSystem) : button.getTexture(paintSystem), new ArrayList<>());
                BookImage imageHover = new BookImage(button.getX(paintSystem, pageOn, partial), button.getY(paintSystem, pageOn, partial), 0, 0, 0, button.width, button.height, button.width, button.height, Math.max(0, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)), button.getHoverTexture(paintSystem), effects);

                boolean drawHover = false;
                w = image.width / 330 * image.scale / 0.062f;
                h = image.height / 330 * image.scale / 0.062f;
                x = image.x - w / 2 + 0.455f;
                y = image.y - h / 2 + 0.49f;

                if (button.clickedScale != 1)
                    drawHover = true;

                if (button.isVisible(paintSystem) && canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                    drawHover = true;
                    if (!button.getTooltipList().isEmpty()) {
                        this.tooltipText = button.getTooltipList();
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }

                if (drawHover && !disabled) {
                    drawImage(imageHover, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.1f, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE, true);
                } else {
                    drawImage(image, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.1f, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE, true);
                }
            }

        }

    }

    @OnlyIn(Dist.CLIENT)
    public void drawBasePage(BookImage bookImage, BookOfShadowsAltarTile altarTile, float leftCursorX, float leftCursorY, float rightCursorX, float rightCursorY, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, DrawingType drawingType, ItemDisplayContext transformType) {

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);//
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV_PREV)
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV_PREV)
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(altarTile, poseStack, drawingType, transformType);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f * bookImage.scale, 0.5f * bookImage.scale, 0.5f * bookImage.scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        poseStack.translate((bookImage.x / 8.1f - 0.03f / 16f) / bookImage.scale, (bookImage.y / 8.1f - 0.053f / 16f) / bookImage.scale, -(zLevel + bookImage.z) / 1600f);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        AtomicReference<String> loc = new AtomicReference<>(bookImage.texture);
        AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
        AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);

        int u = (int) bookImage.u;
        int v = (int) bookImage.v;
        int imageWidth = (int) bookImage.imageWidth;
        int imageHeight = (int) bookImage.imageHeight;
        int width = (int) bookImage.width;
        int height = (int) bookImage.height;

        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose normal = poseStack.last();

        float u1 = 0;
        float u2 = (float) imageWidth / Mth.abs(imageWidth);
        float v1 = 0;
        float v2 = (float) imageHeight / Mth.abs(imageHeight);

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            a = (float) (color >> 24 & 255) / 255.0F;
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }
        VertexConsumer buffer;
        if (a != 1)
            buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse(loc.get())));
        else
            buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));
        buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));

        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
            multiBufferSource.endBatch();

        if (overlay_draw.get()) {
            BookImage ov_img = overlay_image.get();
            VertexConsumer buffer2 = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(ov_img.texture)));

            float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
            float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
            float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
            float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;

            float overlay_a = 1;
            float overlay_r = 1;
            float overlay_g = 1;
            float overlay_b = 1;

            if (color != -1) {
                overlay_r = (float) (color >> 16 & 255) / 255.0F;
                overlay_g = (float) (color >> 8 & 255) / 255.0F;
                overlay_b = (float) (color & 255) / 255.0F;
            }

            poseStack.pushPose();
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            poseStack.popPose();
            if (bufferSource instanceof MultiBufferSource.BufferSource multiBufferSource)
                multiBufferSource.endBatch();
        }

        poseStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawTitle(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, PageOn pageOn, DrawingType drawingType) {

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);


        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        poseStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        poseStack.translate(4.75f / 16f, 0f / 16f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/title.png")));

        PoseStack.Pose normal = poseStack.last();
        int u = 0;
        int v = 0;
        int imageWidth = 128;
        int imageHeight = 128;
        int width = 100;
        int height = 26;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        poseStack.popPose();


    }

    public void resetLines() {
        this.lineWidth = 0;
        this.lineHeight = 0;
    }

    @OnlyIn(Dist.CLIENT)
    public BookParagraphElements resetLinesNewBox(List<BookParagraphElements> elements, int boxOn) {
        this.lineWidth = 0;
        this.lineHeight = 0;
        if (boxOn + 1 < elements.size())
            return elements.get(boxOn + 1);
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawString(BookParagraph bookParagraph, BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float mouseX, float mouseY, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {

        if (bookParagraph.paragraphElements.isEmpty())
            return;

        MutableComponent pageText = bookParagraph.translatablePassage;
        int wordNumber = -1;
        int boxOn = 0;
        BookParagraphElements activeElement = (bookParagraph.paragraphElements.getFirst());

        Font font = ClientProxy.font();
        boolean findNewWord = true;
        String[] words = pageText.getString().trim().split("(\\s+)");
        String pageTextString = pageText.getString();


        int itor = -1;
        for (String word : words) {
            itor++;
            if (word.length() > 2) {
                if (word.charAt(0) == '%' && word.charAt(1) == 'k') {
                    String temp = word.substring(2);

                    String[] temp2 = temp.split("%");
                    temp = temp2[0];

                    String alt = "key." + temp;

                    for (KeyMapping k : ClientProxy.keys) {
                        String name = k.getName();
                        if (name.equals(temp) || name.equals(alt)) {
                            String keyName = k.getTranslatedKeyMessage().getString();
                            if (keyName.length() <= 1)
                                keyName = keyName.toUpperCase(Locale.ROOT);
                            words[itor] = keyName + ((temp2.length > 1) ? temp2[1] : "");
                            pageTextString = pageTextString.replaceAll(word, words[itor]);
                        }
                    }

                }
            }
        }
        List<String> combinedList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(",") && i != 0) {
                combinedList.set(combinedList.size() - 1, combinedList.getLast() + words[i]);
            } else {
                combinedList.add(words[i]);
            }
        }
        words = combinedList.toArray(new String[0]);


        char[] text = pageTextString.toCharArray();

        int[] wordLength = new int[words.length];
        float[] wordWidths = new float[words.length];
        for (int k = 0; k < words.length; k++) {
            wordLength[k] = words[k].length();
            wordWidths[k] = font.width(words[k]);
        }

        boolean breakBool = false;
        ArrayList<String> strings = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length; i = i) {
            if (breakBool)
                break;
            if (text[i] == '\n') {
                this.lineWidth = 0;
                this.lineHeight++;
                strings.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                if (this.lineHeight >= activeElement.height) {
                    activeElement = resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                    if (activeElement == null) {
                        breakBool = true;
                        break;

                    }
                }
                i++;
            } else if (text[i] == ' ') {
                findNewWord = true;
                stringBuilder.append(' ');
                this.lineWidth += font.width(" ");
                if (this.lineWidth > activeElement.width * 3.75f) {
                    this.lineWidth = 0;
                    this.lineHeight++;
                    strings.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    if (this.lineHeight >= activeElement.height) {
                        activeElement = resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                        if (activeElement == null) {
                            breakBool = true;
                            break;

                        }
                    }
                }
                i++;
            } else if (findNewWord) {
                wordNumber++;

                char[] wordText = words[wordNumber].toCharArray();
                if (this.lineWidth > 0 && this.lineWidth + wordWidths[wordNumber] > activeElement.width * 3.75f) {
                    this.lineWidth = 0;
                    this.lineHeight++;
                    strings.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    if (this.lineHeight >= activeElement.height) {
                        activeElement = resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                        if (activeElement == null) {
                            breakBool = true;
                            break;

                        }
                    }
                }
                for (char character : wordText) {
                    stringBuilder.append(character);
                    this.lineWidth += font.width(String.valueOf(character));
                    if (this.lineWidth > activeElement.width * 3.75f) {
                        this.lineWidth = 0;
                        this.lineHeight++;
                        strings.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        if (this.lineHeight >= activeElement.height) {
                            activeElement = resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                            if (activeElement == null) {
                                breakBool = true;
                                break;

                            }
                        }
                    }
                }

                i += wordLength[wordNumber];
            }
        }

        if (!stringBuilder.toString().isEmpty())
            strings.add(stringBuilder.toString());

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8.35f / 16f, 4.5f / 16f, -0.01f / 16f);
        poseStack.scale(0.00272f, 0.00272f, 0.00272f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int boxId = 0;
        int linenumber = 0;
        boolean flag = true;
        while (flag) {
            ArrayList<String> remainder = new ArrayList<>();
            if (bookParagraph.paragraphElements.size() > boxId && bookParagraph.paragraphElements.get(boxId) != null) {

                BookParagraphElements box = bookParagraph.paragraphElements.get(boxId);
                boolean alignVerticalMiddle = box.verticalAlign.equals("middle");

                float offsetY = 0;
                if (alignVerticalMiddle && Math.round(box.height * font.lineHeight) + 1 > strings.size())
                    offsetY = (box.height * font.lineHeight / 2f) - (strings.size() / 2f) * (font.lineHeight);

                for (String s1 : strings) {
                    if ((linenumber + 1) * font.lineHeight <= Math.round(box.height * font.lineHeight) + 1) {
                        float offsetX = 0;
                        if (bookParagraph.align.equals("middle"))
                            offsetX = (font.width(s1)) / 2;

                        font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y * font.lineHeight) + (linenumber * font.lineHeight)) - 4 + offsetY, HexereiUtil.getColorValue(0.12f, 0.12f, 0.12f), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                        poseStack.pushPose();
                        poseStack.translate(0.25f, 0.25f, 1 / 16f);
                        font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y * font.lineHeight) + (linenumber * font.lineHeight)) - 4 + offsetY, 16777216, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                        poseStack.popPose();
                    } else {
                        remainder.add(s1);
                    }
                    ++linenumber;
                }
            } else
                flag = false;
            if (remainder.isEmpty())
                flag = false;
            else {
                boxId++;
                linenumber = 0;
                strings = remainder;
            }
        }

        buffer.endBatch();
        poseStack.popPose();

        resetLines();

    }

    private void renderHighlight(Rect2i[] highlightAreas, PoseStack poseStack, MultiBufferSource bufferSource, int overlay, int light, float xOffset, float yOffset) {
        for (Rect2i rect2i : highlightAreas) {
            float[] col = HexereiUtil.rgbaIntToFloatArray(-16776961);
            fill(RenderType.guiTextHighlight(), poseStack, bufferSource, rect2i.getX() + xOffset, rect2i.getY() + yOffset, 0, rect2i.getWidth(), rect2i.getHeight(), (int) (col[0] * 255), (int) (col[1] * 255), (int) (col[2] * 255), (int) (col[3] * 255), overlay, light);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void drawString(BookWritableTextBox bookWritableTextBox, BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float xCursor, float yCursor, float zLevel, int light, int overlay, PageOn pageOn, DrawingType drawingType) {

        if (bookWritableTextBox.client == null) {
            bookWritableTextBox.client = new BookWritableTextBox.Client(bookWritableTextBox);
            return;
        }
        BookWritableTextBox.Client.DisplayCache displaycache = bookWritableTextBox.client.getDisplayCache(altarTile.currentBook);

        Font font = ClientProxy.font();

        poseStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-8.35f / 16f, 4.5f / 16f, -0.01f / 16f);
        poseStack.scale(0.00272f, 0.00272f, 0.00272f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int linenumber = 0;
        BookParagraphElements box = bookWritableTextBox.paragraphElement;

        float offsetY = 0;

        for (BookWritableTextBox.Client.LineInfo line : displaycache.lines) {

            if ((linenumber + 1) * font.lineHeight <= Math.round(box.height * font.lineHeight) + 1) {
                float offsetX = 0;

                font.drawInBatch(line.asComponent, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + linenumber * font.lineHeight) - 4 + offsetY, HexereiUtil.getColorValue(0.12f, 0.12f, 0.12f), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                poseStack.pushPose();
                poseStack.translate(0.25f, 0.25f, 1 / 16f);
                font.drawInBatch(line.asComponent, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + linenumber * font.lineHeight) - 4 + offsetY, 16777216, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                poseStack.popPose();
            }
            ++linenumber;
        }

        if (PageDrawing.focusedWritableTextBox != null && PageDrawing.focusedWritableTextBox.getRight() == bookWritableTextBox && altarTile == PageDrawing.focusedWritableTextBox.getLeft()){
            if ((int) ClientEvents.getClientTicks() / 6 % 3 == 0 || (int) ClientEvents.getClientTicks() / 6 % 3 == 1) {
                fill(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/pencil_cursor.png")), poseStack, bufferSource, (box.x * 8f) - 24 + displaycache.cursor.x, (box.y * font.lineHeight) - 5 + displaycache.cursor.y, -1, 9, 9, 255, 255, 255, 255, overlay, light);
            }
            renderHighlight(displaycache.selection, poseStack, bufferSource, overlay, light, (box.x * 8f) - 92, (box.y * font.lineHeight) - 36);
        } else {

            if (canInteract(xCursor, yCursor, bookWritableTextBox.paragraphElement.x + 0.45f, bookWritableTextBox.paragraphElement.y, bookWritableTextBox.paragraphElement.width / 6.15f, bookWritableTextBox.paragraphElement.height / 2.57f, altarTile, drawingType)) {
                BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i((int) ((xCursor - bookWritableTextBox.paragraphElement.x - 0.45f) / 5 * 115f), (int) ((yCursor - bookWritableTextBox.paragraphElement.y) / 7.1f * 162f));
                int i = displaycache.getIndexAtPosition(
                        ClientProxy.font(), pos2i
                );
                pos2i = bookWritableTextBox.client.getCursorPosOf(i, altarTile.currentBook);

                fill(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/pencil_cursor.png")), poseStack, bufferSource, (box.x * 8f) - 24 + pos2i.x, (box.y * font.lineHeight) - 5 + pos2i.y, -1, 9, 9, 255, 255, 255, 255, overlay, light);

            }


        }

//            font.drawInBatch(pageText, (xIn * 9f) - 24, (yIn * 9f) - 4, 16777216, false, poseStack.last().pose(), bufferSource, false, 0, light);

        buffer.endBatch();
        poseStack.popPose();

        resetLines();

    }

    public static enum PageOn {
        LEFT_PAGE,
        LEFT_PAGE_UNDER,
        LEFT_PAGE_PREV,
        LEFT_PAGE_PREV_PREV,
        RIGHT_PAGE,
        RIGHT_PAGE_UNDER,
        RIGHT_PAGE_PREV,
        RIGHT_PAGE_PREV_PREV,
        MIDDLE_BUTTON;

        public boolean isOnLeftSide() {
            return this == LEFT_PAGE || this == LEFT_PAGE_PREV || this == LEFT_PAGE_PREV_PREV || this == RIGHT_PAGE_UNDER;
        }
    }

}
