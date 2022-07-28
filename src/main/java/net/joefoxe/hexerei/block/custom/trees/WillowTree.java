package net.joefoxe.hexerei.block.custom.trees;


import net.joefoxe.hexerei.world.gen.ModConfiguredFeatures;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

import java.util.Random;

public class WillowTree extends AbstractTreeGrower {


    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_60014_, boolean p_60015_) {
        return ModConfiguredFeatures.WILLOW;
    }
}
