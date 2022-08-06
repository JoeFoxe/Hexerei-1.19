package net.joefoxe.hexerei.fluid;

import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {

    public static final ResourceLocation QUICKSILVER_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_still");
    public static final ResourceLocation QUICKSILVER_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_flow");
    public static final ResourceLocation QUICKSILVER_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_overlay");
    public static final ResourceLocation BLOOD_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_still");
    public static final ResourceLocation BLOOD_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_flow");
    public static final ResourceLocation BLOOD_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_overlay");
    public static final ResourceLocation TALLOW_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_still");
    public static final ResourceLocation TALLOW_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_flow");
    public static final ResourceLocation TALLOW_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_overlay");


    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Hexerei.MOD_ID);


    public static final RegistryObject<FluidType> QUICKSILVER_FLUID_TYPE = register("quicksilver_fluid",
            FluidType.Properties.create().lightLevel(2).density(15).viscosity(5).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.BUCKET_FILL_LAVA), QUICKSILVER_STILL_RL, QUICKSILVER_FLOWING_RL, QUICKSILVER_OVERLAY_RL, 0xF9FFFFFF);

    public static final RegistryObject<FluidType> BLOOD_FLUID_TYPE = register("blood_fluid",
            FluidType.Properties.create().lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.HONEY_DRINK), BLOOD_STILL_RL, BLOOD_FLOWING_RL, BLOOD_OVERLAY_RL, 0xF9FFFFFF);

    public static final RegistryObject<FluidType> TALLOW_FLUID_TYPE = register("tallow_fluid",
            FluidType.Properties.create().lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.HONEY_DRINK), TALLOW_STILL_RL, TALLOW_FLOWING_RL, TALLOW_OVERLAY_RL, 0xF9FFFFFF);

    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, int color) {
        return FLUID_TYPES.register(name, () -> new CustomFluidType(stillTexture, flowingTexture, overlayTexture,
                color, new Vector3f(48f / 256f, 4f / 255f, 4f / 255f), properties));
    }


    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
