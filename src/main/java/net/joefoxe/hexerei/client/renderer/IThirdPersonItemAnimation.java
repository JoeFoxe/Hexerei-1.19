package net.joefoxe.hexerei.client.renderer;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IThirdPersonItemAnimation {

    <T extends LivingEntity> boolean poseRightArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded);

    default <T extends LivingEntity> boolean poseRightArmMixin(ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded){
        return poseRightArm(stack, (HumanoidModel)model, entity, mainHand, twoHanded);
    }

    <T extends LivingEntity> boolean poseLeftArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded);

    default <T extends LivingEntity> boolean poseleftArmMixin(ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded){
        return poseLeftArm(stack, (HumanoidModel)model, entity, mainHand, twoHanded);
    }

    default boolean isTwoHanded(){
        return false;
    }

}