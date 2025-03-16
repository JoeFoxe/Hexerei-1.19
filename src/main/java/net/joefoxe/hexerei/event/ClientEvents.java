package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
    /**
     * Update light rendering.
     *
     * @param event the catched event.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            LightManager.updateAll(event.getLevelRenderer());
        }
    }
    public static ResourceLocation ORB_KEY = new ResourceLocation("hexerei", "block/crystal_ball_orb1");
    public static BakedModel ORB_MODEL;
    public static ResourceLocation ORB2_KEY = new ResourceLocation("hexerei", "block/crystal_ball_orb2");
    public static BakedModel ORB2_MODEL;

    @SubscribeEvent()
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ORB_KEY);
        event.register(ORB2_KEY);
    }
    @SubscribeEvent
    public static void onBakeModels(ModelEvent.BakingCompleted event) {
        ORB_MODEL = event.getModels().get(ORB_KEY);
        ORB2_MODEL = event.getModels().get(ORB2_KEY);
    }

}
