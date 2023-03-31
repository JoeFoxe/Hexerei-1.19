package net.joefoxe.hexerei.block;

import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;

public class CustomVanillaItemDispenseBehavior extends OptionalDispenseItemBehavior {
    private final DispenseItemBehavior behavior;

    public CustomVanillaItemDispenseBehavior(DispenseItemBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
        return this.behavior.dispense(pSource, pStack);
    }
}
