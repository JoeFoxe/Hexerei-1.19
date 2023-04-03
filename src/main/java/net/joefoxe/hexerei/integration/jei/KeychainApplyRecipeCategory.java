package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.KeychainRecipe;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class KeychainApplyRecipeCategory implements IRecipeCategory<KeychainRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "keychain_apply");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/add_to_candle_gui_jei.png");
    private IDrawable background;
    private final IDrawable icon;

    public ItemStack itemShown;

    private boolean findNewHeatSource;

    @Override
    public List<Component> getTooltipStrings(KeychainRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {

        if(isHovering(mouseX, mouseY, 33, 19, 16, 16)){
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("Any Item"));
            tooltip.add(Component.translatable("item shown: - %s", Component.translatable(itemShown.getHoverName().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCC5522)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            return tooltip;
        }


        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }
    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public KeychainApplyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 144, 86);
        this.icon = new ExtraKeychainIcon(() -> new ItemStack(ModItems.CANDLE.get()));
        this.itemShown = new ItemStack(Registry.ITEM.getRandom(RandomSource.create()).map(Holder::get).orElse(Items.AIR));
    }

    @Override
    public RecipeType<KeychainRecipe> getRecipeType() {
        return new RecipeType<>(new ResourceLocation(Hexerei.MOD_ID, "keychain_apply"), KeychainRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("Keychain Attach");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KeychainRecipe recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);
        builder.setShapeless();

        builder.addSlot(RecipeIngredientRole.INPUT,15, 19).addItemStack(new ItemStack(ModItems.BROOM_KEYCHAIN.get()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 37).addItemStack(new ItemStack(ModItems.BROOM_KEYCHAIN.get()));

    }



    @Override
    public void draw(KeychainRecipe recipe, IRecipeSlotsView view, PoseStack matrixStack, double mouseX, double mouseY) {

        float newHeatSource = (Hexerei.getClientTicks()) % 200 / 200f;
        float craftPercent = (Hexerei.getClientTicks()) % 100 / 100f;
        boolean showOutput = (Hexerei.getClientTicks()) % 200 > 100;
        if ((newHeatSource <= 0.05f && this.findNewHeatSource) || this.itemShown == null) {
            this.findNewHeatSource = false;
            if (Minecraft.getInstance().level != null) {
                this.itemShown = new ItemStack(Registry.ITEM.getRandom(RandomSource.create()).map(Holder::get).orElse(Items.AIR));
            }
        }
        if (newHeatSource > 0.05f)
            this.findNewHeatSource = true;

        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Component outputName = getTitle();
        RenderSystem.enableDepthTest();

        if(isHovering(mouseX, mouseY, 33, 19, 16, 16))
            GuiComponent.fill(matrixStack, 41 - 8, 27 - 8, 41 + 16 - 8, 27 + 16 - 8, 0x66FFFFFF);

        if(!renderer.getModel(this.itemShown, null, null, 0).usesBlockLight())
            Lighting.setupForFlatItems();

        ItemStack stack = new ItemStack(ModItems.BROOM_KEYCHAIN.get());

        ItemStack keychain = new ItemStack(ModItems.BROOM_KEYCHAIN.get());
        ItemStack other = this.itemShown;

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

        matrixStack.pushPose();
        matrixStack.translate(41, 27, 0);
        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));

        matrixStack.translate(0, 0, 100);
        matrixStack.scale(16, 16, 16);
        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
        renderItem(this.itemShown, matrixStack, buffer, LightTexture.FULL_BRIGHT);
        matrixStack.popPose();


        matrixStack.pushPose();
        matrixStack.translate(117, 45, 0);
        matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));
        matrixStack.translate(0, 0, 100);
        matrixStack.scale(16, 16, 16);
        matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));

        renderItem(keychain, matrixStack, buffer, LightTexture.FULL_BRIGHT);
        matrixStack.popPose();



        buffer.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();

        int width = minecraft.font.width(outputName);
        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 131){
            float percent = width/131f;
            matrixStack.pushPose();
            matrixStack.scale(1/percent, 1/percent, 1/percent);
            minecraft.font.draw(matrixStack, outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040);
            matrixStack.popPose();

        }else {
            minecraft.font.draw(matrixStack, outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040);
        }

    }

    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GUI, combinedLightIn,
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