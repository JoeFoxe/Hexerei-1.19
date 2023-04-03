package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.TreeCutter;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CuttingCrystalTile extends BlockEntity {

    public List<BlockPos> boundPos = new ArrayList<>();
    public boolean isParent;

    public static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();
    protected int ticksUntilNextProgress;
    protected float destroyProgress;
    protected int breakerId = -NEXT_BREAKER_ID.incrementAndGet();
    protected BlockPos breakingPos;

    public CuttingCrystalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }




    public void cutTree(Level level, BlockPos breakingPos) {

        TreeCutter.findTree(level, breakingPos)
                .destroyBlocks(level, null, this::dropItemFromCutTree);
    }

    public void dropItemFromCutTree(BlockPos pos, ItemStack stack) {
        float distance = (float) Math.sqrt(pos.distSqr(breakingPos));
        Vec3 dropPos = new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        ItemEntity entity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, stack);
//        entity.setDeltaMovement(Vec3.atLowerCornerOf(breakingPos.subtract(this.worldPosition))
//                .scale(distance / 20f));
        level.addFreshEntity(entity);
    }

    protected boolean shouldRun() {
        if(isParent)
            return true;

        return !boundPos.isEmpty() && boundPos.get(0) != null;
    }
    protected BlockPos getBreakingPos() {
        return getBlockPos().relative(getBlockState().getValue(HorizontalDirectionalBlock.FACING));
    }



    public void destroyNextTick() {
        ticksUntilNextProgress = 1;
    }

    private boolean posEquals(BlockPos pos, BlockPos pos2){
        return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
    }
    public void tick() {

        if (shouldRun() && ticksUntilNextProgress < 0)
            destroyNextTick();

        if (!shouldRun())
            return;

        BlockPos lastPos = breakingPos;
        breakingPos = null;

        if (ticksUntilNextProgress < 0)
            return;
        if (ticksUntilNextProgress-- > 0)
            return;

        BlockPos thisPos = getBlockPos();
        for(BlockPos pos : boundPos){
            if(posEquals(thisPos, pos))
                continue;
            Vec3 vec3_1 = HexereiUtil.getCenterOf(thisPos);
            Vec3 vec3_2 = HexereiUtil.getCenterOf(pos);
            Vec3 vec3_3 = vec3_2.subtract(vec3_1).normalize();
            Vec3 vec3_4 = vec3_1.subtract(vec3_2).normalize();
            BlockHitResult result = level.clip(new ClipContext(vec3_1.add(vec3_3), HexereiUtil.getCenterOf(pos).subtract(vec3_4), net.minecraft.world.level.ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
            if(result.getType() == HitResult.Type.BLOCK && !posEquals(result.getBlockPos(), pos) && !posEquals(result.getBlockPos(), thisPos)){
                breakingPos = result.getBlockPos();
                level.addParticle(ModParticleTypes.EXTINGUISH.get(), thisPos.getX() + 0.5f, thisPos.getY() + 0.5f, thisPos.getZ() + 0.5f, (pos.getX() - thisPos.getX()) / 50f, (pos.getY() - thisPos.getY()) / 50f, (pos.getZ() - thisPos.getZ()) / 50f);
            }

            if(posEquals(pos, thisPos))
                continue;

            level.addParticle(ModParticleTypes.EXTINGUISH.get(), thisPos.getX() + 0.5f, thisPos.getY() + 0.5f, thisPos.getZ() + 0.5f, (pos.getX() - thisPos.getX()) / 50f, (pos.getY() - thisPos.getY()) / 50f, (pos.getZ() - thisPos.getZ()) / 50f);
        }

        if(breakingPos == null) {
            destroyProgress = 0;
            return;
        }



        BlockState stateToBreak = level.getBlockState(breakingPos);
        float blockHardness = stateToBreak.getDestroySpeed(level, breakingPos);

        if (!canBreak(stateToBreak, blockHardness)) {
            if (destroyProgress != 0) {
                destroyProgress = 0;
                if (!level.isClientSide)
                    level.destroyBlockProgress(breakerId, breakingPos, -1);
            }
            return;
        }

        float breakSpeed = getBreakSpeed();
        destroyProgress += Mth.clamp((breakSpeed / blockHardness), 0, 10 - destroyProgress);
        if (!level.isClientSide)
            level.playSound(null, worldPosition, stateToBreak.getSoundType()
                .getHitSound(), SoundSource.NEUTRAL, .25f, 1);

        if (destroyProgress >= 10) {
            if (!level.isClientSide)
                onBlockBroken(stateToBreak);
            destroyProgress = 0;
            ticksUntilNextProgress = -1;
            if (!level.isClientSide)
                level.destroyBlockProgress(breakerId, breakingPos, -1);
            return;
        }

        ticksUntilNextProgress = (int) (blockHardness / breakSpeed);
        if (!level.isClientSide)
            level.destroyBlockProgress(breakerId, breakingPos, (int) destroyProgress);
    }

    public boolean canBreak(BlockState stateToBreak, float blockHardness) {
        return isBreakable(stateToBreak, blockHardness);
    }

    public static boolean isBreakable(BlockState stateToBreak, float blockHardness) {
        return !(stateToBreak.getMaterial()
                .isLiquid() || stateToBreak.getBlock() instanceof AirBlock || blockHardness == -1) && (stateToBreak.is(BlockTags.LOGS) || stateToBreak.is(BlockTags.LEAVES));
    }

    public void onBlockBroken(BlockState stateToBreak) {
        BlockPos pos = getBlockPos();
            Vec3 vec = HexereiUtil.offsetRandomly(HexereiUtil.getCenterOf(breakingPos), level.random, .125f);
            HexereiUtil.destroyBlock(level, breakingPos, 1f, (stack) -> {
                if (stack.isEmpty())
                    return;
                if (!level.getGameRules()
                        .getBoolean(GameRules.RULE_DOBLOCKDROPS))
                    return;
                if (level.restoringBlockSnapshots)
                    return;

                ItemEntity itementity = new ItemEntity(level, vec.x, vec.y, vec.z, stack);
                itementity.setDefaultPickUpDelay();
                itementity.setDeltaMovement(Vec3.ZERO);
                level.addFreshEntity(itementity);
            });

            if(stateToBreak.is(BlockTags.LOGS))
                TreeCutter.findTree(level, breakingPos).destroyBlocks(level, null, this::dropItemFromCutTree);
    }

    protected float getBreakSpeed() {
        return Math.abs(100 / 100f);
    }


    @Override
    public void setChanged() {
        super.setChanged();
    }

    public void sync() {
        setChanged();

        if(level != null){
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {

        return super.getCapability(cap);
    }

    public CuttingCrystalTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CUTTING_CRYSTAL_TILE.get(),blockPos, blockState);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("boundPos")) {
            int size = pTag.getInt("size");
            List<BlockPos> newList = new ArrayList<>();
            CompoundTag boundList = pTag.getCompound("boundPos");

            for(int i = 0; i < size; i++){
                newList.add(NbtUtils.readBlockPos(boundList.getCompound("boundPos" + i)));
            }

            this.boundPos = newList;
        }
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        int size = boundPos.size();
        compound.putInt("size", size);
        CompoundTag boundList = new CompoundTag();
        for(int i = 0; i < size; i++){
            boundList.put("boundPos" + i, NbtUtils.writeBlockPos(this.boundPos.get(i)));
        }
        if(!boundList.isEmpty())
            compound.put("boundPos", boundList);
    }


//    @Override
    public CompoundTag save(CompoundTag compound) {
        super.saveAdditional(compound);
        int size = boundPos.size();
        compound.putInt("size", size);
        CompoundTag boundList = new CompoundTag();
        for(int i = 0; i < size; i++){
            boundList.put("boundPos" + i, NbtUtils.writeBlockPos(this.boundPos.get(i)));
        }
        if(!boundList.isEmpty())
            compound.put("boundPos", boundList);

        return compound;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag) -> this.getUpdateTag());
    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.position().x() - pos.getX() - 0.5f;
        double deltaY = entity.position().y() - pos.getY() - 0.5f;
        double deltaZ = entity.position().z() - pos.getZ() - 0.5f;

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    public static double getDistance(float x1, float y1, float x2, float y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }


//    @Override
//    public double getMaxRenderDistanceSquared() {
//        return 4096D;
//    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(5, 5, 5);
    }

    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.getBlockPos().getZ() - 0.5f, pos.x() - this.getBlockPos().getX() - 0.5f));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public Vec3 rotateAroundVec(Vec3 vector3dCenter,float rotation,Vec3 vector3d)
    {
        Vec3 newVec = vector3d.subtract(vector3dCenter);
        newVec = newVec.yRot(rotation/180f*(float)Math.PI);
        newVec = newVec.add(vector3dCenter);

        return newVec;
    }

}
