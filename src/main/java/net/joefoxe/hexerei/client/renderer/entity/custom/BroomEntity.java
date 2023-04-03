package net.joefoxe.hexerei.client.renderer.entity.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomAttachmentItem;
import net.joefoxe.hexerei.item.custom.BroomBrushItem;
import net.joefoxe.hexerei.item.custom.BroomTickableAttachmentItem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BroomEntity extends Entity implements Container, MenuProvider, HasCustomInventoryScreen {
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FORWARD_DIRECTION = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> LEFT_PADDLE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT_PADDLE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ROCKING_TICKS = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> BROOM_TYPE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> FIRST_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SECOND_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> THIRD_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
    public float speedMultiplier = 0.75f;
    //    protected boolean charging;
    private final float[] paddlePositions = new float[2];
    private float outOfControlTicks;
    public float deltaRotation;
    public float floatingOffset;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    public boolean leftInputDown;
    public boolean rightInputDown;
    public boolean forwardInputDown;
    public boolean backInputDown;
    public boolean jumpInputDown;
    public boolean sneakingInputDown;
    private double waterLevel;
    private float boatGlide;
    private BroomEntity.Status status;
    private BroomEntity.Status previousStatus;
    private double lastYd;
    private boolean rocking;
    private boolean downwards;
    private float rockingIntensity;
    private float rockingAngle;
    private float prevRockingAngle;
    private final int drainTimeMax = 200;
    private int drainTime = drainTimeMax;
    private final int miscDrainTimeMax = 20;
    private int miscDrainTime = miscDrainTimeMax;
    private boolean broomSync = false;
    public boolean floatMode = false;
    public boolean isItem = false;
    public boolean broomCalled = false;
    public int broomCalledDelay = 40;
    public ItemStack selfItem = null;
    public float age = 0;

    //this is a static uuid that will be saved across the broom when broken and placed again - for the whistle to bind to.
    public UUID broomUUID;

    public final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public NonNullList<ItemStack> items = NonNullList.withSize(30, ItemStack.EMPTY);


    public BroomEntity(Level worldIn, double x, double y, double z) {
        this(ModEntityTypes.BROOM.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        speedMultiplier = getBroomType().speedMultiplier();

    }

    public BroomEntity(EntityType<BroomEntity> broomEntityEntityType, Level world) {
        super(broomEntityEntityType, world);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(FORWARD_DIRECTION, 1);
        this.entityData.define(DAMAGE_TAKEN, 0.0F);
        this.entityData.define(LEFT_PADDLE, false);
        this.entityData.define(RIGHT_PADDLE, false);
        this.entityData.define(ROCKING_TICKS, 0);
        this.entityData.define(FIRST_SLOT, 0);
        this.entityData.define(SECOND_SLOT, 0);
        this.entityData.define(THIRD_SLOT, 0);
        this.entityData.define(BROOM_TYPE, "willow");
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return func_242378_a(this, entity);
    }

    public static boolean func_242378_a(Entity p_242378_0_, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /**
     * Returns true if this entity should pushPose and be pushed by other entities when colliding.
     */
    @Override
    public boolean isPushable() {
        return !broomCalled;
    }

    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle result) {
        return PortalShape.getRelativePosition(result, axis, this.position(), this.getDimensions(this.getPose()));
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getPassengersRidingOffset() {
        float height = 1.8f;
        if (this.getControllingPassenger() != null) {
            height = this.getControllingPassenger().getBbHeight();

        }

        return floatingOffset - height;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            this.markHurt();
            boolean flag = source.getDirectEntity() instanceof Player player && player.getAbilities().instabuild;
            if (flag || this.getDamageTaken() > 50.0F) {
                if (!flag) {
                    level.addFreshEntity(new ItemEntity(level, blockPosition().getX() + 0.5f, blockPosition().getY() + 0.5f, blockPosition().getZ() + 0.5f, getPickResult()));
                }

                this.remove(RemovalReason.DISCARDED);
            }

            return true;
        } else {
            return true;
        }
    }


    public static DyeColor getDyeColorNamed(BroomEntity broom) {
        if (broom.getCustomName() == null)
            return null;


        if (broom.getCustomName().getString().equals("Thunderbolt VII"))
            return DyeColor.byId(4);

        if (broom.getCustomName().getString().equals("Firebolt"))
            return DyeColor.byId(14);

        return HexereiUtil.getDyeColorNamed(broom.getCustomName().getString(), 0);

//
//        if(broom.getCustomName().getString().equals("jeb_"))
//            return DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//
//        if(broom.getCustomName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            });
//
//        if(broom.getCustomName().getString().equals("bi_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            });
//
//        if(broom.getCustomName().getString().equals("trans_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            });
//
//        if(broom.getCustomName().getString().equals("joe_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            });
//
//        if(broom.getCustomName().getString().equals("Thunderbolt VII"))
//            return DyeColor.byId(4);
//
//        if(broom.getCustomName().getString().equals("Firebolt"))
//            return DyeColor.byId(14);

//        if(this.getName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/10) % 15)) {
//                case 1 -> 0;
//                case 2 -> 0;
//                case 3 -> 0;
//                case 4 -> 1;
//                case 5 -> 1;
//                case 6 -> 1;
//                case 7 -> 2;
//                case 8 -> 2;
//                case 9 -> 2;
//                case 10 -> 6;
//                case 11 -> 6;
//                case 12 -> 6;
//                case 13 -> 14;
//                case 14 -> 14;
//                case 15 -> 14;
//                default -> 0;
//            });


        //DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//        return null;
    }


    //TODO    @Override  REDO THIS AS IT DOESNT WORK PROPERLY
    @Override
    @NotNull
    public ItemStack getPickResult() {
        ItemStack item = getBroomItem().getDefaultInstance();

        CompoundTag tag = item.getOrCreateTag();
        CompoundTag inv = itemHandler.serializeNBT();
        boolean flag = false;
        for (int i = 0; i < 30; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                flag = true;
                break;
            }
        }
        if (flag)
            tag.put("Inventory", inv);

        tag.putBoolean("floatMode", this.floatMode);

        if (this.broomUUID != null)
            tag.putUUID("broomUUID", this.broomUUID);

        Component name = getCustomName();
        if (name != null && !name.getString().isEmpty())
            item.setHoverName(name);
        return item;
    }


    @Override
    public void onAboveBubbleCol(boolean downwards) {
        if (!this.level.isClientSide) {
            this.rocking = true;
            this.downwards = downwards;
            if (this.getRockingTicks() == 0) {
                this.setRockingTicks(60);
            }
        }

        this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double) this.random.nextFloat(), this.getY() + 0.7D, this.getZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
        }

    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    @Override
    public void push(Entity entityIn) {
        if (entityIn instanceof BroomEntity) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entityIn);
        }

    }

    public Item getBroomItem() {
        return getBroomType().item();
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateHurt() {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 10;
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    public int getExtraBrush() {
        for (int i = 3; i < 30; i++) {
            if (this.itemHandler.getStackInSlot(i).is(HexereiTags.Items.BROOM_BRUSH))
                return i;
        }
        return -1;
    }

    public void damageBrush() {
        this.getModule(BroomSlot.BRUSH).hurt(1, RandomSource.create(), null);
        if (this.getModule(BroomSlot.BRUSH).getDamageValue() >= this.getModule(BroomSlot.BRUSH).getMaxDamage()) {
            this.setModule(BroomSlot.BRUSH, ItemStack.EMPTY);
            this.level.playSound(null, this, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.5F, random.nextFloat() * 0.4F + 1.0F);
            sync();
        }
        for (BroomSlot slot : BroomSlot.values()) {
            if (getModule(slot).getItem() instanceof BroomAttachmentItem broomAttachment) {
                broomAttachment.onBrushDamage(this, random);
            }
        }
    }

    public void transferBrushParticles() {

        for (int i = 0; i < 20; ++i) {

            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;
            double d3 = random.nextFloat() * (float) j;
            double d4 = ((double) random.nextFloat() - 0.5D) * 0.125D;
            double d5 = random.nextFloat() * (float) k;

            float rotOffset = random.nextFloat() * 10 - 5;
            float rot = random.nextFloat() * 360;

            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.DRAGON_BREATH,
                        getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4) + (Math.cos(rot) * ((random.nextDouble() * 0.05d) + 0.5d)),
                        getY() + floatingOffset + 0.1f * random.nextFloat() - this.getDeltaMovement().y(),
                        getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4) + (Math.sin(rot) * ((random.nextDouble() * 0.05d) + 0.5d)),
                        -Math.cos(rot) * ((random.nextDouble() * 0.005d) + 0.025d), (random.nextDouble() - 0.5d) * 0.005d, -Math.sin(rot) * ((random.nextDouble() * 0.005d) + 0.025d));
            }

            level.addParticle(ParticleTypes.PORTAL,
                    getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                    getY() + floatingOffset + 0.1f * random.nextFloat() - this.getDeltaMovement().y(),
                    getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                    d3, d4, d5);


            if (this.getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
                if (brushItem.list != null) {
                    Random random = new Random();
                    for (Tuple<ParticleOptions, Integer> tuple : brushItem.list) {
                        ParticleOptions option = tuple.getA();
                        int delay = tuple.getB();
                        if (random.nextInt(delay) == 0) {

                            level.addParticle(option,
                                    getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                                    getY() + floatingOffset + 0.25f * random.nextFloat() - this.getDeltaMovement().y(),
                                    getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                                    (random.nextDouble() - 0.5d) * 0.05d, (random.nextDouble() - 0.5d) * 0.05d, (random.nextDouble() - 0.5d) * 0.05d);
                        }
                    }
                }
            }

        }
        for (int i = 0; i < 20; ++i) {
            float rotOffset = random.nextFloat() * 10 - 5;

            float rot = 18 * i;

            if (this.getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
                if (brushItem.list != null) {
                    Random random = new Random();
                    for (Tuple<ParticleOptions, Integer> tuple : brushItem.list) {
                        ParticleOptions option = tuple.getA();
                        int delay = tuple.getB();
                        if (random.nextInt(delay) == 0) {

                            level.addParticle(option,
                                    getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                                    getY() + floatingOffset + 0.1f * random.nextFloat() - this.getDeltaMovement().y(),
                                    getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + this.getDeltaMovement().length() / 4),
                                    Math.cos(rot) * ((random.nextDouble() * 0.005d) + 0.15d), (random.nextDouble() - 0.5d) * 0.005d, Math.sin(rot) * ((random.nextDouble() * 0.005d) + 0.15d));
                        }
                    }
                }
            }
        }
    }

    public void damageMisc() {
        this.getModule(BroomSlot.MISC).hurt(1, RandomSource.create(), null);
        if (this.getModule(BroomSlot.MISC).getDamageValue() >= this.getModule(BroomSlot.MISC).getMaxDamage()) {
            this.setModule(BroomSlot.MISC, ItemStack.EMPTY);
            this.level.playSound(null, this, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
            sync();
        }
    }

    @Override
    public void startOpen(Player pPlayer) {
        Container.super.startOpen(pPlayer);
        SoundEvent sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        float volume = 0.75f;
        if (isEnder()) {
            sound = SoundEvents.ENDER_CHEST_OPEN;
            volume = 0.5f;
        }

        level.playSound(null, this.getX(), this.getY() + 0.5D, this.getZ(), sound, SoundSource.BLOCKS, volume, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void stopOpen(Player p_18954_) {
        Container.super.stopOpen(p_18954_);
        SoundEvent sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        float pitch = 0.4f;
        float volume = 0.75f;
        if (isEnder()) {
            sound = SoundEvents.ENDER_CHEST_CLOSE;
            pitch = 0.9f;
            volume = 0.5f;
        }

        level.playSound(null, this.getX(), this.getY() + 0.5D, this.getZ(), sound, SoundSource.BLOCKS, volume, level.random.nextFloat() * 0.1F + pitch);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {

        ++this.age;

        if (!this.broomSync && this.level instanceof ServerLevel) {
            sync();
            this.broomSync = true;
        }
        if (!this.broomSync && this.level instanceof ClientLevel) {

            if (level.isClientSide)
                HexereiPacketHandler.sendToServer(new BroomAskForSyncPacket(this));

            this.broomSync = true;
        }

        this.previousStatus = this.status;
        this.status = this.getBoatStatus();
        if (this.status != BroomEntity.Status.UNDER_WATER && this.status != BroomEntity.Status.UNDER_FLOWING_WATER) {
            this.outOfControlTicks = 0.0F;
        } else {
            ++this.outOfControlTicks;
            if (this.getModule(BroomSlot.MISC).is(ModItems.BROOM_WATERPROOF_TIP.get()))
                this.outOfControlTicks = 0.0F;
        }

        if (!this.level.isClientSide && this.outOfControlTicks >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        Entity entityPassenger = this.getControllingPassenger();
        if (this.level.isClientSide()) {
            if (entityPassenger instanceof LivingEntity && entityPassenger.equals(Hexerei.proxy.getPlayer())) {
                LocalPlayer player = (LocalPlayer) Hexerei.proxy.getPlayer();
                this.setNoGravity(true);
                updateInputs(player.input.left, player.input.right, player.input.up, player.input.down, player.input.jumping, player.input.shiftKeyDown);
            } else if (entityPassenger == null) {
                this.setNoGravity(floatMode);
            }
        } else {
            if (entityPassenger instanceof Player) {
                this.setNoGravity(true);
            } else if (entityPassenger == null) {
                this.setNoGravity(floatMode);
            }
        }
        if (!this.getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
            this.setNoGravity(false);
            this.floatMode = false;
        } else {
            if (this.level.isClientSide()) {
                if (entityPassenger instanceof Player && (getPaddleState(0) || getPaddleState(1))) {
                    drainTime--;
                }
                if (drainTime <= 0) {

                    HexereiPacketHandler.sendToServer(new BroomDamageBrushToServer(this));
                    drainTime = drainTimeMax;
                }
            }
        }

        for (BroomSlot slot : BroomSlot.values()) {
            if (getModule(slot).getItem() instanceof BroomTickableAttachmentItem toTick) {
                toTick.tick(this, getModule(slot));
            }
        }

        super.tick();
        this.tickLerp();


        if (isEnder() && random.nextInt(5) == 0) {

            RandomSource pRandom = random;
            int j = pRandom.nextInt(2) * 2 - 1;
            int k = pRandom.nextInt(2) * 2 - 1;
            double d3 = pRandom.nextFloat() * (float) j;
            double d4 = ((double) pRandom.nextFloat() - 0.5D) * 0.125D;
            double d5 = pRandom.nextFloat() * (float) k;

            float rotOffset = random.nextFloat() * 10 - 5;
            float rot = random.nextFloat() * 360;

            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.DRAGON_BREATH,
                        getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (0.25f + this.getDeltaMovement().length() / 4) + (Math.cos(rot) * ((random.nextDouble() * 0.05d) + 0.5d)),
                        getY() + floatingOffset + 0.1f * random.nextFloat() - this.getDeltaMovement().y(),
                        getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (0.25f + this.getDeltaMovement().length() / 4) + (Math.sin(rot) * ((random.nextDouble() * 0.05d) + 0.5d)),
                        -Math.cos(rot) * ((random.nextDouble() * 0.005d) + 0.025d), (random.nextDouble() - 0.5d) * 0.005d, -Math.sin(rot) * ((random.nextDouble() * 0.005d) + 0.025d));
            }

            level.addParticle(ParticleTypes.PORTAL,
                    getX() - Math.sin(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (0.25f + this.getDeltaMovement().length() / 4),
                    getY() + floatingOffset + 0.1f * random.nextFloat() - this.getDeltaMovement().y(),
                    getZ() + Math.cos(((this.getYRot() - 90f + deltaRotation + rotOffset) / 180f) * (Math.PI)) * (0.25f + this.getDeltaMovement().length() / 4),
                    d3, d4, d5);
        }

        if (this.isControlledByLocalInstance()) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof Player)) {
                this.setPaddleState(false, false);
            }

            if (this.getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH) && level.isClientSide) {

                floatingOffset = HexereiUtil.moveTo(floatingOffset, 0.05f + (float) Math.sin(((this.age * 2f) + (this.getId() * 1000)) / 30f) * 0.15f, 0.0075f);
            }

            this.updateMotion();
            if (this.level.isClientSide) {
                this.controlBoat();
                this.level.sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
                HexereiPacketHandler.sendToServer(new BroomSyncRotationToServer(this, getYRot(), this.getControllingPassenger()));

            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH) && this.getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
                brushItem.renderParticles(this, level, status, random);
            }
        } else {
            this.setDeltaMovement(Vec3.ZERO);
            if (this.floatMode) {
                if (level.isClientSide) {
                    floatingOffset = HexereiUtil.moveTo(floatingOffset, 0.05f + (float) Math.sin(((this.age * 2f) + (this.getId() * 1000)) / 30f) * 0.15f, 0.01f);
                }

                if (this.getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
                    brushItem.renderParticles(this, level, status, random);
                }
            } else
                floatingOffset = HexereiUtil.moveTo(floatingOffset, 0, 0.04f);
        }

        this.updateRocking();

        for (int i = 0; i <= 1; ++i) {
            if (this.getPaddleState(i)) {
                if (!this.isSilent() && (double) (this.paddlePositions[i] % ((float) Math.PI * 2F)) <= (double) ((float) Math.PI / 4F) && ((double) this.paddlePositions[i] + (double) ((float) Math.PI / 8F)) % (double) ((float) Math.PI * 2F) >= (double) ((float) Math.PI / 4F)) {
                    SoundEvent soundevent = this.getPaddleSound();
                    if (soundevent != null) {
                        Vec3 vector3d = this.getViewVector(1.0F);
                        double d0 = i == 1 ? -vector3d.z : vector3d.z;
                        double d1 = i == 1 ? vector3d.x : -vector3d.x;
                        this.level.playSound(null, this.getX() + d0, this.getY(), this.getZ() + d1, soundevent, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.paddlePositions[i] = (float) ((double) this.paddlePositions[i] + (double) ((float) Math.PI / 8F));
            } else {
                this.paddlePositions[i] = 0.0F;
            }
        }

        if (broomCalledDelay > 0) {
            this.broomCalledDelay--;
            if (this.broomCalledDelay <= 0) {
                this.broomCalled = false;
            }
        }
        this.checkInsideBlocks();
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) { //is this still used for something?
                if (!entity.hasPassenger(this)) {
                    if (flag && this.broomCalled && this.getPassengers().size() < 1 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && (entity instanceof Player player)) {
//                        setYRot(this.moveToAngle(getYRot(), player.getYRot(), 1f));

                    } else if (flag && this.getPassengers().size() < 1 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                        //entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }

        if (level.isClientSide() && this.getDeltaMovement().length() >= 0.01d && this.getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
            ItemStack misc = this.getModule(BroomSlot.MISC);
            if (misc.getItem() instanceof BroomAttachmentItem attachmentItem && attachmentItem.shouldRenderParticles(this, level, status)) {
                attachmentItem.renderParticles(this, level, status, random);
            } else {
                if (this.getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
                    brushItem.renderParticles(this, level, status, random);
                }
            }
        }

    }

    private void updateRocking() {
        if (this.level.isClientSide) {
            int i = this.getRockingTicks();
            if (i > 0) {
                this.rockingIntensity += 0.05F;
            } else {
                this.rockingIntensity -= 0.1F;
            }

            this.rockingIntensity = Mth.clamp(this.rockingIntensity, 0.0F, 1.0F);
            this.prevRockingAngle = this.rockingAngle;

            this.rockingAngle = 10.0F * (float) Math.sin(0.5F * (float) this.level.getGameTime()) * this.rockingIntensity;
        } else {
            if (!this.rocking) {
                this.setRockingTicks(0);
            }

            int k = this.getRockingTicks();
            if (k > 0) {
                --k;
                this.setRockingTicks(k);
                int j = 60 - k - 1;
                if (j > 0 && k == 0) {
                    this.setRockingTicks(0);
                    Vec3 vector3d = this.getDeltaMovement();
                    if (this.downwards) {
                        this.setDeltaMovement(vector3d.add(0.0D, -0.7D, 0.0D));
                        this.ejectPassengers();
                    } else {
                        this.setDeltaMovement(vector3d.x, this.hasPassenger(Player.class::isInstance) ? 2.7D : 0.6D, vector3d.z);
                    }
                }

                this.rocking = false;
            }
        }

    }

    @Nullable
    protected SoundEvent getPaddleSound() {
        return switch (this.getBoatStatus()) {
            case IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER -> SoundEvents.BOAT_PADDLE_WATER;
            case UNDER_LAVA, UNDER_FLOWING_LAVA, IN_AIR -> null;
            case ON_LAND -> SoundEvents.BOAT_PADDLE_LAND;
        };
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.getYRot() - (double) this.getYRot());
            this.setYRot((float) ((double) this.getYRot() + d3 / (double) this.lerpSteps));
            this.setXRot((float) ((double) this.getXRot() + (this.lerpPitch - (double) this.getXRot()) / (double) this.lerpSteps));
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }


    public void setPaddleState(boolean left, boolean right) {
        this.entityData.set(LEFT_PADDLE, left);
        this.entityData.set(RIGHT_PADDLE, right);
    }

    /**
     * Determines whether the boat is in water, gliding on land, or in air
     */
    private BroomEntity.Status getBoatStatus() {
        BroomEntity.Status boatentity$status = this.getUnderwaterStatus();
        if (boatentity$status != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return boatentity$status;
        } else if (this.checkInWater()) {
            return BroomEntity.Status.IN_WATER;
        } else {
            float f = this.getBoatGlide();
            if (f > 0.0F) {
                this.boatGlide = f;
                return BroomEntity.Status.ON_LAND;
            } else {
                return BroomEntity.Status.IN_AIR;
            }
        }
    }

    public float getWaterLevelAbove() {
        AABB axisalignedbb = this.getBoundingBox();
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.maxY);
        int l = Mth.ceil(axisalignedbb.maxY - this.lastYd);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        label39:
        for (int k1 = k; k1 < l; ++k1) {
            float f = 0.0F;

            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(l1, k1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if (fluidstate.is(FluidTags.WATER)) {
                        f = Math.max(f, fluidstate.getHeight(this.level, blockpos$mutable));
                    }

                    if (f >= 1.0F) {
                        continue label39;
                    }
                }
            }

            if (f < 1.0F) {
                return (float) blockpos$mutable.getY() + f;
            }
        }

        return (float) (l + 1);
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
    public float getBoatGlide() {
        AABB axisalignedbb = this.getBoundingBox();
        AABB axisalignedbb1 = new AABB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = Mth.floor(axisalignedbb1.minX) - 1;
        int j = Mth.ceil(axisalignedbb1.maxX) + 1;
        int k = Mth.floor(axisalignedbb1.minY) - 1;
        int l = Mth.ceil(axisalignedbb1.maxY) + 1;
        int i1 = Mth.floor(axisalignedbb1.minZ) - 1;
        int j1 = Mth.ceil(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = Shapes.create(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                if (j2 != 2) {
                    for (int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockpos$mutable.set(l1, k2, i2);
                            BlockState blockstate = this.level.getBlockState(blockpos$mutable);
                            if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, blockpos$mutable).move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                                f += blockstate.getFriction(this.level, blockpos$mutable, this);
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / (float) k1;
    }

    @Override
    public boolean fireImmune() {
        return this.getModule(BroomSlot.MISC).is(ModItems.BROOM_NETHERITE_TIP.get());
    }

    private boolean checkInWater() {

        AABB axisalignedbb = this.getBoundingBox();
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.minY);
        int l = Mth.ceil(axisalignedbb.minY + 0.001D);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(k1, l1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if (fluidstate.is(FluidTags.WATER)) {
                        float f = (float) l1 + fluidstate.getHeight(this.level, blockpos$mutable);
                        this.waterLevel = Math.max(f, this.waterLevel);
                        flag |= axisalignedbb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    /**
     * Decides whether the boat is currently underwater.
     */
    @Nullable
    private BroomEntity.Status getUnderwaterStatus() {

        AABB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.maxY);
        int l = Mth.ceil(d0);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        boolean lavaFlag = false;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(k1, l1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if (fluidstate.is(FluidTags.WATER) && d0 < (double) ((float) blockpos$mutable.getY() + fluidstate.getHeight(this.level, blockpos$mutable))) {
                        if (!fluidstate.isSource()) {
                            return BroomEntity.Status.UNDER_FLOWING_WATER;
                        }

                        flag = true;
                    } else if (fluidstate.is(FluidTags.LAVA) && d0 < (double) ((float) blockpos$mutable.getY() + fluidstate.getHeight(this.level, blockpos$mutable))) {
                        if (!fluidstate.isSource()) {
                            return BroomEntity.Status.UNDER_FLOWING_LAVA;
                        }

                        lavaFlag = true;
                    }
                }
            }
        }

        if (lavaFlag)
            return BroomEntity.Status.UNDER_LAVA;

        return flag ? BroomEntity.Status.UNDER_WATER : null;
    }

    /**
     * Update the boat's speed, based on momentum.
     */
    private void updateMotion() {
        double d0 = -0.04F;
        double d1 = this.isNoGravity() ? 0.0D : (double) -0.04F;
        double d2 = 0.0D;
        float momentum = 0.05F;
        this.speedMultiplier = getBroomType().speedMultiplier();

        if (getModule(BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brush){
            speedMultiplier += brush.getSpeedModifier();
        }

        if (this.previousStatus == BroomEntity.Status.IN_AIR && this.status != BroomEntity.Status.IN_AIR && this.status != BroomEntity.Status.ON_LAND) {
//            this.waterLevel = this.getY(1.0D);
//            this.setPos(this.getX(), (double)(this.getWaterLevelAbove() - this.getEyeY()) + 0.101D, this.getZ());
//            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            this.lastYd = 0.0D;
            this.status = BroomEntity.Status.IN_WATER;
        } else {
            Vec3 vector3d = this.getDeltaMovement();
            if (this.status == BroomEntity.Status.IN_WATER) {
                d2 = (this.waterLevel - this.getY()) / (double) this.getBbHeight();
                momentum = 0.9F;
            } else if (this.status == Status.UNDER_FLOWING_WATER || this.status == Status.UNDER_WATER) {
                d2 = (this.waterLevel - this.getY()) / (double) this.getBbHeight();
                momentum = 0.8F;
                if (this.level.isClientSide() && this.getModule(BroomSlot.MISC).is(ModItems.BROOM_WATERPROOF_TIP.get())) {

                    miscDrainTime--;
                    if (miscDrainTime <= 0) {
                        HexereiPacketHandler.sendToServer(new BroomDamageMiscToServer(this));
                        miscDrainTime = miscDrainTimeMax;
                    }
                }
            } else if (this.status == Status.UNDER_FLOWING_LAVA || this.status == Status.UNDER_LAVA) {
                d2 = (this.waterLevel - this.getY()) / (double) this.getBbHeight();
                momentum = 0.7F;

                if (this.level.isClientSide()) {

                    miscDrainTime--;
                    if (miscDrainTime <= 0) {
                        HexereiPacketHandler.sendToServer(new BroomDamageMiscToServer(this));
                        miscDrainTime = miscDrainTimeMax;
                    }
                }


            } else if (this.status == Status.IN_AIR) {
                momentum = 0.9F;
            } else if (this.status == Status.ON_LAND) {
                momentum = this.boatGlide;
                if (this.getControllingPassenger() instanceof Player) {
                    this.boatGlide /= 2.0F;
                }
            }

            this.setDeltaMovement(vector3d.x * (double) momentum, vector3d.y + d1, vector3d.z * (double) momentum);
            this.deltaRotation *= momentum;
            if (d2 > 0.0D) {
                Vec3 vector3d1 = this.getDeltaMovement();
                this.setDeltaMovement(vector3d1.x, (vector3d1.y + d2 * 0.06153846016296973D) * 0.75D, vector3d1.z);
            }
        }
        if (this.isNoGravity() && Mth.abs((float) (this.getDeltaMovement().y() / (1.15f + (Mth.abs((float) this.getDeltaMovement().y()) / 6f)))) > 0f)
            this.setDeltaMovement(this.getDeltaMovement().x(), Mth.abs((float) (this.getDeltaMovement().y() / (1.15f + (Mth.abs((float) this.getDeltaMovement().y()) / 6f)))) < 0.1f ? 0 : this.getDeltaMovement().y() / (1.15f + (Mth.abs((float) this.getDeltaMovement().y()) / 6f)), this.getDeltaMovement().z());
        if (this.getDeltaMovement().y() > this.speedMultiplier / 4f)
            this.setDeltaMovement(this.getDeltaMovement().x(), this.speedMultiplier / 4f, this.getDeltaMovement().z());
        if (this.getDeltaMovement().y() < -this.speedMultiplier / 4f)
            this.setDeltaMovement(this.getDeltaMovement().x(), -this.speedMultiplier / 4f, this.getDeltaMovement().z());
    }

    private void controlBoat() {

        Options settings = Minecraft.getInstance().options;
        boolean down = ModKeyBindings.broomDescend.isDown();

        if (this.isVehicle()) {
            float f = 0.0F;
            if (this.leftInputDown) {
                --this.deltaRotation;
            }

            if (this.rightInputDown) {
                ++this.deltaRotation;
            }

            if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
                f += 0.02F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.forwardInputDown) {
                f += 0.1F;
            }

            if (this.backInputDown) {
                f -= 0.02F;
            }
            if (this.jumpInputDown) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1275f + (0.01f * speedMultiplier), 0));
            }
            if (down) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1275f + (0.01f * speedMultiplier), 0));
            }
            this.setDeltaMovement(this.getDeltaMovement().add((double) (Mth.sin(-(this.getYRot() + 90) * ((float) Math.PI / 180F)) * f) * speedMultiplier, 0.0D, (double) (Mth.cos((this.getYRot() + 90) * ((float) Math.PI / 180F)) * f) * speedMultiplier));
            this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
        }
    }

    @Override
    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float f = 0.0F;
            float f1 = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()) + passenger.getPassengersRidingOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof Animal) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vec3 vector3d = (new Vec3(f, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
            passenger.setPos(this.getX() + vector3d.x, this.getY() + (double) f1, this.getZ() + vector3d.z);
            passenger.setYRot(passenger.getYRot() + this.deltaRotation);
            passenger.setYHeadRot(passenger.getYHeadRot() + this.deltaRotation);
            this.applyYawToEntity(passenger);
            if (passenger instanceof Animal && this.getPassengers().size() > 1) {
                int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setYBodyRot(((Animal) passenger).yBodyRot + (float) j);
                passenger.setYHeadRot(passenger.getYHeadRot() + (float) j);
            }

        }
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entityToUpdate.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entityToUpdate.yRotO += f1 - f;
        entityToUpdate.setYRot(entityToUpdate.getYRot() + f1 - f);
        entityToUpdate.setYHeadRot(entityToUpdate.getYRot());
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("BroomType", this.getBroomType().name());
        compound.put("inv", itemHandler.serializeNBT());
        compound.putBoolean("floatMode", floatMode);
        if (broomUUID != null)
            compound.putUUID("broomUUID", broomUUID);
    }

    @Override
    public boolean save(CompoundTag compound) {
        compound.putString("BroomType", this.getBroomType().name());
        compound.put("inv", itemHandler.serializeNBT());
        compound.putBoolean("floatMode", floatMode);
        if (broomUUID != null)
            compound.putUUID("broomUUID", broomUUID);
        return super.save(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {

        //legacy type
        if (compound.contains("Type", 8)) {
            this.setBroomType(compound.getString("Type"));
        }

        //new type
        if (compound.contains("BroomType", 8)) {
            this.setBroomType(compound.getString("BroomType"));
        }
        itemHandler.deserializeNBT(compound.getCompound("inv"));
        this.floatMode = compound.getBoolean("floatMode");
        if (compound.contains("broomUUID"))
            this.broomUUID = compound.getUUID("broomUUID");


    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        itemHandler.deserializeNBT(compound.getCompound("inv"));
        this.floatMode = compound.getBoolean("floatMode");

        //legacy type
        if (compound.contains("Type", 8)) {
            this.setBroomType(compound.getString("Type"));
        }

        //new type
        if (compound.contains("BroomType", 8)) {
            this.setBroomType(compound.getString("BroomType"));
        }
        itemHandler.deserializeNBT(compound.getCompound("inv"));
        this.floatMode = compound.getBoolean("floatMode");
        if (compound.contains("broomUUID"))
            this.broomUUID = compound.getUUID("broomUUID");


    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(30) {
            @Override
            protected void onContentsChanged(int slot) {
                sync();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.is(HexereiTags.Items.BROOM_MISC);
                    case 1 -> stack.is(HexereiTags.Items.SMALL_SATCHELS) ||
                              stack.is(HexereiTags.Items.MEDIUM_SATCHELS) ||
                              stack.is(HexereiTags.Items.LARGE_SATCHELS);
                    case 2 -> stack.is(HexereiTags.Items.BROOM_BRUSH);
                    default -> true; //satchel inventory
                };
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot == 0 || slot == 1 || slot == 2)
                    return 1;

                return 64;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }


        };
    }


    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos, boolean isEnder) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new BroomContainer(i, BroomEntity.this, playerInventory, playerEntity, isEnder);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("");
            }


        };
    }


    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                MenuProvider containerProvider = createContainerProvider(level, blockPosition(), getModule(BroomSlot.SATCHEL).is(ModItems.ENDER_SATCHEL.get()));

                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, b -> b.writeInt(this.getId()).writeBoolean(isEnder()));

//                    throw new IllegalStateException("Our Container provider is missing!");
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.SUCCESS;
        } else if (this.outOfControlTicks < 60.0F) {
            if (!this.level.isClientSide) {
                if (player.startRiding(this)) {
                    if (getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH))
                        this.push(0, 0.25, 0);
                    return InteractionResult.CONSUME;
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
//        this.lastYd = this.getDeltaMovement().y;
//        if (!this.isPassenger()) {
//            if (onGroundIn) {
//                if (this.fallDistance > 3.0F) {
//                    if (this.status != BroomEntity.Status.ON_LAND) {
//                        this.fallDistance = 0.0F;
//                        return;
//                    }
//
//                    this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
//                    if (!this.level.isClientSide && !this.isRemoved()) {
//                        this.remove(RemovalReason.DISCARDED);
//                        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
//                            this.spawnAtLocation(ModItems.BROOM.get());
//                        }
//                    }
//                }
//
//                this.fallDistance = 0.0F;
//            } else if (!this.level.getFluidState(this.getBlockPosBelowThatAffectsMyMovement()).is(FluidTags.WATER) && y < 0.0D) {
//                this.fallDistance = (float)((double)this.fallDistance - y);
//            }
//
//        }
    }

    public boolean getPaddleState(int side) {
        return this.entityData.<Boolean>get(side == 0 ? LEFT_PADDLE : RIGHT_PADDLE) && this.getControllingPassenger() != null;
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setFloatMode(boolean floatMode) {
        this.floatMode = floatMode;

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BroomSyncFloatModeToServer(this, getFloatMode()));

        sync();

    }

    /**
     * Gets the damage taken from the last hit.
     */
    public boolean getFloatMode() {
        return this.floatMode;
    }

    public void setRotation(float rotation) {
//        this.deltaRotation = deltaRotation;
        this.setYRot(rotation);

        if (!this.level.isClientSide) {
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new BroomSyncRotation(this, rotation));
        }

    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float damageTaken) {
        this.entityData.set(DAMAGE_TAKEN, damageTaken);
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken() {
        return this.entityData.get(DAMAGE_TAKEN);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit) {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit() {
        return this.entityData.get(TIME_SINCE_HIT);
    }

    private void setRockingTicks(int ticks) {
        this.entityData.set(ROCKING_TICKS, ticks);
    }

    private int getRockingTicks() {
        return this.entityData.get(ROCKING_TICKS);
    }

    @OnlyIn(Dist.CLIENT)
    public float getRockingAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int forwardDirection) {
        this.entityData.set(FORWARD_DIRECTION, forwardDirection);
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection() {
        return this.entityData.get(FORWARD_DIRECTION);
    }

    public void setBroomType(String broomType) {
        this.entityData.set(BROOM_TYPE, BroomType.byName(broomType).name());
    }

    public BroomType getBroomType() {
        return BroomType.byName(this.entityData.get(BROOM_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 1 && !this.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Nullable
    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }


    @OnlyIn(Dist.CLIENT)
    public void updateInputs(boolean leftInputDown, boolean rightInputDown, boolean forwardInputDown, boolean backInputDown, boolean jumpInputDown, boolean sneakingInputDown) {
        this.leftInputDown = leftInputDown;
        this.rightInputDown = rightInputDown;
        this.forwardInputDown = forwardInputDown;
        this.backInputDown = backInputDown;
        this.jumpInputDown = jumpInputDown;
        this.sneakingInputDown = sneakingInputDown;

        if (!this.getModule(BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
            this.jumpInputDown = false;
            this.sneakingInputDown = false;
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean canSwim() {
        return this.status == BroomEntity.Status.UNDER_WATER || this.status == BroomEntity.Status.UNDER_FLOWING_WATER;
    }

    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, this.getYRot(), (float) this.lerpPitch);
        }
    }

    @Override
    public int getContainerSize() {
        return 30;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
    }


    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {

        if (index == 1 && !stack.is(HexereiTags.Items.SMALL_SATCHELS))
            return false;
        return this.items.get(index).isEmpty();

    }

    @Override
    public ItemStack removeItem(int index, int count) {

        ItemStack itemStack = ContainerHelper.removeItem(this.items, index, count);
        if (itemStack.getCount() < 1)
            itemStack.setCount(1);
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.items.size()) {
            ItemStack itemStack = stack.copy();
            this.items.set(index, itemStack);
        }


        sync();
    }

    @Override
    public void setChanged() {

    }


    public void sync() {
        setChanged();
        if (!level.isClientSide) {

            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new BroomSyncPacket(this, saveWithoutId(new CompoundTag())));
        }

    }


    @Override
    public boolean stillValid(Player player) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BroomContainer(id, this, inv, player, isEnder());
    }

    public boolean isEnder() {
        return getModule(BroomSlot.SATCHEL).is(ModItems.ENDER_SATCHEL.get());
    }

    public boolean isReplacer() {
        return getModule(BroomSlot.SATCHEL).is(ModItems.REPLACER_SATCHEL.get());
    }

//    private net.minecraftforge.common.util.LazyOptional<?> handler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER)
            return handler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {

        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger(pPlayer))) {
            MenuProvider containerProvider = createContainerProvider(level, blockPosition(), getModule(BroomSlot.SATCHEL).is(ModItems.ENDER_SATCHEL.get()));

            NetworkHooks.openScreen((ServerPlayer) pPlayer, containerProvider, b -> b.writeInt(this.getId()).writeBoolean(isEnder()));
        }
    }


    public enum Status {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        UNDER_LAVA,
        UNDER_FLOWING_LAVA,
        ON_LAND,
        IN_AIR
    }

    public enum AccelerationDirection {
        FORWARD,
        NONE,
        REVERSE,
        CHARGING;

        public static AccelerationDirection fromEntity(LivingEntity entity) {
            if (entity.yya > 0) {
                return FORWARD;
            } else if (entity.yya < 0) {
                return REVERSE;
            }
            return NONE;
        }
    }

    public enum BroomSlot {

        MISC(),
        SATCHEL(),
        BRUSH()

    }

    public ItemStack getModule(BroomSlot slot) {
        return itemHandler.getStackInSlot(slot.ordinal());
    }

    public void setModule(BroomSlot slot, ItemStack module) {
        itemHandler.setStackInSlot(slot.ordinal(), module);
    }

    public List<ItemStack> getSatchelSlots(int satchelSize) {
        List<ItemStack> content = new ArrayList<>();
        for (int i = 3; i < satchelSize; i++) {
            content.add(itemHandler.getStackInSlot(i));
        }
        return content;
    }
}
