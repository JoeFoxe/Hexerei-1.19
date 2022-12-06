package net.joefoxe.hexerei.item.custom;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class WaxBlendItem extends Item {
    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                .put(ModBlocks.POLISHED_WILLOW_CONNECTED.get(), ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get())
                .put(ModBlocks.POLISHED_WILLOW_PILLAR.get(), ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get())
                .put(ModBlocks.POLISHED_WILLOW_LAYERED.get(), ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get())
                .put(ModBlocks.WILLOW_CONNECTED.get(), ModBlocks.WAXED_WILLOW_CONNECTED.get())
                .put(ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get())
                .put(ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get())
                .put(ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get())
                .put(ModBlocks.MAHOGANY_CONNECTED.get(), ModBlocks.WAXED_MAHOGANY_CONNECTED.get())
                .put(ModBlocks.MAHOGANY_WINDOW_PANE.get(), ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get())
                .put(ModBlocks.WILLOW_WINDOW_PANE.get(), ModBlocks.WAXED_WILLOW_WINDOW_PANE.get())
                .put(ModBlocks.MAHOGANY_WINDOW.get(), ModBlocks.WAXED_MAHOGANY_WINDOW.get())
                .put(ModBlocks.WILLOW_WINDOW.get(), ModBlocks.WAXED_WILLOW_WINDOW.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_WHITE.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_ORANGE.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_MAGENTA.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_YELLOW.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIME.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PINK.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GRAY.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_CYAN.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_PURPLE.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLUE.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BROWN.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_GREEN.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_RED.get())
                .put(ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_DYED_BLACK.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_WHITE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_WHITE.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_ORANGE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_ORANGE.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_MAGENTA.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_BLUE.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_YELLOW.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_YELLOW.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIME.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIME.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PINK.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PINK.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GRAY.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GRAY.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_LIGHT_GRAY.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_CYAN.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_CYAN.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_PURPLE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_PURPLE.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLUE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLUE.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BROWN.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BROWN.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_GREEN.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_GREEN.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_RED.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_RED.get())
                .put(ModBlocks.INFUSED_FABRIC_BLOCK_DYED_BLACK.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_DYED_BLACK.get()).build();
    });
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> {
        return WAXABLES.get().inverse();
    });

    public WaxBlendItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        return getWaxed(blockstate).map((newBlockstate) -> {
            Player player = pContext.getPlayer();
            ItemStack itemstack = pContext.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }


            itemstack.shrink(1);
            if(blockstate.getBlock() instanceof CrossCollisionBlock) {
                BlockState changeTo = newBlockstate.setValue(CrossCollisionBlock.NORTH, blockstate.getValue(CrossCollisionBlock.NORTH)).setValue(CrossCollisionBlock.SOUTH, blockstate.getValue(CrossCollisionBlock.SOUTH)).setValue(CrossCollisionBlock.EAST, blockstate.getValue(CrossCollisionBlock.EAST)).setValue(CrossCollisionBlock.WEST, blockstate.getValue(CrossCollisionBlock.WEST)).setValue(CrossCollisionBlock.WATERLOGGED, blockstate.getValue(CrossCollisionBlock.WATERLOGGED));
                level.setBlockAndUpdate(blockpos, changeTo);
//                level.updateNeighborsAt(blockpos, changeTo.getBlock());
//                level.updateNeighborsAt(blockpos, blockstate.getBlock());
            }
            else
                level.setBlock(blockpos, newBlockstate, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            level.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }

    public static Optional<BlockState> getWaxed(BlockState pState) {
        return Optional.ofNullable(WAXABLES.get().get(pState.getBlock())).map((p_150877_) -> {
            return p_150877_.withPropertiesOf(pState);
        });
    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.hexerei.wax_blend").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        super.appendHoverText(stack, world, tooltip, flagIn);
    }
}