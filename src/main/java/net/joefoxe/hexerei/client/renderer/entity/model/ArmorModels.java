package net.joefoxe.hexerei.client.renderer.entity.model;

import net.joefoxe.hexerei.item.custom.MushroomWitchArmorItem;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ArmorModels {
    private static Map<EquipmentSlot, ArmorModel> witchArmor = Collections.emptyMap();
    private static Map<EquipmentSlot, ArmorModel> mushroomWitchArmor = Collections.emptyMap();
    private static Map<EquipmentSlot, ArmorModel> make(EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        Map<EquipmentSlot, ArmorModel> ret = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ret.put(slot, new ArmorModel(ctx.bakeLayer(layer), slot));
        }
        return ret;
    }

    public static void init(EntityRendererProvider.Context context) {
        witchArmor = make(context, ClientProxy.WITCH_ARMOR_LAYER);
        mushroomWitchArmor = make(context, ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER);
    }

    @Nullable
    public static ArmorModel get(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof MushroomWitchArmorItem armor) {
            return mushroomWitchArmor.get(armor.getSlot());
        }
        else if (item instanceof WitchArmorItem armor) {
            return witchArmor.get(armor.getSlot());
        }
        return null;
    }
}
