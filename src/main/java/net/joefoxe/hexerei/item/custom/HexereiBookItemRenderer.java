package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Altar;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.renderer.HerbJarRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;
import java.util.UUID;


public class HexereiBookItemRenderer extends CustomItemRendererWithPageDrawing {

    float degreesOpened;
    float degreesOpened2;
    float yPos;
    float xPos;
    float zPos;
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

//        matrixStackIn.pushPose();
//        matrixStackIn.translate(0.2, -0.1, -0.10);
//        BlockItem item = ((BlockItem) stack.getItem());
//        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
//        matrixStackIn.popPose();

        this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    public static BookOfShadowsAltarTile loadBlockEntityFromItem(ItemStack item) {
        if (item.getItem() instanceof HexereiBookItem) {
            BookOfShadowsAltarTile te = new BookOfShadowsAltarTile(BlockPos.ZERO, ModBlocks.BOOK_OF_SHADOWS_ALTAR.get().defaultBlockState());
            te.itemHandler.setStackInSlot(0, item);
            return te;
        }
        return null;
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
    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTileStuff(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        BookOfShadowsAltarTile tileEntityIn = loadBlockEntityFromItem(stack);


        if(tileEntityIn == null)
            return;

        tileEntityIn.tickCount = Hexerei.getClientTicks();

        if(tileEntityIn.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem){

            CompoundTag tag = stack.getOrCreateTag();

            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            if(tag.contains("opened"))
            {
                if(tag.getBoolean("opened")){
                    tileEntityIn.degreesOpened = 18;
                    tileEntityIn.degreesFlopped = 0;
                    this.degreesOpened = -10;
                    tileEntityIn.degreesSpun = 270;
                }
                else {
                    tileEntityIn.degreesOpened = 90;
                    tileEntityIn.degreesFlopped = 90;
                    if(transformType == ItemTransforms.TransformType.GUI)
                        yPos = 3/16f;
                    if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                    {
                        this.degreesOpened2 = 90;
                        xPos = 4/16f;
                        zPos = -12/32f;
                    }
                    if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                    {
                        this.degreesOpened2 = 90;
                        xPos = 4/16f;
                        zPos = -1/32f;
                    }
                }
            }else {
                tileEntityIn.degreesOpened = 90;
                tileEntityIn.degreesFlopped = 90;
                if(transformType == ItemTransforms.TransformType.GUI)
                    yPos = 3/16f;
                if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                {
                    this.degreesOpened2 = 90;
                    xPos = 4/16f;
                    zPos = -12/32f;
                }
                if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                {
                    this.degreesOpened2 = 90;
                    xPos = 4/16f;
                    zPos = -1/32f;
                }
            }



            tileEntityIn.degreesSpunRender = tileEntityIn.degreesSpun;
            tileEntityIn.degreesFloppedRender = tileEntityIn.degreesFlopped;
            tileEntityIn.degreesOpenedRender = tileEntityIn.degreesOpened;
            tileEntityIn.pageOneRotationRender = tileEntityIn.pageOneRotation;
            tileEntityIn.pageTwoRotationRender = tileEntityIn.pageTwoRotation;
            if (tileEntityIn.degreesOpened != 90) {
                try {

                    drawPages(tileEntityIn, matrixStackIn, (MultiBufferSource.BufferSource) bufferIn, combinedLightIn, combinedOverlayIn, true, transformType, 0);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            else{

                drawBaseButtons(tileEntityIn, matrixStackIn, (MultiBufferSource.BufferSource) bufferIn, combinedLightIn, combinedOverlayIn, false, false, tag.getInt("chapter"), tag.getInt("page"), true, transformType, true);
            }

//            this.itemRenderer = Minecraft.getInstance().getItemRenderer();


            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            if(tag.contains("opened"))
            {
                if(tag.getBoolean("opened")){
                    tileEntityIn.degreesOpened = 18;
                    tileEntityIn.degreesFlopped = 0;
                    this.degreesOpened = -10;
                    tileEntityIn.degreesSpun = 270;
                }
                else {
                    tileEntityIn.degreesOpened = 90;
                    tileEntityIn.degreesFlopped = 90;
                    if(transformType == ItemTransforms.TransformType.GUI)
                        yPos = 3/16f;
                    if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                    {
                        this.degreesOpened2 = 90;
                        xPos = 4/16f;
                        zPos = -12/32f;
                    }
                    if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                    {
                        this.degreesOpened2 = 90;
                        xPos = 4/16f;
                        zPos = -1/32f;
                    }
                }
            }else {
                tileEntityIn.degreesOpened = 90;
                tileEntityIn.degreesFlopped = 90;
                if(transformType == ItemTransforms.TransformType.GUI)
                    yPos = 3/16f;
                if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                {
                    this.degreesOpened2 = 90;
                    xPos = 4/16f;
                    zPos = -12/32f;
                }
                if(transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                {
                    this.degreesOpened2 = 90;
                    xPos = 4/16f;
                    zPos = -1/32f;
                }
            }


            tileEntityIn.degreesSpunRender = tileEntityIn.degreesSpun;
            tileEntityIn.degreesFloppedRender = tileEntityIn.degreesFlopped;
            tileEntityIn.degreesOpenedRender = tileEntityIn.degreesOpened;
            tileEntityIn.pageOneRotationRender = tileEntityIn.pageOneRotation;
            tileEntityIn.pageTwoRotationRender = tileEntityIn.pageTwoRotation;


            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(tileEntityIn.degreesOpened));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, (float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER.get().defaultBlockState(), HexereiUtil.getColorValue(HexereiBookItem.getColor2(stack)));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColorStatic(stack) : HexereiUtil.getColorValue(col));
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate(-(float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, -(float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-tileEntityIn.degreesOpened));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, -(float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK.get().defaultBlockState(), HexereiUtil.getColorValue(HexereiBookItem.getColor2(stack)));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColorStatic(stack) : HexereiUtil.getColorValue(col));
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BINDING.get().defaultBlockState(), HexereiUtil.getColorValue(HexereiBookItem.getColor2(stack)));
            matrixStackIn.popPose();

            if(tileEntityIn.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (tileEntityIn.pageTwoRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotation)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (tileEntityIn.pageTwoRotation / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.turnPage == 1 || tileEntityIn.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f)*(-tileEntityIn.pageTwoRotationRender)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f)*(tileEntityIn.pageOneRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.turnPage == 2 || tileEntityIn.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }
        }



    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.IModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch(rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());

                    poseStack.translate(0.2, -0.1, -0.1);
                    net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
            }

        }
    }

}