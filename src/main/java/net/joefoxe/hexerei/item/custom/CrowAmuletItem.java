package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.AskForMapDataPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CrowAmuletItem extends Item {
    public CrowAmuletItem(Properties properties) {
        super(properties);

    }

//    @Nullable
//    public Packet<?> getUpdatePacket(ItemStack pStack, Level pLevel, Player pPlayer) {
//
//        CompoundTag inv = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
//        if(!inv.isEmpty()) {
//            ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
//            CompoundTag compoundtag = tagList.getCompound(0);
//            ItemStack stack = ItemStack.parseOptional(pLevel.registryAccess(), compoundtag);
//            if(stack.getItem() instanceof MapItem){
//                Integer integer = MapItem.getMapId(stack);
//                MapItemSavedData mapitemsaveddata = MapItem.getSavedData(integer, pLevel);
//                return mapitemsaveddata != null ? mapitemsaveddata.getUpdatePacket(integer, pPlayer) : null;
//            }
//        }
//        return null;
//    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {

        CompoundTag inv = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(!inv.isEmpty()){
            ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
            CompoundTag compoundtag = tagList.getCompound(0);
            ItemStack stack = ItemStack.parseOptional(pLevel.registryAccess(), compoundtag);
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
        CompoundTag inv = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        CompoundTag compoundtag = tagList.getCompound(0);
        ItemStack stack = ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag);
        if(!stack.isEmpty()) {
            if(stack.has(DataComponents.CUSTOM_NAME))
                return Component.translatable("").append(stack.getHoverName()).append(Component.translatable("item.hexerei.crow_filled_amulet"));
            return Component.translatable(ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag).getDescriptionId()).append(Component.translatable("item.hexerei.crow_filled_amulet"));
        }

        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CompoundTag inv = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        CompoundTag compoundtag = tagList.getCompound(0);
        CompoundTag itemTags = tagList.getCompound(0);

        MutableComponent itemText = Component.translatable(ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag).getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(!ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), itemTags).isEmpty())
                tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_with_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            else
                tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_without_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_blank_amulet").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.cosmetic_only").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }


        if(!ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), itemTags).isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_contains", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }


//    @Override
//    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//        super.initializeClient(consumer);
//        CustomItemRenderer renderer = createItemRenderer();
//        if (renderer != null) {
//            consumer.accept(new IClientItemExtensions() {
//                @Override
//                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                    return renderer.getRenderer();
//                }
//            });
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public CustomItemRenderer createItemRenderer() {
//        return new CrowBlankAmuletItemRenderer();
//    }

}
