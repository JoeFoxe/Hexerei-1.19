package net.joefoxe.hexerei.block.connected;

import java.util.HashMap;
import java.util.Map;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
import net.minecraft.resources.ResourceLocation;

public class CTSpriteShifter {

    private static final Map<String, SpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

    public static CTSpriteShiftEntry getCT(CTType type, ResourceLocation blockTexture, ResourceLocation connectedTexture) {
        String key = blockTexture + "->" + connectedTexture + "+" + type.getId();
        if (ENTRY_CACHE.containsKey(key))
            return (CTSpriteShiftEntry) ENTRY_CACHE.get(key);


        CTSpriteShiftEntry entry = new CTSpriteShiftEntry(type);
        entry.set(blockTexture, connectedTexture);
        ENTRY_CACHE.put(key, entry);
        return entry;
    }

}
