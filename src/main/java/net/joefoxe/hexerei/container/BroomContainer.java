package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;


public class BroomContainer extends AbstractContainerMenu implements HasCustomInventoryScreen {
    private final Player playerEntity;
    public final BroomEntity broomEntity;
    private final IItemHandler playerInventory;
    private final IItemHandler playerEnderInventory;
    public static final int OFFSET = 34;

    public boolean isEnder;
    public ItemStack satchel;


//    public BroomContainer(int windowId, BroomEntity broom, Inventory inv) {
//        this(windowId, inv, new SimpleContainer(5));
//    }

    public BroomContainer(int windowId, BroomEntity broomEntity, Inventory playerInventory, Player player, boolean isEnder) {
        super(ModContainers.BROOM_CONTAINER.get(), windowId);
        this.broomEntity = broomEntity;
        broomEntity.startOpen(player);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.playerEnderInventory = new InvWrapper(player.getEnderChestInventory());
        this.isEnder = isEnder;
        this.satchel = broomEntity.itemHandler.getStackInSlot(1);

        int offset = 0;
        broomEntity.sync();
        if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.SMALL_SATCHELS))
            offset = 21;
        if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.MEDIUM_SATCHELS))
            offset = 42;
        if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.LARGE_SATCHELS))
            offset = 63;
        layoutPlayerInventorySlots(11, 106 + offset - OFFSET);

        //add slots for mixing cauldron
        if (broomEntity != null) {
            broomEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {

                addSlot(new SlotItemHandler(h, 0, 37, 47 - OFFSET) {

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(HexereiTags.Items.BROOM_MISC);
                    }
                });

                //satchel slots
                addSlot(new SlotItemHandler(h, 1, 99, 47 - OFFSET) {

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(HexereiTags.Items.SMALL_SATCHELS) || stack.is(HexereiTags.Items.MEDIUM_SATCHELS) || stack.is(HexereiTags.Items.LARGE_SATCHELS);
                    }

                    @Override
                    public boolean mayPickup(Player playerIn) {

                        if (broomEntity.isEnder())
                            return true;

                        ItemStack satchel = broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
                        if (satchel.is(HexereiTags.Items.SMALL_SATCHELS)) {
                            return broomEntity.getSatchelSlots(9).stream().allMatch(ItemStack::isEmpty);
                        }
                        if (satchel.is(HexereiTags.Items.MEDIUM_SATCHELS)) {
                            return broomEntity.getSatchelSlots(18).stream().allMatch(ItemStack::isEmpty);
                        }
                        if (satchel.is(HexereiTags.Items.LARGE_SATCHELS)) {
                            return broomEntity.getSatchelSlots(27).stream().allMatch(ItemStack::isEmpty);
                        }

                        return true;
                    }
                });

                //brush slot
                addSlot(new SlotItemHandler(h, 2, 160, 47 - OFFSET) {

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(HexereiTags.Items.BROOM_BRUSH);
                    }
                });

                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        addSlot(new SlotItemHandler(h, 3 + (i * 9) + j, 15 + 21 * j, 21 * i + 82 - OFFSET) {
                            @Override
                            public boolean mayPlace(@NotNull ItemStack stack) {
                                return !(stack.is(ModItems.WILLOW_BROOM.get()) || stack.is(ModItems.MAHOGANY_BROOM.get()));
                            }
                        });
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        addSlot(new SlotItemHandler(playerEnderInventory, (i * 9) + j, 15 + 21 * j, 21 * i + 82 - OFFSET) {
                            @Override
                            public boolean mayPlace(@NotNull ItemStack stack) {
                                return true;
                            }
                        });

                int offset2 = 0;
                if (!broomEntity.isEnder()) {
                    if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
                        offset2 = 21;

                        for (int i = 0; i < 1; i++)
                            for (int j = 0; j < 9; j++)
                                this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;


                        for (int i = 1; i < 3; i++)
                            for (int j = 0; j < 9; j++)
                                this.slots.get(39 + (i * 9) + j).y = -999;
                    }
                    if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
                        offset2 = 42;

                        for (int i = 0; i < 2; i++)
                            for (int j = 0; j < 9; j++)
                                this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;


                        for (int i = 2; i < 3; i++)
                            for (int j = 0; j < 9; j++)
                                this.slots.get(39 + (i * 9) + j).y = -999;
                    }
                    if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
                        offset2 = 63;

                        for (int i = 0; i < 3; i++)
                            for (int j = 0; j < 9; j++)
                                this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;

                    }
                    for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 9; j++)
                            this.slots.get(39 + (i * 9) + j + 27).y = -999;
                } else {

                    offset2 = 63;

                    for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 9; j++)
                            this.slots.get(39 + (i * 9) + j + 27).y = 21 * i + 82 - OFFSET;
                    for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 9; j++)
                            this.slots.get(39 + (i * 9) + j).y = -999;
                }

                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get((i * 9) + j).y = 106 + (i * 18) + offset2 - OFFSET;

                for (int k = 0; k < 9; k++)
                    this.slots.get(27 + k).y = 106 + 58 + offset2 - OFFSET;


                if (offset2 == 0) {
                    for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 9; j++)
                            this.slots.get(39 + (i * 9) + j).y = -999;
                }
            });
        }

        addDataSlot(new DataSlot() {
            @Override
            public void set(int value) {
                broomEntity.setFloatMode(value != 0);
            }

            @Override
            public int get() {
                return broomEntity.getFloatMode() ? 1 : 0;
            }
        });


    }


    public void removed(Player pPlayer) {
        if (pPlayer instanceof ServerPlayer) {
            ItemStack itemstack = this.getCarried();
            if (!itemstack.isEmpty()) {
                if (pPlayer.isAlive() && !((ServerPlayer) pPlayer).hasDisconnected()) {
                    pPlayer.getInventory().placeItemBackInInventory(itemstack);
                } else {
                    pPlayer.drop(itemstack, false);
                }

                this.setCarried(ItemStack.EMPTY);
            }
        }
        this.broomEntity.stopOpen(pPlayer);

    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
        int offset = 0;

        if (satchel != broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL)) {

            if (!satchel.isEmpty())
                playSoundClose(isEnder);
            else
                playSoundOpen(broomEntity.isEnder());

            satchel = broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
            isEnder = broomEntity.isEnder();
        }

        if (!broomEntity.isEnder()) {
            if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
                offset = 21;

                for (int i = 0; i < 1; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;


                for (int i = 1; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get(39 + (i * 9) + j).y = -999;
            }
            if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
                offset = 42;

                for (int i = 0; i < 2; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;


                for (int i = 2; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get(39 + (i * 9) + j).y = -999;
            }
            if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
                offset = 63;

                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 9; j++)
                        this.slots.get(39 + (i * 9) + j).y = 21 * i + 82 - OFFSET;

            }
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 9; j++)
                    this.slots.get(39 + (i * 9) + j + 27).y = -999;
        } else {

            offset = 63;

            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 9; j++)
                    this.slots.get(39 + (i * 9) + j + 27).y = 21 * i + 82 - OFFSET;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 9; j++)
                    this.slots.get(39 + (i * 9) + j).y = -999;
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.slots.get((i * 9) + j).y = 106 + (i * 18) + offset - OFFSET;

        for (int k = 0; k < 9; k++)
            this.slots.get(27 + k).y = 106 + 58 + offset - OFFSET;


        if (offset == 0) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 9; j++)
                    this.slots.get(39 + (i * 9) + j).y = -999;
        }


    }

    public void playSound() {
        this.broomEntity.getLevel().playSound(null, this.broomEntity.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playSoundOpen(boolean isEnder) {
        SoundEvent sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        float volume = 0.75f;
        if (isEnder) {
            sound = SoundEvents.ENDER_CHEST_OPEN;
            volume = 0.5f;
        }

        this.broomEntity.getLevel().playSound(null, this.broomEntity.getX(), this.broomEntity.getY() + 0.5D, this.broomEntity.getZ(), sound, SoundSource.BLOCKS, volume, this.broomEntity.getLevel().random.nextFloat() * 0.1F + 0.9F);
    }

    public void playSoundClose(boolean isEnder) {
        SoundEvent sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        float pitch = 0.4f;
        float volume = 0.75f;
        if (isEnder) {
            sound = SoundEvents.ENDER_CHEST_CLOSE;
            pitch = 0.9f;
            volume = 0.5f;
        }

        this.broomEntity.getLevel().playSound(null, this.broomEntity.getX(), this.broomEntity.getY() + 0.5D, this.broomEntity.getZ(), sound, SoundSource.BLOCKS, volume, this.broomEntity.getLevel().random.nextFloat() * 0.1F + pitch);
    }

    public void playSound(SoundEvent event) {
        this.broomEntity.getLevel().playSound(null, this.broomEntity.blockPosition(), event, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public boolean getFloatMode() {
        return broomEntity.getFloatMode();
    }

    public void setFloatMode(boolean value) {
        broomEntity.setFloatMode(value);
    }

    @Override
    public boolean stillValid(Player playerIn) {
//        return stillValid(ContainerLevelAccess.create(broomEntity.getLevel(), broomEntity.blockPosition()),
//                playerIn, ModBlocks.COFFER.get());


        if (broomEntity.isRemoved()) {
            return false;
        } else {
            return !(playerIn.distanceToSqr(broomEntity) > 64.0D);
        }
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }

        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }

        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our BlockEntity slot numbers 0 - 8)

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index); //Nonnull list
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int count = 0;

        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS))
            count = 9;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS))
            count = 18;
        if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS))
            count = 27;

        if (broomEntity.isEnder())
            count += 27;


        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + (broomEntity.isEnder() ? 30 : 0), TE_INVENTORY_FIRST_SLOT_INDEX
                                                                                                                + TE_INVENTORY_SLOT_COUNT + count, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT + count) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerEntity, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {

    }
}
