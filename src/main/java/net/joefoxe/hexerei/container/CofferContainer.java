package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;


public class CofferContainer extends AbstractContainerMenu {
    private final BlockEntity tileEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;
    public static final int OFFSET = 28;

    public boolean inWorld;

    public InteractionHand hand;
    public ItemStack stack;


    private Slot cofferSlot(Container container, int slot, int x, int y){
        return new Slot(container, slot, x, y){
            @Override
            public boolean mayPlace(ItemStack pStack) {
                return container.canPlaceItem(slot, pStack);
            }
        };
    }

    public CofferContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContainers.COFFER_CONTAINER.get(), windowId);
        this.tileEntity = world.getBlockEntity(pos);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        if(tileEntity instanceof Container container)
            container.startOpen(player);
        this.inWorld = true;

        layoutPlayerInventorySlots(11, 147 - OFFSET);

        //add slots for coffer
        if(tileEntity != null && tileEntity instanceof Container container) {

            this.addSlot(cofferSlot(container, 0, 15 + (21 * 0), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 1, 15 + (21 * 1), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 2, 15 + (21 * 2), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 3, 15 + (21 * 3), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 4, 15 + (21 * 4), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 5, 15 + (21 * 5), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 6, 15 + (21 * 6), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 7, 15 + (21 * 7), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 8, 15 + (21 * 8), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 9, 15 + (21 * 0), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 10, 15 + (21 * 1), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 11, 15 + (21 * 2), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 12, 15 + (21 * 6), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 13, 15 + (21 * 7), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 14, 15 + (21 * 8), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 15, 15 + (21 * 0), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 16, 15 + (21 * 1), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 17, 15 + (21 * 2), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 18, 15 + (21 * 6), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 19, 15 + (21 * 7), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 20, 15 + (21 * 8), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 21, 15 + (21 * 0), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 22, 15 + (21 * 1), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 23, 15 + (21 * 2), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 24, 15 + (21 * 6), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 25, 15 + (21 * 7), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 26, 15 + (21 * 8), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 27, 15 + (21 * 0), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 28, 15 + (21 * 1), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 29, 15 + (21 * 2), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 30, 15 + (21 * 3), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 31, 15 + (21 * 4), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 32, 15 + (21 * 5), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 33, 15 + (21 * 6), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 34, 15 + (21 * 7), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 35, 15 + (21 * 8), 102 - OFFSET));
        }

        addDataSlot(new DataSlot() {
            @Override
            public void set(int value) {
                ((CofferTile)tileEntity).setButtonToggled(value);
            }
            @Override
            public int get() {
                return ((CofferTile)tileEntity).getButtonToggled();
            }
        });
    }

    public CofferContainer(int windowId, ItemStack itemStack, Inventory playerInventory, Player player, InteractionHand hand) {
        super(ModContainers.COFFER_CONTAINER.get(), windowId);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.inWorld = false;
        this.tileEntity = ModBlocks.COFFER.get().newBlockEntity(BlockPos.ZERO, ModBlocks.COFFER.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));

        this.stack = itemStack;
        this.hand = hand;
//        player.getHandSlots().forEach((stack) -> {stack.equals(this.stack)});


        layoutPlayerInventorySlots(11, 147 - OFFSET);

        //add slots for coffer

        if(this.tileEntity instanceof CofferTile te) {
            te.readInventory(stack.getOrCreateTag()
                    .getCompound("Inventory"));

            te.setDyeColor(Coffer.getColorStatic(stack));

            te.buttonToggled = stack.getOrCreateTag()
                    .getInt("ButtonToggled");
            te.self = itemStack;
            te.setChanged();
        }


        if(tileEntity != null && tileEntity instanceof Container container) {

            this.addSlot(cofferSlot(container, 0, 15 + (21 * 0), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 1, 15 + (21 * 1), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 2, 15 + (21 * 2), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 3, 15 + (21 * 3), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 4, 15 + (21 * 4), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 5, 15 + (21 * 5), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 6, 15 + (21 * 6), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 7, 15 + (21 * 7), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 8, 15 + (21 * 8), 18 - OFFSET));
            this.addSlot(cofferSlot(container, 9, 15 + (21 * 0), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 10, 15 + (21 * 1), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 11, 15 + (21 * 2), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 12, 15 + (21 * 6), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 13, 15 + (21 * 7), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 14, 15 + (21 * 8), 39 - OFFSET));
            this.addSlot(cofferSlot(container, 15, 15 + (21 * 0), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 16, 15 + (21 * 1), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 17, 15 + (21 * 2), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 18, 15 + (21 * 6), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 19, 15 + (21 * 7), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 20, 15 + (21 * 8), 60 - OFFSET));
            this.addSlot(cofferSlot(container, 21, 15 + (21 * 0), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 22, 15 + (21 * 1), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 23, 15 + (21 * 2), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 24, 15 + (21 * 6), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 25, 15 + (21 * 7), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 26, 15 + (21 * 8), 81 - OFFSET));
            this.addSlot(cofferSlot(container, 27, 15 + (21 * 0), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 28, 15 + (21 * 1), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 29, 15 + (21 * 2), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 30, 15 + (21 * 3), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 31, 15 + (21 * 4), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 32, 15 + (21 * 5), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 33, 15 + (21 * 6), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 34, 15 + (21 * 7), 102 - OFFSET));
            this.addSlot(cofferSlot(container, 35, 15 + (21 * 8), 102 - OFFSET));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
    }

    public void playSound() {
        this.tileEntity.getLevel().playSound((Player)null, this.tileEntity.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);;
    }

    public int getToggled() {
        if(this.inWorld)
            return ((CofferTile)tileEntity).getButtonToggled();
        return 0;
    }

    public void setToggled(int value) {
        if(this.inWorld)
            ((CofferTile)tileEntity).setButtonToggled(value);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        if(this.inWorld)
            return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()),
                            playerIn,tileEntity.getBlockState().getBlock());
        return playerIn.getItemInHand(this.hand).equals(this.stack);

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
    private static final int TE_INVENTORY_SLOT_COUNT = 36;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS



//    @Nonnull
//    @Override
//    public ItemStack quickMoveStack(Player player, int slot)
//    {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slotObject = this.slots.get(slot);
//        if(slotObject!=null&&slotObject.hasItem())
//        {
//            ItemStack itemstack1 = slotObject.getItem();
//            itemstack = itemstack1.copy();
//            if(slot < ((CofferTile)this.tileEntity).itemHandler.getSlots())
//            {
//                if(!this.moveItemStackTo(itemstack1, ((CofferTile)this.tileEntity).itemHandler.getSlots(), this.slots.size(), true))
//                {
//                    return ItemStack.EMPTY;
//                }
//            }
//            else if(!this.moveItemStackToWithMayPlace(itemstack1, 0, ((CofferTile)this.tileEntity).itemHandler.getSlots()))
//            {
//                return ItemStack.EMPTY;
//            }
//
//            if(itemstack1.isEmpty())
//            {
//                slotObject.set(ItemStack.EMPTY);
//            }
//            else
//            {
//                slotObject.setChanged();
//            }
//        }
//
//        return itemstack;
//    }
//
//    protected boolean moveItemStackToWithMayPlace(ItemStack pStack, int pStartIndex, int pEndIndex)
//    {
//        boolean inAllowedRange = true;
//        int allowedStart = pStartIndex;
//        for(int i = pStartIndex; i < pEndIndex; i++)
//        {
//            boolean mayplace = this.slots.get(i).mayPlace(pStack);
//            if(inAllowedRange&&(!mayplace||i==pEndIndex-1))
//            {
//                if(moveItemStackTo(pStack, allowedStart, i, false))
//                    return true;
//                inAllowedRange = false;
//            }
//            else if(!inAllowedRange&&mayplace)
//            {
//                allowedStart = i;
//                inAllowedRange = true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
//    {
//        return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
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
