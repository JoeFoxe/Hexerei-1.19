package net.joefoxe.hexerei.block.connected;


import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class ModelSwapper {

    protected CustomBlockModels customBlockModels = new CustomBlockModels();

    public CustomBlockModels getCustomBlockModels() {
        return customBlockModels;
    }



    public void onModelBake(ModelEvent.BakingCompleted event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();

        customBlockModels.forEach((block, modelFunc) -> swapModels(modelRegistry, getAllBlockStateModelLocations(block), modelFunc));
    }

    public void registerListeners(IEventBus modEventBus) {
        modEventBus.addListener(this::onModelBake);
    }

    public static <T extends BakedModel> void swapModels(Map<ResourceLocation, BakedModel> modelRegistry,
                                                         List<ModelResourceLocation> locations, Function<BakedModel, T> factory) {
        locations.forEach(location -> {
            swapModels(modelRegistry, location, factory);
        });
    }

    public static <T extends BakedModel> void swapModels(Map<ResourceLocation, BakedModel> modelRegistry,
                                                         ModelResourceLocation location, Function<BakedModel, T> factory) {
        modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
    }

    public static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block) {
        List<ModelResourceLocation> models = new ArrayList<>();
        ResourceLocation blockRl = HexereiUtil.getKeyOrThrow(block);
        block.getStateDefinition()
                .getPossibleStates()
                .forEach(state -> {
                    models.add(BlockModelShaper.stateToModelLocation(blockRl, state));
                });
        return models;
    }

}