package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HexereiBookItemRenderer extends CustomItemRenderer {

    float degreesOpened;
    float degreesOpened2;
    float yPos;
    float xPos;
    float zPos;
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static BookOfShadowsAltarTile loadBlockEntityFromItem(ItemStack item) {
        if (item.getItem() instanceof HexereiBookItem) {
            BookOfShadowsAltarTile te = new BookOfShadowsAltarTile(BlockPos.ZERO, ModBlocks.BOOK_OF_SHADOWS_ALTAR.get().defaultBlockState());
            te.itemHandler.setStackInSlot(0, item);
            return te;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTileStuff(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        BookOfShadowsAltarTile altarTile = loadBlockEntityFromItem(stack);


        if(altarTile == null)
            return;

        altarTile.tickCount = ClientEvents.getClientTicks();

        if(altarTile.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem) {

            BookData bookData = stack.getOrDefault(ModDataComponents.BOOK, BookData.EMPTY);
            boolean isBookOfShadows = bookData.book().equals(HexereiUtil.getResource("book_of_shadows"));

            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            matrixStackIn.pushPose();
            altarTile.degreesOpened = 90;
            altarTile.degreesFlopped = 90;
            if(bookData.isOpened()) {
                altarTile.degreesOpened = 18;
                altarTile.degreesFlopped = 0;
                this.degreesOpened = -10;
                altarTile.degreesSpun = 270;
            }

            altarTile.degreesSpunRender = altarTile.degreesSpun;
            altarTile.degreesFloppedRender = altarTile.degreesFlopped;
            altarTile.degreesOpenedRender = altarTile.degreesOpened;
            altarTile.pageOneRotationRender = altarTile.pageOneRotation;
            altarTile.pageTwoRotationRender = altarTile.pageTwoRotation;
            if (altarTile.degreesOpened != 90) {
                altarTile.drawing.drawPages(altarTile, 0, 0, 0, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, PageDrawing.DrawingType.GUI, transformType, ClientEvents.getPartial());
            }

//            this.itemRenderer = Minecraft.getInstance().getItemRenderer();


            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            if (bookData.isOpened()) {
                altarTile.degreesOpened = 18;
                altarTile.degreesFlopped = 0;
                this.degreesOpened = -10;
                altarTile.degreesSpun = 270;
            } else {
                altarTile.degreesOpened = 90;
                altarTile.degreesFlopped = 90;
                if (transformType == ItemDisplayContext.GUI) {
                    yPos = 6 / 16f;
                    xPos = 2 / 16f;
                    zPos = -12 / 32f;
                    matrixStackIn.scale(1.35f, 1.35f, 1.35f);
                }
                if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                    this.degreesOpened2 = 90;
                    xPos = 4 / 16f;
                    zPos = -12 / 32f;
                }
                if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                    this.degreesOpened2 = 90;
                    xPos = 4 / 16f;
                    zPos = -1 / 32f;
                }
            }


            altarTile.degreesSpunRender = altarTile.degreesSpun;
            altarTile.degreesFloppedRender = altarTile.degreesFlopped;
            altarTile.degreesOpenedRender = altarTile.degreesOpened;
            altarTile.pageOneRotationRender = altarTile.pageOneRotation;
            altarTile.pageTwoRotationRender = altarTile.pageTwoRotation;


            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(altarTile.degreesOpenedRender - 90));
            matrixStackIn.translate(1f / 32f * (altarTile.degreesOpenedRender / 90f), 1f / 32f * (1 - (altarTile.degreesOpenedRender / 90f)), 0);
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            if (isBookOfShadows) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            } else {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            }
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(altarTile.degreesOpenedRender - 90)));
            matrixStackIn.translate(-1f / 32f * (altarTile.degreesOpenedRender / 90f), 1f / 32f * (1 - (altarTile.degreesOpenedRender / 90f)), 0);
            if (isBookOfShadows) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            } else {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            }
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
            matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
            if (isBookOfShadows) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            } else {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            }
            matrixStackIn.popPose();

            if(altarTile.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpened / 1.12f) / 90f) * (-altarTile.pageOneRotationRender)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpened / 1.12f) / 90f) * (altarTile.pageTwoRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpened / 1.12f) / 90f) * (-altarTile.pageOneRotation)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpened / 1.12f) / 90f) * (altarTile.pageTwoRotation / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(altarTile.turnPage == 1 || altarTile.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - altarTile.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - altarTile.degreesOpened / 1.12f) / 90f) * (-altarTile.pageOneRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(altarTile.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpened / 1.12f) / 90f)*(-altarTile.pageTwoRotationRender)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpened / 1.12f) / 90f)*(altarTile.pageOneRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(altarTile.turnPage == 2 || altarTile.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((altarTile.degreesSpunRender) / 57.3f) / 32f * (altarTile.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, ((BookOfShadowsAltarTile.easeFlop(1 - altarTile.degreesFlopped / 90) - 1) / 16f), 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(altarTile.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80f - altarTile.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - altarTile.degreesOpened / 1.12f) / 90f) * (-altarTile.pageTwoRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }


            matrixStackIn.popPose();
        }



    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    float f = (float) (color >> 16 & 255) / 255.0F;
                    float f1 = (float) (color >> 8 & 255) / 255.0F;
                    float f2 = (float) (color & 255) / 255.0F;
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