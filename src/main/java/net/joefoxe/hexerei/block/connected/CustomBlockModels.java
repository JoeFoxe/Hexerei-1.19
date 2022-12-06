package net.joefoxe.hexerei.block.connected;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.IdentityHashMap;
import java.util.Map;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class CustomBlockModels {

    private final Multimap<ResourceLocation, NonNullFunction<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
    private final Map<Block, NonNullFunction<BakedModel, ? extends BakedModel>> finalModelFunc = new IdentityHashMap<>();

    public void register(ResourceLocation block, NonNullFunction<BakedModel, ? extends BakedModel> func) {
        modelFuncs.put(block, func);
    }

    public void forEach(NonNullBiConsumer<Block, NonNullFunction<BakedModel, ? extends BakedModel>> consumer) {
        loadEntriesIfMissing();
        finalModelFunc.forEach(consumer);
    }

    private void loadEntriesIfMissing() {
        if (finalModelFunc.isEmpty())
            loadEntries();
    }

    private void loadEntries() {
        finalModelFunc.clear();
        modelFuncs.asMap().forEach((location, funcList) -> {
            Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block == null) {
                return;
            }

            NonNullFunction<BakedModel, ? extends BakedModel> finalFunc = null;
            for (NonNullFunction<BakedModel, ? extends BakedModel> func : funcList) {
                if (finalFunc == null) {
                    finalFunc = func;
                } else {
                    finalFunc = finalFunc.andThen(func);
                }
            }

            finalModelFunc.put(block, finalFunc);
        });
    }

}
