package net.joefoxe.hexerei.util;

import com.google.common.collect.Maps;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.ModelSwapper;
import net.joefoxe.hexerei.block.connected.StitchedSprite;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.model.*;
import net.joefoxe.hexerei.client.renderer.entity.render.*;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModItemProperties;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.screen.tooltip.ClientBroomToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientCofferToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientHerbJarToolTip;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.renderer.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static net.joefoxe.hexerei.block.connected.StitchedSprite.ALL;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Hexerei.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientProxy implements SidedProxy {
    public static KeyMapping[] keys = null;

    public static final ModelLayerLocation CANDLE_HERB_LAYER = new ModelLayerLocation(HexereiUtil.getResource("candle_herb_layer"), "main");
    public static final ModelLayerLocation WITCH_ARMOR_LAYER = new ModelLayerLocation(HexereiUtil.getResource("witch_armor"), "main");
    public static final ModelLayerLocation MUSHROOM_WITCH_ARMOR_LAYER = new ModelLayerLocation(HexereiUtil.getResource("mushroom_witch_armor"), "main");

    public static final ModelLayerLocation READING_GLASSES_LAYER = new ModelLayerLocation(HexereiUtil.getResource("reading_glasses"), "main");

    public static final BlockConnectivity BLOCK_CONNECTIVITY = new BlockConnectivity();
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();



    public static Map<String, Font> fontList = new HashMap<>();
    public static int fontIndex = 0;


    public static Font font() {
        if (ClientProxy.fontIndex == 0)
            return Minecraft.getInstance().font;
        else {
            int index = ClientProxy.fontIndex % HexConfig.FONT_LIST.get().size();
            Font toReturn = ClientProxy.fontList.get(HexConfig.FONT_LIST.get().get(index));
            return toReturn == null ? Minecraft.getInstance().font : toReturn;
        }
    }

    public static ResourceLocation fontId() {
        if (ClientProxy.fontIndex == 0)
            return null;
        else {
            int index = ClientProxy.fontIndex % HexConfig.FONT_LIST.get().size();
            return ResourceLocation.parse(HexConfig.FONT_LIST.get().get(index));
        }
    }

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void init() {
    }

    @Override
    public void openCodexGui() {

    }


//    public static void registerISTER(Consumer<IClientItemExtensions> consumer, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory) {
//        consumer.accept(new IClientItemExtensions() {
//            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
//                    () -> factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
//                            Minecraft.getInstance().getEntityModels()));
//
//            @Override
//            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                return renderer.get();
//            }
//        });
//    }

    @SubscribeEvent
    public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        //tooltips
        event.register(HerbJarItem.HerbJarToolTip.class, ClientHerbJarToolTip::new);
        event.register(CofferItem.CofferItemToolTip.class, ClientCofferToolTip::new);
        event.register(BroomItem.BroomItemToolTip.class, ClientBroomToolTip::new);
    }

    @SubscribeEvent
    public static void setup(EntityRenderersEvent.RegisterRenderers e){
        e.registerBlockEntityRenderer(ModTileEntities.CHEST_TILE.get(), ModChestRenderer::new);
        e.registerBlockEntityRenderer(ModTileEntities.SIGN_TILE.get(), SignRenderer::new);
        e.registerBlockEntityRenderer(ModTileEntities.HANGING_SIGN_TILE.get(), HangingSignRenderer::new);
        e.registerBlockEntityRenderer(ModTileEntities.MIXING_CAULDRON_TILE.get(), context -> new MixingCauldronRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.COFFER_TILE.get(), context -> new CofferRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.HERB_JAR_TILE.get(), context -> new HerbJarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CRYSTAL_BALL_TILE.get(), context -> new CrystalBallRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), context -> new BookOfShadowsAltarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.BROOM_STAND_TILE.get(), context -> new BroomStandRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.OWL_COURIER_DEPOT_TILE.get(), context -> new OwlCourierDepotRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CANDLE_TILE.get(), context -> new CandleRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CANDLE_DIPPER_TILE.get(), context -> new CandleDipperRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.DRYING_RACK_TILE.get(), context -> new DryingRackRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), context -> new PestleAndMortarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), context -> new SageBurningPlateRenderer());
        e.registerEntityRenderer(ModEntityTypes.BROOM.get(), BroomRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.HEXEREI_BOAT.get(), ModBoatRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.HEXEREI_CHEST_BOAT.get(), ModChestBoatRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.CROW.get(), CrowRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.OWL.get(), OwlRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.BOOK_CANVAS.get(), HexereiPaintingRenderer::new);
        ModItemProperties.setup();
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BroomModel.LAYER_LOCATION, BroomModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomModel.POWER_LAYER_LOCATION, BroomModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(OwlModel.LAYER_LOCATION, OwlModel::createBodyLayerNone);
        event.registerLayerDefinition(CrowModel.LAYER_LOCATION, CrowModel::createBodyLayerNone);
        event.registerLayerDefinition(CrowModel.POWER_LAYER_LOCATION, CrowModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomBrushBaseModel.LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomBrushBaseModel.POWER_LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomStickBaseModel.LAYER_LOCATION, BroomStickBaseModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomStickBaseModel.POWER_LAYER_LOCATION, BroomStickBaseModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(WitchHazelBroomStickModel.LAYER_LOCATION, WitchHazelBroomStickModel::createBodyLayerNone);
        event.registerLayerDefinition(WitchHazelBroomStickModel.POWER_LAYER_LOCATION, WitchHazelBroomStickModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
        event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
        event.registerLayerDefinition(BroomSmallSatchelModel.LAYER_LOCATION, BroomSmallSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomMediumSatchelModel.LAYER_LOCATION, BroomMediumSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomLargeSatchelModel.LAYER_LOCATION, BroomLargeSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomSeatModel.LAYER_LOCATION, BroomSeatModel::createBodyLayer);
        event.registerLayerDefinition(BroomKeychainModel.LAYER_LOCATION, BroomKeychainModel::createBodyLayer);
        event.registerLayerDefinition(BroomKeychainChainModel.LAYER_LOCATION, BroomKeychainChainModel::createBodyLayer);
        event.registerLayerDefinition(BroomNetheriteTipModel.LAYER_LOCATION, BroomNetheriteTipModel::createBodyLayer);
        event.registerLayerDefinition(BroomWaterproofTipModel.LAYER_LOCATION, BroomWaterproofTipModel::createBodyLayer);
        event.registerLayerDefinition(BroomThrusterBrushModel.LAYER_LOCATION, BroomThrusterBrushModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomThrusterBrushModel.POWER_LAYER_LOCATION, BroomThrusterBrushModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(MoonDustBrushModel.LAYER_LOCATION, MoonDustBrushModel::createBodyLayerNone);
        event.registerLayerDefinition(MoonDustBrushModel.POWER_LAYER_LOCATION, MoonDustBrushModel::createBodyLayerEnlarge);
//        event.registerLayerDefinition(ClientProxy.WITCH_ARMOR_LAYER, WitchArmorModel::createBodyLayer);
//        event.registerLayerDefinition(ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER, MushroomWitchArmorModel::createBodyLayer);
        event.registerLayerDefinition(ClientProxy.CANDLE_HERB_LAYER, CandleHerbLayer::createBodyLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_LAYER, CandleModel::createBodyLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_BASE_LAYER, CandleModel::createBaseLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_HERB_LAYER, CandleModel::createBodyLayerHerb);
        event.registerLayerDefinition(CandleModel.CANDLE_GLOW_LAYER, CandleModel::createBodyLayerGlow);
        event.registerLayerDefinition(CandleModel.CANDLE_SWIRL_LAYER, CandleModel::createBodyLayerSwirl);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/willow"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_willow"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/witch_hazel"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_witch_hazel"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/mahogany"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_mahogany"), "main"), BoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/willow"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_willow"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/witch_hazel"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_witch_hazel"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/mahogany"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_mahogany"), "main"), ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany"), "main"), ModChestRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany_right"), "main"), ModChestRenderer::createDoubleBodyRightLayer);
        event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany_left"), "main"), ModChestRenderer::createDoubleBodyLeftLayer);


        initArmors(event::registerLayerDefinition);

    }

    public static void initArmors(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {

        consumer.accept(ClientProxy.WITCH_ARMOR_LAYER, () -> LayerDefinition.create(WitchArmorModel.createBodyLayer(CubeDeformation.NONE), 128, 128));
        consumer.accept(ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER, () -> LayerDefinition.create(MushroomWitchArmorModel.createBodyLayer(), 128, 128));
    }

    public static final Map<Character, ResourceLocation> TEXT = Maps.newHashMap();
    public static final Map<Character, Float> TEXT_WIDTH = Maps.newHashMap();

//    @SubscribeEvent
//    public static void onTextureStitch(TextureStitchEvent.Pre event) {
//        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
//            return;
//        }
//        registerTextLocations();
//        TEXT.forEach((character, resourceLocation) -> {
//            event.addSprite(resourceLocation);
//        });
//        event.addSprite(PageDrawing.SLOT_ATLAS);
//        event.addSprite(PageDrawing.TITLE);
//
//        ResourceLocation atlasLocation = event.getAtlas().location();
//        List<StitchedSprite> sprites = ALL.get(atlasLocation);
//
//        if (sprites != null) {
//            for (StitchedSprite sprite : sprites) {
//                event.addSprite(sprite.getLocation());
//            }
//        }
//    }

    @SubscribeEvent
    public static void onTextureStitch(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        ResourceLocation atlasLocation = atlas.location();

        List<StitchedSprite> sprites = ALL.get(atlasLocation);
        if (sprites != null) {
            for (StitchedSprite sprite : sprites) {
                sprite.loadSprite(atlas);
            }
        }
    }


    public static void registerTextLocations(){
//        registerTextWidthLocations();
////        for(char i = 'A'; i < 'A' + 23; i++)
//        TEXT.put(' ',HexereiUtil.getResource("book/space"));
//        TEXT.put('1',HexereiUtil.getResource("book/1"));
//        TEXT.put('2',HexereiUtil.getResource("book/2"));
//        TEXT.put('3',HexereiUtil.getResource("book/3"));
//        TEXT.put('4',HexereiUtil.getResource("book/4"));
//        TEXT.put('5',HexereiUtil.getResource("book/5"));
//        TEXT.put('6',HexereiUtil.getResource("book/6"));
//        TEXT.put('7',HexereiUtil.getResource("book/7"));
//        TEXT.put('8',HexereiUtil.getResource("book/8"));
//        TEXT.put('9',HexereiUtil.getResource("book/9"));
//        TEXT.put('0',HexereiUtil.getResource("book/0"));
//        TEXT.put('(',HexereiUtil.getResource("book/left_parentheses"));
//        TEXT.put(')',HexereiUtil.getResource("book/right_parentheses"));
//        TEXT.put('.',HexereiUtil.getResource("book/period"));
//        TEXT.put(',',HexereiUtil.getResource("book/comma"));
//        TEXT.put('!',HexereiUtil.getResource("book/exclamation_point"));
//        TEXT.put('?',HexereiUtil.getResource("book/question_mark"));
//        TEXT.put('`',HexereiUtil.getResource("book/grave"));
//        TEXT.put('~',HexereiUtil.getResource("book/tilde"));
//        TEXT.put('@',HexereiUtil.getResource("book/at"));
//        TEXT.put('#',HexereiUtil.getResource("book/pound"));
//        TEXT.put('$',HexereiUtil.getResource("book/dollar"));
//        TEXT.put('%',HexereiUtil.getResource("book/percent"));
//        TEXT.put('^',HexereiUtil.getResource("book/caret"));
//        TEXT.put('&',HexereiUtil.getResource("book/ampersand"));
//        TEXT.put('*',HexereiUtil.getResource("book/star"));
//        TEXT.put('-',HexereiUtil.getResource("book/dash"));
//        TEXT.put('_',HexereiUtil.getResource("book/underscore"));
//        TEXT.put('=',HexereiUtil.getResource("book/equals"));
//        TEXT.put('+',HexereiUtil.getResource("book/plus"));
//        TEXT.put(':',HexereiUtil.getResource("book/colon"));
//        TEXT.put(';',HexereiUtil.getResource("book/semi_colon"));
//        TEXT.put('|',HexereiUtil.getResource("book/vertical_bar"));
//        TEXT.put('/',HexereiUtil.getResource("book/slash"));
//        TEXT.put('\\',HexereiUtil.getResource("book/backslash"));
//        TEXT.put('[',HexereiUtil.getResource("book/right_bracket"));
//        TEXT.put(']',HexereiUtil.getResource("book/left_bracket"));
//        TEXT.put('{',HexereiUtil.getResource("book/right_brace"));
//        TEXT.put('}',HexereiUtil.getResource("book/left_brace"));
//        TEXT.put('<',HexereiUtil.getResource("book/less_than"));
//        TEXT.put('>',HexereiUtil.getResource("book/greater_than"));
//        TEXT.put('\'',HexereiUtil.getResource("book/apostrophe"));
//        TEXT.put('"',HexereiUtil.getResource("book/quote"));
//
//
//        TEXT.put('A',HexereiUtil.getResource("book/a_upper"));
//        TEXT.put('B',HexereiUtil.getResource("book/b_upper"));
//        TEXT.put('C',HexereiUtil.getResource("book/c_upper"));
//        TEXT.put('D',HexereiUtil.getResource("book/d_upper"));
//        TEXT.put('E',HexereiUtil.getResource("book/e_upper"));
//        TEXT.put('F',HexereiUtil.getResource("book/f_upper"));
//        TEXT.put('G',HexereiUtil.getResource("book/g_upper"));
//        TEXT.put('H',HexereiUtil.getResource("book/h_upper"));
//        TEXT.put('I',HexereiUtil.getResource("book/i_upper"));
//        TEXT.put('J',HexereiUtil.getResource("book/j_upper"));
//        TEXT.put('K',HexereiUtil.getResource("book/k_upper"));
//        TEXT.put('L',HexereiUtil.getResource("book/l_upper"));
//        TEXT.put('M',HexereiUtil.getResource("book/m_upper"));
//        TEXT.put('N',HexereiUtil.getResource("book/n_upper"));
//        TEXT.put('O',HexereiUtil.getResource("book/o_upper"));
//        TEXT.put('P',HexereiUtil.getResource("book/p_upper"));
//        TEXT.put('Q',HexereiUtil.getResource("book/q_upper"));
//        TEXT.put('R',HexereiUtil.getResource("book/r_upper"));
//        TEXT.put('S',HexereiUtil.getResource("book/s_upper"));
//        TEXT.put('T',HexereiUtil.getResource("book/t_upper"));
//        TEXT.put('U',HexereiUtil.getResource("book/u_upper"));
//        TEXT.put('V',HexereiUtil.getResource("book/v_upper"));
//        TEXT.put('W',HexereiUtil.getResource("book/w_upper"));
//        TEXT.put('X',HexereiUtil.getResource("book/x_upper"));
//        TEXT.put('Y',HexereiUtil.getResource("book/y_upper"));
//        TEXT.put('Z',HexereiUtil.getResource("book/z_upper"));
//
//        TEXT.put('a',HexereiUtil.getResource("book/a_lower"));
//        TEXT.put('b',HexereiUtil.getResource("book/b_lower"));
//        TEXT.put('c',HexereiUtil.getResource("book/c_lower"));
//        TEXT.put('d',HexereiUtil.getResource("book/d_lower"));
//        TEXT.put('e',HexereiUtil.getResource("book/e_lower"));
//        TEXT.put('f',HexereiUtil.getResource("book/f_lower"));
//        TEXT.put('g',HexereiUtil.getResource("book/g_lower"));
//        TEXT.put('h',HexereiUtil.getResource("book/h_lower"));
//        TEXT.put('i',HexereiUtil.getResource("book/i_lower"));
//        TEXT.put('j',HexereiUtil.getResource("book/j_lower"));
//        TEXT.put('k',HexereiUtil.getResource("book/k_lower"));
//        TEXT.put('l',HexereiUtil.getResource("book/l_lower"));
//        TEXT.put('m',HexereiUtil.getResource("book/m_lower"));
//        TEXT.put('n',HexereiUtil.getResource("book/n_lower"));
//        TEXT.put('o',HexereiUtil.getResource("book/o_lower"));
//        TEXT.put('p',HexereiUtil.getResource("book/p_lower"));
//        TEXT.put('q',HexereiUtil.getResource("book/q_lower"));
//        TEXT.put('r',HexereiUtil.getResource("book/r_lower"));
//        TEXT.put('s',HexereiUtil.getResource("book/s_lower"));
//        TEXT.put('t',HexereiUtil.getResource("book/t_lower"));
//        TEXT.put('u',HexereiUtil.getResource("book/u_lower"));
//        TEXT.put('v',HexereiUtil.getResource("book/v_lower"));
//        TEXT.put('w',HexereiUtil.getResource("book/w_lower"));
//        TEXT.put('x',HexereiUtil.getResource("book/x_lower"));
//        TEXT.put('y',HexereiUtil.getResource("book/y_lower"));
//        TEXT.put('z',HexereiUtil.getResource("book/z_lower"));
    }
//    public static void registerTextWidthLocations(){
//        TEXT_WIDTH.put(' ',0.026f);
//        TEXT_WIDTH.put('1',0.025f);
//        TEXT_WIDTH.put('2',0.022f);
//        TEXT_WIDTH.put('3',0.025f);
//        TEXT_WIDTH.put('4',0.022f);
//        TEXT_WIDTH.put('5',0.022f);
//        TEXT_WIDTH.put('6',0.022f);
//        TEXT_WIDTH.put('7',0.025f);
//        TEXT_WIDTH.put('8',0.025f);
//        TEXT_WIDTH.put('9',0.025f);
//        TEXT_WIDTH.put('0',0.028f);
//        TEXT_WIDTH.put('(',0.020f);
//        TEXT_WIDTH.put(')',0.020f);
//        TEXT_WIDTH.put('.',0.012f);
//        TEXT_WIDTH.put(',',0.012f);
//        TEXT_WIDTH.put('!',0.020f);
//        TEXT_WIDTH.put('?',0.020f);
//        TEXT_WIDTH.put('`',0.020f);
//        TEXT_WIDTH.put('~',0.040f);
//        TEXT_WIDTH.put('@',0.040f);
//        TEXT_WIDTH.put('#',0.038f);
//        TEXT_WIDTH.put('$',0.038f);
//        TEXT_WIDTH.put('%',0.039f);
//        TEXT_WIDTH.put('^',0.030f);
//        TEXT_WIDTH.put('&',0.038f);
//        TEXT_WIDTH.put('*',0.032f);
//        TEXT_WIDTH.put('-',0.032f);
//        TEXT_WIDTH.put('_',0.038f);
//        TEXT_WIDTH.put('=',0.032f);
//        TEXT_WIDTH.put('+',0.032f);
//        TEXT_WIDTH.put(':',0.014f);
//        TEXT_WIDTH.put(';',0.014f);
//        TEXT_WIDTH.put('|',0.018f);
//        TEXT_WIDTH.put('/',0.038f);
//        TEXT_WIDTH.put('\\',0.038f);
//        TEXT_WIDTH.put('[',0.022f);
//        TEXT_WIDTH.put(']',0.022f);
//        TEXT_WIDTH.put('{',0.022f);
//        TEXT_WIDTH.put('}',0.022f);
//        TEXT_WIDTH.put('<',0.030f);
//        TEXT_WIDTH.put('>',0.030f);
//        TEXT_WIDTH.put('\'',0.011f);
//        TEXT_WIDTH.put('"',0.012f);
//
//        TEXT_WIDTH.put('A',0.042f);
//        TEXT_WIDTH.put('B',0.042f);
//        TEXT_WIDTH.put('C',0.040f);
//        TEXT_WIDTH.put('D',0.040f);
//        TEXT_WIDTH.put('E',0.042f);
//        TEXT_WIDTH.put('F',0.037f);
//        TEXT_WIDTH.put('G',0.042f);
//        TEXT_WIDTH.put('H',0.042f);
//        TEXT_WIDTH.put('I',0.042f);
//        TEXT_WIDTH.put('J',0.037f);
//        TEXT_WIDTH.put('K',0.042f);
//        TEXT_WIDTH.put('L',0.042f);
//        TEXT_WIDTH.put('M',0.042f);
//        TEXT_WIDTH.put('N',0.042f);
//        TEXT_WIDTH.put('O',0.042f);
//        TEXT_WIDTH.put('P',0.042f);
//        TEXT_WIDTH.put('Q',0.042f);
//        TEXT_WIDTH.put('R',0.042f);
//        TEXT_WIDTH.put('S',0.042f);
//        TEXT_WIDTH.put('T',0.042f);
//        TEXT_WIDTH.put('U',0.042f);
//        TEXT_WIDTH.put('V',0.042f);
//        TEXT_WIDTH.put('W',0.047f);
//        TEXT_WIDTH.put('X',0.042f);
//        TEXT_WIDTH.put('Y',0.042f);
//        TEXT_WIDTH.put('Z',0.039f);
//
//        TEXT_WIDTH.put('a',0.027f);
//        TEXT_WIDTH.put('b',0.025f);
//        TEXT_WIDTH.put('c',0.027f);
//        TEXT_WIDTH.put('d',0.025f);
//        TEXT_WIDTH.put('e',0.027f);
//        TEXT_WIDTH.put('f',0.024f);
//        TEXT_WIDTH.put('g',0.027f);
//        TEXT_WIDTH.put('h',0.024f);
//        TEXT_WIDTH.put('i',0.019f);
//        TEXT_WIDTH.put('j',0.019f);
//        TEXT_WIDTH.put('k',0.028f);
//        TEXT_WIDTH.put('l',0.025f);
//        TEXT_WIDTH.put('m',0.038f);
//        TEXT_WIDTH.put('n',0.030f);
//        TEXT_WIDTH.put('o',0.028f);
//        TEXT_WIDTH.put('p',0.028f);
//        TEXT_WIDTH.put('q',0.028f);
//        TEXT_WIDTH.put('r',0.027f);
//        TEXT_WIDTH.put('s',0.028f);
//        TEXT_WIDTH.put('t',0.026f);
//        TEXT_WIDTH.put('u',0.030f);
//        TEXT_WIDTH.put('v',0.030f);
//        TEXT_WIDTH.put('w',0.038f);
//        TEXT_WIDTH.put('x',0.030f);
//        TEXT_WIDTH.put('y',0.032f);
//        TEXT_WIDTH.put('z',0.028f);
//    }



}