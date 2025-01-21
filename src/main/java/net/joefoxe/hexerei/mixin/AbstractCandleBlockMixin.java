package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin {
    @Inject(method = "extinguish", at = @At("HEAD"), cancellable = true)
    private static void extinguish(@Nullable Player player, BlockState blockState, LevelAccessor level, BlockPos blockPos, CallbackInfo ci) {

        if(blockState.getBlock() instanceof Candle candle){
            CandleTile tile = candle.getBlockEntity(level, blockPos);
            Candle.extinguish(level, blockPos,blockState, tile);
        }
    }

}