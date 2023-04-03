package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.Hexerei;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class ClientEvents {
//    @SubscribeEvent
//    public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
//        for (BroomType broomType : BroomType.getValues()) {
//            // Register the model for this broom type
//            event.register(broomType.getModelLocation());
//        }
//    }

}
