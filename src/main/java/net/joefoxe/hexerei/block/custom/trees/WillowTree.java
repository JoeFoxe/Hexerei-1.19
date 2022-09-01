package net.joefoxe.hexerei.block.custom.trees;


import net.joefoxe.hexerei.world.gen.ModConfiguredFeatures;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

public class WillowTree extends AbstractTreeGrower {


    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_60014_, boolean p_60015_) {
        return ModConfiguredFeatures.WILLOW;
    }

}
