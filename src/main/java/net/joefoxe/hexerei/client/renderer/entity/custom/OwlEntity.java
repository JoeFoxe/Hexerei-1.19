package net.joefoxe.hexerei.client.renderer.entity.custom;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.ITargetsDroppedItems;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.QuirkController;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;
import net.joefoxe.hexerei.client.renderer.entity.render.OwlVariant;
import net.joefoxe.hexerei.container.OwlContainer;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.data.owl.OwlLoadedChunksSavedData;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Vector3f;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class OwlEntity extends TamableAnimal implements ContainerListener, FlyingAnimal, ITargetsDroppedItems, Container, MenuProvider, PowerableMob {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.SALMON, Items.COD);
    public BrowPositioning browPositioning = BrowPositioning.NORMAL;
    public BrowAnimation browAnimation;
    public BrowHappyAnimation browHappyAnimation;
    public TailWagAnimation tailWagAnimation;
    public TailFanAnimation tailFanAnimation;
    public HootAnimation hootAnimation;
    public PeckAnimation peckAnimation;
    public HeadTiltAnimation headTiltAnimation;
    public HeadShakeAnimation headShakeAnimation;
    public AnimationController animationController;
    public MessagingController messagingController;
    public float bodyXRot;
    public float bodyXRotLast;
    public float bodyYOffset;
    public float bodyYOffsetLast;
    public float rightWingAngle;
    public float rightWingAngleLast;
    public float rightWingFoldAngle;
    public float rightWingMiddleAngle;
    public float rightWingMiddleAngleLast;
    public float rightWingMiddleFoldAngle;
    public float rightWingTipAngle;
    public float leftWingAngle;
    public float leftWingAngleLast;
    public float leftWingFoldAngle;
    public float leftWingMiddleAngle;
    public float leftWingMiddleAngleLast;
    public float leftWingMiddleFoldAngle;
    public float leftWingTipAngle;
    public boolean dance;
    public int animationCounter;
    public float itemHeldSwing = 0;
    public float itemHeldSwingLast = 0;
    private BlockPos jukebox;

    private int rideCooldownCounter;

    private UUID fishThrowerID;
    private int heldItemTime = 0;
    public OwlTask currentTask;
    public ItemEntity targetingItem;

    public boolean sync;
    public final ItemStackHandler itemHandler = createHandler();

    private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> OWL_DYE_COLOR = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.BOOLEAN);


    public int interactionRange;

    public boolean canAttack;

    private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
    protected FlyingPathNavigation flyingNav;
    protected GroundPathNavigation groundNav;

    private int lastSwappedNavigator = -40;

    public Emotions emotions;
    public EmotionState emotionState;
    private int emotionTicks = 0;

    public QuirkController quirkController;

    public int lowHealthDistressIncreaseTickLast = 0;
    public boolean breedGiftGivenByPlayer = false;
    public int breedGiftGivenByPartnerTimer = 0;
    public UUID breedGiftGivenByPlayerUUID;
    public int waitToGiveTime = 0;

    public OwlEntity(EntityType<OwlEntity> type, Level worldIn) {
        super(type, worldIn);
        registerGoals();


        this.flyingNav = (FlyingPathNavigation) createFlyingNavigation(worldIn);
        this.groundNav = (GroundPathNavigation) createGroundNavigation(worldIn);
        this.moveControl = new OwlMoveController(this, 10);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.animationCounter = 0;
        this.currentTask = OwlTask.NONE;
        this.targetingItem = null;
        this.sync = false;


        this.animationController = new AnimationController();

        this.browAnimation = new BrowAnimation(this);
        this.animationController.addAnimation(this.browAnimation);
        this.browHappyAnimation = new BrowHappyAnimation(this);
        this.animationController.addAnimation(this.browHappyAnimation);
        this.tailWagAnimation = new TailWagAnimation(this);
        this.animationController.addAnimation(this.tailWagAnimation);
        this.tailFanAnimation = new TailFanAnimation(this);
        this.animationController.addAnimation(this.tailFanAnimation);
        this.hootAnimation = new HootAnimation(this);
        this.animationController.addAnimation(this.hootAnimation);
        this.peckAnimation = new PeckAnimation(this);
        this.animationController.addAnimation(this.peckAnimation);
        this.headTiltAnimation = new HeadTiltAnimation(this);
        this.animationController.addAnimation(this.headTiltAnimation);
        this.headShakeAnimation = new HeadShakeAnimation(this);
        this.animationController.addAnimation(this.headShakeAnimation);

        this.messagingController = new MessagingController(this);

        this.bodyXRot = 0;
        this.bodyYOffset = 0;
        this.rightWingAngle = -(float)Math.toRadians(85);
        this.leftWingAngle = (float)Math.toRadians(85);
        this.rightWingMiddleAngle = -(float)Math.toRadians(10);
        this.leftWingMiddleAngle = (float)Math.toRadians(10);
        this.rightWingMiddleFoldAngle = (float)Math.toRadians(30);
        this.leftWingMiddleFoldAngle = -(float)Math.toRadians(30);
        this.rightWingFoldAngle = (float)Math.toRadians(0);
        this.leftWingFoldAngle = -(float)Math.toRadians(0);
        this.rightWingTipAngle = (float)Math.toRadians(60);
        this.leftWingTipAngle = -(float)Math.toRadians(60);
        this.bodyYOffsetLast = this.bodyYOffset;
        this.rightWingAngleLast = this.rightWingAngle;
        this.leftWingAngleLast = this.leftWingAngle;
        this.rightWingMiddleAngleLast = this.rightWingMiddleAngle;
        this.leftWingMiddleAngleLast = this.leftWingMiddleAngle;


        this.interactionRange = 24;
        this.canAttack = true;

        this.emotions = new Emotions(0, 0, 0);
        determineEmotionState();

        this.quirkController = new QuirkController();


    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D) {
            @Override
            public boolean canUse() {
                if (mob instanceof OwlEntity owl)
                    if(owl.isInSittingPose())
                        return false;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new FlyBackToPerchGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new DeliverMessageGoal(this));
        this.goalSelector.addGoal(3, new OwlFavoriteBlockGoal(this, 1.5f));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.25D, 5.0F, 1.0F, true));
//        this.goalSelector.addGoal(2, new WanderAroundPlayerGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
//        this.goalSelector.addGoal(4, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(10, new OwlLookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new OwlGatherItems<>(this, false, false, 40, this.interactionRange));
//        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
//        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
//        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0D, true));
    }


    public class OwlLookAtPlayerGoal extends LookAtPlayerGoal {

        public OwlLookAtPlayerGoal(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
            super(pMob, pLookAtType, pLookDistance);
        }

        @Override
        public boolean canUse() {
            if (OwlEntity.this.currentTask.is(OwlTask.BREEDING) || OwlEntity.this.breedGiftGivenByPartnerTimer > 0)
                return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (OwlEntity.this.currentTask.is(OwlTask.BREEDING) || OwlEntity.this.breedGiftGivenByPartnerTimer > 0)
                return false;
            return super.canContinueToUse();
        }
    }


    @Override
    protected float nextStep() {
        return this.moveDist + 0.25f;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.03F, 0.75F);
    }

    public BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.500001F);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * 1.1f;
    }

    @Override
    public int getMaxHeadYRot() {

        if ((this.onGround() || !this.isFlying()) && this.navigation.isDone()) {
            return 180;
        }
        return 90;
    }

    private static class OwlMoveController extends MoveControl {
        private final int maxTurn;
        public OwlMoveController(OwlEntity crow, int pMaxTurn) {
            super(crow);
            this.maxTurn = pMaxTurn;
        }

        @Override
        public void tick() {
            if (mob.getNavigation() instanceof FlyingPathNavigation) {
                if (this.operation == Operation.MOVE_TO) {
                    this.operation = Operation.WAIT;
                    this.mob.setNoGravity(true);
                    double d0 = this.wantedX - this.mob.getX();
                    double d1 = this.wantedY - this.mob.getY();
                    double d2 = this.wantedZ - this.mob.getZ();
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    if (d3 < (double)2.5000003E-7F) {
                        this.mob.setYya(0.0F);
                        this.mob.setZza(0.0F);
                        return;
                    }

                    float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
                    float f1;
                    if (this.mob.onGround()) {
                        f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    } else {
                        f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                    }

                    this.mob.setSpeed(f1);
                    double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                    if (Math.abs(d1) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F) {
                        float f2 = (float)(-(Mth.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
                        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float)this.maxTurn));
                        this.mob.setYya(d1 > 0.0D ? f1 : -f1);
                    }
                } else {
                    this.mob.setNoGravity(false);

                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                }

            } else {
                if (this.operation == Operation.STRAFE) {
                    float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    float f1 = (float)this.speedModifier * f;
                    float f2 = this.strafeForwards;
                    float f3 = this.strafeRight;
                    float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                    if (f4 < 1.0F) {
                        f4 = 1.0F;
                    }

                    f4 = f1 / f4;
                    f2 *= f4;
                    f3 *= f4;
                    float f5 = Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F));
                    float f6 = Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F));
                    float f7 = f2 * f6 - f3 * f5;
                    float f8 = f3 * f6 + f2 * f5;
                    if (!this.isWalkable(f7, f8)) {
                        this.strafeForwards = 1.0F;
                        this.strafeRight = 0.0F;
                    }

                    this.mob.setSpeed(f1);
                    this.mob.setZza(this.strafeForwards);
                    this.mob.setXxa(this.strafeRight);
                    this.operation = Operation.WAIT;
                } else if (this.operation == Operation.MOVE_TO) {
                    this.operation = Operation.WAIT;
                    double d0 = this.wantedX - this.mob.getX();
                    double d1 = this.wantedZ - this.mob.getZ();
                    double d2 = this.wantedY - this.mob.getY();
                    double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                    if (d3 < (double)2.5000003E-7F) {
                        this.mob.setZza(0.0F);
                        return;
                    }

                    float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    BlockPos blockpos = this.mob.blockPosition();
                    BlockState blockstate = this.mob.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
                    if (d2 > (double)this.mob.maxUpStep() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                        this.mob.getJumpControl().jump();
                        this.operation = Operation.JUMPING;
                    }
                } else if (this.operation == Operation.JUMPING) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.mob.onGround()) {
                        this.operation = Operation.WAIT;
                    }
                } else {
                    this.mob.setZza(0.0F);
                }

            }
        }

        private boolean isWalkable(float pRelativeX, float pRelativeZ) {
            PathNavigation pathnavigation = this.mob.getNavigation();
            if (pathnavigation != null) {
                NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
                if (nodeevaluator != null && nodeevaluator.getPathType(new PathfindingContext(this.mob.level(), this.mob), Mth.floor(this.mob.getX() + (double)pRelativeX), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)pRelativeZ)) != PathType.WALKABLE) {
                    return false;
                }
            }

            return true;
        }
    }

    public void switchNavigator(boolean shouldFly, boolean force) {
        if (Math.abs(this.tickCount - this.lastSwappedNavigator) > 40 || force) {
            if (this.lastSwappedNavigator == -40)
                this.entityData.set(DATA_FLYING, shouldFly);
            this.lastSwappedNavigator = this.tickCount;
            this.navigation = shouldFly ? this.flyingNav : this.groundNav;
        }
    }
    public void switchNavigator(boolean shouldFly) {
        switchNavigator(shouldFly, false);
    }

    public boolean isFlyingNav() {
        return this.navigation == this.flyingNav;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PERCH_POS, Optional.empty());
        builder.define(OWL_DYE_COLOR, -1);
        builder.define(DATA_ID_TYPE_VARIANT, 0);
        builder.define(DATA_FLYING, true);
    }

    public void syncInv() {
        if (!level().isClientSide) {
            HexereiPacketHandler.sendToNearbyClient(this.level(), this, new OwlSyncInvPacket(this, itemHandler.serializeNBT(this.level().registryAccess())));
        }

    }

    public void sync() {
        setChanged();
        if (!level().isClientSide) {

            HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EntitySyncPacket(this, saveWithoutId(new CompoundTag())));
            syncAdditionalData();
        }

    }

    public void syncAdditionalData() {
        setChanged();
        if (!level().isClientSide) {

            CompoundTag tag = new CompoundTag();
            addAdditionalSaveDataNoSuper(tag);
            HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EntitySyncAdditionalDataPacket(this, tag));
        }

    }

    public boolean isMaxHealth(){
        return OwlEntity.this.getHealth() >= OwlEntity.this.getMaxHealth();
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.checkTotemDeathProtection(pCause)) {
            this.messagingController.stopForceloadingChunks();
            super.die(pCause);
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (pReason.shouldDestroy())
            this.messagingController.stopForceloadingChunks();
        super.remove(pReason);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        float f = this.getHealth();

        if(pSource.is(DamageTypes.SWEET_BERRY_BUSH))
            return false;

        if (!this.level().isClientSide) {
            this.emotions.setAnger(this.emotions.getAnger() + (int) Mth.clamp(pAmount * 20, 10, 30));
            this.emotionChanged();
        }


        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isInWall() {

        if(this.isPassenger())
            return false;

        return super.isInWall();
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                syncAdditionalData();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0 && !(stack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.HELMET))
                    return false;
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                if(slot == 0 || slot == 1 || slot == 2)
                    return 1;

                return 64;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }


        };
    }

    public void peck() {
        this.peckAnimation.start();

        this.hootAnimation.start();
    }

    @Override
    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else if (isTame() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity)) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            Entity player = this.getVehicle();
            if (this.isPassenger()) {
                int i = player.getPassengers().indexOf(this);
                float radius = 0.38F;
                float angle = (0.01745329251F * (((Player) player).yBodyRot + (i == 0 ? -90 : 90)));
                this.yHeadRot = ((Player) player).yHeadRot;
                this.yRotO = ((Player) player).yHeadRot;

                this.setPos(player.getX() + radius * Mth.sin((float) (Math.PI + angle)), player.getY() + (!player.isShiftKeyDown() ? 1.4d : 1.2d), player.getZ() + radius * Mth.cos(angle));
                if (!player.isAlive() || player.isShiftKeyDown() || ((Player) player).isFallFlying() || this.getTarget() != null && this.getTarget().isAlive()) {
                    this.removeVehicle();
                }
            }
        }else{
            super.rideTick();
        }
    }

    private void setTypeVariant(int pTypeVariant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, pTypeVariant);
    }

    private void setFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

//    private void setVariantAndMarkings(OwlVariant pVariant, Markings pMarking) {
//        this.setTypeVariant(pVariant.getId() & 255 | pMarking.getId() << 8 & '\uff00');
//    }

    public OwlVariant getVariant() {
        return OwlVariant.byId(this.getTypeVariant() & 255);
    }


    @Override
    public void setRecordPlayingNearby(BlockPos p_21082_, boolean p_21083_) {
        this.jukebox = p_21082_;
        this.dance = p_21083_;
    }

    protected AABB getTargetableArea(double targetDistance) {
        Vec3 renderCenter = new Vec3(OwlEntity.this.getX(), OwlEntity.this.getY(), OwlEntity.this.getZ());
        AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
        return aabb.move(renderCenter);
    }


    public void setBrowPos(OwlEntity.BrowPositioning browPositioning) {
        this.browPositioning = browPositioning;
        if (!level().isClientSide)
            HexereiPacketHandler.sendToNearbyClient(this.level(), this, new BrowPositioningPacket(this, this.browPositioning));
    }
    public void determineEmotionState() {
        EmotionState closestState = null;
        double closestDistance = Double.MAX_VALUE;

        for (EmotionState state : EmotionState.values()) {
            double distance = calculateStateDistance(emotions, state.getScales());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestState = state;
            }
        }

        this.emotionState = closestState;
    }

    private double calculateStateDistance(Emotions scales1, Emotions scales2) {
        int anger = scales1.getAnger() - scales2.getAnger();
        int plead = scales1.getDistress() - scales2.getDistress();
        int happiness = scales1.getHappiness() - scales2.getHappiness();

        return Math.sqrt(anger * anger + plead * plead + happiness * happiness);
    }
    private void adjustEmotion() {
        if (this.random.nextInt(1) == 0){
            int adjustment = (int)Math.round((easeInOutCubic(emotions.getAnger() / 100f) * 0.5 + 0.25) * 20 + 1);
            emotions.setAnger(emotions.getAnger() - adjustment);
        }

        if (this.random.nextInt(1) == 0) {
            int adjustment = (int)Math.round((easeInOutCubic(emotions.getDistress() / 100f) * 0.5 + 0.25) * 20 + 1);
            emotions.setDistress(emotions.getDistress() - adjustment);
        }

        if (this.random.nextInt(1) == 0) {
            int adjustment = (int)Math.round((easeInOutCubic(emotions.getDistress() / 100f) * 0.5 + 0.25) * 20 + 1);
            emotions.setHappiness(emotions.getHappiness() - adjustment);
        }

        emotionChanged();
    }

    public double easeInOutCubic(float x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    @Override
    public void tame(Player pPlayer) {
        emotions.setDistress(emotions.getDistress() - 70);
        emotions.setHappiness(emotions.getHappiness() + 60);
        super.tame(pPlayer);
    }

    public void emotionChanged() {

        if (!level().isClientSide) {
            int packedEmotion = (emotions.getHappiness() << 16) | (emotions.getDistress() << 8) | emotions.getAnger();
            HexereiPacketHandler.sendToNearbyClient(level(), this, new EmotionPacket(this, packedEmotion));
//            System.out.println("--");
//            System.out.println("Happiness - " + emotions.getHappiness());
//            System.out.println("Distress - " + emotions.getDistress());
//            System.out.println("Anger - " + emotions.getAnger());
        }

    }

    private int adjustTowardsRestingPoint(int currentValue, int adjustmentFactor) {
        int restingPoint = 0;
        int difference = currentValue - restingPoint;
        int adjustment = (int) Math.round(difference / (float)adjustmentFactor);

        // Add a small random factor to make the changes less predictable
        adjustment += random.nextInt(5) - 2;

        // Ensure the new value is within the valid range (0-100)
        int newValue = Math.max(0, Math.min(100, currentValue - adjustment));

        return newValue;
    }
    public boolean checkTotemDeathProtection(DamageSource pDamageSource) {
        if (pDamageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            ItemStack itemstack = null;

            boolean triggered = false;
            for(InteractionHand interactionhand : InteractionHand.values()) {
                ItemStack itemstack1 = this.getItemInHand(interactionhand);
                if (itemstack1.is(ModItems.CROW_ANKH_AMULET.get())) {
                    itemstack = itemstack1.copy();
                    itemstack1.shrink(1);
                    triggered = true;
                    break;
                }
            }

            if (triggered) {

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                this.level().broadcastEntityEvent(this, (byte)35);
                this.sync();
            }

            return triggered;
        }
    }


    // Animation interface
    public interface Animation {
        void tick();
        void start();
        void stop();
        boolean isActive();
    }

    public class AnimationBase implements Animation {
        public boolean active;
        public int cooldownTimer;
        public int activeTimer;
        public boolean useCooldown = true;

        @Override
        public void tick() {

            preTick();

            if (active) {

                activeTick();

                activeTimer--;
                if (activeTimer <= 0) {
                    stop();
                }
            } else if (!level().isClientSide){
                if (useCooldown) {
                    cooldownTimer--;
                    if (cooldownTimer <= 0) {
                        start();
                    }
                }
            }

            postTick();
        }

        public void preTick() {
        }

        public void activeTick() {
        }

        public void postTick() {
        }

        @Override
        public void start() {
            this.active = true;
        }

        @Override
        public void stop() {
            this.active = false;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }
    }

    // AnimationController class
    public class AnimationController {
        private List<Animation> animations;

        public AnimationController() {
            this.animations = new ArrayList<>();
        }

        public void addAnimation(Animation animation) {
            this.animations.add(animation);
        }

        public void tick() {
            for (Animation animation : animations) {
                animation.tick();
            }
        }
    }

    public class BrowAnimation extends AnimationBase {

        private BrowAnim browAnim;
        private float browRotTarget;
        private float browRot;
        private OwlEntity owl;

        public float getBrowRot() {
            return browRot;
        }

        public void setBrowAnim(BrowAnim browAnim) {
            this.browAnim = browAnim;
        }

        public BrowAnim getBrowAnim() {
            return browAnim;
        }

        public BrowAnimation(OwlEntity owl) {
            this.browAnim = BrowAnim.BOTH;
            this.browRot = 0;
            this.browRotTarget = 0;
            this.owl = owl;
        }

        public void activeTick() {
            if (this.owl.level().isClientSide)
                this.browRotTarget = Mth.sin((owl.tickCount + owl.getId() * 342)) * 16f;
        }

        public void postTick() {
            browRot = moveTo(browRot, browRotTarget, 30);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 5 + owl.getRandom().nextInt(10);
                HexereiPacketHandler.sendToNearbyClient(owl.level(), owl, new BrowAnimPacket(owl, OwlEntity.BrowAnim.values()[this.owl.getRandom().nextInt(OwlEntity.BrowAnim.values().length)], this.activeTimer));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTimer = random.nextInt(160) + 60;
            this.browRotTarget = 0;
        }
    }

    public class BrowHappyAnimation extends AnimationBase {

        private BrowAnim browAnim;
        private float browRotTarget;
        private float browRot;
        private float browRotLast;
        private OwlEntity owl;

        public float getBrowRot() {
            return browRot;
        }
        public float getBrowRotLast() {
            return browRotLast;
        }

        public void setBrowAnim(BrowAnim browAnim) {
            this.browAnim = browAnim;
        }

        public BrowAnim getBrowAnim() {
            return browAnim;
        }

        public BrowHappyAnimation(OwlEntity owl) {
            this.browAnim = BrowAnim.BOTH;
            this.browRot = 0;
            this.browRotTarget = 0;
            this.owl = owl;
        }

        public void activeTick() {
            if (this.owl.level().isClientSide) {
                float val = (owl.tickCount + owl.getId() * 342);
                this.browRotTarget = Mth.sin(val) * 26f;
            }
        }

        public void postTick() {
            browRotLast = browRot;
            browRot = moveTo(browRot, browRotTarget, 30);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 15 + owl.getRandom().nextInt(10);
                HexereiPacketHandler.sendToNearbyClient(owl.level(), owl, new BrowAnimPacket(owl, OwlEntity.BrowAnim.values()[this.owl.getRandom().nextInt(OwlEntity.BrowAnim.values().length)], this.activeTimer, true));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTimer = random.nextInt(160) + 60;
            this.browRotTarget = 0;
        }
    }

    public class TailWagAnimation extends AnimationBase {

        private float wagRotTarget;
        private float wagRot;
        private OwlEntity owl;

        public float getWagRot() {
            return wagRot;
        }

        public TailWagAnimation(OwlEntity owl) {
            this.wagRotTarget = 0;
            this.wagRot = 0;
            this.owl = owl;
        }

        public void activeTick() {
            if (this.owl.level().isClientSide)
                this.wagRotTarget = Mth.sin((owl.tickCount + owl.getId() * 342)) * 100f;
        }

        public void postTick() {
            wagRot = moveTo(wagRot, wagRotTarget, 30);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 5 + owl.getRandom().nextInt(10);
                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new TailWagPacket(this.owl, this.activeTimer));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTimer = random.nextInt(160) + 20;
            this.wagRotTarget = 0;
        }
    }

    public class TailFanAnimation extends AnimationBase {

        private float fanRotTarget;
        private float fanRot;
        private OwlEntity owl;

        public float getFanRot() {
            return fanRot;
        }

        public TailFanAnimation(OwlEntity owl) {
            this.fanRotTarget = 0;
            this.fanRot = 0;
            this.owl = owl;
        }

        public void activeTick() {
            if (this.owl.level().isClientSide)
                this.fanRotTarget = Mth.sin((owl.tickCount + owl.getId() * 342)) * 100f;
        }

        public void postTick() {
            fanRot = moveTo(fanRot, fanRotTarget, 30);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 5 + owl.getRandom().nextInt(10);
                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new TailFanPacket(this.owl, this.activeTimer));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTimer = random.nextInt(160) + 20;
            this.fanRotTarget = 0;
        }
    }

    public class HootAnimation extends AnimationBase {

        private float hootRotTarget;
        private float hootRot;
        private OwlEntity owl;

        public float getHootRot() {
            return hootRot;
        }

        public HootAnimation(OwlEntity owl) {
            this.hootRotTarget = 0;
            this.hootRot = 0;
            this.owl = owl;
        }

        public void activeTick() {
            if (this.owl.level().isClientSide)
                this.hootRotTarget = 80;
        }

        public void postTick() {
            hootRot = moveTo(hootRot, hootRotTarget, 30);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 15;

                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new OwlHootPacket(this.owl, this.activeTimer));
                owl.playSound(ModSounds.OWL_HOOT.get(), owl.getSoundVolume(), owl.getVoicePitch());
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTimer = random.nextInt(560) + 160;
            this.hootRotTarget = 0;
        }
    }

    public class PeckAnimation extends AnimationBase {

        private float peckRotTarget;
        private float peckRot;
        private OwlEntity owl;

        public float getPeckRot() {
            return peckRot;
        }

        public PeckAnimation(OwlEntity owl) {
            this.peckRotTarget = 0;
            this.peckRot = 0;
            this.owl = owl;
            this.useCooldown = false;
        }

        public void activeTick() {
            this.peckRotTarget = 80;
        }

        public void postTick() {
            peckRot = moveTo(peckRot, peckRotTarget, 15);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 10;

                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new PeckPacket(this.owl));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.peckRotTarget = 0;
        }
    }

    public class HeadTiltAnimation extends AnimationBase {

        public float zTiltTarget;
        private float zTilt;
        public float xTiltTarget;
        private float xTilt;
        private OwlEntity owl;

        public float getzTilt() {
            return zTilt;
        }
        public float getxTilt() {
            return xTilt;
        }

        public HeadTiltAnimation(OwlEntity owl) {
            this.zTiltTarget = 0;
            this.xTiltTarget = 0;
            this.zTilt = 0;
            this.xTilt = 0;
            this.owl = owl;
            this.cooldownTimer = random.nextInt(100);
        }

        public void activeTick() {

        }

        public void postTick() {
            zTilt = moveTo(zTilt, zTiltTarget, 15);
            xTilt = moveTo(xTilt, xTiltTarget, 15);
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = random.nextInt(20) + 10;
                this.xTiltTarget = random.nextInt(100) - 50;
                this.zTiltTarget = random.nextInt(100) - 50;

                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new HeadTiltPacket(this.owl, this.activeTimer, this.xTiltTarget, this.zTiltTarget));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.zTiltTarget = 0;
            this.xTiltTarget = 0;
            this.cooldownTimer = random.nextInt(80) + 20;
        }
    }

    public class HeadShakeAnimation extends AnimationBase {

        public float zTiltTarget;
        private float zTilt;
        private float zTiltLast;
        private OwlEntity owl;

        public float getzTilt() {
            return zTilt;
        }

        public float getzTiltLast() {
            return zTiltLast;
        }

        public HeadShakeAnimation(OwlEntity owl) {
            this.zTiltTarget = 0;
            this.zTilt = 0;
            this.zTiltLast = this.zTilt;
            this.owl = owl;
            this.useCooldown = false;
        }

        @Override
        public void start() {
            super.start();
            if (!this.owl.level().isClientSide) {
                this.activeTimer = 15;

                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new HeadShakePacket(this.owl, this.activeTimer));
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.zTiltTarget = 0;
        }


        @Override
        public void preTick() {
            this.zTiltLast = this.zTilt;
        }

        public void activeTick() {
            this.zTiltTarget = Mth.sin((owl.tickCount + owl.getId() * 642)) * 100f;
        }

        public void postTick() {
            zTilt = moveTo(zTilt, zTiltTarget, 15);
        }
    }

    public static class MessageText {
        public static final int LINES = 12;
        private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.FLAT_CODEC
                .listOf()
                .comapFlatMap(
                        p_337999_ -> Util.fixedSize((List<Component>)p_337999_, LINES)
                                .map(components -> components.toArray(new Component[0])),
                        components -> Arrays.stream(components).toList()
                );
        public static final Codec<MessageText> DIRECT_CODEC = RecordCodecBuilder.create(
                p_338000_ -> p_338000_.group(
                                LINES_CODEC.fieldOf("messages").forGetter(text -> text.messages)
                        )
                        .apply(p_338000_, MessageText::load)
        );
        private final Component[] messages;

        public MessageText() {
            this(emptyMessages(LINES));
        }

        public MessageText(Component[] pMessages) {
            this.messages = pMessages;
        }

        private static Component[] emptyMessages(int size) {
            Component[] messages = new Component[size];
            Arrays.fill(messages, CommonComponents.EMPTY);
            return messages;
        }

        private static MessageText load(Component[] component) {
            return new MessageText(component);
        }

        public Component getMessage(int pIndex) {
            return this.getMessages()[pIndex];
        }

        public MessageText setMessage(int pIndex, Component pText) {
            Component[] acomponent = Arrays.copyOf(this.messages, this.messages.length);
            acomponent[pIndex] = pText;
            return new MessageText(acomponent);
        }

        public Component[] getMessages() {
            return this.messages;
        }
    }

    public static class MessagingController {
        private OwlEntity owl;
        private GlobalPos startPos = null;
        private GlobalPos destinationPos = null;
        private Player destinationPlayer = null;
        private Map<ResourceKey<Level>, Set<ChunkPos>> lastCheckedChunks = new HashMap<>();

        private Stage stage = Stage.DONE;
        private ItemStack messageStack = ItemStack.EMPTY;

        public enum Stage {
            FIND_FLY_OFF_LOCATION, FLY_OFF_AND_TELEPORT, FLY_TO_DESTINATION, FIND_FLY_BACK_LOCATION, FLY_BACK_AND_TELEPORT, RETURN_TO_START, DONE;

            public static Stage byId(int id) {
                return values()[id < 0 || id >= values().length ? 0 : id];
            }
        }

        public MessagingController(OwlEntity owl) {
            this.owl = owl;
        }

        public boolean hasDelivery() {
            return !getMessageStack().isEmpty();
        }

        public boolean isDelivering() {
            return this.stage != Stage.DONE;
        }

        public ItemStack getMessageStack() {
            return messageStack;
        }

        public void setMessageStack(ItemStack messageStack) {
            this.messageStack = messageStack;
        }

        public void tick() {
            if (this.owl.currentTask == OwlTask.DELIVER_MESSAGE) {
                handleActiveState();
            } else {
                handleInactiveState();
            }
        }

        public Map<ResourceKey<Level>, Set<ChunkPos>> getLastCheckedChunks() {
            return lastCheckedChunks;
        }

        public void clearLastCheckedChunks() {
            this.lastCheckedChunks.clear();
        }

        private MessageText loadLines(MessageText pText) {
            for(int i = 0; i < 4; ++i) {
                Component component = pText.getMessage(i);
                pText = pText.setMessage(i, component);
            }

            return pText;
        }

        public boolean forceLoadChunks() {
            if (owl.isTame() && !owl.isDeadOrDying()) {
                if (owl.level() instanceof ServerLevel serverLevel) {
                    if (this.hasDestination()) {
                        GlobalPos dest = getDestination();
                        GlobalPos start = this.startPos;


                        ChunkPos startChunk = new ChunkPos(start.pos());
                        ChunkPos targetChunk = new ChunkPos(dest.pos());

                        Set<ChunkPos> newChunks = new HashSet<>();
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                newChunks.add(new ChunkPos(startChunk.x + dx, startChunk.z + dz));
                            }
                        }
                        if (!newChunks.equals(lastCheckedChunks.get(start.dimension()))) {
                            ServerLevel level = serverLevel.getServer().getLevel(start.dimension());
                            if (level != null) {
                                OwlLoadedChunksSavedData.get().addOwlLoading(level, this.owl, newChunks);
                                if (this.lastCheckedChunks.containsKey(start.dimension()))
                                    this.lastCheckedChunks.get(start.dimension()).clear();
                                this.lastCheckedChunks.put(start.dimension(), newChunks);
                            }
                        }

                        newChunks = new HashSet<>();
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                newChunks.add(new ChunkPos(targetChunk.x + dx, targetChunk.z + dz));
                            }
                        }
                        if (!newChunks.equals(lastCheckedChunks.get(dest.dimension()))) {
                            ServerLevel level = serverLevel.getServer().getLevel(dest.dimension());
                            if (level != null) {
                                OwlLoadedChunksSavedData.get().addOwlLoading(level, this.owl, newChunks);
                                if (this.lastCheckedChunks.containsKey(dest.dimension()))
                                    this.lastCheckedChunks.get(dest.dimension()).clear();
                                this.lastCheckedChunks.put(dest.dimension(), newChunks);
                            }
                        }

                        return true;

                    } else {
//                        System.out.println("Missing destination");
                    }
                } else {
//                    System.out.println("Not server world");
                }
            } else {
//                System.out.println("Not tame or dying");
            }
            return false;
        }

        public void stopForceloadingChunks() {
            if (this.owl.level() instanceof ServerLevel serverLevel) {
                OwlLoadedChunksSavedData.get(serverLevel).clearOwl(serverLevel, this.owl);
                this.owl.messagingController.clearLastCheckedChunks();
            }
        }

        private void handleActiveState() {

            if (!this.owl.level().isClientSide) {
                if (!this.hasDestination()) {
                    if (this.owl.currentTask == OwlTask.DELIVER_MESSAGE) {
                        if (this.stage == Stage.FIND_FLY_OFF_LOCATION || this.stage == Stage.FLY_OFF_AND_TELEPORT || this.stage == Stage.FLY_TO_DESTINATION) {
                            this.stage = Stage.FIND_FLY_BACK_LOCATION;
                        }
                    }
                }
            }
        }

        private void handleInactiveState() {
            if (!this.owl.level().isClientSide){
                if (hasDelivery() && !hasDestination()) {
                    // drop message stack
                    this.owl.spawnAtLocation(this.getMessageStack().copy());
                    this.setMessageStack(ItemStack.EMPTY);
                }
                else if (hasDelivery()) {
                    // set task to deliver message
                    this.owl.currentTask = OwlTask.DELIVER_MESSAGE;
                }
            }
        }

        public void write(CompoundTag nbt) {

            if (startPos != null) {
                Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, startPos).result();
                tag.ifPresent(value -> nbt.put("startPos", value));
            }
            if (destinationPos != null){
                Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, destinationPos).result();
                tag.ifPresent(value -> nbt.put("destinationPos", value));
            }
            if (destinationPlayer != null)
                nbt.putUUID("destinationEntity", destinationPlayer.getUUID());
            if (stage != null)
                nbt.putInt("stage", stage.ordinal());

            if (!messageStack.isEmpty())
                nbt.put("messageStack", messageStack.save(this.owl.registryAccess(), new CompoundTag()));

        }

        public void read(CompoundTag nbt) {

            if (nbt.contains("startPos")) {
                Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("startPos")).result();
                pos.ifPresent(globalPos -> this.startPos = globalPos);
            }
            if (nbt.contains("destinationPos")){

                Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("destinationPos")).result();
                pos.ifPresent(globalPos -> this.destinationPos = globalPos);
            }
            if (nbt.contains("destinationEntity"))
                this.destinationPlayer = this.owl.level().getPlayerByUUID(nbt.getUUID("destinationEntity"));
            if (nbt.contains("stage"))
                this.stage = Stage.byId(nbt.getInt("stage"));

            if (nbt.contains("messageStack"))
                this.messageStack = ItemStack.parseOptional(this.owl.registryAccess(), nbt.getCompound("messageStack"));
            else
                this.messageStack = ItemStack.EMPTY.copy();

        }

        public void setDestination(Player entity) {
            this.destinationPlayer = entity;
        }

        public void setDestination(GlobalPos pos) {
            this.destinationPos = pos;
        }

        public void setStartPos(GlobalPos startPos) {
            this.startPos = startPos;
        }

        public boolean hasDestination() {
            return this.destinationPos != null || this.destinationPlayer != null;
        }

        public GlobalPos getDestination(){
            return destinationPlayer != null ? GlobalPos.of(destinationPlayer.level().dimension(), destinationPlayer.blockPosition()) : destinationPos;
        }

        public void start(GlobalPos startPos) {
            setStartPos(startPos);
            this.stage = Stage.byId(0);
        }
    }


    @Override
    public void tick() {
        super.tick();

        if(!this.sync && this.level() instanceof ServerLevel) {
            sync();
            this.sync = true;
        }
        if(!this.sync && this.level() instanceof ClientLevel) {

            if (level().isClientSide)
                HexereiPacketHandler.sendToServer(new AskForSyncPacket(this));

            this.sync = true;
        }

        if(this.breedGiftGivenByPartnerTimer > 0 && !this.level().isClientSide)
        {
            this.breedGiftGivenByPartnerTimer--;
            if(breedGiftGivenByPartnerTimer == 16){
                this.peck();
                HexereiPacketHandler.sendToNearbyClient(this.level(), this, new PeckPacket(this));
            }
            if(this.breedGiftGivenByPartnerTimer == 0) {
                if (level().getPlayerByUUID(this.breedGiftGivenByPlayerUUID) != null) {
                    this.setInLove(level().getPlayerByUUID(this.breedGiftGivenByPlayerUUID));

                    this.heal(4);

                    if(!level().isClientSide) {
                        HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
                        OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() - 25);
                        OwlEntity.this.emotions.setAnger(OwlEntity.this.emotions.getAnger() - 15 - this.random.nextInt(5));
                        OwlEntity.this.emotionChanged();
                    }

                    if (this.itemHandler.getStackInSlot(1).hasCraftingRemainingItem()) {
                        this.spawnAtLocation(this.itemHandler.getStackInSlot(1).getCraftingRemainingItem());
                    }
                    this.itemHandler.getStackInSlot(1).shrink(1);

                    this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());

                    this.syncInv();
                }
            }
        }

        quirkController.tick(this);

        if(!level().isClientSide) {
            float health = this.getHealth();
            float threshold = this.getMaxHealth() / 3f;
            if (health < threshold) {
                if (tickCount - lowHealthDistressIncreaseTickLast > 20) {
                    if (this.random.nextInt(10) == 0) {
                        this.lowHealthDistressIncreaseTickLast = tickCount;
                        OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() + 5 + OwlEntity.this.random.nextInt(15));
                        OwlEntity.this.emotions.setHappiness(OwlEntity.this.emotions.getHappiness() - (OwlEntity.this.random.nextInt(5) + 1));
                        emotionChanged();
                    }
                }
            }
            if (this.tickCount % 100 == 0) {
                if (this.isTame() && this.getOwner() instanceof Player owner) {

                    if (owner.distanceTo(this) < 5) { // every 5 get happier if nearby owner
                        OwlEntity.this.emotions.setHappiness(OwlEntity.this.emotions.getHappiness() + (OwlEntity.this.random.nextInt(5) + 1));
                    }
                }

                if (this.emotions.isHappy()) { // every 5 seconds heal 1 if the owl is happy
                    if (this.getHealth() < this.getMaxHealth())
                        this.heal(1);
                }
            }


        }

        float deltaDist = (float)Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
        float deltaYDist = (float)Math.sqrt(this.getDeltaMovement().y * this.getDeltaMovement().y);
        this.itemHeldSwingLast = this.itemHeldSwing;
        this.itemHeldSwing = moveTo(this.itemHeldSwing, (float)Mth.clamp((deltaDist * 455f - deltaYDist * 300f) / 4,0, 40), 3 + 10 * Mth.abs(Mth.clamp((deltaDist * 455f - deltaYDist * 300f) / 4,0, 40) - this.itemHeldSwing) / 40);

        this.animationCounter++;
        this.rideCooldownCounter++;

        if (!this.level().isClientSide) {
            if (onGround()) {
                if (isFlying()) {
                    this.entityData.set(DATA_FLYING, false);
                }
            }
            if (isFlyingNav()) {
                if (!isFlying()) {
                    this.entityData.set(DATA_FLYING, true);
                }
            }

            emotionTicks++;
            if (emotionTicks >= 20 * 30) { // 30 seconds
                emotionTicks = 0;

                adjustEmotion();
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.targetingItem == null)
                if (this.currentTask == OwlTask.PICKUP_ITEM)
                    this.currentTask = OwlTask.NONE;
        }




        // Wing animation
        bodyYOffsetLast = bodyYOffset;
        rightWingAngleLast = rightWingAngle;
        leftWingAngleLast = leftWingAngle;
        rightWingMiddleAngleLast = rightWingMiddleAngle;
        leftWingMiddleAngleLast = leftWingMiddleAngle;
        bodyXRotLast = bodyXRot;
//        System.out.println(Thread.currentThread().getName() + "  " + isFlying() + "  " + !onGround());
        if (isFlying() && !onGround()){
            if (this.getDeltaMovement().y < -0.0075) {
                // flying but falling
                bodyXRot = (float) Mth.lerp(0.15, bodyXRot, (float)Math.PI / 3);
                rightWingMiddleFoldAngle = (float) Mth.lerp(0.45, rightWingMiddleFoldAngle, -(float)Math.toRadians(-15));
                leftWingMiddleFoldAngle = (float) Mth.lerp(0.45, leftWingMiddleFoldAngle, (float)Math.toRadians(-15));
                rightWingFoldAngle = (float) Mth.lerp(0.45, rightWingFoldAngle, (float)Math.toRadians(25));
                leftWingFoldAngle = (float) Mth.lerp(0.45, leftWingFoldAngle, -(float)Math.toRadians(25));
                bodyYOffset = (float) Mth.lerp(0.45, bodyYOffset, Math.sin((ClientEvents.getClientTicksWithoutPartial() + 2) / 8f));
                rightWingAngle = (float) Mth.lerp(0.75, rightWingAngle, Math.sin(ClientEvents.getClientTicksWithoutPartial() / 8f) * 0.1f);
                leftWingAngle = (float) Mth.lerp(0.75, leftWingAngle, -Math.sin(ClientEvents.getClientTicksWithoutPartial() / 8f) * 0.1f);
                rightWingMiddleAngle = (float) Mth.lerp(0.75, rightWingMiddleAngle, Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 8) / 8f) * 0.25f - 0.125);
                leftWingMiddleAngle = (float) Mth.lerp(0.75, leftWingMiddleAngle, -Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 8) / 8f) * 0.25f + 0.125);
            } else {
                // flying going up
                bodyXRot = (float) Mth.lerp(0.15, bodyXRot, (float)Math.PI / 4);
                rightWingMiddleFoldAngle = (float) Mth.lerp(0.45, rightWingMiddleFoldAngle, -(float)Math.toRadians(5));
                leftWingMiddleFoldAngle = (float) Mth.lerp(0.45, leftWingMiddleFoldAngle, (float)Math.toRadians(5));
                rightWingFoldAngle = (float) Mth.lerp(0.45, rightWingFoldAngle, (float)Math.toRadians(0));
                leftWingFoldAngle = (float) Mth.lerp(0.45, leftWingFoldAngle, -(float)Math.toRadians(0));
                bodyYOffset = (float) Mth.lerp(0.45, bodyYOffset, Math.sin((ClientEvents.getClientTicksWithoutPartial() + 1) / 4f));
                rightWingAngle = (float) Mth.lerp(0.75, rightWingAngle, Math.sin(ClientEvents.getClientTicksWithoutPartial() / 4f) * 1f);
                leftWingAngle = (float) Mth.lerp(0.75, leftWingAngle, -Math.sin(ClientEvents.getClientTicksWithoutPartial() / 4f) * 1f);
                rightWingMiddleAngle = (float) Mth.lerp(0.75, rightWingMiddleAngle, Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 4) / 4f) * 0.5f - 0.25);
                leftWingMiddleAngle = (float) Mth.lerp(0.75, leftWingMiddleAngle, -Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 4) / 4f) * 0.5f + 0.25);
            }
            rightWingTipAngle = (float) Mth.lerp(0.45, rightWingTipAngle, (float)Math.toRadians(15));
            leftWingTipAngle = (float) Mth.lerp(0.45, leftWingTipAngle, -(float)Math.toRadians(15));
        } else {
            // not flying - fold up the wings
            bodyXRot = (float) Mth.lerp(0.25, bodyXRot, 0);
            bodyYOffset = (float) Mth.lerp(0.25, bodyYOffset, 0);
            rightWingAngle = (float) Mth.lerp(0.45, rightWingAngle, -(float)Math.toRadians(85));
            leftWingAngle = (float) Mth.lerp(0.45, leftWingAngle, (float)Math.toRadians(85));
            rightWingMiddleAngle = (float) Mth.lerp(0.45, rightWingMiddleAngle, -(float)Math.toRadians(10));
            leftWingMiddleAngle = (float) Mth.lerp(0.45, leftWingMiddleAngle, (float)Math.toRadians(10));
            rightWingMiddleFoldAngle = (float) Mth.lerp(0.45, rightWingMiddleFoldAngle, (float)Math.toRadians(30));
            leftWingMiddleFoldAngle = (float) Mth.lerp(0.45, leftWingMiddleFoldAngle, -(float)Math.toRadians(30));
            rightWingFoldAngle = (float) Mth.lerp(0.45, rightWingFoldAngle, (float)Math.toRadians(0));
            leftWingFoldAngle = (float) Mth.lerp(0.45, leftWingFoldAngle, -(float)Math.toRadians(0));
            rightWingTipAngle = (float) Mth.lerp(0.45, rightWingTipAngle, (float)Math.toRadians(60));
            leftWingTipAngle = (float) Mth.lerp(0.45, leftWingTipAngle, -(float)Math.toRadians(60));
        }


        this.animationController.tick();

        this.messagingController.tick();


        if (!this.itemHandler.getStackInSlot(1).isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 60 && isOwlEdible(this.itemHandler.getStackInSlot(1)) && (!this.isTame() || this.getHealth() < this.getMaxHealth() || (this.emotions.getDistress() > 50 || this.emotionState == EmotionState.DISTRESSED))) {
                heldItemTime = 0;
                this.heal(4);

                if(!level().isClientSide) {
                    HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
                    OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() - 25);
                    OwlEntity.this.emotions.setAnger(OwlEntity.this.emotions.getAnger() - 15 - this.random.nextInt(5));
                    OwlEntity.this.emotionChanged();
                }
//                this.level().addParticle(ParticleTypes.ITEM, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);

                this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (TEMPTATION_ITEMS.test(this.itemHandler.getStackInSlot(1)) && fishThrowerID != null && !this.isTame()) {
                    if (getRandom().nextFloat() < 0.5F && this.level().getPlayerByUUID(fishThrowerID) != null && !EventHooks.onAnimalTame(this, this.level().getPlayerByUUID(fishThrowerID))) {
                        this.setTame(true, true);
                        this.setOwnerUUID(this.fishThrowerID);
                        Player player = level().getPlayerByUUID(fishThrowerID);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                        }
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                if (this.itemHandler.getStackInSlot(1).hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.itemHandler.getStackInSlot(1).getCraftingRemainingItem());
                }
                this.itemHandler.getStackInSlot(1).shrink(1);
                syncInv();
            }
        } else {
            heldItemTime = 0;
        }

        determineEmotionState();

    }

    public void eatParticles(ItemStack stack)
    {
        float scale = 3f;
        if(this.isBaby())
            scale = 4;
        Vec3 vec3 = this.calculateViewVector(0, this.yHeadRot);
        for(int i = 0; i < 6; i++)
            level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), this.getRandomX(0.125D) + vec3.x / scale, this.random.nextDouble()/4f - 0.125f + this.getEyeY(), this.getRandomZ(0.125D) + vec3.z / scale, (random.nextDouble() - 0.5d) / 15d, (random.nextDouble() + 0.5d) * 0.15d, (random.nextDouble() - 0.5d) / 15d);

    }

    private float moveTo(float input, float moveTo, float speed)
    {
        float distance = moveTo - input;

        if(Math.abs(distance) <= speed)
        {
            return moveTo;
        }

        if(distance > 0)
        {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }

    public void setPerchPos(BlockPos pos) {
        this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public BlockPos getPerchPos() {
        return this.entityData.get(PERCH_POS).orElse(null);
    }


    public DyeColor getDyeColor() {

        DyeColor color = HexereiUtil.getDyeColorNamed(this.getName().getString(), 0);

        return color == null ? DyeColor.byId(this.entityData.get(OWL_DYE_COLOR)) : color;
    }

    public int getDyeColorId() {
        return this.entityData.get(OWL_DYE_COLOR);
    }

    public void setDyeColor(int color) {
        this.entityData.set(OWL_DYE_COLOR, color);
    }

    public void setDyeColor(DyeColor color) {
        this.entityData.set(OWL_DYE_COLOR, color.getId());
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return createFlyingNavigation(worldIn);
    }

    protected PathNavigation createFlyingNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }
    protected PathNavigation createGroundNavigation(Level worldIn) {
        GroundPathNavigation groundpathnavigator = new GroundPathNavigation(this, worldIn);
        groundpathnavigator.setCanOpenDoors(false);
        groundpathnavigator.setCanFloat(true);
        groundpathnavigator.setCanPassDoors(true);
        return groundpathnavigator;
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .build();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.itemHandler.getStackInSlot(1);
            case HUMANOID_ARMOR -> this.itemHandler.getStackInSlot(0);
            case ANIMAL_ARMOR -> ItemStack.EMPTY;
        };
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        readAdditionalSaveDataNoSuper(compound);
    }
    public void readAdditionalSaveDataNoSuper(CompoundTag compound) {
        this.setTypeVariant(compound.getInt("Variant"));
        if (compound.contains("IsFlyingNav"))
            switchNavigator(compound.getBoolean("IsFlyingNav"), true);
        if (compound.contains("IsFlying"))
            this.setFlying(compound.getBoolean("IsFlying"));
        else
            this.entityData.set(DATA_FLYING, false);
        if(compound.contains("InteractionRange"))
            this.interactionRange = (compound.getInt("InteractionRange"));
        if(compound.contains("CanAttack"))
            this.canAttack = (compound.getBoolean("CanAttack"));
        itemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("inv"));

        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
        if(compound.contains("DyeColor"))
            this.setDyeColor(compound.getInt("DyeColor"));

        if (compound.contains("EmotionScales")) {
            int packedEmotionScales = compound.getInt("EmotionScales");
            int happiness = (packedEmotionScales >> 16) & 0xFF;
            int distress = (packedEmotionScales >> 8) & 0xFF;
            int anger = packedEmotionScales & 0xFF;

            this.emotions = new Emotions(anger, distress, happiness);
        }
        quirkController.read(compound);
        messagingController.read(compound);

        if (compound.contains("task"))
            this.currentTask = OwlTask.byId(compound.getInt("task"));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        addAdditionalSaveDataNoSuper(compound);
    }

    public void addAdditionalSaveDataNoSuper(CompoundTag compound) {
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("InteractionRange", this.interactionRange);
        compound.putBoolean("CanAttack", this.canAttack);
        compound.putBoolean("IsFlying", this.isFlying());
        compound.putBoolean("IsFlyingNav", this.isFlyingNav());
        compound.put("inv", itemHandler.serializeNBT(this.registryAccess()));

        if (this.getPerchPos() != null) {
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
        compound.putInt("DyeColor", this.getDyeColorId());

        int packedEmotionScales = (emotions.getHappiness() << 16) | (emotions.getDistress() << 8) | emotions.getAnger();
        compound.putInt("EmotionScales", packedEmotionScales);
        quirkController.write(compound);
        messagingController.write(compound);
        compound.putInt("task", this.currentTask.ordinal());
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomsource = level.getRandom();
        OwlVariant variant;
        if (spawnGroupData instanceof OwlEntity.CrowGroupData) {
            variant = ((OwlEntity.CrowGroupData)spawnGroupData).variant;
        } else {
            boolean isVariant = randomsource.nextInt(5) == 0;
            variant = Util.getRandom(OwlVariant.values(), randomsource);
            if(!isVariant)
                variant = OwlVariant.GREAT_HORNED;
            spawnGroupData = new OwlEntity.CrowGroupData(variant);
        }

        this.setTypeVariant(variant.getId() & 255);

        List<Block> col = BuiltInRegistries.BLOCK.stream().toList();
        for(int i = 0; i < 25; i++) {
            if (col.toArray()[(int)(col.size() * new Random().nextFloat())] instanceof Block block) {
                try {
                    if (Block.isFaceFull(block.defaultBlockState().getShape(level, this.blockPosition(), CollisionContext.empty()), Direction.UP)) {
                        if (block.asItem() != Items.AIR) {
                            //replace with a generateQuirks function once I add more quirks
                            this.quirkController.addQuirk(new FavoriteBlockQuirk(block, 20));
                            break;
                        }
                    }
                } catch (Exception err) {
                    LOGGER.error("Error trying to set block as favorite: {}", block, err);
//                    err.printStackTrace();
                }
            }
        }


        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
    public static class CrowGroupData extends AgeableMobGroupData {
        public final OwlVariant variant;

        public CrowGroupData(OwlVariant pVariant) {
            super(true);
            this.variant = pVariant;
        }
    }

    public void aiStep() {
        super.aiStep();

        Vec3 motion = this.getDeltaMovement();
        if (isFlying() && motion.y < 0.0D) {
            this.setDeltaMovement(motion.multiply(1.0D, 0.7D, 1.0D));
        }
    }

    @Override
    public void travel(Vec3 vec3d) {
        if(this.getPerchPos() == null) {
            if ((this.isOrderedToSit() || this.isInSittingPose()) && this.currentTask.isNone()) {
                if (this.getNavigation().getPath() != null) {
                    this.getNavigation().stop();
                }
                vec3d = Vec3.ZERO;
            }
        }
        else {
            double topOffset = this.level().getBlockState(this.getPerchPos()).getOcclusionShape(this.level(), this.getPerchPos()).max(Direction.Axis.Y);
            if(this.distanceTo(this.getPerchPos().getX(), this.getPerchPos().getZ()) < 1 && this.position().y() >= this.getPerchPos().getY() + topOffset && this.position().y() < this.getPerchPos().above().getY() + topOffset - 0.75f) {
                if ((this.isOrderedToSit() || this.isInSittingPose()) && this.currentTask.isNone())
                {
                    if (this.getNavigation().getPath() != null) {
                        this.getNavigation().stop();
                    }
                    vec3d = Vec3.ZERO;
                }
            }
        }
        super.travel(vec3d);
    }

    public double distanceTo(double p_20276_, double p_20278_) {
        double d0 = OwlEntity.this.getX() - p_20276_ - 0.5d;
        double d1 = OwlEntity.this.getZ() - p_20278_ - 0.5d;
        return Mth.sqrt((float)(d0 * d0 + d1 * d1));
    }


    @Override
    protected int calculateFallDamage(float p_21237_, float p_21238_) {
        return 0;
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {

    }

    @Override
    public boolean isFood(ItemStack stack) {
        return TEMPTATION_ITEMS.test(stack);
    }

//    @Override
//    public MobType getMobType() {
//        return MobType.UNDEFINED;
//    }

    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {

        OwlEntity owl = ModEntityTypes.OWL.get().create(world);
        if (owl != null) {
            OwlVariant owlVariant;
            owlVariant = this.random.nextBoolean() ? this.getVariant() : ((OwlEntity)entity).getVariant();

            owl.setTypeVariant(owlVariant.getId() & 255);
            owl.setPersistenceRequired();
        }

        return owl;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.itemHandler.getStackInSlot(1).isEmpty()) {
            if (!this.isTame() && isOwlTemptItem(itemstack)) {
                ItemStack particleCopy = itemstack.copy();
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (!this.level().isClientSide) {
                    OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() - 25);
                    OwlEntity.this.emotions.setAnger(OwlEntity.this.emotions.getAnger() - 15 - this.random.nextInt(5));
                    OwlEntity.this.emotionChanged();

                    OwlEntity.this.heal(4);
                    if(!level().isClientSide)
                        HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, particleCopy));

                    if (this.random.nextInt(5) == 0 && !EventHooks.onAnimalTame(this, player)) {
                        this.tame(player);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (this.isTame() && isOwlTemptItem(itemstack) && (this.emotions.getDistress() > 15 || !this.isMaxHealth())) {
                ItemStack particleCopy = itemstack.copy();
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (!this.level().isClientSide) {
                    OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() - 25);
                    OwlEntity.this.emotions.setAnger(OwlEntity.this.emotions.getAnger() - 15 - this.random.nextInt(5));
                    OwlEntity.this.emotionChanged();

                    OwlEntity.this.heal(4);
                    if(!level().isClientSide)
                        HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, particleCopy));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (!player.isSecondaryUseActive() && this.isTame() && this.isOwnedBy(player) && !this.isBaby() && itemstack.getItem() == Items.RABBIT && !this.isInLove()) {
                if (this.getAge() == 0){
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (!this.level().isClientSide) {
                        this.setInLove(player);
                        this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
                        this.itemHandler.setStackInSlot(1, new ItemStack(Items.RABBIT));
                        this.breedGiftGivenByPlayer = true;
                        this.breedGiftGivenByPlayerUUID = player.getUUID();
                        OwlEntity.this.currentTask = OwlTask.BREEDING;
                    }
                } else {
                    if (!this.headShakeAnimation.active)
                        this.headShakeAnimation.start();
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        if (this.isTame() && this.isOwnedBy(player)) {
            if (player.isSecondaryUseActive() && isOwnedBy(player)) {
                if (!level().isClientSide()) {
                    MenuProvider containerProvider = createContainerProvider(level(), blockPosition());

                    player.openMenu(containerProvider, b -> b.writeInt(this.getId()));

                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (itemstack.getItem() == ModItems.COURIER_LETTER.get()) {


                if (this.getOwner() == player){
                    if (this.level().isClientSide) {
                        if (!CourierLetterItem.isSealed(itemstack)) {
                            player.sendSystemMessage(Component.translatable("hexerei.letter.empty"));
                        }
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        if (CourierLetterItem.isSealed(itemstack)) {
                            HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenOwlCourierSendScreenPacket(this.getId(), hand, player.getInventory().selected), serverPlayer);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (itemstack.getItem() == ModItems.COURIER_PACKAGE.get()) {

                CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(itemstack);
                boolean empty = wrapper.isEmpty();
                if (this.getOwner() == player){
                    if (this.level().isClientSide) {
                        if (!(!empty && wrapper.getSealed())) {
                            player.sendSystemMessage(Component.translatable("hexerei.package.empty"));
                        }
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        if (!empty && wrapper.getSealed()) {
                            HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenOwlCourierSendScreenPacket(this.getId(), hand, player.getInventory().selected), serverPlayer);
                        }
                    }
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (!this.level().isClientSide) {
                if(itemstack.getItem() instanceof DyeItem)
                {
                    DyeColor dyecolor = ((DyeItem)itemstack.getItem()).getDyeColor();
                    if (dyecolor != this.getDyeColor() || this.getDyeColorId() == -1) {
                        this.setDyeColor(dyecolor);
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }
                this.setOrderedToSit(!this.isOrderedToSit());

            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource) {
        ItemStack hat = this.itemHandler.getStackInSlot(0);
        ItemStack itemstack = this.itemHandler.getStackInSlot(1);
        ItemStack messageStack = this.messagingController.getMessageStack();
        if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if (!hat.isEmpty()) {
            this.spawnAtLocation(hat.copy());
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
        if (!messageStack.isEmpty()) {
            this.spawnAtLocation(messageStack.copy());
            this.messagingController.messageStack = ItemStack.EMPTY;
        }

        super.dropAllDeathLoot(p_level, damageSource);
    }

    private boolean isOwlEdible(ItemStack stack) {
        return stack.has(DataComponents.FOOD) || isOwlTemptItem(stack);
    }

    private boolean isOwlTemptItem(ItemStack stack) {
        return TEMPTATION_ITEMS.test(stack);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {

        if(this.isTame())
        {

            if (this.getHealth() < this.getMaxHealth())
                return isOwlEdible(stack);
            else if (this.emotions.getDistress() > 50 || this.emotionState == EmotionState.DISTRESSED){
                return isOwlTemptItem(stack);
            }
        }

        return (!this.isTame() && (isOwlEdible(stack) && !this.isMaxHealth()) || isOwlTemptItem(stack));
    }



    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.itemHandler.getStackInSlot(1).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.itemHandler.getStackInSlot(1), 0.0F);
        }
        Entity itemThrower = e.getOwner();
        this.itemHandler.setStackInSlot(1, duplicate);
        if (TEMPTATION_ITEMS.test(e.getItem()) && !this.isTame()) {
            fishThrowerID = itemThrower == null ? null : itemThrower.getUUID();
        } else {
            fishThrowerID = null;
        }
        if (this.currentTask == OwlTask.PICKUP_ITEM)
            this.currentTask = OwlTask.NONE;

    }

    @Override
    public void onFindTarget(ItemEntity e) {
        if (this.currentTask.isNone())
            this.currentTask = OwlTask.PICKUP_ITEM;
        ITargetsDroppedItems.super.onFindTarget(e);
    }

    @Override
    public double getMaxDistToItem() {
        return 1.0D;
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public boolean canSitOnShoulder() {
        return this.rideCooldownCounter > 100;
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (!(entityIn instanceof Player)) {
            super.doPush(entityIn);
        }
    }

    @Override
    public boolean onGround() {
        return this.isPassenger() || super.onGround();
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return this.itemHandler.isItemValid(pIndex, pStack);
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.itemHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = this.itemHandler.getStackInSlot(index).copy();
        if(count >= stack.getCount())
            this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        else{
            itemHandler.getStackInSlot(index).setCount(stack.getCount() - count);
            stack.setCount(count);
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        ItemStack stack = this.itemHandler.getStackInSlot(index).copy();
        this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < 3) {
            this.itemHandler.setStackInSlot(index, stack);
        }

        syncAdditionalData();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this) > 144.0D);
        }
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < 3; i++){
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new OwlContainer(id, this, inv, player);
//        return null;
    }

//    @Override
//    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
//        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER)
//            return handler.cast();
//        return super.getCapability(capability, facing);
//    }


    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new OwlContainer(i, OwlEntity.this, playerInventory, playerEntity);
//                return null;
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("");
            }


        };
    }

    @Override
    public boolean isPowered() {
        return false;
//        return this.itemHandler.getStackInSlot(1).is(ModItems.WARHAMMER.get()) && this.getDisplayName().getString().equals("Thor");
    }

    @Override
    public void containerChanged(Container p_18983_) {

        ItemStack stack = p_18983_.getItem(0);
        stack.setEntityRepresentation(this);
    }

    public class FloatGoal extends Goal {
        private final Mob mob;

        public FloatGoal(Mob p_25230_) {
            this.mob = p_25230_;
            this.setFlags(EnumSet.of(Flag.JUMP));
            p_25230_.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.mob.getRandom().nextFloat() < 0.8F) {
                this.mob.getJumpControl().jump();
                Vec3 randomPos = DefaultRandomPos.getPos(OwlEntity.this, 10, 7);
                BlockPos pos;
                if(randomPos == null)
                    randomPos = LandRandomPos.getPos(OwlEntity.this, 10, 7);
                if(randomPos != null)
                    pos = new BlockPos((int)randomPos.x, (int)randomPos.y, (int)randomPos.z);
                else if(OwlEntity.this.getPerchPos() != null)
                    pos = OwlEntity.this.getPerchPos().above().above();
                else
                    pos = OwlEntity.this.blockPosition().above().above();
                if(!OwlEntity.this.isInSittingPose())
                    this.mob.push(0,0.1,0);
                OwlEntity.this.flyOrWalkTo(pos.getCenter());
                OwlEntity.this.navigation.moveTo(OwlEntity.this.getNavigation().createPath(pos, 0), OwlEntity.this.isFlyingNav() ? 1.5f : 1.0f);
            }

        }
    }

    public class WaterAvoidingRandomStrollGoal extends RandomStrollGoal {
        public static final float PROBABILITY = 0.001F;
        protected final float probability;

        public WaterAvoidingRandomStrollGoal(PathfinderMob p_25987_, double p_25988_) {
            this(p_25987_, p_25988_, 0.001F);
        }

        public WaterAvoidingRandomStrollGoal(PathfinderMob p_25990_, double p_25991_, float p_25992_) {
            super(p_25990_, p_25991_);
            this.probability = p_25992_;
        }

        @Override
        public void start() {
            OwlEntity.this.walkToIfNotFlyTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
        }

        @Override
        public boolean canUse() {
            if(OwlEntity.this.isTame())
                return false;
            if(!OwlEntity.this.currentTask.isNone())
                return false;

            if(OwlEntity.this.isInSittingPose())
                return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if(OwlEntity.this.isInSittingPose())
                return false;

            return super.canContinueToUse();
        }


        @Nullable
        protected Vec3 getPosition() {
            if (this.mob.isInWaterOrBubble()) {
                Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
                return vec3 == null ? super.getPosition() : vec3;
            } else {
                return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
            }
        }
    }

    public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
        public WaterAvoidingRandomFlyingGoal(PathfinderMob p_25981_, double p_25982_) {
            super(p_25981_, p_25982_);
        }

        @Override
        public void start() {
            OwlEntity.this.switchNavigator(true);
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
        }

        @Override
        public boolean canUse() {
            if(OwlEntity.this.isTame())
                return false;
            if(!OwlEntity.this.currentTask.isNone())
                return false;
            if(OwlEntity.this.isInSittingPose())
                return false;

            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if(OwlEntity.this.isInSittingPose())
                return false;

            return super.canContinueToUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vec3 = this.mob.getViewVector(0.0F);
            int i = 8;
            Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3.x, vec3.z, (float) Math.PI / 2F);
        }
    }

    public class LandOnOwnersShoulderGoal extends Goal {
        private final OwlEntity entity;
        private ServerPlayer owner;
        private boolean isSittingOnShoulder;

        public LandOnOwnersShoulderGoal(OwlEntity p_25483_) {
            this.entity = p_25483_;
        }

        @Override
        public boolean canContinueToUse() {

            return super.canContinueToUse();
        }

        public boolean canUse() {
            if(OwlEntity.this.isBaby())
                return false;
            ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
            boolean flag = serverplayer != null && !serverplayer.isSpectator() && !serverplayer.getAbilities().flying && !serverplayer.isInWater() && !serverplayer.isInPowderSnow;
            return !this.entity.isOrderedToSit() && !this.entity.isInSittingPose() && flag && this.entity.canSitOnShoulder() && serverplayer.getPassengers().size() < 2;
        }

        public boolean isInterruptable() {
            return !this.isSittingOnShoulder;
        }

        public void start() {
            this.owner = (ServerPlayer)this.entity.getOwner();
            this.isSittingOnShoulder = false;
        }

        public void tick() {
            if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
                if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {


                    this.isSittingOnShoulder = this.entity.startRiding(this.owner, true);

                    if(!level().isClientSide)
                        HexereiPacketHandler.sendToNearbyClient(this.entity.level(), this.entity, new StartRidingPacket(this.entity, this.owner));
                }

            }
            if(isSittingOnShoulder)
                this.entity.rideCooldownCounter = 0;
        }
    }

    public class FollowOwnerGoal extends Goal {
        private final OwlEntity owl;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public FollowOwnerGoal(OwlEntity owl, double speedModifier, float startDistance, float stopDistance, boolean canFly) {
            this.owl = owl;
            this.level = owl.level();
            this.speedModifier = speedModifier;
            this.navigation = owl.getNavigation();
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.canFly = canFly;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(owl.getNavigation() instanceof GroundPathNavigation) && !(owl.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            if (!this.owl.currentTask.isNone())
                return false;
            LivingEntity livingentity = this.owl.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.owl.isOrderedToSit() || OwlEntity.this.isInSittingPose()) {
                return false;
            } else if (this.owl.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (!this.owl.currentTask.isNone())
                return false;
            if (this.navigation.isDone()) {
                return false;
            } else if (this.owl.isOrderedToSit() || OwlEntity.this.isInSittingPose()) {
                return false;
            } else {
                return !(this.owl.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.owl.getPathfindingMalus(PathType.WATER);
            this.owl.setPathfindingMalus(PathType.WATER, 0.0F);
        }

        public void stop() {

            OwlEntity.this.currentTask = OwlTask.NONE;
            this.owner = null;
            this.navigation.stop();
            this.owl.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.owl.getLookControl().setLookAt(this.owner, 10.0F, (float)this.owl.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(4);
                if (!this.owl.isLeashed() && !this.owl.isPassenger()) {

                    if (this.owl.distanceToSqr(this.owner) >= 144.0D) {
                        if (this.teleportToOwner()) {
                            OwlEntity.this.getNavigation().stop();
                            OwlEntity.this.flyOrWalkTo(this.owner.position());
                            OwlEntity.this.getNavigation().moveTo(this.owner.position().x, this.owner.position().y, this.owner.position().z, this.speedModifier);
                        }
                    } else {
                        OwlEntity.this.flyOrWalkTo(this.owner.position());
                        OwlEntity.this.getNavigation().moveTo(this.owner.position().x, this.owner.position().y, this.owner.position().z, this.speedModifier);
                    }

                }
            }
        }

        private boolean teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return true;
                }
            }

            return false;

        }

        private boolean maybeTeleportTo(int x, int y, int z) {
            if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
                return false;
            } else {
                OwlEntity.this.flyOrWalkTo(new Vec3((double) x + 0.5D, y, (double) z + 0.5D));
                this.owl.moveTo((double) x + 0.5D, y, (double) z + 0.5D, this.owl.getYRot(), this.owl.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos pos) {
//            PathType blockpathtypes = WalkNodeEvaluator.getPathTypeStatic(new PathfindingContext(this.level, this.owl), pos.mutable());

            if (pos.getY() <= -64)
                return false;

            BlockState blockstate = this.level.getBlockState(pos.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.owl.blockPosition());
                return this.level.noCollision(this.owl, this.owl.getBoundingBox().move(blockpos));
            }

        }

        private int randomIntInclusive(int p_25301_, int p_25302_) {
            return this.owl.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
        }
    }

    public class FollowParentGoal extends Goal {
        public static final int HORIZONTAL_SCAN_RANGE = 8;
        public static final int VERTICAL_SCAN_RANGE = 4;
        public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
        private final Animal animal;
        @Nullable
        private Animal parent;
        private final double speedModifier;
        private int timeToRecalcPath;

        public FollowParentGoal(Animal p_25319_, double p_25320_) {
            this.animal = p_25319_;
            this.speedModifier = p_25320_;
        }

        public boolean canUse() {
            if (this.animal.getAge() >= 0) {
                return false;

            } else if (OwlEntity.this.isOrderedToSit() || OwlEntity.this.isInSittingPose()) {
                return false;
            } else {
                List<? extends Animal> list = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
                Animal animal = null;
                double d0 = Double.MAX_VALUE;

                for(Animal animal1 : list) {
                    if (animal1.getAge() >= 0) {
                        double d1 = this.animal.distanceToSqr(animal1);
                        if (!(d1 > d0)) {
                            d0 = d1;
                            animal = animal1;
                        }
                    }
                }

                if (animal == null) {
                    return false;
                } else if (d0 < 9.0D) {
                    return false;
                } else {
                    this.parent = animal;
                    return true;
                }
            }
        }

        public boolean canContinueToUse() {
            if (this.animal.getAge() >= 0) {
                return false;
            } else if (!this.parent.isAlive()) {
                return false;
            } else if (OwlEntity.this.isOrderedToSit() || OwlEntity.this.isInSittingPose()) {
                return false;
            } else {
                double d0 = this.animal.distanceToSqr(this.parent);
                return !(d0 < 9.0D) && !(d0 > 256.0D);
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.parent = null;
        }

        public void tick() {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if(this.parent != null)
                    OwlEntity.this.walkToIfNotFlyTo(this.parent.position());
                this.animal.getNavigation().moveTo(this.parent, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
            }
        }
    }

    public class TemptGoal extends Goal {
        private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
        private final TargetingConditions targetingConditions;
        protected final OwlEntity owl;
        private final double speedModifier;
        private double px;
        private double py;
        private double pz;
        private double pRotX;
        private double pRotY;
        @Nullable
        protected Player player;
        private int calmDown;
        private boolean isRunning;
        private final Ingredient items;
        private final boolean canScare;

        public TemptGoal(OwlEntity p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_) {
            this.owl = p_25939_;
            this.speedModifier = p_25940_;
            this.items = p_25941_;
            this.canScare = p_25942_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
        }

        public boolean canUse() {
            if (owl.isInSittingPose() || owl.isOrderedToSit())
                return false;
            if (this.owl.currentTask.is(OwlTask.PICKUP_ITEM) || this.owl.currentTask.is(OwlTask.BREEDING))
                return false;
            if (this.calmDown > 0) {
                --this.calmDown;
                return false;
            } else {
                this.player = this.owl.level().getNearestPlayer(this.targetingConditions, this.owl);
                return this.player != null;
            }
        }

        private boolean shouldFollow(LivingEntity player) {
            if(this.player != null && OwlEntity.this.isTame() && OwlEntity.this.isOwnedBy(player) && !OwlEntity.this.isBaby() && OwlEntity.this.getAge() == 0) {
                return player.getMainHandItem().is(HexereiTags.Items.OWL_BREEDING_FOOD) || player.getOffhandItem().is(HexereiTags.Items.OWL_BREEDING_FOOD);
            } else
                return player.getMainHandItem().is(HexereiTags.Items.OWL_TAMING_FOOD) || player.getOffhandItem().is(HexereiTags.Items.OWL_TAMING_FOOD);
        }

        public boolean canContinueToUse() {
            if (owl.isInSittingPose() || owl.isOrderedToSit())
                return false;
            if (this.canScare()) {
                if (this.owl.distanceToSqr(this.player) < 36.0D) {
                    if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
                        return false;
                    }

                    if (Math.abs((double) this.player.getXRot() - this.pRotX) > 5.0D || Math.abs((double) this.player.getYRot() - this.pRotY) > 5.0D) {
                        return false;
                    }
                } else {
                    this.px = this.player.getX();
                    this.py = this.player.getY();
                    this.pz = this.player.getZ();
                }

                this.pRotX = this.player.getXRot();
                this.pRotY = this.player.getYRot();
            }

            return this.canUse();
        }

        protected boolean canScare() {
            return this.canScare;
        }

        public void start() {
            this.px = this.player.getX();
            this.py = this.player.getY();
            this.pz = this.player.getZ();
            this.isRunning = true;
        }

        public void stop() {

            OwlEntity.this.currentTask = OwlTask.NONE;
            this.player = null;
            this.owl.getNavigation().stop();
            this.calmDown = reducedTickDelay(100);
            this.isRunning = false;
            OwlEntity.this.setBrowPos(BrowPositioning.NORMAL);
        }

        public void tick() {

            this.owl.getLookControl().setLookAt(this.player, (float)(this.owl.getMaxHeadYRot() + 20), (float)this.owl.getMaxHeadXRot());

            if(OwlEntity.this.isInSittingPose())
                return;

            if (!OwlEntity.this.isTame()) {
                if (this.owl.distanceToSqr(this.player) < 14.25D) {
                    if (OwlEntity.this.random.nextInt(20) == 0) {
                        OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() + 5 + OwlEntity.this.random.nextInt(5));
                        OwlEntity.this.emotionChanged();
                        OwlEntity.this.determineEmotionState();
                    }
                }
            }
            else {
                if (this.owl.distanceToSqr(this.player) < 10.25D) {
                    if (OwlEntity.this.random.nextInt(20) == 0) {
                        OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() + 5 + OwlEntity.this.random.nextInt(5));
                        OwlEntity.this.emotionChanged();
                        OwlEntity.this.determineEmotionState();
                    }
                }
            }

            if (this.owl.distanceToSqr(this.player) < 6.25D) {
                this.owl.getNavigation().stop();
            } else {

                if(random.nextInt(reducedTickDelay(2)) == 0) {
                    OwlEntity.this.walkToIfNotFlyTo(this.player.position());
                }
                this.owl.getNavigation().moveTo(this.player, 1.25f * this.speedModifier);
            }

        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }
//
//    public class MeleeAttackGoal extends Goal {
//        protected final PathfinderMob mob;
//        private final double speedModifier;
//        private final boolean followingTargetEvenIfNotSeen;
//        private Path path;
//        private double pathedTargetX;
//        private double pathedTargetY;
//        private double pathedTargetZ;
//        private int ticksUntilNextPathRecalculation;
//        private int ticksUntilNextAttack;
//        private final int attackInterval = 20;
//        private long lastCanUseCheck;
//        private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
//        private int failedPathFindingPenalty = 0;
//        private boolean canPenalize = false;
//
//        public MeleeAttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_) {
//            this.mob = p_25552_;
//            this.speedModifier = p_25553_;
//            this.followingTargetEvenIfNotSeen = p_25554_;
//            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
//        }
//
//        public boolean canUse() {
//            if(!OwlEntity.this.canAttack)
//                return false;
//            long i = this.mob.level().getGameTime();
//            if (i - this.lastCanUseCheck < 20L) {
//                return false;
//            } else {
//                this.lastCanUseCheck = i;
//                LivingEntity livingentity = this.mob.getTarget();
//                if (livingentity == null) {
//                    return false;
//                } else if (!livingentity.isAlive()) {
//                    return false;
//                } else {
//                    if (canPenalize) {
//                        if (--this.ticksUntilNextPathRecalculation <= 0) {
//                            this.path = this.mob.getNavigation().createPath(livingentity, 0);
//                            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
//                            return this.path != null;
//                        } else {
//                            return true;
//                        }
//                    }
//
//                    if(getCommand() != 1)
//                    {
//                        if(OwlEntity.this.isInSittingPose()) {
//                            OwlEntity.this.setInSittingPose(false);
//                            OwlEntity.this.setOrderedToSit(false);
//                        }
//                    }
//                    this.path = this.mob.getNavigation().createPath(livingentity, 0);
//                    if (this.path != null) {
//                        return true;
//                    } else {
//                        return this.getAttackReachSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
//                    }
//                }
//            }
//        }
//
//        public boolean canContinueToUse() {
//            LivingEntity livingentity = this.mob.getTarget();
//            if (livingentity == null) {
//                return false;
//            } else if (!livingentity.isAlive()) {
//                return false;
//            } else if (!this.followingTargetEvenIfNotSeen) {
//                return !this.mob.getNavigation().isDone();
//            } else if (!this.mob.isWithinRestriction(livingentity.blockPosition())) {
//                return false;
//            } else {
//                if(getCommand() != 1)
//                {
//                    if(OwlEntity.this.isInSittingPose()) {
//                        OwlEntity.this.setInSittingPose(false);
//                        OwlEntity.this.setOrderedToSit(false);
//                    }
//                }
//                return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
//            }
//        }
//
//        public void start() {
//            OwlEntity.this.switchNavigator(true);
//            this.mob.getNavigation().moveTo(this.path, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
//            this.mob.setAggressive(true);
//            this.ticksUntilNextPathRecalculation = 0;
//            this.ticksUntilNextAttack = 0;
//        }
//
//        public void stop() {
//            LivingEntity livingentity = this.mob.getTarget();
//            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
//                this.mob.setTarget(null);
//            }
//
//            this.mob.setAggressive(false);
//            this.mob.getNavigation().stop();
//        }
//
//        public boolean requiresUpdateEveryTick() {
//            return true;
//        }
//
//        public void tick() {
//            if(OwlEntity.this.isPlayingDead())
//                return;
//            LivingEntity livingentity = this.mob.getTarget();
//            if (livingentity != null) {
//                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
//                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
//                double d1 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY() + livingentity.getBbHeight(), livingentity.getZ());
//                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
//                if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
//                    this.pathedTargetX = livingentity.getX();
//                    this.pathedTargetY = livingentity.getY();
//                    this.pathedTargetZ = livingentity.getZ();
//                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
//                    if (this.canPenalize) {
//                        this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
//                        if (this.mob.getNavigation().getPath() != null) {
//                            net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
//                            if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
//                                failedPathFindingPenalty = 0;
//                            else
//                                failedPathFindingPenalty += 10;
//                        } else {
//                            failedPathFindingPenalty += 10;
//                        }
//                    }
//                    if (d0 > 1024.0D) {
//                        this.ticksUntilNextPathRecalculation += 10;
//                    } else if (d0 > 256.0D) {
//                        this.ticksUntilNextPathRecalculation += 5;
//                    }
//
//                    OwlEntity.this.switchNavigator(true);
//                    if (!this.mob.getNavigation().moveTo(OwlEntity.this.getNavigation().createPath(livingentity, 0), OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier)) {
//                        this.ticksUntilNextPathRecalculation += 15;
//                    }
//
//                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
//                }
//
//                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
//                this.checkAndPerformAttack(livingentity, d0, d1);
//            }
//        }
//
//        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_, double p_25559_) {
//            double d0 = this.getAttackReachSqr(p_25557_);
//            if ((p_25558_ <= d0 || p_25559_ <= d0) && this.ticksUntilNextAttack <= 0) {
//                this.resetAttackCooldown();
//                this.mob.swing(InteractionHand.MAIN_HAND);
//                doHurtTarget(p_25557_);
//                OwlEntity.this.peck();
//                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> OwlEntity.this.level().getChunkAt(OwlEntity.this.blockPosition())), new CrowPeckPacket(this.mob));
//            }
//
//        }
//
//
//        public boolean doHurtTarget(Entity p_21372_) {
//            float f = (float) OwlEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
//            float f1 = (float) OwlEntity.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
//            if (p_21372_ instanceof LivingEntity) {
//                f += EnchantmentHelper.getDamageBonus(OwlEntity.this.itemHandler.getStackInSlot(1), ((LivingEntity)p_21372_).getMobType());
//                f1 += (float)EnchantmentHelper.getKnockbackBonus(OwlEntity.this);
//            }
//
//            int i = EnchantmentHelper.getFireAspect(OwlEntity.this);
//            if (i > 0) {
//                p_21372_.setSecondsOnFire(i * 4);
//            }
//
//            boolean flag = p_21372_.hurt(OwlEntity.this.damageSources().mobAttack(OwlEntity.this), f);
//            if (flag) {
//                if (f1 > 0.0F && p_21372_ instanceof LivingEntity) {
//                    ((LivingEntity) p_21372_).knockback(f1 * 0.5F, Mth.sin(OwlEntity.this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(OwlEntity.this.getYRot() * ((float) Math.PI / 180F)));
//                    OwlEntity.this.setDeltaMovement(OwlEntity.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
//                }
//
//                if (p_21372_ instanceof Player player) {
//                    maybeDisableShield(player, OwlEntity.this.itemHandler.getStackInSlot(1), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
//                }
//
//                OwlEntity.this.doEnchantDamageEffects(OwlEntity.this, p_21372_);
//                OwlEntity.this.setLastHurtMob(p_21372_);
//            }
//
//            return flag;
//        }
//
//        private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
//            if (!p_21426_.isEmpty() && !p_21427_.isEmpty() && p_21426_.getItem() instanceof AxeItem && p_21427_.is(Items.SHIELD)) {
//                float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(OwlEntity.this) * 0.05F;
//                if (OwlEntity.this.random.nextFloat() < f) {
//                    p_21425_.getCooldowns().addCooldown(Items.SHIELD, 100);
//                    OwlEntity.this.level().broadcastEntityEvent(p_21425_, (byte)30);
//                }
//            }
//
//        }
//
//
//
//        protected void resetAttackCooldown() {
//            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
//        }
//
//        protected boolean isTimeToAttack() {
//            return this.ticksUntilNextAttack <= 0;
//        }
//
//        protected int getTicksUntilNextAttack() {
//            return this.ticksUntilNextAttack;
//        }
//
//        protected int getAttackInterval() {
//            return this.adjustedTickDelay(20);
//        }
//
//        protected double getAttackReachSqr(LivingEntity p_25556_) {
//            return this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 2.5F + p_25556_.getBbWidth();
//        }
//    }

//    public class OwnerHurtByTargetGoal extends TargetGoal {
//        private final OwlEntity tameAnimal;
//        private LivingEntity ownerLastHurtBy;
//        private int timestamp;
//
//        public OwnerHurtByTargetGoal(OwlEntity p_26107_) {
//            super(p_26107_, false);
//            this.tameAnimal = p_26107_;
//            this.setFlags(EnumSet.of(Flag.TARGET));
//        }
//
//        @Override
//        protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
//            if(entity instanceof OwlEntity && OwlEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(OwlEntity.this.getOwner()))
//            {
//                return false;
//            }
//            return super.canAttack(entity, p_26152_);
//        }
//
//        public boolean canUse() {
//            if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !OwlEntity.this.getCommandSit()) {
//                LivingEntity livingentity = this.tameAnimal.getOwner();
//                if (livingentity == null) {
//                    return false;
//                } else {
//                    if(OwlEntity.this.isInSittingPose()) {
//                        OwlEntity.this.setInSittingPose(false);
//                        OwlEntity.this.setOrderedToSit(false);
//                    }
//                    this.ownerLastHurtBy = livingentity.getLastHurtByMob();
//                    int i = livingentity.getLastHurtByMobTimestamp();
//                    return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
//                }
//            } else {
//                return false;
//            }
//        }
//
//        public void start() {
//            this.mob.setTarget(this.ownerLastHurtBy);
//            LivingEntity livingentity = this.tameAnimal.getOwner();
//            if (livingentity != null) {
//                this.timestamp = livingentity.getLastHurtByMobTimestamp();
//            }
//
//            super.start();
//        }
//    }



//    public class OwnerHurtTargetGoal extends TargetGoal {
//        private final TamableAnimal tameAnimal;
//        private LivingEntity ownerLastHurt;
//        private int timestamp;
//
//        public OwnerHurtTargetGoal(TamableAnimal p_26114_) {
//            super(p_26114_, false);
//            this.tameAnimal = p_26114_;
//            this.setFlags(EnumSet.of(Flag.TARGET));
//        }
//
//        @Override
//        protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
//            if(entity instanceof TamableAnimal && OwlEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(OwlEntity.this.getOwner()))
//            {
//                return false;
//            }
//            return super.canAttack(entity, p_26152_);
//        }
//
//        public boolean canUse() {
//            if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !OwlEntity.this.getCommandSit()) {
//                LivingEntity livingentity = this.tameAnimal.getOwner();
//                if (livingentity == null) {
//                    return false;
//                } else {
//                    if(OwlEntity.this.isInSittingPose()) {
//                        OwlEntity.this.setInSittingPose(false);
//                        OwlEntity.this.setOrderedToSit(false);
//                    }
//                    this.ownerLastHurt = livingentity.getLastHurtMob();
//                    int i = livingentity.getLastHurtMobTimestamp();
//                    return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingentity);
//                }
//            } else {
//                return false;
//            }
//        }
//
//        public void start() {
//            this.mob.setTarget(this.ownerLastHurt);
//            LivingEntity livingentity = this.tameAnimal.getOwner();
//            if (livingentity != null) {
//                this.timestamp = livingentity.getLastHurtMobTimestamp();
//            }
//
//            super.start();
//        }
//    }

    public static void teleportParticles(Level level, Vec3 pos, OwlVariant owlVariant) {
        if (level.isClientSide()) {
            for(int i = 0; i < 10; ++i) {
                RandomSource random = level.getRandom();
                SimpleParticleType particleType = getParticle(owlVariant);

                Vec3 offset = new Vec3(random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), 0, random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1));

                level.addParticle(particleType, true, (double)pos.x() + 0.5D + offset.x, (double)pos.y() + random.nextDouble() * 0.15f, (double)pos.z() + 0.5D + offset.z, offset.x / 4f, random.nextDouble() * -0.05D - 0.05D, offset.z / 4f);
            }

            RandomSource random = level.getRandom();
            SimpleParticleType particleType = ModParticleTypes.STAR_BRUSH.get();
            float radius = 3;
            for(int i = 0; i < 10; i++){
                float rotation = random.nextFloat() * 18f + 36f * i;
                float rad = radius * random.nextFloat() * 0.5f;
                Vec3 offset = new Vec3(rad * Math.cos(rotation), 0, rad * Math.sin(rotation));

                level.addParticle(particleType, true, (double) pos.x() + offset.x, (double) pos.y() + random.nextDouble() * 0.15f, (double) pos.z() + offset.z, offset.x / 20f, random.nextDouble() * 0.025D, offset.z / 20f);
            }

        }
    }

    public static SimpleParticleType getParticle(OwlVariant owlVariant){
        return switch (owlVariant) {
            case GREAT_HORNED -> ModParticleTypes.OWL_TELEPORT.get();
            case BARN -> ModParticleTypes.OWL_TELEPORT_BARN.get();
            case BARRED -> ModParticleTypes.OWL_TELEPORT_BARRED.get();
            case SNOWY -> ModParticleTypes.OWL_TELEPORT_SNOWY.get();
        };
    }

    public enum OwlTask {
        NONE, DELIVER_MESSAGE, GO_TO_FAVORITE_BLOCK, PICKUP_ITEM, BREEDING;

        public boolean isNoneOr(OwlTask owlTask) {
            return this == owlTask || this == NONE;
        }
        public boolean is(OwlTask owlTask) {
            return this == owlTask;
        }
        public boolean isNone() {
            return this == NONE;
        }

        public static OwlTask byId(int id) {
            OwlTask[] type = values();
            return type[id < 0 || id >= type.length ? 0 : id];
        }
    }

    public class DeliverMessageGoal extends Goal {
        private final OwlEntity owl;

        private Vec3 wantedPos = null;

        private static final int REFRESH_MAX = 5;
        private int refresh = REFRESH_MAX;
        private int stuck = 0;
        private int stuckStageTotal = 0;
        private int checkOldPos = 0;
        private Vec3 oldPos = null;
        private Path stuckPath = null;
        private BlockPos stuckPathDest = null;
        public DeliverMessageGoal(OwlEntity owl) {
            this.owl = owl;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canContinueToUse() {
            if(!OwlEntity.this.currentTask.is(OwlTask.DELIVER_MESSAGE))
                return false;
            if(!this.owl.messagingController.isDelivering())
                return false;
//            if(!this.owl.messagingController.hasDestination())
//                return false;
            return true;
        }

        public boolean canUse() {
            if(!OwlEntity.this.currentTask.is(OwlTask.DELIVER_MESSAGE))
                return false;
            if(!this.owl.messagingController.isDelivering())
                return false;
//            if(!this.owl.messagingController.hasDestination())
//                return false;
            if (!this.owl.isTame()) {
                return false;
            }else {
                return true;
            }
        }

        @Override
        public void tick() {
            MessagingController controller = this.owl.messagingController;
            this.refresh++;
            this.stuck++;
            this.stuckStageTotal++;
            this.checkOldPos++;
            if(checkOldPos > 2 * 20) {
                if (oldPos != null) {
                    if (oldPos.distanceTo(this.owl.position()) < 1) {
                        stuck = 10 * 20;
                    }
                }
                oldPos = this.owl.position();
                this.checkOldPos = 0;
            }

            this.owl.setInSittingPose(false);

            if (stuckStageTotal >= 20 * 20) {
                switch (controller.stage) {
                    case FIND_FLY_OFF_LOCATION, FLY_OFF_AND_TELEPORT, FLY_TO_DESTINATION -> {
                        if (controller.getDestination() != null){
                            this.wantedPos = controller.getDestination().pos().getCenter();
                            controller.stage = MessagingController.Stage.FLY_TO_DESTINATION;
                        }
                    }
                    case FIND_FLY_BACK_LOCATION, FLY_BACK_AND_TELEPORT, RETURN_TO_START -> {
                        if (controller.startPos != null) {
                            this.wantedPos = controller.startPos.pos().getCenter();
                            controller.stage = MessagingController.Stage.RETURN_TO_START;
                        }
                    }
                }
                // if stuck for 20 seconds then teleport to the position its trying to get to
                Vec3 oldPos = this.owl.position();
                ResourceKey<Level> oldDim = this.owl.level().dimension();
                teleportTo(this.wantedPos.x(), this.wantedPos.y(), this.wantedPos.z());

                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), this.owl.position(), owl.getVariant()));
                HexereiPacketHandler.sendToNearbyClient(this.owl.level().getServer().getLevel(oldDim), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, owl.getVariant()));

                this.stuckStageTotal = 0;
                this.stuck = 0;
                this.stuckPath = null;
            }
            else if (stuck >= 8 * 20 || this.stuckPath != null) {
                if (this.stuckPath == null) {
                    Vec3 vec3 = this.owl.getViewVector(0.0F);
                    Vec3 vec = getPos(this.owl, 7, 2, 1, vec3.x, vec3.z, (float) Math.PI / 2F);
                    switchNavigator(true, true);
                    if (vec != null) {
                        Path path = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
                        if (path != null && path.canReach()) {
                            this.stuckPath = path;
                            this.stuckPathDest = BlockPos.containing(vec);
                            this.owl.getNavigation().moveTo(this.stuckPath, 2.0f);
                        }
                    }
                } else {
                    this.owl.getNavigation().moveTo(this.stuckPath, 2.0f);
                    if (this.owl.distanceToSqr(this.stuckPathDest.getX(), this.stuckPathDest.getY(), this.stuckPathDest.getZ()) < 3) {
                        this.stuckPath = null;
                        this.stuckPathDest = null;
                        this.stuck = 0;
                    }
                }

            } else {
                switch (controller.stage) {
                    case FIND_FLY_OFF_LOCATION -> {// find the position once then go to the next stage


                        for (int i = 0; i < 10; i++){
                            Vec3 vec3 = this.owl.getViewVector(0.0F);
                            Vec3 vec = getPos(this.owl, 14, 4, 6, vec3.x, vec3.z, (float) Math.PI / 2F);

                            if (vec != null && vec.distanceTo(this.owl.position()) > 8) {
                                switchNavigator(true, true);
                                Path path = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
                                if (path != null && path.canReach()) {
                                    this.wantedPos = vec;
                                    if (controller.forceLoadChunks()) {
                                        controller.stage = MessagingController.Stage.FLY_OFF_AND_TELEPORT;
                                        this.refresh = REFRESH_MAX;
                                        this.stuck = 0;
                                        this.stuckStageTotal = 0;
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    case FLY_OFF_AND_TELEPORT -> {// stage for flying off and teleporting


                        if (controller.hasDestination()){
                            if(this.wantedPos == null){
                                controller.stage = MessagingController.Stage.FIND_FLY_OFF_LOCATION;
                            } else {
                                BlockPos pos = controller.getDestination().pos();
                                Path path = this.owl.getNavigation().createPath(pos, 0);
                                double dist = this.owl.distanceToSqr(Vec3.atBottomCenterOf(pos));
                                if (path != null && path.getDistToTarget() < 2 && dist < 1) {
                                    controller.stage = MessagingController.Stage.FLY_TO_DESTINATION;
                                } else {
                                    path = this.owl.getNavigation().createPath(BlockPos.containing(this.wantedPos), 0);
                                    if (this.refresh > REFRESH_MAX) {
                                        this.owl.getNavigation().moveTo(path, 2.0f);
                                        this.refresh = 0;
                                    }
                                    if (controller.forceLoadChunks()) {
                                        if (OwlEntity.this.distanceToSqr(wantedPos.x, wantedPos.y, wantedPos.z) < 4) {
                                            ResourceKey<Level> oldDim = this.owl.level().dimension();
                                            Vec3 oldPos = new Vec3(this.owl.position().toVector3f());
                                            if (teleportToDest(controller.getDestination().dimension(), pos)) {
                                                ServerLevel dimChange = this.owl.getServer().getLevel(controller.getDestination().dimension());
                                                if (dimChange != null && !dimChange.equals(this.owl.level()))
                                                    this.owl.changeDimension(new DimensionTransition(
                                                            dimChange, pos.getCenter(), this.owl.getDeltaMovement(), this.owl.getYRot(), this.owl.getXRot(), DimensionTransition.DO_NOTHING
                                                    ));
                                                Vec3 newPos = new Vec3(this.owl.position().toVector3f());

                                                HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), this.owl.position(), owl.getVariant()));
                                                HexereiPacketHandler.sendToNearbyClient(this.owl.level().getServer().getLevel(oldDim), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, owl.getVariant()));


                                                this.owl.hootAnimation.start();
                                                controller.stage = MessagingController.Stage.FLY_TO_DESTINATION;
                                                this.refresh = REFRESH_MAX;
                                                this.stuck = 0;
                                                this.stuckStageTotal = 0;
                                                //add particles or something when it teleports
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    case FLY_TO_DESTINATION -> {// stage for flying to the destination and delivering the message


                        if (controller.hasDestination()){

                            BlockPos pos = controller.getDestination().pos();

                            if (this.refresh > REFRESH_MAX) {
                                Path path = this.owl.getNavigation().createPath(pos, 0);
                                this.owl.getNavigation().moveTo(path, 2.0f);
                                this.refresh = 0;
                            }

                            if (controller.forceLoadChunks()) {
                                if (OwlEntity.this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 3) {
                                    controller.stage = MessagingController.Stage.FIND_FLY_BACK_LOCATION;
                                    this.refresh = REFRESH_MAX;
                                    this.stuck = 0;
                                    this.stuckStageTotal = 0;
                                    Map<GlobalPos, OwlCourierDepotData> depots = OwlCourierDepotSavedData.get().getDepots();
                                    if (controller.destinationPos != null) {
                                        if (!this.owl.messagingController.messageStack.isEmpty() && depots.containsKey(controller.getDestination())) {
                                            for (int i = 0; i < depots.get(controller.getDestination()).items.size(); i++) {
                                                if (depots.get(controller.getDestination()).items.get(i).isEmpty()) {
                                                    depots.get(controller.getDestination()).items.set(i, this.owl.messagingController.messageStack.copy());
                                                    this.owl.messagingController.messageStack = ItemStack.EMPTY;
                                                    OwlCourierDepotSavedData.get().setDirty();
                                                    OwlCourierDepotSavedData.get().syncInvToClient(controller.getDestination());
                                                    this.owl.sync();
                                                    this.owl.peck();
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (controller.destinationPlayer != null) {
                                        if (this.owl.level().getServer().getPlayerList().getPlayer(controller.destinationPlayer.getUUID()) != null && controller.destinationPlayer.isAlive()) {
                                            if (!this.owl.messagingController.messageStack.isEmpty()) {
                                                controller.destinationPlayer.getInventory().placeItemBackInInventory(this.owl.messagingController.messageStack.copy());
                                                this.owl.messagingController.messageStack = ItemStack.EMPTY;
                                                this.owl.sync();
                                                this.owl.peck();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    case FIND_FLY_BACK_LOCATION -> {// find the position once then go to the next stage


                        for (int i = 0; i < 10; i++){
                            Vec3 vec3 = this.owl.getViewVector(0.0F);
                            Vec3 vec = getPos(this.owl, 14, 4, 6, vec3.x, vec3.z, (float) Math.PI / 2F);

                            if (vec != null && vec.distanceTo(this.owl.position()) > 8) {
                                switchNavigator(true, true);
                                Path path = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
                                if (path != null && path.canReach()) {
                                    this.wantedPos = vec;
                                    controller.stage = MessagingController.Stage.FLY_BACK_AND_TELEPORT;
                                    this.refresh = REFRESH_MAX;
                                    break;
                                }
                            }
                        }
                    }
                    case FLY_BACK_AND_TELEPORT -> {// stage for flying off from the delivery position and teleporting near the start


                        if (controller.startPos != null) {
                            Path path = this.owl.getNavigation().createPath(controller.startPos.pos(), 0);
                            if (path != null && path.getDistToTarget() < 2 && this.owl.distanceToSqr(Vec3.atBottomCenterOf(controller.startPos.pos())) < 4) {
                                controller.stage = MessagingController.Stage.RETURN_TO_START;
                            } else {
                                if (this.wantedPos == null) {
                                    controller.stage = MessagingController.Stage.FIND_FLY_BACK_LOCATION;
                                } else {
                                    Path path2 = this.owl.getNavigation().createPath(BlockPos.containing(this.wantedPos), 0);
                                    if (this.refresh > REFRESH_MAX) {
                                        this.owl.getNavigation().moveTo(path2, 3.0f);
                                        this.refresh = 0;
                                    }
                                    if (OwlEntity.this.distanceToSqr(wantedPos.x, wantedPos.y, wantedPos.z) < 2) {
                                        BlockPos pos = controller.startPos.pos();
                                        Vec3 oldPos = new Vec3(this.owl.position().toVector3f());
                                        ServerLevel dimChange = this.owl.getServer().getLevel(controller.startPos.dimension());

                                        ResourceKey<Level> dim1 = controller.startPos.dimension();
                                        ResourceKey<Level> dim2 = OwlEntity.this.level().dimension();

                                        if (teleportToDest(controller.startPos.dimension(), pos)) {
                                            Vec3 newPos = new Vec3(this.owl.position().toVector3f());
                                            if (dimChange != null && !dimChange.equals(this.owl.level()))
                                                this.owl.changeDimension(new DimensionTransition(
                                                        dimChange, pos.getCenter(), this.owl.getDeltaMovement(), this.owl.getYRot(), this.owl.getXRot(), DimensionTransition.DO_NOTHING
                                                ));

                                            HexereiPacketHandler.sendToNearbyClient(this.owl.level().getServer().getLevel(dim1), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), newPos, owl.getVariant()));
                                            HexereiPacketHandler.sendToNearbyClient(this.owl.level().getServer().getLevel(dim2), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, owl.getVariant()));

                                            controller.stage = MessagingController.Stage.RETURN_TO_START;
                                            this.wantedPos = controller.startPos.pos().getCenter();
                                            this.refresh = REFRESH_MAX;
                                            this.stuck = 0;
                                            this.stuckStageTotal = 0;
                                            this.owl.hootAnimation.start();
                                            //add particles or something when it teleports
                                        }
                                    }
                                }
                            }
                        }

                    }
                    case RETURN_TO_START -> {// fly back to the start location and end the task


                        if (controller.startPos == null) {
                            end();
                        } else {
                            BlockPos pos = controller.startPos.pos();

                            Path path = this.owl.getNavigation().createPath(pos, 0);
                            if (this.refresh > REFRESH_MAX && path != null) {

                                this.owl.getNavigation().moveTo(path, 2.0f);
                                this.refresh = 0;
                            }
                            if (OwlEntity.this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 1.5f) {
                                this.refresh = REFRESH_MAX;
                                this.stuck = 0;
                                this.stuckStageTotal = 0;
                                controller.stage = MessagingController.Stage.DONE;
                                if (!controller.messageStack.isEmpty()) {
                                    ItemEntity item = this.owl.spawnAtLocation(controller.getMessageStack().copy(), 0.0F);
                                    if (item != null)
                                        item.setUnlimitedLifetime();
                                    if (this.owl.getOwner() instanceof ServerPlayer owner) {
                                        owner.sendSystemMessage(Component.translatable("message.hexerei.owl_could_not_deliver", this.owl.getName().getString(), controller.getMessageStack().getHoverName().getString()));
                                    }
                                    controller.setMessageStack(ItemStack.EMPTY);
                                    this.owl.sync();
                                    this.owl.peck();
                                }
                                end();
                            }
                        }
                    }

                }
            }


            super.tick();
        }

        public void end() {

            MessagingController controller = this.owl.messagingController;
            if (this.owl.level() instanceof ServerLevel serverLevel && controller.stage == MessagingController.Stage.DONE)
                OwlLoadedChunksSavedData.get(serverLevel).clearOwl(serverLevel, this.owl);
            controller.startPos = null;
            controller.destinationPlayer = null;
            controller.destinationPos = null;
            controller.stage = MessagingController.Stage.FIND_FLY_OFF_LOCATION;
            this.owl.currentTask = OwlTask.NONE;

            controller.clearLastCheckedChunks();
            stop();
        }

        public void start() {
            this.owl.setInSittingPose(false);
//            this.owl.setOrderedToSit(false);
        }

        public void stop() {
//            this.owl.setInSittingPose(false);
        }

        @Nullable
        public static Vec3 getPos(PathfinderMob pMob, int pMaxDistance, int pYRange, int pY, double pX, double pZ, double pAmplifier) {
            boolean flag = GoalUtils.mobRestricted(pMob, pMaxDistance);
            return RandomPos.generateRandomPos(pMob, () -> {
                BlockPos blockpos = generateRandomPos(pMob, pMaxDistance, pYRange, pY, pX, pZ, pAmplifier, flag);

                return blockpos != null && pMob.level().getFluidState(blockpos).isEmpty() ? blockpos : null;
            });
        }

        @Nullable
        public static BlockPos generateRandomPos(PathfinderMob pMob, int pMaxDistance, int pYRange, int pY, double pX, double pZ, double pAmplifier, boolean pShortCircuit) {
            BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(pMob.getRandom(), pMaxDistance, pYRange, pY, pX, pZ, pAmplifier);
            if (blockpos == null) {
                return null;
            } else {
                BlockPos blockpos1 = RandomPos.generateRandomPosTowardDirection(pMob, pMaxDistance, pMob.getRandom(), blockpos);
                if (!GoalUtils.isOutsideLimits(blockpos1, pMob) && !GoalUtils.isRestricted(pShortCircuit, pMob, blockpos1)) {
                    blockpos1 = RandomPos.moveUpOutOfSolid(blockpos1, pMob.level().getMaxBuildHeight(), (p_148376_) -> {
                        return GoalUtils.isSolid(pMob, p_148376_);
                    });
                    return GoalUtils.hasMalus(pMob, blockpos1) ? null : blockpos1;
                } else {
                    return null;
                }
            }
        }
    }


    public class OwlFavoriteBlockGoal extends MoveToBlockGoal {
        private final OwlEntity owl;

        private int ticks;
        private int sinceLastOnBlock;
        private int cooldownTicks = 0;
        private boolean useCooldown = false;

        public OwlFavoriteBlockGoal(OwlEntity pOwl, double pSpeedModifier) {
            super(pOwl, pSpeedModifier, 12, 3);
            this.owl = pOwl;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public double acceptedDistance() {
            return 0.25;
        }

        public boolean canUse() {

            if (this.useCooldown && this.cooldownTicks < 500) {
                this.cooldownTicks++;
                if (this.cooldownTicks >= 500) {
                    this.cooldownTicks = 0;
                    this.useCooldown = false;
                }
                if (this.owl.currentTask == (OwlTask.GO_TO_FAVORITE_BLOCK))
                    this.owl.currentTask = OwlTask.NONE;
                return false;
            } else if (this.sinceLastOnBlock < 20) {
                this.sinceLastOnBlock++;
                return false;
            } else if(this.owl.isTame() && this.owl.currentTask.isNoneOr(OwlTask.GO_TO_FAVORITE_BLOCK) && FavoriteBlockQuirk.fromController(this.owl.quirkController).size() > 0) {

                if (this.owl.currentTask == (OwlTask.GO_TO_FAVORITE_BLOCK)){

                    this.ticks++;
                    if (this.ticks > 500 || (this.owl.isOrderedToSit() || this.owl.isInSittingPose())) {
                        this.owl.currentTask = OwlTask.NONE;
                        this.owl.getNavigation().stop();
                        return false;
                    }

                    List<FavoriteBlockQuirk> quirks = FavoriteBlockQuirk.fromController(this.owl.quirkController);
                    for (FavoriteBlockQuirk quirk : quirks) {
                        if (this.owl.getBlockStateOn().is(quirk.getFavoriteBlock())) {
                            this.ticks++;
                            if (this.ticks > 100) {
                                this.owl.currentTask = OwlTask.NONE;
                                this.owl.getNavigation().stop();
                                this.useCooldown = true;
                            }
                            else if (this.owl.getOwner() instanceof Player owner && this.owl.distanceToSqr(owner) >= 144.0D) {
                                if (!this.owl.isOrderedToSit())
                                    this.owl.teleportToOwner();
                                this.owl.currentTask = OwlTask.NONE;
                                this.owl.getNavigation().stop();
                                this.useCooldown = true;
                            }
                            return false;
                        }
                    }
                }
                if (this.owl.isOrderedToSit() || this.owl.isInSittingPose())
                    return false;
                return super.canUse();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            boolean canContinue = super.canContinueToUse();
            return !this.useCooldown && canContinue && !this.owl.isOrderedToSit();
        }

        public void start() {
            super.start();
        }


        public void stop() {
            super.stop();
        }


        public void tick() {
            BlockPos blockpos = this.getMoveToTarget();
            float dist = (float)blockpos.distToCenterSqr(this.mob.position().add(0, 0.5, 0));
            if (!(dist < this.acceptedDistance())) {
                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    this.owl.walkToIfNotFlyTo(new Vec3((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY(), (double)((float)blockpos.getZ()) + 0.5D));
                    this.mob.getNavigation().moveTo((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY(), (double)((float)blockpos.getZ()) + 0.5D, this.speedModifier);

                } else if (dist < 3) {
                    this.mob.getNavigation().stop();
                    this.mob.getMoveControl().setWantedPosition(blockpos.getX() + 0.5f, blockpos.getY(), blockpos.getZ() + 0.5f, this.speedModifier * 1.25f);
                }
            } else {
                if (this.ticks < 100) {
                    this.ticks++;
                } else {
                    this.owl.currentTask = OwlTask.NONE;
                    this.ticks = 0;
                    this.useCooldown = true;
                    this.stop();
                }
                this.sinceLastOnBlock = 0;
                --this.tryTicks;
            }

            if (this.owl.getOwner() instanceof Player owner && this.owl.distanceToSqr(owner) >= 144.0D) {
                this.owl.teleportToOwner();
                this.owl.currentTask = OwlTask.NONE;
                this.owl.getNavigation().stop();
                this.useCooldown = true;
                this.stop();
            }
        }
        @Override
        protected int nextStartTick(PathfinderMob pCreature) {
            return reducedTickDelay(200 + pCreature.getRandom().nextInt(200));
        }
        public boolean posEqual(BlockPos pos1, BlockPos pos2) {
            if (pos1 == null || pos2 == null) {
                return false;
            }
            return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
        }
        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            if (pLevel.isEmptyBlock(pPos.above())) {
                BlockState blockstate = pLevel.getBlockState(pPos);

                List<FavoriteBlockQuirk> quirks = FavoriteBlockQuirk.fromController(this.owl.quirkController);

                for (FavoriteBlockQuirk quirk : quirks) {
                    if (blockstate.is(quirk.getFavoriteBlock())) {
                        boolean collision = !quirk.getFavoriteBlock().defaultBlockState().getCollisionShape(pLevel, BlockPos.ZERO, CollisionContext.empty()).isEmpty();
                        boolean collisionBelow = !pLevel.getBlockState(pPos.below()).getBlock().defaultBlockState().getCollisionShape(pLevel, BlockPos.ZERO, CollisionContext.empty()).isEmpty();
                        if (collision || collisionBelow) {
                            if (this.ticks > 100) {
                                if (posEqual(this.owl.getOnPos(0.5001f), pPos)) {
                                    if (this.owl.currentTask == OwlTask.GO_TO_FAVORITE_BLOCK)
                                        this.owl.currentTask = OwlTask.NONE;
                                    return false;
                                }
                                if (posEqual(this.owl.getOnPos(), pPos)) {
                                    if (this.owl.currentTask == OwlTask.GO_TO_FAVORITE_BLOCK)
                                        this.owl.currentTask = OwlTask.NONE;
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                        this.owl.currentTask = OwlTask.GO_TO_FAVORITE_BLOCK;
                        return true;
                    }
                }

            }
            return false;
        }
    }

    public class SitWhenOrderedToGoal extends Goal {
        private final TamableAnimal mob;

        public SitWhenOrderedToGoal(TamableAnimal p_25898_) {
            this.mob = p_25898_;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public double distanceTo(double p_20276_, double p_20278_) {
            double d0 = OwlEntity.this.getX() - p_20276_ - 0.5d;
            double d1 = OwlEntity.this.getZ() - p_20278_ - 0.5d;
            return Mth.sqrt((float)(d0 * d0 + d1 * d1));
        }

        public boolean canContinueToUse() {
            if(!OwlEntity.this.currentTask.isNone())
                return false;
            if(OwlEntity.this.getPerchPos() != null) {
                double topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);
                if (!(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)) {
                    OwlEntity.this.setOrderedToSit(false);
                    return false;
                }
            }

            return this.mob.isOrderedToSit();
        }

        public boolean canUse() {
            if(OwlEntity.this.currentTask == OwlTask.GO_TO_FAVORITE_BLOCK) {
                List<FavoriteBlockQuirk> quirks = FavoriteBlockQuirk.fromController(OwlEntity.this.quirkController);

                boolean flag = false;
                for (FavoriteBlockQuirk quirk : quirks) {
                    if (OwlEntity.this.getBlockStateOn().is(quirk.getFavoriteBlock())) {
                        flag = true;
                    }
                }
                if (!flag)
                    return false;
            }
            else if(!OwlEntity.this.currentTask.isNone())
                return false;
            if (!this.mob.isTame()) {
                return false;
            } else if (this.mob.isInWaterOrBubble()) {
                return false;
            } else if (!this.mob.onGround()) {
                return false;
            } else {
                LivingEntity livingentity = this.mob.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    if(OwlEntity.this.getPerchPos() != null)
                    {
                        double topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);
                        if (!(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)) {
                            return false;
                        }
                    }

                    return this.mob.distanceToSqr(livingentity) < 288.0D && livingentity.getLastHurtByMob() != null ? false : (this.mob.isOrderedToSit());
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
        }

        public void start() {
            this.mob.getNavigation().stop();
//            this.mob.getNavigation().getPath()
            this.mob.setInSittingPose(true);
        }

        public void stop() {
            this.mob.setInSittingPose(false);
        }
    }




    public class OwlGatherItems<T extends ItemEntity> extends TargetGoal {
        protected final OwlEntity.OwlGatherItems.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super ItemEntity> targetEntitySelector;
        protected int executionChance;
        protected boolean mustUpdate;
        protected ItemEntity targetEntity;
        protected ITargetsDroppedItems hunter;
        private int tickThreshold;
        private int walkCooldown = 0;
        protected int tryTicks = 0;

        public OwlGatherItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
            this(creature, 1, checkSight, onlyNearby, null, tickThreshold);
        }


        public OwlGatherItems(PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, int ticksExisted) {
            super(creature, checkSight, onlyNearby);
            this.executionChance = chance;
            this.tickThreshold = ticksExisted;
            this.hunter = (ITargetsDroppedItems) creature;
            this.theNearestAttackableTargetSorter = new OwlEntity.OwlGatherItems.Sorter(creature);
            this.targetEntitySelector = (Predicate<ItemEntity>) item -> {
                ItemStack stack = item.getItem();
                return !stack.isEmpty() && hunter.canTargetItem(stack) && item.tickCount > tickThreshold;
            };
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(!OwlEntity.this.currentTask.isNoneOr(OwlTask.PICKUP_ITEM))
                return false;
            if (OwlEntity.this.isPassenger() || OwlEntity.this.isVehicle() && OwlEntity.this.getControllingPassenger() != null) {
                return false;
            }
            if(!OwlEntity.this.itemHandler.getStackInSlot(1).isEmpty()){
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = OwlEntity.this.level().getGameTime() % 10;
                if (this.mob.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }

            List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(OwlEntity.this.interactionRange + 1), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {

                if(OwlEntity.this.isInSittingPose()) {
                    OwlEntity.this.setInSittingPose(false);
                    OwlEntity.this.setOrderedToSit(false);
                }
                list.sort(this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                if(targetEntity == null)
                    return false;
                this.hunter.onFindTarget(targetEntity);
                return !((OwlEntity) mob).isInSittingPose() && (mob.getTarget() == null || !mob.getTarget().isAlive());
            }
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 10 == 0;
        }

        protected double getFollowDistance() {
            return 16D;
        }


        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(this.mob.getX() + 0.5, this.mob.getY()+ 0.5, this.mob.getZ() + 0.5D);
            AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.move(renderCenter);
        }

        @Override
        public void start() {
            moveTo();
            super.start();
        }

        protected void moveTo(){
            if(walkCooldown > 0){
                walkCooldown--;
            }else{
                OwlEntity.this.walkToIfNotFlyTo(this.targetEntity.position().add(0.5f, 0.25f, 0.5f));
                this.mob.getNavigation().moveTo(OwlEntity.this.getNavigation().createPath(this.targetEntity.getX() + 0.5f, this.targetEntity.getY() + 0.25f, this.targetEntity.getZ() + 0.5f, 0), OwlEntity.this.isFlyingNav() ? 1.5f : 1.25f);
                walkCooldown = 30 + this.mob.getRandom().nextInt(40);
            }
        }

        public void stop() {

            super.stop();
            this.mob.getNavigation().stop();
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            super.tick();

            if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
                this.stop();
                this.mob.getNavigation().stop();
            } else {
                moveTo();
            }
            if (targetEntity != null && this.mob.hasLineOfSight(targetEntity) && this.mob.getBbWidth() > hunter.getMaxDistToItem() && this.mob.onGround()) {
                this.mob.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY() + 0.5f, targetEntity.getZ(), 1.5f);
            }
            if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < this.hunter.getMaxDistToItem() && OwlEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
                if (!((OwlEntity) hunter).peckAnimation.active) {
                    hunter.peck();
                    HexereiPacketHandler.sendToNearbyClient(this.mob.level(), this.mob, new PeckPacket(this.mob));
                }


                if (((OwlEntity) hunter).peckAnimation.peckRot > 40) {
                    hunter.onGetItem(targetEntity);
                    this.targetEntity.getItem().shrink(1);
                    stop();
                }

            }

            OwlEntity crow = (OwlEntity) mob;
            if (this.targetEntity != null) {
                if (this.mob.distanceTo(targetEntity) <= getMaxDistToItem()) {
                    crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), targetEntity.getY() + 0.5f, this.targetEntity.getZ(), 1.5f);
                }
                if(!crow.isInSittingPose()) {
                    OwlEntity.this.flyOrWalkTo(this.targetEntity.position().add(0, 0.5f, 0));
                    this.mob.getNavigation().moveTo(OwlEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 0.5f, this.targetEntity.getZ(), 0), OwlEntity.this.isFlyingNav() ? 1.5f : 1.0f);
                }

                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    if(targetEntity.position().distanceTo(OwlEntity.this.position()) < 3 && OwlEntity.this.position().y < targetEntity.position().y()) {
                        OwlEntity.this.setNoGravity(false);
                        OwlEntity.this.push((this.targetEntity.position().x - OwlEntity.this.position().x) / 50.0f, (this.targetEntity.position().y - OwlEntity.this.position().y) / 50.0f + 0.1f, (this.targetEntity.position().z - OwlEntity.this.position().z) / 50.0f);
                    }
                    OwlEntity.this.flyOrWalkTo(this.targetEntity.position().add(0, 3.0f, 0));
                    this.mob.getNavigation().moveTo(OwlEntity.this.getNavigation().createPath(this.targetEntity.getX(), targetEntity.getY() + 3f, this.targetEntity.getZ(), 0), OwlEntity.this.isFlyingNav() ? 1.5f : 1.0f);
                }



            }

        }

        public void makeUpdate() {
            this.mustUpdate = true;
        }

        @Override
        public boolean canContinueToUse() {
            if(OwlEntity.this.getHealth() >= OwlEntity.this.getMaxHealth() && OwlEntity.this.isTame())
                return false;
            boolean path = this.mob.getBbWidth() > 2D ||  !this.mob.getNavigation().isDone();

            if(OwlEntity.this.isInSittingPose()) {
                OwlEntity.this.setInSittingPose(false);
                OwlEntity.this.setOrderedToSit(false);
            }
            return path && targetEntity != null && targetEntity.isAlive() && !((OwlEntity) mob).isInSittingPose() &&  (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        public static class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.distanceToSqr(p_compare_1_);
                double d1 = this.theEntity.distanceToSqr(p_compare_2_);
                return Double.compare(d0, d1);
            }
        }

    }

    public class BreedGoal extends Goal {
        private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight();
        protected final Animal animal;
        private final Class<? extends Animal> partnerClass;
        protected final Level level;
        @Nullable
        protected Animal partner;
        private int loveTime;
        private final double speedModifier;

        public BreedGoal(Animal p_25122_, double p_25123_) {
            this(p_25122_, p_25123_, p_25122_.getClass());
        }

        public BreedGoal(Animal p_25125_, double p_25126_, Class<? extends Animal> p_25127_) {
            this.animal = p_25125_;
            this.level = p_25125_.level();
            this.partnerClass = p_25127_;
            this.speedModifier = p_25126_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {

            if (OwlEntity.this.currentTask.is(OwlTask.BREEDING)) {
                this.partner = this.getFreePartner();
                return this.partner != null;
            } else
                return false;
        }

        public boolean canContinueToUse() {
            return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 120;
        }

        @Override
        public void start() {
            if(OwlEntity.this.isInSittingPose()) {
                OwlEntity.this.setInSittingPose(false);
                OwlEntity.this.setOrderedToSit(false);
            }
            super.start();
        }

        public void stop() {
//            CrowEntity.this.breedGiftGivenByPlayer = false;
        }

        public void tick() {

            if(this.partner != null){
                this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
                OwlEntity.this.walkToIfNotFlyTo(this.partner.position());
                this.animal.getNavigation().moveTo(this.partner, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
                ++this.loveTime;
                if (this.animal.distanceToSqr(this.partner) < 4.0D && OwlEntity.this.breedGiftGivenByPlayer) {


                    if(!((OwlEntity) this.partner).isOrderedToSit())
                        ((OwlEntity) this.partner).setOrderedToSit(true);
                    if (!this.partner.getLookControl().isLookingAtTarget())
                        this.partner.getLookControl().setLookAt(this.animal);

                    OwlEntity.this.waitToGiveTime++;

                    if (OwlEntity.this.waitToGiveTime > 20 && OwlEntity.this.onGround() && OwlEntity.this.itemHandler.getStackInSlot(1).is(Items.RABBIT)) {

                        ((OwlEntity) this.partner).setOrderedToSit(false);
                        OwlEntity.this.waitToGiveTime = 0;
                        OwlEntity.this.breedGiftGivenByPlayer = false;

                        OwlEntity.this.peck();
                        HexereiPacketHandler.sendToNearbyClient(OwlEntity.this.level(), OwlEntity.this, new PeckPacket(OwlEntity.this));

                        ItemStack stack = ((OwlEntity) this.partner).itemHandler.getStackInSlot(1).copy();
                        ItemStack stack2 = OwlEntity.this.itemHandler.getStackInSlot(1).copy();
                        ((OwlEntity) this.partner).itemHandler.setStackInSlot(1, stack2);
                        OwlEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                        ItemEntity itemEntity = new ItemEntity(this.partner.level(), this.partner.position().x, this.partner.position().y, this.partner.position().z, stack);
                        this.partner.level().addFreshEntity(itemEntity);

                        if (OwlEntity.this.breedGiftGivenByPlayerUUID != null && level.getPlayerByUUID(OwlEntity.this.breedGiftGivenByPlayerUUID) != null) {
                            ((OwlEntity) this.partner).breedGiftGivenByPlayerUUID = OwlEntity.this.breedGiftGivenByPlayerUUID;
                            ((OwlEntity) this.partner).breedGiftGivenByPartnerTimer = 20;
                        } else if (OwlEntity.this.getOwner() instanceof Player)
                            this.partner.setInLove((Player) OwlEntity.this.getOwner());
                    }
                }
                if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0D) {
                    this.breed();
                    OwlEntity.this.currentTask = OwlTask.NONE;
                    this.partner = null;
                    this.loveTime = 0;
                }
            }

        }
        public boolean canMateOwlBringGift(Animal animal) {
            if(animal.isBaby())
                return false;
            if (animal == OwlEntity.this) {
                return false;
            } else if (animal.getClass() != OwlEntity.this.getClass()) {
                return false;
            } else {
                return OwlEntity.this.itemHandler.getStackInSlot(1).is(Items.RABBIT) && animal.getAge() == 0;
            }
        }
        public boolean canMateOwlReceiveGift(Animal animal) {
            if(animal.isBaby())
                return false;
            if (animal == OwlEntity.this) {
                return false;
            } else if (animal.getClass() != OwlEntity.this.getClass()) {
                return false;
            } else {
                return OwlEntity.this.isInLove() && animal.isInLove();
            }
        }


        @Nullable
        private Animal getFreePartner() {
            List<? extends Animal> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(16.0D));
            double d0 = Double.MAX_VALUE;
            Animal animal = null;

            if(breedGiftGivenByPlayer) {
                for (Animal animal1 : list) {
                    if (this.canMateOwlBringGift(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                        animal = animal1;
                        d0 = this.animal.distanceToSqr(animal1);
                    }
                }
            } else {
                for (Animal animal1 : list) {
                    if (this.canMateOwlReceiveGift(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                        animal = animal1;
                        d0 = this.animal.distanceToSqr(animal1);
                    }
                }
            }

            return animal;
        }

        protected void breed() {
            OwlEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            spawnChildFromBreeding((ServerLevel)this.level, this.partner);
        }



        public void spawnChildFromBreeding(ServerLevel p_27564_, Animal p_27565_) {
            AgeableMob ageablemob = OwlEntity.this.getBreedOffspring(p_27564_, p_27565_);
            final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(OwlEntity.this, p_27565_, ageablemob);
            final boolean cancelled = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled();
            ageablemob = event.getChild();
            if (cancelled) {
                //Reset the "inLove" state for the animals
                OwlEntity.this.setAge(6000);
                p_27565_.setAge(6000);
                OwlEntity.this.resetLove();
                p_27565_.resetLove();
                return;
            }
            if (ageablemob != null) {
                ServerPlayer serverplayer = OwlEntity.this.getLoveCause();
                if (serverplayer == null && p_27565_.getLoveCause() != null) {
                    serverplayer = p_27565_.getLoveCause();
                }

                if (serverplayer != null) {
                    serverplayer.awardStat(Stats.ANIMALS_BRED);
                    CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, OwlEntity.this, p_27565_, ageablemob);
                }

                OwlEntity.this.setAge(6000);
                p_27565_.setAge(6000);
                OwlEntity.this.resetLove();
                p_27565_.resetLove();
                ageablemob.setBaby(true);
                ageablemob.moveTo(this.partner.getX(), this.partner.getY(), this.partner.getZ(), 0.0F, 0.0F);
                p_27564_.addFreshEntityWithPassengers(ageablemob);

                ((OwlEntity)ageablemob).setOwnerUUID(OwlEntity.this.getOwnerUUID());
                ((OwlEntity)ageablemob).setTame(true, false);
//                if (OwlEntity.this.getOwner() != null)
//                    ((OwlEntity)ageablemob).tame((Player) OwlEntity.this.getOwner());
                ((OwlEntity)ageablemob).setOrderedToSit(true);
                p_27564_.broadcastEntityEvent(OwlEntity.this, (byte)18);
                if (p_27564_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    p_27564_.addFreshEntity(new ExperienceOrb(p_27564_, OwlEntity.this.getX(), OwlEntity.this.getY(), OwlEntity.this.getZ(), OwlEntity.this.getRandom().nextInt(7) + 1));
                }

            }
        }

    }

    public class WanderAroundPlayerGoal extends RandomStrollGoal {

        public WanderAroundPlayerGoal(PathfinderMob pMob, double pSpeedModifier) {
            super(pMob, pSpeedModifier, 85);
        }

        @Override
        public void start() {
            OwlEntity.this.flyOrWalkTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25f * this.speedModifier : 0.75f * this.speedModifier);
            this.mob.setNoActionTime(0);
        }

        @Override
        public boolean canUse() {
            if(!OwlEntity.this.currentTask.isNone())
                return false;
            return super.canUse();
//            if (this.mob.isVehicle()) {
//                return false;
//            } else {
//                if (!this.forceTrigger) {
//                    int rtd = reducedTickDelay(this.interval);
//                    int next = this.mob.getRandom().nextInt(rtd);
//                    if (next != 0) {
//                        return false;
//                    }
//                }
//
//                Vec3 vec3 = this.getPosition();
//                if (vec3 == null) {
//                    return false;
//                } else {
//                    this.wantedX = vec3.x;
//                    this.wantedY = vec3.y;
//                    this.wantedZ = vec3.z;
//                    this.forceTrigger = false;
//                    return true;
//                }
//            }
        }

        @Override
        public boolean canContinueToUse() {
            if(!OwlEntity.this.currentTask.isNone())
                return false;
            return super.canContinueToUse();
        }

        protected Vec3 getPosition() {
            int pRadius = 5;
            int pVerticalDistance = 7;
            if (OwlEntity.this.getOwner() != null) {
                boolean flag = GoalUtils.mobRestricted(this.mob, pRadius);

                double d0 = Double.NEGATIVE_INFINITY;
                BlockPos blockpos = null;

                for(int i = 0; i < 10; ++i) {
                    BlockPos pos = RandomPos.generateRandomDirection(OwlEntity.this.getRandom(), pRadius, pVerticalDistance);
                    BlockPos blockpos1;
                    if (OwlEntity.this.getPerchPos() != null)
                        blockpos1 = generateRandomPosTowardDirection(Vec3.atLowerCornerOf(OwlEntity.this.getPerchPos()), this.mob, flag, pos);
                    else if (OwlEntity.this.getOwner() != null)
                        blockpos1 = generateRandomPosTowardDirection(OwlEntity.this.getOwner().position(), this.mob, flag, pos);
                    else
                        blockpos1 = OwlEntity.this.blockPosition();

                    if (blockpos1 != null) {
                        double d1 = OwlEntity.this.getWalkTargetValue(blockpos1);
                        if (d1 > d0) {
                            d0 = d1;
                            blockpos = blockpos1;
                        }
                    }
                }

                return blockpos != null ? Vec3.atBottomCenterOf(blockpos) : null;

            }
            return DefaultRandomPos.getPos(this.mob, 10, 7);
        }

        private static BlockPos generateRandomPosTowardDirection(Position pos, PathfinderMob pMob, boolean pShortCircuit, BlockPos pPos) {
            BlockPos blockpos = generateRandomPosTowardDirection(pos, pPos);
            boolean outsidelimits = !GoalUtils.isOutsideLimits(blockpos, pMob);
            boolean restricted = !GoalUtils.isRestricted(pShortCircuit, pMob, blockpos);
            boolean notstable = !GoalUtils.isNotStable(pMob.getNavigation(), blockpos);
            boolean malus = !GoalUtils.hasMalus(pMob, blockpos);
            return outsidelimits && restricted  && malus ? blockpos : null;
        }

        /**
         * @return a random position within range, only if the mob is currently restricted
         */
        public static BlockPos generateRandomPosTowardDirection(Position pos, BlockPos pPos) {
            int i = pPos.getX();
            int j = pPos.getZ();

            return BlockPos.containing((double)i + pos.x(), (double)pPos.getY() + pos.y(), (double)j + pos.z());
        }


    }


    public class FlyBackToPerchGoal extends Goal {
        private final TamableAnimal mob;

        public FlyBackToPerchGoal(TamableAnimal p_25898_) {
            this.mob = p_25898_;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canContinueToUse() {
            if(OwlEntity.this.getPerchPos() == null)
                return false;
//            double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
//            if(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)
//                return false;
//            if(this.mob.distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY())
//            return this.mob.distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) > 1;

            double topOffset = 0;
            if(OwlEntity.this.getPerchPos() != null)
                topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);
            if(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
                    return false;
            }
            else {
                if(OwlEntity.this.isInSittingPose()) {
                    OwlEntity.this.setInSittingPose(false);
                    OwlEntity.this.setOrderedToSit(false);
                }
                return true;
            }

//            return true;
        }

        public double distanceTo(double p_20276_, double p_20278_) {
            double d0 = OwlEntity.this.getX() - p_20276_ - 0.5d;
            double d1 = OwlEntity.this.getZ() - p_20278_ - 0.5d;
            return Mth.sqrt((float)(d0 * d0 + d1 * d1));
        }

        public boolean canUse() {
            if(!OwlEntity.this.currentTask.isNone())
                return false;
            if (OwlEntity.this.isInSittingPose()) {
                if (getPerchPos() != null){
                    if (getPerchPos().distToCenterSqr(OwlEntity.this.position().x, OwlEntity.this.position().y, OwlEntity.this.position().z) < 1)
                        return false;
                } else {
                    return false;
                }
            }

            if (!this.mob.isTame()) {
                return false;
            } else if (this.mob.isInWaterOrBubble()) {
                return false;
            } else {
                LivingEntity livingentity = this.mob.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    if(OwlEntity.this.getPerchPos() == null)
                        return false;

                    double topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);
                    if(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
                        if(OwlEntity.this.isInSittingPose())
                            return false;
                    }
                    else{
                        if(OwlEntity.this.isInSittingPose()) {
                            OwlEntity.this.setInSittingPose(false);
                            OwlEntity.this.setOrderedToSit(false);
                        }
                        return true;
                    }
                    return !(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset);
                }
            }
        }

        @Override
        public void tick() {
            double topOffset = 0;
            if(OwlEntity.this.getPerchPos() != null)
                topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);

            boolean isStuck = false;

            if(OwlEntity.this.getPerchPos() != null){
                if (!(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)) {

                    flyOrWalkTo(OwlEntity.this.getPerchPos().above().getCenter());
                    OwlEntity.this.navigation.moveTo(this.mob.getNavigation().createPath(OwlEntity.this.getPerchPos().above(), -1), OwlEntity.this.isFlyingNav() ? 1.5f : 1.0f);
                }
            }



            super.tick();
        }

        public void start() {
            if(OwlEntity.this.getPerchPos() != null){
                flyOrWalkTo(OwlEntity.this.getPerchPos().above().getCenter());

                OwlEntity.this.navigation.moveTo(this.mob.getNavigation().createPath(OwlEntity.this.getPerchPos().above(), 0), OwlEntity.this.isFlyingNav() ? 1.5f : 1.0f);
            }
        }

        public void stop() {
            OwlEntity.this.currentTask = OwlTask.NONE;
            if(OwlEntity.this.getPerchPos() != null){
                double topOffset = OwlEntity.this.level().getBlockState(OwlEntity.this.getPerchPos()).getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos()).max(Direction.Axis.Y);
                if (this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
//                CrowEntity.this.setCommandSit();
                    OwlEntity.this.setInSittingPose(true);
                }
            }
//            this.mob.setInSittingPose(false);
        }
    }

    private void flyOrWalkTo(Vec3 pos){

        Path path1 = OwlEntity.this.flyingNav.createPath(BlockPos.containing(pos), 0);
        Path path2 = OwlEntity.this.groundNav.createPath(BlockPos.containing(pos), 0);
        if (path1 != null){
            if (path2 == null || !path2.canReach())
                switchNavigator(true);
            else if (path2.getDistToTarget() > path1.getDistToTarget()) {
                switchNavigator(true);
            } else {
                // if can walk then randomly fly or walk
                switchNavigator(!random.nextBoolean());
            }
        }
    }

    private void walkToIfNotFlyTo(Vec3 pos){

        Path path1 = OwlEntity.this.flyingNav.createPath(BlockPos.containing(pos), 0);
        Path path2 = OwlEntity.this.groundNav.createPath(BlockPos.containing(pos), 0);
        if (path1 != null){
            if (path2 == null)
                switchNavigator(true);
            else if (Math.max(0, path2.getDistToTarget() - 2)> path1.getDistToTarget()) {
                switchNavigator(true);
            } else {
                switchNavigator(false);
            }
        }
    }


    public enum BrowAnim {
        LEFT, RIGHT, BOTH
    }
    public enum BrowPositioning {
        NORMAL(0, 0, 0), PLEAD(0.75f, 0.05f, 40), ANGRY(0.25f, 0.5f, -15);
        final float xOffset;
        final float yOffset;
        final float zRot;
        BrowPositioning(float xOffset, float yOffset, float zRot) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zRot = zRot;
        }

        public float getxOffset() {
            return xOffset;
        }

        public float getyOffset() {
            return yOffset;
        }

        public float getzRot() {
            return zRot;
        }
    }

    public enum EmotionState {

        CONTEMPT(0, 0, 0,new Emotions(0, 0, 0)),
        HAPPY(0, 0, 0,new Emotions(0, 0, 100)),
        DISTRESSED(0.75f, 0.05f, 40,new Emotions(0, 100, 0)),
        ANGRY(0.25f, 0.5f, -15,new Emotions(100, 0, 0));

        private final Emotions scales;
        final float browXOffset;
        final float browYOffset;
        final float browZRot;

        EmotionState(float xOffset, float yOffset, float zRot, Emotions scales) {
            this.browXOffset = xOffset;
            this.browYOffset = yOffset;
            this.browZRot = zRot;

            this.scales = scales;
        }

        public Emotions getScales() {
            return scales;
        }
        public float getxOffset() {
            return browXOffset;
        }

        public float getyOffset() {
            return browYOffset;
        }

        public float getzRot() {
            return browZRot;
        }
    }

    public static class Emotions {
        private int anger; // 0 to 100 (angry)
        private int distress; // 0 to 100 (sad/wanting something)
        private int happiness; // 0 to 100 (happy)

        public Emotions(int anger, int distress, int happiness) {
            this.anger = anger;
            this.distress = distress;
            this.happiness = happiness;
        }

        public void setAnger(int anger) {
            this.anger = Mth.clamp(anger, 0, 100);
        }

        public void setDistress(int distress) {
            this.distress = Mth.clamp(distress, 0, 100);
        }

        public void setHappiness(int happiness) {
            this.happiness = Mth.clamp(happiness, 0, 100);
        }

        public int getAnger() {
            return anger;
        }

        public int getDistress() {
            return distress;
        }

        public int getHappiness() {
            return happiness;
        }

        public boolean isHappy() {
            return getAnger() < 30 && getHappiness() > 50;
        }


        // Getters and setters for anger and happiness...
    }



    private void teleportToOwner() {
        if (this.getOwner() instanceof Player owner) {
            BlockPos blockpos = owner.blockPosition();

            for (int i = 0; i < 30; ++i) {
                int j = this.randomIntInclusive(-5, 5);
                int k = this.randomIntInclusive(-1, 5);
                int l = this.randomIntInclusive(-5, 5);
                boolean flag = this.teleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }
        }

    }

    private boolean teleportToDest(ResourceKey<Level> dim, BlockPos blockpos) {
        for (int i = 0; i < 30; ++i) {
            int j = this.randomInt(6, 12);
            int k = this.randomInt(6, 12);
            int l = this.randomInt(6, 12);
            boolean flag = this.teleportTo(dim, blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return true;
            }
        }
        return false;

    }
    private int randomInt(int min, int max) {
        return (this.getRandom().nextInt((max - min) + 1) + min) * (this.getRandom().nextBoolean() ? -1 : 1);
    }


    private boolean teleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {

            switchNavigator(true, true);
            this.setPos(x, y, z);
            this.moveTo((double) x + 0.5D, y, (double) z + 0.5D, this.getYRot(), this.getXRot());

            return true;
        }
    }
    private boolean teleportTo(ResourceKey<Level> dim, int x, int y, int z) {
        if (!this.canTeleportTo(dim, new BlockPos(x, y, z))) {
            return false;
        } else {

            switchNavigator(true, true);
            this.setPos(x, y, z);
            this.moveTo((double) x + 0.5D, y, (double) z + 0.5D, this.getYRot(), this.getXRot());

            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
//        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level(), pos.mutable());
//
//        BlockState blockstate = this.level().getBlockState(pos.below());
        if (pos.getY() <= -64)
            return false;

        BlockPos offset = pos.subtract(this.blockPosition());

        FluidState state = this.level().getFluidState(pos);
        return this.level().noCollision(this, this.getBoundingBox().move(offset)) && state.isEmpty();

    }

    private boolean canTeleportTo(ResourceKey<Level> dim, BlockPos pos) {
//        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level(), pos.mutable());
//
//        BlockState blockstate = this.level().getBlockState(pos.below());
        if (pos.getY() <= -64)
            return false;

        BlockPos offset = pos.subtract(this.blockPosition());

        if (this.getServer().getLevel(dim) == null)
            return false;

        FluidState state = this.getServer().getLevel(dim).getFluidState(pos);
        return this.getServer().getLevel(dim).noCollision(this, this.getBoundingBox().move(offset)) && state.isEmpty();

    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }


}