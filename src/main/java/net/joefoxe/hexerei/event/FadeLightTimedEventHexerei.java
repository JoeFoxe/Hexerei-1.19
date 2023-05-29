package net.joefoxe.hexerei.event;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.joefoxe.hexerei.light.DynamicLightUtil;
import net.joefoxe.hexerei.light.LambHexereiDynamicLight;
import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FadeLightTimedEventHexerei implements ITimedEvent, LambHexereiDynamicLight {
    protected int lambdynlights$luminance = 0;
    private int lambdynlights$lastLuminance = 0;
    private long lambdynlights$lastUpdate = 0;
    private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

    public Vec3 targetPos;
    public
    int ticksLeft;
    int starterTicks;
    int startLuminance;
    Level level;

    public FadeLightTimedEventHexerei(Level level, Vec3 pos, int duration, int startLuminance) {
        this.targetPos = pos;
        ticksLeft = duration;
        this.starterTicks = duration;
        this.startLuminance = startLuminance;
        this.level = level;
    }

    @Override
    public void tick(boolean serverSide) {
        // We do not want to update the entity on the server.
        if (!serverSide && !LightManager.shouldUpdateDynamicLight()) {
            lambdynlights$luminance = 0;
        }
        if (!serverSide && LightManager.shouldUpdateDynamicLight()) {
            if (this.isExpired()) {
                this.setHexereiDynamicLightEnabled(false);
            } else {
                this.dynamicLightTickH();
                LightManager.updateLightTracking(this);
            }
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            this.setHexereiDynamicLightEnabled(false);
        }
    }

    @Override
    public boolean isExpired() {
        return ticksLeft <= 0;
    }

    @Override
    public double getDynamicLightXH() {
        return targetPos.x;
    }

    @Override
    public double getDynamicLightYH() {
        return targetPos.y;
    }

    @Override
    public double getDynamicLightZH() {
        return targetPos.z;
    }

    @Override
    public Level getDynamicLightWorldH() {
        return level;
    }

    @Override
    public void resetDynamicLightH() {
        this.lambdynlights$lastLuminance = 0;
    }

    @Override
    public int getLuminanceH() {
        return lambdynlights$luminance;
    }

    @Override
    public void dynamicLightTickH() {
        lambdynlights$luminance = starterTicks == 0 ? 0 : (int) ((double) startLuminance * ((double) ticksLeft / (double) this.starterTicks));
    }

    @Override
    public boolean shouldUpdateDynamicLightH() {
        return LightManager.shouldUpdateDynamicLight();
    }

    @Override
    public boolean lambdynlights$updateDynamicLightH(LevelRenderer renderer) {
        int luminance = this.getLuminanceH();

        if (luminance != this.lambdynlights$lastLuminance) {
            this.lambdynlights$lastLuminance = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                var entityChunkPos = new ChunkPos(new BlockPos(targetPos));
                var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, DynamicLightUtil.getSectionCoord(this.targetPos.y), entityChunkPos.z);

                LightManager.scheduleChunkRebuild(renderer, chunkPos);
                LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
                BlockPos blockPos = new BlockPos(targetPos);
                var directionX = (blockPos.getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
                var directionY = (Mth.fastFloor(blockPos.getY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                var directionZ = (blockPos.getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX); // X
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ); // XZ
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite()); // Z
                    } else {
                        chunkPos.move(directionZ.getOpposite()); // origin
                        chunkPos.move(directionY); // Y
                    }
                    LightManager.scheduleChunkRebuild(renderer, chunkPos);
                    LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
                }
            }
            // Schedules the rebuild of removed chunks.
            this.lambdynlights$scheduleTrackedChunksRebuildH(renderer);
            // Update tracked lit chunks.
            this.lambdynlights$trackedLitChunkPos = newPos;
            return true;
        }
        return false;
    }

    @Override
    public void lambdynlights$scheduleTrackedChunksRebuildH(LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.level)
            for (long pos : this.lambdynlights$trackedLitChunkPos) {
                LightManager.scheduleChunkRebuild(renderer, pos);
            }
    }
}
