package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {


//
//	public <T extends ForgeFlowingFluid> FluidBuilder<T, CreateRegistrate> virtualFluid(String name,
//																						FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
//		return entry(name,
//				c -> new VirtualFluidBuilder<>(self(), self(), name, c, Create.asResource("fluid/" + name + "_still"),
//						Create.asResource("fluid/" + name + "_flow"), typeFactory, factory));
//	}

//	private static ForgeFlowingFluid.Properties getBloodProperties() {
//		return new ForgeFlowingFluid.Properties(ModFluidTypes.BLOOD_FLUID_TYPE, BLOOD_FLUID, BLOOD_FLOWING)
//						.block(BLOOD_BLOCK)
//						.bucket(ModItems.BLOOD_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(2);
//	}
//
//
//	private static ForgeFlowingFluid.Properties getQuicksilverProperties() {
//		return new ForgeFlowingFluid.Properties(ModFluidTypes.QUICKSILVER_FLUID_TYPE, QUICKSILVER_FLUID, QUICKSILVER_FLOWING)
//						.block(QUICKSILVER_BLOCK)
//						.bucket(ModItems.QUICKSILVER_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(2);
//	}
//
//	private static ForgeFlowingFluid.Properties getTallowProperties() {
//		return new ForgeFlowingFluid.Properties(ModFluidTypes.TALLOW_FLUID_TYPE, TALLOW_FLUID, TALLOW_FLOWING)
//						.block(TALLOW_BLOCK)
//						.bucket(ModItems.TALLOW_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(3);
//	}


	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Hexerei.MOD_ID);



	public static final DeferredHolder<Fluid, PotionFluid> POTION = FLUIDS.register("potion", PotionFluid::new);


	public static final DeferredHolder<Fluid, BloodFluid.Flowing> BLOOD_FLOWING = FLUIDS.register("blood_flowing", BloodFluid.Flowing::new);

	public static final DeferredHolder<Fluid, BloodFluid.Source> BLOOD_FLUID = FLUIDS.register("blood_fluid", BloodFluid.Source::new);

	public static final DeferredHolder<Block, LiquidBlock> BLOOD_BLOCK = ModBlocks.BLOCKS.register("blood", () -> new LiquidBlock(ModFluids.BLOOD_FLUID.value(),
					BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));


	//make quicksilver fluid eventually
	public static final DeferredHolder<Fluid, LavaFluid> QUICKSILVER_FLOWING = FLUIDS.register("quicksilver_flowing", () -> new LavaFluid.Flowing(){
		@Override
		public FluidType getFluidType() {
			return ModFluidTypes.QUICKSILVER_FLUID_TYPE.value();
		}
	});

	public static final DeferredHolder<Fluid, LavaFluid.Source> QUICKSILVER_FLUID = FLUIDS.register("quicksilver_fluid", () -> new LavaFluid.Source(){
		@Override
		public FluidType getFluidType() {
			return ModFluidTypes.QUICKSILVER_FLUID_TYPE.value();
		}
	});

	public static final DeferredHolder<Block, LiquidBlock> QUICKSILVER_BLOCK = ModBlocks.BLOCKS.register("quicksilver", () -> new LiquidBlock(ModFluids.QUICKSILVER_FLUID.value(),
					BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)));


	public static final DeferredHolder<Fluid, TallowFluid.Flowing> TALLOW_FLOWING = FLUIDS.register("tallow_flowing", TallowFluid.Flowing::new);

	public static final DeferredHolder<Fluid, TallowFluid.Source> TALLOW_FLUID = FLUIDS.register("tallow_fluid", TallowFluid.Source::new);

	public static final DeferredHolder<Block, LiquidBlock> TALLOW_BLOCK = ModBlocks.BLOCKS.register("tallow", () -> new LiquidBlock(ModFluids.TALLOW_FLUID.value(),
					BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));


	public static void register(IEventBus eventBus) {
		FLUIDS.register(eventBus);
	}

}


