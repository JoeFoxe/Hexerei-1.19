package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.minecraft.world.item.ItemStack;

public class BroomTickableAttachmentItem extends BroomAttachmentItem{
    public BroomTickableAttachmentItem(Properties properties) {
        super(properties);
    }

    public void tick(BroomEntity broom, ItemStack stack){}

}
