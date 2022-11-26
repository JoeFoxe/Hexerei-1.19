package net.joefoxe.hexerei.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModWoodType {
    public static WoodType WILLOW = WoodType.create("hexerei_willow");
    public static WoodType POLISHED_WILLOW = WoodType.create("hexerei_polished_willow");
    public static WoodType MAHOGANY = WoodType.create("hexerei_mahogany");
    public static WoodType POLISHED_MAHOGANY = WoodType.create("hexerei_polished_mahogany");
    public static List<WoodType> woodTypes = List.of(WILLOW, MAHOGANY, POLISHED_WILLOW, POLISHED_MAHOGANY);
}