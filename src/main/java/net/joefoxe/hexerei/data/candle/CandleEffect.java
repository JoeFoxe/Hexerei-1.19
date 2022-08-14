package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface CandleEffect {

    Random random = new Random();
    List<BlockPos> area = Util.make(() -> {
        List<BlockPos> list = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(-2, 0, -2, 2, 2, 2)) {
            list.add(pos.immutable());
        }

        return list;
    });

    void tick(Level level, CandleTile blockEntity, CandleData candleData);

    ParticleOptions getParticleType();

    default BlockPos getRandomPos() {
        return area.get(random.nextInt(area.size()));
    }

    default void spawnOutlineParticles(ServerLevel serverLevel, BlockPos pos) {
        spawnOutlineParticles(serverLevel, pos, 0, 1);
    }

    default void spawnOutlineParticles(ServerLevel serverLevel, BlockPos pos, float minY, float maxY) {
        spawnParticle(serverLevel, pos, 0, maxY, 0);
        spawnParticle(serverLevel, pos, 1, maxY, 1);
        spawnParticle(serverLevel, pos, 1, maxY, 0);
        spawnParticle(serverLevel, pos, 0, maxY, 1);

        spawnParticle(serverLevel, pos, 0, minY, 0);
        spawnParticle(serverLevel, pos, 1, minY, 1);
        spawnParticle(serverLevel, pos, 1, minY, 0);
        spawnParticle(serverLevel, pos, 0, minY, 1);
    }

    default void spawnParticle(ServerLevel level, BlockPos pos, double x, double y, double z) {
        level.sendParticles(getParticleType(), pos.getX() + x, pos.getY() + y, pos.getZ() + z, 1, 0, 0, 0, 0.5f);
    }
}