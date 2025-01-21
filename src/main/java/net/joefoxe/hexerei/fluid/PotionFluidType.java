package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.PotionBottleTypeData;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class PotionFluidType extends FluidType {

    public PotionFluidType(Properties properties) {
        super(properties);
    }

    public static int getTintColor(FluidStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
    }

    public static int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return 0xffffffff;
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        PotionFluid.BottleType bottleType = stack.getOrDefault(ModDataComponents.POTION_BOTTLE_TYPE, PotionBottleTypeData.EMPTY).bottleType();
        ItemLike itemFromBottleType = PotionFluidHandler.itemFromBottleType(bottleType);
        return Potion.getName(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion(), itemFromBottleType.asItem().getDescriptionId() + ".effect.");
    }


}
