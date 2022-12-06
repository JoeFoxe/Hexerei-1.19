package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroup {

    public static final CreativeModeTab HEXEREI_GROUP = new CreativeModeTab("hexereiModTab")
    {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.MIXING_CAULDRON.get().asItem());
        }

    };
}
