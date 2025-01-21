package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nonnull;

public class BloodFluid extends FlowingFluid {

	@Override
	public Fluid getFlowing() {
		return ModFluids.BLOOD_FLOWING.get();
	}

	@Override
	public Fluid getSource() {
		return ModFluids.BLOOD_FLUID.get();
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		Block.dropResources(state, worldIn, pos, tileentity);
	}

	@Override
	public Item getBucket() {
		return ModItems.BLOOD_BUCKET.get();
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
	}

	@Override
	protected float getExplosionResistance() {
		return 100f;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModFluids.BLOOD_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSource(@Nonnull FluidState state) {
		return false;
	}

	@Override
	public int getAmount(@Nonnull FluidState state) {
		return 0;
	}

	public boolean isSame(Fluid fluid) {
		return fluid == ModFluids.BLOOD_FLUID.get() || fluid == ModFluids.BLOOD_FLOWING.get();
	}

	public int getSlopeFindDistance(LevelReader level) {
		return 2;
	}

	public int getDropOff(LevelReader level) {
		return 2;
	}

	public int getTickDelay(LevelReader level) {
		return 5;
	}

	@Override
	protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
			}
		} else if (random.nextInt(12) == 0 && !isSource(state)) {
			if (random.nextInt(3) == 0)
				level.addParticle(ModParticleTypes.BLOOD.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8.0f)), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.02D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
			level.addParticle(ModParticleTypes.BLOOD_BIT.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8.0f)), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));

		} else if (random.nextInt(14) == 0 && isSource(state)) {
			if (random.nextInt(2) == 0)
				level.addParticle(ModParticleTypes.BLOOD.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble()), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
			level.addParticle(ModParticleTypes.BLOOD_BIT.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble()), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));

		}
		super.animateTick(level, pos, state, random);
	}

	@Override
	public FluidType getFluidType() {
		return ModFluidTypes.BLOOD_FLUID_TYPE.value();
	}

	public static class Flowing extends BloodFluid {
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public BlockState createLegacyBlock(FluidState state) {
			return (BlockState) ModFluids.BLOOD_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends BloodFluid {
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}


//
//	public static class Flowing extends ForgeFlowingFluid {
//		protected Flowing(Properties properties) {
//			super(properties);
//		}
//
//		protected void createFluidStateDefinition(@Nonnull StateDefinition.Builder<Fluid, FluidState> builder) {
//			super.createFluidStateDefinition(builder);
//			builder.add(LEVEL);
//		}
//
//		public int getAmount(FluidState state) {
//			return state.getValue(LEVEL);
//		}
//
//		public boolean isSource(@Nonnull FluidState state) {
//			return false;
//		}
//
//		@OnlyIn(Dist.CLIENT)
//		public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random) {
//			if (!state.isSource() && !state.getValue(FALLING)) {
//				if (random.nextInt(64) == 0) {
//					worldIn.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
//				}
//			} else if (random.nextInt(12) == 0) {
//				if (random.nextInt(3) == 0)
//					worldIn.addParticle(ModParticleTypes.BLOOD.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8.0f)), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.02D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
//				worldIn.addParticle(ModParticleTypes.BLOOD_BIT.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8.0f)), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
//
//			}
//			//worldIn.addParticle(ModParticleTypes.BLOOD.get(), (double)pos.getX() + random.nextDouble(), (double)pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8)), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
//		}
//
//	}
//
//
//
//	public static class Source extends ForgeFlowingFluid {
//		protected Source(Properties properties) {
//			super(properties);
//		}
//
//		public int getAmount(@Nonnull FluidState state) {
//			return 8;
//		}
//
//		public boolean isSource(@Nonnull FluidState state) {
//			return true;
//		}
//
//		@OnlyIn(Dist.CLIENT)
//		public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random) {
//			if (!state.isSource() && !state.getValue(FALLING)) {
//				if (random.nextInt(64) == 0) {
//					worldIn.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
//				}
//			} else if (random.nextInt(14) == 0) {
//				if (random.nextInt(2) == 0)
//					worldIn.addParticle(ModParticleTypes.BLOOD.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble()), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
//				worldIn.addParticle(ModParticleTypes.BLOOD_BIT.get(), (double) pos.getX() + random.nextDouble(), (double) pos.getY() + (random.nextDouble()), (double) pos.getZ() + random.nextDouble(), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02), -0.01D + (random.nextDouble() * 0.02));
//
//			}
//			//worldIn.addParticle(ModParticleTypes.BLOOD.get(), (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
//		}
//
//	}

}
