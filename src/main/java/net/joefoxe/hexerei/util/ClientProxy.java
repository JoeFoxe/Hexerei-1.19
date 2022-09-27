package net.joefoxe.hexerei.util;

import com.google.common.collect.Maps;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.ModelSwapper;
import net.joefoxe.hexerei.block.connected.StitchedSprite;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.*;
import net.joefoxe.hexerei.client.renderer.entity.render.CrowRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.ModBoatRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.ModChestBoatRenderer;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.ModItemProperties;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.screen.tooltip.ClientBroomToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientCofferToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientHerbJarToolTip;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.renderer.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static net.joefoxe.hexerei.block.connected.StitchedSprite.ALL;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy implements SidedProxy {
    public static KeyMapping[] keys = null;

    public static final ModelLayerLocation CANDLE_HERB_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "candle_herb_layer"), "main");
    public static final ModelLayerLocation WITCH_ARMOR_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "witch_armor"), "main");
    public static final ModelLayerLocation MUSHROOM_WITCH_ARMOR_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "mushroom_witch_armor"), "main");

    public static final ModelLayerLocation READING_GLASSES_LAYER = new ModelLayerLocation(new ResourceLocation(Hexerei.MOD_ID, "reading_glasses"), "main");

    public static final BlockConnectivity BLOCK_CONNECTIVITY = new BlockConnectivity();
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();


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


    public static void registerISTER(Consumer<IClientItemExtensions> consumer, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory) {
        consumer.accept(new IClientItemExtensions() {
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
                    () -> factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels()));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }

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
        e.registerBlockEntityRenderer(ModTileEntities.SIGN_TILE.get(), ModSignRenderer::new);
        e.registerBlockEntityRenderer(ModTileEntities.MIXING_CAULDRON_TILE.get(), context -> new MixingCauldronRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.COFFER_TILE.get(), context -> new CofferRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.HERB_JAR_TILE.get(), context -> new HerbJarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CRYSTAL_BALL_TILE.get(), context -> new CrystalBallRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), context -> new BookOfShadowsAltarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CANDLE_TILE.get(), context -> new CandleRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.CANDLE_DIPPER_TILE.get(), context -> new CandleDipperRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.DRYING_RACK_TILE.get(), context -> new DryingRackRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), context -> new PestleAndMortarRenderer());
        e.registerBlockEntityRenderer(ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), context -> new SageBurningPlateRenderer());
        e.registerEntityRenderer(ModEntityTypes.BROOM.get(), BroomRenderer::new);
        e.registerEntityRenderer(ModEntityTypes.HEXEREI_BOAT.get(), context -> new ModBoatRenderer(context, false));
        e.registerEntityRenderer(ModEntityTypes.HEXEREI_CHEST_BOAT.get(), context -> new ModChestBoatRenderer(context, true));
        e.registerEntityRenderer(ModEntityTypes.CROW.get(), CrowRenderer::new);
        ModItemProperties.makeDowsingRod(ModItems.DOWSING_ROD.get());
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BroomModel.LAYER_LOCATION, BroomModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomModel.POWER_LAYER_LOCATION, BroomModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(CrowModel.LAYER_LOCATION, CrowModel::createBodyLayerNone);
        event.registerLayerDefinition(CrowModel.POWER_LAYER_LOCATION, CrowModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomBrushBaseModel.LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomBrushBaseModel.POWER_LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomStickBaseModel.LAYER_LOCATION, BroomStickBaseModel::createBodyLayerNone);
        event.registerLayerDefinition(BroomStickBaseModel.POWER_LAYER_LOCATION, BroomStickBaseModel::createBodyLayerEnlarge);
        event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
        event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
        event.registerLayerDefinition(BroomSmallSatchelModel.LAYER_LOCATION, BroomSmallSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomMediumSatchelModel.LAYER_LOCATION, BroomMediumSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomLargeSatchelModel.LAYER_LOCATION, BroomLargeSatchelModel::createBodyLayer);
        event.registerLayerDefinition(BroomKeychainModel.LAYER_LOCATION, BroomKeychainModel::createBodyLayer);
        event.registerLayerDefinition(BroomKeychainChainModel.LAYER_LOCATION, BroomKeychainChainModel::createBodyLayer);
        event.registerLayerDefinition(BroomNetheriteTipModel.LAYER_LOCATION, BroomNetheriteTipModel::createBodyLayer);
        event.registerLayerDefinition(BroomWaterproofTipModel.LAYER_LOCATION, BroomWaterproofTipModel::createBodyLayer);
        event.registerLayerDefinition(ClientProxy.WITCH_ARMOR_LAYER, WitchArmorModel::createBodyLayer);
        event.registerLayerDefinition(ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER, MushroomWitchArmorModel::createBodyLayer);
        event.registerLayerDefinition(ClientProxy.CANDLE_HERB_LAYER, CandleHerbLayer::createBodyLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_LAYER, CandleModel::createBodyLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_BASE_LAYER, CandleModel::createBaseLayer);
        event.registerLayerDefinition(CandleModel.CANDLE_HERB_LAYER, CandleModel::createBodyLayerHerb);
        event.registerLayerDefinition(CandleModel.CANDLE_GLOW_LAYER, CandleModel::createBodyLayerGlow);
        event.registerLayerDefinition(CandleModel.CANDLE_SWIRL_LAYER, CandleModel::createBodyLayerSwirl);
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "boat/willow"), "main"), ()-> BoatModel.createBodyModel(false));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "boat/polished_willow"), "main"), ()-> BoatModel.createBodyModel(false));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "boat/mahogany"), "main"), ()-> BoatModel.createBodyModel(false));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "boat/polished_mahogany"), "main"), ()-> BoatModel.createBodyModel(false));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest_boat/willow"), "main"), ()-> BoatModel.createBodyModel(true));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest_boat/polished_willow"), "main"), ()-> BoatModel.createBodyModel(true));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest_boat/mahogany"), "main"), ()-> BoatModel.createBodyModel(true));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest_boat/polished_mahogany"), "main"), ()-> BoatModel.createBodyModel(true));
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany"), "main"), ModChestRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany_right"), "main"), ModChestRenderer::createDoubleBodyRightLayer);
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany_left"), "main"), ModChestRenderer::createDoubleBodyLeftLayer);
        event.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation("hexerei", "sign/mahogany"), "main"), ModSignRenderer::createSignLayer);

    }

    public static final Map<Character, ResourceLocation> TEXT = Maps.newHashMap();
    public static final Map<Character, Float> TEXT_WIDTH = Maps.newHashMap();

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        registerTextLocations();
        TEXT.forEach((character, resourceLocation) -> {
            event.addSprite(resourceLocation);
        });
        event.addSprite(PageDrawing.SLOT_ATLAS);
        event.addSprite(PageDrawing.TITLE);

        ResourceLocation atlasLocation = event.getAtlas().location();
        List<StitchedSprite> sprites = ALL.get(atlasLocation);

        if (sprites != null) {
            for (StitchedSprite sprite : sprites) {
                event.addSprite(sprite.getLocation());
            }
        }
    }
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Post event) {
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
        registerTextWidthLocations();
//        for(char i = 'A'; i < 'A' + 23; i++)
        TEXT.put(' ',new ResourceLocation(Hexerei.MOD_ID, "book/space"));
        TEXT.put('1',new ResourceLocation(Hexerei.MOD_ID, "book/1"));
        TEXT.put('2',new ResourceLocation(Hexerei.MOD_ID, "book/2"));
        TEXT.put('3',new ResourceLocation(Hexerei.MOD_ID, "book/3"));
        TEXT.put('4',new ResourceLocation(Hexerei.MOD_ID, "book/4"));
        TEXT.put('5',new ResourceLocation(Hexerei.MOD_ID, "book/5"));
        TEXT.put('6',new ResourceLocation(Hexerei.MOD_ID, "book/6"));
        TEXT.put('7',new ResourceLocation(Hexerei.MOD_ID, "book/7"));
        TEXT.put('8',new ResourceLocation(Hexerei.MOD_ID, "book/8"));
        TEXT.put('9',new ResourceLocation(Hexerei.MOD_ID, "book/9"));
        TEXT.put('0',new ResourceLocation(Hexerei.MOD_ID, "book/0"));
        TEXT.put('(',new ResourceLocation(Hexerei.MOD_ID, "book/left_parentheses"));
        TEXT.put(')',new ResourceLocation(Hexerei.MOD_ID, "book/right_parentheses"));
        TEXT.put('.',new ResourceLocation(Hexerei.MOD_ID, "book/period"));
        TEXT.put(',',new ResourceLocation(Hexerei.MOD_ID, "book/comma"));
        TEXT.put('!',new ResourceLocation(Hexerei.MOD_ID, "book/exclamation_point"));
        TEXT.put('?',new ResourceLocation(Hexerei.MOD_ID, "book/question_mark"));
        TEXT.put('`',new ResourceLocation(Hexerei.MOD_ID, "book/grave"));
        TEXT.put('~',new ResourceLocation(Hexerei.MOD_ID, "book/tilde"));
        TEXT.put('@',new ResourceLocation(Hexerei.MOD_ID, "book/at"));
        TEXT.put('#',new ResourceLocation(Hexerei.MOD_ID, "book/pound"));
        TEXT.put('$',new ResourceLocation(Hexerei.MOD_ID, "book/dollar"));
        TEXT.put('%',new ResourceLocation(Hexerei.MOD_ID, "book/percent"));
        TEXT.put('^',new ResourceLocation(Hexerei.MOD_ID, "book/caret"));
        TEXT.put('&',new ResourceLocation(Hexerei.MOD_ID, "book/ampersand"));
        TEXT.put('*',new ResourceLocation(Hexerei.MOD_ID, "book/star"));
        TEXT.put('-',new ResourceLocation(Hexerei.MOD_ID, "book/dash"));
        TEXT.put('_',new ResourceLocation(Hexerei.MOD_ID, "book/underscore"));
        TEXT.put('=',new ResourceLocation(Hexerei.MOD_ID, "book/equals"));
        TEXT.put('+',new ResourceLocation(Hexerei.MOD_ID, "book/plus"));
        TEXT.put(':',new ResourceLocation(Hexerei.MOD_ID, "book/colon"));
        TEXT.put(';',new ResourceLocation(Hexerei.MOD_ID, "book/semi_colon"));
        TEXT.put('|',new ResourceLocation(Hexerei.MOD_ID, "book/vertical_bar"));
        TEXT.put('/',new ResourceLocation(Hexerei.MOD_ID, "book/slash"));
        TEXT.put('\\',new ResourceLocation(Hexerei.MOD_ID, "book/backslash"));
        TEXT.put('[',new ResourceLocation(Hexerei.MOD_ID, "book/right_bracket"));
        TEXT.put(']',new ResourceLocation(Hexerei.MOD_ID, "book/left_bracket"));
        TEXT.put('{',new ResourceLocation(Hexerei.MOD_ID, "book/right_brace"));
        TEXT.put('}',new ResourceLocation(Hexerei.MOD_ID, "book/left_brace"));
        TEXT.put('<',new ResourceLocation(Hexerei.MOD_ID, "book/less_than"));
        TEXT.put('>',new ResourceLocation(Hexerei.MOD_ID, "book/greater_than"));
        TEXT.put('\'',new ResourceLocation(Hexerei.MOD_ID, "book/apostrophe"));
        TEXT.put('"',new ResourceLocation(Hexerei.MOD_ID, "book/quote"));


        TEXT.put('A',new ResourceLocation(Hexerei.MOD_ID, "book/a_upper"));
        TEXT.put('B',new ResourceLocation(Hexerei.MOD_ID, "book/b_upper"));
        TEXT.put('C',new ResourceLocation(Hexerei.MOD_ID, "book/c_upper"));
        TEXT.put('D',new ResourceLocation(Hexerei.MOD_ID, "book/d_upper"));
        TEXT.put('E',new ResourceLocation(Hexerei.MOD_ID, "book/e_upper"));
        TEXT.put('F',new ResourceLocation(Hexerei.MOD_ID, "book/f_upper"));
        TEXT.put('G',new ResourceLocation(Hexerei.MOD_ID, "book/g_upper"));
        TEXT.put('H',new ResourceLocation(Hexerei.MOD_ID, "book/h_upper"));
        TEXT.put('I',new ResourceLocation(Hexerei.MOD_ID, "book/i_upper"));
        TEXT.put('J',new ResourceLocation(Hexerei.MOD_ID, "book/j_upper"));
        TEXT.put('K',new ResourceLocation(Hexerei.MOD_ID, "book/k_upper"));
        TEXT.put('L',new ResourceLocation(Hexerei.MOD_ID, "book/l_upper"));
        TEXT.put('M',new ResourceLocation(Hexerei.MOD_ID, "book/m_upper"));
        TEXT.put('N',new ResourceLocation(Hexerei.MOD_ID, "book/n_upper"));
        TEXT.put('O',new ResourceLocation(Hexerei.MOD_ID, "book/o_upper"));
        TEXT.put('P',new ResourceLocation(Hexerei.MOD_ID, "book/p_upper"));
        TEXT.put('Q',new ResourceLocation(Hexerei.MOD_ID, "book/q_upper"));
        TEXT.put('R',new ResourceLocation(Hexerei.MOD_ID, "book/r_upper"));
        TEXT.put('S',new ResourceLocation(Hexerei.MOD_ID, "book/s_upper"));
        TEXT.put('T',new ResourceLocation(Hexerei.MOD_ID, "book/t_upper"));
        TEXT.put('U',new ResourceLocation(Hexerei.MOD_ID, "book/u_upper"));
        TEXT.put('V',new ResourceLocation(Hexerei.MOD_ID, "book/v_upper"));
        TEXT.put('W',new ResourceLocation(Hexerei.MOD_ID, "book/w_upper"));
        TEXT.put('X',new ResourceLocation(Hexerei.MOD_ID, "book/x_upper"));
        TEXT.put('Y',new ResourceLocation(Hexerei.MOD_ID, "book/y_upper"));
        TEXT.put('Z',new ResourceLocation(Hexerei.MOD_ID, "book/z_upper"));

        TEXT.put('a',new ResourceLocation(Hexerei.MOD_ID, "book/a_lower"));
        TEXT.put('b',new ResourceLocation(Hexerei.MOD_ID, "book/b_lower"));
        TEXT.put('c',new ResourceLocation(Hexerei.MOD_ID, "book/c_lower"));
        TEXT.put('d',new ResourceLocation(Hexerei.MOD_ID, "book/d_lower"));
        TEXT.put('e',new ResourceLocation(Hexerei.MOD_ID, "book/e_lower"));
        TEXT.put('f',new ResourceLocation(Hexerei.MOD_ID, "book/f_lower"));
        TEXT.put('g',new ResourceLocation(Hexerei.MOD_ID, "book/g_lower"));
        TEXT.put('h',new ResourceLocation(Hexerei.MOD_ID, "book/h_lower"));
        TEXT.put('i',new ResourceLocation(Hexerei.MOD_ID, "book/i_lower"));
        TEXT.put('j',new ResourceLocation(Hexerei.MOD_ID, "book/j_lower"));
        TEXT.put('k',new ResourceLocation(Hexerei.MOD_ID, "book/k_lower"));
        TEXT.put('l',new ResourceLocation(Hexerei.MOD_ID, "book/l_lower"));
        TEXT.put('m',new ResourceLocation(Hexerei.MOD_ID, "book/m_lower"));
        TEXT.put('n',new ResourceLocation(Hexerei.MOD_ID, "book/n_lower"));
        TEXT.put('o',new ResourceLocation(Hexerei.MOD_ID, "book/o_lower"));
        TEXT.put('p',new ResourceLocation(Hexerei.MOD_ID, "book/p_lower"));
        TEXT.put('q',new ResourceLocation(Hexerei.MOD_ID, "book/q_lower"));
        TEXT.put('r',new ResourceLocation(Hexerei.MOD_ID, "book/r_lower"));
        TEXT.put('s',new ResourceLocation(Hexerei.MOD_ID, "book/s_lower"));
        TEXT.put('t',new ResourceLocation(Hexerei.MOD_ID, "book/t_lower"));
        TEXT.put('u',new ResourceLocation(Hexerei.MOD_ID, "book/u_lower"));
        TEXT.put('v',new ResourceLocation(Hexerei.MOD_ID, "book/v_lower"));
        TEXT.put('w',new ResourceLocation(Hexerei.MOD_ID, "book/w_lower"));
        TEXT.put('x',new ResourceLocation(Hexerei.MOD_ID, "book/x_lower"));
        TEXT.put('y',new ResourceLocation(Hexerei.MOD_ID, "book/y_lower"));
        TEXT.put('z',new ResourceLocation(Hexerei.MOD_ID, "book/z_lower"));
    }
    public static void registerTextWidthLocations(){
        TEXT_WIDTH.put(' ',0.026f);
        TEXT_WIDTH.put('1',0.025f);
        TEXT_WIDTH.put('2',0.022f);
        TEXT_WIDTH.put('3',0.025f);
        TEXT_WIDTH.put('4',0.022f);
        TEXT_WIDTH.put('5',0.022f);
        TEXT_WIDTH.put('6',0.022f);
        TEXT_WIDTH.put('7',0.025f);
        TEXT_WIDTH.put('8',0.025f);
        TEXT_WIDTH.put('9',0.025f);
        TEXT_WIDTH.put('0',0.028f);
        TEXT_WIDTH.put('(',0.020f);
        TEXT_WIDTH.put(')',0.020f);
        TEXT_WIDTH.put('.',0.012f);
        TEXT_WIDTH.put(',',0.012f);
        TEXT_WIDTH.put('!',0.020f);
        TEXT_WIDTH.put('?',0.020f);
        TEXT_WIDTH.put('`',0.020f);
        TEXT_WIDTH.put('~',0.040f);
        TEXT_WIDTH.put('@',0.040f);
        TEXT_WIDTH.put('#',0.038f);
        TEXT_WIDTH.put('$',0.038f);
        TEXT_WIDTH.put('%',0.039f);
        TEXT_WIDTH.put('^',0.030f);
        TEXT_WIDTH.put('&',0.038f);
        TEXT_WIDTH.put('*',0.032f);
        TEXT_WIDTH.put('-',0.032f);
        TEXT_WIDTH.put('_',0.038f);
        TEXT_WIDTH.put('=',0.032f);
        TEXT_WIDTH.put('+',0.032f);
        TEXT_WIDTH.put(':',0.014f);
        TEXT_WIDTH.put(';',0.014f);
        TEXT_WIDTH.put('|',0.018f);
        TEXT_WIDTH.put('/',0.038f);
        TEXT_WIDTH.put('\\',0.038f);
        TEXT_WIDTH.put('[',0.022f);
        TEXT_WIDTH.put(']',0.022f);
        TEXT_WIDTH.put('{',0.022f);
        TEXT_WIDTH.put('}',0.022f);
        TEXT_WIDTH.put('<',0.030f);
        TEXT_WIDTH.put('>',0.030f);
        TEXT_WIDTH.put('\'',0.011f);
        TEXT_WIDTH.put('"',0.012f);

        TEXT_WIDTH.put('A',0.042f);
        TEXT_WIDTH.put('B',0.042f);
        TEXT_WIDTH.put('C',0.040f);
        TEXT_WIDTH.put('D',0.040f);
        TEXT_WIDTH.put('E',0.042f);
        TEXT_WIDTH.put('F',0.037f);
        TEXT_WIDTH.put('G',0.042f);
        TEXT_WIDTH.put('H',0.042f);
        TEXT_WIDTH.put('I',0.042f);
        TEXT_WIDTH.put('J',0.037f);
        TEXT_WIDTH.put('K',0.042f);
        TEXT_WIDTH.put('L',0.042f);
        TEXT_WIDTH.put('M',0.042f);
        TEXT_WIDTH.put('N',0.042f);
        TEXT_WIDTH.put('O',0.042f);
        TEXT_WIDTH.put('P',0.042f);
        TEXT_WIDTH.put('Q',0.042f);
        TEXT_WIDTH.put('R',0.042f);
        TEXT_WIDTH.put('S',0.042f);
        TEXT_WIDTH.put('T',0.042f);
        TEXT_WIDTH.put('U',0.042f);
        TEXT_WIDTH.put('V',0.042f);
        TEXT_WIDTH.put('W',0.047f);
        TEXT_WIDTH.put('X',0.042f);
        TEXT_WIDTH.put('Y',0.042f);
        TEXT_WIDTH.put('Z',0.039f);

        TEXT_WIDTH.put('a',0.027f);
        TEXT_WIDTH.put('b',0.025f);
        TEXT_WIDTH.put('c',0.027f);
        TEXT_WIDTH.put('d',0.025f);
        TEXT_WIDTH.put('e',0.027f);
        TEXT_WIDTH.put('f',0.024f);
        TEXT_WIDTH.put('g',0.027f);
        TEXT_WIDTH.put('h',0.024f);
        TEXT_WIDTH.put('i',0.019f);
        TEXT_WIDTH.put('j',0.019f);
        TEXT_WIDTH.put('k',0.028f);
        TEXT_WIDTH.put('l',0.025f);
        TEXT_WIDTH.put('m',0.038f);
        TEXT_WIDTH.put('n',0.030f);
        TEXT_WIDTH.put('o',0.028f);
        TEXT_WIDTH.put('p',0.028f);
        TEXT_WIDTH.put('q',0.028f);
        TEXT_WIDTH.put('r',0.027f);
        TEXT_WIDTH.put('s',0.028f);
        TEXT_WIDTH.put('t',0.026f);
        TEXT_WIDTH.put('u',0.030f);
        TEXT_WIDTH.put('v',0.030f);
        TEXT_WIDTH.put('w',0.038f);
        TEXT_WIDTH.put('x',0.030f);
        TEXT_WIDTH.put('y',0.032f);
        TEXT_WIDTH.put('z',0.028f);
    }



}