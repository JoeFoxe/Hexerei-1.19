package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.item.ModItems;
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
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nonnull;

public abstract class TallowFluid extends ForgeFlowingFluid {

	protected TallowFluid(Properties properties) {
		super(properties);
	}

	@Override
	public Fluid getFlowing() {
		return ModFluids.TALLOW_FLOWING.get();
	}

	@Override
	public Fluid getSource() {
		return ModFluids.TALLOW_FLUID.get();
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		Block.dropResources(state, worldIn, pos, tileentity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader worldIn) {
		return 3;
	}

	@Override
	protected int getDropOff(LevelReader worldIn) {
		return 2;
	}

	@Override
	public Item getBucket() {
		return ModItems.TALLOW_BUCKET.get();
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(LevelReader p_205569_1_) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100f;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModFluids.TALLOW_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(state)));
	}

	@Override
	public boolean isSource(@Nonnull FluidState state) {
		return false;
	}

	@Override
	public int getAmount(@Nonnull FluidState state) {
		return 0;
	}


	public static class Flowing extends ForgeFlowingFluid
	{
		public Flowing(Properties properties)
		{
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}

		@OnlyIn(Dist.CLIENT)
		public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random) {
			if (!state.isSource() && !state.getValue(FALLING)) {
				if (random.nextInt(64) == 0) {
					worldIn.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
				}
			}
			//worldIn.addParticle(ModParticleTypes.TALLOW.get(), (double)pos.getX() + random.nextDouble(), (double)pos.getY() + (random.nextDouble() * (state.getValue(LEVEL) / 8)), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	public static class Source extends ForgeFlowingFluid
	{
		public Source(Properties properties)
		{
			super(properties);
		}

		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}

		@OnlyIn(Dist.CLIENT)
		public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random) {
			if (!state.isSource() && !state.getValue(FALLING)) {
				if (random.nextInt(64) == 0) {
					worldIn.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
				}
			}
			//worldIn.addParticle(ModParticleTypes.TALLOW.get(), (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

}
