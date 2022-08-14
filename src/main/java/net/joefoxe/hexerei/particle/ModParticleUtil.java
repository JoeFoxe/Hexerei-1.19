package net.joefoxe.hexerei.particle;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticleUtil {

    /*
     * this is just a like any other RegistryEvent, however, we are binding the particle to the Particle Factory.
     * This also is similar to binding TileEntityRenderers to TileEntites.
     */

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.register(ModParticleTypes.CAULDRON.get(), CauldronParticle.Factory::new);
        event.register(ModParticleTypes.BLOOD.get(), BloodParticle.Factory::new);
        event.register(ModParticleTypes.BLOOD_BIT.get(), BloodBitParticle.Factory::new);
        event.register(ModParticleTypes.BROOM.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.BROOM_2.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.BROOM_3.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.BROOM_4.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.BROOM_5.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.BROOM_6.get(), BroomParticle.Factory::new);
        event.register(ModParticleTypes.FOG.get(), FogParticle.Factory::new);
        event.register(ModParticleTypes.EXTINGUISH.get(), ExtinguishParticle.Factory::new);
//        Minecraft.getInstance().particleEngine.register(ModParticleTypes.DOWSING_ROD_1.get(), DowsingRodParticle.Factory::new);
//        Minecraft.getInstance().particleEngine.register(ModParticleTypes.DOWSING_ROD_2.get(), DowsingRod2Particle.Factory::new);
    }



}