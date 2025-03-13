package net.joefoxe.hexerei.data.books;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ClientPaintDataCache {
    private static final Map<PaintSystemSavedData.BookPageIdentifier, PaintData> CACHE = new HashMap<>();

    public static void store(ResourceLocation bookLoc, UUID uuid, PaintData data) {
        CACHE.put(new PaintSystemSavedData.BookPageIdentifier(bookLoc, uuid), data);
    }

    public static Optional<PaintData> get(ResourceLocation bookLoc, UUID uuid) {
        return Optional.ofNullable(CACHE.get(new PaintSystemSavedData.BookPageIdentifier(bookLoc, uuid)));
    }

    public static boolean contains(ResourceLocation bookLoc, UUID uuid) {
        return CACHE.containsKey(new PaintSystemSavedData.BookPageIdentifier(bookLoc, uuid));
    }

    public static void clear(ResourceLocation bookLoc, UUID uuid) {
        CACHE.remove(new PaintSystemSavedData.BookPageIdentifier(bookLoc, uuid));
    }
}