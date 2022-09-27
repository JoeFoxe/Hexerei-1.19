package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.ModChestBlockEntity;
import net.joefoxe.hexerei.util.CachedMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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