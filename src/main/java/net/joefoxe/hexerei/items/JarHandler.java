package net.joefoxe.hexerei.items;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class JarHandler extends ItemStackHandler {

    public final int stacklimit;

    public JarHandler(int size, int stacklimit) {
        super(size);
        this.stacklimit = stacklimit;
    }

    public boolean isEmpty(){
        return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
    }

    public boolean noValidSlots(){
        return IntStream.range(0, getSlots())
                .mapToObj(this::getStackInSlot)
                .allMatch(ItemStack::isEmpty);
    }

//    @Override
//    public void onContentsChanged(int slot) {
//        setChanged();
//    }



    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        this.stacks.set(slot, stack);
//        onContentsChanged(slot);
    }


    @Override
    public int getSlotLimit(int slot) {
        return stacklimit;
    }

    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return stacklimit;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//        if(HexConfig.JARS_ONLY_HOLD_HERBS.get())
//            return stack.is(HexereiTags.Items.HERB_ITEM);
        return true;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, stacklimit);
        //attempting to extract equal to or greater than what is present
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                    this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
                return existing;
            //there is more than requested
        } else {
            if (!simulate) {

                this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                onContentsChanged(slot);
            }
            return existing.copyWithCount(toExtract);
        }
    }

    public NonNullList<ItemStack> getContents(){
        return stacks;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < getContents().size(); i++) {
            if (!getContents().get(i).isEmpty()) {
                int realCount = Math.min(stacklimit, getContents().get(i).getCount());
                ItemStack stack = getContents().get(i).copyWithCount(1);
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                itemTag = (CompoundTag) stack.save(provider, itemTag);
                itemTag.putInt("ExtendedCount", realCount);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", getContents().size());
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : getContents().size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                ItemStack stack = ItemStack.parseOptional(provider, itemTags);
                if (itemTags.contains("ExtendedCount", Tag.TAG_INT)) {
                    stack.setCount(itemTags.getInt("ExtendedCount"));
                }
                stacks.set(slot, stack);
            }
        }
        onLoad();
    }

}
