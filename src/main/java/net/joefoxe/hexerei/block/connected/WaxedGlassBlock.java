package net.joefoxe.hexerei.block.connected;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class WaxedGlassBlock extends GlassBlock implements Waxed {

	public WaxedGlassBlock(Properties p_55926_) {
		super(p_55926_);
	}



	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
//        if (side.getAxis()
//                .isVertical())
//            return adjacentBlockState == state;
		return super.skipRendering(state, adjacentBlockState, side);
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		return getUnWaxed(state, context, toolAction);
	}

}