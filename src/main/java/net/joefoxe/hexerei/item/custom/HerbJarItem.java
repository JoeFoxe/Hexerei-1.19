package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.items.JarHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class HerbJarItem extends BlockItem implements DyeableLeatherItem {

    public HerbJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        CompoundTag inv = stack.getOrCreateTag().getCompound("Inventory");
        ListTag tagList = inv.getList("Items", Tag.TAG_COMPOUND);
        if(Screen.hasShiftDown()) {

            if(tagList.size() >= 1) {
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            if(tagList.size() < 1)
            {
                tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.coffer_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.herb_jar_shift_7").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

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
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return ItemStack.EMPTY;
        } else {
            return ItemStack.of(compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(0));
        }
    }

    private static int getContentsExtra(ItemStack p_150783_) {
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return 0;
        } else {
            return compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(0).getInt("ExtendedCount");
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

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        CustomItemRenderer renderer = createItemRenderer();
        if (renderer != null) {
            consumer.accept(new IItemRenderProperties() {
                @Override
                public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                    return renderer.getRenderer();
                }
            });
        }
    }



    @OnlyIn(Dist.CLIENT)
    public CustomItemRenderer createItemRenderer() {
        return new HerbJarItemRenderer();
    }

}