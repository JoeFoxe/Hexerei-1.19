package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class BottleQuicksilverItem extends HexBottleItem {

    public static FoodProperties FOOD = new FoodProperties.Builder().saturationMod(1).nutrition(1).alwaysEat().effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F).effect(new MobEffectInstance(MobEffects.POISON, 300, 0), 0.8F).build();

    public BottleQuicksilverItem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip.hexerei.bottle_quicksilver_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    }
}
