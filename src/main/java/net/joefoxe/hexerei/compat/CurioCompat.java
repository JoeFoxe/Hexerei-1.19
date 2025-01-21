//package net.joefoxe.hexerei.compat;
//
//import net.joefoxe.hexerei.item.ModItems;
//import net.joefoxe.hexerei.item.custom.GlassesItem;
//import net.minecraft.world.entity.player.Player;
//import net.neoforged.fml.InterModComms;
//import top.theillusivec4.curios.api.CuriosApi;
//import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
//import top.theillusivec4.curios.common.slottype.SlotType;
//
//public class CurioCompat {
//
//    public static boolean hasGlasses(Player player) {
////        return CuriosApi.getCuriosHelper().findFirstCurio(player, (i) -> i.getItem() instanceof GlassesItem).isPresent();
////        return CuriosApi.getCuriosInventory(player).map(inv -> inv.isEquipped(ModItems.READING_GLASSES.get())).get();
//        return false;
//    }
//
//    public static void sendIMC() {
//
//        //TODO fix this
////        InterModComms.sendTo("curios", SlotType.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
//    }
//
//}
