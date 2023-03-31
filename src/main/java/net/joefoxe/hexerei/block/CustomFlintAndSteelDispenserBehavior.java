package net.joefoxe.hexerei.block;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class CustomFlintAndSteelDispenserBehavior extends CustomVanillaItemDispenseBehavior{
    public CustomFlintAndSteelDispenserBehavior(DispenseItemBehavior behavior) {
        super(behavior);
    }

    @Override
    protected ItemStack execute(BlockSource p_123412_, ItemStack p_123413_) {
        Level level = p_123412_.getLevel();
//                this.setSuccess(true);
        this.setSuccess(false);
        Direction direction = p_123412_.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockpos = p_123412_.getPos().relative(direction);
        BlockState blockstate = level.getBlockState(blockpos);
        if (Candle.canBeLit(blockstate, blockpos, level)) {

            CandleTile tile = ((CandleTile) level.getBlockEntity(blockpos));
            boolean flag = false;

            if(blockstate.getBlock() instanceof Candle && tile != null){
                if (tile.candles.get(0).hasCandle && !tile.candles.get(0).lit)
                    tile.candles.get(0).lit = true;
                else if (tile.candles.get(1).hasCandle && !tile.candles.get(1).lit)
                    tile.candles.get(1).lit = true;
                else if (tile.candles.get(2).hasCandle && !tile.candles.get(2).lit)
                    tile.candles.get(2).lit = true;
                else if (tile.candles.get(3).hasCandle && !tile.candles.get(3).lit)
                    tile.candles.get(3).lit = true;
                else {
                    flag = true;
                }
            }

            if(!flag){
                level.playSound((Player) null, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RandomSource.create().nextFloat() * 0.4F + 1.0F);
//                    p_123413_.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(pContext.getHand()));

                if(blockstate.hasProperty(BlockStateProperties.LIT))
                    level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, true));
                level.gameEvent((Entity) null, GameEvent.BLOCK_CHANGE, blockpos);

                if (this.isSuccess() && p_123413_.hurt(1, level.random, null)) {
                    p_123413_.setCount(0);
                }
                this.setSuccess(true);
            }


        }
        if(blockstate.getBlock() instanceof SageBurningPlate sageBurningPlate){
            sageBurningPlate.withTileEntityDo(level, blockpos, te -> {
                if (te.getItems().get(0).is(ModItems.DRIED_SAGE_BUNDLE.get()) && !blockstate.getValue(BlockStateProperties.LIT)) {

                    if(blockstate.hasProperty(BlockStateProperties.LIT))
                        level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, true));
                    level.gameEvent((Entity) null, GameEvent.BLOCK_CHANGE, blockpos);

                    if (this.isSuccess() && p_123413_.hurt(1, level.random, null)) {
                        p_123413_.setCount(0);
                    }
                    this.setSuccess(true);
                }
            });
        }

        return super.execute(p_123412_, p_123413_);
    }
}
