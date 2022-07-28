package net.joefoxe.hexerei.world.structure;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.structure.structures.DarkCovenStructure;
import net.joefoxe.hexerei.world.structure.structures.WitchHutStructure;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraftforge.registries.*;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModStructures {
    public static final DeferredRegister<StructureFeature<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Hexerei.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    public static final RegistryObject<StructureFeature<?>> DARK_COVEN = DEFERRED_REGISTRY_STRUCTURE.register("dark_coven", DarkCovenStructure::new);
    public static final RegistryObject<StructureFeature<?>> WITCH_HUT = DEFERRED_REGISTRY_STRUCTURE.register("witch_hut", WitchHutStructure::new);
}