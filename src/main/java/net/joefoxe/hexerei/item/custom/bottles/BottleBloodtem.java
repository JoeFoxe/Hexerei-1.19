package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BottleBloodtem extends HexBottleItem {

    public static FoodProperties FOOD = new FoodProperties.Builder().saturationMod(1).nutrition(1).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 200), 1).alwaysEat().build();

    public BottleBloodtem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (!world.isClientSide && entityLiving instanceof ServerPlayer player) {
            entityLiving.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }

        return super.finishUsingItem(stack, world, entityLiving);
    }

    @Override
    public Component getTooltip() {
        return Component.translatable("tooltip.hexerei.bottle_blood_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    }

}
