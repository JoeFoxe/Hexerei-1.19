package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class WhistleItem extends Item {
    public WhistleItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);


        CustomData data = itemstack.get(DataComponents.CUSTOM_DATA);
        if(data != null){
            CompoundTag tag = data.copyTag();
            if(tag.contains("broomUUID")){
                playerIn.level().playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.BROOM_WHISTLE.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * new Random().nextFloat());
                BroomEntity broomFound = null;
                UUID broomUUID = tag.getUUID("broomUUID");
                List<BroomEntity> list = level.getEntitiesOfClass(BroomEntity.class, playerIn.getBoundingBox().inflate(64.0D));
                for(BroomEntity broom : list){
                    if(broom.broomUUID != null && broom.broomUUID.toString().equals(broomUUID.toString()) && !broom.hasPassenger(playerIn)){
                        broomFound = broom;
                        broomFound.broomCalled = true;
                        broomFound.floatMode = true;
                        broomFound.broomCalledDelay = (int)broom.distanceTo(playerIn);
                        playerIn.getCooldowns().addCooldown(this, 40);

                        if(!playerIn.isCreative())
                            itemstack.hurtAndBreak(1, playerIn, LivingEntity.getSlotForHand(handIn));

                        break;
                    }
                }

                if(broomFound == null)
                    playerIn.displayClientMessage(Component.translatable("display.hexerei.broom_whistle_not_found"), true);
                else {

                    float xOffset = (float)Math.cos(((playerIn.getYRot() - 90f) / 180f) * (Math.PI)) * 1.5f;
                    float zOffset = (float)Math.sin(((playerIn.getYRot() - 90f) / 180f) * (Math.PI)) * 1.5f;

                    broomFound.setYRot(playerIn.getYRot());
                    broomFound.teleportTo(playerIn.xOld - xOffset, playerIn.yOld + playerIn.getBbHeight() / 2, playerIn.zOld - zOffset);
                    return InteractionResultHolder.success(itemstack);
                }

                playerIn.getCooldowns().addCooldown(this, 40);
                return InteractionResultHolder.success(itemstack);

            } else {

                playerIn.displayClientMessage(Component.translatable("display.hexerei.broom_whistle_not_bound"), true);
            }
        }


        playerIn.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {


        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            boolean flag = false;
            if(data != null) {
                CompoundTag tag = data.copyTag();
                if (tag.contains("broomUUID")) {

                    flag = true;
                    tooltipComponents.add(Component.translatable("Bound to: %s", tag.getUUID("broomUUID").toString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x119911))));

                }
            }
            if(!flag)
                tooltipComponents.add(Component.translatable("Not Bound").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x991100))));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_whistle_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_whistle_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            boolean flag = false;
            if(data != null) {
                CompoundTag tag = data.copyTag();
                if (tag.contains("broomUUID")) {

                    flag = true;
                    tooltipComponents.add(Component.translatable("Bound").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x119911))));

                }
            }
            if(!flag)
                tooltipComponents.add(Component.translatable("Not Bound").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x991100))));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_whistle").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_whistle_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }


        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}