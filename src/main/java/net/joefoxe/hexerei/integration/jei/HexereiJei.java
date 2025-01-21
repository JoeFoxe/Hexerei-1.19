package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.data.recipes.*;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.screen.*;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static net.joefoxe.hexerei.container.CofferContainer.OFFSET;

@JeiPlugin
public class HexereiJei implements IModPlugin {


    public static IRecipesGui runtime;
    public static RecipeManager recipeManager;

    @Override
    public ResourceLocation getPluginUid() {
        return HexereiUtil.getResource("jei_plugin");
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
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid, interpreter);
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

        if(PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty())
            PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes(Minecraft.getInstance().level.potionBrewing());

        registration.addRecipeCategories(
                new AddToCandleRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluteRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new BookOfShadowsRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new MixingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), "Potion"),
                new DipperRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PestleAndMortarRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new WoodcutterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new KeychainApplyRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new BottlingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new BloodSigilRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlantPickingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new DryingRackRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CANDLE_DIPPER.get()), new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HERB_DRYING_RACK.get()), new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WILLOW_WOODCUTTER.get()), new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MAHOGANY_WOODCUTTER.get()), new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WITCH_HAZEL_WOODCUTTER.get()), new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get()), new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class));
    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MixingCauldronScreen.class, 101, 41, 24, 24,
                new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class),
                new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class),
                new RecipeType<>(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class),
                new RecipeType<>(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class),
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

        registration.addGuiContainerHandler(CrowScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(CrowScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - OFFSET, 188, 153);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft() + 184 - 28 + (int)gui.whitelistOffset, gui.getGuiTop() + 17 - OFFSET + 3, 39, 104 - 3);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft() - 5 - (int)gui.leftPanelOffset, gui.getGuiTop() + 17 - OFFSET + 3, 39, 104 - 3);
                ret.add(rect2i);
                return ret;
            }
        });

        registration.addGuiContainerHandler(OwlScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(OwlScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                if (!gui.quirkSideBarHidden){
                    Rect2i rect2i = new Rect2i(gui.getGuiLeft() + 174, gui.getGuiTop() + 37, 26, 26);
                    ret.add(rect2i);
                }
                return ret;
            }
        });

        registration.addGuiContainerHandler(CofferScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(CofferScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - OFFSET, 214, 157);
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

        List<RecipeHolder<CraftingRecipe>> add_to_candle_recipes = rm.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);//rm.getAllRecipesFor(AddToCandleRecipe.Type.INSTANCE);
        List<RecipeHolder<CraftingRecipe>> flute_dye_recipes = new ArrayList<>(add_to_candle_recipes);
        List<RecipeHolder<CraftingRecipe>> book_recipe = new ArrayList<>(add_to_candle_recipes);
        List<RecipeHolder<CraftingRecipe>> keychainRecipe = new ArrayList<>(add_to_candle_recipes);
        registration.addRecipes(new RecipeType<>(AddToCandleRecipeCategory.UID, AddToCandleRecipe.class), add_to_candle_recipes.stream().filter((craftingRecipe) -> {
            return craftingRecipe.value() instanceof AddToCandleRecipe;
        }).map(RecipeHolder::value).toList());

        registration.addRecipes(new RecipeType<>(FluteRecipeCategory.UID, CrowFluteRecipe.class), flute_dye_recipes.stream().filter((craftingRecipe) -> {
            return craftingRecipe.value() instanceof CrowFluteRecipe;
        }).map(RecipeHolder::value).toList());

        registration.addRecipes(RecipeTypes.CRAFTING, book_recipe.stream().filter((craftingRecipe) -> craftingRecipe.value() instanceof BookOfShadowsRecipe).toList());

        List<MixingCauldronRecipe> mixing_recipes = rm.getAllRecipesFor(MixingCauldronRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(RecipeType.create(MixingCauldronRecipeCategory.UID.getNamespace(), MixingCauldronRecipeCategory.UID.getPath(), MixingCauldronRecipe.class), mixing_recipes);

        registration.addRecipes(new RecipeType<>(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class), rm.getAllRecipesFor(FluidMixingRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList());

        if(PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty())
            PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes(Minecraft.getInstance().level.potionBrewing());
        registration.addRecipes(new RecipeType<>(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class), PotionMixingRecipes.ALL);

        registration.addRecipes(new RecipeType<>(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class), BottlingRecipeJEI.getRecipeList(rm));

        List<DipperRecipe> recipes = rm.getAllRecipesFor(DipperRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class), recipes);

        registration.addRecipes(new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class), rm.getAllRecipesFor(PestleAndMortarRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList());

        registration.addRecipes(new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class), rm.getAllRecipesFor(DryingRackRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList());

        List<WoodcutterRecipe> list = new ArrayList<>(rm.getAllRecipesFor(WoodcutterRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList());
        list.addAll(WoodcutterRecipes.ALL);
        registration.addRecipes(new RecipeType<>(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class), list);

        registration.addRecipes(new RecipeType<>(KeychainApplyRecipeCategory.UID, KeychainRecipe.class), keychainRecipe.stream().filter((craftingRecipe) -> {
            return craftingRecipe.value() instanceof KeychainRecipe;
        }).map(RecipeHolder::value).toList());

        registration.addRecipes(new RecipeType<>(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class), BloodSigilRecipeJEI.getRecipeList());

        registration.addRecipes(new RecipeType<>(PlantPickingRecipeCategory.UID, PlantPickingRecipeJEI.class), PlantPickingRecipeJEI.getRecipeList());
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
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                        return NeoForgeTypes.FLUID_STACK;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return fluid;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == NeoForgeTypes.FLUID_STACK)
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
                    return new ITypedIngredient<>() {
                        @Override
                        public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                            return NeoForgeTypes.FLUID_STACK;
                        }

                        @Override
                        public FluidStack getIngredient() {
                            return fluid;
                        }

                        @Override
                        public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                            if (ingredientType == NeoForgeTypes.FLUID_STACK)
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
                return new ITypedIngredient<>() {
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
                    return new ITypedIngredient<>() {
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
                return new ITypedIngredient<>() {
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
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return NeoForgeTypes.FLUID_STACK;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return fluid;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == NeoForgeTypes.FLUID_STACK)
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