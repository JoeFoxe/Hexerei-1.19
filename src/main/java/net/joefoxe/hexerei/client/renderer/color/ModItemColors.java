package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomSeatItem;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.MixingCauldronItem;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;


@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class ModItemColors {
    private ModItemColors() {}
    // FORGE: Use RegistryDelegates as non-Vanilla item crowList are not constant

    @SubscribeEvent
    public static void initItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, color) -> {
            if (color == 0)
                return -1;
//            CandleItem.getColorValue(CandleItem.getDyeColorNamed(stack), stack)
            int col = 255 << 24 | ((WitchArmorItem)stack.getItem()).getColor(stack);
            return col == -1 ? 0 : col;
//            return color == 0 ? -1 : (col == -1 ? -1 : col);
//            return color == 0 ? -1 : ((WitchArmorItem)stack.getItem()).getColor(stack);
        }, ModItems.WITCH_HELMET.get(), ModItems.WITCH_CHESTPLATE.get(), ModItems.WITCH_BOOTS.get());

        event.register((stack, color) -> {
            Block block = Block.byItem(stack.getItem());
            if(block instanceof WaterlilyBlock) {
                return GrassColor.get(0.0D, 0.5D);
            }
            return 0;
        }, ModBlocks.LILY_PAD_BLOCK.get());

        CofferItem.ItemHandlerConsumer items = event.getItemColors()::register;
        items.register((s, t) -> t == 1 ? CofferItem.getColorValue(CofferItem.getDyeColorNamed(s), s) : -1, ModItems.COFFER.get());

        items.register((s, t) -> t == 0 ? MixingCauldronItem.getColorValue(MixingCauldronItem.getDyeColorNamed(s), s) : -1, ModItems.MIXING_CAULDRON.get());

        items.register((s, t) -> t == 0 ? BroomSeatItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.BROOM_SEAT.get());

        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.SMALL_SATCHEL.get());
        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.MEDIUM_SATCHEL.get());
        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.LARGE_SATCHEL.get());


        items.register((s, t) -> t == 1 ? CandleItem.getColorValue(CandleItem.getDyeColorNamed(s), s) : -1, ModItems.CANDLE.get());


        items.register((s, t) -> t == 0 ? ConnectingCarpetDyed.getColorValue(s) : -1,
                ModItems.INFUSED_FABRIC_CARPET.get(),
                ModItems.WAXED_INFUSED_FABRIC_CARPET.get(),
                ModItems.INFUSED_FABRIC_BLOCK.get(),
                ModItems.WAXED_INFUSED_FABRIC_BLOCK.get());
    }


}