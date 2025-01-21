package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {

    public static final ResourceLocation QUICKSILVER_STILL_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/quicksilver_still");
    public static final ResourceLocation QUICKSILVER_FLOWING_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/quicksilver_flow");
    public static final ResourceLocation QUICKSILVER_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/quicksilver_overlay");
    public static final ResourceLocation BLOOD_STILL_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/blood_still");
    public static final ResourceLocation BLOOD_FLOWING_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/blood_flow");
    public static final ResourceLocation BLOOD_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/blood_overlay");
    public static final ResourceLocation TALLOW_STILL_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/tallow_still");
    public static final ResourceLocation TALLOW_FLOWING_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/tallow_flow");
    public static final ResourceLocation TALLOW_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "block/tallow_overlay");


    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Hexerei.MOD_ID);


    public static final DeferredHolder<FluidType, FluidType> POTION_FLUID_TYPE = FLUID_TYPES.register("potion_fluid", () ->
            new PotionFluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(15).viscosity(5).sound(SoundAction.get("bucket_fill"), SoundEvents.BUCKET_FILL)));

    public static final DeferredHolder<FluidType, FluidType> QUICKSILVER_FLUID_TYPE = FLUID_TYPES.register("quicksilver_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(15).viscosity(5).sound(SoundAction.get("bucket_fill"), SoundEvents.BUCKET_FILL_LAVA)));

    public static final DeferredHolder<FluidType, FluidType> BLOOD_FLUID_TYPE = FLUID_TYPES.register("blood_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"), SoundEvents.HONEY_DRINK)));

    public static final DeferredHolder<FluidType, FluidType> TALLOW_FLUID_TYPE = FLUID_TYPES.register("tallow_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"), SoundEvents.HONEY_DRINK)));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
