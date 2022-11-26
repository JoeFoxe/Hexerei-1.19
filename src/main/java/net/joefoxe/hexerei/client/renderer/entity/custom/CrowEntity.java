package net.joefoxe.hexerei.client.renderer.entity.custom;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.PickableDoubleFlower;
import net.joefoxe.hexerei.block.custom.PickableFlower;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.ITargetsDroppedItems;
import net.joefoxe.hexerei.client.renderer.entity.render.CrowVariant;
import net.joefoxe.hexerei.config.HexConfig;
//import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CrowEntity extends TamableAnimal implements ContainerListener, FlyingAnimal, ITargetsDroppedItems, Container, MenuProvider, PowerableMob {
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModItems.SEED_MIXTURE.get());
    public boolean headTilt;
    public int headTiltTimer;
    public float headZTiltAngle;
    public float headZTiltAngleActual;
    public float headXTiltAngle;
    public float headXTiltAngleActual;
    public boolean caw;
    public int cawTimer;
    public float cawTiltAngle;
    public float cawTiltAngleActual;
    public boolean tailWag;
    public int tailWagTimer;
    public float tailWagTiltAngle;
    public float tailWagTiltAngleActual;
    public boolean tailFan;
    public int tailFanTimer;
    public float tailFanTiltAngle;
    public float tailFanTiltAngleActual;
    public boolean peck;
    public int peckTimer;
    public float peckTiltAngle;
    public float peckTiltAngleActual;
    public float rightWingAngle;
    public float rightWingAngleActual;
    public float leftWingAngle;
    public float leftWingAngleActual;
    public boolean dance;
    public int animationCounter;
    public int pickpocketTimer;
    private BlockPos jukebox;

    private int rideCooldownCounter;
    public int cofferHerbJarListResetTimer;
    public List<BlockPos> cofferHerbJarList;
    public List<Villager> villagerList;

    protected final Predicate<Villager> targetEntitySelector;

    private UUID seedThrowerID;
    private int heldItemTime = 0;
    public boolean aiItemFlag = false;
    public boolean aiCofferTileFlag = false;
    public boolean doingTask;
    public boolean searchForNewCropTarget;
    public int searchForNewCropTargetTimer = 0;
    public boolean depositItemBeforePerch;
    public boolean breedNuggetGivenByPlayer = false;
    public int breedNuggetGivenByCrowTimer = 0;
    public UUID breedNuggetGivenByPlayerUUID;
    public int waitToGiveTime = 0;
    public int stuckTimer = 0;
    public Vec3 lastStuckCheckPos = new Vec3(0, 0, 0);

    public boolean sync;
    public final ItemStackHandler itemHandler = createHandler();
//    private LazyOptional<?> itemHandler = null;
//    public SimpleContainer inventory;
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

//    public NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HELP_COMMAND = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> CROW_DYE_COLOR = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.BOOLEAN);

    public int playingDead;

    private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
    public static final int TOTAL_PLAYDEAD_TIME = 200;

    public CrowEntity(EntityType<CrowEntity> type, Level worldIn) {
        super(type, worldIn);
        registerGoals();
        this.moveControl = new FlyingMoveControl(this, 10, false) {

            @Override
            public void tick() {
                if (!CrowEntity.this.isPlayingDead()) {
                    super.tick();
                }

            }
        };
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 4.0F);
        this.headTilt = false;
        this.headTiltTimer = new Random().nextInt(100);
        this.headZTiltAngle = 0;
        this.headZTiltAngleActual = 0;
        this.headXTiltAngle = 0;
        this.headXTiltAngleActual = 0;
        this.cofferHerbJarListResetTimer = 80;
        this.animationCounter = 0;
        this.pickpocketTimer = 0;
        this.targetEntitySelector = input -> true;
        this.doingTask = false;
        this.sync = false;

        this.caw = false;
        this.cawTimer = new Random().nextInt(360) + 360;
        this.cawTiltAngle = 0;
        this.cawTiltAngleActual = 0;

        this.tailWag = false;
        this.tailWagTimer = new Random().nextInt(100);
        this.tailWagTiltAngle = 0;
        this.tailWagTiltAngleActual = 0;

        this.tailFan = false;
        this.tailFanTimer = new Random().nextInt(100);
        this.tailFanTiltAngle = 0;
        this.tailFanTiltAngleActual = 0;

        this.peck = false;
        this.peckTiltAngle = 0;
        this.peckTiltAngleActual = 0;

        this.rightWingAngle = 0;
        this.rightWingAngleActual = 0;
        this.leftWingAngle = 0;
        this.leftWingAngleActual = 0;


        this.playingDead = 0;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new FlyBackToPerchGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F){
            @Override
            public void tick() {
                if(!CrowEntity.this.isPlayingDead())
                    super.tick();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this){
            @Override
            public void tick() {
                if(!CrowEntity.this.isPlayingDead())
                    super.tick();
            }
        });
        this.targetSelector.addGoal(2, new CrowGatherItems(this, false, false, 40, 24));
        this.goalSelector.addGoal(2, new CrowDepositCoffer(this));
        this.goalSelector.addGoal(2, new CrowHarvestGoal((double)1.5F, 24, 6));
        this.goalSelector.addGoal(2, new CrowPickpocketVillager(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0D, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(COMMAND, 0);
        this.entityData.define(HELP_COMMAND, 0);
        this.entityData.define(PERCH_POS, Optional.empty());
        this.entityData.define(CROW_DYE_COLOR, -1);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
        this.entityData.define(DATA_PLAYING_DEAD, false);
    }

    public void sync() {
        setChanged();
        if (!level.isClientSide) {

            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowSyncPacket(this, saveWithoutId(new CompoundTag())));
        }

    }

    public void syncAdditionalData() {
        setChanged();
        if (!level.isClientSide) {

            CompoundTag tag = new CompoundTag();
            addAdditionalSaveDataNoSuper(tag);
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowSyncAdditonalDataPacket(this, tag));
        }

    }

    protected int getInventorySize() {
        return 3;
    }

//    protected void createInventory() {
//        SimpleContainer simplecontainer = this.inventory;
//        this.inventory = new SimpleContainer(this.getInventorySize());
//        if (simplecontainer != null) {
//            simplecontainer.removeListener(this);
//            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());
//
//            for(int j = 0; j < i; ++j) {
//                ItemStack itemstack = simplecontainer.getItem(j);
//                if (!itemstack.isEmpty()) {
//                    this.itemHandler.setStackInSlot(j, itemstack.copy());
//                }
//            }
//        }
//
//        this.inventory.addListener(this);
////        this.updateContainerEquipment();
//        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
//    }


    @Override
    public void die(DamageSource pCause) {
        if (!this.checkTotemDeathProtection(pCause)) {
            super.die(pCause);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        float f = this.getHealth();
        if (!this.level.isClientSide && !this.isNoAi() && ((float)this.level.random.nextInt(3) < pAmount || f / this.getMaxHealth() < 0.5F) && pAmount < f && (pSource.getEntity() != null || pSource.getDirectEntity() != null) && !this.isPlayingDead()) {
            this.playingDead = TOTAL_PLAYDEAD_TIME;
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
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                syncAdditionalData();
            }

//            @Override
//            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//
//                if (slot == 0 && !stack.is(HexereiTags.Items.BROOM_MISC) && !stack.is(HexereiTags.Items.MEDIUM_SATCHELS) && !stack.is(HexereiTags.Items.LARGE_SATCHELS))
//                    return false;
//                if (slot == 1 && !stack.is(HexereiTags.Items.SMALL_SATCHELS) && !stack.is(HexereiTags.Items.MEDIUM_SATCHELS) && !stack.is(HexereiTags.Items.LARGE_SATCHELS))
//                    return false;
//                if (slot == 2 && !stack.is(HexereiTags.Items.BROOM_BRUSH))
//                    return false;
//                return true;
//            }

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
        this.peck = true;
        this.peckTiltAngle = 80;
        this.peckTimer = 10;

        this.caw = false;
        if (!level.isClientSide)
            this.cawTimer = new Random().nextInt(360) + 360;
        this.cawTiltAngle = 0;
        this.cawTiltAngleActual = 0;
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

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

//    private void setVariantAndMarkings(CrowVariant pVariant, Markings pMarking) {
//        this.setTypeVariant(pVariant.getId() & 255 | pMarking.getId() << 8 & '\uff00');
//    }

    public CrowVariant getVariant() {
        return CrowVariant.byId(this.getTypeVariant() & 255);
    }


    @Override
    public void setRecordPlayingNearby(BlockPos p_21082_, boolean p_21083_) {
        this.jukebox = p_21082_;
        this.dance = p_21083_;
    }

    protected AABB getTargetableArea(double targetDistance) {
        Vec3 renderCenter = new Vec3(CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ());
        AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
        return aabb.move(renderCenter);
    }

    private void removeFramedMap(ItemStack pStack) {
        this.getFramedMapId().ifPresent((p_218864_) -> {
            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(p_218864_, this.level);
            if (mapitemsaveddata != null) {
                mapitemsaveddata.removedFromFrame(this.blockPosition(), this.getId());
                mapitemsaveddata.setDirty(true);
            }

        });
        pStack.setEntityRepresentation((Entity)null);
    }


    public boolean checkTotemDeathProtection(DamageSource pDamageSource) {
        if (pDamageSource.isBypassInvul()) {
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

            ItemStack itemstack1 = this.itemHandler.getStackInSlot(2);
            if (!triggered && itemstack1.is(ModItems.CROW_ANKH_AMULET.get())) {
                itemstack = itemstack1.copy();
                itemstack1.shrink(1);
                triggered = true;
            }

            if (triggered) {

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                this.level.broadcastEntityEvent(this, (byte)35);
                this.sync();
            }

            return triggered;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.sync && this.level instanceof ServerLevel) {
            sync();
            this.sync = true;
        }
        if(!this.sync && this.level instanceof ClientLevel) {

            if (level.isClientSide)
                HexereiPacketHandler.sendToServer(new CrowAskForSyncPacket(this));

            this.sync = true;
        }
        if(!level.isClientSide){
            if(this.itemHandler.getStackInSlot(2).is(ModItems.CROW_BLANK_AMULET.get())){
                this.itemHandler.getStackInSlot(2).inventoryTick(level, this, 2, true);
            } if(this.itemHandler.getStackInSlot(2).getItem() instanceof MapItem mapItem){
                MapItemSavedData mapitemsaveddata = MapItem.getSavedData(this.itemHandler.getStackInSlot(2), level);
                if (mapitemsaveddata != null && !mapitemsaveddata.locked) {
                    mapItem.update(level, this, mapitemsaveddata);
                }
            }
        }

        if(this.playingDead > 0){
            if(!isPlayingDead())
                setPlayingDead(true);
            this.playingDead--;
        } else {
            if(isPlayingDead())
                setPlayingDead(false);
        }
        if(CrowEntity.this.isPlayingDead())
            return;

        if(this.breedNuggetGivenByCrowTimer > 0 && !this.level.isClientSide)
        {
            this.breedNuggetGivenByCrowTimer--;
            if(breedNuggetGivenByCrowTimer == 10){
                this.peck();
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(this));
            }
            if(this.breedNuggetGivenByCrowTimer == 0)
                if(level.getPlayerByUUID(this.breedNuggetGivenByPlayerUUID) != null)
                    this.setInLove(level.getPlayerByUUID(this.breedNuggetGivenByPlayerUUID));
        }
        if (this.pickpocketTimer > 0) {
            this.pickpocketTimer--;
        }
        if(this.searchForNewCropTargetTimer > 0)
        {
            this.searchForNewCropTargetTimer--;
            if(this.searchForNewCropTargetTimer <= 0)
                this.searchForNewCropTarget = false;
        }

        this.animationCounter++;
        this.rideCooldownCounter++;
        if (this.cofferHerbJarListResetTimer-- <= 0)
        {
            this.cofferHerbJarList = HexereiUtil.getAllToggledCofferAndHerbJarPositionsNearby( 24, level, this);
            this.cofferHerbJarListResetTimer = 100;
            this.villagerList = this.level.getEntitiesOfClass(Villager.class, this.getTargetableArea(16), this.targetEntitySelector);
        }

        if(this.pickpocketTimer < 5 && this.pickpocketTimer > 0)
        {
            this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
            this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        }

        // Wing animation
        if (this.getDeltaMovement().y < -0.0075) {
            rightWingAngle = Mth.sin(Hexerei.getClientTicksWithoutPartial() / 5f) * 0.05f;
            leftWingAngle = -Mth.sin(0.97f + Hexerei.getClientTicksWithoutPartial() / 5f) * 0.05f;
        } else {
            rightWingAngle = (float) Math.sin(Hexerei.getClientTicksWithoutPartial() / 5f) * 0.8f;
            leftWingAngle = -(float) Math.sin(Hexerei.getClientTicksWithoutPartial() / 5f) * 0.8f;
        }
        this.rightWingAngleActual = moveTo(this.rightWingAngleActual, rightWingAngle, 0.1f);
        this.leftWingAngleActual = moveTo(this.leftWingAngleActual, leftWingAngle, 0.1f);

        // Head tilt animation
        if (this.headTiltTimer-- <= 0)
        {
            this.headTilt = !this.headTilt;
            if(this.headTilt)
            {
                this.headTiltTimer = random.nextInt(80) + 20;
                this.headZTiltAngle = random.nextInt(180) - 90;
                this.headXTiltAngle = random.nextInt(180) - 90;
            }
            else
            {
                this.headTiltTimer = random.nextInt(80) + 20;
                this.headZTiltAngle = 0;
                this.headXTiltAngle = 0;
            }

        }
        if (level.isClientSide) {
            this.headZTiltAngleActual = moveTo(this.headZTiltAngleActual, headZTiltAngle, 15f);
            this.headXTiltAngleActual = moveTo(this.headXTiltAngleActual, headXTiltAngle, 15f);
        }

        // Peck animation
        if(peckTimer > 0)
            this.peckTimer--;
        if (this.peckTimer <= 0 && this.peck)
        {
            this.peck = false;
            this.peckTiltAngle = 0;

        }
        this.peckTiltAngleActual = moveTo(this.peckTiltAngleActual, peckTiltAngle, 15f);


        // Caw animation
        if (!level.isClientSide)
            if (!this.itemHandler.getStackInSlot(1).isEmpty())
                this.cawTimer = 360;

        if(this.cawTimer > 0) {
            this.cawTimer--;
            if(this.cawTimer <= 0) {
                this.caw = !this.caw;
                if (this.caw) {


                    if (!level.isClientSide) {
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowCawPacket(this));
                        this.playSound(ModSounds.CROW_CAW.get(), this.getSoundVolume(), this.getVoicePitch());
                        this.cawTimer = 15;
                    }

                } else {
                    if (!level.isClientSide)
                        this.cawTimer = random.nextInt(360) + 360;
                    else
                        this.cawTiltAngle = 0;
                }
            }
        }
        if (level.isClientSide)
            this.cawTiltAngleActual = moveTo(this.cawTiltAngleActual, cawTiltAngle, 30f);


        // Wag animation
        if(this.tailWagTimer > 0) {
            this.tailWagTimer--;
            if(this.tailWagTimer <= 0) {
                this.tailWag = !this.tailWag;
                if (this.tailWag) {


                    if (!level.isClientSide) {
                        if(!this.tailFan)
                            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowTailWagPacket(this));
                        else
                            this.tailWag = false;
                        this.tailWagTimer = 15;
                    }

                } else {
                    if (!level.isClientSide)
                        this.tailWagTimer = random.nextInt(80) + 80;
                    else
                        this.tailWagTiltAngle = 0;
                }
            }
        }
        if(this.level.isClientSide)
        {
            if(this.tailWag)
            {
                this.tailWagTiltAngle = Mth.sin(Hexerei.getClientTicks()) * 100f;
            }

            this.tailWagTiltAngleActual = moveTo(this.tailWagTiltAngleActual, tailWagTiltAngle, 30f);
        }

        // Fan animation
        if(this.tailFanTimer > 0) {
            this.tailFanTimer--;
            if(this.tailFanTimer <= 0) {
                this.tailFan = !this.tailFan;
                if (this.tailFan) {


                    if (!level.isClientSide) {

                        if(!this.tailWag)
                            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowTailFanPacket(this));
                        else
                            this.tailFan = false;
                        this.tailFanTimer = 15;
                    }

                } else {
                    if (!level.isClientSide)
                        this.tailFanTimer = random.nextInt(80) + 20;
                    else
                        this.tailFanTiltAngle = 0;
                }
            }
        }
        if(this.level.isClientSide)
        {
//            if(this.tailFan)
//            {
//                this.tailFanTiltAngle = Mth.sin(Hexerei.getClientTicks()) * 100f;
//            }

            this.tailFanTiltAngleActual = moveTo(this.tailFanTiltAngleActual, tailFanTiltAngle, 20f);
        }



        if (!this.itemHandler.getStackInSlot(1).isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 60 && isCrowEdible(this.itemHandler.getStackInSlot(1)) && (!this.isTame() || this.getHealth() < this.getMaxHealth())) {
                heldItemTime = 0;
                this.heal(4);

                if(!level.isClientSide)
                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowEatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
//                this.level.addParticle(ParticleTypes.ITEM, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);

                this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (this.itemHandler.getStackInSlot(1).getItem() == ModItems.SEED_MIXTURE.get() && seedThrowerID != null && !this.isTame()) {
                    if (getRandom().nextFloat() < 0.3F) {
                        this.setTame(true);
                        this.setCommand(1);
                        this.setOwnerUUID(this.seedThrowerID);
                        Player player = level.getPlayerByUUID(seedThrowerID);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                        }
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }
                if (this.itemHandler.getStackInSlot(1).hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.itemHandler.getStackInSlot(1).getCraftingRemainingItem());
                }
                this.itemHandler.getStackInSlot(1).shrink(1);
            }
        } else {
            heldItemTime = 0;
        }

    }

    public void eatParticles(ItemStack stack)
    {
        float scale = 3f;
        if(this.isBaby())
            scale = 4;
        Vec3 vec3 = this.calculateViewVector(0, this.yBodyRot);
        for(int i = 0; i < 6; i++)
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), this.getRandomX(0.125D) + vec3.x / scale, this.random.nextDouble()/4f - 0.125f + this.getEyeY(), this.getRandomZ(0.125D) + vec3.z / scale, (level.random.nextDouble() - 0.5d) / 15d, (level.random.nextDouble() + 0.5d) * 0.15d, (level.random.nextDouble() - 0.5d) / 15d);

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


    public int getHelpCommand() {
        return this.entityData.get(HELP_COMMAND);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public boolean getCommandFollow() {
        return this.entityData.get(COMMAND) == 0;
    }

    public boolean getCommandSit() {
        return this.entityData.get(COMMAND) == 1;
    }

    public boolean getCommandWander() {
        return this.entityData.get(COMMAND) == 2;
    }

    public boolean getCommandHelp() {
        return this.entityData.get(COMMAND) == 3;
    }


    // 0 - follow player/shoulder sit,
    // 1 - sit,
    // 2 - wander,
    // 3 - collect items
    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowSyncCommandToServer(this, command));
        else
        {
            this.setOrderedToSit(command == 1);

//            if(command == 1 || command == 2)
//            {
//                if(this.pickpocketTimer > 0)
//                {
//                    this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
//                    this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
//                }
//            }
        }
    }

    public void setHelpCommand(int command) {
        this.entityData.set(HELP_COMMAND, command);

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowSyncHelpCommandToServer(this, command));
    }

    public void setCommandFollow() {
        this.entityData.set(COMMAND, 0);
        this.setOrderedToSit(false);
    }

    public void setCommandSit() {
        this.entityData.set(COMMAND, 1);
        this.setOrderedToSit(true);
//        if(this.pickpocketTimer > 0)
//        {
//            this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
//            this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
//        }
    }

    public void setCommandWander() {
        this.entityData.set(COMMAND, 2);
        this.setOrderedToSit(false);
//        if(this.pickpocketTimer > 0)
//        {
//            this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
//            this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
//        }
    }

    public void setCommandHelp() {
        this.entityData.set(COMMAND, 3);
        this.setOrderedToSit(false);
    }

    public void setPerchPos(BlockPos pos) {
        this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public BlockPos getPerchPos() {
        return this.entityData.get(PERCH_POS).orElse(null);
    }


    public DyeColor getDyeColor() {


        DyeColor color = HexereiUtil.getDyeColorNamed(this.getName().getString(), 0);

        return color == null ? DyeColor.byId(this.entityData.get(CROW_DYE_COLOR)) : color;
//
//        if(this.getName().getString().equals("jeb_"))
//            return DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//
//        if(this.getName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            });
//
//        if(this.getName().getString().equals("bi_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            });
//
//        if(this.getName().getString().equals("trans_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            });
//
//        if(this.getName().getString().equals("joe_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            });
//
////        if(this.getName().getString().equals("les_"))
////            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/10) % 15)) {
////                case 1 -> 0;
////                case 2 -> 0;
////                case 3 -> 0;
////                case 4 -> 1;
////                case 5 -> 1;
////                case 6 -> 1;
////                case 7 -> 2;
////                case 8 -> 2;
////                case 9 -> 2;
////                case 10 -> 6;
////                case 11 -> 6;
////                case 12 -> 6;
////                case 13 -> 14;
////                case 14 -> 14;
////                case 15 -> 14;
////                default -> 0;
////            });
//
//
//                    //DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//        return DyeColor.byId(this.entityData.get(CROW_DYE_COLOR));
    }

    public int getDyeColorId() {
        return this.entityData.get(CROW_DYE_COLOR);
    }

    public void setDyeColor(int color) {
        this.entityData.set(CROW_DYE_COLOR, color);
    }

    public void setDyeColor(DyeColor color) {
        this.entityData.set(CROW_DYE_COLOR, color.getId());
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
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
        switch(slot.getType()) {
            case HAND:
                return this.itemHandler.getStackInSlot(1);
            case ARMOR:
                return this.itemHandler.getStackInSlot(0);
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommand(compound.getInt("Command"));
        this.setHelpCommand(compound.getInt("HelpCommand"));
        this.pickpocketTimer = compound.getInt("PickpocketTimer");
        this.setTypeVariant(compound.getInt("Variant"));
        this.playingDead = (compound.getInt("PlayingDeadTimer"));
//        this.itemHandler.setStackInSlot(0, ItemStack.of(compound.getCompound("HandItem")));
//        this.itemHandler.setStackInSlot(1, ItemStack.of(compound.getCompound("HeadItem")));
//        this.itemHandler.setStackInSlot(2, ItemStack.of(compound.getCompound("MiscItem")));
        itemHandler.deserializeNBT(compound.getCompound("inv"));

        removeFramedMap(itemHandler.getStackInSlot(2));
//        this.inventory.fromTag(compound.getList("inv", 0));
//        this.setItemSlot(EquipmentSlot.HEAD, itemHandler.getStackInSlot(0));
//        this.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.getStackInSlot(1));
        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
        if(compound.contains("DyeColor"))
            this.setDyeColor(compound.getInt("DyeColor"));
    }

    public void readAdditionalSaveDataNoSuper(CompoundTag compound) {
        this.setCommand(compound.getInt("Command"));
        this.setHelpCommand(compound.getInt("HelpCommand"));
        this.pickpocketTimer = compound.getInt("PickpocketTimer");
        this.setTypeVariant(compound.getInt("Variant"));
        this.playingDead = compound.getInt("PlayingDeadTimer");
//        this.itemHandler.setStackInSlot(0, ItemStack.of(compound.getCompound("HandItem")));
//        this.itemHandler.setStackInSlot(1, ItemStack.of(compound.getCompound("HeadItem")));
//        this.itemHandler.setStackInSlot(2, ItemStack.of(compound.getCompound("MiscItem")));
        itemHandler.deserializeNBT(compound.getCompound("inv"));
//        this.inventory.fromTag(compound.getList("inv", 0));
//        this.setItemSlot(EquipmentSlot.HEAD, itemHandler.getStackInSlot(0));
//        this.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.getStackInSlot(1));
        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
        if(compound.contains("DyeColor"))
            this.setDyeColor(compound.getInt("DyeColor"));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Command", this.getCommand());
        compound.putInt("HelpCommand", this.getHelpCommand());
        compound.putInt("PickpocketTimer", pickpocketTimer);
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("PlayingDeadTimer", this.playingDead);
//        compound.put("HandItem", this.itemHandler.getStackInSlot(0).save(new CompoundTag()));
//        compound.put("HeadItem", this.itemHandler.getStackInSlot(1).save(new CompoundTag()));
//        compound.put("MiscItem", this.itemHandler.getStackInSlot(2).save(new CompoundTag()));
        compound.put("inv", itemHandler.serializeNBT());
//        compound.put("inv", this.inventory.createTag());

        if (this.getPerchPos() != null) {
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
        compound.putInt("DyeColor", this.getDyeColorId());
    }

    public void addAdditionalSaveDataNoSuper(CompoundTag compound) {
        compound.putInt("Command", this.getCommand());
        compound.putInt("HelpCommand", this.getHelpCommand());
        compound.putInt("PickpocketTimer", pickpocketTimer);
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("PlayingDeadTimer", this.playingDead);
//        compound.put("HandItem", this.itemHandler.getStackInSlot(0).save(new CompoundTag()));
//        compound.put("HeadItem", this.itemHandler.getStackInSlot(1).save(new CompoundTag()));
//        compound.put("MiscItem", this.itemHandler.getStackInSlot(2).save(new CompoundTag()));
        compound.put("inv", itemHandler.serializeNBT());
//        compound.put("inv", this.inventory.createTag());

        if (this.getPerchPos() != null) {
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
        compound.putInt("DyeColor", this.getDyeColorId());
    }


    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        CrowVariant variant;
        if (pSpawnData instanceof CrowEntity.CrowGroupData) {
            variant = ((CrowEntity.CrowGroupData)pSpawnData).variant;
        } else {
            boolean isVariant = randomsource.nextInt(5) == 0;
            variant = Util.getRandom(CrowVariant.values(), randomsource);
            if(!isVariant)
                variant = CrowVariant.BLACK;
            pSpawnData = new CrowEntity.CrowGroupData(variant);
        }

        this.setTypeVariant(variant.getId() & 255);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
    public static class CrowGroupData extends AgeableMob.AgeableMobGroupData {
        public final CrowVariant variant;

        public CrowGroupData(CrowVariant pVariant) {
            super(true);
            this.variant = pVariant;
        }
    }

    public void aiStep() {
        super.aiStep();
        Vec3 motion = this.getDeltaMovement();
        if (!this.onGround && motion.y < 0.0D) {
            this.setDeltaMovement(motion.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    public void travel(Vec3 vec3d) {
        if(this.getPerchPos() == null) {
            if (this.isOrderedToSit() || this.isInSittingPose()) {
                if (this.getNavigation().getPath() != null) {
                    this.getNavigation().stop();
                }
                vec3d = Vec3.ZERO;
            }
        }
        else {
            double topOffset = this.level.getBlockState(this.getPerchPos()).getBlock().getOcclusionShape(this.level.getBlockState(this.getPerchPos()), this.level, this.getPerchPos()).max(Direction.Axis.Y);
            if(this.distanceTo(this.getPerchPos().getX(), this.getPerchPos().getZ()) < 1 && this.position().y() >= this.getPerchPos().getY() + topOffset && this.position().y() < this.getPerchPos().above().getY() + topOffset - 0.75f) {
                if (this.isOrderedToSit() || this.isInSittingPose())
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
        double d0 = CrowEntity.this.getX() - p_20276_ - 0.5d;
        double d1 = CrowEntity.this.getZ() - p_20278_ - 0.5d;
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
        return stack.getItem() == ModItems.SEED_MIXTURE.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public OptionalInt getFramedMapId() {
        ItemStack itemstack = this.getItem(2);
        if (itemstack.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId(itemstack);
            if (integer != null) {
                return OptionalInt.of(integer);
            }
        }

        return OptionalInt.empty();
    }
    public boolean canBeSeenAsEnemy() {
        return !this.isPlayingDead() && super.canBeSeenAsEnemy();
    }

    public void setPlayingDead(boolean pPlayingDead) {
        if(this.getItem(2).is(ModItems.CROW_ANKH_AMULET.get())) {
            this.getItem(2).getOrCreateTag().putBoolean("Active", pPlayingDead);
            if(pPlayingDead)
                addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
        }
        if(pPlayingDead != isPlayingDead()) {
//            this.setDeltaMovement(0, 0, 0);
//            this.moveControl.setWantedPosition(xo, yo, zo, 1);
            this.setNoGravity(false);
            this.setSpeed(0);
            this.navigation.stop();
            this.caw = false;
            this.cawTimer = random.nextInt(360) + 360;
            this.tailFan = false;
            this.tailFanTimer = random.nextInt(80) + 20;
            this.tailWag = false;
            this.tailWagTimer = random.nextInt(80) + 80;
            this.sync();
        }
        this.entityData.set(DATA_PLAYING_DEAD, pPlayingDead);
    }

    public boolean isPlayingDead() {
        return this.entityData.get(DATA_PLAYING_DEAD);
    }

    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
//        return ModEntityTypes.CROW.get().create(world);
//        return null;


        CrowEntity crow = ModEntityTypes.CROW.get().create(world);
        if (crow != null) {
            CrowVariant crowVariant;
            crowVariant = this.random.nextBoolean() ? this.getVariant() : ((CrowEntity)entity).getVariant();

            crow.setTypeVariant(crowVariant.getId() & 255);
            crow.setPersistenceRequired();
        }

        return crow;
    }

//    @Override
//    public boolean setEntityOnShoulder(ServerPlayer player) {
//        if (player.getShoulderEntityLeft().contains("UUID")
//                && player.getShoulderEntityLeft().getUUID("UUID").equals(getUUID()))
//            return false;
//        if (player.getShoulderEntityRight().contains("UUID")
//                && player.getShoulderEntityRight().getUUID("UUID").equals(getUUID()))
//            return false;
//        CompoundTag compoundnbt = new CompoundTag();
//        compoundnbt.putString("id", this.getEncodeId());
//        this.saveWithoutId(compoundnbt);
//        if (player.setEntityOnShoulder(compoundnbt)) {
//            this.remove(RemovalReason.DISCARDED);
//            return true;
//        } else {
//            return false;
//        }
//    }



    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(!player.isSecondaryUseActive()) {
            if (this.isTame() && this.isOwnedBy(player)) {
                if (player.getItemInHand(hand).is(ModItems.CROW_FLUTE.get())) {
                    if (player.getItemInHand(hand).getOrCreateTag().contains("commandMode")) {
                        if (player.getItemInHand(hand).getOrCreateTag().getInt("commandMode") == 1) {
                            CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
                            ListTag nbtTagList = tag.getList("crowList", Tag.TAG_COMPOUND);
                            CompoundTag itemTag = new CompoundTag();
                            boolean flag = false;
                            for (int i = 0; i < nbtTagList.size(); i++) {
                                CompoundTag compoundTag = nbtTagList.getCompound(i);

                                UUID thisuuid = this.getUUID();

                                UUID tempuuid = null;
                                if(compoundTag.contains("UUID"))
                                    tempuuid = compoundTag.getUUID("UUID");

                                if (tempuuid != null && thisuuid.toString().equals(tempuuid.toString())) {
                                    flag = true;
                                    player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_deselect_message", this.getName()), true);
                                    this.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1f, 0.75f);
                                    nbtTagList.remove(i);
                                    break;
                                }
                            }

                            if (!flag) {
                                if(nbtTagList.size() < 9){
                                    itemTag.putUUID("UUID", this.getUUID());
                                    itemTag.putInt("ID", this.getId());
                                    nbtTagList.add(itemTag);
                                    player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_selected_message", this.getName()), true);
                                    this.playSound(ModSounds.CROW_FLUTE_SELECT.get(), 1f, 0.75f);
                                    tag.put("crowList", nbtTagList);
                                }
                                else
                                {
                                    player.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                                    player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 10);
                                }
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        if (player.isSecondaryUseActive() && isOwnedBy(player)) {
            if (!level.isClientSide()) {
                MenuProvider containerProvider = createContainerProvider(level, blockPosition());

                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, b -> b.writeInt(this.getId()));

            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }else if(this.itemHandler.getStackInSlot(1).isEmpty()){
            if (!this.isTame() && itemstack.getItem() == ModItems.SEED_MIXTURE.get()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (!this.level.isClientSide) {
                    if (this.random.nextInt(10) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                        this.tame(player);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }else if (this.isTame() && !this.isBaby() && itemstack.getItem() == Items.GOLD_NUGGET && !this.isInLove()) {
                this.breedNuggetGivenByPlayer = true;
                this.breedNuggetGivenByPlayerUUID = player.getUUID();
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (!this.level.isClientSide) {
                    this.setInLove(player);
                    this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
                    this.itemHandler.setStackInSlot(1, new ItemStack(Items.GOLD_NUGGET));
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else if (this.isTame() && this.isOwnedBy(player)) {
                if (!this.level.isClientSide) {
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
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() == 4) {
                        this.setCommand(0);
                    }
                    if(this.getCommand() == 3) {
                        if(this.getHelpCommand() == 0)
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_0", this.getName()), true);
                        if(this.getHelpCommand() == 1)
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_1", this.getName()), true);
                        if(this.getHelpCommand() == 2)
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_2", this.getName()), true);
                    }
                    else
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_" + this.getCommand(), this.getName()), true);
                    this.setOrderedToSit(this.getCommand() == 1);



                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
                return super.mobInteract(player, hand);
            }
        }
        else
        {
            if(!(itemstack.getItem() == Items.GOLD_NUGGET && (breedNuggetGivenByPlayer || breedNuggetGivenByCrowTimer > 0))) {
                this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
                this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }

    @Override
    protected void dropAllDeathLoot(DamageSource p_21192_) {
        ItemStack hat = this.itemHandler.getStackInSlot(0);
        ItemStack itemstack = this.itemHandler.getStackInSlot(1);
        if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if (!hat.isEmpty()) {
            this.spawnAtLocation(hat);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.dropAllDeathLoot(p_21192_);
    }

    private boolean isCrowEdible(ItemStack stack) {
        return stack.getItem().isEdible() || TEMPTATION_ITEMS.test(stack);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        if(this.isTame() && this.getCommandHelp() && this.cofferHerbJarList != null && !this.cofferHerbJarList.isEmpty())
        {
            int k = 0;
            boolean flag = false;
            if(getPerchPos() != null){
                BlockEntity perchEntity = level.getBlockEntity(getPerchPos());
                if (perchEntity instanceof HerbJarTile herbJarTile && herbJarTile.buttonToggled != 0)
                    if (HexConfig.JARS_ONLY_HOLD_HERBS.get()) {
                        if (stack.is(HexereiTags.Items.HERB_ITEM)) {
                            if(herbJarTile.itemHandler.getStackInSlot(0).isEmpty() || (herbJarTile.itemHandler.getStackInSlot(0).getTag() == stack.getTag() && herbJarTile.itemHandler.getStackInSlot(0).sameItem(stack)))
                                return true;
                        }
                    } else {
                        if(herbJarTile.itemHandler.getStackInSlot(0).isEmpty() || herbJarTile.itemHandler.getStackInSlot(0).getTag() == stack.getTag() && herbJarTile.itemHandler.getStackInSlot(0).sameItem(stack))
                            return true;
                    }
                if (perchEntity instanceof CofferTile cofferTile && cofferTile.buttonToggled != 0) {
                    if(cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(stack) || cofferTile.hasItem(Items.AIR))
                        return true;
                }
            }
            for (BlockPos blockPos : this.cofferHerbJarList) {
                BlockEntity blockEntity = level.getBlockEntity(this.cofferHerbJarList.get(k++));
                if (blockEntity instanceof CofferTile cofferTile) {
                    for (int i = 0; i < cofferTile.itemStackHandler.getSlots(); i++) {
                        if ((cofferTile).itemStackHandler.getStackInSlot(i).is(stack.getItem())) {
                            flag = true;
                            break;
                        }
                    }
                }


                if (blockEntity instanceof HerbJarTile herbJarTile) {
                    if((herbJarTile).itemHandler.getStackInSlot(0).is(stack.getItem())){
                        flag = true;
                        break;
                    }
                }
            }

            return flag;
        }

        if(this.isTame() && this.getHealth() < this.getMaxHealth())
        {

            return isCrowEdible(stack);
        }

        return (!this.isTame() && isCrowEdible(stack));
    }



    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.itemHandler.getStackInSlot(1).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.itemHandler.getStackInSlot(1), 0.0F);
        }
        this.itemHandler.setStackInSlot(1, duplicate);
        if (e.getItem().getItem() == ModItems.SEED_MIXTURE.get() && !this.isTame()) {
            seedThrowerID = e.getThrower();
        } else {
            seedThrowerID = null;
        }
    }

    @Override
    public void onFindTarget(ItemEntity e) {
        ITargetsDroppedItems.super.onFindTarget(e);
    }

    @Override
    public double getMaxDistToItem() {
        return 1.0D;
    }

    @Override
    public boolean isFlying() {
        return !onGround;
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
    public int getContainerSize() {
        return 3;
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
        return new CrowContainer(id, this, inv, player);
//        return null;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();
        return super.getCapability(capability, facing);
    }


    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new CrowContainer(i, CrowEntity.this, playerInventory, playerEntity);
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
        return this.itemHandler.getStackInSlot(1).is(ModItems.WARHAMMER.get()) && this.getDisplayName().getString().equals("Thor");
    }

    @Override
    public void containerChanged(Container p_18983_) {

        ItemStack stack = p_18983_.getItem(2);
        stack.setEntityRepresentation(this);
    }

    public class FloatGoal extends Goal {
        private final Mob mob;

        public FloatGoal(Mob p_25230_) {
            this.mob = p_25230_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP));
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
                Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
                BlockPos pos;
                if(randomPos == null)
                    randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
                if(randomPos != null)
                    pos = new BlockPos(randomPos.x, randomPos.y, randomPos.z);
                else if(CrowEntity.this.getPerchPos() != null)
                    pos = CrowEntity.this.getPerchPos().above().above();
                else
                    pos = CrowEntity.this.blockPosition().above().above();
                if(!CrowEntity.this.isInSittingPose() && !CrowEntity.this.getCommandSit())
                    this.mob.push(0,0.1,0);
                CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), 1.5f);
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
        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;

            if(CrowEntity.this.isInSittingPose() || CrowEntity.this.isOrderedToSit())
                return false;
            if(CrowEntity.this.getCommand() == 3 && !(CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0))
                return false;

            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if(CrowEntity.this.isInSittingPose() || CrowEntity.this.isOrderedToSit())
                return false;
            if(CrowEntity.this.getCommand() == 3 && !(CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0))
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
        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;
            if(CrowEntity.this.isInSittingPose() || CrowEntity.this.isOrderedToSit())
                return false;

            if(CrowEntity.this.getCommand() == 3 && !(CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0))
                return false;

            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if(CrowEntity.this.isInSittingPose() || CrowEntity.this.isOrderedToSit())
                return false;
            if(CrowEntity.this.getCommand() == 3 && !(CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0))
                return false;

            return super.canContinueToUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vec3 = this.mob.getViewVector(0.0F);
            int i = 8;
            Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
        }
    }


    public class CrowGatherItems<T extends ItemEntity> extends TargetGoal {
        protected final CrowGatherItems.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super ItemEntity> targetEntitySelector;
        protected int executionChance;
        protected boolean mustUpdate;
        protected ItemEntity targetEntity;
        protected ITargetsDroppedItems hunter;
        private int tickThreshold;
        private float radius = 9F;
        private int walkCooldown = 0;
        protected int tryTicks = 0;

        public CrowGatherItems(PathfinderMob creature, boolean checkSight) {
            this(creature, checkSight, false);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public CrowGatherItems(PathfinderMob creature, boolean checkSight, int tickThreshold) {
            this(creature, checkSight, false, tickThreshold, 9);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }


        public CrowGatherItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby) {
            this(creature, 1, checkSight, onlyNearby, null, 0);
        }

        public CrowGatherItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
            this(creature, 1, checkSight, onlyNearby, null, tickThreshold);
            this.radius = radius;
        }


        public CrowGatherItems(PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, int ticksExisted) {
            super(creature, checkSight, onlyNearby);
            this.executionChance = chance;
            this.tickThreshold = ticksExisted;
            this.hunter = (ITargetsDroppedItems) creature;
            this.theNearestAttackableTargetSorter = new CrowGatherItems.Sorter(creature);
            this.targetEntitySelector = new Predicate<ItemEntity>() {
                @Override
                public boolean apply(@Nullable ItemEntity item) {
                    ItemStack stack = item.getItem();
                    return !stack.isEmpty()  && hunter.canTargetItem(stack) && item.tickCount > tickThreshold;
                }
            };
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;
            if(CrowEntity.this.getHealth() >= CrowEntity.this.getMaxHealth() && (CrowEntity.this.isTame() && (((CrowEntity) mob).getCommand() != 3 || ((CrowEntity) mob).getHelpCommand() != 0)))
                return false;
            if (this.mob.isPassenger() || mob.isVehicle() && mob.getControllingPassenger() != null) {
                return false;
            }
            if(!CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()){
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = this.mob.level.getGameTime() % 10;
                if (this.mob.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }

            List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {

                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                if(targetEntity == null)
                    return false;
                this.hunter.onFindTarget(targetEntity);
                return !((CrowEntity) mob).isInSittingPose() && (mob.getTarget() == null || !mob.getTarget().isAlive());
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
            AABB aabb = new AABB(-radius, -radius, -radius, radius, radius, radius);
            return aabb.move(renderCenter);
        }

        @Override
        public void start() {
            CrowEntity.this.doingTask = true;
            CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            moveTo();
            super.start();
        }

        protected void moveTo(){
            if(walkCooldown > 0){
                walkCooldown--;
            }else{
                this.mob.getNavigation().moveTo(CrowEntity.this.getNavigation().createPath(this.targetEntity.getX() + 0.5f, this.targetEntity.getY() + 0.25f, this.targetEntity.getZ() + 0.5f, 0), 1);
                walkCooldown = 30 + this.mob.getRandom().nextInt(40);
            }
        }

        public void stop() {
            
            CrowEntity.this.doingTask = false;
            super.stop();
            this.mob.getNavigation().stop();
            this.targetEntity = null;
            ((CrowEntity) mob).aiItemFlag = false;
        }

        @Override
        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            super.tick();

            boolean isStuck = false;
            if(CrowEntity.this.stuckTimer++ > 80)
            {
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25D) {
                    isStuck = true;
                }
                CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }
            if(isStuck)
            {
                Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
                BlockPos pos;
                if(randomPos == null)
                    randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
                if(randomPos != null)
                    pos = new BlockPos(randomPos.x, randomPos.y, randomPos.z);
                else if(CrowEntity.this.getPerchPos() != null)
                    pos = CrowEntity.this.getPerchPos().above().above();
                else
                    pos = CrowEntity.this.blockPosition().above().above();


                CrowEntity.this.navigation.stop();
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1D && CrowEntity.this.isOnGround()) {
//                    CrowEntity.this.push(0,2,0);
                    CrowEntity.this.push(Math.min(0.2f,(pos.getX() - CrowEntity.this.position().x) / 20.0f), 0.15f, Math.min(0.2f,(pos.getZ() - CrowEntity.this.position().z) / 20.0f));
                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), 1.5f);

                }
                else {

                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0), 1.5f);
                }

                CrowEntity.this.stuckTimer = 0;
            }

            if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
                this.stop();
                this.mob.getNavigation().stop();
            }else{
                moveTo();
            }
            if(targetEntity != null && this.mob.hasLineOfSight(targetEntity) && this.mob.getBbWidth() > ((CrowEntity) hunter).getMaxDistToItem() && this.mob.isOnGround()){
                this.mob.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY() + 0.5f, targetEntity.getZ(), 1.5f);
            }
            if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < this.hunter.getMaxDistToItem() && CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
                if(!((CrowEntity) hunter).peck){
                    hunter.peck();
                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> ((CrowEntity) hunter).level.getChunkAt(((CrowEntity) hunter).blockPosition())), new CrowPeckPacket(this.mob));
                }


                if(((CrowEntity) hunter).peckTiltAngleActual > 40){
                    hunter.onGetItem(targetEntity);
                    CrowEntity.this.depositItemBeforePerch = true;
                    this.targetEntity.getItem().shrink(1);
                    stop();
                }

            }

            CrowEntity crow = (CrowEntity) mob;
            if (this.targetEntity != null) {
                crow.aiItemFlag = true;
                if (this.mob.distanceTo(targetEntity) <= getMaxDistToItem()) {
                    crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), targetEntity.getY() + 0.5f, this.targetEntity.getZ(), 1.5f);
//                    crow.peck();
//                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(this.mob));
                }
                if(!crow.isInSittingPose())
                    this.mob.getNavigation().moveTo(CrowEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 0.5f, this.targetEntity.getZ(), 0), 1.5f);

                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    if(targetEntity.position().distanceTo(CrowEntity.this.position()) < 3 && CrowEntity.this.position().y < targetEntity.position().y()) {
                        CrowEntity.this.setNoGravity(false);
                        CrowEntity.this.push((this.targetEntity.position().x - CrowEntity.this.position().x) / 50.0f, (this.targetEntity.position().y - CrowEntity.this.position().y) / 50.0f + 0.1f, (this.targetEntity.position().z - CrowEntity.this.position().z) / 50.0f);
                    }
                    this.mob.getNavigation().moveTo(CrowEntity.this.getNavigation().createPath(this.targetEntity.getX(), targetEntity.getY() + 3f, this.targetEntity.getZ(), 0), 1.5f);
                }



            }

        }

        public void makeUpdate() {
            this.mustUpdate = true;
        }

        @Override
        public boolean canContinueToUse() {
            if(CrowEntity.this.getHealth() >= CrowEntity.this.getMaxHealth() && (CrowEntity.this.isTame() && (((CrowEntity) mob).getCommand() != 3 || ((CrowEntity) mob).getHelpCommand() != 0)))
                return false;
            boolean path = this.mob.getBbWidth() > 2D ||  !this.mob.getNavigation().isDone();

            if(CrowEntity.this.isInSittingPose()) {
                CrowEntity.this.setInSittingPose(false);
                CrowEntity.this.setOrderedToSit(false);
            }
            return path && targetEntity != null && targetEntity.isAlive() && !((CrowEntity) mob).isInSittingPose() &&  (mob.getTarget() == null || !mob.getTarget().isAlive());
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

    public class LandOnOwnersShoulderGoal extends Goal {
        private final CrowEntity entity;
        private ServerPlayer owner;
        private boolean isSittingOnShoulder;

        public LandOnOwnersShoulderGoal(CrowEntity p_25483_) {
            this.entity = p_25483_;
        }

        @Override
        public boolean canContinueToUse() {

            if(getCommand() == 0)
            {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
            }
            return super.canContinueToUse();
        }

        public boolean canUse() {
            if(CrowEntity.this.isBaby())
                return false;
            ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
            boolean flag = serverplayer != null && !serverplayer.isSpectator() && !serverplayer.getAbilities().flying && !serverplayer.isInWater() && !serverplayer.isInPowderSnow;
            return !this.entity.isOrderedToSit() && !this.entity.isInSittingPose() && flag && this.entity.getCommand() == 0 && this.entity.canSitOnShoulder() && serverplayer.getPassengers().size() < 2;
        }

        public boolean isInterruptable() {
            return !this.isSittingOnShoulder;
        }

        public void start() {
            this.owner = (ServerPlayer)this.entity.getOwner();
            this.isSittingOnShoulder = false;
        }

        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
                if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {


                    this.isSittingOnShoulder = this.entity.startRiding(this.owner, true);

                    if(!level.isClientSide)
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowStartRidingPacket(this.entity, this.owner));
                }

            }
            if(isSittingOnShoulder)
                this.entity.rideCooldownCounter = 0;
        }
    }



    public class CrowHarvestGoal extends MoveToBlockGoal {
        private static final int WAIT_TICKS = 40;
        protected int ticksWaited;
        protected int tryTicks = 0;

        public CrowHarvestGoal(double p_28675_, int p_28676_, int p_28677_) {
            super(CrowEntity.this, p_28675_, p_28676_, p_28677_);
        }

        @Override
        protected int nextStartTick(PathfinderMob p_25618_) {
            return reducedTickDelay(20 + p_25618_.getRandom().nextInt(20));
        }

        public double acceptedDistance() {
            return 2.0D;
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 100 == 0;
        }

        protected boolean isValidTarget(LevelReader p_28680_, BlockPos pos) {
            BlockState blockstate = p_28680_.getBlockState(pos);
            if(blockstate.is(HexereiTags.Blocks.CROW_HARVESTABLE))
            {
                if(blockstate.getBlock() instanceof StemBlock)
                    return false;
                if(blockstate.hasProperty(BlockStateProperties.AGE_3))
                    return blockstate.getValue(BlockStateProperties.AGE_3) >= 3;
                else if(blockstate.hasProperty(BlockStateProperties.AGE_7))
                    return blockstate.getValue(BlockStateProperties.AGE_7) >= 7;
                return CaveVines.hasGlowBerries(blockstate);
            }
            if(blockstate.is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE))
            {
                if(blockstate.getBlock() instanceof StemGrownBlock)
                {
                    if(level.getBlockState(pos.north()).getBlock() instanceof AttachedStemBlock stemBlock)
                        if(stemBlock.fruit == level.getBlockState(pos).getBlock())
                            return true;
                    if(level.getBlockState(pos.south()).getBlock() instanceof AttachedStemBlock stemBlock)
                        if(stemBlock.fruit == level.getBlockState(pos).getBlock())
                            return true;
                    if(level.getBlockState(pos.east()).getBlock() instanceof AttachedStemBlock stemBlock)
                        if(stemBlock.fruit == level.getBlockState(pos).getBlock())
                            return true;
                    if(level.getBlockState(pos.west()).getBlock() instanceof AttachedStemBlock stemBlock)
                        if(stemBlock.fruit == level.getBlockState(pos).getBlock())
                            return true;
                    return false;
                }
                if(ForgeRegistries.BLOCKS.getKey(blockstate.getBlock()).toString().equals("immersiveengineering:hemp")){
                    return level.getBlockState(pos.below()).is(blockstate.getBlock());
                }
                else
                    return true;
            }
            return false;
        }

        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            BlockPos blockpos = this.getMoveToTarget();

            if (this.isReachedTarget()) {
                if (this.ticksWaited >= 10) {
                    this.onReachedTarget();
                } else {
                    ++this.ticksWaited;
                }
            }

            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                if(blockpos.closerThan(new Vec3i(CrowEntity.this.position().x,CrowEntity.this.position().y, CrowEntity.this.position().z), 3) && CrowEntity.this.position().y < blockpos.getY()) {
                    CrowEntity.this.setNoGravity(false);
                    CrowEntity.this.push((blockpos.getX() - CrowEntity.this.position().x) / 50.0f, (blockpos.getY() - CrowEntity.this.position().y) / 50.0f + 0.1f, (blockpos.getZ() - CrowEntity.this.position().z) / 50.0f);
                }
                this.mob.getNavigation().moveTo(CrowEntity.this.getNavigation().createPath((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY() + 1, (double)((float)blockpos.getZ()) + 0.5D, 0), this.speedModifier);

            }

            super.tick();
        }


        protected void onReachedTarget() {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(CrowEntity.this.level, CrowEntity.this)) {
                BlockState blockstate = CrowEntity.this.level.getBlockState(this.blockPos);
                if(blockstate.is(HexereiTags.Blocks.CROW_HARVESTABLE)){
                    if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                        this.pickSweetBerries(blockstate);
                        CrowEntity.this.peck();
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));
                    } else if (CaveVines.hasGlowBerries(blockstate)) {
                        this.pickGlowBerry(blockstate);
                        CrowEntity.this.peck();
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));
                    } else if (blockstate.getBlock() instanceof PickableDoubleFlower && !CrowEntity.this.level.isClientSide) {

                        ItemStack firstOutput = new ItemStack(((PickableDoubleFlower) blockstate.getBlock()).firstOutput.get(), 4);
                        ItemStack secondOutput = ItemStack.EMPTY;
                        if (((PickableDoubleFlower) blockstate.getBlock()).secondOutput != null)
                            secondOutput = new ItemStack(((PickableDoubleFlower) blockstate.getBlock()).secondOutput.get(), ((PickableDoubleFlower) blockstate.getBlock()).maxSecondOutput);
                        int j = Math.max(1, level.random.nextInt(firstOutput.getCount()));
                        int k = 0;
                        if (((PickableDoubleFlower) blockstate.getBlock()).secondOutput != null)
                            k = Math.max(1, level.random.nextInt(secondOutput.getCount()));
                        ((PickableDoubleFlower) blockstate.getBlock()).popResource(level, this.blockPos, new ItemStack(firstOutput.getItem(), Math.max(1, (int) Math.floor(j))));
                        if (level.random.nextInt(2) == 0 && ((PickableDoubleFlower) blockstate.getBlock()).secondOutput != null)
                            ((PickableDoubleFlower) blockstate.getBlock()).popResource(level, this.blockPos, new ItemStack(secondOutput.getItem(), Math.max(1, (int) Math.floor(k))));

                        if (blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                            CrowEntity.this.level.setBlock(this.blockPos, level.getBlockState(this.blockPos).setValue(BlockStateProperties.AGE_3, 0), 2);
                            CrowEntity.this.level.setBlock(this.blockPos.above(), level.getBlockState(this.blockPos.above()).setValue(BlockStateProperties.AGE_3, 0), 2);
                        }
                        if (blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                            CrowEntity.this.level.setBlock(this.blockPos.below(), level.getBlockState(this.blockPos.below()).setValue(BlockStateProperties.AGE_3, 0), 2);
                            CrowEntity.this.level.setBlock(this.blockPos, level.getBlockState(this.blockPos).setValue(BlockStateProperties.AGE_3, 0), 2);
                        }
                        CrowEntity.this.peck();
                        CrowEntity.this.playSound(SoundEvents.CAVE_VINES_PICK_BERRIES, 1.0F, 1.0F);
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));

                    } else if (blockstate.getBlock() instanceof PickableFlower && !CrowEntity.this.level.isClientSide) {

                        ItemStack firstOutput = new ItemStack(((PickableFlower) blockstate.getBlock()).firstOutput.get(), 4);
                        ItemStack secondOutput = ItemStack.EMPTY;
                        if (((PickableFlower) blockstate.getBlock()).secondOutput != null)
                            secondOutput = new ItemStack(((PickableFlower) blockstate.getBlock()).secondOutput.get(), ((PickableFlower) blockstate.getBlock()).maxSecondOutput);
                        int j = Math.max(1, level.random.nextInt(firstOutput.getCount()));
                        int k = 0;
                        if (((PickableFlower) blockstate.getBlock()).secondOutput != null)
                            k = Math.max(1, level.random.nextInt(secondOutput.getCount()));
                        ((PickableFlower) blockstate.getBlock()).popResource(level, this.blockPos, new ItemStack(firstOutput.getItem(), Math.max(1, (int) Math.floor(j))));
                        if (level.random.nextInt(2) == 0 && ((PickableFlower) blockstate.getBlock()).secondOutput != null)
                            ((PickableFlower) blockstate.getBlock()).popResource(level, this.blockPos, new ItemStack(secondOutput.getItem(), Math.max(1, (int) Math.floor(k))));

                        CrowEntity.this.level.setBlock(this.blockPos, blockstate.setValue(BlockStateProperties.AGE_3, 0), 2);

                        CrowEntity.this.peck();
                        CrowEntity.this.playSound(SoundEvents.CAVE_VINES_PICK_BERRIES, 1.0F, 1.0F);
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));

                    } else if (!CrowEntity.this.level.isClientSide) {
                        List<ItemStack> drops = blockstate.getBlock().getDrops(blockstate, (ServerLevel) CrowEntity.this.level, this.blockPos, CrowEntity.this.level.getBlockEntity(this.blockPos));
                        for (ItemStack drop : drops) {

                            if (blockstate.hasProperty(BlockStateProperties.AGE_3)) {
                                CrowEntity.this.level.addFreshEntity(new ItemEntity(CrowEntity.this.level, this.blockPos.getX() + 0.5f, this.blockPos.getY() + 0.25f, this.blockPos.getZ() + 0.5f, drop));
                                CrowEntity.this.level.setBlock(this.blockPos, blockstate.setValue(BlockStateProperties.AGE_3, 0), 2);
                            }
                            if (blockstate.hasProperty(BlockStateProperties.AGE_7)) {
                                CrowEntity.this.level.addFreshEntity(new ItemEntity(CrowEntity.this.level, this.blockPos.getX() + 0.5f, this.blockPos.getY() + 0.25f, this.blockPos.getZ() + 0.5f, drop));
                                CrowEntity.this.level.setBlock(this.blockPos, blockstate.setValue(BlockStateProperties.AGE_7, 0), 2);
                            }
                            CrowEntity.this.peck();
                            CrowEntity.this.playSound(SoundEvents.CROP_BREAK, 1.0F, 1.0F);
                            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));

                        }
                    }
                }
                else if(blockstate.is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE)){
                    LootContext.Builder builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.getRandom()).withParameter(LootContextParams.ORIGIN, position()).withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_HOE));

                    for(ItemStack stack : blockstate.getDrops(builder)){
                        Block.popResource(level, this.blockPos, stack);

                        CrowEntity.this.playSound(blockstate.getSoundType().getBreakSound(), 1.0F, 1.0F);
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));
                    }
                    ((ServerLevel) level).setBlock(this.blockPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        private void pickGlowBerry(BlockState p_148927_) {
            CaveVines.use(p_148927_, CrowEntity.this.level, this.blockPos);
        }

        private void pickSweetBerries(BlockState p_148929_) {
            int i = p_148929_.getValue(SweetBerryBushBlock.AGE);
            p_148929_.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1));
            int j = 1 + CrowEntity.this.level.random.nextInt(2) + (i == 3 ? 1 : 0);
            ItemStack itemstack = CrowEntity.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemstack.isEmpty()) {
                CrowEntity.this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --j;
            }

            if (j > 0) {
                Block.popResource(CrowEntity.this.level, this.blockPos, new ItemStack(Items.SWEET_BERRIES, j));
            }

            CrowEntity.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
            CrowEntity.this.level.setBlock(this.blockPos, p_148929_.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1)), 2);
        }

        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;
            if(getHelpCommand() == 1 && getCommand() == 3){
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
            }
            return !CrowEntity.this.isSleeping() && getHelpCommand() == 1 && getCommand() == 3 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {

            if(getHelpCommand() == 1 && getCommand() == 3)
            {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
            }

            return super.canContinueToUse() && CrowEntity.this.getCommand() == 3 && CrowEntity.this.getHelpCommand() == 1;
        }

        public void start() {
            CrowEntity.this.doingTask = true;
            this.ticksWaited = 0;
            super.start();
        }

        @Override
        public void stop() {
            if(CrowEntity.this.getCommandHelp() && CrowEntity.this.getHelpCommand() == 1) {
                CrowEntity.this.searchForNewCropTarget = true;
                CrowEntity.this.searchForNewCropTargetTimer = 40;
            }

            CrowEntity.this.doingTask = false;
        }
    }

    private class CrowDepositCoffer extends Goal {
        private final CrowEntity entity;
        protected final CrowDepositCoffer.Sorter theNearestAttackableTargetSorter;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private BlockEntity targetEntity;
        private BlockPos flightTarget = null;
        private int cooldown = 0;
        private int tryTicks = 0;
        private Tag tag;



        CrowDepositCoffer(CrowEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new CrowDepositCoffer.Sorter(CrowEntity.this);
        }

        @Override
        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;

            if (CrowEntity.this.isPassenger() || CrowEntity.this.aiItemFlag || CrowEntity.this.isVehicle() || CrowEntity.this.isInSittingPose() || !CrowEntity.this.isTame()) {
                return false;
            }
            if(CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()){
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = CrowEntity.this.level.getGameTime() % 10;
                if (CrowEntity.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (CrowEntity.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            if (this.entity.cofferHerbJarList == null || this.entity.cofferHerbJarList.isEmpty()) {
                return false;
            } else {


                if(getHelpCommand() == 0 && getCommand() == 3)
                {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                }
                else
                    return false;

                Collections.sort(this.entity.cofferHerbJarList, this.theNearestAttackableTargetSorter);
                boolean flag = false;
                for(BlockPos pos : this.entity.cofferHerbJarList){
                    this.targetEntity = level.getBlockEntity(pos);

                    if(this.targetEntity == null)
                        break;

                    BlockPos perchPos = getPerchPos();
                    if(this.targetEntity.getBlockPos().equals(perchPos)){
                        if(targetEntity instanceof CofferTile cofferTile){
                            if(cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(CrowEntity.this.itemHandler.getStackInSlot(1)) || cofferTile.hasItem(Items.AIR)) {
                                flag = true;
                                break;
                            }
                        } else if(targetEntity instanceof HerbJarTile herbJarTile){
                            if(herbJarTile.itemHandler.getStackInSlot(0).isEmpty() || (herbJarTile.itemHandler.getStackInSlot(0).getTag() == CrowEntity.this.itemHandler.getStackInSlot(1).getTag() && herbJarTile.itemHandler.getStackInSlot(0).sameItem(CrowEntity.this.itemHandler.getStackInSlot(1)))) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if(targetEntity instanceof CofferTile) {

                        if (((CofferTile) this.targetEntity).hasItem(CrowEntity.this.itemHandler.getStackInSlot(1).getItem())) {
                            flag = true;
                            break;
                        }
                    }
                    else if(targetEntity instanceof HerbJarTile) {
                        if (((HerbJarTile) this.targetEntity).itemHandler.getStackInSlot(0).getItem() == CrowEntity.this.itemHandler.getStackInSlot(1).getItem()) {
                            flag = true;
                            break;
                        }
                    }
                }
                if(!flag)
                    return false;

                this.mustUpdate = false;
                CrowEntity.this.aiCofferTileFlag = true;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {

            if(getHelpCommand() == 0 && getCommand() == 3)
            {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
            }
            return targetEntity != null && !CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty() && CrowEntity.this.getCommand() == 3 && CrowEntity.this.getHelpCommand() == 0;
        }

        @Override
        public void start() {
            CrowEntity.this.doingTask = true;
            CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
        }

        public void stop() {
            
            CrowEntity.this.doingTask = false;
            flightTarget = null;
            this.targetEntity = null;
            CrowEntity.this.aiCofferTileFlag = false;
            CrowEntity.this.depositItemBeforePerch = false;
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        @Override
        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            if (cooldown > 0) {
                cooldown--;
            }

            boolean isStuck = false;
            if(CrowEntity.this.stuckTimer++ > 80)
            {
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25D) {
                    isStuck = true;
                }
                CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }
            if(isStuck)
            {
                Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
                BlockPos pos;
                if(randomPos == null)
                    randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
                if(randomPos != null)
                    pos = new BlockPos(randomPos.x, randomPos.y, randomPos.z);
                else if(CrowEntity.this.getPerchPos() != null)
                    pos = CrowEntity.this.getPerchPos().above().above();
                else
                    pos = CrowEntity.this.blockPosition().above().above();

                CrowEntity.this.navigation.stop();
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1D && CrowEntity.this.isOnGround()) {
//                    CrowEntity.this.push(0,2,0);
                    CrowEntity.this.push(Math.min(0.2f,(pos.getX() - CrowEntity.this.position().x) / 20.0f), 0.15f, Math.min(0.2f,(pos.getZ() - CrowEntity.this.position().z) / 20.0f));
                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), 1.5f);

                }
                else {

                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0), 1.5f);
                }

                CrowEntity.this.stuckTimer = 0;

//                CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), 1.5f);
            }

            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.targetEntity.getBlockPos().getX() + 0.5, targetEntity.getBlockPos().getY() + 0.5f, this.targetEntity.getBlockPos().getZ() + 0.5, 0), 1.5f);
                if(targetEntity.getBlockPos().closerThan(new Vec3i(CrowEntity.this.position().x,CrowEntity.this.position().y, CrowEntity.this.position().z), 3) && CrowEntity.this.position().y < targetEntity.getBlockPos().getY()) {
                    CrowEntity.this.setNoGravity(false);
                    CrowEntity.this.push((this.targetEntity.getBlockPos().getX() - CrowEntity.this.position().x) / 50.0f, (this.targetEntity.getBlockPos().getY() - CrowEntity.this.position().y) / 50.0f + 0.1f, (this.targetEntity.getBlockPos().getZ() - CrowEntity.this.position().z) / 50.0f);
                }
            }

            if (flightTarget != null) {
                if(CrowEntity.this.horizontalCollision){
                    CrowEntity.this.getMoveControl().setWantedPosition(flightTarget.getX() + 0.5f, CrowEntity.this.getY() + 1F, flightTarget.getZ() + 0.5f, 1.5f);

                }else{
//                    CrowEntity.this.getMoveControl().setWantedPosition(flightTarget.getX(), flightTarget.getY(), flightTarget.getZ(), 1F);
                    CrowEntity.this.getNavigation().moveTo(this.entity.getNavigation().createPath(flightTarget.getX() + 0.5f, flightTarget.getY() + 1f, flightTarget.getZ() + 0.5f, 0), 1.5f);
                }
            }
            if (targetEntity != null) {
                flightTarget = targetEntity.getBlockPos();
                if (CrowEntity.this.distanceToSqr(targetEntity.getBlockPos().getX() + 0.5f, targetEntity.getBlockPos().getY(), targetEntity.getBlockPos().getZ() + 0.5f) < this.entity.getMaxDistToItem() * 1.25f) {
                    try{
                        BlockEntity entity = targetEntity;
                        LazyOptional<IItemHandler> handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
                        if(handler.orElse(null) != null && cooldown == 0) {
                            ItemStack duplicate = CrowEntity.this.itemHandler.getStackInSlot(1).copy();
                            ItemStack insertSimulate = ItemHandlerHelper.insertItem(handler.orElse(null), duplicate, true);
                            if (!insertSimulate.equals(duplicate)) {
                                ItemStack shrunkenStack = ItemHandlerHelper.insertItem(handler.orElse(null), duplicate, false);
                                if(shrunkenStack.isEmpty()){
                                    CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                                }else{
                                    CrowEntity.this.itemHandler.setStackInSlot(1, shrunkenStack);
                                }
                                CrowEntity.this.peck();
                                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));
                            }else{
                                cooldown = 20;
                            }
                        }
                    }catch (Exception e){
                    }
                    this.stop();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ());
            AABB aabb = new AABB(-16, -16, -16, 16, 16, 16);
            return aabb.move(renderCenter);
        }


        public class Sorter implements Comparator<BlockPos> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(BlockPos p_compare_1_, BlockPos p_compare_2_) {
                double d0 = this.theEntity.distanceToSqr(Vec3.atCenterOf(p_compare_1_));
                double d1 = this.theEntity.distanceToSqr(Vec3.atCenterOf(p_compare_2_));
                return Double.compare(d0, d1);
            }
        }
    }

    private class CrowPickpocketVillager extends Goal {
        private final CrowEntity entity;
        protected final CrowPickpocketVillager.Sorter theNearestAttackableTargetSorter;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Villager targetEntity;
        private Vec3 flightTarget = null;
        private int tryTicks = 0;


        CrowPickpocketVillager(CrowEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new CrowPickpocketVillager.Sorter(CrowEntity.this);

        }

        @Override
        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;
            if(CrowEntity.this.pickpocketTimer > 0)
                return false;


            if (!CrowEntity.this.isTame()) {
                return false;
            }
            if (CrowEntity.this.isVehicle()) {
                return false;
            }
            if (CrowEntity.this.isPassenger()) {
                return false;
            }
            if(CrowEntity.this.getCommand() != 3)
                return false;
            if(CrowEntity.this.getHelpCommand() != 2)
                return false;

            if (!this.mustUpdate) {
                long worldTime = CrowEntity.this.level.getGameTime() % 10;
                if (CrowEntity.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (CrowEntity.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            if (this.entity.villagerList == null || this.entity.villagerList.isEmpty()) {
                return false;
            } else {

                if(getHelpCommand() == 2 && getCommand() == 3)
                {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                }
                Collections.sort(this.entity.villagerList, this.theNearestAttackableTargetSorter);
                this.targetEntity = this.entity.villagerList.get(0);
                this.mustUpdate = false;
                if(!CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()){
                    CrowEntity.this.spawnAtLocation(CrowEntity.this.itemHandler.getStackInSlot(1).copy());
                    CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                }
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {

            if(getHelpCommand() == 2 && getCommand() == 3)
            {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
            }

            if(!CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty())
                if(CrowEntity.this.pickpocketTimer > 0)
                    return true;

            return targetEntity != null && CrowEntity.this.getCommand() == 3 && CrowEntity.this.getHelpCommand() == 2;
        }

        @Override
        public void start() {
            CrowEntity.this.doingTask = true;
        }

        public void stop() {

            
            CrowEntity.this.doingTask = false;
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            if (CrowEntity.this.pickpocketTimer <= 0)
            {
                if (targetEntity != null) {

                    if (CrowEntity.this.distanceToSqr(targetEntity.position().x, targetEntity.position().y + 0.75f, targetEntity.position().z) < this.entity.getMaxDistToItem() * 8)
                        this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.targetEntity.position().x, this.targetEntity.position().y + 0.75f, this.targetEntity.position().z, 0), 1.5f);
                    else
                        this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.targetEntity.position().x, this.targetEntity.position().y + 1.75f, this.targetEntity.position().z, 0), 1.5f);
                    flightTarget = targetEntity.position();

                    if (CrowEntity.this.distanceToSqr(targetEntity.position().x, targetEntity.position().y + 0.75f, targetEntity.position().z) < this.entity.getMaxDistToItem() * 2) {
                        this.entity.lookAt(targetEntity, 10.0F, (float) this.entity.getMaxHeadXRot());
                        CrowEntity.this.peck();
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));

                        CrowEntity.this.pickpocketTimer = HexConfig.CROW_PICKPOCKET_COOLDOWN.get();
                        BlockPos pos = RandomPos.generateRandomDirection(this.entity.getRandom(), 10, 1);
                        flightTarget = new Vec3(pos.getX() + CrowEntity.this.position().x, pos.getY() + CrowEntity.this.position().y + 2.0f, pos.getZ() + CrowEntity.this.position().z);


                        LootContext.Builder builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.getRandom()).withParameter(LootContextParams.THIS_ENTITY, targetEntity);

                        LootContext ctx = builder.create(LootContextParamSet.builder().build());

                        NonNullList<ItemStack> stacks = NonNullList.create();
                        stacks.addAll(level.getServer().getLootTables().get(new ResourceLocation(Hexerei.MOD_ID + ":entities/crow_pickpocket_villager")).getRandomItems(ctx));

                        if(!stacks.isEmpty())
                            CrowEntity.this.itemHandler.setStackInSlot(1, stacks.get(0));

                        targetEntity.getBrain().setActiveActivityIfPossible(Activity.PANIC);

                    }
                    ++this.tryTicks;
                    if (this.shouldRecalculatePath()) {
                        if(targetEntity.position().distanceTo(CrowEntity.this.position()) < 3 && CrowEntity.this.position().y < targetEntity.position().y()) {
                            CrowEntity.this.setNoGravity(false);
                            CrowEntity.this.push((this.targetEntity.position().x - CrowEntity.this.position().x) / 50.0f, (this.targetEntity.position().y - CrowEntity.this.position().y) / 50.0f + 0.1f, (this.targetEntity.position().z - CrowEntity.this.position().z) / 50.0f);
                        }
                        CrowEntity.this.getNavigation().moveTo(this.entity.getNavigation().createPath(this.targetEntity.getX(), targetEntity.getY() + 3f, this.targetEntity.getZ(), 0), 1.5f);
                    }
                }
            }
            if(CrowEntity.this.pickpocketTimer < HexConfig.CROW_PICKPOCKET_COOLDOWN.get() && CrowEntity.this.pickpocketTimer > HexConfig.CROW_PICKPOCKET_COOLDOWN.get() / 2 )
            {
                if (flightTarget != null) {
                    this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.flightTarget.x, this.flightTarget.y + 0.75f, this.flightTarget.z, 0), 1.5f);
                    if (CrowEntity.this.distanceToSqr(flightTarget.x, flightTarget.y + 0.75f, flightTarget.z) < this.entity.getMaxDistToItem() * 4){
                        BlockPos pos = RandomPos.generateRandomDirection(this.entity.getRandom(), 10, 4);
                        flightTarget = new Vec3(pos.getX() + CrowEntity.this.position().x, CrowEntity.this.position().y, pos.getZ() + CrowEntity.this.position().z);

                    }
                }
            }
            if(CrowEntity.this.pickpocketTimer <= HexConfig.CROW_PICKPOCKET_COOLDOWN.get() / 2 && CrowEntity.this.pickpocketTimer > 5 )
            {
                if(CrowEntity.this.getPerchPos() == null){
                    if (flightTarget != null) {
                        this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.flightTarget.x, this.flightTarget.y + 0.75f, this.flightTarget.z, 0), 1.5f);
                        if (CrowEntity.this.distanceToSqr(flightTarget.x, flightTarget.y + 0.75f, flightTarget.z) < this.entity.getMaxDistToItem() * 4) {
                            BlockPos pos = RandomPos.generateRandomDirection(this.entity.getRandom(), 10, 4);
                            flightTarget = new Vec3(pos.getX() + CrowEntity.this.position().x, CrowEntity.this.position().y, pos.getZ() + CrowEntity.this.position().z);

                        }
                    }
                }
                else
                {
                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(CrowEntity.this.getPerchPos().above(), 0), 1.5f);
                }
            }

            if(CrowEntity.this.pickpocketTimer < 5 && CrowEntity.this.pickpocketTimer > 0 )
            {
                this.stop();
            }
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        public class Sorter implements Comparator<Villager> {
            private final Entity crow;

            public Sorter(Entity theEntityIn) {
                this.crow = theEntityIn;
            }

            public int compare(Villager p_compare_1_, Villager p_compare_2_) {
                double d0 = this.crow.distanceToSqr(new Vec3(p_compare_1_.position().x, p_compare_1_.position().y, p_compare_1_.position().z));
                double d1 = this.crow.distanceToSqr(new Vec3(p_compare_2_.position().x, p_compare_2_.position().y, p_compare_2_.position().z));
                return Double.compare(d0, d1);
            }
        }
    }

    public class FollowOwnerGoal extends Goal {
        private final TamableAnimal tamable;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public FollowOwnerGoal(TamableAnimal p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
            this.tamable = p_25294_;
            this.level = p_25294_.level;
            this.speedModifier = p_25295_;
            this.navigation = p_25294_.getNavigation();
            this.startDistance = p_25296_;
            this.stopDistance = p_25297_;
            this.canFly = p_25298_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            if (!(p_25294_.getNavigation() instanceof GroundPathNavigation) && !(p_25294_.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.tamable.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.tamable.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
                return false;
            } else if (((CrowEntity)this.tamable).getCommand() != 0) {
                return false;
            } else if (this.tamable.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else {
                if(getCommand() == 0)
                {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                }
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.tamable.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
                return false;
            } else {
                if(getCommand() == 0)
                {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                }
                return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            
            CrowEntity.this.doingTask = false;
            this.owner = null;
            this.navigation.stop();
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {

                    if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                        this.teleportToOwner();
                    } else {
                        this.navigation.moveTo(this.owner, this.speedModifier);
                    }

                }
            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
            if (Math.abs((double)p_25304_ - this.owner.getX()) < 2.0D && Math.abs((double)p_25306_ - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
                return false;
            } else {
                this.tamable.moveTo((double)p_25304_ + 0.5D, (double)p_25305_, (double)p_25306_ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos p_25308_) {
            BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, p_25308_.mutable());

            BlockState blockstate = this.level.getBlockState(p_25308_.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = p_25308_.subtract(this.tamable.blockPosition());
                return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
            }

        }

        private int randomIntInclusive(int p_25301_, int p_25302_) {
            return this.tamable.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
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

            } else if (CrowEntity.this.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
                return false;
            } else {
                List<? extends Animal> list = this.animal.level.getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
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
            } else if (CrowEntity.this.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
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
            if(CrowEntity.this.isPlayingDead())
                return;
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.animal.getNavigation().moveTo(this.parent, this.speedModifier);
            }
        }
    }

    public class TemptGoal extends Goal {
        private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
        private final TargetingConditions targetingConditions;
        protected final PathfinderMob mob;
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

        public TemptGoal(PathfinderMob p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_) {
            this.mob = p_25939_;
            this.speedModifier = p_25940_;
            this.items = p_25941_;
            this.canScare = p_25942_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
        }

        public boolean canUse() {
            if(CrowEntity.this.getCommandSit() && CrowEntity.this.isInSittingPose())
                return false;
            if (this.calmDown > 0) {
                --this.calmDown;
                return false;
            } else {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
                this.player = this.mob.level.getNearestPlayer(this.targetingConditions, this.mob);
                return this.player != null;
            }
        }

        private boolean shouldFollow(LivingEntity p_148139_) {
            return this.items.test(p_148139_.getMainHandItem()) || this.items.test(p_148139_.getOffhandItem());
        }

        public boolean canContinueToUse() {
            if(CrowEntity.this.getCommandSit() || CrowEntity.this.isInSittingPose())
                return false;
            if (this.canScare()) {
                if (this.mob.distanceToSqr(this.player) < 36.0D) {
                    if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
                        return false;
                    }

                    if (Math.abs((double)this.player.getXRot() - this.pRotX) > 5.0D || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0D) {
                        return false;
                    }
                } else {
                    this.px = this.player.getX();
                    this.py = this.player.getY();
                    this.pz = this.player.getZ();
                }

                this.pRotX = (double)this.player.getXRot();
                this.pRotY = (double)this.player.getYRot();
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
            
            CrowEntity.this.doingTask = false;
            this.player = null;
            this.mob.getNavigation().stop();
            this.calmDown = reducedTickDelay(100);
            this.isRunning = false;
        }

        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
            if (this.mob.distanceToSqr(this.player) < 6.25D) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.player, this.speedModifier);
            }

        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }

    public class MeleeAttackGoal extends Goal {
        protected final PathfinderMob mob;
        private final double speedModifier;
        private final boolean followingTargetEvenIfNotSeen;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private int ticksUntilNextAttack;
        private final int attackInterval = 20;
        private long lastCanUseCheck;
        private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
        private int failedPathFindingPenalty = 0;
        private boolean canPenalize = false;

        public MeleeAttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_) {
            this.mob = p_25552_;
            this.speedModifier = p_25553_;
            this.followingTargetEvenIfNotSeen = p_25554_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            long i = this.mob.level.getGameTime();
            if (i - this.lastCanUseCheck < 20L) {
                return false;
            } else {
                this.lastCanUseCheck = i;
                LivingEntity livingentity = this.mob.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    if (canPenalize) {
                        if (--this.ticksUntilNextPathRecalculation <= 0) {
                            this.path = this.mob.getNavigation().createPath(livingentity, 0);
                            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                            return this.path != null;
                        } else {
                            return true;
                        }
                    }

                    if(getCommand() != 1)
                    {
                        if(CrowEntity.this.isInSittingPose()) {
                            CrowEntity.this.setInSittingPose(false);
                            CrowEntity.this.setOrderedToSit(false);
                        }
                    }
                    this.path = this.mob.getNavigation().createPath(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    } else {
                        return this.getAttackReachSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                    }
                }
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!this.followingTargetEvenIfNotSeen) {
                return !this.mob.getNavigation().isDone();
            } else if (!this.mob.isWithinRestriction(livingentity.blockPosition())) {
                return false;
            } else {
                if(getCommand() != 1)
                {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                }
                return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
            }
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.path, this.speedModifier);
            this.mob.setAggressive(true);
            this.ticksUntilNextPathRecalculation = 0;
            this.ticksUntilNextAttack = 0;
        }

        public void stop() {
            LivingEntity livingentity = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.mob.setTarget((LivingEntity)null);
            }

            this.mob.setAggressive(false);
            this.mob.getNavigation().stop();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                double d1 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY() + livingentity.getBbHeight(), livingentity.getZ());
                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                    this.pathedTargetX = livingentity.getX();
                    this.pathedTargetY = livingentity.getY();
                    this.pathedTargetZ = livingentity.getZ();
                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                    if (this.canPenalize) {
                        this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                        if (this.mob.getNavigation().getPath() != null) {
                            net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                            if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                                failedPathFindingPenalty = 0;
                            else
                                failedPathFindingPenalty += 10;
                        } else {
                            failedPathFindingPenalty += 10;
                        }
                    }
                    if (d0 > 1024.0D) {
                        this.ticksUntilNextPathRecalculation += 10;
                    } else if (d0 > 256.0D) {
                        this.ticksUntilNextPathRecalculation += 5;
                    }

                    if (!this.mob.getNavigation().moveTo(CrowEntity.this.getNavigation().createPath(livingentity, 0), this.speedModifier)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }

                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
                }

                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                this.checkAndPerformAttack(livingentity, d0, d1);
            }
        }

        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_, double p_25559_) {
            double d0 = this.getAttackReachSqr(p_25557_);
            if ((p_25558_ <= d0 || p_25559_ <= d0) && this.ticksUntilNextAttack <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                doHurtTarget(p_25557_);
                CrowEntity.this.peck();
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> CrowEntity.this.level.getChunkAt(CrowEntity.this.blockPosition())), new CrowPeckPacket(this.mob));
            }

        }


        public boolean doHurtTarget(Entity p_21372_) {
            float f = (float)CrowEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)CrowEntity.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (p_21372_ instanceof LivingEntity) {
                f += EnchantmentHelper.getDamageBonus(CrowEntity.this.itemHandler.getStackInSlot(1), ((LivingEntity)p_21372_).getMobType());
                f1 += (float)EnchantmentHelper.getKnockbackBonus(CrowEntity.this);
            }

            int i = EnchantmentHelper.getFireAspect(CrowEntity.this);
            if (i > 0) {
                p_21372_.setSecondsOnFire(i * 4);
            }

            boolean flag = p_21372_.hurt(DamageSource.mobAttack(CrowEntity.this), f);
            if (flag) {
                if (f1 > 0.0F && p_21372_ instanceof LivingEntity) {
                    ((LivingEntity)p_21372_).knockback((double)(f1 * 0.5F), (double)Mth.sin(CrowEntity.this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(CrowEntity.this.getYRot() * ((float)Math.PI / 180F))));
                    CrowEntity.this.setDeltaMovement(CrowEntity.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                if (p_21372_ instanceof Player) {
                    Player player = (Player)p_21372_;
                    maybeDisableShield(player, CrowEntity.this.itemHandler.getStackInSlot(1), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
                }

                CrowEntity.this.doEnchantDamageEffects(CrowEntity.this, p_21372_);
                CrowEntity.this.setLastHurtMob(p_21372_);
            }

            return flag;
        }

        private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
            if (!p_21426_.isEmpty() && !p_21427_.isEmpty() && p_21426_.getItem() instanceof AxeItem && p_21427_.is(Items.SHIELD)) {
                float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(CrowEntity.this) * 0.05F;
                if (CrowEntity.this.random.nextFloat() < f) {
                    p_21425_.getCooldowns().addCooldown(Items.SHIELD, 100);
                    CrowEntity.this.level.broadcastEntityEvent(p_21425_, (byte)30);
                }
            }

        }



        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
        }

        protected boolean isTimeToAttack() {
            return this.ticksUntilNextAttack <= 0;
        }

        protected int getTicksUntilNextAttack() {
            return this.ticksUntilNextAttack;
        }

        protected int getAttackInterval() {
            return this.adjustedTickDelay(20);
        }

        protected double getAttackReachSqr(LivingEntity p_25556_) {
            return (double)(this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 2.5F + p_25556_.getBbWidth());
        }
    }

    public class OwnerHurtByTargetGoal extends TargetGoal {
        private final TamableAnimal tameAnimal;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public OwnerHurtByTargetGoal(TamableAnimal p_26107_) {
            super(p_26107_, false);
            this.tameAnimal = p_26107_;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
            if(entity instanceof TamableAnimal && CrowEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(CrowEntity.this.getOwner()))
            {
                return false;
            }
            return super.canAttack(entity, p_26152_);
        }

        public boolean canUse() {
            if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !CrowEntity.this.getCommandSit()) {
                LivingEntity livingentity = this.tameAnimal.getOwner();
                if (livingentity == null) {
                    return false;
                } else {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                    this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                    int i = livingentity.getLastHurtByMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
                }
            } else {
                return false;
            }
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurtBy);
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }



    public class OwnerHurtTargetGoal extends TargetGoal {
        private final TamableAnimal tameAnimal;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public OwnerHurtTargetGoal(TamableAnimal p_26114_) {
            super(p_26114_, false);
            this.tameAnimal = p_26114_;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
            if(entity instanceof TamableAnimal && CrowEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(CrowEntity.this.getOwner()))
            {
                return false;
            }
            return super.canAttack(entity, p_26152_);
        }

        public boolean canUse() {
            if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !CrowEntity.this.getCommandSit()) {
                LivingEntity livingentity = this.tameAnimal.getOwner();
                if (livingentity == null) {
                    return false;
                } else {
                    if(CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                    }
                    this.ownerLastHurt = livingentity.getLastHurtMob();
                    int i = livingentity.getLastHurtMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingentity);
                }
            } else {
                return false;
            }
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurt);
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtMobTimestamp();
            }

            super.start();
        }
    }


    public class SitWhenOrderedToGoal extends Goal {
        private final TamableAnimal mob;

        public SitWhenOrderedToGoal(TamableAnimal p_25898_) {
            this.mob = p_25898_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public double distanceTo(double p_20276_, double p_20278_) {
            double d0 = CrowEntity.this.getX() - p_20276_ - 0.5d;
            double d1 = CrowEntity.this.getZ() - p_20278_ - 0.5d;
            return Mth.sqrt((float)(d0 * d0 + d1 * d1));
        }

        public boolean canContinueToUse() {
            if(CrowEntity.this.getPerchPos() != null) {
                double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
                if (!(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)) {
                    CrowEntity.this.setOrderedToSit(false);
                    return false;
                }
            }

            return this.mob.isOrderedToSit();
        }

        public boolean canUse() {
            if(CrowEntity.this.doingTask)
                return false;
            if (!this.mob.isTame()) {
                return false;
            } else if (this.mob.isInWaterOrBubble()) {
                return false;
            } else if (!this.mob.isOnGround()) {
                return false;
            } else {
                LivingEntity livingentity = this.mob.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    if(CrowEntity.this.getPerchPos() != null)
                    {
                        double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
                        if (!(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)) {
                            return false;
                        }
                    }

                    return this.mob.distanceToSqr(livingentity) < 288.0D && livingentity.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
                }
            }
        }

        @Override
        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
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
            this.level = p_25125_.level;
            this.partnerClass = p_25127_;
            this.speedModifier = p_25126_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if (!this.animal.isInLove()) {
                return false;
            } else {
                this.partner = this.getFreePartner();
                return this.partner != null;
            }
        }

        public boolean canContinueToUse() {
            return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 120;
        }

        @Override
        public void start() {
            CrowEntity.this.doingTask = true;
            CrowEntity.this.setCommandHelp();
            if(CrowEntity.this.isInSittingPose()) {
                CrowEntity.this.setInSittingPose(false);
                CrowEntity.this.setOrderedToSit(false);
            }
            super.start();
        }

        public void stop() {
            CrowEntity.this.doingTask = false;
            this.partner = null;
            this.loveTime = 0;
//            CrowEntity.this.breedNuggetGivenByPlayer = false;
        }

        public void tick() {

            if(CrowEntity.this.isPlayingDead())
                return;
            if(this.partner != null){
                this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
                this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
                ++this.loveTime;
                if (this.animal.distanceToSqr(this.partner) < 4.0D && CrowEntity.this.breedNuggetGivenByPlayer) {


                    if(!((CrowEntity) this.partner).getCommandSit())
                        ((CrowEntity) this.partner).setCommandSit();
                    this.partner.getLookControl().setLookAt(this.animal, 10.0F, (float) this.animal.getMaxHeadXRot());

                    CrowEntity.this.waitToGiveTime++;

                    if (CrowEntity.this.waitToGiveTime > 20 && CrowEntity.this.isOnGround() && CrowEntity.this.itemHandler.getStackInSlot(1).is(Items.GOLD_NUGGET)) {

                        ((CrowEntity) this.partner).setCommandFollow();
                        CrowEntity.this.waitToGiveTime = 0;
                        CrowEntity.this.breedNuggetGivenByPlayer = false;

                        CrowEntity.this.peck();
                        HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new CrowPeckPacket(CrowEntity.this));

                        ItemStack stack = ((CrowEntity) this.partner).itemHandler.getStackInSlot(1).copy();
                        ItemStack stack2 = CrowEntity.this.itemHandler.getStackInSlot(1).copy();
                        ((CrowEntity) this.partner).itemHandler.setStackInSlot(1, stack2);
                        CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                        ItemEntity itemEntity = new ItemEntity(this.partner.getLevel(), this.partner.position().x, this.partner.position().y, this.partner.position().z, stack);
                        ((CrowEntity) this.partner).getLevel().addFreshEntity(itemEntity);

                        if (CrowEntity.this.breedNuggetGivenByPlayerUUID != null && level.getPlayerByUUID(CrowEntity.this.breedNuggetGivenByPlayerUUID) != null) {
                            ((CrowEntity) this.partner).breedNuggetGivenByPlayerUUID = CrowEntity.this.breedNuggetGivenByPlayerUUID;
                            ((CrowEntity) this.partner).breedNuggetGivenByCrowTimer = 20;
                        } else if (CrowEntity.this.getOwner() instanceof Player)
                            this.partner.setInLove((Player) CrowEntity.this.getOwner());
                    }
                }
                if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0D) {
                    if(CrowEntity.this.itemHandler.getStackInSlot(1).is(Items.GOLD_NUGGET))
                        this.breed();
                }
            }

        }
        public boolean canMateCrowBringNugget(Animal animal) {
            if(animal.isBaby())
                return false;
            if (animal == CrowEntity.this) {
                return false;
            } else if (animal.getClass() != CrowEntity.this.getClass()) {
                return false;
            } else {
                return CrowEntity.this.isInLove();
            }
        }
        public boolean canMateCrowReceiveNugget(Animal animal) {
            if(animal.isBaby())
                return false;
            if (animal == CrowEntity.this) {
                return false;
            } else if (animal.getClass() != CrowEntity.this.getClass()) {
                return false;
            } else {
                return CrowEntity.this.isInLove() && animal.isInLove();
            }
        }


        @Nullable
        private Animal getFreePartner() {
            List<? extends Animal> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(16.0D));
            double d0 = Double.MAX_VALUE;
            Animal animal = null;

            if(breedNuggetGivenByPlayer) {
                for (Animal animal1 : list) {
                    if (this.canMateCrowBringNugget(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                        animal = animal1;
                        d0 = this.animal.distanceToSqr(animal1);
                    }
                }
            } else {
                for (Animal animal1 : list) {
                    if (this.canMateCrowReceiveNugget(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                        animal = animal1;
                        d0 = this.animal.distanceToSqr(animal1);
                    }
                }
            }

            return animal;
        }

        protected void breed() {
            CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            spawnChildFromBreeding((ServerLevel)this.level, this.partner);
        }



        public void spawnChildFromBreeding(ServerLevel p_27564_, Animal p_27565_) {
            AgeableMob ageablemob = CrowEntity.this.getBreedOffspring(p_27564_, p_27565_);
            final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(CrowEntity.this, p_27565_, ageablemob);
            final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            ageablemob = event.getChild();
            if (cancelled) {
                //Reset the "inLove" state for the animals
                CrowEntity.this.setAge(6000);
                p_27565_.setAge(6000);
                CrowEntity.this.resetLove();
                p_27565_.resetLove();
                return;
            }
            if (ageablemob != null) {
                ServerPlayer serverplayer = CrowEntity.this.getLoveCause();
                if (serverplayer == null && p_27565_.getLoveCause() != null) {
                    serverplayer = p_27565_.getLoveCause();
                }

                if (serverplayer != null) {
                    serverplayer.awardStat(Stats.ANIMALS_BRED);
                    CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, CrowEntity.this, p_27565_, ageablemob);
                }

                CrowEntity.this.setAge(6000);
                p_27565_.setAge(6000);
                CrowEntity.this.resetLove();
                p_27565_.resetLove();
                ageablemob.setBaby(true);
                ageablemob.moveTo(CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ(), 0.0F, 0.0F);
                p_27564_.addFreshEntityWithPassengers(ageablemob);
                ((CrowEntity)ageablemob).setOwnerUUID(CrowEntity.this.breedNuggetGivenByPlayerUUID);
                ((CrowEntity)ageablemob).setCommandSit();
                p_27564_.broadcastEntityEvent(CrowEntity.this, (byte)18);
                if (p_27564_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    p_27564_.addFreshEntity(new ExperienceOrb(p_27564_, CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ(), CrowEntity.this.getRandom().nextInt(7) + 1));
                }

            }
        }

    }


    public class FlyBackToPerchGoal extends Goal {
        private final TamableAnimal mob;

        public FlyBackToPerchGoal(TamableAnimal p_25898_) {
            this.mob = p_25898_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            if(CrowEntity.this.getPerchPos() == null)
                return false;
            if(CrowEntity.this.aiItemFlag)
                return false;
            if(CrowEntity.this.depositItemBeforePerch)
                return false;
//            double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
//            if(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)
//                return false;
//            if(this.mob.distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY())
//            return this.mob.distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) > 1;

            if(getCommand() == 0)
                return false;
            if(getCommand() == 2)
            {
                if(distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) < 288)
                    return false;
//                        else if(CrowEntity.this.isInSittingPose()) {
//                            CrowEntity.this.setInSittingPose(false);
//                            CrowEntity.this.setOrderedToSit(false);
//                        }
            }
            if(getCommand() == 3)
            {
                if(distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) > 288)
                {
                    if(CrowEntity.this.navigation.getPath() != null)
                    {
                        if (CrowEntity.this.isInSittingPose()) {
                            CrowEntity.this.setInSittingPose(false);
                            CrowEntity.this.setOrderedToSit(false);
                        }
                        return true;
                    }
                    if(CrowEntity.this.navigation.isStuck())
                    {
                        if (CrowEntity.this.isInSittingPose()) {
                            CrowEntity.this.setInSittingPose(false);
                            CrowEntity.this.setOrderedToSit(false);
                        }
                        return true;
                    }
                }
                else if(CrowEntity.this.navigation.getPath() == null)
                    return true;
            }
            double topOffset = 0;
            if(CrowEntity.this.getPerchPos() != null)
                topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
            if(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
                    return false;
            }
            else {
                if(CrowEntity.this.isInSittingPose()) {
                    CrowEntity.this.setInSittingPose(false);
                    CrowEntity.this.setOrderedToSit(false);
                }
                return true;
            }

//            return true;
        }

        public double distanceTo(double p_20276_, double p_20278_) {
            double d0 = CrowEntity.this.getX() - p_20276_ - 0.5d;
            double d1 = CrowEntity.this.getZ() - p_20278_ - 0.5d;
            return Mth.sqrt((float)(d0 * d0 + d1 * d1));
        }

        public boolean canUse() {
            if(CrowEntity.this.searchForNewCropTarget)
                return false;
            if(CrowEntity.this.aiItemFlag)
                return false;
            if(CrowEntity.this.doingTask)
                return false;
            if(CrowEntity.this.depositItemBeforePerch)
                return false;
            if (!this.mob.isTame()) {
                return false;
            } else if (this.mob.isInWaterOrBubble()) {
                return false;
            } else {
                LivingEntity livingentity = this.mob.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    if(CrowEntity.this.getPerchPos() == null)
                        return false;
//                    if(CrowEntity.this.isInSittingPose())
//                        return false;

                    if(getCommand() == 0)
                        return false;
                    if(getCommand() == 2)
                    {
                        if(distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) < 288)
                            return false;
//                        else if(CrowEntity.this.isInSittingPose()) {
//                            CrowEntity.this.setInSittingPose(false);
//                            CrowEntity.this.setOrderedToSit(false);
//                        }
                    }
                    if(getCommand() == 3)
                    {
                        if(distanceToSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()) > 288)
                        {
                            if(CrowEntity.this.navigation.getPath() != null)
                            {
                                if (CrowEntity.this.isInSittingPose()) {
                                    CrowEntity.this.setInSittingPose(false);
                                    CrowEntity.this.setOrderedToSit(false);
                                }
                                return true;
                            }
                            if(CrowEntity.this.navigation.isStuck())
                            {
                                if (CrowEntity.this.isInSittingPose()) {
                                    CrowEntity.this.setInSittingPose(false);
                                    CrowEntity.this.setOrderedToSit(false);
                                }
                                return true;
                            }
                        }
                        else if(CrowEntity.this.navigation.getPath() == null)
                            return true;
                    }

                    double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
                    if(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
                        if(CrowEntity.this.isInSittingPose())
                            return false;
                    }
                    else{
                        if(CrowEntity.this.isInSittingPose()) {
                            CrowEntity.this.setInSittingPose(false);
                            CrowEntity.this.setOrderedToSit(false);
                        }
                        return true;
                    }
                    return !(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset);
                }
            }
        }

        @Override
        public void tick() {
            if(CrowEntity.this.isPlayingDead())
                return;
            double topOffset = 0;
            if(CrowEntity.this.getPerchPos() != null)
                topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);

            boolean isStuck = false;
            if(CrowEntity.this.stuckTimer++ > 80)
            {
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25D) {
                    isStuck = true;
                    CrowEntity.this.stuckTimer = 0;
                }
                CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }
            if(isStuck)
            {
                Vec3 randomPos = DefaultRandomPos.getPos(this.mob, 10, 7);
                BlockPos pos = null;
                if(CrowEntity.this.getPerchPos() != null)
                    pos = CrowEntity.this.getPerchPos().above().above();
                else
                    pos = CrowEntity.this.blockPosition().above().above();

                CrowEntity.this.navigation.stop();
                if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1D && CrowEntity.this.isOnGround()) {
//                    CrowEntity.this.push(0,2,0);
                    CrowEntity.this.push(Math.min(0.2f,(pos.getX() - CrowEntity.this.position().x) / 20.0f), 0.15f, Math.min(0.2f,(pos.getZ() - CrowEntity.this.position().z) / 20.0f));
                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), 1.5f);

                }
                else {

                    CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0), 1.5f);
                }

                CrowEntity.this.stuckTimer = 0;
            }
            else if(CrowEntity.this.getPerchPos() != null){
                if (!(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)) {

                    CrowEntity.this.navigation.moveTo(this.mob.getNavigation().createPath(CrowEntity.this.getPerchPos().above(), 0), 1.5f);
                }
            }



            super.tick();
        }

        public void start() {
            CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            if(CrowEntity.this.getPerchPos() != null){
//                CrowEntity.this.doingTask = true;

                CrowEntity.this.navigation.moveTo(this.mob.getNavigation().createPath(CrowEntity.this.getPerchPos().above(), 0), 1.5f);
            }
        }

        public void stop() {
            CrowEntity.this.aiItemFlag = false;
            CrowEntity.this.doingTask = false;
            if(CrowEntity.this.getPerchPos() != null){
                double topOffset = CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()).getBlock().getOcclusionShape(CrowEntity.this.level.getBlockState(CrowEntity.this.getPerchPos()), CrowEntity.this.level, CrowEntity.this.getPerchPos()).max(Direction.Axis.Y);
                if (this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1 && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
//                CrowEntity.this.setCommandSit();
                    CrowEntity.this.setInSittingPose(true);
                }
            }
//            this.mob.setInSittingPose(false);
        }
    }


}