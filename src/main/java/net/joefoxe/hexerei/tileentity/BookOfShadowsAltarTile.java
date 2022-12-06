package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BookOfShadowsAltarTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {

//    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
//    protected NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    public final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public PageDrawing drawing;
    public float[] bookmarkHoverAmount = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public float degreesSpun;
    public float degreesSpunTo;
    public float degreesSpunRender;
    public float degreesSpunSpeed;
    public float degreesOpened;
    public float degreesOpenedTo;
    public float degreesOpenedRender;
    public float degreesOpenedSpeed;
    public float degreesFlopped;
    public float degreesFloppedTo;
    public float degreesFloppedRender;
    public float degreesFloppedSpeed;
    public int turnPage;
    public int turnToPage;
    public int turnToChapter;
    public float buttonScale;
    public float buttonScaleTo;
    public float buttonScaleRender;
    public float buttonScaleSpeed;
    public float bookmarkSelectorScale;
    public float pageOneRotation;
    public float pageTwoRotation;
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


    public BookOfShadowsAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);

        turnPage = 0;
        buttonScale = 1;
        buttonScaleTo = 1;
        buttonScaleRender = 1;
        buttonScaleSpeed = 0;
        bookmarkSelectorScale = 0;
        pageOneRotation = 0;
        pageOneRotationRender = 0;
        pageOneRotationTo = 0;
        pageOneRotationSpeed = 0;
        pageTwoRotation = 0;
        pageTwoRotationRender = 0;
        pageTwoRotationTo = 0;
        pageTwoRotationSpeed = 0;
        degreesFlopped = 90;
        degreesFloppedTo = 90;
        degreesFloppedSpeed = 0;
        degreesFloppedRender = 90;
        degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offseting 90 degrees from the start will close the book
        degreesOpenedTo = 90;
        degreesOpenedSpeed = 0;
        degreesOpenedRender = 90;
        degreesSpun = 0;
        degreesSpunTo = 0;
        degreesSpunSpeed = 0;
        degreesSpunRender = 0;
        candlePos1Slot = 0;
        candlePos2Slot = 0;
        candlePos3Slot = 0;
        drawing = new PageDrawing();
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
                if(!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
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

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt)
    {
        this.deserializeNBT(pkt.getTag());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public void sync() {
        if (this.level != null) {
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

            if(this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }

    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inv", itemHandler.serializeNBT());
        tag.putInt("turnPage", this.turnPage);
        tag.putInt("turnToPage", this.turnToPage);
        tag.putInt("turnToChapter", this.turnToChapter);
        tag.putFloat("degreesSpun", this.degreesSpun);
        tag.putFloat("degreesFlopped", this.degreesFlopped);
        tag.putFloat("degreesOpened", this.degreesOpened);
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", itemHandler.serializeNBT());
        tag.putInt("turnPage", this.turnPage);
        tag.putInt("turnToPage", this.turnToPage);
        tag.putInt("turnToChapter", this.turnToChapter);
        tag.putFloat("degreesSpun", this.degreesSpun);
        tag.putFloat("degreesFlopped", this.degreesFlopped);
        tag.putFloat("degreesOpened", this.degreesOpened);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        itemHandler.deserializeNBT(compoundTag.getCompound("inv"));
        this.turnPage = compoundTag.getInt("turnPage");
        this.turnToPage = compoundTag.getInt("turnToPage");
        this.turnToChapter = compoundTag.getInt("turnToChapter");
        this.degreesSpun = compoundTag.getFloat("degreesSpun");
        this.degreesSpunRender = degreesSpun;
        this.degreesFlopped = compoundTag.getFloat("degreesFlopped");
        this.degreesFloppedRender = degreesFlopped;
        this.degreesOpened = compoundTag.getFloat("degreesOpened");
        this.degreesOpenedRender = degreesOpened;
    }
    public int interact(Player player, InteractionHand handIn) {
        if(!player.isShiftKeyDown()) {
            if(this.itemHandler.getStackInSlot(0).isEmpty()){
                if (!player.getItemInHand(handIn).isEmpty()) {
                    Random rand = new Random();
                    if (this.itemHandler.getStackInSlot(0).isEmpty()) {
                        this.itemHandler.setStackInSlot(0, player.getItemInHand(handIn));
                        level.playSound((Player) null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                        player.setItemInHand(handIn, ItemStack.EMPTY);
                        if(this.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem book){

                            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
                            this.turnToChapter = tag.getInt("chapter");
                            this.turnToPage = tag.getInt("page");
                            this.closestDist = (getDistanceToEntity(player, this.worldPosition));
                            this.closestPlayerPos = player.position();
                            this.closestPlayer = player;
                            this.degreesSpun = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunTo = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunRender = 270 - getAngle(this.closestPlayerPos);

                            setChanged();
                        }
                        return 1;
                    }
                }
            }
            else
            {
                if(this.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem){
                    CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
                    if(!tag.getBoolean("opened") && this.degreesOpened == 90){

                        level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_OPENING.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
                        tag.putBoolean("opened", !tag.getBoolean("opened"));
                        setChanged();
                        return 1;
                    }
                }
            }
        }
        else if(!this.itemHandler.getStackInSlot(0).isEmpty())
        {

            setChanged();

            if(player.getMainHandItem().isEmpty())
                player.setItemInHand(InteractionHand.MAIN_HAND ,this.itemHandler.getStackInSlot(0).copy());
            else
                player.inventory.placeItemBackInInventory(this.itemHandler.getStackInSlot(0).copy());

            this.degreesFlopped = 90;
            this.degreesFloppedRender = 90;
            this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offseting 90 degrees from the start will close the book
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

            level.playSound((Player) null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

            setChanged();

            return 1;
        }

        return 0;
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return super.getModelData();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
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
        for(int i = 0; i < this.itemHandler.getSlots(); i++)
            items.set(i, this.itemHandler.getStackInSlot(i));
        return items;
    }

    @Override
    public ItemStack removeItem(int p_59613_, int p_59614_) {
        this.unpackLootTable((Player)null);
        ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemHandler.getSlots() && !this.itemHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0 ? this.getItems().get(p_59613_).split(p_59614_) : ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_59630_) {
        this.unpackLootTable((Player)null);
        if(p_59630_ >= 0 && p_59630_ < this.itemHandler.getSlots())
        {
            this.itemHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
            return this.itemHandler.getStackInSlot(p_59630_);

        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int p_59611_) {
        this.unpackLootTable((Player)null);
        return this.itemHandler.getStackInSlot(p_59611_);
    }

    @Override
    public void setItem(int p_59616_, ItemStack p_59617_) {
        this.unpackLootTable((Player)null);
        this.itemHandler.setStackInSlot(p_59616_, p_59617_);
        if (p_59617_.getCount() > this.getMaxStackSize()) {
            p_59617_.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        for(int i = 0; i <  Math.min(itemsIn.size(), this.itemHandler.getSlots()); i++)
            this.itemHandler.setStackInSlot(i, itemsIn.get( i));
    }


    @Override
    public void clearContent() {
        super.clearContent();
//        this.items.clear();

        for(int i = 0; i < this.itemHandler.getSlots(); i++)
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
    }

    public BookOfShadowsAltarTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(),blockPos, blockState);
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

    @Override
    public AABB getRenderBoundingBox() {
        AABB aabb = super.getRenderBoundingBox().inflate(5, 5, 5);
        return aabb;
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

    private float moveToAngle(float input, float movedTo, float speed)
    {
        float distance = movedTo - input;

        if(Math.abs(distance) <= speed)
        {
            return movedTo;
        }

        if(distance > 0)
        {
            if(Math.abs(distance) < 180)
                input += speed;
            else
                input -= speed;
        } else {
            if(Math.abs(distance) < 180)
                input -= speed;
            else
                input += speed;
        }

        if(input < -90){
            input += 360;
        }
        if(input > 270)
            input -= 360;

        return input;
    }

    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5f, pos.x() - this.worldPosition.getX() - 0.5f));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    private boolean getCandle(Level world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof CandleTile;
    }

//    @Override
    public void tick() {
        if(level.isClientSide) {
//              used for testing positioning on the book pages
////            for(int i = 0; i< 10; i++){
//            {
//                float xIn = 0.75f;
//                float yIn = 0.25f;
//
//
//
//                Vector3f vector3f = new Vector3f(0, 0, 0);
//                Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
//
//                BlockPos blockPos = this.getBlockPos();
//
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + this.degreesOpened / 1.12f));
//                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//
////                Vector3f vector3f = new Vector3f(0, 0, 0);
////                Vector3f vector3f_1 = new Vector3f(-0.05f + -xIn * 0.06f, 0.5f - yIn * 0.061f, -0.02f);
////
////                BlockPos blockPos = this.getBlockPos();
////
////                vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + this.degreesOpened / 1.12f)));
////                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//                vector3f.add(vector3f_1);
//
//                vector3f.transform(Vector3f.YP.rotationDegrees(this.degreesSpun));
//
//                Vec3 vec = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), blockPos.getY() + 18 / 16f, blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                Vec3 vec2 = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), vector3f.y() + blockPos.getY() + 18 / 16f, vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//            }
//
//
//
//
//            {
//                float xIn = 0.75f;
//                float yIn = 0.25f;
//
//                Vector3f vector3f = new Vector3f(0, 0, 0);
//                Vector3f vector3f_1 = new Vector3f(-0.05f + -xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
//
//                BlockPos blockPos = this.getBlockPos();
//
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + this.degreesOpened / 1.12f)));
//                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//                vector3f.add(vector3f_1);
//
//                vector3f.transform(Vector3f.YP.rotationDegrees(this.degreesSpun));
//
//                Vec3 vec = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), blockPos.getY() + 18 / 16f, blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                Vec3 vec2 = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), vector3f.y() + blockPos.getY() + 18 / 16f, vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//            }
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec.x, vec.y, vec.z, 0, 0, 0);
//            }

            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();

            if(this.turnPage != 0 || (tag.contains("opened") && !tag.getBoolean("opened"))) {
                this.buttonScaleSpeed = 0.1f * (this.buttonScale + 0.25f);
                this.buttonScaleTo = 0;
            }
            else {
                this.buttonScaleSpeed = 0.15f * (this.buttonScale + 0.25f);
                this.buttonScaleTo = 1;
            }



            tickCount++;

            closestPlayerPos = null;
            numberOfCandles = 0;

            candlePos1 = new BlockPos(0, 0, 0);
            candlePos2 = new BlockPos(0, 0, 0);
            candlePos3 = new BlockPos(0, 0, 0);

            for(int k = -1; k <= 1; ++k) {
                for(int l = -1; l <= 1; ++l) {
                    if ((k != 0 || l != 0) && level.isEmptyBlock(worldPosition.offset(l, 0, k)) && level.isEmptyBlock(worldPosition.offset(l, 1, k))) {
                        if((level.getBlockEntity(worldPosition.offset(l * 2, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                        {
                                for (int i = 0; i < candleTile.numberOfCandles; i++) {

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
                        if((level.getBlockEntity(worldPosition.offset(l * 2, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                        {

                            for(int i = 0; i < candleTile.numberOfCandles; i++) {

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

                            if((level.getBlockEntity(worldPosition.offset(l * 2, 0, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                            {

                                for(int i = 0; i < candleTile.numberOfCandles; i++) {

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
                            if((level.getBlockEntity(worldPosition.offset(l * 2, 1, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                            {

                                for(int i = 0; i < candleTile.numberOfCandles; i++) {

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
                            if((level.getBlockEntity(worldPosition.offset(l, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                            {

                                for(int i = 0; i < candleTile.numberOfCandles; i++) {

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
                            if((level.getBlockEntity(worldPosition.offset(l, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles)
                            {

                                for(int i = 0; i < candleTile.numberOfCandles; i++) {

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

            if(numberOfCandles >= 1)
            {
                degreesSpunCandles = moveToAngle(degreesSpunCandles, degreesSpunCandles + 1, 0.025f);

                if(candlePos1Slot == 0) {

                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).x = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).x, (worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).y = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).y, (worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).z = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).z, (worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(0).z) / 3f));
                }
                if(candlePos1Slot == 1) {
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).x = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).x, (worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).y = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).y, (worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).z = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).z, (worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(1).z) / 3f));
                }
                if(candlePos1Slot == 2) {
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).x = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).x, (worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).y = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).y, (worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).z = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).z, (worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(2).z) / 3f));
                }
                if(candlePos1Slot == 3) {
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).x = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).x, (worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos1.getX() + (float)Math.sin(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).y = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).y, (worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos1.getY() + 1f + (float)Math.sin(Hexerei.getClientTicks()/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).z = moveTo(((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).z, (worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos1.getZ() + (float)Math.cos(degreesSpunCandles) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos1)).candles.get(3).z) / 3f));
                }
            }
            if(numberOfCandles >= 2)
            {
                if(candlePos2Slot == 0) {
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).x = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).x, (worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).y = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).y, (worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).z = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).z, (worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(0).z) / 3f));
                }
                if(candlePos2Slot == 1) {
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).x = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).x, (worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).y = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).y, (worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).z = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).z, (worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(1).z) / 3f));
                }
                if(candlePos2Slot == 2) {
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).x = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).x, (worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).y = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).y, (worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).z = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).z, (worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(2).z) / 3f));
                }
                if(candlePos2Slot == 3) {
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).x = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).x, (worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos2.getX() + (float)Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).y = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).y, (worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos2.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 10)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).z = moveTo(((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).z, (worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos2.getZ() + (float)Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI*2f/3f)) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos2)).candles.get(3).z) / 3f));
                }
            }
            if(numberOfCandles >= 3)
            {
                if(candlePos3Slot == 0) {
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).x = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).x, (worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).y = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).y, (worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).z = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).z, (worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(0).z) / 3f));
                }
                if(candlePos3Slot == 1) {
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).x = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).x, (worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).y = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).y, (worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).z = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).z, (worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(1).z) / 3f));
                }
                if(candlePos3Slot == 2) {
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).x = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).x, (worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).y = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).y, (worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).z = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).z, (worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(2).z) / 3f));
                }
                if(candlePos3Slot == 3) {
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).returnToBlock = false;
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).x = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).x, (worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getX() - candlePos3.getX() + (float)Math.sin(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).x) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).y = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).y, (worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10), 0.02f + 0.08f * (Math.abs((worldPosition.getY() - candlePos3.getY() + 1f + (float)Math.sin((Hexerei.getClientTicks() + 20)/20f)/10) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).y) / 3f));
                    ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).z = moveTo(((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).z, (worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + Math.PI*2f/3f*2f) * 1.25f), 0.02f + 0.20f * (Math.abs((worldPosition.getZ() - candlePos3.getZ() + (float)Math.cos(degreesSpunCandles + (Math.PI*2f/3f*2f) * 1.25f) * 1.25f) - ((CandleTile) level.getBlockEntity(candlePos3)).candles.get(3).z) / 3f));
                }
            }



        }



        closestDist = maxDist;
        Item item = this.itemHandler.getStackInSlot(0).getItem();
//        System.out.println(level.isClientSide ? "Client - " + item : "Server - " + item);
        if(item instanceof HexereiBookItem){
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


//            System.out.println(level.isClientSide ? "Client - " + this.turnPage : "Server - " + this.turnPage);
//            System.out.println(level.isClientSide ? "Client - " + this.pageOneRotation : "Server - " + this.pageOneRotation);
//            System.out.println(this.pageOneRotation);
//            System.out.println(this.pageTwoRotation);
//            System.out.println("---");
//            System.out.println(this.slotClickedTick);

            if(slotClicked != -1)
                this.slotClickedTick++;

            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();

            if(tag.getBoolean("opened")){


                this.buttonScale = moveTo(this.buttonScale, this.buttonScaleTo, this.buttonScaleSpeed);
                this.buttonScaleRender = this.buttonScale;

                if(this.slotClicked != -1 && this.slotClickedTick > 5)
                    this.bookmarkSelectorScale = moveTo(this.bookmarkSelectorScale, 1, 0.15f * (this.bookmarkSelectorScale + 0.25f));
                else
                    this.bookmarkSelectorScale = 0;

                if (this.closestPlayerPos != null) {
                    if (this.degreesFlopped == 0) {
                        this.degreesSpunTo = 270 - getAngle(this.closestPlayerPos);
                        this.degreesSpunSpeed = 2.22f;
                        this.degreesSpun = moveToAngle(this.degreesSpun, this.degreesSpunTo, this.degreesSpunSpeed);
                    }
                    this.degreesFloppedTo = 0;
                    this.degreesFloppedSpeed = 3f + 6 * (Math.abs(degreesFlopped - 60)) / 90;
                    this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                } else {
                    if (this.degreesOpened == 90) {
                        this.degreesFloppedTo = 90;
                        this.degreesFloppedSpeed = 2f + 4 * (45 - Math.abs(45 - degreesFlopped)) / 90;
                        this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                    }
                }

                if (this.degreesFlopped == 0) {
                    this.degreesOpenedTo = (float) (this.closestDist * (360 / maxDist)) / 4;
                    this.degreesOpenedSpeed = 1f + 5 * (45 - Math.abs(45 - degreesOpened)) / 90;
                }
                else {
                    this.degreesOpenedTo = 90;
                    this.degreesOpenedSpeed = 1f + 6 * (45 - Math.abs(45 - degreesOpened)) / 90;
                }
                this.degreesOpened = moveTo(this.degreesOpened, this.degreesOpenedTo, this.degreesOpenedSpeed);

                if(this.turnPage == 1)
                {

                    if(this.pageOneRotation == 180)
                    {
                        clickedNext(this, 1);
                        this.pageOneRotationRender = 0;
                        this.pageOneRotation = 0;
                        this.pageOneRotationTo = 0;
//                        this.pageOneRotationSpeed = 0;
                        this.turnPage = 0;
//                        setChanged();
                    }else{
                        if (this.pageOneRotation == 0 && !level.isClientSide)
                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));

                        this.pageOneRotationSpeed = ((float) Math.sin(this.pageOneRotation / 180 * Math.PI) * (float) Math.sin(this.pageOneRotation / 180 * Math.PI) * 15) + 4.25f;
                        this.pageOneRotationTo = (float) 180;
                        this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                    }
                }
                if(pageOneRotationTo == 0)
                    this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                if(this.turnPage == 2)
                {
                    if(this.pageTwoRotation == 180)
                    {
                        clickedBack(this, 1);
                        this.pageTwoRotationRender = 0;
                        this.pageTwoRotation = 0;
                        this.pageTwoRotationTo = 0;
//                        this.pageTwoRotationSpeed = 0;
                        this.turnPage = 0;
//                        setChanged();
                    }
                    else {

                        if (this.pageTwoRotation == 0 && !level.isClientSide)
                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));

                        this.pageTwoRotationSpeed = ((float) Math.sin(this.pageTwoRotation / 180 * Math.PI) * (float) Math.sin(this.pageTwoRotation / 180 * Math.PI) * 15) + 4.25f;
                        this.pageTwoRotationTo = (float) 180;
                        this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                    }
                }
                if(pageTwoRotationTo == 0)
                    this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                if(this.turnPage == -1)
                {

                    CompoundTag tag2 = this.itemHandler.getStackInSlot(0).getOrCreateTag();
                    if(tag2.contains("chapter")) {
                        BookEntries bookEntries = BookManager.getBookEntries();
                        int chapter = tag2.getInt("chapter");
                        int page = tag2.getInt("page");
                        int pageOnNum = bookEntries.chapterList.get(chapter).startPage + page;
                        int destPageNum = bookEntries.chapterList.get(this.turnToChapter).startPage + this.turnToPage;
                        int numPagesToDest = Math.abs(destPageNum - pageOnNum);
                        if(page % 2 == 1)
                            page--;

                        if (chapter > this.turnToChapter || (chapter == this.turnToChapter && page > this.turnToPage)) {


                            if(this.pageTwoRotation == 180)
                            {
                                clickedBack(this, numPagesToDest > 100 ? 3 : numPagesToDest > 50 ? 2 : 1);
                                this.pageTwoRotation = 0;
                                this.pageTwoRotationRender = 0;
                                this.pageTwoRotationTo = 0;
                                this.pageTwoRotationSpeed = 0.01f;
                            }else{
                                if (this.pageTwoRotation == 0 && !level.isClientSide && numPagesToDest > 1) {
                                    level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.3f + 0.7f));
                                }

                                this.pageTwoRotationSpeed = (((float) Math.sin(this.pageTwoRotation / 180 * Math.PI) * 50 + 15)) * (1 + Math.min(numPagesToDest, 50) / 90f) * (1 + Math.min(numPagesToDest, 50) / 90f);
                                this.pageTwoRotationTo = (float) 180;
                                this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                            }
                        }



                        if (chapter < this.turnToChapter || (chapter == this.turnToChapter && page < this.turnToPage)) {


                            if(this.pageOneRotation == 180)
                            {
                                clickedNext(this, numPagesToDest > 100 ? 3 : numPagesToDest > 50 ? 2 : 1);
                                this.pageOneRotation = 0;
                                this.pageOneRotationRender = 0;
                                this.pageOneRotationTo = 0;
                                this.pageOneRotationSpeed = 0.01f;
                            }else{
                                if (this.pageOneRotation == 0 && !level.isClientSide && numPagesToDest > 0) {
                                    level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.3f + 0.7f));
                                }

                                this.pageOneRotationSpeed = (((float) Math.sin(this.pageOneRotation / 180 * Math.PI) * 50 + 15)) * (1 + Math.min(numPagesToDest, 50) / 90f) * (1 + Math.min(numPagesToDest, 50) / 90f);
                                this.pageOneRotationTo = (float) 180;
                                this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                            }
                        }


                        if(chapter == this.turnToChapter && (page == this.turnToPage || page + 1 == turnToPage)){
                            this.turnPage = 0;
                            this.pageTwoRotation = 0;
                            this.pageTwoRotationTo = 0;
                            this.pageTwoRotationRender = 0;
                            this.pageTwoRotationSpeed = 0.01f;
                            this.pageOneRotation = 0;
                            this.pageOneRotationTo = 0;
                            this.pageOneRotationRender = 0;
                            this.pageOneRotationSpeed = 0.01f;
//                            setChanged();
                        }

                    }
                }


            }
            else{
                this.degreesOpenedTo = 90;
                this.degreesOpenedSpeed = 2f + 6 * (Math.abs(45 - degreesOpened)) / 90;
                this.degreesOpened = moveTo(this.degreesOpened, this.degreesOpenedTo, this.degreesOpenedSpeed);
                if (this.degreesOpened == 90) {
                    this.degreesFloppedTo = 90;
                    this.degreesFloppedSpeed = 2f + 7 * (45 - Math.abs(45 - degreesFlopped)) / 90;
                    this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                }
            }
        } else {
            this.degreesFlopped = 90;
            this.degreesFloppedRender = 90;
            this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offseting 90 degrees from the start will close the book
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
//            this.turnPage = 0;
//            this.turnToPage = 0;
//            this.turnToChapter = 0;
        }


    }

    public void clickedNext(BookOfShadowsAltarTile altarTile, int pages){
        CompoundTag tag2 = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();
        if(BookManager.getBookEntries() != null){
            for (int i = 0; i < pages; i++) {
                int currentPage = tag2.getInt("page");
                int currentChapter = tag2.getInt("chapter");
                if (currentPage < BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - (2)) {
                    tag2.putInt("page", currentPage + (2));
                    if (currentChapter < BookManager.getBookEntries().chapterList.size() - 1 && currentPage + (2) > BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1) {
                        tag2.putInt("chapter", ++currentChapter);
                        tag2.putInt("page", BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    }
                } else {
                    if (currentChapter < BookManager.getBookEntries().chapterList.size() - 1) {
                        tag2.putInt("chapter", ++currentChapter);
                        tag2.putInt("page", 0);
                    } else {
                        tag2.putInt("page", BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    }
                }
            }
        }
    }
    public void clickedBack(BookOfShadowsAltarTile altarTile, int pages){
        CompoundTag tag2 = altarTile.itemHandler.getStackInSlot(0).getOrCreateTag();
        if(BookManager.getBookEntries() != null){
            for (int i = 0; i < pages; i++) {
                int currentPage = tag2.getInt("page");
                int currentChapter = tag2.getInt("chapter");
                if (currentPage > 0) {

                    if (currentChapter > 0 && currentPage - (2) < 0) {
                        tag2.putInt("chapter", --currentChapter);
                        tag2.putInt("page", BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    } else
                        tag2.putInt("page", Math.max(currentPage - (2), 0));

                } else {
                    if (currentChapter > 0) {
                        tag2.putInt("chapter", --currentChapter);
                        tag2.putInt("page", BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    } else {
                        tag2.putInt("page", 0);
                    }
                }
            }
        }
    }
//bookNonItemTooltip.hyperlink_page

    public void setTurnPage(int turnPage, int chapter, int page) {

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookTurnPageToServer(this, turnPage, chapter, page));
        else {
            setChanged();
        }

        this.turnToChapter = chapter;
        this.turnToPage = page;

        boolean flag = false;
        if (turnPage == -2) {
            turnPage += 2;
            flag = true;
        }

        if(flag) {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_CLOSE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
            if(tag.contains("opened"))
                tag.putBoolean("opened", false);
        }

        this.turnPage = turnPage;

    }
    public void setTurnPage(int turnPage) {

        setTurnPage(turnPage, -1, -1);

    }
    public void clickPageBookmark(int chapter, int page) {


        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkPageToServer(this, chapter, page));
        else {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_BUTTON.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
            if(tag.contains("bookmarks")) {
                CompoundTag bookmarks = tag.getCompound("bookmarks");

                boolean flag = false;
                int firstEmpty = 20;
                for(int i = 0; i < 20; i++){
                    if(bookmarks.contains("slot_" + i)){
                        CompoundTag slot = bookmarks.getCompound("slot_" + i);
                        int bookmark_color = slot.getInt("color");
                        int bookmark_chapter = slot.getInt("chapter");
                        int bookmark_page = slot.getInt("page");

                        if(chapter == bookmark_chapter && (page == bookmark_page || page - 1 == bookmark_page) && !flag){
                            slot.putInt("color", bookmark_color + 1 > 15 ? 0 : bookmark_color + 1);
                            //change color of bookmark
                            flag = true;
                        }
                    }else if(firstEmpty > i)
                        firstEmpty = i;
                }
                if(!flag){
                    //add new bookmark
                    CompoundTag new_bookmark = new CompoundTag();
                    new_bookmark.putInt("chapter", chapter);
                    new_bookmark.putInt("page", page);
                    new_bookmark.putInt("color", new Random().nextInt(16));
                    //find first empty slot
                    bookmarks.put("slot_" + firstEmpty, new_bookmark);

                }
            }
            setChanged();
        }



    }
    public void swapBookmarks(int slot1, int slot2) {


        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkSwapToServer(this, slot1, slot2));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_SWAP.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
            if(tag.contains("bookmarks")) {
                CompoundTag bookmarks = tag.getCompound("bookmarks");

                if(bookmarks.contains("slot_" + slot1)) {

                    CompoundTag slotTag = bookmarks.getCompound("slot_" + slot1);
                    CompoundTag slotTag_temp = slotTag.copy();
                    CompoundTag slot2Tag = null;

                    if(bookmarks.contains("slot_" + slot2))
                        slot2Tag = bookmarks.getCompound("slot_" + slot2);

                    if(slot2Tag != null){
                        bookmarks.put("slot_" + slot1, slot2Tag.copy());
                    }
                    else {
                        bookmarks.remove("slot_" + slot1);
                    }

                    bookmarks.put("slot_" + slot2, slotTag_temp);

//                    int bookmark_color = slotTag.getInt("color");
//                    int bookmark_chapter = slotTag.getInt("chapter");
//                    int bookmark_page = slotTag.getInt("page");
//
//                    if (chapter == bookmark_chapter && (page == bookmark_page || page - 1 == bookmark_page) && !flag) {
//                        System.out.println("change bookmark color");
//                        slotTag.putInt("color", bookmark_color + 1 > 15 ? 0 : bookmark_color + 1);
//                        //change color of bookmark
//                        flag = true;
//                    }
                }

            }
            setChanged();
        }



    }
    public void deleteBookmark(int slot1) {


        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkDeleteToServer(this, slot1));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_DELETE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
            CompoundTag tag = this.itemHandler.getStackInSlot(0).getOrCreateTag();
            if(tag.contains("bookmarks")) {
                CompoundTag bookmarks = tag.getCompound("bookmarks");

                if(bookmarks.contains("slot_" + slot1)) {

                    bookmarks.remove("slot_" + slot1);
                }

            }
            setChanged();
        }



    }

    @Override
    public int getContainerSize() {
        return 0;
    }

//    @Override
//    public int getContainerSize() {
//        return 0;
//    }

}
