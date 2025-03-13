package net.joefoxe.hexerei;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.joefoxe.hexerei.block.CustomFlintAndSteelDispenserBehavior;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.ModWoodType;
import net.joefoxe.hexerei.client.renderer.CrowPerchRenderer;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.ModContainers;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.data.books.PaintSystemSavedData;
import net.joefoxe.hexerei.data.datagen.ModRecipeProvider;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipes;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.event.ModLootModifiers;
import net.joefoxe.hexerei.events.GlassesZoomKeyPressEvent;
import net.joefoxe.hexerei.events.SageBurningPlateEvent;
import net.joefoxe.hexerei.events.WitchArmorEvent;
import net.joefoxe.hexerei.fluid.ModFluidTypes;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.item.ModArmorMaterial;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItemGroup;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.light.LightManager;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiSupporterBenefits;
import net.joefoxe.hexerei.util.ServerProxy;
import net.joefoxe.hexerei.util.SidedProxy;
import net.joefoxe.hexerei.world.biomemods.ModBiomeModifiers;
import net.joefoxe.hexerei.world.gen.ModFeatures;
import net.joefoxe.hexerei.world.processor.ModStructureProcessors;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.joefoxe.hexerei.world.terrablender.ModRegion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

//import static net.joefoxe.hexerei.util.ClientProxy.MODEL_SWAPPER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Hexerei.MOD_ID)
public class Hexerei {

	public static final String MOD_ID = "hexerei";
	public static boolean curiosLoaded = false;


	public static SidedProxy proxy;

	public static boolean entityClicked = false;

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();


	public static LinkedList<BlockPos> sageBurningPlateTileList = new LinkedList<>();

	public static class DynamicRegistries {
		public static RegistryAccess get() {
			return EffectiveSide.get().isClient() ? ClientDynamicRegistries.get() : ServerLifecycleHooks.getCurrentServer().registryAccess();
		}

		private static class ClientDynamicRegistries {
			public static RegistryAccess get() { return Minecraft.getInstance().hasSingleplayerServer() ? Minecraft.getInstance().getSingleplayerServer().registryAccess() : Minecraft.getInstance().level.registryAccess(); }
		}
	}

	public Hexerei(IEventBus eventBus, ModContainer modContainer, Dist dist){

		proxy = (FMLEnvironment.dist.isClient() ? new ClientProxy() : new ServerProxy());

//        eventBus.addListener(this::gatherData);

//        eventBus.addListener(HexereiDataGenerator::gatherData);
		//eventBus.addGenericListener(RecipeSerializer.class, ModItems::registerRecipeSerializers);
		modContainer.registerConfig(ModConfig.Type.CLIENT, HexConfig.CLIENT_CONFIG, "Hexerei-client.toml");
		modContainer.registerConfig(ModConfig.Type.COMMON, HexConfig.COMMON_CONFIG, "Hexerei-common.toml");

		if (dist.isClient()) {
			NeoForge.EVENT_BUS.addListener(ClientEvents::clientTickEvent);
			NeoForge.EVENT_BUS.addListener(ClientEvents::renderWorldLastEvent);
			eventBus.addListener(ClientEvents::registerMenu);
			eventBus.addListener(ClientEvents::onRegisterShaders);
			eventBus.addListener(ClientEvents::onRegisterClientExtensions);
			eventBus.addListener(ClientEvents::onRegisterAdditionalModels);
			ClientProxy.MODEL_SWAPPER.registerListeners(eventBus);
		}

		ModDataComponents.COMPONENTS.register(eventBus);
		ModArmorMaterial.MATERIALS.register(eventBus);
		ModItems.register(eventBus);
		ModBlocks.register(eventBus);
		ModFluids.register(eventBus);
		ModFluidTypes.register(eventBus);
		ModTileEntities.register(eventBus);
		ModContainers.register(eventBus);
		ModRecipeTypes.register(eventBus);
		ModParticleTypes.PARTICLES.register(eventBus);
		ModFeatures.register(eventBus);
		ModStructureProcessors.DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register(eventBus);
		ModStructures.DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
		ModSounds.register(eventBus);
		ModEntityTypes.register(eventBus);
		ModBiomeModifiers.register(eventBus);
		HexereiJeiCompat.init();
		ModLootModifiers.init(eventBus);
		HexereiModNameTooltipCompat.init();

		try {
			Thread thread = new Thread(HexereiSupporterBenefits::init);
			thread.setDaemon(true);
			thread.setName("supporter-lookup");
			thread.start();
		} catch(Exception err) {
			err.printStackTrace();
		}


		eventBus.addListener(this::loadComplete);

		eventBus.addListener(this::setup);
		eventBus.addListener(this::registerSpawnPlacementsEvent);
		// Register the enqueueIMC method for modloading
//		eventBus.addListener(this::enqueueIMC);
		// Register the doClientStuff method for modloading
		eventBus.addListener(this::doClientStuff);

		ModItemGroup.ITEM_GROUP.register(eventBus);

		NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::playerLogin);
		eventBus.addListener(EventPriority.LOWEST, this::gatherData);

		curiosLoaded = ModList.get().isLoaded("curios");
	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		gen.addProvider(true, new ModRecipeProvider(output, lookupProvider));
//		gen.addProvider(event.includeServer(), new WorldGenProvider(output, event.getLookupProvider()));
//		gen.addProvider(event.includeServer(), new ModBiomeTagsProvider(output, lookupProvider, event.getExistingFileHelper()));
//		gen.addProvider(event.includeServer(), new HexereiRecipeProvider(gen));
	}



	public void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
		ServerPlayer player = (ServerPlayer) event.getEntity();
		OwlCourierDepotSavedData.get().syncToClient(player);
		BookManager.sendBookPagesToClient(player);
		BookManager.sendBookEntriesToClient(player);
		PaintSystemSavedData.sendToClients();
		WoodcutterRecipes.sendToClient(player);
	}
	public void setupCrowPerchRenderer() {
		NeoForge.EVENT_BUS.register(CrowPerchRenderer.class);
	}

	private void registerSpawnPlacementsEvent(RegisterSpawnPlacementsEvent event) {
		event.register(ModEntityTypes.CROW.get(), SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
		event.register(ModEntityTypes.OWL.get(), SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// some preinit code

		event.enqueueWork(() -> {
			DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new CustomFlintAndSteelDispenserBehavior(DispenserBlock.DISPENSER_REGISTRY.get(Items.FLINT_AND_STEEL)));

			AxeItem.STRIPPABLES = new ImmutableMap.Builder<Block, Block>().putAll(AxeItem.STRIPPABLES)
					.put(ModBlocks.MAHOGANY_LOG.get(), ModBlocks.STRIPPED_MAHOGANY_LOG.get())
					.put(ModBlocks.MAHOGANY_WOOD.get(), ModBlocks.STRIPPED_MAHOGANY_WOOD.get())
					.put(ModBlocks.WILLOW_LOG.get(), ModBlocks.STRIPPED_WILLOW_LOG.get())
					.put(ModBlocks.WILLOW_WOOD.get(), ModBlocks.STRIPPED_WILLOW_WOOD.get())
					.put(ModBlocks.WITCH_HAZEL_LOG.get(), ModBlocks.STRIPPED_WITCH_HAZEL_LOG.get())
					.put(ModBlocks.WITCH_HAZEL_WOOD.get(), ModBlocks.STRIPPED_WITCH_HAZEL_WOOD.get()).build();
//            ModStructures.setupStructures();
//            ModConfiguredStructures.registerConfiguredStructures();
			WoodType.register(ModWoodType.MAHOGANY);
			WoodType.register(ModWoodType.WILLOW);
			WoodType.register(ModWoodType.WITCH_HAZEL);
			WoodType.register(ModWoodType.POLISHED_MAHOGANY);
			WoodType.register(ModWoodType.POLISHED_WILLOW);
			WoodType.register(ModWoodType.POLISHED_WITCH_HAZEL);

			BroomType.create("mahogany", ModItems.MAHOGANY_BROOM.get(), 0.8f);
			BroomType.create("willow", ModItems.WILLOW_BROOM.get(), 0.4f);
			BroomType.create("witch_hazel", ModItems.WITCH_HAZEL_BROOM.get(), 0.6f);


			LightManager.init();

			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MANDRAKE_PLANT.getId(), ModBlocks.POTTED_MANDRAKE_PLANT);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BELLADONNA_PLANT.getId(), ModBlocks.POTTED_BELLADONNA_PLANT);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.YELLOW_DOCK_BUSH.getId(), ModBlocks.POTTED_YELLOW_DOCK_BUSH);
			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MUGWORT_BUSH.getId(), ModBlocks.POTTED_MUGWORT_BUSH);
		});
		if (ModList.get().isLoaded("terrablender") && HexConfig.WILLOW_SWAMP_RARITY.get() > 0) {
			event.enqueueWork(ModRegion::init);
		}
	}
	public static List<String> buildResourceLocations(String resourcePath) throws IOException {
		ResourceLocation loc = ResourceLocation.parse(resourcePath);
		String resourceDirectory = loc.getPath();
		Path basePath = Paths.get("src/main/resources/assets", loc.getNamespace(), resourceDirectory).getParent();
		try (Stream<Path> paths = Files.walk(basePath)) {
			return paths .filter(Files::isRegularFile) .filter(path -> {
				String fileName = path.getFileName().toString().toLowerCase();
				return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
			}) .map(path -> {
				String relativeImagePath = basePath.relativize(path).toString().replace("\\", "/"); return loc.getNamespace() + ":textures/" + relativeImagePath;
			}).toList();
		}
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client

		setupCrowPerchRenderer();
		event.enqueueWork(() -> {

            PageDrawing.pageTextureLocs = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources("textures/book/pages", p_345740_ -> p_345740_.getPath().endsWith(".png")).keySet().stream().filter((loc) -> loc.getNamespace().equals(MOD_ID)).toList());
            PageDrawing.overlayTextureLocs = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources("textures/book/page_overlays", p_345740_ -> p_345740_.getPath().endsWith(".png")).keySet().stream().filter((loc) -> loc.getNamespace().equals(MOD_ID)).toList());
			PageDrawing.overlayTextureLocs.add(null);
			PageDrawing.overlayTextureLocs.add(null);
			PageDrawing.overlayTextureLocs.add(null);
			PageDrawing.overlayTextureLocs.add(null);
			PageDrawing.overlayTextureLocs.add(null);
			PageDrawing.overlayTextureLocs.add(null);
//                List<String> resourceLocations = buildResourceLocations("hexerei:textures/book/pages/page_1.png");

            Sheets.addWoodType(ModWoodType.MAHOGANY);
			Sheets.addWoodType(ModWoodType.WILLOW);
			Sheets.addWoodType(ModWoodType.WITCH_HAZEL);
			Sheets.addWoodType(ModWoodType.POLISHED_MAHOGANY);
			Sheets.addWoodType(ModWoodType.POLISHED_WILLOW);
			Sheets.addWoodType(ModWoodType.POLISHED_WITCH_HAZEL);

			ItemBlockRenderTypes.setRenderLayer(ModFluids.QUICKSILVER_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.QUICKSILVER_FLOWING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD_FLOWING.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.TALLOW_FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(ModFluids.TALLOW_FLOWING.get(), RenderType.translucent());

		});

//		if (curiosLoaded) GlassesCurioRender.register();

	}

//    @SubscribeEvent
//    public static void recipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
//        register(new Serializer2(), "coffer_dyeing", event.getRegistry());
//    }
//
//    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
//        registry.register(obj.setRegistryName(HexereiUtil.getResource(name)));
//    }

//	private void enqueueIMC(final InterModEnqueueEvent event) {
//		if (curiosLoaded) CurioCompat.sendIMC();
//	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(new SageBurningPlateEvent());
		NeoForge.EVENT_BUS.register(new WitchArmorEvent());


		if(FMLEnvironment.dist.isClient()) {
			NeoForge.EVENT_BUS.register(new GlassesZoomKeyPressEvent());
//			NeoForge.EVENT_BUS.register(new PageDrawing());
			if (ModList.get().isLoaded("ars_nouveau")) net.joefoxe.hexerei.compat.LightManagerCompat.fallbackToArs();
		}

	}


}
