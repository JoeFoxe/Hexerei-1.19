package net.joefoxe.hexerei.block.connected.behavior;

import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.SimpleCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE

public class GlassPaneCTBehaviour extends SimpleCTBehaviour {

    public GlassPaneCTBehaviour(CTSpriteShiftEntry shift) {
        super(shift);
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        return state.getBlock() == other.getBlock();
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, net.minecraft.core.Direction face) {
        if (face.getAxisDirection() == AxisDirection.NEGATIVE)
            return true;
        return super.reverseUVsHorizontally(state, face);
    }
}