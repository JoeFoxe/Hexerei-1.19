package net.joefoxe.hexerei.data.books;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.*;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJei;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.screen.tooltip.HexereiBookTooltip;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PageDrawing {
    public float lineWidth;
    public float lineHeight;
    public float tick;
    public ItemStack tooltipStack;
    public List<Component> tooltipText;
    public BookImage slotOverlay;
    public boolean drawTooltipStack;
    public boolean drawTooltipStackFlag;
    public boolean drawTooltipTextFlag;
    public float drawTooltipScale;
    public boolean drawTooltipText;
    public boolean drawSlotOverlay;
    public PageOn slotOverlayPageOn;
    public boolean isRightPressedOld;
    public boolean isLeftPressedOld;

    public static ItemRenderer itemRenderer;

    public double mouseXOld;
    public double mouseYOld;
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    public static final ResourceLocation SLOT_ATLAS = new ResourceLocation(Hexerei.MOD_ID, "book/slot");
    public static final ResourceLocation SLOT = new ResourceLocation(Hexerei.MOD_ID, "textures/book/slot.png");
    public static final ResourceLocation TITLE = new ResourceLocation(Hexerei.MOD_ID, "book/title");

    public PageDrawing() {
        this.lineWidth = 0;
        this.lineHeight = 0;
        this.tick = 0;
        this.tooltipStack = ItemStack.EMPTY;
        this.tooltipText = new ArrayList<>();
        this.slotOverlay = new BookImage(0,0,1, 0,0, 20, 20,20,20,1,"hexerei:textures/book/slot_hover.png", new ArrayList<BookImageEffect>());
        this.drawTooltipStack = false;
        this.drawTooltipStackFlag = false;
        this.drawTooltipTextFlag = false;
        this.drawTooltipScale = 0;
        this.drawTooltipText = false;
        this.drawSlotOverlay = false;
        this.slotOverlayPageOn = PageOn.LEFT_PAGE;
        this.isRightPressedOld = false;
        this.isLeftPressedOld = false;
//        this.blockEntity;
        itemRenderer = Hexerei.proxy.getLevel() == null ? null : Hexerei.proxy.getLevel().isClientSide ? Minecraft.getInstance().getItemRenderer() : null;
        this.mouseXOld = 0;
        this.mouseYOld = 0;
    }



//    private final ResourceLocation TEXT = new ResourceLocation(Hexerei.MOD_ID,
//            "textures/gui/text.png");

    private static final Quaternion ITEM_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternion quaternion = new Quaternion(Vector3f.XP, 65f, true);
        quaternion.mul(new Quaternion(Vector3f.YP, 50f, true));
        return quaternion;
    });
    private static final Quaternion ITEM_LIGHT_ROTATION_FLAT = new Quaternion(Vector3f.XP, 45f, true);

    public static ItemStack getTagStack(TagKey<Item> key){
        Optional<Item> optional = Registry.ITEM.getTag(key).flatMap(tag -> tag.getRandomElement(new Random())).map(Holder::value);

        if (optional.isPresent()) {
            Item item = optional.get();
            ItemStack itemStack = new ItemStack(item);
            return itemStack;
        }
        return ItemStack.EMPTY;
//        return Registry.ITEM.getTag(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(loc))).flatMap(tag -> tag.getRandomElement(new Random())).map(Holder::value);
    }
    public static Optional<Item> getTagStackStatic(TagKey<Item> key){
        return Registry.ITEM.getTag(key).flatMap(tag -> tag.getRandomElement(new Random())).map(Holder::value);
    }


    @OnlyIn(Dist.CLIENT)
    public static void renderItem(BookOfShadowsAltarTile tileEntityIn, @NotNull BookItemsAndFluids itemStackElement, PoseStack matrixStackIn, MultiBufferSource.BufferSource buffer, float xIn, float yIn, float zLevel, int combinedLight, int combinedOverlay, PageOn pageOn, boolean isItem) {

        ItemStack itemStack = itemStackElement.item;

        if(itemStackElement.type.equals("tag")){
            int mod = ((int)Hexerei.getClientTicks()) % 60;

            if(itemStackElement.item.isEmpty())
            {
                itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
            }

            if((mod == 59 || mod == 58) && itemStackElement.refreshTag){
                itemStack = getTagStack(itemStackElement.key);
                if(itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                if(itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
            }
            if(mod == 1 || mod == 2){
                itemStackElement.refreshTag = true;
            }
        }

        matrixStackIn.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.021f / 16f);
        matrixStackIn.scale(0.049f, 0.049f, 0.001f);
        matrixStackIn.translate(yIn * 1.259f, -xIn * 1.259f, 0);
        Matrix3f matrix3f = matrixStackIn.last().normal();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90));

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-180));
        matrixStackIn.translate(-4.75f / 8f, -4.5f / 8f, 0);
        matrixStackIn.scale(0.065f, 0.065f, 0.05f);

        renderGuiItemDecorations(buffer, Minecraft.getInstance().font, itemStack, matrixStackIn, 0, 0, combinedOverlay, combinedLight);
        matrixStackIn.translate(0.75f / 8f / 0.065f, 0.5f / 8f / 0.065f, 0);
        matrixStackIn.scale(0.965f, 0.965f, 0.965f);
        renderGuiItemCount(buffer, Minecraft.getInstance().font, itemStack, matrixStackIn, 0, 0, combinedOverlay, combinedLight);
        matrixStackIn.popPose();


        try {
            if(itemRenderer == null)
                itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemModel.isGui3d()) {
                matrixStackIn.last().normal().mul(ITEM_LIGHT_ROTATION_3D);
            }
            else {
                matrixStackIn.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
            }

            itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, matrixStackIn, buffer, combinedLight, combinedOverlay, itemModel);
        } catch (Exception e) {
            // Shrug
        }

        matrixStackIn.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItemDecorations(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light){

        if (itemStack.isBarVisible()) {

            matrixStackIn.pushPose();
            int i = itemStack.getBarWidth();
            int j = itemStack.getBarColor();
            fillRect(matrixStackIn ,bufferSource, xIn + 2.75f, yIn + 13.75f, 0, 13, 1.5f, 0, 0, 0, 255, overlay, light);
            fillRect(matrixStackIn ,bufferSource, xIn + 2.75f, yIn + 13.75f, -0.5f, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255, overlay, light);
            matrixStackIn.popPose();
        }

    }


    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItemCount(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light){

        if (itemStack.getCount() > 1) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0,0,-7f);
            String s = String.valueOf(itemStack.getCount());
            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)) + 1f, (yIn + 6 + 3) + 1f, HexereiUtil.getColorValueAlpha(0.245f,0.245f,0.245f,1), false, matrixStackIn.last().pose(), bufferSource, false, overlay, light);

            matrixStackIn.translate(0,0,-6f);
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)), (yIn + 6 + 3), 16777215, false, matrixStackIn.last().pose(), bufferSource, false, overlay, light);
            multibuffersource$buffersource.endBatch();
            matrixStackIn.popPose();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGuiItem(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light){


        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));
        matrixStackIn.scale(16,16,1f);
        matrixStackIn.translate(yIn * 1.25f * 2 / 40 + 0.55f, -xIn * 1.25f * 2 / 40 - 0.55f, -2f);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));


        try {
            BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);

            if(Minecraft.getInstance().cameraEntity != null){
                Vec3 vec = Minecraft.getInstance().cameraEntity.getPosition(0);
                Vector3f vector3f = new Vector3f(2, 1, 3000);
                Quaternion quat = new Quaternion(vector3f, 0, false);
                matrixStackIn.last().normal().mul(quat);
            }
            if (itemModel.isGui3d()) {
                matrixStackIn.last().normal().mul(Util.make(() -> {
                    Quaternion quaternion = new Quaternion(Vector3f.XP, 65f, true);
                    quaternion.mul(new Quaternion(Vector3f.YP, 50f, true));
                    return quaternion;
                }));
            }
            else {
                matrixStackIn.last().normal().mul(new Quaternion(Vector3f.XP, 45f, true));
            }


            itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, matrixStackIn, bufferSource, light, overlay, itemModel);
        } catch (Exception e) {
            // Shrug
        }




        matrixStackIn.popPose();
    }



    @OnlyIn(Dist.CLIENT)
    private static void fillRect(PoseStack poseStack, MultiBufferSource p_115153_, float xIn, float yIn, float zIn, float widthIn, float heightIn, int p_115158_, int p_115159_, int p_115160_, int p_115161_, int overlay, int light) {

        poseStack.pushPose();
        poseStack.translate(0,0,-4.15f);
        Matrix3f normal = poseStack.last().normal();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth =  1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float)imageWidth;
        float u2 = (u + (float)width) / (float)imageWidth;
        float v1 = (v + 0.0F) / (float)imageHeight;
        float v2 = (v + (float)height) / (float)imageHeight;


        VertexConsumer buffer = p_115153_.getBuffer(RenderType.entityCutout(new ResourceLocation("hexerei:textures/book/blank.png")));
        buffer.vertex(matrix4f, (xIn + 0), (yIn + 0), zIn).                color(p_115158_, p_115159_, p_115160_, p_115161_).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f, (xIn + 0), (yIn + heightIn), zIn).        color(p_115158_, p_115159_, p_115160_, p_115161_).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f, (xIn + widthIn), (yIn + heightIn), zIn).color(p_115158_, p_115159_, p_115160_, p_115161_).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f, (xIn + widthIn), (yIn + 0), zIn).        color(p_115158_, p_115159_, p_115160_, p_115161_).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        poseStack.popPose();
    }



    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPageUnder(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if(!isItem)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
        if(isItem && transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-55));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.translate(0,1f/32f,0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-(80f-tileEntityIn.degreesOpenedRender/1.12f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(-tileEntityIn.pageTwoRotationRender)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(tileEntityIn.pageOneRotationRender / 16f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-180));
        matrixStack.translate(0,-1/2f + 1/8f - 1/128f,0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPage(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemTransforms.TransformType transformType){


        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if(!isItem)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
        if(isItem && transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-55));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/33);
        matrixStack.translate(0,1f/32f,0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-(80f-tileEntityIn.degreesOpenedRender/1.12f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(-tileEntityIn.pageTwoRotationRender)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(tileEntityIn.pageOneRotationRender / 16f)));
//        matrixStack.translate(0,1/64f,0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPageUnder(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if(!isItem)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
        if(isItem && transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-55));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.translate(0,1f/32f,0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((80f-tileEntityIn.degreesOpenedRender/1.12f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(-tileEntityIn.pageOneRotationRender)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(tileEntityIn.pageTwoRotationRender / 16f)));
//        matrixStack.translate(0, 1 / 64f, 0);

    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPage(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if(!isItem)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
        if(isItem && transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-55));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.translate(0,1f/32f,0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((80f-tileEntityIn.degreesOpenedRender/1.12f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(-tileEntityIn.pageOneRotationRender)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f)*(tileEntityIn.pageTwoRotationRender / 16f)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-180));
        matrixStack.translate(0,-1/2f + 1/8f - 1/128f,0);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToLeftPagePrevious(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        if(tileEntityIn.turnPage != 2 && tileEntityIn.turnPage != -1){
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if(!isItem)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
            else
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
            if(isItem && transformType != ItemTransforms.TransformType.NONE)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(tileEntityIn.degreesOpenedRender));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, (float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90));
//            matrixStackIn.translate(0, 1 / 64f, 0);
        } else {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if(!isItem)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
            else
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
            if(isItem && transformType != ItemTransforms.TransformType.NONE)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpenedRender / 1.12f)));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender / 16f + 180/16f)));
//            matrixStackIn.translate(0, 1 / 64f, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void translateToRightPagePrevious(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        if(tileEntityIn.turnPage != 1 && tileEntityIn.turnPage != -1){
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(-(float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, -(float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if(!isItem)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
            else
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
            if(isItem && transformType != ItemTransforms.TransformType.NONE)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-tileEntityIn.degreesOpenedRender));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, -(float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90));
            matrixStackIn.translate(0, -0.375f - 1/128f, 0);
        } else {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if(!isItem)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
            else
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 10)));
            if(isItem && transformType != ItemTransforms.TransformType.NONE)
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((80f - tileEntityIn.degreesOpenedRender / 1.12f)));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender / 16f + 180/16f)));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-180));
            matrixStackIn.translate(0,-0.375f - 1/128f,0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void translateToMiddleButton(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemTransforms.TransformType transformType){

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if(transformType == ItemTransforms.TransformType.GUI)
            yPos = 3/16f;
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -12/32f;
        }
        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            degreesOpened = 90;
            xPos = 4/16f;
            zPos = -1/32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if(!isItem)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 - 13)));
        if(isItem && transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-55));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(270));
        matrixStack.translate(2.95f / 64f,7.1f / 16f,11f / 32f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-180));
//        matrixStack.translate(0,0,0);
//        matrixStack.scale(0.003f,0.003f,0.003f);
//        matrixStack.translate(-16, -16, -10);

    }

    @OnlyIn(Dist.CLIENT)
    public void drawPage(BookPage page, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource.BufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem, ItemTransforms.TransformType transformType) throws CommandSyntaxException {
        drawPage(page, tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, pageOn, isItem, transformType, -1);
    }
    @OnlyIn(Dist.CLIENT)
    public void drawPage(BookPage page, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource.BufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem, ItemTransforms.TransformType transformType, int pageNum) throws CommandSyntaxException {

        if(page != null) {


            Player playerIn = Hexerei.proxy.getPlayer();

            double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
            Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
            Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

            for(int i = 0; i < page.paragraph.size(); i++){
                drawString(((BookParagraph) (page.paragraph.toArray()[i])), tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
            }

            //draw page number
            if(pageOn == PageOn.LEFT_PAGE || pageOn == PageOn.LEFT_PAGE_PREV || pageOn == PageOn.RIGHT_PAGE_UNDER){
                BookEntries bookEntries = BookManager.getBookEntries();
                if(bookEntries != null){
                    int pageOnNum = pageNum + 1 - bookEntries.chapterList.get(0).endPage;
                    BookParagraphElements bookParagraphElements = new BookParagraphElements(14.3f, 19.25f, 1, 30);
                    ArrayList<BookParagraphElements> list = new ArrayList<>();
                    list.add(bookParagraphElements);
                    BookParagraph bookParagraph;
                    if (pageOnNum > 0)
                        bookParagraph = new BookParagraph(list, String.valueOf(pageOnNum), "left");
                    else
                        bookParagraph = new BookParagraph(list, HexereiUtil.intToRoman(pageNum + 1), "left");
                    bookParagraph.paragraphElements.get(0).x -= Minecraft.getInstance().font.width(bookParagraph.passage) / 8f;

                    drawString(bookParagraph, tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
            }
            if(pageOn == PageOn.RIGHT_PAGE || pageOn == PageOn.RIGHT_PAGE_PREV || pageOn == PageOn.LEFT_PAGE_UNDER){
                BookEntries bookEntries = BookManager.getBookEntries();
                if(bookEntries != null){
                    int pageOnNum = pageNum + 1 - bookEntries.chapterList.get(0).endPage;
                    BookParagraphElements bookParagraphElements = new BookParagraphElements(0, 19.25f, 1, 30);
                    ArrayList<BookParagraphElements> list = new ArrayList<>();
                    list.add(bookParagraphElements);
                    BookParagraph bookParagraph;
                    if (pageOnNum > 0)
                        bookParagraph = new BookParagraph(list, String.valueOf(pageOnNum), "left");
                    else
                        bookParagraph = new BookParagraph(list, HexereiUtil.intToRoman(pageNum + 1), "left");

                    drawString(bookParagraph, tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
            }

            for(int i = 0; i < page.itemList.size(); i++){
                BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page.itemList.toArray()[i]));
                drawItemInSlot(tileEntityIn, bookItemStackInSlot, matrixStackIn, bufferIn, bookItemStackInSlot.x, bookItemStackInSlot.y, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
            }

            if(transformType == ItemTransforms.TransformType.NONE){
                for (int i = 0; i < page.itemList.size(); i++) {
                    BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page.itemList.toArray()[i]));

                    if (pageOn == PageOn.LEFT_PAGE) {

                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {


                            if (bookItemStackInSlot.item != null) {
                                if(!bookItemStackInSlot.item.isEmpty()){
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
                    if (pageOn == PageOn.RIGHT_PAGE) {


                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            if (bookItemStackInSlot.item != null) {
                                if(!bookItemStackInSlot.item.isEmpty()){
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
                }

                for (int i = 0; i < page.entityList.size(); i++) {
                    BookEntity bookEntity = ((BookEntity) (page.entityList.toArray()[i]));

                    if(bookEntity.entity != null)
                        bookEntity.entity.tickCount = (int)Hexerei.getClientTicksWithoutPartial();

                    if (pageOn == PageOn.LEFT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - (bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                        Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 1, 0.04f);

                            MouseHandler handler = Minecraft.getInstance().mouseHandler;

                            if(handler.isRightPressed() && !this.isRightPressedOld && tileEntityIn.slotClicked == -1)
                                Hexerei.entityClicked = true;
                            if(Hexerei.entityClicked)
                                bookEntity.toRotate += (this.mouseXOld - handler.xpos());
                        }
                        else {
                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 0, 0.08f);
                        }


                        if(bookEntity.hoverTick > 0){
                            MouseHandler handler = Minecraft.getInstance().mouseHandler;

                            BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64,32,64,32,0.75f * bookEntity.hoverTick,"hexerei:textures/book/rotate_entity.png",new ArrayList<>());
                            drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            if(handler.isRightPressed()) {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - ((float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * (float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32,48,32,48,0.45f * bookEntity.hoverTick,"hexerei:textures/book/right_click_icon_hover.png",new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                            else{
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - ((float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * (float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32,48,32,48,0.45f * bookEntity.hoverTick,"hexerei:textures/book/right_click_icon.png",new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                        }


                        if(bookEntity.toRotate != 0){
                            if(bookEntity.toRotate > 0) {
                                bookEntity.rot += Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            } else{
                                bookEntity.rot -= Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            }
                            bookEntity.toRotate = moveTo(bookEntity.toRotate, 0, Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f));
                        }
                    } else if (pageOn == PageOn.RIGHT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f + -(bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                        Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);

                        MouseHandler handler = Minecraft.getInstance().mouseHandler;

                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 1, 0.04f);

                            if(handler.isRightPressed() && !this.isRightPressedOld && tileEntityIn.slotClicked == -1)
                                Hexerei.entityClicked = true;
                            if(Hexerei.entityClicked)
                                bookEntity.toRotate += (this.mouseXOld - handler.xpos());
                        }
                        else {
                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 0, 0.08f);
                        }

                        if(bookEntity.hoverTick > 0){

                            BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64,32,64,32,0.75f * bookEntity.hoverTick,"hexerei:textures/book/rotate_entity.png",new ArrayList<>());
                            drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            if(handler.isRightPressed()) {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - ((float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * (float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32,48,32,48,0.45f * bookEntity.hoverTick,"hexerei:textures/book/right_click_icon_hover.png",new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                            else{
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - ((float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * (float)Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32,48,32,48,0.45f * bookEntity.hoverTick,"hexerei:textures/book/right_click_icon.png",new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                        }

                        if(bookEntity.toRotate != 0){
                            if(bookEntity.toRotate > 0) {
                                bookEntity.rot += Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            } else{
                                bookEntity.rot -= Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            }
                            bookEntity.toRotate = moveTo(bookEntity.toRotate, 0, Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f));
                        }
                    }
                }


                for (int i = 0; i < page.nonItemTooltipList.size(); i++) {
                    BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page.nonItemTooltipList.toArray()[i]));

                    if (pageOn == PageOn.LEFT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                        Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                            this.tooltipText = bookNonItemTooltip.textComponentsList;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }
                        if (this.drawTooltipText)
                            break;
                    } else if (pageOn == PageOn.RIGHT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f + -bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                        Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            List<Component> components = new ArrayList<>();
                            this.tooltipText = bookNonItemTooltip.textComponentsList;
                            this.tooltipStack = ItemStack.EMPTY;
//                        if (Minecraft.getInstance().mouseHandler.isRightPressed() && !this.isRightPressedOld) {
//                            System.out.println("clicked: " + this.tooltipText.get(0).getString());
//                        }
                            this.drawTooltipText = true;
                        }
                        if (this.drawTooltipText)
                            break;
                    }
                }


            }
            for(int i = 0; i < page.imageList.size(); i++){
                BookImage bookImage = ((BookImage) (page.imageList.toArray()[i]));
                drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                if (bookImage.extra_tooltips == null || bookImage.extra_tooltips.size() < 1)
                    continue;

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        this.tooltipText = bookImage.extra_tooltips;
                        this.tooltipStack = ItemStack.EMPTY;
                        this.drawTooltipText = true;
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        this.tooltipText = bookImage.extra_tooltips;
                        this.tooltipStack = ItemStack.EMPTY;
                        this.drawTooltipText = true;
                    }
                }


            }


            //drawing slot overlay
            if(this.drawSlotOverlay)
                drawImage(this.slotOverlay, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, this.slotOverlayPageOn, isItem);

            if(page.showTitle.equals("Hexerei"))
                drawTitle(tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, pageOn, isItem);

            for(int i = 0; i < page.entityList.size(); i++){
                BookEntity bookEntity = ((BookEntity) (page.entityList.toArray()[i]));
                if(bookEntity.entity instanceof LivingEntity livingEntity) {
                    if (bookEntity.entityTagsList.size() > 0 && tileEntityIn.tickCount > bookEntity.entityTagsLastChange + 40) {
                        bookEntity.entityTagsLastChange = (int)tileEntityIn.tickCount;
                        bookEntity.entityTagsListOn++;
                        if(bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                            bookEntity.entityTagsListOn = 0;
                        int on = bookEntity.entityTagsListOn;

                        if (bookEntity.entityTagsListOnSet != bookEntity.entityTagsListOn && !bookEntity.entityTagsList.get(on).equals("")) {

                            CompoundTag tag = TagParser.parseTag(bookEntity.entityTagsList.get(on));

                            String tag2 = tag.getString("CustomName");

                            CompoundTag stringTag = TagParser.parseTag(tag2);

                            livingEntity.setCustomName(new TranslatableComponent(stringTag.getString("text")));

                            livingEntity.load(tag);

                            if (livingEntity instanceof TamableAnimal)
                                ((TamableAnimal) livingEntity).setTame(true);

                            bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;

                            if (livingEntity instanceof TamableAnimal tamableAnimal) {
                                tamableAnimal.setInSittingPose(true);
                                tamableAnimal.setOrderedToSit(true);
                                tamableAnimal.setOnGround(true);
                                tamableAnimal.tick();

                                if (tamableAnimal instanceof CrowEntity crowEntity) {
                                    crowEntity.setCommandSit();
                                    crowEntity.tick();
                                }
                                bookEntity.entity = tamableAnimal;
                            }
                        }
                    }
                    drawLivingEntity(tileEntityIn, matrixStackIn, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.rot, 20, (float) (107), (float) (88 - 30), livingEntity, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
                else if(bookEntity.entity != null) {
                    if (bookEntity.entityTagsList.size() > 0 && tileEntityIn.tickCount > bookEntity.entityTagsLastChange + 40) {
                        bookEntity.entityTagsLastChange = (int)tileEntityIn.tickCount;
                        bookEntity.entityTagsListOn++;
                        if(bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                            bookEntity.entityTagsListOn = 0;
                        int on = bookEntity.entityTagsListOn;
                        if (bookEntity.entityTagsListOnSet != on && !bookEntity.entityTagsList.get(on).equals("")) {

                            CompoundTag tag = TagParser.parseTag(bookEntity.entityTagsList.get(on));

                            String tag2 = tag.getString("CustomName");

                            CompoundTag stringTag = TagParser.parseTag(tag2);

                            bookEntity.entity.setCustomName(new TranslatableComponent(stringTag.getString("text")));

                            bookEntity.entity.load(tag);

                            bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;

                        }
                    }
                    drawEntity(tileEntityIn, matrixStackIn, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.rot, 20, (float) (107), (float) (88 - 30), bookEntity.entity, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
                else {
                    Optional<EntityType<?>> optionalEntityType = EntityType.byString(bookEntity.entityType);
                    if(optionalEntityType.isPresent()) {
                        Entity entity = (Entity) optionalEntityType.get().create(Hexerei.proxy.getLevel());

                        if(entity instanceof LivingEntity livingEntity){
                            bookEntity.entity = entity;


                            if (!bookEntity.entityTags.equals("") && entity != null) {

                                CompoundTag tag = TagParser.parseTag(bookEntity.entityTags);

                                String tag2 = tag.getString("CustomName");

                                CompoundTag stringTag = TagParser.parseTag(tag2);

                                livingEntity.setCustomName(new TranslatableComponent(stringTag.getString("text")));

                                livingEntity.readAdditionalSaveData(tag);

                                if (livingEntity instanceof TamableAnimal)
                                    ((TamableAnimal) livingEntity).setTame(true);

                            }

                            if (livingEntity instanceof TamableAnimal tamableAnimal) {
                                tamableAnimal.setInSittingPose(true);
                                tamableAnimal.setOrderedToSit(true);
                                tamableAnimal.setOnGround(true);
                                tamableAnimal.tick();

                                if (tamableAnimal instanceof CrowEntity crowEntity) {
                                    crowEntity.setCommandSit();
                                    crowEntity.tick();
                                }
                                bookEntity.entity = tamableAnimal;
                            }
                        }else {
                            bookEntity.entity = entity;


                            if (!bookEntity.entityTags.equals("") && entity != null) {

                                CompoundTag tag = TagParser.parseTag(bookEntity.entityTags);

                                String tag2 = tag.getString("CustomName");

                                CompoundTag stringTag = TagParser.parseTag(tag2);

                                entity.setCustomName(new TranslatableComponent(stringTag.getString("text")));

                                entity.load(tag);

                            }
                        }
                    }
                }
            }
        }




    }

    @OnlyIn(Dist.CLIENT)
    public void drawLivingEntity(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource.BufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, LivingEntity livingEntity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem){
        matrixStackIn.pushPose();

        if(livingEntity instanceof TamableAnimal tamableAnimal && !tamableAnimal.isInSittingPose()) {
            tamableAnimal.setInSittingPose(true);
            tamableAnimal.setOnGround(true);
        }

//        livingEntity.tickCount += 1;

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);

        matrixStackIn.translate(-1f/512f, 0, 0);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        matrixStackIn.scale(0.049f * scale,0.049f * scale,0.003f);
        matrixStackIn.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));

        float $$6 = (float)Math.atan((double)(p_98854_ / 40.0F));
        float $$7 = (float)Math.atan((double)(p_98855_ / 40.0F));
        Quaternion $$10 = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion $$11 = Vector3f.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        float $$12 = livingEntity.yBodyRot;
        float $$13 = livingEntity.getYRot();
        float $$15 = livingEntity.yHeadRotO;
        float $$16 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = rot * 0.60F + livingEntity.getId();
        livingEntity.setYRot(rot * 0.60F + livingEntity.getId());
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conj();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStackIn, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        livingEntity.yBodyRot = $$12;
        livingEntity.setYRot($$13);
        livingEntity.yHeadRotO = $$15;
        livingEntity.yHeadRot = $$16;
        matrixStackIn.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawEntity(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource.BufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, Entity entity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem){
        matrixStackIn.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemTransforms.TransformType.NONE);

        matrixStackIn.translate(-1f/512f, 0, 0);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        matrixStackIn.scale(0.049f * scale,0.049f * scale,0.003f);
        matrixStackIn.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));

        float $$7 = (float)Math.atan((double)(p_98855_ / 40.0F));
        Quaternion $$10 = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion $$11 = Vector3f.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-(rot * 0.60F + entity.getId())));
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conj();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStackIn, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        matrixStackIn.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void drawPages(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, int light, int overlay, float partialTicks) throws CommandSyntaxException {
        drawPages(tileEntityIn, matrixStack, bufferSource, light, overlay, false, ItemTransforms.TransformType.NONE, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawPages(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, int light, int overlay, boolean isItem, ItemTransforms.TransformType transformType, float partialTicks) throws CommandSyntaxException {
        this.tick++;

        if(ClientProxy.keys == null)
            ClientProxy.keys = Minecraft.getInstance().options.keyMappings;

        this.drawSlotOverlay = false;
        this.drawTooltipStack = false;
        this.drawTooltipText = false;
        BookEntries bookEntries = BookManager.getBookEntries();

        if(bookEntries == null)
            return;

        ItemStack stack = tileEntityIn.itemHandler.getStackInSlot(0);

        CompoundTag tag = stack.getOrCreateTag();

        float pageOneSpeed = 1;
        float pageTwoSpeed = 1;
        if(tileEntityIn.turnPage == 1)
        {
            if (tileEntityIn.pageOneRotation != 180) {
                pageOneSpeed = ((float) Math.sin(tileEntityIn.pageOneRotationRender / 180 * Math.PI) * (float) Math.sin(tileEntityIn.pageOneRotationRender / 180 * Math.PI) * 15) + 4.25f;
            }
        }
        if(tileEntityIn.turnPage == 2)
        {
            if (tileEntityIn.pageTwoRotation != 180) {
                pageTwoSpeed = ((float) Math.sin(tileEntityIn.pageTwoRotationRender / 180 * Math.PI) * (float) Math.sin(tileEntityIn.pageTwoRotationRender / 180 * Math.PI) * 15) + 4.25f;
            }
        }

        if(tileEntityIn.turnPage == -1)
        {
            CompoundTag tag2 = tileEntityIn.itemHandler.getStackInSlot(0).getOrCreateTag();
            if(tag2.contains("chapter")) {
                int chapter = tag2.getInt("chapter");
                int page = tag2.getInt("page");
                int pageOnNum = bookEntries.chapterList.get(chapter).startPage + page;
                int destPageNum = bookEntries.chapterList.get(tileEntityIn.turnToChapter).startPage + tileEntityIn.turnToPage;
                int numPagesToDest = Math.abs(destPageNum - pageOnNum);
                pageTwoSpeed = (((float) Math.sin(tileEntityIn.pageTwoRotationRender / 180 * Math.PI) * 50 + 15)) * (1 + Math.min(numPagesToDest, 50) / 90f) * (1 + Math.min(numPagesToDest, 50) / 90f);
                pageOneSpeed = (((float) Math.sin(tileEntityIn.pageOneRotationRender / 180 * Math.PI) * 50 + 15)) * (1 + Math.min(numPagesToDest, 50) / 90f) * (1 + Math.min(numPagesToDest, 50) / 90f);
            }
        }
        tileEntityIn.pageOneRotationRender = moveTo(tileEntityIn.pageOneRotation, tileEntityIn.pageOneRotationTo, pageOneSpeed * partialTicks);

        tileEntityIn.pageTwoRotationRender = moveTo(tileEntityIn.pageTwoRotation, tileEntityIn.pageTwoRotationTo, pageTwoSpeed * partialTicks);

//        System.out.println(Minecraft.getInstance().screen == null ? "null" : Minecraft.getInstance().screen);

        String location1 = "";
        String location2 = "";
        String location1_back = "";
        String location2_back = "";
        String location1_next = "";
        String location2_next = "";
        int location1_p = 0;
        int location2_p = 0;
        int location1_back_p = 0;
        int location2_back_p  = 0;
        int location1_next_p  = 0;
        int location2_next_p  = 0;
        int chapter = 0;
        int page = 0;
        if (tag.contains("chapter")){
            chapter = tag.getInt("chapter");
            page = tag.getInt("page");
            if(page % 2 == 1)
                page--;

            if(page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0) {
                BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page);
                location1 = pageEntry.location;
                location1_p = pageEntry.pageNum;
            }
            if(bookEntries.chapterList.get(chapter).pages.size() > page + 1) {
                BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page + 1);
                location2 = pageEntry.location;
                location2_p = pageEntry.pageNum;
            }


            int next_page_chapter = chapter;
            int next_page_page = page;
            int back_page_chapter = chapter;
            int back_page_page = page;
            if(next_page_page < BookManager.getBookEntries().chapterList.get(chapter).pages.size() - 2)
                next_page_page += 2;
            else if(chapter < BookManager.getBookEntries().chapterList.size() - 1) {
                next_page_chapter++;
                next_page_page = 0;
            }
            else
                next_page_chapter = -1;

            if(next_page_chapter != -1 && next_page_chapter < bookEntries.chapterList.size() && next_page_page < bookEntries.chapterList.get(next_page_chapter).pages.size()) {

                BookPageEntry pageEntry = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page);
                location1_next = pageEntry.location;
                location1_next_p = pageEntry.pageNum;
                if (bookEntries.chapterList.get(next_page_chapter).pages.size() > next_page_page + 1) {
                    BookPageEntry pageEntry2 = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page + 1);
                    location2_next = pageEntry2.location;
                    location2_next_p = pageEntry2.pageNum;
                }
            }



            if(back_page_page - 2 >= 0)
                back_page_page-=2;
            else if(back_page_chapter > 0) {
                back_page_chapter--;
                back_page_page = BookManager.getBookEntries().chapterList.get(back_page_chapter).pages.size() - 1;
                if(back_page_page % 2 == 1)
                    back_page_page--;
            }
            else
                back_page_chapter = -1;

            if(back_page_chapter != -1 && back_page_chapter < bookEntries.chapterList.size() && back_page_page < bookEntries.chapterList.get(back_page_chapter).pages.size()){

                BookPageEntry pageEntry = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page);
                location1_back = pageEntry.location;
                location1_back_p = pageEntry.pageNum;
                if (bookEntries.chapterList.get(back_page_chapter).pages.size() > back_page_page + 1) {
                    BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page + 1);
                    location2_back = pageEntry2.location;
                    location2_back_p = pageEntry2.pageNum;
                }
            }

        }
//
        if(transformType != ItemTransforms.TransformType.GUI){

            BookPage page1 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location1));
            BookPage page2 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location2));
            drawPage(page1, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, isItem, transformType, location1_p);
            drawPage(page2, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, isItem, transformType, location2_p);
            BookPage page1_under = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location2_back));
            BookPage page1_prev = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location1_back));
            BookPage page2_under = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location1_next));
            BookPage page2_prev = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location2_next));
            drawPage(page1_under, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_UNDER, isItem, transformType, location2_back_p);
            drawPage(page2_under, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_UNDER, isItem, transformType, location1_next_p);
            drawPage(page1_prev, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_PREV, isItem, transformType, location1_back_p);
            drawPage(page2_prev, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_PREV, isItem, transformType, location2_next_p);
        }
        else{

            BookPage page1 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, "book/book_pages/gui_page_1"));
            BookPage page2 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, "book/book_pages/gui_page_1"));
            drawPage(page1, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, isItem, transformType, location1_p);
            drawPage(page2, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, isItem, transformType, location2_p);
        }

        drawBaseButtons(tileEntityIn, matrixStack, bufferSource, light, overlay, !location1_next.equals(""), !location1_back.equals(""), chapter, page, isItem);

        MouseHandler handler = Minecraft.getInstance().mouseHandler;


        if(this.drawTooltipStack && tileEntityIn.turnPage == 0) {
            this.drawTooltipStackFlag = true;
            this.drawTooltipTextFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1.5f, 0.04f);
        }
        else if(this.drawTooltipText && tileEntityIn.turnPage == 0) {
            this.drawTooltipTextFlag = true;
            this.drawTooltipStackFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1.5f, 0.04f);
        }
        else {
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 0, 0.05f);
            if(this.drawTooltipScale == 0) {
                this.drawTooltipStackFlag = false;
                this.drawTooltipTextFlag = false;
            }
        }

        if(this.drawTooltipScale > 0) {
            if(this.drawTooltipStackFlag)
                drawTooltipImage(this.tooltipStack, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, isItem);
            else
                drawTooltipText(tileEntityIn, matrixStack, bufferSource, 0, light, overlay, isItem);
        }


        this.mouseXOld = handler.xpos();
        this.mouseYOld = handler.ypos();

        this.isRightPressedOld = handler.isRightPressed();
        this.isLeftPressedOld = handler.isLeftPressed();


    }

    @OnlyIn(Dist.CLIENT)
    public void drawBaseButtons(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, boolean isItem) {
        drawBaseButtons(tileEntityIn, matrixStack, bufferSource, light, overlay, drawNext, drawBack, chapter, page, isItem, ItemTransforms.TransformType.NONE, false);
    }
    @OnlyIn(Dist.CLIENT)
    public void drawBaseButtons(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, boolean isItem, ItemTransforms.TransformType transformType, boolean fullyExtended) {

        Player playerIn = null;
        if(tileEntityIn.getLevel() != null && tileEntityIn.getLevel().isClientSide)
            playerIn = Hexerei.proxy.getPlayer();
        if(playerIn != null){

            boolean drawBookmarkButton = chapter != 0;

            double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
            Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
            Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);
            CompoundTag tag = tileEntityIn.itemHandler.getStackInSlot(0).getOrCreateTag();


            if(drawBookmarkButton && !isItem){
                Vector3f vector3f = new Vector3f(0, 0, 0);
                Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.064f, 0.5f - -1f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
                vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                BookImageEffect bookImageEffect_hover_overlay = new BookImageEffect("hover_overlay", 35, 10f, new BookImage(-0.5f, -1f, -1, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, "hexerei:textures/book/bookmark_button_hover.png", effects));


                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;

                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    effects.add(bookImageEffect_hover_overlay);
                }


                if(tag.contains("bookmarks") && tag.getBoolean("opened")) {

                    int bookmark_color = 0;
                    int bookmark_chapter = 0;
                    int bookmark_page = 0;
                    boolean flag2 = false;
                    CompoundTag bookmarks = tag.getCompound("bookmarks");
                    for(int i = 0; i < 20; i++){
                        if(bookmarks.contains("slot_" + i)){
                            CompoundTag slot = bookmarks.getCompound("slot_" + i);
                            bookmark_color = slot.getInt("color");
                            bookmark_chapter = slot.getInt("chapter");
                            bookmark_page = slot.getInt("page");

                            if(chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)){
                                flag2 = true;
                                break;
                            }
                        }
                    }
                    // draw bookmark button
                    if(flag2){


                        if(flag){
                            List<Component> list = new ArrayList<>();
                            DyeColor col = DyeColor.byId(bookmark_color);

                            String output = col.getName().substring(0, 1).toUpperCase() + col.getName().substring(1);
                            output = output.replaceAll("_", " ");

                            list.add(new TranslatableComponent("Change Color - %s", new TranslatableComponent("%s", output).withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col)))).withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }


                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_underlay.png", effects);
                        BookImage bookImage_overlay = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_overlay.png", effects);

                        drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
                        drawImage(bookImage_overlay, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);

                    }else{

                        if(flag){
                            List<Component> list = new ArrayList<>();

                            list.add(new TranslatableComponent("Bookmark Page").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }

                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button.png", effects);

                        drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
                    }

                    //draw bookmarks



                }
            }
            if(tag.contains("bookmarks")){

                int bookmark_color = 0;
                int bookmark_chapter = 0;
                int bookmark_page = 0;
                CompoundTag bookmarks = tag.getCompound("bookmarks");
                for(int i = 0; i < 20; i++){
                    boolean flag2 = false;
                    if(bookmarks.contains("slot_" + i)){

                        CompoundTag slot = bookmarks.getCompound("slot_" + i);
                        bookmark_color = slot.getInt("color");
                        bookmark_chapter = slot.getInt("chapter");
                        bookmark_page = slot.getInt("page");


                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();

                        if(i < 5){

                            float xIn = -0.4f - tileEntityIn.buttonScaleRender - 0.15f;
                            float yIn = i * 1.5f;
                            if(fullyExtended) {
                                xIn = -1.55f + 0.5f;
                                yIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;

                            }

                            float bookX = xIn + 0.4f - tileEntityIn.bookmarkHoverAmount[i]/3 * tileEntityIn.buttonScaleRender;
                            if(fullyExtended)
                                bookX = xIn + 0.4f - 0.33f;

                            if(flag2){
                                List<Component> list = new ArrayList<>();
                                DyeColor col = DyeColor.byId(bookmark_color);

                                BookEntries bookEntries = BookManager.getBookEntries();

                                if(bookEntries != null){
                                    list.add(new TranslatableComponent("%s%s - Page %s%s",
                                            new TranslatableComponent("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                        }
                        if(i >= 5 && i < 10){


                            float yIn = -0.95f - tileEntityIn.buttonScaleRender - 0.25f;
                            float xIn = -5.5f + i * 1.15f;
                            if(fullyExtended) {
                                yIn = -2.15f + 0.65f;
                                xIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;

                            }


                            float bookY = yIn + 0.5f - tileEntityIn.bookmarkHoverAmount[i]/3 * tileEntityIn.buttonScaleRender;
                            if(fullyExtended)
                                bookY = yIn + 0.5f - 0.33f;

                            if(flag2){
                                List<Component> list = new ArrayList<>();
                                DyeColor col = DyeColor.byId(bookmark_color);

                                BookEntries bookEntries = BookManager.getBookEntries();

                                if(bookEntries != null){
                                    list.add(new TranslatableComponent("%s%s - Page %s%s",
                                            new TranslatableComponent("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                        }
                        if(i >= 10 && i < 15){

                            float yIn = -0.95f - tileEntityIn.buttonScaleRender - 0.25f;
                            float xIn = -11.25f + i * 1.15f;
                            if(fullyExtended) {
                                yIn = -2.15f + 0.65f;
                                xIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;

                            }


                            if(flag2){
                                List<Component> list = new ArrayList<>();
                                DyeColor col = DyeColor.byId(bookmark_color);

                                BookEntries bookEntries = BookManager.getBookEntries();

                                if(bookEntries != null){
                                    list.add(new TranslatableComponent("%s%s - Page %s%s",
                                            new TranslatableComponent("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }


                            float bookY = yIn + 0.5f - tileEntityIn.bookmarkHoverAmount[i]/3 * tileEntityIn.buttonScaleRender;
                            if(fullyExtended)
                                bookY = yIn + 0.5f - 0.33f;

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                        }
                        if(i >= 15){

                            float xIn = 5.5f + tileEntityIn.buttonScaleRender;
                            float yIn = (i - 15) * 1.5f;
                            if(fullyExtended) {
                                xIn = 6.65f;
                                yIn -= 0.25f;
                            }
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - tileEntityIn.buttonScaleRender/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;

                            }

                            float bookX = xIn - 0.4f + tileEntityIn.bookmarkHoverAmount[i]/3 * tileEntityIn.buttonScaleRender;
                            if(fullyExtended)
                                bookX = xIn - 0.4f - 0.33f;

                            if(flag2){
                                List<Component> list = new ArrayList<>();
                                DyeColor col = DyeColor.byId(bookmark_color);

                                BookEntries bookEntries = BookManager.getBookEntries();


                                if(bookEntries != null){
                                    list.add(new TranslatableComponent("%s%s - Page %s%s",
                                            new TranslatableComponent("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                            new TranslatableComponent("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(DyeColor.byId(bookmark_color)), isItem, transformType);
                        }



                        if(chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)){
                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                        }


                    }

                    if(chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)){
//                        tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                    }
                    else if(tileEntityIn.bookmarkHoverAmount[i] > 0 && !flag2)
                        tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 0, 0.05f);
                }


                //send to server to update the slotClicked

                if(tileEntityIn.slotClicked != -1) {
                    for (int i = 0; i < 20; i++) {

                    if(i == tileEntityIn.slotClicked)
                        continue;

                    boolean flag2 = false;

                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                        if (i < 5) {

                            float xIn = -1.4f;
                            float yIn = i * 1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 90, light, overlay, PageOn.LEFT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 5 && i < 10) {


                            float xIn = -5.5f + i * 1.15f;
                            float yIn = -1.95f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 0, light, overlay, PageOn.LEFT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 10 && i < 15) {

                            float xIn = -11.25f + i * 1.15f;
                            float yIn = -1.95f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 0, light, overlay, PageOn.RIGHT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 15) {

                            float xIn = 6.5f;
                            float yIn = (i - 15) * 1.5f;
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - tileEntityIn.buttonScaleRender/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, -90, light, overlay, PageOn.RIGHT_PAGE, -1, isItem, transformType);
                        }


                        if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                        } else if (tileEntityIn.bookmarkHoverAmount[i] > 0 && !flag2) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 0, 0.05f);
                        }

                    }
                }
            }




            if(drawBack && !isItem){
                Vector3f vector3f = new Vector3f(0, 0, 0);
                Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.064f, 0.5f - 7.25f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
                vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);

                String loc = "hexerei:textures/book/back_page.png";

                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    loc = "hexerei:textures/book/back_page_hover.png";

                    List<Component> list = new ArrayList<>();
                    list.add(new TranslatableComponent("Back").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                    this.tooltipText = list;
                    this.tooltipStack = ItemStack.EMPTY;
                    this.drawTooltipText = true;
                }

                BookImage bookImage = new BookImage(-0.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2, loc, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
            }


            if(!isItem){
                Vector3f vector3f = new Vector3f(0, 0, 0);
                //back position
                Vector3f vector3f_1 = new Vector3f(0, 0.5f - 7f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc_close = "hexerei:textures/book/close.png";
                String loc_del = "hexerei:textures/book/delete.png";

                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.MIDDLE_BUTTON);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);

                    if (tileEntityIn.slotClicked != -1 && tileEntityIn.slotClickedTick > 5) {
                        loc_del = "hexerei:textures/book/delete_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(new TranslatableComponent("Delete Bookmark").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                    else {
                        loc_close = "hexerei:textures/book/close_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(new TranslatableComponent("Close Book").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }
                BookImage bookImage;
                if (tileEntityIn.slotClicked != -1 && tileEntityIn.slotClickedTick > 5)
                    bookImage = new BookImage(0, 0, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.bookmarkSelectorScale / 1.5f, loc_del, effects);
                else
                    bookImage = new BookImage(0, 0, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, loc_close, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, isItem);


                vector3f = new Vector3f(0, 0, 0);
                vector3f_1 = new Vector3f(0, 0.5f - -1f * 0.061f, -0.03f);

                blockPos = tileEntityIn.getBlockPos();
                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                effects = new ArrayList<>();
                bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc = "hexerei:textures/book/home.png";

                flag = false;
                intersectionVec = intersectPoint(0, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.MIDDLE_BUTTON);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    loc = "hexerei:textures/book/home_hover.png";
                    List<Component> list = new ArrayList<>();
                    list.add(new TranslatableComponent("Home").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                    this.tooltipText = list;
                    this.drawTooltipText = true;
                    this.tooltipStack = ItemStack.EMPTY;
                }

                bookImage = new BookImage(0, -8.1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, loc, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, isItem);


                if (drawNext) {
                    vector3f = new Vector3f(0, 0, 0);
                    vector3f_1 = new Vector3f(-0.05f + -5.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);

                    blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                    effects = new ArrayList<>();
                    bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                    bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                    loc = "hexerei:textures/book/next_page.png";


                    flag = false;
                    intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag = true;
                    }
                    if (flag) {
                        effects.add(bookImageEffect_scale);
                        effects.add(bookImageEffect_tilt);
                        loc = "hexerei:textures/book/next_page_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(new TranslatableComponent("Next").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }


                    bookImage = new BookImage(5.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2, loc, effects);

                    drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, isItem);
                }
            }
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

    public float getAngle(Vec3 pos, BlockEntity blockEntity) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - blockEntity.getBlockPos().getZ() - 0.5f, pos.x() - blockEntity.getBlockPos().getX() - 0.5f));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }
    public float moveToAngle(float input, float movedTo, float speed)
    {
        float distance = movedTo - input;

        if(Math.abs(distance) <= speed)
        {
            return movedTo;
        }

        if(distance > 0)
        {
            if(Math.abs(distance) < 180)
                input += speed;
            else
                input -= speed;
        } else {
            if(Math.abs(distance) < 180)
                input -= speed;
            else
                input += speed;
        }

        if(input < -90){
            input += 360;
        }
        if(input > 270)
            input -= 360;

        return input;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawItemInSlot(BookOfShadowsAltarTile tileEntityIn, BookItemsAndFluids bookItemStackInSlot, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {
        if(bookItemStackInSlot.type.equals("item") || bookItemStackInSlot.type.equals("tag")) {
            if(bookItemStackInSlot.show_slot)
                drawSlot(tileEntityIn, bookItemStackInSlot.item, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
            renderItem(tileEntityIn, bookItemStackInSlot, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
        }else if(bookItemStackInSlot.type.equals("fluid")){
            drawFluidInSlot(tileEntityIn, bookItemStackInSlot, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public AABB getpositionAABBNext(BookOfShadowsAltarTile altarTile){

        BlockPos blockPos = altarTile.getBlockPos();
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(-0.05f + -5.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);
        vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));
        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

        AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
        return aabb;
    }
    @OnlyIn(Dist.CLIENT)
    public AABB getpositionAABBBack(BookOfShadowsAltarTile altarTile){
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();

        vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

        vector3f.add(vector3f_1);

        vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
        return aabb;
    }
    @OnlyIn(Dist.CLIENT)
    public AABB getpositionAABBLeft(BookOfShadowsAltarTile altarTile, float xIn, float yIn){
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();

        vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

        vector3f.add(vector3f_1);

        vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
        return aabb;
    }
    @OnlyIn(Dist.CLIENT)
    public AABB getpositionAABBClose(BookOfShadowsAltarTile altarTile){
        Vector3f vector3f = new Vector3f(0, 0, 0);
        //back position
        Vector3f vector3f_1 = new Vector3f(0, 0.5f - 7f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

        AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));


        return aabb;
    }
    @OnlyIn(Dist.CLIENT)
    public AABB getpositionAABBHome(BookOfShadowsAltarTile altarTile){
        Vector3f vector3f = new Vector3f(0, 0, 0);
        //back position
        Vector3f vector3f_1 = new Vector3f(0, 0.5f - -1f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

        AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));


        return aabb;
    }


    @OnlyIn(Dist.CLIENT)
    protected static BlockHitResult getPlayerPOVHitResult(int i, Level level, Player player, ClipContext.Fluid p_41438_) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();

        if(i == 1){
            vec3 = vec3.subtract(0, 1, 0);
        }
        if(i == 2){
            vec3 = vec3.subtract(0.25, 1, 0);
        }
        if(i == 3){
            vec3 = vec3.subtract(-0.25, 1, 0);
        }
        if(i == 4){
            vec3 = vec3.subtract(0, 1, 0.25);
        }
        if(i == 5){
            vec3 = vec3.subtract(0, 1, -0.25);
        }

        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, net.minecraft.world.level.ClipContext.Block.OUTLINE, p_41438_, null));
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClickEvent(InputEvent.MouseInputEvent event) {

        Player playerIn = Hexerei.proxy.getPlayer();
        if(event.getButton() == 1 && playerIn != null){
            if(event.getAction() == 0) {
                this.isRightPressedOld = false;
                Hexerei.entityClicked = false;

                if(Minecraft.getInstance().screen != null)
                    return;


                double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();


                    for(int j = 0; j < 6; j++){
                        BlockHitResult raytrace = getPlayerPOVHitResult(j, playerIn.level, playerIn, ClipContext.Fluid.NONE);
                        if(raytrace.getType() != HitResult.Type.MISS) {
                            BlockPos pos = raytrace.getBlockPos();


                            BlockEntity blockEntity = playerIn.level.getBlockEntity(pos);
                            if(blockEntity instanceof BookOfShadowsAltarTile altarTile && altarTile.turnPage == 0 && altarTile.slotClicked != -1){


                                Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
                                Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);

                                CompoundTag tag = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();



                                if(tag.contains("bookmarks")){

                                    int bookmark_color = 0;
                                    int bookmark_chapter = 0;
                                    int bookmark_page = 0;
                                    boolean flag = false;
                                    int int_slot = 0;
                                    CompoundTag bookmarks = tag.getCompound("bookmarks");


                                    if(altarTile.slotClicked != -1){
                                        Vec3 intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
                                        AABB aabb = getpositionAABBClose(altarTile);
                                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                            //send signal to server that you deleted your bookmark.
                                            altarTile.deleteBookmark(altarTile.slotClicked);
                                        }
                                    }




                                    for(int i = 0; i < 20; i++){
                                        boolean flag2 = false;
                                        if(bookmarks.contains("slot_" + i)){

                                            CompoundTag slot = bookmarks.getCompound("slot_" + i);
                                            bookmark_color = slot.getInt("color");
                                            bookmark_chapter = slot.getInt("chapter");
                                            bookmark_page = slot.getInt("page");


                                        }

                                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                                        if(i < 5){

                                            float xIn = -0.4f - altarTile.buttonScale - 0.15f;
                                            float yIn = i * 1.5f;
                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();
                                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                            vector3f.add(vector3f_1);
                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                                flag2 = true;
                                            }
                                        }
                                        if(i >= 5 && i < 10){

                                            float xIn = -5.5f + i * 1.15f;
                                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();
                                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                            vector3f.add(vector3f_1);
                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                                flag2 = true;
                                            }
                                        }
                                        if(i >= 10 && i < 15){

                                            float xIn = -11.25f + i * 1.15f;
                                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();
                                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                            vector3f.add(vector3f_1);
                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                                flag2 = true;
                                            }
                                        }
                                        if(i >= 15){

                                            float xIn = 5.5f + altarTile.buttonScale + 0.15f;
                                            float yIn = (i - 15) * 1.5f;
                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();
                                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                            vector3f.add(vector3f_1);
                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                                flag2 = true;
                                            }
                                        }
                                        if(flag2){
                                            if(altarTile.slotClicked == i){
                                                if(altarTile.slotClickedTick < 20){
                                                    //click the same bookmark
                                                    altarTile.setTurnPage(-1, bookmark_chapter, bookmark_page);
                                                }
                                            }
                                            else {
                                                //drag the bookmark to another slot
                                                altarTile.swapBookmarks(altarTile.slotClicked, i);
                                                altarTile.bookmarkHoverAmount[i] = 0;
                                                altarTile.bookmarkHoverAmount[altarTile.slotClicked] = 0;
                                            }

                                            int_slot = i;
                                            break;
                                        }

                                    }
                                    if(int_slot != altarTile.slotClicked || altarTile.slotClickedTick > 5)
                                        playerIn.swing(InteractionHand.MAIN_HAND);
                                    altarTile.slotClicked = -1;
                                    altarTile.slotClickedTick = 0;
                                    break;
                                }

                            }

//                            playerIn.swing(InteractionHand.MAIN_HAND);

                        }
                }
            }

        }
        else if(event.getButton() == 0){
            this.isLeftPressedOld = event.getAction() == 1;
        }

    }


    public String getModNameForModId(String modId) {
        ModList modList = ModList.get();
        return modList.getModContainerById(modId)
                .map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName)
                .orElseGet(() -> StringUtils.capitalize(modId));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
//        System.out.println(event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue());

        if(!HexereiJeiCompat.LOADED)
            return;

        if(Minecraft.getInstance().screen != null)
            return;

        Player playerIn = Hexerei.proxy.getPlayer();

        //released
        if(playerIn != null && event.getAction() == 0){

        }

        //pressed
        if(playerIn != null && event.getAction() == 1){
            if(event.getKey() != ModKeyBindings.bookJEIShowUses.getKey().getValue() && event.getKey() != ModKeyBindings.bookJEIShowRecipe.getKey().getValue())
                return;
            if((Minecraft.getInstance().screen instanceof IRecipesGui))
                return;

            for(int l = 0; l < 6; l++) {
                BlockHitResult raytrace = getPlayerPOVHitResult(l, playerIn.level, playerIn, ClipContext.Fluid.NONE);
                if (raytrace.getType() != HitResult.Type.MISS) {
                    BlockPos pos = raytrace.getBlockPos();

                    BlockEntity blockEntity = playerIn.level.getBlockEntity(pos);
                    if(blockEntity instanceof BookOfShadowsAltarTile altarTile && altarTile.turnPage == 0){

                        CompoundTag tag = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();

                        if(tag.contains("opened") && tag.getBoolean("opened")) {
                            double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
                            Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
                            Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);
                            if (tag.contains("chapter")) {

                                String location1 = "";
                                String location2 = "";
                                BookEntries bookEntries = BookManager.getBookEntries();
                                if(bookEntries != null) {
                                    int chapter = tag.getInt("chapter");
                                    int page = tag.getInt("page");
                                    if (page % 2 == 1)
                                        page--;

                                    int start = bookEntries.chapterList.get(chapter).startPage;
                                    int end = bookEntries.chapterList.get(chapter).endPage;

                                    if (page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0)
                                        location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
                                    if (end - start > page + 1)
                                        location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

                                    BookPage page1 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location1));
                                    BookPage page2 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location2));


                                    if (page1 != null) {
                                        for (int i = 0; i < page1.itemList.size(); i++) {

                                            BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page1.itemList.toArray()[i]));

                                            if (bookItemStackInSlot.item != null && bookItemStackInSlot.item.isEmpty())
                                                continue;

                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();

                                            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                                            vector3f.add(vector3f_1);

                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            float size = 0.03f;
                                            AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                                            Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                                if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                                    if (bookItemStackInSlot.item != null) {
                                                        HexereiJei.showUses(bookItemStackInSlot.item);
                                                    } else {
                                                        HexereiJei.showUses(bookItemStackInSlot.fluid);
                                                    }
                                                }
                                                if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                                    if (bookItemStackInSlot.item != null) {
                                                        HexereiJei.showRecipe(bookItemStackInSlot.item);
                                                    } else {
                                                        HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }
                                    if (page2 != null) {

                                        for (int i = 0; i < page2.itemList.size(); i++) {

                                            BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page2.itemList.toArray()[i]));

                                            if (bookItemStackInSlot.item == null || bookItemStackInSlot.item.isEmpty())
                                                continue;

                                            Vector3f vector3f = new Vector3f(0, 0, 0);
                                            Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                                            BlockPos blockPos = altarTile.getBlockPos();

                                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                                            vector3f.add(vector3f_1);

                                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                            float size = 0.03f;
                                            AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                                            Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                                if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                                    if (bookItemStackInSlot.item != null) {
                                                        HexereiJei.showUses(bookItemStackInSlot.item);
                                                    } else {
                                                        HexereiJei.showUses(bookItemStackInSlot.fluid);
                                                    }
                                                }
                                                if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                                    if (bookItemStackInSlot.item != null) {
                                                        HexereiJei.showRecipe(bookItemStackInSlot.item);
                                                    } else {
                                                        HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClickEvent(InputEvent.ClickInputEvent event) {

        if(Minecraft.getInstance().screen != null)
            return;

        Player playerIn = Hexerei.proxy.getPlayer();
        if(playerIn != null && event.isUseItem()){



            for(int i = 0; i < 6; i++){
                BlockHitResult raytrace = getPlayerPOVHitResult(i, playerIn.level, playerIn, ClipContext.Fluid.NONE);
                if(raytrace.getType() != HitResult.Type.MISS) {
                    BlockPos pos = raytrace.getBlockPos();


                    BlockEntity blockEntity = playerIn.level.getBlockEntity(pos);
                    if(blockEntity instanceof BookOfShadowsAltarTile altarTile && altarTile.turnPage == 0){

                        if(altarTile.slotClicked != -1){
                            if(++altarTile.slotClickedTick > 0) {
                                event.setSwingHand(false);
                                event.setCanceled(true);
                                event.setResult(Event.Result.DENY);
                            }
                        }

                        CompoundTag tag = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();

                        if(tag.contains("opened") && tag.getBoolean("opened")) {
                            int clicked = checkClick(playerIn, altarTile);
//                            System.out.println(clicked);
                            if (clicked == 1) {
                                if (altarTile.slotClicked == -1 && clickedNext(altarTile)) {
                                    altarTile.setTurnPage(clicked);

                                    event.setCanceled(true);
                                    event.setResult(Event.Result.DENY);
                                    break;
                                }
                            }
                            if (clicked == 2) {
                                if (altarTile.slotClicked == -1 && clickedBack(altarTile)) {
                                    altarTile.setTurnPage(clicked);

                                    event.setCanceled(true);
                                    event.setResult(Event.Result.DENY);
                                    break;
                                }
                            }
                            if (clicked == -2) {
                                //close
                                altarTile.setTurnPage(clicked);

                                event.setCanceled(true);
                                event.setResult(Event.Result.DENY);
                                break;
                            }
                            if (clicked == -1) {

                                event.setCanceled(true);
                                event.setResult(Event.Result.DENY);
                                break;
                            }
                            if (clicked == -3) {
                                //close

                                event.setCanceled(true);
                                event.setResult(Event.Result.DENY);
                                break;
                            }
                            if (clicked == 3) {
                                // clicked bookmark
                                if(tag.getInt("chapter") != 0) {
                                    altarTile.clickPageBookmark(tag.getInt("chapter"), tag.getInt("page"));

                                    event.setCanceled(true);
                                    event.setResult(Event.Result.DENY);
                                    break;
                                }
                            }
                            if (clicked == -5){
                                event.setSwingHand(false);
                                event.setCanceled(true);
                                event.setResult(Event.Result.DENY);
                                break;
                            }
                        }
                    }
                }
            }

            this.isRightPressedOld = true;
        }
    }



    @OnlyIn(Dist.CLIENT)
    private static Vec3 intersectPoint(Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, Vec3 planePoint) {
        Vec3 diff = rayPoint.subtract(planePoint);
        double prod1 = diff.dot(planeNormal);
        double prod2 = rayVector.dot(planeNormal);
        double prod3 = prod1 / prod2;
        return rayPoint.subtract(rayVector.scale(prod3));
    }
    @OnlyIn(Dist.CLIENT)
    private static Vec3 intersectPoint(float xIn, float yIn, Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        if(pageOn == PageOn.RIGHT_PAGE){
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        }else if(pageOn == PageOn.MIDDLE_BUTTON) {
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(0f, 0.5f - yIn * 0.061f, -0.03f);

            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        }else {
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        }
    }


    @OnlyIn(Dist.CLIENT)
    public class LinePlaneIntersection {

        private static Vec3 intersectPoint(Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, Vec3 planePoint) {
            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        }

        public static void main() {
            Vec3 rv = new Vec3(0.0, -1.0, -1.0);
            Vec3 rp = new Vec3(0.0, 0.0, 10.0);
            Vec3 pn = new Vec3(0.0, 0.0, 1.0);
            Vec3 pp = new Vec3(0.0, 0.0, 5.0);
            Vec3 ip = intersectPoint(rv, rp, pn, pp);
//            System.out.println("The ray intersects the plane at " + ip);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3 planeNormal(BookOfShadowsAltarTile altarTile, PageOn pageOn){
        if(pageOn == PageOn.RIGHT_PAGE){
            Vector3f vector3f = new Vector3f(0, 0, 0);

            Vector3f vector3f_1 = new Vector3f(-0.05f - -0.5f * 0.06f, 0.5f - 7.05f * 0.061f, -0.03f);

            BlockPos blockPos = altarTile.getBlockPos();

            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f.add(vector3f_1);

            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));


            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_2 = new Vector3f(0, 0, 0);
            Vector3f vector3f_2_1 = new Vector3f(-0.05f - 0 * 0.06f, 0.5f - 0 * 0.061f, -0.03f);

            vector3f_2_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_2_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_2.add(vector3f_2_1);

            vector3f_2.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_2 = new Vec3(vector3f_2.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_2.y() + blockPos.getY() + 18 / 16f,
                    vector3f_2.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_3 = new Vector3f(0, 0, 0);
            Vector3f vector3f_3_1 = new Vector3f(-0.05f - 10 * 0.06f, 0.5f - 10 * 0.061f, -0.03f);

            vector3f_3_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_3_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_3.add(vector3f_3_1);

            vector3f_3.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_3 = new Vec3(vector3f_3.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_3.y() + blockPos.getY() + 18 / 16f,
                    vector3f_3.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


            Vec3 vec3_pr = vec_2.subtract(vec);
            Vec3 vec3_pq = vec_3.subtract(vec);

            Vec3 pn = vec3_pr.cross(vec3_pq);

            return pn;
        }else{
            Vector3f vector3f = new Vector3f(0, 0, 0);

            Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.06f, 0.5f - 7.05f * 0.061f, -0.03f);

            BlockPos blockPos = altarTile.getBlockPos();

            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f.add(vector3f_1);

            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));


            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_2 = new Vector3f(0, 0, 0);
            Vector3f vector3f_2_1 = new Vector3f(0.35f - 0 * 0.06f, 0.5f - 0 * 0.061f, -0.03f);

            vector3f_2_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_2_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_2.add(vector3f_2_1);

            vector3f_2.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_2 = new Vec3(vector3f_2.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_2.y() + blockPos.getY() + 18 / 16f,
                    vector3f_2.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_3 = new Vector3f(0, 0, 0);
            Vector3f vector3f_3_1 = new Vector3f(0.35f - 10 * 0.06f, 0.5f - 10 * 0.061f, -0.03f);

            vector3f_3_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_3_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_3.add(vector3f_3_1);

            vector3f_3.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_3 = new Vec3(vector3f_3.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_3.y() + blockPos.getY() + 18 / 16f,
                    vector3f_3.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


            Vec3 vec3_pr = vec_2.subtract(vec);
            Vec3 vec3_pq = vec_3.subtract(vec);

            Vec3 pn = vec3_pr.cross(vec3_pq);

            return pn;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int checkClick(Player playerIn, BookOfShadowsAltarTile altarTile){
        int clicked = 0;

        double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
        Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
        Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);
        if(!this.isRightPressedOld){


            Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
            AABB aabb = getpositionAABBNext(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: next1");
                clicked = 1;
                return clicked;
            }

            intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
            aabb = getpositionAABBBack(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: back2");
                clicked = 2;
                return clicked;
            }
            intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
            aabb = getpositionAABBClose(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: close");
                clicked = -2;
                return clicked;
            }
            intersectionVec = intersectPoint(0, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
            aabb = getpositionAABBHome(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: home");
                clicked = -1;
                altarTile.setTurnPage(clicked, 0, 0);
                return clicked;
            }
        }

        if(!this.isRightPressedOld){
            Vec3 intersectionVec = intersectPoint(-0.5f, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
            AABB aabb = getpositionAABBLeft(altarTile, -0.5f, -1f);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: bookmark");
                clicked = 3;


//                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods");
                return clicked;
            }
        }

        if(!this.isRightPressedOld) {

            CompoundTag tag = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();

            if (tag.contains("bookmarks")) {

                int bookmark_color = 0;
                int bookmark_chapter = 0;
                int bookmark_page = 0;
                boolean flag = false;
                CompoundTag bookmarks = tag.getCompound("bookmarks");
                for (int i = 0; i < 20; i++) {
                    if (bookmarks.contains("slot_" + i)) {

                        boolean flag2 = false;


                        CompoundTag slot = bookmarks.getCompound("slot_" + i);
                        bookmark_color = slot.getInt("color");
                        bookmark_chapter = slot.getInt("chapter");
                        bookmark_page = slot.getInt("page");


                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                        if (i < 5) {


                            float xIn = -0.4f - altarTile.buttonScale - 0.15f;
                            float yIn = i * 1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn - altarTile.bookmarkHoverAmount[i] / 3, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (i >= 5 && i < 10) {


                            float xIn = -5.5f + i * 1.15f;
                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (i >= 10 && i < 15) {

                            float xIn = -11.25f + i * 1.15f;
                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (i >= 15) {

                            float xIn = 5.5f + altarTile.buttonScale + 0.15f;
                            float yIn = (i - 15) * 1.5f;
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - altarTile.buttonScale/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (flag2) {
                            altarTile.slotClicked = i;
                            clicked = -1;
//                            altarTile.setTurnPage(clicked, bookmark_chapter, bookmark_page);
//                            return clicked;
                        }
                    }
                }
            }
        }


        ItemStack stack = altarTile.itemHandler.getStackInSlot(0);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("chapter")){

            String location1 = "";
            String location2 = "";
            BookEntries bookEntries = BookManager.getBookEntries();

            int chapter = tag.getInt("chapter");
            int page = tag.getInt("page");
            if(page % 2 == 1)
                page--;

            if(bookEntries != null) {

                int start = bookEntries.chapterList.get(chapter).startPage;
                int end = bookEntries.chapterList.get(chapter).endPage;

                if (page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0)
                    location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
                if (end - start > page + 1)
                    location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

                BookPage page1 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location1));
                BookPage page2 = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location2));

                if (page1 != null) {
                    if (!this.isRightPressedOld) {
                        for (int i = 0; i < page1.nonItemTooltipList.size(); i++) {

                            BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page1.nonItemTooltipList.toArray()[i]));


                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                            Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                            if (bookNonItemTooltip.hyperlink_chapter == -1 && bookNonItemTooltip.hyperlink_url.equals(""))
                                continue;
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;
                                if (!bookNonItemTooltip.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookNonItemTooltip.hyperlink_url);
                                if (bookNonItemTooltip.hyperlink_chapter != -1)
                                    altarTile.setTurnPage(clicked, Math.max(bookNonItemTooltip.hyperlink_chapter, 0), Math.max(bookNonItemTooltip.hyperlink_page, 0));
                                break;
                            }
                        }
                        for (int i = 0; i < page1.itemList.size(); i++) {

                            BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page1.itemList.toArray()[i]));


                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            float size = 0.03f;
                            AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                            Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;
                                String itemRegistryName;

                                if (bookItemStackInSlot.item != null)
                                    itemRegistryName = bookItemStackInSlot.item.getItem().getRegistryName().toString();
                                else
                                    itemRegistryName = bookItemStackInSlot.fluid.getFluid().getRegistryName().toString();

                                boolean flag = false;
                                if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
                                    BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                                    if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                        altarTile.setTurnPage(clicked, hyperlink.chapter, hyperlink.page);
                                    flag = true;
                                }
                                if (!flag) {
                                    for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                        for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                            String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                            BookPage page_check = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location3));
                                            if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                                if (!(chapter == j && (page == k || page == k - 1)))
                                                    altarTile.setTurnPage(clicked, j, k);
                                                BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                                flag = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!flag) {
                                }
                                break;
                            }
                        }

                        for (int i = 0; i < page1.imageList.size(); i++) {
                            BookImage bookImage = ((BookImage) (page1.imageList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpenedRender / 1.12f));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                            Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                //add hyperlink stuff here
//                            loc.set(bookImageEffect.hoverImage.imageLoc);
                                clicked = -1;

                                if (!bookImage.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookImage.hyperlink_url);
                                if (bookImage.hyperlink_chapter != -1)
                                    altarTile.setTurnPage(clicked, Math.max(bookImage.hyperlink_chapter, 0), Math.max(bookImage.hyperlink_page, 0));
                                break;
                            }
                        }
                    }

                    if (altarTile.slotClicked == -1) {
                        for (int i = 0; i < page1.entityList.size(); i++) {
                            BookEntity bookEntity = ((BookEntity) (page1.entityList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - (bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + altarTile.degreesOpenedRender / 1.12f));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                            Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                if (!this.isRightPressedOld) {
                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    Hexerei.entityClicked = true;
                                }
                                return -5;
                            }
                        }
                    }

                }
                if (page2 != null) {

                    if (!this.isRightPressedOld) {
                        for (int i = 0; i < page2.nonItemTooltipList.size(); i++) {

                            BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page2.nonItemTooltipList.toArray()[i]));


                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                            Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                            if (bookNonItemTooltip.hyperlink_chapter == -1 && bookNonItemTooltip.hyperlink_url.equals(""))
                                continue;
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;

                                if (!bookNonItemTooltip.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookNonItemTooltip.hyperlink_url);
                                if (bookNonItemTooltip.hyperlink_chapter != -1)
                                    altarTile.setTurnPage(clicked, Math.max(bookNonItemTooltip.hyperlink_chapter, 0), Math.max(bookNonItemTooltip.hyperlink_page, 0));
                                break;
                            }
                        }
                        for (int i = 0; i < page2.itemList.size(); i++) {

                            BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page2.itemList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            float size = 0.03f;
                            AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                            Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;

                                String itemRegistryName;

                                if (bookItemStackInSlot.item != null)
                                    itemRegistryName = bookItemStackInSlot.item.getItem().getRegistryName().toString();
                                else
                                    itemRegistryName = bookItemStackInSlot.fluid.getFluid().getRegistryName().toString();

                                boolean flag = false;
                                if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
//                                System.out.println("Found previous hyperlink");
                                    BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                                    if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                        altarTile.setTurnPage(clicked, hyperlink.chapter, hyperlink.page);
                                    flag = true;
                                }
                                if (!flag) {
                                    for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                        for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                            String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                            BookPage page_check = BookManager.getBookPages(new ResourceLocation(Hexerei.MOD_ID, location3));
                                            if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                                if (!(chapter == j && (page == k || page == k - 1)))
                                                    altarTile.setTurnPage(clicked, j, k);
                                                BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                                flag = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!flag) {
//                                System.out.println("No hyperlink found");
                                }
                                break;
                            }
                        }

                        for (int i = 0; i < page2.imageList.size(); i++) {

                            BookImage bookImage = ((BookImage) (page2.imageList.toArray()[i]));

                            if (bookImage.hyperlink_chapter == -1 && bookImage.hyperlink_url.equals(""))
                                continue;

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                            Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                clicked = -1;

                                if (!bookImage.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookImage.hyperlink_url);
                                if (bookImage.hyperlink_chapter != -1)
                                    altarTile.setTurnPage(clicked, Math.max(bookImage.hyperlink_chapter, 0), Math.max(bookImage.hyperlink_page, 0));
                                break;

                                //add hyperlink stuff here
//                            loc.set(bookImageEffect.hoverImage.imageLoc);
                            }
                        }
                    }


                    if (altarTile.slotClicked == -1) {
                        for (int i = 0; i < page2.entityList.size(); i++) {
                            BookEntity bookEntity = ((BookEntity) (page2.entityList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f + -(bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + altarTile.degreesOpenedRender / 1.12f)));
                            vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.transform(Vector3f.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                            Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                if (!this.isRightPressedOld) {
                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    Hexerei.entityClicked = true;
                                }
                                return -5;
                            }
                        }
                    }

                }
            }


        }

        return clicked;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean clickedBack(BookOfShadowsAltarTile altarTile){

        CompoundTag tag2 = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();
        int currentPage = tag2.getInt("page");
        int currentChapter = tag2.getInt("chapter");
        return currentChapter > 0 || currentPage > 1;

    }

    @OnlyIn(Dist.CLIENT)
    public boolean clickedNext(BookOfShadowsAltarTile altarTile){

        CompoundTag tag2 = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();
        int currentPage = tag2.getInt("page");
        int currentChapter = tag2.getInt("chapter");
        return currentChapter < BookManager.getBookEntries().chapterList.size()-1 || currentPage < BookManager.getBookEntries().chapterList.get(currentChapter).pages.size()-2;

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
    public void drawSlot(BookOfShadowsAltarTile tileEntityIn, ItemStack stack, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f,0.5f,0.5f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f/16f, -0.053f/16f, 0);
        matrixStack.translate(xIn/8.1f, yIn/8.1f, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation("hexerei:textures/book/slot.png")));

        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
        Matrix3f normal = matrixStack.last().normal();
        int u = 0;
        int v = 0;
        int imageWidth = 18;
        int imageHeight = 18;
        int width = 18;
        int height = 18;
        float u1 = (u + 0.0F) / (float)imageWidth;
        float u2 = (u + (float)width) / (float)imageWidth;
        float v1 = (v + 0.0F) / (float)imageHeight;
        float v2 = (v + (float)height) / (float)imageHeight;

        buffer.vertex(matrix,  0,  -0.055f / 18 * height,-0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0,  0.055f / 18 * height, -0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, 0.055f / 18 * height,   0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, -0.055f / 18 * height,  0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();


        matrixStack.popPose();

    }
    @OnlyIn(Dist.CLIENT)
    public void drawFluidInSlot(BookOfShadowsAltarTile tileEntityIn, @NotNull BookItemsAndFluids bookItemsAndFluids, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();
        FluidStack stack = bookItemsAndFluids.fluid;
        int capacity = bookItemsAndFluids.capacity;
        boolean showSlot = bookItemsAndFluids.show_slot;
        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f,0.5f,0.5f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f/16f, -0.053f/16f, 0);
        matrixStack.translate(xIn/8.1f, yIn/8.1f, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);


        Matrix4f matrix = matrixStack.last().pose();
        if(showSlot){
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation("hexerei:textures/book/slot.png")));

            matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
            Matrix3f normal = matrixStack.last().normal();
            int u = 0;
            int v = 0;
            int imageWidth = 18;
            int imageHeight = 18;
            int width = 18;
            int height = 18;
            float u1 = (u + 0.0F) / (float)imageWidth;
            float u2 = (u + (float)width) / (float)imageWidth;
            float v1 = (v + 0.0F) / (float)imageHeight;
            float v2 = (v + (float)height) / (float)imageHeight;

            buffer.vertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        }
        drawFluid(matrixStack, bufferSource, (int)bookItemsAndFluids.fluid_width, (int)bookItemsAndFluids.fluid_height, stack, capacity, light, overlay, bookItemsAndFluids.fluid_offset_x, bookItemsAndFluids.fluid_offset_y, bookItemsAndFluids.fluid_width, bookItemsAndFluids.fluid_height);

        matrixStack.popPose();

    }



    @OnlyIn(Dist.CLIENT)
    private void drawFluid(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, final int tiledWidth, final int tiledHeight, FluidStack fluidStack, int capacity, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

        FluidAttributes attributes = fluid.getAttributes();
        int fluidColor = attributes.getColor(fluidStack);

        int amount = fluidStack.getAmount();
//        int amount = (int)Math.abs((Math.sin(Hexerei.getClientTicks() / 100) * 2000));
        if(amount == 0)
            amount = capacity > 0 ? capacity : 1000;
        int scaledAmount = (amount * tiledHeight) / (capacity != 0 ? capacity : 1000);
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > tiledHeight) {
            scaledAmount = tiledHeight;
        }
        if(capacity == 0)
            scaledAmount = tiledHeight;

        drawTiledSprite(poseStack, bufferSource, tiledWidth, tiledHeight, fluidColor, scaledAmount, fluidStillSprite, capacity, amount, light, overlay, x_offset, y_offset, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawTiledSprite(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int capacity, int amount, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final int yTileCount = scaledAmount / TEXTURE_SIZE;
        final int yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        final int yStart = tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width2 = (xTile == xTileCount) ? xRemainder : (int)width;
                int height2 = (yTile == yTileCount) ? yRemainder : (int)height;
//                if(capacity > 0 && capacity >= amount)
//                    height2 *= ((float)amount / (float)capacity);
                int x_tile = (xTile * TEXTURE_SIZE);
                int y_tile = yStart - ((yTile + 1) * (int)height);
                if (width2 > 0 && height2 > 0) {
                    int maskTop = (int)height - height2;
                    int maskRight = (int)width - width2;

                    drawTextureWithMasking(poseStack, bufferSource, capacity, amount, x_tile, y_tile, sprite, color, maskTop, maskRight, 1, light, overlay, x_offset, y_offset, width, height);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
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
    private static void drawTextureWithMasking(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, int capacity, int amount, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int color, int maskTop, int maskRight, float zLevel, int light, int overlay, float x_offset, float y_offset, float width, float height) {
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
        poseStack.translate(0.001f,0.0485f + (y_offset * 0.005975f),(x_offset * 0.005975f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        poseStack.popPose();


        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        buffer.vertex(matrix, 0,  -0.055f / 18 * (width),0).color(red, green, blue, alpha).uv(uMin, vMax).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix, 0,  0.055f / 18 * (width), 0).color(red, green, blue, alpha).uv(uMax, vMax).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix, 0, 0.055f / 18 * (width), 0.055f / 9 * (height - maskTop)).color(red, green, blue, alpha).uv(uMax, vMin).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix, 0, -0.055f / 18 * (width),  0.055f / 9 * (height - maskTop)).color(red, green, blue, alpha).uv(uMin, vMin).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();

    }


    @OnlyIn(Dist.CLIENT)
    public void drawTooltipImage(ItemStack stack, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float zLevel, int light, int overlay, boolean isItem) {

        matrixStack.pushPose();

        matrixStack.translate(8f/16f , 18f/16f, 8f/16f);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(270));
        matrixStack.translate(0.25f,-(1 - (this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1)) / 12f,0);
        matrixStack.scale(this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1,this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1,this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1);

        RenderSystem.setShader(GameRenderer::getNewEntityShader);

        this.tooltipStack = stack;
        if(!this.tooltipStack.isEmpty()) {
            List<Component> tooltip = stack.getTooltipLines(Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);

            if(tooltip.size() > 0)
                tooltip.addAll(this.tooltipText);

            String modId = this.tooltipStack.getItem().getRegistryName().getNamespace();
            String modName = getModNameForModId(modId);
            TranslatableComponent modNameComponent = new TranslatableComponent(modName);
            modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
            if(!HexereiModNameTooltipCompat.LOADED)
                tooltip.add(modNameComponent);

            this.renderTooltip(this.tooltipStack, bufferSource, matrixStack, tooltip, stack.getTooltipImage(), 0, 0, overlay, light);
        }

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getFluidTooltip(BookItemsAndFluids bookItemStackInSlot) {
        FluidStack fluidStack = bookItemStackInSlot.fluid;
        int capacity = bookItemStackInSlot.capacity;
        int amount = bookItemStackInSlot.amount;
        List<Component> tooltip = new ArrayList<>();
        Fluid fluidType = fluidStack.getFluid();
        if (fluidType == null) {
            return tooltip;
        }

        TranslatableComponent displayName = (TranslatableComponent)fluidStack.getDisplayName();
        displayName.withStyle(ChatFormatting.WHITE);
        tooltip.add(displayName);
        if (capacity != 0) {
            MutableComponent amountString = new TranslatableComponent("book.hexerei.tooltip.liquid.amount.with.capacity", nf.format(amount), nf.format(capacity));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        }else if(amount != 0){
            TranslatableComponent amountString = new TranslatableComponent("book.hexerei.tooltip.liquid.amount", nf.format(amount));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        }

        if(bookItemStackInSlot.extra_tooltips.size() > 0)
            tooltip.addAll(bookItemStackInSlot.extra_tooltips);


        String modId = fluidStack.getFluid().getRegistryName().getNamespace();
        String modName = getModNameForModId(modId);
        TranslatableComponent modNameComponent = new TranslatableComponent(modName);
        modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
        tooltip.add(modNameComponent);

        return tooltip;
    }



    @OnlyIn(Dist.CLIENT)
    public void drawTooltipText(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float zLevel, int light, int overlay, boolean isItem) {

        matrixStack.pushPose();

        matrixStack.translate(8f/16f , 18f/16f, 8f/16f);
        matrixStack.translate((float)Math.sin((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f) , 0f/16f, (float)Math.cos((tileEntityIn.degreesSpunRender)/57.1f)/32f * (tileEntityIn.degreesOpenedRender/5f - 12f));
        matrixStack.translate(0 , -((tileEntityIn.degreesFloppedRender / 90))/16f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender/2 + 45)));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0,0,-(tileEntityIn.degreesFloppedRender/10f)/32);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(270));
        float scale = Math.min(this.drawTooltipScale, 1);
        matrixStack.translate(0.25f,-(1 - (scale < 0.5f ? scale * 2f : 1)) / 12f,0);
        matrixStack.scale(scale < 0.5f ? scale * 2f : 1,scale < 0.5f ? scale * 2f : 1,scale < 0.5f ? scale * 2f : 1);

        RenderSystem.setShader(GameRenderer::getNewEntityShader);

        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);

        this.renderTooltip(this.tooltipStack,bufferSource, matrixStack, this.tooltipText, Optional.empty(), 0, 0, overlay, light);

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void renderTooltip(ItemStack stack, MultiBufferSource buffer, PoseStack p_169389_, List<Component> components, Optional<TooltipComponent> p_169391_, int p_169392_, int p_169393_, int overlay, int light) {
        List<ClientTooltipComponent> list = ForgeHooksClient.gatherTooltipComponents(stack, components, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font, Minecraft.getInstance().font);
        List<Component> newComponentList = new ArrayList<>();
        for (Component component : components){
            newComponentList.add(new TranslatableComponent(component.getString()).withStyle(component.getStyle().withColor(0x292929)));
        }
        List<ClientTooltipComponent> list2 = ForgeHooksClient.gatherTooltipComponents(stack, newComponentList, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font, Minecraft.getInstance().font);
        this.renderTooltipInternal(buffer, p_169389_, list, list2, p_169392_, p_169393_, overlay, light);
    }


    @OnlyIn(Dist.CLIENT)
    private void renderTooltipInternal(MultiBufferSource bufferSource, PoseStack matrixStack, List<ClientTooltipComponent> clientTooltipComponentList, List<ClientTooltipComponent> clientTooltipComponentList2, int p_169386_, int p_169387_, int overlay, int light) {
        if (!clientTooltipComponentList.isEmpty()) {

            RenderTooltipEvent.Pre preEvent = ForgeHooksClient.onRenderTooltipPre(this.tooltipStack, matrixStack, p_169386_, p_169387_, 750, 750, clientTooltipComponentList, Minecraft.getInstance().font, Minecraft.getInstance().font);
            if (preEvent.isCanceled()) {
                return;
            }

            int i = 0;
            int j = clientTooltipComponentList.size() == 1 ? -2 : 0;

            ClientTooltipComponent clientTooltipComponent;
            int l;
            for(Iterator var8 = clientTooltipComponentList.iterator(); var8.hasNext(); j += clientTooltipComponent.getHeight()) {
                clientTooltipComponent = (ClientTooltipComponent)var8.next();
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

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation("hexerei:textures/book/blank.png")));

            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
            matrixStack.scale(0.003f,0.003f,0.003f);
            matrixStack.translate(-(i + 15)/2f, -(j + 15)/2f, -10);
            RenderTooltipEvent.Color colorEvent = ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, matrixStack, j2, k2, preEvent.getFont(), clientTooltipComponentList);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 0, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 0, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 0, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 0, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderStart(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 0, colorEvent.getBorderEnd(), colorEvent.getBorderEnd(), overlay, light);
            RenderSystem.enableDepthTest();

            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            matrixStack.translate(0.0D, 0.0D, 0.01D);

            matrixStack.scale(1,1,0.00001f);
            int l1 = k2;
//
            Matrix4f matrix4f = matrixStack.last().pose();
            int l2;
            ClientTooltipComponent clientTooltipComponent2;
            for(l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = clientTooltipComponentList.get(l2);
                if(clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip)
                    hexereiBookTooltip.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource, overlay, light);
                else if(clientTooltipComponent2 instanceof ClientTextTooltip clientTextTooltip){
                    int r = (int) (0.25f * 255.0f);
                    int g = (int) (0.25f * 255.0f);
                    int b = (int) (0.25f * 255.0f);
                    int a = (int) (1 * 255.0F);


                    int col = (a << 24) | (r << 16) | (g << 8) | b;
                        Font font = preEvent.getFont();
                        matrix4f = matrixStack.last().pose();
                        font.renderText(clientTextTooltip.text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, false, 0, light);
                        matrixStack.pushPose();
                        matrixStack.translate(0.5f, 0.5f, 7500);
                        matrix4f = matrixStack.last().pose();
                        font.renderText(((ClientTextTooltip) clientTooltipComponentList2.get(l2)).text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, false, 0, light);
                        matrixStack.popPose();


                }
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

            multibuffersource$buffersource.endBatch();
            l1 = k2;

            matrixStack.scale(1,1,333.333f);
            for(l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = (ClientTooltipComponent)clientTooltipComponentList.get(l2);
                RenderSystem.enableDepthTest();
                if(clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip)
                    hexereiBookTooltip.renderImage(preEvent.getFont(), bufferSource, j2, l1, matrixStack, this.itemRenderer, 0, overlay, light);
//                else
//                    clientTooltipComponent2.renderImage(preEvent.getFont(), j2, l1, matrixStack, this.itemRenderer, 0);
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

        }

    }

    @OnlyIn(Dist.CLIENT)
    private static int adjustColor(int p_92720_) {
        return (p_92720_ & -67108864) == 0 ? p_92720_ | -16777216 : p_92720_;
    }

    @OnlyIn(Dist.CLIENT)
    protected static void fillGradient(PoseStack poseStack, VertexConsumer buffer, int p_93126_, int p_93127_, int p_93128_, int p_93129_, float p_93130_, int p_93131_, int p_93132_, int overlay, int light) {
        float $$9 = (float)(p_93131_ >> 24 & 255) / 255.0F;
        float $$10 = (float)(p_93131_ >> 16 & 255) / 255.0F;
        float $$11 = (float)(p_93131_ >> 8 & 255) / 255.0F;
        float $$12 = (float)(p_93131_ & 255) / 255.0F;
        float $$13 = (float)(p_93132_ >> 24 & 255) / 255.0F;
        float $$14 = (float)(p_93132_ >> 16 & 255) / 255.0F;
        float $$15 = (float)(p_93132_ >> 8 & 255) / 255.0F;
        float $$16 = (float)(p_93132_ & 255) / 255.0F;

        Matrix3f normal = poseStack.last().normal();
        Matrix4f matrix4f = poseStack.last().pose();




        int u = 0;
        int v = 0;
        int imageWidth =  1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float)imageWidth;
        float u2 = (u + (float)width) / (float)imageWidth;
        float v1 = (v + 0.0F) / (float)imageHeight;
        float v2 = (v + (float)height) / (float)imageHeight;

        buffer.vertex(matrix4f,  p_93128_,  p_93127_, p_93130_).color($$10, $$11, $$12, $$9) .uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f,  p_93126_,  p_93127_, p_93130_).color($$10, $$11, $$12, $$9) .uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f,  p_93126_,  p_93129_, p_93130_).color($$14, $$15, $$16, $$13).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix4f,  p_93128_,  p_93129_, p_93130_).color($$14, $$15, $$16, $$13).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();



    }


    @OnlyIn(Dist.CLIENT)
    public void drawBookmark(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float zLevel, float rotate, int light, int overlay, PageOn pageOn, int color, boolean isItem, ItemTransforms.TransformType transformType) {

        matrixStack.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(tileEntityIn, matrixStack, isItem, transformType);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f * bookImage.scale,0.5f * bookImage.scale,0.5f * bookImage.scale);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));

        matrixStack.translate((bookImage.x/8.1f - 0.03f/16f) / bookImage.scale, (bookImage.y/8.1f - 0.053f/16f) / bookImage.scale, -zLevel/1600f / bookImage.scale);

        bookImage.effects.forEach((bookImageEffect -> {
            if(bookImageEffect.type.equals("scale")){

                float f = bookImageEffect.amount - 1;

                float x = (f/2f + 1 + ((f/2f) * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
                matrixStack.scale(x,x,x);
            }
        }));

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if(bookImageEffect.type.equals("tilt")){
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
            }
        }));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(rotate));
        if(transformType != ItemTransforms.TransformType.NONE)
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-35));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);


        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation(bookImage.imageLoc)));

        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
        Matrix3f normal = matrixStack.last().normal();
        int u = (int)bookImage.u;
        int v = (int)bookImage.v;
        int imageWidth = (int)bookImage.imageWidth;
        int imageHeight = (int)bookImage.imageHeight;
        int width = (int)bookImage.width;
        int height = (int)bookImage.height;
        float u1 = (u + 0.0F) / (float)imageWidth;
        float u2 = (u + (float)width) / (float)imageWidth;
        float v1 = (v + 0.0F) / (float)imageHeight;
        float v2 = (v + (float)height) / (float)imageHeight;

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if(color != -1){
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }


        if(transformType != ItemTransforms.TransformType.NONE){
            buffer.vertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0, -0.055f / 18 * width).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0, 0.055f / 18 * width).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();

            buffer.vertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0, 0.055f / 18 * width).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0, -0.055f / 18 * width).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        } else{
            buffer.vertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer.vertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        }
        matrixStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {
        drawImage(bookImage, tileEntityIn, matrixStack,bufferSource,zLevel,light,overlay,pageOn,-1, isItem, ItemTransforms.TransformType.NONE);
    }
    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, boolean isItem, ItemTransforms.TransformType transformType) {

        matrixStack.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, transformType);//
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        else if(pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(tileEntityIn, matrixStack, isItem, transformType);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f * bookImage.scale,0.5f * bookImage.scale,0.5f * bookImage.scale);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));

        matrixStack.translate((bookImage.x/8.1f - 0.03f/16f) / bookImage.scale, (bookImage.y/8.1f - 0.053f/16f) / bookImage.scale, -(zLevel + bookImage.z)/1600f);

        bookImage.effects.forEach((bookImageEffect -> {
            if(bookImageEffect.type.equals("scale")){

                float f = bookImageEffect.amount - 1;

                float x = (f/2f + 1 + ((f/2f) * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
                matrixStack.scale(x,x,x);
            }
        }));

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if(bookImageEffect.type.equals("tilt")){
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
            }
        }));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);




        AtomicReference<String> loc = new AtomicReference<>(bookImage.imageLoc);
        AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
        AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);

        AtomicReference<Integer> u = new AtomicReference<>((int)bookImage.u);
        AtomicReference<Integer> v = new AtomicReference<>((int)bookImage.v);
        AtomicReference<Integer> imageWidth = new AtomicReference<>((int)bookImage.imageWidth);
        AtomicReference<Integer> imageHeight = new AtomicReference<>((int)bookImage.imageHeight);
        AtomicReference<Integer> width = new AtomicReference<>((int)bookImage.width);
        AtomicReference<Integer> height = new AtomicReference<>((int)bookImage.height);



        AtomicBoolean flag = new AtomicBoolean(false);

        bookImage.effects.forEach((bookImageEffect -> {
            if(bookImageEffect.type.equals("hover_change_texture")){

                LocalPlayer playerIn = (LocalPlayer)Hexerei.proxy.getPlayer();

                double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
                Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
                Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag.set(true);
                        loc.set(bookImageEffect.hoverImage.imageLoc);
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag.set(true);
                        loc.set(bookImageEffect.hoverImage.imageLoc);
                    }
                }


            }
            if(bookImageEffect.type.equals("hover_overlay")){

                LocalPlayer playerIn = (LocalPlayer)Hexerei.proxy.getPlayer();

                double reach = playerIn.getAttribute((Attribute) ForgeMod.REACH_DISTANCE.get()).getValue();
                Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
                Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                        overlay_image.set(bookImageEffect.hoverImage);
                        overlay_draw.set(true);
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.transform(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                        overlay_image.set(bookImageEffect.hoverImage);
                        overlay_draw.set(true);
                    }
                }

            }



            if(flag.get()){

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if(bookHoverImageEffect.type.equals("scale")){

                        float f = bookHoverImageEffect.amount - 1;

                        float x = (f/2f + 1 + ((f/2f) * Mth.sin((Hexerei.getClientTicks()) / bookHoverImageEffect.speed)));
                        matrixStack.scale(x,x,x);
                    }
                }));

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if(bookHoverImageEffect.type.equals("tilt")){
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-bookHoverImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookHoverImageEffect.speed)));
                    }
                }));

            }


        }));

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation(loc.get())));

        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
        Matrix3f normal = matrixStack.last().normal();

        float u1 = (u.get() + 0.0F) / (float)imageWidth.get();
        float u2 = (u.get() + (float)width.get()) / (float)imageWidth.get();
        float v1 = (v.get() + 0.0F) / (float)imageHeight.get();
        float v2 = (v.get() + (float)height.get()) / (float)imageHeight.get();

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if(color != -1){
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }


        buffer.vertex(matrix,  0,  -0.055f / 18 * height.get(),-0.055f / 18 * width.get()).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0,  0.055f / 18 * height.get(), -0.055f / 18 * width.get()).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, 0.055f / 18 * height.get(),   0.055f / 18 * width.get()).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, -0.055f / 18 * height.get(),  0.055f / 18 * width.get()).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();


        if(overlay_draw.get())
        {
            BookImage ov_img = overlay_image.get();
            VertexConsumer buffer2 = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation(ov_img.imageLoc)));

            float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
            float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
            float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
            float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;

            float overlay_a = 1;
            float overlay_r = 1;
            float overlay_g = 1;
            float overlay_b = 1;

            if(color != -1){
                overlay_r = (float) (color >> 16 & 255) / 255.0F;
                overlay_g = (float) (color >> 8 & 255) / 255.0F;
                overlay_b = (float) (color & 255) / 255.0F;
            }

            matrixStack.pushPose();
            buffer2.vertex(matrix,  ov_img.z/2000f,  -0.055f / 18 * ov_img.height,-0.055f / 18 * ov_img.width).color(overlay_r, overlay_g, overlay_b, overlay_a).uv(overlay_u1, overlay_v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer2.vertex(matrix,  ov_img.z/2000f,  0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).color(overlay_r, overlay_g, overlay_b, overlay_a).uv(overlay_u1, overlay_v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer2.vertex(matrix,  ov_img.z/2000f, 0.055f / 18 * ov_img.height,   0.055f / 18 * ov_img.width).color(overlay_r, overlay_g, overlay_b, overlay_a).uv(overlay_u2, overlay_v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            buffer2.vertex(matrix,  ov_img.z/2000f, -0.055f / 18 * ov_img.height,  0.055f / 18 * ov_img.width).color(overlay_r, overlay_g, overlay_b, overlay_a).uv(overlay_u2, overlay_v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
            matrixStack.popPose();
        }

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawTitle(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);



        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f,0.5f,0.5f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f/16f, -0.053f/16f, 0);
        matrixStack.translate(4.75f/16f, 0f/16f, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation("hexerei:textures/book/title.png")));

        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
        Matrix3f normal = matrixStack.last().normal();
        int u = 0;
        int v = 0;
        int imageWidth = 100;
        int imageHeight = 26;
        int width = 100;
        int height = 26;
        float u1 = (u + 0.0F) / (float)imageWidth;
        float u2 = (u + (float)width) / (float)imageWidth;
        float v1 = (v + 0.0F) / (float)imageHeight;
        float v2 = (v + (float)height) / (float)imageHeight;

        buffer.vertex(matrix,  0,  -0.055f / 18 * height,-0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0,  0.055f / 18 * height, -0.055f / 18 * width).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, 0.055f / 18 * height,   0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();
        buffer.vertex(matrix,  0, -0.055f / 18 * height,  0.055f / 18 * width).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(normal, 1F, 0F, 0F).endVertex();

        matrixStack.popPose();


    }


    @OnlyIn(Dist.CLIENT)
    public void drawCharacter(char character,BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float mouseX, float mouseY, int xIn, int yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {


        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ClientProxy.TEXT.get(character));

        matrixStack.pushPose();

        if(pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        if(pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
        else if(pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-8.35f / 16f, 5.5f / 16f, -0.07f / 16f);
        matrixStack.scale(0.5f,0.5f,0.5f);
        matrixStack.translate(this.lineHeight + yIn * 0.05f, -this.lineWidth - (ClientProxy.TEXT_WIDTH.get(character) / 2) - xIn * 0.042f, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));

        RenderSystem.setShader(GameRenderer::getNewEntityShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());


        matrixStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);
        Matrix3f normal = matrixStack.last().normal();
        buffer.vertex(matrix, -0.032f, -0.032f, 0.0f).color(0.12f,0.12f,0.12f, 1.0f).uv(sprite.getU0(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
        buffer.vertex(matrix, 0.032f, -0.032f, 0.0f) .color(0.12f,0.12f,0.12f, 1.0f).uv(sprite.getU0(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
        buffer.vertex(matrix, 0.032f, 0.032f, 0.0f)  .color(0.12f,0.12f,0.12f, 1.0f).uv(sprite.getU1(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
        buffer.vertex(matrix, -0.032f, 0.032f, 0.0f) .color(0.12f,0.12f,0.12f, 1.0f).uv(sprite.getU1(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();

        //shadow for special font
//        matrixStack.translate(0.001,0.001,-0.001);
//        normal = matrixStack.last().normal();
//        buffer.vertex(matrix, -0.032f, -0.032f, 0.0f).color(0.03f,0.03f,0.03f, 1.0f).uv(sprite.getU0(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
//        buffer.vertex(matrix, 0.032f, -0.032f, 0.0f) .color(0.03f,0.03f,0.03f, 1.0f).uv(sprite.getU0(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
//        buffer.vertex(matrix, 0.032f, 0.032f, 0.0f)  .color(0.03f,0.03f,0.03f, 1.0f).uv(sprite.getU1(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
//        buffer.vertex(matrix, -0.032f, 0.032f, 0.0f) .color(0.03f,0.03f,0.03f, 1.0f).uv(sprite.getU1(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(normal, 1,0,0).endVertex();
        matrixStack.popPose();

    }

    public void resetLines(){
        this.lineWidth = 0;
        this.lineHeight = 0;
    }

    @OnlyIn(Dist.CLIENT)
    public BookParagraphElements resetLinesNewBox(BookParagraph bookParagraph, int boxOn){
        this.lineWidth = 0;
        this.lineHeight = 0;
        if(boxOn + 1 < bookParagraph.paragraphElements.toArray().length)
            return (BookParagraphElements)(bookParagraph.paragraphElements.toArray()[boxOn+1]);
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawString(BookParagraph bookParagraph, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource.BufferSource bufferSource, float mouseX, float mouseY, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        TranslatableComponent pageText = bookParagraph.translatablePassage;
        int wordNumber = -1;
        int boxOn = 0;
        BookParagraphElements activeElement = (bookParagraph.paragraphElements.get(0));



        boolean drawSpecialFont = HexConfig.FANCY_FONT_IN_BOOK.get();


        if(drawSpecialFont){
            boolean findNewWord = true;
            String[] words = pageText.getString().trim().split("\\s+");
            String pageTextString = pageText.getString();
            int itor = -1;
            for(String word : words){
                itor++;
                if(word.length() > 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (word.charAt(0) == '%' && word.charAt(1) == 'k') {
                        for(int i = 2; i < word.length(); i++){
                            stringBuilder.append(word.charAt(i));
                        }
                        String temp = stringBuilder.toString();

                        String alt = "key." + temp;

                        for (KeyMapping k : ClientProxy.keys) {
                            String name = k.getName();
                            if (name.equals(temp) || name.equals(alt)) {
                                String keyName = k.getTranslatedKeyMessage().getString();
                                if(keyName.length() <= 1)
                                    keyName = keyName.toUpperCase(Locale.ROOT);
                                words[itor] = keyName;
                                pageTextString = pageTextString.replaceAll(word, words[itor]);
                            }
                        }

                    }
                }
            }
            char[] text = pageTextString.toCharArray();

            int[] wordLength = new int[words.length];
            float[] wordWidths = new float[words.length];
            for (int k = 0; k < words.length; k++) {
                wordLength[k] = words[k].length();
                char[] wordText = words[k].toCharArray();
                for (char character : wordText) {
                    if (ClientProxy.TEXT.containsKey(character))
                        wordWidths[k] += ClientProxy.TEXT_WIDTH.get(character);
                    else
                        wordWidths[k] += ClientProxy.TEXT_WIDTH.get(' ');
                }
            }

            boolean breakBool = false;
            for (int i = 0; i < text.length; i = i) {
                if (breakBool)
                    break;
                if (text[i] == '\n') {
                    this.lineWidth = 0;
                    this.lineHeight += 0.05f;
                    if (this.lineHeight >= activeElement.height * 0.05f) {
                        activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                        if (activeElement == null) {
                            breakBool = true;
                            break;

                        }
                    }
                    i++;
                } else if (text[i] == ' ') {
                    findNewWord = true;
                    drawCharacter(' ', tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
                    this.lineWidth += ClientProxy.TEXT_WIDTH.get(' ');
                    if (this.lineWidth > activeElement.width * 0.02) {
                        this.lineWidth = 0;
                        this.lineHeight += 0.05f;
                        if (this.lineHeight >= activeElement.height * 0.05f) {
                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
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
                    if (this.lineWidth + wordWidths[wordNumber] > activeElement.width * 0.02) {
                        this.lineWidth = 0;
                        this.lineHeight += 0.05f;
                        if (this.lineHeight >= activeElement.height * 0.05f) {
                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                            if (activeElement == null) {
                                breakBool = true;
                                break;

                            }
                        }
                    }
                    for (char character : wordText) {
                        if (ClientProxy.TEXT.containsKey(character)) {
                            drawCharacter(character, tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
                            this.lineWidth += ClientProxy.TEXT_WIDTH.get(character);
                            if (this.lineWidth > activeElement.width * 0.02) {
                                this.lineWidth = 0;
                                this.lineHeight += 0.05f;
                                if (this.lineHeight >= activeElement.height * 0.05f) {
                                    activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                                    if (activeElement == null) {
                                        breakBool = true;
                                        break;

                                    }
                                }
                            }
                        } else {
                            drawCharacter(' ', tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
                            this.lineWidth += ClientProxy.TEXT_WIDTH.get(' ');
                            if (this.lineWidth > activeElement.width * 0.02) {
                                this.lineWidth = 0;
                                this.lineHeight += 0.05f;
                                if (this.lineHeight >= activeElement.height * 0.05f) {
                                    activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                                    if (activeElement == null) {
                                        breakBool = true;
                                        break;

                                    }
                                }
                            }
                        }
                    }

                    i += wordLength[wordNumber];
                }
            }
        }

        if(!drawSpecialFont){

            Font font = Minecraft.getInstance().font;
            boolean findNewWord = true;
            String[] words = pageText.getString().trim().split("\\s+");
            String pageTextString = pageText.getString();


            int itor = -1;
            for(String word : words){
                itor++;
                if(word.length() > 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (word.charAt(0) == '%' && word.charAt(1) == 'k') {
                        for(int i = 2; i < word.length(); i++){
                            stringBuilder.append(word.charAt(i));
                        }
                        String temp = stringBuilder.toString();

                        String alt = "key." + temp;

//                        for (KeyMapping k : ClientProxy.keys) {
//                            String name = k.getName();
//                            if (name.equals(temp) || name.equals(alt)) {
//                                String keyName = k.getTranslatedKeyMessage().getString();
//                                if(keyName.length() <= 1)
//                                    keyName = keyName.toUpperCase(Locale.ROOT);
//                                words[itor] = keyName;
//                                pageTextString = pageTextString.replaceAll(word, words[itor]);
//                            }
//                        }

                    }
                }

            }
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
                        activeElement = resetLinesNewBox(bookParagraph, boxOn++);
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
                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
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
                    if (this.lineWidth + wordWidths[wordNumber] > activeElement.width * 3.75f) {
                        this.lineWidth = 0;
                        this.lineHeight++;
                        strings.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        if (this.lineHeight >= activeElement.height) {
                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
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
                                activeElement = resetLinesNewBox(bookParagraph, boxOn++);
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

            if(!stringBuilder.toString().isEmpty())
                strings.add(stringBuilder.toString());

            matrixStack.pushPose();

            if(pageOn == PageOn.LEFT_PAGE)
                translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
            else if(pageOn == PageOn.LEFT_PAGE_UNDER)
                translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
            else if(pageOn == PageOn.LEFT_PAGE_PREV)
                translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
            if(pageOn == PageOn.RIGHT_PAGE)
                translateToRightPage(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
            else if(pageOn == PageOn.RIGHT_PAGE_UNDER)
                translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);
            else if(pageOn == PageOn.RIGHT_PAGE_PREV)
                translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemTransforms.TransformType.NONE);

            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
            matrixStack.translate(-8.35f / 16f, 4.5f / 16f, -0.01f / 16f);
            matrixStack.scale(0.00272f,0.00272f,0.00272f);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));

            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//            font.drawInBatch(s1, (activeElement.x * 9f) - 24, (activeElement.y * 9f) - 4, 16777216, false, matrixStack.last().pose(), bufferSource, false, 0, light);


            int boxId = 0;
            int linenumber = 0;
            boolean flag = true;
            while(flag) {
                ArrayList<String> remainder = new ArrayList<>();
                if(bookParagraph.paragraphElements.size() > boxId && bookParagraph.paragraphElements.get(boxId)!=null) {

                    BookParagraphElements box = bookParagraph.paragraphElements.get(boxId);

                    for (String s1 : strings) {
                        if((linenumber+1) * font.lineHeight <= Math.round(box.height * font.lineHeight) + 1) {
                            float offsetX = 0;
                            if(bookParagraph.align.equals("middle"))
                                offsetX = (font.width(s1)) / 2;

                            font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + Math.round(linenumber * font.lineHeight)) - 4, HexereiUtil.getColorValue(0.12f,0.12f,0.12f), false, matrixStack.last().pose(), bufferSource, false, 0, light);
                            matrixStack.pushPose();
                            matrixStack.translate(0.25f, 0.25f, 1 / 16f);
                            font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + Math.round(linenumber * font.lineHeight)) - 4, 16777216, false, matrixStack.last().pose(), bufferSource, false, 0, light);
                            matrixStack.popPose();
                        }
                        else {
                            remainder.add(s1);
                        }
                        ++linenumber;
                    }
                }
                else
                    flag = false;
                if(remainder.isEmpty())
                    flag = false;
                else {
                    boxId ++;
                    linenumber = 0;
                    strings = remainder;
                }
            }

//            font.drawInBatch(pageText, (xIn * 9f) - 24, (yIn * 9f) - 4, 16777216, false, matrixStack.last().pose(), bufferSource, false, 0, light);

            multibuffersource$buffersource.endBatch();
            matrixStack.popPose();
        }

        resetLines();

    }

    public static enum PageOn {
        LEFT_PAGE,
        LEFT_PAGE_UNDER,
        LEFT_PAGE_PREV,
        RIGHT_PAGE,
        RIGHT_PAGE_UNDER,
        RIGHT_PAGE_PREV,
        MIDDLE_BUTTON

    }

}
