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
    public static ForgeConfigSpec.ConfigValue<Integer> CROW_PICKPOCKET_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> COFFER_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<Integer> WILLOW_SWAMP_RARITY;


    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FONT_LIST;

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
        builder.push("Crow Pickpocket Cooldown");
        CROW_PICKPOCKET_COOLDOWN = builder
                .comment("time (in ticks) for crow being able to pickpocket again (base 1 minute 30 seconds)")
                .translation("hexerei.config.crow_pickpocket_cooldown")
                .define("crow_pickpocket_cooldown", 1800);
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

        builder.push("Biome Generation");
        WILLOW_SWAMP_RARITY = builder.comment("rarity of the willow swamp biome, 0 to disable").defineInRange("willow_swamp_rarity", 2, 0, Integer.MAX_VALUE);
        builder.pop();
        COMMON_CONFIG = builder.build();


        builder = new ForgeConfigSpec.Builder();

        //_____________  C L I E N T     C O N F I G   _____________//

        builder.push("Settings");
        builder.pop();

        builder.push("List of Extra Fonts");
        FONT_LIST = builder
                .comment("list of fonts that can be used, mainly for the book of shadows")
                .translation("hexerei.config.font_list")
                .defineList("font_list", List.of("minecraft:default", "hexerei:fancy", "hexerei:bloody", "hexerei:earth", "hexerei:seattle", "hexerei:medieval", "hexerei:augusta"),(o) -> true);
        builder.pop();


        CLIENT_CONFIG = builder.build();


    }
}
