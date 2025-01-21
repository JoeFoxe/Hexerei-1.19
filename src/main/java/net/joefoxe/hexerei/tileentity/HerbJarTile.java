package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.HerbJarSyncCrowButtonToServer;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class HerbJarTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider, ICapabilityProvider<HerbJarTile, Direction, IItemHandler> {

    public JarHandler itemHandler;

    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final FormattedCharSequence[] renderText = new FormattedCharSequence[1];

    private final Component[] signText = new Component[]{Component.literal("Text")};

    public int degreesOpened;

    public Component customName;

    private long lastClickTime;
    private UUID lastClickUUID;

    public int buttonToggled;
    public DyedItemColor dyeColor;



    public HerbJarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
        this.itemHandler = createHandler();
        this.dyeColor = HerbJar.DEFAULT_COLOR;
    }

    public HerbJarTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.HERB_JAR_TILE.get(),blockPos, blockState);
    }

    public void setButtonToggled(int buttonToggled) {
        this.buttonToggled = buttonToggled;

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new HerbJarSyncCrowButtonToServer(this, buttonToggled));

    }

    public boolean hasDyeColor() {
        return this.dyeColor != HerbJar.DEFAULT_COLOR;
    }

    public int getButtonToggled() {
        return this.buttonToggled;
    }


    public void readInventory(HolderLookup.Provider provider, CompoundTag compound) {
        itemHandler.deserializeNBT(provider, compound);
    }

    public int getDyeColor(){
        DyeColor dye = HexereiUtil.getDyeColorNamed(this.getDisplayName().getString());
        if(dye != null)
            return HexereiUtil.getColorValue(dye);
        return this.dyeColor != null ? this.dyeColor.rgb() : HerbJar.DEFAULT_COLOR.rgb();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getItemStackInSlot(int slot) {
        return this.itemHandler.getStackInSlot(slot);
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.itemHandler.setStackInSlot(0, itemsIn.get(0));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Hexerei.MOD_ID + ".herb_jar");
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("inv", itemHandler.serializeNBT(registries));
        if (this.customName != null)
            compound.putString("CustomName", Component.Serializer.toJson(this.customName, registries));
        compound.putInt("ButtonToggled", this.buttonToggled);

        if (this.dyeColor != HerbJar.DEFAULT_COLOR)
            DyedItemColor.CODEC.optionalFieldOf("DyeColor", HerbJar.DEFAULT_COLOR).codec()
                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.dyeColor).resultOrPartial((string) -> {}).ifPresent((p_337994_) -> compound.merge((CompoundTag)p_337994_));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
        if(tag.contains("ButtonToggled"))
            this.buttonToggled = tag.getInt("ButtonToggled");
        if(tag.contains("DyeColor")) {
            this.dyeColor = DyedItemColor.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("DyeColor")).getOrThrow().getFirst();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }


    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public void sync() {

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


    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new HerbJarContainer(id, ((HerbJar)this.getBlockState().getBlock()).getCloneItemStack(this.level, this.worldPosition, this.getBlockState()), this.level, this.worldPosition, player, player.player);
    }


    @Override
    public void clearContent() {
        super.clearContent();
    }


    @Nullable
    public FormattedCharSequence reorderText(int row, Function<Component, FormattedCharSequence> textProcessorFunction) {
        if (this.renderText[row] == null && this.customName != null) {
            this.renderText[row] = textProcessorFunction.apply(this.customName);
        }

        return this.renderText[row];
    }


    private JarHandler createHandler() {
        return new JarHandler(1,1024) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(HexConfig.JARS_ONLY_HOLD_HERBS.get()) {
                    return stack.is(HexereiTags.Items.HERB_ITEM);
                }
                return true;
            }

        };
    }



    @Override
    public @Nullable IItemHandler getCapability(HerbJarTile object, Direction context) {
        return this.itemHandler;
    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName
                : Component.translatable("");
    }

    @Nonnull
    public ItemStack takeItems (int slot, int count) {

        ItemStack stack = this.itemHandler.getStackInSlot(slot).copy();
        stack.setCount(Math.min(count, this.itemHandler.getStackInSlot(slot).getMaxStackSize()));
        this.itemHandler.getStackInSlot(slot).setCount(this.itemHandler.getStackInSlot(slot).getCount() - stack.getCount());

        return stack;
    }

    public int putItems (int slot, @Nonnull ItemStack stack, int count) {
        if(HexConfig.JARS_ONLY_HOLD_HERBS.get())
            if(!stack.is(HexereiTags.Items.HERB_ITEM))
                return 0;

        if (this.itemHandler.getContents().get(0).isEmpty()) {
            this.itemHandler.insertItem(0, stack.copy(), false);
            setChanged();
            stack.shrink(count);
            return count;
        }

        if (!ItemStack.isSameItemSameComponents(stack, this.itemHandler.getContents().get(0)))
            return 0;

        int countAdded = Math.min(count, stack.getCount());
        countAdded = Math.min(countAdded, 1024 - this.itemHandler.getContents().get(0).getCount());

        this.itemHandler.getContents().get(0).setCount(this.itemHandler.getContents().get(0).getCount() + countAdded);
        stack.shrink(countAdded);
        return countAdded;
    }


    public void clientUpdateCount (final int slot, final int count) {
        if (!Objects.requireNonNull(getLevel()).isClientSide)
            return;
        Minecraft.getInstance().tell(() -> HerbJarTile.this.clientUpdateCountAsync(slot, count));
    }

    private void clientUpdateCountAsync (int slot, int count) {
        if (this.itemHandler.getStackInSlot(0).getCount() != count){
            ItemStack newStack = this.itemHandler.getStackInSlot(0).copy();
            this.itemHandler.setStackInSlot(0, newStack);
        }
    }


    public int interactPutItems (Player player) {
        int count;
        if (Objects.requireNonNull(getLevel()).getGameTime() - lastClickTime < 10 && player.getUUID().equals(lastClickUUID))
            count = interactPutCurrentInventory(0, player);
        else
            count = interactPutCurrentItem(0, player);

        lastClickTime = getLevel().getGameTime();
        lastClickUUID = player.getUUID();
        if(count > 0)
            setChanged();

        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.itemHandler.isEmpty();
    }

    public int interactPutCurrentItem (int slot, Player player) {

        int count = 0;
        ItemStack playerStack = player.getInventory().getSelected();
        if (!playerStack.isEmpty())
            count = putItems(slot, playerStack, playerStack.getCount());

        return count;
    }


    public int interactPutCurrentInventory (int slot, Player player) {
        int count = 0;
        if (!this.itemHandler.getContents().get(0).isEmpty()) {
            for (int i = 0, n = player.getInventory().getContainerSize(); i < n; i++) {
                ItemStack subStack = player.getInventory().getItem(i);
                if (!subStack.isEmpty()) {
                    int subCount = putItems(slot, subStack, subStack.getCount());
                    if (subCount > 0 && subStack.getCount() == 0)
                        player.getInventory().setItem(i, ItemStack.EMPTY);

                    count += subCount;
                }
            }
        }

        if (count > 0)
            if (player instanceof ServerPlayer)
                ((ServerPlayer) player).initMenu(player.containerMenu);

        return count;
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return super.createMenu(containerId, playerInventory, player);
    }

    @Override
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public Component getName() {
        return customName;
    }

    public int getDegreesOpened() {
        return this.degreesOpened;
    }
    public void setDegreesOpened(int degrees) {
        this.degreesOpened =  degrees;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }
}
