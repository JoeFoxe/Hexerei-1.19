package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.recipes.BookOfShadowsRecipe;
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.joefoxe.hexerei.data.recipes.KeychainRecipe;
import net.joefoxe.hexerei.data.recipes.KeychainUndoRecipe;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.custom.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class ModItems {

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


    public static final RegistryObject<Item> SMALL_SATCHEL = ITEMS.register("small_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

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
                    public static void registerFluteColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, SMALL_SATCHEL.get());

                    }
                }
            });

    public static final RegistryObject<Item> MEDIUM_SATCHEL = ITEMS.register("medium_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

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
                    public static void registerFluteColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, MEDIUM_SATCHEL.get());

                    }
                }

            });

    public static final RegistryObject<Item> LARGE_SATCHEL = ITEMS.register("large_satchel",
            () -> new SatchelItem(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

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
                    public static void registerFluteColors(RegisterColorHandlersEvent.Item event)
                    {
                        SatchelItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, LARGE_SATCHEL.get());

                    }
                }
            });

    public static final RegistryObject<Item> GOLD_RINGS = ITEMS.register("gold_rings",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> BROOM_NETHERITE_TIP = ITEMS.register("broom_netherite_tip",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(HexConfig.BROOM_NETHERITE_TIP_DURABILITY.get())){

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
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(HexConfig.BROOM_WATERPROOF_TIP_DURABILITY.get())){

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
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(HexConfig.BROOM_BRUSH_DURABILITY.get())){

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

                    tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    super.appendHoverText(stack, world, tooltip, flagIn);
                }
            });

    public static final RegistryObject<Item> WET_HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("wet_herb_enhanced_broom_brush",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("herb_enhanced_broom_brush",
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(HexConfig.ENHANCED_BROOM_BRUSH_DURABILITY.get())){

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
            () -> new BucketItem(() -> ModFluids.BLOOD_FLUID.get(), new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> TALLOW_BUCKET = ITEMS.register("tallow_bucket",
            () -> new BucketItem(() -> ModFluids.TALLOW_FLUID.get(), new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

    public static final RegistryObject<Item> QUICKSILVER_BUCKET = ITEMS.register("quicksilver_bucket",
            () -> new BucketItem(() -> ModFluids.QUICKSILVER_FLUID.get(), new Item.Properties().stacksTo(1).tab(ModItemGroup.HEXEREI_GROUP)));

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
            () -> new Item(new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP).durability(HexConfig.SAGE_BUNDLE_DURATION.get())){
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

    public static final RegistryObject<Item> COFFER = ITEMS.register("coffer",
            () -> new CofferItem(ModBlocks.COFFER.get(),new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)){

                @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
                static class ColorRegisterHandler
                {
                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public static void registerFluteColors(RegisterColorHandlersEvent.Item event)
                    {
                        CofferItem.ItemHandlerConsumer items = event.getItemColors()::register;
                        // s = stack, t = tint-layer
                        items.register((s, t) -> t == 1 ? getColorValue(CofferItem.getDyeColorNamed(s), s) : -1, COFFER.get());

                    }
                }
            });


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


    public static void registerRecipeSerializers(RegisterEvent evt) {
            //Use Deferred registry for serializer
        /*
//        evt.getRegistry().register(CofferDyeingRecipe.SERIALIZER.setRegistryName(Hexerei.MOD_ID, "coffer_dyeing"));
        evt.getRegistry().register(CrowFluteRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Hexerei.MOD_ID, "crow_flute_dye")));
        evt.getRegistry().register(BookOfShadowsRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Hexerei.MOD_ID, "book_of_shadows_dye")));
        evt.getRegistry().register(KeychainRecipe.SERIALIZER.setRegistryName(Hexerei.MOD_ID, "keychain_apply"));
        evt.getRegistry().register(KeychainUndoRecipe.SERIALIZER.setRegistryName(Hexerei.MOD_ID, "keychain_undo"));
         */
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
