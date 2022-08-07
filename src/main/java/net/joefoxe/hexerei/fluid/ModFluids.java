package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final Material BLOOD = (new Material.Builder(MaterialColor.WATER)).noCollider().nonSolid().replaceable().liquid().build();
    public static final Material TALLOW = (new Material.Builder(MaterialColor.WATER)).noCollider().nonSolid().replaceable().liquid().build();


    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Hexerei.MOD_ID);


    public static final RegistryObject<FlowingFluid> QUICKSILVER_FLUID = FLUIDS.register("quicksilver_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.QUICKSILVER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> QUICKSILVER_FLOWING = FLUIDS.register("quicksilver_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.QUICKSILVER_PROPERTIES));

    public static final ForgeFlowingFluid.Properties QUICKSILVER_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.QUICKSILVER_FLUID_TYPE, QUICKSILVER_FLUID, QUICKSILVER_FLOWING).slopeFindDistance(2).levelDecreasePerBlock(2)
            .block(ModFluids.QUICKSILVER_BLOCK).bucket(ModItems.QUICKSILVER_BUCKET); //, FluidAttributes.builder(QUICKSILVER_STILL_RL, QUICKSILVER_FLOWING_RL).density(15).luminosity(15).viscosity(15).sound(SoundEvents.BUCKET_EMPTY_LAVA).overlay(QUICKSILVER_OVERLAY_RL).color(0xF9FFFFFF)).slopeFindDistance(2).levelDecreasePerBlock(2).block(() -> ModFluids.QUICKSILVER_BLOCK.get()).bucket(() -> ModItems.QUICKSILVER_BUCKET.get());

    public static final RegistryObject<LiquidBlock> QUICKSILVER_BLOCK = ModBlocks.BLOCKS.register("quicksilver", () -> new LiquidBlock(ModFluids.QUICKSILVER_FLUID,
            BlockBehaviour.Properties.of(Material.LAVA).noCollission().explosionResistance(100f).noLootTable()));



    public static final RegistryObject<BloodFluid.Source> BLOOD_FLUID = FLUIDS.register("blood_fluid", () -> new BloodFluid.Source(ModFluids.BLOOD_PROPERTIES));

    public static final RegistryObject<BloodFluid.Flowing> BLOOD_FLOWING = FLUIDS.register("blood_flowing", () -> new BloodFluid.Flowing(ModFluids.BLOOD_PROPERTIES));

    public static final ForgeFlowingFluid.Properties BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.BLOOD_FLUID_TYPE, BLOOD_FLUID, BLOOD_FLOWING).slopeFindDistance(2).levelDecreasePerBlock(2)
            .block(ModFluids.BLOOD_BLOCK).bucket(ModItems.BLOOD_BUCKET); //, FluidAttributes.builder(QUICKSILVER_STILL_RL, QUICKSILVER_FLOWING_RL).density(15).luminosity(15).viscosity(15).sound(SoundEvents.BUCKET_EMPTY_LAVA).overlay(QUICKSILVER_OVERLAY_RL).color(0xF9FFFFFF)).slopeFindDistance(2).levelDecreasePerBlock(2).block(() -> ModFluids.QUICKSILVER_BLOCK.get()).bucket(() -> ModItems.QUICKSILVER_BUCKET.get());

    public static final RegistryObject<LiquidBlock> BLOOD_BLOCK = ModBlocks.BLOCKS.register("blood", () -> new LiquidBlock(ModFluids.BLOOD_FLUID,
            BlockBehaviour.Properties.of(BLOOD).noCollission().explosionResistance(100f).noLootTable()));




    public static final RegistryObject<BloodFluid.Source> TALLOW_FLUID = FLUIDS.register("tallow_fluid", () -> new BloodFluid.Source(ModFluids.TALLOW_PROPERTIES));

    public static final RegistryObject<BloodFluid.Flowing> TALLOW_FLOWING = FLUIDS.register("tallow_flowing", () -> new BloodFluid.Flowing(ModFluids.TALLOW_PROPERTIES));

    public static final ForgeFlowingFluid.Properties TALLOW_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.TALLOW_FLUID_TYPE, TALLOW_FLUID, TALLOW_FLOWING).slopeFindDistance(2).levelDecreasePerBlock(3)
            .block(ModFluids.TALLOW_BLOCK).bucket(ModItems.TALLOW_BUCKET); //, FluidAttributes.builder(QUICKSILVER_STILL_RL, QUICKSILVER_FLOWING_RL).density(15).luminosity(15).viscosity(15).sound(SoundEvents.BUCKET_EMPTY_LAVA).overlay(QUICKSILVER_OVERLAY_RL).color(0xF9FFFFFF)).slopeFindDistance(2).levelDecreasePerBlock(2).block(() -> ModFluids.QUICKSILVER_BLOCK.get()).bucket(() -> ModItems.QUICKSILVER_BUCKET.get());

    public static final RegistryObject<LiquidBlock> TALLOW_BLOCK = ModBlocks.BLOCKS.register("tallow", () -> new LiquidBlock(ModFluids.TALLOW_FLUID,
            BlockBehaviour.Properties.of(TALLOW).noCollission().explosionResistance(100f).noLootTable()));



    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }




}


