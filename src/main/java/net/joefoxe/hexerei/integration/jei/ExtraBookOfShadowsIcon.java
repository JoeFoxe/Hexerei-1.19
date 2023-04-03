package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import mezz.jei.api.gui.drawable.IDrawable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ExtraBookOfShadowsIcon implements IDrawable {

    private Supplier<ItemStack> extraSupplier;
    private ItemStack extraStack;

    private boolean findNewRecipe;

    private Recipe<?> recipeShown;
    private List<CraftingRecipe> flute_recipe;
    private int color1 = 0;
    private int color2 = 1;
    private Random rand = new Random();

    public ExtraBookOfShadowsIcon(Supplier<ItemStack> secondary) {
        this.extraSupplier = secondary;
        this.findNewRecipe = true;
        if(Minecraft.getInstance().level != null) {
            List<CraftingRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
            this.flute_recipe = recipes.stream().filter((craftingRecipe) -> craftingRecipe instanceof CrowFluteRecipe).toList();
        }
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


        float timer = (Hexerei.getClientTicks()) % 100 / 100f;
        if((timer <= 0.1 && findNewRecipe) || recipeShown == null){
            findNewRecipe = false;
            recipeShown = flute_recipe.get(new Random().nextInt(flute_recipe.size()));
            this.color1 = rand.nextInt(DyeColor.values().length - 1);
            this.color2 = rand.nextInt(DyeColor.values().length - 1);
        }
        if(timer > 0.1){
            findNewRecipe = true;
        }


        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));

        matrixStack.pushPose();
        matrixStack.translate(9, -9, 9);
        matrixStack.scale(20, 20, 20);
        Vec3 rotationOffset = new Vec3(0, 0, 0);
        float zRot = 0;
        float xRot = 20;
        float yRot = 30;
        matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
//        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
//        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) xRot));
//        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
        matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


        ItemStack output_stack = HexereiBookItem.withColors(color1);
//        output_stack.getOrCreateTagElement("display").putInt("color", color2);
        renderItem(output_stack, matrixStack, buffer, LightTexture.FULL_BRIGHT);

        matrixStack.popPose();



//        matrixStack.pushPose();
//        matrixStack.translate(14, -14, 100);
//        matrixStack.scale(.5f, .5f, .5f);
//        matrixStack.scale(16, 16, 16);
//        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
//        if (!recipe_stack.isEmpty())
//            renderItem(recipe_stack, matrixStack, buffer, LightTexture.FULL_BRIGHT);
//        matrixStack.popPose();

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
