package net.joefoxe.hexerei.client.renderer.entity.custom;

import javax.annotation.Nullable;

import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HexereiPaintingEntity extends HangingEntity {
    public static final EntityDataAccessor<String> PAINTING_LOCATION = SynchedEntityData.defineId(HexereiPaintingEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> U0 = SynchedEntityData.defineId(HexereiPaintingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> U1 = SynchedEntityData.defineId(HexereiPaintingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> V0 = SynchedEntityData.defineId(HexereiPaintingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> V1 = SynchedEntityData.defineId(HexereiPaintingEntity.class, EntityDataSerializers.FLOAT);

    public HexereiPaintingEntity(EntityType<? extends HexereiPaintingEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PAINTING_LOCATION, "minecraft:missingno");
        builder.define(U0, 0f);
        builder.define(U1, 1f);
        builder.define(V0, 0f);
        builder.define(V1, 1f);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
//        if (DATA_PAINTING_VARIANT_ID.equals(key)) {
//            this.recalculateBoundingBox();
//        }

    }

    public void setU0(float value) {
        this.entityData.set(U0, value);
    }

    public float getU0() {
        return this.entityData.get(U0);
    }

    public void setU1(float value) {
        this.entityData.set(U1, value);
    }

    public float getU1() {
        return this.entityData.get(U1);
    }

    public void setV0(float value) {
        this.entityData.set(V0, value);
    }

    public float getV0() {
        return this.entityData.get(V0);
    }

    public void setV1(float value) {
        this.entityData.set(V1, value);
    }

    public float getV1() {
        return this.entityData.get(V1);
    }

    public void setPaintingLocation(String value) {
        this.entityData.set(PAINTING_LOCATION, value);
    }

    public String getPaintingLocation() {
        return this.entityData.get(PAINTING_LOCATION);
    }


    @Override
    public void setDirection(Direction facingDirection) {
        super.setDirection(facingDirection);
    }

    private HexereiPaintingEntity(Level level, BlockPos pos) {
        super(ModEntityTypes.BOOK_CANVAS.get(), level, pos);
    }

    private HexereiPaintingEntity(Level level, BlockPos pos, Direction direction) {
        this(level, pos);
        this.setDirection(direction);
    }

    public static HexereiPaintingEntity create(Level level, BlockPos pos, Direction direction) {
        return new HexereiPaintingEntity(level, pos, direction);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("painting_location", getPaintingLocation());
        compound.putFloat("U0", getU0());
        compound.putFloat("U1", getU1());
        compound.putFloat("V0", getV0());
        compound.putFloat("V1", getV1());
        compound.putByte("facing", (byte)this.direction.get2DDataValue());
        super.addAdditionalSaveData(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        setPaintingLocation(compound.getString("painting_location"));
        setU0(compound.getFloat("U0"));
        setU1(compound.getFloat("U1"));
        setV0(compound.getFloat("V0"));
        setV1(compound.getFloat("V1"));
        this.direction = Direction.from2DDataValue(compound.getByte("facing"));
        super.readAdditionalSaveData(compound);
        this.setDirection(this.direction);
    }

    protected AABB calculateBoundingBox(BlockPos pos, Direction p_direction) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(p_direction, (double)-0.46875F);
        double d0 = this.offsetForPaintingSize(1);
        double d1 = this.offsetForPaintingSize(1);
        Direction direction = p_direction.getCounterClockWise();
        Vec3 vec31 = vec3.relative(direction, d0).relative(Direction.UP, d1);
        Direction.Axis direction$axis = p_direction.getAxis();
        double d2 = direction$axis == Axis.X ? (double)0.0625F : (double)1;
        double d3 = (double)1;
        double d4 = direction$axis == Axis.Z ? (double)0.0625F : (double)1;
        return AABB.ofSize(vec31, d2, d3, d4);
    }

    private double offsetForPaintingSize(int size) {
        return size % 2 == 0 ? (double)0.5F : (double)0.0F;
    }

    public void dropItem(@Nullable Entity brokenEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player) {
                Player player = (Player)brokenEntity;
                if (player.hasInfiniteMaterials()) {
                    return;
                }
            }
            ItemStack stack = ModItems.BOOK_CANVAS.get().getDefaultInstance();
            if (!ResourceLocation.parse(getPaintingLocation()).equals(ResourceLocation.withDefaultNamespace("missingno"))){
                CompoundTag tag = new CompoundTag();
                tag.putString("painting_location", getPaintingLocation());
                tag.putFloat("U0", getU0());
                tag.putFloat("U1", getU1());
                tag.putFloat("V0", getV0());
                tag.putFloat("V1", getV1());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
            this.spawnAtLocation(stack);
        }

    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
    }

    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.setPos(x, y, z);
    }

    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    public ItemStack getPickResult() {
        return new ItemStack(ModItems.BOOK_CANVAS);
    }

}