package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
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
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static net.joefoxe.hexerei.integration.jei.FluidMixingRecipeCategory.getTagStack;
import static net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer.MAX_Y;
import static net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer.MIN_Y;

public class MixingCauldronRecipeCategory implements IRecipeCategory<MixingCauldronRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "mixingcauldron");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/mixing_cauldron_gui_jei.png");
    public final static ResourceLocation TEXTURE_BLANK =
            new ResourceLocation(Hexerei.MOD_ID, "textures/block/blank.png");
    private IDrawable background;
    private final IDrawable icon;

    private final IDrawable liquid;
    private final IDrawable cauldron;
    private final IDrawable output1;
    private final IDrawable output2;

    private Block heatSource;
    private boolean findNewHeatSource;

    @Override
    public List<Component> getTooltipStrings(MixingCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        //79, 59       24 x 18

        if(recipe.getHeatCondition() != FluidMixingRecipe.HeatCondition.NONE && isHovering(mouseX, mouseY, 79, 59, 24, 18)){
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("tooltip.hexerei.heat_source"));

            if(Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("Heat source shown: - %s", Component.translatable(heatSource.getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCC5522)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }



            return tooltip;
        }


        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }
    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }



    public MixingCauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 186, 109);
        this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.POTION), "Normal", true);
//        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MIXING_CAULDRON.get()));
        this.liquid = helper.createDrawable(TEXTURE, 208, 12, 16, 32);
        this.cauldron = helper.createDrawable(TEXTURE, 238, 50, 12, 10);
        this.output1 = helper.createDrawable(TEXTURE, 209, 64, 47, 82);
        this.output2 = helper.createDrawable(TEXTURE, 209, 146, 47, 82);
        this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
    }

    @Override
    public RecipeType<MixingCauldronRecipe> getRecipeType() {
        return new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("Item Mixing");
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
    public void setRecipe(IRecipeLayoutBuilder builder, MixingCauldronRecipe recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);
        builder.setShapeless();

        FluidStack input = recipe.getLiquid();
        FluidStack output = recipe.getLiquidOutput();

        if(recipe.getFluidLevelsConsumed() != 0) {
            if(!input.isEmpty())
                input.setAmount(Mth.clamp(recipe.getFluidLevelsConsumed(), 0, 2000));
        }
        else {
            if(!output.isEmpty())
                output.setAmount(2000);

        }
        if(!output.isEmpty())
            output.setAmount(2000-recipe.getFluidLevelsConsumed());

        boolean flag = recipe.getLiquid().hasTag() || recipe.getLiquidOutput().hasTag();

        CompoundTag tag = recipe.getLiquid().hasTag() ? recipe.getLiquid().getOrCreateTag() : new CompoundTag();
        CompoundTag tag2 = recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getOrCreateTag() : new CompoundTag();
        boolean compare = NbtUtils.compareNbt(tag2, tag, true);


        boolean changesFluid = false;

        if(!output.isEmpty() && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || (flag && recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) && !compare))) {
            changesFluid = true;

            builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 71)
                    .setFluidRenderer(2000, true, 12, 10)
                    .setBackground(this.cauldron, 0, 0)
                    .setOverlay(this.cauldron, 0, 0)
                    .addFluidStack(recipe.getLiquidOutput().getFluid(), 2000, recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getTag() : new CompoundTag())
                    .addTooltipCallback(HexereiJei.addFluidTooltip(recipe.getFluidLevelsConsumed(), Component.translatable("Leftover fluid is mixed into this")));
        }

        if(!input.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT,  20,  57)
                    .setFluidRenderer(2000, false, 16, 32)
                    .setBackground(this.liquid, 0, 0)
                    .setOverlay(this.liquid, 0, 0)
                    .addFluidStack(recipe.getLiquid().getFluid(), recipe.getFluidLevelsConsumed(), recipe.getLiquid().hasTag() ? recipe.getLiquid().getTag() : new CompoundTag())
                    .addTooltipCallback(HexereiJei.addFluidTooltip());
        }
        int size = recipe.getIngredients().size();

        if(size > 0)
            builder.addSlot(RecipeIngredientRole.INPUT,83, 18).addIngredients(recipe.getIngredients().get(0));
        if(size > 1)
            builder.addSlot(RecipeIngredientRole.INPUT,105, 27).addIngredients(recipe.getIngredients().get(1));
        if(size > 2)
            builder.addSlot(RecipeIngredientRole.INPUT,114, 49).addIngredients(recipe.getIngredients().get(2));
        if(size > 3)
            builder.addSlot(RecipeIngredientRole.INPUT,105, 71).addIngredients(recipe.getIngredients().get(3));
        if(size > 4)
            builder.addSlot(RecipeIngredientRole.INPUT,83, 80).addIngredients(recipe.getIngredients().get(4));
        if(size > 5)
            builder.addSlot(RecipeIngredientRole.INPUT,61, 71).addIngredients(recipe.getIngredients().get(5));
        if(size > 6)
            builder.addSlot(RecipeIngredientRole.INPUT,52, 49).addIngredients(recipe.getIngredients().get(6));
        if(size > 7)
            builder.addSlot(RecipeIngredientRole.INPUT,61, 27).addIngredients(recipe.getIngredients().get(7));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 156, changesFluid ? 30 : 49).addItemStack(recipe.getResultItem());

    }



    @Override
    public void draw(MixingCauldronRecipe recipe, IRecipeSlotsView view, PoseStack matrixStack, double mouseX, double mouseY) {


        FluidStack input = recipe.getLiquid();
        FluidStack output = recipe.getLiquidOutput();
        if(recipe.getHeatCondition() == FluidMixingRecipe.HeatCondition.HEATED || recipe.getHeatCondition() == FluidMixingRecipe.HeatCondition.SUPERHEATED){

            input.setAmount(2000);
            output.setAmount(2000);

            float newHeatSource = (Hexerei.getClientTicks()) % 200 / 200f;
            float craftPercent = (Hexerei.getClientTicks()) % 100 / 100f;
            boolean showOutput = (Hexerei.getClientTicks()) % 200 > 100;
            if ((newHeatSource <= 0.05f && this.findNewHeatSource) || this.heatSource == null) {
                this.findNewHeatSource = false;
                if (Minecraft.getInstance().level != null) {
                    this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
                }
            }
            if (newHeatSource > 0.05f)
                this.findNewHeatSource = true;

            boolean flag = recipe.getLiquid().hasTag() || recipe.getLiquidOutput().hasTag();
            CompoundTag tag = recipe.getLiquid().hasTag() ? recipe.getLiquid().getOrCreateTag() : new CompoundTag();
            CompoundTag tag2 = recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getOrCreateTag() : new CompoundTag();
            boolean compare = NbtUtils.compareNbt(tag2, tag, true);

            Minecraft minecraft = Minecraft.getInstance();
            Component outputName = recipe.getResultItem().getHoverName();
            int width = minecraft.font.width(outputName);
            float lineHeight = minecraft.font.lineHeight / 2f;
            if(width > 131){
                float percent = width/131f;
                matrixStack.pushPose();
                matrixStack.scale(1/percent, 1/percent, 1/percent);
                minecraft.font.draw(matrixStack, outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040);
                matrixStack.popPose();
            } else
                minecraft.font.draw(matrixStack, outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040);

            BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
            rendererer.getBlockModel(blockState);
            BakedModel bakedModel = rendererer.getBlockModel(blockState);//renderer.getModel(new ItemStack(ModBlocks.WILLOW_ALTAR.get()), null, null, 0);

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pushPose();
            matrixStack.translate(75f, 67, 100.0F + renderer.blitOffset);
            matrixStack.translate(8.0F, -8.0F, 0.0F);
            matrixStack.scale(16.0F, 16.0F, 16.0F);
            matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));

            Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);
            float zRot = 0;
            float xRot = 20;
            float yRot = 30;

            matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
            matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            Lighting.setupFor3DItems();
            matrixStack.last().normal().mul(Vector3f.YP.rotationDegrees((float) -45));
            renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, blockState, 0xFFFFFFFF);

            matrixStack.pushPose();
            matrixStack.translate(0F, -1.0F, 0.0F);
            BlockState state = this.heatSource.defaultBlockState();
            if (state.getBlock() instanceof LiquidBlock liquidBlock) {
                state = liquidBlock.getFluidState(liquidBlock.defaultBlockState()).createLegacyBlock().setValue(LiquidBlock.LEVEL, 7);
                MixingCauldronRenderer.renderFluidBlockGUI(matrixStack, buffer, new FluidStack(liquidBlock.getFluid(), 2000), 1, OverlayTexture.NO_OVERLAY);
            }
            renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
            matrixStack.popPose();

            float fillPercentage = 1;
            if (!showOutput) {
                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                float height = MixingCauldronRenderer.MIN_Y + (MixingCauldronRenderer.MAX_Y - MixingCauldronRenderer.MIN_Y) * fillPercentage;


                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ItemStack[] items = recipe.getIngredients().get(i).getItems();
                    if (items.length > 0) {
                        if (!items[((int)Hexerei.getClientTicksWithoutPartial() / 40) % items.length].isEmpty()) {
                            matrixStack.pushPose();
                            matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);

                            //rotation offset when crafting
                            double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                            matrixStack.translate(
                                    0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                                    (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                                    0D + Math.cos(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((45 * i) - 1f + (2 * Math.sin((Hexerei.getClientTicks() + i * 20) / 40)))));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 22) / 40)))));
                            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 24) / 40)))));
                            matrixStack.scale(1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));

                            matrixStack.scale(0.4f, 0.4f, 0.4f);
                            renderItem(items[((int)Hexerei.getClientTicksWithoutPartial() / 40) % items.length], matrixStack, buffer, LightTexture.FULL_BRIGHT);
                            matrixStack.popPose();
                        }
                    }
                }
            } else {
                {
                    float percentNeeded = recipe.getFluidLevelsConsumed() / 2000f;
                    float percentChanged = percentNeeded * craftPercent;
                    fillPercentage = 1 - percentChanged;
                    if (output.isEmpty()) {
                        if (input.getFluid().is(Tags.Fluids.GASEOUS))
                            MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                        else
                            MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                    } else {
                        if (output.getFluid().is(Tags.Fluids.GASEOUS))
                            MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, output, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                        else
                            MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, output, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                    }

                    // output item
                    ItemStack item2 = recipe.getResultItem();
                    if (!item2.isEmpty()) {

                        matrixStack.pushPose();
                        float height = MIN_Y + (MAX_Y - MIN_Y) * fillPercentage;
                        matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);


                        if (fillPercentage > 0) {
                            matrixStack.translate(0D, (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 60 + 20) / 10) * 0.2D, 0D);
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((45) - 1f + (2 * Math.sin((Hexerei.getClientTicks() + 20) / 40))) - (((1 - craftPercent) * (1 - craftPercent)) * 720f)));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((Hexerei.getClientTicks() + 22) / 40)))));
                            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + 24) / 40)))));
                        } else {
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (((1 - craftPercent) * (1 - craftPercent)) * 720f)));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(85f));
                            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-2.5f));
                        }

                        matrixStack.scale(0.4f, 0.4f, 0.4f);
                        renderItem(item2, matrixStack, buffer, LightTexture.FULL_BRIGHT);
                        matrixStack.popPose();
                    }
                }
            }
            matrixStack.popPose();

            Lighting.setupFor3DItems();
            buffer.endBatch();
            RenderSystem.enableDepthTest();

            if (!output.isEmpty() && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || (flag && recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) && !compare))) {
                output2.draw(matrixStack, 138, 16);
                matrixStack.scale(0.6f, 0.6f, 0.6f);
                minecraft.font.draw(matrixStack, Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"), 139 * 1.666f, 58 * 1.666f, 0xFF404040);
            } else
                output1.draw(matrixStack, 138, 16);

        } else { // else not heated

            if (recipe.getFluidLevelsConsumed() != 0) {
                if (!input.isEmpty())
                    input.setAmount(Mth.clamp(recipe.getFluidLevelsConsumed(), 0, 2000));
            } else {
                if (!output.isEmpty())
                    output.setAmount(2000);

            }
            if (!output.isEmpty())
                output.setAmount(2000 - recipe.getFluidLevelsConsumed());


            float newHeatSource = (Hexerei.getClientTicks()) % 200 / 200f;
            float craftPercent = (Hexerei.getClientTicks()) % 100 / 100f;
            boolean showOutput = (Hexerei.getClientTicks()) % 200 > 100;
            if ((newHeatSource <= 0.05f && this.findNewHeatSource) || this.heatSource == null) {
                this.findNewHeatSource = false;
                if (Minecraft.getInstance().level != null) {
                    this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
                }

            }
            if (newHeatSource > 0.05f) {
                this.findNewHeatSource = true;
            }


            boolean flag = recipe.getLiquid().hasTag() || recipe.getLiquidOutput().hasTag();

            CompoundTag tag = recipe.getLiquid().hasTag() ? recipe.getLiquid().getOrCreateTag() : new CompoundTag();
            CompoundTag tag2 = recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getOrCreateTag() : new CompoundTag();
            boolean compare = NbtUtils.compareNbt(tag2, tag, true);

            Minecraft minecraft = Minecraft.getInstance();
            Component outputName = recipe.getResultItem().getHoverName();
            int width = minecraft.font.width(outputName);
            float lineHeight = minecraft.font.lineHeight / 2f;
            if(width > 131){
                float percent = width/131f;
                matrixStack.pushPose();
                matrixStack.scale(1/percent, 1/percent, 1/percent);
                minecraft.font.draw(matrixStack, outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040);
                matrixStack.popPose();
            } else
                minecraft.font.draw(matrixStack, outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040);

            BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
            rendererer.getBlockModel(blockState);
            BakedModel bakedModel = rendererer.getBlockModel(blockState);//renderer.getModel(new ItemStack(ModBlocks.WILLOW_ALTAR.get()), null, null, 0);

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pushPose();
            matrixStack.translate(70f, 73, 100.0F + renderer.blitOffset);
            matrixStack.translate(8.0F, -8.0F, 0.0F);
            matrixStack.scale(20.0F, 20.0F, 20.0F);
            matrixStack.mulPoseMatrix(Matrix4f.createScaleMatrix(1, -1, 1));

            Vec3 rotationOffset = new Vec3(0, 0, 0);
            float zRot = 0;
            float xRot = 20;
            float yRot = 30;

            matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(zRot));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
            matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            boolean flatLighting = !bakedModel.usesBlockLight();
            if (flatLighting) {
                Lighting.setupForFlatItems();
            }
            renderBlock(matrixStack, buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);

            float percentNeeded = recipe.getFluidLevelsConsumed() / 2000f;
            float percentChanged = percentNeeded * craftPercent;
            float fillPercentage = 1;
            if (!showOutput) {
                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                float height = MIN_Y + (MAX_Y - MIN_Y) * fillPercentage;

                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ItemStack[] items = recipe.getIngredients().get(i).getItems();
                    if (items.length > 0) {
                        if (!items[((int) Hexerei.getClientTicksWithoutPartial() / 40) % items.length].isEmpty()) {
                            matrixStack.pushPose();
                            matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);

                            //rotation offset when crafting
                            double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                            matrixStack.translate(
                                    0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                                    (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                                    0D + Math.cos(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((45 * i) - 1f + (2 * Math.sin((Hexerei.getClientTicks() + i * 20) / 40)))));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 22) / 40)))));
                            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + i * 24) / 40)))));
                            matrixStack.scale(1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));

                            matrixStack.scale(0.4f, 0.4f, 0.4f);
                            renderItem(items[((int) Hexerei.getClientTicksWithoutPartial() / 40) % items.length], matrixStack, buffer, LightTexture.FULL_BRIGHT);
                            matrixStack.popPose();
                        }
                    }


                }
            } else {
                fillPercentage = 1 - percentChanged;
                if (output.isEmpty()) {
                    if (input.getFluid().is(Tags.Fluids.GASEOUS))
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                    else
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                } else {
                    if (output.getFluid().is(Tags.Fluids.GASEOUS))
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, output, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                    else
                        MixingCauldronRenderer.renderFluidGUI(matrixStack, buffer, output, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                }

                // output item
                ItemStack item2 = recipe.getResultItem();
                if (!item2.isEmpty()) {

                    matrixStack.pushPose();
                    float height = MIN_Y + (MAX_Y - MIN_Y) * fillPercentage;
                    matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);


                    if (fillPercentage > 0) {
                        matrixStack.translate(0D, (Math.sin(Math.PI * (Hexerei.getClientTicks()) / 60 + 20) / 10) * 0.2D, 0D);
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((45) - 1f + (2 * Math.sin((Hexerei.getClientTicks() + 20) / 40))) - (((1 - craftPercent) * (1 - craftPercent)) * 720f)));
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((Hexerei.getClientTicks() + 22) / 40)))));
                        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + 24) / 40)))));
                    } else {
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (((1 - craftPercent) * (1 - craftPercent)) * 720f)));
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees(85f));
                        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-2.5f));
                    }


//                matrixStack.translate(0D,(Math.sin(Math.PI * (Hexerei.getClientTicks()) / 60 + 20) / 10) * 0.2D,0D);
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees((float)((45) -1f + (2 * Math.sin((Hexerei.getClientTicks() + 20) / 40))) - (((1 - craftPercent) * (1 - craftPercent)) * 720f)));
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees((float)((45) -1f + (2 * Math.sin((Hexerei.getClientTicks() + 20) / 40)))));
//                matrixStack.mulPose(Vector3f.XP.rotationDegrees((float)(82.5f + (5 * Math.cos((Hexerei.getClientTicks() + 22) / 40)))));
//                matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float)(-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + 24) / 40)))));

                    matrixStack.scale(0.4f, 0.4f, 0.4f);
                    renderItem(item2, matrixStack, buffer, LightTexture.FULL_BRIGHT);
                    matrixStack.popPose();


                }


            }

//        // output item
//        ItemStack item2 = recipe.getResultItem();
//        if (!item2.isEmpty()) {
//
//            matrixStack.pushPose();
//            matrixStack.translate(0.5D, height + 1f / 256f, 0.5D);
//
//            if(fillPercentage > 0) {
//                matrixStack.translate(0D,(Math.sin(Math.PI * (Hexerei.getClientTicks()) / 60 + 20) / 10) * 0.2D,0D);
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees((float)((45) -1f + (2 * Math.sin((Hexerei.getClientTicks() + 20) / 40)))));
//                matrixStack.mulPose(Vector3f.XP.rotationDegrees((float)(82.5f + (5 * Math.cos((Hexerei.getClientTicks() + 22) / 40)))));
//                matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float)(-2.5f + (5 * Math.cos((Hexerei.getClientTicks() + 24) / 40)))));
//            } else {
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees(45));
//                matrixStack.mulPose(Vector3f.XP.rotationDegrees(85f));
//                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-2.5f));
//            }
//
//            matrixStack.scale(0.4f, 0.4f, 0.4f);
//            renderItem(item2, matrixStack, buffer, LightTexture.FULL_BRIGHT);
//            matrixStack.popPose();
//
//
//        }

            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flatLighting) {
                Lighting.setupFor3DItems();
            }

            matrixStack.popPose();


            if (!output.isEmpty() && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || (flag && recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) && !compare))) {

                output2.draw(matrixStack, 138, 16);


                matrixStack.scale(0.6f, 0.6f, 0.6f);
                minecraft.font.draw(matrixStack, Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"), 139 * 1.666f, 58 * 1.666f, 0xFF404040);

            } else {

                output1.draw(matrixStack, 138, 16);
            }
        }
    }

    private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 1);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, int color) {
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