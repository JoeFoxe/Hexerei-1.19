package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.Objects;

public class MixingCauldronRenderer implements BlockEntityRenderer<MixingCauldronTile> {

    public static final float CORNERS = (float)MixingCauldron.SHAPE.min(Direction.Axis.X) + 3 / 16f;
    public static final float MIN_Y = 4f / 16f;
    public static final float MAX_Y = 15f/ 16f;

    @Override
    public void render(MixingCauldronTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof MixingCauldronTile))
            return;
        // Rendering for the items inside the cauldron
        float craftPercent = 0;
        //Mixing the items
        if(Objects.requireNonNull(tileEntityIn.getLevel()).getBlockState(tileEntityIn.getPos()).hasBlockEntity() && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof MixingCauldronTile) {
            craftPercent = tileEntityIn.getLevel().getBlockState(tileEntityIn.getPos()).getValue(MixingCauldron.CRAFT_DELAY) / (float) MixingCauldronTile.craftDelayMax;
        }
        else return;


        renderBlock(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, tileEntityIn.dyeColor != 0x422F1E && tileEntityIn.dyeColor != 0), null, tileEntityIn.getDyeColor());

        float fillPercentage = 0;
        FluidStack fluidStack = tileEntityIn.getFluidInTank(0);
        if(!fluidStack.isEmpty()) {
            matrixStackIn.pushPose();
            fillPercentage = Math.min(1, (float) fluidStack.getAmount() / tileEntityIn.getTankCapacity(0));
//            matrixStackIn.scale(1.05f, 1, 1.05f);
            if (fluidStack.getFluid().is(Tags.Fluids.GASEOUS))
                renderFluid(matrixStackIn, bufferIn, fluidStack, fillPercentage, 1, combinedLightIn, tileEntityIn);
            else
                renderFluid(matrixStackIn, bufferIn, fluidStack, 1, fillPercentage, combinedLightIn, tileEntityIn);
            matrixStackIn.popPose();
        }
        float height = MIN_Y + (MAX_Y - MIN_Y) * fillPercentage;

        for(int i = 0; i < 8; i++)
        {
            ItemStack item = tileEntityIn.getItemStackInSlot(i);
            if (!item.isEmpty()) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5D, height + 1f / 256f, 0.5D);

                //rotation offset when crafting
                double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                if(fillPercentage > 0) {
                    matrixStackIn.translate(
                            0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                            (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                            0D + Math.cos(itemRotationOffset)  / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)((45 * i) -1f + (2 * Math.sin((tileEntityIn.degrees + i * 20) / 40)))));
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees((float)(82.5f + (5 * Math.cos((tileEntityIn.degrees + i * 22) / 40)))));
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)(-2.5f + (5 * Math.cos((tileEntityIn.degrees + i * 24) / 40))) ));
                    matrixStackIn.scale(1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));
                } else {
                    matrixStackIn.translate(0D + Math.sin(itemRotationOffset) / 3.5, 0,0D + Math.cos(itemRotationOffset) / 3.5);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(45 * i));
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(85f ) );
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-2.5f ));
                }

                matrixStackIn.scale(0.4f, 0.4f, 0.4f);
                renderItem(item, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
                matrixStackIn.popPose();
            }


        }

        // output item
        ItemStack item2 = new ItemStack(tileEntityIn.getItemInSlot(8));
        if (!item2.isEmpty()) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, height + 1f / 256f, 0.5D);

            if(fillPercentage > 0) {
                matrixStackIn.translate(0D,(Math.sin(Math.PI * (Hexerei.getClientTicks()) / 60 + 20) / 10) * 0.2D,0D);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)((45) -1f + (2 * Math.sin((tileEntityIn.degrees + 20) / 40))) - ((craftPercent * craftPercent) * 720f)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees((float)(82.5f + (5 * Math.cos((tileEntityIn.degrees + 22) / 40)))));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)(-2.5f + (5 * Math.cos((tileEntityIn.degrees + 24) / 40)))));
            } else {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(45 - ((craftPercent * craftPercent) * 720f)));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(85f));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-2.5f));
            }

            matrixStackIn.scale(0.4f, 0.4f, 0.4f);
            renderItem(item2, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            matrixStackIn.popPose();


        }

        //gives the wobble
        ((MixingCauldronTile)tileEntityIn).degrees++;
        if (tileEntityIn.getItemInSlot(9) == ModItems.BLOOD_SIGIL.get()) {
            if(tileEntityIn.getItemInSlot(9).asItem() == ModItems.BLOOD_SIGIL.get())
            {
                matrixStackIn.pushPose();
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BLOOD_SIGIL.get().defaultBlockState());
                matrixStackIn.popPose();
            }
        }
    }

    private static void renderFluid(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight, MixingCauldronTile tileEntityIn){
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucentNoCrumbling());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
        Color color2 = new Color(BiomeColors.getAverageWaterColor(tileEntityIn.getLevel(), new BlockPos(tileEntityIn.getPos().getX(), tileEntityIn.getPos().getY(), tileEntityIn.getPos().getZ())));
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);

        alpha *= (color >> 24 & 255) / 255f;


        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        if(tileEntityIn.getFluidStack().isFluidEqual(new FluidStack(Fluids.WATER, 1))) {
            red = color2.getRed()/255f;
            green = color2.getGreen()/255f;
            blue = color2.getBlue()/255f;
        }

        renderQuads(matrixStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, heightPercentage, combinedLight);
    }

    public static void renderFluidGUI(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight){
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucentNoCrumbling());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);

        alpha *= (color >> 24 & 255) / 255f;

        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuads(matrixStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, heightPercentage, combinedLight);
    }

    public static void renderFluidBlockGUI(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, int combinedLight){
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucentNoCrumbling());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);

        alpha *= (color >> 24 & 255) / 255f;

        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuadsBlock(matrixStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, combinedLight);
    }

    private static void renderQuads(Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float heightPercentage, int light){
        float height = MIN_Y + (MAX_Y - MIN_Y) * heightPercentage;
        float minU = sprite.getU(CORNERS * 16);
        float maxU = sprite.getU((1 - CORNERS) * 16);
        float minV = sprite.getV(CORNERS * 16);
        float maxV = sprite.getV((1 - CORNERS) * 16);
        vertexBuilder.vertex(matrix, CORNERS, height, CORNERS).color(r, g, b, alpha).uv(minU, minV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, CORNERS, height, 1 - CORNERS).color(r, g, b, alpha).uv(minU, maxV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - CORNERS, height, 1 - CORNERS).color(r, g, b, alpha).uv(maxU, maxV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - CORNERS, height, CORNERS).color(r, g, b, alpha).uv(maxU, minV).uv2(light).normal(0, 1, 0).endVertex();
    }

    private static void renderQuadsBlock(Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, int light){
        float height = (MIN_Y + (MAX_Y - MIN_Y)) * 0.8f;
        float minU = sprite.getU(CORNERS * 16);
        float maxU = sprite.getU((1 - CORNERS) * 16);
        float minV = sprite.getV(CORNERS * 16);
        float maxV = sprite.getV((1 - CORNERS) * 16);

        vertexBuilder.vertex(matrix, CORNERS / 5f, height, CORNERS / 5f).color(r, g, b, alpha).uv(minU, minV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).color(r, g, b, alpha).uv(minU, maxV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).color(r, g, b, alpha).uv(maxU, maxV).uv2(light).normal(0, 1, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - CORNERS / 5f, height, CORNERS / 5f).color(r, g, b, alpha).uv(maxU, minV).uv2(light).normal(0, 1, 0).endVertex();


        float shading = 0.75f;
        vertexBuilder.vertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(minU, minV).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, height, CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(minU, maxV).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, 0, CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(maxU, maxV).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(maxU, minV).uv2(light).normal(-1, 0, 0).endVertex();


        shading = 0.45f;
        vertexBuilder.vertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(minU, minV).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(minU, maxV).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(maxU, maxV).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, 1 - CORNERS / 5f, 0, 1 - CORNERS / 5f).color(r * shading, g * shading, b * shading, alpha).uv(maxU, minV).uv2(light).normal(0, 0, -1).endVertex();
    }

    // THIS IS WHAT I WAS LOOKING FOR FOREVER AHHHHH
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
