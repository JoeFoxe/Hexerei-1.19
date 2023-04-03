package net.joefoxe.hexerei.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;

public class PotionFluid extends ForgeFlowingFluid {
    protected PotionFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getSource() {
        return super.getSource();
    }

    @Override
    public Fluid getFlowing() {
        return this;
    }

    @Override
    public Item getBucket() {
        return Items.AIR;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState p_207193_1_) {
        return false;
    }

    @Override
    public int getAmount(FluidState p_207192_1_) {
        return 0;
    }

    public static FluidStack of(int amount, Potion potion) {
        FluidStack fluidStack = new FluidStack(ModFluids.POTION.get()
                .getSource(), amount);
        addPotionToFluidStack(fluidStack, potion);
        return fluidStack;
    }

    public static FluidStack withEffects(int amount, Potion potion, List<MobEffectInstance> customEffects) {
        FluidStack fluidStack = of(amount, potion);
        appendEffects(fluidStack, customEffects);
        return fluidStack;
    }

    public static FluidStack addPotionToFluidStack(FluidStack fs, Potion potion) {
        ResourceLocation resourcelocation = getKeyOrThrow(potion);
        if (potion == Potions.EMPTY) {
            fs.removeChildTag("Potion");
            return fs;
        }
        fs.getOrCreateTag()
                .putString("Potion", resourcelocation.toString());
        return fs;
    }

    public static FluidStack appendEffects(FluidStack fs, Collection<MobEffectInstance> customEffects) {
        if (customEffects.isEmpty())
            return fs;
        CompoundTag compoundnbt = fs.getOrCreateTag();
        ListTag listnbt = compoundnbt.getList("CustomPotionEffects", 9);
        for (MobEffectInstance effectinstance : customEffects)
            listnbt.add(effectinstance.save(new CompoundTag()));
        compoundnbt.put("CustomPotionEffects", listnbt);
        return fs;
    }

    public enum BottleType {
        REGULAR, SPLASH, LINGERING
    }

    public static <V> ResourceLocation getKeyOrThrow(Potion value) {
        IForgeRegistry<Potion> registry = ForgeRegistries.POTIONS;
        ResourceLocation key = registry.getKey(value);
        if (key == null) {
            throw new IllegalArgumentException("Could not get key for value " + value + "!");
        }
        return key;
    }
}
