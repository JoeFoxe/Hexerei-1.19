package net.joefoxe.hexerei.compat;

import com.hollingsworth.arsnouveau.setup.Config;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.light.LightManager;

import static com.hollingsworth.arsnouveau.common.light.LightManager.getLightRegistry;

public class LightManagerCompat {
    public static void fallbackToArs() {

        var hexerei = LightManager.getLightRegistry();

        var ars = getLightRegistry();

        for (var entry : hexerei.entrySet()) {
            if (ars.putIfAbsent(entry.getKey(), entry.getValue()) != null) {
                ars.get(entry.getKey()).addAll(entry.getValue());
            }
        }

        //merge configs
        Config.ITEM_LIGHTMAP.putAll(HexConfig.ITEM_LIGHTMAP);
        Config.ENTITY_LIGHT_MAP.putAll(HexConfig.ENTITY_LIGHT_MAP);

        HexConfig.DYNAMIC_LIGHT_TOGGLE.set(false);
    }
}
