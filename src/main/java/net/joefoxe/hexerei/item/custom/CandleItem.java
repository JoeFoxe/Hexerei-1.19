package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CandleItem extends BlockItem implements DyeableLeatherItem {

    public CandleItem(Block block, Properties properties) {
        super(block, properties);
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
        return new CandleItemRenderer();
    }


    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerCandleColors(RegisterColorHandlersEvent.Item event)
        {
            CandleItem.ItemHandlerConsumer items = event.getItemColors()::register;
            items.register((s, t) -> t == 1 ? getColorValue(CandleItem.getDyeColorNamed(s), s) : -1, ModItems.CANDLE.get());

        }
    }

    @Override
    public void setColor(ItemStack p_41116_, int p_41117_) {
        DyeableLeatherItem.super.setColor(p_41116_, p_41117_);
    }


    public void setHeight(ItemStack p_41116_, int p_41117_) {
        p_41116_.getOrCreateTagElement("display").putInt("height", p_41117_);
    }

    public static int getHeight(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("height", 99) ? compoundtag.getInt("height") : 7;
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = getColorStatic(stack);
        if(color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    public static int getDyeColorNamed(String name) {

        if(HexereiUtil.getDyeColorNamed(name)!= null){
            float f3 = ((float) (((Hexerei.getClientTicks()) / 10f * 4) % 16)) / (float) 16;

            DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = Sheep.getColorArray(col1);
            float[] afloat2 = Sheep.getColorArray(col2);
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValue(f, f1, f2);
        }
        return 0;

    }

    public static DyeColor getDyeColorNamed(ItemStack stack) {

        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }

//    public ItemStackHandler createHandler() {
//        return new ItemStackHandler(36) {
//
//            @Override
//            public int getSlotLimit(int slot) {
//                return 64;
//            }
//        };
//    }
//
//
//    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
//        ItemStackHandler handler = createHandler();
//        handler.deserializeNBT(stack.getOrCreateTag().getCompound("Inventory"));
//
//        return Optional.of(new CandleItem.CofferItemToolTip(handler, stack));
//    }
//
//    public record CofferItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
//    }

}