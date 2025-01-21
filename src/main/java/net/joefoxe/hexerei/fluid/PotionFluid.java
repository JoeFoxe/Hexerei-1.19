package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Collection;
import java.util.List;

public class PotionFluid extends FlowingFluid {

    @Override
    public FluidType getFluidType() {
        return ModFluidTypes.POTION_FLUID_TYPE.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {

    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 0;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 0;
    }

    @Override
    public Fluid getFlowing() {
        return this;
    }
    @Override
    public Fluid getSource() {
        return this;
    }

    @Override
    public Item getBucket() {
        return Items.AIR;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState p_207193_1_) {
        return true;
    }

    @Override
    public int getAmount(FluidState p_207192_1_) {
        return 0;
    }

    public static FluidStack of(int amount, PotionContents potion) {
        FluidStack fluidStack = new FluidStack(ModFluids.POTION.get(), amount);
        addPotionToFluidStack(fluidStack, potion);
        return fluidStack;
    }

    public static FluidStack addPotionToFluidStack(FluidStack fs, PotionContents potion) {
        if (potion != null && potion != PotionContents.EMPTY)
            fs.set(DataComponents.POTION_CONTENTS, potion);
//        ResourceLocation resourcelocation = getKeyOrThrow(potion);
//        if (potion == Potions.WATER) {
//            fs.removeChildTag("Potion");
//            return fs;
//        }
//        fs.getOrCreateTag()
//                .putString("Potion", resourcelocation.toString());
        return fs;
    }
//
//    public static FluidStack appendEffects(FluidStack fs, Collection<MobEffectInstance> customEffects) {
//        if (customEffects.isEmpty())
//            return fs;
//        CompoundTag compoundnbt = fs.getOrCreateTag();
//        ListTag listnbt = compoundnbt.getList("CustomPotionEffects", 9);
//        for (MobEffectInstance effectinstance : customEffects)
//            listnbt.add(effectinstance.save(new CompoundTag()));
//        compoundnbt.put("CustomPotionEffects", listnbt);
//        return fs;
//    }

    public enum BottleType implements StringRepresentable {
        REGULAR, SPLASH, LINGERING;

        public static BottleType byId(int id) {
            BottleType[] type = values();
            return type[id < 0 || id >= type.length ? 0 : id];
        }

        public static final StringRepresentable.EnumCodec<BottleType> CODEC = StringRepresentable.fromEnum(BottleType::values);

        @Override
        public String getSerializedName() {
            return switch (this) {
                case REGULAR -> "REGULAR";
                case SPLASH -> "SPLASH";
                case LINGERING -> "LINGERING";
            };
        }
    }

    public static <V> ResourceLocation getKeyOrThrow(Potion value) {
        ResourceLocation key = BuiltInRegistries.POTION.getKey(value);
        if (key == null) {
            throw new IllegalArgumentException("Could not get key for value " + value + "!");
        }
        return key;
    }
}
