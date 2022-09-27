package net.joefoxe.hexerei.client.renderer.entity.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModChestBoatEntity extends ChestBoat {


    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);
    public ModChestBoatEntity(EntityType<ModChestBoatEntity> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Type.WILLOW.ordinal());
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putString("model", getModel().getName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("model", Tag.TAG_STRING)) {
            this.entityData.set(DATA_ID_TYPE, Type.byName(nbt.getString("model")).ordinal());
        }
    }

    public void setType(Type pBoatType) {
        this.entityData.set(DATA_ID_TYPE, pBoatType.ordinal());
    }

    public Boat.Type getBoatType() {
        return Boat.Type.OAK;
    }

    public Type getModBoatType() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    public Item getDropItem() {
        switch (Type.byId(this.entityData.get(DATA_ID_TYPE))) {
            case WILLOW:
            default:
                return ModItems.WILLOW_CHEST_BOAT.get();
            case POLISHED_WILLOW:
                return ModItems.POLISHED_WILLOW_CHEST_BOAT.get();
            case MAHOGANY:
                return ModItems.MAHOGANY_CHEST_BOAT.get();
            case POLISHED_MAHOGANY:
                return ModItems.POLISHED_MAHOGANY_CHEST_BOAT.get();
        }
    }



    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        this.lastYd = getDeltaMovement().y;
        if (!isPassenger()) {
            if (onGround) {
                if (this.fallDistance > 3f) {
                    if (this.status != Status.ON_LAND) {
                        this.fallDistance = 0f;
                        return;
                    }
                    causeFallDamage(this.fallDistance, 1f, DamageSource.FALL);
                    if (!this.level.isClientSide && !this.isRemoved()) {
                        this.remove(RemovalReason.KILLED);
                        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            for (int i = 0; i < 3; ++i) {
                                spawnAtLocation(getModel().getPlanks());
                            }
                            for (int j = 0; j < 2; ++j) {
                                spawnAtLocation(Items.STICK);
                            }
                        }
                    }
                }
                this.fallDistance = 0f;
            } else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0d) {
                this.fallDistance = (float) ((double) this.fallDistance - y);
            }
        }
    }

    public ModChestBoatEntity withModel(Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
        return this;
    }

    public Type getModel() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }


    public enum Type {
        WILLOW("willow", ModBlocks.WILLOW_PLANKS),
        POLISHED_WILLOW("polished_willow", ModBlocks.POLISHED_WILLOW_PLANKS),
        MAHOGANY("mahogany", ModBlocks.MAHOGANY_PLANKS),
        POLISHED_MAHOGANY("polished_mahogany", ModBlocks.POLISHED_MAHOGANY_PLANKS);

        private final String name;
        private final Supplier<Block> supplierPlanks;

        Type(String name, Supplier<Block> supplierPlanks) {
            this.name = name;
            this.supplierPlanks = supplierPlanks;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.supplierPlanks.get();
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int id) {
            Type[] type = values();
            return type[id < 0 || id >= type.length ? 0 : id];
        }

        public static Type byName(String aName) {
            Type[] type = values();
            return Arrays.stream(type).filter(t -> t.getName().equals(aName)).findFirst().orElse(type[0]);
        }
    }
}
