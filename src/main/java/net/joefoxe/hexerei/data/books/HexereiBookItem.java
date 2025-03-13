package net.joefoxe.hexerei.data.books;


import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.fml.util.thread.EffectiveSide;

import java.util.List;
import java.util.UUID;

public class HexereiBookItem extends Item {



    public HexereiBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (EffectiveSide.get().isClient())
            ClientHelper.setScreen(player, usedHand);
        return super.use(level, player, usedHand);
    }

    @Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        super.onCraftedPostProcess(stack, level);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        BookData bookData = stack.get(ModDataComponents.BOOK);
        if (bookData != null) {
            if (bookData.getUUID() == BookData.EMPTY_UUID){
                bookData = bookData.setUUID(UUID.randomUUID());
                stack.set(ModDataComponents.BOOK, bookData);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    static class ClientHelper {
        public static void setScreen(Player player, InteractionHand usedHand) {
            Minecraft.getInstance().setScreen(new net.joefoxe.hexerei.screen.BookOfShadowsScreen(player, usedHand));
        }
    }

    public static ItemStack withColors(ItemStack stack, int color1, int color2) {
        stack.set(ModDataComponents.BOOK_COLORS, new BookColorData(color1, color2));

        return stack;
    }

    public static int getColor1(ItemStack stack) {

        BookColorData bookColorData = stack.getOrDefault(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY);

        return bookColorData.color1();
    }


    public static int getColor2(ItemStack stack) {

        BookColorData bookColorData = stack.getOrDefault(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY);

        return bookColorData.color2();
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.book_of_shadows_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

