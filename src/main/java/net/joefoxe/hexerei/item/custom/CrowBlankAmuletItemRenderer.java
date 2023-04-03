package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.OptionalInt;

public class CrowBlankAmuletItemRenderer extends CustomItemRenderer {

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

//        matrixStackIn.pushPose();
//        matrixStackIn.translate(0.2, -0.1, -0.10);
//        BlockItem item = ((BlockItem) stack.getItem());
//        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, null);
//        matrixStackIn.popPose();

        this.renderTileStuff(stack.getOrCreateTag(), stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }



    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GUI, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn, ItemTransforms.TransformType transformType) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, transformType, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }

    public OptionalInt getFramedMapId(ItemStack stack) {
        if (stack.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId(stack);
            if (integer != null) {
                return OptionalInt.of(integer);
            }
        }

        return OptionalInt.empty();
    }

    public void renderTileStuff(CompoundTag tag, ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();
        matrixStackIn.translate(45/64f, 13/32f, 7/16f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        ItemStack otherItem = ItemStack.EMPTY;


        CompoundTag tag2 = stack.getOrCreateTag();
        if(tag2.contains("Items")){
            ListTag list = tag2.getList("Items", 10);
            ItemStack other = ItemStack.of(list.getCompound(0));
            if (!other.isEmpty() && !list.isEmpty()) {
                otherItem = other;
            }
        }

        Level level = Minecraft.getInstance().level;


        if(otherItem.is(Items.FILLED_MAP) && level != null) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(-1/2f, -1/2f, 0);
            matrixStackIn.scale(1F/128F, 1F/128F, 1);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180f));
            matrixStackIn.translate(-128F, -128F, 0);
            matrixStackIn.scale(0.8695f, 0.8695f, 1);
            matrixStackIn.translate(9.5F, 9.5F, 1F/128F);

            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(otherItem, level);
            if(mapitemsaveddata != null && getFramedMapId(otherItem).isPresent())
                Minecraft.getInstance().gameRenderer.getMapRenderer().render(matrixStackIn, bufferIn, getFramedMapId(otherItem).getAsInt(), mapitemsaveddata, true, combinedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.translate(0, 0, 0.03F);
            renderItem(new ItemStack(ModItems.CROW_BLANK_AMULET_TRINKET_FRAME.get()), matrixStackIn, bufferIn, combinedLightIn, ItemTransforms.TransformType.FIXED);
        } else {

            renderItem(new ItemStack(ModItems.CROW_BLANK_AMULET_TRINKET.get(), 1), matrixStackIn, bufferIn, combinedLightIn, ItemTransforms.TransformType.FIXED);
            matrixStackIn.pushPose();
            BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(otherItem, null, null, 0);
            if(itemModel.isGui3d()) {
                matrixStackIn.translate(0, -0.025F, -0.035F);
                matrixStackIn.scale(1, 1, 0.1F);
                matrixStackIn.last().normal().transpose();
            } else {
                matrixStackIn.translate(0, -0.025F, -0.025F);
                matrixStackIn.scale(0.85F, 0.85F, 1F);
            }
            matrixStackIn.scale(0.7F, 0.7F, 0.7F);
            renderItem(otherItem, matrixStackIn, bufferIn, combinedLightIn, ItemTransforms.TransformType.FIXED);
            matrixStackIn.popPose();
        }



        matrixStackIn.popPose();



    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;

//                    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_) {
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}