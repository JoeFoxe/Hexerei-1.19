package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AbstractCandleEffect implements CandleEffect{

    private static final int MAX_TIME = 8 * 20;

    public int checkCooldown;

    public ParticleOptions particle;

    public List<ResourceLocation> particleLocation;

    public AbstractCandleEffect(ParticleOptions particleOptions){
        this.particle = particleOptions;
    }
    public AbstractCandleEffect(){
    }


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
    }

    @Override
    public ParticleOptions getParticleType() {
        return particle;
    }



}
