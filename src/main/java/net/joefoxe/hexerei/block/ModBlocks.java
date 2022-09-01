package net.joefoxe.hexerei.block;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.*;
import net.joefoxe.hexerei.block.custom.trees.MahoganyTree;
import net.joefoxe.hexerei.block.custom.trees.WillowTree;
import net.joefoxe.hexerei.item.ModItemGroup;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {


	public static final DeferredRegister<Block> BLOCKS
					= DeferredRegister.create(ForgeRegistries.BLOCKS, Hexerei.MOD_ID);

	public static final DeferredRegister<Item> ITEMS = ModItems.ITEMS;

//    public static final RegistryObject<Block> SCRAP_BLOCK = registerBlock("scrap_block",
//            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2).setRequiresTool().explosionResistance(5f)));

	public static final RegistryObject<MixingCauldron> MIXING_CAULDRON = registerBlockNoItem("mixing_cauldron",
					() -> new MixingCauldron(BlockBehaviour.Properties.of(Material.METAL).explosionResistance(4f).requiresCorrectToolForDrops().strength(3).lightLevel(state -> 12)));

	public static final RegistryObject<CandleDipper> CANDLE_DIPPER = registerBlock("candle_dipper",
					() -> new CandleDipper(BlockBehaviour.Properties.of(Material.METAL).noCollission().noOcclusion().strength(3).requiresCorrectToolForDrops().explosionResistance(8f)));

	public static final RegistryObject<Coffer> COFFER = registerBlockNoItem("coffer",
					() -> new Coffer(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).requiresCorrectToolForDrops().explosionResistance(8f)));

	public static final RegistryObject<Altar> BOOK_OF_SHADOWS_ALTAR = registerBlock("book_of_shadows_altar",
			() -> new Altar(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<ConnectingTable> WILLOW_ALTAR = registerBlock("willow_altar",
			() -> new ConnectingTable(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

//    public static final RegistryObject<Block> SAGE_BUNDLE_PLATE = registerBlock("sage_bundle_plate",
//            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<SageBlock> SAGE = BLOCKS.register("sage_crop",
					() -> new SageBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

	public static final RegistryObject<PestleAndMortar> PESTLE_AND_MORTAR = registerBlock("pestle_and_mortar",
					() -> new PestleAndMortar(BlockBehaviour.Properties.of(Material.STONE).strength(2).explosionResistance(2f)));

	public static final RegistryObject<SageBurningPlate> SAGE_BURNING_PLATE = registerBlock("sage_burning_plate",
					() -> new SageBurningPlate(BlockBehaviour.Properties.of(Material.METAL).strength(1).explosionResistance(2f)));

	public static final RegistryObject<CrystalBall> CRYSTAL_BALL = registerBlock("crystal_ball",
					() -> new CrystalBall(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(2f).lightLevel(state -> 9)));

	public static final RegistryObject<HerbJar> HERB_JAR = registerBlockNoItem("herb_jar",
					() -> new HerbJar(BlockBehaviour.Properties.of(Material.GLASS).strength(1).explosionResistance(0.5f).strength(1f, 1f).sound(SoundType.GLASS)));

	public static final RegistryObject<HerbDryingRackFull> HERB_DRYING_RACK_FULL = registerBlock("herb_drying_rack_full",
					() -> new HerbDryingRackFull(BlockBehaviour.Properties.of(Material.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<HerbDryingRack> HERB_DRYING_RACK = registerBlock("herb_drying_rack",
					() -> new HerbDryingRack(BlockBehaviour.Properties.of(Material.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<Candelabra> CANDELABRA = registerBlock("candelabra",
					() -> new Candelabra(BlockBehaviour.Properties.of(Material.METAL).strength(1).explosionResistance(2f).lightLevel(state -> state.getValue(Candelabra.LIT) ? 15 : 0)));

	public static final RegistryObject<Candle> CANDLE = registerBlockNoItem("candle",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_BLUE = registerBlockNoItem("candle_blue",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_BLACK = registerBlockNoItem("candle_black",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_LIME = registerBlockNoItem("candle_lime",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_ORANGE = registerBlockNoItem("candle_orange",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_PINK = registerBlockNoItem("candle_pink",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_PURPLE = registerBlockNoItem("candle_purple",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_RED = registerBlockNoItem("candle_red",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_CYAN = registerBlockNoItem("candle_cyan",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

	public static final RegistryObject<Candle> CANDLE_YELLOW = registerBlockNoItem("candle_yellow",
					() -> new Candle(BlockBehaviour.Properties.of(Material.VEGETABLE).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));


	public static final RegistryObject<MahoganyLog> MAHOGANY_LOG = registerBlock("mahogany_log",
					() -> new MahoganyLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

	public static final RegistryObject<MahoganyWood> MAHOGANY_WOOD = registerBlock("mahogany_wood",
					() -> new MahoganyWood(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_MAHOGANY_LOG = registerBlock("stripped_mahogany_log",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_MAHOGANY_WOOD = registerBlock("stripped_mahogany_wood",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));

	public static final RegistryObject<Block> MAHOGANY_PLANKS = registerBlock("mahogany_planks",
					() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> MAHOGANY_STAIRS = registerBlock("mahogany_stairs",
					() -> new StairBlock(() -> MAHOGANY_PLANKS.get().defaultBlockState(),
									BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceBlock> MAHOGANY_FENCE = registerBlock("mahogany_fence",
					() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> MAHOGANY_FENCE_GATE = registerBlock("mahogany_fence_gate",
					() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<SlabBlock> MAHOGANY_SLAB = registerBlock("mahogany_slab",
					() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<WoodButtonBlock> MAHOGANY_BUTTON = registerBlock("mahogany_button",
					() -> new WoodButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission()));

	public static final RegistryObject<PressurePlateBlock> MAHOGANY_PRESSURE_PLATE = registerBlock("mahogany_pressure_plate",
					() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission()));

	public static final RegistryObject<DoorBlock> MAHOGANY_DOOR = registerBlock("mahogany_door",
					() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));

	public static final RegistryObject<TrapDoorBlock> MAHOGANY_TRAPDOOR = registerBlock("mahogany_trapdoor",
					() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));

	public static final RegistryObject<LeavesBlock> MAHOGANY_LEAVES = registerBlock("mahogany_leaves",
					() -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion().isSuffocating(Properties::never).isViewBlocking(Properties::never)));

	public static final RegistryObject<SaplingBlock> MAHOGANY_SAPLING = registerBlock("mahogany_sapling",
					() -> new SaplingBlock(new MahoganyTree(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));


	public static final RegistryObject<WillowLog> WILLOW_LOG = registerBlock("willow_log",
					() -> new WillowLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

	public static final RegistryObject<WillowWood> WILLOW_WOOD = registerBlock("willow_wood",
					() -> new WillowWood(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));

	public static final RegistryObject<Block> WILLOW_PLANKS = registerBlock("willow_planks",
					() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> WILLOW_STAIRS = registerBlock("willow_stairs",
					() -> new StairBlock(() -> WILLOW_PLANKS.get().defaultBlockState(),
									BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<Block> WILLOW_VINES = registerBlock("willow_vines",
					() -> new WillowVinesBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));

	public static final RegistryObject<Block> WILLOW_VINES_PLANT = registerBlockNoItem("willow_vines_plant",
					() -> new WillowVinesPlantBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));

	public static final RegistryObject<FenceBlock> WILLOW_FENCE = registerBlock("willow_fence",
					() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> WILLOW_FENCE_GATE = registerBlock("willow_fence_gate",
					() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<SlabBlock> WILLOW_SLAB = registerBlock("willow_slab",
					() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<WoodButtonBlock> WILLOW_BUTTON = registerBlock("willow_button",
					() -> new WoodButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission()));

	public static final RegistryObject<PressurePlateBlock> WILLOW_PRESSURE_PLATE = registerBlock("willow_pressure_plate",
					() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission()));

	public static final RegistryObject<DoorBlock> WILLOW_DOOR = registerBlock("willow_door",
					() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));

	public static final RegistryObject<TrapDoorBlock> WILLOW_TRAPDOOR = registerBlock("willow_trapdoor",
					() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));

	public static final RegistryObject<LeavesBlock> WILLOW_LEAVES = registerBlock("willow_leaves",
					() -> new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion().isSuffocating(Properties::never).isViewBlocking(Properties::never)));

	public static final RegistryObject<SaplingBlock> WILLOW_SAPLING = registerBlock("willow_sapling",
					() -> new SaplingBlock(new WillowTree(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

	public static final RegistryObject<FloweringLilyPadBlock> LILY_PAD_BLOCK = registerBlockNoItem("flowering_lily_pad",
					() -> new FloweringLilyPadBlock(BlockBehaviour.Properties.of(Material.VEGETABLE).instabreak().sound(SoundType.LILY_PAD).noOcclusion()));

	public static final RegistryObject<PickableFlower> MANDRAKE_FLOWER = registerBlock("mandrake_flower",
					() -> new PickableFlower(MobEffects.REGENERATION, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), ModItems.MANDRAKE_FLOWERS, 2, ModItems.MANDRAKE_ROOT, 1) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<PickableFlower> BELLADONNA_FLOWER = registerBlock("belladonna_flower",
					() -> new PickableFlower(MobEffects.POISON, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), ModItems.BELLADONNA_FLOWERS, 2, ModItems.BELLADONNA_BERRIES, 6) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

//            () -> new PickableFlower(MobEffects.POISON, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), new ItemStack(ModItems.BELLADONNA_FLOWERS.get(), 4),  new ItemStack(ModItems.BELLADONNA_BERRIES.get(), 5), 5));

	public static final RegistryObject<PickableDoubleFlower> MUGWORT_BUSH = registerBlock("mugwort_bush",
					() -> new PickableDoubleFlower(MobEffects.GLOWING, 10, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.AZALEA), ModItems.MUGWORT_LEAVES, 4, ModItems.MUGWORT_FLOWERS, 3) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<PickableDoubleFlower> YELLOW_DOCK_BUSH = registerBlock("yellow_dock_bush",
					() -> new PickableDoubleFlower(MobEffects.GLOWING, 10, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.AZALEA), ModItems.YELLOW_DOCK_LEAVES, 4, ModItems.YELLOW_DOCK_FLOWERS, 3) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<Block> POTTED_MANDRAKE_FLOWER = registerBlockNoItem("potted_mandrake_flower",
					() -> new FlowerPotBlock(ModBlocks.MANDRAKE_FLOWER.get(), BlockBehaviour.Properties.of(Material.DECORATION).strength(2.0F).noOcclusion().instabreak()));

	public static final RegistryObject<Block> POTTED_BELLADONNA_FLOWER = registerBlockNoItem("potted_belladonna_flower",
					() -> new FlowerPotBlock(ModBlocks.BELLADONNA_FLOWER.get(), BlockBehaviour.Properties.of(Material.DECORATION).strength(2.0F).noOcclusion().instabreak()));

	public static final RegistryObject<Block> POTTED_YELLOW_DOCK_BUSH = registerBlockNoItem("potted_yellow_dock_bush",
					() -> new FlowerPotBlock(ModBlocks.YELLOW_DOCK_BUSH.get(), BlockBehaviour.Properties.of(Material.DECORATION).strength(2.0F).noOcclusion().instabreak()));

	public static final RegistryObject<Block> POTTED_MUGWORT_BUSH = registerBlockNoItem("potted_mugwort_bush",
					() -> new FlowerPotBlock(ModBlocks.MUGWORT_BUSH.get(), BlockBehaviour.Properties.of(Material.DECORATION).strength(2.0F).noOcclusion().instabreak()));


	public static final RegistryObject<AmethystBlock> SELENITE_BLOCK = registerBlock("selenite_block",
					() -> new AmethystBlock(BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.TERRACOTTA_WHITE).strength(0.5F).sound(SoundType.AMETHYST).noOcclusion()));

	public static final RegistryObject<AmethystBlock> BUDDING_SELENITE = registerBlock("budding_selenite",
					() -> new BuddingSelenite(BlockBehaviour.Properties.of(Material.AMETHYST).randomTicks().strength(1.0F).sound(SoundType.AMETHYST).noOcclusion()));

	public static final RegistryObject<AmethystBlock> SELENITE_CLUSTER = registerBlock("selenite_cluster",
					() -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().strength(0.5F).randomTicks().sound(SoundType.AMETHYST_CLUSTER).noOcclusion().lightLevel((p_152632_) -> {
						return 5;
					})));

	public static final RegistryObject<AmethystBlock> LARGE_SELENITE_BUD = registerBlock("large_selenite_bud",
					() -> new AmethystClusterBlock(5, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.MEDIUM_AMETHYST_BUD).noOcclusion().lightLevel((p_152629_) -> {
						return 4;
					})));

	public static final RegistryObject<AmethystBlock> MEDIUM_SELENITE_BUD = registerBlock("medium_selenite_bud",
					() -> new AmethystClusterBlock(4, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.LARGE_AMETHYST_BUD).noOcclusion().lightLevel((p_152617_) -> {
						return 2;
					})));

	public static final RegistryObject<AmethystBlock> SMALL_SELENITE_BUD = registerBlock("small_selenite_bud",
					() -> new AmethystClusterBlock(3, 4, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.SMALL_AMETHYST_BUD).noOcclusion().lightLevel((p_187409_) -> {
						return 1;
					})));

	// SIGILS
	public static final RegistryObject<Block> BLOOD_SIGIL = registerBlockNoItem("blood_sigil",
					() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2).requiresCorrectToolForDrops().explosionResistance(5f)));

	// BLOCK PARTIALS

	public static final RegistryObject<Block> CANDLE_DIPPER_WICK_BASE = registerBlockNoItem("candle_dipper_wick_base",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_WICK = registerBlockNoItem("candle_dipper_wick",
					() -> new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_1 = registerBlockNoItem("candle_dipper_candle_1",
					() -> new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_2 = registerBlockNoItem("candle_dipper_candle_2",
					() -> new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_3 = registerBlockNoItem("candle_dipper_candle_3",
					() -> new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0).noCollission().explosionResistance(1f)));
	public static final RegistryObject<Block> CRYSTAL_BALL_ORB = registerBlockNoItem("crystal_ball_orb",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_LARGE_RING = registerBlockNoItem("crystal_ball_large_ring",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_SMALL_RING = registerBlockNoItem("crystal_ball_small_ring",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_STAND = registerBlockNoItem("crystal_ball_stand",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_COVER = registerBlockNoItem("book_of_shadows_cover",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_COVER_CORNERS = registerBlockNoItem("book_of_shadows_cover_corners",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BACK = registerBlockNoItem("book_of_shadows_back",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BACK_CORNERS = registerBlockNoItem("book_of_shadows_back_corners",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BINDING = registerBlockNoItem("book_of_shadows_binding",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_PAGE = registerBlockNoItem("book_of_shadows_page_blank",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> COFFER_CHEST = registerBlockNoItem("coffer_chest",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(3).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> COFFER_LID = registerBlockNoItem("coffer_lid",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(3).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> COFFER_CONTAINER = registerBlockNoItem("coffer_container",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> COFFER_HINGE = registerBlockNoItem("coffer_hinge",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_GENERIC = registerBlockNoItem("herb_jar_generic",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_BELLADONNA = registerBlockNoItem("herb_jar_belladonna",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MANDRAKE_FLOWER = registerBlockNoItem("herb_jar_mandrake_flower",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MANDRAKE_ROOT = registerBlockNoItem("herb_jar_mandrake_root",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MUGWORT = registerBlockNoItem("herb_jar_mugwort",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_YELLOW_DOCK = registerBlockNoItem("herb_jar_yellow_dock",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_BROWN_MUSHROOM_1 = registerBlockNoItem("herb_drying_rack_brown_mushroom_1",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_BROWN_MUSHROOM_2 = registerBlockNoItem("herb_drying_rack_brown_mushroom_2",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_RED_MUSHROOM_1 = registerBlockNoItem("herb_drying_rack_red_mushroom_1",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_RED_MUSHROOM_2 = registerBlockNoItem("herb_drying_rack_red_mushroom_2",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> PESTLE_AND_MORTAR_PESTLE = registerBlockNoItem("pestle_and_mortar_pestle",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_5 = registerBlockNoItem("dried_sage_bundle_plate_5",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_4 = registerBlockNoItem("dried_sage_bundle_plate_4",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_3 = registerBlockNoItem("dried_sage_bundle_plate_3",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_2 = registerBlockNoItem("dried_sage_bundle_plate_2",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_1 = registerBlockNoItem("dried_sage_bundle_plate_1",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_5_LIT = registerBlockNoItem("dried_sage_bundle_plate_5_lit",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_4_LIT = registerBlockNoItem("dried_sage_bundle_plate_4_lit",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_3_LIT = registerBlockNoItem("dried_sage_bundle_plate_3_lit",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_2_LIT = registerBlockNoItem("dried_sage_bundle_plate_2_lit",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_1_LIT = registerBlockNoItem("dried_sage_bundle_plate_1_lit",
					() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2).explosionResistance(8f)));


	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<T> registerBlockNoItem(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		return toReturn;
	}

	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
						new BlockItem.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}

	public static class Properties {

		public static boolean always(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
			return true;
		}

		public static boolean hasPostProcess(BlockState state, BlockGetter reader, BlockPos pos) {
			return true;
		}

		public static boolean never(BlockState state, BlockGetter reader, BlockPos pos) {
			return false;
		}
	}


//    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
//        return register(name, sup, block -> item(block));
//    }
//
//    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
//        RegistryObject<T> ret = registerNoItem(name, sup);
//        ITEMS.register(name, itemCreator.apply(ret));
//        return ret;
//    }
//
//    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
//        return BLOCKS.register(name, sup);
//    }
//
//    private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
//        return () -> new BlockItem(block.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP));
//    }


}
