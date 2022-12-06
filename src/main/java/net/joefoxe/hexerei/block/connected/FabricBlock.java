package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class FabricBlock extends WaxedLayeredBlock implements CTDyable {

	public FabricBlock(Properties p_55926_, DyeColor dyeColor) {
		super(p_55926_);
		this.dyeColor = dyeColor;
	}
	public FabricBlock(Properties p_55926_) {
		super(p_55926_);
		this.dyeColor = null;
	}

	public DyeColor dyeColor;


	@Override
	public DyeColor getDyeColor() {
		return dyeColor;
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		return getUnWaxed(state, context, toolAction);
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos blockpos, Player player, InteractionHand pHand, BlockHitResult pHit) {
		if(player.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
			DyeColor dyecolor = dyeItem.getDyeColor();
			if(this.dyeColor == dyecolor)
				return InteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
			}
			BlockState newBlockstate = getBlockByColor(dyecolor).defaultBlockState();

			if(!player.isCreative())
				player.getItemInHand(pHand).shrink(1);
			if(!player.isCreative() && pState.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				Block.popResource(pLevel, blockpos, new ItemStack(Items.GOLD_NUGGET));
			pLevel.setBlockAndUpdate(blockpos, newBlockstate);
			pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
			pLevel.levelEvent(player, 3003, blockpos, 0);
			return InteractionResult.sidedSuccess(pLevel.isClientSide);

		}
		else if(player.getItemInHand(pHand).getItem() == Items.GOLD_NUGGET) {
			if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				return InteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
			}
			BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get().defaultBlockState();
			if(!player.isCreative())
				player.getItemInHand(pHand).shrink(1);

			pLevel.setBlockAndUpdate(blockpos, newBlockstate);
			pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
			pLevel.levelEvent(player, 3004, blockpos, 0);
			pLevel.playSound(player, blockpos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.sidedSuccess(pLevel.isClientSide);

		}

		return super.use(pState, pLevel, blockpos, player, pHand, pHit);
	}

	//                    if(player.getItemInHand(pHand).getItem() instanceof DyeItem)
//	{
//		DyeColor dyecolor = ((DyeItem)itemstack.getItem()).getDyeColor();


	public static Block getBlockByColor(@Nullable DyeColor pColor) {
		if (pColor == null) {
			return Blocks.SHULKER_BOX;
		} else {
			switch (pColor) {
				case WHITE:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_WHITE.get();
				case ORANGE:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_ORANGE.get();
				case MAGENTA:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get();
				case LIGHT_BLUE:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get();
				case YELLOW:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_YELLOW.get();
				case LIME:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIME.get();
				case PINK:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PINK.get();
				case GRAY:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GRAY.get();
				case LIGHT_GRAY:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get();
				case CYAN:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_CYAN.get();
				case PURPLE:
				default:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PURPLE.get();
				case BLUE:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLUE.get();
				case BROWN:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BROWN.get();
				case GREEN:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GREEN.get();
				case RED:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_RED.get();
				case BLACK:
					return ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLACK.get();
			}
		}
	}

}