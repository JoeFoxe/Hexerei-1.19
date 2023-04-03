package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.custom.ModChest;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class ModChestBlockEntity extends ChestBlockEntity {
    public ModChestBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        super.openersCounter = this.openersCounter;

    }


    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level p_155357_, BlockPos p_155358_, BlockState p_155359_) {
            boolean flag = false;
            if(p_155357_.getBlockEntity(p_155358_) instanceof ModChestBlockEntity blockEntity) {
                BlockState state = p_155357_.getBlockState(p_155358_);
                if(state.hasProperty(ModChest.TYPE)) {
                    if(state.getValue(ModChest.TYPE) == ChestType.SINGLE)
                        if (hasCustomName() && getCustomName().getString().equals("Hooty"))
                            flag = true;

                }
            }
            if (flag)
                ModChestBlockEntity.playSound(p_155357_, p_155358_, p_155359_, ModSounds.HOOTSIFER.get());
            else
                ModChestBlockEntity.playSound(p_155357_, p_155358_, p_155359_, SoundEvents.CHEST_OPEN);

        }

        protected void onClose(Level p_155367_, BlockPos p_155368_, BlockState p_155369_) {
            ModChestBlockEntity.playSound(p_155367_, p_155368_, p_155369_, SoundEvents.CHEST_CLOSE);
        }

        protected void openerCountChanged(Level p_155361_, BlockPos p_155362_, BlockState p_155363_, int p_155364_, int p_155365_) {
            ModChestBlockEntity.this.signalOpenCount(p_155361_, p_155362_, p_155363_, p_155364_, p_155365_);
        }

        protected boolean isOwnContainer(Player p_155355_) {
            if (!(p_155355_.containerMenu instanceof ChestMenu)) {
                return false;
            } else {
                Container container = ((ChestMenu)p_155355_.containerMenu).getContainer();
                return container == ModChestBlockEntity.this || container instanceof CompoundContainer && ((CompoundContainer)container).contains(ModChestBlockEntity.this);
            }
        }
    };

    private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> chestHandler;
    @Override
    public void setBlockState(BlockState pBlockState) {
        super.setBlockState(pBlockState);
        if (this.chestHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = this.chestHandler;
            this.chestHandler = null;
            oldHandler.invalidate();
        }
    }
    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this.chestHandler == null)
                this.chestHandler = net.minecraftforge.common.util.LazyOptional.of(this::createHandler);
            return this.chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (chestHandler != null) {
            chestHandler.invalidate();
            chestHandler = null;
        }
    }
    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ModChest)) {
            return new net.minecraftforge.items.wrapper.InvWrapper(this);
        }
        Container inv = ModChest.getContainer((ModChest) state.getBlock(), state, getLevel(), getBlockPos(), true);
        return new net.minecraftforge.items.wrapper.InvWrapper(inv == null ? this : inv);
    }

    static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
        ChestType chesttype = pState.getValue(ModChest.TYPE);
        if (chesttype != ChestType.LEFT) {
            double d0 = (double)pPos.getX() + 0.5D;
            double d1 = (double)pPos.getY() + 0.5D;
            double d2 = (double)pPos.getZ() + 0.5D;
            if (chesttype == ChestType.RIGHT) {
                Direction direction = ModChest.getConnectedDirection(pState);
                d0 += (double)direction.getStepX() * 0.5D;
                d2 += (double)direction.getStepZ() * 0.5D;
            }

            pLevel.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }


    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("CustomName")){
            Component component = Component.Serializer.fromJson(pTag.getString("CustomName"));
            if (component != null)
                setCustomName(component);
        }
        sync();

    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag) -> this.getUpdateTag());
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt)
    {
        this.deserializeNBT(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
//        sync();
    }

    public void sync() {
        if(level != null){
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

//            if (this.level != null)
//                this.level.sendBlockUpdated(this.getPos(), this.level.getBlockState(this.getPos()), this.level.getBlockState(this.getPos()),
//                        Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.hasCustomName())
            pTag.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));

    }
    public CompoundTag save(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.hasCustomName())
            pTag.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));

        return pTag;

    }
    public ModChestBlockEntity(BlockPos pPos, BlockState pBlockState) {

        this(ModTileEntities.CHEST_TILE.get(), pPos, pBlockState);
    }

}
