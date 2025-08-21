package net.joefoxe.hexerei.block.custom;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PickablePlant extends BushBlock implements BonemealableBlock {
    protected static final float AABB_OFFSET = 3.0F;
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    public static final int MAX_AGE = 3;
    public ResourceKey<Item> firstOutputKey;
    public int maxFirstOutput;
    public ResourceKey<Item> secondOutputKey;
    public int maxSecondOutput;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;


    public static final MapCodec<PickablePlant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    propertiesCodec(),
                    ResourceKey.codec(Registries.ITEM).fieldOf("firstOutput").forGetter(d -> d.firstOutputKey),
                    Codec.INT.fieldOf("maxFirstOutput").forGetter(d -> d.maxFirstOutput),
                    ResourceKey.codec(Registries.ITEM).fieldOf("secondOutput").forGetter(d -> d.secondOutputKey),
                    Codec.INT.fieldOf("maxSecondOutput").forGetter(d -> d.maxSecondOutput)
            )
            .apply(instance, PickablePlant::new));

    public PickablePlant(BlockBehaviour.Properties properties, ResourceKey<Item> firstOutput , int maxFirstOutput, ResourceKey<Item> secondOutput , int maxSecondOutput) {
        super(properties);

        this.firstOutputKey = firstOutput;
        this.maxFirstOutput = maxFirstOutput;
        this.secondOutputKey = secondOutput;
        this.maxSecondOutput = maxSecondOutput;

        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));

    }

    public VoxelShape getShape(BlockState p_53517_, BlockGetter p_53518_, BlockPos p_53519_, CollisionContext p_53520_) {
        Vec3 vec3 = p_53517_.getOffset(p_53518_, p_53519_);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public BlockBehaviour.OffsetType getOffsetType() {
        return BlockBehaviour.OffsetType.XZ;
    }


    public boolean isRandomlyTicking(BlockState p_57284_) {
        return p_57284_.getValue(AGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        if (i < 3 && level.getRawBrightness(pos.above(), 0) >= 9 && net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt(5) == 0)) {
            BlockState blockstate = state.setValue(AGE, Integer.valueOf(i + 1));
            level.setBlock(pos, blockstate, 2);
            net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockstate));
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        return !flag && stack.is(Items.BONE_MEAL)
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        if (i > 1) {
            ItemStack firstOutput = new ItemStack(DataFixUtils.orElse(level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.firstOutputKey), this), Math.max(1, level.random.nextInt(this.maxFirstOutput)) / (flag ? 1 : 2));
            ItemStack secondOutput = ItemStack.EMPTY;
            if (this.secondOutputKey != null)
                secondOutput = new ItemStack(DataFixUtils.orElse(level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.secondOutputKey), this), Math.max(1, level.random.nextInt(this.maxSecondOutput)) / (flag ? 1 : 2));
            popResource(level, pos, firstOutput);
            if (level.random.nextInt(2) == 0 && this.secondOutputKey != null)
                popResource(level, pos, secondOutput);
            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(pos, state.setValue(AGE, 0), 2);

            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, level.getBlockState(pos)));
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57282_) {
        p_57282_.add(AGE);
    }
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    public boolean isBonemealSuccess(Level p_57265_, RandomSource p_57266_, BlockPos p_57267_, BlockState p_57268_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_57251_, RandomSource p_57252_, BlockPos p_57253_, BlockState p_57254_) {
        int i = Math.min(3, p_57254_.getValue(AGE) + 1);
        p_57251_.setBlock(p_57253_, p_57254_.setValue(AGE, i), 2);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}