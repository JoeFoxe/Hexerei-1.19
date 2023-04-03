package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.food.FoodProperties;

public class BottleTallowItem extends HexBottleItem {

    public static FoodProperties FOOD = new FoodProperties.Builder().saturationMod(1).nutrition(1).alwaysEat().build();

    public BottleTallowItem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip.hexerei.bottle_tallow_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    }
}
