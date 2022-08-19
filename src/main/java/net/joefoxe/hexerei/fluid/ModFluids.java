package net.joefoxe.hexerei.fluid;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.FluidBuilder;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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
import org.apache.commons.codec.binary.Hex;
import com.tterrag.registrate.util.entry.FluidEntry;

public class ModFluids {

	public static final Registrate REGISTRATE = Hexerei.registrate();


	// TODO implement potion fluid based on this, then run genData
//	public static final FluidEntry<PotionFluid> POTION =
//			REGISTRATE.fluid("potion", PotionFluidType::new, PotionFluid::new)
//					.lang("Potion")
//					.register();
	// Better example form Ender IO since Create has their own extensions for virtual fluids
	public static final FluidEntry<PotionFluid> POTION = REGISTRATE.fluid("potion", new ResourceLocation(Hexerei.MOD_ID, "block/potion_still"),
					new ResourceLocation(Hexerei.MOD_ID, "block/potion_flow"), PotionFluidType::new, PotionFluid::new)
					.renderType(RenderType::translucent)
            .source(PotionFluid::new)
			.lang("Potion")
            .noBlock()
			.noBucket()
			.register();


//
//	public <T extends ForgeFlowingFluid> FluidBuilder<T, CreateRegistrate> virtualFluid(String name,
//																						FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
//		return entry(name,
//				c -> new VirtualFluidBuilder<>(self(), self(), name, c, Create.asResource("fluid/" + name + "_still"),
//						Create.asResource("fluid/" + name + "_flow"), typeFactory, factory));
//	}

	public static final Material BLOOD = (new Material.Builder(MaterialColor.WATER)).noCollider().nonSolid().replaceable().liquid().build();
	public static final Material TALLOW = (new Material.Builder(MaterialColor.WATER)).noCollider().nonSolid().replaceable().liquid().build();

	private static ForgeFlowingFluid.Properties getBloodProperties() {
		return new ForgeFlowingFluid.Properties(ModFluidTypes.BLOOD_FLUID_TYPE, BLOOD_FLUID, BLOOD_FLOWING)
						.block(BLOOD_BLOCK)
						.bucket(ModItems.BLOOD_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(2);
	}

	private static ForgeFlowingFluid.Properties getQuicksilverProperties() {
		return new ForgeFlowingFluid.Properties(ModFluidTypes.QUICKSILVER_FLUID_TYPE, QUICKSILVER_FLUID, QUICKSILVER_FLOWING)
						.block(QUICKSILVER_BLOCK)
						.bucket(ModItems.QUICKSILVER_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(2);
	}

	private static ForgeFlowingFluid.Properties getTallowProperties() {
		return new ForgeFlowingFluid.Properties(ModFluidTypes.TALLOW_FLUID_TYPE, TALLOW_FLUID, TALLOW_FLOWING)
						.block(TALLOW_BLOCK)
						.bucket(ModItems.TALLOW_BUCKET).slopeFindDistance(2).levelDecreasePerBlock(3);
	}

	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Hexerei.MOD_ID);


	public static final RegistryObject<Fluid> BLOOD_FLOWING = FLUIDS.register("blood_flowing", () -> new BloodFluid.Flowing(getBloodProperties()));

	public static final RegistryObject<BloodFluid.Source> BLOOD_FLUID = FLUIDS.register("blood_fluid", () -> new BloodFluid.Source(getBloodProperties()));

	public static final RegistryObject<LiquidBlock> BLOOD_BLOCK = ModBlocks.BLOCKS.register("blood", () -> new LiquidBlock(ModFluids.BLOOD_FLUID,
					BlockBehaviour.Properties.of(BLOOD).noCollission().explosionResistance(100f).noLootTable()));


	public static final RegistryObject<Fluid> QUICKSILVER_FLOWING = FLUIDS.register("quicksilver_flowing", () -> new ForgeFlowingFluid.Flowing(getQuicksilverProperties()));

	public static final RegistryObject<ForgeFlowingFluid.Source> QUICKSILVER_FLUID = FLUIDS.register("quicksilver_fluid", () -> new ForgeFlowingFluid.Source(getQuicksilverProperties()));

	public static final RegistryObject<LiquidBlock> QUICKSILVER_BLOCK = ModBlocks.BLOCKS.register("quicksilver", () -> new LiquidBlock(ModFluids.QUICKSILVER_FLUID,
					BlockBehaviour.Properties.of(Material.LAVA).noCollission().explosionResistance(100f).noLootTable()));


	public static final RegistryObject<Fluid> TALLOW_FLOWING = FLUIDS.register("tallow_flowing", () -> new TallowFluid.Flowing(getTallowProperties()));

	public static final RegistryObject<TallowFluid.Source> TALLOW_FLUID = FLUIDS.register("tallow_fluid", () -> new TallowFluid.Source(getTallowProperties()));

	public static final RegistryObject<LiquidBlock> TALLOW_BLOCK = ModBlocks.BLOCKS.register("tallow", () -> new LiquidBlock(ModFluids.TALLOW_FLUID,
					BlockBehaviour.Properties.of(TALLOW).noCollission().explosionResistance(100f).noLootTable()));


	public static void register(IEventBus eventBus) {
		FLUIDS.register(eventBus);
	}


}


