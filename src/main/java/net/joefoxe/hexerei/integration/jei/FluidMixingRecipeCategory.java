package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.*;

public class FluidMixingRecipeCategory implements IRecipeCategory<FluidMixingRecipe> {
    public final static ResourceLocation UID = HexereiUtil.getResource("fluid_mixing");
    public final static ResourceLocation POTION_UID = HexereiUtil.getResource("potion_mixing");
    public final static ResourceLocation TEXTURE =
            HexereiUtil.getResource("textures/gui/fluid_mixing_gui_jei.png");
    public final static ResourceLocation TEXTURE_BLANK =
            HexereiUtil.getResource("textures/block/blank.png");
    private IDrawable background;
    private final IDrawable icon;

    private final IDrawable liquid;
    private final IDrawable cauldron;
    private final IDrawable output1;
    private final IDrawable output2;


    private Block heatSource;
    private boolean findNewHeatSource;
    private String type;


    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidMixingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {

        if(recipe.getHeatCondition() != FluidMixingRecipe.HeatCondition.NONE && isHovering(mouseX, mouseY, 79, 59, 24, 18)){
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
        }
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    public FluidMixingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 186, 109);
        this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.WATER_BUCKET));
        this.liquid = helper.createDrawable(TEXTURE, 208, 12, 16, 32);
        this.cauldron = helper.createDrawable(TEXTURE, 238, 50, 12, 10);
        this.output1 = helper.createDrawable(TEXTURE, 209, 64, 47, 82);
        this.output2 = helper.createDrawable(TEXTURE, 209, 146, 47, 82);
        this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
        this.type = "Fluid";
    }

    public FluidMixingRecipeCategory(IGuiHelper helper, String type) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 186, 109);
        this.type = type;
        if(Objects.equals(this.type, "Potion"))
            this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.POTION), type);
        else
            this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.WATER_BUCKET));
        this.liquid = helper.createDrawable(TEXTURE, 208, 12, 16, 32);
        this.cauldron = helper.createDrawable(TEXTURE, 238, 50, 12, 10);
        this.output1 = helper.createDrawable(TEXTURE, 209, 64, 47, 82);
        this.output2 = helper.createDrawable(TEXTURE, 209, 146, 47, 82);
        this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
    }

    public static Block getTagStack(TagKey<Block> key){

        if (BuiltInRegistries.BLOCK.getTag(key).isPresent()){
            Optional<Holder<Block>> optional = BuiltInRegistries.BLOCK.getTag(key).get().getRandomElement(RandomSource.create());
            return optional.orElse(Holder.direct(Blocks.AIR)).value();
        }
        return Blocks.AIR;

    }

    @Override
    public RecipeType<FluidMixingRecipe> getRecipeType() {
        return new RecipeType<>(HexereiUtil.getResource(this.type.toLowerCase(Locale.ROOT) + "_mixing"), FluidMixingRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category." + ((this.type + "_mixing")).toLowerCase(Locale.ROOT));
    }

//    @Override
//    public IDrawable getBackground() {
//        return this.background;
//    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidMixingRecipe recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);
        builder.setShapeless();


        FluidStack input = recipe.getLiquid();
        FluidStack output = recipe.getLiquidOutput();
        input.setAmount(2000);
        output.setAmount(2000);


        builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 51)
                .setFluidRenderer(2000, true, 12, 10)
                .setBackground(this.cauldron, 0, 0)
                .setOverlay(this.cauldron, 0, 0)
                .addFluidStack(recipe.getLiquidOutput().getFluid(), 2000, recipe.getLiquidOutput().getComponentsPatch());

        builder.addSlot(RecipeIngredientRole.INPUT,  20,  57)
                .setFluidRenderer(2000, false, 16, 32)
                .setBackground(this.liquid, 0, 0)
                .setOverlay(this.liquid, 0, 0)
                .addFluidStack(recipe.getLiquid().getFluid(), 2000, recipe.getLiquid().getComponentsPatch());
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

    }

    @Override
    public void draw(FluidMixingRecipe recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        background.draw(guiGraphics);

        if(recipe.getHeatCondition() == FluidMixingRecipe.HeatCondition.HEATED || recipe.getHeatCondition() == FluidMixingRecipe.HeatCondition.SUPERHEATED){

            FluidStack input = recipe.getLiquid();
            FluidStack output = recipe.getLiquidOutput();
            input.setAmount(2000);
            output.setAmount(2000);

            float newHeatSource = (ClientEvents.getClientTicks()) % 200 / 200f;
            float craftPercent = (ClientEvents.getClientTicks()) % 100 / 100f;
            boolean showOutput = (ClientEvents.getClientTicks()) % 200 > 100;
            if ((newHeatSource <= 0.05f && this.findNewHeatSource) || this.heatSource == null) {
                this.findNewHeatSource = false;
                if (Minecraft.getInstance().level != null) {
                    this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
                }
            }
            if (newHeatSource > 0.05f)
                this.findNewHeatSource = true;

//            boolean flag = recipe.getLiquid().hasTag() || recipe.getLiquidOutput().hasTag();
//            CompoundTag tag = recipe.getLiquid().hasTag() ? recipe.getLiquid().getOrCreateTag() : new CompoundTag();
//            CompoundTag tag2 = recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getOrCreateTag() : new CompoundTag();
            boolean compare = FluidStack.isSameFluidSameComponents(recipe.getLiquid(), recipe.getLiquidOutput());



            Minecraft minecraft = Minecraft.getInstance();
            Component outputName = recipe.getLiquidOutput().getHoverName();
            int width = minecraft.font.width(outputName);
            float lineHeight = minecraft.font.lineHeight / 2f;
            if(width > 131){
                float percent = width/131f;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(1/percent, 1/percent, 1/percent);
                minecraft.font.drawInBatch(outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                guiGraphics.pose().popPose();
            } else
                minecraft.font.drawInBatch(outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

            BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
//            BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
//            rendererer.getBlockModel(blockState);
//            BakedModel bakedModel = rendererer.getBlockModel(blockState);//renderer.getModel(new ItemStack(ModBlocks.WILLOW_ALTAR.get()), null, null, 0);

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(75f, 67, 100.0F);
            guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
            guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
            guiGraphics.pose().mulPose(new Matrix4f().scale(1, -1, 1));

            Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);
            float zRot = 0;
            float xRot = 20;
            float yRot = 30;

            guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
            guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            Lighting.setupFor3DItems();
            guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees((float) -45));
            renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFFFFFFFF);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0F, -1.0F, 0.0F);
            BlockState state = this.heatSource.defaultBlockState();
            if (state.getBlock() instanceof LiquidBlock liquidBlock) {
                state = liquidBlock.fluid.defaultFluidState().createLegacyBlock().setValue(LiquidBlock.LEVEL, 7);
                MixingCauldronRenderer.renderFluidBlockGUI(guiGraphics.pose(), buffer, new FluidStack(liquidBlock.fluid, 2000), 1, OverlayTexture.NO_OVERLAY);
            }
            renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
            guiGraphics.pose().popPose();

            float fillPercentage = 1;
            if (!showOutput) {
                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                float height = MixingCauldronRenderer.MIN_Y + (MixingCauldronRenderer.MAX_Y - MixingCauldronRenderer.MIN_Y) * fillPercentage;


                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ItemStack[] items = recipe.getIngredients().get(i).getItems();
                    if (items.length > 0) {
                        if (!items[((int)ClientEvents.getClientTicksWithoutPartial() / 40) % items.length].isEmpty()) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(0.5D, height + 1f / 256f, 0.5D);

                            //rotation offset when crafting
                            double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                            guiGraphics.pose().translate(
                                    0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                                    (Math.sin(Math.PI * (ClientEvents.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                                    0D + Math.cos(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees((float) ((45 * i) - 1f + (2 * Math.sin((ClientEvents.getClientTicks() + i * 20) / 40)))));
                            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((ClientEvents.getClientTicks() + i * 22) / 40)))));
                            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((ClientEvents.getClientTicks() + i * 24) / 40)))));
                            guiGraphics.pose().scale(1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));

                            guiGraphics.pose().scale(0.4f, 0.4f, 0.4f);
                            renderItem(items[((int)ClientEvents.getClientTicksWithoutPartial() / 40) % items.length], minecraft.level, guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT);
                            guiGraphics.pose().popPose();
                        }
                    }
                }
            } else {
                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
            }
            guiGraphics.pose().popPose();

            Lighting.setupFor3DItems();
            buffer.endBatch();
            RenderSystem.enableDepthTest();

            if (!output.isEmpty() && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || (recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) && !compare))) {
                output2.draw(guiGraphics, 138, 16);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
                minecraft.font.drawInBatch(Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"), 139 * 1.666f, 38 * 1.666f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                guiGraphics.pose().popPose();
            } else
                output1.draw(guiGraphics, 138, 16);

        } else { // else not heated
            FluidStack input = recipe.getLiquid();
            FluidStack output = recipe.getLiquidOutput();
            input.setAmount(2000);
            output.setAmount(2000);

            float newHeatSource = (ClientEvents.getClientTicks()) % 200 / 200f;
            float craftPercent = (ClientEvents.getClientTicks()) % 100 / 100f;
            boolean showOutput = (ClientEvents.getClientTicks()) % 200 > 100;
            if ((newHeatSource <= 0.05f && this.findNewHeatSource) || this.heatSource == null) {
                this.findNewHeatSource = false;
                if (Minecraft.getInstance().level != null) {
                    this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
                }
            }
            if (newHeatSource > 0.05f) {
                this.findNewHeatSource = true;
            }

//            boolean flag = recipe.getLiquid().hasTag() || recipe.getLiquidOutput().hasTag();
//
//            CompoundTag tag = recipe.getLiquid().hasTag() ? recipe.getLiquid().getOrCreateTag() : new CompoundTag();
//            CompoundTag tag2 = recipe.getLiquidOutput().hasTag() ? recipe.getLiquidOutput().getOrCreateTag() : new CompoundTag();
//            boolean compare = NbtUtils.compareNbt(tag2, tag, true);
            boolean compare = FluidStack.isSameFluidSameComponents(recipe.getLiquid(), recipe.getLiquidOutput());

            Minecraft minecraft = Minecraft.getInstance();

            Component outputName = recipe.getLiquidOutput().getHoverName();

            int width = minecraft.font.width(outputName);
            float lineHeight = minecraft.font.lineHeight / 2f;
            if(width > 131){
                float percent = width/131f;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(1/percent, 1/percent, 1/percent);
                minecraft.font.drawInBatch(outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                guiGraphics.pose().popPose();

            }else {
                minecraft.font.drawInBatch(outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }

            BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);


            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
            rendererer.getBlockModel(blockState);
            BakedModel bakedModel = rendererer.getBlockModel(blockState);//renderer.getModel(new ItemStack(ModBlocks.WILLOW_ALTAR.get()), null, null, 0);

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(70f, 73, 100.0F);
            guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
            guiGraphics.pose().scale(20.0F, 20.0F, 20.0F);
            guiGraphics.pose().mulPose(new Matrix4f().scale(1, -1, 1));

            Vec3 rotationOffset = new Vec3(0, 0, 0);
            float zRot = 0;
            float xRot = 20;
            float yRot = 30;


            guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
            guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            boolean flatLighting = !bakedModel.usesBlockLight();
            if (flatLighting) {
                Lighting.setupForFlatItems();
            }
            renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);

            float fillPercentage = 1;
            if (!showOutput) {
                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, 1, fillPercentage, OverlayTexture.NO_OVERLAY);
                float height = MixingCauldronRenderer.MIN_Y + (MixingCauldronRenderer.MAX_Y - MixingCauldronRenderer.MIN_Y) * fillPercentage;


                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ItemStack[] items = recipe.getIngredients().get(i).getItems();
                    if (items.length > 0) {
                        if (!items[((int)ClientEvents.getClientTicksWithoutPartial() / 40) % items.length].isEmpty()) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(0.5D, height + 1f / 256f, 0.5D);

                            //rotation offset when crafting
                            double itemRotationOffset = 0.8 * i + (craftPercent * (20f * craftPercent));
                            guiGraphics.pose().translate(
                                    0D + Math.sin(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)),
                                    (Math.sin(Math.PI * (ClientEvents.getClientTicks()) / 30 + (i * 20)) / 10) * 0.2D,
                                    0D + Math.cos(itemRotationOffset) / (3.5f + ((craftPercent * craftPercent) * 10.0f)));
                            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees((float) ((45 * i) - 1f + (2 * Math.sin((ClientEvents.getClientTicks() + i * 20) / 40)))));
                            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees((float) (82.5f + (5 * Math.cos((ClientEvents.getClientTicks() + i * 22) / 40)))));
                            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float) (-2.5f + (5 * Math.cos((ClientEvents.getClientTicks() + i * 24) / 40)))));
                            guiGraphics.pose().scale(1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f), 1 - (craftPercent * 0.5f));

                            guiGraphics.pose().scale(0.4f, 0.4f, 0.4f);
                            renderItem(items[((int)ClientEvents.getClientTicksWithoutPartial() / 40) % items.length], minecraft.level, guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT);
                            guiGraphics.pose().popPose();
                        }
                    }


                }
            } else {

                if (input.getFluid().is(Tags.Fluids.GASEOUS))
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, fillPercentage, 1, OverlayTexture.NO_OVERLAY);
                else
                    MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, 1, fillPercentage, OverlayTexture.NO_OVERLAY);

            }



            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flatLighting) {
                Lighting.setupFor3DItems();
            }

            guiGraphics.pose().popPose();


            if (!output.isEmpty() && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || (recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) && !compare))) {

                output2.draw(guiGraphics, 138, 16);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
                minecraft.font.drawInBatch(Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"), 139 * 1.666f, 38 * 1.666f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                guiGraphics.pose().popPose();
            } else {

                output1.draw(guiGraphics, 138, 16);
            }
        }





    }

    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
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
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}