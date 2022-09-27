package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class WaxedLayeredBlockDyed extends WaxedLayeredBlock implements CTDyable {

	public DyeColor dyeColor;
	public WaxedLayeredBlockDyed(Properties p_55926_, DyeColor dyeColor) {
		super(p_55926_);

		this.dyeColor = dyeColor;
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		return getUnWaxed(state, context, toolAction);
	}

	public static int getColorValue(ItemStack stack) {
		if(Block.byItem(stack.getItem()) instanceof ConnectingCarpetDyed connectingCarpetDyed)
			return HexereiUtil.getColorValue(connectingCarpetDyed.dyeColor);
		return SatchelItem.getColorStatic(stack);
	}


	public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
		return HexereiUtil.getColorValue(((ConnectingCarpetDyed)state.getBlock()).dyeColor);
	}

	@Override
	public DyeColor getDyeColor() {
		return dyeColor;
	}
}