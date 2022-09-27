package net.joefoxe.hexerei.item.custom;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.block.Block;

public class DyeableBlockItem extends BlockItem implements DyeableLeatherItem {
    public DyeableBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
}
