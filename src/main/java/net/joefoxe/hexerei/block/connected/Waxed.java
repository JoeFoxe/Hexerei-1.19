package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import static net.joefoxe.hexerei.item.custom.WaxBlendItem.WAX_OFF_BY_BLOCK;
import static net.minecraft.world.level.block.RotatedPillarBlock.AXIS;

public interface Waxed {

    public default BlockState getUnWaxed(BlockState state, UseOnContext context, ToolAction toolAction){
        boolean cloth = CleaningClothItem.CLOTH_WAX_OFF.equals(toolAction);
        WAX_OFF_BY_BLOCK.get().get(state.getBlock());
        BlockState toReturn = state;

        if(cloth){
            toReturn = WAX_OFF_BY_BLOCK.get().get(state.getBlock()).defaultBlockState();
            if(state.hasProperty(AXIS) && WAX_OFF_BY_BLOCK.get().get(state.getBlock()).defaultBlockState().hasProperty(AXIS))
                toReturn = WAX_OFF_BY_BLOCK.get().get(state.getBlock()).defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            context.getLevel().scheduleTick(context.getClickedPos(), (Block) this, 1);
        }

        return toReturn;
    }

}
