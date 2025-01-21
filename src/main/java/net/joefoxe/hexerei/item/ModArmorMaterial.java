package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterial {


    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Hexerei.MOD_ID);


    public static final Holder<ArmorMaterial> INFUSED_FABRIC = MATERIALS.register(
            "infused_fabric", () -> register("infused_fabric", Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 4);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
        map.put(ArmorItem.Type.BODY, 8);
    }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f, 0.0f, () -> Ingredient.of(ModItems.INFUSED_FABRIC.get()), true));

    private static ArmorMaterial register(
            String pName, EnumMap<ArmorItem.Type, Integer> pDefense, int pEnchantmentValue,
            Holder<SoundEvent> pEquipSound, float pToughness, float pKnockbackResistance,
            Supplier<Ingredient> pRepairIngredient, boolean dyeable) {
        List<ArmorMaterial.Layer> list = new java.util.ArrayList<>(List.of(new ArmorMaterial.Layer(HexereiUtil.getResource(pName))));
        if (dyeable)
            list.add(new ArmorMaterial.Layer(HexereiUtil.getResource(pName), "", true));
        return register(pDefense, pEnchantmentValue, pEquipSound, pToughness, pKnockbackResistance, pRepairIngredient, list);
    }

    private static ArmorMaterial register(
            EnumMap<ArmorItem.Type, Integer> pDefense, int pEnchantmentValue, Holder<SoundEvent> pEquipSound,
            float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngridient,
            List<ArmorMaterial.Layer> pLayers) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, pDefense.get(armoritem$type));
        }

        return new ArmorMaterial(enummap, pEnchantmentValue, pEquipSound, pRepairIngridient, pLayers, pToughness, pKnockbackResistance);
    }
}
