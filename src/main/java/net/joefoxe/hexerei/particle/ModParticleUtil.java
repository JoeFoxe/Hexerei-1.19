package net.joefoxe.hexerei.particle;

import net.joefoxe.hexerei.Hexerei;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Hexerei.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ModParticleUtil {

    /*
     * this is just a like any other RegistryEvent, however, we are binding the particle to the Particle Factory.
     * This also is similar to binding TileEntityRenderers to TileEntites.
     */

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.CAULDRON.get(), CauldronParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BLOOD.get(), BloodParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BLOOD_BIT.get(), BloodBitParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BOOK_TEST.get(), BookTestingParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM_2.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM_3.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM_4.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM_5.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.BROOM_6.get(), BroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.FOG.get(), FogParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.EXTINGUISH.get(), ExtinguishParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.MOON_BRUSH_1.get(), MoonBroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.MOON_BRUSH_2.get(), MoonBroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.MOON_BRUSH_3.get(), MoonBroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.MOON_BRUSH_4.get(), MoonBroomParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.STAR_BRUSH.get(), StarBroomParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.OWL_TELEPORT.get(), OwlTeleportParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.OWL_TELEPORT_BARN.get(), OwlTeleportParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.OWL_TELEPORT_BARRED.get(), OwlTeleportParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.OWL_TELEPORT_SNOWY.get(), OwlTeleportParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.MAHOGANY_LEAVES.get(), FlowingLeavesParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.WITCH_HAZEL_LEAVES.get(), FlowingLeavesParticle.Factory::new);
    }



}