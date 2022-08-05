package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
//import net.joefoxe.hexerei.client.renderer.entity.model.OrcArmorModel;
//import net.joefoxe.hexerei.client.renderer.entity.model.WitchArmorModel;
import net.joefoxe.hexerei.client.renderer.entity.model.WitchArmorModel;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static net.joefoxe.hexerei.util.ClientProxy.WITCH_ARMOR_LAYER;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
public class WitchArmorItem extends DyeableArmorItem {

    public WitchArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }



    @Override
    public int getColor(ItemStack stack) {
        String name = stack.getHoverName().getString();
        DyeColor col = HexereiUtil.getDyeColorNamed(name, 0);
        CompoundTag compoundtag = stack.getTagElement("display");

        if(compoundtag == null || !compoundtag.contains("color", 99))
            return 1908001;
        if(col != null){

            float f3 = ((float)(((Hexerei.getClientTicks()) / 10f * 4 ) % 16)) / (float)16;

            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

//            float[] afloat1 = col.getTextureDiffuseColors();
//            float[] afloat2 = col2.getTextureDiffuseColors();
            float[] afloat1 = Sheep.getColorArray(col);
            float[] afloat2 = Sheep.getColorArray(col2);
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValue(f, f1, f2);
//            return HexereiUtil.getColorValue(col);

        }
        return compoundtag.getInt("color");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {

        if(HexereiUtil.getColorStatic(stack) != 0x422F1E)
            return Hexerei.MOD_ID + ":textures/models/armor/witch_armor_layer1_dyed" + ((type == null) ? "" : "_" + type) + ".png";
        return Hexerei.MOD_ID + ":textures/models/armor/witch_armor_layer1.png";
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return super.getEquipmentSlot(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            Player player = null;
            if(level != null && level.isClientSide)
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

            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_pieces").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
            tooltip.add(Component.translatable("").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltip.add(Component.translatable(" %s - %s",Component.translatable("item.hexerei.witch_helmet"),Component.translatable("item.hexerei.mushroom_witch_hat")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(hat ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltip.add(Component.translatable(" %s",Component.translatable("item.hexerei.witch_chestplate")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(robe ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltip.add(Component.translatable(" %s",Component.translatable("item.hexerei.witch_boots")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(boots ? (num < 2 ? 0x1C7044 : 0x31C475) : 0x333333))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 2).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x333333 : 0x31C475))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x333333 : 0x31C475))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 3).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x4F1C18 : 0x249100))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x333333 : 0x31C475))));
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 0x333333 : 0x31C475))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            Player player = null;
            if(level != null && level.isClientSide)
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
            tooltip.add(Component.translatable("tooltip.hexerei.witch_armor_bonus", num, 2).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 0x4F1C18 : 0x249100))));
        }
        super.appendHoverText(stack, level, tooltip, flagIn);
    }



    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {

        consumer.accept(new IItemRenderProperties() {
            static WitchArmorModel model;

            @Override
            public WitchArmorModel getArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
                if (model == null) model = new WitchArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.WITCH_ARMOR_LAYER));
                float pticks = Minecraft.getInstance().getFrameTime();
                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
                model.slot = slot;
                model.copyFromDefault(_default);
                model.setupAnim(entity, entity.animationPosition, entity.animationSpeed, entity.tickCount + pticks, netHeadYaw, netHeadPitch);
                return model;
            }
        });
    }

}