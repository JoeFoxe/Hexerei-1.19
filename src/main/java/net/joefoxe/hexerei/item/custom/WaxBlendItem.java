package net.joefoxe.hexerei.item.custom;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class WaxBlendItem extends Item {
    public static final BiMap<Block, Block> WAXABLES = new ImmutableBiMap.Builder<Block, Block>()
            .put(ModBlocks.WILLOW_PLANKS.get(), ModBlocks.POLISHED_WILLOW_PLANKS.get())
            .put(ModBlocks.POLISHED_WILLOW_CONNECTED.get(), ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get())
            .put(ModBlocks.POLISHED_WILLOW_PILLAR.get(), ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get())
            .put(ModBlocks.POLISHED_WILLOW_LAYERED.get(), ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get())
            .put(ModBlocks.WILLOW_CONNECTED.get(), ModBlocks.WAXED_WILLOW_CONNECTED.get())
            .put(ModBlocks.POLISHED_WITCH_HAZEL_CONNECTED.get(), ModBlocks.WAXED_POLISHED_WITCH_HAZEL_CONNECTED.get())
            .put(ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get(), ModBlocks.WAXED_POLISHED_WITCH_HAZEL_PILLAR.get())
            .put(ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get(), ModBlocks.WAXED_POLISHED_WITCH_HAZEL_LAYERED.get())
            .put(ModBlocks.WITCH_HAZEL_CONNECTED.get(), ModBlocks.WAXED_WITCH_HAZEL_CONNECTED.get())
            .put(ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get())
            .put(ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get())
            .put(ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get())
            .put(ModBlocks.MAHOGANY_CONNECTED.get(), ModBlocks.WAXED_MAHOGANY_CONNECTED.get())
            .put(ModBlocks.MAHOGANY_WINDOW_PANE.get(), ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get())
            .put(ModBlocks.WILLOW_WINDOW_PANE.get(), ModBlocks.WAXED_WILLOW_WINDOW_PANE.get())
            .put(ModBlocks.WITCH_HAZEL_WINDOW_PANE.get(), ModBlocks.WAXED_WITCH_HAZEL_WINDOW_PANE.get())
            .put(ModBlocks.MAHOGANY_WINDOW.get(), ModBlocks.WAXED_MAHOGANY_WINDOW.get())
            .put(ModBlocks.WILLOW_WINDOW.get(), ModBlocks.WAXED_WILLOW_WINDOW.get())
            .put(ModBlocks.WITCH_HAZEL_WINDOW.get(), ModBlocks.WAXED_WITCH_HAZEL_WINDOW.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB.get())
            .put(ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get(), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get())
            .put(ModBlocks.STONE_WINDOW.get(), ModBlocks.WAXED_STONE_WINDOW.get())
            .put(ModBlocks.STONE_WINDOW_PANE.get(), ModBlocks.WAXED_STONE_WINDOW_PANE.get())
            .put(ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_ORNATE.get())
            .put(ModBlocks.INFUSED_FABRIC_BLOCK.get(), ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get()).build();
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(WAXABLES::inverse);

    public WaxBlendItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        return getWaxed(blockstate).map((newBlockstate) -> {
            if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR))
                newBlockstate.setValue(ConnectingCarpetDyed.COLOR, blockstate.getValue(ConnectingCarpetDyed.COLOR));
            Player player = pContext.getPlayer();
            ItemStack itemstack = pContext.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }


            itemstack.shrink(1);
            if(blockstate.getBlock() instanceof CrossCollisionBlock) {
                BlockState changeTo = newBlockstate.setValue(CrossCollisionBlock.NORTH, blockstate.getValue(CrossCollisionBlock.NORTH)).setValue(CrossCollisionBlock.SOUTH, blockstate.getValue(CrossCollisionBlock.SOUTH)).setValue(CrossCollisionBlock.EAST, blockstate.getValue(CrossCollisionBlock.EAST)).setValue(CrossCollisionBlock.WEST, blockstate.getValue(CrossCollisionBlock.WEST)).setValue(CrossCollisionBlock.WATERLOGGED, blockstate.getValue(CrossCollisionBlock.WATERLOGGED));

                level.setBlockAndUpdate(blockpos, changeTo);
            }
            else
                level.setBlock(blockpos, newBlockstate, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            level.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }

    public static Optional<BlockState> getWaxed(BlockState pState) {
        return Optional.ofNullable(WAXABLES.get(pState.getBlock())).map((p_150877_) -> {
            return p_150877_.withPropertiesOf(pState);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.hexerei.wax_blend").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}