package net.joefoxe.hexerei.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModChestItem extends BlockItem {
    private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
//    private final ChestBlock.Type type;

    public ModChestItem(Block block , Properties properties) {
        super(block ,properties);
//        this.type = chestType;
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
        return new ChestItemRenderer();
    }
//
//    @Override
//    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
//
//        if(Screen.hasShiftDown()) {
//
//            tooltip.add(Component.translatable("tooltip.hexerei.broom_shift_2", Component.translatable(ModKeyBindings.broomDescend.getKey().getName()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCCCC00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.broom_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.broom_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            if(stack.is(ModItems.MAHOGANY_BROOM.get())) {
//                tooltip.add(Component.translatable(""));
//                tooltip.add(Component.translatable("tooltip.hexerei.mahogany_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.mahogany_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            }
//            else {
//                tooltip.add(Component.translatable(""));
//                tooltip.add(Component.translatable("tooltip.hexerei.willow_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//                tooltip.add(Component.translatable("tooltip.hexerei.willow_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            }
//        }
//
//
//        super.appendHoverText(stack, world, tooltip, flagIn);
//    }
}