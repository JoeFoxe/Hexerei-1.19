package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Hexerei.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, Hexerei.MOD_ID);


//    public static final RegistryObject<MixingCauldronRecipe.Serializer> MIXING_SERIALIZER = RECIPE_SERIALIZERS.register("mixingcauldron", MixingCauldronRecipe.Serializer::new);
//    public static RecipeType<MixingCauldronRecipe> MIXING_CAULDRON_RECIPE = new MixingCauldronRecipe.MixingCauldronRecipeType();


//    public static final RegistryObject<DipperRecipe.Serializer> DIPPER_SERIALIZER = RECIPE_SERIALIZERS.register("dipper", DipperRecipe.Serializer::new);
//    public static RecipeType<DipperRecipe> DIPPER_RECIPE = new DipperRecipe.DipperRecipeType();


//    public static final RegistryObject<DryingRackRecipe.Serializer> DRYING_RACK_SERIALIZER = RECIPE_SERIALIZERS.register("drying_rack", DryingRackRecipe.Serializer::new);
//    public static RecipeType<DryingRackRecipe> DRYING_RACK_RECIPE = new DryingRackRecipe.DryingRackRecipeType();


//    public static final RegistryObject<PestleAndMortarRecipe.Serializer> PESTLE_AND_MORTAR_SERIALIZER = RECIPE_SERIALIZERS.register("pestle_and_mortar", PestleAndMortarRecipe.Serializer::new);
//    public static RecipeType<PestleAndMortarRecipe> PESTLE_AND_MORTAR_RECIPE = new PestleAndMortarRecipe.PestleAndMortarRecipeType();


    public static final RegistryObject<RecipeType<MixingCauldronRecipe>> MIXING_CAULDRON_TYPE = RECIPE_TYPES.register("mixingcauldron", () -> MixingCauldronRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<MixingCauldronRecipe>> MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("mixingcauldron", () -> MixingCauldronRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<FluidMixingRecipe>> FLUID_MIXING_TYPE = RECIPE_TYPES.register("fluid_mixing", () -> FluidMixingRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<FluidMixingRecipe>> FLUID_MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fluid_mixing", () -> FluidMixingRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<DipperRecipe>> DIPPER_TYPE = RECIPE_TYPES.register("dipper", () -> DipperRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<DipperRecipe>> DIPPER_SERIALIZER =
            RECIPE_SERIALIZERS.register("dipper", () -> DipperRecipe.Serializer.INSTANCE);



    public static final RegistryObject<RecipeType<DryingRackRecipe>> DRYING_RACK_TYPE = RECIPE_TYPES.register("drying_rack", () -> DryingRackRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<DryingRackRecipe>> DRYING_RACK_SERIALIZER =
            RECIPE_SERIALIZERS.register("drying_rack", () -> DryingRackRecipe.Serializer.INSTANCE);



    public static final RegistryObject<RecipeType<PestleAndMortarRecipe>> PESTLE_AND_MORTAR_TYPE = RECIPE_TYPES.register("pestle_and_mortar", () -> PestleAndMortarRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<PestleAndMortarRecipe>> PESTLE_AND_MORTAR_SERIALIZER =
            RECIPE_SERIALIZERS.register("pestle_and_mortar", () -> PestleAndMortarRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<AddToCandleRecipe>> ADD_TO_CANDLE_TYPE = RECIPE_TYPES.register("add_to_candle", () -> AddToCandleRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<AddToCandleRecipe>> ADD_TO_CANDLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("add_to_candle", () -> AddToCandleRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<CutCandleRecipe>> CUT_CANDLE_TYPE = RECIPE_TYPES.register("cut_candle", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<CutCandleRecipe>> CUT_CANDLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("cut_candle", () -> new SimpleRecipeSerializer<>(CutCandleRecipe::new));



    public static final RegistryObject<RecipeSerializer<WoodcutterRecipe>> WOODCUTTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("woodcutting", () -> WoodcutterRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<FillWaxingKitRecipe>> FILL_WAXING_KIT_TYPE = RECIPE_TYPES.register("fill_waxing_kit", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<FillWaxingKitRecipe>> FILL_WAXING_KIT_SERIALIZER =
            RECIPE_SERIALIZERS.register("fill_waxing_kit", () -> new SimpleRecipeSerializer<>(FillWaxingKitRecipe::new));


    public static final RegistryObject<RecipeType<CrowFluteRecipe>> CROW_FLUTE_DYE_TYPE = RECIPE_TYPES.register("crow_flute_dye", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<CrowFluteRecipe>> CROW_FLUTE_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("crow_flute_dye", CrowFluteRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_DYE_TYPE = RECIPE_TYPES.register("book_of_shadows_dye", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("book_of_shadows_dye", BookOfShadowsRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<KeychainRecipe>> KEYCHAIN_APPLY_TYPE = RECIPE_TYPES.register("keychain_apply", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<KeychainRecipe>> KEYCHAIN_APPLY_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_apply", () -> new SimpleRecipeSerializer<>(KeychainRecipe::new));

    public static final RegistryObject<RecipeType<KeychainUndoRecipe>> KEYCHAIN_UNDO_TYPE = RECIPE_TYPES.register("keychain_undo", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<KeychainUndoRecipe>> KEYCHAIN_UNDO_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_undo", () -> new SimpleRecipeSerializer<>(KeychainUndoRecipe::new));

    public static final RegistryObject<RecipeType<WhistleBindRecipe>> WHISTLE_BIND_TYPE = RECIPE_TYPES.register("whistle_bind", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<WhistleBindRecipe>> WHISTLE_BIND_SERIALIZER = RECIPE_SERIALIZERS.register("whistle_bind", () -> new SimpleRecipeSerializer<>(WhistleBindRecipe::new));

    public static final RegistryObject<RecipeType<CrowAmuletRecipe>> CROW_AMULET_APPLY_TYPE = RECIPE_TYPES.register("crow_amulet_apply", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<CrowAmuletRecipe>> CROW_AMULET_APPLY_SERIALIZER = RECIPE_SERIALIZERS.register("crow_amulet_apply", () -> new SimpleRecipeSerializer<>(CrowAmuletRecipe::new));

    public static final RegistryObject<RecipeType<CrowAmuletUndoRecipe>> CROW_AMULET_UNDO_TYPE = RECIPE_TYPES.register("crow_amulet_undo", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<CrowAmuletUndoRecipe>> CROW_AMULET_UNDO_SERIALIZER = RECIPE_SERIALIZERS.register("crow_amulet_undo", () -> new SimpleRecipeSerializer<>(CrowAmuletUndoRecipe::new));

    public static final RegistryObject<RecipeType<WoodcutterRecipe>> WOODCUTTING_TYPE = RECIPE_TYPES.register("woodcutting", ModRecipeType::new);


    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);

    }



}
