package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.*;
import net.joefoxe.hexerei.screen.BroomScreen;
import net.joefoxe.hexerei.screen.CofferScreen;
import net.joefoxe.hexerei.screen.MixingCauldronScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import Mode;

@JeiPlugin
public class HexereiJei implements IModPlugin {


    public static IRecipesGui runtime;
    public static RecipeManager recipeManager;


//    public static final RecipeType<MixingCauldronRecipe> TYPE = RecipeType.create(Hexerei.MOD_ID, "mixingcauldron", MixingCauldronRecipe.class);
    public static final ResourceLocation MIXING_CAULDRON_UID = new ResourceLocation(Hexerei.MOD_ID, "mixingcauldron");
    public static final ResourceLocation DIPPER_UID = new ResourceLocation(Hexerei.MOD_ID, "dipper");
    public static final ResourceLocation DRYING_RACK_UID = new ResourceLocation(Hexerei.MOD_ID, "drying_rack");
    public static final ResourceLocation PESTLE_AND_MORTAR_UID = new ResourceLocation(Hexerei.MOD_ID, "pestle_and_mortar");

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
    public void registerCategories(IRecipeCategoryRegistration registration) {


        registration.addRecipeCategories(
                new MixingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new DipperRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PestleAndMortarRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new DryingRackRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MIXING_CAULDRON.get()), MIXING_CAULDRON_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CANDLE_DIPPER.get()), DIPPER_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HERB_DRYING_RACK.get()), DRYING_RACK_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PESTLE_AND_MORTAR.get()), PESTLE_AND_MORTAR_UID);
    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MixingCauldronScreen.class, 66, 30, 28, 26, new RecipeType<>(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class));

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
                Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop(), 214, 82 + gui.offset);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() + 79 + gui.offset, 214, 34);
                ret.add(rect2i);
                rect2i = new Rect2i(gui.getGuiLeft() + 184, gui.getGuiTop() + 55 + gui.offset + ((int)gui.dropdownOffset), 26, 58);
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
        List<PestleAndMortarRecipe> pestle_and_mortar_recipes = rm.getAllRecipesFor(PestleAndMortarRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class), pestle_and_mortar_recipes);
        List<DipperRecipe> dipper_recipes = rm.getAllRecipesFor(DipperRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(DipperRecipeCategory.UID, DipperRecipe.class), dipper_recipes);
        List<DryingRackRecipe> drying_rack_recipes = rm.getAllRecipesFor(DryingRackRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(DryingRackRecipeCategory.UID, DryingRackRecipe.class), drying_rack_recipes);
    }




    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

        registration.addRecipeTransferHandler(new MixingCauldronTransferInfo(), ModRecipeTypes.MIXING_SERIALIZER.getId());
//        registration.addRecipeTransferHandler(MixingCauldronContainer.class, registration.getTransferHelper(), 8, ModRecipeTypes.MIXING_SERIALIZER.getId());
//        IRecipeTransferHandlerHelper handlerHelper = registration.getTransferHelper();
//        IStackHelper stackHelper = registration.getJeiHelpers().getStackHelper();
//        registration.addRecipeTransferHandler(new CraftingContainerRecipeTransferHandlerBase<MixingCauldronContainer>(handlerHelper, stackHelper) {
//            @Override
//            public Class<MixingCauldronContainer> getContainerClass() {
//                return MixingCauldronContainer.class;
//            }
//        }, RecipeTypes.CRAFTING);
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

            @Override
            public Mode getMode() {
                return Mode.INPUT;
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

                @Override
                public Mode getMode() {
                    return Mode.INPUT;
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

            @Override
            public Mode getMode() {
                return Mode.INPUT;
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

                @Override
                public Mode getMode() {
                    return Mode.INPUT;
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

            @Override
            public Mode getMode() {
                return Mode.OUTPUT;
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

            @Override
            public Mode getMode() {
                return Mode.OUTPUT;
            }
        });
    }
}