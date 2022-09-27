package net.joefoxe.hexerei.block.connected;

import net.minecraft.world.item.DyeColor;

public interface CTDyable {

    public default DyeColor getDyeColor(){
        return DyeColor.BLACK;
    }
}
