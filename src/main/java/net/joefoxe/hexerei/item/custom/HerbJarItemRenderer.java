package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.joefoxe.hexerei.item.ModItems;
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


public class HerbJarItemRenderer extends CustomItemRenderer {

    private HerbJarRenderer renderer;

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.2, -0.1, -0.10);
        BlockItem item = ((BlockItem) stack.getItem());
        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.popPose();

        this.renderTileStuff(stack.getOrCreateTag(), stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    @OnlyIn(Dist.CLIENT)
    public static HerbJarTile loadBlockEntityFromItem(CompoundTag tag, ItemStack item) {
        if (item.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof HerbJar herbJar) {
                HerbJarTile te = (HerbJarTile)herbJar.newBlockEntity(BlockPos.ZERO, block.defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
                te.itemHandler.deserializeNBT(tag.getCompound("Inventory"));
                te.dyeColor = getCustomColor(tag);
                if(item.hasCustomHoverName())
                    te.customName = item.getHoverName();
//                if (te != null) te.load(tag);
                return te;
            }
        }
        return null;
    }


    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
    }

    public void renderTileStuff(CompoundTag tag, ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        HerbJarTile tileEntityIn = loadBlockEntityFromItem(tag, stack);

        String name = tileEntityIn.getDisplayName().getString();
        DyeColor col = HexereiUtil.getDyeColorNamed(name);

        int color = col != null ? HexereiUtil.getColorValue(col) : HexereiUtil.getColorStatic(stack);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate(8D/16D, 4.25D/16D, 4D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(0));
        matrixStackIn.translate(0.2, -0.1, -0.1);
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.HERB_JAR.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, (color != 0x422F1E && color != 0)), color);
        matrixStackIn.popPose();


        if(!tileEntityIn.itemHandler.isEmpty())
        {
            BlockState state = null; //ModBlocks.HERB_JAR_GENERIC.get().defaultBlockState()

            Item item = tileEntityIn.itemHandler.getContents().get(0).getItem();
            if(item == ModBlocks.BELLADONNA_FLOWER.get().asItem() ||
                    item == ModItems.BELLADONNA_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_BELLADONNA.get().defaultBlockState();
            if(item == ModBlocks.MUGWORT_BUSH.get().asItem() ||
                    item == ModItems.MUGWORT_LEAVES.get().asItem() ||
                    item == ModItems.MUGWORT_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_MUGWORT.get().defaultBlockState();
            if(item == ModBlocks.MANDRAKE_FLOWER.get().asItem() ||
                    item == ModItems.MANDRAKE_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_MANDRAKE_FLOWER.get().defaultBlockState();
            if(item == ModItems.MANDRAKE_ROOT.get())
                state = ModBlocks.HERB_JAR_MANDRAKE_ROOT.get().defaultBlockState();
            if(item == ModBlocks.YELLOW_DOCK_BUSH.get().asItem() ||
                    item == ModItems.YELLOW_DOCK_LEAVES.get().asItem() ||
                    item == ModItems.YELLOW_DOCK_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_YELLOW_DOCK.get().defaultBlockState();

            Random rand = new Random(0);

            Minecraft minecraft = Minecraft.getInstance();
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            ItemModelShaper shaper = itemRenderer.getItemModelShaper();
            boolean is3dModel = shaper.getModelManager().getModel(new ModelResourceLocation(item.getRegistryName(), "inventory")).isGui3d();
            for(int a = 0; a < ((float)tileEntityIn.itemHandler.getContents().get(0).getCount() / 1024f) * 10f; a++){
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.2, -0.1, -0.10);
//                boolean is3dModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory")).isGui3d();
                if(is3dModel) {
                    matrixStackIn.translate(0, 1.5D / 16D, 0);
                }

                if(state != null){
                    matrixStackIn.translate(8D / 16D, 0.5D / 16D * a, 8D / 16D);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 * a));
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(0 ));
                    renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                } else {

                    matrixStackIn.translate(8D / 16D, 0.5D / 16D * a + 1D/16D, 8D / 16D);
                    matrixStackIn.scale(0.4f,0.4f,0.4f);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rand.nextInt(90) * a));
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(0 ));
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(80 + rand.nextInt(20)));
                    if(is3dModel)
                        matrixStackIn.scale(1.20f, 1.20f, 1.20f);
                    renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), matrixStackIn, bufferIn, combinedLightIn);
                }


                matrixStackIn.popPose();
            }
        }


        int i = 0x464F56;
        int j = (int)((double) NativeImage.getR(i) * 0.4D);
        int k = (int)((double)NativeImage.getG(i) * 0.4D);
        int l = (int)((double)NativeImage.getB(i) * 0.4D);
        int i1 = NativeImage.combine(   0, l, k, j);


        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate(8D / 16D, 8D / 16D, 1 - 12.05D / 16D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));

        matrixStackIn.scale(0.010416667F / 1.5f, -0.010416667F / 1.5f, 0.010416667F / 1.5f);


        Component component = null;
        if(stack.hasCustomHoverName())
            component = stack.getHoverName();
//
        if(component == null){
            if (tileEntityIn.getItemStackInSlot(0) != ItemStack.EMPTY) {
                if (tileEntityIn.getItemStackInSlot(0).getHoverName().getString().equals(""))
                    component = tileEntityIn.getItemStackInSlot(0).getItem().getName(tileEntityIn.getItemStackInSlot(0));
                else
                    component = tileEntityIn.getItemStackInSlot(0).getHoverName();
            }
        }


        if(component != null){
            List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 70);
            float f3 = (float) (-Minecraft.getInstance().font.width(list.get(0)) / 2);
            if(tileEntityIn.dyeColor != 0x422F1E && tileEntityIn.dyeColor != 0)
                matrixStackIn.translate(0, 5, 1);
            Minecraft.getInstance().font.drawInBatch(list.get(0), f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, false, 0, combinedLightIn);

            if (list.size() > 1) {
                matrixStackIn.translate(0, 10, 0);
                f3 = (float) (-Minecraft.getInstance().font.width(list.get(1)) / 2);
                Minecraft.getInstance().font.drawInBatch(list.get(1), f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, false, 0, combinedLightIn);
            }
        }

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.IModelData modelData, int color) {
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
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
            }

        }
    }

}