package net.joefoxe.hexerei.world.structure;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.structure.structures.DarkCovenStructure;
import net.joefoxe.hexerei.world.structure.structures.WitchHutStructure;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.*;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, Hexerei.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    public static final RegistryObject<StructureType<DarkCovenStructure>> DARK_COVEN = DEFERRED_REGISTRY_STRUCTURE.register("dark_coven", () -> () -> DarkCovenStructure.CODEC);
    public static final RegistryObject<StructureType<WitchHutStructure>> WITCH_HUT = DEFERRED_REGISTRY_STRUCTURE.register("witch_hut", () -> () -> WitchHutStructure.CODEC);
}