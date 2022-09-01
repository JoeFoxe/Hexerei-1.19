package net.joefoxe.hexerei.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;


import net.minecraft.world.item.Item.Properties;

public class KeychainItem extends Item {


    public NonNullList<ItemStack> containedItem = NonNullList.withSize(1, ItemStack.EMPTY);

    public KeychainItem(Properties properties) {
        super(properties);

    }
//ContainerHelper.saveAllItems(tag, this.items);




    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag inv = stack.getOrCreateTag();
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        CompoundTag compoundtag = tagList.getCompound(0);
        CompoundTag itemTags = tagList.getCompound(0);

        MutableComponent itemText = (MutableComponent) Component.translatable(ItemStack.of(compoundtag).getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(!ItemStack.of(itemTags).isEmpty())
            tooltip.add(Component.translatable("tooltip.hexerei.keychain_with_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            else
            tooltip.add(Component.translatable("tooltip.hexerei.keychain_without_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }


        if(!ItemStack.of(itemTags).isEmpty()) {
            tooltip.add(Component.translatable("tooltip.hexerei.keychain_contains", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }

        super.appendHoverText(stack, world, tooltip, flagIn);
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        CustomItemRenderer renderer = createItemRenderer();
        if (renderer != null) {
            consumer.accept(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer.getRenderer();
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public CustomItemRenderer createItemRenderer() {
        return new BroomKeychainItemRenderer();
    }

}
