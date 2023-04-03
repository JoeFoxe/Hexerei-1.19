package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.AskForMapDataPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CrowAmuletItem extends Item {
    public CrowAmuletItem(Properties properties) {
        super(properties);

    }

    @Nullable
    public Packet<?> getUpdatePacket(ItemStack pStack, Level pLevel, Player pPlayer) {

        if(pStack.hasTag()) {
            CompoundTag inv = pStack.getOrCreateTag();
            ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
            CompoundTag compoundtag = tagList.getCompound(0);
            ItemStack stack = ItemStack.of(compoundtag);
            if(stack.getItem() instanceof MapItem){
                Integer integer = MapItem.getMapId(stack);
                MapItemSavedData mapitemsaveddata = MapItem.getSavedData(integer, pLevel);
                return mapitemsaveddata != null ? mapitemsaveddata.getUpdatePacket(integer, pPlayer) : null;
            }
        }
        return null;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {

        if(pStack.hasTag()){
            CompoundTag inv = pStack.getOrCreateTag();
            ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
            CompoundTag compoundtag = tagList.getCompound(0);
            ItemStack stack = ItemStack.of(compoundtag);
            if (stack.getItem() instanceof MapItem mapItem) {
                mapItem.inventoryTick(pStack, pLevel, pEntity, pSlotId, true);
                MapItemSavedData mapitemsaveddata = MapItem.getSavedData(stack, pLevel);
                if(mapitemsaveddata == null)
                    HexereiPacketHandler.sendToServer(new AskForMapDataPacket(stack));
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public Component getName(ItemStack pStack) {
        CompoundTag inv = pStack.getOrCreateTag();
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        CompoundTag compoundtag = tagList.getCompound(0);
        ItemStack stack = ItemStack.of(compoundtag);
        if(!stack.isEmpty()) {
            if(stack.hasCustomHoverName())
                return Component.translatable("").append(stack.getHoverName()).append(Component.translatable("item.hexerei.crow_filled_amulet"));
            return Component.translatable(ItemStack.of(compoundtag).getDescriptionId()).append(Component.translatable("item.hexerei.crow_filled_amulet"));
        }

        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag inv = stack.getOrCreateTag();
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        CompoundTag compoundtag = tagList.getCompound(0);
        CompoundTag itemTags = tagList.getCompound(0);

        MutableComponent itemText = Component.translatable(ItemStack.of(compoundtag).getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(!ItemStack.of(itemTags).isEmpty())
                tooltip.add(Component.translatable("tooltip.hexerei.keychain_with_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            else
                tooltip.add(Component.translatable("tooltip.hexerei.keychain_without_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltip.add(Component.translatable("tooltip.hexerei.crow_blank_amulet").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.cosmetic_only").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
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
        return new CrowBlankAmuletItemRenderer();
    }

}
