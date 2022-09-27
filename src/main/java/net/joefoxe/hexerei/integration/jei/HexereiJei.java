package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.data.recipes.*;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.screen.BroomScreen;
import net.joefoxe.hexerei.screen.CofferScreen;
import net.joefoxe.hexerei.screen.MixingCauldronScreen;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FALLING;

@JeiPlugin
public class HexereiJei implements IModPlugin {


    public static IRecipesGui runtime;
    public static RecipeManager recipeManager;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Hexerei.MOD_ID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

        HexereiJei.runtime = jeiRuntime.getRecipesGui();
        IModPlugin.super.onRuntimeAvailable(jeiRuntime);
    }


    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
        PotionFluid potionFluid = ModFluids.POTION.get();
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
    }


    public static IRecipeSlotTooltipCallback addFluidTooltip() {
        return addFluidTooltip(-1);
    }

    public static IRecipeSlotTooltipCallback addFluidTooltip(int mbAmount) {
        return (view, tooltip) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
            if (displayed.isEmpty())
                return;

            FluidStack fluidStack = displayed.get();

            if (fluidStack.getFluid().isSame(ModFluids.POTION.get())) {
                Component name = fluidStack.getDisplayName();
                if (tooltip.isEmpty())
                    tooltip.add(0, name);
                else
                    tooltip.set(0, name);

                ArrayList<Component> potionTooltip = new ArrayList<>();
                PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
                tooltip.addAll(1, potionTooltip.stream().toList());
            }

            int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
            Component text = Component.literal(String.valueOf(amount)).append(Component.translatable("hexerei.generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
            if (tooltip.isEmpty())
                tooltip.add(0, text);
            else {
                List<Component> siblings = tooltip.get(0).getSiblings();
                siblings.add(Component.literal(" "));
                siblings.add(text);
            }
        };
    }
    public static IRecipeSlotTooltipCallback addFluidTooltip(int mbAmount, Component component) {
        return addFluidTooltip(mbAmount, List.of(component));
    }

    public static IRecipeSlotTooltipCallback addFluidTooltip(int mbAmount, List<Component> list) {
        return (view, tooltip) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
            if (displayed.isEmpty())
                return;

            tooltip.addAll(list);

            FluidStack fluidStack = displayed.get();

            if (fluidStack.getFluid().isSame(ModFluids.POTION.get())) {
                Component name = fluidStack.getDisplayName();
                if (tooltip.isEmpty())
                    tooltip.add(0, name);
                else
                    tooltip.set(0, name);

                ArrayList<Component> potionTooltip = new ArrayList<>();
                PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
                tooltip.addAll(1, potionTooltip.stream().toList());
            }

            int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
            Component text = Component.literal(String.valueOf(amount)).append(Component.translatable("hexerei.generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
            if (tooltip.isEmpty())
                tooltip.add(0, text);
            else {
                List<Component> siblings = tooltip.get(0).getSiblings();
                siblings.add(Component.literal(" "));
                siblings.add(text);
            }
        };
    }

    public static IRecipeSlotTooltipCallback addFluidTooltipDipper(int mbAmount) {
        return (view, tooltip) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
            if (displayed.isEmpty())
                return;

            List<Component> list = tooltip.get(tooltip.size() - 2).getSiblings();

            list.add(Component.translatable(" - per dip action").withStyle(Style.EMPTY.withColor(11184810)));

            FluidStack fluidStack = displayed.get();

            if (fluidStack.getFluid().isSame(ModFluids.POTION.get())) {
                Component name = fluidStack.getDisplayName();
                if (tooltip.isEmpty())
                    tooltip.add(0, name);
                else
                    tooltip.set(0, name);

                ArrayList<Component> potionTooltip = new ArrayList<>();
                PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
                tooltip.addAll(1, potionTooltip.stream().toList());
            }

            int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
            Component text = Component.literal(String.valueOf(amount)).append(Component.translatable("hexerei.generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
            if (tooltip.isEmpty())
                tooltip.add(0, text);
            else {
                List<Component> siblings = tooltip.get(0).getSiblings();
                siblings.add(Component.literal(" "));
                siblings.add(text);
            }
        };
    }


    public static IRecipeSlotTooltipCallback addExtraTooltips(List<Component> list) {
        return (view, tooltip) -> {
            if (list.isEmpty())
                return;

            tooltip.addAll(list);
        };
    }

    public static List<FluidStack> withImprovedVisibility(List<FluidStack> stacks) {
        return stacks.stream()
                .map(HexereiJei::withImprovedVisibility)
                .collect(Collectors.toList());
    }

    public static FluidStack withImprovedVisibility(FluidStack stack) {
        FluidStack display = stack.copy();
        int displayedAmount = (int) (stack.getAmount() * .75f) + 250;
        display.setAmount(displayedAmount);
        return display;
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

//        HexereiRecipeCategory<?>
//
//                milling = builder(AbstractCrushingRecipe.class)
//                .addTypedRecipes(AllRecipeTypes.MILLING)
//                .catalyst(AllBlocks.MILLSTONE::get)
//                .doubleItemIcon(AllBlocks.MILLSTONE.get(), AllItems.WHEAT_FLOUR.get())
//                .emptyBackground(177, 53)
//                .build("milling", MillingCategory::new),

        if(PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty())
            PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes();

//        ForgeRegistries.
        registration.addRecipeCategories(
                new MixingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new AddToCandleRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new KeychainApplyRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), "Potion"),
                new DipperRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PestleAndMortarRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new WoodcutterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new DryingRackRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CANDLE_DIPPER.get()), new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HERB_DRYING_RACK.get()), new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WILLOW_WOODCUTTER.get()), new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MAHOGANY_WOODCUTTER.get()), new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get()), new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class));
    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MixingCauldronScreen.class, 101, 41, 24, 24,
                new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class),
                new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class),
                new RecipeType<>(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class));

        registration.addGuiContainerHandler(MixingCauldronScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(MixingCauldronScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                Rect2i rect2i = new Rect2i(gui.getGuiLeft() + 23, gui.getGuiTop(), 142, 97);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft() + 160, gui.getGuiTop() + 32, 49, 48);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() + 97, 188, 30);
                ret.add(rect2i);
                return ret;
            }
        });
        registration.addGuiContainerHandler(CofferScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(CofferScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop(), 214, 157);
                ret.add(rect2i);
                return ret;
            }
        });
        registration.addGuiContainerHandler(BroomScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(BroomScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - BroomContainer.OFFSET, 214, 82 + gui.offset);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() + 79 + gui.offset - BroomContainer.OFFSET, 214, 34);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft() + 184, gui.getGuiTop() + 55 + gui.offset + ((int)gui.dropdownOffset) - BroomContainer.OFFSET, 26, 58);
                ret.add(rect2i);

                return ret;
            }
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        recipeManager = rm;
        List<MixingCauldronRecipe> mixing_recipes = rm.getAllRecipesFor(MixingCauldronRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class), mixing_recipes);


        if(Minecraft.getInstance().level != null) {
            List<CraftingRecipe> add_to_candle_recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);//rm.getAllRecipesFor(AddToCandleRecipe.Type.INSTANCE);
            add_to_candle_recipes = add_to_candle_recipes.stream().filter((craftingRecipe) -> {
                return craftingRecipe instanceof AddToCandleRecipe;
            }).toList();
            registration.addRecipes(new RecipeType<>(AddToCandleRecipeCategory.UID, AddToCandleRecipe.class), add_to_candle_recipes);
        }
        if(Minecraft.getInstance().level != null) {
            List<CraftingRecipe> keychainRecipe = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);//rm.getAllRecipesFor(AddToCandleRecipe.Type.INSTANCE);
            keychainRecipe = keychainRecipe.stream().filter((craftingRecipe) -> {
                return craftingRecipe instanceof KeychainRecipe;
            }).toList();
            registration.addRecipes(new RecipeType<>(KeychainApplyRecipeCategory.UID, KeychainRecipe.class), keychainRecipe);
        }
        List<FluidMixingRecipe> fluid_mixing_recipes = rm.getAllRecipesFor(FluidMixingRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class), fluid_mixing_recipes);

        if(PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty())
            PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes();
        if(PotionMixingRecipes.ALL != null)
            registration.addRecipes(new RecipeType<>(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class), PotionMixingRecipes.ALL);

        List<PestleAndMortarRecipe> pestle_and_mortar_recipes = rm.getAllRecipesFor(PestleAndMortarRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class), pestle_and_mortar_recipes);

        List<DipperRecipe> dipper_recipes = rm.getAllRecipesFor(DipperRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class), dipper_recipes);

        List<DryingRackRecipe> drying_rack_recipes = rm.getAllRecipesFor(DryingRackRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class), drying_rack_recipes);

        List<WoodcutterRecipe> woodcutter_recipes = rm.getAllRecipesFor(WoodcutterRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class), woodcutter_recipes);
    }




    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new MixingCauldronTransferInfo(registration.getTransferHelper()), new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class));
    }

    public static void showUses(FluidStack fluid) {
        //recipe catalyst lookup
        if(HexereiJei.runtime == null)
            return;
        HexereiJei.runtime.show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<FluidStack>() {
                    @Override
                    public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                        return ForgeTypes.FLUID_STACK;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return fluid;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == ForgeTypes.FLUID_STACK)
                            return ((Optional<V>) Optional.of(fluid));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return RecipeIngredientRole.CATALYST;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                return Optional.empty();
            }
        });
        if(!(Minecraft.getInstance().screen instanceof IRecipesGui)){
            if(HexereiJei.runtime == null)
                return;
            HexereiJei.runtime.show(new IFocus<FluidStack>() {
                @Override
                public ITypedIngredient<FluidStack> getTypedValue() {
                    return new ITypedIngredient<FluidStack>() {
                        @Override
                        public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                            return ForgeTypes.FLUID_STACK;
                        }

                        @Override
                        public FluidStack getIngredient() {
                            return fluid;
                        }

                        @Override
                        public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                            if (ingredientType == ForgeTypes.FLUID_STACK)
                                return ((Optional<V>) Optional.of(fluid));
                            return Optional.empty();
                        }
                    };
                }

                @Override
                public RecipeIngredientRole getRole() {
                    return RecipeIngredientRole.INPUT;
                }

                @Override
                public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                    return Optional.empty();
                }
            });
        }

//        HexereiJei.runtime.showTypes(list);
    }
    public static void showUses(ItemStack item) {
//        ArrayList<RecipeType<?>> list = new ArrayList<>();
//        list.add(new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class));
        //if item is cauldron then do type
        //recipe catalyst lookup
        if(HexereiJei.runtime == null)
            return;
        HexereiJei.runtime.show(new IFocus<ItemStack>() {
            @Override
            public ITypedIngredient<ItemStack> getTypedValue() {
                return new ITypedIngredient<ItemStack>() {
                    @Override
                    public IIngredientType<ItemStack> getType() {
                        return VanillaTypes.ITEM_STACK;
                    }

                    @Override
                    public ItemStack getIngredient() {
                        return item;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == VanillaTypes.ITEM_STACK)
                            return ((Optional<V>) Optional.of(item));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return RecipeIngredientRole.CATALYST;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                return Optional.empty();
            }
        });
        if(!(Minecraft.getInstance().screen instanceof IRecipesGui)){
            if(HexereiJei.runtime == null)
                return;
            HexereiJei.runtime.show(new IFocus<ItemStack>() {
                @Override
                public ITypedIngredient<ItemStack> getTypedValue() {
                    return new ITypedIngredient<ItemStack>() {
                        @Override
                        public IIngredientType<ItemStack> getType() {
                            return VanillaTypes.ITEM_STACK;
                        }

                        @Override
                        public ItemStack getIngredient() {
                            return item;
                        }

                        @Override
                        public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                            if (ingredientType == VanillaTypes.ITEM_STACK)
                                return ((Optional<V>) Optional.of(item));
                            return Optional.empty();
                        }
                    };
                }

                @Override
                public RecipeIngredientRole getRole() {
                    return RecipeIngredientRole.INPUT;
                }

                @Override
                public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                    return Optional.empty();
                }
            });
        }

//        HexereiJei.runtime.showTypes(list);
    }
    public static void showRecipe(ItemStack item) {
        if(HexereiJei.runtime == null)
            return;
        HexereiJei.runtime.show(new IFocus<ItemStack>() {
            @Override
            public ITypedIngredient<ItemStack> getTypedValue() {
                return new ITypedIngredient<ItemStack>() {
                    @Override
                    public IIngredientType<ItemStack> getType() {
                        return VanillaTypes.ITEM_STACK;
                    }

                    @Override
                    public ItemStack getIngredient() {
                        return item;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == VanillaTypes.ITEM_STACK)
                            return ((Optional<V>) Optional.of(item));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return RecipeIngredientRole.OUTPUT;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                return Optional.empty();
            }
        });
    }
    public static void showRecipe(FluidStack fluid) {
        if(HexereiJei.runtime == null)
            return;
        HexereiJei.runtime.show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<FluidStack>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return ForgeTypes.FLUID_STACK;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return fluid;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == ForgeTypes.FLUID_STACK)
                            return ((Optional<V>) Optional.of(fluid));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return RecipeIngredientRole.OUTPUT;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {

                return Optional.empty();
            }
        });
    }

}