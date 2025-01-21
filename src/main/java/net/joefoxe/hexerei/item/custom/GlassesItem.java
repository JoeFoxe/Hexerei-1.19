package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.TwoHandedItemAnimation;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.events.GlassesZoomKeyPressEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class GlassesItem extends Item implements IThirdPersonItemAnimation {

    public GlassesItem(Properties builder) {
        super(builder);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 1200;
    }

    public UseAnim getUseAnimation(ItemStack p_151224_) {
        return UseAnim.SPYGLASS;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player p_151219_, InteractionHand p_151220_) {
//        p_151219_.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
        p_151219_.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 0.5F);
        p_151219_.awardStat(Stats.ITEM_USED.get(this));
        if(level.isClientSide())
            GlassesZoomKeyPressEvent.zoomWithItemToggled = true;
        if(level.isClientSide())
            GlassesZoomKeyPressEvent.zoomAmount = Minecraft.getInstance().gameRenderer.fov;
        return ItemUtils.startUsingInstantly(level, p_151219_, p_151220_);
    }

    public ItemStack finishUsingItem(ItemStack p_151209_, Level p_151210_, LivingEntity p_151211_) {
        this.stopUsing(p_151211_);
        return p_151209_;
    }

    public void releaseUsing(ItemStack p_151213_, Level p_151214_, LivingEntity p_151215_, int p_151216_) {
        this.stopUsing(p_151215_);
    }

    private void stopUsing(LivingEntity p_151207_) {
        if(p_151207_.level().isClientSide())
            GlassesZoomKeyPressEvent.zoomWithItemToggled = false;
        p_151207_.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.reading_glasses_shift", ModKeyBindings.glassesZoom.getTranslatedKeyMessage()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends LivingEntity> boolean poseRightArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, false);
            twoHanded.bool = false;
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends LivingEntity> boolean poseLeftArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, true);
            twoHanded.bool = false;
            return true;
        }
        return false;
    }


    @OnlyIn(Dist.CLIENT)
    private <T extends LivingEntity> void animateHands(HumanoidModel<T> model, T entity, boolean leftHand) {


        ModelPart mainHand = model.rightArm;
        ModelPart offHand = model.leftArm;
        ModelPart head = model.head;

        if(!leftHand){
            mainHand.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            mainHand.yRot = head.yRot - 0.2617994F / 3;
            AnimationUtils.bobModelPart(model.rightArm, entity.tickCount, -1.0F);
        }

        if(leftHand){
            offHand.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            offHand.yRot = head.yRot + 0.2617994F / 3;
            AnimationUtils.bobModelPart(model.leftArm, entity.tickCount, 1.0F);
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

}
