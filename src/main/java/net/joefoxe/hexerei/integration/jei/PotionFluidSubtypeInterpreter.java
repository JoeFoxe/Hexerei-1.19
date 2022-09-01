package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;


// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class PotionFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {

    @Override
    public String apply(FluidStack ingredient, UidContext context) {
        if (!ingredient.hasTag())
            return IIngredientSubtypeInterpreter.NONE;

        CompoundTag tag = ingredient.getOrCreateTag();
        Potion potionType = PotionUtils.getPotion(tag);
        String potionTypeString = potionType.getName("");
        String bottleType = HexereiUtil.readEnum(tag, "Bottle", PotionFluid.BottleType.class)
                .toString();

        StringBuilder stringBuilder = new StringBuilder(potionTypeString);
        List<MobEffectInstance> effects = PotionUtils.getCustomEffects(tag);

        stringBuilder.append(";")
                .append(bottleType);
        for (MobEffectInstance effect : potionType.getEffects())
            stringBuilder.append(";")
                    .append(effect);
        for (MobEffectInstance effect : effects)
            stringBuilder.append(";")
                    .append(effect);
        return stringBuilder.toString();
    }

}
