package net.joefoxe.hexerei.integration;

import net.minecraftforge.fml.ModList;

public class HexereiModNameTooltipCompat {
    public static boolean LOADED;

    public static void init() {
        LOADED = ModList.get().isLoaded("modnametooltip");
    }
}
