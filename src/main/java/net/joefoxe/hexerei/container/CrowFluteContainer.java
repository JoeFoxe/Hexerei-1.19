package net.joefoxe.hexerei.container;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.List;
import java.util.UUID;


public class CrowFluteContainer extends AbstractContainerMenu {
    private final Player playerEntity;
    public final ItemStack stack;
    public InteractionHand hand;
    private final IItemHandler playerInventory;
    public List<Entity> crowList;

    public CrowFluteContainer(int windowId, ItemStack itemStack, Inventory playerInventory, Player player, InteractionHand hand, CompoundTag list) {
        super(ModContainers.CROW_FLUTE_CONTAINER.get(), windowId);
        this.stack = itemStack;
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.hand = hand;
        this.crowList = Lists.newArrayList();

        ListTag listTag = list.getList("crowList", CompoundTag.TAG_COMPOUND);

        for(int i = 0; i < listTag.size(); i++){
            CompoundTag tag = listTag.getCompound(i);
            if(player.level.isClientSide) {

                int crowId = tag.getInt("ID");

                this.crowList.add((player.level).getEntity(crowId));
            }
        }

    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);


    }




    public int getCommand() {

        return stack.getOrCreateTag().getInt("commandSelected");
    }

    public void setCommand(int value) {
        ((CrowFluteItem)stack.getItem()).setCommand(value, stack, playerEntity, hand);
        stack.getOrCreateTag().putInt("commandSelected", value);
    }

    public int getHelpCommand() {
        return stack.getOrCreateTag().getInt("helpCommandSelected");
    }

    public void setHelpCommand(int value) {
        ((CrowFluteItem)stack.getItem()).setHelpCommand(value, stack, playerEntity, hand);
        stack.getOrCreateTag().putInt("helpCommandSelected", value);
    }

    public void setCommandMode(int value) {
        ((CrowFluteItem)stack.getItem()).setCommandMode(value, stack, playerEntity, hand);
        stack.getOrCreateTag().putInt("commandMode", value);
    }

    public int getCommandMode() {
        return stack.getOrCreateTag().getInt("commandMode");
    }

    public void clearCrowList() {
        ((CrowFluteItem)stack.getItem()).clearCrowList(stack, playerEntity, hand);
        this.crowList.clear();
    }

    public void clearCrowPerch() {
        ((CrowFluteItem)stack.getItem()).clearCrowPerch(stack, playerEntity, hand);
//        this.crowList.clear();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem() == stack;
    }

}
