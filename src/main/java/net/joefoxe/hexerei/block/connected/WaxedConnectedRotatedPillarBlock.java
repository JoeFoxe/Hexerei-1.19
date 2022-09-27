package net.joefoxe.hexerei.block.connected;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class WaxedConnectedRotatedPillarBlock extends ConnectedPillarBlock implements Waxed {

	public WaxedConnectedRotatedPillarBlock(Properties p_55926_) {
		super(p_55926_);
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		return getUnWaxed(state, context, toolAction);
	}

}