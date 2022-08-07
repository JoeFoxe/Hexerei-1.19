package net.joefoxe.hexerei;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.CrowPerchRenderer;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.ModContainers;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.data.recipes.HexereiRecipeProvider;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.event.ModLootModifiers;
import net.joefoxe.hexerei.events.CrowFluteEvent;
import net.joefoxe.hexerei.events.GlassesZoomKeyPressEvent;
import net.joefoxe.hexerei.events.SageBurningPlateEvent;
import net.joefoxe.hexerei.events.WitchArmorEvent;
import net.joefoxe.hexerei.fluid.ModFluidTypes;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.screen.*;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.*;
import net.joefoxe.hexerei.world.biome.ModBiomes;
import net.joefoxe.hexerei.world.gen.ModFeatures;
import net.joefoxe.hexerei.world.processor.DarkCovenLegProcessor;
import net.joefoxe.hexerei.world.processor.MangroveTreeLegProcessor;
import net.joefoxe.hexerei.world.processor.WitchHutLegProcessor;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Hexerei.MOD_ID)
public class Hexerei {

	public static SidedProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static final String MOD_ID = "hexerei";

	public static GlassesZoomKeyPressEvent glassesZoomKeyPressEvent;
	public static boolean entityClicked = false;

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
					.disableHtmlEscaping()
					.create();

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(HexereiConstants.CHANNEL_NAME)
					.clientAcceptedVersions(HexereiConstants.PROTOCOL_VERSION::equals)
					.serverAcceptedVersions(HexereiConstants.PROTOCOL_VERSION::equals)
					.networkProtocolVersion(HexereiConstants.PROTOCOL_VERSION::toString)
					.simpleChannel();

	public static StructureProcessorType<WitchHutLegProcessor> WITCH_HUT_LEG_PROCESSOR = () -> WitchHutLegProcessor.CODEC;
	public static StructureProcessorType<DarkCovenLegProcessor> DARK_COVEN_LEG_PROCESSOR = () -> DarkCovenLegProcessor.CODEC;
	public static StructureProcessorType<MangroveTreeLegProcessor> MANGROVE_TREE_LEG_PROCESSOR = () -> MangroveTreeLegProcessor.CODEC;

	public static LinkedList<BlockPos> sageBurningPlateTileList = new LinkedList<>();

	public Hexerei() {


		// Register the setup method for modloading
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

//        eventBus.addListener(this::gatherData);

//        eventBus.addListener(HexereiDataGenerator::gatherData);
		//eventBus.addGenericListener(RecipeSerializer.class, ModItems::registerRecipeSerializers);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HexConfig.CLIENT_CONFIG, "Hexerei-client.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HexConfig.COMMON_CONFIG, "Hexerei-common.toml");

		ModItems.register(eventBus);
		ModBlocks.register(eventBus);
		ModFluids.register(eventBus);
		ModFluidTypes.register(eventBus);
		ModTileEntities.register(eventBus);
		ModContainers.register(eventBus);
		ModRecipeTypes.register(eventBus);
		ModParticleTypes.PARTICLES.register(eventBus);
		ModFeatures.register(eventBus);
		ModStructures.DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
		ModBiomes.register(eventBus);
		ModSounds.register(eventBus);
		ModEntityTypes.register(eventBus);

		HexereiJeiCompat.init();
		ModLootModifiers.init();
		HexereiModNameTooltipCompat.init();


		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;


		eventBus.addListener(this::loadComplete);

		eventBus.addListener(this::setup);
		// Register the enqueueIMC method for modloading
		eventBus.addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		eventBus.addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		eventBus.addListener(this::doClientStuff);

//        forgeEventBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
//        forgeEventBus.addListener(EventPriority.NORMAL, WitchHutStructure::setupStructureSpawns);


		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();

		gen.addProvider(event.includeServer(), new HexereiRecipeProvider(gen));
	}


	@OnlyIn(Dist.CLIENT)
	public void setupCrowPerchRenderer() {
		MinecraftForge.EVENT_BUS.register(CrowPerchRenderer.class);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// some preinit code

		event.enqueueWork(() -> {

			AxeItem.STRIPPABLES = new ImmutableMap.Builder<Block, Block>().putAll(AxeItem.STRIPPABLES)
							.put(ModBlocks.MAHOGANY_LOG.get(), ModBlocks.STRIPPED_MAHOGANY_LOG.get())
							.put(ModBlocks.MAHOGANY_WOOD.get(), ModBlocks.STRIPPED_MAHOGANY_WOOD.get())
							.put(ModBlocks.WILLOW_LOG.get(), ModBlocks.STRIPPED_WILLOW_LOG.get())
							.put(ModBlocks.WILLOW_WOOD.get(), ModBlocks.STRIPPED_WILLOW_WOOD.get()).build();
//            ModStructures.setupStructures();
//            ModConfiguredStructures.registerConfiguredStructures();

			Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(MOD_ID, "witch_hut_leg_processor"), WITCH_HUT_LEG_PROCESSOR);
			Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(MOD_ID, "dark_coven_leg_processor"), DARK_COVEN_LEG_PROCESSOR);
			Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(MOD_ID, "mangrove_tree_leg_processor"), MANGROVE_TREE_LEG_PROCESSOR);

			HexereiPacketHandler.register();

			SpawnPlacements.register(ModEntityTypes.CROW.get(), SpawnPlacements.Type.ON_GROUND,
							Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MANDRAKE_FLOWER.getId(), ModBlocks.POTTED_MANDRAKE_FLOWER);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BELLADONNA_FLOWER.getId(), ModBlocks.POTTED_BELLADONNA_FLOWER);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.YELLOW_DOCK_BUSH.getId(), ModBlocks.POTTED_YELLOW_DOCK_BUSH);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MUGWORT_BUSH.getId(), ModBlocks.POTTED_MUGWORT_BUSH);

			ComposterBlock.COMPOSTABLES.put(ModBlocks.WILLOW_VINES.get().asItem(), 0.5F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.WILLOW_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.MAHOGANY_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.WILLOW_SAPLING.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.MAHOGANY_SAPLING.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.MANDRAKE_FLOWER.get().asItem(), 1F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.BELLADONNA_FLOWER.get().asItem(), 1F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.MUGWORT_BUSH.get().asItem(), 1F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.YELLOW_DOCK_BUSH.get().asItem(), 1F);
			ComposterBlock.COMPOSTABLES.put(ModBlocks.LILY_PAD_BLOCK.get().asItem(), 1F);
			ComposterBlock.COMPOSTABLES.put(ModItems.BELLADONNA_BERRIES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.BELLADONNA_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.MANDRAKE_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.MANDRAKE_ROOT.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.MUGWORT_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.MUGWORT_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_DOCK_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_DOCK_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_BELLADONNA_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_MANDRAKE_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_MUGWORT_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_MUGWORT_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_YELLOW_DOCK_FLOWERS.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_YELLOW_DOCK_LEAVES.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.SAGE.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.SAGE_SEED.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.DRIED_SAGE.get().asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ModItems.TALLOW_IMPURITY.get().asItem(), 0.3F);

		});
	}


	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client

		setupCrowPerchRenderer();
		event.enqueueWork(() -> {


			ItemBlockRenderTypes.setRenderLayer(ModFluids.QUICKSILVER_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.QUICKSILVER_FLOWING.get(), RenderType.translucent());

			ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD_FLOWING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.TALLOW_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.TALLOW_FLOWING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAHOGANY_DOOR.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAHOGANY_TRAPDOOR.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAHOGANY_TRAPDOOR.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.WILLOW_DOOR.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.WILLOW_TRAPDOOR.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MIXING_CAULDRON.get(), RenderType.cutoutMipped());

			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_BALL.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_BALL_ORB.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_BALL_LARGE_RING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_BALL_SMALL_RING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.HERB_DRYING_RACK_FULL.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.HERB_DRYING_RACK.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAHOGANY_SAPLING.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.WILLOW_SAPLING.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MANDRAKE_FLOWER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_MANDRAKE_FLOWER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.BELLADONNA_FLOWER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_BELLADONNA_FLOWER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUGWORT_BUSH.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_MUGWORT_BUSH.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.YELLOW_DOCK_BUSH.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_YELLOW_DOCK_BUSH.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDELABRA.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.SAGE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.LILY_PAD_BLOCK.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.WILLOW_VINES.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.WILLOW_VINES_PLANT.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.COFFER.get(), RenderType.cutout());

			ItemBlockRenderTypes.setRenderLayer(ModBlocks.SELENITE_BLOCK.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.SELENITE_CLUSTER.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.BUDDING_SELENITE.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.LARGE_SELENITE_BUD.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.MEDIUM_SELENITE_BUD.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.SMALL_SELENITE_BUD.get(), RenderType.translucent());

			MenuScreens.register(ModContainers.MIXING_CAULDRON_CONTAINER.get(), MixingCauldronScreen::new);
			MenuScreens.register(ModContainers.COFFER_CONTAINER.get(), CofferScreen::new);
			MenuScreens.register(ModContainers.HERB_JAR_CONTAINER.get(), HerbJarScreen::new);
			MenuScreens.register(ModContainers.BROOM_CONTAINER.get(), BroomScreen::new);
			MenuScreens.register(ModContainers.CROW_CONTAINER.get(), CrowScreen::new);
			MenuScreens.register(ModContainers.CROW_FLUTE_CONTAINER.get(), CrowFluteScreen::new);


		});


	}

	static float clientTicks = 0;
	static float clientTicksPartial = 0;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderLast(RenderLevelStageEvent event) {
		clientTicksPartial = event.getPartialTick();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void clientTickEvent(TickEvent.ClientTickEvent event) {
		if (event.type == TickEvent.Type.CLIENT)
			clientTicks += 1;
	}


	public static float getClientTicks() {
		return clientTicks + clientTicksPartial;
	}

	public static float getClientTicksWithoutPartial() {
		return clientTicks;
	}

	public static float getPartial() {
		return clientTicksPartial;
	}

//    @SubscribeEvent
//    public static void recipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
//        register(new Serializer2(), "coffer_dyeing", event.getRegistry());
//    }
//
//    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
//        registry.register(obj.setRegistryName(new ResourceLocation(MOD_ID, name)));
//    }

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		InterModComms.sendTo("hexerei", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event) {

	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(new SageBurningPlateEvent());
		MinecraftForge.EVENT_BUS.register(new WitchArmorEvent());
		MinecraftForge.EVENT_BUS.register(new CrowFluteEvent());

		MinecraftForge.EVENT_BUS.register(new PageDrawing());
		glassesZoomKeyPressEvent = new GlassesZoomKeyPressEvent();
		MinecraftForge.EVENT_BUS.register(glassesZoomKeyPressEvent);

	}


}
