package net.joefoxe.hexerei.item;

import com.tterrag.registrate.Registrate;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.*;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class ModItems {


    public static final Registrate REGISTRATE = Hexerei.registrate();



//    public static final ItemEntry<BlockItem> WILLOW_CONNECTED = REGISTRATE.block("willow_connected", Block::new)
//            .properties(p -> p.color(MaterialColor.TERRACOTTA_GREEN))
//            .item()
//            .tab(() -> ModItemGroup.HEXEREI_GROUP)
//            .register();


    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Hexerei.MOD_ID);


    public static final RegistryObject<Item> BOOK_OF_SHADOWS = ITEMS.register("book_of_shadows",
            () -> new HexereiBookItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1)));


    public static final RegistryObject<Item> MAHOGANY_BROOM = ITEMS.register("mahogany_broom",
            () -> new BroomItem(BroomEntity.Type.MAHOGANY, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1).fireResistant()));

    public static final RegistryObject<Item> WILLOW_BROOM = ITEMS.register("willow_broom",
            () -> new BroomItem(BroomEntity.Type.WILLOW, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1)));

//    public static final RegistryObject<Item> FIRE_TABLET = ITEMS.register("fire_tablet",
//            () -> new FireTabletItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WHISTLE = ITEMS.register("broom_whistle",
            () -> new WhistleItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(100)));

    public static final RegistryObject<Item> WAX_BLEND = ITEMS.register("wax_blend",
            () -> new WaxBlendItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CLOTH = ITEMS.register("cloth",
            () -> new CleaningClothItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXING_KIT = ITEMS.register("waxing_kit",
            () -> new WaxingKitItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1), false));

    public static final RegistryObject<Item> CREATIVE_WAXING_KIT = ITEMS.register("creative_waxing_kit",
            () -> new WaxingKitItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1).rarity(Rarity.EPIC), true));

    public static final RegistryObject<Item> WILLOW_BOAT = ITEMS.register("willow_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.WILLOW ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> POLISHED_WILLOW_BOAT = ITEMS.register("polished_willow_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_WILLOW ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> MAHOGANY_BOAT = ITEMS.register("mahogany_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.MAHOGANY ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> POLISHED_MAHOGANY_BOAT = ITEMS.register("polished_mahogany_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_MAHOGANY ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WILLOW_CHEST_BOAT = ITEMS.register("willow_chest_boat",
            () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.WILLOW ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> POLISHED_WILLOW_CHEST_BOAT = ITEMS.register("polished_willow_chest_boat",
            () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_WILLOW ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> MAHOGANY_CHEST_BOAT = ITEMS.register("mahogany_chest_boat",
            () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.MAHOGANY ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> POLISHED_MAHOGANY_CHEST_BOAT = ITEMS.register("polished_mahogany_chest_boat",
            () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_MAHOGANY ,new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> SMALL_SATCHEL = ITEMS.register("small_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomSmallSatchelModel(context.bakeLayer(BroomSmallSatchelModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_small_satchel.png");
                    this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_small_satchel_dye.png");
                }

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.small_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }


                @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
                static class ColorRegisterHandler
                {
                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public static void registerSatchelColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, SMALL_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, MEDIUM_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, LARGE_SATCHEL.get());

                    }
                }
            });

    public static final RegistryObject<Item> MEDIUM_SATCHEL = ITEMS.register("medium_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel.png");
                    this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel_dye.png");
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }


                @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
                static class ColorRegisterHandler
                {
                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public static void registerSatchelColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, SMALL_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, MEDIUM_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, LARGE_SATCHEL.get());

                    }
                }

            });

    public static final RegistryObject<Item> LARGE_SATCHEL = ITEMS.register("large_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){


                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomLargeSatchelModel(context.bakeLayer(BroomLargeSatchelModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_large_satchel.png");
                    this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_large_satchel_dye.png");
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.large_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }


                @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
                static class ColorRegisterHandler
                {
                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public static void registerSatchelColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, SMALL_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, MEDIUM_SATCHEL.get());
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, LARGE_SATCHEL.get());

                    }
                }
            });


    public static final RegistryObject<Item> ENDER_SATCHEL = ITEMS.register("ender_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_ender_satchel.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.ender_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }

            });

    public static final RegistryObject<Item> REPLACER_SATCHEL = ITEMS.register("replacer_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_replacer_satchel.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.replacer_satchel_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.replacer_satchel_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }

            });

    public static final RegistryObject<Item> GOLD_RINGS = ITEMS.register("gold_rings",
            () -> new BroomAttachmentItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomRingsModel(context.bakeLayer(BroomRingsModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> BROOM_NETHERITE_TIP = ITEMS.register("broom_netherite_tip",
            () -> new BroomAttachmentItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(/*HexConfig.BROOM_NETHERITE_TIP_DURABILITY.get()*/200)){


                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomNetheriteTipModel(context.bakeLayer(BroomNetheriteTipModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_netherite_tip.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_netherite_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    else{
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> BROOM_WATERPROOF_TIP = ITEMS.register("broom_waterproof_tip",
            () -> new BroomAttachmentItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(/*HexConfig.BROOM_WATERPROOF_TIP_DURABILITY.get()*/800)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomWaterproofTipModel(context.bakeLayer(BroomWaterproofTipModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_waterproof_tip.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_waterproof_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                    }
                    else{
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> BROOM_KEYCHAIN = ITEMS.register("broom_keychain",
            () -> new KeychainItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> BROOM_KEYCHAIN_BASE = ITEMS.register("broom_keychain_base",
            () -> new Item(new Item.Properties()){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                        tooltip.add(Component.translatable("the base is not for use, see the broom keychain.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WET_BROOM_BRUSH = ITEMS.register("wet_broom_brush",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> BROOM_BRUSH = ITEMS.register("broom_brush",
            () -> new BroomAttachmentItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(/*HexConfig.BROOM_BRUSH_DURABILITY.get()*/100)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WET_HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("wet_herb_enhanced_broom_brush",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("herb_enhanced_broom_brush",
            () -> new BroomAttachmentItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(/*HexConfig.ENHANCED_BROOM_BRUSH_DURABILITY.get()*/200)){

                @OnlyIn(Dist.CLIENT)
                @Override
                public void bakeModels() {
                    EntityModelSet context = Minecraft.getInstance().getEntityModels();
                    this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
                    this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/herb_enhanced_brush.png");
                    this.dye_texture = null;
                }
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WARHAMMER = ITEMS.register("warhammer",
            () -> new SwordItem(ModItemTier.ARMOR_SCRAP, 3, -2.4F,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> ARMOR_SCRAP = ITEMS.register("armor_scrap",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> BLOOD_BUCKET = ITEMS.register("blood_bucket",
            () -> new BucketItem(ModFluids.BLOOD_FLUID, new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> TALLOW_BUCKET = ITEMS.register("tallow_bucket",
            () -> new BucketItem(ModFluids.TALLOW_FLUID, new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> QUICKSILVER_BUCKET = ITEMS.register("quicksilver_bucket",
            () -> new BucketItem(ModFluids.QUICKSILVER_FLUID, new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> QUICKSILVER_BOTTLE = ITEMS.register("quicksilver_bottle",
            () -> new BottleQuicksilverItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> BLOOD_BOTTLE = ITEMS.register("blood_bottle",
            () -> new BottleBloodtem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> TALLOW_BOTTLE = ITEMS.register("tallow_bottle",
            () -> new BottleTallowItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> LAVA_BOTTLE = ITEMS.register("lava_bottle",
            () -> new BottleLavaItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(100)));

    public static final RegistryObject<Item> MILK_BOTTLE = ITEMS.register("milk_bottle",
            () -> new BottleMilkItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> BLOOD_SIGIL = ITEMS.register("blood_sigil",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> ANIMAL_FAT = ITEMS.register("animal_fat",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> TALLOW_IMPURITY = ITEMS.register("tallow_impurity",
            () -> new TallowImpurityItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> INFUSED_FABRIC = ITEMS.register("infused_fabric",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> SELENITE_SHARD = ITEMS.register("selenite_shard",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.selenite_shard").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                    }
                    else{
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> SAGE = ITEMS.register("sage",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> SAGE_SEED = ITEMS.register("sage_seed",
            () -> new ItemNameBlockItem(ModBlocks.SAGE.get(), new Item.Properties()
                    //.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).fastToEat().build())
                    .tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.sage_seeds").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> SAGE_BUNDLE = ITEMS.register("sage_bundle",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_SAGE_BUNDLE = ITEMS.register("dried_sage_bundle",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(/*HexConfig.SAGE_BUNDLE_DURATION.get()*/3600)){
                @Override
                public boolean isEnchantable(ItemStack p_41456_) {
                    return false;
                }

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

                        int duration = stack.getMaxDamage() - stack.getDamageValue();
                        float percentDamaged = stack.getDamageValue() / (float)stack.getMaxDamage();
                        int minutes = duration / 60;
                        int seconds = duration % 60;
                        char color = 'a';

                        if(percentDamaged > 0.4f)
                            color = '2';
                        if(percentDamaged > 0.60f)
                            color = 'e';
                        if(percentDamaged > 0.70f)
                            color = '6';
                        if(percentDamaged > 0.85f)
                            color = 'c';
                        if(percentDamaged > 0.95f)
                            color = '4';
                        String string = (minutes > 1 ? "\u00A7" + color + minutes + "\u00A7r" + " minutes" + (seconds >= 1 ? " " : "") : minutes == 1 ? "\u00A7" + color + minutes + "\u00A7r" + " minute" + (seconds >= 1 ? " " : "") : "") + (seconds > 1 ? "\u00A7" + color + seconds + "\u00A7r" + " seconds" : seconds == 1 ? "\u00A7" + color + seconds + "\u00A7r" + " second" : "");
                        MutableComponent itemText = (MutableComponent) Component.translatable(ModBlocks.SAGE_BURNING_PLATE.get().getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

                        tooltip.add(Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_1", string).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_2", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

//                        tooltip.add(Component.translatable("tooltip.hexerei.dried_sage_bundle").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                        tooltip.add(Component.translatable("tooltip.hexerei.shift_for_info"));
                    }

                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> LILY_PAD_ITEM = ITEMS.register("flowering_lily_pad",
            () -> new FloweringLilyPadItem(ModBlocks.LILY_PAD_BLOCK.get(),(new Item.Properties()).tab(ModItemGroup.HEXEREI_GROUP))); // ModBlocks.LILY_PAD_BLOCK.get(),


    public static final RegistryObject<FlowerOutputItem> BELLADONNA_FLOWERS = ITEMS.register("belladonna_flowers",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> BELLADONNA_BERRIES = ITEMS.register("belladonna_berries",
            () -> new FlowerOutputItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).fast().effect(new MobEffectInstance(MobEffects.POISON, 100, 2) , 100f).build()).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> MANDRAKE_FLOWERS = ITEMS.register("mandrake_flowers",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> MANDRAKE_ROOT = ITEMS.register("mandrake_root",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> MUGWORT_FLOWERS = ITEMS.register("mugwort_flowers",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> MUGWORT_LEAVES = ITEMS.register("mugwort_leaves",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> YELLOW_DOCK_FLOWERS = ITEMS.register("yellow_dock_flowers",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<FlowerOutputItem> YELLOW_DOCK_LEAVES = ITEMS.register("yellow_dock_leaves",
            () -> new FlowerOutputItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> DRIED_SAGE = ITEMS.register("dried_sage",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_BELLADONNA_FLOWERS = ITEMS.register("dried_belladonna_flowers",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_MANDRAKE_FLOWERS = ITEMS.register("dried_mandrake_flowers",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_MUGWORT_FLOWERS = ITEMS.register("dried_mugwort_flowers",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_MUGWORT_LEAVES = ITEMS.register("dried_mugwort_leaves",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_YELLOW_DOCK_FLOWERS = ITEMS.register("dried_yellow_dock_flowers",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> DRIED_YELLOW_DOCK_LEAVES = ITEMS.register("dried_yellow_dock_leaves",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<BlendItem> MINDFUL_TRANCE_BLEND = ITEMS.register("mindful_trance_blend",
            () -> new BlendItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));



    public static final RegistryObject<DowsingRodItem> DOWSING_ROD = ITEMS.register("dowsing_rod",
            () -> new DowsingRodItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> HERB_JAR = ITEMS.register("herb_jar",
            () -> new HerbJarItem(ModBlocks.HERB_JAR.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WILLOW_CHEST = ITEMS.register("willow_chest",
            () -> new ModChestItem(ModBlocks.WILLOW_CHEST.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> MAHOGANY_CHEST = ITEMS.register("mahogany_chest",
            () -> new ModChestItem(ModBlocks.MAHOGANY_CHEST.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WILLOW_SIGN = ITEMS.register("willow_sign",
            () -> new SignItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP), ModBlocks.WILLOW_SIGN.get(), ModBlocks.WILLOW_WALL_SIGN.get()));

    public static final RegistryObject<Item> MAHOGANY_SIGN = ITEMS.register("mahogany_sign",
            () -> new SignItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP), ModBlocks.MAHOGANY_SIGN.get(), ModBlocks.MAHOGANY_WALL_SIGN.get()));

    public static final RegistryObject<Item> POLISHED_WILLOW_SIGN = ITEMS.register("polished_willow_sign",
            () -> new SignItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP), ModBlocks.POLISHED_WILLOW_SIGN.get(), ModBlocks.POLISHED_WILLOW_WALL_SIGN.get()));

    public static final RegistryObject<Item> POLISHED_MAHOGANY_SIGN = ITEMS.register("polished_mahogany_sign",
            () -> new SignItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP), ModBlocks.POLISHED_MAHOGANY_SIGN.get(), ModBlocks.POLISHED_MAHOGANY_WALL_SIGN.get()));

    public static final RegistryObject<Item> MAHOGANY_WINDOW_PANE = ITEMS.register("mahogany_window_pane",
            () -> new BlockItem(ModBlocks.MAHOGANY_WINDOW_PANE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXED_MAHOGANY_WINDOW_PANE = ITEMS.register("waxed_mahogany_window_pane",
            () -> new BlockItem(ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WILLOW_WINDOW_PANE = ITEMS.register("willow_window_pane",
            () -> new BlockItem(ModBlocks.WILLOW_WINDOW_PANE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXED_WILLOW_WINDOW_PANE = ITEMS.register("waxed_willow_window_pane",
            () -> new BlockItem(ModBlocks.WAXED_WILLOW_WINDOW_PANE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> MAHOGANY_WINDOW = ITEMS.register("mahogany_window",
            () -> new BlockItem(ModBlocks.MAHOGANY_WINDOW.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WILLOW_WINDOW = ITEMS.register("willow_window",
            () -> new BlockItem(ModBlocks.WILLOW_WINDOW.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXED_MAHOGANY_WINDOW = ITEMS.register("waxed_mahogany_window",
            () -> new BlockItem(ModBlocks.WAXED_MAHOGANY_WINDOW.get(), new Item.Properties()));

    public static final RegistryObject<Item> WAXED_WILLOW_WINDOW = ITEMS.register("waxed_willow_window",
            () -> new BlockItem(ModBlocks.WAXED_WILLOW_WINDOW.get(), new Item.Properties()));

    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_ORNATE = ITEMS.register("infused_fabric_carpet_ornate",
            () -> new BlockItem(ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }

                @org.jetbrains.annotations.Nullable
                @Override
                protected BlockState getPlacementState(BlockPlaceContext pContext) {
                    if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof SlabBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.SLAB_TYPE) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM)
                        return ModBlocks.CARPET_SLAB.get().defaultBlockState();
                    if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof StairBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.HALF) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.HALF) == Half.BOTTOM)
                        return ModBlocks.CARPET_STAIRS.get().getStateForPlacement(pContext);
                    return super.getPlacementState(pContext);
                }
            });

    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_ORNATE = ITEMS.register("infused_fabric_block_ornate",
            () -> new BlockItem(ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET = ITEMS.register("infused_fabric_carpet",
            () -> new BlockItem(ModBlocks.INFUSED_FABRIC_CARPET.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }



                @org.jetbrains.annotations.Nullable
                @Override
                protected BlockState getPlacementState(BlockPlaceContext pContext) {
                    if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof SlabBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.SLAB_TYPE) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM)
                        return ModBlocks.CARPET_SLAB.get().defaultBlockState();
                    if(pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof StairBlock && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).hasProperty(BlockStateProperties.HALF) && pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getValue(BlockStateProperties.HALF) == Half.BOTTOM)
                        return ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get().getStateForPlacement(pContext);
                    return super.getPlacementState(pContext);
                }

            });

    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET = ITEMS.register("waxed_infused_fabric_carpet",
            () -> new BlockItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get(), new Item.Properties()));

    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK = ITEMS.register("infused_fabric_block",
            () -> new BlockItem(ModBlocks.INFUSED_FABRIC_BLOCK.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK = ITEMS.register("waxed_infused_fabric_block",
            () -> new BlockItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_WHITE = ITEMS.register("infused_fabric_carpet_dyed_white",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_ORANGE = ITEMS.register("infused_fabric_carpet_dyed_orange",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_MAGENTA = ITEMS.register("infused_fabric_carpet_dyed_magenta",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE = ITEMS.register("infused_fabric_carpet_dyed_light_blue",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_YELLOW = ITEMS.register("infused_fabric_carpet_dyed_yellow",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_LIME = ITEMS.register("infused_fabric_carpet_dyed_lime",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_PINK = ITEMS.register("infused_fabric_carpet_dyed_pink",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_GRAY = ITEMS.register("infused_fabric_carpet_dyed_gray",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY = ITEMS.register("infused_fabric_carpet_dyed_light_gray",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_CYAN = ITEMS.register("infused_fabric_carpet_dyed_cyan",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_PURPLE = ITEMS.register("infused_fabric_carpet_dyed_purple",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_BLUE = ITEMS.register("infused_fabric_carpet_dyed_blue",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_BROWN = ITEMS.register("infused_fabric_carpet_dyed_brown",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_GREEN = ITEMS.register("infused_fabric_carpet_dyed_green",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_RED = ITEMS.register("infused_fabric_carpet_dyed_red",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_CARPET_DYED_BLACK = ITEMS.register("infused_fabric_carpet_dyed_black",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_WHITE = ITEMS.register("waxed_infused_fabric_carpet_dyed_white",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_WHITE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_ORANGE = ITEMS.register("waxed_infused_fabric_carpet_dyed_orange",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_ORANGE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_MAGENTA = ITEMS.register("waxed_infused_fabric_carpet_dyed_magenta",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE = ITEMS.register("waxed_infused_fabric_carpet_dyed_light_blue",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_YELLOW = ITEMS.register("waxed_infused_fabric_carpet_dyed_yellow",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_YELLOW.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_LIME = ITEMS.register("waxed_infused_fabric_carpet_dyed_lime",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIME.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_PINK = ITEMS.register("waxed_infused_fabric_carpet_dyed_pink",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PINK.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_GRAY = ITEMS.register("waxed_infused_fabric_carpet_dyed_gray",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GRAY.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY = ITEMS.register("waxed_infused_fabric_carpet_dyed_light_gray",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_CYAN = ITEMS.register("waxed_infused_fabric_carpet_dyed_cyan",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_CYAN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_PURPLE = ITEMS.register("waxed_infused_fabric_carpet_dyed_purple",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PURPLE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_BLUE = ITEMS.register("waxed_infused_fabric_carpet_dyed_blue",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLUE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_BROWN = ITEMS.register("waxed_infused_fabric_carpet_dyed_brown",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BROWN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_GREEN = ITEMS.register("waxed_infused_fabric_carpet_dyed_green",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GREEN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_RED = ITEMS.register("waxed_infused_fabric_carpet_dyed_red",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_RED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_CARPET_DYED_BLACK = ITEMS.register("waxed_infused_fabric_carpet_dyed_black",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLACK.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_WHITE = ITEMS.register("infused_fabric_block_dyed_white",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_WHITE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_ORANGE = ITEMS.register("infused_fabric_block_dyed_orange",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_MAGENTA = ITEMS.register("infused_fabric_block_dyed_magenta",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE = ITEMS.register("infused_fabric_block_dyed_light_blue",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_YELLOW = ITEMS.register("infused_fabric_block_dyed_yellow",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_LIME = ITEMS.register("infused_fabric_block_dyed_lime",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIME.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_PINK = ITEMS.register("infused_fabric_block_dyed_pink",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PINK.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_GRAY = ITEMS.register("infused_fabric_block_dyed_gray",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GRAY.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY = ITEMS.register("infused_fabric_block_dyed_light_gray",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_CYAN = ITEMS.register("infused_fabric_block_dyed_cyan",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_CYAN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_PURPLE = ITEMS.register("infused_fabric_block_dyed_purple",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_BLUE = ITEMS.register("infused_fabric_block_dyed_blue",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLUE.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_BROWN = ITEMS.register("infused_fabric_block_dyed_brown",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BROWN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_GREEN = ITEMS.register("infused_fabric_block_dyed_green",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GREEN.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_RED = ITEMS.register("infused_fabric_block_dyed_red",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_RED.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
    public static final RegistryObject<Item> INFUSED_FABRIC_BLOCK_DYED_BLACK = ITEMS.register("infused_fabric_block_dyed_black",
            () -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLACK.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_WHITE = ITEMS.register("waxed_infused_fabric_block_dyed_white",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_WHITE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_ORANGE = ITEMS.register("waxed_infused_fabric_block_dyed_orange",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_MAGENTA = ITEMS.register("waxed_infused_fabric_block_dyed_magenta",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE = ITEMS.register("waxed_infused_fabric_block_dyed_light_blue",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_YELLOW = ITEMS.register("waxed_infused_fabric_block_dyed_yellow",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_LIME = ITEMS.register("waxed_infused_fabric_block_dyed_lime",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIME.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_PINK = ITEMS.register("waxed_infused_fabric_block_dyed_pink",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PINK.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_GRAY = ITEMS.register("waxed_infused_fabric_block_dyed_gray",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GRAY.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY = ITEMS.register("waxed_infused_fabric_block_dyed_light_gray",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_CYAN = ITEMS.register("waxed_infused_fabric_block_dyed_cyan",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_CYAN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_PURPLE = ITEMS.register("waxed_infused_fabric_block_dyed_purple",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_BLUE = ITEMS.register("waxed_infused_fabric_block_dyed_blue",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLUE.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_BROWN = ITEMS.register("waxed_infused_fabric_block_dyed_brown",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BROWN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_GREEN = ITEMS.register("waxed_infused_fabric_block_dyed_green",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GREEN.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_RED = ITEMS.register("waxed_infused_fabric_block_dyed_red",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_RED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_INFUSED_FABRIC_BLOCK_DYED_BLACK = ITEMS.register("waxed_infused_fabric_block_dyed_black",
            () -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLACK.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> COFFER = ITEMS.register("coffer",
            () -> new CofferItem(ModBlocks.COFFER.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> MIXING_CAULDRON = ITEMS.register("mixing_cauldron",
            () -> new MixingCauldronItem(ModBlocks.MIXING_CAULDRON.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE = ITEMS.register("candle",
            () -> new CandleItem(ModBlocks.CANDLE.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_BLUE = ITEMS.register("candle_blue",
            () -> new CandleItem(ModBlocks.CANDLE_BLUE.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_BLACK = ITEMS.register("candle_black",
            () -> new CandleItem(ModBlocks.CANDLE_BLACK.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_LIME = ITEMS.register("candle_lime",
            () -> new CandleItem(ModBlocks.CANDLE_LIME.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_ORANGE = ITEMS.register("candle_orange",
            () -> new CandleItem(ModBlocks.CANDLE_ORANGE.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_PINK = ITEMS.register("candle_pink",
            () -> new CandleItem(ModBlocks.CANDLE_PINK.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_PURPLE = ITEMS.register("candle_purple",
            () -> new CandleItem(ModBlocks.CANDLE_PURPLE.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_RED = ITEMS.register("candle_red",
            () -> new CandleItem(ModBlocks.CANDLE_RED.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_CYAN = ITEMS.register("candle_cyan",
            () -> new CandleItem(ModBlocks.CANDLE_CYAN.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> CANDLE_YELLOW = ITEMS.register("candle_yellow",
            () -> new CandleItem(ModBlocks.CANDLE_YELLOW.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> SEED_MIXTURE = ITEMS.register("seed_mixture",
            () -> new SeedMixtureItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> CROW_FLUTE = ITEMS.register("crow_flute",
            () -> new CrowFluteItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

//    public static final RegistryObject<SpawnEggItem> CROW_SPAWN_EGG = ITEMS.register("crow_spawn_egg", () ->
//            new SpawnEggItem((EntityType<CrowEntity>)EntityType.Builder.<CrowEntity>of(CrowEntity::new, MobCategory.CREATURE)
//            .build(Hexerei.MOD_ID + ":" + "crow"), 0x161616, 0x333333, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<ForgeSpawnEggItem> CROW_SPAWN_EGG = ITEMS.register("crow_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.CROW,0x161616, 0x333333,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> CROW_ANKH_AMULET = ITEMS.register("crow_ankh_amulet",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    if(Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                        tooltip.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    } else {
                        tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    }
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });


    public static final RegistryObject<Item> CROW_BLANK_AMULET = ITEMS.register("crow_blank_amulet",
            () -> new CrowAmuletItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).stacksTo(1)));

    public static final RegistryObject<Item> CROW_BLANK_AMULET_TRINKET = ITEMS.register("crow_blank_amulet_trinket",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CROW_BLANK_AMULET_TRINKET_FRAME = ITEMS.register("crow_blank_amulet_trinket_frame",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<GlassesItem> READING_GLASSES = ITEMS.register("reading_glasses",
            () -> new GlassesItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    // EGG ITEMS

//    public static final RegistryObject<ModSpawnEggItem> CROW_SPAWN_EGG = ITEMS.register("crow_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntityTypes.CROW, 0x161616, 0x333333,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

//    public static final RegistryObject<ModSpawnEggItem> PIGEON_SPAWN_EGG = ITEMS.register("pigeon_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntityTypes.PIGEON, 0x879995, 0x576ABC,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    // ARMOR ITEMS
//    public static final RegistryObject<Item> ORC_HELMET = ITEMS.register("orc_helmet",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.HEAD,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> ORC_CHESTPLATE = ITEMS.register("orc_chestplate",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.CHEST,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> ORC_LEGGINGS = ITEMS.register("orc_leggings",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> ORC_BOOTS = ITEMS.register("orc_boots",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.FEET,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

//    public static final RegistryObject<Item> DRUID_HELMET = ITEMS.register("druid_helmet",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.HEAD,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> DRUID_CHESTPLATE = ITEMS.register("druid_chestplate",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.CHEST,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> DRUID_LEGGINGS = ITEMS.register("druid_leggings",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
//
//    public static final RegistryObject<Item> DRUID_BOOTS = ITEMS.register("druid_boots",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.FEET,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WITCH_HELMET = ITEMS.register("witch_helmet",
            () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WITCH_CHESTPLATE = ITEMS.register("witch_chestplate",
            () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

//    public static final RegistryObject<Item> WITCH_LEGGINGS = ITEMS.register("witch_leggings",
//            () -> new WitchArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> WITCH_BOOTS = ITEMS.register("witch_boots",
            () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, EquipmentSlot.FEET,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));


    public static final RegistryObject<Item> MUSHROOM_WITCH_HAT = ITEMS.register("mushroom_witch_hat",
            () -> new MushroomWitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));





    public static final RegistryObject<Item> WILLOW_CONNECTED = ITEMS.register("willow_connected",
            () -> new BlockItem(ModBlocks.WILLOW_CONNECTED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> POLISHED_WILLOW_CONNECTED = ITEMS.register("polished_willow_connected",
            () -> new BlockItem(ModBlocks.POLISHED_WILLOW_CONNECTED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });


    public static final RegistryObject<Item> POLISHED_WILLOW_PILLAR = ITEMS.register("polished_willow_pillar",
            () -> new BlockItem(ModBlocks.POLISHED_WILLOW_PILLAR.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> POLISHED_WILLOW_LAYERED = ITEMS.register("polished_willow_layered",
            () -> new BlockItem(ModBlocks.POLISHED_WILLOW_LAYERED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });


    public static final RegistryObject<Item> WAXED_POLISHED_MAHOGANY_CONNECTED = ITEMS.register("waxed_polished_mahogany_connected",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WAXED_POLISHED_MAHOGANY_PILLAR = ITEMS.register("waxed_polished_mahogany_pillar",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_POLISHED_MAHOGANY_LAYERED = ITEMS.register("waxed_polished_mahogany_layered",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WAXED_MAHOGANY_CONNECTED = ITEMS.register("waxed_mahogany_connected",
            () -> new BlockItem(ModBlocks.WAXED_MAHOGANY_CONNECTED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });




    public static final RegistryObject<Item> WAXED_POLISHED_WILLOW_CONNECTED = ITEMS.register("waxed_polished_willow_connected",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WAXED_POLISHED_WILLOW_PILLAR = ITEMS.register("waxed_polished_willow_pillar",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> WAXED_POLISHED_WILLOW_LAYERED = ITEMS.register("waxed_polished_willow_layered",
            () -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WAXED_WILLOW_CONNECTED = ITEMS.register("waxed_willow_connected",
            () -> new BlockItem(ModBlocks.WAXED_WILLOW_CONNECTED.get(), new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.waxed_connected_texture_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
//    public static final RegistryObject<Item> WAXED_POLISHED_WILLOW_LAYERED = ITEMS.register("waxed_polished_willow_layered",
//            () -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get(), new Item.Properties()
//                    .tab(ModItemGroup.HEXEREI_GROUP)){
//                @Override
//                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
//                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                    super.appendHoverText(stack, world, tooltip, flagIn);
//                }
//            });




    public static final RegistryObject<Item> MAHOGANY_CONNECTED = ITEMS.register("mahogany_connected",
            () -> new BlockItem(ModBlocks.MAHOGANY_CONNECTED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> POLISHED_MAHOGANY_CONNECTED = ITEMS.register("polished_mahogany_connected",
            () -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> POLISHED_MAHOGANY_PILLAR = ITEMS.register("polished_mahogany_pillar",
            () -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });
    public static final RegistryObject<Item> POLISHED_MAHOGANY_LAYERED = ITEMS.register("polished_mahogany_layered",
            () -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), new Item.Properties()
                    .tab(ModItemGroup.HEXEREI_GROUP)){
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
                    tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
