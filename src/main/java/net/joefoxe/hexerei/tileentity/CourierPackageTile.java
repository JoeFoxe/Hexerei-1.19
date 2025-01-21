package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.CourierPackage;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CourierPackageTile extends RandomizableContainerBlockEntity implements Container, Clearable {

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
    private boolean sealed = false;

    public CourierPackageTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    public CourierPackageTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.COURIER_PACKAGE_TILE.get(),blockPos, blockState);
    }

    public boolean interact(Player pPlayer) {
        unpackLootTable(pPlayer);
        if ((!isEmpty()) && hasLevel()) {
            if (level instanceof ServerLevel serverLevel) {
                for (ItemStack stack : itemStacks) {
                    ItemEntity ie = new ItemEntity(level, this.getBlockPos().getX() + 0.5f, this.getBlockPos().getY() + 0.5f, this.getBlockPos().getZ() + 0.5f, stack, serverLevel.random.nextDouble() * 0.2D - 0.1D, 0.3D, serverLevel.random.nextDouble() * 0.2D - 0.1D);
                    ie.setDefaultPickUpDelay();
                    level.addFreshEntity(ie);
                }
                clearContent();
                setChanged();
                serverLevel.playSound(null, this.getBlockPos(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.playSound(null, this.getBlockPos(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f, 0.20f);
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.02);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()), this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 25, 0.25, 0.25, 0.25, 0.02);
                level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CourierPackage.STATE, CourierPackage.State.OPENED));
            }
            return true;
        }

        return false;
    }

    @Override
    public BlockEntityType<?> getType() {
        return super.getType();
    }


    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        super.setItem(pIndex, pStack);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.itemStacks = itemsIn;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public void startOpen(Player p_18955_) {
        super.startOpen(p_18955_);
    }

    @Override
    public void stopOpen(Player p_18954_) {
        super.stopOpen(p_18954_);
    }

    @Override
    public boolean canPlaceItem(int p_18952_, ItemStack stack) {
        String id = HexereiUtil.getRegistryName(stack.getItem()).toString();
        if(HexConfig.COFFER_BLACKLIST.get().contains(id))
            return false;
        return super.canPlaceItem(p_18952_, stack);
    }

    @Override
    public int countItem(Item p_18948_) {
        return super.countItem(p_18948_);
    }


    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Hexerei.MOD_ID + ".coffer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new CofferContainer(id, this.level, this.worldPosition, player, player.player);
    }

    @Override
    public void clearContent() {
        super.clearContent();

        this.itemStacks = NonNullList.withSize(this.itemStacks.size(), ItemStack.EMPTY);
    }

//    @Override
    public CompoundTag save(CompoundTag pTag, HolderLookup.Provider registries) {
        saveAdditional(pTag, registries);

        return pTag;
    }

    public CompoundTag saveData(CompoundTag pTag, HolderLookup.Provider registries) {
        if (!this.isEmpty()) {
            return save(pTag, registries);
        }
        return pTag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadFromTag(tag, registries);
    }

    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.saveAdditional(pTag, registries);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.itemStacks, false, registries);
        }
        pTag.putBoolean("Sealed", this.sealed);

    }

    public void loadFromTag(CompoundTag pTag, HolderLookup.Provider registries) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag) && pTag.contains("Items", Tag.TAG_LIST)) {
            ContainerHelper.loadAllItems(pTag, this.itemStacks, registries);
        }
        if (pTag.contains("Sealed"))
            this.sealed = pTag.getBoolean("Sealed");

    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return this.save(new CompoundTag(), registries);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }

    public void sync() {
        setChanged();

        if(level != null){
            if (!level.isClientSide) {
                CompoundTag tag = new CompoundTag();
                this.saveAdditional(tag, level.registryAccess());
                HexereiPacketHandler.sendToNearbyClient(level, worldPosition, new TESyncPacket(worldPosition, tag));
            }

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }



    public boolean isEmpty() {

        if(this.itemStacks != null){
            for (int i = 0; i < this.getItems().size(); i++) {
                if (!this.getItems().get(i).isEmpty())
                    return false;
            }
            return true;
        }
        return true;

    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

//    @Override
    public void tick() {
//        if(level.isClientSide)
//            return;

    }

    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize();
    }
}
