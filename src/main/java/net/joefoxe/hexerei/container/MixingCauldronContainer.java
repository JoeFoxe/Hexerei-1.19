package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;


public class MixingCauldronContainer extends AbstractContainerMenu {
    public final BlockEntity tileEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;
    private FluidStack fluid;

    public MixingCauldronContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContainers.MIXING_CAULDRON_CONTAINER.get(), windowId);
        this.tileEntity = world.getBlockEntity(pos);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        layoutPlayerInventorySlots(14, 120);

        //add slots for mixing cauldron
        if(tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                this.addSlot(new SlotItemHandler(h, 9, 42, 24));
                this.addSlot(new SlotItemHandler(h, 0, 105, 17));
                this.addSlot(new SlotItemHandler(h, 1, 127, 26));
                this.addSlot(new SlotItemHandler(h, 2, 136, 48));
                this.addSlot(new SlotItemHandler(h, 3, 127, 70));
                this.addSlot(new SlotItemHandler(h, 4, 105, 79));
                this.addSlot(new SlotItemHandler(h, 5, 83, 70));
                this.addSlot(new SlotItemHandler(h, 6, 74, 48));
                this.addSlot(new SlotItemHandler(h, 7, 83, 26));
                this.addSlot(new SlotItemHandler(h, 8, 178, 48));
            });
        }

        if(tileEntity instanceof MixingCauldronTile mixingTile)
            this.fluid = mixingTile.getFluidStack();


        addDataSlot(new DataSlot() {
            @Override
            public void set(int value) {
                ((MixingCauldronTile)tileEntity).setCraftDelay(value);
            }
            @Override
            public int get() {
                return ((MixingCauldronTile)tileEntity).getCraftDelay();
            }
        });

    }

    public void setFluid(FluidStack fluidStack) {
        this.fluid = fluidStack;
    }

    public FluidStack getFluid() {
//        return fluid;
        if(tileEntity instanceof MixingCauldronTile mixingTile)
            return mixingTile.getFluidStack();
        return fluid;
    }
    public FluidStack getRenderedFluid() {
//        return fluid;
        if(tileEntity instanceof MixingCauldronTile mixingTile)
            return mixingTile.renderedFluid;
        return fluid;
    }

    public float getCraftPercent() {
        if(tileEntity instanceof MixingCauldronTile)
        {
            MixingCauldronTile cauldronTile = (MixingCauldronTile) tileEntity;
            if(!cauldronTile.getCrafted())
            {
                return (float)cauldronTile.craftDelay / (float) MixingCauldronTile.craftDelayMax;
            }
        }
        return 0;
    }

    public float getCraftPercentHalf() {
        if(tileEntity instanceof MixingCauldronTile)
        {
            MixingCauldronTile cauldronTile = (MixingCauldronTile) tileEntity;
            float delayHalf = cauldronTile.craftDelay - MixingCauldronTile.craftDelayMax / 2f;
            if(!cauldronTile.getCrafted())
            {

                return delayHalf / (float) MixingCauldronTile.craftDelayMax;
            }
        }
        return 0;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()),
                playerIn, ModBlocks.MIXING_CAULDRON.get());
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
    private static final int TE_INVENTORY_SLOT_COUNT = 10;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS


//    public ItemStack quickMoveStack(Player p_40199_, int p_40200_) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(p_40200_);
//        if (slot != null && slot.hasItem()) {
//            ItemStack itemstack1 = slot.getItem();
//            itemstack = itemstack1.copy();
//            if (p_40200_ < this.container.getContainerSize()) {
//                if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemstack1.isEmpty()) {
//                slot.set(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//        }
//
//        return itemstack;
//    }

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
