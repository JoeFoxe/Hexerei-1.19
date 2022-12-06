package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface CandleEffect {


    Random random = new Random();
    List<BlockPos> area = Util.make(() -> {
        List<BlockPos> list = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(-3, 0, -3, 3, 3, 3)) {
            list.add(pos.immutable());
        }

        return list;
    });

    void tick(Level level, CandleTile blockEntity, CandleData candleData);

    ParticleOptions getParticleType();

    default BlockPos getRandomPos() {
        return area.get(random.nextInt(area.size()));
    }

    default <T>AbstractCandleEffect getCopy() {
        return new AbstractCandleEffect();
    }

    default String getLocationName() {
        return "hexerei:no_effect";
    }

}