package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
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


    public static final RegistryObject<RecipeSerializer<MixingCauldronRecipe>> MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("mixingcauldron", () -> MixingCauldronRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<DipperRecipe>> DIPPER_SERIALIZER =
            RECIPE_SERIALIZERS.register("dipper", () -> DipperRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<DryingRackRecipe>> DRYING_RACK_SERIALIZER =
            RECIPE_SERIALIZERS.register("drying_rack", () -> DryingRackRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PestleAndMortarRecipe>> PESTLE_AND_MORTAR_SERIALIZER =
            RECIPE_SERIALIZERS.register("pestle_and_mortar", () -> PestleAndMortarRecipe.Serializer.INSTANCE);


    public static final RegistryObject<RecipeType<CrowFluteRecipe>> CROW_FLUTE_DYE_TYPE = RECIPE_TYPES.register("crow_flute_dye", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<CrowFluteRecipe>> CROW_FLUTE_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("crow_flute_dye", () -> new CrowFluteRecipe.Serializer());

    public static final RegistryObject<RecipeType<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_DYE_TYPE = RECIPE_TYPES.register("book_of_shadows_dye", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("book_of_shadows_dye", () -> new BookOfShadowsRecipe.Serializer());

    public static final RegistryObject<RecipeType<KeychainRecipe>> KEYCHAIN_APPLY_TYPE = RECIPE_TYPES.register("keychain_apply", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<KeychainRecipe>> KEYCHAIN_APPLY_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_apply", () -> new SimpleRecipeSerializer<>(KeychainRecipe::new));

    public static final RegistryObject<RecipeType<KeychainUndoRecipe>> KEYCHAIN_UNDO_TYPE = RECIPE_TYPES.register("keychain_undo", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<KeychainUndoRecipe>> KEYCHAIN_UNDO_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_undo", () -> new SimpleRecipeSerializer<>(KeychainUndoRecipe::new));

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);

//        Registry.register(Registry.RECIPE_TYPE, MixingCauldronRecipe.TYPE_ID, MIXING_CAULDRON_RECIPE);

//        Registry.register(Registry.RECIPE_TYPE, DipperRecipe.TYPE_ID, DIPPER_RECIPE);

//        Registry.register(Registry.RECIPE_TYPE, DryingRackRecipe.TYPE_ID, DRYING_RACK_RECIPE);

//        Registry.register(Registry.RECIPE_TYPE, PestleAndMortarRecipe.TYPE_ID, PESTLE_AND_MORTAR_RECIPE);

    }



}
