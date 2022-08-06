package net.joefoxe.hexerei.config;

import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class HexConfig {

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue JARS_ONLY_HOLD_HERBS;


    public static ForgeConfigSpec.ConfigValue<Integer> SAGE_BURNING_PLATE_RANGE;

    public static ForgeConfigSpec.ConfigValue<Integer> SAGE_BUNDLE_DURATION;

    public static ForgeConfigSpec.ConfigValue<Integer> WITCH_HUT_SPACING;

    public static ForgeConfigSpec.ConfigValue<Integer> WITCH_HUT_SEPARATION;

    public static ForgeConfigSpec.ConfigValue<Integer> DARK_COVEN_SPACING;

    public static ForgeConfigSpec.ConfigValue<Integer> DARK_COVEN_SEPARATION;

    public static ForgeConfigSpec.ConfigValue<Integer> BROOM_BRUSH_DURABILITY;

    public static ForgeConfigSpec.ConfigValue<Integer> ENHANCED_BROOM_BRUSH_DURABILITY;

    public static ForgeConfigSpec.ConfigValue<Integer> BROOM_NETHERITE_TIP_DURABILITY;

    public static ForgeConfigSpec.ConfigValue<Integer> BROOM_WATERPROOF_TIP_DURABILITY;

    public static ForgeConfigSpec.ConfigValue<Integer> CROW_PICKPOCKET_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<Integer> CROW_SPAWN_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<Integer> CROW_SPAWN_MIN_COUNT;
    public static ForgeConfigSpec.ConfigValue<Integer> CROW_SPAWN_MAX_COUNT;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> COFFER_BLACKLIST;



    public static ForgeConfigSpec.BooleanValue FANCY_FONT_IN_BOOK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        //_____________  S E R V E R     C O N F I G   _____________//

        builder.push("Settings");
        builder.pop();
        builder.push("Herb Jar Settings");
        JARS_ONLY_HOLD_HERBS = builder
                .comment("Disabling allows jars to hold any item")
                .translation("hexerei.config.jars_only_hold_herbs")
                .define("jars_only_hold_herbs", true);
        builder.pop();
        builder.push("Sage Burning Plate Settings");
        SAGE_BURNING_PLATE_RANGE = builder
                .comment("Range of the Sage Burning Plate, setting to 0 will disable completely")
                .translation("hexerei.config.spawn_disable_range")
                .define("spawn_disable_range", 48);
        builder.pop();
        builder.push("Sage Bundle Settings");
        SAGE_BUNDLE_DURATION = builder
                .comment("Duration of how long each bundle will last while burning")
                .translation("hexerei.config.sage_bundle_duration")
                .define("sage_bundle_duration_in_seconds", 3600);
        builder.pop();
        builder.push("");
        builder.pop();
        builder.push("Structures");
        builder.pop();
        builder.push("Witch Hut Spacing");
        WITCH_HUT_SPACING = builder
                .comment("spacing between witch huts, lower both spacing and separation to increase spawn rates")
                .translation("hexerei.config.witch_hut_spacing")
                .define("witch_hut_spacing", 20);
        builder.pop();
        builder.push("Witch Hut Separation");
        WITCH_HUT_SEPARATION = builder
                .comment("separation of the witch huts, lower both spacing and separation to increase spawn rates")
                .translation("hexerei.config.witch_hut_separation")
                .define("witch_hut_separation", 8);
        builder.pop();
        builder.push("Dark Coven Spacing");
        DARK_COVEN_SPACING = builder
                .comment("spacing between dark covens, lower both spacing and separation to increase spawn rates")
                .translation("hexerei.config.dark_coven_spacing")
                .define("dark_coven_spacing", 29);
        builder.pop();
        builder.push("Dark Coven Separation");
        DARK_COVEN_SEPARATION = builder
                .comment("separation of the dark covens, lower both spacing and separation to increase spawn rates")
                .translation("hexerei.config.dark_coven_separation")
                .define("dark_coven_separation", 11);
        builder.pop();
        builder.push("Broom Brush Durability");
        BROOM_BRUSH_DURABILITY = builder
                .comment("100 durability will be about 16 minutes of flight time")
                .translation("hexerei.config.broom_brush_durability")
                .define("broom_brush_durability", 100);
        builder.pop();
        builder.push("Enhanced Broom Brush Durability");
        ENHANCED_BROOM_BRUSH_DURABILITY = builder
                .comment("200 durability will be about 32 minutes of flight time")
                .translation("hexerei.config.enhanced_broom_brush_durability")
                .define("enhanced_broom_brush_durability", 200);
        builder.pop();
        builder.push("Broom Netherite Tip Durability");
        BROOM_NETHERITE_TIP_DURABILITY = builder
                .comment("1 second of active time per 1 durability")
                .translation("hexerei.config.broom_netherite_tip_sdurability")
                .define("broom_netherite_tip_durability", 200);
        builder.pop();
        builder.push("Broom Waterproof Tip Durability");
        BROOM_WATERPROOF_TIP_DURABILITY = builder
                .comment("1 second of active time per 1 durability")
                .translation("hexerei.config.broom_waterproof_tip_durability")
                .define("broom_waterproof_tip_durability", 800);
        builder.pop();
        builder.push("Crow Pickpocket Cooldown");
        CROW_PICKPOCKET_COOLDOWN = builder
                .comment("time (in ticks) for crow being able to pickpocket again (base 1 minute 30 seconds)")
                .translation("hexerei.config.crow_pickpocket_cooldown")
                .define("crow_pickpocket_cooldown", 1800);
        builder.pop();
        builder.push("Crow Generation Weight");
        CROW_SPAWN_WEIGHT = builder
                .comment("weight of the crow generation (how often it'll spawn)")
                .translation("hexerei.config.crow_generation_weight")
                .define("crow_generation_weight", 20);
        builder.pop();
        builder.push("Crow Generation Min Count");
        CROW_SPAWN_MIN_COUNT = builder
                .comment("min number of crows spawning per group")
                .translation("hexerei.config.crow_generation_min_count")
                .define("crow_generation_min_count", 2);
        builder.pop();
        builder.push("Crow Generation Max Count");
        CROW_SPAWN_MAX_COUNT = builder
                .comment("max number of crows spawning per group")
                .translation("hexerei.config.crow_generation_max_count")
                .define("crow_generation_max_count", 5);
        builder.pop();

        builder.push("Coffer Item Blacklist");
        ArrayList<String> list = new ArrayList<>();
        list.add("minecraft:shulker_box");
        for(DyeColor color : DyeColor.values()){
            String str = "minecraft:"+ color.getName() +"_shulker_box";
            list.add(str);
        }
        list.add("hexerei:coffer");
        COFFER_BLACKLIST = builder
                .comment("blacklists items from being placed inside of coffers")
                .translation("hexerei.config.coffer_blacklist")
                .defineList("coffer_blacklist", list,(o) -> true);
        builder.pop();

        COMMON_CONFIG = builder.build();


        builder = new ForgeConfigSpec.Builder();

        //_____________  C L I E N T     C O N F I G   _____________//

        builder.push("Settings");
        builder.pop();
        builder.push("Fancy font in book");
        FANCY_FONT_IN_BOOK = builder
                .comment("Enabling this allows fancy cursive font for the book (english only not fully implemented)")
                .translation("hexerei.config.fancy_font_in_book")
                .define("fancy_font_in_book", false);
        builder.pop();


        CLIENT_CONFIG = builder.build();


    }
}
