package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mezz.jei.api.gui.drawable.IDrawable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ExtraCauldronIcon implements IDrawable {

    private Supplier<ItemStack> extraSupplier;
    private ItemStack extraStack;

    private boolean findNewRecipe;

    private Recipe<?> recipeShown;
    private String type;
    private boolean showOutputItemInstead;

    public ExtraCauldronIcon(Supplier<ItemStack> secondary) {
        this.extraSupplier = secondary;
        this.findNewRecipe = true;
        this.type = "Fluid";
        this.showOutputItemInstead = false;
    }
    public ExtraCauldronIcon(Supplier<ItemStack> secondary, boolean showOutputItemInstead) {
        this.extraSupplier = secondary;
        this.findNewRecipe = true;
        this.type = "Fluid";
        this.showOutputItemInstead = showOutputItemInstead;
    }
    public ExtraCauldronIcon(Supplier<ItemStack> secondary, String type, boolean showOutputItemInstead) {
        this.extraSupplier = secondary;
        this.findNewRecipe = true;
        this.type = type;
        this.showOutputItemInstead = showOutputItemInstead;
    }
    public ExtraCauldronIcon(Supplier<ItemStack> secondary, String type) {
        this.extraSupplier = secondary;
        this.findNewRecipe = true;
        this.type = type;
        this.showOutputItemInstead = false;
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }


    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        if (extraStack == null) {
            extraStack = extraSupplier.get();
        }

        float craftPercent = (Hexerei.getClientTicks()) % 100 / 100f;
        if((craftPercent <= 0.1 && findNewRecipe) || recipeShown == null){
            findNewRecipe = false;
            if(Minecraft.getInstance().level != null) {
                List<?> list;
                if(this.type.equals("Fluid"))
                    list = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(FluidMixingRecipe.Type.INSTANCE);
                else if(this.type.equals("Potion"))
                    list = PotionMixingRecipes.ALL;
                else
                    list = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(MixingCauldronRecipe.Type.INSTANCE);

                recipeShown = (Recipe<?>) list.get(new Random().nextInt(list.size()));
            }

        }
        if(craftPercent > 0.1){
            findNewRecipe = true;
        }


        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));

        matrixStack.pushPose();
        matrixStack.translate(2, -13, 0);
        matrixStack.scale(10, 10, 10);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 rotationOffset = new Vec3(0, 0, 0);
        float zRot = 0;
        float xRot = 20;
        float yRot = 30;
        matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
        {
//            renderer.render(primaryStack, ItemTransforms.TransformType.GUI, false, matrixStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, renderer.getModel(secondaryStack, (Level)null, (LivingEntity)null, 0));
//            matrixStack.translate(40, 1, 0);
//            renderItem(primaryStack, matrixStack, buffer, LightTexture.FULL_BRIGHT);
//            renderer.render(primaryStack, ItemTransforms.TransformType.GUI, false, matrixStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, renderer.getModel(secondaryStack, (Level)null, (LivingEntity)null, 0));
//            matrixStack.translate(40, 1, 0);
//            renderItem(secondaryStack, matrixStack, buffer, LightTexture.FULL_BRIGHT);


            BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
            BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
            rendererer.getBlockModel(blockState);
            BakedModel bakedModel = rendererer.getBlockModel(blockState);


//            boolean flatLighting = !bakedModel.usesBlockLight();
//            if (flatLighting) {
//                Lighting.setupForFlatItems();
//            }

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);
            if(recipeShown instanceof FluidMixingRecipe || recipeShown instanceof MixingCauldronRecipe){
                float fillPercentage = 1;
                if(recipeShown instanceof FluidMixingRecipe recipe){
                    if (recipe.getLiquid().getFluid().is(Tags.Fluids.GASEOUS))
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, recipe.getLiquid(), fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                    else
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, recipe.getLiquid(), 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                }
                if(recipeShown instanceof MixingCauldronRecipe recipe){
                    if (recipe.getLiquid().getFluid().is(Tags.Fluids.GASEOUS))
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, recipe.getLiquid(), fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                    else
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, recipe.getLiquid(), 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                }

                float height = MixingCauldronRenderer.MIN_Y + (MixingCauldronRenderer.MAX_Y - MixingCauldronRenderer.MIN_Y) * fillPercentage;

                Lighting.setupFor3DItems();
                for(int i = 0; i < recipeShown.getIngredients().size(); i++)
                {
                    ItemStack[] items = recipeShown.getIngredients().get(i).getItems();
                    if (items.length > 0) {
                        matrixStack.pushPose();
                        matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);

                        //rotation offset when crafting
                        double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                        matrixStack.translate(
                                0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                                (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                                0D + Math.cos(itemRotationOffset)  / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float)((45 * i) -1f + (2 * Math.sin((Hexerei.getClientTicks() + i * 20) / 40)))));
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float)(82.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 22) / 40)))));
                        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float)(-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 24) / 40))) ));
                        matrixStack.scale(1 - (craftPercent * 0.5f),1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));

                        matrixStack.scale(0.4f, 0.4f, 0.4f);
                        renderItemFixed(items[((int)Hexerei.getClientTicksWithoutPartial() / 40) % items.length], matrixStack, buffer, LightTexture.FULL_BRIGHT);
                        matrixStack.popPose();
                    }
                }
            }


        }
        matrixStack.popPose();



        matrixStack.pushPose();
        matrixStack.translate(14, -14, 100);
        matrixStack.scale(.5f, .5f, .5f);
        matrixStack.scale(16, 16, 16);
        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
        if (!extraStack.isEmpty() || showOutputItemInstead) {
            if(!showOutputItemInstead){
                renderItem(extraStack, matrixStack, buffer, LightTexture.FULL_BRIGHT);
            } else {
                renderItem(recipeShown.getResultItem(), matrixStack, buffer, LightTexture.FULL_BRIGHT);
            }
        }
        matrixStack.popPose();

        matrixStack.popPose();
        buffer.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
    }


    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GUI, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }
    private void renderItemFixed(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }
}
