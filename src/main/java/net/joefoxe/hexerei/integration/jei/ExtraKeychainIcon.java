package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.data.recipes.AddToCandleRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
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

public class ExtraKeychainIcon implements IDrawable {

    private Supplier<ItemStack> extraSupplier;
    private ItemStack extraStack;

    private boolean findNewItem;

    private ItemStack attachedItem;

    public ExtraKeychainIcon(Supplier<ItemStack> secondary) {
        this.extraSupplier = secondary;
        this.findNewItem = true;
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
        if((timer <= 0.1 && findNewItem) || attachedItem == null){
            findNewItem = false;
            attachedItem = new ItemStack(Registry.ITEM.getRandom(RandomSource.create()).map(Holder::get).orElse(Items.AIR));

        }
        if(timer > 0.1){
            findNewItem = true;
        }


        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
        Lighting.setupForFlatItems();

        matrixStack.pushPose();
        matrixStack.translate(9, -9, 9);
        matrixStack.scale(15, 15, 15);
        Vec3 rotationOffset = new Vec3(0, 0, 0);
        float zRot = 0;
        float xRot = 20;
        float yRot = 210;
        matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) xRot));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
        matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


        ItemStack recipe_stack = attachedItem;
        //keychain

        ItemStack stack = new ItemStack(ModItems.BROOM_KEYCHAIN.get());

        ItemStack keychain = new ItemStack(ModItems.BROOM_KEYCHAIN.get());
        ItemStack other = attachedItem;

        if (keychain.getItem() instanceof KeychainItem && !other.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            if(keychain.hasTag())
                tag = keychain.getTag();

            ListTag listtag = new ListTag();

            if (!other.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)0);
                other.save(compoundtag);
                listtag.add(compoundtag);
            }

            tag.put("Items", listtag);

            keychain.setTag(tag);
        }
        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
        renderItemFixed(keychain, matrixStack, buffer, LightTexture.FULL_BRIGHT);
        matrixStack.popPose();



        matrixStack.pushPose();
        matrixStack.translate(14, -14, 100);
        matrixStack.scale(.5f, .5f, .5f);
        matrixStack.scale(16, 16, 16);
        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
        if (!recipe_stack.isEmpty())
            renderItem(recipe_stack, matrixStack, buffer, LightTexture.FULL_BRIGHT);
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
            switch(rendershape) {
                case MODEL:
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());

                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, poseStack, p_110915_, p_110916_, p_110917_);
            }

        }
    }
}
