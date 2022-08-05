package net.joefoxe.hexerei.data.books;


import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItemRenderer;
import net.joefoxe.hexerei.item.custom.CustomItemRenderer;
import net.joefoxe.hexerei.item.custom.CustomItemRendererWithPageDrawing;
import net.joefoxe.hexerei.item.custom.HexereiBookItemRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class HexereiBookItem extends Item implements DyeableLeatherItem {



    public HexereiBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {

        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains("chapter")) {
            tag.putInt("chapter", 0);
            tag.putInt("page", 0);
            tag.putBoolean("opened", false);
        }
        if(!tag.contains("bookmarks"))
            tag.put("bookmarks", new CompoundTag());
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
    }

    public static ItemStack withColors(int color2) {
        ItemStack stack = new ItemStack(ModItems.BOOK_OF_SHADOWS.get());
        stack.getOrCreateTag().putInt("dyeColor2", color2);

        return stack;
    }


    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0xB77819;
    }

    public static DyeColor getColor2(ItemStack stack) {

        if(!stack.getOrCreateTag().contains("dyeColor2"))
            return DyeColor.RED;

        return DyeColor.byId(stack.getOrCreateTag().getInt("dyeColor2"));
    }



        @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.book_of_shadows_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, level, tooltip, flagIn);
    }



    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        CustomItemRendererWithPageDrawing renderer = createItemRenderer();
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
    public CustomItemRendererWithPageDrawing createItemRenderer() {
        return new HexereiBookItemRenderer();
    }
}

