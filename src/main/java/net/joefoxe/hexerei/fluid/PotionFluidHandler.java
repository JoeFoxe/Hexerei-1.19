package net.joefoxe.hexerei.fluid;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotionFluidHandler {

    public static Pair<FluidStack, ItemStack> emptyPotion(ItemStack stack, boolean simulate) {
        FluidStack fluid = getFluidFromPotionItem(stack);
        if (!simulate)
            stack.shrink(1);
        return Pair.of(fluid, new ItemStack(Items.GLASS_BOTTLE));
    }

    public static FluidStack getFluidFromPotionItem(ItemStack stack) {
        Potion potion = PotionUtils.getPotion(stack);
        List<MobEffectInstance> list = PotionUtils.getCustomEffects(stack);
        PotionFluid.BottleType bottleTypeFromItem = bottleTypeFromItem(stack.getItem());
        if (potion == Potions.WATER && list.isEmpty() && bottleTypeFromItem == PotionFluid.BottleType.REGULAR)
            return new FluidStack(Fluids.WATER, 333);
        FluidStack fluid = PotionFluid.withEffects(333, potion, list);
        HexereiUtil.writeEnum(fluid.getOrCreateTag(), "Bottle", bottleTypeFromItem);
        return fluid;
    }

    public static FluidStack getFluidFromPotion(Potion potion, PotionFluid.BottleType bottleType, int amount) {
        if (potion == Potions.WATER && bottleType == PotionFluid.BottleType.REGULAR)
            return new FluidStack(Fluids.WATER, amount);
        FluidStack fluid = PotionFluid.of(amount, potion);
        HexereiUtil.writeEnum(fluid.getOrCreateTag(), "Bottle", bottleType);
        return fluid;
    }

    public static Potion getPotionFromFluidStack(FluidStack fs) {
        ResourceLocation rl = new ResourceLocation(fs.getOrCreateTag().getString("Potion"));
        return ForgeRegistries.POTIONS.getValue(rl);
    }

    public static PotionFluid.BottleType bottleTypeFromItem(Item item) {
        if (item == Items.LINGERING_POTION)
            return PotionFluid.BottleType.LINGERING;
        if (item == Items.SPLASH_POTION)
            return PotionFluid.BottleType.SPLASH;
        return PotionFluid.BottleType.REGULAR;
    }

    public static ItemLike itemFromBottleType(PotionFluid.BottleType type) {
        switch (type) {
            case LINGERING:
                return Items.LINGERING_POTION;
            case SPLASH:
                return Items.SPLASH_POTION;
            case REGULAR:
            default:
                return Items.POTION;
        }
    }

    public static int getRequiredAmountForFilledBottle(ItemStack stack, FluidStack availableFluid) {
        return 250;
    }

    public static ItemStack fillBottle(FluidStack fluid) {
        CompoundTag tag = fluid.getOrCreateTag();
        ItemStack potionStack = new ItemStack(itemFromBottleType(HexereiUtil.readEnum(tag, "Bottle", PotionFluid.BottleType.class)));
        PotionUtils.setPotion(potionStack, PotionUtils.getPotion(tag));
        PotionUtils.setCustomEffects(potionStack, PotionUtils.getCustomEffects(tag));
        return potionStack;
    }

    // Modified version of PotionUtils#addPotionTooltip
    @OnlyIn(Dist.CLIENT)
    public static void addPotionTooltip(FluidStack fs, List<Component> tooltip, float p_185182_2_) {

        List<MobEffectInstance> list = fs.isEmpty() ? new ArrayList<>() : PotionUtils.getAllEffects(fs.getOrCreateTag());
        if(!ForgeRegistries.FLUIDS.getKey(fs.getFluid()).getPath().equals("potion"))
            return;
        List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            tooltip.add((Component.translatable("effect.none")).withStyle(ChatFormatting.GRAY));
        } else {
            for (MobEffectInstance effectinstance : list) {
                MutableComponent textcomponent = Component.translatable(effectinstance.getDescriptionId());
                MobEffect effect = effectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(),
                                effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier),
                                attributemodifier.getOperation());
                        list1.add(new Tuple<>(
                                entry.getKey().getDescriptionId(),
                                attributemodifier1));
                    }
                }

                if (effectinstance.getAmplifier() > 0) {
                    textcomponent.append(" ")
                            .append(Component.translatable("potion.potency." + effectinstance.getAmplifier()).getString());
                }

                if (effectinstance.getDuration() > 20) {
                    textcomponent.append(" (")
                            .append(MobEffectUtil.formatDuration(effectinstance, p_185182_2_))
                            .append(")");
                }

                tooltip.add(textcomponent.withStyle(effect.getCategory()
                        .getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
//            tooltip.add(Component.immutableEmpty());
            tooltip.add((Component.translatable("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));

            for (Tuple<String, AttributeModifier> tuple : list1) {
                AttributeModifier attributemodifier2 = tuple.getB();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE
                        && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    tooltip.add((Component.translatable(
                            "attribute.modifier.plus." + attributemodifier2.getOperation()
                                    .toValue(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
                            Component.translatable(tuple.getA())))
                            .withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    tooltip.add((Component.translatable(
                            "attribute.modifier.take." + attributemodifier2.getOperation()
                                    .toValue(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
                            Component.translatable(tuple.getA())))
                            .withStyle(ChatFormatting.RED));
                }
            }
        }

    }
}
