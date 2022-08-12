package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.MushroomWitchArmorModel;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class MushroomWitchArmorItem extends WitchArmorItem {

    public MushroomWitchArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Hexerei.MOD_ID + ":textures/models/armor/mushroom_witch_armor_layer1.png";
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return super.getEquipmentSlot(stack);
    }
//
//    @Override
//    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
//        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//
//            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//        } else {
//            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//
////            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor"));
//        }
//        super.appendHoverText(stack, world, tooltip, flagIn);
//    }



    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {

        consumer.accept(new IClientItemExtensions() {
            static MushroomWitchArmorModel model;

            @Override
            public MushroomWitchArmorModel getGenericArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
                if (model == null) model = new MushroomWitchArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER));
                float pticks = Minecraft.getInstance().getFrameTime();
                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
                model.slot = slot;
                model.copyFromDefault(_default);
                model.entity = entity;
                model.crouching = entity.isCrouching();
                model.young = entity.isBaby();
                model.setupAnim(entity, entity.animationPosition, entity.animationSpeed, entity.tickCount + pticks, netHeadYaw, netHeadPitch);
                return model;
            }
        });
    }

}