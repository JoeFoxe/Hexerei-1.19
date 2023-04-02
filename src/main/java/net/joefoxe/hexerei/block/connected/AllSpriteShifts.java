package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class AllSpriteShifts {


    public static final CTSpriteShiftEntry MAHOGANY_CONNECTED = omni("mahogany_connected", "mahogany_planks_connected");
    public static final CTSpriteShiftEntry POLISHED_MAHOGANY_CONNECTED = omni("polished_mahogany");
    public static final CTSpriteShiftEntry POLISHED_MAHOGANY_PILLAR_SIDE = pillar("polished_mahogany_pillar");
    public static final CTSpriteShiftEntry POLISHED_MAHOGANY_PILLAR_TOP = omni("polished_mahogany_cap", "polished_mahogany_connected");
    public static final CTSpriteShiftEntry POLISHED_MAHOGANY_LAYERED = layered("polished_mahogany_layered");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_MAHOGANY = omniRandom("polished_smooth_mahogany");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_MAHOGANY_GLASS = omni("polished_smooth_mahogany_glass","polished_smooth_mahogany_glass_pane_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS = omni("waxed_polished_smooth_mahogany_glass","polished_smooth_mahogany_glass_pane_connected");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WILLOW_GLASS = omni("polished_smooth_willow_glass","polished_smooth_willow_glass_pane_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WILLOW_GLASS = omni("waxed_polished_smooth_willow_glass","polished_smooth_willow_glass_pane_connected");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WITCH_HAZEL_GLASS = omni("polished_smooth_witch_hazel_glass","polished_smooth_witch_hazel_glass_pane_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS = omni("waxed_polished_smooth_witch_hazel_glass","polished_smooth_witch_hazel_glass_pane_connected");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_MAHOGANY_GLASS_PANE = omni("polished_smooth_mahogany_glass_pane");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS_PANE = omni("waxed_polished_smooth_mahogany_glass_pane","polished_smooth_mahogany_glass_pane_connected");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WILLOW_GLASS_PANE = omni("polished_smooth_willow_glass_pane");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WILLOW_GLASS_PANE = omni("waxed_polished_smooth_willow_glass_pane","polished_smooth_willow_glass_pane_connected");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE = omni("polished_smooth_witch_hazel_glass_pane");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE = omni("waxed_polished_smooth_witch_hazel_glass_pane","polished_smooth_witch_hazel_glass_pane_connected");
    public static final CTSpriteShiftEntry WAXED_MAHOGANY_CONNECTED = omni("waxed_mahogany_connected", "mahogany_planks_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_MAHOGANY_CONNECTED = omni("waxed_polished_mahogany", "polished_mahogany_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_MAHOGANY_PILLAR_SIDE = pillar("waxed_polished_mahogany_pillar", "polished_mahogany_pillar_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_MAHOGANY_PILLAR_TOP = omni("waxed_polished_mahogany_cap", "polished_mahogany_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_MAHOGANY_LAYERED = layered("waxed_polished_mahogany_layered", "polished_mahogany_layered_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_MAHOGANY = omniRandom("waxed_polished_smooth_mahogany", "polished_smooth_mahogany_connected");
    public static final CTSpriteShiftEntry INFUSED_FABRIC_CARPET = omniRandom("infused_fabric");
    public static final CTSpriteShiftEntry INFUSED_FABRIC_CARPET_ORNATE = omniRandom("infused_fabric_ornate");
    public static final CTSpriteShiftEntry WAXED_INFUSED_FABRIC_CARPET = omniRandom("waxed_infused_fabric","infused_fabric_connected");
    public static final CTSpriteShiftEntry INFUSED_FABRIC_CARPET_DYED = omniRandom("infused_fabric_dyed");
    public static final CTSpriteShiftEntry WAXED_INFUSED_FABRIC_CARPET_DYED = omniRandom("waxed_infused_fabric_dyed", "infused_fabric_dyed_connected");

    public static final CTSpriteShiftEntry WILLOW_CONNECTED = omni("willow_connected", "willow_planks_connected");
    public static final CTSpriteShiftEntry POLISHED_WILLOW_CONNECTED = omni("polished_willow");
    public static final CTSpriteShiftEntry POLISHED_WILLOW_PILLAR_SIDE = pillar("polished_willow_pillar");
    public static final CTSpriteShiftEntry POLISHED_WILLOW_PILLAR_TOP = omni("polished_willow_cap", "polished_willow_connected");
    public static final CTSpriteShiftEntry POLISHED_WILLOW_LAYERED = layered("polished_willow_layered");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WILLOW = omniRandom("polished_smooth_willow");
    public static final CTSpriteShiftEntry WAXED_WILLOW_CONNECTED = omni("waxed_willow_connected", "willow_planks_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WILLOW_CONNECTED = omni("waxed_polished_willow", "polished_willow_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WILLOW_PILLAR_SIDE = pillar("waxed_polished_willow_pillar", "polished_willow_pillar_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WILLOW_PILLAR_TOP = omni("waxed_polished_willow_cap", "polished_willow_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WILLOW_LAYERED = layered("waxed_polished_willow_layered", "polished_willow_layered_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WILLOW = omniRandom("waxed_polished_smooth_willow", "polished_smooth_willow_connected");

    public static final CTSpriteShiftEntry WITCH_HAZEL_CONNECTED = omni("witch_hazel_connected", "witch_hazel_planks_connected");
    public static final CTSpriteShiftEntry POLISHED_WITCH_HAZEL_CONNECTED = omni("polished_witch_hazel");
    public static final CTSpriteShiftEntry POLISHED_WITCH_HAZEL_PILLAR_SIDE = pillar("polished_witch_hazel_pillar");
    public static final CTSpriteShiftEntry POLISHED_WITCH_HAZEL_PILLAR_TOP = omni("polished_witch_hazel_cap", "polished_witch_hazel_connected");
    public static final CTSpriteShiftEntry POLISHED_WITCH_HAZEL_LAYERED = layered("polished_witch_hazel_layered");
    public static final CTSpriteShiftEntry POLISHED_SMOOTH_WITCH_HAZEL = omniRandom("polished_smooth_witch_hazel");
    public static final CTSpriteShiftEntry WAXED_WITCH_HAZEL_CONNECTED = omni("waxed_witch_hazel_connected", "witch_hazel_planks_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WITCH_HAZEL_CONNECTED = omni("waxed_polished_witch_hazel", "polished_witch_hazel_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WITCH_HAZEL_PILLAR_SIDE = pillar("waxed_polished_witch_hazel_pillar", "polished_witch_hazel_pillar_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WITCH_HAZEL_PILLAR_TOP = omni("waxed_polished_witch_hazel_cap", "polished_witch_hazel_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_WITCH_HAZEL_LAYERED = layered("waxed_polished_witch_hazel_layered", "polished_witch_hazel_layered_connected");
    public static final CTSpriteShiftEntry WAXED_POLISHED_SMOOTH_WITCH_HAZEL = omniRandom("waxed_polished_smooth_witch_hazel", "polished_smooth_witch_hazel_connected");

    public static final CTSpriteShiftEntry STONE_WINDOW_CONNECTED = omni("stone_window", "stone_window_connected");
    public static final CTSpriteShiftEntry STONE_WINDOW_PANE_CONNECTED = omni("stone_window_pane", "stone_window_connected");
    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }
    private static CTSpriteShiftEntry omni(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName));
    }

    private static CTSpriteShiftEntry omniRandom(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL_RANDOM_7_50P, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName));
    }
    private static CTSpriteShiftEntry omniRandom(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL_RANDOM_7_50P, name);
    }
    private static CTSpriteShiftEntry pillar(String name) {
        return getCT(AllCTTypes.RECTANGLE, name);
    }

    private static CTSpriteShiftEntry pillar(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName));
    }
    private static CTSpriteShiftEntry layered(String name) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, name);
    }

    private static CTSpriteShiftEntry layered(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.HORIZONTAL_KRYPPERS, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName));
    }

    private static CTSpriteShiftEntry type(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, new ResourceLocation(Hexerei.MOD_ID,"block/" + blockTextureName), new ResourceLocation(Hexerei.MOD_ID, "block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

}
