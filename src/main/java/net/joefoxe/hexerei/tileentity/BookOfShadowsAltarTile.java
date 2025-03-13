package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;
import static net.joefoxe.hexerei.util.HexereiUtil.moveToAngle;

public class BookOfShadowsAltarTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {

    public final ItemStackHandler itemHandler = createHandler();
    private final Optional<IItemHandler> handler = Optional.of(itemHandler);

    public BookData currentBook;
    public PageDrawing drawing;
    public float bookYaw;
    public float bookYawO;
    public float bookYawIncrement;
    public float degreesSpun;
    public float degreesSpunOld;
    public float degreesSpunTo;
    public float degreesSpunRender;
    public float degreesSpunSpeed;
    public float degreesOpened;
    public float openedPercent;
    public float openedPercentOld;
    public float degreesOpenedTo;
    public float degreesOpenedRender;
    public float degreesOpenedSpeed;
    public float floppedPercent;
    public float floppedPercentOld;
    public float degreesFlopped;
    public float degreesFloppedTo;
    public float degreesFloppedRender;
    public float degreesFloppedSpeed;
    public boolean drawTooltip;
    public float tooltipScale;
    public float tooltipScaleOld;
    public int turnPage;
    public int turnToPage;
    public int turnToChapter;
    public float buttonScale;
    public float buttonScaleOld;
    public float buttonScaleTo;
    public float buttonScaleRender;
    public float buttonScaleSpeed;
    public float bookmarkSelectorScale;
    public float pageOneRotation;
    public float pageTwoRotation;
    public float pageOneRotationLast;
    public float pageTwoRotationLast;
    public float pageOneRotationTo;
    public float pageTwoRotationTo;
    public float pageOneRotationRender;
    public float pageTwoRotationRender;
    public float pageOneRotationSpeed;
    public float pageTwoRotationSpeed;
    public float numberOfCandles;
    public float maxCandles = 3;
    public BlockPos candlePos1;
    public BlockPos candlePos2;
    public BlockPos candlePos3;
    public int candlePos1Slot;
    public int candlePos2Slot;
    public int candlePos3Slot;
    public float degreesSpunCandles;
    public float tickCount;

    public Vec3 closestPlayerPos;
    public Player closestPlayer;
    public double closestDist;

    public final double maxDist = 5;

    public int slotClicked = -1;

    public int slotClickedTick = 0;
    public boolean fromItem = false;


    public BookOfShadowsAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);

        this.bookYaw = 0;
        this.bookYawO = 0;
        this.bookYawIncrement = 0;
        this.drawTooltip = false;
        this.tooltipScale = 0;
        this.tooltipScaleOld = 0;
        this.turnPage = 0;
        this.buttonScale = 1;
        this.buttonScaleTo = 1;
        this.buttonScaleRender = 1;
        this.buttonScaleSpeed = 0;
        this.bookmarkSelectorScale = 0;
        this.pageOneRotation = 0;
        this.pageOneRotationLast = 0;
        this.pageOneRotationRender = 0;
        this.pageOneRotationTo = 0;
        this.pageOneRotationSpeed = 0;
        this.pageTwoRotation = 0;
        this.pageTwoRotationLast = 0;
        this.pageTwoRotationRender = 0;
        this.pageTwoRotationTo = 0;
        this.pageTwoRotationSpeed = 0;
        this.floppedPercent = 1;
        this.floppedPercentOld = 1;
        this.degreesFlopped = 90;
        this.degreesFloppedTo = 1;
        this.degreesFloppedSpeed = 0;
        this.degreesFloppedRender = 90;
        this.openedPercent = 1;
        this.openedPercentOld = 1;
        this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
        this.degreesOpenedTo = 1;
        this.degreesOpenedSpeed = 0;
        this.degreesOpenedRender = 90;
        this.degreesSpun = 0;
        this.degreesSpunTo = 0;
        this.degreesSpunSpeed = 0;
        this.degreesSpunRender = 0;
        this.candlePos1Slot = 0;
        this.candlePos2Slot = 0;
        this.candlePos3Slot = 0;
        this.drawing = new PageDrawing(this);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
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

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public void sync() {
        if (this.level != null) {
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

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        return tag;
    }


    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inv", itemHandler.serializeNBT(registries));
//        tag.putInt("turnPage", this.turnPage);
//        tag.putInt("turnToPage", this.turnToPage);
//        tag.putInt("turnToChapter", this.turnToChapter);
        tag.putFloat("degreesSpun", this.degreesSpun);
        tag.putFloat("floppedPercent", this.floppedPercent);
        tag.putFloat("openedPercent", this.openedPercent);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
        if (this.currentBook == null)
            this.currentBook = itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
//        this.turnPage = tag.getInt("turnPage");
//        this.turnToPage = tag.getInt("turnToPage");
//        this.turnToChapter = tag.getInt("turnToChapter");
        this.degreesSpun = tag.getFloat("degreesSpun");
        this.degreesSpunRender = degreesSpun;
        this.bookYaw = degreesSpun;
        this.floppedPercent = tag.getFloat("floppedPercent");
        this.floppedPercentOld = this.floppedPercent;
        this.openedPercent = tag.getFloat("openedPercent");
        this.openedPercentOld = this.openedPercent;
    }

    public boolean interact(Player player, InteractionHand handIn, ItemStack stackIn) {
        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        if (!player.isShiftKeyDown()) {
            if (stack.isEmpty()) {
                Random rand = new Random();
                if (!stackIn.isEmpty()) {
                    this.itemHandler.setStackInSlot(0, stackIn);
                    level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                    player.setItemInHand(handIn, ItemStack.EMPTY);
                    BookData bookData = stackIn.get(ModDataComponents.BOOK);
                    if (stackIn.getItem() instanceof HexereiBookItem) {

                        if (bookData != null){
                            this.turnToChapter = bookData.getChapter();
                            this.turnToPage = bookData.getPage();
                            this.closestDist = (getDistanceToEntity(player, this.worldPosition));
                            this.closestPlayerPos = player.position();
                            this.closestPlayer = player;
                            this.degreesSpun = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunTo = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunRender = 270 - getAngle(this.closestPlayerPos);
                        }

                        setChanged();
                    }
                    return true;
                }
            } else {
                if (stack.getItem() instanceof HexereiBookItem) {
                    BookData bookData = stack.get(ModDataComponents.BOOK);
                    if (bookData != null){
                        if (!bookData.isOpened() && this.openedPercent == 1) {

                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_OPENING.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
                            bookData = bookData.setOpened(true);
                            stack.set(ModDataComponents.BOOK, bookData);
                            this.itemHandler.setStackInSlot(0, stack);
                            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
                            setChanged();
                            return true;
                        }
                    }
                }
            }
        }
        if (!stack.isEmpty()) {
            if ((stack.getItem() instanceof HexereiBookItem) && !player.isShiftKeyDown()) {
                return false;
            }

            setChanged();

            if (player.getItemInHand(handIn).isEmpty())
                player.setItemInHand(handIn, this.itemHandler.getStackInSlot(0).copy());
            else
                player.getInventory().placeItemBackInInventory(this.itemHandler.getStackInSlot(0).copy());

            resetBookRotations();

            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

            setChanged();

            return true;
        }

        return false;
    }

    public void resetBookRotations() {
        this.floppedPercent = 1;
        this.degreesFlopped = 90;
        this.degreesFloppedRender = 90;
        this.openedPercent = 1;
        this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
        this.degreesOpenedRender = 90;
        this.degreesSpun = 0;
        this.degreesSpunRender = 0;
        this.degreesSpunTo = 0;
        this.pageOneRotation = 0;
        this.pageOneRotationTo = 0;
        this.pageOneRotationRender = 0;
        this.pageTwoRotation = 0;
        this.pageTwoRotationTo = 0;
        this.pageTwoRotationRender = 0;
        this.turnPage = 0;
        this.turnToPage = 0;
        this.turnToChapter = 0;
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return null;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
        for (int i = 0; i < this.itemHandler.getSlots(); i++)
            items.set(i, this.itemHandler.getStackInSlot(i));
        return items;
    }

    @Override
    public ItemStack removeItem(int p_59613_, int p_59614_) {
        this.unpackLootTable(null);
        ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemHandler.getSlots() && !this.itemHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0 ? this.getItems().get(p_59613_).split(p_59614_) : ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_59630_) {
        this.unpackLootTable(null);
        if (p_59630_ >= 0 && p_59630_ < this.itemHandler.getSlots()) {
            this.itemHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
            return this.itemHandler.getStackInSlot(p_59630_);

        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int p_59611_) {
        this.unpackLootTable(null);
        return this.itemHandler.getStackInSlot(p_59611_);
    }

    @Override
    public void setItem(int p_59616_, ItemStack p_59617_) {
        this.unpackLootTable(null);
        this.itemHandler.setStackInSlot(p_59616_, p_59617_);
        if (p_59617_.getCount() > this.getMaxStackSize()) {
            p_59617_.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        for (int i = 0; i < Math.min(itemsIn.size(), this.itemHandler.getSlots()); i++)
            this.itemHandler.setStackInSlot(i, itemsIn.get(i));
    }


    @Override
    public void clearContent() {
        super.clearContent();
//        this.items.clear();

        for (int i = 0; i < this.itemHandler.getSlots(); i++)
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
    }

    public BookOfShadowsAltarTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), blockPos, blockState);
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

    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5f, pos.x() - this.worldPosition.getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private boolean getCandle(Level world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof CandleTile;
    }

    public static float easeFlop(float x) {
        return x < 0.5f
                ? (float) Math.pow(2, 20 * x - 10) / 2
                : (float) (2 - Math.pow(2, -20 * x + 10)) / 2f;
    }

    public static float easeOpened(float x) {
    float c1 = 1f;
    float c2 = c1 * 1.525f;

        return x < 0.5f
                ? (float) (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2f
                : (float) (2 - Math.pow(2, -20 * x + 10)) / 2f;
    }
    public static float easeButtons(float x) {
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;

        return x < 0.5
                ? (float) (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (float) (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public void tickCandles() {
        tickCount++;

        numberOfCandles = 0;

        candlePos1 = new BlockPos(0, 0, 0);
        candlePos2 = new BlockPos(0, 0, 0);
        candlePos3 = new BlockPos(0, 0, 0);

        for (int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k != 0 || l != 0)) {
                    if ((level.getBlockEntity(worldPosition.offset(l * 2, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {
                        for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                            if ((i == 0 && candleTile.candles.get(0).lit)
                                    || (i == 1 && candleTile.candles.get(1).lit)
                                    || (i == 2 && candleTile.candles.get(2).lit)
                                    || (i == 3 && candleTile.candles.get(3).lit)) {
                                if (numberOfCandles == 0) {
                                    candlePos1 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos1Slot = i;
                                }
                                if (numberOfCandles == 1) {
                                    candlePos2 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos2Slot = i;
                                }
                                if (numberOfCandles == 2) {
                                    candlePos3 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos3Slot = i;
                                }
                                numberOfCandles++;
                            }
                        }

                    }
                    if ((level.getBlockEntity(worldPosition.offset(l * 2, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                        for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                            if ((i == 0 && candleTile.candles.get(0).lit)
                                    || (i == 1 && candleTile.candles.get(1).lit)
                                    || (i == 2 && candleTile.candles.get(2).lit)
                                    || (i == 3 && candleTile.candles.get(3).lit)) {
                                if (numberOfCandles == 0) {
                                    candlePos1 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos1Slot = i;
                                }
                                if (numberOfCandles == 1) {
                                    candlePos2 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos2Slot = i;
                                }
                                if (numberOfCandles == 2) {
                                    candlePos3 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos3Slot = i;
                                }
                                numberOfCandles++;
                            }
                        }
                    }

                    if (l != 0 && k != 0) {

                        if ((level.getBlockEntity(worldPosition.offset(l * 2, 0, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l * 2, 0, k);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l * 2, 0, k);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l * 2, 0, k);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }
                        if ((level.getBlockEntity(worldPosition.offset(l * 2, 1, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l * 2, 1, k);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l * 2, 1, k);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l * 2, 1, k);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }

                        }
                        if ((level.getBlockEntity(worldPosition.offset(l, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l, 0, k * 2);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l, 0, k * 2);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l, 0, k * 2);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }
                        if ((level.getBlockEntity(worldPosition.offset(l, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l, 1, k * 2);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l, 1, k * 2);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l, 1, k * 2);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }

                    }
                }
            }
        }

        degreesSpunCandles = moveToAngle(degreesSpunCandles, degreesSpunCandles + 1, 0.025f);

        if (numberOfCandles >= 1 && level.getBlockEntity(candlePos1) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos1Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos1.getX() + (float) Math.sin(degreesSpunCandles) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos1.getY() + 1f + (float) Math.sin(this.tickCount / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos1.getZ() + (float) Math.cos(degreesSpunCandles) * 1.25f);
        }
        if (numberOfCandles >= 2 && level.getBlockEntity(candlePos2) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos2Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos2.getX() + (float) Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI * 2f / 3f)) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos2.getY() + 1f + (float) Math.sin((this.tickCount + 10) / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos2.getZ() + (float) Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI * 2f / 3f)) * 1.25f);
        }
        if (numberOfCandles >= 3 && level.getBlockEntity(candlePos3) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos3Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos3.getX() + (float) Math.sin(degreesSpunCandles + Math.PI * 2f / 3f * 2f) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos3.getY() + 1f + (float) Math.sin((this.tickCount + 20) / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos3.getZ() + (float) Math.cos(degreesSpunCandles + Math.PI * 2f / 3f * 2f) * 1.25f);
        }
    }

    public void tickBook(ItemStack stack) {
        tickBook(stack, false);
    }

    static class ClientSounds {
        public static void playTurnPageSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOK_TURN_PAGE_SLOW.get(), (new Random().nextFloat() * 0.25f + 0.75f), 0.25f * (new Random().nextFloat() * 0.25f + 0.5f)));
        }
        public static void playTurnPageFastSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOK_TURN_PAGE_FAST.get(), (new Random().nextFloat() * 0.3f + 0.7f), 0.25f * (new Random().nextFloat() * 0.25f + 0.5f)));
        }
        public static void playBookCloseSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOK_CLOSE.get(), (new Random().nextFloat() * 0.25f + 0.75f)));
        }
        public static void playBookmarkSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOKMARK_BUTTON.get(), (new Random().nextFloat() * 0.25f + 0.75f)));
        }
        public static void playBookmarkSwapSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOKMARK_SWAP.get(), (new Random().nextFloat() * 0.25f + 0.75f), 0.25f * 0.75f));
        }
        public static void playBookmarkDeleteSound() {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.BOOKMARK_DELETE.get(), (new Random().nextFloat() * 0.25f + 0.75f)));
        }
    }
    public static int snapToCardinalDirection(float angle) {
        // Normalize the angle to the range [0, 360)
        angle = angle % 360;
        if (angle < 0) {
            angle += 360; // Ensure the angle is positive
        }

        // Define the cardinal directions
//        int[] cardinalDirections = {0, 90, 180, 270, 360};
        int[] directions = {0, 45, 90, 135, 180, 225, 270, 315, 360};

        // Find the closest cardinal direction
        int closestDirection = 0;
        float smallestDifference = 360;

        for (int direction : directions) {
            float difference = Math.abs(angle - direction);
            if (difference < smallestDifference) {
                smallestDifference = difference;
                closestDirection = direction;
            }
        }

        // Normalize 360° to 0° (since they represent the same direction)
        if (closestDirection == 360) {
            closestDirection = 0;
        }

        return closestDirection;
    }
    public void tickBook(ItemStack stack, boolean fromItem) {
        if (stack.getItem() instanceof HexereiBookItem) {
            if (slotClicked != -1)
                this.slotClickedTick++;

            BookData bookData = (fromItem || this.level.isClientSide) ? this.currentBook : stack.get(ModDataComponents.BOOK);
            this.pageOneRotationLast = this.pageOneRotation;
            this.pageTwoRotationLast = this.pageTwoRotation;
            boolean opened = bookData != null && bookData.isOpened();
            if (opened) {

                this.buttonScale = moveTo(this.buttonScale, this.buttonScaleTo, this.buttonScaleSpeed);
                this.buttonScaleRender = this.buttonScale;

                if (this.slotClicked != -1 && this.slotClickedTick > 5)
                    this.bookmarkSelectorScale = moveTo(this.bookmarkSelectorScale, 1, 0.15f * (this.bookmarkSelectorScale + 0.25f));
                else
                    this.bookmarkSelectorScale = 0;

                if ((this.closestPlayerPos != null || fromItem)) {
                    if (!fromItem && (this.level.isClientSide ? this.degreesFloppedRender < 0.9f * 90 : this.floppedPercent < 0.9f)) {

                        Vec3 playerPos = this.closestPlayerPos;
                        double dx = playerPos.x - getBlockPos().getX() - 0.5f;
                        double dz = playerPos.z - getBlockPos().getZ() - 0.5f;
                        float yaw = 270f - (float) (Math.atan2(dz, dx) * (180 / Math.PI));
                        yaw = snapToCardinalDirection(yaw);

                        this.bookYawIncrement = updateIncrement(bookYaw, yaw, bookYawIncrement);
                        this.bookYaw = updateAngle(bookYaw, bookYawIncrement);

                    }
                    this.degreesFloppedTo = 0;
                    this.degreesFloppedSpeed = (3f + 6 * (Math.abs(floppedPercent - 0.66f))) / 90f / 2f;
                    this.degreesOpenedTo = 0;
                    this.degreesOpenedSpeed = (2f + 5 * (0.5f - Math.abs(0.5f - openedPercent))) / 90f / 2f;
//                    this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                } else {
                    this.degreesOpenedTo = 1;
                    this.degreesOpenedSpeed = (2f + 6 * (0.5f - Math.abs(0.5f - openedPercent))) / 90f / 2f;
                    this.degreesFloppedTo = 1;
                    this.degreesFloppedSpeed = (2f + 4 * (0.5f - Math.abs(0.5f - floppedPercent))) / 90 / 2f;
                }
                this.degreesSpun = this.bookYaw;
                this.floppedPercent = moveTo(this.floppedPercent, this.degreesFloppedTo, this.degreesFloppedSpeed);
                this.degreesFlopped = floppedPercent * 90;


//                if (this.floppedPercent < 1) {
//                    this.degreesOpenedTo = Mth.clamp((float) (Math.max(0, this.closestDist - 2) * (360 / Math.max(1, maxDist - 2))) / 4, 0.0f, 90f) / 90f;
//                    this.degreesOpenedSpeed = (2f + 5 * (0.5f - Math.abs(0.5f - openedPercent))) / 90f / 2f;
//                } else {
//                    this.degreesOpenedTo = 1;
//                    this.degreesOpenedSpeed = (2f + 6 * (0.5f - Math.abs(0.5f - openedPercent))) / 90f / 2f;
//                }

                this.openedPercent = moveTo(this.openedPercent, this.degreesOpenedTo, this.degreesOpenedSpeed);
                this.degreesOpened = easeOpened(openedPercent) * 90;

                if (this.turnPage == 1) {

                    if (this.pageOneRotation == 180) {
                        bookData = clickedNext(bookData, 1);
                        this.pageOneRotationRender = 0;
                        this.pageOneRotation = 0;
                        this.pageOneRotationTo = 0;
//                        this.pageOneRotationSpeed = 0;
                        this.turnPage = 0;
                        this.pageOneRotationLast = this.pageOneRotation;
//                        setChanged();
                    } else {
                        if (this.pageOneRotation == 0) {
                            if (fromItem) {
                                if (EffectiveSide.get().isClient())
                                    ClientSounds.playTurnPageSound();
                            } else if (!level.isClientSide) {
                                level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));
                            }
                        }

                        float f = (float) Math.sin(this.pageOneRotation / 180 * Math.PI);
                        this.pageOneRotationSpeed = (f * f * 35) + 10f;
                        this.pageOneRotationTo = (float) 180;
                    }
                } else if (this.turnPage == 2) {
                    if (this.pageTwoRotation == 180) {
                        bookData = clickedBack(bookData, 1);
                        this.pageTwoRotationRender = 0;
                        this.pageTwoRotation = 0;
                        this.pageTwoRotationTo = 0;
                        this.pageTwoRotationLast = this.pageTwoRotation;
//                        this.pageTwoRotationSpeed = 0;
                        this.turnPage = 0;
//                        setChanged();
                    } else {

                        if (this.pageTwoRotation == 0) {
                            if (fromItem) {
                                if (EffectiveSide.get().isClient())
                                    ClientSounds.playTurnPageSound();
                            } else if (!level.isClientSide) {
                                level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));
                            }
                        }

                        float f = (float) Math.sin(this.pageTwoRotation / 180 * Math.PI);
                        this.pageTwoRotationSpeed = (f * f * 35) + 10f;
                        this.pageTwoRotationTo = (float) 180;
//                        this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                    }
                } else if (this.turnPage == -1) {

                    BookEntries bookEntries = BookManager.getBookEntries(currentBook.getBook());
                    if (bookEntries != null) {
                        int chapter = bookData.getChapter();
                        int page = bookData.getPage();
                        int pageOnNum = bookEntries.chapterList.get(chapter).startPage + page;
                        if (this.turnToChapter >= bookEntries.chapterList.size())
                            this.turnToChapter = bookEntries.chapterList.size() - 1;
                        if (this.turnToPage >= bookEntries.chapterList.get(this.turnToChapter).pages.size())
                            this.turnToPage = bookEntries.chapterList.get(this.turnToChapter).pages.size() - 1;
                        int destPageNum = bookEntries.chapterList.get(this.turnToChapter).startPage + this.turnToPage;
                        int numPagesToDest = Math.abs(destPageNum - pageOnNum);
                        if (page % 2 == 1)
                            page--;

                        int pagesToTurn = numPagesToDest > 90 ? 13 : numPagesToDest > 75 ? 11 : numPagesToDest > 60 ? 9 : numPagesToDest > 45 ? 7 : numPagesToDest > 30 ? 5 : numPagesToDest > 15 ? 3 : 1;

                        if (chapter > this.turnToChapter || (chapter == this.turnToChapter && page > this.turnToPage)) {

                            if (this.pageTwoRotation == 180) {
                                bookData = clickedBack(bookData, pagesToTurn);
                                this.pageTwoRotation = 0;
                                this.pageTwoRotationRender = 0;
                                this.pageTwoRotationLast = this.pageTwoRotation;
                                this.pageTwoRotationTo = 0;
                                this.pageTwoRotationSpeed = 0.01f;
                            } else {
                                if (this.pageTwoRotation == 0 && numPagesToDest > 1) {

                                    if (fromItem) {
                                        if (EffectiveSide.get().isClient())
                                            ClientSounds.playTurnPageFastSound();
                                    } else if (!level.isClientSide) {
                                        level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.3f + 0.7f));
                                    }

                                }


                                float f = (1 + Math.min(numPagesToDest, 50) / 200f);
                                this.pageTwoRotationSpeed = 65 * f * f + 15;
                                this.pageTwoRotationTo = (float) 180;
                            }
                        }


                        if (chapter < this.turnToChapter || (chapter == this.turnToChapter && page < this.turnToPage)) {


                            if (this.pageOneRotation == 180) {
                                bookData = clickedNext(bookData, pagesToTurn);
                                this.pageOneRotation = 0;
                                this.pageOneRotationRender = 0;
                                this.pageOneRotationTo = 0;
                                this.pageOneRotationLast = this.pageOneRotation;
                                this.pageOneRotationSpeed = 0.01f;
                            } else {
                                if (this.pageOneRotation == 0 && numPagesToDest > 0) {

                                    if (fromItem) {
                                        if (EffectiveSide.get().isClient())
                                            ClientSounds.playTurnPageFastSound();
                                    } else if (!level.isClientSide) {
                                        level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.3f + 0.7f), (level.random.nextFloat() * 0.25f + 0.5f));
                                    }

                                }
                                float f = (1 + Math.min(numPagesToDest, 50) / 200f);
                                this.pageOneRotationSpeed = 65 * f * f + 15;
                                this.pageOneRotationTo = (float) 180;
                            }
                        }


                        if (chapter == this.turnToChapter && (page == this.turnToPage || page + 1 == turnToPage)) {
                            this.turnPage = 0;
                            this.pageTwoRotation = 0;
                            this.pageTwoRotationTo = 0;
                            this.pageTwoRotationRender = 0;
                            this.pageTwoRotationSpeed = 0.01f;
                            this.pageOneRotation = 0;
                            this.pageOneRotationTo = 0;
                            this.pageOneRotationRender = 0;
                            this.pageOneRotationSpeed = 0.01f;
                            this.pageTwoRotationLast = this.pageTwoRotation;
                            this.pageOneRotationLast = this.pageOneRotation;
                        }
                    }

                } else if (this.turnPage == 0) {
                    this.currentBook = this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
                    this.pageTwoRotation = 0;
                    this.pageTwoRotationTo = 0;
                    this.pageTwoRotationRender = 0;
                    this.pageTwoRotationSpeed = 0.01f;
                    this.pageOneRotation = 0;
                    this.pageOneRotationTo = 0;
                    this.pageOneRotationRender = 0;
                    this.pageOneRotationSpeed = 0.01f;
                    this.pageTwoRotationLast = this.pageTwoRotation;
                    this.pageOneRotationLast = this.pageOneRotation;
                }

                this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);


            } else {

                this.currentBook = this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
                this.degreesOpenedTo = 1;
                this.degreesOpenedSpeed = (2f + 6 * Math.abs(0.5f - openedPercent)) / 90f / 2f;
                this.openedPercent = moveTo(this.openedPercent, this.degreesOpenedTo, this.degreesOpenedSpeed);
                this.degreesOpened = easeOpened(openedPercent) * 90;
                if (this.openedPercent > 0.2f) {
                    this.degreesFloppedTo = 1;
                    this.degreesFloppedSpeed = (2f + 7 * (0.5f - Math.abs(0.5f - floppedPercent))) / 90f / 2f;
                }
                this.floppedPercent = moveTo(this.floppedPercent, this.degreesFloppedTo, this.degreesFloppedSpeed);
                this.degreesFlopped = floppedPercent * 90;
            }
            BookData bookData1 = (fromItem || this.level.isClientSide) ? this.currentBook : this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
            if (bookData1 != bookData) {
                if (!(fromItem || this.level.isClientSide)) {
                    ItemStack stack1 = this.itemHandler.getStackInSlot(0).copy();
                    stack1.set(ModDataComponents.BOOK, bookData);
                    this.itemHandler.setStackInSlot(0, stack1);
                } else {
                    this.currentBook = bookData;
                }
            }
        } else {
            this.currentBook = null;
            this.floppedPercent = 1;
            this.degreesFlopped = 90;
            this.degreesFloppedRender = 90;
            this.openedPercent = 1f;
            this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
            this.degreesOpenedRender = 90;
            this.degreesSpun = 0;
            this.degreesSpunRender = 0;
            this.degreesSpunTo = 0;
            this.pageOneRotation = 0;
            this.pageOneRotationTo = 0;
            this.pageOneRotationRender = 0;
            this.pageTwoRotation = 0;
            this.pageTwoRotationTo = 0;
            this.pageTwoRotationRender = 0;
        }
    }

    public void tickClient() {
        this.openedPercentOld = this.openedPercent;
        this.floppedPercentOld = this.floppedPercent;
        this.degreesSpunOld = this.degreesSpun;
        this.tooltipScaleOld = this.tooltipScale;
        this.buttonScaleOld = this.buttonScale;

        this.drawing.tick();

        if (this.drawTooltip) {
            this.tooltipScale = moveTo(this.tooltipScale, 1f, 0.075f);
        } else {
            this.tooltipScale = moveTo(this.tooltipScale, 0f, 0.15f);
        }

        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        BookData bookData = stack.get(ModDataComponents.BOOK);

        if (this.turnPage != 0 || (bookData != null && !bookData.isOpened())) {
            this.buttonScaleSpeed = 0.1f * (this.buttonScale + 0.25f);
            this.buttonScaleTo = 0;
        } else {
            this.buttonScaleSpeed = 0.15f * (this.buttonScale + 0.25f);
            this.buttonScaleTo = 1;
        }

        closestPlayerPos = null;
    }

    //    @Override
    public void tick() {
        if (level.isClientSide) {
            tickClient();
        }

        tickCandles();


        closestDist = maxDist;
        if (this.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem) {
            Player playerEntity = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), maxDist, false);
            if (playerEntity != null) {
                double dist = (getDistanceToEntity(playerEntity, this.worldPosition));
                if (dist < maxDist) {
                    if (dist < this.closestDist) {
                        this.closestDist = dist;
                        this.closestPlayerPos = playerEntity.position();
                        this.closestPlayer = playerEntity;
                    }
                }
            }
        }

        tickBook(this.itemHandler.getStackInSlot(0));


    }

    public float updateIncrement(float currentAngle, float targetAngle, float lastIncrement) {
        // Normalize angles to the range -270 to 90
        targetAngle = normalizeAngle(targetAngle);
        currentAngle = normalizeAngle(currentAngle);

        float angleDifference = targetAngle - currentAngle;

        // Calculate the shortest direction
        if (angleDifference > 180) {
            angleDifference -= 360;
        } else if (angleDifference < -180) {
            angleDifference += 360;
        }
        float distance = Math.abs(angleDifference);

        if (Mth.abs(lastIncrement) < 0.1f && distance < 0.9f)
            return 0;
        float adjustment = ((distance / 180f) * (distance / 180f) + 0.175f) * (angleDifference > 0 ? 1 : -1);
        if (Mth.abs(lastIncrement) < 0.8f && distance < 10f)
            return (lastIncrement + adjustment) * (0.72f + 0.20f * Mth.abs(lastIncrement) / 0.8f);

        return (lastIncrement + adjustment) * (0.92f);
    }

    public float updateAngle(float currentAngle, float maxIncrement) {
        // Normalize angles to the range -270 to 90
        currentAngle = normalizeAngle(currentAngle);

        currentAngle += maxIncrement;

        return normalizeAngle(currentAngle);
    }

    private float normalizeAngle(float angle) {
        while (angle > 90) {
            angle -= 360;
        }
        while (angle < -270) {
            angle += 360;
        }
        return angle;
    }

    public BookData clickedNext(BookData bookData, int pages) {
        if (bookData == null)
            return bookData;

        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
        if (bookEntries != null) {
            for (int i = 0; i < pages; i++) {
                int currentPage = bookData.getPage();
                int currentChapter = bookData.getChapter();
                if (currentPage < bookEntries.chapterList.get(currentChapter).pages.size() - (2)) {
                    bookData = bookData.setPage(currentPage + (2));
                    if (currentChapter < bookEntries.chapterList.size() - 1 && currentPage + (2) > bookEntries.chapterList.get(currentChapter).pages.size() - 1) {
                        bookData = bookData.setChapter(++currentChapter);
                        bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                    }
                } else {
                    if (currentChapter < bookEntries.chapterList.size() - 1) {
                        bookData = bookData.setChapter(++currentChapter);
                        bookData = bookData.setPage(0);
                    } else {
                        bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                    }
                }
            }
        }

        return bookData;
    }

    public BookData clickedBack(BookData bookData, int pages) {
        if (bookData == null)
            return bookData;

        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
//        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
//        BookData bookData = stack.get(ModDataComponents.BOOK);

        if (bookEntries != null) {
            for (int i = 0; i < pages; i++) {

                int currentPage = bookData.getPage();
                int currentChapter = bookData.getChapter();
                if (currentPage > 0) {

                    if (currentChapter > 0 && currentPage - (2) < 0) {
                        bookData = bookData.setChapter(--currentChapter);
                        bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                    } else {
                        bookData = bookData.setPage(Math.max(currentPage - (2), 0));
                    }

                } else {
                    if (currentChapter > 0) {
                        bookData = bookData.setChapter(--currentChapter);
                        bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                    } else {
                        bookData = bookData.setPage(0);
                    }
                }
            }
        }

        return bookData;
//        stack.set(ModDataComponents.BOOK, bookData);
//        this.itemHandler.setStackInSlot(0, stack);
    }

    public void forceTurnPage(int turnPage, int chapter, int page) {
        if (currentBook == null)
            return;

        if (turnPage == -2) {
            turnPage += 2;
            if (EffectiveSide.get().isClient())
                ClientSounds.playBookCloseSound();

            if (currentBook.isOpened())
                currentBook = currentBook.setOpened(false);

        }
        this.turnPage = turnPage;
        this.turnToChapter = chapter;
        this.turnToPage = page;

        this.pageOneRotationRender = 0;
        this.pageOneRotation = 0;
        this.pageOneRotationTo = 0;
        this.pageOneRotationLast = this.pageOneRotation;
        this.pageTwoRotationRender = 0;
        this.pageTwoRotation = 0;
        this.pageTwoRotationTo = 0;
        this.pageTwoRotationLast = this.pageTwoRotation;
    }

    public void setTurnPage(int turnPage, int chapter, int page) {


        if (this.fromItem) {
            forceTurnPage(turnPage, chapter, page);
            return;
        }

        if (level.isClientSide) {
            HexereiPacketHandler.sendToServer(new BookTurnPageToServer(this, turnPage, chapter, page));
            return;
        }
        else {
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookTurnPage(this, turnPage, chapter, page, bookData.getChapter(), bookData.getPage()));
        }

        this.turnToChapter = chapter;
        this.turnToPage = page;

        boolean flag = false;
        if (turnPage == -2) {
            turnPage += 2;
            flag = true;
        }

        if (flag) {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_CLOSE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));

            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);

            if (bookData != null && bookData.isOpened())
                bookData = bookData.setOpened(false);

            stack.set(ModDataComponents.BOOK, bookData);
            this.itemHandler.setStackInSlot(0, stack);

            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));

        }

        this.turnPage = turnPage;

    }

    public void setTurnPage(int turnPage) {
        if (turnPage == -1)
            setTurnPage(turnPage, 0, 0);
        else
            setTurnPage(turnPage, -1, -1);
    }

    public void forcePageBookmark(int chapter, int page) {

        if (EffectiveSide.get().isClient())
            ClientSounds.playBookmarkSound();
        if (this.currentBook != null) {
            BookEntries bookEntries = BookManager.getBookEntries(this.currentBook.getBook());
            if (bookEntries != null) {
                List<BookData.Bookmarks.Slot> slots = this.currentBook.getBookmarks().getSlots();
                boolean flag = false;
                BookData.Bookmarks.Slot firstEmpty = null;
                for (BookData.Bookmarks.Slot slot : slots) {
                    if (!slot.getId().isEmpty()) {
                        if (bookEntries.chapterList.get(chapter).pages.get(page).location.equals(slot.getId())) {
                            slot.setColor(DyeColor.byId(slot.getColor().getId() + 1 >= DyeColor.values().length ? 0 : slot.getColor().getId() + 1));
                            flag = true;
                            break;
                        }

                    } else if (firstEmpty == null) {
                        firstEmpty = slot;
                    }
                }
                if (!flag && firstEmpty != null) {
                    firstEmpty.setId(bookEntries.chapterList.get(chapter).pages.get(page).location);
                    firstEmpty.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
                }
            }
        }
    }

    public void clickPageBookmark(int chapter, int page) {

        if (this.fromItem) {
            forcePageBookmark(chapter, page);
            return;
        }

        if (level == null) return;

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkPageToServer(this, chapter, page));
        else {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_BUTTON.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
                if (bookEntries != null) {
                    List<BookData.Bookmarks.Slot> slots = bookData.getBookmarks().getSlots();
                    boolean flag = false;
                    BookData.Bookmarks.Slot firstEmpty = null;
                    for (BookData.Bookmarks.Slot slot : slots) {
                        if (!slot.getId().isEmpty()) {
                            if (bookEntries.chapterList.get(chapter).pages.get(page).location.equals(slot.getId())) {
                                slot.setColor(DyeColor.byId(slot.getColor().getId() + 1 >= DyeColor.values().length ? 0 : slot.getColor().getId() + 1));
                                flag = true;
                                break;
                            }

                        } else if (firstEmpty == null) {
                            firstEmpty = slot;
                        }
                    }
                    if (!flag && firstEmpty != null) {
                        firstEmpty.setId(bookEntries.chapterList.get(chapter).pages.get(page).location);
                        firstEmpty.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
                    }
                }
            }

            stack.set(ModDataComponents.BOOK, bookData);
            this.itemHandler.setStackInSlot(0, stack);

            HexereiPacketHandler.sendToNearbyClient(this.level, this.getBlockPos(), new BookSyncDataPacket(this.getBlockPos()));

            setChanged();
        }


    }

    public void forceSwapBookmarks(int slot1, int slot2) {
        if (EffectiveSide.get().isClient())
            ClientSounds.playBookmarkSwapSound();
        List<BookData.Bookmarks.Slot> slots = new ArrayList<>(currentBook.getBookmarks().getSlots());

        BookData.Bookmarks.Slot temp = slots.get(slot1).copyWithIndex(slot2);
        slots.set(slot1, slots.get(slot2).copyWithIndex(slot1));
        slots.set(slot2, temp);

        currentBook = currentBook.setBookmarks(new BookData.Bookmarks(slots));
    }

    public void swapBookmarks(int slot1, int slot2) {

        if (this.fromItem) {
            forceSwapBookmarks(slot1, slot2);
            return;
        }

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkSwapToServer(this, slot1, slot2));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_SWAP.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                List<BookData.Bookmarks.Slot> slots = new ArrayList<>(bookData.getBookmarks().getSlots());

                BookData.Bookmarks.Slot temp = slots.get(slot1).copyWithIndex(slot2);
                slots.set(slot1, slots.get(slot2).copyWithIndex(slot1));
                slots.set(slot2, temp);

                bookData = bookData.setBookmarks(new BookData.Bookmarks(slots));

                stack.set(ModDataComponents.BOOK, bookData);
                this.itemHandler.setStackInSlot(0, stack);

                HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
            }
            setChanged();
        }

    }

    public void forceDeleteBookmark(int slot1) {
        if (EffectiveSide.get().isClient())
            ClientSounds.playBookmarkDeleteSound();
        List<BookData.Bookmarks.Slot> slots = new ArrayList<>(currentBook.getBookmarks().getSlots());

        slots.set(slot1, new BookData.Bookmarks.Slot("", DyeColor.WHITE, slot1));

        currentBook = currentBook.setBookmarks(new BookData.Bookmarks(slots));


    }

    public void deleteBookmark(int slot1) {

        if (this.fromItem) {
            forceDeleteBookmark(slot1);
            return;
        }

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkDeleteToServer(this, slot1));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_DELETE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                List<BookData.Bookmarks.Slot> slots = new ArrayList<>(bookData.getBookmarks().getSlots());

                slots.set(slot1, new BookData.Bookmarks.Slot("", DyeColor.WHITE, slot1));

                bookData = bookData.setBookmarks(new BookData.Bookmarks(slots));

                stack.set(ModDataComponents.BOOK, bookData);
                this.itemHandler.setStackInSlot(0, stack);

                HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
            }
            setChanged();
        }


    }

    @Override
    public int getContainerSize() {
        return 0;
    }

}
