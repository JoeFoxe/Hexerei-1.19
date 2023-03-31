package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
//
//    @SubscribeEvent
//    public static void getFogDensity(EntityViewRenderEvent.FogDensity event) {
//        Camera info = event.getCamera();
//        FluidState fluidState = info.level.getFluidState(info.blockPosition);
//        if (fluidState.isEmpty())
//            return;
//        Fluid fluid = fluidState.getType();
//
//        if (fluid.isSame(ModFluids.BLOOD_FLUID.get())) {
//            event.setDensity(10f);
//            event.setCanceled(true);
//        }
//
//    }
//
//    @SubscribeEvent
//    public static void getFogColor(EntityViewRenderEvent.FogColors event) {
//        Camera info = event.getCamera();
//        FluidState fluidState = info.level.getFluidState(info.blockPosition);
//        if (fluidState.isEmpty())
//            return;
//        Fluid fluid = fluidState.getType();
//
//        if (fluid.isSame(ModFluids.BLOOD_FLUID.get())) {
//            event.setRed(48 / 256f);
//            event.setGreen(4 / 256f);
//            event.setBlue(4 / 256f);
//        }
//
//    }
}
