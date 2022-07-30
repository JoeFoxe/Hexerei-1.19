package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CofferItem extends BlockItem implements DyeableLeatherItem {

    public CofferItem(Block block, Properties properties) {
        super(block, properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    @Override
    public void setColor(ItemStack p_41116_, int p_41117_) {
        DyeableLeatherItem.super.setColor(p_41116_, p_41117_);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
//        int col2 = getDyeColorNamed(stack.getHoverName().getString());
        int dyeCol = getColorStatic(stack);
//        if (col2 != 0) dyeCol = col2;
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

//            float[] afloat1 = col.getTextureDiffuseColors();
//            float[] afloat2 = col2.getTextureDiffuseColors();
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
//
//        if(stack.getHoverName().getString().equals("jeb_"))
//            return DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//
//        if(stack.getHoverName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            });
//
//        if(stack.getHoverName().getString().equals("bi_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            });
//
//        if(stack.getHoverName().getString().equals("trans_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            });
//
//        if(stack.getHoverName().getString().equals("joe_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            });
//
////        if(this.getName().getString().equals("les_"))
////            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks() + 4)/10) % 15)) {
////                case 1 -> 0;
////                case 2 -> 0;
////                case 3 -> 0;
////                case 4 -> 1;
////                case 5 -> 1;
////                case 6 -> 1;
////                case 7 -> 2;
////                case 8 -> 2;
////                case 9 -> 2;
////                case 10 -> 6;
////                case 11 -> 6;
////                case 12 -> 6;
////                case 13 -> 14;
////                case 14 -> 14;
////                case 15 -> 14;
////                default -> 0;
////            });
//
//
//        //DyeColor.byId((int)(((Hexerei.getClientTicks() + 4)/10) % 16));
//        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
//        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod"));
//        } else {
//            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod"));
//        }

        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }

    public ItemStackHandler createHandler() {
        return new ItemStackHandler(36) {

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
    }


    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStackHandler handler = createHandler();
        handler.deserializeNBT(stack.getOrCreateTag().getCompound("Inventory"));
//        for(int i = 0; i < stacks.length; i++){
//            handler.setStackInSlot(slots[i], stacks[i]);
////            nonnulllist.add(stack);
//        }

        return Optional.of(new CofferItem.CofferItemToolTip(handler, stack));
    }

    public record CofferItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
    }

//    private static int getContentWeight(ItemStack p_150779_) {
//        return getContents(p_150779_).mapToInt((p_186356_) -> {
//            return (64 / p_150779_.getMaxStackSize()) * p_186356_.getCount();
//        }).sum();
//    }

    private static ItemStack[] getContents(ItemStack p_150783_) {
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return new ItemStack[0];
        } else {
            ItemStack[]stacks = new ItemStack[36];
            for(int i = 0; i < stacks.length; i++)
                stacks[i] = ItemStack.of(compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i));
            return stacks;
        }
    }

    private static int[] getContentsSlot(ItemStack p_150783_) {
        CompoundTag compoundtag = p_150783_.getTag();
        if (compoundtag == null) {
            return new int[0];
        } else {
            int[]slots = new int[36];
            for(int i = 0; i < compoundtag.getCompound("Inventory").getList("Items", 10).size(); i++)
                slots[i] = compoundtag.getCompound("Inventory").getList("Items", 10).getCompound(i).getInt("Slot");
//            ListTag listtag = ;
            return slots;
        }
    }


//    @Override
//    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//        ClientProxy.registerISTER(consumer, HerbJarItemRenderer::new);
//    }
//
//    @Override
//    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//        super.initializeClient(consumer);
//        CustomItemRenderer renderer = createItemRenderer();
//        if (renderer != null) {
//            consumer.accept(new IItemRenderProperties() {
//                @Override
//                public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
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



//
//    @Override
//    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//        consumer.accept(new IItemRenderProperties() {
//            @Override
//            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
//                return HerbJarItemRenderer.INSTANCE;
//            }
//        });
//    }

}