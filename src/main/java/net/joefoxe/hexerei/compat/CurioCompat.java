package net.joefoxe.hexerei.compat;

import net.joefoxe.hexerei.item.custom.GlassesItem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class CurioCompat {

    public static boolean hasGlasses(Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, (i) -> i.getItem() instanceof GlassesItem).isPresent();
    }

    public static void sendIMC() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
    }

}
