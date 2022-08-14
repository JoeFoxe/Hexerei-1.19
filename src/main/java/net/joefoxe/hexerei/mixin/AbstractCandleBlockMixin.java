package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.CandleExtinguishPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin {
    @OnlyIn(Dist.CLIENT)
    @Inject(method = "extinguish", at = @At(value = "HEAD"), cancellable = true)
    private static void extinguish(@Nullable Player player, BlockState blockState, LevelAccessor level, BlockPos blockPos, CallbackInfo ci) {
        if(blockState.getBlock() instanceof Candle candle){
            CandleTile tile = candle.getBlockEntity(level, blockPos);
            Candle.extinguish(level, blockPos,blockState, tile);
            if (!level.isClientSide()) {
                PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
                        blockPos.getX(), blockPos.getY(), blockPos.getZ(), 500, ((Level) level).dimension());
                HexereiPacketHandler.instance.send(PacketDistributor.NEAR.with(() -> point), new CandleExtinguishPacket(blockPos));
            }
        }
    }

}