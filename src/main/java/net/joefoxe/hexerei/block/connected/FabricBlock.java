package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.DyeColorData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed.COLOR;

public class FabricBlock extends WaxedLayeredBlock implements CTDyable {

	public FabricBlock(Properties p_55926_) {
		super(p_55926_);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(COLOR);
	}


	@Override
	public DyeColor getDyeColor(BlockState blockState) {
		if (blockState.hasProperty(COLOR))
			return blockState.getValue(COLOR);
		return DyeColor.WHITE;
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return getUnWaxed(state, context, itemAbility);
	}


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		ItemStack stack = context.getItemInHand();
		if (state != null && state.hasProperty(COLOR)){
			CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			if (tag.contains("color")) {
				DyeColor color = DyeColor.byName(tag.getString("color"), DyeColor.WHITE); // Default to WHITE if the colorName is invalid
				return state.setValue(COLOR, color);
			}
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder pParams) {
		List<ItemStack> drops = super.getDrops(state, pParams);
		if (!state.hasProperty(COLOR))
			return drops;
		List<ItemStack> updated_drops = new ArrayList<>();
		for (ItemStack stack : drops){
			if (stack.getItem() == ModBlocks.INFUSED_FABRIC_BLOCK.get().asItem() || stack.getItem() == ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get().asItem()){
				DyeColor color = state.getValue(COLOR);
				CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
				tag.putString("color", color.getName());
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
			}
			updated_drops.add(stack);
		}
		return updated_drops;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

		if(player.getItemInHand(hand).getItem() instanceof DyeItem dyeItem) {
			DyeColor dyecolor = dyeItem.getDyeColor();
			if(state.getBlock() != ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				if(this.getDyeColor(state) == dyecolor)
					return ItemInteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, player.getItemInHand(hand));
			}
			BlockState newBlockstate = level.getBlockState(pos).setValue(COLOR, dyecolor);

			if(state.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get()) {
				Block.popResource(level, pos, new ItemStack(Items.GOLD_NUGGET));
				newBlockstate = ModBlocks.INFUSED_FABRIC_BLOCK.get().defaultBlockState().setValue(COLOR, dyecolor);
			}

			level.setBlockAndUpdate(pos, newBlockstate);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlockstate));
			level.levelEvent(player, 3003, pos, 0);
			return ItemInteractionResult.sidedSuccess(level.isClientSide);

		}
		else if(player.getItemInHand(hand).getItem() == Items.GOLD_NUGGET) {
			if(state.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				return ItemInteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, player.getItemInHand(hand));
			}
			BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get().defaultBlockState();
			if(!player.isCreative())
				player.getItemInHand(hand).shrink(1);

			level.setBlockAndUpdate(pos, newBlockstate);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlockstate));
			level.levelEvent(player, 3004, pos, 0);
			level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			return ItemInteractionResult.sidedSuccess(level.isClientSide);

		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

}