package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.items.JarHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HerbJarItem extends BlockItem {

    public HerbJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CompoundTag inv = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("Inventory");
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        if(Screen.hasShiftDown()) {

            if(tagList.size() >= 1) {
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            if(tagList.size() < 1)
            {
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_7").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            }

        }
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {

        return super.place(context);
    }

    @Override
    public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
        super.registerBlocks(pBlockToItemMap, pItem);
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
        JarHandler handler = createHandler();
        int count = getContentsExtra(p_150775_);
        ItemStack stack = getContents(p_150775_);
        stack.setCount(count);
        handler.setStackInSlot(0, stack);
        return Optional.of(new HerbJarToolTip(handler));
    }

    public record HerbJarToolTip(JarHandler jarHandler) implements TooltipComponent {
    }

    private JarHandler createHandler() {
        return new JarHandler(1,1024);
    }

    private static ItemStack getContents(ItemStack p_150783_) {
        CustomData compoundtag = p_150783_.get(DataComponents.CUSTOM_DATA);
        if (compoundtag == null) {
            return ItemStack.EMPTY;
        } else {
            return ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag.copyTag().getCompound("Inventory").getList("Items", 10).getCompound(0));
        }
    }

    private static int getContentsExtra(ItemStack p_150783_) {
        CustomData compoundtag = p_150783_.get(DataComponents.CUSTOM_DATA);
        if (compoundtag == null) {
            return 0;
        } else {
            return compoundtag.copyTag().getCompound("Inventory").getList("Items", 10).getCompound(0).getInt("ExtendedCount");
        }
//        int countAll = new int[1];
//        if (compoundtag != null) {
//            int size = compoundtag.getCompound("Inventory").getList("Items", 10).size();
//            for (int i = 0; i < size; i++) {
//                int count = compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(0).getInt("ExtendedCount");
//                countAll = count;
//            }
//        }
//        return countAll;
    }
//
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
//
//
//    @OnlyIn(Dist.CLIENT)
//    public CustomItemRenderer createItemRenderer() {
//        return new HerbJarItemRenderer();
//    }

}