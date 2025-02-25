package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.tileentity.CandleDipperTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CandleDipperRenderer implements BlockEntityRenderer<CandleDipperTile> {


    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    @Override
    public void render(CandleDipperTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CandleDipperTile))
            return;

        float rotation = 0;

        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            rotation = 180;
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            rotation = 0;
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            rotation = 90;
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            rotation = 270;
        }

        matrixStackIn.pushPose();
        matrixStackIn.translate(
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z()));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
        if(!(Block.byItem(tileEntityIn.getItems().get(0).getItem()) instanceof Candle && CandleItem.getBaseLayer(tileEntityIn.getItems().get(0)) != null)) {
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK_BASE.get().defaultBlockState());
        }
        matrixStackIn.popPose();

        if(!tileEntityIn.getItems().get(0).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z()));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.getItems().get(0).getItem() == Items.STRING) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK.get().defaultBlockState());
            }
            else {
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                matrixStackIn.translate(0, 3.25f/16f + 0.0001f, 0);

                if(!(Block.byItem(tileEntityIn.getItems().get(0).getItem()) instanceof Candle)) {
                    matrixStackIn.scale(0.4f, 0.4f, 0.4f);
                    matrixStackIn.translate(0, -1.5f/16f, 0);
                } else {
                    matrixStackIn.scale(0.9f, 0.9f, 0.9f);
                    matrixStackIn.translate(0, 0.5f/16f, 0);
                }
                renderItem(tileEntityIn.getItems().get(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
            matrixStackIn.popPose();
        }
        if(tileEntityIn.getItems().get(0).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z()));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.dipperSlots.get(0).timesDipped == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_1.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(0).timesDipped == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_2.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(0).timesDipped == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_3.get().defaultBlockState());
            matrixStackIn.popPose();
        }




        matrixStackIn.pushPose();
        matrixStackIn.translate(
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.x(), tileEntityIn.dipperSlots.get(1).pos.x()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.y(), tileEntityIn.dipperSlots.get(1).pos.y()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.z(), tileEntityIn.dipperSlots.get(1).pos.z()));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
        if(!(Block.byItem(tileEntityIn.getItems().get(1).getItem()) instanceof Candle && CandleItem.getBaseLayer(tileEntityIn.getItems().get(1)) != null)) {
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK_BASE.get().defaultBlockState());
        }
        matrixStackIn.popPose();

        if(!tileEntityIn.getItems().get(1).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.x(), tileEntityIn.dipperSlots.get(1).pos.x()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.y(), tileEntityIn.dipperSlots.get(1).pos.y()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.z(), tileEntityIn.dipperSlots.get(1).pos.z()));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.getItems().get(1).getItem() == Items.STRING) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK.get().defaultBlockState());
            }
            else {
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                matrixStackIn.translate(0, 3.25f/16f + 0.0001f, 0);

                if(!(Block.byItem(tileEntityIn.getItems().get(1).getItem()) instanceof Candle)) {
                    matrixStackIn.scale(0.4f, 0.4f, 0.4f);
                    matrixStackIn.translate(0, -1.5f/16f, 0);
                } else {
                    matrixStackIn.scale(0.9f, 0.9f, 0.9f);
                    matrixStackIn.translate(0, 0.5f/16f, 0);
                }
                renderItem(tileEntityIn.getItems().get(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
            matrixStackIn.popPose();
        }


        if(tileEntityIn.getItems().get(1).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.dipperSlots.get(1).pos.x(), tileEntityIn.dipperSlots.get(1).pos.y(), tileEntityIn.dipperSlots.get(1).pos.z());
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.dipperSlots.get(1).timesDipped == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_1.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(1).timesDipped == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_2.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(1).timesDipped == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_3.get().defaultBlockState());
            matrixStackIn.popPose();
        }



        matrixStackIn.pushPose();
        matrixStackIn.translate(
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.x(), tileEntityIn.dipperSlots.get(2).pos.x()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.y(), tileEntityIn.dipperSlots.get(2).pos.y()),
                Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.z(), tileEntityIn.dipperSlots.get(2).pos.z()));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
        if(!(Block.byItem(tileEntityIn.getItems().get(2).getItem()) instanceof Candle && CandleItem.getBaseLayer(tileEntityIn.getItems().get(2)) != null)) {
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK_BASE.get().defaultBlockState());
        }
        matrixStackIn.popPose();

        if(!tileEntityIn.getItems().get(2).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.x(), tileEntityIn.dipperSlots.get(2).pos.x()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.y(), tileEntityIn.dipperSlots.get(2).pos.y()),
                    Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.z(), tileEntityIn.dipperSlots.get(2).pos.z()));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.getItems().get(2).getItem() == Items.STRING) {
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_WICK.get().defaultBlockState());
            }
            else {
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                matrixStackIn.translate(0, 3.25f/16f + 0.0001f, 0);

                if(!(Block.byItem(tileEntityIn.getItems().get(2).getItem()) instanceof Candle)) {
                    matrixStackIn.scale(0.4f, 0.4f, 0.4f);
                    matrixStackIn.translate(0, -1.5f/16f, 0);
                } else {
                    matrixStackIn.scale(0.9f, 0.9f, 0.9f);
                    matrixStackIn.translate(0, 0.5f/16f, 0);
                }
                renderItem(tileEntityIn.getItems().get(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
            matrixStackIn.popPose();
        }

        if(tileEntityIn.getItems().get(2).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.dipperSlots.get(2).pos.x(), tileEntityIn.dipperSlots.get(2).pos.y(), tileEntityIn.dipperSlots.get(2).pos.z());
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if(tileEntityIn.dipperSlots.get(2).timesDipped == 1)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_1.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(2).timesDipped == 2)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_2.get().defaultBlockState());
            if(tileEntityIn.dipperSlots.get(2).timesDipped == 3)
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CANDLE_DIPPER_CANDLE_3.get().defaultBlockState());
            matrixStackIn.popPose();
        }


    }

    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int overlayLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                overlayLightIn, matrixStackIn, bufferIn, level, 1);
    }


    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }


}
