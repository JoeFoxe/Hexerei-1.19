package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

//@EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei")
public class WitchArmorItem extends ArmorItem {

    public WitchArmorItem(Holder<ArmorMaterial> materialIn, ArmorItem.Type type, Properties builder) {
        super(materialIn, type, builder);
    }



    public int getColor(ItemStack stack) {
        String name = stack.getHoverName().getString();
        DyeColor col = HexereiUtil.getDyeColorNamed(name, 0);
        if(col != null){

            float f3 = (((ClientEvents.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col.getTextureDiffuseColor());
            float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValueAlpha(f, f1, f2, 1);

        }
        return stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(-1, true)).rgb();
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {

        if(HexereiUtil.getDyeColor(stack) != 0x422F1E) {
            if (layer.dyeable())
                return ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/models/armor/witch_armor_layer1_dyed.png");
//            else
//                return ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/models/armor/witch_armor_layer1_dyed_overlay.png");
        }
        return ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/models/armor/witch_armor_layer1.png");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return super.getEquipmentSlot(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            Player player = null;
            if(context.level() != null && context.level().isClientSide)
                player = Hexerei.proxy.getPlayer();

            int num = 0;
            boolean hat = player != null && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WitchArmorItem;
            boolean robe = player != null && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WitchArmorItem;
            boolean boots = player != null && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WitchArmorItem;
            if(hat)
                num++;
            if(robe)
                num++;
            if(boots)
                num++;

            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_pieces").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
            tooltipComponents.add(Component.translatable("").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltipComponents.add(Component.translatable(" %s - %s",Component.translatable("item.hexerei.witch_helmet"),Component.translatable("item.hexerei.mushroom_witch_hat")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(hat ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltipComponents.add(Component.translatable(" %s",Component.translatable("item.hexerei.witch_chestplate")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(robe ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltipComponents.add(Component.translatable(" %s",Component.translatable("item.hexerei.witch_boots")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(boots ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 2).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x333333 : 0x31C475))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x333333 : 0x31C475))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 3).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x4F1C18 : 0x249100))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x333333 : 0x31C475))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x333333 : 0x31C475))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            Player player = null;
            if(context.level() != null && context.level().isClientSide)
                player = Hexerei.proxy.getPlayer();

            int num = 0;
            boolean hat = player != null && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WitchArmorItem;
            boolean robe = player != null && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WitchArmorItem;
            boolean boots = player != null && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WitchArmorItem;
            if(hat)
                num++;
            if(robe)
                num++;
            if(boots)
                num++;
            tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 2).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

//    @Override
//    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//        consumer.accept(new IClientItemExtensions() {
//            @Override
//            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
//                return ArmorModels.get(stack);
//            }
//        });
//    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//
//        consumer.accept(new IClientItemExtensions() {
//            static WitchArmorModel<LivingEntity> model;
//
//            @Override
//            public WitchArmorModel<LivingEntity> getGenericArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
//                if (model == null)
//                    model = new WitchArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.WITCH_ARMOR_LAYER));
//                float pticks = Minecraft.getInstance().getFrameTime();
//                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
//                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
//                float netHeadYaw = f1 - f;
//                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
//                model.slot = slot;
//                model.copyFromDefault(_default);
//                model.entity = entity;
//                model.entityClass = entity.getClass();
//                model.crouching = entity.isCrouching();
//                model.young = entity.isBaby();
////                model.setupAnim(entity, entity.animationPosition, entity.animationSpeed, entity.tickCount, netHeadYaw, netHeadPitch);
//                return model;
//            }
//        });
//    }

}