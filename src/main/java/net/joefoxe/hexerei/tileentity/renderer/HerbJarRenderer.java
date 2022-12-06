package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import java.util.Random;

public class HerbJarRenderer implements BlockEntityRenderer<HerbJarTile> {

    private final Font font;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private final ItemModelShaper shaper = itemRenderer.getItemModelShaper();

    public HerbJarRenderer() {
        super();
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public void render(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof HerbJarTile))
            return;


        if(tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() && tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasProperty(HorizontalDirectionalBlock.FACING)){
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH)
                renderItemsNorth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                renderItemsWest(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                renderItemsSouth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                renderItemsEast(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        }
        else
            return;

        matrixStackIn.pushPose();
        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(1, 0D / 16D, 1);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(0));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(1D, 0D / 16D, 0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
        }
//        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.CRYSTAL_BALL_ORB.get().defaultBlockState(), RenderType.translucent(), tileEntityIn.getDyeColor());
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModBlocks.HERB_JAR.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, tileEntityIn.dyeColor != 0x422F1E && tileEntityIn.dyeColor != 0), null, tileEntityIn.getDyeColor());
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

            long r = tileEntityIn.getBlockPos().asLong();
            Random rand = new Random(r);


            BakedModel itemModel = itemRenderer.getModel(new ItemStack(item), null, null, 0);



            boolean is3dModel = itemModel.isGui3d();
            for(int a = 0; a < ((float)tileEntityIn.itemHandler.getContents().get(0).getCount() / 1024f) * 20f; a++){
                matrixStackIn.pushPose();

//                boolean is3dModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory")).isGui3d();
                if(is3dModel) {
                    matrixStackIn.translate(0, 1.5D / 16D, 0);
                }
                if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {


                    if(state != null){
                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a, 8D / 16D);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180 ));
                        renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                    } else {

                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a + 1D/16D, 8D / 16D);
                        matrixStackIn.scale(0.4f,0.4f,0.4f);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180 ));
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if(is3dModel)
                            matrixStackIn.scale(1.20f, 1.20f, 1.20f);
                        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), 0, matrixStackIn, bufferIn, combinedLightIn);
                    }

                } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
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
                        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), 0, matrixStackIn, bufferIn, combinedLightIn);
                    }
                } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    if(state != null){
                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a, 8D / 16D);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 ));
                        renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                    } else {

                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a + 1D/16D, 8D / 16D);
                        matrixStackIn.scale(0.4f,0.4f,0.4f);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 ));
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if(is3dModel)
                            matrixStackIn.scale(1.20f, 1.20f, 1.20f);
                        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), 0, matrixStackIn, bufferIn, combinedLightIn);
                    }
                } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    if(state != null){
                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a, 8D / 16D);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270 ));
                        renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                    } else {

                        matrixStackIn.translate(8D / 16D, 0.5D / 16D * a + 1D/16D, 8D / 16D);
                        matrixStackIn.scale(0.4f,0.4f,0.4f);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270 ));
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if(is3dModel)
                            matrixStackIn.scale(1.20f, 1.20f, 1.20f);
                        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), 0, matrixStackIn, bufferIn, combinedLightIn);
                    }
                }
                matrixStackIn.popPose();
            }
        }


        int i = 0x464F56;
        int j = (int)((double) NativeImage.getR(i) * 0.4D);
        int k = (int)((double)NativeImage.getG(i) * 0.4D);
        int l = (int)((double)NativeImage.getB(i) * 0.4D);
        int i1 = NativeImage.combine(   0, l, k, j);

        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(8D / 16D, 8D / 16D, 12.05D / 16D);
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.translate(8D / 16D, 8D / 16D, 1 - 12.05D / 16D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(1 - 12.05D / 16D, 8D / 16D, 8D / 16);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(12.05D / 16D, 8D / 16D, 8D / 16);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        }

        matrixStackIn.scale(0.00694445f, -0.00694445f, 0.00694445f);

        Component component = tileEntityIn.customName;

        if(component == null)
        {

            if(!tileEntityIn.getItemStackInSlot(0).isEmpty()) {
                if(tileEntityIn.getItemStackInSlot(0).getHoverName().getString().equals(""))
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



//        FormattedCharSequence ireorderingprocessor = tileEntityIn.reorderText(0, (p_243502_1_) -> {
//            List<FormattedCharSequence> list = font.split(p_243502_1_, 90);
//            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
//        });
//        if (ireorderingprocessor != null) {
//            float f3 = (float)(-font.width(ireorderingprocessor) / 2);
//            font.drawInBatch(ireorderingprocessor, f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, false, 0, combinedLightIn);
//        }

    }

    private void renderItemsNorth(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                  MultiBufferSource bufferIn, int combinedLightIn)
    {

        matrixStackIn.pushPose();
        matrixStackIn.translate(8D/16D, 4.25D/16D, 12D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        renderItem(new ItemStack(tileEntityIn.itemHandler.getContents().get(0).getItem(), 1), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

    }

    private void renderItemsSouth(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn)
    {

        matrixStackIn.pushPose();
        matrixStackIn.translate(8D/16D, 4.25D/16D, 4D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

        renderItem(new ItemStack(tileEntityIn.itemHandler.getContents().get(0).getItem(), 1), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


    }

    private void renderItemsWest(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn)
    {

        matrixStackIn.pushPose();
        matrixStackIn.translate(1, 0, 0);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
        matrixStackIn.translate(8D/16D, 4.25D/16D, 4D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
            renderItem(new ItemStack(tileEntityIn.itemHandler.getContents().get(0).getItem(), 1), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


    }

    private void renderItemsEast(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn)
    {
        matrixStackIn.pushPose();
        matrixStackIn.translate(1, 0, 0);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
        matrixStackIn.translate(8D/16D, 4.25D/16D, 12D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        renderItem(new ItemStack(tileEntityIn.itemHandler.getContents().get(0).getItem(), 1), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();
    }

    private void renderItem(ItemStack stack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }




    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
            }

        }
    }


}
