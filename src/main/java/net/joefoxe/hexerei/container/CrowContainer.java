package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;


public class CrowContainer extends AbstractContainerMenu {
    private final Player playerEntity;
    public final CrowEntity crowEntity;
    private final IItemHandler playerInventory;



//    public BroomContainer(int windowId, BroomEntity crow, Inventory inv) {
//        this(windowId, inv, new SimpleContainer(5));
//    }

    public CrowContainer(int windowId, CrowEntity crowEntity, Inventory playerInventory, Player player) {
        super(ModContainers.CROW_CONTAINER.get(), windowId);
        this.crowEntity = crowEntity;
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        crowEntity.sync();
        layoutPlayerInventorySlots(14, 147);

        //add slots for crow
        if(crowEntity != null) {
            crowEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {

//                addSlot(EquipmentSlot.HEAD)


                addSlot(new SlotItemHandler(h, 0, 86, 50){

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
//                        return stack.is(HexereiTags.Items.BROOM_MISC);
                        return stack.canEquip(EquipmentSlot.HEAD, crowEntity);
                    }

//                    public boolean mayPickup(Player p_39744_) {
//                        ItemStack itemstack = this.getItem();
//                        return (!EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(p_39744_);
//                    }


                });

                //satchel slot
                addSlot(new SlotItemHandler(h, 1, 37, 50) {

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return true;
                    }

                });
                addSlot(new SlotItemHandler(h, 2, 134, 50){

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return true;
                    }
                });


//                for(int i = 0; i < 3; i++)
//                    for(int j = 0; j < 9; j++)
//                        addSlot(new SlotItemHandler(h, 3 + (i * 9) + j , 15 + 21 * j, 21 * i + 82));
//
//
//                for(int i = 0; i < 3; i++)
//                    for(int j = 0; j < 9; j++)
//                        this.slots.get((i * 9) + j).y = 106 + (i * 18);
//
//                for(int k = 0; k < 9; k++)
//                    this.slots.get(27 + k).y = 106 + 58;


            });
        }

        addDataSlot(new DataSlot() {
            @Override
            public void set(int value) {
                crowEntity.setCommand(value);
            }
            @Override
            public int get() {
                return crowEntity.getCommand();
            }
        });



    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);


    }

    public void playSound() {
        this.crowEntity.getLevel().playSound((Player)null, this.crowEntity.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);;
    }

    public int getCommand() {
        return crowEntity.getCommand();
    }

    public void setCommand(int value) {
        crowEntity.setCommand(value);
    }

    public int getHelpCommand() {
        return crowEntity.getHelpCommand();
    }

    public void setHelpCommand(int value) {
        crowEntity.setHelpCommand(value);
    }

    @Override
    public boolean stillValid(Player playerIn) {
//        return stillValid(ContainerLevelAccess.create(crowEntity.getLevel(), crowEntity.blockPosition()),
//                playerIn, ModBlocks.COFFER.get());


        if (crowEntity.isRemoved()) {
            return false;
        } else {
            return !(playerIn.distanceToSqr(crowEntity) > 64.0D);
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
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();


        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
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
}
