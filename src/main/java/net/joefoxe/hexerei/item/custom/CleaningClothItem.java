package net.joefoxe.hexerei.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.List;

import static net.joefoxe.hexerei.item.custom.WaxBlendItem.WAX_OFF_BY_BLOCK;

public class CleaningClothItem extends Item {
    public static final ToolAction CLOTH_WAX_OFF = ToolAction.get("cloth_wax_off");

    public CleaningClothItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();

        BlockState cleanedState = getCleanedState(blockstate);
        if(getCleanedState(blockstate)  != null) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
        }
        if (cleanedState != null) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            level.setBlock(blockpos, cleanedState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, cleanedState));
            if (player != null) {
                itemstack.hurtAndBreak(1, player, (p_150686_) -> {
                    p_150686_.broadcastBreakEvent(pContext.getHand());
                });
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }


    public static BlockState getCleanedState(BlockState originalState) {
        if(WAX_OFF_BY_BLOCK.get().containsKey(originalState.getBlock())){
            Block block = WAX_OFF_BY_BLOCK.get().get(originalState.getBlock());
            if (block == null)
                return null;
            if (block.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS))
                return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS));
            else if (block instanceof CrossCollisionBlock)
                return block.defaultBlockState().setValue(CrossCollisionBlock.NORTH, originalState.getValue(CrossCollisionBlock.NORTH)).setValue(CrossCollisionBlock.SOUTH, originalState.getValue(CrossCollisionBlock.SOUTH)).setValue(CrossCollisionBlock.EAST, originalState.getValue(CrossCollisionBlock.EAST)).setValue(CrossCollisionBlock.WEST, originalState.getValue(CrossCollisionBlock.WEST)).setValue(CrossCollisionBlock.WATERLOGGED, originalState.getValue(CrossCollisionBlock.WATERLOGGED));
            else
                return block.defaultBlockState();
        } else {
            return null;
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.hexerei.cloth").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        super.appendHoverText(stack, world, tooltip, flagIn);
    }
}